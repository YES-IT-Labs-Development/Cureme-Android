//
//  HealthReportView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 30/12/25.
//

import SwiftUI

struct HealthReportView: View {
    
    @StateObject private var viewModel = HealthReportViewModel()
    @StateObject private var filterViewModel = FilterHealthReportPopupViewModel()
    @State private var showPopup = false
    @EnvironmentObject private var coordinator: Coordinator
    @State private var showSharePopup = false
    @State private var reportToShare: HealthReportData?
    
    @EnvironmentObject private var tabVM: TabViewModel
    
    
    var body: some View {
        ZStack{
            VStack(spacing: 16) {
                VStack(alignment: .leading) {
                    Text("Health Reports")
                        .font(.custom("Urbanist-Medium", size: 20))
                        .padding(.leading, 22)
                    
                    Divider()
                }
                
                // SEARCH BAR
                ReportSearchBarView(
                    text: $viewModel.searchText,
                    onFilterTap: {
                        showPopup = true
                    }
                )
                .padding(.horizontal)
                
                // LIST
                ScrollView {
                    VStack(spacing: 16) {
                        
                        if viewModel.filteredReports.isEmpty {
                            
                            VStack(spacing: 12) {
                                
                                Image(systemName: "doc.text.magnifyingglass")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 60, height: 60)
                                    .foregroundColor(.gray)
                                
                                Text("No Data Found")
                                    .font(.custom("Urbanist-SemiBold", size: 18))
                                    .foregroundColor(.black)
                                
                                Text("No health reports available.")
                                    .font(.custom("Urbanist-Regular", size: 14))
                                    .foregroundColor(.gray)
                            }
                            .padding(.top, 200)
                            .padding(.horizontal)
                            
                        } else {
                            
                            ForEach(viewModel.filteredReports) { report in
                                
                                HealthReportCardView(
                                    healthReport: report,
                                    isMenuOpen: viewModel.showMenu &&
                                    viewModel.selectedHealthReportID == report.id
                                ) { point in
                                    
                                    viewModel.menuPosition = point
                                    viewModel.selectedHealthReportID = report.id
                                    viewModel.showMenu = true
                                }
                            }
                        }
                    }
                    .padding(.horizontal)
                    .padding(.bottom, 50)
                }
                .disableScrollBounce()
            }
            .background(Color(.white))
            
            if viewModel.showMenu {
                HealthReportContextMenu(
                    proposedPosition: viewModel.menuPosition,
                    onDismiss: {
                        withAnimation {
                            viewModel.showMenu = false
                        }
                    },
                    onViewTap: {
                        navigateToDetail()
                    },
                    onShareTap: {
                        // Set the selected report to share
                        if let reportID = viewModel.selectedHealthReportID {
                            reportToShare = viewModel.filteredReports.first { $0.id == reportID }
                            showSharePopup = true
                            
//                            if let firstAttachment = viewModel.attachments.first?.filePath {
//                                 
//                                 // Add your base URL here
//                                 viewModel.shareableLink =
//                                 "https://curemegpt.tgastaging.com/\(firstAttachment)"
//                             }
                        }
                        viewModel.showMenu = false
                    }
                )
            }
            if showSharePopup, let report = reportToShare {
                Color.black.opacity(0.4)  // Dim background
                    .ignoresSafeArea()
                    .onTapGesture {
                        
                        showSharePopup = false
                        reportToShare = nil
                    }
                
                SharePopUpView(
                    title: "Share Report",
                    message: "You can share \(report.title ?? "") with others.",
                    onClose: {
                        showSharePopup = false
                        reportToShare = nil
                    },
                    message1: AppsFlyerHelper.createReportShareLink(reportID: report.chatID ?? report.id)
                )
                .transition(.scale)
                .zIndex(1)
            }
            
            if viewModel.showActivity {
                CustomLoderView(isVisible: $viewModel.showActivity)
                    .ignoresSafeArea()
            }
            
        }
        .onAppear {
            viewModel.getHealthReportsAPI()
        }
        .sheet(isPresented: $showPopup) {
            FilterHealthReportPopupView(viewModel: filterViewModel) { selectedMember in
                print("Selected filter:", selectedMember.selectedMember?.name ?? "")
                viewModel.selectedMember = selectedMember.selectedMember
            }
            .presentationDetents([.height(360)])
            .presentationCornerRadius(24)
        }
        
    }
    
    //    private func navigateToDetail() {
    //        guard let reportID = viewModel.selectedHealthReportID else { return }
    //        coordinator.push(.reportDescriptionView)
    //        viewModel.showMenu = false
    //    }
    
    private func navigateToDetail() {
        
        guard let reportID = viewModel.selectedHealthReportID else { return }
        
        coordinator.push(.reportDescriptionView(chatID: reportID))
        
        viewModel.showMenu = false
    }
}

// MARK: - healthReport LIST
extension HealthReportView {
    private var HealthReportsView: some View {
        ScrollView {
            VStack(spacing: 16) {
                // SEARCH BAR INSIDE SCROLL
                ReportSearchBarView(
                    text: $viewModel.searchText,
                    onFilterTap: {
                        showPopup = true
                    }
                )
                .padding(.horizontal)
                .padding(.top, 4)

            }
            .padding(.horizontal)
            .padding(.bottom, 110)
        }
        .disableScrollBounce()
    }
}

struct ReportSearchBarView: View {
    
    @Binding var text: String
    let onFilterTap: () -> Void
    
    @FocusState private var isFocused: Bool
    
    var body: some View {
        
        HStack {
            
            HStack {
                
                Image(systemName: "magnifyingglass")
                    .foregroundColor(.gray)
                
                TextField("Search", text: $text)
                    .focused($isFocused)
                
                // Clear Text Button
                if !text.isEmpty {
                    Button {
                        text = ""
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.gray)
                    }
                }
            }
            .padding()
            .background(Color(hex: "#F4F4F4"))
            .cornerRadius(40)
            
            // Filter Button
            if text.isEmpty {
                Button(action: {
                    onFilterTap()
                }) {
                    Image("FillterBtn")
                }
            }
            
            // Cancel Button
            if !text.isEmpty {
                
                Button("Cancel") {
                    
                    text = ""
                    isFocused = false
                    
                }
                .font(.custom("Urbanist-Medium", size: 14))
                .foregroundColor(Color(hex: "#4338CA"))
                .transition(.move(edge: .trailing))
            }
        }
        .animation(.easeInOut, value: text.isEmpty)
    }
}

struct HealthReportCardView: View {
    let healthReport: HealthReportData
    let isMenuOpen: Bool
    let onMenuTap: (CGPoint) -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            // MARK: Header
            HStack(alignment: .top) {
                RoundedRectangle(cornerRadius: 130)
                    .fill(Color.white)
                    .frame(width: 48)
                    .overlay(
                        RoundedRectangle(cornerRadius: 130)
                            .stroke(Color(hex: "#3C3C3C"), lineWidth: 1)
                    )
                    .overlay(
                        Image("pad")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 22, height: 22)
                    )
                
                VStack(alignment: .leading, spacing: 4) {
                    HStack{
                        VStack(alignment: .leading, spacing: 10){
                            Text(healthReport.title ?? "")
                                .font(.custom("Urbanist-Medium", size: 16))
                                .padding(.leading)
                                .lineLimit(1)

                            Text(
                                !(healthReport.familyName ?? "").isEmpty
                                ? (healthReport.familyName ?? "")
                                : (healthReport.userName ?? "")
                            )
                                .font(.custom("Urbanist-Medium", size: 14))
                                .foregroundColor(Color(hex: "#4338CA"))
                                .lineLimit(1)
                                .padding(.leading)
                            
                            StatusBadgeView(status: healthReport.severity ?? .low)
                            
                          //  StatusBadgeView(status: healthReport.severity)
                                .padding(.leading)
                           // Spacer()
                        }
                        
                        Spacer()
                        
                        GeometryReader { geo in
                            Button {
                                let frame = geo.frame(in: .global)
                                onMenuTap(
                                    CGPoint(
                                        x: frame.midX - 40,
                                        y: frame.maxY + 12
                                    )
                                )
                            } label: {
                                Image(isMenuOpen ? "DotButtonActive" : "DotButton")
                            }
                        }
                        .frame(width: 24, height: 24)
                        .padding(.trailing)
                        .padding(.top, -30)
                    }
                    
                    infoRow(icon: "Date", text: (healthReport.chatDate ?? "").toAppDateString())
     
                    infoRow(icon: "mage_note", text: healthReport.aiMessage ?? "")
                    
                        HStack(spacing: 4) {
                            Text("\(healthReport.filesCount ?? 0)")
                                .font(.custom("Urbanist-Medium", size: 10))
                            
                            Text("Files")
                                .font(.custom("Urbanist-Medium", size: 10))
                                .lineLimit(1)
                        }
                        //.padding(.leading)
                        .foregroundColor(Color(hex: "#4338CA"))
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(Color(hex: "#4338CA").opacity(0.10))
                        .cornerRadius(80)
                        .padding(.leading)
                }
            }
        }
        .padding()
        .background(
            RoundedRectangle(cornerRadius: 30)
                .fill(Color(hex: "#F4F4F4"))
        )
        .overlay(
            RoundedRectangle(cornerRadius: 30)
                .stroke(Color(hex: "#996BFE").opacity(0.3), lineWidth: 1)
        )
    }
}

private func infoRow(icon: String, text: String) -> some View {
    HStack(spacing: 10) {
        Image(icon)
            .resizable()
            .frame(width: 29, height: 29)

        Text(text)
            .font(.custom("Urbanist-Regular", size: 14))
            .lineLimit(3)
            .truncationMode(.tail)

        Spacer()
    }
    .padding(.leading, 10)
}


struct StatusBadgeView: View {
    
    let status: HealthReportStatus

    var body: some View {
        
        HStack(spacing: 6) {
            
            Image(status.icon)
                .resizable()
                .scaledToFit()
                .frame(width: 14, height: 14)
            
            Text(status.title)
                .font(.custom("Urbanist-Medium", size: 10))
        }
        .padding(.horizontal, 8)
        .padding(.vertical, 4)
        .foregroundColor(status.textColor)
        .background(
                    Capsule()
                        .fill(status.backgroundColor)
                )
        .overlay(
            RoundedRectangle(cornerRadius: 80)
                .stroke(status.borderColour)
        )
    }
}

struct HealthReportContextMenu: View {
    let proposedPosition: CGPoint
    let onDismiss: () -> Void
    let onViewTap: () -> Void
    let onShareTap: () -> Void

    private let menuWidth: CGFloat = 130
    private let menuHeight: CGFloat = 90

    var body: some View {
        GeometryReader { geo in

            let safePosition = clampedPosition(
                proposed: proposedPosition,
                screen: geo.size
            )

            ZStack {
                // BACKDROP
                Color.black.opacity(0.001)
                    .ignoresSafeArea()
                    .onTapGesture { onDismiss() }

                VStack(spacing: 16) {
                    menuRow(
                        icon: "eye",
                        title: "View",
                        action: onViewTap
                    )

                    menuRow(
                        icon: "share",
                        title: "Share",
                        action: onShareTap
                    )
                }
                .padding()
                .frame(width: menuWidth)
                .background(Color.white)
                .cornerRadius(16)
                .shadow(radius: 10)
                .position(safePosition)
            }
        }
    }

    private func menuRow(
        icon: String,
        title: String,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            HStack(spacing: 12) {
                Image(icon)
                Text(title)
                    .font(.custom("Urbanist-Medium", size: 16))
                Spacer()
            }
            .foregroundColor(Color(hex: "#374151"))
        }
    }

    private func clampedPosition(
        proposed: CGPoint,
        screen: CGSize
    ) -> CGPoint {

        let x = min(
            max(menuWidth / 2 + 16, proposed.x),
            screen.width - menuWidth / 2 - 16
        )

        let y = min(
            max(menuHeight / 2 + 16, proposed.y),
            screen.height - menuHeight / 2 - 16
        )

        return CGPoint(x: x, y: y)
    }
}

#Preview {
    HealthReportView()
}
