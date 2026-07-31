//
//  AddMedicationModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 02/01/26.
//

import Foundation
import SwiftUI

struct AddMedicationModel: Identifiable {
    let id = UUID()
    var time: Date
    var familyMember: String = ""
    var medicationType: String = ""
    var medicationName: String = ""
    var dosage: String = ""
    var frequency: String = ""
    var days: String = ""
    var startDate: Date = Date()
    var endDate: Date = Date()
    var preferredDoctor: String = ""
    var preferredClinic: String = ""
    var reminder: String = ""
    var notes: String = ""
}

enum MedicationDropdownType {
    case familyMember
    case medicationType
    case frequency
    case days
}

enum MedicationFlow {
    case mediNew
    case mediReschedule
}



// MARK: - DataClass
struct MedicationModel: Codable {
    let data: [DatumMedicate]?
}


struct MedicationModelDetails: Codable {
    let data: DatumMedicate?
}

// MARK: - Datum
struct DatumMedicate: Codable {
    let id, userID, familyMemberID: Int?
    let medicationType, medicationName, dosage, frequency: String?
    let days: String?
    let reminderTime: [String]?
    let startDate, endDate, prescriptionDocs, notes: String?
    let medicationForWhom: String?
    let reminderStatus: Int?
    let createdAt, updatedAt: String?
    let user: UserMedicate?
    let familyMember: FamilyMemberMedicate?

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
        case user
        case familyMember = "family_member"
    }
}

// MARK: - FamilyMember
struct FamilyMemberMedicate: Codable {
    let id: Int?
    let fullName: String?

    enum CodingKeys: String, CodingKey {
        case id
        case fullName = "full_name"
    }
}

// MARK: - User
struct UserMedicate: Codable {
    let id: Int?
    let name: String?
}
