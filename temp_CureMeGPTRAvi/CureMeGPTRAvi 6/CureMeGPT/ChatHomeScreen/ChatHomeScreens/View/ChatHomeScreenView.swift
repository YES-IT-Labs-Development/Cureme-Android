//
//  ChatHomeScreenView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/01/26.
//

import SwiftUI
import IQKeyboardManagerSwift

struct ChatHomeScreenView: View {
    @StateObject private var viewModel = ChatHomeScreenViewModel()
    @State private var inputText = ""
    @StateObject private var sideMenuVM = SideMenuViewModel()
    let menuWidth = UIScreen.main.bounds.width * 0.75
    @EnvironmentObject private var coordinator: Coordinator
    @State private var isDropdownOpen = false
    @State private var showNewCasePopup = false
    
    @State private var showText = false
    
    @State private var selectedAttachment: ChatAttachment?
    @State private var showAttachmentSheet = false
    @State private var showImagePicker = false
    @State private var showDocumentPicker = false
 
    @StateObject private var speech = SpeechRecognizer()
    @FocusState private var isInputFocused: Bool
    
    @EnvironmentObject private var tabVM: TabViewModel
    
    var body: some View {
        ZStack(alignment: .trailing) {
            VStack(spacing: 0) {
                
                // MARK: - Header
                headerView
                
                ScrollView {
                    VStack(spacing: 20) {
                        
                        // MARK: - Logo
                        Image("HomeLogo")
                            .resizable()
                            .frame(width: 128, height: 128)
                            .padding(.top, 40)
                        
                        // MARK: - Greeting
                        VStack(spacing: 10) {
                            HStack(spacing: 0){
                                // Text("Good afternoon,")
                                Text("\(viewModel.greetingText),")
                                    .font(.custom("Urbanist-Medium", size: 26))
                                    .foregroundColor(.black)
                                
                                Text(" \(viewModel.selectedMemberDetail?.name ?? "")")
                                    .font(.custom("Urbanist-Medium", size: 26))
                                    .foregroundStyle(
                                        LinearGradient(
                                            colors: [
                                                Color(red: 67/255, green: 56/255, blue: 202/255),
                                                Color(red: 33/255, green: 28/255, blue: 100/255)
                                            ],
                                            startPoint: .leading,
                                            endPoint: .trailing
                                        )
                                    )
                            }
                            
                            ZStack(alignment: .topLeading) {

                                VStack(spacing: 4) {

                                    // MAIN VIEW
                                    HStack(spacing: 6) {
                                        if let profilePhoto = viewModel.selectedMemberDetail?.profilePhoto, !profilePhoto.isEmpty {
                                            Image.loadProfileImage(profilePhoto.imgFullPath(), width: 28, height: 28, cornerRadius: 14)
                                        } else {
                                            Image("Frame 1")
                                                .resizable()
                                                .frame(width: 28, height: 28)
                                        }
                                        
                                        Text("Ask for : ")
                                            .font(.custom("Urbanist-Medium", size: 16))
                                            // Spacer()
                                      //  Text("\(viewModel.selectedMemberDetail?.name ?? "" )")
                                        Text(
                                            viewModel.selectedMemberDetail?.relationship?.isEmpty == false
                                            ? "\(viewModel.selectedMemberDetail?.name ?? "") (\(viewModel.selectedMemberDetail?.relationship ?? ""))"
                                            : "\(viewModel.selectedMemberDetail?.name ?? "")"
                                        )
                                            .font(.custom("Urbanist-Medium", size: 16))
                                        
                                        Image("DropDown")
                                            .rotationEffect(.degrees(isDropdownOpen ? 180 : 0))
                                            .padding(.trailing, 10)
                                    }
                                    .padding(.vertical, 10)
                                    .padding(.horizontal, 14)   //  FIXED LEADING/TRAILING
                                    .background(Color(hex: "#996BFE").opacity(0.10))
                                    .cornerRadius(30)
                                    .onTapGesture {
                                        withAnimation {
                                            isDropdownOpen.toggle()
                                        }
                                    }

                                    // DROPDOWN
                                    if isDropdownOpen {
                                        ScrollView {
                                            VStack(spacing: 0) {
                                                ForEach(viewModel.membersListDetails, id: \.id) { member in
                                                    Button {
                                                        viewModel.selectedMemberDetail = member
                                                        withAnimation {
                                                            isDropdownOpen = false
                                                        }
                                                    } label: {
                                                        HStack {
                                                            Text(
                                                                member.relationship?.isEmpty == false
                                                                ? "\(member.name ?? "") (\(member.relationship ?? ""))"
                                                                : "\(member.name ?? "")"
                                                            )
                                                                .font(.custom("Urbanist-Regular", size: 14))
                                                                .foregroundColor(.black)

                                                            Spacer()

                                                            if viewModel.selectedMemberDetail?.id == member.id {
                                                                Image("Checkmark")
                                                                    .resizable()
                                                                    .frame(width: 22, height: 22)
                                                            }
                                                        }
                                                        .padding(.vertical, 12)
                                                        .padding(.horizontal, 14)
                                                    }

                                                    if member.id != viewModel.membersListDetails.last?.id {
                                                        Divider()
                                                            .padding(.leading, 14)
                                                    }
                                                }
                                            }
                                        }
                                        .frame(maxWidth: .infinity)
                                        .frame(
                                            height: viewModel.membersListDetails.count <= 5
                                            ? CGFloat(viewModel.membersListDetails.count) * 40
                                            : 160
                                        )
                                        .background(Color.white)
                                        .cornerRadius(12)
                                        .shadow(radius: 5)
                                    }
                                }
                            }
                            .padding(.horizontal, 36)   //  GLOBAL LEADING/TRAILING FOR SCREEN

                        }
                        
                        // MARK: - New Case Chat Button
                        Button {
                            showNewCasePopup = true
                        } label: {
                            HStack {
                                Image("mage_file-3 1")
                                    .resizable()
                                    .frame(width: 24, height: 24)
                                Text("New Case Chat")
                                    .font(.custom("Urbanist-Medium", size: 16))
                                    .foregroundColor(.white)
                            }
                            .frame(maxWidth: .infinity, minHeight: 30)
                            .padding()
                            .background(
                                LinearGradient(
                                    colors: [
                                        Color(red: 67/255, green: 56/255, blue: 202/255),
                                        Color(red: 33/255, green: 28/255, blue: 100/255)
                                    ],
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                            .cornerRadius(45)
                            .foregroundColor(.white)
                            .cornerRadius(14)
                        }
                        .padding(.horizontal, 40)
                        
                        // MARK: - Suggested Questions
                        VStack(spacing: 16) {
 
                            // Main Suggested Questions
                            ForEach(viewModel.questions) { question in
                                QuestionCard(text: question.text) {
                                    print("Sending memberId:", viewModel.selectedMemberDetail?.id ?? 0)
                                    coordinator.push(.chatScreenView(chatId: 0, initialText: question.text, memberId: viewModel.selectedMemberDetail?.id ?? 0,selectedAttachment))
                                }
                            }

                            // Get Fit Questions
                            ForEach(viewModel.getFitQuestions) { question in
                                GetFitQuestionCard(text: question.text) {
                                    print("Sending memberId:", viewModel.selectedMemberDetail?.id ?? 0)
                                    coordinator.push(.chatScreenView(chatId: 0, initialText: question.text, memberId: viewModel.selectedMemberDetail?.id ?? 0, selectedAttachment ))
                                }
                            }
                            
                        }
                        .padding(.horizontal)
                    }
                    .padding(.bottom, 20)
                }
                .disableScrollBounce()
                // MARK: - Bottom Input
                bottomInputView
                    .opacity(sideMenuVM.isMenuOpen ? 0 : 1)
                    .allowsHitTesting(!sideMenuVM.isMenuOpen)
            }
            .allowsHitTesting(!sideMenuVM.isMenuOpen)
            
            // DARK OVERLAY
            if sideMenuVM.isMenuOpen {
                Color.black.opacity(0.3)
                    .ignoresSafeArea()
                    .onTapGesture {
                        sideMenuVM.closeMenu()
                        
                    }
            }
            
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
                    print("Sending memberId:", viewModel.selectedMemberDetail?.id ?? 0)
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
                        coordinator.push(.chatScreenView(chatId: 0, initialText: "", memberId: viewModel.selectedMemberDetail?.id ?? 0,selectedAttachment ))
                    }
                },

//                onSelectChat: { chatId, member in
//                    
//                    sideMenuVM.closeMenu()
//                    
//                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
//                        
//                        // ✅ SET SELECTED MEMBER FROM SIDE MENU
////                        if let member = member {
////                            viewModel.selectedMemberDetail = member
////                        }
//                        if let member = member,
//                           let matched =
//                            viewModel.membersListDetails.first(where: {
//                                $0.id == member.id
//                            }) {
//
//                            viewModel.selectedMemberDetail = matched
//                        }
//                        print("Selected member from side menu:", viewModel.selectedMemberDetail?.id ?? 0)
//
//                        coordinator.push(
//                            .chatScreenView(
//                                chatId: chatId,
//                                initialText: "",
//                                memberId: viewModel.selectedMemberDetail?.id ?? 0,
//                                selectedAttachment
//                            )
//                        )
//                    }
//                }
                onSelectChat: { chatId, member, isCaseChat in

                    sideMenuVM.closeMenu()

                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {

                        // ✅ Set selected member
                        if let member = member {
                            let matched = viewModel.membersListDetails.first(where: { m in
                                if m.id == member.id { return true }
                                let mIsMyself = m.relationship?.lowercased() == "myself" || m.id == 0 || m.id == Int(UserDetail.shared.getID())
                                let memberIsMyself = member.relationship?.lowercased() == "myself" || member.id == 0 || member.id == Int(UserDetail.shared.getID())
                                return mIsMyself && memberIsMyself
                            })
                            if let matched = matched {
                                viewModel.selectedMemberDetail = matched
                            }
                        }

                        print("Selected member from side menu:",
                              viewModel.selectedMemberDetail?.id ?? 0)

                        coordinator.push(
                            .chatScreenView(
                                chatId: chatId,
                                initialText: isCaseChat ? "casechat" : "",
                                memberId: viewModel.selectedMemberDetail?.id ?? 0,
                                selectedAttachment
                            )
                        )
                    }
                }
            )
                .frame(width: menuWidth)
                .offset(x: sideMenuVM.isMenuOpen ? 0 : menuWidth)
                .animation(.easeInOut(duration: 0.25), value: sideMenuVM.isMenuOpen)
                .ignoresSafeArea()
            
                .onAppear {
                    IQKeyboardManager.shared.isEnabled = false
                    speech.stop()          // ✅ stop mic before navigating
                    speech.reset()
                    tabVM.isTabBarHidden = true   // 👈 HIDE TAB BAR
                    viewModel.familyListQuestionPromptAPI { success in
                        if success {
                            if viewModel.selectedMemberDetail == nil {
                                viewModel.selectedMemberDetail = viewModel.membersListDetails.first
                            }
//                            if viewModel.selectedMemberDetail == nil {
//                                viewModel.selectedMemberDetail = viewModel.membersListDetails.first
//                            }
                        }
                    }
                }
                .onDisappear {
                    IQKeyboardManager.shared.isEnabled = true
                }
            if viewModel.showActivity {
                CustomLoderView(isVisible: $viewModel.showActivity)
                    .ignoresSafeArea()
            }
        }
        .padding(.bottom, tabVM.isTabBarHidden ? 0 : 80)
        .ignoresSafeArea(.container, edges: tabVM.isTabBarHidden ? .bottom : [])
        .animation(.easeInOut(duration: 0.1), value: sideMenuVM.isMenuOpen)
        .toolbar(.hidden, for: .tabBar)
        .keyboardDoneButton()
        
        

        .onChange(of: speech.isRecording) { isRecording in
            if !isRecording {
                if !speech.Voicetext.isEmpty {
                    inputText = speech.Voicetext
                } else {
                    inputText = ""
                }
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
                dismissKeyboard()
            }
        }
        
        .onChange(of: viewModel.selectedMemberDetail) { newMember in
            if let newMember = newMember {
                sideMenuVM.selectedMemberDetail = newMember
            }
        }
        
        .onChange(of: sideMenuVM.selectedMemberDetail) { newMember in
            if let newMember = newMember {
                viewModel.selectedMemberDetail = newMember
            }
        }
        .sheet(isPresented: $showAttachmentSheet) {
            AttachmentSheetView(
                onGallery: {
                    showAttachmentSheet = false
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                        showImagePicker = true
                    }
                },
                onDocument: {
                    showAttachmentSheet = false
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                        showDocumentPicker = true
                    }
                }
            )
        }

        .sheet(isPresented: $showImagePicker) {
            ImagePicker { image in
                selectedAttachment = .image(image)
            }
        }

        .sheet(isPresented: $showDocumentPicker) {
            ChatDocumentPicker { url in
                selectedAttachment = .document(url)
            }
        }
        .overlay {
            SideMenuSharePopupView(viewModel: sideMenuVM)

            if showNewCasePopup {
                ZStack {
                    Color.black.opacity(0.4)
                        .ignoresSafeArea()
                        .onTapGesture {
                            showNewCasePopup = false
                        }
                    
                    let memberName = viewModel.selectedMemberDetail?.name ?? ""
                    let relationship = viewModel.selectedMemberDetail?.relationship ?? ""
                    let relationshipStr = relationship.isEmpty ? "" : " (\(relationship))"
                    
                    NewCaseChatPopUpView(
                        title: "Start a New Case Chat?",
                        message: "This new case chat will be created only for \(memberName)\(relationshipStr). Once created, you cannot switch members in the middle. The full case history will be saved in \(memberName)\(relationshipStr)’s records.",
                        onClose: {
                            showNewCasePopup = false
                        },
                        createCaseChat: {
                            showNewCasePopup = false
                            let selectedMemberId = viewModel.selectedMemberDetail?.id ?? 0
                            coordinator.push(.chatScreenView(chatId: 0, initialText: "casechat", memberId: selectedMemberId, selectedAttachment))
                        }
                    )
                }
                .zIndex(9999)
            }
        }
    }

    private func dismissKeyboard() {
        UIApplication.shared.sendAction(
            #selector(UIResponder.resignFirstResponder),
            to: nil,
            from: nil,
            for: nil
        )
    }
}

private extension ChatHomeScreenView {
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
                Button {
                    tabVM.selectedTab = AppTab.home   // 👈 SWITCH TAB
                       tabVM.isTabBarHidden = false     // 👈 SHOW TAB BAR
                } label: {
                    Image("Close Button 1")
                        .resizable()
                        .frame(width: 45, height: 45)
                }

                // Profile Section
                Button(action: {
                    isInputFocused = false
                    dismissKeyboard()

                    // ✅ sync before opening
                    sideMenuVM.membersListDetails = viewModel.membersListDetails
                    sideMenuVM.selectedMemberDetail = viewModel.selectedMemberDetail
                    
                    sideMenuVM.openMenu()
                }) {
                    Image("SideMenu")
                        .resizable()
                        .frame(width: 45, height: 45)
                }
            }
        }
        .padding()
        .background(Color.white)
    }
}

struct QuestionCard: View {
    let text: String
    
    var onTap: (() -> Void)?   // 👈 ADD

    var body: some View {
        HStack{
            Image("paper")
                .resizable()
                .frame(width: 50, height: 50)
            Text(text)
                .font(.custom("Urbanist-Regular", size: 16))
                .foregroundColor(.black)
                .frame(maxWidth: .infinity, alignment: .leading)
            
                .onTapGesture {          // 👈 HANDLE TAP
                           onTap?()
                       }
        }
        .padding(6)
        .background(Color(hex: "#F8F8F8"))
        .cornerRadius(90)
    }
}

struct GetFitQuestionCard: View {
    let text: String
    var onTap: (() -> Void)?   // 👈 ADD
    var body: some View {
        HStack {
            Image("Frame 127263862711")
                .resizable()
                .frame(width: 50, height: 50)

            Text(text)
                .font(.custom("Urbanist-Regular", size: 16))
                .foregroundColor(.black)
                .frame(maxWidth: .infinity, alignment: .leading)
            
                .onTapGesture {          // 👈 HANDLE TAP
                       onTap?()
                   }
        }
        .padding(6)
        .background(Color(hex: "#F8F8F8"))
        .cornerRadius(90)
    }
}


private extension ChatHomeScreenView {
    
    var bottomInputView: some View {
        
        HStack(alignment: .center, spacing: 12) {
            
            
            // MARK: - INPUT CONTAINER
            VStack(spacing: 6) {
                if let attachment = selectedAttachment {
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
                                selectedAttachment = nil
                            } label: {
                                Image("CrossButton")
                                    .font(.system(size: 4, weight: .regular))
                                    .foregroundColor(.white)
                                    .frame(width: 20, height: 20)
                                    .background(Color.black.opacity(0.7))
                                    .clipShape(Circle())
                            }
                            .offset(x: 3, y: -3) //  perfect top-right corner
                        }
                        
                        Spacer() //  keeps preview on LEFT
                    }
                    .padding(.horizontal, 14)
                    .padding(.top, 10)
                }
                
                // 👁 SEE TEXT (TOP CENTER)
                if speech.isRecording  {
                    Button("See text") {
                       
                        showText = true
                        
                        // important: move voice text to input
                           inputText = speech.Voicetext
                        
                    }
                    .font(.system(size: 13, weight: .regular))
                    .foregroundColor(.black.opacity(0.7))
                    .padding(.top, 5)
                }
                  
                HStack(spacing: 10) {
                    
                    Button {
                        showAttachmentSheet = true
                    } label: {
                        Image("clip")
                            .resizable()
                            .frame(width: 18, height: 18)
                    }
                    
                    // ❌ CANCEL BUTTON
                    if speech.isRecording {
                        Button {
                            speech.stop()          // stop recording
                              speech.reset()         // clear voice text
                              showText = false       // hide text UI
                              inputText = ""         // reset to initial state
                           
                        } label: {
                            Image(systemName: "xmark")
                                .foregroundColor(.black.opacity(0.6))
                                .font(.system(size: 14, weight: .semibold))
                        }
                    }
                    
                    // 🎤 CONTENT AREA
                    if speech.isRecording {
                        
                        if showText {
                            Text(speech.Voicetext.isEmpty ? "Listening..." : speech.Voicetext)
                                .font(.custom("Urbanist-Regular", size: 14))
                                .foregroundColor(.black)
                                .frame(maxWidth: .infinity, alignment: .leading)
                        } else {
                            
                            // 🎧 WAVEFORM (FULL WIDTH)
                            HStack(spacing: 4) {
                                ForEach(0..<30) { _ in
                                    Capsule()
                                        .frame(width: 2, height: CGFloat.random(in: 6...16))
                                        .foregroundColor(.black.opacity(0.6))
                                }
                            }
                            .frame(maxWidth: .infinity)
                        }
                        
                    } else {
                        
                        TextField("Ask anything", text: $inputText)
                            .font(.custom("Urbanist-Regular", size: 14))
                            .focused($isInputFocused)
                    }
                }
                .padding(.horizontal, 14)
                .padding(.vertical, 14)
            }
            .background(Color(hex: "#E9E4F2"))
            .cornerRadius(30) // 🔥 MORE ROUNDED LIKE DESIGN
            
            
            // MARK: - SEND / MIC BUTTON
            ZStack {
                
               // if inputText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                    let hasText = !inputText.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
                    let hasAttachment = selectedAttachment != nil
                if !hasText && !hasAttachment {
                            Image("AudioIcon")
                                .resizable()
                                .frame(width: 50, height: 55)
                                .foregroundColor(.white)
                        .scaleEffect(speech.isRecording ? 1.1 : 1.0)
                        .animation(.easeInOut(duration: 0.2), value: speech.isRecording)
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
                        
                        let selectedMemberId =
                               viewModel.selectedMemberDetail?.id ?? 0

                        print("Sending memberId:", viewModel.selectedMemberDetail?.id ?? 0)
                        coordinator.push(
                            .chatScreenView(
                                chatId: 0,
                                initialText: inputText,
                                memberId: selectedMemberId,selectedAttachment))
                           
                        
                        inputText = ""
                        selectedAttachment = nil
                    } label: {

                                Image("ArrowIcon")
                                    .resizable()
                                    .frame(width: 50, height: 55)
                                    .foregroundColor(.white)
                    }
                }
            }
        }
        .padding(.horizontal)
        .padding(.vertical, 8)
        .padding(.bottom, 20)
    }
}

//#Preview {
//    ChatHomeScreenView()
//}
//
