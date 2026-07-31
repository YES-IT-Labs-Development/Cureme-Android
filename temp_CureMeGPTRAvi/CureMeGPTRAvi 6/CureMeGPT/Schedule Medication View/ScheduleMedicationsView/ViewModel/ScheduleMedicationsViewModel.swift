//
//  ScheduleMedicationsViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 30/12/25.
//

import Foundation
import SwiftUI
import Combine

final class ScheduleMedicationsViewModel: ObservableObject {
    
    enum Tab {
        case appointments
        case medications
    }
    
    @Published var selectedTab: Tab = .medications
    @Published var searchText: String = "" {
        didSet {
            if searchText.isEmpty {
                selectedMemberId = nil
                selectedMemberRelation = nil
                if selectedMemberName != nil {
                    selectedMemberName = nil
                }
            }
        }
    }
    @Published var showMedicationMenu: Bool = false
    @Published var selectedMedicationID: UUID? = nil
    @Published var showMenu = false
    @Published var menuPosition: CGPoint? = nil

    @Published var medicationPopup: MedicationMenuAction?
    @Published var medicationMenuPosition: CGPoint?
    
    @Published var selectedMemberName: String? = nil {
        didSet {
            if let name = selectedMemberName, !name.isEmpty {
                if searchText != name {
                    searchText = name
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
    
    @Published var showToast = false
    @Published var toastMessage = ""
    
    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    
    private var cancellables = Set<AnyCancellable>()
    
    @Published var selectedMedication: ScheduleMedicationsModel?
    
    var shouldOpenMemberFilter: Bool {
        // decide when to open member popup
        // change this logic as per your requirement
        return !searchText.isEmpty
    }
    
    @Published var medications: [ScheduleMedicationsModel] = []

    
//    var filteredMedications: [ScheduleMedicationsModel] {
//        if searchText.isEmpty {
//            return medications
//        }
//        return medications.filter {
//            $0.title.localizedCaseInsensitiveContains(searchText)
//        }
//    }
    
    var filteredMedications: [ScheduleMedicationsModel] {
        return medications.filter { medication in
            
            let matchesSearch = searchText.isEmpty ||
            medication.title.localizedCaseInsensitiveContains(searchText) ||
            medication.memberName.localizedCaseInsensitiveContains(searchText)
            
            let matchesMember: Bool
            if let memberId = selectedMemberId {
                if selectedMemberRelation?.lowercased() == "myself" {
                    matchesMember = medication.medicationForWhom?.lowercased() == "self"
                } else {
                    matchesMember = medication.familyMemberID == memberId
                }
            } else {
                matchesMember = true
            }
            
            return matchesSearch && matchesMember
        }
    }
    
    func applyFilter(
        type: FilterAppointmentViewModel.AppointmentType,
        member: String
    ) {
        // apply appointment filter
    }

    func applyMemberFilter(name: String, id: Int? = nil, relation: String? = nil) {
        selectedMemberId = id
        selectedMemberRelation = relation
        selectedMemberName = name
    }
    
    
//  for GetFamilyMember
    func getMedicationListAPI(completion: @escaping (Bool) -> Void) {
       // isLoading = true
        self.showActivity = true
        APIManager.shared.getMedicationListAPI()
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
                    print(response.data ?? "","YAHOO Data")
                    let list = response.data?.data ?? []
//                          self.medications = list.map { item in
//                              ScheduleMedicationsModel(
//                                appID: item.id ?? 0, title: item.medicationName ?? "",
//                                  dosage: item.dosage ?? "",
//                                  memberName: item.familyMember?.fullName ?? "",
//                                  medicationType: item.medicationType ?? "",
//                                  frequency: item.frequency ?? "",
//                                  days: item.days?.components(separatedBy: ",") ?? [],
//                                  times: item.reminderTime ?? [],
//                                  startDate: item.startDate ?? "",
//                                  endDate: item.endDate ?? "",
//                                  note: item.notes ?? "",
//                                  image: item.prescriptionDocs ?? "", // you can load image from URL
//                                  doctorName: "",
//                                  category: "",
//                                  location: "",
//                                  description: ""
//                              )
//                          }
                    
                    self.medications = list.map { item in

                        let memberName: String

                        switch item.medicationForWhom?.lowercased() {
                        case "family":
                            memberName = item.familyMember?.fullName
                                ?? item.user?.name
                                ?? "N/A"

                        case "self":
                            memberName = item.user?.name
                                ?? "N/A"

                        default:
                            memberName = item.user?.name
                                ?? item.familyMember?.fullName
                                ?? "N/A"
                        }

                        return ScheduleMedicationsModel(
                            appID: item.id ?? 0,
                            title: item.medicationName ?? "",
                            dosage: item.dosage ?? "",
                            memberName: memberName,
                            medicationType: item.medicationType ?? "",
                            frequency: item.frequency ?? "",

                            days: item.days?
                                .components(separatedBy: ",")
                                .map { $0.trimmingCharacters(in: .whitespaces) } ?? [],

                            times: (item.reminderTime ?? []).map { self.formatTime12Hour($0) },

                            startDate: item.startDate ?? "",
                            endDate: item.endDate ?? "",
                            note: item.notes ?? "",
                            image: item.prescriptionDocs ?? "",

                            doctorName: "",
                            category: "",
                            location: "",
                            description: "",
                            
                            familyMemberID: item.familyMemberID,
                            medicationForWhom: item.medicationForWhom
                        )
                    }
                      
                } else {
                    self.errorMessage = response.message ?? "Unknown error"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            
            .store(in: &cancellables)
    }
    func deleteMedicationAPI(medicationID: Int, completion: @escaping (Bool) -> Void) {
        
        self.showActivity = true
        
        APIManager.shared.deleteMedicationAPI(medication_id: medicationID)
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
                    print("Delete API finished")
                }
                
            } receiveValue: { [weak self] response in
                guard let self = self else { return }
                
                if response.success ?? false {
                    
                    // REMOVE FROM LOCAL LIST
                    self.medications.removeAll { $0.appID == medicationID }
                    
                    // Toast
                    self.toastMessage = response.message ?? "Deleted successfully"
                    self.showToast = true
                    
                    completion(true)
                    
                } else {
                    self.errorMessage = response.message ?? "Unknown error"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            .store(in: &cancellables)
    }
    
    private func formatTime12Hour(_ timeStr: String) -> String {
        let inputFormatter = DateFormatter()
        inputFormatter.locale = Locale(identifier: "en_US_POSIX")
        inputFormatter.dateFormat = "HH:mm"
        
        let outputFormatter = DateFormatter()
        outputFormatter.locale = Locale(identifier: "en_US_POSIX")
        outputFormatter.dateFormat = "hh:mm a"
        
        if let date = inputFormatter.date(from: timeStr) {
            return outputFormatter.string(from: date)
        }
        
        // Try with seconds "HH:mm:ss"
        inputFormatter.dateFormat = "HH:mm:ss"
        if let date = inputFormatter.date(from: timeStr) {
            return outputFormatter.string(from: date)
        }
        
        return timeStr
    }
}
