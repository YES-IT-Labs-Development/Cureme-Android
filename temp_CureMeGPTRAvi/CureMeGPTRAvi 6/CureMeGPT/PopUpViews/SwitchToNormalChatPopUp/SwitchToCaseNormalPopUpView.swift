//
//  SwitchToCaseNormalPopUpView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 02/02/26.
//

import SwiftUI

struct SwitchToCaseNormalPopUpView: View {
    var title: String
    var message: String
    var onClose: () -> Void
    var onAddMember: () -> Void   // NEW
    @EnvironmentObject private var coordinator: Coordinator
    
    var body: some View {
        VStack(spacing: 20) {

            HStack(alignment: .top) {
                Image("Frame 2147223261")
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
                Text(message)
                    .font(.custom("Urbanist-Regular", size: 16))
                Spacer()
            }

            HStack {
               
                Button(action: {
                    onAddMember()
                }) {
                    Text("Stay on Normal Chat")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(Color(hex: "#181B1A"))
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .overlay(RoundedRectangle(cornerRadius: 40).stroke(Color.black, lineWidth: 1)
                        )
                }

                // GO TO ASK AI
                Button(action: onClose) {
                    Text("Yes, Switch")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(width: 130, height: 50)
                        .background(
                            LinearGradient(
                                colors: [
                                    Color(red: 67/255, green: 56/255, blue: 202/255),
                                    Color(red: 33/255, green: 28/255, blue: 100/255)
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
    SwitchToCaseNormalPopUpView(title: "Switch to Case Chat?",
                                  message: "This chat will be converted into a case chat. Your conversation will be tracked with case history and medical records. Do you want to continue?",
                                  onClose: {
        "ok"
    },
                                  onAddMember: {
        "ok"
    }
    )
}
