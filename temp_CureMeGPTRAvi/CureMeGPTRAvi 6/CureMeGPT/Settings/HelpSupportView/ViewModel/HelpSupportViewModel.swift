//
//  HelpSupportViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 05/12/25.
//
//
//import SwiftUI
//import Combine
//
//final class HelpSupportViewModel: ObservableObject {
//    
//    
//    @Published var helpSupportData: HelpSupportModel?
//    @Published var showActivity = false
//    @Published var errorMessage: String?
//    @Published var isPresentAlert = false
//
//    private var cancellables = Set<AnyCancellable>()
//
//    @Published var supportEmail: String = "support@curemegpt.com"
//
//    func sendEmail() {
//        guard let url = URL(string: "mailto:\(supportEmail)") else { return }
//        UIApplication.shared.open(url)
//    }
//
//    func openFAQ() {
//        // Replace with FAQ URL screen navigation if needed
//        if let url = URL(string: "https://your-faq-link.com") {
//            UIApplication.shared.open(url)
//        }
//    }
//
//    func openFeedbackForm() {
//        // Redirect to feedback / suggestions page
//        if let url = URL(string: "https://your-feedback-link.com") {
//            UIApplication.shared.open(url)
//        }
//    }
//    
//    
//    func apiForHelpSupport() {
//
//        showActivity = true
//
//        APIManager.shared.apiHelpSupport()
//            .receive(on: DispatchQueue.main)
//            .sink { [weak self] result in
//
//                guard let self = self else { return }
//                self.showActivity = false
//
//                if case .failure(let error) = result {
//                    self.errorMessage = error.localizedDescription
//                    self.isPresentAlert = true
//                }
//
//            } receiveValue: { [weak self] response in
//
//                guard let self = self else { return }
//                self.showActivity = false
//
//                if response.success ?? false {
//                    self.helpSupportData = response.data?.data 
//                } else {
//                    self.errorMessage = response.message
//                    self.isPresentAlert = true
//                }
//
//            }
//            .store(in: &cancellables)
//    }
//}

import SwiftUI
import Combine

final class HelpSupportViewModel: ObservableObject {

    @Published var helpSupportData: HelpSupportModel?
    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    @Published var supportEmail: String = "support@curemegpt.com"

    private var cancellables = Set<AnyCancellable>()

    // MARK: - Send Email
    func sendEmail() {
        guard let url = URL(string: "mailto:\(supportEmail)"),
              UIApplication.shared.canOpenURL(url) else { return }

        UIApplication.shared.open(url)
    }

    // MARK: - Open FAQ
    func openFAQ() {
        guard let url = URL(string: "https://your-faq-link.com"),
              UIApplication.shared.canOpenURL(url) else { return }

        UIApplication.shared.open(url)
    }

    // MARK: - Open Feedback
    func openFeedbackForm() {
        guard let url = URL(string: "https://your-feedback-link.com"),
              UIApplication.shared.canOpenURL(url) else { return }

        UIApplication.shared.open(url)
    }

    // MARK: - Help Support API
    func apiForHelpSupport() {

        showActivity = true

        APIManager.shared.apiHelpSupport()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] completion in

                guard let self else { return }
                self.showActivity = false

                if case .failure(let error) = completion {
                    self.errorMessage = error.localizedDescription
                    self.isPresentAlert = true
                }

            } receiveValue: { [weak self] response in

                guard let self else { return }

                if response.success == true {
                    self.helpSupportData = response.data
                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                }
            }
            .store(in: &cancellables)
    }
}
