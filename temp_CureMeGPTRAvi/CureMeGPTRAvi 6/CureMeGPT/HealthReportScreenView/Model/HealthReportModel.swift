//
//  HealthReportModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 30/12/25.
//

import Foundation
import SwiftUI

//struct HealthReportModel: Identifiable, Codable {
//    let id = UUID()
//    let title: String
//    let doctorName: String
//    let patientName: String
//    let date: String
//    let time: String
//    let location: String
//    let description: String
//    let image: String
//    let fileCount: String
//    let status: ReportStatus
//}


// MARK: - Data Model
struct HealthReportData: Codable, Identifiable, Hashable {
    
    var id: Int {
        chatID ?? 0
    }
    
    let chatID: Int?
    let title: String?
    let userName: String?
    let familyName: String?
    let severity: HealthReportStatus?
    let chatDate: String?
    let aiMessage: String?
    let filesCount: Int?
    
    enum CodingKeys: String, CodingKey {
        case chatID = "chat_id"
        case title
        case userName = "user_name"
        case familyName = "family_name"
        case severity
        case chatDate = "chat_date"
        case aiMessage = "ai_message"
        case filesCount = "files_count"
    }
}


//enum healthReportStatus {
//    
//    case low
//    case medium
//    case high
//    
//    var title: String {
//        switch self {
//            
//        case .low:
//            return "Low"
//            
//        case .medium:
//            return "Medium"
//            
//        case .high:
//            return "High"
//        }
//    }
//
//    var healthicontextColor: Color {
//        switch self {
//            
//        case .low:
//            return Color(hex: "#16A34A")
//            
//        case .medium:
//            return Color(hex: "#F59E0B")
//            
//        case .high:
//            return Color(hex: "#DC2626")
//        }
//    }
//
//    var healthiconbackgroundColor: Color {
//        switch self {
//            
//        case .low:
//            return Color(hex: "#19BB9B").opacity(0.10)
//            
//        case .medium:
//            return Color(hex: "#F59E0B").opacity(0.10)
//            
//        case .high:
//            return Color(hex: "#F31D1D").opacity(0.10)
//        }
//    }
//
//    var healthiconborderColour: Color {
//        switch self {
//            
//        case .low:
//            return Color(hex: "#19BB9B")
//            
//        case .medium:
//            return Color(hex: "#F59E0B")
//            
//        case .high:
//            return Color(hex: "#F31D1D")
//        }
//    }
//    
//    // MARK: - Icon
//    
//    var healthicon: String {
//        switch self {
//            
//        case .low:
//            return "tick"          // Green success icon asset
//            
//        case .medium:
//            return "warning"       // Orange warning icon asset
//            
//        case .high:
//            return "alert"         // Red alert icon asset
//        }
//    }
//}


enum HealthReportStatus: String, Codable, Hashable {
    
    case low
    case medium
    case high
    
    // MARK: - Title
    
    var title: String {
        switch self {
            
        case .low:
            return "Normal"
            
        case .medium:
            return "Medium"
            
        case .high:
            return "High"
        }
    }
    
    // MARK: - Icon
    
    var icon: String {
        switch self {
            
        case .low:
            return "tick"
            
        case .medium:
            return "alert-02"
            
        case .high:
            return "alert-02"
        }
    }
    
    // MARK: - Colors
    
    var textColor: Color {
        switch self {
            
        case .low:
            return Color(hex: "#16A34A")
            
        case .medium:
            return Color(hex: "#F31D1D")
            
        case .high:
            return Color(hex: "#F31D1D")
        }
    }
    
    var backgroundColor: Color {
        switch self {
            
        case .low:
            return Color(hex: "#19BB9B").opacity(0.10)
            
        case .medium:
            return Color(hex: "#F31D1D").opacity(0.10)
            
        case .high:
            return Color(hex: "#F31D1D").opacity(0.10)
        }
    }
    
    var borderColour: Color {
        switch self {
            
        case .low:
            return Color(hex: "#19BB9B")
            
        case .medium:
            return Color(hex: "#F31D1D")
            
        case .high:
            return Color(hex: "#F31D1D")
        }
    }
}


enum ReportStatus {
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
