//
//  Privacy&ConsentView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 01/12/25.
//

import SwiftUI

struct PrivacyConsentView: View {
    let flow: PrivacyConsentFlow
    @StateObject private var vm = PrivacyConsentViewModel()
    @EnvironmentObject private var coordinator: Coordinator

    var body: some View {
            VStack(alignment: .leading, spacing: 20) {
                VStack(alignment: .leading, spacing: 10) {
                    Text("Privacy & Consent")
                        .font(.custom("Urbanist-Bold", size: 24))
                        .foregroundColor(.white)
                    
                    Text("Please review and agree to continue")
                        .font(.custom("Urbanist-Regular", size: 16))
                        .foregroundColor(.white)
                }
                .padding(.horizontal, 24)
                .padding(.top, 70)
                .frame(maxWidth: .infinity, alignment: .leading)
                .frame(height: 225)
                .background(
                    LinearGradient(
                        colors: [
                            Color(red: 67/255, green: 56/255, blue: 202/255),
                            Color(red: 33/255, green: 28/255, blue: 100/255)
                        ],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
                .clipShape(RoundedCornerShape(corners: [.bottomLeft, .bottomRight], radius: 25))
                
          ScrollView(showsIndicators: false) {
                VStack{
                    // MARK: - Medical Disclaimer Box
                    disclaimerBox(
                        title: "Medical Disclaimer",
                        subtitle: "This app provides AI-powered health insights for informational purposes only. It is not a substitute for professional medical advice, diagnosis, or treatment.",
                        backgroundColor: Color(hex: "#EEF2FE"),
                        borderColor: Color(hex: "#E0E7FF"),
                        titleColor: Color(hex: "#4338CA")
                    )
                    .padding(.top, 20)
                    .padding(.bottom, 20)
                    
                    // MARK: - Data Privacy Box
                    disclaimerBox(
                        title: "Data Privacy",
                        subtitle: "Your health data is encrypted and stored securely. We comply with HIPAA and other privacy regulations.",
                        backgroundColor: Color(hex: "#FFF5F5"),
                        borderColor: Color(hex: "#FEE2E2"),
                        titleColor: Color(hex: "#EF4444")
                    )
                    
                    // MARK: - Checkboxes Section
                    VStack(alignment: .leading, spacing: 2) {
                        
                        HStack(alignment: .top, spacing: 12) {
                            
                            Button(action: {
                                vm.agreePrivacyPolicy.toggle()
                            }) {
                                Image(vm.agreePrivacyPolicy ? "FilledCheckBox" : "CheckBox")
                                    .resizable()
                                    .frame(width: 23, height: 23)
                            }

                            (
                                Text("I Have Read And Agree To The ")
                                    .foregroundColor(Color(hex: "#697383"))
                                +
                                Text(vm.setting(for: "privacy-policy")?.title ?? "Privacy Policy")
                                    .foregroundColor(.blue)
                            )
                            .font(.custom("Urbanist-Regular", size: 13))
                            .onTapGesture {
                                if let item = vm.setting(for: "privacy-policy") {
                                    coordinator.push(.aboutPrivacyTermView(data: item))
                                }
                            }

                            Spacer()
                        }
                        .padding(.bottom, 20)
                        HStack(alignment: .top, spacing: 12) {
                            
                            Button(action: {
                                vm.agreeTerms.toggle()
                            }) {
                                Image(vm.agreeTerms ? "FilledCheckBox" : "CheckBox")
                                    .resizable()
                                    .frame(width: 23, height: 23)
                            }

                            (
                                Text("I Agree To The ")
                                    .foregroundColor(Color(hex: "#697383"))
                                +
                                Text(vm.setting(for: "terms-conditions")?.title ?? "Terms Of Service")
                                    .foregroundColor(.blue)
                            )
                            .font(.custom("Urbanist-Regular", size: 13))
                            .onTapGesture {
                                if let item = vm.setting(for: "terms-conditions") {
                                    coordinator.push(.aboutPrivacyTermView(data: item))
                                }
                            }

                            Spacer()
                        }
                        
                        CheckBoxRow(
                            title: "I Understand This App Does Not Replace Professional Medical Advice",
                            isChecked: $vm.understandMedicalAdvice
                        )
                        
                        CheckBoxRow(
                            title: "I consent to the processing of my health data for AI analysis",
                            isChecked: $vm.consentAIProcessing
                        )
                    }
                    .padding()
                    .background(RoundedRectangle(cornerRadius: 20).stroke(Color(hex: "#697383").opacity(0.3)))
                    .padding(.top, 20)
                    
                    // MARK: - Continue Button
                    Button(action: {
                        guard vm.validate() else { return }
                        
                        UserDefaults.standard.set(true, forKey: "hasAcceptedPrivacyConsent")
                        
                        if flow == .onboarding {
                            coordinator.push(.completeProfileView(flow: .profileSetup))
                        } else if flow == .askAI {
                            coordinator.selectedAppTab = .magic
                            coordinator.popToHome()
                        }
                    }) {
                        Text("I Agree - Continue")
                            .font(.custom("Urbanist-SemiBold", size: 16))
                            .frame(maxWidth: .infinity)
                            .frame(height: 55)
                            .background(
                                LinearGradient(
                                    colors: [
                                        Color(red: 67/255, green: 56/255, blue: 202/255),
                                        Color(red: 33/255, green: 28/255, blue: 100/255)
                                    ],
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                            .foregroundColor(.white)
                            .cornerRadius(40)
                    }
                    .padding(.top, 20)
                   
                    HStack(spacing: 8) {
                        Image("solar_lock-linear") // Your asset name
                            .resizable()
                            .frame(width: 20, height: 20)

                        Text("Encrypted")
                            .font(.custom("Urbanist-Medium", size: 16))
                            .foregroundColor(.gray).opacity(0.75)
                    }
                    .padding(.top, 20)
                    .padding(.bottom, 60)
                }
                .padding(.horizontal, 25)
            }
          .disableScrollBounce()
        }
            .onAppear {
                vm.getSettingDataAPI()
            }
        .ignoresSafeArea(.all)
        .alert("Alert", isPresented: $vm.showToast) {
            Button("OK") { }
        } message: {
            Text(vm.toastMessage)
        }
    }

    struct CheckBoxRow: View {
        let title: String
        @Binding var isChecked: Bool

        var body: some View {
            HStack(alignment: .top, spacing: 12) {
                Button(action: { isChecked.toggle() }) {
                    Image(isChecked
                          ? "FilledCheckBox"
                          : "CheckBox")
                    .resizable()
                    .frame(width: 23, height: 23)
                }

                Text(title)
                    .font(.custom("Urbanist-Regular", size: 13))
                    .foregroundColor(Color(hex: "#697383"))
                    .multilineTextAlignment(.leading)

                Spacer()
            }
            .padding(.vertical, 4)
            .padding(.top, 14)
        }
    }
    
    // MARK: - Disclaimer Box View
    private func disclaimerBox(
        title: String,
        subtitle: String,
        backgroundColor: Color,
        borderColor: Color,
        titleColor: Color = Color(hex: "#4338CA")
    ) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title)
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundColor(titleColor)

            Text(subtitle)
                .font(.custom("Urbanist-Medium", size: 14))
                .foregroundColor(Color(hex: "#697383"))
                .lineSpacing(4)
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(backgroundColor)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(borderColor, lineWidth: 1)
        )
    }
}

#Preview {
    PrivacyConsentView(flow: .onboarding)
}
