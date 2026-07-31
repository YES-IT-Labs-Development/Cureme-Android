//
//  ChatScreenSubviews.swift
//  CureMeGPT
//
//  Created by Antigravity on 09/07/26.
//

import SwiftUI

struct ChatBubbleView: View {
    @EnvironmentObject private var coordinator: Coordinator

    let message: ChatMessage
    let viewModel: ChatScreenViewModel
    let onCopy: () -> Void

    var body: some View {
        HStack {
            if message.sender == .ai {
                bubbleContent
                Spacer(minLength: 40)
            } else {
                Spacer(minLength: 40)
                bubbleContent
            }
        }
        .padding(.horizontal)
        .padding(.vertical, 4)
    }
    
    private var bubbleContent: some View {
        VStack(alignment: .leading, spacing: 8) {

            // TEXT BUBBLE
            if !message.text.isEmpty {
                HStack(alignment: .bottom, spacing: 8) {
                    if message.sender == .ai {
                        Image("Close Button111")
                            .resizable()
                            .frame(width: 32, height: 32)
                    }
                    
                    Text(message.text)
                        .font(.system(size: 14))
                        .foregroundColor(message.sender == .user ? .white : .black)
                        .padding(12)
                        .background(
                            message.sender == .user
                            ? Color(hex: "#4338CA")
                            : Color(hex: "#F8F8F8")
                        )
                        .cornerRadius(16)
                }
                .frame(
                    maxWidth: UIScreen.messageMaxWidth,
                    alignment: message.sender == .user ? .trailing : .leading
                )
            }

            // 👉 EMERGENCY/CRITICAL SCHEDULE APPOINTMENT BUTTON
            if message.sender == .ai,
               let actionType = message.meta?.severity?.lowercased(),
               actionType == "high"  {

                ScheduleButton {
                    coordinator.push(.newAppointmentScheduleView(flow: .new, chatId: viewModel.currentChatId))
                }
                .frame(maxWidth: UIScreen.messageMaxWidth, alignment: .leading)
                .padding(.leading, 40)
            }

            // 👉 SCHEDULE BUTTON (AI ONLY – ONE)
            if message.sender == .ai,
               case .scheduleButton = message.type,
               message.id == viewModel.messages.last(where: {
                   if case .scheduleButton = $0.type { return true }
                   return false
               })?.id {

                ScheduleButton {
                    viewModel.scheduleAppointment()
                }
                .frame(maxWidth: UIScreen.messageMaxWidth, alignment: .leading)
                .padding(.leading, 40)
            }

            // ✅ AI ACTION ICONS
            if message.sender == .ai {
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

            // ✅ USER COPY + EDIT (RESTORED)
            if message.sender == .user {
                HStack {
                    Spacer()

                    Button(action: onCopy) {
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

struct StatusBubble: View {
    let text: String

    var body: some View {
        Text(text)
            .font(.system(size: 13))
            .foregroundColor(.gray)
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            .background(Color.gray.opacity(0.2))
            .cornerRadius(16)
    }
}

struct ScheduleButton: View {
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text("Schedule Appointment")
                .font(.system(size: 14, weight: .semibold))
                .foregroundColor(.white)
                .padding(.vertical, 14)
                .frame(maxWidth: 260)
                .background(
                    Image("BackgroundBtn") // Asset name
                         .resizable()
                         .scaledToFill() )
                .cornerRadius(26)
        }
    }
}

struct ChatInputBar: View {
    @Binding var text: String
    let attachment: ChatAttachment?
    let onSend: () -> Void
    let onAttachTap: () -> Void
    let onRemoveAttachment: () -> Void

    private let buttonHeight: CGFloat = 58
    private let cornerRadius: CGFloat = 24
    private let bgColor = Color(hex: "#996BFE").opacity(0.10)

    var body: some View {
        HStack(alignment: .bottom, spacing: 12) {

            // 🔲 MAIN INPUT CONTAINER (IMAGE + TEXT)
            VStack(alignment: .leading, spacing: 8) {

                // 🖼 IMAGE PREVIEW
                if case let .image(image) = attachment {
                    ZStack(alignment: .topTrailing) {
                        Image(uiImage: image)
                            .resizable()
                            .scaledToFill()
                            .frame(width: 106, height: 106)
                            .clipped()
                            .cornerRadius(16)
                            .padding(.leading, 30)
                            .padding(.top, 12)

                        Button {
                            onRemoveAttachment()
                        } label: {
                            Image("CrossButton")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(.white)
                                .frame(width: 15, height: 15)
                                .background(Color.black.opacity(0.6))
                                .clipShape(Circle())
                                .padding(.leading, 4)
                                .padding(.top, 12)
                        }
                        .padding(6)
                    }
                }

                // TEXT INPUT ROW
                HStack(alignment: .bottom, spacing: 10) {
                    Button {
                        onAttachTap()
                    } label: {
                        Image("clip")
                            .resizable()
                            .frame(width: 20, height: 20)
                            .padding(.leading, 12)
                            .padding(.bottom, 12)
                    }

                    TextField("Ask anything", text: $text, axis: .vertical)
                        .font(.custom("Urbanist-Regular", size: 14))
                        .lineLimit(1...5)
                        .padding(.vertical, 12)
                }
                .frame(minHeight: buttonHeight)
            }
            .padding(.trailing, 12)
            .background(bgColor)              // SAME BG FOR IMAGE + TEXT
            .cornerRadius(cornerRadius)       // SINGLE CORNER RADIUS

            // ➤ SEND / BUTTON
            Button {
                if !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty || attachment != nil {
                    onSend()
                }
            } label: {
                Image(
                    (text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty && attachment == nil)
                    ? "AudioIcon"
                    : "ArrowIcon"
                )
                .resizable()
                .frame(width: 50, height: buttonHeight)
            }
        }
        .padding()
    }
}

struct MessageActionRow: View {
    let message: ChatMessage
    let onLike: () -> Void
    let onDislike: () -> Void
    let onCopy: () -> Void
    let onEdit: () -> Void

    var body: some View {
        HStack {
            Button(action: onCopy) {
                Image("CopyIcon")
                    .resizable()
                    .frame(width: 30, height: 30)
            }
            
            if !message.isDisliked {
                Button(action: onLike) {
                    Image(message.isLiked ? "LikedIcon" : "LikeIcon")
                        .resizable()
                        .frame(width: 30, height: 30)
                }
            }

            if !message.isLiked {
                Button(action: onDislike) {
                    Image(message.isDisliked ? "FilledDislikeIcon" : "DislikeIcon")
                        .resizable()
                        .frame(width: 30, height: 30)
                }
            }
         }
        .font(.system(size: 14))
        .foregroundColor(.gray)
        .padding(.top, 4)
    }
}

struct AskForDropdownView: View {
    @ObservedObject var viewModel: ChatScreenViewModel
    @ObservedObject var sideMenuVM: SideMenuViewModel
    let isCaseChat: Bool
    var isFromSharedLink: Bool = false
    var isMemberExist: Bool = true

    var body: some View {
        ZStack {
            // 👇 BACKGROUND TAP LAYER
            if viewModel.isDropdownOpen {
                Color.black.opacity(0.001) // invisible but tappable
                    .ignoresSafeArea()
                    .onTapGesture {
                        withAnimation {
                            viewModel.isDropdownOpen = false
                        }
                    }
            }

            VStack {
                VStack(alignment: .leading, spacing: 6) {
                    // Selected user (Capsule)
                    HStack(spacing: 10) {
                        if let profilePhoto = viewModel.selectedMemberDetail?.profilePhoto, !profilePhoto.isEmpty {
                            Image.loadProfileImage(profilePhoto.imgFullPath(), width: 28, height: 28, cornerRadius: 14)
                        } else {
                            Image("Frame 1")
                                .resizable()
                                .frame(width: 28, height: 28)
                        }

                        Button {
                            if !isCaseChat && !isFromSharedLink && isMemberExist {
                                withAnimation {
                                    viewModel.isDropdownOpen.toggle()
                                }
                            }
                        } label: {
                            HStack(spacing: 6) {
                                Text(selectedMemberDisplayText)
                                    .font(.custom("Urbanist-Medium", size: 15))
                                    .foregroundColor(Color(hex: "#4338CA"))

                                Spacer()

                                if !isCaseChat && !isFromSharedLink && isMemberExist {
                                    Image("DropDown")
                                        .resizable() // 👈 IMPORTANT
                                        .frame(width: 9, height: 6) // increase size here
                                        .rotationEffect(
                                            .degrees(viewModel.isDropdownOpen ? 180 : 0)
                                        )
                                }
                            }
                        }
                        .disabled(isCaseChat || isFromSharedLink || !isMemberExist)
                    }
                    .frame(height: 44)
                    .padding(.horizontal, 15)
                    .frame(maxWidth: .infinity)
                    .background(
                        Capsule()
                            .fill(Color(hex: "#996BFE").opacity(0.20))
                    )

                    // Dropdown
                    if viewModel.isDropdownOpen {
                        Group {
                            if viewModel.membersListDetails.count > 5 {
                                ScrollView {
                                    dropdownContent
                                }
                                .frame(maxHeight: 225)
                            } else {
                                dropdownContent
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .background(
                            RoundedRectangle(cornerRadius: 8)
                                .fill(Color.white)
                                .shadow(color: .black.opacity(0.25), radius: 4)
                        )
                    }
                }
                .frame(maxWidth: 225)
            }
            .frame(maxWidth: .infinity)
        }
    }

    private var selectedMemberDisplayText: String {
        guard let member = viewModel.selectedMemberDetail else { return "" }
        let rawName = member.name ?? ""
        let rawRel = member.relationship ?? ""
        let isMyselfRel = rawRel.lowercased() == "myself" || rawName.lowercased() == "myself"
        
        if isFromSharedLink {
            if isMyselfRel {
                let finalName = (rawName.isEmpty || rawName.lowercased() == "myself") ? UserDetail.shared.getName() : rawName
                return finalName.isEmpty ? "User" : finalName
            } else {
                return rawRel.isEmpty ? rawName : "\(rawName) (\(rawRel))"
            }
        } else {
            return rawRel.isEmpty ? rawName : "\(rawName) (\(rawRel))"
        }
    }

    private var dropdownContent: some View {
        VStack(spacing: 0) {
            ForEach(viewModel.membersListDetails) { user in
                Button {
                    let targetType: TypeEnum = isCaseChat ? .typeCase : .normal
                    let matchedChat = sideMenuVM.getChatList.first { chat in
                        guard chat.type == targetType else { return false }
                        
                        let isMyself = user.relationship?.lowercased() == "myself" || user.id == 0 || (user.id != nil && user.id == Int(UserDetail.shared.getID()))
                        if isMyself {
                            return chat.familyMemberID == nil || chat.familyMemberID == 0 || (chat.familyMemberID != nil && chat.familyMemberID == Int(UserDetail.shared.getID()))
                        } else {
                            return chat.familyMemberID == user.id
                        }
                    }

                    if viewModel.selectedMemberDetail?.id == user.id {
                        withAnimation {
                            viewModel.isDropdownOpen = false
                        }
                        
                        if let _ = viewModel.currentChatId {
                            viewModel.getChatMessageListAPI { _ in }
                        } else if viewModel.isSameMember(id1: user.id, id2: viewModel.originalMemberId), let originalChatId = viewModel.originalChatId {
                            viewModel.currentChatId = originalChatId
                            viewModel.getChatMessageListAPI { _ in }
                        } else if let matchedChat = matchedChat, let chatId = matchedChat.id {
                            viewModel.currentChatId = chatId
                            viewModel.getChatMessageListAPI { _ in }
                        }
                        return
                    }

                    if viewModel.isSameMember(id1: user.id, id2: viewModel.originalMemberId), let originalChatId = viewModel.originalChatId {
                        viewModel.selectedMemberDetail = user
                        viewModel.currentChatId = originalChatId
                        viewModel.getChatMessageListAPI { success in
                            if success {
                                print("Loaded original chat \(originalChatId)")
                            }
                        }
                        withAnimation {
                            viewModel.isDropdownOpen = false
                        }
                        return
                    }

                    viewModel.selectedMemberDetail = user

                    if let matchedChat = matchedChat, let chatId = matchedChat.id {
                        viewModel.currentChatId = chatId
                        viewModel.getChatMessageListAPI { success in
                            if success {
                                print("Loaded existing chat \(chatId) for member \(user.name ?? "")")
                            }
                        }
                    } else {
                        viewModel.messages.removeAll()        // clear UI chat
                        viewModel.currentChatId = nil         // reset chat session
                    }

                    withAnimation {
                        viewModel.isDropdownOpen = false
                    }
                } label: {
                    HStack {
                        Text(  user.relationship?.isEmpty == false
                               ? "\(user.name ?? "") (\(user.relationship ?? ""))"
                                 : "\(user.name ?? "")")
                            .font(.custom("Urbanist-Medium", size: 15))
                            .foregroundColor(Color(hex: "#4338CA"))

                        Spacer()

                        if viewModel.selectedMemberDetail?.id == user.id {
                            Image("Checkmark")
                                .resizable()
                                .frame(width: 22, height: 22)
                        }
                    }
                    .padding(.vertical, 10)
                    .padding(.horizontal, 15)
                }

                Divider()
            }
        }
    }
}

struct ChatRemoteImageBubbleView: View {
    let imageURL: URL
    let text: String?
    let sender: ChatSender
    
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
            if imageURL.pathExtension.lowercased() == "pdf" {
                PDFBubbleCardView(url: imageURL, sender: sender)
            } else {
                Image.loadImage5(imageURL.absoluteString)
                    .scaledToFill()
                    .frame(width: 206, height: 210)
                    .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
                    .overlay(
                        RoundedRectangle(cornerRadius: 16, style: .continuous)
                            .stroke(Color.clear, lineWidth: 0)
                    )
            }
            
            if let text, !text.isEmpty {
                Text(text)
                    .font(.system(size: 14))
                    .foregroundColor(.white)
                    .padding(10)
                    .background(Color(hex: "#4338CA"))
                    .cornerRadius(12)
            }
        }
        .padding(10)
    }
}

struct PDFBubbleCardView: View {
    let url: URL
    let sender: ChatSender
    @State private var showPDF = false
    
    var body: some View {
        Button {
            showPDF = true
        } label: {
            HStack(spacing: 12) {
                ZStack {
                    Circle()
                        .fill(Color.red.opacity(0.1))
                        .frame(width: 40, height: 40)
                    
                    Image(systemName: "doc.text.fill")
                        .font(.system(size: 18))
                        .foregroundColor(.red)
                }
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(url.lastPathComponent)
                        .font(.custom("Urbanist-Medium", size: 14))
                        .foregroundColor(.black)
                        .lineLimit(1)
                    
                    Text("PDF Document")
                        .font(.custom("Urbanist-Regular", size: 12))
                        .foregroundColor(.gray)
                }
                
                Spacer(minLength: 8)
                
                Image(systemName: "eye.fill")
                    .font(.system(size: 16))
                    .foregroundColor(Color(hex: "#4338CA"))
            }
            .padding(12)
            .background(Color(hex: "#F8F8F8"))
            .cornerRadius(16)
            .frame(width: 240)
        }
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
}

struct ChatAppointmentCardView: View {
    let appointment: Appointment

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text(appointment.title)
                .font(.system(size: 16, weight: .semibold))
                .foregroundColor(.white)

            Text(appointment.doctor)
                .font(.system(size: 14))
                .foregroundColor(.white.opacity(0.9))

            HStack {
                Label(appointment.date, systemImage: "calendar")
                Spacer()
                Label(appointment.time, systemImage: "clock")
            }
            .font(.system(size: 12))
            .foregroundColor(.white)

            Label(appointment.address, systemImage: "location")
                .font(.system(size: 12))
                .foregroundColor(.white.opacity(0.9))

            Label(appointment.note, systemImage: "info.circle")
                .font(.system(size: 12))
                .foregroundColor(.white.opacity(0.9))
        }
        .padding()
        .background(Color(hex: "#4F46E5"))
        .cornerRadius(20)
        .frame(maxWidth: UIScreen.main.bounds.width * 0.7)
    }
}
