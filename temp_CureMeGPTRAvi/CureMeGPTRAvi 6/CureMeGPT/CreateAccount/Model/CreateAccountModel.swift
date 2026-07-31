//
//  CreateAccountModel.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 18/11/25.
//

import SwiftUI

//struct CreateAccountModel: Identifiable, Hashable, Codable {
//    let id = UUID()
//    let emailOrPhone: String?
//    let otp: String?
//}

struct CreateAccountResponse: Codable {
    let success: Bool?
    let code: Int?
    let message: String?
    let data: CreateAccountData?
}

struct CreateAccountData: Codable {
    let user: CreateUser?
}

struct CreateUser: Codable {
    let id: Int?
    let name: String?
    let email: String?
    let phone: String?
    let otp: Int?
}
