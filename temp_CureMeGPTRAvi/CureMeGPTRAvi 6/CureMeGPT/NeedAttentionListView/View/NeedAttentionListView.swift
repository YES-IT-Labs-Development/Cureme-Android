//
//  NeedAttentionListView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/12/25.
//

import SwiftUI

struct NeedAttentionCardView: View {
    
    let alert: NeedAttentionListModel
    let onScheduleTap: () -> Void
    
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 6) {
                Text(alert.title)
                    .lineLimit(2)
                    .font(.custom("Urbanist-Medium", size: 16))
                    .foregroundColor(Color(hex: "#F31D1D"))
                    
               
                Text("For: \(alert.patientName)")
                    .font(.custom("Urbanist-Regular", size: 14))
                    .foregroundColor(Color(hex: "#4338CA"))
                    .padding(.top, 4)
            }
            
            Spacer()
            
            Button(action: onScheduleTap) {
                HStack(spacing: 6) {
                    Image("Schedule")
                        .resizable()
                        .frame(width: 109, height: 52)
                }
                
                .padding(.horizontal, 8)
            }
        }
        .padding()
        .frame(height: 86)
        .background(
            RoundedRectangle(cornerRadius: 30)
                .stroke(Color(Color(hex: "#F31D1D").opacity(0.4)), lineWidth: 1)
                .background(
                    RoundedRectangle(cornerRadius: 18)
                        .fill(Color(hex: "#F31D1D").opacity(0.20))
                )
        )
        .cornerRadius(30)
    }
}


struct NeedAttentionListView: View {
    @StateObject private var viewModel: NeedAttentionListViewModel
    @EnvironmentObject private var coordinator: Coordinator
    
    init(alerts: [HealthAlert]) {
        _viewModel = StateObject(wrappedValue: NeedAttentionListViewModel(healthAlerts: alerts))
    }
    
    var body: some View {
        HStack {
            Button(action: {
                coordinator.pop()
            }) {
                Image("backIcon")
                     .resizable()
                     .frame(width: 45, height: 45)
                     .padding(.leading, 10)
            }
            Text("Thing Needing Attention")
                .font(.custom("PlusJakartaSans-Medium", size: 20))
                .foregroundColor(.black)
                .padding(.leading, 10)
            
            Spacer()
        }
        .padding(.leading)
        
        Divider()
        
        ScrollView {
            VStack(spacing: 12) {
                ForEach(viewModel.alerts) { alert in
                    NeedAttentionCardView(alert: alert) {
                        coordinator.push(.newAppointmentScheduleView(flow: .new, chatId: nil))
                    }
                }
            }
            .padding()
        }
        .background(Color.white)
        .disableScrollBounce()
    }
}

#Preview {
    NeedAttentionListView(alerts: [])
}
