//
//  SideMenuViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/01/26.
//
//

import SwiftUI
import Combine

// MARK: - ENUM

enum HistoryType {
    case chat
    case caseChat
    
    var title: String {
        switch self {
        case .chat: return "Chat History"
        case .caseChat: return "Case Chat History"
        }
    }
}

// MARK: - VIEW MODEL

class SideMenuViewModel: ObservableObject {
    @Published var selectedHistory: HistoryType = .chat
    @Published var searchText: String = ""
   
    @Published var isMenuOpen: Bool = false
    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false

    private var cancellables = Set<AnyCancellable>()
    
    @Published var showRenamePopup: Bool = false
    @Published var renameText: String = ""
    @Published var renamingItem: ChatData? = nil
    
    @Published var showSharePopup: Bool = false
    @Published var sharingItem: ChatData? = nil
    
    
    @Published var membersListDetails: [FamilyDetail] = []
    @Published var selectedMemberDetail: FamilyDetail?
   
    @Published var getChatList: [ChatData] = []
    
    @Published var showHistoryMenu: Bool = false
    @Published var historyMenuPosition: CGPoint?
    @Published var selectedHistoryItem: ChatData?

    @Published var chatHistory: [ChatData] = []
    @Published var caseHistory: [ChatData] = []
    
    func openMenu() {
        withAnimation {
            isMenuOpen = true
        }
    }
    
    func closeMenu() {
        withAnimation {
            isMenuOpen = false
        }
        
    }
    
   
    // MARK: - Search History
    var filteredHistory: [ChatData] {

        let source =
        selectedHistory == .chat
        ? chatHistory
        : caseHistory

        let query =
        searchText
            .trimmingCharacters(in: .whitespacesAndNewlines)
            .lowercased()

        guard !query.isEmpty else {
            return source
        }

        return source.filter {
            ($0.title ?? "")
                .lowercased()
                .contains(query)
        }
    }
    
    func deleteChat(chat_id: Int,  completion: @escaping (Bool) -> Void) {
        
        showActivity = true
        
        APIManager.shared.deleteChatAPI(chatID: chat_id)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                self?.showActivity = false
                
                switch result {
                case .failure(let error):
                    self?.errorMessage = error.localizedDescription
                    self?.isPresentAlert = true
                    completion(false)
                    
                case .finished:
                    break
                }
                
            } receiveValue: { [weak self] response in
                
                if response.success ?? false {
                    completion(true)
                } else {
                    self?.errorMessage = response.message
                    self?.isPresentAlert = true
                    completion(false)
                }
            }
            .store(in: &cancellables)
    }
    
    func renameChatAPI(id: String, newName: String, completion: @escaping (Bool) -> Void) {
        
        showActivity = true
        
        APIManager.shared.renameChat(chatID: id, title: newName)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                self?.showActivity = false
                
                switch result {
                case .failure(let error):
                    self?.errorMessage = error.localizedDescription
                    self?.isPresentAlert = true
                    completion(false)
                    
                case .finished:
                    break
                }
                
            } receiveValue: { [weak self] response in
                
                if response.success ?? false {
                    completion(true)
                } else {
                    self?.errorMessage = response.message
                    self?.isPresentAlert = true
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
                                name: "\(user.name ?? "")",
                                relationship: "MySelf",   // 👈 Important
                                profilePhoto: user.profilePhoto?.imgFullPath() ?? UserDetail.shared.getProfileImg()
                            )
                            combinedList.append(selfMember)
                        }
                        
                        // Add family members
                        combinedList.append(contentsOf: response.data?.familyDetails ?? [])
                        
                        self.membersListDetails = combinedList
                        
                        print(self.membersListDetails, "Member List Details")
                        
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
        func getchatList(completion: @escaping (Bool) -> Void) {
           // isLoading = true
            self.showActivity = true
            APIManager.shared.getChatList()
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
                        
                        let result = response.data
                        
                        let list = result?.data ?? []

                        self.getChatList = list

                        // ✅ Separate based on type
                        self.chatHistory = list
                            .filter { $0.type == .normal }

                        self.caseHistory = list
                            .filter { $0.type == .typeCase }

                        print("Chat History:", self.chatHistory)
                        print("Case History:", self.caseHistory)
                     
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
        func userFamilyChatList(completion: @escaping (Bool) -> Void) {
           // isLoading = true
            self.showActivity = true
            APIManager.shared.user_family_chat_list(family_member_id: "\(self.selectedMemberDetail?.id ?? 0)" )
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
                        
                        let result = response.data
                        
                        let list = result?.data ?? []

                        self.getChatList = list

                        // ✅ Separate based on type
                        self.chatHistory = list
                            .filter { $0.type == .normal }

                        self.caseHistory = list
                            .filter { $0.type == .typeCase }

                        print("Chat History:", self.chatHistory)
                        print("Case History:", self.caseHistory)
                     
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
