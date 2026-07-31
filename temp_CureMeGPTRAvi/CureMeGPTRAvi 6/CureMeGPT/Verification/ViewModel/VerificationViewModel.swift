//
//  VerificationViewModel.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 17/11/25.
//

import Foundation
import Combine

class VerifyPhoneViewModel: ObservableObject {
    
    @Published var otp: String = ""
    @Published var OtpError: String?
    @Published var fcmToken: String?
    @Published var timerValue: Int = 30     // Countdown value (seconds)
    @Published var showTimer: Bool = false  // Show "Resend OTP in"
    @Published var resendEnabled: Bool = true
    @Published var emailPhone: String = ""
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var registrationSuccess = false
    
    private var timer: Timer?
    @Published var isPresentAlert = false
    @Published var showActivity = false
    @Published var alertMsg: String = ""
    
    private let userDefaultManager: UserDefaultManagerProtocol
    private var cancellables = Set<AnyCancellable>()
    init(userDefaultsManager: UserDefaultManagerProtocol = USerDefaultManager()) {
        
        self.userDefaultManager = userDefaultsManager
    }
    
    func startTimer() {
        timer?.invalidate()
        
        timerValue = 30
        showTimer = true
        resendEnabled = false
        
        timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { t in
            if self.timerValue > 0 {
                self.timerValue -= 1
            } else {
                t.invalidate()
                self.showTimer = false
                self.resendEnabled = true
            }
        }
    }
    
    func validateInputs() -> Bool {
        if otp.count == 0{
            errorMessage = "Please enter OTP."
            isPresentAlert = true
            return false
        }else if otp.count < 5{
            errorMessage = "Please enter complete otp."
            isPresentAlert = true
            return false
        }
        return true
    }
    
    func verifyAccountAPI(
        source: VerificationSource,
        otp: String,
        emailPhone: String,
        fcmToken: String,
        completion: @escaping (Bool) -> Void
    ) {
        guard validateInputs() else { return }

        showActivity = true
        let publisher: AnyPublisher<BaseResponse<VerifyAccountResponse>, Error>

        // Decide which API to call
        if source == .createAccount {
            publisher = APIManager.shared.verifyAccountAPI(
                otp: otp,
                emailPhone: emailPhone,
                fcmToken: fcmToken
            )
        } else {
            publisher = APIManager.shared.verifyForgotOtpAPI(
                otp: otp,
                emailPhone: emailPhone,
                fcmToken: fcmToken
            )
        }

        publisher
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

                if let data = response.data {

                    if let token = data.token {
                        print("Token:", token)
                        UserDefaults.standard.set(token, forKey: "token")
                    }

                    if let user = response.data?.user {
                        if let encoded = try? JSONEncoder().encode(user) {
                            UserDefaults.standard.set(encoded, forKey: "user_data")
                        }
                    }

                    completion(true)

                } else {

                    self.errorMessage = response.message ?? "Something went wrong"
                    self.isPresentAlert = true
                    completion(false)
                }
            }
            .store(in: &cancellables)
    }
    
    func resendOtpAPI(
        emailPhone: String,
        completion: @escaping (Bool) -> Void
    ) {
        showActivity = true

        APIManager.shared.ResendOTPAPI(
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
            if response.status == true {
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
