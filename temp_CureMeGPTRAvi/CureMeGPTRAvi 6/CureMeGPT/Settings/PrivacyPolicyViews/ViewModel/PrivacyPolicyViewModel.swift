//
//  AboutPrivacyTermViewModel.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 24/11/25.
//

import SwiftUI
import Combine

class PrivacyPolicyViewModel: ObservableObject {

    @Published var privacyPolicyData : PrivacyPolicyModel?

    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false

    private var cancellables = Set<AnyCancellable>()

    func apiprivacyPolicyData() {

        showActivity = true

        APIManager.shared.apiPrivaryPolicy()
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
                    self.privacyPolicyData = response.data
                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                }

            }
            .store(in: &cancellables)
    }
  
}


