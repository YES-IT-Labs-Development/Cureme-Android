//
//  SuccessPopUpView.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 18/11/25.
//

import SwiftUI

struct SuccessPopupView: View {
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
                        .foregroundColor(Color.init(hex: "#050505"))
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
                        .foregroundColor(Color(hex: "#050505"))
                    Spacer()
                }
            }
            
            // OK BUTTON
            Button(action: onClose) {
                Text("OK")
                    .font(.custom("Urbanist-SemiBold", size: 16))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 55)
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
    SuccessPopupView(title: "Password updated Successfully!",
                     message: "Your password has been updated.",
                     onClose: {
        "ok" 
    })
}
