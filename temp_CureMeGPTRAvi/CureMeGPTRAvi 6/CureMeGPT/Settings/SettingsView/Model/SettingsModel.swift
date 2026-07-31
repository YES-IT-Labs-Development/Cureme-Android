//
//  SettingsModel.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 24/11/25.
//

import SwiftUI

struct SettingsModel: Identifiable {
    let id = UUID()
    let icon: String
    let title: String
    let isToggle: Bool
    var toggleValue: Bool = false
    let route: Route?
}
