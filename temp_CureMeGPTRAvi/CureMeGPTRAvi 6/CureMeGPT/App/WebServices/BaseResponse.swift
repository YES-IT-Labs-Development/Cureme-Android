//
//  BaseResponse.swift
//  LawCo
//
//  Created by YATIN  KALRA on 12/06/24.
//

//import Foundation
//
//struct BaseResponse<T: Decodable>: Decodable {
//    let status: Bool?
//    
//    let message, deviceType, token: String?
//    let data: T?
//    
//  
//    enum CodingKeys: String, CodingKey {
//        case status = "status"
//        case message
//        case data
//        case deviceType
//        case token
//    }
// 
//}
//
//

import Foundation

struct BaseResponse<T: Decodable>: Decodable {
    let success: Bool?
    let status: Bool?
    let code: Int?
    let message: String?
    let data: T?

    // These are not in your current response, but optional
    let deviceType: String?
    let token: String?

    enum CodingKeys: String, CodingKey {
        case success
        case status
        case code
        case message
        case data
        case deviceType
        case token
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.success = try container.decodeIfPresent(Bool.self, forKey: .success)
        self.status = try container.decodeIfPresent(Bool.self, forKey: .status)
        self.code = try container.decodeIfPresent(Int.self, forKey: .code)
        
        let rawMessage = try container.decodeIfPresent(String.self, forKey: .message)
        let validationErrors = try? container.decodeIfPresent([String: [String]].self, forKey: .data)
        if let validationErrors = validationErrors, !validationErrors.isEmpty {
            let allErrors = validationErrors.values.flatMap { $0 }
            if !allErrors.isEmpty {
                self.message = allErrors.joined(separator: "\n")
            } else {
                self.message = rawMessage
            }
        } else {
            self.message = rawMessage
        }

        // Safely decode data - if it fails (e.g. contains validation error structure), set to nil
        self.data = try? container.decodeIfPresent(T.self, forKey: .data)

        self.deviceType = try container.decodeIfPresent(String.self, forKey: .deviceType)
        self.token = try container.decodeIfPresent(String.self, forKey: .token)
    }
}

