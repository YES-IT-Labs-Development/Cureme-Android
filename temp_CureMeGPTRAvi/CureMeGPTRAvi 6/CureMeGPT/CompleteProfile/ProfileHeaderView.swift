//
//  ProfileHeaderView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 03/02/26.
//

import SwiftUI

struct ProfileHeaderView: View {
    let title: String
    let showSkip: Bool
    let onBack: () -> Void
    let onSkip: (() -> Void)?

    var body: some View {
        HStack {
//            Button(action: onBack) {
//                Image("backIcon")
//                    .resizable()
//                    .frame(width: 45, height: 45)
//            }
            
            
            Button {
                UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                onBack()             // Navigate back
            } label: {
                Image("backIcon")
                    .resizable()
                    .frame(width: 45, height: 45)
            }
            

            Text(title)
                .font(.custom("Urbanist-Medium", size: 20))
                .foregroundColor(.black)

            Spacer()

//            if showSkip, let onSkip {
//                Button("Skip for Now", action: onSkip)
//                    .font(.custom("Urbanist-Medium", size: 16))
//                    .foregroundColor(Color(hex: "#211C64"))
//            }
            if showSkip, let onSkip {
                Button(action: onSkip) {
                    Image("Skip for Now") // apna image name
                        .resizable()
                        .scaledToFit()
                        .frame(width: 90, height: 25)
                }
            }
        }
        .padding(.horizontal)
        .padding(.top, 10)
    }
    
   
}
