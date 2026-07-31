//
//  FamilyPersonDetailModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 14/01/26.
//

import Foundation
import SwiftUI

struct FamilyPersonDetailModel {
    let name: String
    let profileImage: String   // image name or URL
}

struct FamilyPersonInfoItem: Identifiable {
    let id = UUID()
    let title: String
    let value: String
}

struct PersonHealthInfoItem: Identifiable {
    let id = UUID()
    let title: String
    let value: String
}

struct PersonMedicalInfoItem: Identifiable {
    let id = UUID()
    let title: String
    let value: String
}

//struct PersonDocumentsItem: Identifiable {
//    let id = UUID()
//    let name: String
//    let typeIcon: String
//}

struct PersonDocumentsItem: Identifiable {
    let id = UUID()
    let name: String
    let filePath: String
    let typeIcon: String
}


// MARK: - WelcomeData
struct familyProfileData: Codable {
    let data: familyProfileDetails?
}

// MARK: - DataData
struct familyProfileDetails: Codable {
    let id, userID: Int?
    let relationship, fullName, contactNumber, email: String?
    let dob, gender, height, weight: String?
    var profileImage, bloodGroup: String?
    let allergies: [String]?
    let emergencyContactName, emergencyContactNumber: String?
    let chronicCondition: [String]?
    let surgicalHistory: String?
    let currentMedications, currentSupplements: [String]?
    let createdAt, updatedAt: String?
    let deletedAt: String?
    let medicalDocuments: [String]?
    let getUser: GetUser?

    enum CodingKeys: String, CodingKey {
        case id
        case userID = "user_id"
        case relationship
        case fullName = "full_name"
        case contactNumber = "contact_number"
        case email, dob, gender, height, weight
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
        case medicalDocuments = "medical_documents"
        case getUser = "get_user"
    }
}

// MARK: - GetUser
struct GetUser: Codable {
    let id: Int?
    let name: String?
}
