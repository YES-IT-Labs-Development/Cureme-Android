//
//  ChatScreenModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/01/26.
//

import Foundation
import UIKit

import Foundation

enum ChatPopupType {
    case switchCase
    case share
    case delete
}



enum ChatSender {
    case user
    case ai
}

enum ChatMessageType {
    case text
    case image(UIImage, String?)
    case document(URL, String?)
    case scheduleButton
    case appointmentCard(Appointment)
    case remoteImage(URL, String?) // ✅ ADD THIS
    case status(String)
    case typing
}

struct Appointment {
    let title: String
    let doctor: String
    let date: String
    let time: String
    let address: String
    let note: String
}

struct ChatMessage: Identifiable {
    let id : Int?
   
    let sender: ChatSender
    let text: String
    let suggestions: [String]?
    
    let type: ChatMessageType

    var isLiked: Bool = false
    var isDisliked: Bool = false
    
    let meta: Meta?
    
    init(
        id: Int?,
        sender: ChatSender,
        text: String,
        suggestions: [String]?,
        type: ChatMessageType,
        isLiked: Bool = false,
        isDisliked: Bool = false,
        meta: Meta? = nil
    ) {
        self.id = id
        self.sender = sender
        self.text = text
        self.suggestions = suggestions
        self.type = type
        self.isLiked = isLiked
        self.isDisliked = isDisliked
        self.meta = meta
    }
}

enum ChatAttachment: Identifiable, Hashable {
    case image(UIImage)
    case document(URL)

    var id: UUID { UUID() }
    
    
    static func == (lhs: ChatAttachment, rhs: ChatAttachment) -> Bool {
         switch (lhs, rhs) {
         case (.image(let l), .image(let r)):
             return l == r
         case (.document(let l), .document(let r)):
             return l == r
         default:
             return false
         }
     }
}





// MARK: - DataClass
struct ChatDataModel: Codable {
    let chatID: Int?
    let message: String?
    let meta: Meta?
    let category, confidence, responseTime, source: String?
    let messageID: Int?
    let fileURL: String?
    let docsType: String?

    enum CodingKeys: String, CodingKey {
        case chatID = "chat_id"
        case message, meta, category, confidence
        case responseTime = "response_time"
        case source
        case messageID = "message_id"
        case fileURL = "file_url"
        case docsType = "docs_type"
    }
}

// MARK: - Meta
struct Meta: Codable {
    let actionRequired: Bool?
    let actionType, severity, reason, category: String?

    enum CodingKeys: String, CodingKey {
        case actionRequired = "action_required"
        case actionType = "action_type"
        case severity, reason, category
    }
}


// MARK: - DataClass
struct ChatMessageListModel: Codable {
    let data: [GetChatMessage]?
}

// MARK: - Datum
struct GetChatMessage: Codable {
    let id, chatID: Int?
    let role, message: String?
    let meta: Meta?
    let docsPath, docsType: String?
    let status: String?
    let errorMessage: String?
    let responseTime, aiConfidence: Double?
    let source, category: String?
    let likeDislikeStatus: Int?
    let createdAt, updatedAt: String?

    enum CodingKeys: String, CodingKey {
        case id
        case chatID = "chat_id"
        case role, message, meta
        case docsPath = "docs_path"
        case docsType = "docs_type"
        case status
        case errorMessage = "error_message"
        case responseTime = "response_time"
        case aiConfidence = "ai_confidence"
        case source, category
        case likeDislikeStatus = "like_dislike_status"
        case createdAt = "created_at"
        case updatedAt = "updated_at"
    }
}

