//
//  PersonProfileModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 06/01/26.
//

import Foundation
import SwiftUI


struct PersonProfileModel {
    let name: String
    let email: String
    var profileImage: String   // image name or URL
}

struct PersonalInfoItem: Identifiable {
    let id = UUID()
    let icon: String
    let value: String
    let isHighlighted: Bool
}

struct HealthInfoItem: Identifiable {
    let id = UUID()
    let title: String
    let value: String
}

struct MedicalInfoItem: Identifiable {
    let id = UUID()
    let title: String
    let value: String
}

struct DocumentsItem: Identifiable {
    let id = UUID()
    let name: String
    let typeIcon: String
    let path: String
}


// MARK: - DataClass
struct UserDataModel: Codable {
    let user: UserModel?
}

// MARK: - User
struct UserModel: Codable {
    let id: Int?
    let name, email, phone: String?
    let emailVerifiedAt: String?
    var deviceType, fcmToken, profileImage: String?
    let otp: String?
    let dob, gender, height, weight: String?
    let bloodGroup, allergies, emergencyContactName, emergencyContactNumber: String?
    let chronicCondition, surgicalHistory, currentMedications, currentSupplements: String?
    let medicalDocuments: [MedicalDocument]?
    let accountVerificationStatus, userStatus: Int?
    let lastLoginAt: String?
    let deleteAccountFeedback: String?
    let createdAt, updatedAt: String?
    let deletedAt: String?

    enum CodingKeys: String, CodingKey {
        case id, name, email, phone
        case emailVerifiedAt = "email_verified_at"
        case deviceType = "device_type"
        case fcmToken = "fcm_token"
        case profileImage = "profile_image"
        case otp, dob, gender, height, weight
        case bloodGroup = "blood_group"
        case allergies
        case emergencyContactName = "emergency_contact_name"
        case emergencyContactNumber = "emergency_contact_number"
        case chronicCondition = "chronic_condition"
        case surgicalHistory = "surgical_history"
        case currentMedications = "current_medications"
        case currentSupplements = "current_supplements"
        case medicalDocuments = "medical_documents"
        case accountVerificationStatus = "account_verification_status"
        case userStatus = "user_status"
        case lastLoginAt = "last_login_at"
        case deleteAccountFeedback = "delete_account_feedback"
        case createdAt = "created_at"
        case updatedAt = "updated_at"
        case deletedAt = "deleted_at"
    }
}

// MARK: - MedicalDocument
struct MedicalDocument: Codable {
    let path, name: String?
}
