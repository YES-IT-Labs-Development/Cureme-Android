//
//  AppsFlyerHelper.swift
//  CureMeGPT
//

import UIKit
import AppsFlyerLib

class AppsFlyerHelper {
    static let shared = AppsFlyerHelper()
    
    static let devKey = "eyUDbhV7uqoqsrG3fKPWaF"
    static let appleAppID = "6759031062"
    static let templateID = "Ehr9"
    static let domain = "share-chat.onelink.me"
    
    private init() {}
    
    /// Generates Chat Share Link in the exact user-specified OneLink structure:
    /// https://share-chat.onelink.me/Ehr9?c=chat_share&chatId=...&memberName=...&chatHistory=true&pid=share&af_channel=share&type=normal&deep_link_value=chat
    static func createChatShareLink(
        chatID: Int,
        memberID: Int? = nil,
        memberName: String? = nil,
        isCaseChat: Bool,
        chatHistory: Bool = true
    ) -> String {
        let chatType = isCaseChat ? "caseChat" : "normal"
        let encChatID = LinkEncryptionHelper.encrypt(id: chatID)
        let nameStr = memberName ?? ""
        let encMemberName = LinkEncryptionHelper.encryptString(nameStr)
        return "https://\(domain)/\(templateID)?c=chat_share&chatId=\(encChatID)&memberName=\(encMemberName)&chatHistory=\(chatHistory)&pid=share&af_channel=share&type=\(chatType)&deep_link_value=chat"
    }
    
    /// Generates Report Share Link in matching OneLink structure:
    /// https://share-chat.onelink.me/Ehr9?c=report_share&reportId=...&pid=share&af_channel=share&deep_link_value=report
    static func createReportShareLink(reportID: Int) -> String {
        let encReportID = LinkEncryptionHelper.encrypt(id: reportID)
        return "https://\(domain)/\(templateID)?c=report_share&reportId=\(encReportID)&pid=share&af_channel=share&deep_link_value=report"
    }
    
    /// Dynamically generates short OneLink URL using AppsFlyer SDK
    func generateInviteUrl(
        campaign: String = "chat_share",
        deepLinkScheme: String = "curemegpt://",
        webFallbackLink: String? = nil,
        customParams: [String: String] = [:],
        completion: @escaping (URL?) -> Void
    ) {
        AppsFlyerShareInviteHelper.generateInviteLink(linkGenerator: { generator in
            generator.setBaseDeepLink("https://\(AppsFlyerHelper.domain)/\(AppsFlyerHelper.templateID)")
            generator.setCampaign(campaign)
            generator.setChannel("share")
            generator.addParameterValue(deepLinkScheme, forKey: "af_dp")
            
            if let webFallback = webFallbackLink {
                generator.addParameterValue(webFallback, forKey: "af_web_dp")
            }
            
            for (key, value) in customParams {
                generator.addParameterValue(value, forKey: key)
            }
            
            return generator
        }, completionHandler: { url, error in
            if let error = error {
                print("AppsFlyer Link Generation error: \(error.localizedDescription)")
            }
            DispatchQueue.main.async {
                completion(url)
            }
        })
    }
}
