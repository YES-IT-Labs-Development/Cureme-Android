//
//  ScheduleMedicationsModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 30/12/25.
//

import Foundation
import SwiftUI

struct ScheduleMedicationsModel: Identifiable {
    let id = UUID()
    let appID: Int
    let title: String
    let dosage: String
    let memberName: String
    
    let medicationType: String
    let frequency: String   // Weekly
    
    let days: [String]      // ["Monday", "Tuesday"]
    let times: [String]     // ["09:00 AM", "09:00 PM", "10:00 AM", "04:00 PM"]
    
    let startDate: String
    let endDate: String
    
    let note: String
    let image: String
    
    let doctorName: String
    let category: String
    let location: String
    let description: String
    
    let familyMemberID: Int?
    let medicationForWhom: String?
}

enum FilterPopupType: Identifiable {
    case appointments
    case members
    
    var id: Int {
        hashValue
    }
}
