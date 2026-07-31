//
//  NewAppointmentScheduleViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 30/12/25.
//


import Foundation
import SwiftUI
import Combine

final class NewAppointmentScheduleViewModel: ObservableObject {

    @Published var form = NewAppointmentScheduleModel()
    
    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false

    private var cancellables = Set<AnyCancellable>()
    
    @Published var for_whom_id: String? = ""
    @Published var appointment_type_id: String? = ""
    @Published var recommended_chat_id: String? = ""
    @Published var description: String? = ""
    @Published var date: String? = ""
    @Published var time: String? = ""
    @Published var preferred_doctor: String? = ""
    @Published var preferred_clinic: String? = ""
    @Published var appointment_reminder: String? = ""
    
    @Published var isCompleted: Bool = false
    
    @Published var isDateSelected = false
    @Published var isTimeSelected = false
    @Published var showToast = false
    @Published var toastMessage = ""
    
    @Published var appointmentID: String? = "1"
    
   
    // 🔥 API Data
       @Published var membersList: [FamilyMembers] = []
       @Published var selectedMember: FamilyMembers?
    
    @Published var membersListDetails: [FamilyDetail] = []
    // 🔥 Selected
       @Published var appointmentTypesList: [AppointmentTypes] = []
       @Published var selectedAppointmentType: AppointmentTypes?

    // Dropdown data
    let members = ["Self", "Child", "Spouse"]
    let appointmentTypes = ["Normal Check-up", "Dental Check-up", "Root Canal", "Brain Check-up", "Hair Check-up", "Skin Check-up", "Heart Check-up", "Lungs Check-up", "Liver Check-up","Intestine Check-up", "Kidney Check-up", "Bones Check-up","Feet Check-up", "Hand Check-up", "ENT Check-up"]
    let reminders = ["10 Min Before", "30 Min Before", "2 Hrs. Before", "24 Hrs. Before"]

    func scheduleAppointment() {
        // API Call / Validation
        print("Appointment Scheduled:", form)
    }
    
    
//  for get_appointment_type
    func schedule_appointmentAPI(completion: @escaping (Bool) -> Void) {
       // isLoading = true
        self.showActivity = true
        APIManager.shared.schedule_appointmentAPI(for_whom_id: for_whom_id ?? "", appointment_type_id: appointment_type_id ?? "", recommended_chat_id: recommended_chat_id ?? "", description: description ?? "", date: date ?? "", time: time ?? "", preferred_doctor: preferred_doctor ?? "", preferred_clinic: preferred_clinic ?? "", appointment_reminder: appointment_reminder ?? "")
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
    
    
    
//  for rescheduleAppointment
    func rescheduleAppointmentAPI(completion: @escaping (Bool) -> Void) {
       // isLoading = true
        self.showActivity = true
    
        APIManager.shared.rescheduleAppointmentAPI(
            appointment_id: appointmentID ?? "",
            for_whom_id: for_whom_id ?? "",
            appointment_type_id: appointment_type_id ?? "",
            description: description ?? "",
            date: date ?? "",
            time: time ?? "",
            preferred_doctor: preferred_doctor ?? "",
            preferred_clinic: preferred_clinic ?? "",
            appointment_reminder: appointment_reminder ?? "",
            recommendedchatid: recommended_chat_id ?? ""
        )
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
    
    
    
//  for GetFamilyMember
    func getScheduleAppointmentDetails(completion: @escaping (Bool) -> Void) {
       // isLoading = true
        self.showActivity = true
        APIManager.shared.getScheduleAppointmentDetails(appointment_id: appointmentID ?? "1")
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
                   
                    if let data = response.data?.data {
                        
                        self.isCompleted = (data.completeStatus == 1)

                        print("✅ Complete Status:", self.isCompleted)
                        
                        // Description
                        self.form.description = data.description ?? ""
                        
                        // Doctor
                        self.form.preferredDoctor = data.preferredDoctor ?? ""

                        // Clinic
                        self.form.preferredClinic = data.preferredClinic ?? ""
                        
                        // Reminder
                        self.form.reminder = data.appointmentReminder ?? ""
                        
                        // Date
//
                        if let dateStr = data.date {
                            let formatter = DateFormatter()
                            formatter.dateFormat = "MM-dd-yyyy"   // ✅ FIXED
                            
                            if let dateObj = formatter.date(from: dateStr) {
                                self.form.date = dateObj
                                self.isDateSelected = true
                            } else {
                                print("❌ Date parsing failed:", dateStr)
                            }
                        }
                        
                        // Time
                        if let timeStr = data.time {
                            let formatter = DateFormatter()
                            formatter.dateFormat = "HH:mm:ss"
                            if let timeObj = formatter.date(from: timeStr) {
                                self.form.time = timeObj
                                self.isTimeSelected = true
                            }
                        }
                        
                        // Appointment Type
                        if let type = data.appointmentType {
                            self.selectedAppointmentType = AppointmentTypes(
                                id: type.id ?? 0,
                                name: type.name ?? ""
                            )
                            self.form.appointmentType = type.name ?? ""
                        }
                        
                        // Member
                        if let member = data.familyMember {
                            self.selectedMember = FamilyMembers(
                                id: member.id ?? 0,
                                name: member.fullName ?? "",
                                relation: ""
                            )
                            self.form.member = member.fullName ?? ""
                        } else {
                            self.selectedMember = FamilyMembers(
                                id: data.userID ?? 0,
                                name: "MySelf",
                                relation: "MySelf"
                            )
                            self.form.member = "MySelf"
                        }
                    }
                
                    
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
    
//  for GetFamilyMember
//    func getFamilyMemberAPI(completion: @escaping (Bool) -> Void) {
//       // isLoading = true
//        self.showActivity = true
//        APIManager.shared.getfamilymemberslistAPI()
//            .receive(on: DispatchQueue.main)
//            .sink { [weak self] result in
//                guard let self = self else { return }
//                self.showActivity = false
//                switch result {
//                case .failure(let error):
//                    self.errorMessage = error.localizedDescription
//                    // Handle connection issues
//                    if ((self.errorMessage?.contains("no local endpoint")) != nil) {
//                       
//                        self.errorMessage = "Internal Server Error. \nPlease try again."
//                    }
//                    
//                    self.isPresentAlert = true
//                    completion(false)
//                case .finished:
//                    print("API call finished")
//                }
//            } receiveValue: { [weak self] response in
//                guard let self = self else { return }
//                
//                if response.success ?? false {
//                 
//                    // 🔥 MAP API → MODEL
//                    self.membersList = response.data?.people?.map {
//                                        FamilyMembers(
//                                            id: $0.id ?? 0,
//                                            name: $0.name ?? "",
//                                            relation: $0.relationship ?? ""
//                                        )
//                                    } ?? []
//                    
//                    print(self.membersList,"Member List")
//                    
//                    completion(true)
//                } else {
//                    self.errorMessage = response.message ?? "Unknown error"
//                    self.isPresentAlert = true
//                    completion(false)
//                }
//            }
//            
//            .store(in: &cancellables)
//    }
    
    
//  for get_appointment_type
    func get_appointment_type_API(completion: @escaping (Bool) -> Void) {
       // isLoading = true
        self.showActivity = true
        APIManager.shared.get_appointment_type_API()
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
                  //  print(response.data ?? "","YAHOO Data")
                    
                    self.appointmentTypesList = response.data?.data?.map {
                                          AppointmentTypes(
                                              id: $0.id ?? 0,
                                              name: $0.name ?? ""
                                          )
                                      } ?? []
                    
                    
                    print(self.appointmentTypesList,"appointmentTypesList")
                    
                    completion(true)
                } else {
                    self.errorMessage = response.message ?? "Unknown error"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            
            .store(in: &cancellables)
    }
    
    func isPastTimeSelected() -> Bool {
        let calendar = Calendar.current
        if calendar.isDateInToday(form.date) {
            let now = Date()
            let timeComponents = calendar.dateComponents([.hour, .minute], from: form.time)
            
            var combinedComponents = calendar.dateComponents([.year, .month, .day], from: form.date)
            combinedComponents.hour = timeComponents.hour
            combinedComponents.minute = timeComponents.minute
            combinedComponents.second = 0
            
            if let combinedDate = calendar.date(from: combinedComponents), combinedDate < now {
                return true
            }
        }
        return false
    }
    
    func validateForm() -> Bool {
        
        // Check for past time on current date
        if isPastTimeSelected() {
            toastMessage = "You cannot select a past time for today."
            showToast = true
            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                self.showToast = false
            }
            return false
        }
        
        // Member
        guard let member = selectedMember else {
            errorMessage = "Please select member"
            isPresentAlert = true
            return false
        }
        
        // Appointment Type
        guard let type = selectedAppointmentType else {
            errorMessage = "Please select appointment type"
            isPresentAlert = true
            return false
        }
        
        // Description
        if form.description.trimmingCharacters(in: .whitespaces).isEmpty {
            errorMessage = "Please enter description"
            isPresentAlert = true
            return false
        }
        
        // Date
        if !isDateSelected {
            errorMessage = "Please select date"
            isPresentAlert = true
            return false
        }
        
        // Time
        if !isTimeSelected {
            errorMessage = "Please select time"
            isPresentAlert = true
            return false
        }
        
        // Doctor
        if form.preferredDoctor.trimmingCharacters(in: .whitespaces).isEmpty {
            errorMessage = "Please enter preferred doctor"
            isPresentAlert = true
            return false
        }
        
        // Clinic
        if form.preferredClinic.trimmingCharacters(in: .whitespaces).isEmpty {
            errorMessage = "Please enter preferred clinic"
            isPresentAlert = true
            return false
        }
        
        // Reminder
        if form.reminder.isEmpty {
            errorMessage = "Please select reminder"
            isPresentAlert = true
            return false
        }
        
        // ✅ Assign values for API (ONLY AFTER VALIDATION)
        if member.relation == "MySelf" || member.name == "MySelf" {
            for_whom_id = ""
        } else {
            for_whom_id = "\(member.id)"
        }
        appointment_type_id = "\(type.id)"
        description = form.description
        date = form.date.formatted("MM-dd-yyyy")
        time = form.time.formatted("HH:mm")
        preferred_doctor = form.preferredDoctor
        preferred_clinic = form.preferredClinic
        appointment_reminder = form.reminder
        
        return true
    }
    
}
