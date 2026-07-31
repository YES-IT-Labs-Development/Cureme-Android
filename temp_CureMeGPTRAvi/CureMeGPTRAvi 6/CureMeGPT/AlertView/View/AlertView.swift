//
//  AlertView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 31/12/25.
//

import SwiftUI

struct AlertView: View {
    
    @StateObject private var viewModel = AlertViewModel()
    @EnvironmentObject private var coordinator: Coordinator
    
    var body: some View {
        
        ZStack {
            
            VStack(spacing: 0) {
                
                // HEADER
                HStack {
                    
                    Button {
                        coordinator.pop()
                    } label: {
                        Image("backIcon")
                            .resizable()
                            .frame(width: 45, height: 45)
                    }
                    
                    Text("Alerts")
                        .font(.custom("Urbanist-Medium", size: 20))
                    
                    Spacer()
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)
                .padding(.bottom, 5)
                
                Divider()
                
                if viewModel.reminders.isEmpty && !viewModel.isLoading {

                    VStack(spacing: 12) {

                        Image(systemName: "bell.slash")
                            .font(.system(size: 50))
                            .foregroundColor(.gray)

                        Text("No Alerts Found")
                            .font(.custom("Urbanist-Medium", size: 18))
                            .foregroundColor(.gray)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .padding(.horizontal)

                }
                
             else {
                
                // ALERT LIST
                ScrollView(showsIndicators: false) {
                    
                    LazyVStack(spacing: 16) {
                        
                        ForEach(viewModel.reminders) { reminder in
                            
                            AlertListView(
                                reminder: reminder,
                                onCompleteTap: {
                                    viewModel.toggleCompletion(for: reminder)
                                },
                                onActionTap: {
                                    print("Action tapped")
                                }
                            )
                        }
                    }
                    .padding()
                }
                .disableScrollBounce()
            }
        }
            
            // LOADER
            if viewModel.isLoading {
                
                ZStack {
                    
                    Color.black.opacity(0.3)
                        .ignoresSafeArea()
                    
                    CustomLoderView(isVisible: .constant(true))
                }
                .zIndex(999)
            }
        }
    }
}

// MARK: - ALERT CELL

struct AlertListView: View {
    
    let reminder: AlertModel
    let onCompleteTap: () -> Void
    let onActionTap: () -> Void
    
    var body: some View {
        
        VStack(alignment: .leading, spacing: 12) {
            
            // USER
         //   Text("For: \(reminder.userName)")
            Text("For: \((reminder.familyMemberName.isEmpty == false ? reminder.familyMemberName : reminder.userName))")
                .font(.custom("Urbanist-Medium", size: 12))
                .foregroundColor(Color(hex: "#211C64"))
            
            // TITLE + TIME
            HStack(alignment: .top) {
                
                Text(reminder.title ?? "")
                    .font(.custom("Urbanist-Medium", size: 16))
                    .foregroundColor(.black)
                
                Spacer()
                
                Text(reminder.timeText ?? "")
                    .font(.custom("Urbanist-Medium", size: 14))
                    .foregroundColor(Color(hex: "#4338CA"))
            }
            
            // DESCRIPTION
            Text(reminder.description ?? "")
                .font(.custom("Urbanist-Medium", size: 14))
                .foregroundColor(Color(hex: "#374151"))
            
            // PRIORITY VIEW
            // PRIORITY VIEW
            if reminder.priority != .normal {

                HStack(spacing: 10) {

                    Text(reminder.priority.title)
                        .font(.custom("Urbanist-Medium", size: 12))
                        .padding(.horizontal, 12)
                        .padding(.vertical, 4)
                        .foregroundColor(reminder.priority.color)
                        .background(
                            RoundedRectangle(cornerRadius: 12)
                                .fill(reminder.priority.color.opacity(0.1))
                        )
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(reminder.priority.color, lineWidth: 1)
                        )

                    // ACTION REQUIRED BUTTON
                    if reminder.actionRequired == 1 {

                        Button(action: onActionTap) {

                            Image("ActionRequired")
                               
                        }
                        //.disabled(false)
                    }
                }
            }
            
            // BOTTOM BUTTONS
            HStack {
                
                Button {
                    
                } label: {
                    
                    Image("TimeImage")
                        .resizable()
                        .frame(width: 42, height: 42)
                }
                
                Spacer()
                if reminder.type?.lowercased() == "appointment" {
                    
                    Button(action: onCompleteTap) {
                        
                        HStack(spacing: 8) {
                            
//                            Image(
//                                reminder.appointmentcompletestatus
//                                ? "FilledCheckBox"
//                                : "CheckBox"
//                            )
//                            .resizable()
//                            .frame(width: 25, height: 25)
                            
                            Image(
                                reminder.appointmentcompletestatus.lowercased() == "completed"
                                ? "FilledCheckBox"
                                : "CheckBox"
                            )
                            .resizable()
                            .frame(width: 25, height: 25)
                            
                            Text("Mark As Complete")
                                .font(.custom("Urbanist-Regular", size: 16))
                                .foregroundColor(.black)
                        }
                    }
//                    .disabled(
//                        reminder.appointmentcompletestatus.lowercased() == "completed"
//                    )
                }
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 30)
                .fill(Color(hex: "#4338CA").opacity(0.03))
                .overlay(
                    RoundedRectangle(cornerRadius: 30)
                        .stroke(
                            Color(hex: "#4338CA").opacity(0.3),
                            lineWidth: 1
                        )
                )
                .shadow(
                    color: .black.opacity(0.05),
                    radius: 6,
                    x: 0,
                    y: 4
                )
        )
    }
}

#Preview {
    
    AlertView()
        .environmentObject(Coordinator())
}
