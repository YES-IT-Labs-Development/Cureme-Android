//
//  AppDelegate.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 27/05/26.
//

import UIKit
import Firebase
import FirebaseMessaging
import UserNotifications
import IQKeyboardManagerSwift
import AppsFlyerLib

class AppDelegate: NSObject,
                   UIApplicationDelegate,
                   UNUserNotificationCenterDelegate,
                   MessagingDelegate {
    
    static var fcmToken: String = ""

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        
       // IQKeyboardManager.shared.isEnabled = false

        FirebaseApp.configure()

        UNUserNotificationCenter.current().delegate = self

        let authOptions: UNAuthorizationOptions = [
            .alert,
            .badge,
            .sound
        ]

        UNUserNotificationCenter.current()
            .requestAuthorization(options: authOptions) { granted, error in
                print("Permission:", granted)
            }

        application.registerForRemoteNotifications()

        Messaging.messaging().delegate = self

        // Configure AppsFlyer SDK
        AppsFlyerLib.shared().initialize(devKey: AppsFlyerHelper.devKey, appId: AppsFlyerHelper.appleAppID)
        AppsFlyerLib.shared().delegate = self
        AppsFlyerLib.shared().deepLinkDelegate = self
        AppsFlyerLib.shared().isDebug = true

        if let url = launchOptions?[.url] as? URL {
            AppsFlyerLib.shared().handleOpen(url)
        }
        AppsFlyerLib.shared().start()

        return true
    }

    // APNS TOKEN
    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        print("APNS TOKEN:", deviceToken)
        Messaging.messaging().apnsToken = deviceToken
    }

    // FCM TOKEN
    func messaging(
        _ messaging: Messaging,
        didReceiveRegistrationToken fcmToken: String?
    ) {
        print("🔥 FCM Token:", fcmToken ?? "")
        AppDelegate.fcmToken = fcmToken ?? ""
    }

    // Open URL handling for Deep Linking
    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        AppsFlyerLib.shared().handleOpen(url)
        return true
    }

    // Universal Links / UserActivity handling
    func application(
        _ application: UIApplication,
        continue userActivity: NSUserActivity,
        restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void
    ) -> Bool {
        let appsFlyerRestorationHandler: ([Any]?) -> Void = { restoringObjects in
            restorationHandler(restoringObjects as? [UIUserActivityRestoring])
        }
        AppsFlyerLib.shared().continue(userActivity, restorationHandler: appsFlyerRestorationHandler)
        return true
    }

    // FOREGROUND
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler:
        @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        let userInfo = notification.request.content.userInfo
        print("🔥 Foreground Notification UserInfo:", userInfo)
        
        NotificationCenter.default.post(
            name: NSNotification.Name("DidReceiveForegroundNotification"),
            object: nil,
            userInfo: userInfo
        )
        
        if #available(iOS 14.0, *) {
            completionHandler([.banner, .list, .sound])
        } else {
            completionHandler([.alert, .sound])
        }
    }

    // TAP
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        print("Notification tapped")
        completionHandler()
    }
}

// MARK: - AppsFlyerLibDelegate & AppsFlyerDeepLinkDelegate
extension AppDelegate: AppsFlyerLibDelegate, AppsFlyerDeepLinkDelegate {
    
    func onConversionDataSuccess(_ conversionInfo: [AnyHashable: Any]) {
        print("AppsFlyer Conversion data received: \(conversionInfo)")
    }
    
    func onConversionDataFail(_ error: Error) {
        print("AppsFlyer Failed to retrieve conversion data: \(error.localizedDescription)")
    }
    
    func didResolveDeepLink(_ result: DeepLinkResult) {
        switch result.status {
        case .found:
            guard let deepLink = result.deepLink else { return }
            let linkString = deepLink.clickEvent["link"] as? String ?? ""
            let afDp = deepLink.clickEvent["af_dp"] as? String ?? ""
            let deepLinkValue = deepLink.deeplinkValue ?? ""
            print("AppsFlyer DeepLink URL: \(linkString)")
            print("AppsFlyer DeepLink Value: \(deepLinkValue)")
            
            DispatchQueue.main.async {
                NotificationCenter.default.post(
                    name: NSNotification.Name("AppsFlyerDeepLinkReceived"),
                    object: nil,
                    userInfo: [
                        "deepLink": deepLink,
                        "linkString": linkString,
                        "afDp": afDp,
                        "deepLinkValue": deepLinkValue,
                        "clickEvent": deepLink.clickEvent
                    ]
                )
            }
        case .notFound:
            print("AppsFlyer No deep link found.")
        case .failure:
            print("AppsFlyer Error resolving deep link: \(result.error?.localizedDescription ?? "Unknown error")")
        @unknown default:
            break
        }
    }
}
