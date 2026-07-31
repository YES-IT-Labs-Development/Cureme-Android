//
//  CustomProgressView.swift
//  Shopping
//
//  Created by Berkay Sancar on 27.07.2024.
//

import SwiftUI

struct CustomLoderView: View {
    
    @Binding var isVisible: Bool
    
    var body: some View {
        if isVisible {
            ZStack {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()

                VStack {
                    LottieView(animationName: "loader")
                        .frame(width: 280, height: 280) // adjust size if needed
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color.gray.opacity(0.3))
                .ignoresSafeArea()
            }
            .allowsHitTesting(true)
        }
    }
}

#Preview {
    CustomLoderView(isVisible: .constant(true))
}
