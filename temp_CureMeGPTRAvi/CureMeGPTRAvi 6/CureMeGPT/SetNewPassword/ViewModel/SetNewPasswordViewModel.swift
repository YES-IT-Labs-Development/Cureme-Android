//
//  SetNewPasswordViewModel.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 17/11/25.
//

import SwiftUI
import Combine

class SetNewPasswordViewModel: ObservableObject {
    @Published var fullName = ""
    @Published var emailPhone = ""
    @Published var otp = 0
    @Published var newpassword = ""
    @Published var confirmPassword = ""
    @Published var isChecked = false
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var registrationSuccess = false
    @Published var createAccountResponse: CreateUser?
    
    @Published var isPresentAlert = false
    @Published var showActivity = false
    @Published var alertMsg: String = ""
    
    private let userDefaultManager: UserDefaultManagerProtocol
    
    private var cancellables = Set<AnyCancellable>()
    
    init(userDefaultsManager: UserDefaultManagerProtocol = USerDefaultManager()) {
        self.userDefaultManager = userDefaultsManager
    }
    
    func validateInputs() -> Bool {
       
        if newpassword.isEmpty {
            errorMessage = "Please enter password."
            isPresentAlert = true
            return false
        }
        
        let passwordRegEx = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$"
        let predicate = NSPredicate(format: "SELF MATCHES %@", passwordRegEx)
        if !predicate.evaluate(with: newpassword) {
            errorMessage = """
            Password must be at least 8 characters long, \
            and include at least one uppercase letter, one lowercase letter, one special character and one digit.
            """
            isPresentAlert = true
            return false
        }
        
        if confirmPassword.isEmpty {
            errorMessage = "Please enter your confirm password."
            isPresentAlert = true
            return false
        }
        
        if !predicate.evaluate(with: confirmPassword) {
            errorMessage =  """
            Confirm password must be at least 8 characters long, \
            and include at least one uppercase letter, one lowercase letter, one special character and one digit.
            """
            isPresentAlert = true
            return false
        }
        
        if newpassword != confirmPassword {
            errorMessage = "Passwords do not match. Please try again."
            isPresentAlert = true
            return false
        }
        
        //        if !isChecked {
        //            errorMessage = "You must agree to the Terms & Conditions."
        //            isPresentAlert = true
        //            return false
        //        }
        
        errorMessage = nil
        isPresentAlert = false
        return true
    }
    
    func setNewPasswordAPI(
        password: String,
        emailPhone: String,
        completion: @escaping (Bool) -> Void
        
    ) {
        guard self.validateInputs() else { return }
        DispatchQueue.main.async {
            self.showActivity = true   // START loader
        }
        APIManager.shared.updatePasswordApi(
            password: password,
            emailPhone: emailPhone
        )
        .sink { completionResult in

            switch completionResult {
            case .failure(let error):
                
                DispatchQueue.main.async {
                    self.showActivity = false   // STOP loader
                }
                DispatchQueue.main.async {
                    self.errorMessage = error.localizedDescription
                    self.isPresentAlert = true
                }
                completion(false)
               
            case .finished:
                break
            }
        } receiveValue: { response in
            
            self.showActivity = false

            if response.data != nil {
                completion(true)
                print(response)
                DispatchQueue.main.async {
                    self.showActivity = false   // STOP loader
                }
                } else {
                    self.errorMessage = response.message ?? "Something went wrong"
                    self.isPresentAlert = true
                    completion(false)
                    DispatchQueue.main.async {
                        self.showActivity = false   // STOP loader
                    }
                }
        }
        .store(in: &cancellables)
    }
 }
