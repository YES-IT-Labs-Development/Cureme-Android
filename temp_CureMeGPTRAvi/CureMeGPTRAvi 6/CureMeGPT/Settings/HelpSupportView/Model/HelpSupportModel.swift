//
//  HelpSupportModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 05/12/25.
//

// MARK: - WelcomeData
struct HelpSupportModel: Codable {
    let data: HelpSupportData?
}

// MARK: - DataData
struct HelpSupportData: Codable {
    let id: Int?
    let title, content, status, slug: String?
    let createdAt, updatedAt: String?

    enum CodingKeys: String, CodingKey {
        case id, title, content, status, slug
        case createdAt = "created_at"
        case updatedAt = "updated_at"
    }
}
