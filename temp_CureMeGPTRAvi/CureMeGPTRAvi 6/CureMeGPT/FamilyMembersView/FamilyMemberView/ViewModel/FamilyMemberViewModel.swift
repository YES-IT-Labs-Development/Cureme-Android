//
//  FamilyMemberViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 05/01/26.
//

import SwiftUI
import Combine

final class FamilyMemberViewModel: ObservableObject {
    
    @Published var showToast = false
    @Published var toastMessage = ""
    
    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    
    private var cancellables = Set<AnyCancellable>()
    
    @Published var memberList: [FamilyMemberModels] = []
    
    @Published var searchText: String = ""
    
    @Published var result: DataClass?
    
    let totalFamilyAppointmentCount: Int = 0
      let totalFamilyMedicationCount: Int = 0
    
    
    private var searchCancellable: AnyCancellable?
    
    init() {
        searchCancellable = $searchText
            .debounce(for: .milliseconds(300), scheduler: DispatchQueue.main)
            .sink { _ in
                // future API search here
            }
    }
    
    @Published var members: [FamilyMemberModel] = [
        FamilyMemberModel(
            name: "James Logan",
            age: 40,
            relationship: .selfUser,
            imageName: "profile",
            progress: "2/2",
            date: "2",
        ),
        FamilyMemberModel(
            name: "Rose Logan",
            age: 35,
            relationship: .spouse,
            imageName: "Profile1",
            progress: "2/4",
            date: "2",
        ),
        FamilyMemberModel(
            name: "Peter Logan",
            age: 17,
            relationship: .son,
            imageName: "Profile2",
            progress: "2/3",
            date: "2",
        )
    ]
    
    
    var filteredMembers: [FamilyMemberModels] {
        if searchText.isEmpty {
            return memberList
        } else {
            return memberList.filter {
                ($0.fullName ?? "").localizedCaseInsensitiveContains(searchText) ||
                ($0.relationship ?? "").localizedCaseInsensitiveContains(searchText)
            }
        }
    }
    
    
    func resetSearch() {
        searchText = ""
    }
    //  for familyMember List
        func family_member_listAPI(completion: @escaping (Bool) -> Void) {
           // isLoading = true
            self.showActivity = true
            APIManager.shared.family_member_listAPI()
                .receive(on: DispatchQueue.main)
                .sink { [weak self] result in
                    guard let self = self else { return }
                    self.showActivity = false
                    switch result {
                    case .failure(let error):
                        self.errorMessage = error.localizedDescription
                        // Handle connection issues
                        if ((self.errorMessage?.contains("no local endpoint")) != nil) {
                           
                            self.errorMessage = "Internal Server Error. \nPlease try again."
                        }
                        
                        self.isPresentAlert = true
                        completion(false)
                    case .finished:
                        print("API call finished")
                    }
                } receiveValue: { [weak self] response in
                    guard let self = self else { return }
                    
                    if response.success ?? false {
                      
                        self.result = response.data
                        
                        print(result ?? "","result Data")
                        
                        self.memberList = self.result?.familyMembers ?? []
                      
                          
                    } else {
                        self.errorMessage = response.message ?? "Unknown error"
                        self.isPresentAlert = true
                        completion(false)
                    }
                }
                
                .store(in: &cancellables)
        }
    
    
    // MARK: - Delete Family Member
    func deleteFamilyMemberAPI(memberId: Int, completion: @escaping (Bool) -> Void) {
        
        self.showActivity = true
        
        APIManager.shared.deleteFamilyMemberAPI(family_member_id: memberId)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                guard let self = self else { return }
                
                self.showActivity = false
                
                switch result {
                case .failure(let error):
                    self.errorMessage = error.localizedDescription
                    
                    if ((self.errorMessage?.contains("no local endpoint")) != nil) {
                        self.errorMessage = "Internal Server Error.\nPlease try again."
                    }
                    
                    self.isPresentAlert = true
                    completion(false)
                    
                case .finished:
                    print("Delete API finished")
                }
                
            } receiveValue: { [weak self] response in
                guard let self = self else { return }
                
                if response.success ?? false {
                    
                    self.toastMessage = response.message ?? "Member deleted successfully"
                    self.showToast = true
                    
                    // ✅ Remove locally (important for UI update)
                    self.memberList.removeAll { $0.id == memberId }
                    
                    completion(true)
                    
                } else {
                    self.errorMessage = response.message ?? "Something went wrong"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            .store(in: &cancellables)
    }
}

