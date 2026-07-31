//
//  ChatScreenViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/01/26.
//

import Foundation
import Combine
import SwiftUI
import PDFKit
final class ChatScreenViewModel: ObservableObject {
    
    @Published var chatDataResult : ChatDataModel?
    
    @Published var currentChatId: Int? = nil
    var originalChatId: Int? = nil
    var originalMemberId: Int? = nil
    
    func isSameMember(id1: Int?, id2: Int?) -> Bool {
        guard let id1 = id1, let id2 = id2 else { return false }
        if id1 == id2 { return true }
        
        let id1IsMyself = id1 == 0 || id1 == Int(UserDetail.shared.getID())
        let id2IsMyself = id2 == 0 || id2 == Int(UserDetail.shared.getID())
        return id1IsMyself && id2IsMyself
    }
    
    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false

    private var cancellables = Set<AnyCancellable>()
    
    @Published var membersListDetails: [FamilyDetail] = []
    @Published var selectedMemberDetail: FamilyDetail?

    @Published var messages: [ChatMessage] = []
   
  
    @Published var isDropdownOpen: Bool = false
    @Published var inputText: String = ""
    @Published var selectedAttachment: ChatAttachment?
    @Published var showAttachmentSheet = false
    @Published var showImagePicker = false
    @Published var showDocumentPicker = false
    
    @Published var showToast = false
    @Published var toastMessage = ""
    
    // MARK: - Actions
        func likeMessage(_ message: ChatMessage) {
            guard let msgId = message.id, msgId > 0 else { return }
            let newIsLiked = !message.isLiked
            
            update(message) {
                $0.isLiked = newIsLiked
                if newIsLiked {
                    $0.isDisliked = false
                }
            }
            
            let status = newIsLiked ? 1 : 2
            
            APIManager.shared.responseLikeDislikeAPI(chatMessageId: msgId, status: status)
                .sink { completion in
                    if case .failure(let error) = completion {
                        print("❌ Like API failed:", error.localizedDescription)
                    }
                } receiveValue: { response in
                    print("✅ Like API response:", response.message ?? "")
                }
                .store(in: &cancellables)
        }

        func dislikeMessage(_ message: ChatMessage) {
            guard let msgId = message.id, msgId > 0 else { return }
            let newIsDisliked = !message.isDisliked
            
            update(message) {
                $0.isDisliked = newIsDisliked
                if newIsDisliked {
                    $0.isLiked = false
                }
            }
            
            let status = newIsDisliked ? 2 : 1
            
            APIManager.shared.responseLikeDislikeAPI(chatMessageId: msgId, status: status)
                .sink { completion in
                    if case .failure(let error) = completion {
                        print("❌ Dislike API failed:", error.localizedDescription)
                    }
                } receiveValue: { response in
                    print("✅ Dislike API response:", response.message ?? "")
                }
                .store(in: &cancellables)
        }

        func copyText(_ text: String) {
            UIPasteboard.general.string = text
            self.toastMessage = "Copied"
            self.showToast = true
            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                self.showToast = false
            }
        }

        func copyMessage(_ message: ChatMessage) {
            copyText(message.text)
        }

        func editMessage(_ message: ChatMessage) {
            inputText = message.text
        }

        private func update(_ message: ChatMessage, action: (inout ChatMessage) -> Void) {
            guard let index = messages.firstIndex(where: { $0.id == message.id }) else { return }
            action(&messages[index])
        }
    
    private let appointment = Appointment(
            title: "Normal Check-up",
            doctor: "Dr. Emily Rodriguez",
            date: "09/01/2025",
            time: "10:30 AM",
            address: "Health Care Hub, 20 Cooper Square, New York, NY 10003, USA",
            note: "Regular 6-month check-up with cleaning"
        )

    
    
    func deleteChat(chat_id: Int,  completion: @escaping (Bool) -> Void) {
        
        showActivity = true
        
        APIManager.shared.deleteChatAPI(chatID: chat_id)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                self?.showActivity = false
                
                switch result {
                case .failure(let error):
                    self?.errorMessage = error.localizedDescription
                    self?.isPresentAlert = true
                    completion(false)
                    
                case .finished:
                    break
                }
                
            } receiveValue: { [weak self] response in
                
                if response.success ?? false {
                    completion(true)
                } else {
                    self?.errorMessage = response.message
                    self?.showToast = true
                    self?.toastMessage = "\(response.message ?? "")"
                    
                    self?.isPresentAlert = true
                    completion(false)
                }
            }
            .store(in: &cancellables)
    }
    
    
    //  for getChatMessageListAPI
        func getChatMessageListAPI(completion: @escaping (Bool) -> Void) {
           // isLoading = true
            self.showActivity = true
            APIManager.shared.getChatMessageListAPI(currentChatId: self.currentChatId )
                .receive(on: DispatchQueue.main)
                .sink { [weak self] result in
                    guard let self = self else { return }
                    self.showActivity = false
                    switch result {
                    case .failure(let error):
                        self.errorMessage = error.localizedDescription
                        // Handle connection issues
                        if ((self.errorMessage?.contains("no local endpoint")) != nil) {
                           
                            self.errorMessage = "Internal Server Error. \nPlease try again."
                        }
                        
                        self.isPresentAlert = true
                        completion(false)
                    case .finished:
                        print("API call finished")
                    }
                } receiveValue: { [weak self] response in
                    guard let self = self else { return }

                    if response.success ?? false {
                        
                        let apiMessages = response.data?.data ?? []
                        
                        
                        self.messages = apiMessages.compactMap { item in
                            
                            let sender: ChatSender =
                                item.role?.lowercased() == "user" ? .user : .ai
                            
                            let isFileMessage = item.message == "User uploaded a file"
                            
                            var imageURL: URL? = nil
                            
                            // ✅ 1. docsPath
                            if let path = item.docsPath, !path.isEmpty {
                                imageURL = URL(string: path.imgFullPath())
                                print(imageURL ?? "","imageURL")
                            }
                            
                            // ❗ 2. Fallback (IMPORTANT)
                            // Agar backend ne docsPath nahi diya
                            if imageURL == nil && isFileMessage {
                                
                                // ⚠️ TEMP FIX: reuse last known image base
                                print("⚠️ Missing docsPath for file message")
                                
                                // skip OR show placeholder
                            }
                            
                            // ✅ IMAGE / DOCUMENT
                            if let url = imageURL {
                                let isPDF = url.pathExtension.lowercased() == "pdf" || item.docsType?.lowercased() == "pdf"
                                if isPDF {
                                    return ChatMessage(
                                        id: item.id ?? 0,
                                        sender: sender,
                                        text: item.message ?? "",
                                        suggestions: nil,
                                        type: .document(url, item.message),
                                        isLiked: item.likeDislikeStatus == 1,
                                        isDisliked: item.likeDislikeStatus == 2
                                    )
                                } else {
                                    return ChatMessage(
                                        id: item.id ?? 0,
                                        sender: sender,
                                        text: "",
                                        suggestions: nil,
                                        type: .remoteImage(url, nil),
                                        isLiked: item.likeDislikeStatus == 1,
                                        isDisliked: item.likeDislikeStatus == 2
                                    )
                                }
                            }
                            
                            // ❗ IMPORTANT: Avoid showing "User uploaded a file"
                            if isFileMessage {
                                return nil // skip fake text
                            }
                            
                            // ✅ TEXT
                            return ChatMessage(
                                id: item.id ?? 0,
                                sender: sender,
                                text: item.message ?? "",
                                suggestions: nil,
                                type: .text,
                                isLiked: item.likeDislikeStatus == 1,
                                isDisliked: item.likeDislikeStatus == 2,
                                meta: item.meta
                            )
                        }

                        completion(true)
                        
                    } else {
                        self.errorMessage = response.message ?? "Unknown error"
                        self.isPresentAlert = true
                        completion(false)
                    }
                }
                
               
                .store(in: &cancellables)
        }
    
    //  for GetFamilyMember
        func userWithFamilyDetailsAPI(completion: @escaping (Bool) -> Void) {
           // isLoading = true
            self.showActivity = true
            APIManager.shared.userWithFamilyDetails()
                .receive(on: DispatchQueue.main)
                .sink { [weak self] result in
                    guard let self = self else { return }
                    self.showActivity = false
                    switch result {
                    case .failure(let error):
                        self.errorMessage = error.localizedDescription
                        // Handle connection issues
                        if ((self.errorMessage?.contains("no local endpoint")) != nil) {
                           
                            self.errorMessage = "Internal Server Error. \nPlease try again."
                        }
                        
                        self.isPresentAlert = true
                        completion(false)
                    case .finished:
                        print("API call finished")
                    }
                } receiveValue: { [weak self] response in
                    guard let self = self else { return }

                    if response.success ?? false {
                        var combinedList: [FamilyDetail] = []
                        // Add self user (jh)
                        if let user = response.data?.userDetails {
                            let selfMember = FamilyDetail(
                                id: 0,
                                name: "\(user.name ?? "")",
                                relationship: "MySelf",   // 👈 Important
                                profilePhoto: user.profilePhoto?.imgFullPath() ?? UserDetail.shared.getProfileImg()
                            )
                            combinedList.append(selfMember)
                        }
                        
                        // Add family members
                        combinedList.append(contentsOf: response.data?.familyDetails ?? [])
                        
                        self.membersListDetails = combinedList
                        
                        print(self.membersListDetails, "Member List Details")
                        
                        completion(true)
                        
                    } else {
                        self.errorMessage = response.message ?? "Unknown error"
                        self.isPresentAlert = true
                        completion(false)
                    }
                }
                .store(in: &cancellables)
        }

    
    private func extractTextFromPDF(at url: URL) -> String {
        guard let document = PDFDocument(url: url) else { return "" }
        var extractedText = ""
        for i in 0..<document.pageCount {
            if let page = document.page(at: i), let pageText = page.string {
                extractedText += pageText + "\n"
            }
        }
        return extractedText.trimmingCharacters(in: .whitespacesAndNewlines)
    }
    
    func sendMessage(type: String, familyMemberID: Int) {

        let trimmedText = inputText.trimmingCharacters(in: .whitespacesAndNewlines)

        guard !trimmedText.isEmpty || selectedAttachment != nil else {
            return
        }

        var imageData: Data? = nil
        var pickedImage: UIImage? = nil
        var pickedDocumentURL: URL? = nil
        var pdfText = ""

        // ✅ HANDLE IMAGE
        if case let .image(image) = selectedAttachment {
            imageData = image.jpegData(compressionQuality: 0.7)
            pickedImage = image
        }
        
        // ✅ HANDLE DOCUMENT (PDF)
        if case let .document(url) = selectedAttachment {
            do {
                print("DEBUG PDF: Selected URL is \(url)")
                if let attr = try? FileManager.default.attributesOfItem(atPath: url.path) {
                    print("DEBUG PDF: Size on disk is \(attr[.size] ?? 0) bytes")
                } else {
                    print("DEBUG PDF: Could not get attributes of file at path \(url.path)")
                }
                
                let isAccessing = url.startAccessingSecurityScopedResource()
                let data = try Data(contentsOf: url)
                if isAccessing {
                    url.stopAccessingSecurityScopedResource()
                }
                imageData = data
                pickedDocumentURL = url
                
                print("DEBUG PDF: Data read size is \(data.count) bytes")
                if data.count >= 20 {
                    let firstBytes = data.prefix(20)
                    print("DEBUG PDF: First 20 bytes: \(firstBytes.map { String(format: "%02hhx ", $0) }.joined())")
                    if let string = String(data: firstBytes, encoding: .utf8) {
                        print("DEBUG PDF: First 20 bytes as UTF8: \(string)")
                    }
                }
                
                if url.pathExtension.lowercased() == "pdf" {
                    pdfText = extractTextFromPDF(at: url)
                    print("DEBUG PDF: Extracted \(pdfText.count) characters of text")
                } else if url.pathExtension.lowercased() == "txt" {
                    if let text = try? String(contentsOf: url, encoding: .utf8) {
                        pdfText = text
                        print("DEBUG TXT: Extracted \(pdfText.count) characters of text")
                    }
                }
            } catch {
                print("Error reading document data: \(error.localizedDescription)")
            }
        }

        // ✅ Generate temporary ID
        let tempId = Int(Date().timeIntervalSince1970 * 1000)

        // ✅ USER MESSAGE
        let userMessage: ChatMessage

        if let image = pickedImage {
            userMessage = ChatMessage(
                id: tempId, // 👈 ADD THIS
                sender: .user,
                text: "",
                suggestions: nil,
                type: .image(image, trimmedText.isEmpty ? nil : trimmedText)
            )
        } else if let documentURL = pickedDocumentURL {
            userMessage = ChatMessage(
                id: tempId,
                sender: .user,
                text: "",
                suggestions: nil,
                type: .document(documentURL, trimmedText.isEmpty ? nil : trimmedText)
            )
        } else {
            userMessage = ChatMessage(
                id: tempId, // 👈 ADD THIS
                sender: .user,
                text: trimmedText,
                suggestions: nil,
                type: .text
            )
        }

        messages.append(userMessage)

        // RESET INPUT
        inputText = ""
        selectedAttachment = nil

        // Append typing indicator
        let typingMessageId = Int(Date().timeIntervalSince1970 * 1000) + 1
        let typingMessage = ChatMessage(
            id: typingMessageId,
            sender: .ai,
            text: "",
            suggestions: nil,
            type: .typing
        )
        messages.append(typingMessage)

        let apiMessage: String
        if !pdfText.isEmpty {
            if trimmedText.isEmpty {
                apiMessage = "[PDF Content]:\n\(pdfText)"
            } else {
                apiMessage = "\(trimmedText)\n\n[PDF Content]:\n\(pdfText)"
            }
        } else {
            apiMessage = trimmedText
        }

        // ✅ API CALL
        sendChatAPI(
            message: apiMessage,
            type: type,
            familyMemberID: familyMemberID,
            imageData: imageData
        ) { [weak self] success in
            guard let self = self else { return }

            if success {
                let reply = self.chatDataResult?.message ?? "No response"

                let isPDF = self.chatDataResult?.docsType?.lowercased() == "pdf" ||
                            (self.chatDataResult?.fileURL?.lowercased().hasSuffix(".pdf") ?? false)

                let messageType: ChatMessageType
                if isPDF, let fileURLString = self.chatDataResult?.fileURL, let fileURL = URL(string: fileURLString) {
                    messageType = .document(fileURL, reply)
                } else {
                    messageType = .text
                }

                DispatchQueue.main.async {
                    self.messages.removeAll(where: { $0.id == typingMessageId })
                    self.messages.append(
                        ChatMessage(
                            id: self.chatDataResult?.messageID ?? typingMessageId,
                            sender: .ai,
                            text: reply,
                            suggestions: nil,
                            type: messageType,
                            meta: self.chatDataResult?.meta
                        )
                    )
                }
            } else {
                DispatchQueue.main.async {
                    self.messages.removeAll(where: { $0.id == typingMessageId })
                }
            }
        }
    }
    
    //  for familyMember List
    func sendChatAPI(
        message: String,
        type: String,
        familyMemberID: Int,
        imageData: Data?,
        completion: @escaping (Bool) -> Void
    ) {
            // self.showActivity = true
        APIManager.shared.sendChatAPI(message: message, type: type, family_member_id: familyMemberID, currentChatId: self.currentChatId, imageData: imageData)
                .receive(on: DispatchQueue.main)
                .sink { [weak self] result in
                    guard let self = self else { return }
                    self.showActivity = false
                    switch result {
                    case .failure(let error):
                        self.errorMessage = error.localizedDescription
                        // Handle connection issues
                        if ((self.errorMessage?.contains("no local endpoint")) != nil) {
                           
                            self.errorMessage = "Internal Server Error. \nPlease try again."
                        }
                        
                        self.isPresentAlert = true
                        completion(false)
                    case .finished:
                        print("API call finished")
                    }
                } receiveValue: { [weak self] response in
                    guard let self = self else { return }
                    
                    if response.success ?? false {
                      
                        self.chatDataResult = response.data
                        
                        if let chatId = response.data?.chatID {
                            self.currentChatId = chatId
                        }
                        
                        print(chatDataResult ?? "","chatDataResult")
                        completion(true) // ✅ ADD THIS
                    } else {
                        self.errorMessage = response.message ?? "Unknown error"
                        self.isPresentAlert = true
                        completion(false)
                    }
                }
                
                .store(in: &cancellables)
        }

    func scheduleAppointment() {

        let baseId = Int(Date().timeIntervalSince1970 * 1000)

        messages.append(
            ChatMessage(
                id: baseId, // 👈 ADD THIS
                sender: .ai,
                text: "",
                suggestions: nil,
                type: .appointmentCard(appointment)
            )
        )

        messages.append(
            ChatMessage(
                id: baseId + 1, // 👈 ADD THIS (ensure different ID)
                sender: .ai,
                text: "",
                suggestions: nil,
                type: .status("Appointment has been successfully scheduled.")
            )
        )
    }
}

