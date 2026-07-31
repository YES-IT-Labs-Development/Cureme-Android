//
//  ReportDescriptionModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 07/01/26.
//

import SwiftUI

// MARK: - MODEL
//struct ReportDetail: Identifiable {
//    let id = UUID()
//    let title: String
//    let value: String
//    let status: ReportDetailStatus
//}

//struct Attachment: Identifiable {
//        let id = UUID()
//        let name: String
//        let icon: String
//    }

struct Attachment: Identifiable {
    let id = UUID()
    let name: String
    let filePath: String
    let icon: String
}

enum ReportDetailStatus {
    case attention
    case normal

    var title: String {
        switch self {
        case .attention: return "High"
        case .normal: return "Normal"
        }
    }

    var textColor: Color {
        switch self {
        case .attention: return Color(hex: "#DC2626") // red
        case .normal: return Color(hex: "#16A34A") // green
        }
    }

    var backgroundColor: Color {
        switch self {
        case .attention: return Color(hex: "#F31D1D").opacity(0.10)
        case .normal: return Color(hex: "#19BB9B").opacity(0.10)
        }
    }

    var borderColour: Color {
        switch self {
        case .attention: return Color(hex: "#F31D1D")
        case .normal: return Color(hex: "#19BB9B")
        }
    }
    
    var icon: String {
        switch self {
        case .attention: return "alert" // your asset
        case .normal: return "tick"        // your asset
        }
    }
}




// MARK: - DataClass
struct ReportDetail: Codable {
    let chatID: Int?
    let title, userName: String?
    let familyName: String?
    let severity, chatDate, summary, detailedAnalysis: String?
    let aiInsights: AIInsights?
    let attachments: [String]?
    let userProfilePic: String?
    let familyProfilePic: String?

    enum CodingKeys: String, CodingKey {
        case chatID = "chat_id"
        case title
        case userName = "user_name"
        case familyName = "family_name"
        case severity
        case chatDate = "chat_date"
        case summary
        case detailedAnalysis = "detailed_analysis"
        case aiInsights = "ai_insights"
        case attachments
        case userProfilePic = "user_profile_pic"
        case familyProfilePic = "family_profile_pic"
    }
}

// MARK: - AIInsights
struct AIInsights: Codable {
    let reason, category, severity, actionType: String?
    let symptomAnalysis: [String]?

    enum CodingKeys: String, CodingKey {
        case reason, category, severity
        case actionType = "action_type"
        case symptomAnalysis = "symptom_analysis"
    }
}
