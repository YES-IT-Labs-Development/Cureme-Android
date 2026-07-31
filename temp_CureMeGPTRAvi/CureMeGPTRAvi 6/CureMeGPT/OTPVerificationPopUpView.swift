//
//  OTPVerificationPopUpView.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 11/03/26.
//
import SwiftUI

struct OTPVerificationView: View {
    @ObservedObject var vm: ProfileViewModel
    let type: OTPType
    let serverOTP: String     // ✅ server OTP
    var completion: (Bool) -> Void
    
    var onClose: () -> Void
    
    
    @State private var isResending = false
    
    @State private var otp = ["", "", "", ""]
    @FocusState private var focusedIndex: Int?
    
    @State private var showError = false
    @State private var errorMsg = ""
    
    var body: some View {
        
        ZStack(alignment: .center) {
            
            // Full screen background
            Color.black.opacity(0.4)
                .ignoresSafeArea()
            
            // Popup Card
            VStack(spacing: 20) {
                
                // TOP RIGHT CLOSE BUTTON
                HStack {
                    Spacer()
                    
                    Button {
                        onClose()
                    } label: {
                        Image(systemName: "xmark")
                            .font(.system(size: 18, weight: .semibold))
                            .foregroundColor(.black)
                            .frame(width: 32, height: 32)
                    }
                }
                
                Text(type == .phone ? "Verify Phone" : "Verify Email")
                    .font(.custom("Urbanist-Medium", size: 18))
                
                Text(type == .phone ?
                     "Enter the OTP sent to your phone" :
                        "Enter the OTP sent to your email")
                .foregroundColor(.gray)
                .font(.custom("Urbanist-Regular", size: 15))
                
                HStack(spacing: 15) {
                    
                    ForEach(0..<4, id: \.self) { index in
                        
                        TextField("", text: $otp[index])
                            .frame(width: 50, height: 50)
                            .multilineTextAlignment(.center)
                            .keyboardType(.numberPad)
                            .background(Color.gray.opacity(0.1))
                            .cornerRadius(10)
                            .focused($focusedIndex, equals: index)
                            .onChange(of: otp[index]) { newValue in
                                showError = false
                                if newValue.count > 1 {
                                    otp[index] = String(newValue.prefix(1))
                                }
                                
                                if newValue.count == 1 && index < 3 {
                                    focusedIndex = index + 1
                                }
                                
                                if newValue.isEmpty && index > 0 {
                                    focusedIndex = index - 1
                                }
                            }
                    }
                }
                
                Button {
                    
                    let otpCode = otp.joined()
                    
                    if otpCode.isEmpty || otpCode.count < 4 {
                        
                        errorMsg = "Please enter OTP"
                        showError = true
                        return
                    }
                    
                    if otpCode == serverOTP {
                        
                        showError = false
                        completion(true)
                        
                    } else {
                        
                        errorMsg = "Invalid OTP"
                        showError = true
                    }
                    
                } label: {
                    
                    Text("Verify OTP")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(
                            Image("BackgroundBtn") // Asset name
                                .resizable()
                                .scaledToFill()
                        )
                   
                        .cornerRadius(25)
                }
                VStack(spacing: 8) {
                    
                    Button {
                        
                        print("Phone:", vm.profile.phone)
                        print("Email:", vm.profile.email)
                        
                        // Close keyboard
                        focusedIndex = nil
                        
                        isResending = true
                        
                        vm.emailPhone = type == .phone
                        ? vm.profile.phone
                        : vm.profile.email
                        
                        vm.apiforVerifyPhoneEmail { success in
                            
                            DispatchQueue.main.async {
                                
                                isResending = false
                                
                                if success {
                                    
                                    otp = ["", "", "", ""]
                                    showError = false
                                    
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                                        vm.showToast = false
                                    }
                                    
                                    focusedIndex = 0
                                }
                            }
                        }
                        
                    } label: {
                        HStack(spacing: 4) {
                            Text("Didn't receive verification code?")
                                .font(.custom("Urbanist-Regular", size: 15))
                                .foregroundColor(.gray)
                            
                            Text("Resend")
                                .foregroundColor(.blue)
                                .font(.custom("Urbanist-Medium", size: 15))
                        }
                        .font(.footnote)
                    }
                    
                }
                
                if showError {
                    Text(errorMsg)
                        .foregroundColor(.red)
                        .font(.footnote)
                }
            }
            .padding(.horizontal, 15)
            .padding(.bottom,25)
            .padding(.trailing, 5)
            .padding(.top, 25)
            .frame(width: 320)
            .background(Color.white)
            .cornerRadius(20)
            .shadow(radius: 20)
            if vm.showActivity {
                CustomLoderView(isVisible: $vm.showActivity)
                    .ignoresSafeArea()
            }
        }
        
        .onAppear {
            focusedIndex = 0
        }
        .overlay(alignment: .bottom) {
            
            if vm.showToast {
                ToastView(message: vm.toastMessage)
                    .padding(.top, 60)
                    .transition(.move(edge: .top).combined(with: .opacity))
                    .zIndex(9999)
            }
            
        }
        .animation(.easeInOut, value: vm.showToast)
        
        .customAlert(
            isPresented: $vm.isPresentAlert,
            message: vm.errorMessage ?? ""
        ) {
            print("OK tapped")
        }
    }
}
#Preview {
    OTPVerificationView(
        vm: ProfileViewModel(),
        type: .phone,
        serverOTP: "1234",
        completion: { _ in },
        onClose: { }
    )
}

enum OTPType {
    case phone
    case email
}
