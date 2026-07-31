//
//  AboutPrivacyTermView.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 24/11/25.
//

import SwiftUI

struct PrivacyPolicyView: View {
    @StateObject private var vm = PrivacyPolicyViewModel()
    @EnvironmentObject private var coordinator: Coordinator
    
    var body: some View {
        ZStack{
            VStack{
                HStack{
                    Button(action: {
                        coordinator.pop()
                    }) {
                        Image("backIcon")
                            .resizable()
                            .frame(width: 45, height: 45)
                            .padding(.leading, 10)
                    }
                    Text(vm.privacyPolicyData?.data?.title ?? "")
                        .font(.custom("PlusJakartaSans-Medium", size: 20))
                        .foregroundColor(Color.black)
                        .padding(.leading, 10)
                    Spacer()
                }
                .padding()
                
                VStack(alignment: .leading) {
                    ScrollView {
                        Text(highlightedText(vm.privacyPolicyData?.data?.content ?? ""))
                            .font(.custom("Urbanist-Medium", size: 16))
                            .foregroundColor(Color(hex: "#181818"))
                            .padding()
                            .padding(.top, 10)
                            .lineSpacing(10)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color.white)
                    .padding(.horizontal, 20)
                    .disableScrollBounce()
                }
                .padding(.top, 10)
            }
            .onAppear {
                vm.apiprivacyPolicyData()   // ✅ IMPORTANT
            }
            .customAlert(
                      isPresented: $vm.isPresentAlert,
                      message: vm.errorMessage ?? "Error"
                  ) {
                      print("OK tapped")
                  }
//            .alert(isPresented: $vm.isPresentAlert) {
//                Alert(title: Text("Error"),
//                      message: Text(vm.errorMessage ?? ""),
//                      dismissButton: .default(Text("OK")))
//            }
            
            //  LOADER
            if vm.showActivity {
                CustomLoderView(isVisible: $vm.showActivity)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color.black.opacity(0.3))
                    .ignoresSafeArea()
            }

        }
    }
    
    // MARK: - Highlight Function
    private func highlightedText(_ text: String) -> AttributedString {
        let keyword = "CureMeGPT"
        var result = AttributedString()

        let parts = text.components(separatedBy: keyword)

        for index in parts.indices {
            // Normal text
            var normal = AttributedString(parts[index])
            normal.foregroundColor = Color(hex: "#181818")
            result.append(normal)

            // Highlighted keyword (except after last part)
            if index < parts.count - 1 {
                var highlighted = AttributedString(keyword)
                highlighted.foregroundColor = Color(hex: "#4338CA")
                result.append(highlighted)
            }
        }

        return result
    }
}

#Preview {
    PrivacyPolicyView()
}
