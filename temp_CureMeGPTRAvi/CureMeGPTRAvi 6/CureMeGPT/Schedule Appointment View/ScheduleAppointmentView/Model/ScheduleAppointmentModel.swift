//
//  ScheduleAppointmentModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 30/12/25.
//

import Foundation
import SwiftUI

struct ScheduleAppointmentModel: Identifiable {
    let id = UUID()
    let apiID: Int 
    let title: String
    var patientName: String
    let doctorName: String
    let category: String
    let date: String
    let time: String
    let location: String
    let description: String
    let image: String
    var isCompleted: Bool = false
    let familyMemberID: Int?
    let appointmentForWhom: String?
    let recommendedChatID: String?
}

enum GlobalDeletePopup {
    case appointment
    case medication
}




// MARK: - DataClass
struct AppointmentModelSchedule: Codable {
    let data: [ScheduleAppoinmentData]?
}

struct AppointmentDetailsModel: Codable {
    let data: ScheduleAppoinmentData?
}

// MARK: - Datum
struct ScheduleAppoinmentData: Codable {
    let id, userID, familyMemberID, appointmentTypeID: Int?
    let recommendedChatID: String?
    let description, date, time, preferredDoctor: String?
    let preferredClinic, appointmentReminder, appointmentForWhom: String?
    let completeStatus: Int?
    let createdAt, updatedAt: String?
    let user: AppointmentType?
    let familyMember: FamilyMemberappointment?
    let appointmentType: AppointmentType?

    enum CodingKeys: String, CodingKey {
        case id
        case userID = "user_id"
        case familyMemberID = "family_member_id"
        case appointmentTypeID = "appointment_type_id"
        case recommendedChatID = "recommended_chat_id"
        case description, date, time
        case preferredDoctor = "preferred_doctor"
        case preferredClinic = "preferred_clinic"
        case appointmentReminder = "appointment_reminder"
        case appointmentForWhom = "appointment_for_whom"
        case completeStatus = "complete_status"
        case createdAt = "created_at"
        case updatedAt = "updated_at"
        case user
        case familyMember = "family_member"
        case appointmentType = "appointment_type"
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        id = try container.decodeIfPresent(Int.self, forKey: .id)
        userID = try container.decodeIfPresent(Int.self, forKey: .userID)
        familyMemberID = try container.decodeIfPresent(Int.self, forKey: .familyMemberID)
        appointmentTypeID = try container.decodeIfPresent(Int.self, forKey: .appointmentTypeID)
        
        // Handle both Int and String for recommendedChatID
        if let intVal = try? container.decodeIfPresent(Int.self, forKey: .recommendedChatID) {
            recommendedChatID = "\(intVal)"
        } else if let strVal = try? container.decodeIfPresent(String.self, forKey: .recommendedChatID) {
            recommendedChatID = strVal
        } else {
            recommendedChatID = nil
        }
        
        description = try container.decodeIfPresent(String.self, forKey: .description)
        date = try container.decodeIfPresent(String.self, forKey: .date)
        time = try container.decodeIfPresent(String.self, forKey: .time)
        preferredDoctor = try container.decodeIfPresent(String.self, forKey: .preferredDoctor)
        preferredClinic = try container.decodeIfPresent(String.self, forKey: .preferredClinic)
        appointmentReminder = try container.decodeIfPresent(String.self, forKey: .appointmentReminder)
        appointmentForWhom = try container.decodeIfPresent(String.self, forKey: .appointmentForWhom)
        completeStatus = try container.decodeIfPresent(Int.self, forKey: .completeStatus)
        createdAt = try container.decodeIfPresent(String.self, forKey: .createdAt)
        updatedAt = try container.decodeIfPresent(String.self, forKey: .updatedAt)
        user = try container.decodeIfPresent(AppointmentType.self, forKey: .user)
        familyMember = try container.decodeIfPresent(FamilyMemberappointment.self, forKey: .familyMember)
        appointmentType = try container.decodeIfPresent(AppointmentType.self, forKey: .appointmentType)
    }
}

// MARK: - AppointmentType
struct AppointmentType: Codable {
    let id: Int?
    let name: String?
}

// MARK: - FamilyMember
struct FamilyMemberappointment: Codable {
    let id: Int?
    let fullName: String?

    enum CodingKeys: String, CodingKey {
        case id
        case fullName = "full_name"
    }
}
