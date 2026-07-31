//
//  ReportDescriptionView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 07/01/26.
//

import SwiftUI
import SDWebImageSwiftUI

// MARK: - VIEW
struct ReportDescriptionView: View {
    @StateObject private var viewModel = ReportDescriptionViewModel()
    @EnvironmentObject private var coordinator: Coordinator
   // let status: ReportDetail
    let chatID : Int?
    var isFromSharedLink: Bool = false
    @State private var showPopup: Bool = false
    @State private var popupAction: (() -> Void)? = nil
    
    var body: some View {
        ZStack{
            VStack(spacing: 0) {
                HStack {
                    Button(action: {
                        coordinator.pop()
                    }){
                        Image("backIcon")
                            .resizable()
                            .frame(width: 45, height: 45)
                    }
                    Text("Back to Reports")
                        .font(.custom("Urbanist-Medium", size: 20))
                        .foregroundColor(.black)
                    
                    Spacer()
                }
                .padding(.horizontal, 26)
                .padding(.top, 10)
                .padding(.bottom, 8)
                .background(Color.white)
                
                ScrollView {
                    VStack(spacing: 16) {
                        if !viewModel.title.isEmpty {
                            headerSection
                        }
                        if !viewModel.summary.isEmpty {
                            summarySection
                        }
                        if !viewModel.detailedAnalysis.isEmpty {
                            detailedAnalysisSection
                        }
                        if !viewModel.highlights.isEmpty {
                            highlightsSection
                        }
                        if !viewModel.attachments.isEmpty {
                            AttachedFilesSection(
                                doc: viewModel.attachments,
                                onDownload: { filePath in
                                    downloadReportAsPDF()
                                }
                            )
                        }
                    }
                    .padding()
                }
                .disableScrollBounce()
            }
            .onAppear {
                if let chatID = self.chatID {
                    viewModel.getReportDetails(chat_id: "\(chatID)") { success in
                        if success {
                            print("Report details loaded successfully")
                        } else {
                            print("Failed to load report details")
                        }
                    }
                }
            }
            
                if showPopup {
                    // DARK BACKGROUND
                    Color.black.opacity(0.4)
                        .edgesIgnoringSafeArea(.all)
                        .transition(.opacity)
                        .onTapGesture {
                            withAnimation {
                                showPopup = false
                                popupAction?()
                            }
                        }
                    
                    // USING YOUR EXISTING POPUP VIEW EXACTLY AS IT IS
                    SharePopUpView(title: "Share Report",
                                   message: "Create a view-only link to this report.",
                                   onClose: {
                        showPopup = false
                    },
                          //  message1: "Shareable link"
                                   message1: viewModel.shareableLink
                        // Navigate AFTER popup close animation finishes

                    )
                    .transition(.scale.combined(with: .opacity))
                    .zIndex(1)
                }
               
            if viewModel.showActivity {
                CustomLoderView(isVisible: $viewModel.showActivity)
                    .ignoresSafeArea()
            }
             
            }
    }

    // MARK: - HEADER
    private var headerSection: some View {
        
        VStack(alignment: .leading, spacing: 14) {
            // MARK: Header
            HStack(alignment: .top) {
                RoundedRectangle(cornerRadius: 130)
                    .fill(
                        LinearGradient(
                            colors: [
                                Color(red: 67/255, green: 56/255, blue: 202/255),
                                Color(red: 33/255, green: 28/255, blue: 100/255)
                            ],
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                    )
                    .frame(width: 48)
                    .overlay(
                        RoundedRectangle(cornerRadius: 130)
                            .stroke(Color(hex: "#3C3C3C"), lineWidth: 1)
                    )
                
                    .overlay(
                        Image("PadWhite")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 22, height: 22)
                        
                    )
                
                VStack(alignment: .leading, spacing: 4) {
                    HStack{
                        VStack(alignment: .leading, spacing: 10){
                            if !viewModel.title.isEmpty {
                                Text(viewModel.title)
                                    .font(.custom("Urbanist-Regular", size: 20))
                                    .padding(.leading)
                                    .lineLimit(1)
                            }
                            Spacer()
                        }
                        
                        if !isFromSharedLink {
                            Button(action: {
                                let reportID = self.chatID ?? viewModel.selectedHealthReportID ?? 0
                                viewModel.shareableLink = AppsFlyerHelper.createReportShareLink(reportID: reportID)
                                showPopup = true
                            }) {
                                Image("Share")
                                    .resizable()
                                    .frame(width: 45, height: 45)
                            }
                            .frame(width: 24, height: 24)
                            .padding(.trailing)
                            .padding(.top, -30)
                        }
                }
                
                    HStack{
                        infoRow(icon: "ScheduleCalendar", text: viewModel.date)
                        
//                        Button(action:{
//                            
//                    }) {
//                        Image("Download")
//                    }
//                    .frame(width: 24, height: 24)
//                    .padding(.trailing)
                        Button(action: {
                            downloadReportAsPDF()
                        }) {
                            Image("Download")
                        }
                        .frame(width: 24, height: 24)
                        .padding(.trailing)
                    }
                    
                    patientInfoRow(profilePicURL: viewModel.profilePicURL, fallbackIcon: "Frame 1272638625", text: viewModel.patientName)
                        .foregroundColor(Color(hex: "#4338CA"))
                    
                }
            }
        }
        .padding()
        .padding(.top, 10)
        .background(Color(hex: "#F4F4F4"))
        .cornerRadius(30)
        .shadow(color: .black.opacity(0.05), radius: 6)
    }

    private func infoRow(icon: String, text: String) -> some View {
        HStack(spacing: 10) {
            Image(icon)
                .resizable()
                .frame(width: 29, height: 29)

            Text(text)
                .font(.custom("Urbanist-Regular", size: 14))
                .lineLimit(1)
                .truncationMode(.tail)

            Spacer()
        }
        .padding(.leading, 10)
        
    }

    private func patientInfoRow(profilePicURL: String?, fallbackIcon: String, text: String) -> some View {
        HStack(spacing: 10) {
            if let urlString = profilePicURL, let url = URL(string: urlString) {
                WebImage(url: url) { image in
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                } placeholder: {
                    Image(fallbackIcon)
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                }
                .frame(width: 29, height: 29)
                .cornerRadius(14.5)
                .clipped()
            } else {
                Image(fallbackIcon)
                    .resizable()
                    .frame(width: 29, height: 29)
            }

            Text(text)
                .font(.custom("Urbanist-Regular", size: 14))
                .lineLimit(1)
                .truncationMode(.tail)

            Spacer()
        }
        .padding(.leading, 10)
    }
    
    struct StatusBadgeView: View {
        let status: ReportDetailStatus

        var body: some View {
            HStack(spacing: 6) {
                Image(status.icon)
                    .resizable()
                    .frame(width: 14, height: 14)
                
                Text(status.title)
                    .font(.custom("Urbanist-Medium", size: 10))
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 4)
            .foregroundColor(status.textColor)
            .background(status.backgroundColor)
            .overlay(RoundedRectangle(cornerRadius: 80).stroke(status.borderColour)
              )
           }
       }
    
    // MARK: - SUMMARY
    private var summarySection: some View {
        sectionContainer(title: "Summary") {
            Text(viewModel.summary)
                .font(.custom("Urbanist-Regular", size: 15))
                .foregroundColor(Color(hex: "#3C3C3C"))
        }
        .background(Color(hex: "#F4F4F4"))
        .cornerRadius(30)
    }

    // MARK: - DETAILED ANALYSIS
    private var detailedAnalysisSection: some View {
        sectionContainer(title: "Detailed Analysis") {
            Text(viewModel.detailedAnalysis)
                .font(.custom("Urbanist-Regular", size: 15))
                .foregroundColor(Color(hex: "#3C3C3C"))
        }
        .background(Color(hex: "#F4F4F4"))
        .cornerRadius(30)
    }

    // MARK: - HIGHLIGHTS


    private var highlightsSection: some View {
        sectionContainer(title: "AI Insights") {

            VStack(spacing: 16) {

                ForEach(viewModel.highlights) { item in

                    HStack(alignment: .top) {

                        Text(item.title)
                            .font(.custom("Urbanist-Medium", size: 15))
                            .foregroundColor(Color(hex: "#4338CA"))
                            .frame(width: 130, alignment: .leading)

                        Text(item.value)
                            .font(.custom("Urbanist-Regular", size: 15))
                            .foregroundColor(Color(hex: "#3C3C3C"))
                            .multilineTextAlignment(.leading)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                }
            }
        }
        .background(Color(hex: "#F4F4F4"))
        .cornerRadius(30)
    }
 
    struct AttachedFilesSection: View {

        let doc: [Attachment]

        var onDownload: (String) -> Void

        var body: some View {

            VStack(alignment: .leading, spacing: 12) {

                Text("Attached Files")
                    .font(.custom("Urbanist-SemiBold", size: 18))
                    .foregroundColor(.black)

                ForEach(doc) { item in

                    HStack {

                        Image("Documents")
                            .frame(width: 40, height: 50)

                        Text(item.name)
                            .font(.subheadline)
                            .lineLimit(1)

                        Spacer()

                        Button {

                            onDownload(item.filePath)

                        } label: {

                            Image("DownloadDocIcon")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 40, height: 40)
                        }
                    }
                    .padding()
                   
                    .background(
                        Color(hex: "#4338CA").opacity(0.20)
                    )
                    .cornerRadius(28)
                }
            }
            .padding(.top, 10)
            .padding(.horizontal, 20)
            .padding(.bottom, 10)
            .background(Color(hex: "#F4F4F4"))
            .cornerRadius(30)
        }
    }
    
    // MARK: - ATTACHMENTS
    private var attachmentsSection: some View {
        sectionContainer(title: "Attachments") {
            VStack(spacing: 12) {
                ForEach(viewModel.attachments) { file in
                    HStack {
                        Image(systemName: file.icon)
                            .foregroundColor(.purple)

                        Text(file.name)
                            .font(.subheadline)

                        Spacer()

                        Image(systemName: "arrow.down.circle")
                            .foregroundColor(.secondary)
                    }
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                }
            }
        }
    }

    // MARK: - REUSABLE SECTION CONTAINER
    private func sectionContainer<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title)
                .font(.custom("Urbanist-SemiBold", size: 16))
                .foregroundColor(Color(hex: "#4338CA"))

            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.vertical, 16)
        .padding(.horizontal, 20)
    }

    private func downloadReportAsPDF() {
        if let pdfURL = ReportPDFGenerator.generateReportPDF(
            title: viewModel.title,
            date: viewModel.date,
            patientName: viewModel.patientName,
            summary: viewModel.summary,
            detailedAnalysis: viewModel.detailedAnalysis,
            highlights: viewModel.highlights,
            attachments: viewModel.attachments
        ) {
            sharePDF(url: pdfURL)
        }
    }

    private func sharePDF(url: URL) {
        let activityVC = UIActivityViewController(
            activityItems: [url],
            applicationActivities: nil
        )
        if let rootVC = UIApplication.shared.windows.first?.rootViewController {
            var topController = rootVC
            while let presented = topController.presentedViewController {
                topController = presented
            }
            topController.present(activityVC, animated: true)
        }
    }
}

private func downloadFile(from urlString: String) {

    guard let url = URL(string: urlString) else { return }

    let task = URLSession.shared.downloadTask(with: url) { tempURL, response, error in

        guard let tempURL = tempURL, error == nil else {
            print("Download failed:", error?.localizedDescription ?? "")
            return
        }

        do {

            let documentsDirectory = FileManager.default.urls(
                for: .documentDirectory,
                in: .userDomainMask
            ).first!

            let fileName = url.lastPathComponent

            let destinationURL = documentsDirectory.appendingPathComponent(fileName)

            // Remove old file if exists
            if FileManager.default.fileExists(atPath: destinationURL.path) {
                try FileManager.default.removeItem(at: destinationURL)
            }

            try FileManager.default.moveItem(at: tempURL, to: destinationURL)

            DispatchQueue.main.async {

                print("File downloaded to:", destinationURL)

                // Share / Open downloaded file
                let activityVC = UIActivityViewController(
                    activityItems: [destinationURL],
                    applicationActivities: nil
                )

                UIApplication.shared.windows.first?.rootViewController?
                    .present(activityVC, animated: true)
            }

        } catch {
            print("File save error:", error.localizedDescription)
        }
    }

    task.resume()
}

// MARK: - PREVIEW
//#Preview {
//    ReportDescriptionView(status: ReportDetail(
//        title: "Cavity size",
//        value: "2mm diameter",
//        status: .attention
//       )
//    )
//}

//
//#Preview {
//    
//    ReportDescriptionView(
//        status: ReportDetail(
//            chatID: 269,
//            title: "Dental Report",
//            userName: "Rahul Singh",
//            familyName: nil,
//            severity: "medium",
//            chatDate: "04/21/2026",
//            summary: "Summary",
//            detailedAnalysis: "Detailed Analysis",
//            aiInsights: nil,
//            attachments: nil
//        )
//    )
//}
