//
//  GeneralProfileViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 04/12/25.
//

import SwiftUI
import Combine

class GeneralProfileViewModel: ObservableObject {

    @Published var showActivity = false
    private var cancellables = Set<AnyCancellable>()
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    
    @Published var bloodGroupError: String?
    @Published var allergyError: String?
    @Published var nameError: String?
    @Published var contactError: String?
    @Published var otherAllergyError: String?
    
    // Emergency Info
    @Published var emergencyName: String = ""
    @Published var emergencyPhone: String = ""
    
    // Blood Group
    @Published var selectedBloodGroup: String = ""
    @Published var otherAllergyText: String = ""
    
    var familyMemberID: String? = UserDetail.shared.getID()

    let bloodGroups = [
        "A+", "A−", "B+", "B−",
        "AB+", "AB−", "O+", "O−"
    ]

    // Allergies
   // @Published var selectedAllergies: Set<String> = []
    
    
    @Published var selectedAllergies: Set<String> = []
    @Published var otherAllergyInput: String = ""
    @Published var otherAllergyList: [String] = []

    let allergyOptions = [
        "Drug", "Food", "Environmental", "Aspirin",
        "Latex", "Ibuprofen","Shellfish", "Nuts", "Penicillin", "Others"
    ]

    func toggleAllergy(_ item: String) {

        if item == "Others" {
            // Only toggle Others (UI purpose)
            if selectedAllergies.contains("Others") {
                selectedAllergies.remove("Others")
                otherAllergyInput = ""
                otherAllergyList.removeAll()
                otherAllergyError = nil
            } else {
                selectedAllergies.insert("Others")
            }
            return
        }

        // Normal toggle (do NOT remove Others)
        if selectedAllergies.contains(item) {
            selectedAllergies.remove(item)
        } else {
            selectedAllergies.insert(item)
        }

        allergyError = nil
    }

    func addOtherAllergy() {
        let trimmed = otherAllergyInput.trimmingCharacters(in: .whitespaces)

        guard !trimmed.isEmpty else { return }

        // Avoid duplicate
        if !otherAllergyList.contains(trimmed) {
            otherAllergyList.append(trimmed)
        }

        otherAllergyInput = ""
    }
    
    
    func updateOtherAllergyFromInput(_ text: String) {
        otherAllergyInput = text

        otherAllergyList = text
            .split(separator: ",")
            .map { $0.trimmingCharacters(in: .whitespaces) }
            .filter { !$0.isEmpty }
    }
    
    
    func validateForm() -> Bool {
        bloodGroupError = nil
        allergyError = nil
        otherAllergyError = nil
        nameError = nil
        contactError = nil
        
        var isValid = true
        
        // Blood Group Validation
        if selectedBloodGroup.isEmpty {
            bloodGroupError = "Please select blood group"
            isValid = false
        }
        
        // Allergy Validation
        if selectedAllergies.isEmpty {
            allergyError = "Please select at least one allergy"
            isValid = false
        }
 
        if selectedAllergies.contains("Others") && otherAllergyList.isEmpty {
            otherAllergyError = "Please enter allergy"
            isValid = false
        }
        
        // Emergency Phone Number Validation (Optional, but must be exactly 10 digits if entered)
        let cleanedPhone = emergencyPhone.trimmingCharacters(in: .whitespacesAndNewlines)
        if !cleanedPhone.isEmpty {
            if cleanedPhone.count != 10 || !cleanedPhone.allSatisfy(\.isNumber) {
                contactError = "Emergency contact number must be exactly 10 digits"
                isValid = false
            }
        }
        
        return isValid
    }
    
    
 // Create General profile
    func completeGeneralProfileAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true

        var allergiesString: [String] {
            let normal = selectedAllergies.filter { $0 != "Others" }
            return Array(normal) + otherAllergyList
        }

        APIManager.shared.completeGeneralProfileAPI(
            blood_group: selectedBloodGroup,
            allergies: allergiesString,
            other_allergy: otherAllergyText,
            emergency_name: emergencyName,
            emergency_phone: emergencyPhone
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
                print("General Profile Success:", response)
                completion(true)
            } else {
                self.errorMessage = response.message
                self.isPresentAlert = true
                completion(false)
            }
            
        }
        .store(in: &cancellables)
    }
    
    // Api for update General profile
    func ApiforupdateGeneralprofile(completion: @escaping (Bool) -> Void) {

        showActivity = true

       // let allergiesString = selectedAllergies.joined(separator: ",")
        
        var allergiesString: [String] {
            let normal = selectedAllergies.filter { $0 != "Others" }
            return Array(normal) + otherAllergyList
        }

        APIManager.shared.updateGeneralProfileAPI(
            blood_group: selectedBloodGroup,
            allergies: allergiesString,
            other_allergy: otherAllergyText,
            emergency_name: emergencyName,
            emergency_phone: emergencyPhone
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
                print("General Profile Update Success:", response)
                
                completion(true)
            } else {
                self.errorMessage = response.message
                self.isPresentAlert = true
                completion(false)
            }
            
        }
        .store(in: &cancellables)
    }
    
    
    
    // Api for update Member General profile
    func ApiforUpdateMemberGeneralprofile(completion: @escaping (Bool) -> Void) {

        showActivity = true

       // let allergiesString = selectedAllergies.joined(separator: ",")
        
        var allergiesString: [String] {
            let normal = selectedAllergies.filter { $0 != "Others" }
            return Array(normal) + otherAllergyList
        }

        APIManager.shared.ApiforUpdateMemberGeneralprofile(
            family_member_id: familyMemberID ?? "",
            blood_group: selectedBloodGroup,
            allergies: allergiesString,
            other_allergy: otherAllergyText,
            emergency_name: emergencyName,
            emergency_phone: emergencyPhone
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
                print("General Profile Update Success:", response)
                
                completion(true)
            } else {
                self.errorMessage = response.message
                self.isPresentAlert = true
                completion(false)
            }
            
        }
        .store(in: &cancellables)
    }
    
    
    //  for Get General profile API 
        func apiGetGeneralProfile(completion: @escaping (Bool) -> Void) {
           // isLoading = true
            self.showActivity = true
            APIManager.shared.getGeneralProfileAPI()
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
                      //  self.setProfileData(data: response.data)
                        print(response.data ?? "","General Profile Data")
                        
                        self.setGeneralProfileData(data: response.data)
                        
                        completion(true)
                    } else {
                        
                        self.errorMessage = response.message ?? "Unknown error"
                        self.isPresentAlert = true
                        completion(false)
                    }
                }
                
                .store(in: &cancellables)
        }
    
    
    //  for getFamilyGeneralProfileAPI
        func getFamilyGeneralProfileAPI(completion: @escaping (Bool) -> Void) {
           // isLoading = true
            self.showActivity = true
            APIManager.shared.getFamilyGeneralProfileAPI(familyMemberID: familyMemberID ?? "")
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
                      //  self.setProfileData(data: response.data)
                        print(response.data?.data ?? "","General Profile Data")
                        
                        self.setMemberGeneralProfileData(data: response.data?.data)
                        
                        completion(true)
                    } else {
                        
                        self.errorMessage = response.message ?? "Unknown error"
                        self.isPresentAlert = true
                        completion(false)
                    }
                }
                
                .store(in: &cancellables)
        }
    private func parseAllergies(_ allergies: [String]) {
        let standardOptions = Set(allergyOptions.filter { $0 != "Others" })
        
        var selected: Set<String> = []
        var others: [String] = []
        
        for allergy in allergies {
            if let matchedStandard = standardOptions.first(where: { $0.caseInsensitiveCompare(allergy) == .orderedSame }) {
                selected.insert(matchedStandard)
            } else {
                others.append(allergy)
            }
        }
        
        if !others.isEmpty {
            selected.insert("Others")
            otherAllergyList = others
            otherAllergyInput = others.joined(separator: ", ")
        }
        
        selectedAllergies = selected
    }
        
    func setGeneralProfileData(data: GeneralProfileModel?) {
        guard let data = data else { return }

        // Blood Group
        selectedBloodGroup = data.bloodGroup ?? ""

        // Allergies (array → Set)
        if let allergies = data.knownAllergies {
            parseAllergies(allergies)
        }

        // Emergency Info
        emergencyName = data.emergencyContactName ?? ""
        emergencyPhone = data.emergencyPhoneNumber ?? ""

        print("General Profile Set Successfully")
    }
    
    func setMemberGeneralProfileData(data: MemberGeneralProfileModel?) {
        guard let data = data else { return }

        // Blood Group
        selectedBloodGroup = data.bloodGroup ?? ""

        // Allergies (array → Set)
        if let allergies = data.knownAllergies {
            parseAllergies(allergies)
        }

        // Emergency Info
        emergencyName = data.emergencyContactName ?? ""
        emergencyPhone = data.emergencyContactNumber ?? ""

        print("Member General Profile Set Successfully")
    }
    
    
    
    
 //  add_family_member_general
    func addfamilymembergeneralAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true

        //let allergiesString = selectedAllergies.joined(separator: ",")
        
        var allergiesString: [String] {
            let normal = selectedAllergies.filter { $0 != "Others" }
            return Array(normal) + otherAllergyList
        }

        APIManager.shared.addfamilymembergeneralAPI(
            family_member_id: UserDetail.shared.getID(),
            blood_group: selectedBloodGroup,
            allergies: allergiesString,
            other_allergy: otherAllergyText,
            emergency_name: emergencyName,
            emergency_phone: emergencyPhone
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
                print("Family Member General Profile Success:", response)
                completion(true)
            } else {
                self.errorMessage = response.message
                self.isPresentAlert = true
                completion(false)
            }
            
        }
        .store(in: &cancellables)
    }
    
    
}
