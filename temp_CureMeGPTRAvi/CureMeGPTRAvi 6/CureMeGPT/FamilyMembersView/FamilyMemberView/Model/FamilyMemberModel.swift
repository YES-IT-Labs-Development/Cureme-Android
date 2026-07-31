//
//  FamilyMemberModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 05/01/26.
//

import SwiftUI

enum Relationship: String {
    case selfUser = "Self"
    case spouse = "Spouse"
    case son = "Son"
}

struct FamilyMemberModel: Identifiable {
    let id = UUID()
    let name: String
    let age: Int
    let relationship: Relationship
    let imageName: String
    let progress: String
    let date: String
}




// MARK: - DataClass
struct DataClass: Codable {
    let totalFamilyMembers, totalFamilyMedicationCount, totalFamilyAppointmentCount: Int?
    let familyMembers: [FamilyMemberModels]

    enum CodingKeys: String, CodingKey {
        case totalFamilyMembers = "total_family_members"
        case totalFamilyMedicationCount = "total_family_medication_count"
        case totalFamilyAppointmentCount = "total_family_appointment_count"
        case familyMembers
    }
}

// MARK: - FamilyMember
struct FamilyMemberModels: Codable, Identifiable {
    let id, userID: Int?
    let relationship, fullName, contactNumber, email: String?
    let dob,age, gender, height, weight: String?
    let profileImage: String?
    let bloodGroup, allergies, emergencyContactName, emergencyContactNumber: String?
    let chronicCondition, surgicalHistory, currentMedications, currentSupplements: String?
    let createdAt, updatedAt: String?
    let deletedAt: String?
    let medicationCount, appointmentCount, completedAppointmentCount: Int?
    let medicalDocuments: [String]?

    enum CodingKeys: String, CodingKey {
        case id
        case userID = "user_id"
        case relationship
        case fullName = "full_name"
        case contactNumber = "contact_number"
        case email, dob,age, gender, height, weight
        case profileImage = "profile_image"
        case bloodGroup = "blood_group"
        case allergies
        case emergencyContactName = "emergency_contact_name"
        case emergencyContactNumber = "emergency_contact_number"
        case chronicCondition = "chronic_condition"
        case surgicalHistory = "surgical_history"
        case currentMedications = "current_medications"
        case currentSupplements = "current_supplements"
        case createdAt = "created_at"
        case updatedAt = "updated_at"
        case deletedAt = "deleted_at"
        case medicationCount = "medication_count"
        case appointmentCount = "appointment_count"
        case completedAppointmentCount
        case medicalDocuments = "medical_documents"
    }
}
