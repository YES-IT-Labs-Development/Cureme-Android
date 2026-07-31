//
//  SideMenuView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/01/26.
//


// MARK: - MAIN VIEW

import SwiftUI

struct SideMenuView: View {
    
    // FROM PARENT
    @ObservedObject var viewModel: SideMenuViewModel
    var onNewCaseChat: () -> Void
    var onNewChat: () -> Void
    
   // var onSelectChat: (Int) -> Void
    
    //let onSelectChat: (Int, FamilyDetail?) -> Void
    
    let onSelectChat: (Int, FamilyDetail?, Bool) -> Void
    var onDeleteChat: ((Int) -> Void)? = nil
    var isFromSharedLink: Bool = false
   
    // LOCAL STATE
    @State private var showHistoryDropdown = false
   
    @State private var selectedUser: String = ""
    
    @State private var showUserDropdown = false
    
    @State private var hasLoadedChatList = false
    
    @State private var hasLoadedUsers = false
    @FocusState private var isSearchFocused: Bool
    
    var body: some View {
        ZStack {
          
        VStack(alignment: .leading, spacing: 16) {
            
            // MARK: - LOGO
            HStack {
                Image("HomeLogo")
                    .resizable()
                    .frame(width: 30, height: 30)
                
                Text("CureMeGPT")
                    .font(.custom("Urbanist-SemiBold", size: 18))
                    .foregroundColor(Color(hex: "#211C64"))
            }
            .padding(.top, 40)
            
            // MARK: - SEARCH
            TextField("Search", text: $viewModel.searchText)
                .padding()
                .background(Color.gray.opacity(0.1))
                .cornerRadius(30)
                .focused($isSearchFocused)
            
            // MARK: - BUTTONS
            
            Button(action: {
                onNewChat()   //FIXED
            }) {
                menuButton(title: "New Chat", icon: "plus.bubble")
            }
            
            Button(action: {
                onNewCaseChat()   //FIXED
            }) {
                menuButton(title: "New Case Chat", icon: "doc.text")
            }
            
            // MARK: - HISTORY DROPDOWN
            
            VStack(alignment: .leading, spacing: 6) {
                HStack {
                    Text(viewModel.selectedHistory.title ?? "Select Type")
                        .padding(10)
                        .font(.custom("Urbanist-Medium", size: 17))
                    //.foregroundColor(Color(hex: "#4338CA")) // same as user dropdown
                        .foregroundColor(.black)
                    
                    Image("DropDown")
                        .padding(.trailing, 10)
                        .rotationEffect(.degrees(showHistoryDropdown ? 180 : 0))
                    
                    Spacer()
                }
                .onTapGesture {
                    withAnimation {
                        showHistoryDropdown.toggle()
                        // CLOSE OTHERS
                        showUserDropdown = false
                        viewModel.showHistoryMenu = false
                    }
                }
                
                if showHistoryDropdown {
                    VStack(spacing: 0) {
                        
                        dropdownItem( title: "Chat History",
                                      type: .chat)
                        Divider()
                        
                        dropdownItem(title: "Case Chat History",
                                     type: .caseChat )
                    }
                    .padding(10)
                    .background(Color.white)
                    .cornerRadius(12)
                    .shadow(radius: 5)
                    
                }
            }
            
            // MARK: - USER DROPDOWN
            
            UserDropdownView(
                isOpen: $showUserDropdown,
                selectedUser: $viewModel.selectedMemberDetail,
                users: viewModel.membersListDetails,
                isFromSharedLink: isFromSharedLink
            )

            .onChange(of: viewModel.selectedMemberDetail) { selectedUser in
                guard let user = selectedUser else { return }
                
                showHistoryDropdown = false
                viewModel.showHistoryMenu = false
                
                // ✅ Call API based on selected user
                let isMyself = user.relationship?.lowercased() == "myself" || user.id == 0 || user.id == Int(UserDetail.shared.getID())
                if isMyself {
                    viewModel.getchatList { success in
                        if success {
                            print("Loaded chat list for myself")
                        }
                    }
                } else {
                    viewModel.userFamilyChatList { success in
                        if success {
                            print("Loaded family chat list for user:", user.name ?? "")
                        }
                    }
                }
            }
            .padding(.horizontal, 0)
          
            // MARK: - LIST

            ScrollView {
                VStack(spacing: 12) {
                    
                    if currentList().isEmpty {
                        VStack {
                            Spacer()
                            
                            Text("No Chat Found")
                                .font(.custom("Urbanist-Medium", size: 15))
                                .foregroundColor(.black.opacity(0.25))
                            
                            Spacer()
                        }
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                    } else {
                        
                        ForEach(currentList(), id: \.id) { item in
                            historyRow(
                                item: item,
                                isMenuOpen: viewModel.selectedHistoryItem?.id == item.id
                            )
                        }
                    }
                }
                .padding(.top, 10)
                .coordinateSpace(name: "global")
            }
            
            Spacer()
            
                
        }
        .padding()
        .frame(maxWidth: .infinity, maxHeight: .infinity) // 🔥 IMPORTANT
            
        .sheet(isPresented: $viewModel.showRenamePopup) {
            RenameBottomSheet(viewModel: viewModel)
        }
            
            if viewModel.showActivity {
                CustomLoderView(isVisible: $viewModel.showActivity)
                    .ignoresSafeArea()
            }
        
    }
        
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(Color(UIColor.systemGray6))
        .cornerRadius(30)
        
        .onAppear {
            viewModel.userWithFamilyDetailsAPI { success in
                if success {
                    if viewModel.selectedMemberDetail == nil {
                        viewModel.selectedMemberDetail = viewModel.membersListDetails.first
                    }
                    let isMyself = viewModel.selectedMemberDetail?.relationship?.lowercased() == "myself" || viewModel.selectedMemberDetail?.id == 0 || viewModel.selectedMemberDetail?.id == Int(UserDetail.shared.getID())
                    if isMyself {
                        viewModel.getchatList { _ in }
                    } else {
                        viewModel.userFamilyChatList { _ in }
                    }
                }
            }
        }
        .onChange(of: viewModel.isMenuOpen) { isOpen in
            if isOpen {
                let isMyself = viewModel.selectedMemberDetail?.relationship?.lowercased() == "myself" || viewModel.selectedMemberDetail?.id == 0 || viewModel.selectedMemberDetail?.id == Int(UserDetail.shared.getID())
                if isMyself {
                    viewModel.getchatList { _ in }
                } else {
                    viewModel.userFamilyChatList { _ in }
                }
            } else {
                isSearchFocused = false
            }
        }
        
        if viewModel.showHistoryMenu,
           let position = viewModel.historyMenuPosition {
            
            SideMenuContextMenu(
                proposedPosition: position,
                onDismiss: {
                    viewModel.showHistoryMenu = false
                    viewModel.historyMenuPosition = nil
                    viewModel.selectedHistoryItem = nil   // 🔥 IMPORTANT
                },

                onRename: {
                    if let item = viewModel.selectedHistoryItem {
                        viewModel.renamingItem = item
                        viewModel.renameText = item.title ?? ""
                        viewModel.showRenamePopup = true
                    }
                    
                    viewModel.showHistoryMenu = false
                    viewModel.selectedHistoryItem = nil
                },
                onShare: {
                    if let item = viewModel.selectedHistoryItem {
                        viewModel.sharingItem = item
                        viewModel.showSharePopup = true
                    }
                    viewModel.showHistoryMenu = false
                    viewModel.selectedHistoryItem = nil   // 🔥 IMPORTANT
                },
                onDelete: {
                    if let item = viewModel.selectedHistoryItem {
                        deleteItem(item)
                    }
                    viewModel.showHistoryMenu = false
                    viewModel.selectedHistoryItem = nil   // 🔥 IMPORTANT
                }
            )
            .zIndex(999)
        }
    }
    
    // MARK: - HELPERS
    
//    func currentList() -> [ChatData] {
//        if viewModel.selectedHistory == .chat {
//            return viewModel.chatHistory
//        } else {
//            return viewModel.caseHistory
//        }
//    }
    
    func currentList() -> [ChatData] {

        let list =
        viewModel.selectedHistory == .chat
        ? viewModel.chatHistory
        : viewModel.caseHistory

        let search =
        viewModel.searchText
            .trimmingCharacters(in: .whitespacesAndNewlines)
            .lowercased()

        guard !search.isEmpty else {
            return list
        }

        return list.filter { item in

            (item.title ?? "")
                .lowercased()
                .contains(search)

        }
    }
    
    func menuButton(title: String, icon: String) -> some View {
        HStack {
            Image(systemName: icon)
            Text(title)
                .font(.headline)
        }
        .foregroundColor(.white)
        .frame(maxWidth: .infinity)
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
        .cornerRadius(30)
    }
 
    
    func dropdownItem(title: String, type: HistoryType) -> some View {
        
        Button {
            viewModel.selectedHistory = type
            viewModel.searchText = ""   // ← add this
            showHistoryDropdown = false
        } label: {
            
            HStack {
                
                Text(title)
                    .font(.custom("Urbanist-Medium", size: 15))
                    .foregroundColor(
                        viewModel.selectedHistory == type
                        ? Color(hex: "#4338CA")   // ✅ Selected
                        : .black                  // ✅ Unselected
                    )
                
                Spacer()
                
                if viewModel.selectedHistory == type {
                    Image("Checkmark")
                        .resizable()
                        .frame(width: 22, height: 22)
                }
            }
            .padding(.vertical, 10)
            .padding(.horizontal, 20)
        }
        
    }
    
    @State private var selectedItem: String? = nil
    @State private var showMenuForItem: String? = nil

    func historyRow(
        item: ChatData,
        isMenuOpen: Bool
    ) -> some View {

        HStack {

            // LEFT ICON
            Image(systemName: "clock")
                .font(.system(size: 14))
                .frame(width: 28, height: 28)
                .background(Color.gray.opacity(0.2))
                .clipShape(Circle())

            // TITLE
            Text(item.title ?? "")
                .font(.custom("Urbanist-Medium", size: 14))
                .lineLimit(1)

            Spacer()

            // ✅ 3 DOT BUTTON (CENTER + CORRECT POSITION)
            Button(action: {
                
                
            }) {
                GeometryReader { geo in
                    
                    Image(isMenuOpen ? "ph_dots-three-outline-light 1" : "ph_dots-three-outline-light")
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: 30, height: 30)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .contentShape(Rectangle())

                        .onTapGesture {
                            let frame = geo.frame(in: .global)
                            
                            let anchor = CGPoint(
                                x: frame.midX - 40,
                                y: frame.maxY + 80
                            )
                            
                            //  TOGGLE LOGIC
                            if viewModel.selectedHistoryItem?.id == item.id &&
                                viewModel.showHistoryMenu {
                                
                                // CLOSE
                                viewModel.showHistoryMenu = false
                                viewModel.selectedHistoryItem = nil
                                
                            } else {
                                // OPEN NEW
                                viewModel.selectedHistoryItem = item
                                viewModel.historyMenuPosition = anchor
                                viewModel.showHistoryMenu = true
                            }
                        }
                }
            }
            .frame(width: 40, height: 50) // full row height → perfect vertical center
        }
//        .onTapGesture {
//            if let chatId = item.id {
//                onSelectChat(chatId, viewModel.selectedMemberDetail)   // 👈 TRIGGER
//            }
//        }
        .onTapGesture {
            if let chatId = item.id {

                let isCaseChat =
                    (viewModel.selectedHistory == .caseChat)

                onSelectChat(
                    chatId,
                    viewModel.selectedMemberDetail,
                    isCaseChat
                )
            }
        }
        .padding(.horizontal, 12)
        .frame(height: 50)
        .background(Color(hex: "#996BFE").opacity(0.10))
        .cornerRadius(25)
    }

    struct SideMenuContextMenu: View {
        
        let proposedPosition: CGPoint
        let onDismiss: () -> Void
        let onRename: () -> Void
        let onShare: () -> Void
        let onDelete: () -> Void
        
        private let menuWidth: CGFloat = 160
        private let menuHeight: CGFloat = 80
        
        var body: some View {
            GeometryReader { geo in
                
                let safePosition = clampedPosition(
                    proposed: proposedPosition,
                    screen: geo.size
                )
                
                ZStack {
                    
                    // BACKDROP
                    Color.black.opacity(0.001)
                        .ignoresSafeArea()
                        .onTapGesture { onDismiss() }
                    
                    VStack(alignment: .leading, spacing: 8) {
                       
                        // RENAME
                       menuRow("Pencil", "Rename")
                           .onTapGesture {
                               onRename()
                           }

                       // SHARE CHAT
                       menuRow("share", "Share Chat")
                           .onTapGesture {
                               onShare()
                           }

                       // DELETE
                       menuRow("Delete", "Delete", isDestructive: true)
                           .onTapGesture {
                               onDelete()
                           }
                    }
                    .padding()
                    .frame(width: menuWidth)
                    .background(Color(hex: "#F4F4F4"))
                    .cornerRadius(16)
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
            
            HStack(spacing: 10) {
                
                Image(icon) // ✅ ASSET IMAGE
                    .resizable()
                    .renderingMode(.original)
                    .frame(width: 22, height: 22)
                
                Text(title)
                    .font(.custom("Urbanist-Medium", size: 15))
                    .foregroundColor(isDestructive ? .red : Color(hex: "#374151"))
                
                Spacer()
            }
            .padding(.vertical, 8)
            .contentShape(Rectangle()) // ✅ full row clickable
        }
        

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
    

    func deleteItem(_ item: ChatData) {
        
        guard let id = item.id else { return } // 👈 Ensure ID exists
        
        viewModel.deleteChat(chat_id: id) { success in
            if success {
                
                // ✅ Remove from main list
                viewModel.getChatList.removeAll { $0.id == id }
                
                // ✅ Remove from chat history
                viewModel.chatHistory.removeAll { $0.id == id }
                
                // ✅ Remove from case history
                viewModel.caseHistory.removeAll { $0.id == id }
                
                // Trigger callback to notify parent view
                onDeleteChat?(id)
            }
        }
    }
}

struct UserDropdownView: View {
    
    //@State private var isOpen = false
    
    @Binding var isOpen: Bool   // ✅ controlled from parent

    @Binding var selectedUser: FamilyDetail?
    let users: [FamilyDetail]
    var isFromSharedLink: Bool = false
    
    var body: some View {

        VStack(alignment: .leading, spacing: 6) {
            
            // HEADER
            HStack(spacing: 6) {

                Text(
                    (selectedUser?.relationship?.isEmpty == false)
                    ? "\(selectedUser?.name ?? "") (\(selectedUser?.relationship ?? ""))"
                    : (selectedUser?.name ?? "Select User")
                )
                .font(.custom("Urbanist-Medium", size: 15))
                .foregroundColor(Color(hex: "#4338CA"))

                Spacer()
                
                if !isFromSharedLink {
                    Image("DropDown")
                        .rotationEffect(.degrees(isOpen ? 180 : 0))
                }
            }
            .padding(.horizontal, 16)
            .frame(height: 50)
            .background(Color.white)
            .overlay(
                RoundedRectangle(cornerRadius: 25)
                    .stroke(Color.gray.opacity(0.5), lineWidth: 1.5)
            )
            .cornerRadius(25)
            .zIndex(1)
            .onTapGesture {
                if !isFromSharedLink {
                    withAnimation {
                        isOpen.toggle()
                    }
                }
            }

            // DROPDOWN (THIS MUST BE HERE)
            if isOpen {
                Group {
                    if users.count > 5 {
                        ScrollView {
                            dropdownContent
                        }
                        .frame(maxHeight: 225)
                    } else {
                        dropdownContent
                    }
                }
                .background(Color.white)
                .cornerRadius(12)
                .shadow(radius: 6)
            
            }
        }
        .frame(maxWidth: .infinity)
        
    }
    private var dropdownContent: some View {
        VStack(spacing: 0) {
            ForEach(users, id: \.id) { user in
                Button {
                    selectedUser = user
                    isOpen = false
                } label: {
                    HStack {
                        
                        Text(
                            user.relationship?.isEmpty == false
                            ? "\(user.name ?? "") (\(user.relationship ?? ""))"
                            : "\(user.name ?? "")"
                        )
                        .font(.custom("Urbanist-Medium", size: 15))
                        .foregroundColor(
                            user.id == selectedUser?.id
                            ? Color(hex: "#4338CA")   //  selected
                            : .black                  //  Rest
                        )
                        
                        Spacer()
                        
                        if user.id == selectedUser?.id {
                            Image("Checkmark")
                                .resizable()
                                .frame(width: 22, height: 22)
                        }
                    }
                    .padding(.vertical, 10)
                    .padding(.horizontal, 15)
                }
                
                if user.id != users.last?.id {
                    Divider()
                }
            }
        }
       
    }
}


struct RenameBottomSheet: View {
    
    @ObservedObject var viewModel: SideMenuViewModel
    @FocusState private var isFocused: Bool
    
    var body: some View {
        
        VStack(spacing: 25) {
            
            // 🔘 HANDLE BAR (WhatsApp style)
            Capsule()
                .fill(Color.gray.opacity(0.4))
                .frame(width: 40, height: 5)
                .padding(.top, 8)
            
            // TITLE
            Text("Rename Chat")
               
                .font(.custom("Urbanist-Medium", size: 22))
            
            // TEXTFIELD
            TextField("Enter name", text: $viewModel.renameText)
                .padding()
                .background(Color.gray.opacity(0.25))
                .cornerRadius(30)
                .focused($isFocused)
            
            // BUTTONS
            HStack(spacing: 12) {
                
                // CANCEL
                Button {
                    viewModel.showRenamePopup = false
                } label: {
                    Text("Cancel")
                        .font(.custom("Urbanist-Medium", size: 20))
                        .foregroundColor(Color(hex: "#181B1A"))
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .contentShape(Rectangle()) // 🔥 IMPORTANT
                }
                .overlay(
                    RoundedRectangle(cornerRadius: 26)
                        .stroke(Color.black, lineWidth: 1)
                )
                
                // SAVE
                Button {
                    renameAction()
                } label: {
                    Text("Save")
                        .font(.custom("Urbanist-Medium", size: 20))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
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
                        .cornerRadius(26)
                        .contentShape(Rectangle()) // 🔥 IMPORTANT
                }
            }
        }
        .padding()
        .onAppear {
            isFocused = true // auto open keyboard
        }
        .presentationDetents([.height(220)]) // WhatsApp height
        .presentationDragIndicator(.hidden)  // we added custom
    }
    
    func renameAction() {
        guard let item = viewModel.renamingItem else { return }
        
        let newName = viewModel.renameText.trimmingCharacters(in: .whitespaces)
        if newName.isEmpty { return }
        
        viewModel.renameChatAPI(
            id: "\(item.id ?? 0)",
            newName: newName
        ) { success in
            
            if success {
                // UPDATE UI
                if viewModel.selectedHistory == .chat {
                    if let index = viewModel.chatHistory.firstIndex(where: { $0.id == item.id }) {
                        viewModel.chatHistory[index].title = newName
                    }
                } else {
                    if let index = viewModel.caseHistory.firstIndex(where: { $0.id == item.id }) {
                        viewModel.caseHistory[index].title = newName
                    }
                }
                
                viewModel.showRenamePopup = false
            }
        }
    }
}

// MARK: - SIDE MENU SHARE POPUP VIEW (FULL SCREEN)
struct SideMenuSharePopupView: View {
    @ObservedObject var viewModel: SideMenuViewModel

    var body: some View {
        if viewModel.showSharePopup, let item = viewModel.sharingItem {
            ZStack {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture {
                        viewModel.showSharePopup = false
                        viewModel.sharingItem = nil
                    }
                
                let sharingMemberName: String = {
                    if let familyMemberID = item.familyMemberID, familyMemberID != 0 {
                        if let member = viewModel.membersListDetails.first(where: { $0.id == familyMemberID }) {
                            return member.name ?? ""
                        }
                    }
                    return viewModel.selectedMemberDetail?.name ?? UserDetail.shared.getName()
                }()
                
                SharePopUpView(
                    title: "Share Chat",
                    message: "Create a view-only link to this chat.",
                    onClose: {
                        viewModel.showSharePopup = false
                        viewModel.sharingItem = nil
                    },
                    message1: AppsFlyerHelper.createChatShareLink(
                        chatID: item.id ?? 0,
                        memberID: (item.familyMemberID != nil && item.familyMemberID != 0) ? item.familyMemberID! : (viewModel.selectedMemberDetail?.id ?? Int(UserDetail.shared.getID()) ?? 0),
                        memberName: sharingMemberName,
                        isCaseChat: item.type == .typeCase || viewModel.selectedHistory == .caseChat
                    )
                )
            }
            .zIndex(10000)
        }
    }
}

