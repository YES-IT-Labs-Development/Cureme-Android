//
//  AppointmentSummaryView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 07/01/26.
//

import SwiftUI

struct AppointmentSummaryView: View {
    
    @ObservedObject var viewModel: AppointmentSummaryViewModel
    
    var body: some View {
        ZStack {
            // Background tap area (outside popup)
            Color.black.opacity(0.45)
                .ignoresSafeArea()
                .onTapGesture {
                    viewModel.dismiss()
                }
            
            // Popup content (captures taps correctly)
            popupContent
                .allowsHitTesting(true)
            
            //            if viewModel.showToast {
            //                VStack {
            //                    Spacer()
            //
            //                    Text(viewModel.toastMessage)
            //                        .font(.custom("Urbanist-Medium", size: 14))
            //                        .foregroundColor(.white)
            //                        .padding(.horizontal, 20)
            //                        .padding(.vertical, 12)
            //                        .background(
            //                            Capsule()
            //                                .fill(Color.black.opacity(0.85))
            //                        )
            //                        .padding(.bottom, 50)
            //                }
            //                .transition(.move(edge: .bottom).combined(with: .opacity))
            //                .zIndex(999)
            //            }
        }
        
    }
    
    private var popupContent: some View {
        VStack(spacing: 16) {
            
            // Header
            HStack(spacing: 12) {
                Image("RightCheckMark")
                    .resizable()
                    .frame(width: 42, height: 42)
                
                Text("Summary Details")
                    .font(.custom("Urbanist-Medium", size: 18))
                
                Spacer()
                
                Button {
                    print("Hide It Now")
                    viewModel.dismiss()
                } label: {
                    Image("CrossButton")
                        .resizable()
                        .frame(width: 42, height: 42)
                }
            }
            
            if viewModel.isLoading {
                Spacer()
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: Color(hex: "#4338CA")))
                    .scaleEffect(1.2)
                Spacer()
            } else {
                ScrollView {
                    VStack(alignment: .leading, spacing: 14) {
                        if !viewModel.title.isEmpty {
                            Text(viewModel.title)
                                .font(.custom("Urbanist-Bold", size: 18))
                                .foregroundColor(Color(hex: "#211C64"))
                        }
                        
                        if !viewModel.summary.isEmpty {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Summary")
                                    .font(.custom("Urbanist-SemiBold", size: 15))
                                    .foregroundColor(.black.opacity(0.8))
                                
                                Text(viewModel.summary)
                                    .font(.custom("Urbanist-Regular", size: 14))
                                    .foregroundColor(Color(hex: "#374151"))
                                    .lineSpacing(4)
                            }
                        }
                        
                        if !viewModel.symptoms.isEmpty {
                            VStack(alignment: .leading, spacing: 6) {
                                Text("Symptoms")
                                    .font(.custom("Urbanist-SemiBold", size: 15))
                                    .foregroundColor(.black.opacity(0.8))
                                
                                ForEach(viewModel.symptoms, id: \.self) { symptom in
                                    HStack(alignment: .top, spacing: 8) {
                                        Text("•")
                                            .font(.custom("Urbanist-Bold", size: 14))
                                            .foregroundColor(Color(hex: "#4338CA"))
                                        Text(symptom)
                                            .font(.custom("Urbanist-Regular", size: 14))
                                            .foregroundColor(Color(hex: "#374151"))
                                            .lineSpacing(4)
                                    }
                                }
                            }
                        }
                        
                        if !viewModel.recommendations.isEmpty {
                            VStack(alignment: .leading, spacing: 6) {
                                Text("Recommendations")
                                    .font(.custom("Urbanist-SemiBold", size: 15))
                                    .foregroundColor(.black.opacity(0.8))
                                
                                ForEach(viewModel.recommendations, id: \.self) { recommendation in
                                    HStack(alignment: .top, spacing: 8) {
                                        Text("•")
                                            .font(.custom("Urbanist-Bold", size: 14))
                                            .foregroundColor(Color(hex: "#4338CA"))
                                        Text(recommendation)
                                            .font(.custom("Urbanist-Regular", size: 14))
                                            .foregroundColor(Color(hex: "#374151"))
                                            .lineSpacing(4)
                                    }
                                }
                            }
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                }
                .disableScrollBounce()
            }
            
            Button {
                viewModel.downloadSummary()
            } label: {
                Text("Download")
                    .font(.custom("Urbanist-Medium", size: 16))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
//                    .background(
//                        LinearGradient(
//                            colors: [
//                                Color(red: 67/255, green: 56/255, blue: 202/255),
//                                Color(red: 33/255, green: 28/255, blue: 100/255)
//                            ],
//                            startPoint: .leading,
//                            endPoint: .trailing
//                        )
//                    )
                    .background {
                        Image("BackgroundBtn")
                            .resizable()
                            .scaledToFill()
                    }
                    .contentShape(Capsule())
                    .foregroundColor(.white)
                    .clipShape(Capsule())
                    //.cornerRadius(45)
            }
            .disabled(viewModel.isLoading)
            .opacity(viewModel.isLoading ? 0.5 : 1.0)
        }
        .padding()
        .frame(maxWidth: 340, maxHeight: 460)
        .background(Color.white)
        .cornerRadius(28)
    }
}

#Preview {
    let previewVM = AppointmentSummaryViewModel()
    previewVM.isPresented = true
    previewVM.title = "Cancer Diagnosis Concern"
    previewVM.summary = "User reports a cancer diagnosis and is advised to contact a specialist for proper treatment."
    previewVM.recommendations = [
        "Contact a specialist doctor for proper evaluation and treatment."
    ]

    return AppointmentSummaryView(viewModel: previewVM)
}

