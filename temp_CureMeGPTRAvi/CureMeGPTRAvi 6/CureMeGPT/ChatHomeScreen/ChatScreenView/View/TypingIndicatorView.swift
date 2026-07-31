//
//  TypingIndicatorView.swift
//  CureMeGPT
//
//  Created by Antigravity on 09/07/26.
//

import SwiftUI

struct TypingIndicatorView: View {
    @State private var dotOffset1: CGFloat = 0
    @State private var dotOffset2: CGFloat = 0
    @State private var dotOffset3: CGFloat = 0
    
    var body: some View {
        HStack(spacing: 6) {
            Circle()
                .fill(Color.gray)
                .frame(width: 8, height: 8)
                .offset(y: dotOffset1)
            Circle()
                .fill(Color.gray)
                .frame(width: 8, height: 8)
                .offset(y: dotOffset2)
            Circle()
                .fill(Color.gray)
                .frame(width: 8, height: 8)
                .offset(y: dotOffset3)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(Color(hex: "#F8F8F8"))
        .cornerRadius(16)
        .onAppear {
            animateDots()
        }
    }
    
    private func animateDots() {
        let animation = Animation.easeInOut(duration: 0.4).repeatForever(autoreverses: true)
        withAnimation(animation.delay(0)) {
            dotOffset1 = -6
        }
        withAnimation(animation.delay(0.15)) {
            dotOffset2 = -6
        }
        withAnimation(animation.delay(0.3)) {
            dotOffset3 = -6
        }
    }
}
