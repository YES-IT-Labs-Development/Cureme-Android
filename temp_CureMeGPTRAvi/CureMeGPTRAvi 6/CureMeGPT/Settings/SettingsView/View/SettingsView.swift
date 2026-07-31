//
//  SettingsView.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 24/11/25.
//

import SwiftUI

struct SettingsView: View {
    @StateObject var vm = SettingsViewModel()
    @EnvironmentObject private var coordinator: Coordinator
    @State private var showPopup: Bool = false
    @State private var popupAction: (() -> Void)? = nil
    
    var body: some View {
        ZStack{
            // SHOW ONLY LOADER
            if vm.showActivity {
                
                CustomLoderView(isVisible: $vm.showActivity)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color.black.opacity(0.3))
                    .ignoresSafeArea()
                
            } else {
                VStack(alignment: .leading, spacing: 0) {
                    // Top title
                    HStack {
                        Button(action: {
                            coordinator.pop()
                        }) {
                            Image("backIcon")
                                .resizable()
                                .frame(width: 45, height: 45)
                                .padding(.leading, 10)
                            
                        }
                        
                        Text("Settings")
                            .font(.custom("Urbanist-Medium", size: 20))
                            .foregroundColor(.black)
                            .padding(.leading, 10)
                        Spacer()
                    }
                    .padding(.horizontal)
                    .padding(.top, 0)
                    
                    VStack{
                        ScrollView {
                            VStack(spacing: 0) {
                                ForEach(vm.items.indices, id: \.self) { index in
                                    let item = vm.items[index]
                                    
                                    SettingsRow(
                                        item: item,
                                        toggleValue: $vm.items[index].toggleValue
                                    )
                                    
                                    .onTapGesture {
                                        
                                        guard !item.isToggle else { return }
                                        
                                        // Last item -> FAQ
                                        if index == vm.items.count - 1 {
                                            coordinator.push(.faqView)
                                            return
                                        }
                                        
                                        if let route = item.route {
                                            coordinator.push(route)
                                        }
                                    }
                                    
                                    Divider().padding(.horizontal, 20)
                                }
                            }
                            .cornerRadius(26)
                            .background(
                                RoundedRectangle(cornerRadius: 20)
                                    .fill(Color(hex: "#4338CA").opacity(0.04))
                            )
                            .padding()
                            .contentShape(Rectangle())
                            
                            Button(action: {
                                showPopup = true
                                
                            }){
                                Image("settingLogoutIcon")
                                    .resizable()
                                    .frame(width: 140, height: 140)
                                    .scaledToFit()
                                    .shadow(
                                        color: Color(hex: "#F31D1D").opacity(0.20), //  shadow color
                                        radius: 10,
                                        x: 0,
                                        y: 6
                                    )
                            }
                            
                            Image("Frame 2147223359")
                                .resizable()
                                .frame(width: .infinity, height: 80)
                                .background(Color.clear)
                                .padding()
                        }
                        .disableScrollBounce()
                        .contentShape(Rectangle())
                        //.frame(alignment: .center)
                    }
                    .contentShape(Rectangle())
                    
                    
                }
                .background(
                    LinearGradient(colors: [.white, .white.opacity(0.6)],
                                   startPoint: .top,
                                   endPoint: .bottom)
                    .edgesIgnoringSafeArea(.all)
                )
                
            }
            
            if showPopup {
                // DARK BACKGROUND
                Color.black.opacity(0.4)
                    .edgesIgnoringSafeArea(.all)
                    .transition(.opacity)
                    .onTapGesture {
                        withAnimation {
                            showPopup = false
                            popupAction?()
                        }
                    }
                
                // USING YOUR EXISTING POPUP VIEW EXACTLY AS IT IS
                LogoutConfirmPopUpView(title: "Confirm Logout",
                                       message: "Are you sure you want to log out of your account?",
                                       onClose: {
                    showPopup = false
                },
                                       onLogout: {
                    withAnimation(.easeOut(duration: 0.25)) {
                        showPopup = false
                    }
                }
                )
                .transition(.scale.combined(with: .opacity))
                .zIndex(1)
            }
            
            
        }
        .onAppear {
            vm.getSettingDataAPI()   //IMPORTANT
        }
        
        .customAlert(
            isPresented: $vm.isPresentAlert,
            message: vm.errorMessage ?? "Error"
        ) {
            print("OK tapped")
        }
    }
}

struct SettingsRow: View {
    let item: SettingsModel
    @Binding var toggleValue: Bool
    
    var body: some View {
        HStack(spacing: 16) {
            Image(item.icon)
                .resizable()
                .frame(width: 30, height: 30)
            
            Text(item.title)
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundColor(Color.black)
            
            Spacer()
            
            if item.isToggle {
                Toggle("", isOn: $toggleValue)
                    .labelsHidden()
            } else {
                Image("RightArrow")
                
            }
        }
        // Divider()
        .padding(4)
        .padding(.vertical, 14)
        .padding(.horizontal, 10)
        .contentShape(Rectangle())   // 👈 IMPORTANT
    }
}

#Preview {
    SettingsView()
}
