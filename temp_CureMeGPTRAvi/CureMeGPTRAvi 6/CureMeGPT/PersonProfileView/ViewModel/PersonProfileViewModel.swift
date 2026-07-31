//
//  PersonProfileViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 06/01/26.
//

import SwiftUI
import Combine

enum Tab {
    case home, chat, profile
}

final class PersonProfileViewModel: ObservableObject {
    @EnvironmentObject private var coordinator: Coordinator
    
    @Published var profile: PersonProfileModel?
    
    @Published var showActivity = false
    private var cancellables = Set<AnyCancellable>()
    
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    
    @Published var showToast = false
    @Published var toastMessage = ""
   
 
    @Published var items: [PersonalInfoItem] = [
        PersonalInfoItem(icon: "Frame 1", value: "James Logan", isHighlighted: true),
        PersonalInfoItem(icon: "Frame 2", value: "05/15/1988", isHighlighted: false),
        PersonalInfoItem(icon: "Frame 3", value: "78 Kg", isHighlighted: false),
        PersonalInfoItem(icon: "Frame 4", value: "+1 555 312 456", isHighlighted: false),
        PersonalInfoItem(icon: "Frame 5", value: "172 Cm", isHighlighted: false),
        PersonalInfoItem(icon: "Frame 6", value: "Male", isHighlighted: false),
      //  PersonalInfoItem(icon: "Frame 7", value: "james@gmail.com", isHighlighted: false)
    ]
    
    var healthItems: [HealthInfoItem] = [
           HealthInfoItem(title: "Blood Group", value: "O+"),
           HealthInfoItem(title: "Known Allergies", value: "Nuts"),
           HealthInfoItem(title: "Emergency Contact", value: "--"),
           HealthInfoItem(title: "Emergency Ph.", value: "--")
       ]
    
    var medicalItems: [MedicalInfoItem] = [
           MedicalInfoItem(title: "Chronic Conditions", value: "Hypertension"),
           MedicalInfoItem(title: "Surgical History", value: "--"),
           MedicalInfoItem(title: "Current Medications", value: "--"),
           MedicalInfoItem(title: "", value: "--"),
           MedicalInfoItem(title: "", value: "--"),
           MedicalInfoItem(title: "", value: "--"),
           MedicalInfoItem(title: "Current Supplements", value: "--"),
           MedicalInfoItem(title: "", value: "--"),
           MedicalInfoItem(title: "", value: "--"),
           MedicalInfoItem(title: "", value: "--")
    ]
    
    @Published var uploadedFiles: [DocumentsItem] = [
        DocumentsItem(name: "Demo_1.Pdf", typeIcon: "doc.richtext", path: "")
        //DocumentsItem(name: "analysis_report.pdf", typeIcon: "doc.richtext")
    ]
    
    // MARK: - Actions
    func onBackTap() {
      //  coordinator.selectedTab = .home
    }
    
    func onEditTap() {
       // coordinator.push(.completeProfileView(flow: .editProfile))
    }
    
    func onSettingTap() {
        print("Delete tapped")
    }
    
    func onUploadTap() {
        print("Upload profile image")
    }
    
    
    func deleteProfilePhoto(completion: @escaping (Bool) -> Void) {

        showActivity = true

        APIManager.shared.deleteProfilePhotoAPI()
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

                    UserDetail.shared.setProfileImg("")

                    self.profile?.profileImage = ""

                    self.toastMessage = "Profile photo deleted successfully."
                    self.showToast = true

                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        self.showToast = false
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
    
    // MARK: - updateProfilePic API CALL
  
    func updateProfilePicAPI(image: UIImage, completion: @escaping (Bool) -> Void) {

        showActivity = true

        guard let imageData = image.jpegData(compressionQuality: 0.8) else {
            showActivity = false
            completion(false)
            return
        }
        
        let compressedData = compressAndResizeImage(imageData)

        APIManager.shared.updateProfilePhotoAPI(imageData: compressedData)
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
                    
                    let profileImgURL  = response.data?.user?.profileImage ?? ""
                    
                    UserDetail.shared.setProfileImg("\(AppURL.imageURL)\(profileImgURL)")
                    
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        self.showToast = false
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
    
    
    // MARK: - get Document API CALL
    func  getMyProfile(completion: @escaping (Bool) -> Void) {

        showActivity = true

        APIManager.shared.getUserDetailsAPI()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in

                guard let self = self else { return }
                self.showActivity = false

//                if case .failure(let error) = result {
//                    self.errorMessage = error.localizedDescription
//                    self.isPresentAlert = true
//                    completion(false)
//                }
                
                if case .failure(let error) = result {
                    self.errorMessage = error.localizedDescription
                    // Handle connection issues
                    if ((self.errorMessage?.contains("no local endpoint")) != nil) {
                        
                        self.errorMessage = "Internal Server Error. \nPlease try again."
                    }
                    
                    self.isPresentAlert = true
                    completion(false)
                }
            } receiveValue: { [weak self] response in

                guard let self = self else { return }
                self.showActivity = false

                if response.success ?? false {
                    
                    let data = response.data
                 
                    print(data ,"User Data coming here")
                    
                    self.setProfileData(data: data?.user)
                        
                    completion(true)
                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                    completion(false)
                }

            }
            .store(in: &cancellables)
    }
    
    
    
    
    func setProfileData(data: UserModel?) {
        guard let data = data else { return }
        
        
        self.profile = PersonProfileModel(name: data.name ?? "", email: data.email ?? "", profileImage: data.profileImage ?? "")

        // Personal Info
        items = [
            PersonalInfoItem(icon: "Frame 1", value: data.name ?? "-", isHighlighted: true),
            PersonalInfoItem(icon: "Frame 2", value: data.dob ?? "-", isHighlighted: false),
            PersonalInfoItem(icon: "Frame 3", value: data.weight ?? "-", isHighlighted: false),
            PersonalInfoItem(icon: "Frame 4", value: data.phone ?? "-", isHighlighted: false),
            PersonalInfoItem(icon: "Frame 5", value: data.height ?? "-", isHighlighted: false),
            PersonalInfoItem(icon: "Frame 6", value: data.gender ?? "-", isHighlighted: false)
        ]

        // Convert String → Array
        let allergiesArray = data.allergies?.components(separatedBy: ",") ?? []
        let chronicArray = data.chronicCondition?.components(separatedBy: ",") ?? []

        // Health
        healthItems = [
            HealthInfoItem(title: "Blood Group", value: data.bloodGroup ?? "--"),
            HealthInfoItem(title: "Known Allergies", value: allergiesArray.joined(separator: ", ")),
            HealthInfoItem(title: "Emergency Contact", value: data.emergencyContactName ?? "--"),
            HealthInfoItem(title: "Emergency Ph.", value: data.emergencyContactNumber ?? "--")
        ]

        //  Medical
        medicalItems = [
            MedicalInfoItem(title: "Chronic Conditions", value: chronicArray.joined(separator: ", ")),
            MedicalInfoItem(title: "Surgical History", value: data.surgicalHistory ?? "--"),
            MedicalInfoItem(title: "Current Medications", value: data.currentMedications ?? "--"),
            MedicalInfoItem(title: "Current Supplements", value: data.currentSupplements ?? "--")
        ]

        // Documents
        if let docs = data.medicalDocuments {
            uploadedFiles = docs.map {
                DocumentsItem(
                    name: $0.name ?? "Document",
                    typeIcon: "FileIcon", path: $0.path ?? ""
                )
            }
        }

        // Image
        
        let name = data.name ?? ""
        UserDetail.shared.setName(name)
        
        if let image = data.profileImage,
           !image.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {

            let profileImgURL = "\(AppURL.imageURL)\(image)"
            UserDetail.shared.setProfileImg(profileImgURL)
            profile?.profileImage = profileImgURL
            print(AppURL.imageURL + (data.profileImage ?? ""),"Profile Data Set Successfully")

        } else {

            UserDetail.shared.setProfileImg("")
            profile?.profileImage = ""
        }
  
    }
}

