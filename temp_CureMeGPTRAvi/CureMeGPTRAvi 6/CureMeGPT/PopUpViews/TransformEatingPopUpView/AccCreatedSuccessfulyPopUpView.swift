//
//  TransformEatingPopUpView.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 18/11/25.
//

import SwiftUI

struct AccCreatedSuccessfulyPopUpView: View {
    var title: String
    var message: String
    var onClose: () -> Void
    var onSetUpProfile: () -> Void
    var onGoToAskAI: () -> Void
    
    var body: some View {
        VStack(spacing: 20) {
            HStack(alignment: .top) {
                Image("RightCheckMark")
                    .resizable()
                    .frame(width: 55, height: 55)
                    .foregroundColor(Color.brown)
                
                VStack(alignment: .leading, spacing: 4) {
                    Text(title)
                        .font(.custom("Urbanist-Medium", size: 18))
                        .foregroundColor(Color(hex: "#050505"))
                        .padding(.top, 6)
                }
                
                Spacer()
                                
                // Close Button
                Button(action: onClose) {
                    Image("CrossButton")
                        .resizable()
                        .frame(width: 45, height: 45)
                        .foregroundColor(.brown)
                }
            }
    
            VStack{
                HStack{
                    Text(message)
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(Color.init(hex: "#050505"))
                    Spacer()
                }
            }
            
            // OK BUTTON
            HStack{
                Button(action: onSetUpProfile) {
                    Text("Set Up Profile")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(Color(hex: "#181B1A"))
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .overlay(RoundedRectangle(cornerRadius: 40).stroke(Color.black, lineWidth: 1))
                        
                }
                Button(action: onGoToAskAI) {
                    Text("Go to Ask AI")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
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
                .shadow(color: .black.opacity(0.15), radius: 8, x: 0, y: 3)
        )
        .padding(.horizontal, 15)
     }
  }

  #Preview{
      AccCreatedSuccessfulyPopUpView(title: "Account Created Successfully!",
                     message: "Your account is ready. Start exploring now!",
                     onClose: {},
                     onSetUpProfile: {},
                     onGoToAskAI: {})
  }
