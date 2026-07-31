//
//  APIManager.swift
//  Tradesman Tech
//  Created by YATIN  KALRA on 10/01/25.
//
//

import Combine
import Foundation
import Alamofire

class APIManager {
    static let shared = APIManager()
    private init() {}
//
    
//   MARK: -  CreateAccount API Call
    func createAccountAPI(fullname:String, password:String, emailPhone:String) -> AnyPublisher<BaseResponse<CreateAccountResponse>, Error> {
        let parameters: [String: Any] = [
           
            APIKeys.emailPhone : emailPhone,
            APIKeys.name : fullname,
            APIKeys.password : password,
            APIKeys.deviceType : "ios",
           
        ]
        
        return APIServices<CreateAccountResponse>()
            .post(endpoint: .signup, parameters: parameters)
            .eraseToAnyPublisher()
    }
    
    // MARK: -  Verify Account API Call
    func verifyAccountAPI(otp: String, emailPhone:String, fcmToken: String) -> AnyPublisher<BaseResponse<VerifyAccountResponse>, Error> {
            let parameters: [String: Any] = [
                APIKeys.otp: otp,
                APIKeys.emailPhone: emailPhone,
                APIKeys.fcmToken: AppDelegate.fcmToken
            ]
            
            return APIServices<VerifyAccountResponse>()
                .post(endpoint: .verify_account, parameters: parameters)
                .eraseToAnyPublisher()
        }
    
    // MARK: -  Resend OTP API Call
    func ResendOTPAPI(emailPhone:String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
            let parameters: [String: Any] = [
                APIKeys.emailPhone: emailPhone,
            ]
            
            return APIServices<EmptyModel>()
                .post(endpoint: .resend_otp, parameters: parameters)
                .eraseToAnyPublisher()
        }
    
    // MARK: -  Forgot Password API Call
    func ForgotPasswordAPI(emailPhone:String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
            let parameters: [String: Any] = [
                APIKeys.emailPhone: emailPhone,
            ]
            
            return APIServices<EmptyModel>()
                .post(endpoint: .forgot_password, parameters: parameters)
                .eraseToAnyPublisher()
        }
   
    // MARK: -  Login API Call
    func loginApi(password: String, emailPhone:String) -> AnyPublisher<BaseResponse<LoginResponse>, Error> {
        let parameters: [String: Any] = [
            APIKeys.password: password,
            APIKeys.emailPhone :emailPhone,
            APIKeys.deviceType: "ios",
            APIKeys.fcmToken: AppDelegate.fcmToken
        ]
        return APIServices<LoginResponse>()
            .post(endpoint: .login, parameters: parameters)
            .eraseToAnyPublisher()
    }

    // MARK: -  Verify Forgot Otp API Call
    func verifyForgotOtpAPI(otp: String, emailPhone:String, fcmToken: String) -> AnyPublisher<BaseResponse<VerifyAccountResponse>, Error> {
            let parameters: [String: Any] = [
                APIKeys.otp: otp,
                APIKeys.emailPhone: emailPhone,
                APIKeys.fcmToken: AppDelegate.fcmToken
            ]
            
            return APIServices<VerifyAccountResponse>()
            .post(endpoint: .verify_forgot_otp, parameters: parameters)
                .eraseToAnyPublisher()
        }
    
    // MARK: -  Update Password API Call
    func updatePasswordApi(password: String, emailPhone:String) -> AnyPublisher<BaseResponse<LoginResponse>, Error> {
        let parameters: [String: Any] = [
            APIKeys.password: password,
            APIKeys.emailPhone :emailPhone,
        ]
        return APIServices<LoginResponse>()
            .post(endpoint: .update_password, parameters: parameters)
            .eraseToAnyPublisher()
    }
    
 // MARK: -  Complete Personal Profile API Call
    func completePersonalProfileAPI(full_name: String, contact_number:String,email: String, dob:String,gender: String, height:String, weight: String, imgData:[String: Data]) -> AnyPublisher<BaseResponse<PersonalProfileData>, Error> {
        
        let parameters: [String: Any] = [
            APIKeys.full_name: full_name,
            APIKeys.contact_number : contact_number,
            APIKeys.email: email,
            APIKeys.dob: dob,
            APIKeys.gender: gender,
            APIKeys.height : height,
            APIKeys.weight: weight
        ]
        
      
        return APIServices<PersonalProfileData>()
            
            .post(endpoint: .complete_personal_profile, parameters: parameters, images: imgData)
            .eraseToAnyPublisher()
    }
    
    // MARK: -  Complete Member Personal Profile API Call
       func completeMemberPersonalProfileAPI(full_name: String, contact_number:String,email: String, dob:String,gender: String, height:String, weight: String, relation: String, imgData:[String: Data]) -> AnyPublisher<BaseResponse<FamilyMemberDataM>, Error> {
           
           let parameters: [String: Any] = [
               APIKeys.full_name: full_name,
               APIKeys.relation: relation,
               APIKeys.contact_number : contact_number,
               APIKeys.emailaddress: email,
               APIKeys.dob: dob,
               APIKeys.gender: gender,
               APIKeys.height : height,
               APIKeys.weight: weight
           ]
           
           return APIServices<FamilyMemberDataM>()
               
               .post(endpoint: .add_family_member_personal, parameters: parameters, images: imgData)
               .eraseToAnyPublisher()
       }
       

    // MARK: -  update Member Personal Profile API Call
       func updateMemberPersonalProfileAPI(family_member_id: String,full_name: String, contact_number:String,email: String, dob:String,gender: String, height:String, weight: String, relation: String, imgData:[String: Data]) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
           
           let parameters: [String: Any] = [
            APIKeys.family_member_id: family_member_id,
               APIKeys.full_name: full_name,
               APIKeys.relation: relation,
               APIKeys.contact_number : contact_number,
               APIKeys.emailaddress: email,
               "dateOfBirth": dob,
               APIKeys.gender: gender,
               APIKeys.height : height,
               APIKeys.weight: weight
           ]
           
           return APIServices<EmptyModel>()
               
               .post(endpoint: .update_family_personal_profile, parameters: parameters, images: imgData)
               .eraseToAnyPublisher()
       }
    
    
    
 // MARK: -  update Personal Profile API Call
    func apiforUpdatePersonalProfile(full_name: String, contact_number:String,email: String, dob:String,gender: String, height:String, weight: String, imgData:[String: Data]) -> AnyPublisher<BaseResponse<PersonalProfileData>, Error> {
        
        let parameters: [String: Any] = [
            APIKeys.full_name: full_name,
            APIKeys.contact_number : contact_number,
            APIKeys.email: email,
            APIKeys.dob: dob,
            APIKeys.gender: gender,
            APIKeys.height : height,
            APIKeys.weight: weight
        ]
        
      
        return APIServices<PersonalProfileData>()
            
            .post(endpoint: .update_personal_profile, parameters: parameters, images: imgData)
            .eraseToAnyPublisher()
    }
    
    // MARK: -   GetPersonalProfile API Call
       func apiForGetPersonalProfile() -> AnyPublisher<BaseResponse<PersonalProfileModel>, Error> {
           
           let parameters: [String: Any] = [:]
           
           return APIServices<PersonalProfileModel>()
               
               .post(endpoint: .get_personal_profile, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    // MARK: - getMemberPersonalProfile
    func getMemberPersonalProfile(familymemberid: String) -> AnyPublisher<BaseResponse<FamilyMemberDataM>, Error> {
           
            let parameters: [String: Any] = [
            APIKeys.family_member_id: familymemberid ]
           
           return APIServices<FamilyMemberDataM>()
               
               .post(endpoint: .get_family_personal_profile, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    // MARK: - get_family_members_list
    func getfamilymemberslistAPI() -> AnyPublisher<BaseResponse<familyDetailsModel>, Error> {
           
            let parameters: [String: Any] = [
                : ]
           
           return APIServices<familyDetailsModel>()
               
               .post(endpoint: .get_family_members_list, parameters: parameters)
               .eraseToAnyPublisher()
       }
   
    
    // MARK: - getScheduleAppointmentDetails
    func getScheduleAppointmentDetails(appointment_id:String) -> AnyPublisher<BaseResponse<AppointmentDetailsModel>, Error> {
           
            let parameters: [String: Any] = [
               "appointment_id" : appointment_id]
           
           return APIServices<AppointmentDetailsModel>()
               
               .post(endpoint: .get_schedule_appointment_details, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    
//    // MARK: - getHealthReportsAPI
//    func getHealthReportsAPI() -> AnyPublisher<BaseResponse<HealthReportData>, Error> {
//        
//        let parameters: [String: Any] = [
//            :]
//           
//           return APIServices<HealthReportData>()
//               
//               .post(endpoint: .healthreport, parameters: parameters)
//               .eraseToAnyPublisher()
//       }
    
    
    // MARK: - getHealthReportsAPI

    func getHealthReportsAPI() -> AnyPublisher<BaseResponse<[HealthReportData]>, Error> {
        
        let parameters: [String: Any] = [:]
        
        return APIServices<[HealthReportData]>()
            .post(
                endpoint: .healthreport,
                parameters: parameters
            )
            .eraseToAnyPublisher()
    }
    // MARK: - mark complete appointment
    func markAsCompleteAppointmentAPI(appointment_id:String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
           
            let parameters: [String: Any] = [
               "appointment_id" : appointment_id]
           
           return APIServices<EmptyModel>()
               
               .post(endpoint: .appointment_mark_as_complete_incomplete, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    // MARK: - mark complete appointment
    func healthreportdetails(chat_id:String) -> AnyPublisher<BaseResponse<ReportDetail>, Error> {
           
            let parameters: [String: Any] = [
               "chat_id" : chat_id]
           
           return APIServices<ReportDetail>()
               
               .post(endpoint: .healthreportdetails, parameters: parameters)
               .eraseToAnyPublisher()
       }
   
   
    // MARK: - mark complete appointment
    func deteleAppointAPI(appointment_id:String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
           
            let parameters: [String: Any] = [
               "appointment_id" : appointment_id]
           
           return APIServices<EmptyModel>()
               
               .post(endpoint: .delete_appointment, parameters: parameters)
               .eraseToAnyPublisher()
       }
   
   
    
    // MARK: - get_appointment_list
    func getappointmentlistAPI() -> AnyPublisher<BaseResponse<AppointmentModelSchedule>, Error> {
           
            let parameters: [String: Any] = [
                : ]
           
           return APIServices<AppointmentModelSchedule>()
               
               .post(endpoint: .get_appointment_list, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    // MARK: - get_appointment_list
    func getMedicationListAPI() -> AnyPublisher<BaseResponse<MedicationModel>, Error> {
           
            let parameters: [String: Any] = [
                : ]
           
           return APIServices<MedicationModel>()
               
               .post(endpoint: .get_medication_list, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    // MARK: - get_appointment_list
    func family_member_listAPI() -> AnyPublisher<BaseResponse<DataClass>, Error> {
           
            let parameters: [String: Any] = [
                : ]
           
           return APIServices<DataClass>()
               
               .post(endpoint: .family_member_list, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    func sendChatAPI(
        message: String,
        type: String,
        family_member_id: Int?,
        currentChatId: Int?,
        imageData: Data?
    ) -> AnyPublisher<BaseResponse<ChatDataModel>, Error> {
        
        var parameters: [String: Any] = [
            APIKeys.message: message,
            APIKeys.type: type
        ]
        
        if let memberID = family_member_id, memberID != 0 {
            parameters["family_member_id"] = memberID
        }
        
        // ✅ Add chat_id only if available
        if let chatId = currentChatId {
            parameters["chat_id"] = chatId
        }
        
        var images: [String: Data?] = [:]
        // ✅ Only send image if exists
        if let data = imageData {
            let compressedData = compressAndResizeImage(data)
            images["file"] = compressedData
        }
        
        return APIServices<ChatDataModel>()
            .post(endpoint: .chat, parameters: parameters, images: images)
            .eraseToAnyPublisher()
    }
    
    // MARK: - get_appointment_list
    func get_family_member_profileAPI(family_member_id: String) -> AnyPublisher<BaseResponse<familyProfileData>, Error> {
           
            let parameters: [String: Any] = [
                APIKeys.famil_member_id  : family_member_id ]
           
           return APIServices<familyProfileData>()
               
               .post(endpoint: .get_family_member_profile, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    func deleteMedicationAPI(medication_id : Int) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
           
            let parameters: [String: Any] = [
                APIKeys.medication_id   : medication_id]
           
           return APIServices<EmptyModel>()
               
               .post(endpoint: .delete_medication, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    func deleteFamilyMemberAPI(family_member_id : Int) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
           
            let parameters: [String: Any] = [
                APIKeys.family_member_id   : family_member_id]
           
           return APIServices<EmptyModel>()
               
               .post(endpoint: .delete_family_member, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    func userWithFamilyDetails() -> AnyPublisher<BaseResponse<userWithFamilyDetailModels>, Error> {
           
            let parameters: [String: Any] = [
                : ]
           
           return APIServices<userWithFamilyDetailModels>()
               .post(endpoint: .user_with_family_details, parameters: parameters)
               .eraseToAnyPublisher()
       }
    

        
        func getChatMessageListAPI(
            currentChatId: Int? ) -> AnyPublisher<BaseResponse<ChatMessageListModel>, Error> {
            
            var parameters: [String: Any] = [:]
            // Add chat_id only if available
            if let chatId = currentChatId {
                parameters["chat_id"] = chatId
            }
           
           return APIServices<ChatMessageListModel>()
               .post(endpoint: .get_chat_messages, parameters: parameters)
               .eraseToAnyPublisher()
       }

        func responseLikeDislikeAPI(chatMessageId: Int, status: Int) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
            let parameters: [String: Any] = [
                "chat_message_id": chatMessageId,
                "like_dislike_status": status
            ]
            return APIServices<EmptyModel>()
                .post(endpoint: .response_like_dislike, parameters: parameters)
                .eraseToAnyPublisher()
        }
    
    func getChatList() -> AnyPublisher<BaseResponse<ChatMessageModel>, Error> {
           
            let parameters: [String: Any] = [
                : ]
           
           return APIServices<ChatMessageModel>()
               .post(endpoint: .chat_list, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    func user_family_chat_list(family_member_id : String) -> AnyPublisher<BaseResponse<ChatMessageModel>, Error> {
           
            let parameters: [String: Any] = [
                APIKeys.family_member_id  : family_member_id]
           
           return APIServices<ChatMessageModel>()
               .post(endpoint: .user_family_chat_list, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    func getPromptQuestionFamilyDetailAPI() -> AnyPublisher<BaseResponse<getPromptQuestionModel>, Error> {
           
            let parameters: [String: Any] = [
                : ]
           
           return APIServices<getPromptQuestionModel>()
               .post(endpoint: .get_prompt_questions, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    // MARK: - get_appointment_type_API
    func get_appointment_type_API() -> AnyPublisher<BaseResponse<AppointmentTypeModel>, Error> {
           
            let parameters: [String: Any] = [
                : ]
           
           return APIServices<AppointmentTypeModel>()
               
               .post(endpoint: .get_appointment_type, parameters: parameters)
               .eraseToAnyPublisher()
       }
    // MARK: -  Complete General Profile API Call
    
       func completeGeneralProfileAPI(blood_group: String, allergies:[String],other_allergy: String, emergency_name:String,emergency_phone: String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
           
           let parameters: [String: Any] = [
            APIKeys.blood_group: blood_group,
            APIKeys.allergies: allergies,
            APIKeys.other_allergy: other_allergy,
            APIKeys.emergency_name: emergency_name,
            APIKeys.emergency_phone: emergency_phone
           ]
         
           return APIServices<EmptyModel>()
               .post(endpoint: .complete_general_profile, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    // MARK: -  Create add_family_member_general API Call
    
    func addfamilymembergeneralAPI(family_member_id: String, blood_group: String, allergies:[String],other_allergy: String, emergency_name:String,emergency_phone: String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
           
           let parameters: [String: Any] = [
            APIKeys.family_member_id: family_member_id,
            APIKeys.blood_group: blood_group,
            APIKeys.known_allergies: allergies,
            APIKeys.other_allergy: other_allergy,
            APIKeys.emergency_name: emergency_name,
            APIKeys.emergency_phone: emergency_phone
           ]
         
           return APIServices<EmptyModel>()
               
               .post(endpoint: .add_family_member_general, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
   
    
    
    // MARK: -  Create schedule_appointmentAPI  Call
    
    func schedule_appointmentAPI(for_whom_id: String, appointment_type_id: String, recommended_chat_id:String,description: String, date:String,time: String,preferred_doctor: String,preferred_clinic: String,appointment_reminder: String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {

           let parameters: [String: Any] = [
            APIKeys.for_whom_id: for_whom_id,
            APIKeys.appointment_type_id: appointment_type_id,
            APIKeys.recommended_chat_id: recommended_chat_id,
            APIKeys.description: description,
            APIKeys.date: date,
            APIKeys.time: time,
            APIKeys.preferred_doctor: preferred_doctor,
            APIKeys.preferred_clinic: preferred_clinic,
            APIKeys.appointment_reminder: appointment_reminder
            
           ]
         
           return APIServices<EmptyModel>()
               
               .post(endpoint: .schedule_appointment, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    func rescheduleAppointmentAPI(appointment_id: String, for_whom_id: String, appointment_type_id: String, description: String, date: String, time: String, preferred_doctor: String, preferred_clinic: String, appointment_reminder: String, recommendedchatid: String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {

           let parameters: [String: Any] = [
            APIKeys.appointment_id: appointment_id,
            APIKeys.recommended_chat_id: recommendedchatid,
            APIKeys.for_whom_id: for_whom_id,
            APIKeys.appointment_type_id: appointment_type_id,
            APIKeys.description: description,
            APIKeys.date: date,
            APIKeys.time: time,
            APIKeys.preferred_doctor: preferred_doctor,
            APIKeys.preferred_clinic: preferred_clinic,
            APIKeys.appointment_reminder: appointment_reminder]
         
           return APIServices<EmptyModel>()
               
               .post(endpoint: .reschedule_appointment, parameters: parameters)
               .eraseToAnyPublisher()
       }

    
    // MARK: -  Create add_medication  Call
    
    func addMedicationAPI(
        for_whom_id: String,
        medication_type: String,
        medication_name: String,
        dosage: String,
        frequency: String,
        days: String,
        reminder_time: [String],
        start_date: String,
        end_date: String,
        notes: String,
        reminder_status: String,
        imageData: Data?   // ✅ optional
    ) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {

        var parameters: [String: Any] = [
            APIKeys.for_whom_id: for_whom_id,
            APIKeys.medication_type: medication_type,
            APIKeys.medication_name: medication_name,
            APIKeys.dosage: dosage,
            APIKeys.reminder_time: reminder_time,
            APIKeys.start_date: start_date,
            APIKeys.end_date: end_date,
            APIKeys.notes: notes,
            APIKeys.reminder_status: reminder_status
        ]

        if frequency == "Alternate Days" || frequency == "Alternate" {
            parameters[APIKeys.frequency] = "Alternate"
        } else {
            parameters[APIKeys.frequency] = frequency
            parameters[APIKeys.days] = days
        }
        
        var images: [String: Data?] = [:]
        // ✅ Only send image if exists
        if let data = imageData {
            let compressedData = compressAndResizeImage(data)
            images["prescription_docs"] = compressedData
        }
        return APIServices<EmptyModel>()
            .post(endpoint: .add_medication, parameters: parameters, images: images)
            .eraseToAnyPublisher()
    }
    
   // api for update Medication
    
    func updateMedicationAPI(
        
        medication_id: String,
        for_whom_id: String,
        medication_type: String,
        medication_name: String,
        dosage: String,
        frequency: String,
        days: String,
        reminder_time: [String],
        start_date: String,
        end_date: String,
        notes: String,
        reminder_status: String,
        imageData: Data?   //  optional
    ) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {

        var parameters: [String: Any] = [
            APIKeys.medication_id: medication_id,
            APIKeys.for_whom_id: for_whom_id,
            APIKeys.medication_type: medication_type,
            APIKeys.medication_name: medication_name,
            APIKeys.dosage: dosage,
            APIKeys.reminder_time: reminder_time,
            APIKeys.start_date: start_date,
            APIKeys.end_date: end_date,
            APIKeys.notes: notes,
            APIKeys.reminder_status: reminder_status
        ]

        if frequency == "Alternate Days" || frequency == "Alternate" {
            parameters[APIKeys.frequency] = "Alternate"
        } else {
            parameters[APIKeys.frequency] = frequency
            parameters[APIKeys.days] = days
        }
        
        var images: [String: Data?] = [:]
        // Only send image if exists
        if let data = imageData {
            let compressedData = compressAndResizeImage(data)
            images["prescription_docs"] = compressedData
        }
        return APIServices<EmptyModel>()
            .post(endpoint: .update_medication, parameters: parameters, images: images)
            .eraseToAnyPublisher()
    }
    
    // getMedicationDetails
    
    func getMedicationDetails(medicationID : String) -> AnyPublisher<BaseResponse<MedicationModelDetails>, Error> {
           
           let parameters: [String: Any] = [
            APIKeys.medication_id : medicationID
           ]
         
           return APIServices<MedicationModelDetails>()
               
               .post(endpoint: .get_medication_details, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    // MARK: -  update General Profile API Call
    
       func updateGeneralProfileAPI(blood_group: String, allergies:[String],other_allergy: String, emergency_name:String,emergency_phone: String) -> AnyPublisher<BaseResponse<GeneralProfileModel>, Error> {
           
           let parameters: [String: Any] = [
            APIKeys.blood_group: blood_group,
            APIKeys.known_allergies: allergies,
            APIKeys.emergency_contact_name: emergency_name,
            APIKeys.emergency_phone_number: emergency_phone
           ]
         
           return APIServices<GeneralProfileModel>()
               .post(endpoint: .update_general_profile, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    // MARK: -  update Member General Profile API Call
    
       func ApiforUpdateMemberGeneralprofile(family_member_id: String,blood_group: String, allergies:[String],other_allergy: String, emergency_name:String,emergency_phone: String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
           
           let parameters: [String: Any] = [
            APIKeys.family_member_id: family_member_id,
            APIKeys.bloodGroup: blood_group,
            APIKeys.knownAllergies: allergies,
            APIKeys.emergencyContactName: emergency_name,
            APIKeys.emergencyContactNumber: emergency_phone
           ]
         
           return APIServices<EmptyModel>()
               .post(endpoint: .update_family_general_profile, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    // MARK: -  getGeneralProfile API Call
    
       func getGeneralProfileAPI() -> AnyPublisher<BaseResponse<GeneralProfileModel>, Error> {
           
           let parameters: [String: Any] = [
            :
           ]
         
           return APIServices<GeneralProfileModel>()
               
               .post(endpoint: .get_general_profile, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    
    // MARK: -  getFamilyGeneralProfile API Call
    
    func getFamilyGeneralProfileAPI(familyMemberID : String) -> AnyPublisher<BaseResponse<MemberDataModel>, Error> {
           
           let parameters: [String: Any] = [
            APIKeys.family_member_id : familyMemberID
           ]
         
           return APIServices<MemberDataModel>()
               
               .post(endpoint: .get_family_general_profile, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    // MARK: -  Complete History Profile API
    func completeHistoryProfileAPI(chronic_condition: String, surgical_history:String,current_medications: String, current_supplements:String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
        
        let chronicArray = chronic_condition.split(separator: ",").map { String($0).trimmingCharacters(in: .whitespacesAndNewlines) }.filter { !$0.isEmpty }
        let medsArray = current_medications.split(separator: ",").map { String($0).trimmingCharacters(in: .whitespacesAndNewlines) }.filter { !$0.isEmpty }
        let suppsArray = current_supplements.split(separator: ",").map { String($0).trimmingCharacters(in: .whitespacesAndNewlines) }.filter { !$0.isEmpty }
        
        let parameters: [String: Any] = [
         APIKeys.chronic_condition: chronicArray,
         APIKeys.surgical_history: surgical_history,
         APIKeys.current_medications: medsArray,
         APIKeys.current_supplements: suppsArray
        ]
      
        return APIServices<EmptyModel>()
            
            .post(endpoint: .complete_general_profile_history, parameters: parameters)
            .eraseToAnyPublisher()
    }
 
 
 // MARK: -  Complete History Profile API Call
 func updateGeneralProfileHistoryAPI(chronic_condition: String, surgical_history:String,current_medications: String, current_supplements:String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
     
     let chronicArray = chronic_condition.split(separator: ",").map { String($0).trimmingCharacters(in: .whitespacesAndNewlines) }.filter { !$0.isEmpty }
     let medsArray = current_medications.split(separator: ",").map { String($0).trimmingCharacters(in: .whitespacesAndNewlines) }.filter { !$0.isEmpty }
     let suppsArray = current_supplements.split(separator: ",").map { String($0).trimmingCharacters(in: .whitespacesAndNewlines) }.filter { !$0.isEmpty }
     
     let parameters: [String: Any] = [
      APIKeys.chronic_condition: chronicArray,
      APIKeys.surgical_history: surgical_history,
      APIKeys.current_medications: medsArray,
      APIKeys.current_supplements: suppsArray
     ]
   
     return APIServices<EmptyModel>()
         
         .post(endpoint: .update_general_profile_history, parameters: parameters)
         .eraseToAnyPublisher()
 }
 
 // MARK: -  Complete History Profile API Call
 
    func addFamilyMemberHistoryProfileAPI(family_member_id: String,chronic_condition: String, surgical_history:String,current_medications: String, current_supplements:String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
        
        let chronicArray = chronic_condition.split(separator: ",").map { String($0).trimmingCharacters(in: .whitespacesAndNewlines) }.filter { !$0.isEmpty }
        let medsArray = current_medications.split(separator: ",").map { String($0).trimmingCharacters(in: .whitespacesAndNewlines) }.filter { !$0.isEmpty }
        let suppsArray = current_supplements.split(separator: ",").map { String($0).trimmingCharacters(in: .whitespacesAndNewlines) }.filter { !$0.isEmpty }
        
        let parameters: [String: Any] = [
         APIKeys.family_member_id: family_member_id,
         APIKeys.chroniccondition: chronicArray,
         APIKeys.surgical_history: surgical_history,
         APIKeys.current_medication: medsArray,
         APIKeys.current_supplements: suppsArray
        ]
      
        return APIServices<EmptyModel>()
            
            .post(endpoint: .add_family_member_history, parameters: parameters)
            .eraseToAnyPublisher()
    }
 
 // MARK: -  updateFamilyMemberHistoryProfile API Call
 
    func updateFamilyHistoryProfileAPI(family_member_id: String,chronic_condition: String, surgical_history:String,current_medications: String, current_supplements:String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
        
        let chronicArray = chronic_condition.split(separator: ",").map { String($0).trimmingCharacters(in: .whitespacesAndNewlines) }.filter { !$0.isEmpty }
        let medsArray = current_medications.split(separator: ",").map { String($0).trimmingCharacters(in: .whitespacesAndNewlines) }.filter { !$0.isEmpty }
        let suppsArray = current_supplements.split(separator: ",").map { String($0).trimmingCharacters(in: .whitespacesAndNewlines) }.filter { !$0.isEmpty }
        
        let parameters: [String: Any] = [
         APIKeys.family_member_id: family_member_id,
         "chronicCondition" : chronicArray,
         "surgicalHistory" : surgical_history,
         "currentMedications" : medsArray,
         "currentSupplements" : suppsArray
        ]
      
        return APIServices<EmptyModel>()
            
            .post(endpoint: .update_family_history_profile, parameters: parameters)
            .eraseToAnyPublisher()
    }
    
    
    
    func getProfileHistoryAPI() -> AnyPublisher<BaseResponse<GeneralProfileHistoryModel>, Error> {
        
        let parameters: [String: Any] = [
            :
        ]
      
        return APIServices<GeneralProfileHistoryModel>()
            
            .post(endpoint: .get_general_profile_history, parameters: parameters)
            .eraseToAnyPublisher()
    }
    
    func renameChat(chatID : String, title: String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
        
        let parameters: [String: Any] = [
            APIKeys.chat_id :chatID,
            APIKeys.title : title
        ]
      
        return APIServices<EmptyModel>()
            
            .post(endpoint: .rename_chat, parameters: parameters)
            .eraseToAnyPublisher()
    }
    
    func deleteChatAPI(chatID : Int) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
        
        let parameters: [String: Any] = [
            APIKeys.chat_id :chatID
            
        ]
      
        return APIServices<EmptyModel>()
            
            .post(endpoint: .delete_chat, parameters: parameters)
            .eraseToAnyPublisher()
    }
    
    func viewSummaryAPI(chatID: Int) -> AnyPublisher<BaseResponse<ChatSummaryModel>, Error> {
        let parameters: [String: Any] = [
            "chat_id": chatID
        ]
        return APIServices<ChatSummaryModel>()
            .post(endpoint: .view_summary, parameters: parameters)
            .eraseToAnyPublisher()
    }
    
    func getFamilyHistoryAPI(familyMemberID: String) -> AnyPublisher<BaseResponse<GeneralProfileHistoryModel>, Error> {
        
        let parameters: [String: Any] = [
            APIKeys.family_member_id: familyMemberID
        ]
      
        return APIServices<GeneralProfileHistoryModel>()
            
            .post(endpoint: .get_family_history_profile, parameters: parameters)
            .eraseToAnyPublisher()
    }
    
    // MARK: -  Complete OnboardingData API Call
    
       func apiFAQData() -> AnyPublisher<BaseResponse<faqWrapper>, Error> {
           
           let parameters: [String: Any] = [:]
            
           return APIServices<faqWrapper>()
               
               .post(endpoint: .faqs, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    // MARK: -  Complete AboutUsData API Call
    
       func apiAboutUsData() -> AnyPublisher<BaseResponse<AboutUsModel>, Error> {
           
           let parameters: [String: Any] = [:]
            
           return APIServices<AboutUsModel>()
               
               .post(endpoint: .about_us, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    // MARK: -  Complete TermCondition API Call
    
       func apiTermConditionData() -> AnyPublisher<BaseResponse<TermConditionModels>, Error> {
           
           let parameters: [String: Any] = [:]
            
           return APIServices<TermConditionModels>()
               
               .post(endpoint: .terms_condition, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    // MARK: -  Complete FAQ API Call
    
       func apiOnboardingData() -> AnyPublisher<BaseResponse<OnboardingWrapper>, Error> {
           
           let parameters: [String: Any] = [:]
            
           return APIServices<OnboardingWrapper>()
               
               .post(endpoint: .onboarding_data, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    // MARK: -  Complete Privary Policy API Call
    
       func apiPrivaryPolicy() -> AnyPublisher<BaseResponse<PrivacyPolicyModel>, Error> {
           
           let parameters: [String: Any] = [:]
            
           return APIServices<PrivacyPolicyModel>()
               
               .post(endpoint: .privacy_policy, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    // MARK: -  Complete Privary Policy API Call
    
       func apiAccountPolicy() -> AnyPublisher<BaseResponse<AccountPrivacyModel>, Error> {
           
           let parameters: [String: Any] = [:]
            
           return APIServices<AccountPrivacyModel>()
               
               .post(endpoint: .account_privacy, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    // MARK: -  Complete Privary Policy API Call
    
       func apiForSettingData() -> AnyPublisher<BaseResponse<SettingResponseData>, Error> {
           
           let parameters: [String: Any] = [:]
            
           return APIServices<SettingResponseData>()
               
               .post(endpoint: .get_all_static_pages, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    // MARK: -  Complete OnboardingData API Call
    
       func apiHelpSupport() -> AnyPublisher<BaseResponse<HelpSupportModel>, Error> {
           
           let parameters: [String: Any] = [:]
            
           return APIServices<HelpSupportModel>()
               
               .post(endpoint: .help_support, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    // MARK: -  Complete Documents Profile API Call
       func completeDocumentsProfileAPI(documents: [Data]) -> AnyPublisher<BaseResponse<PersonalProfileDataDocs>, Error> {
           
           let parameters: [String: Any] = [
               :
           ]
           
           var images: [String: Data] = [:]

               for (index, data) in documents.enumerated() {
                   images["documents[\(index)]"] = data
               }

           
           return APIServices<PersonalProfileDataDocs>()
               
               .post(endpoint: .complete_profile_documents, parameters: parameters, images: images)
               .eraseToAnyPublisher()
       }
    
    // MARK: -  addfamilymembermedicaldocuments API Call
       func addfamilymembermedicaldocumentsAPI(familyMemberId: String,documents: [Data]) -> AnyPublisher<BaseResponse<PersonalProfileDataDocs>, Error> {
           let parameters: [String: Any] = [
            APIKeys.family_member_id: familyMemberId
           ]
           var images: [String: Data] = [:]
               for (index, data) in documents.enumerated() {
                   images["medical_documents[\(index)]"] = data
               }
           return APIServices<PersonalProfileDataDocs>()
               .post(endpoint: .add_family_member_medical_documents, parameters: parameters, images: images)
               .eraseToAnyPublisher()
       }
    
    
    // MARK: - Update Profile Photo API
    func updateProfilePhotoAPI(imageData: Data) -> AnyPublisher<BaseResponse<PersonalProfileDataDocs>, Error> {
        
        let parameters: [String: Any] = [:]
        
        var images: [String: Data] = [:]
        
        // Compress Data
        let compressedData = compressAndResizeImage(imageData)
        images["profile_image"] = compressedData
        
        return APIServices<PersonalProfileDataDocs>()
            .post(
                endpoint: .update_profile_picture,
                parameters: parameters,
                images: images
            )
            .eraseToAnyPublisher()
    }
    
    
    // MARK: - delete Profile Photo API
    func deleteProfilePhotoAPI() -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
        
        let parameters: [String: Any] = [:]
        
        return APIServices<EmptyModel>()
            .post(
                endpoint: .delete_profile_photo,
                parameters: parameters
            )
            .eraseToAnyPublisher()
    }
    
    
    // MARK: - delete family Profile Photo API
    func deleteFamilyProfilePhotoAPI(family_member_id: String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
        
        let parameters: [String: Any] = [
            APIKeys.family_member_id:family_member_id]
        
        return APIServices<EmptyModel>()
            .post(
                endpoint: .delete_family_profile_photo,
                parameters: parameters
            )
            .eraseToAnyPublisher()
    }
    
    // MARK: - Update Profile Photo API
    func updateMemberProfilePhotoAPI(family_member_id: Int, imageData: Data) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
        
        let parameters: [String: Any] = [
            APIKeys.family_member_id:family_member_id]
        
        var images: [String: Data] = [:]
        
        // Compress Data
        let compressedData = compressAndResizeImage(imageData)
        images["profile_image"] = compressedData
        
        return APIServices<EmptyModel>()
            .post(
                endpoint: .update_family_profile_picture,
                parameters: parameters,
                images: images
            )
            .eraseToAnyPublisher()
    }
    

    
    
    // MARK: -  update Documents Profile API Call
    func updateFamilyDocumentAPI(
        documents: [Data],family_member_id: String,
        existingDocuments: [String]
    ) -> AnyPublisher<BaseResponse<PersonalProfileDataDocs>, Error> {
        
        let parameters: [String: Any] = [ APIKeys.family_member_id:family_member_id ]   // same as complete
        
        var images: [String: Data] = [:]

        var index = 0

        // Step 1: existing documents ko empty Data ke saath pass karo
        for path in existingDocuments {
            if let data = path.imgFullPath().toData() { // convert URL → Data
                images["medical_documents[\(index)]"] = data
                index += 1
            }
        }

        // Step 2: new documents
        for data in documents {
            images["medical_documents[\(index)]"] = data
            index += 1
        }

        return APIServices<PersonalProfileDataDocs>()
            .post(
                endpoint: .update_family_medical_documents,
                parameters: parameters,
                images: images
            )
            .eraseToAnyPublisher()
    }
    
    // MARK: -  update Documents Profile API Call
    func updateDocumentsProfileAPI(
        documents: [Data],
        existingDocuments: [String]
    ) -> AnyPublisher<BaseResponse<PersonalProfileDataDocs>, Error> {
        
        let parameters: [String: Any] = [:]   // 👈 same as complete
        
        var images: [String: Data] = [:]

        var index = 0

        // ✅ Step 1: existing documents ko empty Data ke saath pass karo
        for path in existingDocuments {
            if let data = path.imgFullPath().toData() { // 👈 convert URL → Data
                images["documents[\(index)]"] = data
                index += 1
            }
        }

        // ✅ Step 2: new documents
        for data in documents {
            images["documents[\(index)]"] = data
            index += 1
        }

        return APIServices<PersonalProfileDataDocs>()
            .post(
                endpoint: .update_profile_documents,
                parameters: parameters,
                images: images
            )
            .eraseToAnyPublisher()
    }
    
    
    // MARK: -  Complete Documents Profile API Call
       func getProfileDocumentsAPI() -> AnyPublisher<BaseResponse<DocumentModelData>, Error> {
           
           let parameters: [String: Any] = [ : ]
           
           return APIServices<DocumentModelData>()
               
               .post(endpoint: .get_profile_documents, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    // MARK: -  getfamilyDocumentAPI
    func getfamilyDocumentAPI(family_member_id: String) -> AnyPublisher<BaseResponse<DocumentModelData>, Error> {
           
           let parameters: [String: Any] = [
            APIKeys.family_member_id: family_member_id ]
           
           return APIServices<DocumentModelData>()
               
               .post(endpoint: .get_family_medical_documents, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
    // MARK: -  get_user_detailsAPI
    func getUserDetailsAPI() -> AnyPublisher<BaseResponse<UserDataModel>, Error> {
           
           let parameters: [String: Any] = [
            : ]
           
           return APIServices<UserDataModel>()
               
               .post(endpoint: .get_user_details, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    // MARK: -  get_user_detailsAPI
    func getHomeApi() -> AnyPublisher<BaseResponse<HomeDataModel>, Error> {
           
           let parameters: [String: Any] = [
            : ]
           
           return APIServices<HomeDataModel>()
               
               .post(endpoint: .home, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    // MARK: -  getNotificationData
    func getAlertDataAPI() -> AnyPublisher<BaseResponse<[AlertData]>, Error> {
           
           let parameters: [String: Any] = [
            : ]
           
           return APIServices<[AlertData]>()
               
               .post(endpoint: .get_user_alerts, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    
  

    // MARK: - api for verify Email/Phone
       func apiforverifyEmailPhone(emailPhone: String) -> AnyPublisher<BaseResponse<VerificationModelEmailPhone>, Error> {
           
           let parameters: [String: Any] = [
               APIKeys.emailPhone: emailPhone
           ]
           
//           //  Authorization Token
//               let token = UserDefaults.standard.string(forKey: "token") ?? ""
//                print(token,"token")
//               print(parameters,"parameters")
//               let headers: [String: String] = [
//                   "Authorization": "Bearer \(token)",
//                   "Accept": "application/json"
//               ]
           return APIServices<VerificationModelEmailPhone>()
               
               .post(endpoint: .sendOTPEmailPhone, parameters: parameters)
               .eraseToAnyPublisher()
       }
    
    

//    // MARK: -  Login API Call
//    func redeemAPI(facilityId: String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
//        let parameters: [String: Any] = [
//            APIKeys.facilityid: facilityId
//          
//        ]
//        return APIServices<EmptyModel>()
//            .post(endpoint: .redeem, parameters: parameters)
//            .eraseToAnyPublisher()
//    }

//    // MARK: -  Login API Call
//    func subscriptionPlanAPI() -> AnyPublisher<BaseResponse<SubscriptionModel>, Error> {
//        let parameters: [String: Any] = [
//            :
//        ]
//        return APIServices<SubscriptionModel>()
//            .post(endpoint: .subscriptionplans, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    // MARK: -  forgotPassword API Call
//    func forgotPasswordAPI(email_or_phone:String,type: Int) -> AnyPublisher<BaseResponse<ForgotPasswordModel>, Error> {
//        let parameters: [String: Any] = [
//            APIKeys.email_or_phone :email_or_phone,
//            APIKeys.type  :type
//        ]
//        return APIServices<ForgotPasswordModel>()
//            .post(endpoint: .forgotpassword, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: -  forgotPassword API Call
//    func sendOTP(email_or_phone:String,type: Int) -> AnyPublisher<BaseResponse<ForgotPasswordModel>, Error> {
//        let parameters: [String: Any] = [
//            APIKeys.email_or_phone :email_or_phone,
//            APIKeys.type  :type
//        ]
//        return APIServices<ForgotPasswordModel>()
//            .post(endpoint: .forgotpassword, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: -  forgotPassword API Call
//    func logOUTAPI() -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
//        let parameters: [String: Any] = [:]
//        return APIServices<EmptyModel>()
//            .post(endpoint: .logout, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: -  Login API Call
//    func profileOTPVerifyAPI(email_or_phone:String,OTP:String,type: Int) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
//        let parameters: [String: Any] = [
//            APIKeys.email_or_phone :email_or_phone,
//            APIKeys.otp :OTP,
//            APIKeys.type : type
//        ]
//        return APIServices<EmptyModel>()
//            .post(endpoint: .verifyotp, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: -  Login API Call
//    func signupOTPVerifyAPI(email_or_phone:String,OTP:String,password: String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
//        let parameters: [String: Any] = [
//            APIKeys.email_or_phone :email_or_phone,
//            APIKeys.otp :OTP,
//            APIKeys.password : password
//        ]
//        return APIServices<EmptyModel>()
//            .post(endpoint: .signupverifyotp, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: -  Payment API Call
//    func paymentAPI(selectedsubscriptionid:String) -> AnyPublisher<BaseResponse<PaymentModel>, Error> {
//        let parameters: [String: Any] = [
//            APIKeys.selected_subscription_id :selectedsubscriptionid
//           
//        ]
//        return APIServices<PaymentModel>()
//            .post(endpoint: .subscribe, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: -  Payment API Call
//    func PaymentDoneAPI(sessionID : String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
//        let parameters: [String: Any] = [
//            APIKeys.sessionid : sessionID
//        ]
//        return APIServices<EmptyModel>()
//            .post(endpoint: .Paymentdone, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    // MARK: -  Payment API Call
//    func cancelPlanAPI(planId:String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
//        let parameters: [String: Any] = [
//            APIKeys.planId :planId
//           
//        ]
//        return APIServices<EmptyModel>()
//            .post(endpoint: .cancelPlan, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: -  Login API Call
//    func otpVerfifyAPI(email_or_phone:String,OTP:String,type: Int) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
//        let parameters: [String: Any] = [
//            APIKeys.email_or_phone :email_or_phone,
//            APIKeys.otp :OTP,
//            APIKeys.type : type
//        ]
//        return APIServices<EmptyModel>()
//            .post(endpoint: .verifyotp, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    // MARK: -  CreatePassword API Call
//    func CreatePasswordAPI(emailphone: String, password: String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
//        let parameters: [String: Any] = [
//            APIKeys.password: password,
//            APIKeys.email_or_phone :emailphone
//        ]
//        return APIServices<EmptyModel>()
//            .post(endpoint: .createpassword, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: -  PrivacyPolicy API Call
//    func AboutUsApiCall() -> AnyPublisher<BaseResponse<AboutUsModel>, Error> {
//        let parameters: [String: Any] = [:]
//        
//        return APIServices<AboutUsModel>()
//            .get(endpoint: .aboutus, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: -  FAQ API Call
//    func FAQApiCall() -> AnyPublisher<BaseResponse<FAQModel>, Error> {
//        let parameters: [String: Any] = [:]
//        
//        return APIServices<FAQModel>()
//            .get(endpoint: .faq, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: -  SavedFacility API Call
//    func SavedFacilityAPI() -> AnyPublisher<BaseResponse<SavedDataModel>, Error> {
//        let parameters: [String: Any] = [
//            APIKeys.id : UserDetail.shared.getUserId()
//        ]
//        return APIServices<SavedDataModel>()
//            .post(endpoint: .savedfacilities, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: -  SavedFacility API Call
//    func getFilterDataAPI() -> AnyPublisher<BaseResponse<FilterModel>, Error> {
//        let parameters: [String: Any] = [
//            :
//        ]
//        return APIServices<FilterModel>()
//            .post(endpoint: .getfilter, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: - remove Bookmark facility API Call
//    func  removeBookmarkfacility(facilityid : Int) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
//        let parameters: [String: Any] = [
//           
//            APIKeys.facilityid : facilityid
//        ]
//        return APIServices<EmptyModel>()
//            .post(endpoint: .togglesavefacility, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: - save CompareAPI  Call
//    func  saveCompareAPI(facilityid : Int,Type : String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
//        let parameters: [String: Any] = [
//            APIKeys.facilityid : facilityid,
//            APIKeys.type : Type
//        ]
//        return APIServices<EmptyModel>()
//            .post(endpoint: .savecompare, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: -CompareAPI Call
//    func  CompareAPI(page:Int) -> AnyPublisher<BaseResponse<CompareFacilityModel>, Error> {
//        let parameters: [String: Any] = [ APIKeys.page : page ]
//        return APIServices<CompareFacilityModel>()
//            .post(endpoint: .compare, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    //   MARK: -  Home API Call
//    func faclityListingAPI(lat:String,long:String,page:Int,city:String,priceFrom:String,priceTo:String,minSqft:String?,maxSqft:String?,type:String,status:String) -> AnyPublisher<BaseResponse<FeaturedListModel>, Error> {
//        let parameters: [String: Any] = [APIKeys.lat : lat, APIKeys.long : long,APIKeys.page : page, APIKeys.city : city, APIKeys.priceFrom : priceFrom, APIKeys.priceTo : priceTo, APIKeys.type : type,APIKeys.status : status,APIKeys.minSqft : minSqft ?? "", APIKeys.maxSqft : maxSqft ?? ""]
//        return APIServices<FeaturedListModel>()
//            .post(endpoint: .facilitylist, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    func getHomeDataAPI() -> AnyPublisher<BaseResponse<HomeDataModel>, Error> {
//        let parameters: [String: Any] =  [:]
//        
//        return APIServices<HomeDataModel>()
//            .post(endpoint: .homeapi, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    
//    func getFacilityDetailsAPI(facilityid : Int) -> AnyPublisher<BaseResponse<FacilityDetails>, Error> {
//        let parameters: [String: Any] =  [
//            APIKeys.facilityid : facilityid]
//        return APIServices<FacilityDetails>()
//            .post(endpoint: .facilitydetail, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//  
//    // MARK: -  Notification API Call
//    func myProfileApiCall() -> AnyPublisher<BaseResponse<getProfileModel>, Error> {
//        let parameters: [String: Any] = [
//            APIKeys.id: UserDetail.shared.getUserId()
//        ]
//        
//        return APIServices<getProfileModel>()
//            .post(endpoint: .myProfile, parameters: parameters)
//            .eraseToAnyPublisher()
//    }
//    
//    // MARK: -  Notification API Call
//        func deleteAccountAPI() -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
//            let parameters: [String: Any] = [:]
//    
//            return APIServices<EmptyModel>()
//                .post(endpoint: .deleteAccount, parameters: parameters)
//                .eraseToAnyPublisher()
//        }
//    
//    // MARK: -  Update Profile API Call
//    
//    //        "latitude": lat,
//    //               "longitude": long
//    
//    //    func updateProfileApi(name: String,email:String,phoneNum:String, location: String,latitude: String,longitude: String, imgData:[String: Data]) ->
//    AnyPublisher<BaseResponse<getProfileModel>, Error> {
//        let userid = UserDetail.shared.getUserId()
//        let parameters: [String: Any] = [
//            APIKeys.id: userid,
//            APIKeys.name: name,
//            APIKeys.phone: phoneNum,
//            APIKeys.location: location,
//            APIKeys.email : email,
//            APIKeys.lat : latitude,
//            APIKeys.long : longitude
//        ]
//        return APIServices<getProfileModel>()
//            .post(endpoint: .editprofile, parameters: parameters, images: imgData)
//            .eraseToAnyPublisher()
//    }
//    
    // MARK: - Delete Account API Call
    func deleteAccountAPI(feedback: String) -> AnyPublisher<BaseResponse<EmptyModel>, Error> {
        let parameters: [String: Any] = [
            APIKeys.delete_account_feedback: feedback
        ]
        
        return APIServices<EmptyModel>()
            .post(endpoint: .delete_account, parameters: parameters)
            .eraseToAnyPublisher()
    }
}



extension String {
    func toData() -> Data? {
        guard let url = URL(string: self) else { return nil }
        var request = URLRequest(url: url)
        // Set User-Agent to bypass Mod_Security block
        request.setValue("Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148", forHTTPHeaderField: "User-Agent")
        
        let semaphore = DispatchSemaphore(value: 0)
        var resultData: Data? = nil
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            if let httpResponse = response as? HTTPURLResponse,
               (200...299).contains(httpResponse.statusCode),
               let mimeType = httpResponse.mimeType,
               !mimeType.contains("text/html") {
                resultData = data
            }
            semaphore.signal()
        }
        task.resume()
        _ = semaphore.wait(timeout: .now() + 15)
        
        return resultData
    }
}


func compressAndResizeImage(_ data: Data, maxSizeKB: Int = 500) -> Data {
    
    // Skip compression if the data is a PDF
    let isPDF = data.count >= 4 && data.prefix(4) == Data([0x25, 0x50, 0x44, 0x46]) // %PDF
    if isPDF {
        return data
    }
    
    guard let image = UIImage(data: data) else { return data }
    
    // 🔹 Resize
    let maxWidth: CGFloat = 800
    let scale = maxWidth / image.size.width
    let newSize = CGSize(width: image.size.width * scale, height: image.size.height * scale)
    
    UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
    image.draw(in: CGRect(origin: .zero, size: newSize))
    let resizedImage = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    
    guard let finalImage = resizedImage else { return data }
    
    // 🔹 Compress
    var compression: CGFloat = 0.9
    let maxBytes = maxSizeKB * 1024
    var finalData = finalImage.jpegData(compressionQuality: compression) ?? data
    
    while finalData.count > maxBytes && compression > 0.1 {
        compression -= 0.1
        if let newData = finalImage.jpegData(compressionQuality: compression) {
            finalData = newData
        }
    }
    
    return finalData
}
