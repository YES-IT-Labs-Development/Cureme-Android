//
//  ReportDescriptionViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 07/01/26.
//

import SwiftUI
import Combine

// MARK: - UI Insight Model

struct ReportInsight: Identifiable {
    let id = UUID()
    let title: String
    let value: String
    let status: ReportDetailStatus
}

// MARK: - ViewModel

final class ReportDescriptionViewModel: ObservableObject {
    
    // MARK: - Published Properties
    @Published var shareableLink: String = ""
    @Published var selectedHealthReportID: Int?
    @Published var menuPosition: CGPoint = .zero
    
    // MARK: - Loader & Alert
    
    @Published var showActivity: Bool = false
    @Published var errorMessage: String = ""
    @Published var isPresentAlert: Bool = false
    
    // MARK: - Report Details
    
    @Published var title: String = ""
    @Published var date: String = ""
    @Published var patientName: String = ""
    @Published var profilePicURL: String? = nil
    
    @Published var summary: String = ""
    @Published var detailedAnalysis: String = ""
    
    // MARK: - AI Insights
    
    @Published var highlights: [ReportInsight] = []
    
    // MARK: - Attachments
    
    @Published var attachments: [Attachment] = []
    
    // MARK: - Private
    
    private var cancellables = Set<AnyCancellable>()
}

// MARK: - API Calling

extension ReportDescriptionViewModel {
    
    func getReportDetails(chat_id: String,
                          completion: @escaping (Bool) -> Void = { _ in }) {
        
        self.showActivity = true
        
        APIManager.shared.healthreportdetails(chat_id: chat_id)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                
                guard let self = self else { return }
                
                self.showActivity = false
                
                switch result {
                    
                case .failure(let error):
                    
                    self.errorMessage = error.localizedDescription
                    
                    if self.errorMessage.contains("no local endpoint") {
                        self.errorMessage = "Internal Server Error.\nPlease try again."
                    }
                    
                    self.isPresentAlert = true
                    
                    completion(false)
                    
                case .finished:
                    print("Health Report Details API Finished")
                }
                
            }
//        receiveValue: { [weak self] response in
//                
//                guard let self = self else { return }
//                
//                if response.success ?? false {
//                    
//                    guard let data = response.data else {
//                        completion(false)
//                        return
//                    }
//                    
//                    // MARK: - Basic Details
//                    
//                    self.title = data.title ?? ""
//                    self.date = data.chatDate ?? ""
//                    
//                    
//                   // self.patientName = data.userName ?? ""
//                    
//                    self.patientName = data.familyName?.isEmpty == false
//                        ? data.familyName!
//                        : (data.userName ?? "")
//                    
//                    // MARK: - Summary
//                    
//                    self.summary = data.summary ?? ""
//                    
//                    // MARK: - Detailed Analysis
//                    
//                    self.detailedAnalysis = data.detailedAnalysis ?? ""
//                    
//                    // MARK: - AI Insights
//                    
//                    var insightArray: [ReportInsight] = []
//                    
//                    if let severity = data.aiInsights?.severity,
//                       !severity.isEmpty {
//                        
//                        insightArray.append(
//                            ReportInsight(
//                                title: "Severity",
//                                value: severity.capitalized,
//                                status: severity.lowercased() == "medium" || severity.lowercased() == "high"
//                                ? .attention
//                                : .normal
//                            )
//                        )
//                    }
//                    
//                    if let reason = data.aiInsights?.reason,
//                       !reason.isEmpty {
//                        
//                        insightArray.append(
//                            ReportInsight(
//                                title: "Reason",
//                                value: reason,
//                                status: .attention
//                            )
//                        )
//                    }
//                    
//                    if let category = data.aiInsights?.category,
//                       !category.isEmpty {
//                        
//                        insightArray.append(
//                            ReportInsight(
//                                title: "Category",
//                                value: category.replacingOccurrences(of: "_", with: " ").capitalized,
//                                status: .normal
//                            )
//                        )
//                    }
//                    
//                    if let actionType = data.aiInsights?.actionType,
//                       !actionType.isEmpty {
//                        
//                        insightArray.append(
//                            ReportInsight(
//                                title: "Recommended Action",
//                                value: actionType.replacingOccurrences(of: "_", with: " ").capitalized,
//                                status: .attention
//                            )
//                        )
//                    }
//                    
//                    if let symptoms = data.aiInsights?.symptomAnalysis,
//                       !symptoms.isEmpty {
//                        
//                        insightArray.append(
//                            ReportInsight(
//                                title: "Symptoms",
//                                value: symptoms.joined(separator: ", "),
//                                status: .attention
//                            )
//                        )
//                    }
//                    
//                    self.highlights = insightArray
//                    
//                    // MARK: - Attachments
//                    
//                    self.attachments = (data.attachments ?? []).map { filePath in
//                        
//                        let fileName = URL(string: filePath)?.lastPathComponent ?? filePath
//                        
//                        return Attachment(
//                            name: fileName,
//                            icon: "doc.richtext"
//                        )
//                    }
//                    
//                    completion(true)
//                    
//                } else {
//                    
//                    self.errorMessage = response.message ?? "Unknown Error"
//                    self.isPresentAlert = true
//                    
//                    completion(false)
//                }
//            }
        receiveValue: { [weak self] response in
            
            guard let self = self else { return }
            
            if response.success ?? false {
                
                guard let data = response.data else {
                    completion(false)
                    return
                }
                
                // MARK: - Basic Details
                
                self.title = data.title ?? ""
                self.date = (data.chatDate ?? "").toAppDateString()
                
                // family_name first, if nil/empty then user_name
                self.patientName =
                !(data.familyName?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ?? true)
                ? data.familyName!
                : (data.userName ?? "")
                
                // Determine which profile picture to show: family profile pic or user profile pic
                let rawProfilePic = !(data.familyName?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ?? true)
                ? (data.familyProfilePic ?? data.userProfilePic)
                : data.userProfilePic
                
                if let rawProfilePic = rawProfilePic, !rawProfilePic.isEmpty {
                    self.profilePicURL = rawProfilePic.imgFullPath()
                } else {
                    self.profilePicURL = nil
                }
                
                // MARK: - Summary
                
                self.summary = data.summary ?? ""
                
                // MARK: - Detailed Analysis
                
                self.detailedAnalysis = data.detailedAnalysis ?? ""
                
                // MARK: - AI Insights
                
                var insightArray: [ReportInsight] = []
                
                // severity
                if let severity = data.aiInsights?.severity,
                   !severity.isEmpty {
                    
                    insightArray.append(
                        ReportInsight(
                            title: "Severity",
                            value: severity.capitalized,
                            status: severity.lowercased() == "high" || severity.lowercased() == "medium"
                            ? .attention
                            : .normal
                        )
                    )
                }
                
                // reason
                if let reason = data.aiInsights?.reason,
                   !reason.isEmpty {
                    
                    insightArray.append(
                        ReportInsight(
                            title: "Reason",
                            value: reason,
                            status: .attention
                        )
                    )
                }
                
                // category
                if let category = data.aiInsights?.category,
                   !category.isEmpty {
                    
                    insightArray.append(
                        ReportInsight(
                            title: "Category",
                            value: category
                                .replacingOccurrences(of: "_", with: " ")
                                .capitalized,
                            status: .normal
                        )
                    )
                }
                
                // action type
                if let actionType = data.aiInsights?.actionType,
                   !actionType.isEmpty {
                    
                    insightArray.append(
                        ReportInsight(
                            title: "Recommended Action",
                            value: actionType
                                .replacingOccurrences(of: "_", with: " ")
                                .capitalized,
                            status: .attention
                        )
                    )
                }
                
                // symptoms
                if let symptoms = data.aiInsights?.symptomAnalysis,
                   !symptoms.isEmpty {
                    
                    insightArray.append(
                        ReportInsight(
                            title: "Symptoms",
                            value: symptoms.joined(separator: ", "),
                            status: .attention
                        )
                    )
                }
                
                self.highlights = insightArray
                
                // MARK: - Attachments
                
                self.attachments = (data.attachments ?? []).map { filePath in
                    
                    let fileName = URL(string: filePath)?.lastPathComponent ?? filePath
                    
                    return Attachment(
                        name: fileName,
                        filePath: filePath,
                        icon: "doc.richtext"
                    )
                }
                
                completion(true)
                
            } else {
                
                self.errorMessage = response.message ?? "Unknown Error"
                self.isPresentAlert = true
                
                completion(false)
            }
        }
            .store(in: &cancellables)
    }
}
