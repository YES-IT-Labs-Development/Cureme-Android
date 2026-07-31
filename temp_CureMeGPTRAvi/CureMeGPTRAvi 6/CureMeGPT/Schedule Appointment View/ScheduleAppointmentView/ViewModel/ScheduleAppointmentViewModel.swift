//
//  ScheduleAppointmentViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 30/12/25.
//

import Foundation
import SwiftUI
import Combine

enum SchedulePopupType {
    // Appointment
    case appointmentNew
    case appointmentReschedule

    // Medication
    case medicationEdit
    case medicationDelete
}

enum AppointmentMenuAction {
    case complete
    case reschedule
    case delete
}

enum MedicationMenuAction {
    case edit
    case delete
}

final class ScheduleAppointmentViewModel: ObservableObject {

    enum Tab {
        case appointments
        case medications
    }
    
    @Published var selectedTab: Tab = .appointments
    @Published var searchText: String = "" {
        didSet {
            if searchText.isEmpty {
                selectedMemberId = nil
                selectedMemberRelation = nil
                if !selectedMember.isEmpty {
                    selectedMember = ""
                }
            }
        }
    }
    @Published var showMenu: Bool = false
    @Published var selectedAppointmentID: UUID?
    @Published var menuPosition: CGPoint?
    @Published var medicationMenuPosition: CGPoint?
    @Published var summaryViewModel = AppointmentSummaryViewModel()
    @Published var isTabBarHidden: Bool = false
    @Published var activePopup: AppointmentMenuAction?
    
    @Published var showMedicationMenu: Bool = false
    
    @State private var resetMedicationSearch = false
    @Published var showToast = false
    @Published var toastMessage = ""
    
    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    
    private var cancellables = Set<AnyCancellable>()
    
  
    @Published var scheduleAppointData: [ScheduleAppoinmentData] = []
    
    @Published var selectedAppointment: ScheduleAppointmentModel?

    @Published var selectedType: FilterAppointmentViewModel.AppointmentType = .upcoming
    @Published var selectedMember: String = "" {
        didSet {
            if !selectedMember.isEmpty {
                if searchText != selectedMember {
                    searchText = selectedMember
                }
            } else {
                selectedMemberId = nil
                selectedMemberRelation = nil
                if !searchText.isEmpty {
                    searchText = ""
                }
            }
        }
    }
    @Published var selectedMemberId: Int? = nil
    @Published var selectedMemberRelation: String? = nil
  
    var shouldOpenMemberFilter: Bool {
        // decide when to open member popup
        // change this logic as per your requirement
        return !searchText.isEmpty
    }
    
    @Published var appointments: [ScheduleAppointmentModel] = []
//        ScheduleAppointmentModel(
//            apiID: 0, title: "Normal Check-up",
//            doctorName: "Dr. Emily Rodriguez",
//            category: "For: Peter Logan",
//            date: "09/09/2024",
//            time: "10:30 AM",
//            location: "Health Center, 120 Cooper Square, New York NY 10003, USA",
//            description: "Regular 6-month check-up with cleaning",
//            image: "BagIcon",
//            isCompleted: false
//            
//        ),
//        ScheduleAppointmentModel(
//            apiID: 0, title: "Dental Check-up",
//            doctorName: "Dr. Sarah Johnson",
//            category: "For: James Logan",
//            date: "08/18/2024",
//            time: "10:30 AM",
//            location: "120 Cooper Square, New York NY 10003, USA",
//            description: "Regular 6-month check-up with cleaning",
//            image: "TeethIcon",
//            isCompleted: false
//        )
//    ]
    
//    var filteredAppointments: [ScheduleAppointmentModel] {
//        if searchText.isEmpty {
//            return appointments
//        }
//        return appointments.filter {
//            $0.title.localizedCaseInsensitiveContains(searchText)
//        }
//    }
    
    var filteredAppointments: [ScheduleAppointmentModel] {
        var result = appointments
        
        // 🔹 Filter by type
        result = result.filter { appointment in
            switch selectedType {
            case .upcoming:
                return true
            case .past:
                return appointment.isCompleted
            }
        }
        
        // 🔹 Filter by member ID
        if let memberId = selectedMemberId {
            result = result.filter { appointment in
                if self.selectedMemberRelation?.lowercased() == "myself" {
                    return appointment.appointmentForWhom?.lowercased() == "self"
                } else {
                    return appointment.familyMemberID == memberId
                }
            }
        }
        
        // 🔹 Search filter
        if !searchText.isEmpty {
            result = result.filter {
                $0.title.localizedCaseInsensitiveContains(searchText) ||
                $0.patientName.localizedCaseInsensitiveContains(searchText)
            }
        }
        
        return result
    }
    
    func markAppointmentComplete(id: UUID) {
        if let index = appointments.firstIndex(where: { $0.id == id }) {
            appointments[index].isCompleted.toggle()
        }
    }
    
//    func applyFilter(
//        type: FilterAppointmentViewModel.AppointmentType,
//        member: String
//    ) {
//        // apply appointment filter
//    }
    
    func applyFilter(
        type: FilterAppointmentViewModel.AppointmentType,
        member: String,
        memberId: Int? = nil,
        relation: String? = nil
    ) {
        self.selectedType = type
        self.selectedMember = member
        self.selectedMemberId = memberId
        self.selectedMemberRelation = relation
    }

    func applyMemberFilter(name: String, id: Int? = nil, relation: String? = nil) {
        self.selectedMember = name
        self.selectedMemberId = id
        self.selectedMemberRelation = relation
    }
    
    func openSummary(for appointment: ScheduleAppointmentModel) {
        // CLOSE MENU FIRST
        showMenu = false
        menuPosition = nil
        selectedAppointmentID = nil

        // OPEN SUMMARY
        if let recommendedChatID = appointment.recommendedChatID,
           let chatId = Int(recommendedChatID) {
            summaryViewModel.showSummary(chatId: chatId)
        }
    }
    
    // Mark as delete Appointment api
    func deleteAppointment(id: Int, completion: @escaping (Bool) -> Void) {
        
        guard let appointment = appointments.first(where: { $0.apiID == id }) else {
            completion(false)
            return
        }

        showActivity = true

        APIManager.shared.deteleAppointAPI(appointment_id: "\(appointment.apiID)")
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                guard let self = self else { return }
                self.showActivity = false

                switch result {
                case .failure(let error):
                    self.errorMessage = error.localizedDescription
                    self.isPresentAlert = true
                    completion(false)

                case .finished:
                    print("🗑️ Delete API finished")
                }
            } receiveValue: { [weak self] response in
                guard let self = self else { return }

                if response.success ?? false {

                    print("🗑️ Deleted Successfully")

                    // 🔥 REMOVE FROM UI
                    self.appointments.removeAll { $0.apiID == id }

                    // 🔥 TOAST
                    self.toastMessage = response.message ?? "Appointment Deleted"
                    self.showToast = true

                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        self.showToast = false
                    }

                    completion(true)

                } else {
                    self.errorMessage = response.message ?? "Error"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            .store(in: &cancellables)
    }
    
        // Mark as completed Appointment api
    func markAppointmentComplete(id: Int, completion: @escaping (Bool) -> Void) {
        
        guard let appointment = appointments.first(where: { $0.apiID == id }) else {
            completion(false)
            return
        }

        showActivity = true

        APIManager.shared.markAsCompleteAppointmentAPI(appointment_id: "\(appointment.apiID)")
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                guard let self = self else { return }
                self.showActivity = false

                switch result {
                case .failure(let error):
                    self.errorMessage = error.localizedDescription
                    self.isPresentAlert = true
                    completion(false)

                case .finished:
                    print("✅ API finished")
                }
            } receiveValue: { [weak self] response in
                guard let self = self else { return }

                if response.success ?? false {

                    // 🔥 Update UI
                    if let index = self.appointments.firstIndex(where: { $0.apiID == id }) {
                        self.appointments[index].isCompleted.toggle()
                        let isCompletedNow = self.appointments[index].isCompleted
                        self.toastMessage = response.message ?? (isCompletedNow ? "Appointment Completed" : "Appointment Marked Active")
                    }

                    // 🔥 Toast
                    self.showToast = true

                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        self.showToast = false
                    }

                    completion(true)

                } else {
                    self.errorMessage = response.message ?? "Error"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            .store(in: &cancellables)
    }
    
//  for GetFamilyMember
    func getappointmentlistAPI(completion: @escaping (Bool) -> Void) {
       // isLoading = true
        self.showActivity = true
        APIManager.shared.getappointmentlistAPI()
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
                   // print(response.data ?? "","YAHOO Data")
                
                    // 🔥 MAP API → MODEL
                    self.scheduleAppointData = response.data?.data ?? []
                    
                    
//                    self.appointments = (response.data?.data ?? []).map { item in
//                        ScheduleAppointmentModel(
//                            apiID: item.id ?? 0,
//                            title: item.appointmentType?.name ?? "N/A", patientName: <#String#>,
//                            doctorName: item.preferredDoctor ?? "N/A",
//                            category: "For: \(item.familyMember?.fullName ?? "N/A")",
//                            date: item.date ?? "",
//                            time: self.formatTime(item.time ?? ""),
//                            location: item.preferredClinic ?? "",
//                            description: item.description ?? "",
//                            image: self.getImageByType(item.appointmentType?.name ?? ""),
//                            isCompleted: item.completeStatus == 1
//                        )
//                    }
                    
                    self.appointments = (response.data?.data ?? []).map { item in

                        let patientName: String

                        if item.appointmentForWhom == "self" {
                            patientName = item.user?.name ?? "N/A"
                        } else {
                            patientName = item.familyMember?.fullName ?? item.user?.name ?? "N/A"
                        }

                        return ScheduleAppointmentModel(
                            apiID: item.id ?? 0,
                            title: item.appointmentType?.name ?? "N/A",
                            patientName: patientName,
                            doctorName: item.preferredDoctor ?? "N/A",
                            category: "For: \(patientName)",
                            date: item.date ?? "",
                            time: self.formatTime(item.time ?? ""),
                            location: item.preferredClinic ?? "",
                            description: item.description ?? "",
                            image: self.getImageByType(item.appointmentType?.name ?? ""),
                            isCompleted: item.completeStatus == 1,
                            familyMemberID: item.familyMemberID,
                            appointmentForWhom: item.appointmentForWhom,
                            recommendedChatID: item.recommendedChatID
                        )
                    }
                    
                    print(self.scheduleAppointData,"scheduleAppointData ")
                    
                    completion(true)
                } else {
                    self.errorMessage = response.message ?? "Unknown error"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            
            .store(in: &cancellables)
    }
    
    
    
    
    func formatTime(_ time: String) -> String {
        let inputFormatter = DateFormatter()
        inputFormatter.dateFormat = "HH:mm:ss"
        
        let outputFormatter = DateFormatter()
        outputFormatter.dateFormat = "hh:mm a"
        
        if let date = inputFormatter.date(from: time) {
            return outputFormatter.string(from: date)
        }
        return time
    }
    func getImageByType(_ type: String) -> String {
        switch type {
        case "Dental Check-up":
            return "TeethIcon"
        case "Brain Check-up":
            return "BrainIcon"
        default:
            return "streamline-plump_medical-bag"
        }
    }
}
