//
//  AppointmentSummaryViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 07/01/26.
//

import Foundation
import Combine
import UIKit


class AppointmentSummaryViewModel: ObservableObject {

    @Published var isPresented: Bool = false
    @Published var title: String = ""
    @Published var summary: String = ""
    @Published var symptoms: [String] = []
    @Published var recommendations: [String] = []
    @Published var isLoading: Bool = false
    
    @Published var showToast = false
    @Published var toastMessage = ""

    private var cancellables = Set<AnyCancellable>()

    func showSummary(chatId: Int) {
        self.isPresented = true
        self.isLoading = true
        self.title = ""
        self.summary = ""
        self.symptoms = []
        self.recommendations = []
        
        APIManager.shared.viewSummaryAPI(chatID: chatId)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] completion in
                self?.isLoading = false
                if case .failure(let error) = completion {
                    self?.toastMessage = error.localizedDescription
                    self?.showToast = true
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        self?.showToast = false
                    }
                }
            } receiveValue: { [weak self] response in
                guard let self = self else { return }
                if response.success ?? false, let data = response.data {
                    self.title = data.title ?? "Summary"
                    self.summary = data.summary ?? ""
                    self.symptoms = data.symptoms ?? []
                    self.recommendations = data.recommendations ?? []
                } else {
                    self.toastMessage = response.message ?? "Failed to fetch summary"
                    self.showToast = true
                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                        self.showToast = false
                    }
                }
            }
            .store(in: &cancellables)
    }

    func dismiss() {
        isPresented = false
    }
    
    func downloadSummary() {
        let fileName = "AppointmentSummary.txt"
        
        guard let documentsDirectory = FileManager.default.urls(
            for: .documentDirectory,
            in: .userDomainMask
        ).first else {
            return
        }
        
        let fileURL = documentsDirectory.appendingPathComponent(fileName)
        
        var fileContent = ""
        if !title.isEmpty {
            fileContent += "Title: \(title)\n\n"
        }
        if !summary.isEmpty {
            fileContent += "Summary: \(summary)\n\n"
        }
        if !symptoms.isEmpty {
            fileContent += "Symptoms:\n"
            for symptom in symptoms {
                fileContent += "- \(symptom)\n"
            }
            fileContent += "\n"
        }
        if !recommendations.isEmpty {
            fileContent += "Recommendations:\n"
            for recommendation in recommendations {
                fileContent += "- \(recommendation)\n"
            }
        }
        
        do {
            try fileContent.write(
                to: fileURL,
                atomically: true,
                encoding: .utf8
            )
            
            DispatchQueue.main.async {
//                self.toastMessage = "Summary saved successfully."
//                self.showToast = true
                
//                DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
//                    self.showToast = false
//                }
                
                // Present share sheet
                let activityVC = UIActivityViewController(
                    activityItems: [fileURL],
                    applicationActivities: nil
                )
                if let rootVC = UIApplication.shared.windows.first?.rootViewController {
                    var topController = rootVC
                    while let presented = topController.presentedViewController {
                        topController = presented
                    }
                    topController.present(activityVC, animated: true)
                }
            }
            
            print("✅ File Saved:")
            print(fileURL.path)
            
        } catch {
            print("❌ Error Saving File:", error.localizedDescription)
            
            DispatchQueue.main.async {
                self.toastMessage = error.localizedDescription
                self.showToast = true

                DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                    self.showToast = false
                }
            }
        }
    }
}

