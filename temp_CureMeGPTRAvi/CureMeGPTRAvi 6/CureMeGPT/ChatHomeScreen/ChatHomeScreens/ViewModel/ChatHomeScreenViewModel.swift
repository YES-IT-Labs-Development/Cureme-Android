//
//  ChatHomeScreenViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/01/26.
//

import Foundation
import Combine

final class ChatHomeScreenViewModel: ObservableObject {
    
    @Published var showActivity = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    
    
    @Published var currentChatId: Int? = nil

    private var cancellables = Set<AnyCancellable>()
    
    @Published var membersListDetails: [FamilyDetail] = []
    
    @Published var promptQuestionList : [PromptQuestion] = []
    
    @Published var selectedMemberDetail: FamilyDetail?
    
    @Published var questions: [SuggestedQuestion] = []
    @Published var getFitQuestions: [SuggestedQuestion] = []
    
    
    var greetingText: String {
        let hour = Calendar.current.component(.hour, from: Date())
        
        switch hour {
        case 5..<12:
            return "Good Morning"
        case 12..<17:
            return "Good Afternoon"
        case 17..<21:
            return "Good Evening"
        default:
            return "Good Night"
        }
    }
   
    //  for GetFamilyMember
        func familyListQuestionPromptAPI(completion: @escaping (Bool) -> Void) {
           // isLoading = true
            self.showActivity = true
            APIManager.shared.getPromptQuestionFamilyDetailAPI()
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
                        
                        
                        self.promptQuestionList = response.data?.promptQuestions ?? []
                        
                        let allQuestions = response.data?.promptQuestions ?? []
                           
                           // GENERAL
                           self.questions = allQuestions
                               .filter { $0.category?.rawValue.lowercased() == "general" }
                               .map { SuggestedQuestion(text: $0.question ?? "") }
                           
                           // GET FIT
                           self.getFitQuestions = allQuestions
                               .filter { $0.category?.rawValue.lowercased() == "getfit" }
                               .map { SuggestedQuestion(text: $0.question ?? "") }
                        
                        print(self.promptQuestionList,"promptQuestionList Data yahi hai")
                        
                        var combinedList: [FamilyDetail] = []

                        //  Add logged-in user
                        if let user = response.data?.userDetails {
                            let selfMember = FamilyDetail(
                                id:0,
                                name: "\(user.name ?? "")",
                                relationship: "MySelf",
                                profilePhoto: user.profilePhoto?.imgFullPath() ?? UserDetail.shared.getProfileImg()
                            )
                            combinedList.append(selfMember)
                        }

                        //  Add family members
                        if let familyList = response.data?.familyDetails {
                            combinedList.append(contentsOf: familyList)
                        }

                        // Assign
                       // self.membersListDetails = combinedList
                        
                        let previousSelectedId = self.selectedMemberDetail?.id

                        self.membersListDetails = combinedList

                        if let previousSelectedId,
                           let selected = combinedList.first(where: { $0.id == previousSelectedId }) {
                            self.selectedMemberDetail = selected
                        } else {
                            self.selectedMemberDetail = combinedList.first
                        }

                        //print(self.membersListDetails, "Member List Details")

                        completion(true)
                    }  else {
                        self.errorMessage = response.message ?? "Unknown error"
                        self.isPresentAlert = true
                        completion(false)
                    }
                }
                .store(in: &cancellables)
        }
}
