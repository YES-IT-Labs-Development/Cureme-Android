//
//  ResetPasswordView.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 17/11/25.
//

import SwiftUI

struct ResetPasswordView: View {
    @EnvironmentObject private var coordinator: Coordinator
    @State private var email: String = ""
    @StateObject private var vm = ResetPasswordViewModel()
    
    var body: some View {
        ZStack {
            VStack{
                // Title + Subtitle
                VStack(spacing: 10) {
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Reset Your Password")
                            .font(.custom("Urbanist-Bold", size: 24))
                            .foregroundColor(.white)
                        
                        Text("Enter your registered email or phone number to reset your password.")
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
                    
                    
                    // Email Field
                    HStack {
                        Image("EmailImg")
                            .resizable()
                            .frame(width: 50, height: 50)
                        
                        VStack(spacing: 4) {
                            TextField("Email / Phone Number", text: $vm.emailPhone)
                                .autocapitalization(.none)
                                .padding()
                                .background(
                                    RoundedRectangle(cornerRadius: 30)
                                        .stroke(Color(hex: "#D9D9D9"), lineWidth: 1)
                                )
                        }
                    }
                    .padding(.horizontal, 25)
                    .padding(.top, 60)
                    
                    // Send Code Button (moved closer)
                    Button(action: {
                            vm.forgotPasswordAPI(emailPhone: vm.emailPhone) { success in
                                
                                if success {
                                    coordinator.push(
                                        .verificationView(
                                            source: .resetPassword,
                                            emailPhone: vm.emailPhone
                                        )
                                    )
                                }
                            }
                    }) {
                        Text("Send Code")
                            .foregroundColor(.white)
                            .font(.custom("PlusJakartaSans", size: 16))
                            .fontWeight(.medium)
                            .frame(maxWidth: .infinity)
                            .frame(height: 55)
                            .background(
                                               Image("BackgroundBtn") // Asset name
                                                    .resizable()
                                                  .scaledToFill() )
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
                    .padding(.horizontal, 25)
                    .padding(.top, 20)
                    
                    Spacer()
                    HStack {
                       // Text("Back")
                            //.font(.custom("Urbanist-Medium", size: 18))
                        
                        Button(action: {
                            coordinator.pop()
                        }){
                            Text("Back")
                                 .font(.custom("Urbanist-Medium", size: 18))
                                 .foregroundColor(Color.black)
//                            Text("Login")
//                                .font(.custom("Urbanist-Medium", size: 18))
//                                .foregroundColor(Color(red: 67/255, green: 56/255, blue: 202/255))
                        }
                    }
                    .padding(.bottom, 70)
                }
                .ignoresSafeArea()
            }
            if vm.showActivity {
                CustomLoderView(isVisible: $vm.showActivity)
                    .ignoresSafeArea()
            }
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
        .ignoresSafeArea(edges: .all)
        Spacer()
    }
}

#Preview {
    ResetPasswordView()
}
