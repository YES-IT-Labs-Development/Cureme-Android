//
//  NeedAttentionListModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/12/25.
//

import Foundation

struct NeedAttentionListModel: Identifiable {
    let id = UUID()
    let title: String
    let patientName: String
    let isCritical: Bool
}
