//
//  HistoryProfileModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 04/12/25.
//

import Foundation

struct HistoryProfileModel: Identifiable {
    let id = UUID()
    var medications: [String] = [""]
    var chronicConditions: [String] = []
    var surgicalHistory: String = ""
    var supplements: [String] = [""]
}




struct GeneralProfileHistoryModel: Codable {
    var chronicCondition: [String]?
    var surgicalHistory: String?
    var currentMedications: [String]?
    var currentSupplements: [String]?

    enum CodingKeys: String, CodingKey {
        case chronicCondition = "chronic_condition"
        case surgicalHistory = "surgical_history"
        case currentMedications = "current_medications"
        case currentSupplements = "current_supplements"
        case data = "data"
    }

    enum FamilyCodingKeys: String, CodingKey {
        case chronicCondition = "chronicCondition"
        case surgicalHistory = "surgicalHistory"
        case currentMedications = "currentMedications"
        case currentSupplements = "currentSupplements"
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        if container.contains(.data),
           let nestedContainer = try? container.nestedContainer(keyedBy: FamilyCodingKeys.self, forKey: .data) {
            
            chronicCondition = try nestedContainer.decodeIfPresent([String].self, forKey: .chronicCondition)
            surgicalHistory = try nestedContainer.decodeIfPresent(String.self, forKey: .surgicalHistory)
            currentMedications = try nestedContainer.decodeIfPresent([String].self, forKey: .currentMedications)
            currentSupplements = try nestedContainer.decodeIfPresent([String].self, forKey: .currentSupplements)
            
        } else {
            if let rootChronic = try? container.decodeIfPresent([String].self, forKey: .chronicCondition) {
                chronicCondition = rootChronic
            } else if let camelContainer = try? decoder.container(keyedBy: FamilyCodingKeys.self) {
                chronicCondition = try? camelContainer.decodeIfPresent([String].self, forKey: .chronicCondition)
            }
            
            if let rootSurgical = try? container.decodeIfPresent(String.self, forKey: .surgicalHistory) {
                surgicalHistory = rootSurgical
            } else if let camelContainer = try? decoder.container(keyedBy: FamilyCodingKeys.self) {
                surgicalHistory = try? camelContainer.decodeIfPresent(String.self, forKey: .surgicalHistory)
            }
            
            if let rootMeds = try? container.decodeIfPresent([String].self, forKey: .currentMedications) {
                currentMedications = rootMeds
            } else if let camelContainer = try? decoder.container(keyedBy: FamilyCodingKeys.self) {
                currentMedications = try? camelContainer.decodeIfPresent([String].self, forKey: .currentMedications)
            }
            
            if let rootSupps = try? container.decodeIfPresent([String].self, forKey: .currentSupplements) {
                currentSupplements = rootSupps
            } else if let camelContainer = try? decoder.container(keyedBy: FamilyCodingKeys.self) {
                currentSupplements = try? camelContainer.decodeIfPresent([String].self, forKey: .currentSupplements)
            }
        }
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(chronicCondition, forKey: .chronicCondition)
        try container.encode(surgicalHistory, forKey: .surgicalHistory)
        try container.encode(currentMedications, forKey: .currentMedications)
        try container.encode(currentSupplements, forKey: .currentSupplements)
    }
}
