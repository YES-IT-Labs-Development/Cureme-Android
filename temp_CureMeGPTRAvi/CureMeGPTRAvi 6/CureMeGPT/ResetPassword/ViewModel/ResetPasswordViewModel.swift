//
//  ResetPasswordViewModel.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 17/11/25.
//

import SwiftUI
import Combine

class ResetPasswordViewModel: ObservableObject {
    @Published var emailPhone = ""
    @Published var isChecked = false
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var registrationSuccess = false
    
    @Published var isPresentAlert = false
    @Published var showActivity = false
    @Published var alertMsg: String = ""
    private let userDefaultManager: UserDefaultManagerProtocol
    
    private var cancellables = Set<AnyCancellable>()
    
    
    init(userDefaultsManager: UserDefaultManagerProtocol = USerDefaultManager()) {
        
        self.userDefaultManager = userDefaultsManager
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
        errorMessage = nil
        isPresentAlert = false
        return true
    }
    
    func forgotPasswordAPI(
        emailPhone: String,
        completion: @escaping (Bool) -> Void
    ) {
        
        guard self.validateInputs() else { return }
        DispatchQueue.main.async {
            self.showActivity = true   // START loader
        }
        
        APIManager.shared.ForgotPasswordAPI(
            emailPhone: emailPhone,
        )
        .receive(on: DispatchQueue.main)
        .sink { result in
            
            self.showActivity = false
            
            if case .failure(let error) = result {
                self.errorMessage = error.localizedDescription
                self.isPresentAlert = true
                completion(false)
            }
            
        } receiveValue: { response in
            
            self.showActivity = false
            
            // Correct condition
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
            }
         }
        .store(in: &cancellables)
     }
  }
    
//    struct CustomTextField: View {
//        var icon: String
//        var placeholder: String
//        @Binding var text: String
//        
//        var body: some View {
//            HStack {
//                Image(icon)
//                    .resizable()
//                    .frame(width: 50, height: 50)
//                
//                TextField(placeholder, text: $text)
//                    .autocapitalization(.none)
//                    .padding()
//                    .background(
//                        RoundedRectangle(cornerRadius: 40)
//                            .stroke(Color(hex: "#D9D9D9"), lineWidth: 1)
//                    )
//            }
//           // .padding(.horizontal, 30)
//        }
//    }
//
//struct CustomAuthSecureField: View {
//    var icon: String
//    var placeholder: String
//    @Binding var text: String
//    
//    var body: some View {
//        HStack {
//            Image(systemName: icon)
//                .foregroundColor(.brown)
//                .frame(width: 30)
//            
//            SecureField(placeholder, text: $text)
//                .padding(.vertical, 12)
//        }
//        .padding(.horizontal)
//        .background(.white)
//        .cornerRadius(30)
//        .shadow(color: .gray.opacity(0.2), radius: 5)
//    }
//}
