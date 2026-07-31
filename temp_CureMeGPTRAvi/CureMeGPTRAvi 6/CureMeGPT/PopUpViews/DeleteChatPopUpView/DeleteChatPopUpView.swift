//
//  DeleteChatPopUpView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 02/02/26.
//

import SwiftUI

struct DeleteChatPopUpView: View {
    var title: String
    var message: String
    var warningTxt: String
    var onClose: () -> Void
    var onDelete: () -> Void   // NEW
    @EnvironmentObject private var coordinator: Coordinator
    
    var body: some View {
        VStack(spacing: 20) {
            HStack(alignment: .top) {
                Image("Delete1")
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
            
            HStack(alignment: .top) {
                Image("solar_info")
                    .resizable()
                    .frame(width: 15, height: 15)
                Text(warningTxt)
                    .font(.custom("Urbanist-Regular", size: 16))
                    .foregroundColor(Color(hex: "#F31D1D"))
                //Spacer()
            }
              
            HStack {

                Button(action: onClose) {
                    Text("Cancel")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(Color(hex: "#181B1A"))
                        .frame(maxWidth: .infinity)
                        .frame(width: 126, height: 50)
                        .overlay(
                            RoundedRectangle(cornerRadius: 40)
                                .stroke(Color.black, lineWidth: 1)
                        )
                }

                Button(action: {
                    print("👉 YES DELETE TAPPED")
                    onDelete()
                }) {
                    Text("Yes, Delete Chat")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(
                            LinearGradient(
                                colors: [
                                    Color(red: 253/255, green: 58/255, blue: 58/255),
                                    Color(red: 203/255, green: 8/255, blue: 8/255)
                                ],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .cornerRadius(40)
                }
            }
//            HStack {
//               
//                Button(action: {
//                    onDelete()
//                }) {
//                    Text("Cancel")
//                        .font(.custom("Urbanist-Medium", size: 16))
//                        .foregroundColor(Color(hex: "#181B1A"))
//                        .frame(maxWidth: .infinity)
//                        .frame(width: 126, height: 50)
//                        .overlay(RoundedRectangle(cornerRadius: 40).stroke(Color.black, lineWidth: 1)
//                        )
//                }
//
//                // GO TO ASK AI
//                Button(action: onClose) {
//                    Text("Yes, Delete Chat")
//                        .font(.custom("Urbanist-Medium", size: 16))
//                        .foregroundColor(.white)
//                        .frame(maxWidth: .infinity)
//                        .frame(height: 50)
//                        .background(
//                            LinearGradient(
//                                colors: [
//                                    Color(red: 253/255, green: 58/255, blue: 58/255).opacity(16),
//                                    Color(red: 203/255, green: 8/255, blue: 8/255)
//                                ],
//                                startPoint: .leading,
//                                endPoint: .trailing
//                            )
//                        )
//                        .cornerRadius(40)
//
//                }
//            }
            
            HStack{
                Text("You’re always in control — deleted chats cannot be restored.")
                    .font(.custom("Urbanist-Regular", size: 12))
                    .foregroundColor(Color(hex: "#374151"))
                Spacer()
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
    DeleteChatPopUpView(title: "Delete Chat",
                        message: "Once deleted, this chat and its medical history cannot be recovered.", warningTxt: "Deleting may affect AI’s ability to suggest based on your past health history.",
                                  onClose: {
        "ok"
    },
                                  onDelete: {
        "ok"
    }
    )
}
