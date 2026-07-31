//
//  LoginViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 03/03/26.
//

import Combine
import Foundation

class LoginViewModel: ObservableObject {
    
    @Published var emailPhone: String = ""
    @Published var password: String = ""
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    @Published var loginSuccess = false
    @Published var showActivity = false
    @Published var alertMsg: String = ""
    private var cancellables = Set<AnyCancellable>()
    
    func clearCredentials() {
        emailPhone = ""
        password = ""
        errorMessage = nil
        isPresentAlert = false
        loginSuccess = false
        showActivity = false
    }
    
    func isValidEmail(_ email: String) -> Bool {
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        return NSPredicate(format: "SELF MATCHES %@", emailRegEx).evaluate(with: email)
    }
    
    func isValidPhone(_ phone: String) -> Bool {
        let phoneRegEx = "^[0-9]{10,15}$" // Accepts 10 to 15 digits
        return NSPredicate(format: "SELF MATCHES %@", phoneRegEx).evaluate(with: phone)
    }
    
    func validateInputs() -> Bool {
        
        if emailPhone.isEmpty {
            errorMessage = "Please enter a email/phone number."
            isPresentAlert = true
            return false
        }
        
        let containsEmailSymbols = emailPhone.contains("@") || emailPhone.contains(".")
        let containsLetters = emailPhone.rangeOfCharacter(from: CharacterSet.letters) != nil
        let containsNumbers = emailPhone.rangeOfCharacter(from: CharacterSet.decimalDigits) != nil
        
        if containsEmailSymbols {
            // Likely an email
            if !isValidEmail(emailPhone) {
                errorMessage = "Please enter a valid email address."
                isPresentAlert = true
                return false
            }
            
        } else if !isValidPhone(emailPhone) {
            // Likely a phone number, but still invalid
            if containsLetters {
                errorMessage = "Please enter a valid email address."
            } else if containsNumbers {
                errorMessage = "Please enter a valid phone number."
            } else {
                errorMessage = "Please enter a valid email address or phone number."
            }
            isPresentAlert = true
            return false
        }
        
        if password.isEmpty {
            errorMessage = "Please enter password."
            isPresentAlert = true
            return false
        }
        
        // 2. Regex pattern check
        // let passwordRegEx = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"
        let passwordRegEx = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$"
        let predicate = NSPredicate(format: "SELF MATCHES %@", passwordRegEx)
        if !predicate.evaluate(with: password) {
            errorMessage = """
            Password must be at least 8 characters long, \
            and include at least one uppercase letter, one lowercase letter, one special character and one digit.
            """
            isPresentAlert = true
            return false
        }
        return true
    }
    
    func loginAPI(
        password: String,
        emailPhone: String,
        completion: @escaping (Bool) -> Void
    ) {
        guard validateInputs() else { return }
        showActivity = true
        APIManager.shared.loginApi(password: password, emailPhone: emailPhone)
            .receive(on: DispatchQueue.main)
            .sink { completion in
                
                self.showActivity = false
                
                if case .failure(let error) = completion {
                    self.errorMessage = error.localizedDescription
                    self.isPresentAlert = true
                }
                
            } receiveValue: { response in
                
                self.showActivity = false

                if response.data != nil {
                    completion(true)
                    print(response,"response")
                    
                    let token = response.data?.token
                    print(token ?? "", "Saved Token")

                    UserDefaults.standard.set(token, forKey: "token")
                    
                    let profileimg = response.data?.user?.profileImage ?? ""
                    
                    UserDetail.shared.setProfileImg(profileimg.imgFullPath())
                    
                    DispatchQueue.main.async {
                        self.showActivity = false   // STOP loader
                    }
                   
                } else {
                    self.errorMessage = response.message ?? "Login failed"
                    self.isPresentAlert = true
                }
            }
            .store(in: &cancellables)
    }
}

