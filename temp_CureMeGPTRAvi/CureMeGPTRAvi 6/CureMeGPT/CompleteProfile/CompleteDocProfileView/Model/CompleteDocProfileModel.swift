//
//  CompleteDocProfileModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/12/25.
//

import SwiftUI
import UniformTypeIdentifiers

// MARK: - Model
struct UploadedFile: Identifiable {
    let id = UUID()
    let name: String
    let typeIcon: String
    let data: Data?
    var fileURL: String?   // API se aane wale file ke liye 
}



//// MARK: - WelcomeData
//struct DocumentModel: Codable {
//    let data: DocumentModelData?
//}
//
//struct DocumentModelData: Codable, Identifiable {
//    let id: Int?
//    let medicalDocuments: [String]?
//    let Med_Docs: [String]?
//    enum CodingKeys: String, CodingKey {
//        case id
//        case Med_Docs = "medicalDocuments"
//        case medicalDocuments = "medical_documents"
//        
//    }
//}

//struct DocumentModel: Codable {
//    let data: DocumentModelData?
//}
//
//struct DocumentModelData: Codable, Identifiable {
//
//    let id: Int?
//
//    // API key: medicalDocuments
//    let medicalDocuments: [String]?
//
//    // Local optional use
//    let Med_Docs: [String]?
//
//    enum CodingKeys: String, CodingKey {
//        case id
//        case medicalDocuments
//    }
//
//    init(from decoder: Decoder) throws {
//
//        let container = try decoder.container(keyedBy: CodingKeys.self)
//
//        id = try container.decodeIfPresent(Int.self, forKey: .id)
//
//        medicalDocuments = try container.decodeIfPresent(
//            [String].self,
//            forKey: .medicalDocuments
//        )
//
//        // 👇 same value assign
//        Med_Docs = medicalDocuments
//    }
//}


struct DocumentModel: Codable {
    let data: DocumentModelData?
}

struct DocumentModelData: Codable, Identifiable {

    let id: Int?

    // Family API
    let medicalDocuments: [String]?

    // Profile API
    let medical_documents: [String]?

    // Common use
    let Med_Docs: [String]?

    enum CodingKeys: String, CodingKey {
        case id
        case medicalDocuments
        case medical_documents
    }

    init(from decoder: Decoder) throws {

        let container = try decoder.container(keyedBy: CodingKeys.self)

        id = try container.decodeIfPresent(
            Int.self,
            forKey: .id
        )

        // Family API
        medicalDocuments = try container.decodeIfPresent(
            [String].self,
            forKey: .medicalDocuments
        )

        // Profile API
        medical_documents = try container.decodeIfPresent(
            [String].self,
            forKey: .medical_documents
        )

        // Common variable
        Med_Docs = medicalDocuments ?? medical_documents
    }
}
