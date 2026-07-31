//
//  AlertModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 31/12/25.
//

import SwiftUI
//
//enum ReminderPriority {
//    case high
//    case medium
//    case normal
//    
//    var title: String {
//        switch self {
//        case .high: return "High"
//        case .medium: return "Medium"
//        case .normal: return ""
//        }
//    }
//    
//    var color: Color {
//        switch self {
//        case .high: return Color(hex: "#F31D1D")
//        case .medium: return Color(hex: "#F36F1D")
//        case .normal: return .clear
//        }
//    }
//}

//struct AlertModel: Identifiable {
//    let id = UUID()
//    let userName: String
//    let title: String
//    let description: String
//    let timeText: String
//    let priority: ReminderPriority
//    var isCompleted: Bool
//}

//struct AlertModel: Identifiable {
//
//    let id = UUID()
//    let userName: String
//    let familyMemberName: String?
//    let title: String
//    let description: String
//    let timeText: String
//    let priority: ReminderPriority
//    
//    var isCompleted: Bool
//}
//
//struct AlertData: Codable, Identifiable {
//    
//    let id: Int?
//    let userName: String?
//    let familyMemberName: String?
//    let title: String?
//    let description: String?
//    let timeText: String?
//    let severity: String?   // ✅ ADD THIS
//    var isCompleted: Bool?
//    
//    enum CodingKeys: String, CodingKey {
//        case id
//        case userName = "user_name"
//        case familyMemberName = "family_member_name"
//        case title
//        case description
//        case timeText = "time_text"
//        case severity
//        case isCompleted = "is_completed"
//    }
//}
//
//func mapPriority(_ severity: String?) -> ReminderPriority {
//    
//    switch severity?.lowercased() {
//        
//    case "high":
//        return .high
//        
//    case "medium":
//        return .medium
//        
//    default:
//        return .normal
//    }
//}

import SwiftUI

// MARK: - PRIORITY

enum ReminderPriority {

    case high
    case medium
    case normal

    var title: String {

        switch self {

        case .high:
            return "High"

        case .medium:
            return "Medium"

        case .normal:
            return ""
        }
    }

    var color: Color {

        switch self {

        case .high:
            return Color(hex: "#F31D1D")

        case .medium:
            return Color(hex: "#F36F1D")

        case .normal:
            return .clear
        }
    }
}

// MARK: - UI MODEL

struct AlertModel: Identifiable {

    let id: Int
    

    let userName: String
    let familyMemberName: String

    let type: String?

    let title: String?
    let description: String?

    let timeText: String?

    let severity: String?

    let actionRequired: Int?
    
    let reference_id: Int?
    
    let priority: ReminderPriority
    
    var appointmentcompletestatus : String

    var isCompleted: Bool
}


// MARK: - API DATA MODEL

struct AlertData: Codable, Identifiable {

    let id: Int?

    let userName: String?
    
    let familyMemberName: String?
    
    let appointmentcompletestatus: String?
    
    let type: String?

    let title: String?
    
    let message: String?

    let severity: String?

    let actionRequired: Int?
    
    let reference_id: Int?
    
    let isRead: Int?

    let notificationTime: String?

    enum CodingKeys: String, CodingKey {
        case id
        case userName = "user_name"
        case familyMemberName = "family_member_name"
        case appointmentcompletestatus = "appointment_complete_status"
        case type
        case title
        case message
        case severity
        case actionRequired = "action_required"
        case reference_id = "reference_id"
        case isRead = "is_read"
        case notificationTime = "notification_time"
    }
}
