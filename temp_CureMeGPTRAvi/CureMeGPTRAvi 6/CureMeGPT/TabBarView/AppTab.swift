//
//  Untitled.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 02/12/25.
//

import SwiftUI

enum AppTab: CaseIterable {
    case home, schedule, magic, family, reports

    var selectedIcon: String {
        switch self {
        case .home: return "HOME"
        case .schedule: return "SCHEDULE"
        case .magic: return "middle_tab_icon"   // your center hexagon icon
        case .family: return "FAMILY"
        case .reports: return "REPORTS"
        }
    }
    

    var unselectedIcon: String {
        switch self {
        case .home: return "home 1"
        case .schedule: return "schedule 1"
        case .magic: return "middle_tab_icon"   // same for center
        case .family: return "family 1"
        case .reports: return "reports 1"
        }
    }

    var title: String {
        switch self {
        case .home: return "HOME"
        case .schedule: return "SCHEDULE"
        case .magic: return ""
        case .family: return "FAMILY"
        case .reports: return "REPORTS"
        }
    }
}

class TabViewModel: ObservableObject {
    @Published var selectedTab: AppTab = .home
    @Published var isTabBarHidden: Bool = false 
}
