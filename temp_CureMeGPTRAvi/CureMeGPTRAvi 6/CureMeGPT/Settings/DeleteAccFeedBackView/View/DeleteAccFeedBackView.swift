//
//  DeleteAccFeedBackView.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 25/11/25.
//

import SwiftUI

struct DeleteAccFeedBackView: View {
    
    @StateObject private var vm = DeleteAccFeedBackViewModel()
    @EnvironmentObject private var coordinator: Coordinator
    
    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                headerView
                VStack(spacing: 20) {
                    ScrollView {
                        VStack {
                            topTexts
                            feedbackBox
                            deleteButton
                            Spacer()
                        }
                        .padding(.top)
                    }
                    .disableScrollBounce()
                }
                .padding(.horizontal, 20)
                .keyboardDoneButton()
            }
            
            if vm.showActivity {
                CustomLoderView(isVisible: $vm.showActivity)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color.black.opacity(0.3))
                    .ignoresSafeArea()
            }
        }
        .customAlert(
            isPresented: $vm.isPresentAlert,
            message: vm.errorMessage ?? "Error"
        ) {
            print("OK tapped")
        }
    }
}

// MARK: - Components
extension DeleteAccFeedBackView {
    private var headerView: some View {
        HStack {
            Button {
                coordinator.pop()
            } label: {
                Image("backIcon")
                    .resizable()
                    .frame(width: 45, height: 45)
            }
            
            Text("Delete Account")
                .font(.custom("PlusJakartaSans-Medium", size: 20))
                .foregroundColor(Color(hex: "#2E1302"))
                .padding(.leading, 10)
            Spacer()
        }
        .padding(.leading, 24)
        .padding(.top, 20)
        .padding(.bottom, 20)
    }
    
    private var topTexts: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("I don’t want to use CureMeGPT anymore")
                .font(.custom("Urbanist-Medium", size: 18))
                .foregroundColor(Color(hex: "##181818"))
            
            Text("Do you have any feedback for us? We would love to hear from you ! (optional)")
                .font(.custom("PlusJakartaSans-Regular", size: 16))
                .foregroundColor(Color(hex: "#697383"))
        }
        .lineSpacing(6)
        .padding(.top)
    }
    
    private var feedbackBox: some View {
        PlaceholderTextEditor(
            text: $vm.feedback,
            placeholder: "Please share your feedback (optional)"
            
        )
        .frame(height: 160)
        .padding()
        .background(Color(hex: "#FAF4F4"))
        .cornerRadius(20)
        .padding(.top, 10)
    }
    
    private var deleteButton: some View {
        Button(action: {
            vm.deleteAccount()
            // coordinator.push(.accountDeletedSuccess)
        }) {
            Text("Delete Account")
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .padding()
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
                .padding(.top, 20)
        }
        .padding(.horizontal, 0)
    }
}

struct PlaceholderTextEditor: View {
    @Binding var text: String
    let placeholder: String
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            
            // Placeholder text
            if text.isEmpty {
                Text(placeholder)
                    .font(.custom("Urbanist-Regular", size: 15))
                    .foregroundColor(Color(hex: "#697383"))
                    .padding(.top, 10)
                    .padding(.leading, 4)
            }
            
            // Actual editor
            TextEditor(text: $text)
                .font(.custom("Urbanist-Regular", size: 15))
                .foregroundColor(Color(hex: "#2E1302"))
                .scrollContentBackground(.hidden)
                .background(Color.clear)
            //.padding(4)
        }
        .cornerRadius(16)
    }
}

#Preview {
    DeleteAccFeedBackView()
}
