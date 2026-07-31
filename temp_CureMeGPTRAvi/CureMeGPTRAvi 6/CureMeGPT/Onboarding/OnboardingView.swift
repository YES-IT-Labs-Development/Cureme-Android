//
//  OnboardingView.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 28/11/25.
//

import Foundation

struct OnboardingItem: Identifiable {
    let id = UUID()
    let imageName: String
    let title: String
    let description: String
}
import SwiftUI
import FirebaseCoreInternal

struct OnboardingView: View {
    @State private var currentPage = 0
    @EnvironmentObject private var coordinator: Coordinator
    @StateObject private var vm = OnboardingViewModel()
    
    var body: some View {
        ZStack(alignment: .top) {
            VStack(spacing: 0) {
                
                // FULL SCREEN TABVIEW
                TabView(selection: $currentPage) {
                    
                    ForEach(vm.onboardingItems.indices, id: \.self) { index in
                        VStack {
                            Image.loadImage(
                                vm.onboardingItems[index].image.imgFullPath(),
                                width: UIScreen.main.bounds.width,
                                height: UIScreen.main.bounds.height * 0.50,
                                cornerRadius: 20,
                                contentMode: .fill
                            )
                            .padding(.top, -40)
                            .ignoresSafeArea(edges: .top)
                            
                            // INDICATORS
                            HStack(spacing: 10) {
                                ForEach(0..<vm.onboardingItems.count, id: \.self) { index in
                                    Capsule()
                                        .fill(index == currentPage ? Color(hex: "#4338CA") : Color.gray.opacity(0.3))
                                        .frame(width: index == currentPage ? 45 : 14, height: 8)
                                        .animation(.easeInOut, value: currentPage)
                                }
                            }
                            .padding(.bottom, 20)
                            .padding(.top, 40)
                            
                            VStack(spacing: 12) {
                                // Text(onboardingData[index].title)
                                Text(vm.onboardingItems[index].heading)
                                //.font(.system(size: 24, weight: .bold))
                                    .font(.custom("Urbanist-Bold", size: 24))
                                    .multilineTextAlignment(.center)
                                    .padding(.top, 30)
                                
                                
                                Text(vm.onboardingItems[index].description)
                                
                                    .font(.custom("Urbanist-Regular", size: 18))
                                    .multilineTextAlignment(.center)
                                    .foregroundColor(.black)
                                    .padding(.top, 30)
                            }
                            .padding(.horizontal, 30)
                            
                            Spacer()
                        }
                        .edgesIgnoringSafeArea(.top)
                        // .tag(index)
                    }
                }
                .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
                .animation(.none, value: currentPage)
                .edgesIgnoringSafeArea(.top)
                
                // BUTTONS
                HStack {
                    if currentPage == vm.onboardingItems.count - 1 {
                        // LAST PAGE → SHOW ONLY "GET STARTED"
                        Button {
                            coordinator.push(.login)
                            print("Navigate to login")
                        } label: {
                            Text("Get Started")
                                .foregroundColor(.white)
                                .font(.custom("Urbanist-SemiBold", size: 16))
                                .padding()
                                .frame(maxWidth: .infinity)
                                .background( Image("BackgroundBtn") // Asset name
                                            .resizable()
                                             .scaledToFill()   )
//                                .background(
//                                    LinearGradient(
//                                        colors: [
//                                            Color(red: 67/255, green: 56/255, blue: 202/255),
//                                            Color(red: 33/255, green: 28/255, blue: 100/255)
//                                        ],
//                                        startPoint: .leading,
//                                        endPoint: .trailing
//                                    )
//                                )

//                                .background(
//                                    LinearGradient(colors: [Color.init(red: 67/255, green: 56/255, blue: 202/255), Color.init(red: 77/255, green: 56/255, blue: 212/255)],
//                                                   startPoint: .leading,
//                                                   endPoint: .trailing)
//                                )
                                .cornerRadius(25)
                        }
                        
                    } else {
                        HStack(spacing: 16) {
                            // SKIP BUTTON
                            Button("Skip") {
                                goToLastPage()
                            }
                            .foregroundColor(.black)
                            .font(.custom("Urbanist-SemiBold", size: 16))
                            .frame(width: 110, height: 50)
                            .background(Color.white)
                            .overlay(
                                RoundedRectangle(cornerRadius: 25)
                                    .stroke(Color(hex: "#3741514D").opacity(30), lineWidth: 1)
                            )
                            .cornerRadius(25)
                            
                            // NEXT / CONTINUE BUTTON
                            Button {
                                goToNextPage()
                            } label: {
                                Text(currentPage == 0 ? "Next" : "Continue")
                                    .foregroundColor(.white)
                                    .font(.custom("Urbanist-SemiBold", size: 16))
                                    .frame(width: 250, height: 50)
                                    .background( Image("BackgroundBtn") // Asset name
                                                .resizable()
                                                 .scaledToFill()   )
//                                    .background(
//                                        LinearGradient(
//                                            colors: [
//                                                Color(red: 67/255, green: 56/255, blue: 202/255),
//                                                Color(red: 33/255, green: 28/255, blue: 100/255)
//                                            ],
//                                            startPoint: .leading,
//                                            endPoint: .trailing
//                                        )
//                                    )

//                                    .background(
//                                        LinearGradient(colors: [Color.init(red: 67/255, green: 56/255, blue: 202/255), Color.init(red: 77/255, green: 56/255, blue: 212/255)],
//                                                       startPoint: .leading,
//                                                       endPoint: .trailing)
//                                    )
                                    .cornerRadius(25)
                            }
                        }
                    }
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 50)
                .animation(.easeInOut, value: currentPage)
                
            }
            
            .edgesIgnoringSafeArea(.top)
            if vm.showActivity {
                CustomLoderView(isVisible: $vm.showActivity)
                    .ignoresSafeArea()
            }
        }
        .edgesIgnoringSafeArea(.top)
        
        .onAppear {
            vm.apiForOnboardingData()
        }
        
    }
    
    // MARK: - Navigation Logic
    
    private func goToNextPage() {
        if currentPage < vm.onboardingItems.count - 1 {
            currentPage += 1          // <-- NO ANIMATION HERE
        } else {
            print("Navigate to login")
        }
    }
    
    private func goToLastPage() {
        withAnimation { currentPage = vm.onboardingItems.count - 1 }
    }
}

#Preview {
    OnboardingView()
}
