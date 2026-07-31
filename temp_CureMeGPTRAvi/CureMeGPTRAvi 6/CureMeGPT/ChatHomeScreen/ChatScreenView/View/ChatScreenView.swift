////
////  ChatScreenView.swift
////  CureMeGPT
////
////  Created by YES IT Labs on 29/01/26.


import SwiftUI
import IQKeyboardManagerSwift

struct ChatScreenView: View {
    
    @StateObject private var viewModel = ChatScreenViewModel()
    @StateObject private var sideMenuVM = SideMenuViewModel()
    @EnvironmentObject private var coordinator: Coordinator
    @State private var showSideMenu = false
    let menuWidth = UIScreen.main.bounds.width * 0.75
    @State private var showContextMenu = false
    @State private var contextMenuPosition: CGPoint = .zero
    @State private var showPopup: Bool = false
    @State private var showDeletePopup: Bool = false
    @State private var showNewCasePopup = false
    @State private var popupAction: (() -> Void)? = nil
    @State private var activePopup: ChatPopupType? = nil
    
    @State private var hasSentInitialMessage = false
    @State private var isCaseChat = false
    @State private var isMemberExist = true
    
    @StateObject private var speech = SpeechRecognizer()
    @State private var showText = false
    @State private var keyboardOverlap: CGFloat = 0
    @FocusState private var isInputFocused: Bool
    
    let initialText: String?   //ADD THIS
    let chatID: Int?
    let memberId: Int?
    var memberName: String? = nil
    let selectedAttachment: ChatAttachment?
    var isFromSharedLink: Bool = false
    var body: some View {
        ZStack(alignment: .trailing) {
            VStack(spacing: 0) {
                // Header — fixed at top; only chat area shrinks when keyboard opens
                headerView
                    .layoutPriority(1)
                    .zIndex(2)
              
                ScrollViewReader { proxy in
                    ScrollView {
                        LazyVStack {
                            ForEach(viewModel.messages) { message in
                                switch message.type {
                                case .text, .scheduleButton:
                                    ChatBubbleView(
                                        message: message,
                                        viewModel: viewModel,
                                        onCopy: {
                                            viewModel.copyMessage(message)
                                        }
                                    )
                                case .remoteImage(let url, let text):   //ADD THIS
                                    ChatRemoteImageBubbleView(
                                        imageURL: url,
                                        text: text,
                                        sender: message.sender
                                    )
                                    
                                case .document(let url, let text):
                                    ChatDocumentBubbleView(
                                        message: message,
                                        fileURL: url,
                                        text: text,
                                        sender: message.sender,
                                        viewModel: viewModel
                                    )

                                case .image(let image, let text):
                                    ChatImageBubbleView(
                                        image: image,
                                        text: text,
                                        sender: message.sender,
                                        onCopy: {
                                            if let text = text {
                                                viewModel.copyText(text)
                                            }
                                        },
                                        onEdit: {
                                            if let text = text {
                                                viewModel.inputText = text
                                            }
                                        }
                                    )
                                    
                                case .appointmentCard(let appointment):
                                    ChatAppointmentCardView(appointment: appointment)
                                    
                                case .status(let text):
                                    StatusBubble(text: text)
                                case .typing:
                                    HStack(alignment: .bottom, spacing: 8) {
                                        Image("HomeLogo")
                                            .resizable()
                                            .frame(width: 32, height: 32)
                                        
                                        TypingIndicatorView()
                                        Spacer()
                                    }
                                    .padding(.horizontal)
                                    .padding(.vertical, 4)
                                }
                               
                            }
                        }
                    }
                    .disableScrollBounce()
                    .onChange(of: viewModel.messages.count) { _ in
                        if let last = viewModel.messages.last {
                            proxy.scrollTo(last.id, anchor: .bottom)
                        }
                    }
                    .onReceive(NotificationCenter.default.publisher(for: UIResponder.keyboardWillHideNotification)) { _ in
                        if let last = viewModel.messages.last {
                            withAnimation {
                                proxy.scrollTo(last.id, anchor: .bottom)
                            }
                        }
                    }
                }
                .padding(.top, 12)
                .disableScrollBounce()
                
                AskForDropdownView(viewModel: viewModel, sideMenuVM: sideMenuVM, isCaseChat: isCaseChat, isFromSharedLink: isFromSharedLink, isMemberExist: isMemberExist)
                
                if !isMemberExist {
                    HStack(spacing: 8) {
                        Image(systemName: "exclamationmark.triangle.fill")
                            .foregroundColor(.orange)
                        Text("This family member no longer exists. Chat options are disabled.")
                            .font(.custom("Urbanist-Medium", size: 13))
                            .foregroundColor(Color(hex: "#71717A"))
                        Spacer()
                    }
                    .padding(.horizontal, 14)
                    .padding(.vertical, 8)
                    .background(Color.orange.opacity(0.12))
                    .cornerRadius(10)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 4)
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
      
            .safeAreaInset(edge: .bottom, spacing: 0) {
                bottomInputView
                    .background(.white)
                    .padding(.bottom, keyboardOverlap)
                    .background(.white)
                    .opacity(sideMenuVM.isMenuOpen ? 0 : 1)
            }
            .allowsHitTesting(!sideMenuVM.isMenuOpen)
            .onAppear {
                
                // Disable after ChatHomeScreenView.onDisappear re-enables it
                
                let cleanedInitial =
                 (initialText ?? "")
                     .trimmingCharacters(in: .whitespacesAndNewlines)
                     .lowercased()
 
                 isCaseChat = cleanedInitial == "casechat"
                 
                 DispatchQueue.main.async {
                     IQKeyboardManager.shared.isEnabled = false
                 }
 
                 // Load existing chat immediately to improve response time
                 if let chatID = chatID, chatID > 0 {
                     viewModel.originalChatId = chatID
                     viewModel.currentChatId = chatID
                     viewModel.getChatMessageListAPI { success in
                         if success {
                             print("Loaded old chat")
                         }
                     }
                 }
                 if let memberId = memberId {
                     viewModel.originalMemberId = memberId
                 }
                  if isFromSharedLink, let mName = memberName, !mName.isEmpty {
                      viewModel.selectedMemberDetail = FamilyDetail(id: memberId ?? 0, name: mName, relationship: nil, profilePhoto: nil)
                  }
                  viewModel.userWithFamilyDetailsAPI { success in
                       guard success else { return }

                       var matched: FamilyDetail? = nil
                       if let memberId = memberId {
                           matched = viewModel.membersListDetails.first { m in
                               if m.id == memberId { return true }
                               let mIsMyself = m.relationship?.lowercased() == "myself" || m.id == 0 || m.id == Int(UserDetail.shared.getID())
                               let memberIdIsMyself = memberId == 0 || memberId == Int(UserDetail.shared.getID())
                               return mIsMyself && memberIdIsMyself
                           }
                       }

                       if isFromSharedLink {
                           if let mName = memberName, !mName.isEmpty, let matchedByName = viewModel.membersListDetails.first(where: { $0.name?.lowercased() == mName.lowercased() }) {
                               viewModel.selectedMemberDetail = matchedByName
                               self.isMemberExist = true
                           } else if let matchedById = matched {
                               viewModel.selectedMemberDetail = matchedById
                               self.isMemberExist = true
                           } else {
                               self.isMemberExist = false
                               viewModel.selectedMemberDetail = FamilyDetail(id: memberId ?? 0, name: memberName ?? "Member", relationship: "", profilePhoto: nil)
                           }
                       } else if let memberId = memberId, memberId != 0, memberId != Int(UserDetail.shared.getID()) {
                           if let matchedMember = matched {
                               viewModel.selectedMemberDetail = matchedMember
                               self.isMemberExist = true
                           } else {
                               self.isMemberExist = false
                               viewModel.selectedMemberDetail = FamilyDetail(id: memberId, name: memberName ?? "Deleted Member", relationship: "", profilePhoto: nil)
                           }
                       } else {
                           if viewModel.selectedMemberDetail == nil {
                               viewModel.selectedMemberDetail = viewModel.membersListDetails.first
                           }
                           self.isMemberExist = true
                       }
 
                     // NEW CHAT (only if we didn't load an existing chat)
                     if chatID == nil || chatID == 0 {
                         let cleanedInitial =
                         (initialText ?? "")
                             .trimmingCharacters(in: .whitespacesAndNewlines)

                         let finalMessage =
                         cleanedInitial.lowercased() == "casechat"
                         ? ""
                         : cleanedInitial
                         
                         let hasText = !finalMessage.isEmpty
                         let hasAttachment = selectedAttachment != nil

                         if !hasSentInitialMessage &&
                             (hasText || hasAttachment) {

                             hasSentInitialMessage = true

                             viewModel.inputText = finalMessage
                             viewModel.selectedAttachment = selectedAttachment

                             let requestType =
                             cleanedInitial.lowercased() == "casechat"
                             ? "case"
                             : "normal"

                             viewModel.sendMessage(
                                 type: requestType,
                                 familyMemberID: viewModel.selectedMemberDetail?.id ?? 0
                             )
                         }
                     }
                 }
            }
            if viewModel.showActivity {
                CustomLoderView(isVisible: $viewModel.showActivity)
                    .ignoresSafeArea()
            }
            
            // DARK OVERLAY
            if sideMenuVM.isMenuOpen {
                Color.black.opacity(0.3)
                    .ignoresSafeArea()
                    .onTapGesture {
                        sideMenuVM.closeMenu()
                    }
            }
            
            // SIDE MENU (RIGHT)
            SideMenuView(
                viewModel: sideMenuVM,
                onNewCaseChat: {
                    sideMenuVM.closeMenu()
                    
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
                        showNewCasePopup = true
                    }
                },
                onNewChat: {
                    sideMenuVM.closeMenu()
                },

                onSelectChat: { chatId, member, selectedIsCaseChat in

                    sideMenuVM.closeMenu()

                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {

                        // Update current mode
                        self.isCaseChat = selectedIsCaseChat

                        viewModel.currentChatId = chatId
                        viewModel.originalChatId = chatId

                        let matched = viewModel.membersListDetails.first(where: { m in
                            if m.id == member?.id { return true }
                            let mIsMyself = m.relationship?.lowercased() == "myself" || m.id == 0 || m.id == Int(UserDetail.shared.getID())
                            let memberIsMyself = member?.relationship?.lowercased() == "myself" || member?.id == 0 || member?.id == Int(UserDetail.shared.getID())
                            return mIsMyself && memberIsMyself
                        })
                        if let matched = matched {
                            viewModel.selectedMemberDetail = matched
                            viewModel.originalMemberId = matched.id
                        }

                        viewModel.getChatMessageListAPI { success in
                            if success {
                                print(
                                    "Loaded chat \(chatId) caseChat=\(selectedIsCaseChat)"
                                )
                            }
                        }
                    }
                },
                onDeleteChat: { deletedChatId in
                    if deletedChatId == viewModel.currentChatId {
                        coordinator.pop()
                    }
                },
                isFromSharedLink: isFromSharedLink
            )
            .frame(width: menuWidth)
            .offset(x: sideMenuVM.isMenuOpen ? 0 : menuWidth)
            .animation(.easeInOut(duration: 0.25), value: sideMenuVM.isMenuOpen)
            .ignoresSafeArea()
            
        }
        
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .ignoresSafeArea(.keyboard, edges: .bottom)
        .onReceive(
            NotificationCenter.default.publisher(
                for: UIResponder.keyboardWillChangeFrameNotification
            )
        ) { notification in
            updateKeyboardOverlap(from: notification)
        }
        .onReceive(
            NotificationCenter.default.publisher(
                for: UIResponder.keyboardWillHideNotification
            )
        ) { _ in
            withAnimation(.easeOut(duration: 0.25)) {
                keyboardOverlap = 0
            }
        }
        .onChange(of: isInputFocused) { focused in
            if focused {
                IQKeyboardManager.shared.isEnabled = false
            }
        }
        .onChange(of: sideMenuVM.isMenuOpen) { isOpen in
            if isOpen {
                isInputFocused = false
                keyboardOverlap = 0
                dismissKeyboard()
            }
        }
        .onDisappear {
            IQKeyboardManager.shared.isEnabled = true
        }
        .overlay(alignment: .bottom) {
            
            if viewModel.showToast {
                ToastView(message: viewModel.toastMessage)
                    .padding(.top, 60)
                    .transition(.move(edge: .top).combined(with: .opacity))
                    .zIndex(9999)
            }
            
        }
        .animation(.easeInOut, value: viewModel.showToast)
        .sheet(isPresented: $viewModel.showAttachmentSheet) {
            AttachmentSheetView {
                viewModel.showAttachmentSheet = false
                viewModel.showImagePicker = true
            } onDocument: {
                        viewModel.showAttachmentSheet = false
                viewModel.showDocumentPicker = true
            }
        }
        //.keyboardDoneButton()
       
        .onChange(of: viewModel.selectedMemberDetail) { newMember in
             if let newMember = newMember {
                 sideMenuVM.selectedMemberDetail = newMember
                 
                 let isMyself = newMember.relationship?.lowercased() == "myself" || newMember.id == 0 || newMember.id == Int(UserDetail.shared.getID())
                 if isMyself {
                     sideMenuVM.getchatList { _ in }
                 } else {
                     sideMenuVM.userFamilyChatList { _ in }
                 }
             }
         }
        
        .onChange(of: sideMenuVM.selectedMemberDetail) { newMember in
            if let newMember = newMember {
                viewModel.selectedMemberDetail = newMember
            }
        }
        
        .sheet(isPresented: $viewModel.showImagePicker) {
            ImagePicker { image in
                viewModel.selectedAttachment = .image(image)
            }
        }
        
        .sheet(isPresented: $viewModel.showDocumentPicker) {
            ChatDocumentPicker { url in
                viewModel.selectedAttachment = .document(url)
            }
        }
        
        // FULL SCREEN POPUP (ROOT LEVEL)

        .overlay {

            // CONTEXT MENU
            if showContextMenu {

                ChatContextMenu(
                    proposedPosition: CGPoint(
                        x: UIScreen.main.bounds.width - 120,
                        y: 120
                    ),
                    onDismiss: {
                        showContextMenu = false
                    },
                    onAction: { action in

                        showContextMenu = false

                        DispatchQueue.main.asyncAfter(
                            deadline: .now() + 0.15
                        ) {
                            activePopup = action
                        }
                    }, isCaseChat: isCaseChat, isFromSharedLink: isFromSharedLink, isMemberExist: isMemberExist
                )
                .zIndex(9998)
            }

            // EXISTING POPUP
            if let popup = activePopup {

                ZStack {

                    Color.black.opacity(0.4)
                        .ignoresSafeArea()
//                        .onTapGesture {
//                            activePopup = nil
//                        }

                    popupView(for: popup)
                        .frame(
                            maxWidth: .infinity,
                            maxHeight: .infinity
                        )
                        .transition(
                            .scale.combined(
                                with: .opacity
                            )
                        )
                }
                .zIndex(9999)
            }
            
            // NEW CASE CHAT POPUP
            if showNewCasePopup {
                ZStack {
                    Color.black.opacity(0.4)
                        .ignoresSafeArea()
                        .onTapGesture {
                            showNewCasePopup = false
                        }
                    
                    let memberName = viewModel.selectedMemberDetail?.name ?? ""
                    let relationship = viewModel.selectedMemberDetail?.relationship ?? ""
                    let relationshipStr = (relationship.isEmpty || relationship.lowercased() == "myself") ? "" : " (\(relationship))"
                    
                    NewCaseChatPopUpView(
                        title: "Start a New Case Chat?",
                        message: "This new case chat will be created only for \(memberName)\(relationshipStr). Once created, you cannot switch members in the middle. The full case history will be saved in \(memberName)\(relationshipStr)’s records.",
                        onClose: {
                            showNewCasePopup = false
                        },
                        createCaseChat: {
                            showNewCasePopup = false
                            viewModel.messages.removeAll()
                            viewModel.currentChatId = nil
                            viewModel.inputText = ""
                            viewModel.selectedAttachment = nil
                            self.isCaseChat = true
                            viewModel.sendMessage(
                                type: "case",
                                familyMemberID: viewModel.selectedMemberDetail?.id ?? 0
                            )
                        }
                    )
                }
                .zIndex(9999)
            }
            
            SideMenuSharePopupView(viewModel: sideMenuVM)
        }
    }

    private func updateKeyboardOverlap(from notification: Notification) {
        guard !sideMenuVM.isMenuOpen else {
            keyboardOverlap = 0
            return
        }

        guard let keyboardFrame = notification.userInfo?[
            UIResponder.keyboardFrameEndUserInfoKey
        ] as? CGRect else {
            return
        }

        let coveredHeight = max(
            0,
            UIScreen.main.bounds.maxY - keyboardFrame.minY
        )
        let overlap = max(0, coveredHeight - bottomSafeAreaInset)
        let duration = notification.userInfo?[
            UIResponder.keyboardAnimationDurationUserInfoKey
        ] as? Double ?? 0.25

        withAnimation(.easeOut(duration: duration)) {
            keyboardOverlap = overlap
        }
    }

    private var bottomSafeAreaInset: CGFloat {
        let windowScene = UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .first { $0.activationState == .foregroundActive }

        return windowScene?.windows.first(where: \.isKeyWindow)?
            .safeAreaInsets.bottom ?? 0
    }

    private func dismissKeyboard() {
        UIApplication.shared.sendAction(
            #selector(UIResponder.resignFirstResponder),
            to: nil,
            from: nil,
            for: nil
        )
    }
    
    @ViewBuilder
    private func popupView(for popup: ChatPopupType) -> some View {
        switch popup {

        case .switchCase:

            ChatSwitchCasePopupView(
                title: isCaseChat
                ? "Switch to Normal Chat?"
                : "Switch to Case Chat?",

                message: isCaseChat
                ? "This will start a new normal chat. Do you want to continue?"
                : "This chat will be converted into a case chat. Do you want to continue?",

                stayButtonTitle: isCaseChat
                ? "Stay on Case Chat"
                : "Stay on Normal Chat",

                switchButtonTitle: isCaseChat
                ? "Yes, Switch"
                : "Yes, Switch",

                onClose: {
                    activePopup = nil
                },

                onSwitch: {

                    activePopup = nil

                    // Clear old chat
                    viewModel.messages.removeAll()
                    viewModel.currentChatId = nil
                    viewModel.inputText = ""
                    viewModel.selectedAttachment = nil

                    if isCaseChat {

                        // Case -> Normal
                        isCaseChat = false

                    } else {

                        // Normal -> Case
                        isCaseChat = true

                        viewModel.sendMessage(
                            type: "case",
                            familyMemberID:
                                viewModel.selectedMemberDetail?.id ?? 0
                        )
                    }
                }            )
        case .share:
            let currentMemberName = viewModel.selectedMemberDetail?.name ?? UserDetail.shared.getName()
            SharePopUpView(
                title: "Share Chat",
                message: "Create a view-only link to this chat.",
                onClose: {
                    activePopup = nil
                },
                message1: AppsFlyerHelper.createChatShareLink(
                    chatID: viewModel.currentChatId ?? chatID ?? 0,
                    memberID: viewModel.selectedMemberDetail?.id ?? memberId ?? 0,
                    memberName: currentMemberName,
                    isCaseChat: isCaseChat
                )
            )
     
        case .delete:

            DeleteChatPopUpView(
                title: "Delete Chat",
                message: "Once deleted, this chat and its medical history cannot be recovered.",
                warningTxt: "Deleting may affect AI’s ability to suggest based on your past health history.",

                onClose: {
                    activePopup = nil
                },

                onDelete: {

                    guard let chatId = viewModel.currentChatId else {
                        print("currentChatId nil")
                        return
                    }

                    print("DELETE CLICKED \(chatId)")

                    viewModel.showActivity = true

                    viewModel.deleteChat(
                        chat_id: chatId
                    ) { success in

                        DispatchQueue.main.async {

                            viewModel.showActivity = false

                            if success {
                                print("DELETE SUCCESS")
                                viewModel.toastMessage = "Chat deleted successfully"
                                viewModel.showToast = true
                                DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                                    viewModel.showToast = false
                                    viewModel.messages.removeAll()
                                    viewModel.currentChatId = nil
                                    activePopup = nil
                                    coordinator.pop()
                                }
                            } else {

                                print("DELETE FAILED")

                            }
                        }
                    }
                }
            )
        }
    }
}

private extension ChatScreenView {
    var headerView: some View {
        HStack {
            Image("HomeLogo")
                .resizable()
                .frame(width: 30, height: 30)
            
            HStack(spacing: 1){
                Text("CureMe")
                    .font(.custom("Urbanist-SemiBold", size: 22))
                    .foregroundColor(Color(hex: "#211C64"))
                
                Text("GPT")
                    .font(.custom("Urbanist-SemiBold", size: 22))
                    .foregroundColor(Color(hex: "#3C3C3C"))
            }
            
            Spacer()
            
            HStack(spacing: 12) {
                // Notification Button
                Button(action: {
                    coordinator.pop()
                }) {
                    Image("Close Button 1")
                        .resizable()
                        .frame(width: 45, height: 45)
                    
                }
                // Profile Section
                Button(action: {
                    guard isMemberExist else { return }
                    isInputFocused = false
                    keyboardOverlap = 0
                    dismissKeyboard()
                    sideMenuVM.membersListDetails = viewModel.membersListDetails
                    sideMenuVM.selectedMemberDetail = viewModel.selectedMemberDetail
                    sideMenuVM.openMenu()
                }) {
                    Image("SideMenu")
                        .resizable()
                        .frame(width: 45, height: 45)
                }
                .disabled(!isMemberExist)
                .opacity(isMemberExist ? 1.0 : 0.4)
                
                Button {
                    guard isMemberExist else { return }
                    showContextMenu = true
                } label: {
                    Image("Dot")
                        .resizable()
                        .frame(width: 45, height: 45)
                }
                .anchorPreference(
                    key: DotButtonAnchorKey.self,
                    value: .bounds
                ) { $0 }
                .frame(width: 45, height: 45)
                .disabled(!isMemberExist)
                .opacity(isMemberExist ? 1.0 : 0.4)
            }
        }
        .padding(12)
        .background(Color.white)
    }
}

struct DotButtonAnchorKey: PreferenceKey {
    static var defaultValue: Anchor<CGRect>? = nil
    
    static func reduce(
        value: inout Anchor<CGRect>?,
        nextValue: () -> Anchor<CGRect>?
    ) {
        value = nextValue() ?? value
    }
}

struct ChatImageBubbleView: View {
    
    let image: UIImage
    let text: String?
    let sender: ChatSender
    
    let onCopy: () -> Void
    let onEdit: () -> Void
    
    var body: some View {
        HStack {
            if sender == .ai {
                bubbleWithActions
                Spacer(minLength: 40)
            } else {
                Spacer(minLength: 40)
                bubbleWithActions
            }
        }
        .padding(.horizontal)
        .padding(.vertical, 4)
    }
    
    private var bubbleWithActions: some View {
        VStack(alignment: sender == .user ? .trailing : .leading, spacing: 6) {
            bubbleContent
        }
        .frame(maxWidth: UIScreen.messageMaxWidth,
               alignment: sender == .user ? .trailing : .leading)
    }
    
    private var bubbleContent: some View {
        VStack(alignment: .trailing, spacing: 6) {
            
            Image(uiImage: image)
                .resizable()
                .scaledToFill()
                .frame(width: 206, height: 210)
                .clipped()
                .cornerRadius(16)
            
            if let text {
                VStack(alignment: .trailing, spacing: 6) {
                    Text(text)
                        .font(.system(size: 14))
                        .foregroundColor(.white)
                }
                .padding(10)
                .background(
                    sender == .user
                    ? Color(hex: "#4338CA")
                    : Color(hex: "#F8F8F8")
                )
                .cornerRadius(16)
            }
        }
        .padding(10)
        .cornerRadius(20)
        .frame(
            maxWidth: UIScreen.messageMaxWidth,
            alignment: sender == .user ? .trailing : .leading
        )
    }
}

struct ChatContextMenu: View {
    
    let proposedPosition: CGPoint
    let onDismiss: () -> Void
    let onAction: (ChatPopupType) -> Void
    let isCaseChat: Bool
    var isFromSharedLink: Bool = false
    var isMemberExist: Bool = true
    
    private let menuWidth: CGFloat = 180
    private let menuHeight: CGFloat = 120
    
    var body: some View {
        GeometryReader { geo in
            
            let safePosition = clampedPosition(
                proposed: proposedPosition,
                screen: geo.size
            )
            
            ZStack {
                Color.black.opacity(0.001)
                    .ignoresSafeArea()
                    .onTapGesture { onDismiss() }
                
                VStack(alignment: .leading, spacing: 14) {
                    
                    Button {
                        onDismiss()
                        onAction(.switchCase)
                    } label: {
                       // menuRow("proicons_file", "Switch to Case")
                        menuRow(
                              "proicons_file",
                              isCaseChat
                              ? "Switch to Normal"
                              : "Switch to Case"
                          )
                    }
                    .disabled(!isMemberExist)
                    .opacity(isMemberExist ? 1.0 : 0.4)
                    
                    if !isFromSharedLink {
                        Button {
                            onDismiss()
                            onAction(.share)
                        } label: {
                            menuRow("share 1", "Share Chat")
                        }
                        .disabled(!isMemberExist)
                        .opacity(isMemberExist ? 1.0 : 0.4)
                    }
                    
                    Button {
                        onDismiss()
                        onAction(.delete)
                    } label: {
                        menuRow("Delete", "Delete", isDestructive: true)
                    }
                }
                .frame(width: menuWidth)
                .padding()
                .background(Color(hex: "#F4F4F4"))
                .cornerRadius(20)
                .shadow(radius: 10)
                .position(safePosition)
            }
        }
    }
    
    private func menuRow(
        _ icon: String,
        _ title: String,
        isDestructive: Bool = false
    ) -> some View {
        HStack {
            Image(icon)
                .resizable()
                .frame(width: 24, height: 24)
                .foregroundColor(isDestructive ? .red : Color(hex: "#374151"))
            
            Text(title)
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundColor(isDestructive ? .red : Color(hex: "#374151"))
            
            Spacer()
        }
        .padding(.vertical, 6)
    }
    
    // MARK: - Clamp
    private func clampedPosition(
        proposed: CGPoint,
        screen: CGSize
    ) -> CGPoint {
        
        let x = min(
            max(menuWidth / 2 + 16, proposed.x),
            screen.width - menuWidth / 2 - 16
        )
        
        let y = min(
            max(menuHeight / 2 + 16, proposed.y),
            screen.height - menuHeight / 2 - 16
        )
        
        return CGPoint(x: x, y: y)
    }
}

//#Preview {
//    ChatScreenView(initialText: "", chatID: 0, memberId: 0)
//}

private extension ChatScreenView {

    var bottomInputView: some View {

        HStack(alignment: .center, spacing: 12) {

            VStack(spacing: 6) {

                // IMAGE PREVIEW
                if let attachment = viewModel.selectedAttachment {
                    HStack {
                        ZStack(alignment: .topTrailing) {
                            switch attachment {
                            case .image(let image):
                                Image(uiImage: image)
                                    .resizable()
                                    .scaledToFill()
                                    .frame(width: 106, height: 106)
                                    .clipped()
                                    .cornerRadius(16)
                                
                            case .document(let url):
                                HStack(spacing: 8) {
                                    Image(systemName: "doc.text.fill")
                                        .font(.system(size: 24))
                                        .foregroundColor(.red)
                                    
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(url.lastPathComponent)
                                            .font(.custom("Urbanist-Medium", size: 12))
                                            .foregroundColor(.black)
                                            .lineLimit(1)
                                        Text("PDF Document")
                                            .font(.custom("Urbanist-Regular", size: 10))
                                            .foregroundColor(.gray)
                                    }
                                }
                                .padding(12)
                                .background(Color.white)
                                .cornerRadius(12)
                                .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
                                .frame(height: 106)
                            }

                            Button {
                                viewModel.selectedAttachment = nil
                            } label: {

                                Image("CrossButton")
                                    .font(.system(size: 4, weight: .regular))
                                    .foregroundColor(.white)
                                    .frame(width: 20, height: 20)
                                    .background(Color.black.opacity(0.7))
                                    .clipShape(Circle())

                            }
                            .offset(x: 3, y: -3)
                        }

                        Spacer()
                    }
                    .padding(.horizontal, 14)
                    .padding(.top, 10)
                }

                // SEE TEXT
                if speech.isRecording {

                    Button("See text") {

                        showText = true
                        viewModel.inputText =
                        speech.Voicetext
                    }
                    .font(.system(size: 13))
                }

                HStack(spacing: 10) {

                    // ATTACH
                    Button {

                        viewModel.showAttachmentSheet = true

                    } label: {

                        Image("clip")
                            .resizable()
                            .frame(width: 18, height: 18)
                    }

                    // CANCEL RECORDING
                    if speech.isRecording {

                        Button {

                            speech.stop()
                            speech.reset()

                            showText = false
                            viewModel.inputText = ""

                        } label: {

                            Image(systemName: "xmark")
                        }
                    }

                    // INPUT
                    if speech.isRecording {

                        if showText {

                            Text(
                                speech.Voicetext.isEmpty
                                ? "Listening..."
                                : speech.Voicetext
                            )
                            .frame(
                                maxWidth: .infinity,
                                alignment: .leading
                            )

                        } else {

                            HStack(spacing: 4) {

                                ForEach(0..<30) { _ in

                                    Capsule()
                                        .frame(
                                            width: 2,
                                            height:
                                            CGFloat.random(
                                                in: 6...16
                                            )
                                        )
                                }
                            }
                            .frame(maxWidth: .infinity)
                        }

                    } else {

                        TextField(
                            isMemberExist ? "Ask anything" : "Member no longer exists",
                            text: $viewModel.inputText
                        )
                        .font(
                            .custom(
                                "Urbanist-Regular",
                                size: 14
                            )
                        )
                        .focused($isInputFocused)
                        .disabled(!isMemberExist)
                    }
                }
                .padding(.horizontal, 14)
                .padding(.vertical, 14)

            }
            .background(Color(hex: "#E9E4F2"))
            .cornerRadius(30)

            // SEND / MIC
            ZStack {

                let hasText =
                !viewModel.inputText
                    .trimmingCharacters(
                        in:
                        .whitespacesAndNewlines
                    )
                    .isEmpty

                let hasAttachment =
                viewModel.selectedAttachment
                != nil

                if !hasText &&
                    !hasAttachment {

                    Image("AudioIcon")
                        .resizable()
                        .frame(
                            width: 50,
                            height: 55
                        )
                        .scaleEffect(
                            speech.isRecording
                            ? 1.1 : 1
                        )
                        .animation(
                            .easeInOut(
                                duration: 0.2
                            ),
                            value:
                            speech.isRecording
                        )
                        .onTapGesture {

                            if !speech.isRecording {
                                showText = false
                                speech.start()
                            }
                        }

                } else {

                    Button {

                        speech.stop()
                        speech.reset()

                        showText = false

                        UIApplication.shared.sendAction(
                            #selector(
                                UIResponder
                                    .resignFirstResponder
                            ),
                            to: nil,
                            from: nil,
                            for: nil
                        )

                        let message =
                        viewModel.inputText
                            .trimmingCharacters(
                                in:
                                .whitespacesAndNewlines
                            )

                        guard
                            !message.isEmpty ||
                            viewModel
                            .selectedAttachment
                            != nil
                        else {
                            return
                        }

                        viewModel.inputText =
                        message

                        let requestType = isCaseChat
                        ? "case"
                        : "normal"

                        viewModel.sendMessage(
                            type: requestType,
                            familyMemberID:
                                viewModel.selectedMemberDetail?.id ?? 0
                        )

                        viewModel.inputText = ""
                        viewModel.selectedAttachment = nil

                    } label: {

                        Image("ArrowIcon")
                            .resizable()
                            .frame(
                                width: 50,
                                height: 55
                            )
                    }
                }
            }
        }
        .padding(.horizontal)
        .padding(.vertical, 8)
        .padding(.bottom, 8)
        .disabled(!isMemberExist)
        .opacity(!isMemberExist ? 0.5 : 1)

        .onChange(
            of: speech.isRecording
        ) { recording in

            if !recording {

                if !speech.Voicetext
                    .isEmpty {

                    viewModel.inputText =
                    speech.Voicetext
                }
            }
        }
    }
}

struct ChatDocumentBubbleView: View {
    @EnvironmentObject private var coordinator: Coordinator
    
    let message: ChatMessage
    let fileURL: URL
    let text: String?
    let sender: ChatSender
    let viewModel: ChatScreenViewModel
    
    var body: some View {
        HStack {
            if sender == .ai {
                content
                Spacer(minLength: 40)
            } else {
                Spacer(minLength: 40)
                content
            }
        }
        .padding(.horizontal)
        .padding(.vertical, 4)
    }
    
    var content: some View {
        VStack(alignment: sender == .user ? .trailing : .leading, spacing: 6) {
            // 1. Text bubble first
            if let text = text, !text.isEmpty {
                HStack(alignment: .bottom, spacing: 8) {
                    if sender == .ai {
                        Image("Close Button111")
                            .resizable()
                            .frame(width: 32, height: 32)
                    }
                    
                    Text(text)
                        .font(.system(size: 14))
                        .foregroundColor(sender == .user ? .white : .black)
                        .padding(12)
                        .background(
                            sender == .user
                            ? Color(hex: "#4338CA")
                            : Color(hex: "#F8F8F8")
                        )
                        .cornerRadius(16)
                }
                .frame(
                    maxWidth: UIScreen.messageMaxWidth,
                    alignment: sender == .user ? .trailing : .leading
                )
            }
            
            // 2. New PDF report card below text
            HStack(spacing: 8) {
                if sender == .ai {
                    if text == nil || text?.isEmpty == true {
                        Image("Close Button111")
                            .resizable()
                            .frame(width: 32, height: 32)
                    } else {
                        Spacer()
                            .frame(width: 32) // Matches logo size spacing
                    }
                }
                
                NewPDFBubbleCardView(url: fileURL, sender: sender)
            }
            
            // 3. AI action buttons (Like, Dislike, Copy) below PDF card
            if sender == .ai {
                MessageActionRow(
                    message: message,
                    onLike: { viewModel.likeMessage(message) },
                    onDislike: { viewModel.dislikeMessage(message) },
                    onCopy: { viewModel.copyMessage(message) },
                    onEdit: { viewModel.editMessage(message) }
                )
                .frame(maxWidth: UIScreen.messageMaxWidth, alignment: .leading)
                .padding(.leading, 40)
            }
            
            // 4. User action buttons below PDF card
            if sender == .user {
                HStack {
                    Spacer()
                    
                    Button {
                        viewModel.copyMessage(message)
                    } label: {
                        Image("CopyIcon")
                            .resizable()
                            .frame(width: 30, height: 30)
                    }
                    
                    Button {
                        viewModel.editMessage(message)
                    } label: {
                        Image("PencilIcon")
                            .resizable()
                            .frame(width: 30, height: 30)
                    }
                }
                .frame(maxWidth: UIScreen.messageMaxWidth)
            }
        }
    }
}

struct NewPDFBubbleCardView: View {
    let url: URL
    let sender: ChatSender

    @State private var showPDF = false
    @State private var isDownloading = false

    var body: some View {
        Button {
            showPDF = true
        } label: {
            HStack(spacing: 5) {

                // PDF Icon
                Image("Frame 1272638626")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 60, height: 60)
                    .padding(.leading, -3)

                // File Name
                VStack(alignment: .leading, spacing: 0) {
                    Text(getFilename())
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(Color(hex: "#4C36CD"))
                        .lineLimit(2)
                }

               // Spacer()

                // Download Button
                if isDownloading {
                    ProgressView()
                        .progressViewStyle(
                            CircularProgressViewStyle(
                                tint: Color(hex: "#4C36CD")
                            )
                        )
                        .frame(width: 55, height: 55)
                        .padding(.trailing, 0)
                } else {
                    Button {
                        downloadPDF()
                    } label: {
                            Image("Frame 1272638627")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 60, height: 60)
                            .padding(.trailing, -3)
                                  
                    }
                    .buttonStyle(.plain)
                    .padding(.trailing, 0)
                }
            }
            .frame(width: 270, height: 70) // Increased height
            .background(Color(hex: "#E5E0FA"))
            .clipShape(RoundedRectangle(cornerRadius: 30))
        }
        .buttonStyle(.plain)
        .sheet(isPresented: $showPDF) {
            NavigationView {
                PDFViewer(fileURL: url)
                    .navigationTitle(url.lastPathComponent)
                    .navigationBarTitleDisplayMode(.inline)
                    .toolbar {
                        ToolbarItem(placement: .navigationBarLeading) {
                            Button("Close") {
                                showPDF = false
                            }
                        }
                    }
            }
        }
    }

    private func getFilename() -> String {
        let name = url.lastPathComponent
        if name.starts(with: "report_") &&
            name.lowercased().hasSuffix(".pdf") {
            return "Summary_report.pdf"
        }
        return name.isEmpty ? "Summary_report.pdf" : name
    }

    private func downloadPDF() {
        isDownloading = true

        URLSession.shared.downloadTask(with: url) { localURL, response, error in

            DispatchQueue.main.async {
                isDownloading = false
            }

            guard let localURL = localURL, error == nil else {
                print("Download failed: \(error?.localizedDescription ?? "Unknown error")")
                return
            }

            let fileManager = FileManager.default
            let documentsURL = fileManager.urls(for: .documentDirectory,
                                                in: .userDomainMask).first!
            let destinationURL = documentsURL.appendingPathComponent(getFilename())

            do {
                if fileManager.fileExists(atPath: destinationURL.path) {
                    try fileManager.removeItem(at: destinationURL)
                }

                try fileManager.moveItem(at: localURL, to: destinationURL)

                DispatchQueue.main.async {
                    let activityVC = UIActivityViewController(
                        activityItems: [destinationURL],
                        applicationActivities: nil
                    )

                    if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                       let rootVC = windowScene.windows.first(where: \.isKeyWindow)?.rootViewController {
                        if let popover = activityVC.popoverPresentationController {
                            popover.sourceView = rootVC.view
                            popover.sourceRect = CGRect(
                                x: rootVC.view.bounds.midX,
                                y: rootVC.view.bounds.midY,
                                width: 0,
                                height: 0
                            )
                            popover.permittedArrowDirections = []
                        }

                        rootVC.present(activityVC, animated: true)
                    }
                }
            } catch {
                print("Error saving file: \(error.localizedDescription)")
            }
        }.resume()
    }
}
