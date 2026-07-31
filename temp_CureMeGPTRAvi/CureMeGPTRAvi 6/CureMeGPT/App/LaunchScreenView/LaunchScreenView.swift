//
//  ContentView.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 28/11/25.
//

import SwiftUI

struct LaunchScreenView: View {
    @EnvironmentObject private var coordinator: Coordinator
    let profileVM = ProfileViewModel()
    var body: some View {
        
        NavigationStack(path: $coordinator.path) {
                ZStack {
                    // Background Color
                    LinearGradient(colors: [
                        Color.white,
                        Color(red: 0.97, green: 0.97, blue: 1.0)
                    ], startPoint: .top, endPoint: .bottom)
                    .ignoresSafeArea()
                    
                    // Top Right Decorative Image
                    VStack {
                        HStack {
                            Spacer()
                            Image("Group 1000003142")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 280) // Adjust according to asset
                                .padding(.top, 0)
                                .padding(.trailing, -40)
                        }
                        Spacer()
                    }
                    .ignoresSafeArea()
                    
                    // Bottom Left Decorative Image
                    VStack {
                        Spacer()
                        HStack {
                            Image("Group 1000003140")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 280)
                                .padding(.leading, -40)
                            Spacer()
                        }
                    }
                    .ignoresSafeArea()
                    // Center Logo + Text
                    VStack(spacing: 20) {
                        Image("FinalLogo")  // CureMeGPT logo
                            .resizable()
                            .scaledToFit()
                            .frame(width: 207)
                            .frame(height: 45)
                    }
                }
            .navigationDestination(for: Route.self) { route in
                switch route {
                case .tabBarView:
                    MainTabContainer()
                        .navigationBarBackButtonHidden()
                case .OnboardingView:
                    OnboardingView()
                        .navigationBarBackButtonHidden()
                case .login:
                    LoginView()
                        .navigationBarBackButtonHidden()
                case .resetPasswordView:
                    ResetPasswordView()
                        .navigationBarBackButtonHidden()
                    
                case .createAccountView:
                    CreateAccountView()
                        .navigationBarBackButtonHidden()
                    
                case let .verificationView(source, emailPhone):
                    VerificationView(source: source, emailPhone: emailPhone)
                        .navigationBarBackButtonHidden()
                case .setNewPasswordView(let emailPhone):
                    SetNewPasswordView(emailPhone: emailPhone)
                        .navigationBarBackButtonHidden()
                case .privacyConsentView(let flow):
                    PrivacyConsentView(flow: flow)
                        .navigationBarBackButtonHidden()
                case .completeProfileView(let flow):
                    CompleteProfileView( flow: flow)
                        .navigationBarBackButtonHidden()
                case .aboutPrivacyTermView(let data):
                    AboutPrivacyTermView(pageData: data)
                        .navigationBarBackButtonHidden()
                case .generalProfileView(let flow):
                    GeneralProfileView(flow: flow)
                        .navigationBarBackButtonHidden()
                case .historyProfileView(let flow):
                    HistoryProfileView(flow: flow)
                        .navigationBarBackButtonHidden()
                case .privacyPolicyView:
                    PrivacyPolicyView()
                        .navigationBarBackButtonHidden()
                case .termsAndConditionView:
                    TermConditionView()
                        .navigationBarBackButtonHidden()
                case .accountPrivacyView(let data):
                    AccountPrivacyVIew(data: data)
                        .navigationBarBackButtonHidden()
                case .faqView:
                    FAQScreenView()
                        .navigationBarBackButtonHidden()
                case .deleteAccReasionView:
                    DeleteAccReasonView()
                        .navigationBarBackButtonHidden()
                case .deleteAccFeedBackView:
                    DeleteAccFeedBackView()
                        .navigationBarBackButtonHidden()
                case .documentsView(let flow):
                    CompleteDocProfileView(flow: flow)
                        .navigationBarBackButtonHidden()
                case .newAppointmentScheduleView(let flow, let chatId):
                    NewAppointmentScheduleView(flow: flow, chatId: chatId)
                        .navigationBarBackButtonHidden()
                case .personProfileView:
                    PersonProfileView()
                        .navigationBarBackButtonHidden()
                case .settingView:
                    SettingsView()
                        .navigationBarBackButtonHidden()
//                case .reportDescriptionView:
//                    ReportDescriptionView(status: ReportDetail(
//                        title: "Cavity size",
//                        value: "2mm diameter",
//                        status: .attention
//                        )
//                    )
                case .reportDescriptionView(let chatID, let isFromSharedLink):
                    ReportDescriptionView(chatID: chatID, isFromSharedLink: isFromSharedLink)
                       
                        .navigationBarBackButtonHidden()
                case .needAttentionListView(let alerts):
                      NeedAttentionListView(alerts: alerts)
                        .navigationBarBackButtonHidden()
                case .addMedicationView(let flow):
                    AddMedicationView(flow: flow)
                        .navigationBarBackButtonHidden()
                    
                case .chatScreenView(let ChatID, let textNeedToSend, let memberID, let attachment, let isFromSharedLink, let memberName):
                    ChatScreenView(initialText: textNeedToSend, chatID: ChatID, memberId: memberID, memberName: memberName, selectedAttachment: attachment, isFromSharedLink: isFromSharedLink)
                        .navigationBarBackButtonHidden()
                    
                case .familyPersonDetailView:
                    FamilyPersonDetailView()
                        .navigationBarBackButtonHidden()
                case .helpSupportView(let data):
                    HelpSupportView(pageData: data)
                        .navigationBarBackButtonHidden()
                case .alertView:
                    AlertView()
                        .navigationBarBackButtonHidden()
                }
            }
        }
        .onAppear(perform: handleOnAppear)
    }
    
    func handleOnAppear() {
        let userdefault = UserDefaults.standard.string(forKey: "token") ?? ""
        
        if userdefault == "" {
            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                withAnimation {
                    if coordinator.path.isEmpty {
                        coordinator.push(.OnboardingView)
                    }
                }
            }
        } else {
            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                withAnimation {
                    if coordinator.path.isEmpty {
                        coordinator.push(.tabBarView)
                    }
                }
            }
        }
    }
}
#Preview {
    LaunchScreenView()
}


//extension ReportDetail {
//    
//    static var dummy: ReportDetail {
//        ReportDetail(
//            chatID: 0,
//            title: "",
//            userName: "",
//            familyName: nil,
//            severity: "",
//            chatDate: "",
//            summary: "",
//            detailedAnalysis: "",
//            aiInsights: nil,
//            attachments: []
//        )
//    }
//}


