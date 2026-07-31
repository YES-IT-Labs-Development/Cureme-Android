//
//  CompleteProfileModel.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 02/12/25.
//

import Foundation

// MARK: - Main Response
struct PersonalProfileResponse: Decodable {
    let success: Bool?
    let code: Int?
    let message: String?
    let data: PersonalProfileData?
}

struct PersonalProfileData: Decodable {
    let user: UserProfile?
}


// MARK: - User Model

//
//struct UserProfile: Codable {
//
//    var id: Int?
//    var name: String = ""
//    var email: String = ""
//    var phone: String = ""
// 
//    var emailVerifiedAt: String?
//    var deviceType: String?
//    var fcmToken: String?
//
//    var profileImage: String?
//    var profileImageData: Data? = nil   // local upload only
//
//    var otp: String?
//
//    var dob: String? = ""
//
//    var gender: String = ""
//
//    var height: String = ""
//    var heightUnit: String = "Cm"
//
//    var weight: String = ""
//    var weightUnit: String = "Kg"
//
//    var bloodGroup: String?
//    var allergies: String?
//
//    var emergencyContactName: String?
//    var emergencyContactNumber: String?
//
//    var chronicCondition: String?
//    var surgicalHistory: String?
//
//    var currentMedications: String?
//    var currentSupplements: String?
//
//    var medicalDocuments: String?
//
//    var accountVerificationStatus: Int?
//    var userStatus: Int?
//
//    var lastLoginAt: String?
//    var createdAt: String?
//    var updatedAt: String?
//    var deletedAt: String?
//
//    enum CodingKeys: String, CodingKey {
//
//        case id, name, email, phone, otp, gender
//        case height, weight
//        case bloodGroup = "blood_group"
//        case allergies
//
//        case emailVerifiedAt = "email_verified_at"
//        case deviceType = "device_type"
//        case fcmToken = "fcm_token"
//
//        case profileImage = "profile_image"
//
//        case emergencyContactName = "emergency_contact_name"
//        case emergencyContactNumber = "emergency_contact_number"
//
//        case chronicCondition = "chronic_condition"
//        case surgicalHistory = "surgical_history"
//
//        case currentMedications = "current_medications"
//        case currentSupplements = "current_supplements"
//
//        case medicalDocuments = "medical_documents"
//
//        case accountVerificationStatus = "account_verification_status"
//        case userStatus = "user_status"
//
//        case lastLoginAt = "last_login_at"
//        case createdAt = "created_at"
//        case updatedAt = "updated_at"
//        case deletedAt = "deleted_at"
//    }
//}
struct UserProfile: Decodable {

    var id: Int?

    var name: String = ""
    var email: String = ""
    var phone: String = ""

    var emailVerifiedAt: String?
    var deviceType: String?
    var fcmToken: String?

    // 👇 API image
    var profileImage: String?

    // 👇 Local upload image
    var profileImageData: Data? = nil

    var otp: String?
    var dob: String? = ""

    var gender: String = ""

    var height: String = ""
    var heightUnit: String = "Cm"

    var weight: String = ""
    var weightUnit: String = "Kg"

    var bloodGroup: String?
    var allergies: String?

    var emergencyContactName: String?
    var emergencyContactNumber: String?

    var chronicCondition: String?
    var surgicalHistory: String?

    var currentMedications: String?
    var currentSupplements: String?

    var medicalDocuments: String?

    var accountVerificationStatus: Int?
    var userStatus: Int?

    var lastLoginAt: String?
    var createdAt: String?
    var updatedAt: String?
    var deletedAt: String?

    // 👇 Empty init for local use
    init() { }

    enum CodingKeys: String, CodingKey {

        case id
        case name
        case email
        case phone
        case otp
        case dob
        case gender
        case height
        case weight

        case bloodGroup = "blood_group"
        case allergies

        case emailVerifiedAt = "email_verified_at"
        case deviceType = "device_type"
        case fcmToken = "fcm_token"

        // 👇 Multiple image keys
        case profileImage = "profile_image"
        case profilePhoto = "profilePhoto"
        case profile_photo = "profile_photo"

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

    init(from decoder: Decoder) throws {

        let container = try decoder.container(keyedBy: CodingKeys.self)

        id = try container.decodeIfPresent(Int.self, forKey: .id)

        name = try container.decodeIfPresent(String.self, forKey: .name) ?? ""
        email = try container.decodeIfPresent(String.self, forKey: .email) ?? ""
        phone = try container.decodeIfPresent(String.self, forKey: .phone) ?? ""

        otp = try container.decodeIfPresent(String.self, forKey: .otp)
        dob = try container.decodeIfPresent(String.self, forKey: .dob)

        gender = try container.decodeIfPresent(String.self, forKey: .gender) ?? ""

        height = try container.decodeIfPresent(String.self, forKey: .height) ?? ""
        weight = try container.decodeIfPresent(String.self, forKey: .weight) ?? ""

        bloodGroup = try container.decodeIfPresent(String.self, forKey: .bloodGroup)
        allergies = try container.decodeIfPresent(String.self, forKey: .allergies)

        emailVerifiedAt = try container.decodeIfPresent(String.self, forKey: .emailVerifiedAt)
        deviceType = try container.decodeIfPresent(String.self, forKey: .deviceType)
        fcmToken = try container.decodeIfPresent(String.self, forKey: .fcmToken)

        // 👇 Handle all image keys
        // 👇 Handle all image keys safely
        let image1 = try container.decodeIfPresent(
            String.self,
            forKey: .profileImage
        )

        let image2 = try container.decodeIfPresent(
            String.self,
            forKey: .profilePhoto
        )

        let image3 = try container.decodeIfPresent(
            String.self,
            forKey: .profile_photo
        )

        profileImage = image1 ?? image2 ?? image3

        emergencyContactName = try container.decodeIfPresent(String.self, forKey: .emergencyContactName)
        emergencyContactNumber = try container.decodeIfPresent(String.self, forKey: .emergencyContactNumber)

        chronicCondition = try container.decodeIfPresent(String.self, forKey: .chronicCondition)
        surgicalHistory = try container.decodeIfPresent(String.self, forKey: .surgicalHistory)

        currentMedications = try container.decodeIfPresent(String.self, forKey: .currentMedications)
        currentSupplements = try container.decodeIfPresent(String.self, forKey: .currentSupplements)

        medicalDocuments = try container.decodeIfPresent(String.self, forKey: .medicalDocuments)

        accountVerificationStatus = try container.decodeIfPresent(Int.self, forKey: .accountVerificationStatus)
        userStatus = try container.decodeIfPresent(Int.self, forKey: .userStatus)

        lastLoginAt = try container.decodeIfPresent(String.self, forKey: .lastLoginAt)
        createdAt = try container.decodeIfPresent(String.self, forKey: .createdAt)
        updatedAt = try container.decodeIfPresent(String.self, forKey: .updatedAt)
        deletedAt = try container.decodeIfPresent(String.self, forKey: .deletedAt)
    }
}


// MARK: - DataClass
struct VerificationModelEmailPhone: Codable {
    let otp: String?
}




struct PersonalProfileDataDocs: Codable {
    let user: UserProfileDocs?
}

struct UserProfileDocs: Codable {

    var id: Int?
    var name: String?
    var email: String?
    var phone: String?
 
    var emailVerifiedAt: String?
    var deviceType: String?
    var fcmToken: String?

    var profileImage: String?
    var profileImageData: Data?   // local upload only

    var otp: String?

    var dob: String?

    var gender: String?

    var height: String?
    var heightUnit: String?

    var weight: String?
    var weightUnit: String?

    var bloodGroup: String?
    var allergies: String?

    var emergencyContactName: String?
    var emergencyContactNumber: String?

    var chronicCondition: String?
    var surgicalHistory: String?

    var currentMedications: String?
    var currentSupplements: String?

    var medicalDocuments: String?

    var accountVerificationStatus: Int?
    var userStatus: Int?

    var lastLoginAt: String?
    var createdAt: String?
    var updatedAt: String?
    var deletedAt: String?

    enum CodingKeys: String, CodingKey {

        case id, name, email, phone, otp, gender
        case height, weight
        case bloodGroup = "blood_group"
        case allergies

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


// getPersonalProfileModel


// MARK: - DataClass
struct PersonalProfileModel: Codable {
    var id: Int?
    var fullName: String?
    var contactNumber: String?
    var emailAddress: String?
    var dob, gender, height, weight: String?
    var profilePhoto: String?

    enum CodingKeys: String, CodingKey {
        case id
        case fullName = "full_name"
        case contactNumber = "contact_number"
        case emailAddress = "email_address"
        case dob, gender, height, weight
        case profilePhoto = "profile_photo"
    }
}

// MARK: - WelcomeData
struct MemberDataModel: Codable {
    let data: MemberGeneralProfileModel?
}

// MARK: - DataData
struct MemberGeneralProfileModel: Codable {
    let id: Int?
    let bloodGroup: String?
    let knownAllergies: [String]?
    let emergencyContactName, emergencyContactNumber: String?
}


// MARK: - DataClass
struct GeneralProfileModel: Codable {
    let id : Int?
    let bloodGroup: String?
    let knownAllergies: [String]?
    let emergencyContactName, emergencyPhoneNumber: String?

    enum CodingKeys: String, CodingKey {
        case id
        case bloodGroup = "blood_group"
        case knownAllergies = "known_allergies"
        case emergencyContactName = "emergency_contact_name"
        case emergencyPhoneNumber = "emergency_phone_number"
    }
}


// // MARK: - FamilyMember

struct FamilyMemberDataM: Codable {
    let data: FamilyMemberDataModel?
}


struct FamilyMemberDataModel: Codable {
    let id: Int?
    let fullName, contactNumber, emailAddress, dateOfBirth: String?
    let gender, height, weight, profilePhoto: String?

    enum CodingKeys: String, CodingKey {
        case id
        case fullName = "fullName"
        case fullNameSnake = "full_name"
        case contactNumber = "contactNumber"
        case contactNumberSnake = "contact_number"
        case emailAddress = "emailAddress"
        case emailAddressSnake = "email_address"
        case dob = "dob"
        case dateOfBirth = "dateOfBirth"
        case dateOfBirthSnake = "date_of_birth"
        case gender, height, weight
        case profilePhoto = "profilePhoto"
        case profilePhotoSnake = "profile_photo"
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        id = try container.decodeIfPresent(Int.self, forKey: .id)
        
        let fName1 = try container.decodeIfPresent(String.self, forKey: .fullName)
        let fName2 = try container.decodeIfPresent(String.self, forKey: .fullNameSnake)
        fullName = fName1 ?? fName2
        
        let cNum1 = try container.decodeIfPresent(String.self, forKey: .contactNumber)
        let cNum2 = try container.decodeIfPresent(String.self, forKey: .contactNumberSnake)
        contactNumber = cNum1 ?? cNum2
        
        let email1 = try container.decodeIfPresent(String.self, forKey: .emailAddress)
        let email2 = try container.decodeIfPresent(String.self, forKey: .emailAddressSnake)
        emailAddress = email1 ?? email2
        
        let rawDob = try container.decodeIfPresent(String.self, forKey: .dob)
        let rawDateOfBirth1 = try container.decodeIfPresent(String.self, forKey: .dateOfBirth)
        let rawDateOfBirth2 = try container.decodeIfPresent(String.self, forKey: .dateOfBirthSnake)
        dateOfBirth = rawDob ?? rawDateOfBirth1 ?? rawDateOfBirth2
        
        gender = try container.decodeIfPresent(String.self, forKey: .gender)
        height = try container.decodeIfPresent(String.self, forKey: .height)
        weight = try container.decodeIfPresent(String.self, forKey: .weight)
        
        let pPhoto1 = try container.decodeIfPresent(String.self, forKey: .profilePhoto)
        let pPhoto2 = try container.decodeIfPresent(String.self, forKey: .profilePhotoSnake)
        profilePhoto = pPhoto1 ?? pPhoto2
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encodeIfPresent(id, forKey: .id)
        try container.encodeIfPresent(fullName, forKey: .fullName)
        try container.encodeIfPresent(contactNumber, forKey: .contactNumber)
        try container.encodeIfPresent(emailAddress, forKey: .emailAddress)
        try container.encodeIfPresent(dateOfBirth, forKey: .dateOfBirth)
        try container.encodeIfPresent(gender, forKey: .gender)
        try container.encodeIfPresent(height, forKey: .height)
        try container.encodeIfPresent(weight, forKey: .weight)
        try container.encodeIfPresent(profilePhoto, forKey: .profilePhoto)
    }
}
