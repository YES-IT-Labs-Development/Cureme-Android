//
//  RouteClass.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 28/11/25.
//

import SwiftUI


 
enum PrivacyConsentFlow: Hashable {
    case onboarding
    case askAI
}

enum Route: Hashable {
    case tabBarView
    case OnboardingView
    case login
    case resetPasswordView
    case createAccountView
    case verificationView(source: VerificationSource, emailPhone: String)   // Accept value
    case setNewPasswordView(emailPhone: String)
    case privacyConsentView(flow: PrivacyConsentFlow)
    case completeProfileView(flow: ProfileFlowType)
    case aboutPrivacyTermView(data: SettingData)
    case generalProfileView(flow: ProfileFlowType)
    case historyProfileView(flow: ProfileFlowType)
    case termsAndConditionView
    case privacyPolicyView
    case accountPrivacyView(data: SettingData)
    case alertView
    case faqView
    case addMedicationView(flow: MedicationFlow)
    case needAttentionListView(alerts: [HealthAlert])
    case deleteAccReasionView
    case deleteAccFeedBackView
    case chatScreenView(chatId: Int?, initialText: String, memberId: Int?, ChatAttachment?, isFromSharedLink: Bool = false, memberName: String? = nil)
    case documentsView(flow: ProfileFlowType)
    case personProfileView
    case settingView
   // case reportDescriptionView
    case reportDescriptionView(chatID: Int, isFromSharedLink: Bool = false)
    case newAppointmentScheduleView(flow: AppointmentFlow, chatId: Int? = nil)
    case familyPersonDetailView
    case helpSupportView(data: SettingData)
}
 
final class Coordinator: ObservableObject {
    @Published var selectedTab: Int = 0
    @Published var isTabBarHidden: Bool = false
    @Published var path: [Route] = []
    @Published var selectedAppTab: AppTab = .home
    
    @Published var root: Route = .tabBarView
    @Published var activeNotificationPopup: InAppNotificationPayload? = nil
    
   
    
    func push(_ route: Route) {
        path.append(route)
    }
 
    func pop() {
        self.path.removeLast()
    }
    
    func popToRoot() {
        path.removeAll()
    }
    
    func switchToTabMain(index: Int) {
        selectedTab = index
    }
    func logoutAndGoToLogin() {
        path = [.login]
        selectedTab = 0
        isTabBarHidden = false
        selectedAppTab = .home
        activeNotificationPopup = nil
    }
//
    func popToHome() {
        path = [.tabBarView]
    }
 
}

struct InAppNotificationPayload: Identifiable, Hashable {
    let id: UUID = UUID()
    let type: String       // "appointment" or "medication"
    let title: String
    let message: String
    let caution: String?   // optional caution/instructions
}

enum VerificationType: Hashable {
    case phone(number: String)
    case email(address: String)
    
}
struct Reservation: Hashable {
    var dateTime: String
    var guestCount: Int
    var specialRequest: String
}
