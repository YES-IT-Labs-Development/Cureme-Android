//
//  DeleteAccReasonView.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 25/11/25.
//

import SwiftUI

struct DeleteAccReasonView: View {
    @StateObject private var vm = DeleteAccReasonVM()
    @EnvironmentObject private var coordinator: Coordinator
    
    var body: some View {
        VStack(spacing: 20) {
            // Header
            HStack {
                Button(action: {
                    coordinator.pop()
                }) {
                    Image("backIcon")
                        .resizable()
                        .frame(width: 45, height: 45)
                        .padding(.leading, 10)
                }
                
                Text("Delete Account")
                    .font(.custom("Urbanist-Medium", size: 20))
                    .foregroundColor(Color.black)
                    .padding(.leading, 10)
                Spacer()
                
            }
            .padding()
            
            // Content Box With Dashed Border
            VStack(alignment: .leading, spacing: 16) {
                Text("Delete Account")
                    .font(.custom("Urbanist-Medium", size: 18))
                    .foregroundColor(Color.black)
                    .padding(.horizontal)
                
                Text("Why would you like to delete your account")
                    .font(.custom("Urbanist-Regular", size: 15))
                    .foregroundColor(Color(hex: "#697383"))
                    .padding(.horizontal)
                
                ScrollView{
                    VStack(spacing: 12) {
                        ForEach(vm.reasons) { reason in
                            Button {
                               // vm.didSelectReason(reason)
                                coordinator.push(.deleteAccFeedBackView)
                                
                            } label: {
                                DeleteReasonRow(title: reason.title)
                                     .font(.custom("Urbanist-Medium", size: 18))
                                .foregroundColor(Color.black)
                                    .multilineTextAlignment(.leading)
                            }
                            Divider().padding(.horizontal, 14)
                        }
                    }
                    .padding(.top)
                    .background(
                        RoundedRectangle(cornerRadius: 20)
                            .fill(Color(hex: "#4338CA").opacity(0.04))
                    )
                    .cornerRadius(30)
                    
                }
                .disableScrollBounce()
                .cornerRadius(26)
                .padding(.bottom)
                Spacer()
               
            }
            .padding(.horizontal)
            Spacer()
        }
        .keyboardDoneButton()
    }
}

struct DeleteReasonRow: View {
    let title: String
    
    var body: some View {
        HStack {
            Text(title)
                .foregroundColor(.black)
            
            Spacer()
            
            Image("RightArrow")
                .font(.system(size: 14))
                .foregroundColor(.gray)
        }
        .padding()
        .cornerRadius(14)
    }
}

#Preview {
    DeleteAccReasonView()
}
