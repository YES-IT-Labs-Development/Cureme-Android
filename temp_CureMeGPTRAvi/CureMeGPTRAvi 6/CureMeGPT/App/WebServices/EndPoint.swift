//
//  EndPoints.swift
//  RPG
//
//  Created by YATIN  KALRA on 12/02/24.
//

import Foundation

extension Notification.Name {
    static let locationDidUpdate = Notification.Name("locationDidUpdate")
}

struct AppKeys {
    static let  googleAPi = "AIzaSyC9NuN_f-wESHh3kihTvpbvdrmKlTQurxw"
    static let googleMapAPI = "AIzaSyBh1m5LWl-qV1nVkT1WZeWAzng5eP42RNk"
}
struct AppLocation {
    static var  lat = ""
    static var  long = ""
    static var  Address = ""
    static var  city = ""
    static var  state = ""
    static var  zip = ""
}
struct AppURL {
    
    static let baseURL = "https://curemegpt.tgastaging.com/api/"
    static let imageURL = "https://curemegpt.tgastaging.com/"
    
}

extension AppURL {
    
    enum Endpoint: String {
        
        case signup                           = "signup" //1
        case verify_account                   = "verify_account" //2
        case login                            = "login" //3
        case resend_otp                       = "resend_otp" //4
        case forgot_password                  = "forgot_password" //5
        case verify_forgot_otp                = "verify_forgot_otp"//6
        case update_password                  = "update_password"//7
        case complete_personal_profile        = "complete_personal_profile"//8
        case get_personal_profile             = "get_personal_profile"//9
        case update_personal_profile          = "update_personal_profile"//10
        case complete_general_profile         = "complete_general_profile"//11
        case get_general_profile              = "get_general_profile"//12
        case update_general_profile           = "update_general_profile"//13
        case add_family_member_personal       = "add_family_member_personal"//14
        case get_family_personal_profile      = "get_family_personal_profile"//15
        case add_family_member_general        = "add_family_member_general"//16
        case update_family_personal_profile   = "update_family_personal_profile"//17
        case sendOTPEmailPhone                = "verify_email_phone"//18
        case complete_general_profile_history = "complete_general_profile_history"//19
        case complete_profile_documents       = "complete_profile_documents"//20
        case onboarding_data                  = "onboarding_data"//21
        case help_support                     = "help_support"//22
        case faqs                             = "faqs"//23
        case about_us                         = "about_us"//24
        case terms_condition                  = "terms_condition"//25
        case privacy_policy                   = "privacy_policy"//26
        case account_privacy                  = "account_privacy"//27
        case update_family_general_profile    = "update_family_general_profile"//28
        case get_family_general_profile       = "get_family_general_profile"//29
        case get_today_mood                   = "get_today_mood"
        case update_general_profile_history   = "update_general_profile_history"//30
        case add_family_member_history        = "add_family_member_history"//31
        case get_profile_documents            = "get_profile_documents"//32
        case update_family_history_profile    = "update_family_history_profile"//33
        case get_family_history_profile       = "get_family_history_profile"
        case get_family_medical_documents    = "get_family_medical_documents"//34
        case get_general_profile_history    = "get_general_profile_history"//35
        case add_family_member_medical_documents    = "add_family_member_medical_documents"//36
        case update_family_medical_documents    = "update_family_medical_documents"//37
        case update_profile_documents    = "update_profile_documents"//38
        case get_user_details    = "get_user_details"//39
       
        case update_profile_picture    = "update_profile_picture"//40
        
        case get_family_members_list    = "get_family_members_list"//41
        
        case get_appointment_type    = "get_appointment_type"//42
        
        case schedule_appointment    = "schedule_appointment"//43
        
        case get_appointment_list    = "get_appointment_list"//44
        
        case get_schedule_appointment_details    = "get_schedule_appointment_details"//45
        
        case reschedule_appointment    = "reschedule_appointment"//46
        
        case appointment_mark_as_complete_incomplete    = "appointment_mark_as_complete_incomplete"//47
        
        case delete_appointment    = "delete_appointment"//48
        
        case add_medication    = "add_medication"//49
        
        case get_medication_list    = "get_medication_list"//50
        
        case get_medication_details    = "get_medication_details"//51
        
        case update_medication    = "update_medication"//52
        
        case delete_medication    = "delete_medication"//53
        
        case family_member_list    = "family_member_list"//54
        
        case get_family_member_profile    = "get_family_member_profile"//55
        
        case delete_family_member    = "delete_family_member"//56
        
        case update_family_profile_picture    = "update_family_profile_picture"//57
        
        case user_with_family_details    = "user_with_family_details"//58
        
        case chat    = "chat"//59
        
        case get_prompt_questions    = "get_prompt_questions"//60
        
        case chat_list    = "user_chat_list"//61
        
        case rename_chat    = "rename_chat"//62
        
        case get_chat_messages    = "get_chat_messages"//63
        
        case delete_chat    = "delete_chat"//64
        
        case response_like_dislike = "response_like_dislike"
        case view_summary = "view_summary"
        
        
        case user_family_chat_list    = "user_family_chat_list"//65
        
        case home    = "home"//65
        
        case healthreport    = "health_report"//65
        
        case healthreportdetails    = "health_report_details"//65
        
        
        case get_user_alerts    = "get_user_alerts"//65
        
        case delete_profile_photo    = "delete_profile_photo"//40
        
        case delete_family_profile_photo    = "delete_family_profile_photo"//40
        
        
        case get_all_static_pages    = "get_all_static_pages"//40
        
        case delete_account                   = "delete_account"
        
        
        
        
        
        
        var path: String {
            let url = AppURL.baseURL
            return url + self.rawValue
        }
    }
}
extension String {
    func imgFullPath() -> String {
        if self.hasPrefix("http://") || self.hasPrefix("https://") {
            return self
        }
        return "\(AppURL.imageURL)\(self)"
    }
}
