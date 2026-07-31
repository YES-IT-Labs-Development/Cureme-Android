//
//  CompleteProfileViewModel.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 02/12/25.
//

import SwiftUI
import Combine

class ProfileViewModel: ObservableObject {
    
    @Published var profile = UserProfile()
    
    @Published var showDatePicker = false
    @Published var showGenderPicker = false
    @Published var showImagePicker = false
    @Published var showValidationErrors = false
    
    @Published var showToast = false
    @Published var toastMessage = ""
   
    @Published var dob: Date? = nil
    
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    @Published var loginSuccess = false
    @Published var showActivity = false
    private var cancellables = Set<AnyCancellable>()
    @Published var alertMsg: String = ""
    @Published var emailPhone: String = ""
    @Published var otpFromServer: String = ""
    @Published var imgData: Data = Data()
    @Published var profileImg: String = ""
    
    @Published var relation: String = ""
    
    @Published var familymemberid: String = ""
    
    @Published var isPhoneVerified = false
    @Published var isEmailVerified = false
  
    
    let genderOptions = ["Male", "Female", "Other"]
   // let relationOptions = ["Mother", "Father","Brother", "Sister", "Daughter", "Son", "Other"]
    let relationOptions = [  "Father", "Mother", "Grandfather", "Grandmother", "Great-Grandfather",
                             "Great-Grandmother", "Brother", "Sister", "Son", "Daughter",
                             "Grandson", "Granddaughter", "Husband", "Wife", "Uncle", "Aunt",
                             "Nephew", "Niece", "Cousin", "Father-in-law", "Mother-in-law",
                             "Brother-in-law", "Sister-in-law", "Son-in-law", "Daughter-in-law"]
    
    let heightUnits = ["Cm", "Ft"]
    let weightUnits = ["Kg", "Lb"]
    
    var fullNameError: String? {
        guard showValidationErrors else { return nil }
        
        if profile.name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            return "Full name is required"
        }
        return nil
    }
    
    var relationError: String? {
        guard showValidationErrors else { return nil }
        
        if relation.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            return "Relation is required"
        }
        return nil
    }

   

//    var contactError: String? {
//        guard showValidationErrors else { return nil }
//        
//        let phone = profile.phone.trimmingCharacters(in: .whitespacesAndNewlines)
//        
//        if phone.isEmpty {
//            return "Contact number is required"
//        }
//        
//        if phone.count != 10 {
//            return "Enter valid phone number"
//        }
//        
//        return nil
//    }

//    var emailError: String? {
//        guard showValidationErrors else { return nil }
//        
//        let email = profile.email.trimmingCharacters(in: .whitespacesAndNewlines)
//        
//        if email.isEmpty {
//            return "Email is required"
//        }
//        
//        if !isValidEmail(email) {
//            return "Enter valid email address"
//        }
//        
//        return nil
//    }

    var genderError: String? {
        guard showValidationErrors else { return nil }
        
        if profile.gender.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            return "Gender is required"
        }
        
        return nil
    }

    var heightError: String? {
        guard showValidationErrors else { return nil }
        
        let height = profile.height.trimmingCharacters(in: .whitespacesAndNewlines)
        
        if height.isEmpty {
            return "Height is required"
        }
        
        if Double(height) == nil {
            return "Enter valid height"
        }
        
        return nil
    }

    var weightError: String? {
        guard showValidationErrors else { return nil }
        
        let weight = profile.weight.trimmingCharacters(in: .whitespacesAndNewlines)
        
        if weight.isEmpty {
            return "Weight is required"
        }
        
        if Double(weight) == nil {
            return "Enter valid weight"
        }
        
        return nil
    }
    
    // MARK: - Email Validation
    private func isValidEmail(_ email: String) -> Bool {
        let emailRegex =
        "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        return NSPredicate(format:"SELF MATCHES %@", emailRegex)
            .evaluate(with: email)
    }
    
//    contactError == nil &&
//    emailError == nil &&
    var dobError: String? {
        guard showValidationErrors else { return nil }
        
        if dob == nil {
            return "Date of birth is required"
        }
        return nil
    }

    func isFormValid() -> Bool {
        return fullNameError == nil &&
        
        genderError == nil &&
        heightError == nil &&
        weightError == nil &&
        dobError == nil
    }
    // Profile Section Start
    
    func completePersonalProfileAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true
      
        let formatter = DateFormatter()
        formatter.dateFormat = "dd-MM-yyyy"
        let dobString = dob.map { formatter.string(from: $0) } ?? ""
 
        var images: [String: Data] = [:]

        if let data = profile.profileImageData {
            images["profile_image"] = resizeAndCompressImage(imageData: data)
        }
        
        APIManager.shared.completePersonalProfileAPI(
            full_name: profile.name ?? "" ,
            contact_number: profile.phone ?? "",
            email: profile.email ?? "",
            dob: dobString,
            gender: profile.gender ?? "" ,
            height: "\(profile.height) \(profile.heightUnit)",//heightValue,
            weight: "\(profile.weight) \(profile.weightUnit)",
            imgData: images  //weightValue,
    
        )
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
            self.showActivity = false
            
            if response.success == true {
                print("Profile Success:", response )
                let profileImgURL = response.data?.user?.profileImage ?? ""
                UserDetail.shared.setProfileImg("\(AppURL.imageURL)\(profileImgURL)")
                completion(true)
            } else {
                self.errorMessage = response.message
                self.isPresentAlert = true
                completion(false)
            }
            
        }
        .store(in: &cancellables)
    }
    
// UpdatePersonalProfile
    func apiforUpdatePersonalProfile(completion: @escaping (Bool) -> Void) {
        showActivity = true
        let formatter = DateFormatter()
        formatter.dateFormat = "dd-MM-yyyy"
        let dobString = dob.map { formatter.string(from: $0) } ?? ""
        var images: [String: Data] = [:]
        if let data = profile.profileImageData {
            images["profile_photo"] = resizeAndCompressImage(imageData: data)
        }
        APIManager.shared.apiforUpdatePersonalProfile(
            full_name: profile.name ?? "" ,
            contact_number: profile.phone ?? "",
            email: profile.email ?? "",
            dob: dobString,
            gender: profile.gender ?? "" ,
            height: "\(profile.height) \(profile.heightUnit)",//heightValue,
            weight: "\(profile.weight) \(profile.weightUnit)",
            imgData: images  //weightValue,
    
        )
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
            self.showActivity = false
            
            if response.success == true {
                print("Profile update response:", response )
                let profileImgURL = response.data?.user?.profileImage ?? ""
                UserDetail.shared.setProfileImg("\(AppURL.imageURL)\(profileImgURL)")
                completion(true)
            } else {
                self.errorMessage = response.message
                self.isPresentAlert = true
                completion(false)
            }
            
        }
        .store(in: &cancellables)
    }
    
    
//  for Get Personal profile
    func apiMyGetPersoalProfile(completion: @escaping (Bool) -> Void) {
       // isLoading = true
        self.showActivity = true
        APIManager.shared.apiForGetPersonalProfile()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                guard let self = self else { return }
                self.isLoading = false
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
                    self.setProfileData(data: response.data)
                    print(response.data ?? "","YAHOO Data")
                    
                    completion(true)
                } else {
                    
                    self.errorMessage = response.message ?? "Unknown error"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            
            .store(in: &cancellables)
    }
    
    func setProfileData(data: PersonalProfileModel?) {
        guard let data = data else { return }

        // Basic fields
        profile.name = data.fullName ?? ""
        profile.email = data.emailAddress ?? ""
        profile.phone = data.contactNumber ?? ""
        profile.gender = data.gender ?? ""
        
        
        isPhoneVerified = !(data.contactNumber ?? "").isEmpty
        isEmailVerified = !(data.emailAddress ?? "").isEmpty

        // Height & Weight split (agar "170 Cm" format me aa raha hai)
        if let height = data.height {
            let components = height.split(separator: " ")
            profile.height = components.first.map { String($0) } ?? ""
            profile.heightUnit = components.count > 1 ? String(components[1]) : "Cm"
        }

        if let weight = data.weight {
            let components = weight.split(separator: " ")
            profile.weight = components.first.map { String($0) } ?? ""
            profile.weightUnit = components.count > 1 ? String(components[1]) : "Kg"
        }

        // DOB convert (String → Date)
        if let dobString = data.dob {
            let formatter = DateFormatter()
            formatter.dateFormat = "dd-MM-yyyy"
            if let date = formatter.date(from: dobString) {
                self.dob = date
            }
        }

        // Profile Image URL
     //   profileImg = data.profilePhoto ?? ""
        
        profile.profileImage = data.profilePhoto ?? ""
        profileImg = data.profilePhoto ?? ""
 
        print("Profile Data Set Successfully")
    }
    
    func apiforVerifyPhoneEmail(completion: @escaping (Bool) -> Void) {

        showActivity = true
      
        APIManager.shared.apiforverifyEmailPhone(emailPhone: emailPhone)
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
            self.showActivity = false
            
            if response.success == true {
                print("Verification Success:", response)
                
                self.otpFromServer = response.data?.otp ?? ""
                showToast = true
                self.toastMessage = " OTP:- \(self.otpFromServer) "
                
                completion(true)
            } else {
                self.errorMessage = response.message
                self.isPresentAlert = true
                completion(false)
            }
            
        }
        .store(in: &cancellables)
    }
    
    // Profile Section End
    
    // Add Family Member
    func addFamilyMemberAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true
      
        let formatter = DateFormatter()
        formatter.dateFormat = "MM-dd-yyyy"
        let dobString = dob.map { formatter.string(from: $0) } ?? ""
 
        var images: [String: Data] = [:]

        if let data = profile.profileImageData {
            images["profile_photo"] = resizeAndCompressImage(imageData: data)
        }
        
        APIManager.shared.completeMemberPersonalProfileAPI(
            full_name: profile.name ?? "" ,
            contact_number: profile.phone ?? "",
            email: profile.email ?? "",
            dob: dobString,
            gender: profile.gender ?? "" ,
            height: "\(profile.height) \(profile.heightUnit)",//heightValue,
            weight: "\(profile.weight) \(profile.weightUnit)", relation: relation,
            imgData: images  //weightValue,
     
        )
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
            self.showActivity = false
            
            if response.success == true {
                print("Profile Success:", response )
                print(response.data ?? "","response Data")
                
                let id  = response.data?.data?.id ?? 0
                
                print(id,"id yahi check karni hai")
                
                UserDetail.shared.setID("\(id)")
                
                completion(true)
            } else {
                self.errorMessage = response.message
                self.isPresentAlert = true
                completion(false)
            }
            
        }
        .store(in: &cancellables)
    }
    
    
//  for update Member Personal profile
    func apiforUpdateMemberPersonalProfile(completion: @escaping (Bool) -> Void) {
        
        showActivity = true
      
        let formatter = DateFormatter()
        formatter.dateFormat = "MM-dd-yyyy"
        let dobString = dob.map { formatter.string(from: $0) } ?? ""
 
        var images: [String: Data] = [:]

        if let data = profile.profileImageData {
            images["profilePhoto"] = resizeAndCompressImage(imageData: data)
        }
        
        APIManager.shared.updateMemberPersonalProfileAPI(family_member_id: UserDetail.shared.getID(),
            full_name: profile.name  ,
            contact_number: profile.phone,
            email: profile.email ,
            dob: dobString,
            gender: profile.gender ,
            height: "\(profile.height) \(profile.heightUnit)",//heightValue,
            weight: "\(profile.weight) \(profile.weightUnit)", relation: relation,
            imgData: images  //weightValue,
        )
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                guard let self = self else { return }
                self.isLoading = false
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
                   // self.setMemberPersonalProfileData(data: response.data)
                    print(response.data ?? "","YAHOO Data")
                    
                    completion(true)
                } else {
                    
                    self.errorMessage = response.message ?? "Unknown error"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            
            .store(in: &cancellables)
    }
    
    
//  for Get Member Personal profile
    func apiMyGetMemberPersoalProfile(completion: @escaping (Bool) -> Void) {
       // isLoading = true
        self.showActivity = true
        APIManager.shared.getMemberPersonalProfile(familymemberid: UserDetail.shared.getID())
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                guard let self = self else { return }
                self.isLoading = false
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
                    self.setMemberPersonalProfileData(data: response.data?.data)
                    print(response.data ?? "","YAHOO Data")
                    
                    completion(true)
                } else {
                    
                    self.errorMessage = response.message ?? "Unknown error"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            
            .store(in: &cancellables)
    }
    
    
    
    func setMemberPersonalProfileData(data: FamilyMemberDataModel?) {
        guard let data = data else { return }

        // Basic fields
        profile.name = data.fullName ?? ""
        profile.email = data.emailAddress ?? ""
        profile.phone = data.contactNumber ?? ""
        profile.gender = data.gender ?? ""
        
        isPhoneVerified = !(data.contactNumber ?? "").isEmpty
        isEmailVerified = !(data.emailAddress ?? "").isEmpty

        // Height & Weight split (agar "170 Cm" format me aa raha hai)
        if let height = data.height {
            let components = height.split(separator: " ")
            profile.height = components.first.map { String($0) } ?? ""
            profile.heightUnit = components.count > 1 ? String(components[1]) : "Cm"
        }

        if let weight = data.weight {
            let components = weight.split(separator: " ")
            profile.weight = components.first.map { String($0) } ?? ""
            profile.weightUnit = components.count > 1 ? String(components[1]) : "Kg"
        }

        // DOB convert (String → Date)
        if let dobString = data.dateOfBirth {
            let formatter = DateFormatter()
            let formats = ["dd-MM-yyyy", "MM-dd-yyyy", "yyyy-MM-dd"]
            var parsedDate: Date? = nil
            for format in formats {
                formatter.dateFormat = format
                if let date = formatter.date(from: dobString) {
                    parsedDate = date
                    break
                }
            }
            if let date = parsedDate {
                self.dob = date
            }
        }

        // Profile Image URL
       // profileImg = data.profilePhoto ?? ""
        
        profile.profileImage = data.profilePhoto ?? ""
        profileImg = data.profilePhoto ?? ""

//        // Optional: Image Data (agar load karna ho)
//        loadImage(from: data.image)

        print(" Member Profile Data Set Successfully")
    }
    
  }

struct ValidationText: View {
    let message: String
    
    var body: some View {
        Text(message)
            .font(.custom("Urbanist-Regular", size: 12))
            .foregroundColor(.red)
    }
}

enum ProfileFlowType {
    case profileSetup
    case addFamilyMember
    case editProfile
    case editFamilyMember

    var title: String {
        switch self {
        case .profileSetup:
            return "Complete Your Profile"
        case .addFamilyMember:
            return "Add Family Member"
        case .editProfile:
            return "Edit Profile"
        case .editFamilyMember:
            return "Edit Family Member Details"
        }
    }

    var showSkipButton: Bool {
        self == .profileSetup
    }

    var primaryButtonTitle: String {
        switch self {
        case .profileSetup:
            return "Save & Continue"
        case .addFamilyMember:
            return "Save Member"
        case .editProfile:
            return "Update"
        case .editFamilyMember:
            return "Save & Continue"
        }
    }

    var onBackAction: (Coordinator) -> Void {
        { coordinator in
            coordinator.pop()
        }
    }

    var onDoneAction: (Coordinator) -> Void {
        { coordinator in
            switch self {
            case .profileSetup:
                coordinator.selectedAppTab = .home
                coordinator.push(.tabBarView)
            case .addFamilyMember:
                coordinator.pop()
            case .editProfile:
                coordinator.pop()
            case .editFamilyMember:
                coordinator.pop()
            }
        }
    }
}


func resizeAndCompressImage(imageData: Data) -> Data {
    
    guard let image = UIImage(data: imageData) else { return imageData }
    
    let newSize = CGSize(width: 800, height: 800)
    
    UIGraphicsBeginImageContextWithOptions(newSize, false, 0.7)
    image.draw(in: CGRect(origin: .zero, size: newSize))
    
    let resizedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    
    return resizedImage?.jpegData(compressionQuality: 0.7) ?? imageData
}
