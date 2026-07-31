//
//  AccountPrivacyViewModel.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 24/11/25.
//

import Foundation
import SwiftUI
//
//class AccountPrivacyViewModel: ObservableObject {
//    @Published var model = AccountPrivacyModel(
//        description: """
//CureMeGPT values your privacy and gives you full control over your personal information. You can manage your account settings at any time — including changing your password or deleting your account — directly from Profile → Settings → Account Privacy.
//If you choose to delete your account, all your personal information, chat history, medical documents, and family member records will be permanently erased from our system and cannot be recovered.
//For added security, you can update or change your password anytime from the same section.
//We never sell or share your data, and your privacy and security will always remain our top priority.
//"""
//    )
//}



import SwiftUI
import Combine

class AccountPrivacyViewModel: ObservableObject {

    @Published var accountPolicyData : AccountPrivacyModel?

    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false

    private var cancellables = Set<AnyCancellable>()

    func apiprivacyPolicyData() {

        showActivity = true

        APIManager.shared.apiAccountPolicy()
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
                    self.accountPolicyData = response.data
                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                }

            }
            .store(in: &cancellables)
    }
  
}


