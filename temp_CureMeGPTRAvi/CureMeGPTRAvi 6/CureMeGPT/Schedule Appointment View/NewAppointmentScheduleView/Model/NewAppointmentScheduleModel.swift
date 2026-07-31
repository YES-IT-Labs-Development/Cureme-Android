//
//  NewAppointmentScheduleModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 30/12/25.
//

import Foundation
import SwiftUI

struct NewAppointmentScheduleModel {
    var member: String = ""
    var appointmentType: String = ""
    var description: String = ""
    var date: Date = Date()
    var time: Date = Date()
    var preferredDoctor: String = ""
    var preferredClinic: String = ""
    var reminder: String = ""
}

enum DropdownType {
    case member
    case appointmentType
    case reminder
}

enum AppointmentFlow {
    case new
    case reschedule
}




// MARK: - DataClass
struct familyDetailsModel: Codable {
    let people: [Person]?
}

// MARK: - Person
struct Person: Codable {
    let id: Int?
    let name, relationship, profilePhoto: String?

    enum CodingKeys: String, CodingKey {
        case id, name, relationship
        case profilePhoto = "profile_photo"
    }
}


// MARK: - DataClass
struct AppointmentTypeModel: Codable {
    let data: [Datum]?
}

// MARK: - Datum
struct Datum: Codable {
    let id: Int?
    let name: String?
    let createdAt, updatedAt: String?

    enum CodingKeys: String, CodingKey {
        case id, name
        case createdAt = "created_at"
        case updatedAt = "updated_at"
    }
}


struct FamilyMembers: Identifiable {
    let id: Int
    let name: String
    let relation: String
}

struct AppointmentTypes: Identifiable {
    let id: Int
    let name: String
}


// MARK: - DataClass
struct userWithFamilyDetailModels: Codable {
    let userDetails: UserDetails?
    let familyDetails: [FamilyDetail]?
}

// MARK: - FamilyDetail
struct FamilyDetail: Codable, Identifiable, Equatable {
    var id: Int?
    let name, relationship, profilePhoto: String?
    
        static func == (lhs: FamilyDetail, rhs: FamilyDetail) -> Bool {
            return lhs.id == rhs.id
        }

    enum CodingKeys: String, CodingKey {
        case id, name, relationship
        case profilePhoto = "profile_photo"
    }
}


// MARK: - UserDetails
struct UserDetails: Codable {
    let id: Int?
    let name: String?
    let profilePhoto: String?

    enum CodingKeys: String, CodingKey {
        case id, name
        case profilePhoto = "profile_photo"
    }
}

// MARK: - DataClass
struct getPromptQuestionModel: Codable {
    let promptQuestions: [PromptQuestion]?
    let familyDetails: [FamilyDetail]?
    let userDetails: UserDetails?

    enum CodingKeys: String, CodingKey {
        case promptQuestions = "prompt_questions"
        case familyDetails = "family_details"
        case userDetails
    }
}


// MARK: - PromptQuestion
struct PromptQuestion: Codable {
    let id: Int?
    let question: String?
    let category: Category?
    let description: String?
    let status, usageCount: Int?
    let createdAt, updatedAt: String?

    enum CodingKeys: String, CodingKey {
        case id, question, category, description, status
        case usageCount = "usage_count"
        case createdAt = "created_at"
        case updatedAt = "updated_at"
    }
}

enum Category: String, Codable {
    case general = "General"
    case getFit = "GetFit"
}





// MARK: - DataClass
struct ChatMessageModel: Codable {
    let data: [ChatData]?
}

// MARK: - Datum
//struct ChatData: Codable {
//    let id, userID: Int?
//    let familyMemberID: String?
//    var title: String?
//    let type: TypeEnum?
//    let createdAt: String?
//
//    enum CodingKeys: String, CodingKey {
//        case id
//        case userID = "user_id"
//        case familyMemberID = "family_member_id"
//        case title, type
//        case createdAt = "created_at"
//    }
//}

struct ChatData: Codable {
    let id, userID: Int?
    let familyMemberID: Int?
    var title: String?
    let type: TypeEnum?
    let createdAt: String?

    enum CodingKeys: String, CodingKey {
        case id
        case userID = "user_id"
        case familyMemberID = "family_member_id"
        case title, type
        case createdAt = "created_at"
    }

    // ✅ CUSTOM DECODER HERE
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        id = try container.decodeIfPresent(Int.self, forKey: .id)
        userID = try container.decodeIfPresent(Int.self, forKey: .userID)

        // 👇 Handle both Int & String safely
        if let intVal = try? container.decodeIfPresent(Int.self, forKey: .familyMemberID) {
            familyMemberID = intVal
        } else if let strVal = try? container.decodeIfPresent(String.self, forKey: .familyMemberID) {
            familyMemberID = Int(strVal)
        } else {
            familyMemberID = nil
        }

        title = try container.decodeIfPresent(String.self, forKey: .title)
        type = try container.decodeIfPresent(TypeEnum.self, forKey: .type)
        createdAt = try container.decodeIfPresent(String.self, forKey: .createdAt)
    }
}

enum TypeEnum: String, Codable {
    case normal = "normal"
    case typeCase = "case"
}
