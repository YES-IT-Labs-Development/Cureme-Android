//
//  SharePopUpView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 02/02/26.
//

import SwiftUI

struct SharePopUpView: View {
    
    var title: String
    var message: String
    var onClose: () -> Void
    var message1: String
    
    @State private var linkTxt: String = ""
    @State private var copied = false
    
    var body: some View {
        
        VStack(spacing: 20) {
            
            HStack(alignment: .top) {
                
                Image("sharePopUpIcon")
                    .resizable()
                    .frame(width: 55, height: 55)
                
                Text(title)
                    .font(.custom("Urbanist-Medium", size: 18))
                    .foregroundColor(Color(hex: "#050505"))
                    .padding(.top, 14)
                
                Spacer()
                
                Button(action: onClose) {
                    Image("CrossButton")
                        .resizable()
                        .frame(width: 45, height: 45)
                }
            }
            
            HStack {
                Text(message)
                    .font(.custom("Urbanist-Regular", size: 16))
                
                Spacer()
            }
            
            HStack {
                Text("Shareable Link")
                    .font(.custom("Urbanist-Medium", size: 16))
                
                Spacer()
            }
            
            HStack {
                TextField("Shareable link", text: $linkTxt)
                    .font(.custom("Urbanist-Regular", size: 14))
                    .foregroundColor(.black)
                    .lineLimit(1)
                    .truncationMode(.middle)
                    .padding()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(
                        RoundedRectangle(cornerRadius: 30)
                            .stroke(Color.gray.opacity(0.3), lineWidth: 1)
                    )
                
                Button(action: {
                    let textToCopy = linkTxt.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? message1 : linkTxt
                    UIPasteboard.general.string = textToCopy
                    withAnimation {
                        copied = true
                    }
                }) {
                    Image("Copyy")
                        .resizable()
                        .frame(width: 42, height: 55)
                }
            }
            
            if copied {
                VStack(alignment: .leading, spacing: 6) {
                    HStack {
                        Image(systemName: "checkmark.circle.fill")
                            .foregroundColor(.green)
                        Text("Link Copied:")
                            .font(.custom("Urbanist-Bold", size: 14))
                            .foregroundColor(.green)
                    }
                    Text(linkTxt.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? message1 : linkTxt)
                        .font(.custom("Urbanist-Regular", size: 13))
                        .foregroundColor(Color(hex: "#050505"))
                        .lineLimit(2)
                        .truncationMode(.middle)
                }
                .padding(10)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color.green.opacity(0.1))
                )
            }
        }
        .padding(20)
        .background(
            RoundedRectangle(cornerRadius: 25)
                .fill(.white)
                .shadow(color: .black.opacity(0.15), radius: 8)
        )
        .padding(.horizontal, 30)
        .onAppear {
            if !message1.isEmpty {
                linkTxt = message1
            }
        }
        .onChange(of: message1) { newMsg in
            if !newMsg.isEmpty {
                linkTxt = newMsg
            }
        }
    }
}
