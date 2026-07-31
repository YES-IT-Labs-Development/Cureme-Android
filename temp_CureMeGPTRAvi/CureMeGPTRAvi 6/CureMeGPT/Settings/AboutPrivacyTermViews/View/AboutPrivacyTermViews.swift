//
//  AboutPrivacyTermView.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 24/11/25.
//


//
//  AboutPrivacyTermView.swift
//

import SwiftUI

struct AboutPrivacyTermView: View {

    let pageData: SettingData   // ← receive data

    @EnvironmentObject private var coordinator: Coordinator

    var body: some View {

        VStack {

            // HEADER
            HStack {

                Button {
                    coordinator.pop()
                } label: {

                    Image("backIcon")
                        .resizable()
                        .frame(width: 45, height: 45)
                }

                Text(pageData.title)
                    .font(.custom("PlusJakartaSans-Medium", size: 20))
                    .foregroundColor(.black)
                    .padding(.leading, 10)

                Spacer()
            }
            .padding()

            // CONTENT
            ScrollView {

                Text(highlightedText(pageData.content))
                    .font(.custom("Urbanist-Medium", size: 16))
                    .lineSpacing(10)
                    .padding(.top, 10)

            }
            .padding(.horizontal, 20)
            .disableScrollBounce()
        }
        .background(Color.white)
    }
}



//import SwiftUI
//
//struct AboutPrivacyTermView: View {
//    @StateObject private var vm = AboutPrivacyTermViewModel()
//    @EnvironmentObject private var coordinator: Coordinator
//
//    var body: some View {
//
//        ZStack {
//
//            VStack {
//
//                // HEADER
//                HStack {
//                    Button(action: {
//                        coordinator.pop()
//                    }) {
//                        Image("backIcon")
//                            .resizable()
//                            .frame(width: 45, height: 45)
//                    }
//
//                    Text(vm.aboutUsData?.data?.title ?? "")
//                        .font(.custom("PlusJakartaSans-Medium", size: 20))
//                        .foregroundColor(.black)
//                        .padding(.leading, 10)
//
//                    Spacer()
//                }
//                .padding()
//
//                // CONTENT
//                ScrollView {
//
//                    if let content = vm.aboutUsData?.data?.content {
//
//                        Text(highlightedText(content))
//                            .font(.custom("Urbanist-Medium", size: 16))
//                            .lineSpacing(10)
//                            .padding(.top, 10)
//
//                    } else {
//                        Text("No Data Found")
//                            .padding()
//                    }
//                }
//                .padding(.horizontal, 20)
//                .disableScrollBounce()
//            }
//
//            //  LOADER
//            if vm.showActivity {
//                CustomLoderView(isVisible: $vm.showActivity)
//                    .frame(maxWidth: .infinity, maxHeight: .infinity)
//                    .background(Color.black.opacity(0.3))
//                    .ignoresSafeArea()
//            }
//        }
////        .onAppear {
////            vm.apiAboutUsData()   // ✅ IMPORTANT
////        }
//
//        .customAlert(
//                  isPresented: $vm.isPresentAlert,
//                  message: vm.errorMessage ?? "Error"
//              ) {
//                  print("OK tapped")
//              }
////        .alert(isPresented: $vm.isPresentAlert) {
////            Alert(title: Text("Error"),
////                  message: Text(vm.errorMessage ?? ""),
////                  dismissButton: .default(Text("OK")))
////        }
//    }
//}
//
//#Preview {
//    AboutPrivacyTermView()
//}
//
//
////    // MARK: - Highlight Function
    func highlightedText(_ text: String) -> AttributedString {
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
