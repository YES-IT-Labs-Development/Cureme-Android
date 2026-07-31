//
//  FamilyPersonDetailViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 14/01/26.
//

import SwiftUI
import Combine

final class FamilyPersonDetailViewModel: ObservableObject {
    @EnvironmentObject private var coordinator: Coordinator
    
    @Published var profile: PersonProfileModel
    
    
    @Published var showToast = false
    @Published var toastMessage = ""
    
    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    
    private var cancellables = Set<AnyCancellable>()
    
    @Published var memberProfileData: familyProfileDetails?
    
    init() {
        self.profile = PersonProfileModel(
            name: "Rose Logan", email: "",
            profileImage: "Rose" // asset image
            
        )
    }
    @Published var personalInfoItems: [FamilyPersonInfoItem] = []
    @Published var healthItems: [PersonHealthInfoItem] = []
    @Published var medicalItems: [PersonMedicalInfoItem] = []
    @Published var uploadedPersonFiles: [PersonDocumentsItem] = []

    
    // MARK: - Actions
    func onBackTap() {
        print("Back tapped")
    }
    
    func onEditTap() {
        coordinator.push(.completeProfileView(flow: .editFamilyMember))
    }
    
    func onDeleteTap() {
        print("Delete tapped")
    }
    
    func onUploadTap() {
        print("Upload profile image")
    }
    
    
    
    
    // MARK: - updateProfilePic API CALL
  
    func updateMemberProfilePhotoAPI(memberID : Int, image: UIImage, completion: @escaping (Bool) -> Void) {

        showActivity = true

        guard let imageData = image.jpegData(compressionQuality: 0.8) else {
            showActivity = false
            completion(false)
            return
        }
        
        let compressedData = compressAndResizeImage(imageData)

        APIManager.shared.updateMemberProfilePhotoAPI(family_member_id: memberID, imageData: compressedData)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in

                guard let self = self else { return }
                self.showActivity = false

                if case .failure(let error) = result {
                    self.errorMessage = error.localizedDescription
                    
                    if self.errorMessage?.contains("no local endpoint") == true {
                        self.errorMessage = "Internal Server Error.\nPlease try again."
                    }

                    self.isPresentAlert = true
                    completion(false)
                }

            } receiveValue: { [weak self] response in

                guard let self = self else { return }

                if response.success ?? false {
                    
                    print("✅ Profile Updated")
                    
                    self.toastMessage = "Profile Photo updated successfully."
                    self.showToast = true
                    
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        self.showToast = false
                    }
                    
//                    // 🔥 IMPORTANT: Refresh profile image from server
//                    self.getMyProfile { _ in }
                    
                    completion(true)
                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                    completion(false)
                }

            }
            .store(in: &cancellables)
    }
    
    
    func deletefamilyProfilePhoto(memberId: String,completion: @escaping (Bool) -> Void) {

        showActivity = true

        APIManager.shared.deleteFamilyProfilePhotoAPI(family_member_id : memberId)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in

                guard let self = self else { return }
                self.showActivity = false

                if case .failure(let error) = result {
                    self.errorMessage = error.localizedDescription
                    self.isPresentAlert = true
                    completion(false)
                }

            } receiveValue: { [weak self] response in

                guard let self = self else { return }

                if response.success ?? false {

                    if response.success ?? false {

                        // Remove image locally
                        self.profile.profileImage = ""
                        
                        // If you have API image url in memberProfileData
                        self.memberProfileData?.profileImage = nil

                        self.toastMessage = "Profile photo deleted successfully."
                        self.showToast = true

                        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                            self.showToast = false
                        }

                        completion(true)
                    }

                    completion(true)

                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            .store(in: &cancellables)
    }
    
    
    
    // MARK: - Delete Family Member
    func deleteFamilyMemberAPI(memberId: Int, completion: @escaping (Bool) -> Void) {
        
        self.showActivity = true
        
        APIManager.shared.deleteFamilyMemberAPI(family_member_id: memberId)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                guard let self = self else { return }
                
                self.showActivity = false
                
                switch result {
                case .failure(let error):
                    self.errorMessage = error.localizedDescription
                    
                    if ((self.errorMessage?.contains("no local endpoint")) != nil) {
                        self.errorMessage = "Internal Server Error.\nPlease try again."
                    }
                    
                    self.isPresentAlert = true
                    completion(false)
                    
                case .finished:
                    print("Delete API finished")
                }
                
            } receiveValue: { [weak self] response in
                guard let self = self else { return }
                
                if response.success ?? false {
                    
                    self.toastMessage = response.message ?? "Member deleted successfully"
                    self.showToast = true
                    
                    completion(true)
                    
                } else {
                    self.errorMessage = response.message ?? "Something went wrong"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            .store(in: &cancellables)
    }
    
    //  for family Member Details
        func get_family_member_profileAPI(completion: @escaping (Bool) -> Void) {
           // isLoading = true
            self.showActivity = true
            let id  = UserDetail.shared.getID()
            APIManager.shared.get_family_member_profileAPI(family_member_id: id)
                .receive(on: DispatchQueue.main)
                .sink { [weak self] result in
                    guard let self = self else { return }
                    self.showActivity = false
                    switch result {
                    case .failure(let error):
                        self.errorMessage = error.localizedDescription
                        // Handle connection issues
                        if ((self.errorMessage?.contains("no local endpoint")) != nil) {
                           
                            self.errorMessage = "Internal Server Error. \nPlease try again."
                        }
                        
                        self.isPresentAlert = true
                        completion(false)
                    case .finished:
                        print("API call finished")
                    }
                } receiveValue: { [weak self] response in
                    guard let self = self else { return }
                    
                    if response.success ?? false {
                      
                        self.memberProfileData = response.data?.data
                        
                        print(memberProfileData ?? "","member Profile Data Result")
                        
                        // ✅ MAP DATA HERE
                        self.mapProfileData(self.memberProfileData)
                        
                          
                    } else {
                        self.errorMessage = response.message ?? "Unknown error"
                        self.isPresentAlert = true
                        completion(false)
                    }
                }
                
                .store(in: &cancellables)
        }
    
    func mapProfileData(_ data: familyProfileDetails?) {
        guard let data = data else { return }

        // PERSONAL INFO
        self.personalInfoItems = [
            FamilyPersonInfoItem(title: "Full Name", value: data.fullName ?? "--"),
            FamilyPersonInfoItem(title: "Contact Number", value: data.contactNumber ?? "--"),
            FamilyPersonInfoItem(title: "Email Address", value: data.email ?? "--"),
            FamilyPersonInfoItem(title: "Relation To You", value: data.relationship ?? "--"),
            FamilyPersonInfoItem(title: "Date of Birth", value: data.dob ?? "--"),
            FamilyPersonInfoItem(title: "Gender", value: data.gender ?? "--"),
            FamilyPersonInfoItem(title: "Height", value: data.height ?? "--"),
            FamilyPersonInfoItem(title: "Weight", value: data.weight ?? "--")
        ]

        // HEALTH
        self.healthItems = [
            PersonHealthInfoItem(title: "Blood Group", value: data.bloodGroup ?? "--"),
            PersonHealthInfoItem(
                title: "Known Allergies",
                value: data.allergies?.joined(separator: ", ") ?? "--"
            ),
            PersonHealthInfoItem(title: "Emergency Contact", value: data.emergencyContactName ?? "--"),
            PersonHealthInfoItem(title: "Emergency Ph.", value: data.emergencyContactNumber ?? "--")
        ]

        // MEDICAL
        self.medicalItems = [
           
            PersonMedicalInfoItem(
                title: "Chronic Conditions",
                value: data.chronicCondition?.joined(separator: ", ") ?? "--"
            ),
            PersonMedicalInfoItem(title: "Surgical History", value: data.surgicalHistory ?? "--"),
            PersonMedicalInfoItem(title: "Current Medications", value: data.currentMedications?.joined(separator: ", ") ?? "--"),
            PersonMedicalInfoItem(title: "Current Supplements", value: data.currentSupplements?.joined(separator: ", ") ?? "--")
        ]

        // DOCUMENTS
        self.uploadedPersonFiles = data.medicalDocuments?.map { filePath in
            
            let fileName = URL(fileURLWithPath: filePath).lastPathComponent
            
            return PersonDocumentsItem(
                name: fileName,
                filePath: filePath,
                typeIcon: "doc.richtext"
            )
            
        } ?? []
        // DOCUMENTS
//        self.uploadedPersonFiles = data.medicalDocuments?.map {
//            PersonDocumentsItem(name: $0.fileName ?? "", typeIcon: "doc.richtext")
//        } ?? []
    }
    
}

