//
//  SetNewPassword.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 17/11/25.
//

import SwiftUI

struct SetNewPasswordView: View {
    @EnvironmentObject private var coordinator: Coordinator
    @State var password: String = ""
    @State var confirmPassword: String = ""
    @State private var isPasswordVisible: Bool = false
    @State private var isconfirmPasswordVisible: Bool = false
    @StateObject private var vm = SetNewPasswordViewModel()
    @State private var showPopup: Bool = false
    @State private var popupAction: (() -> Void)? = nil
    @State var emailPhone: String = ""
    
    var body: some View {
        ZStack {
            // MAIN CONTENT — Will NOT move now
            VStack(spacing: 24) {
                VStack(alignment: .leading, spacing: 10) {
                    Text("New Password")
                        .font(.custom("Urbanist-Bold", size: 24))
                        .foregroundColor(.white)
                    
                    Text("Please enter your new password.")
                        .font(.custom("Urbanist-Regular", size: 16))
                        .foregroundColor(.white)
                }
                .padding(.horizontal, 24)
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
                
                
                // INPUT FIELDS + SUBMIT
                VStack {
                    // Password
                    HStack(spacing: 12) {
                        Image("LockImg")
                            .resizable()
                            .frame(width: 50, height: 50)

                        HStack(spacing: 8) {

                            Group {
                                if isPasswordVisible {
                                    TextField("Password", text: $vm.newpassword)
                                } else {
                                    SecureField("Password", text: $vm.newpassword)
                                }
                            }
                            .font(.custom("Urbanist-Regular", size: 15))
                            .textInputAutocapitalization(.never)
                            .autocorrectionDisabled()

                            Button {
                                withAnimation(.easeInOut(duration: 0.15)) {
                                    isPasswordVisible.toggle()
                                }
                            } label: {
                                Image(isPasswordVisible ? "OpenEye" : "ri_eye-off-line")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 22, height: 22)
                                    .padding(6)
                                    .contentShape(Rectangle())
                            }
                            .buttonStyle(.plain)
                        }
                        .padding(.horizontal, 18)
                        .frame(height: 55)
                        .background(
                            RoundedRectangle(cornerRadius: 28)
                                .stroke(Color(hex: "#D9D9D9"), lineWidth: 1)
                        )
                    }
                    .padding(.top, 50)
                    
                    // Confirm Password
                    HStack(spacing: 12) {
                        Image("LockImg")
                            .resizable()
                            .frame(width: 50, height: 50)

                        HStack(spacing: 8) {

                            Group {
                                if isconfirmPasswordVisible {
                                    TextField("Confirm Password", text: $vm.confirmPassword)
                                } else {
                                    SecureField("Confirm Password", text: $vm.confirmPassword)
                                }
                            }
                            .font(.custom("Urbanist-Regular", size: 15))
                            .textInputAutocapitalization(.never)
                            .autocorrectionDisabled()

                            Button {
                                withAnimation(.easeInOut(duration: 0.15)) {
                                    isconfirmPasswordVisible.toggle()
                                }
                            } label: {
                                Image(isconfirmPasswordVisible ? "OpenEye" : "ri_eye-off-line")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 22, height: 22)
                                    .padding(6)
                                    .contentShape(Rectangle())
                            }
                            .buttonStyle(.plain)
                        }
                        .padding(.horizontal, 18)
                        .frame(height: 55)
                        .background(
                            RoundedRectangle(cornerRadius: 28)
                                .stroke(Color(hex: "#D9D9D9"), lineWidth: 1)
                        )
                    }
                    .padding(.top, 20)
                   
                    
                    // SUBMIT BUTTON
                    Button {

                        vm.setNewPasswordAPI(
                            password: vm.newpassword,
                            emailPhone: emailPhone
                        ) { success in
                            
                            if success {
                                withAnimation {
                                    showPopup = true
                                }
                            }
                        }

                    } label: {
                        Text("Submit")
                            .font(.custom("Urbanist-SemiBold", size: 17))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 55)
                            .background( Image("BackgroundBtn") // Asset name
                                        .resizable()
                                         .scaledToFill()   )

                            .clipShape(Capsule())
                    }
                    .padding(.top, 20)
                    .padding(.horizontal, 0)
                }
                .padding(.horizontal, 25)
                
                Spacer()   // <-- VALID SPACER INSIDE MAIN VSTACK
            }
            .blur(radius: showPopup ? 3 : 0)
            .ignoresSafeArea()
            
            if vm.showActivity {
                CustomLoderView(isVisible: $vm.showActivity)
                    .ignoresSafeArea()
            }
            
            if showPopup {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture {
                        withAnimation {
                            showPopup = false
                            // Navigate AFTER popup close animation finishes
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
                                coordinator.push(.login)
                            }
                        }
                    }
                
                SuccessPopupView(
                    title: "Password updated Successfully!",
                    message: "Your password has been updated.",
                    
                    onClose: {
                        withAnimation(.easeOut(duration: 0.25)) {
                            showPopup = false
                        }
                        
                        // Navigate AFTER popup close animation finishes
                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
                            coordinator.push(.login)
                        }
                    }
                )
                .transition(.scale.combined(with: .opacity))
            }
        }
        .animation(.easeOut, value: showPopup)
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
    SetNewPasswordView()
}
