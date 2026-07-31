//
//  HistoryProfileViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 04/12/25.
//

import SwiftUI
import Combine

class HistoryProfileViewModel: ObservableObject {
    
   // @Published var surgicalHistory: String = ""
    
    @Published var form = HistoryProfileModel()
    
    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    
    @Published var chronicError: String?
    @Published var surgicalError: String?
    @Published var medicationError: String?
    @Published var supplementError: String?
    @Published var otherAllergyError: String?
    
    @Published var otherChronicInput: String = ""
    @Published var otherChronicList: [String] = []
    @Published var otherChronicError: String?
    
    var familyMemberID : String? = UserDetail.shared.getID()
 
    private var cancellables = Set<AnyCancellable>()
    
    let chronicConditionOptions = [
        "Diabetes","Asthma","Hypertension","Thyroid",
        "Arthritis","Heart Disease","Anxiety","Depression","Others"
    ]

    // Toggle chip selection
    func toggleCondition(_ condition: String) {
        if condition == "Others" {
            if form.chronicConditions.contains("Others") {
                form.chronicConditions.removeAll { $0 == "Others" }
                otherChronicInput = ""
                otherChronicList.removeAll()
                otherChronicError = nil
            } else {
                form.chronicConditions.append("Others")
            }
            return
        }

        if form.chronicConditions.contains(condition) {
            form.chronicConditions.removeAll { $0 == condition }
        } else {
            form.chronicConditions.append(condition)
        }
        chronicError = nil
    }
    
    func updateOtherChronicFromInput(_ text: String) {
        otherChronicInput = text
        otherChronicList = text
            .split(separator: ",")
            .map { $0.trimmingCharacters(in: .whitespaces) }
            .filter { !$0.isEmpty }
    }
    
    private func parseChronicConditions(_ conditions: [String]) {
        let standardOptions = Set(chronicConditionOptions.filter { $0 != "Others" })
        
        var selected: [String] = []
        var others: [String] = []
        
        for condition in conditions {
            if let matchedStandard = standardOptions.first(where: { $0.caseInsensitiveCompare(condition) == .orderedSame }) {
                selected.append(matchedStandard)
            } else {
                others.append(condition)
            }
        }
        
        if !others.isEmpty {
            selected.append("Others")
            otherChronicList = others
            otherChronicInput = others.joined(separator: ", ")
        }
        
        form.chronicConditions = selected
    }

//    Add new medication entry
//    func addMedication() {
//        form.medications.append("")
//    }
    
    func addMedication() {
        let trimmed = form.medications[0].trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }

        form.medications.append(trimmed)
        form.medications[0] = ""   // keep + row fixed
    }

    func removeMedication(at index: Int) {
        guard index > 0 else { return }
        form.medications.remove(at: index)
    }

    // MARK: - Supplements
    func addSupplement() {
        let trimmed = form.supplements[0].trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }

        form.supplements.append(trimmed)
        form.supplements[0] = ""   // keep + row fixed
    }

    func removeSupplement(at index: Int) {
        guard index > 0 else { return }
        form.supplements.remove(at: index)
    }
    
    
    
    func validateForm() -> Bool {
        
        chronicError = nil
        surgicalError = nil
        medicationError = nil
        supplementError = nil
        otherChronicError = nil
      
        var isValid = true
        
        // Chronic Condition Validation
        if form.chronicConditions.isEmpty {
            chronicError = "Please select at least one chronic condition"
            isValid = false
        }
        
        if form.chronicConditions.contains("Others") && otherChronicList.isEmpty {
            otherChronicError = "Please enter chronic condition"
            isValid = false
        }
        
//        // Surgical History Validation
//        if form.surgicalHistory.trimmingCharacters(in: .whitespaces).isEmpty {
//            surgicalError = "Please enter surgical history"
//            isValid = false
//        }
        
//        // Medication Validation
//        let meds = form.medications.dropFirst().filter { !$0.trimmingCharacters(in: .whitespaces).isEmpty }
//        
//        if meds.isEmpty {
//            medicationError = "Please add at least one medication"
//            isValid = false
//        }
//        
//        // Supplement Validation
//        let supplements = form.supplements.dropFirst().filter { !$0.trimmingCharacters(in: .whitespaces).isEmpty }
//        
//        if supplements.isEmpty {
//            supplementError = "Please add at least one supplement"
//            isValid = false
//        }
        
        return isValid
    }
    
    
    func completeHistoryProfileAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true
        let normalConditions = form.chronicConditions.filter { $0 != "Others" }
        let chronicConditions = (normalConditions + otherChronicList).joined(separator: ",")
        let medications = form.medications.dropFirst().joined(separator: ",")
        let supplements = form.supplements.dropFirst().joined(separator: ",")
       
        APIManager.shared.completeHistoryProfileAPI(
            chronic_condition: chronicConditions,
            surgical_history:  form.surgicalHistory,
            current_medications: medications,
            current_supplements: supplements)
        

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
    
    
    func updateGeneralProfileHistoryAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true
        let normalConditions = form.chronicConditions.filter { $0 != "Others" }
        let chronicConditions = (normalConditions + otherChronicList).joined(separator: ",")
        let medications = form.medications.dropFirst().joined(separator: ",")
        let supplements = form.supplements.dropFirst().joined(separator: ",")
       
        APIManager.shared.updateGeneralProfileHistoryAPI(
            chronic_condition: chronicConditions,
            surgical_history:  form.surgicalHistory,
            current_medications: medications,
            current_supplements: supplements)
        

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
    
    // Add FamilyMemberHistoryProfile
    func addFamilyMemberHistoryProfileAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true
        let normalConditions = form.chronicConditions.filter { $0 != "Others" }
        let chronicConditions = (normalConditions + otherChronicList).joined(separator: ",")
        let medications = form.medications.dropFirst().joined(separator: ",")
        let supplements = form.supplements.dropFirst().joined(separator: ",")
       
        APIManager.shared.addFamilyMemberHistoryProfileAPI(
            family_member_id: familyMemberID ?? "", chronic_condition: chronicConditions,
            surgical_history:  form.surgicalHistory,
            current_medications: medications,
            current_supplements: supplements)
        

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
    
    
    
    // Add FamilyMemberHistoryProfile
    func updateFamilyHistoryProfileAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true
        let normalConditions = form.chronicConditions.filter { $0 != "Others" }
        let chronicConditions = (normalConditions + otherChronicList).joined(separator: ",")
        let medications = form.medications.dropFirst().joined(separator: ",")
        let supplements = form.supplements.dropFirst().joined(separator: ",")
       
        APIManager.shared.updateFamilyHistoryProfileAPI(
            family_member_id: familyMemberID ?? "", chronic_condition: chronicConditions,
            surgical_history:  form.surgicalHistory,
            current_medications: medications,
            current_supplements: supplements)
        

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
    
    
    func getHistoryProfile(completion: @escaping (Bool) -> Void) {

        showActivity = true
        let chronicConditions = form.chronicConditions.joined(separator: ",")
           let medications = form.medications.dropFirst().joined(separator: ",")
           let supplements = form.supplements.dropFirst().joined(separator: ",")
       
        APIManager.shared.getProfileHistoryAPI()
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
                print("History Profile Success:", response)
                let data = response.data
                print(data,"History Profile Data Coming form api")
                
                self.setHistoryProfileData(data: data)   // 👈 yahi main kaam hai
                
                completion(true)
            } else {
                self.errorMessage = response.message
                self.isPresentAlert = true
                completion(false)
            }
            
        }
        .store(in: &cancellables)
    }
    
    
    func getFamilyHistoryProfile(completion: @escaping (Bool) -> Void) {

        showActivity = true
        
        APIManager.shared.getFamilyHistoryAPI(familyMemberID: familyMemberID ?? "")
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
                print("History Profile Success:", response)
                let data = response.data
                print(data,"History family Data Coming form api")
                
                self.setHistoryProfileData(data: data)   // 👈 yahi main kaam hai
                
                completion(true)
            } else {
                self.errorMessage = response.message
                self.isPresentAlert = true
                completion(false)
            }
            
        }
        .store(in: &cancellables)
    }
    
    func setHistoryProfileData(data: GeneralProfileHistoryModel?) {
        guard let data = data else { return }

        // ✅ Chronic Conditions
        if let conditions = data.chronicCondition {
            parseChronicConditions(conditions)
        }

        // ✅ Surgical History
        form.surgicalHistory = data.surgicalHistory ?? ""

        // ✅ Medications (important: first empty row maintain karo)
        if let meds = data.currentMedications {
            form.medications = [""] + meds   // first index input field ke liye
        }

        // ✅ Supplements (same logic)
        if let supplements = data.currentSupplements {
            form.supplements = [""] + supplements
        }

        print("History Profile Data Set Successfully")
    }
 }
