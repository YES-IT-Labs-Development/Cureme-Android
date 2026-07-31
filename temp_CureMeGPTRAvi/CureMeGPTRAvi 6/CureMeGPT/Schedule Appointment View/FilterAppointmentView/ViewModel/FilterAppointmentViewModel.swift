//
//  FilterAppointmentViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 31/12/25.
//

import Foundation
import Combine



final class FilterAppointmentViewModel: ObservableObject {
    
    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    
   
    @Published var showToast = false
    @Published var toastMessage = ""
    
    @Published var memberList: [FamilyMemberModels] = []
   
    @Published var result: DataClass?
    
    @Published var membersListDetails: [FamilyDetail] = []
        @Published var selectedMemberDetail: FamilyDetail?

    @Published var membersList: [FamilyMembers] = []
    @Published var selectedMember: FamilyMembers?
    @Published var searchText: String = ""
   
    @Published var selectedMemberName: String = ""

    private var cancellables = Set<AnyCancellable>()

    @Published var selectedType: AppointmentType = .upcoming
   // @Published var selectedMember: String = ""

    let members = ["Peter Logan", "Sarah Logan", "John Logan"]

    func reset() {
        selectedType = .upcoming
       //selectedMember = ""
    }
    
    enum AppointmentType {
        case upcoming
        case past
    }
    
 
    var filteredMembers: [FamilyMemberModels] {
        
        // ✅ If member selected → show only that member
        if let selected = selectedMember {
            return memberList.filter {
                ($0.fullName ?? "") == (selected.name ?? "")
            }
        }
        
        // ✅ Else apply search
        if !searchText.isEmpty {
            return memberList.filter {
                ($0.fullName ?? "").localizedCaseInsensitiveContains(searchText) ||
                ($0.relationship ?? "").localizedCaseInsensitiveContains(searchText)
            }
        }
        
        // ✅ Default
        return memberList
    }
    
    func resetSearch() {
        searchText = ""
        selectedMemberDetail = nil   // 🔥 IMPORTANT
    }
//
////  for GetFamilyMember
    func getFamilyMemberAPI(completion: @escaping (Bool) -> Void) {
       // isLoading = true
        self.showActivity = true
        APIManager.shared.getfamilymemberslistAPI()
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
                 
                    // 🔥 MAP API → MODEL
                    self.membersList = response.data?.people?.map {
                                        FamilyMembers(
                                            id: $0.id ?? 0,
                                            name: $0.name ?? "",
                                            relation: $0.relationship ?? ""
                                        )
                                    } ?? []
                    
                    print(self.membersList,"Member List")
                    
                    completion(true)
                } else {
                    self.errorMessage = response.message ?? "Unknown error"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            .store(in: &cancellables)
    }
    
    
//  for GetFamilyMember
    func userWithFamilyDetailsAPI(completion: @escaping (Bool) -> Void) {
       // isLoading = true
        self.showActivity = true
        APIManager.shared.userWithFamilyDetails()
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
                    var combinedList: [FamilyDetail] = []
                    // Add self user (jh)
                    if let user = response.data?.userDetails {
                        let selfMember = FamilyDetail(
                            id: user.id,
                            name: user.name ?? "",
                            relationship: "Myself",   // 👈 Important
                            profilePhoto: user.profilePhoto?.imgFullPath() ?? UserDetail.shared.getProfileImg()
                        )
                        combinedList.append(selfMember)
                    }
                    
                    // Add family members
                    combinedList.append(contentsOf: response.data?.familyDetails ?? [])
                    
                    self.membersListDetails = combinedList
                    
                    self.membersList = combinedList.map {
                        FamilyMembers(
                            id: $0.id ?? 0,
                            name: $0.name ?? "",
                            relation: $0.relationship ?? ""
                        )
                    }
                    
                    print(self.membersListDetails, "ye check karni hai Member List Details")
                    
                    completion(true)
                    
                } else {
                    self.errorMessage = response.message ?? "Unknown error"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            .store(in: &cancellables)
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
