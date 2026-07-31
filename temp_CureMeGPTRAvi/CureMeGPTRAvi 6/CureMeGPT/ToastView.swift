//
//  ToastView.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 11/03/26.
//
import SwiftUI
struct ToastView: View {
    
    var message: String
    
    var body: some View {
        Text(message)
            .font(.system(size: 14, weight: .medium))
            .foregroundColor(.white)
            .padding(.horizontal, 20)
            .padding(.vertical, 12)
            .background(Color.black.opacity(0.8))
            .cornerRadius(10)
            .shadow(radius: 5)
    }
}
