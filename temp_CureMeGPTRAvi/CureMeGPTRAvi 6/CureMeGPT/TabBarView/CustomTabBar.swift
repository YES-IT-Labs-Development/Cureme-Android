//
//  CustomTabBar.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 02/12/25.
//

import SwiftUI

struct CustomTabBar: View {
    @ObservedObject var vm: TabViewModel
    @EnvironmentObject private var coordinator: Coordinator
    
    var body: some View {
        HStack {
            ForEach(AppTab.allCases, id: \.self) { tab in
                Spacer(minLength: 0)

                if tab == .magic {
                    Image(vm.selectedTab == tab ? tab.selectedIcon : tab.unselectedIcon)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 70, height: 70)
                        .offset(y: -22)
                        .shadow(
                            color: Color(hex: "#4338CA").opacity(0.50), //  shadow color
                            radius: 10,         
                            x: 0,
                            y: 6
                        )
                        .onTapGesture {
                            vm.selectedTab = tab
                           // if !UserDefaults.standard.bool(forKey: "hasAcceptedPrivacyConsent") {
                                //coordinator.push(.privacyConsentView(flow: .askAI))
                           // }
                        }
                } else {
                    Image(vm.selectedTab == tab ? tab.selectedIcon : tab.unselectedIcon)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 70, height: 70)
                        .onTapGesture {
                            vm.selectedTab = tab
                        }
                }

                Spacer(minLength: 0)
            }
        }
        .padding(.horizontal, 5)
        .padding(.top, 8)
        .padding(.bottom, safeAreaBottomHeight() > 0 ? 16 : 8)
        .background(Color.white)
    }

    private func safeAreaBottomHeight() -> CGFloat {
        UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }?.safeAreaInsets.bottom ?? 0
    }
}

extension CustomTabBar {
    private func tabView(_ tab: AppTab) -> some View {
        VStack(spacing: 4) {
            Image(vm.selectedTab == tab ? tab.selectedIcon : tab.unselectedIcon)
                .resizable()
                .scaledToFit()
                .frame(width: 70, height: 70)

            if tab != .magic {
                Text(tab.title)
                    .font(.system(size: 12))
                    .foregroundColor(vm.selectedTab == tab ? .purple : .black)
            }
        }
    }
}

#Preview {
    CustomTabBar(vm: TabViewModel())
}
