//
//  AskQuestionsModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 05/12/25.
//

import SwiftUI


struct faqWrapper: Codable {
    let data: [FAQItem]
}


struct FAQItem: Codable, Identifiable {
    let id: Int?
    let heading: String
    let answer: String
    var isExpanded: Bool = false

    enum CodingKeys: String, CodingKey {
        case id
        case heading = "question"
        case answer
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        id = try container.decodeIfPresent(Int.self, forKey: .id)
        heading = try container.decodeIfPresent(String.self, forKey: .heading) ?? ""
        answer = try container.decodeIfPresent(String.self, forKey: .answer) ?? ""
        isExpanded = false
    }
}
