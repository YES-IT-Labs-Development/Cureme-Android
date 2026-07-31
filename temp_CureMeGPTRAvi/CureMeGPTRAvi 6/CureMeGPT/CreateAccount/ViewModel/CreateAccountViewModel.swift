//
//  CreateAccountViewModel.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 18/11/25.
//

import SwiftUI
import Combine

class CreateAccountViewModel: ObservableObject {
    //  @Published var avatars: [CreateAccountModel] = []
    //  @Published var selectedAvatar: CreateAccountModel? = nil
    @Published var fullName = ""
    @Published var emailOrPhone = ""
    @Published var otp = 0
    @Published var newpassword = ""
    @Published var confirmPassword = ""
    @Published var isChecked = false
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var registrationSuccess = false
    @Published var createAccountResponse: CreateUser?
    // @Published var loginRespnse: RegisterModel?
    
    // @Published var createAccountResponse: CreateAccountModel?
    
    @Published var isPresentAlert = false
    @Published var showActivity = false
    @Published var alertMsg: String = ""
    private let userDefaultManager: UserDefaultManagerProtocol
    
    private var cancellables = Set<AnyCancellable>()
    
    
    init(userDefaultsManager: UserDefaultManagerProtocol = USerDefaultManager()) {
        
        self.userDefaultManager = userDefaultsManager
    }
    
    //    init() {
    //        loadAvatars()
    //    }
    
    //    func loadAvatars() {
    //        avatars = [
    //            CreateAccountModel(imageName: "Emojy2"),
    //            CreateAccountModel(imageName: "Emojy3"),
    //            CreateAccountModel(imageName: "Emojy4"),
    //            CreateAccountModel(imageName: "Emojy2"),
    //            CreateAccountModel(imageName: "Emojy3"),
    //            CreateAccountModel(imageName: "Emojy4"),
    //            CreateAccountModel(imageName: "Emojy2")
    //        ]
    //        selectedAvatar = avatars.first
    //    }
    
    //   func selectAvatar(_ avatar: CreateAccountModel) {
    //      selectedAvatar = avatar
    //  }
    
    func isValidEmail(_ email: String) -> Bool {
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        return NSPredicate(format: "SELF MATCHES %@", emailRegEx).evaluate(with: email)
    }
    
    func isValidPhone(_ phone: String) -> Bool {
        let phoneRegEx = "^[0-9]{10,15}$" // Accepts 10 to 15 digits
        return NSPredicate(format: "SELF MATCHES %@", phoneRegEx).evaluate(with: phone)
    }
    
    func validateInputs() -> Bool {
        if fullName.isEmpty {
            errorMessage = "Please enter your full name."
            isPresentAlert = true
            return false
        }
        
        if emailOrPhone.isEmpty {
            errorMessage = "Please enter a email/phone number."
            isPresentAlert = true
            return false
        }
        
        let containsEmailSymbols = emailOrPhone.contains("@") || emailOrPhone.contains(".")
        let containsLetters = emailOrPhone.rangeOfCharacter(from: CharacterSet.letters) != nil
        let containsNumbers = emailOrPhone.rangeOfCharacter(from: CharacterSet.decimalDigits) != nil
        
        if containsEmailSymbols {
            // Likely an email
        if !isValidEmail(emailOrPhone) {
            errorMessage = "Please enter a valid email address."
            isPresentAlert = true
            return false
        }
            
        } else if !isValidPhone(emailOrPhone) {
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
        
        if newpassword.isEmpty {
            errorMessage = "Please enter password."
            isPresentAlert = true
            return false
        }
        
        // 2. Regex pattern check
        // let passwordRegEx = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"
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
    
    func createAccountAPI(
        fullname: String,
        password: String,
        emailPhone: String,
        completion: @escaping (Bool) -> Void
    ) {
        guard self.validateInputs() else { return }
        DispatchQueue.main.async {
            self.showActivity = true   // START loader
        }
        APIManager.shared.createAccountAPI(
            fullname: fullname,
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
            
            DispatchQueue.main.async {
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
        }
        .store(in: &cancellables)
    }
 }

struct CustomTextField: View {
    var icon: String
    var placeholder: String
    @Binding var text: String

    var body: some View {
        HStack(spacing: 16) {
            Image(icon)
                .resizable()
                .frame(width: 50, height: 50)

            TextField(placeholder, text: $text)
                .font(.custom("Urbanist-Regular", size: 16))
                .autocapitalization(.none)
                .padding(.horizontal, 16)
                .frame(height: 50)
                .background(
                    RoundedRectangle(cornerRadius: 25)
                        .stroke(Color(hex: "#D9D9D9"), lineWidth: 1)
                )
        }
    }
}
struct CustomAuthSecureField: View {
    var icon: String
    var placeholder: String
    @Binding var text: String
    
    var body: some View {
        HStack {
            Image(systemName: icon)
                .foregroundColor(.brown)
                .frame(width: 30)
            
            SecureField(placeholder, text: $text)
                .padding(.vertical, 12)
        }
        .padding(.horizontal)
        .background(.white)
        .cornerRadius(30)
        .shadow(color: .gray.opacity(0.2), radius: 5)
    }
}
