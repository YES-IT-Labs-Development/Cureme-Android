//
//  CustomAlertModifier.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 15/06/26.
//

import SwiftUI

struct CustomAlertModifier: ViewModifier {

    @Binding var isPresented: Bool
    var message: String
    var buttonTitle: String = "OK"
    var action: (() -> Void)? = nil

    func body(content: Content) -> some View {
        content
            .overlay {
                if isPresented {
                    ZStack {
                        Color.black.opacity(0.4)
                            .ignoresSafeArea()
                            .contentShape(Rectangle())

                        VStack(spacing: 20) {

                            Text(message)
                                .font(.custom("Urbanist-Regular", size: 16))
                                .multilineTextAlignment(.center)

                            Button {
                                isPresented = false
                                action?()
                            } label: {
                                Text(buttonTitle)
                                    .font(.custom("Urbanist-SemiBold", size: 16))
                                    .foregroundColor(.black)
                                    .frame(maxWidth: .infinity)
                                    .frame(height: 50)
                                    .background(Color.gray.opacity(0.2))
                                    .cornerRadius(25)
                            }
                        }
                        .padding()
                        .frame(width: 350)
                        .background(Color.white)
                        .cornerRadius(30)
                        .shadow(radius: 10)
                    }
                    .zIndex(999) // <- important
                }
            }
    }
}
