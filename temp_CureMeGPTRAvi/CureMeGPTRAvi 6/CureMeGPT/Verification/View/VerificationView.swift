//
//  VerificationView.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 17/11/25.
//

import SwiftUI

enum VerificationSource {
    case createAccount
    case resetPassword
}

struct VerificationView: View {
    let source: VerificationSource   // <-- NEW
    let emailPhone: String
    @EnvironmentObject private var coordinator: Coordinator
    @StateObject var vm = VerifyPhoneViewModel()
    @State private var resendTimer: Int = 30
    @State private var isTimerRunning: Bool = true
    @State private var showPopup: Bool = false
    @State private var popupAction: (() -> Void)? = nil
    
    init(source: VerificationSource, emailPhone: String) {
        self.source = source
        self.emailPhone = emailPhone
        self.resendTimer = resendTimer
        self.isTimerRunning = isTimerRunning
        for family in UIFont.familyNames {
            print("== \(family) ==")
            for name in UIFont.fontNames(forFamilyName: family) {
                print("   → \(name)")
            }
        }
    }
    
    var body: some View {
        ZStack {
            VStack(spacing: 24) {
                VStack(spacing: 10) {
                    Text("Verify Your Account")
                        .font(.custom("Urbanist-Bold", size: 24))
                        .foregroundColor(.white)
                        .padding(.horizontal, 24)
                        .padding(.leading, -60)
                    
                    Text("We’ve sent a 5-digit code to your email.")
                        .font(.custom("Urbanist-Regular", size: 16))
                            .foregroundColor(.white)
                            .padding(.horizontal, 24)
                    }
                //.padding(.horizontal, 24)
                .padding(.top, 70)
                .frame(maxWidth: .infinity, alignment: .leading)
                .frame(height: 225)
                .background(
                    LinearGradient(
                        colors: [
                            Color(red: 67/255, green: 56/255, blue: 202/255),
                            Color(red: 33/255, green: 28/255, blue: 100/255)
                        ],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
                .clipShape(RoundedCornerShape(corners: [.bottomLeft, .bottomRight], radius: 25))
                
                VStack{
                    // OTP fields
                    HStack(spacing: 14) {
                        OTPFieldView(numberOfFields: 5, otp: $vm.otp)
                    }
                    
                    // Sent Code Button
                            Button(action: {
                                vm.verifyAccountAPI(
                                    source: source,
                                    otp: vm.otp,
                                    emailPhone: emailPhone,
                                    fcmToken: vm.fcmToken ?? ""
                                ) { success in
                                    
                                    if success {
                                        if source == .resetPassword {
                                            coordinator.push(.setNewPasswordView(emailPhone: emailPhone))
                                        } else {
                                            withAnimation {
                                                showPopup = true
                                            }
                                        }
                                    }
                                }
                            }) {
                        Text("Verify & Continue")
                            .font(.custom("Urbanist-SemiBold", size: 16))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 55)
                            .background(
                                  Image("BackgroundBtn") // Asset name
                                      .resizable()
                                      .scaledToFill()
                              )
                                
//                            .background(
//                                LinearGradient(
//                                    colors: [
//                                        Color(red: 67/255, green: 56/255, blue: 202/255),
//                                        Color(red: 33/255, green: 28/255, blue: 100/255)
//                                    ],
//                                    startPoint: .leading,
//                                    endPoint: .trailing
//                                )
//                            )
                            .cornerRadius(40)
                    }
                    .padding(.top, 22)
                    
                    // RESEND SECTION
                    VStack(alignment: .leading, spacing: 10) {
                        HStack(spacing: 6) {
                            Text("Didn't receive code?")
                                .font(.custom("Urbanist-Medium", size: 18))
                                .foregroundColor(Color.black)
                            
                            // WHEN TIMER IS RUNNING
                            if vm.showTimer {
                                let seconds = vm.timerValue % 60
                                HStack(spacing: 2) {
                                    Text("Resend OTP in")
                                        .font(.custom("Urbanist-Medium", size: 18))
                                        .foregroundColor(.black)
                                    
                                    Text("\(String(format: " %02d", seconds))s")
                                        .font(.custom("Urbanist-Medium", size: 18))
                                        .foregroundColor(Color(hex: "#1E3A8A"))   // <-- Only seconds colored
                                }
                                .transition(.opacity)
                            }
                            
                            // WHEN TIMER IS FINISHED → SHOW RESEND BUTTON
                            if vm.resendEnabled {
                                Button("RESEND") {
                                    vm.otp = ""
                                    vm.OtpError = nil
                                    vm.startTimer()
                                    print(emailPhone)
                                    vm.resendOtpAPI(emailPhone: emailPhone,
                                                    completion: { success in
                                        
                                        if success {
                                            withAnimation {
                                                showPopup = true
                                            }
                                        }
                                    }
                                    )
                                }
                                .font(.custom("PlusJakartaSans-Regular", size: 18))
                                .foregroundColor(Color(hex: "#2E1302"))
                                .transition(.opacity)
                            }
                        }
                        .padding(.top, 20)
                    }
                    Spacer()
                }
                .padding(.horizontal, 25)
                .padding(.top, 50)
                
                Spacer()
                HStack {
//                    Text("Back to")
//                        .font(.custom("Urbanist-Medium", size: 18))
//                    
                    Button("Back") {
                        coordinator.pop()
                    }
                        .font(.custom("Urbanist-Medium", size: 20))
                        .foregroundColor(Color.black)
                        //.foregroundColor(Color(red: 67/255, green: 56/255, blue: 202/255))
                }
                .padding(.bottom, 70)
            }
            .ignoresSafeArea(.all)
        
            //.padding(.horizontal, 30)
            .blur(radius: showPopup ? 3 : 0)   // blur background when popup opens
            
            if vm.showActivity {
                CustomLoderView(isVisible: $vm.showActivity)
                    .ignoresSafeArea()
            }
            // -------------------
            if showPopup {
                // DARK BACKGROUND
                Color.black.opacity(0.4)
                    .edgesIgnoringSafeArea(.all)
                    .transition(.opacity)
                    .onTapGesture {
                        withAnimation {
                            showPopup = false
                            popupAction?()
                        }
                    }
                
             // USING YOUR EXISTING POPUP VIEW EXACTLY AS IT IS
                AccCreatedSuccessfulyPopUpView(
                        title: "Account Created Successfully!",
                        message: "Your account is ready. Start exploring now!.",
                        onClose: {
                            withAnimation {
                                showPopup = false
                            }
                        },
                        onSetUpProfile: {
                            withAnimation {
                                showPopup = false
                                coordinator.push(.privacyConsentView(flow: .onboarding))
                            }
                        },
                        onGoToAskAI: {
                            withAnimation {
                                showPopup = false
                                coordinator.push(.privacyConsentView(flow: .askAI))
                            }
                        }
                    )
                    .transition(.scale)
            }
        }
        .animation(.easeOut, value: showPopup)
        .onAppear {
            vm.startTimer()   // Start countdown automatically
        }
        .customAlert(
                  isPresented: $vm.isPresentAlert,
                  message: vm.errorMessage ?? "Error"
              ) {
                  print("OK tapped")
              }
//        .alert(isPresented: $vm.isPresentAlert) {
//            Alert(title: Text(vm.errorMessage ?? ""))
//        }
    }
}

#Preview {
    VerificationView(source: .createAccount, emailPhone: "test@gmail.com")
}
