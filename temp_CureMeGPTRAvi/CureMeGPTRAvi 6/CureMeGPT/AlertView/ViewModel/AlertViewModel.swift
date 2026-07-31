//
//  AlertViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 31/12/25.

//    @Published var reminders: [AlertModel] = [
//        AlertModel(
//            userName: "Rosy Logan",
//            title: "Medication Reminder",
//            description: "Take your Lisinopril 10mg. Don't forget to take it with food.",
//            timeText: "Just Now",
//            priority: .normal,
//            isCompleted: false
//        ),
//        AlertModel(
//            userName: "James Logan",
//            title: "Urgent: Health Report Available",
//            description: "Your recent blood test results show elevated cholesterol levels. Please schedule a follow-up with your doctor.",
//            timeText: "30m ago",
//            priority: .high,
//            isCompleted: false
//        ),
//        AlertModel(
//            userName: "James Logan",
//            title: "Dental Cleaning Today",
//            description: "Don't forget your dental cleaning appointment today at 2:00 PM with Dr. Sarah Johnson.",
//            timeText: "32m ago",
//            priority: .medium,
//            isCompleted: false
//        )
//    ]
//

import SwiftUI
import Combine

final class AlertViewModel: ObservableObject {
    
    @Published var reminders: [AlertModel] = []
    @Published var toastMessage = ""
    @Published var showToast = false
    @Published var isLoading = false
    @Published var errorMessage: String?
    
   
    @Published var isPresentAlert = false
    
    private var cancellables = Set<AnyCancellable>()
    
    // MARK: - INIT
    init() {
        fetchAlerts()
    }
    
    // MARK: - FETCH ALERTS API
    func fetchAlerts() {
        
        isLoading = true
        errorMessage = nil
        
        APIManager.shared.getAlertDataAPI()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] completion in
                
                guard let self = self else { return }
                
                self.isLoading = false
                
                if case .failure(let error) = completion {
                    
                    self.errorMessage = error.localizedDescription
                    print("Alert API Error:", error.localizedDescription)
                }
                
            } receiveValue: { [weak self] response in
                
                guard let self = self else { return }
                
                self.isLoading = false
                
                if response.success ?? false {
                    
                        self.reminders = response.data?.map {

                            AlertModel(

                                id: $0.id ?? 0,

                                userName: $0.userName ?? "",

                                familyMemberName: $0.familyMemberName ?? "",

                                type: $0.type ?? "",

                                title: $0.title ?? "",

                                description: $0.message ?? "",

                                timeText: $0.notificationTime ?? "",

                                severity: $0.severity,

                                actionRequired: $0.actionRequired ?? 0,
                                
                                reference_id: $0.reference_id ?? 0,

                                priority: self.mapPriority($0.severity),
                                
                                appointmentcompletestatus: $0.appointmentcompletestatus ?? "",

                                isCompleted: ($0.isRead ?? 0) == 1
                            )

                        } ?? []
                    
                   // self.reminders.removeAll()
                        
                   
                    
                } else {
                    
                    self.errorMessage = response.message
                }
            }
            .store(in: &cancellables)
    }
    
    
    // MARK: - MARK COMPLETE API
    // MARK: - MARK COMPLETE / INCOMPLETE API
    func markAppointmentComplete(
        id: Int,
        completion: @escaping (Bool) -> Void
    ) {

        guard let appointment = reminders.first(where: { $0.id == id }) else {
            completion(false)
            return
        }

        // Toggle status
        let status =
        appointment.appointmentcompletestatus.lowercased() == "completed"
        ? "incompleted"
        : "completed"

        isLoading = true

        APIManager.shared
            .markAsCompleteAppointmentAPI(
                appointment_id: "\(appointment.reference_id ?? 0)",
            )
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in

                guard let self = self else { return }

                self.isLoading = false

                switch result {

                case .failure(let error):

                    self.errorMessage = error.localizedDescription
                    self.isPresentAlert = true
                    completion(false)

                case .finished:
                    break
                }

            } receiveValue: { [weak self] response in

                guard let self = self else { return }

                if response.success ?? false {

                    // UPDATE LOCAL STATUS
                    if let index = self.reminders.firstIndex(where: { $0.id == id }) {

                        self.reminders[index].appointmentcompletestatus = status
                    }

                    // TOAST
                    self.toastMessage = response.message ??
                    (status == "completed"
                     ? "Marked as completed"
                     : "Marked as incompleted")

                    self.showToast = true

                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        self.showToast = false
                    }

                    completion(true)

                } else {

                    self.errorMessage = response.message ?? "Something went wrong"
                    self.isPresentAlert = true
                    completion(false)
                }

            }
            .store(in: &cancellables)
    }
 

    // MARK: - COMPLETE BUTTON ACTION
    func toggleCompletion(for reminder: AlertModel) {

        markAppointmentComplete(id: reminder.id) { success in

            if success {
                print("Appointment completed")
            }
        }
    }
    
    private func mapPriority(_ value: String?) -> ReminderPriority {
        
        switch value?.lowercased() {
            
        case "high":
            return .high
            
        case "medium":
            return .medium
            
        default:
            return .normal
        }
    }
}
