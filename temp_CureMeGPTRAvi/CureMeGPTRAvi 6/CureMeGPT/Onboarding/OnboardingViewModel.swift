//
//  OnboardingViewModel.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 13/03/26.
//
import SwiftUI
import Combine

class OnboardingViewModel: ObservableObject {

    @Published var onboardingItems: [OnboardingModel] = []

    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false

    private var cancellables = Set<AnyCancellable>()

    func apiForOnboardingData() {

        showActivity = true

        APIManager.shared.apiOnboardingData()
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
                    self.onboardingItems = response.data?.data ?? []
                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                }

            }
            .store(in: &cancellables)
    }
}
