//
//  FAQScreenView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 05/12/25.
//

import SwiftUI

struct FAQScreenView: View {
    @EnvironmentObject private var coordinator: Coordinator
    @StateObject private var vm = FAQViewModel()

    var body: some View {

        ZStack {
            
            VStack {

                HStack {
                    Button(action: {
                        coordinator.pop()
                    }) {
                        Image("backIcon")
                            .resizable()
                            .frame(width: 45, height: 45)
                            .padding(.leading, 10)
                    }
                    
                    Text("Frequently Ask Questions")
                        .font(.custom("Urbanist-Medium", size: 20))
                        .foregroundColor(Color.black)
                        .padding(.leading, 10)
                    Spacer()
                }
                .padding()

                ScrollView {
                    VStack(spacing: 16) {
                        ForEach(vm.faQItems) { item in
                            FAQSectionView(item: item) {
                                vm.toggleFAQ(item)
                            }
                        }
                    }
                    .padding(.horizontal)
                    .padding(.top, 20)
                }
                .disableScrollBounce()
            }

            // LOADER ON TOP OF EVERYTHING
            if vm.showActivity {
                CustomLoderView(isVisible: $vm.showActivity)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color.black.opacity(0.3)) // optional dim
                    .ignoresSafeArea()
            }
        }
        .onAppear {
            vm.apiForFAQData()
        }
        
        .customAlert(
                  isPresented: $vm.isPresentAlert,
                  message: vm.errorMessage ?? "Error"
              ) {
                  print("OK tapped")
              }
//        .alert(isPresented: $vm.isPresentAlert) {
//            Alert(title: Text("Error"),
//                  message: Text(vm.errorMessage ?? ""),
//                  dismissButton: .default(Text("OK")))
//        }
    }
}


struct FAQSectionView: View {
    let item: FAQItem
    let onTap: () -> Void

    var body: some View {
        VStack(spacing: 0) {

            // QUESTION HEADER
            HStack {
                Text(item.heading)
                    .font(.custom("Urbanist-Regular", size: 14))
                    .foregroundColor(item.isExpanded  ? .white : .black)

                Spacer()

                Image(item.isExpanded ? "DropUp" : "DropDown")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 12, height: 12)
//                Image(item.isExpanded ? "DropUp" : "DropDown")
//                    .rotationEffect(.degrees(item.isExpanded ? 180 : 0))
            }
            .padding()
            .background(
                item.isExpanded
                ? AnyShapeStyle(
                    LinearGradient(
                        colors: [
                            Color(red: 67/255, green: 56/255, blue: 202/255),
                            Color(red: 33/255, green: 28/255, blue: 100/255)
                        ],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
                : AnyShapeStyle(Color(hex: "#4338CA").opacity(0.10))
            )
            .clipShape(
                RoundedCorner(
                    radius: 12,
                    corners: item.isExpanded ? [.topLeft, .topRight] : .allCorners
                )
            )
            .onTapGesture {
                withAnimation(.easeInOut(duration: 0.25)) {
                    onTap()
                }
            }

            // ANSWER BODY
            if item.isExpanded {
                Text(item.answer)
                    .font(.custom("Urbanist-Regular", size: 14))
                    .foregroundColor(.black)
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .clipShape(
                        RoundedCorner(
                            radius: 12,
                            corners: [.bottomLeft, .bottomRight]
                        )
                    )
                    .transition(.opacity.combined(with: .move(edge: .top)))
            }
        }
        .background(Color(hex: "#4338CA").opacity(0.10))
        .cornerRadius(30)
    }
}


#Preview {
    FAQScreenView()
}
