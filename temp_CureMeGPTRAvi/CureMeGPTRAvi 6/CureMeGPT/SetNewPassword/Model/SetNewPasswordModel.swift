//
//  SetNewPasswordModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 05/03/26.
//

import Foundation

// MARK: - Main Response
struct UpdatePasswordResponse: Codable {
    let success: Bool?
    let code: Int?
    let message: String?
    let data: UpdatePasswordData?
}

// MARK: - Data
struct UpdatePasswordData: Codable {
    let user: UpdatePasswordUser?
}

// MARK: - User Model
struct UpdatePasswordUser: Codable {
    let id: Int?
    let name: String?
    let email: String?
    let phone: String?
    let emailVerifiedAt: String?
    let deviceType: String?
    let fcmToken: String?
    let profileImage: String?
    let otp: String?
    let dob: String?
    let gender: String?
    let height: String?
    let weight: String?
    let bloodGroup: String?
    let allergies: String?
    let emergencyContactName: String?
    let emergencyContactNumber: String?
    let chronicCondition: String?
    let surgicalHistory: String?
    let currentMedications: String?
    let currentSupplements: String?
    let medicalDocuments: String?
    let accountVerificationStatus: Int?
    let userStatus: Int?
    let lastLoginAt: String?
    let createdAt: String?
    let updatedAt: String?
    let deletedAt: String?

    enum CodingKeys: String, CodingKey {
        case id, name, email, phone, otp, dob, gender, height, weight, allergies
        case bloodGroup = "blood_group"
        case emailVerifiedAt = "email_verified_at"
        case deviceType = "device_type"
        case fcmToken = "fcm_token"
        case profileImage = "profile_image"
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
        case createdAt = "created_at"
        case updatedAt = "updated_at"
        case deletedAt = "deleted_at"
    }
}
