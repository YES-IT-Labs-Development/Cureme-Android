//
//  OnboardingModel.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 13/03/26.
//

// MARK: - DataClass

struct OnboardingWrapper: Codable {
    let data: [OnboardingModel]
}

struct OnboardingModel: Codable, Identifiable {

    let id: Int
    let heading: String
    let description: String
    let image: String

}
