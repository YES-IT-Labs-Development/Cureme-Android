//
//  HealthReportViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 30/12/25.
//

import Foundation
import SwiftUI

//final class HealthReportViewModel: ObservableObject {
//    
//    @Published var searchText: String = ""
//    @Published var showMenu: Bool = false
//    @Published var selectedHealthReportID: UUID?
//    @Published var menuPosition: CGPoint = .zero
//    
//    var shouldOpenMemberFilter: Bool {
//        // decide when to open member popup
//        // change this logic as per your requirement
//        return !searchText.isEmpty
//    }
//    
//    @Published var reports: [HealthReportModel] = [
//        HealthReportModel(
//            title: "Dental X-ray Analysis",
//            doctorName: "Dr. Emily Rodriguez",
//            patientName: "For: Peter Logan",
//            date: "08/26/2025",
//            time: "10:30 AM",
//            location: "Health Center, 120 Cooper Square, New York NY 10003, USA",
//            description: "Minor cavity detected in upper left molar. Early intervention recommended.",
//            image: "BagIcon",
//            fileCount: "2",
//            status: .attention
//        ),
//        HealthReportModel(
//            title: "Blood Test Results",
//            doctorName: "Dr. Sarah Johnson",
//            patientName: "For: Rosy Logan",
//            date: "08/26/2025",
//            time: "10:30 AM",
//            location: "120 Cooper Square, New York NY 10003, USA",
//            description: "All blood markers within normal range. Excellent overall health indicators.",
//            image: "TeethIcon",
//            fileCount: "2",
//            status: .normal
//        ), HealthReportModel(
//            title: "Dental X-ray Analysis",
//            doctorName: "Dr. Emily Rodriguez",
//            patientName: "For: Peter Logan",
//            date: "08/26/2025",
//            time: "10:30 AM",
//            location: "Health Center, 120 Cooper Square, New York NY 10003, USA",
//            description: "Minor cavity detected in upper left molar. Early intervention recommended.",
//            image: "BagIcon",
//            fileCount: "2",
//            status: .attention
//        ),
//        HealthReportModel(
//            title: "Blood Test Results",
//            doctorName: "Dr. Sarah Johnson",
//            patientName: "For: Rosy Logan",
//            date: "08/26/2025",
//            time: "10:30 AM",
//            location: "120 Cooper Square, New York NY 10003, USA",
//            description: "All blood markers within normal range. Excellent overall health indicators.",
//            image: "TeethIcon",
//            fileCount: "2",
//            status: .normal
//        ), HealthReportModel(
//            title: "Dental X-ray Analysis",
//            doctorName: "Dr. Emily Rodriguez",
//            patientName: "For: Peter Logan",
//            date: "08/26/2025",
//            time: "10:30 AM",
//            location: "Health Center, 120 Cooper Square, New York NY 10003, USA",
//            description: "Minor cavity detected in upper left molar. Early intervention recommended.",
//            image: "BagIcon",
//            fileCount: "2",
//            status: .attention
//        ),
//        HealthReportModel(
//            title: "Blood Test Results",
//            doctorName: "Dr. Sarah Johnson",
//            patientName: "For: Rosy Logan",
//            date: "08/26/2025",
//            time: "10:30 AM",
//            location: "120 Cooper Square, New York NY 10003, USA",
//            description: "All blood markers within normal range. Excellent overall health indicators.",
//            image: "TeethIcon",
//            fileCount: "2",
//            status: .normal
//        )
//    ]
//    
//    var filteredReports: [HealthReportModel] {
//        if searchText.isEmpty {
//            return reports
//        }
//        return reports.filter {
//            $0.title.localizedCaseInsensitiveContains(searchText)
//        }
//    }
//    
////    func applyFilter(
////        type: HealthReportViewModel.filteredReports,
////        member: String
////    ) {
////        // apply appointment filter
////    }
//
//    func applyMemberFilter(_ member: String) {
//        // apply member-only filter
//    }
//}

import Foundation
import SwiftUI
import Combine

final class HealthReportViewModel: ObservableObject {
    
    // MARK: - Published Properties
    
    @Published var searchText: String = "" {
        didSet {
            if searchText.isEmpty {
                if !filteredMember.isEmpty {
                    filteredMember = ""
                }
                if selectedMember != nil {
                    selectedMember = nil
                }
            }
        }
    }
    @Published var showMenu: Bool = false
    @Published var selectedHealthReportID: Int?
    @Published var menuPosition: CGPoint = .zero
    
    @Published var reports: [HealthReportData] = []
    @Published var filteredMember: String = ""
    
    // MARK: - Loader & Alert
    
    @Published var showActivity: Bool = false
    @Published var errorMessage: String = ""
    @Published var isPresentAlert: Bool = false
    
    @Published var selectedMember: FamilyMembers? {
        didSet {
            if let member = selectedMember {
                if filteredMember != member.name {
                    filteredMember = member.name
                }
                if searchText != member.name {
                    searchText = member.name
                }
            } else {
                if !filteredMember.isEmpty {
                    filteredMember = ""
                }
                if !searchText.isEmpty {
                    searchText = ""
                }
            }
        }
    }
   
    
    // MARK: - Combine
    
    private var cancellables = Set<AnyCancellable>()
    
    // MARK: - Computed
    
    var shouldOpenMemberFilter: Bool {
        return !searchText.isEmpty
    }
    
    var filteredReports: [HealthReportData] {
        
        var filtered = reports
        
        // MARK: Search Filter
        
        if !searchText.isEmpty {
            
            filtered = filtered.filter {
                
                ($0.title?.localizedCaseInsensitiveContains(searchText) ?? false)
                ||
                ($0.userName?.localizedCaseInsensitiveContains(searchText) ?? false)
                ||
                ($0.familyName?.localizedCaseInsensitiveContains(searchText) ?? false)
                ||
                ($0.aiMessage?.localizedCaseInsensitiveContains(searchText) ?? false)
            }
        }
        
        // MARK: Member Filter
        
        if !filteredMember.isEmpty {
            
            filtered = filtered.filter {
                
                ($0.userName?.localizedCaseInsensitiveContains(filteredMember) ?? false)
                ||
                ($0.familyName?.localizedCaseInsensitiveContains(filteredMember) ?? false)
            }
        }
        
        return filtered
    }
    
    // MARK: - Init
    
    init() {
        getHealthReportsAPI()
    }
}

// MARK: - API

extension HealthReportViewModel {
    
    func getHealthReportsAPI(completion: @escaping (Bool) -> Void = { _ in }) {
        
        self.showActivity = true
        
        APIManager.shared.getHealthReportsAPI()
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
                    print("Health Reports API finished")
                }
                
            } receiveValue: { [weak self] response in
                
                guard let self = self else { return }
                
                if response.success ?? false {
                    
                    self.reports = response.data ?? []
                    
                    print(self.reports, "Health Reports")
                    
                    completion(true)
                    
                } else {
                    
                    self.errorMessage = response.message ?? "Unknown error"
                    self.isPresentAlert = true
                    
                    completion(false)
                }
            }
            .store(in: &cancellables)
    }
}

// MARK: - Filters

extension HealthReportViewModel {
    
    func applyMemberFilter(_ member: String) {
        filteredMember = member
    }
    
    func clearFilters() {
        filteredMember = ""
        searchText = ""
    }
}

// MARK: - Helpers

extension HealthReportViewModel {
    
    func patientDisplayName(for report: HealthReportData) -> String {
        
        if let userName = report.userName,
           !userName.isEmpty {
            return "For: \(userName)"
        }
        
        if let familyName = report.familyName,
           !familyName.isEmpty {
            return "For: \(familyName)"
        }
        
        return "For: Unknown"
    }
    
    func reportStatus(for severity: String?) -> ReportStatus {
        
        switch severity?.lowercased() {
            
        case "low":
            return .normal
            
        case "medium":
            return .attention
            
        case "high":
            return .attention
            
        default:
            return .normal
        }
    }
}
