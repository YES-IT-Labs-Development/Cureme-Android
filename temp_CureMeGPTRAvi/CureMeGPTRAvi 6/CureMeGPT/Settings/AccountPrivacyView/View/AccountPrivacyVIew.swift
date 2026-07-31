//
//  AccountPrivacyVIew.swift
// CureMeGpt APP
//
//  Created by YES IT Labs on 24/11/25.
//

import SwiftUI

struct AccountPrivacyVIew: View {
  //  @StateObject private var vm = AccountPrivacyViewModel()
    @EnvironmentObject private var coordinator: Coordinator
    
    let data: SettingData
    
    var body: some View {
        ZStack {
            VStack {
                // ----------- HEADER --------------
                HStack {
                    Button(action: {
                        coordinator.pop()
                    }) {
                        Image("backIcon")
                            .resizable()
                            .frame(width: 45, height: 45)
                            .padding(.leading, 10)
                    }
                    //Text(vm.accountPolicyData?.data?.title ?? "")
                    Text(data.title)
                        .font(.custom("PlusJakartaSans-Medium", size: 20))
                        .foregroundColor(Color(hex: "#2E1302"))
                        .padding(.leading, 10)
                    
                    Spacer()
                }
                .padding()
                
                // ----------- CONTENT --------------
                VStack(alignment: .leading, spacing: 20) {
                    
                    // Description with dotted border
                    ScrollView {
                        Text(highlightedText(data.content))
                       // Text(highlightedText(vm.accountPolicyData?.data?.content ?? ""))
                            .font(.custom("PlusJakartaSans-Regular", size: 16))
                            .foregroundColor(Color(hex: "#595959"))
                            .padding()
                            .lineSpacing(10)
                        
                        // ----------- YOUR NEW BUTTONS --------------
                        VStack(spacing: 20) {
                            AccountPrivacySettings(
                                icon: "RestImg",
                                title: "Reset Your Password"
                            ) {
                                coordinator.push(.resetPasswordView)
                            }
                            
                            AccountPrivacySettings(
                                icon: "AccountImg",
                                title: "Delete Account"
                            ) {
                                coordinator.push(.deleteAccReasionView)
                            }
                        }
                        .padding(.horizontal, 20)
                        .padding(.top, 10)

                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color.white)
                    .padding(.horizontal, 20)
                    .disableScrollBounce()
                    
                }
                
                Spacer()
            }
//            .onAppear {
//                vm.apiprivacyPolicyData()   // ✅ IMPORTANT
//            }
            
//            .customAlert(
//                      isPresented: $vm.isPresentAlert,
//                      message: vm.errorMessage ?? "Error"
//                  ) {
//                      print("OK tapped")
//                  }

            //  LOADER
//            if vm.showActivity {
//                CustomLoderView(isVisible: $vm.showActivity)
//                    .frame(maxWidth: .infinity, maxHeight: .infinity)
//                    .background(Color.black.opacity(0.3))
//                    .ignoresSafeArea()
//            }
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

//#Preview {
//    AccountPrivacyVIew()
//}


   
struct AccountPrivacySettings: View {
    var icon: String
    var title: String
    var action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 16) {
                // Left circular icon
                ZStack {
                    Circle()
                        .fill(Color.clear)  // brown color same as screenshot
                        .frame(width: 36, height: 36)
                    
                    Image(icon)
                        .foregroundColor(.white)
                }

                Text(title)
                    .font(.custom("PlusJakartaSans-Regular", size: 16))
                    .foregroundColor(Color(hex: "#2E1302"))
                
                Spacer()

                Image("RightArrow")
                    .foregroundColor(Color(hex: "#2E1302").opacity(0.6))
            }
            .padding(14)
            .overlay(
                RoundedRectangle(cornerRadius: 40)
                    .stroke(Color(hex: "#CED4DA"), lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
}
