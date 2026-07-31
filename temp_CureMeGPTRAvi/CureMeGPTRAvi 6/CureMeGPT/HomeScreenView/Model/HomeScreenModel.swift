//
//  HomeScreenModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/12/25.
//

import Foundation

struct HealthAlert: Identifiable, Hashable {
    let id = UUID()
    let title: String
    let subtitle: String
    let type: AlertType
}

enum AlertType: Hashable {
    case critical
    case warning
}

struct HealthOverviewItem: Identifiable {
    let id = UUID()
    let title: String
    let subtitle: String
    let icon: String
}

struct ProfileOverviewResponse: Codable {
    let profileCompletion: Int
    let currentMedications: [String]
    let allergies: [String]
    let recommendedSteps: [String]
}

//struct MoodOption: Identifiable {
//    let id = UUID()
//    let emoji: String
//    let title: String
//}

struct MoodOption: Identifiable {
    let id = UUID()
    let emojiAsset: String   // asset image name
    let title: String
}

// MARK: - Mood API Response
struct MoodResponseModel: Codable {
    let success: Bool?
    let code: Int?
    let message: String?
    let data: MoodDataModel?
}

struct MoodDataModel: Codable {
    let title: String?
    let summary: String?
}

struct FamilyMember: Identifiable {
    let id = UUID()
    let idNumber: Int
    let name: String
    let dob: String
    let relation: String
    let imageName: String
    let lastCheckupDays: Int
    let alerts: [HealthAlert]
}

struct PersonLists: Identifiable {
    let id = UUID()
    let title: String
}


// MARK: - DataClass
struct HomeDataModel: Codable {
    let userContext: UserContext?
    let healthSummary: HealthSummary?
    let recommendedNextSteps: [String]?
    let thingsNeedAttention: ThingsNeedAttention?
    let membersDetails: MembersDetails?

    enum CodingKeys: String, CodingKey {
        case userContext = "user_context"
        case healthSummary = "health_summary"
        case recommendedNextSteps = "recommended_next_steps"
        case thingsNeedAttention = "things_need_attention"
        case membersDetails = "members_details"
    }
}

// MARK: - HealthSummary
struct HealthSummary: Codable {
    let allergies, currentMedications: [String]?

    enum CodingKeys: String, CodingKey {
        case allergies
        case currentMedications = "current_medications"
    }
}

// MARK: - MembersDetails
struct MembersDetails: Codable {
    let myself: Myself?
    let family: [Myself]?
}

// MARK: - Myself
struct Myself: Codable {
    let id: Int?
    let name, dob, profileImage, relationship: String?
    let activeAlerts: ActiveAlerts?
    let lastAppointmentDaysAgo: Int?

    enum CodingKeys: String, CodingKey {
        case id, name, dob, relationship
        case profileImage = "profile_image"
        case activeAlerts = "active_alerts"
        case lastAppointmentDaysAgo = "last_appointment_days_ago"
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        id = try container.decodeIfPresent(Int.self, forKey: .id)
        name = try container.decodeIfPresent(String.self, forKey: .name)
        dob = try container.decodeIfPresent(String.self, forKey: .dob)
        profileImage = try container.decodeIfPresent(String.self, forKey: .profileImage)
        relationship = try container.decodeIfPresent(String.self, forKey: .relationship)
        activeAlerts = try container.decodeIfPresent(ActiveAlerts.self, forKey: .activeAlerts)
        
        // Handle last_appointment_days_ago safely (supporting both Int and String if needed)
        if let intVal = try? container.decodeIfPresent(Int.self, forKey: .lastAppointmentDaysAgo) {
            lastAppointmentDaysAgo = intVal
        } else if let strVal = try? container.decodeIfPresent(String.self, forKey: .lastAppointmentDaysAgo) {
            lastAppointmentDaysAgo = Int(strVal)
        } else {
            lastAppointmentDaysAgo = nil
        }
    }
}

// MARK: - ActiveAlerts
struct ActiveAlerts: Codable {
    let appointments: [Appointmentss]?
    let medications: [Medication]?
}

// MARK: - Appointment
struct Appointmentss: Codable {
    let id, userID: Int?
    let familyMemberID: Int?
    let appointmentTypeID: Int?
    let appointmentType: String?
    let recommendedChatID: String?
    let description, date, time, preferredDoctor: String?
    let preferredClinic, appointmentReminder, appointmentForWhom: String?
    let completeStatus: Int?
    let createdAt, updatedAt: String?

    enum CodingKeys: String, CodingKey {
        case id
        case userID = "user_id"
        case familyMemberID = "family_member_id"
        case appointmentTypeID = "appointment_type_id"
        case appointmentType = "appointment_type"
        case recommendedChatID = "recommended_chat_id"
        case description, date, time
        case preferredDoctor = "preferred_doctor"
        case preferredClinic = "preferred_clinic"
        case appointmentReminder = "appointment_reminder"
        case appointmentForWhom = "appointment_for_whom"
        case completeStatus = "complete_status"
        case createdAt = "created_at"
        case updatedAt = "updated_at"
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        id = try container.decodeIfPresent(Int.self, forKey: .id)
        userID = try container.decodeIfPresent(Int.self, forKey: .userID)
        
        // Handle familyMemberID safely (supporting both Int and String if needed)
        if let intVal = try? container.decodeIfPresent(Int.self, forKey: .familyMemberID) {
            familyMemberID = intVal
        } else if let strVal = try? container.decodeIfPresent(String.self, forKey: .familyMemberID) {
            familyMemberID = Int(strVal)
        } else {
            familyMemberID = nil
        }
        
        appointmentTypeID = try container.decodeIfPresent(Int.self, forKey: .appointmentTypeID)
        
        appointmentType = try container.decodeIfPresent(String.self, forKey: .appointmentType)
        
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
    }
}

struct Medication: Codable {
    let id: Int?
    let userID: Int?
    let familyMemberID: Int?
    
    let medicationType: String?
    let medicationName: String?
    let dosage: String?
    let frequency: String?
    let days: String?
    
    let reminderTime: String?
    let startDate: String?
    let endDate: String?
    let prescriptionDocs: String?
    
    let notes: String?
    let medicationForWhom: String?
    let reminderStatus: Int?
    
    let createdAt: String?
    let updatedAt: String?

    enum CodingKeys: String, CodingKey {
        case id
        case userID = "user_id"
        case familyMemberID = "family_member_id"
        case medicationType = "medication_type"
        case medicationName = "medication_name"
        case dosage, frequency, days
        case reminderTime = "reminder_time"
        case startDate = "start_date"
        case endDate = "end_date"
        case prescriptionDocs = "prescription_docs"
        case notes
        case medicationForWhom = "medication_for_whom"
        case reminderStatus = "reminder_status"
        case createdAt = "created_at"
        case updatedAt = "updated_at"
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        id = try? container.decode(Int.self, forKey: .id)
        userID = try? container.decode(Int.self, forKey: .userID)

        // ✅ Flexible decoding
        if let intVal = try? container.decode(Int.self, forKey: .familyMemberID) {
            familyMemberID = intVal
        } else if let strVal = try? container.decode(String.self, forKey: .familyMemberID) {
            familyMemberID = Int(strVal)
        } else {
            familyMemberID = nil
        }

        medicationType = try? container.decode(String.self, forKey: .medicationType)
        medicationName = try? container.decode(String.self, forKey: .medicationName)
        dosage = try? container.decode(String.self, forKey: .dosage)
        frequency = try? container.decode(String.self, forKey: .frequency)
        days = try? container.decode(String.self, forKey: .days)

        reminderTime = try? container.decode(String.self, forKey: .reminderTime)
        startDate = try? container.decode(String.self, forKey: .startDate)
        endDate = try? container.decode(String.self, forKey: .endDate)
        prescriptionDocs = try? container.decode(String.self, forKey: .prescriptionDocs)

        notes = try? container.decode(String.self, forKey: .notes)
        medicationForWhom = try? container.decode(String.self, forKey: .medicationForWhom)
        reminderStatus = try? container.decode(Int.self, forKey: .reminderStatus)

        createdAt = try? container.decode(String.self, forKey: .createdAt)
        updatedAt = try? container.decode(String.self, forKey: .updatedAt)
    }
}


// MARK: - ThingsNeedAttention
struct ThingsNeedAttention: Codable {
    let myself: [String]?
    let family: [Family]?
}

// MARK: - Family
struct Family: Codable {
    let name: String?
    let symptoms: [String]?
}

// MARK: - UserContext
struct UserContext: Codable {
    let id: Int?
    let name: String?
    let profileCompletion: Int?

    enum CodingKeys: String, CodingKey {
        case id, name
        case profileCompletion = "profile_completion"
    }
}
