//
//  DeleteAccReasonVM.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 25/11/25.
//

import SwiftUI

class DeleteAccReasonVM: ObservableObject {
    
    @Published var reasons: [DeleteAccReasonModel] = [
        DeleteAccReasonModel(title: "I no longer use the app"),
        DeleteAccReasonModel(title: "I’m concerned about my data privacy"),
        DeleteAccReasonModel(title: "I found another app I prefer"),
        DeleteAccReasonModel(title: "The app doesn’t meet my needs"),
        DeleteAccReasonModel(title: "I want to remove all my personal data")
    ]
    
    func didSelectReason(_ reason: DeleteAccReasonModel) {
        print("Selected reason: \(reason.title)")
        // API call or navigation can be triggered here
    }
}
