//
//  AddMedicationViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 02/01/26.
//

import Foundation
import SwiftUI
import Combine

final class AddMedicationViewModel: ObservableObject {
    
    
    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false

    private var cancellables = Set<AnyCancellable>()

    // MARK: - Reminder Time
    @Published var reminderTimes: [AddMedicationModel] = []
    @Published var showTimePicker: Bool = false
    @Published var selectedTime: Date = Date()
   // @Published var isEveryDay: Bool = false
    @Published var enableReminder: Bool = false

    // MARK: - Main Form Model
    @Published var form: AddMedicationModel
    
    
    @Published var for_whom_id: String? = ""
    @Published var medication_type: String? = ""
    @Published var medication_name: String? = ""
    @Published var dosage: String? = ""
    @Published var frequencyy: String? = ""
    @Published var dayss: String? = ""
    @Published var reminder_time: [String] = []
    @Published var start_date: String? = ""
    @Published var end_date: String? = ""
    @Published var notes: String? = ""
    @Published var reminder_status: String? = ""
    
    @Published var medicationID: String? = UserDetail.shared.getID()
    
    @Published var selectedFile: UploadedFile?
   
    @Published var membersList: [FamilyMembers] = []
    @Published var selectedMember: FamilyMembers?
    
    @Published var membersListDetails: [FamilyDetail] = []
    
    @Published var isStartDateSelected = false
    
    @Published var isEndDateSelected = false

    // MARK: - Dropdown Data
   // let familyMembers = ["MySelf", "Mother", "Father","Brother", "Sister", "Daughter", "Son", "Other"]
    let familyMembers = ["MySelf","Father", "Mother", "Grandfather", "Grandmother", "Great-Grandfather",
                         "Great-Grandmother", "Brother", "Sister", "Son", "Daughter",
                         "Grandson", "Granddaughter", "Husband", "Wife", "Uncle", "Aunt",
                         "Nephew", "Niece", "Cousin", "Father-in-law", "Mother-in-law",
                         "Brother-in-law", "Sister-in-law", "Son-in-law", "Daughter-in-law"]
    
    
    let medicationTypes = ["Medicine", "Supplements"]
    let frequency = ["Daily", "Alternate Days", "Weekly"]
    let days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]

    // MARK: - Init
    init() {
        let defaultTime = AddMedicationModel(time: Date())
        self.reminderTimes = [defaultTime]   // 👈 THIS LINE
        self.form = AddMedicationModel(time: Date())
    }
    init(editModel: AddMedicationModel?) {
        if let model = editModel {
            self.form = model
            self.reminderTimes = [
                AddMedicationModel(time: model.time)
            ]
        } else {
            let newModel = AddMedicationModel(time: Date())
            self.form = newModel
            self.reminderTimes = [newModel]
        }
    }

    // MARK: - Reminder Actions
    func addTime() {
        let newReminder = AddMedicationModel(time: selectedTime)
        reminderTimes.append(newReminder)
    }

    func updateFirstTime() {
            reminderTimes[0].time = selectedTime
        }
    
    func removeLastTime() {
        guard reminderTimes.count > 1 else { return }
        reminderTimes.removeLast()
    }
    
    func removeTime(at index: Int) {
        guard reminderTimes.count > 1 else { return }
        reminderTimes.remove(at: index)
    }
    
    // MARK: - Submit
    func scheduleMedication() {
        // Combine reminder times into form if needed
        print("Medication Scheduled:")
        print("Reminders:", reminderTimes)
        print("Form:", form)
    }
    
//  for addMedicationAPI
    func addMedicationAPI(completion: @escaping (Bool) -> Void) {
       // isLoading = true
        self.showActivity = true

        let imageData = selectedFile?.data
        
        APIManager.shared.addMedicationAPI(for_whom_id: for_whom_id ?? "", medication_type: medication_type ?? "", medication_name: medication_name ?? "", dosage: dosage ?? "", frequency: frequencyy ?? "", days: dayss ?? "", reminder_time: reminder_time, start_date: start_date ?? "", end_date: end_date ?? "", notes: notes ?? "", reminder_status: reminder_status ?? "", imageData: imageData)
    
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
                  
                    print(response.message ?? "")
                    
                    completion(true)
                    
                } else {
                    self.errorMessage = response.message ?? "Unknown error"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            
            .store(in: &cancellables)
    }
    
    func updateMedicationAPI(completion: @escaping (Bool) -> Void) {
        
        self.showActivity = true
        
        let imageData = selectedFile?.data
        
        APIManager.shared.updateMedicationAPI(
            medication_id: medicationID ?? "",   // 🔥 IMPORTANT
            for_whom_id: for_whom_id ?? "",
            medication_type: medication_type ?? "",
            medication_name: medication_name ?? "",
            dosage: dosage ?? "",
            frequency: frequencyy ?? "",
            days: dayss ?? "",
            reminder_time: reminder_time,
            start_date: start_date ?? "",
            end_date: end_date ?? "",
            notes: notes ?? "",
            reminder_status: reminder_status ?? "",
            imageData: imageData
        )
        
        .receive(on: DispatchQueue.main)
        .sink { [weak self] result in
            guard let self = self else { return }
            self.showActivity = false
            
            switch result {
            case .failure(let error):
                self.errorMessage = error.localizedDescription
                
                if ((self.errorMessage?.contains("no local endpoint")) != nil) {
                    self.errorMessage = "Internal Server Error. \nPlease try again."
                }
                
                self.isPresentAlert = true
                completion(false)
                
            case .finished:
                print("Update API finished")
            }
            
        } receiveValue: { [weak self] response in
            guard let self = self else { return }
            
            if response.success ?? false {
                print("✅ Updated Successfully")
                completion(true)
            } else {
                self.errorMessage = response.message ?? "Unknown error"
                self.isPresentAlert = true
                completion(false)
            }
        }
        
        .store(in: &cancellables)
    }
    
    
    
    
//  for GetFamilyMember
    func getMedicationDetails(completion: @escaping (Bool) -> Void) {
       // isLoading = true
        self.showActivity = true
        APIManager.shared.getMedicationDetails(medicationID: medicationID ?? "")
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
                   // self.setProfileData(data: response.data)
                    
                    print(response.data?.data ,"Details Data")
                   
                    guard let item = response.data?.data else {
                             print("❌ No data found")
                             return
                         }
                         
                         // 🔹 Basic Fields
                    self.form.familyMember = item.familyMember?.fullName ?? "MySelf"
                         self.form.medicationType = item.medicationType ?? ""
                         self.form.medicationName = item.medicationName ?? ""
                         self.form.dosage = item.dosage ?? ""
                         self.form.frequency = item.frequency ?? ""
                         self.form.days = item.days ?? ""
                         self.form.notes = item.notes ?? ""
                    
                    if let docPath = item.prescriptionDocs, !docPath.isEmpty {
                        
                        let fileName = (docPath as NSString).lastPathComponent
                        
                        self.selectedFile = UploadedFile(
                            name: fileName, typeIcon: "",   // ✅ only file name
                            data: nil,
                            fileURL: docPath.imgFullPath()
                        )
                    }
                         
                         // 🔹 Member Selection
                          if let memberId = item.familyMember?.id {
                              self.selectedMember = self.membersList.first {
                                  $0.id == memberId
                              }
                          } else {
                              self.selectedMember = FamilyMembers(
                                  id: item.userID ?? 0,
                                  name: "MySelf",
                                  relation: "MySelf"
                              )
                          }
                     
                     if let member = selectedMember {
                         if member.relation == "MySelf" || member.name == "MySelf" {
                             for_whom_id = ""
                         } else {
                             for_whom_id = "\(member.id)"
                         }
                     }
                         
                         // 🔹 Dates
                         let formatter = DateFormatter()
                         formatter.dateFormat = "MM-dd-yyyy"
                         
                         if let start = item.startDate,
                            let startDate = formatter.date(from: start) {
                             self.form.startDate = startDate
                             self.isStartDateSelected = true
                         }
                         
                         if let end = item.endDate,
                            let endDate = formatter.date(from: end) {
                             self.form.endDate = endDate
                             self.isEndDateSelected = true
                         }
                         
                         // 🔹 Reminder ON/OFF
                         self.enableReminder = item.reminderStatus == 1
                         
                         // 🔹 Reminder Times
                         let timeFormatter = DateFormatter()
                         timeFormatter.dateFormat = "HH:mm"
                         
                         self.reminderTimes = (item.reminderTime ?? []).compactMap { timeStr in
                             if let date = timeFormatter.date(from: timeStr) {
                                 return AddMedicationModel(time: date)
                             }
                             return nil
                         }
                         
                         // fallback (important)
                         if self.reminderTimes.isEmpty {
                             self.reminderTimes = [AddMedicationModel(time: Date())]
                         }
                         
                         print("✅ Form Filled Successfully")
                         completion(true)
                         
                     }  else {
                    self.errorMessage = response.message ?? "Unknown error"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            
            .store(in: &cancellables)
    }
    
 
    //  for GetFamilyMember
        func userWithFamilyDetailsAPI(completion: @escaping (Bool) -> Void) {
           // isLoading = true
            self.showActivity = true
            APIManager.shared.userWithFamilyDetails()
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
                        var combinedList: [FamilyDetail] = []
                        // Add self user (jh)
                        if let user = response.data?.userDetails {
                            let selfMember = FamilyDetail(
                                id: user.id,
                                name: "MySelf",// "\(user.name ?? "")",
                                relationship: "MySelf",   // 👈 Important
                                profilePhoto: user.profilePhoto?.imgFullPath() ?? UserDetail.shared.getProfileImg()
                            )
                            combinedList.append(selfMember)
                        }
                        
                        // Add family members
                        combinedList.append(contentsOf: response.data?.familyDetails ?? [])
                        
                        self.membersListDetails = combinedList
                        
                        print(self.membersListDetails, "Member List Details")
                        
                        completion(true)
                        
                    } else {
                        self.errorMessage = response.message ?? "Unknown error"
                        self.isPresentAlert = true
                        completion(false)
                    }
                }
                .store(in: &cancellables)
        }
    
    
    
    func validateFields() -> Bool {
 
        // 🔹 Member (ONLY FOR ADD)
        if (medicationID?.isEmpty ?? true) {

            guard selectedMember != nil else {
                errorMessage = "Please select family member"
                isPresentAlert = true
                return false
            }
        }
        
        // 🔹 Medication Type
        if form.medicationType.isEmpty {
            errorMessage = "Please select medication type"
            isPresentAlert = true
            return false
        }
        
        // 🔹 Medication Name
        if form.medicationName.trimmingCharacters(in: .whitespaces).isEmpty {
            errorMessage = "Please enter medication name"
            isPresentAlert = true
            return false
        }
        
        // 🔹 Dosage
        if form.dosage.trimmingCharacters(in: .whitespaces).isEmpty {
            errorMessage = "Please enter dosage"
            isPresentAlert = true
            return false
        }
        
        // 🔹 Frequency
        if form.frequency.isEmpty {
            errorMessage = "Please select frequency"
            isPresentAlert = true
            return false
        }
        
        // 🔹 Days (only if needed)
        if form.frequency.lowercased() == "weekly" {
            if form.days.isEmpty {
                errorMessage = "Please select days"
                isPresentAlert = true
                return false
            }
        }
        
        // 🔹 Reminder
        if enableReminder && reminderTimes.isEmpty {
            errorMessage = "Please add at least one reminder time"
            isPresentAlert = true
            return false
        }
        
        if !isStartDateSelected {
            errorMessage = "Please select start date"
            isPresentAlert = true
            return false
        }

        if !isEndDateSelected {
            errorMessage = "Please select end date"
            isPresentAlert = true
            return false
        }
      
        
        // 🔹 Date Validation
        if form.endDate < form.startDate {
            errorMessage = "End date must be after start date"
            isPresentAlert = true
            return false
        }
        
        if form.notes.isEmpty {
            errorMessage = "Please enter notes"
            isPresentAlert = true
            return false
        }
        
        
        
        // ASSIGN VALUES
        
        //for_whom_id = "\(member.id)"
        
        if let member = selectedMember {
             if member.relation == "MySelf" || member.name == "MySelf" {
                 for_whom_id = ""
             } else {
                 for_whom_id = "\(member.id)"
             }
         }
        
        medication_type = form.medicationType
        medication_name = form.medicationName
        dosage = form.dosage
        frequencyy = form.frequency
        dayss = form.days
        
        // Date format
        let formatter = DateFormatter()
        formatter.dateFormat = "MM-dd-yyyy"
        
        start_date = formatter.string(from: form.startDate)
        end_date = formatter.string(from: form.endDate)
        
        notes = form.notes
        reminder_status = enableReminder ? "1" : "0"
        
        // Reminder time convert
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "HH:mm"
        
        reminder_time = reminderTimes.map {
            timeFormatter.string(from: $0.time)
        }
        
        return true
    }
}

