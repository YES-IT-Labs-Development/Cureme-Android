//
//  Privacy&ConsentViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 01/12/25.
//

import SwiftUI
import Combine

class PrivacyConsentViewModel: ObservableObject {

    @Published var agreePrivacyPolicy: Bool = false
    @Published var agreeTerms: Bool = false
    @Published var understandMedicalAdvice: Bool = false
    @Published var consentAIProcessing: Bool = false
    
    @Published var showToast = false
    @Published var toastMessage = ""
    
    @Published var settingsData: SettingResponseData?

   
       private var cancellables = Set<AnyCancellable>()

    // Button can be enabled only when all mandatory items are selected
    var isFormValid: Bool {
        agreePrivacyPolicy &&
        agreeTerms &&
        understandMedicalAdvice &&
        consentAIProcessing
    }
    
    func validate() -> Bool {
          guard isFormValid else {
              toastMessage = "Please accept all required consents."
              showToast = true
              return false
          }
          return true
      }
    
    func getSettingDataAPI() {

         APIManager.shared.apiForSettingData()
             .receive(on: DispatchQueue.main)
             .sink { _ in

             } receiveValue: { [weak self] response in

                 guard let self = self else { return }

                 if response.success ?? false {
                     self.settingsData = response.data
                 }
             }
             .store(in: &cancellables)
     }

     func setting(for slug: String) -> SettingData? {
         settingsData?.data.first(where: { $0.slug == slug })
     }
}
