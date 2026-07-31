//
//  InAppNotificationPopupView.swift
//  CureMeGPT
//
//  Created by Antigravity on 2026-07-07.
//

import SwiftUI

struct InAppNotificationPopupContainerView: View {
    var payload: InAppNotificationPayload
    var onClose: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            if payload.type == "medication" {
                medicationPopupView
            } else {
                appointmentPopupView
            }
        }
    }

    // MARK: - Appointment Popup View
    private var appointmentPopupView: some View {
        VStack(alignment: .leading, spacing: 20) {
            // Header: Icon + Title + Close Button
            HStack(alignment: .top, spacing: 12) {
                ZStack {
                    Circle()
                        .fill(Color(hex: "#2B2690"))
                        .frame(width: 44, height: 44)
                    
                    if isDental {
                        Image("TeethIcon")
                            .resizable()
                            .renderingMode(.template)
                            .foregroundColor(.white)
                            .frame(width: 20, height: 20)
                    } else {
                        Image(systemName: "calendar")
                            .resizable()
                            .foregroundColor(.white)
                            .frame(width: 20, height: 20)
                    }
                }
                
                Text(payload.title)
                    .font(.custom("Urbanist-Medium", size: 18))
                    .foregroundColor(Color(hex: "#050505"))
                    .lineLimit(2)
                    .padding(.top, 10)
                
                Spacer()
                
                Button(action: onClose) {
                    ZStack {
                        Circle()
                            .stroke(Color(hex: "#EAEAEA"), lineWidth: 1)
                            .background(Circle().fill(Color.white))
                            .frame(width: 44, height: 44)
                        Image(systemName: "xmark")
                            .foregroundColor(Color(hex: "#181B1A"))
                            .font(.system(size: 14, weight: .medium))
                    }
                }
            }
            
            // Message
            Text(boldedMessage(payload.message))
                .font(.custom("Urbanist-Regular", size: 16))
                .foregroundColor(Color(hex: "#181B1A"))
                .lineSpacing(4)
                .frame(maxWidth: .infinity, alignment: .leading)
            
            // Buttons
            HStack(spacing: 12) {
                Button(action: onClose) {
                    Text("Remind Me Later")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(Color(hex: "#181B1A"))
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .overlay(
                            RoundedRectangle(cornerRadius: 40)
                                .stroke(Color(hex: "#181B1A"), lineWidth: 1)
                        )
                }
                
                Button(action: onClose) {
                    Text("Got It")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(
                            LinearGradient(
                                colors: [
                                    Color(hex: "#4338CA"),
                                    Color(hex: "#211C64")
                                ],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .cornerRadius(40)
                }
            }
        }
        .padding(20)
        .background(
            RoundedRectangle(cornerRadius: 25)
                .fill(.white)
                .shadow(color: .black.opacity(0.15), radius: 8)
        )
        .padding(.horizontal, 24)
    }

    // MARK: - Medication Popup View
    private var medicationPopupView: some View {
        VStack(alignment: .leading, spacing: 20) {
            // Header: Icon + Title + Close Button
            HStack(alignment: .top, spacing: 12) {
                ZStack {
                    Circle()
                        .fill(Color(hex: "#2B2690"))
                        .frame(width: 44, height: 44)
                    
                    Image("CapsuleImage")
                        .resizable()
                        .renderingMode(.template)
                        .foregroundColor(.white)
                        .frame(width: 20, height: 20)
                }
                
                Text(payload.title)
                    .font(.custom("Urbanist-Medium", size: 18))
                    .foregroundColor(Color(hex: "#050505"))
                    .lineLimit(2)
                    .padding(.top, 10)
                
                Spacer()
                
                Button(action: onClose) {
                    ZStack {
                        Circle()
                            .stroke(Color(hex: "#EAEAEA"), lineWidth: 1)
                            .background(Circle().fill(Color.white))
                            .frame(width: 44, height: 44)
                        Image(systemName: "xmark")
                            .foregroundColor(Color(hex: "#181B1A"))
                            .font(.system(size: 14, weight: .medium))
                    }
                }
            }
            
            // Message
            Text(boldedMessage(payload.message))
                .font(.custom("Urbanist-Regular", size: 16))
                .foregroundColor(Color(hex: "#181B1A"))
                .lineSpacing(4)
                .frame(maxWidth: .infinity, alignment: .leading)
            
            // Caution Label
            if let caution = payload.caution, !caution.isEmpty {
                HStack(alignment: .top, spacing: 8) {
                    Image(systemName: "info.circle")
                        .foregroundColor(Color(hex: "#FF4D4D"))
                        .font(.system(size: 16))
                        .padding(.top, 1)
                    
                    Text(caution)
                        .font(.custom("Urbanist-Italic", size: 14))
                        .foregroundColor(Color(hex: "#FF4D4D"))
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
            }
            
            // Buttons
            HStack(spacing: 12) {
                Button(action: onClose) {
                    Text("Snooze")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(Color(hex: "#181B1A"))
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .overlay(
                            RoundedRectangle(cornerRadius: 40)
                                .stroke(Color(hex: "#181B1A"), lineWidth: 1)
                        )
                }
                
                Button(action: onClose) {
                    Text("Mark As Taken")
                        .font(.custom("Urbanist-Medium", size: 16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(
                            LinearGradient(
                                colors: [
                                    Color(hex: "#4338CA"),
                                    Color(hex: "#211C64")
                                ],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .cornerRadius(40)
                }
            }
        }
        .padding(20)
        .background(
            RoundedRectangle(cornerRadius: 25)
                .fill(.white)
                .shadow(color: .black.opacity(0.15), radius: 8)
        )
        .padding(.horizontal, 24)
    }

    // MARK: - Helpers
    private var isDental: Bool {
        let checkString = (payload.title + " " + payload.message).lowercased()
        return checkString.contains("dental") || checkString.contains("tooth") || checkString.contains("teeth") || checkString.contains("dentist")
    }

    private func boldedMessage(_ message: String) -> LocalizedStringKey {
        // Bold part starting with medication name if matches medication reminder
        if message.hasPrefix("Time to take your ") {
            let med = message.replacingOccurrences(of: "Time to take your ", with: "")
            return LocalizedStringKey("Time to take your **\(med)**")
        }
        
        // Bold timing part of appointment reminders
        if let range = message.range(of: "today at") {
            let prefix = String(message[..<range.lowerBound])
            let suffix = String(message[range.lowerBound...])
            return LocalizedStringKey("\(prefix)**\(suffix)**")
        }
        
        return LocalizedStringKey(message)
    }
}

#Preview {
    ZStack {
        Color.black.opacity(0.4).ignoresSafeArea()
        VStack(spacing: 24) {
            InAppNotificationPopupContainerView(
                payload: InAppNotificationPayload(
                    type: "appointment",
                    title: "Dental Cleaning Today",
                    message: "Don't forget your dental cleaning appointment today at 2:00 PM with Dr. Sarah Johnson.",
                    caution: nil
                ),
                onClose: {}
            )
            
            InAppNotificationPopupContainerView(
                payload: InAppNotificationPayload(
                    type: "medication",
                    title: "Medication Reminder",
                    message: "Time to take your Lisinopril 10mg.",
                    caution: "Don't forget to take it with food."
                ),
                onClose: {}
            )
        }
    }
}
