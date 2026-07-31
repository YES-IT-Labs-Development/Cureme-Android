//
//  AccountPrivacyModel.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 24/11/25.
//

import Foundation

//struct AccountPrivacyModel {
//    var description: String
//}


// MARK: - WelcomeData
struct AccountPrivacyModel: Codable {
    let data: DataDatas?
}

// MARK: - DataData
struct DataDatas: Codable {
    let id: Int?
    let title, content, status, slug: String?
    let createdAt, updatedAt: String?

    enum CodingKeys: String, CodingKey {
        case id, title, content, status, slug
        case createdAt = "created_at"
        case updatedAt = "updated_at"
    }
}
