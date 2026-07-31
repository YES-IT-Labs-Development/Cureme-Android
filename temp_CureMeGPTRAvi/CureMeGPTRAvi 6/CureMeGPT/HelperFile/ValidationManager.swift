//
//  ValidationManager.swift
//  CureMeGPT
//
//  Created by Antigravity on 09/07/26.
//

import Foundation

class ValidationManager: ObservableObject {
    @Published var showAlert = false
    @Published var alertMessage = ""
    
    func validateFields(_ fields: [String: String]) -> Bool {
        for (fieldName, value) in fields {
            if value.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                alertMessage = "\(fieldName) cannot be empty"
                showAlert = true
                return false
            }
        }
        return true
    }
}
