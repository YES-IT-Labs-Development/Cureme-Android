//
//  AboutPrivacyTermViewModel.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 24/11/25.
//

import SwiftUI
import Combine

class AboutPrivacyTermViewModel: ObservableObject {

    @Published var aboutUsData : AboutUsModel?

    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false

    private var cancellables = Set<AnyCancellable>()

    func apiAboutUsData() {

        showActivity = true

        APIManager.shared.apiAboutUsData()
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
                    self.aboutUsData = response.data
                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                }

            }
            .store(in: &cancellables)
    }
  
}


struct AboutUsModel: Codable {
    let data: AboutUsData?
}

// MARK: - DataData
struct AboutUsData: Codable {
    let id: Int?
    let title, content, status, slug: String?
    let createdAt, updatedAt: String?

    enum CodingKeys: String, CodingKey {
        case id, title, content, status, slug
        case createdAt = "created_at"
        case updatedAt = "updated_at"
    }
}
