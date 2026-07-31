//
//  SettingsViewModel.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 24/11/25.
//


import SwiftUI
import Combine

class SettingsViewModel: ObservableObject {

    @Published var settingsData: SettingResponseData?

    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false

    @Published var items: [SettingsModel] = []

    private var cancellables = Set<AnyCancellable>()

    func getSettingDataAPI() {

        showActivity = true

        APIManager.shared.apiForSettingData()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] completion in

                self?.showActivity = false

                if case .failure(let error) = completion {
                    self?.errorMessage = error.localizedDescription
                    self?.isPresentAlert = true
                }

            } receiveValue: { [weak self] response in

                guard let self else { return }

                self.showActivity = false

                if response.success ?? false {

                    self.settingsData = response.data

//                    // Convert API response → Settings UI
//                    self.items = response.data?.data.map {
//                        SettingsModel(
//                            icon: iconForSlug($0.slug),
//                            title: $0.title,
//                            isToggle: false,
//                            route: routeForSlug($0)
//                        )
//                    } ?? [] + [
//                        SettingsModel(
//                            icon: "AskImg",
//                            title: "Frequently Ask Questions",
//                            isToggle: false,
//                            route: .faqView
//                        )
//                    ]
                    
                    
                    self.items = response.data?.data.map {
                        SettingsModel(
                            icon: iconForSlug($0.slug),
                            title: $0.title,
                            isToggle: false,
                            route: routeForSlug($0)
                        )
                    } ?? []

                    self.items.append(
                        SettingsModel(
                            icon: "AskImg",
                            title: "Frequently Ask Questions",
                            isToggle: false,
                            route: .faqView
                        )
                    )

                    print("Items Count =", self.items.count)

                    for item in self.items {
                        print(item.title)
                    }

                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                }

            }
            .store(in: &cancellables)
    }
}

// MARK: - Helpers

func iconForSlug(_ slug: String) -> String {
    switch slug {
    case "about-us":
        return "AboutImg"

    case "privacy-policy":
        return "PrivacyImg"

    case "terms-conditions":
        return "TermImg"

    case "help-support":
        return "HelpImg"

    case "account-privacy":
        return "AccountImg"
        
    case "demo-page":
        return "AskImg"

    default:
        return "AskImg"
    }
}

func routeForSlug(_ item: SettingData) -> Route? {
    if item.slug == "account-privacy" {
        return .accountPrivacyView(data: item)
    } else if item.slug == "help-support" {
        return .helpSupportView(data: item)
    } else {
        return .aboutPrivacyTermView(data: item)
    }
}



// MARK: - Data Wrapper
struct SettingResponseData: Codable {
    let data: [SettingData]
}

// MARK: - Static Page 
struct SettingData: Codable,Hashable {
    let id: Int
    let title: String
    let content: String
    let slug: String
    let status: String
}
