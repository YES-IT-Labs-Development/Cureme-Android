//
//  LogoutConfirmPopUpView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 02/02/26.
//

import SwiftUI

struct LogoutConfirmPopUpView: View {
    var title: String
    var message: String
    var onClose: () -> Void
    var onLogout: () -> Void   // NEW
    
    @EnvironmentObject private var coordinator: Coordinator
    
    var body: some View {
        VStack(spacing: 20) {
            HStack(alignment: .top) {
                Image("LogoutIcon")
                    .resizable()
                    .frame(width: 55, height: 55)

                Text(title)
                    .font(.custom("Urbanist-Medium", size: 18))
                    .foregroundColor(Color(hex: "#050505"))
                    .padding(.top, 14)

                Spacer()

                Button(action: onClose) {
                    Image("CrossButton")
                        .resizable()
                        .frame(width: 45, height: 45)
                }
            }

            HStack {
//                Text(message)
                Text("Are you sure you want to log out of your\naccount?")
                    .font(.custom("Urbanist-Regular", size: 16))
                    .foregroundColor(.black)
                    .multilineTextAlignment(.leading)
                    .frame(maxWidth: .infinity, alignment: .leading)
                
                .padding(.horizontal, 10)
                   // .font(.custom("Urbanist-Regular", size: 16))
                //Spacer()
            }
           
            HStack {
                Button(action: {
                    onClose()
                }) {
                    Text("Cancel")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(Color(hex: "#181B1A"))
                        .frame(width: 138, height: 50)
                        .overlay(RoundedRectangle(cornerRadius: 40).stroke(Color.black, lineWidth: 1)
                        )
                }

                Button(action: {
                    // ✅ Clear all saved details from UserDefaults
                    UserDetail.shared.clearAllSavedDetails()
                    
                    // ✅ Post logout notification to handle navigation centrally
                    NotificationCenter.default.post(name: Notification.Name("logout"), object: nil)
                    
                    // Call additional logout logic if needed
                    onLogout()
                }){
                    Text("Yes, Logout")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(
                            LinearGradient(
                                colors: [
                                    Color(red: 253/255, green: 58/255, blue: 58/255).opacity(16),
                                    Color(red: 203/255, green: 8/255, blue: 8/255)
                                ],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .cornerRadius(40)
                }
            }
        }
        .padding(20)
        .background(
            RoundedRectangle(cornerRadius: 25)
                .fill(.white)
                .shadow(color: .black.opacity(0.15), radius: 8)
        )
        .padding(.horizontal, 30)
    }
}

#Preview{
    LogoutConfirmPopUpView(title: "Confirm Logout",
                        message: "Are you sure you want to log out of your account?",
                                  onClose: {
        "ok"
    },
                                  onLogout: {
        "ok"
    }
    )
}
