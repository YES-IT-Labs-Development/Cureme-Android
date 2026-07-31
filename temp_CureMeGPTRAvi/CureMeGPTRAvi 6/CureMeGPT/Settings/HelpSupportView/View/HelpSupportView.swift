//
//  HelpSupportView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 05/12/25.
//

import SwiftUI

struct HelpSupportView: View {
    let pageData: SettingData
    @EnvironmentObject private var coordinator: Coordinator
    @StateObject private var vm = HelpSupportViewModel()

    private var parsedContent: (title: String, bodyText: String) {
        let lines = pageData.content.components(separatedBy: .newlines)
            .map { $0.trimmingCharacters(in: .whitespacesAndNewlines) }
            .filter { !$0.isEmpty }
        
        var title = "Need Medical Help? We’re Here for You!"
        var bodyText = "Whether you need help with your profile, AI chat, or technical issues, the CureMeGPT support team is ready to assist you anytime."
        
        if let firstLine = lines.first {
            title = firstLine
        }
        
        if lines.count > 1 {
            let filteredLines = lines.dropFirst().filter { !$0.contains("Email Us") && !$0.contains("support@") }
            if !filteredLines.isEmpty {
                bodyText = filteredLines.joined(separator: "\n\n")
            }
        }
        
        return (title, bodyText)
    }

    private var supportEmail: String {
        let text = pageData.content
        let pattern = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
        guard let regex = try? NSRegularExpression(pattern: pattern, options: .caseInsensitive) else {
            return "support@curemegpt.com"
        }
        let nsString = text as NSString
        if let match = regex.firstMatch(in: text, options: [], range: NSRange(location: 0, length: nsString.length)) {
            return nsString.substring(with: match.range)
        }
        return "support@curemegpt.com"
    }

    private func highlightKeyword(in text: String, keyword: String, color: Color, isUnderlined: Bool = false) -> AttributedString {
        var result = AttributedString()
        let parts = text.components(separatedBy: keyword)
        
        for index in parts.indices {
            var normal = AttributedString(parts[index])
            normal.foregroundColor = Color(hex: "#181818")
            result.append(normal)
            
            if index < parts.count - 1 {
                var highlighted = AttributedString(keyword)
                highlighted.foregroundColor = color
                if isUnderlined {
                    highlighted.underlineStyle = .single
                }
                result.append(highlighted)
            }
        }
        return result
    }

    var body: some View {
        ZStack {
            VStack{
                HStack {
                    Button(action: {
                        coordinator.pop()
                    }) {
                        Image("backIcon")
                            .resizable()
                            .frame(width: 45, height: 45)
                            .padding(.leading, 10)
                    }
                    
                    Text(pageData.title)
                        .font(.custom("Urbanist-Medium", size: 20))
                        .foregroundColor(Color.black)
                        .padding(.leading, 10)
                    Spacer()
                    
                }
                .padding()
                
                ScrollView(showsIndicators: false) {
                    VStack(alignment: .leading, spacing: 20) {
                        
                        // MARK: - TITLE
                        Text(parsedContent.title)
                            .font(.custom("Urbanist-Medium", size: 18))
                            .foregroundColor(Color(hex: "#4338CA"))
                            .padding(.top, 10)
                        
                        // MARK: - SUBTITLE
                        Text(highlightKeyword(in: parsedContent.bodyText, keyword: "CureMeGPT", color: Color(hex: "#4338CA")))
                            .font(.custom("Urbanist-Medium", size: 16))
                            .lineSpacing(6)
                            .padding(.bottom, 10)
                        
                        // MARK: - EMAIL BUTTON
                        Button(action: {
                            if let url = URL(string: "mailto:\(supportEmail)"),
                               UIApplication.shared.canOpenURL(url) {
                                UIApplication.shared.open(url)
                            }
                        }) {
                            Text("Email Us: \(supportEmail)")
                                .font(.custom("Urbanist-Medium", size: 16))
                                .foregroundColor(.black)
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.white)
                                .cornerRadius(25)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 25)
                                        .stroke(Color(hex: "#D9D9D9"), lineWidth: 1)
                                )
                        }
                        
                        // MARK: - FAQ BOX
                        supportInfoBox(
                            title: "Need Quick Answers?",
                            message: highlightKeyword(
                                in: "Visit our FAQ section to explore answers about AI consultations, profile setup, medication reminders, and privacy settings.",
                                keyword: "FAQ section",
                                color: Color(hex: "#4338CA"),
                                isUnderlined: true
                            ),
                            action: {
                                coordinator.push(.faqView)
                            }
                        )
                        
                        // MARK: - FEEDBACK BOX
                        supportInfoBox(
                            title: "Feedback & Suggestions",
                            message: highlightKeyword(
                                in: "Help us improve CureMeGPT by sharing your feedback or new feature ideas. Your input helps us build a smarter, more caring health assistant.",
                                keyword: "CureMeGPT",
                                color: Color(hex: "#4338CA")
                            ),
                            action: {
                               // vm.openFeedbackForm()
                            }
                        )
                        
                        Spacer()
                    }
                    .padding(.horizontal, 20)
                }
                .disableScrollBounce()
                
                Text("© 2026 CureMeGPT · Your privacy is our priority")
                    .font(.custom("Urbanist-Medium", size: 14))
                    .foregroundColor(Color(hex: "#181818"))
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.bottom, 20)
                
            }
            if vm.showActivity {
                CustomLoderView(isVisible: $vm.showActivity)
                    .ignoresSafeArea()
            }
        }
    }
    
    // MARK: - BOX COMPONENT
    @ViewBuilder
    private func supportInfoBox(
        title: String,
        message: AttributedString,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            VStack(alignment: .leading, spacing: 10) {
                Text(title)
                    .font(.custom("Urbanist-Medium", size: 16))
                    .foregroundColor(Color(hex: "#000000"))
                
                Text(message)
                    .font(.custom("Urbanist-Medium", size: 16))
                    .foregroundColor(Color(hex: "#181818"))
                    .multilineTextAlignment(.leading)
                    .lineSpacing(4)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(16)
            .background(Color(hex: "#F7F7FB"))
            .cornerRadius(16)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

#Preview {
    HelpSupportView(pageData: SettingData(id: 7, title: "Help & Support", content: "Need Medical Help? We're Here for You!\r\n\r\nWhether you need help with your profile, AI chat, or technical issues, the CureMeGPT support team is ready to assist you anytime. \r\n\r\n            Email Us: support@curemegpt.com", slug: "help-support", status: "Published"))
}
