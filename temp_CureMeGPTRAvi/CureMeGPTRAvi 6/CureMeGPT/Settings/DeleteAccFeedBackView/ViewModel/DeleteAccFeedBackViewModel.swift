//
//  DeleteAccFeedBackViewModel.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 25/11/25.
//

import SwiftUI
import Combine

class DeleteAccFeedBackViewModel: ObservableObject {
    @Published var feedback: String = ""
    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    
    private var cancellables = Set<AnyCancellable>()
    
    func deleteAccount() {
        showActivity = true
        
        APIManager.shared.deleteAccountAPI(feedback: feedback)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                guard let self = self else { return }
                self.showActivity = false
                
                if case .failure(let error) = result {
                    self.errorMessage = error.localizedDescription
                    self.isPresentAlert = true
                }
            } receiveValue: { [weak self] response in
                guard let self = self else { return }
                self.showActivity = false
                
                if response.success ?? false {
                    // Successfully deleted account. Trigger logout notification to clean up and route to login.
                    NotificationCenter.default.post(name: Notification.Name("logout"), object: nil)
                } else {
                    self.errorMessage = response.message ?? "Failed to delete account"
                    self.isPresentAlert = true
                }
            }
            .store(in: &cancellables)
    }
}
