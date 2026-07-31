//
//  ChatSummaryModel.swift
//  CureMeGPT
//

import Foundation

struct ChatSummaryModel: Codable {
    let chatID: Int?
    let title: String?
    let summary: String?
    let symptoms: [String]?
    let recommendations: [String]?

    enum CodingKeys: String, CodingKey {
        case chatID = "chat_id"
        case title, summary, symptoms, recommendations
    }
}
