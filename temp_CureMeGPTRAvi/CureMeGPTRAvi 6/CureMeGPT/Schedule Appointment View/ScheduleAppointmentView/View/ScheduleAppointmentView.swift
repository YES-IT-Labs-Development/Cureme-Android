//
//  ScheduleAppointmentView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 30/12/25.
//

import SwiftUI

enum DeleteSource {
    case appointment
    case medication
}

struct ScheduleAppointmentView: View {
    @EnvironmentObject private var coordinator: Coordinator
    @StateObject private var viewModel = ScheduleAppointmentViewModel()
    @State private var showPopup: Bool = false
    @State private var popupAction: (() -> Void)? = nil
    @StateObject private var summaryVM = AppointmentSummaryViewModel()
    @State private var deleteSource: DeleteSource?
    @State private var showFilter = false
    @State private var activeFilterPopup: FilterPopupType?
    @EnvironmentObject var tabVM: TabViewModel
    @StateObject private var filterAppointmentVM = FilterAppointmentViewModel()
   
    
    @State private var resetMedicationSearch = false
    @State private var resetAppointmentSearch = false
    
    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            VStack(spacing: 16) {
                VStack(alignment: .leading) {
                    Text("Health Schedule")
                        .font(.custom("Urbanist-Medium", size: 20))
                        .padding(.leading, 22)
                    Divider()
                }
                
                SegmentedHeaderView(selectedTab: $viewModel.selectedTab)
                
                Group {
                    switch viewModel.selectedTab {
                    case .appointments:
                        appointmentsView
                    case .medications:
                       // ScheduleMedicationsView()
                        ScheduleMedicationsView(resetSearchTrigger: resetMedicationSearch)
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                
            }
            .padding(.bottom, 0)
            .background(Color.white)
            //            .allowsHitTesting(
            //                !viewModel.showMedicationMenu && !viewModel.showMenu
            //            )
            .ignoresSafeArea(.keyboard)   // only ignore keyboard
            //.padding(.horizontal, 30)
            .blur(radius: showPopup ? 3 : 0)
            
            if showPopup {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture {
                        showPopup = false
                    }
                    .allowsHitTesting(true)
            }
            
            
            // blur background when popup opens
            
            // FLOATING BUTTON
            floatingScheduleButton
                .zIndex(1)
            
                if viewModel.showMenu, let position = viewModel.menuPosition {
                    AppointmentContextMenu(
                        proposedPosition: position,
                        isCompleted: viewModel.appointments.first(where: { $0.id == viewModel.selectedAppointmentID })?
                            .isCompleted ?? false,
                        onDismiss: {
                            viewModel.showMenu = false
                            viewModel.menuPosition = nil
                            viewModel.selectedAppointmentID = nil
                        },
                        onAction: { action in
                            viewModel.showMenu = false
                            viewModel.menuPosition = nil
                            //viewModel.selectedAppointmentID = nil
                            print()
                            
                            handleMenuAction(action)
                        }
                    )
                    .zIndex(10)
                }
           
            // POPUP OVERLAY
            if showPopup {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture {
                        showPopup = false
                    }
                
                DeleteAppointmentPopUpView(title: popupTitle,
                                           message: popupMessage,
                                           onClose: {
                    withAnimation {
                        showPopup = false
                    }
                },
                                           onDelete: {
                    if let selected = viewModel.selectedAppointment {
                        viewModel.deleteAppointment(id: selected.apiID) { success in
                            if success {
                                print("✅ Delete Success")
                                withAnimation {
                                    showPopup = false
                                }
                            } else {
                                print("❌ Delete Failed")
                            }
                        }
                    }
                }
                )
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .transition(.scale.combined(with: .opacity))
            }
            
            // MEDICATION CONTEXT MENU
            if viewModel.showMedicationMenu,
               let position = viewModel.medicationMenuPosition {
                
                MedicationContextMenu(
                    proposedPosition: position,
                    onDismiss: {
                        viewModel.showMedicationMenu = false
                        viewModel.medicationMenuPosition = nil
                    },
                    onAction: { action in
                        viewModel.showMedicationMenu = false
                        viewModel.medicationMenuPosition = nil
                    }
                )
                .zIndex(999)
            }
            
            if summaryVM.isPresented {
                AppointmentSummaryView(viewModel: summaryVM)
                    .transition(.scale.combined(with: .opacity))
                    .zIndex(100)
            }
            
            // ✅ TOAST VIEW
            if viewModel.showToast {
                VStack {
                    Spacer()
                    
                    ToastView(message: viewModel.toastMessage)
                        .padding(.bottom, 100)
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .zIndex(9999) // 🔥 sabse upar
            }
            // 🔥 FULL SCREEN LOADER
            if viewModel.showActivity {
                ZStack {
                    Color.black.opacity(0.3)
                        .ignoresSafeArea()
                    
                    CustomLoderView(isVisible: $viewModel.showActivity)
                }
                .ignoresSafeArea()
                .zIndex(99999) // 🔥 sabse upar
            }
        }
        .animation(.easeOut, value: showPopup)
        
        .animation(.easeInOut, value: viewModel.showToast)
        
        .onAppear {
            tabVM.isTabBarHidden = false
            viewModel.searchText = ""

            // optional: reset filters too
            viewModel.selectedType = .upcoming
            viewModel.selectedMember = ""

            if viewModel.selectedTab == .appointments {
                callAppointmentAPIIfNeeded(forceRefresh: true)
            }
        }
        .onDisappear {
            viewModel.appointments.removeAll()
            // RESET FILTERS
            viewModel.searchText = ""
            viewModel.selectedType = .upcoming
            viewModel.selectedMember = ""
        }
        
        .onChange(of: viewModel.selectedTab) { newTab in
            
            // RESET APPOINTMENT SEARCH
            if newTab == .appointments {
                viewModel.searchText = ""   // ADD THIS
                callAppointmentAPIIfNeeded(forceRefresh: true)
            }
            if newTab == .medications {
                resetMedicationSearch.toggle()
            }
        }
        
        .onChange(of: viewModel.summaryViewModel.isPresented) { isPresented in
            tabVM.isTabBarHidden = isPresented
        }
       
        .sheet(item: $activeFilterPopup) { popup in
            switch popup {
            case .appointments:
                FilterAppointmentsView(
                    viewModel: filterAppointmentVM
                ) { filterVM in
                    viewModel.applyFilter(
                        type: filterVM.selectedType,
                        member: filterVM.selectedMember?.name ?? "",
                        memberId: filterVM.selectedMember?.id,
                        relation: filterVM.selectedMember?.relation
                    )
                }
                
            case .members:
                FilterMembersView(
                    viewModel: filterAppointmentVM
                ) { selectedMember in
                    viewModel.applyMemberFilter(
                        name: selectedMember.name ?? "",
                        id: selectedMember.id,
                        relation: selectedMember.relationship
                    )
                }
            }
        }
        
    }
    
    
    private func callAppointmentAPIIfNeeded(forceRefresh: Bool = false) {
        if forceRefresh || viewModel.appointments.isEmpty {
            viewModel.getappointmentlistAPI { success in
                print(success ? "✅ Appointment API called" : "❌ API failed")
            }
        }
    }
    private func handleMenuAction(_ action: AppointmentMenuAction) {
        guard let selectedID = viewModel.selectedAppointmentID else { return }
        
        print(selectedID,"selectedID")
        
        
        guard let selected = viewModel.selectedAppointment else { return }
        
        print("Selected API ID:", selected.apiID)
        
        UserDetail.shared.setID("\(selected.apiID)")
        
        // CLOSE MENU AFTER we capture ID
        viewModel.showMenu = false
        viewModel.menuPosition = nil
        viewModel.selectedAppointmentID = nil
        
        switch action {
        case .complete:
            viewModel.markAppointmentComplete(id: selected.apiID) { success in
                if success {
                    print("Completed successfully")
                } else {
                    print( "Failed")
                }
            }
        case .reschedule:
            let chatId = selected.recommendedChatID.flatMap { Int($0) }
            coordinator.push(.newAppointmentScheduleView(flow: .reschedule, chatId: chatId))
        case .delete:
            deleteSource = .appointment
            viewModel.activePopup = .delete
            showPopup = true
        }
    }
    
    
    private var popupTitle: String {
        guard viewModel.activePopup == .delete else { return "" }
        
        switch deleteSource {
        case .appointment:
            return "Delete Appointment?"
        case .medication:
            return "Delete Medication?"
        case .none:
            return ""
        }
    }

    
    private var popupMessage: String {
        guard viewModel.activePopup == .delete else { return "" }

        switch deleteSource {
        case .appointment:

            let patientName = viewModel.selectedAppointment?.patientName ?? "this"

            return "Are you sure you want to delete \(patientName)'s appointment? This action cannot be undone."

        case .medication:

            let patientName = viewModel.selectedAppointment?.title ?? "this"

            return "Are you sure you want to delete \(patientName)'s medication? This action cannot be undone."

        case .none:
            return ""
        }
    }
}

// MARK: - APPOINTMENTS LIST
extension ScheduleAppointmentView {
    private var appointmentsView: some View {
        
        ScrollView {
            VStack(spacing: 16) {
                SearchBarView(
                    text: $viewModel.searchText,
                    onFilterTap: {
                        activeFilterPopup = viewModel.selectedTab == .appointments
                        ? .appointments
                        : .members
                    }
                )
                .padding(.top, 4)
                
                .onChange(of: resetAppointmentSearch) { _ in
                    viewModel.searchText = ""
                }
                .onChange(of: viewModel.searchText) { newValue in
                    if newValue.isEmpty {
                        viewModel.selectedMember = ""
                        viewModel.selectedMemberId = nil
                        viewModel.selectedMemberRelation = nil
                        viewModel.selectedType = .upcoming
                        
                        filterAppointmentVM.selectedType = .upcoming
                        filterAppointmentVM.selectedMember = nil
                        filterAppointmentVM.searchText = ""
                    }
                }
                if viewModel.filteredAppointments.isEmpty {
                    
                    VStack(spacing: 12) {
                        Image(systemName: "calendar.badge.exclamationmark")
                            .resizable()
                            .frame(width: 50, height: 50)
                            .foregroundColor(.gray)
                        
                        Text("No Appointment Found")
                            .font(.custom("Urbanist-Medium", size: 16))
                            .foregroundColor(.gray)
                        
                        //Text("You don’t have any scheduled appointments yet.")
                            //.font(.custom("Urbanist-Regular", size: 14))
                           // .foregroundColor(.gray)
                    }
                    .padding(.top, 100)
                    
                } else {
                    
                    // APPOINTMENT LIST
                    ForEach(viewModel.filteredAppointments) { appointment in
                        AppointmentCardView(
                            appointment: appointment,
                            onMenuTap: { point in
                                viewModel.menuPosition = point
                                viewModel.selectedAppointmentID = appointment.id
                                viewModel.selectedAppointment = appointment
                                viewModel.showMenu = true
                            }, isMenuOpen: viewModel.showMenu &&
                            viewModel.selectedAppointmentID == appointment.id,
                            onViewSummary: {
                                if let recommendedChatID = appointment.recommendedChatID,
                                   let chatId = Int(recommendedChatID) {
                                    summaryVM.showSummary(chatId: chatId)
                                }
                            }
                        )
                    }
                }
            }
            .padding(.horizontal)
            .padding(.bottom, 110)
        }
        .disableScrollBounce()
    }
    
    @ViewBuilder
    private var floatingScheduleButton: some View {
        Button {
            UserDetail.shared.removeID()
            if viewModel.selectedTab == .appointments {
                coordinator.push(.newAppointmentScheduleView(flow: .new, chatId: nil))
            } else {
                coordinator.push(.addMedicationView(flow: .mediNew))
            }
        } label: {
            Image(
                viewModel.selectedTab == .appointments
                ? "AddSchedule"
                : "AddMedication"
            )
            .frame(width: 154, height: 60)
        }
        .padding(.trailing, 20)
        .padding(.bottom, 70)
    }
}

struct SegmentedHeaderView: View {
    @Binding var selectedTab: ScheduleAppointmentViewModel.Tab
    
    var body: some View {
        HStack {
            segmentButton(title: "Appointments", tab: .appointments)
            segmentButton(title: "Medications", tab: .medications)
            
        }
        .padding(6)
        .background(Color.white)
        .cornerRadius(20)
        .padding(.horizontal)
        
    }
    
    private func segmentButton(title: String, tab: ScheduleAppointmentViewModel.Tab) -> some View {
        Button {
            selectedTab = tab
        } label: {
            Text(title)
                .font(.custom("Urbanist-Regular", size: 16))
                .foregroundColor(selectedTab == tab ? .white : Color(hex: "#697383"))
                .frame(maxWidth: .infinity)
                .padding(.vertical, 14)
                .background(
                    selectedTab == tab ? Color(hex: "#3C3C3C") : Color.clear
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 30)
                        .stroke(lineWidth: 0.6).foregroundColor(Color(hex: "#697383"))
                )
                .cornerRadius(56)
        }
    }
}

struct SearchBarView: View {
    @Binding var text: String
    var onFilterTap: () -> Void
    
    var body: some View {
        HStack {
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(.gray)
                
                TextField("Search", text: $text)
                    .foregroundColor(.black)
                
                // ✅ CLEAR BUTTON (❌)
                if !text.isEmpty {
                    Button {
                        withAnimation {
                            text = ""
                        }
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .foregroundColor(.gray)
                    }
                }
                
                Spacer()
            }
            .padding()
            .background(Color(hex: "#F4F4F4"))
            .cornerRadius(40)
            
            Button(action: {
                onFilterTap()
            }) {
                Image("FillterBtn")
            }
        }
    }
}

struct AppointmentCardView: View {
    let appointment: ScheduleAppointmentModel
    let onMenuTap: (CGPoint) -> Void
    let isMenuOpen: Bool
    let onViewSummary: () -> Void
    
    var body: some View {
        VStack{
            HStack(alignment: .top, spacing: 12) {
                RoundedRectangle(cornerRadius: 130)
                    .fill(Color.white)
                    .frame(width: 48)
                    .overlay(
                        RoundedRectangle(cornerRadius: 130)
                            .stroke(Color(hex: "#3C3C3C"), lineWidth: 1)
                    )
                
                    .overlay(
                        // Image(appointment.image)
                        Image("streamline-plump_medical-bag")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 25, height: 25)
                    )
                
                //.cornerRadius(130)
                VStack(alignment: .leading, spacing: 12) {
                    header
                    
                    HStack{
                        infoRow(icon: "ScheduleCalendar", text: appointment.date.toAppDateString())
                        infoRow(icon: "ScheduleTime",text: "\(appointment.time)")
                    }
                    infoRow(icon: "ScheduleLocation", text: appointment.location)
                    infoRow(icon: "mage_note", text: appointment.description)
                    
                    if let recommendedChatID = appointment.recommendedChatID, !recommendedChatID.isEmpty {
                        Button(action:{
                            onViewSummary()
                            print("View Summary")
                        }) {
                            Spacer()
                            HStack{
                                Image("solar_notes-outline")
                                    .foregroundColor(.white)
                                    .frame(width: 24, height: 24)
                                
                                Text("View Summary")
                                    .font(.custom("Urbanist-Medium", size: 14))
                                //.frame(width: 120, height: 35)
                                    .foregroundColor(.white)
                            }
                            .frame(width: 150, height: 35)
                            .padding(.vertical, 4)
                            .padding(.horizontal, 4)
                            .background(
                                LinearGradient(
                                    colors: [
                                        Color(red: 67/255, green: 56/255, blue: 202/255),
                                        Color(red: 33/255, green: 28/255, blue: 100/255)
                                    ],
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                            .cornerRadius(45)
                        }
                    }
                }
                //
                //                .background(Color.white)
                //                .cornerRadius(20)
                //                .shadow(color: .black.opacity(0.05), radius: 6)
            }
        }
        .padding()
        .background(Color(hex: "#F4F4F4"))
        .cornerRadius(33)
        
    }
    
    private var header: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                HStack(spacing: 8) {
                    Text(appointment.title)
                        .font(.custom("Urbanist-Medium", size: 16))
                    
//                    if appointment.isCompleted {
//                        Text("Completed")
//                            .font(.custom("Urbanist-Medium", size: 12))
//                            .foregroundColor(Color(hex: "#10B981"))
//                            .padding(.horizontal, 8)
//                            .padding(.vertical, 2)
//                            .background(Color(hex: "#D1FAE5"))
//                            .cornerRadius(8)
//                    }
                }
                
                Text(appointment.doctorName)
                    .font(.custom("Urbanist-Medium", size: 14))
                    .foregroundColor(Color(hex: "#374151"))
                
                Text(appointment.category)
                    .font(.custom("Urbanist-Medium", size: 14))
                    .foregroundColor(Color(hex: "#4338CA"))
            }
            
            Spacer()
            
            GeometryReader { geo in
                Button {
                    let frame = geo.frame(in: .named("ScheduleScreen"))
                    let anchor = CGPoint(
                        x: frame.midX - 80,
                        y: frame.maxY + 28
                    )
                    onMenuTap(anchor)
                } label: {
                    Image(isMenuOpen ? "DotButtonActive" : "DotButton")
                }
            }
            .frame(width: 40, height: 40)
        }
    }
    
    private func infoRow(icon: String, text: String) -> some View {
        HStack(alignment: .top) {
            Image(icon)
                .resizable()
                .frame(width: 30, height: 30)
            Text(text)
                .font(.custom("Urbanist-Regular", size: 14))
                .padding(.leading, 4)
                .padding(.trailing, 4)
                .padding(.top, 6)
        }
        //.padding(.leading, 6)
    }
}

struct AppointmentContextMenu: View {
    let proposedPosition: CGPoint
    let isCompleted: Bool
    let onDismiss: () -> Void
    let onAction: (AppointmentMenuAction) -> Void
    
    private let menuWidth: CGFloat = 180
    private let menuHeight: CGFloat = 120
    
    var body: some View {
        GeometryReader { geo in
            
            let safePosition = clampedPosition(
                proposed: proposedPosition,
                screen: geo.size
            )
            
            ZStack {
                Color.black.opacity(0.001)
                    .ignoresSafeArea()
                    .onTapGesture { onDismiss() }
                
                VStack(alignment: .leading, spacing: 14) {
                 
                    menuRow("RectangleBox",
                            "Mark As Complete",
                            showCheck: isCompleted
                    )
                    .onTapGesture {
                        onAction(.complete)
                    }
                    
                    menuRow("Pencil", "Reschedule")
                        .opacity(isCompleted ? 0.5 : 1)   // faded look
                        .allowsHitTesting(!isCompleted)   // 🔥 disable click
                        .onTapGesture {
                            if !isCompleted {
                                onAction(.reschedule)
                            }
                        }
                    
                    menuRow("Delete", "Delete", isDestructive: true)
                        .onTapGesture {
                            onAction(.delete)
                        }
                }
                
                .frame(width: menuWidth)
                .padding()
                .background(Color(hex: "#F4F4F4"))
                .cornerRadius(16)
                .shadow(radius: 10)
                .position(safePosition)
            }
        }
    }
    
    private func menuRow(
        _ icon: String,
        _ title: String,
        isDestructive: Bool = false,
        showCheck: Bool = false
    ) -> some View {
        
        HStack(spacing: 8) {
            
            Image(showCheck ? "FilledCheckBox" : icon)
                .resizable()
                .renderingMode(.original)
                .frame(width: 22, height: 22)
            
            Text(title)
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundColor(isDestructive ? .red : Color(hex: "#374151"))
            
            Spacer()
        }
        .padding(.vertical, 6)
        .contentShape(Rectangle()) // 👈 IMPORTANT (see Problem 2)
    }
    
    
    // MARK: - Clamp
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

struct MedicationContextMenu: View {
    let proposedPosition: CGPoint
    let onDismiss: () -> Void
    let onAction: (MedicationMenuAction) -> Void
    
    private let menuWidth: CGFloat = 180
    private let menuHeight: CGFloat = 90
    
    var body: some View {
        GeometryReader { geo in
            
            let safePosition = clampedPosition(
                proposed: proposedPosition,
                screen: geo.size
            )
            
            ZStack {
                Color.black.opacity(0.001)
                    .ignoresSafeArea()
                    .onTapGesture { onDismiss()
                        
                    }
                
                VStack(alignment: .leading, spacing: 14) {
                    
                    menuRow("Pencil", "Edit")
                        .contentShape(Rectangle())
                        .onTapGesture { onAction(.edit) }
                    
                    menuRow("Delete", "Delete", isDestructive: true)
                        .contentShape(Rectangle())
                        .onTapGesture {
                            onAction(.delete)
                        }
                    
                }
                .frame(width: menuWidth)
                .padding()
                .background(Color(hex: "#F4F4F4"))
                .cornerRadius(16)
                .shadow(radius: 10)
                .shadow(radius: 10)
                .position(safePosition)
            }
        }
    }
    
    private func menuRow(
        _ icon: String,
        _ title: String,
        isDestructive: Bool = false
    ) -> some View {
        HStack {
            Image(icon)
                .resizable()
                .frame(width: 20, height: 20)
            
            Text(title)
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundColor(isDestructive ? .red : Color(hex: "#374151"))
            
            Spacer()
        }
        .padding(.vertical, 6)
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
    ScheduleAppointmentView()
}
