//
//  ProfileSetUpPopUpView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/12/25.
//

import SwiftUI

struct ProfileSetUpPopUpView: View {
    var title: String
    var message: String
    var onClose: () -> Void
    
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
                        .font(.custom("Urbanist-Regular", size: 16))
                        .foregroundColor(Color.init(hex: "#050505"))
                    Spacer()
                }
            }
            
            // OK BUTTON
            HStack{
                Button(action: onClose) {
                    Text("Add Member")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(Color(hex: "#181B1A"))
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .overlay(RoundedRectangle(cornerRadius: 40).stroke(Color.black, lineWidth: 1))
                        
                }
                Button(action: onClose) {
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
        .padding(.horizontal, 30)
     }
  }

  #Preview{
      ProfileSetUpPopUpView(title: "Account Created Successfully!",
                     message: "You can now add your family members or start asking AI for help.",
                     onClose: {
         "ok"
     })
  }
