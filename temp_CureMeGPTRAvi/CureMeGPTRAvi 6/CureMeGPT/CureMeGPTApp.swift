//
//  CureMeGPTApp.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 28/11/25.
//

import SwiftUI
import AppsFlyerLib

@main
struct CureMeGPTApp: App {
    @StateObject var coordinator = Coordinator()
    
    @UIApplicationDelegateAdaptor(AppDelegate.self)
       var delegate
    
    init() {
        UIApplication.enableGlobalDoneButton()
        UIScrollView.appearance().bounces = false
        UIScrollView.appearance().alwaysBounceVertical = false
        UIScrollView.appearance().alwaysBounceHorizontal = false
    }
    
    var body: some Scene {
        WindowGroup {
            ZStack {
                LaunchScreenView()
                    .environmentObject(coordinator)   // APPLY HERE
                
                if let payload = coordinator.activeNotificationPopup {
                    Color.black.opacity(0.4)
                        .ignoresSafeArea()
                        .onTapGesture {
                            coordinator.activeNotificationPopup = nil
                        }
                        .transition(.opacity)
                    
                    InAppNotificationPopupContainerView(payload: payload) {
                        coordinator.activeNotificationPopup = nil
                    }
                    .transition(.scale.combined(with: .opacity))
                    .zIndex(99999)
                }
            }
            .animation(.easeInOut(duration: 0.3), value: coordinator.activeNotificationPopup)
            .onOpenURL { url in
                print("App opened with URL: \(url.absoluteString)")
                AppsFlyerLib.shared().handleOpen(url)
                if let target = parseDeepLink(from: url) {
                    handleDeepLinkTarget(target)
                }
            }
            .onContinueUserActivity(NSUserActivityTypeBrowsingWeb) { userActivity in
                print("App continued user activity: \(userActivity.webpageURL?.absoluteString ?? "")")
                AppsFlyerLib.shared().continue(userActivity, restorationHandler: nil)
                if let url = userActivity.webpageURL, let target = parseDeepLink(from: url) {
                    handleDeepLinkTarget(target)
                }
            }
            .onReceive(NotificationCenter.default.publisher(for: NSNotification.Name("AppsFlyerDeepLinkReceived"))) { notification in
                if let userInfo = notification.userInfo {
                    print("Received AppsFlyer Deep Link event in SwiftUI:", userInfo)
                    handleIncomingDeepLink(userInfo: userInfo)
                }
            }
            .onReceive(NotificationCenter.default.publisher(for: NSNotification.Name("DidReceiveForegroundNotification"))) { notification in
                if let userInfo = notification.userInfo {
                    var type = ""
                    var title = ""
                    var message = ""
                    var caution: String? = nil
                    
                    // Parse from data payload or check if JSON string / dictionary
                    if let data = userInfo["data"] as? [String: Any] {
                        type = data["type"] as? String ?? ""
                        title = data["title"] as? String ?? ""
                        message = data["message"] as? String ?? ""
                        caution = data["caution"] as? String ?? data["instructions"] as? String
                    } else if let dataStr = userInfo["data"] as? String,
                              let dataObj = try? JSONSerialization.jsonObject(with: Data(dataStr.utf8), options: []) as? [String: Any] {
                        type = dataObj["type"] as? String ?? ""
                        title = dataObj["title"] as? String ?? ""
                        message = dataObj["message"] as? String ?? ""
                        caution = dataObj["caution"] as? String ?? dataObj["instructions"] as? String
                    } else {
                        // Flat payload checks
                        type = userInfo["type"] as? String ?? ""
                        title = userInfo["title"] as? String ?? ""
                        message = userInfo["message"] as? String ?? ""
                        caution = userInfo["caution"] as? String ?? userInfo["instructions"] as? String
                    }
                    
                    // Fallback to aps alert
                    if title.isEmpty || message.isEmpty {
                        if let aps = userInfo["aps"] as? [String: Any],
                           let alert = aps["alert"] as? [String: Any] {
                            if title.isEmpty { title = alert["title"] as? String ?? "" }
                            if message.isEmpty { message = alert["body"] as? String ?? "" }
                        }
                    }
                    
                    if !type.isEmpty && (!title.isEmpty || !message.isEmpty) {
                        if type == "appointment" || type == "medication" {
                            coordinator.activeNotificationPopup = InAppNotificationPayload(
                                type: type,
                                title: title,
                                message: message,
                                caution: caution
                            )
                        }
                    }
                }
            }
            .onReceive(NotificationCenter.default.publisher(for: Notification.Name("logout"))) { _ in
                // Clear all saved details from UserDefaults
                UserDetail.shared.clearAllSavedDetails()
                // Reset coordinator path to login
                coordinator.logoutAndGoToLogin()
            }
        }
    }
    
    // MARK: - Deep Link Parsing & Routing Helpers
    private enum DeepLinkTarget {
        case report(id: Int)
        case chat(id: Int, memberID: Int?, memberName: String?)
    }
    
    private func parseDeepLink(from url: URL) -> DeepLinkTarget? {
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: false) else { return nil }
        let queryItems = components.queryItems ?? []
        
        var reportID: Int? = nil
        var chatID: Int? = nil
        var memberID: Int? = nil
        var memberName: String? = nil
        var deepLinkValue: String? = nil
        
        for item in queryItems {
            let name = item.name.lowercased()
            let val = item.value ?? ""
            if ["reportid", "report_id"].contains(name), let id = LinkEncryptionHelper.decrypt(string: val) {
                reportID = id
            } else if ["chatid", "chat_id"].contains(name), let id = LinkEncryptionHelper.decrypt(string: val) {
                chatID = id
            } else if ["memberid", "member_id", "family_member_id", "familymemberid"].contains(name), let id = LinkEncryptionHelper.decrypt(string: val) {
                memberID = id
            } else if ["membername", "member_name", "family_member_name", "familymembername"].contains(name) {
                memberName = LinkEncryptionHelper.decryptString(val) ?? val
            } else if name == "deep_link_value" {
                deepLinkValue = val.lowercased()
            }
        }
        
        let scheme = url.scheme?.lowercased() ?? ""
        let host = url.host?.lowercased() ?? ""
        
        if scheme == "curemegpt" {
            if host == "report" || url.path.contains("report") {
                if let rID = reportID, rID > 0 { return .report(id: rID) }
            }
            if host == "chat" || url.path.contains("chat") {
                if let cID = chatID, cID > 0 { return .chat(id: cID, memberID: memberID, memberName: memberName) }
            }
        }
        
        if deepLinkValue == "report" {
            if let rID = reportID, rID > 0 { return .report(id: rID) }
        }
        
        if deepLinkValue == "chat" {
            if let cID = chatID, cID > 0 { return .chat(id: cID, memberID: memberID, memberName: memberName) }
        }
        
        let pathStr = host + url.path
        if pathStr.contains("chat") && !pathStr.contains("report") {
            if let cID = chatID, cID > 0 { return .chat(id: cID, memberID: memberID, memberName: memberName) }
            if let last = Int(url.lastPathComponent) ?? LinkEncryptionHelper.decrypt(string: url.lastPathComponent), last > 0 { return .chat(id: last, memberID: memberID, memberName: memberName) }
        }
        
        if let rID = reportID, rID > 0 {
            return .report(id: rID)
        }
        
        if let cID = chatID, cID > 0 {
            return .chat(id: cID, memberID: memberID, memberName: memberName)
        }
        
        return nil
    }
    
    private func handleDeepLinkTarget(_ target: DeepLinkTarget) {
        switch target {
        case .report(let id):
            navigateToReportDescription(reportID: id)
        case .chat(let id, let memberID, let memberName):
            navigateToChatScreen(chatID: id, memberID: memberID, memberName: memberName)
        }
    }
    
    private func navigateToReportDescription(reportID: Int) {
        print("🚀 DeepLink parsed! Navigating to Health Report description for ID: \(reportID)")
        DispatchQueue.main.async {
            coordinator.selectedAppTab = .reports
            coordinator.selectedTab = 3
            
            let route = Route.reportDescriptionView(chatID: reportID, isFromSharedLink: true)
            
            if !coordinator.path.contains(.tabBarView) {
                coordinator.path = [.tabBarView, route]
            } else {
                if let tabBarIndex = coordinator.path.firstIndex(of: .tabBarView) {
                    coordinator.path = Array(coordinator.path.prefix(through: tabBarIndex))
                } else {
                    coordinator.path = [.tabBarView]
                }
                coordinator.push(route)
            }
        }
    }
    
    private func navigateToChatScreen(chatID: Int, memberID: Int? = nil, memberName: String? = nil) {
        print("🚀 DeepLink parsed! Navigating to ChatScreen for chatID: \(chatID), memberID: \(memberID ?? 0), memberName: \(memberName ?? "")")
        DispatchQueue.main.async {
            coordinator.selectedAppTab = .magic
            coordinator.selectedTab = 2
            
            let route = Route.chatScreenView(chatId: chatID, initialText: "", memberId: memberID, nil, isFromSharedLink: true, memberName: memberName)
            
            if !coordinator.path.contains(.tabBarView) {
                coordinator.path = [.tabBarView, route]
            } else {
                if let tabBarIndex = coordinator.path.firstIndex(of: .tabBarView) {
                    coordinator.path = Array(coordinator.path.prefix(through: tabBarIndex))
                } else {
                    coordinator.path = [.tabBarView]
                }
                coordinator.push(route)
            }
        }
    }
    
    private func handleIncomingDeepLink(userInfo: [AnyHashable: Any]) {
        var reportID: Int? = nil
        var chatID: Int? = nil
        var memberID: Int? = nil
        var memberName: String? = nil
        
        if let clickEvent = userInfo["clickEvent"] as? [String: Any] {
            if let val = clickEvent["reportID"] as? String, let id = LinkEncryptionHelper.decrypt(string: val) { reportID = id }
            else if let val = clickEvent["reportID"] as? Int { reportID = val }
            else if let val = clickEvent["report_id"] as? String, let id = LinkEncryptionHelper.decrypt(string: val) { reportID = id }
            else if let val = clickEvent["report_id"] as? Int { reportID = val }
            
            if let val = clickEvent["chatID"] as? String, let id = LinkEncryptionHelper.decrypt(string: val) { chatID = id }
            else if let val = clickEvent["chatID"] as? Int { chatID = val }
            else if let val = clickEvent["chat_id"] as? String, let id = LinkEncryptionHelper.decrypt(string: val) { chatID = id }
            else if let val = clickEvent["chat_id"] as? Int { chatID = val }
            
            if let val = clickEvent["family_member_id"] as? String, let id = LinkEncryptionHelper.decrypt(string: val) { memberID = id }
            else if let val = clickEvent["family_member_id"] as? Int { memberID = val }
            else if let val = clickEvent["memberID"] as? String, let id = LinkEncryptionHelper.decrypt(string: val) { memberID = id }
            else if let val = clickEvent["memberID"] as? Int { memberID = val }
            
            if let val = clickEvent["memberName"] as? String { memberName = LinkEncryptionHelper.decryptString(val) ?? val }
            else if let val = clickEvent["member_name"] as? String { memberName = LinkEncryptionHelper.decryptString(val) ?? val }
            else if let val = clickEvent["family_member_name"] as? String { memberName = LinkEncryptionHelper.decryptString(val) ?? val }
        }
        
        if reportID == nil && chatID == nil {
            let urlStrings = [
                userInfo["linkString"] as? String,
                userInfo["afDp"] as? String
            ].compactMap { $0 }
            
            for str in urlStrings {
                if let url = URL(string: str), let target = parseDeepLink(from: url) {
                    handleDeepLinkTarget(target)
                    return
                }
            }
        }
        
        if let rID = reportID, rID > 0 {
            navigateToReportDescription(reportID: rID)
        } else if let cID = chatID, cID > 0 {
            navigateToChatScreen(chatID: cID, memberID: memberID, memberName: memberName)
        }
    }
}
