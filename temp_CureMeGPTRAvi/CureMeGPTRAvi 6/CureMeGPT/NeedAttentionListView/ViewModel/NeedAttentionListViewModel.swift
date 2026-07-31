//
//  NeedAttentionListViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/12/25.
//

import Foundation

final class NeedAttentionListViewModel: ObservableObject {
    
    @Published var alerts: [NeedAttentionListModel] = []
    
    init(healthAlerts: [HealthAlert] = []) {
        self.alerts = healthAlerts.map { alert in
            NeedAttentionListModel(
                title: alert.title,
                patientName: alert.subtitle,
                isCritical: alert.type == .critical
            )
        }
    }
    
    func scheduleTapped(for alert: NeedAttentionListModel) {
        print("Schedule tapped for \(alert.patientName)")
    }
}
