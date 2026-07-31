//
//  AskQuestionsViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 05/12/25.
//

import SwiftUI
import Combine

class FAQViewModel: ObservableObject {

    @Published var faQItems: [FAQItem] = []

    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false

    private var cancellables = Set<AnyCancellable>()

    func apiForFAQData() {

        showActivity = true

        APIManager.shared.apiFAQData()
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
                    self.faQItems = response.data?.data ?? []
                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                }

            }
            .store(in: &cancellables)
    }
    func toggleFAQ(_ item: FAQItem) {
        if let index = faQItems.firstIndex(where: { $0.id == item.id }) {
            faQItems[index].isExpanded.toggle()
        }
    }
}
