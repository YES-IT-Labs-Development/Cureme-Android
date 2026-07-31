//
//  ScheduleMedicationsView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 30/12/25.
//

import SwiftUI

struct ScheduleMedicationsView: View {
    @StateObject private var viewModel = ScheduleMedicationsViewModel()
    @EnvironmentObject private var coordinator: Coordinator
    @State private var showFilter = false
    @State private var activeFilterPopup: FilterPopupType?
    @StateObject private var filterAppointmentVM = FilterAppointmentViewModel()
    @State private var showPopup: Bool = false
    @State private var popupAction: (() -> Void)? = nil
    @State private var deleteSource: DeleteSource?
    var resetSearchTrigger: Bool   // 👈 receive trigger
    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            
            ScrollView{
                VStack(spacing: 16) {
                    SearchBarView(
                        text: $viewModel.searchText,
                        onFilterTap: {
                            activeFilterPopup = .members
                        }
                    )
                    // 👇 MAIN LOGIC
                           .onChange(of: resetSearchTrigger) { _ in
                               viewModel.searchText = ""   // 🔥 RESET HERE
                           }
                    
                    if viewModel.filteredMedications.isEmpty {
                        
                        VStack(spacing: 12) {
                            Image(systemName: "calendar.badge.exclamationmark")
                                .resizable()
                                .frame(width: 50, height: 50)
                                .foregroundColor(.gray)
                            
                            Text("No Medication Found")
                                .font(.custom("Urbanist-Medium", size: 16))
                                .foregroundColor(.gray)
                            
                            //Text("You don’t have any scheduled appointments yet.")
                                //.font(.custom("Urbanist-Regular", size: 14))
                               // .foregroundColor(.gray)
                        }
                        .padding(.top, 100)
                        
                    } else {
                    
                        ForEach(viewModel.filteredMedications) { medication in
                            MedicationsCardView(
                                medication: medication,
                                isMenuOpen: viewModel.showMenu &&
                                viewModel.selectedMedicationID == medication.id
                            ) { point in
                                viewModel.menuPosition = point
                                viewModel.selectedMedicationID = medication.id
                                viewModel.selectedMedication = medication
                                viewModel.showMenu = true
                            }
                        }
                    }
                }
                .padding(.horizontal)
                .padding(.bottom, 110) // space for button
              
                
            }
            .disableScrollBounce()
            //  floatingScheduleButton
            .zIndex(1)
            
            if viewModel.medicationPopup != nil {
                ZStack {
                    Color.black.opacity(0.4)
                        .ignoresSafeArea()
                        .onTapGesture {
                            showPopup = false
                        }
                    
                    DeleteAppointmentPopUpView(
                        title: popupTitle,
                        message: popupMessage,
                        onClose: {
                            withAnimation {
                                viewModel.medicationPopup = nil
                            }
                        },
                        onDelete: {
 if let selected = viewModel.selectedMedication {
     viewModel.deleteMedicationAPI(medicationID: selected.appID) { success in
         if success {
             print("✅ Delete Success")
             withAnimation {
                 viewModel.medicationPopup = nil
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
                .zIndex(1000)
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
                        handleMedicationMenu(action)
                    }
                )
                .zIndex(999)
            }
            
            if viewModel.showMenu,
               let position = viewModel.menuPosition {
                
                MedicationsContextMenu(
                    proposedPosition: position,
                    onDismiss: {
                        viewModel.showMenu = false
                        viewModel.menuPosition = nil
                        viewModel.selectedMedicationID = nil
                    },
                    onAction: { action in
                        viewModel.showMenu = false
                        viewModel.menuPosition = nil
                        viewModel.selectedMedicationID = nil
                        handleMedicationMenu(action)
                    }
                )
                .zIndex(999)
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
        .coordinateSpace(name: "ScheduleScreen")
        //.background(Color(.systemGroupedBackground))
        
        .onAppear {
            // ALWAYS RESET SEARCH
            DispatchQueue.main.async {
                
            viewModel.searchText = ""

            viewModel.selectedMemberName = ""

            if viewModel.selectedTab == .medications {
                viewModel.getMedicationListAPI { success in
                    if success {
                        print("Medication List API called successfully")
                    } else {
                        print("API failed")
                    }
                }
            }
            }
        }
        .onDisappear {
            DispatchQueue.main.async {
            viewModel.medications.removeAll()
            //RESET FILTERS
            viewModel.searchText = ""
            viewModel.selectedTab = .medications
            viewModel.selectedMemberName = ""
            }
        }
        
       
        .sheet(item: $activeFilterPopup) { popup in
            switch popup {
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

            case .appointments:
                EmptyView()
            }
        }
    }
    
    private func resetSearch() {
        viewModel.searchText = ""
        viewModel.selectedMemberName = ""
    }
    
    private var popupTitle: String {
        guard viewModel.medicationPopup == .delete else { return "" }

        switch deleteSource {
        case .appointment:
            return "Delete Appointment?"
        case .medication:
            return "Delete Medication?"
        case .none:
            return ""
        }
    }

//    private var popupMessage: String {
//        guard viewModel.medicationPopup == .delete else { return "" }
//
//        switch deleteSource {
//        case .appointment:
//            return "Are you sure you want to delete Peter’s appointment? This action cannot be undone."
//        case .medication:
//            return "Are you sure you want to delete Rosy’s medication? This action cannot be undone."
//        case .none:
//            return ""
//        }
//    }
//    
    private var popupMessage: String {
        guard viewModel.medicationPopup == .delete else { return "" }

        let name = viewModel.selectedMedication?.memberName ?? "this patient"

        return "Are you sure you want to delete \(name)'s medication? This action cannot be undone."
    }
    
    private var floatingScheduleButton: some View {
        Button {
            //coordinator.push(.addMedicationView)
        } label: {
            Image("AddMedication")
                .foregroundColor(.white)
                .frame(width: 154, height: 60)
        }
        .padding(.trailing, 20)
        .padding(.bottom, 120)
    }
    
    private func handleMenuAction(_ action: MedicationMenuAction) {
        
        guard let selected = viewModel.selectedMedication else { return }
        
        print("📌 Selected API ID:", selected.appID)
        
        switch action {
        case .delete:
            deleteSource = .medication
            viewModel.medicationPopup = .delete
            
        case .edit:
            coordinator.push(.addMedicationView(flow: .mediReschedule))
            
        }
    }

    private func handleMedicationMenu(_ action: MedicationMenuAction) {
        
        guard let selected = viewModel.selectedMedication else {
            print("No medication selected")
            return
        }

        print("Selected API ID:", selected.appID)

        switch action {

        case .delete:
            deleteSource = .medication
            viewModel.medicationPopup = .delete

            DispatchQueue.main.asyncAfter(deadline: .now() + 0.15) {
                showPopup = true
            }

        case .edit:
            print("Edit clicked for ID:", selected.appID)
            
            UserDetail.shared.setID("\(selected.appID)")
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.15) {
                coordinator.push(.addMedicationView(flow: .mediReschedule))
            }
        }
    }
}

struct MedicationsCardView: View {
    let medication: ScheduleMedicationsModel
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
//                        Image(medication.image)
//                            .resizable()
//                            .scaledToFit()
//                            .frame(width: 22, height: 22)
                        
                        Image("Capsule")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 40, height: 40)
                    )
                
                VStack(alignment: .leading, spacing: 10) {
                    HStack{
                        Text("\(medication.title) - \(medication.dosage)")
                            .font(.custom("Urbanist-Medium", size: 16))
                                                
                        Spacer()
                        
                        GeometryReader { geo in
                            Button {
                                let frame = geo.frame(in: .named("ScheduleScreen"))
                                onMenuTap(
                                    CGPoint(
                                        x: frame.midX - 32,
                                        y: frame.maxY + 82
                                    )
                                )
                            } label: {
                                Image(isMenuOpen ? "DotButtonActive" : "DotButton")
                            }
                        }
                        .frame(width: 24, height: 24)
                        .padding(.trailing)
                    }
                    
                   // Text("For: \(medication.memberName)")
                    
                    Text("For: \(medication.memberName)")
                    
                        .font(.custom("Urbanist-Regular", size: 14))
                        .foregroundColor(Color(hex: "#4338CA"))
                    
                    Text("Medication Type: \(medication.medicationType)")
                        .font(.custom("Urbanist-Regular", size: 14))
                        .foregroundColor(.black)
                    
                    // MARK: Tags
                        tagView(text: medication.frequency)
                    
                    // MARK: Days
                    HStack(spacing: 6) {
                        Image("DaysImg")
                        ForEach(medication.days, id: \.self) { day in
                            pillView(text: day)
                        }
                    }
                    
                    // MARK: Times
                    HStack(alignment: .top, spacing: 6) {
                        Image("ScheduleTime")
                            .resizable()
                            .frame(width: 30, height: 30)
                        LazyVGrid(
                            columns: [
                                GridItem(.adaptive(minimum: 80), spacing: 8)
                            ],
                            spacing: 10
                        ) {
                            ForEach(medication.times, id: \.self) { time in
                                timeChipView(text: time)
                            }
                        }
                    }
                    
                    // MARK: Date Range
                    HStack(spacing: 6) {
                        Image("ScheduleCalendar")
                        Text(medication.startDate.toAppDateString())
                            .font(.custom("Urbanist-Regular", size: 14))
                        
                        Image("ScheduleCalendar")
                            .padding(.leading, 6)
                        Text(medication.endDate.toAppDateString())
                            .font(.custom("Urbanist-Regular", size: 14))
                           
                    }
                    
                    // MARK: Note
                    HStack(spacing: 6) {
                        Image("mage_note")
                        Text(medication.note)
                            .font(.custom("Urbanist-Regular", size: 14))
                    }
                }
            }
        }
        .padding()
        .background(Color(hex: "#F4F4F4"))
        .cornerRadius(24)
        .shadow(color: .black.opacity(0.05), radius: 6)
    }

    // MARK: Components
    private func tagView(text: String) -> some View {
        Text(text)
            .font(.custom("Urbanist-Medium", size: 12))
            .padding(.horizontal, 10)
            .padding(.vertical, 6)
            .background(Color(hex: "#4338CA").opacity(0.10))
            .foregroundColor(Color(hex: "#211C64"))
            .cornerRadius(12)
    }

    private func pillView(text: String) -> some View {
        Text(text)
            .font(.custom("Urbanist-Regular", size: 12))
            .padding(.horizontal, 10)
            .padding(.vertical, 6)
            .background(Color.gray.opacity(0.15))
            .cornerRadius(12)
    }
    
    private func timeChipView(text: String) -> some View {
        Text(text)
            .font(.custom("Urbanist-Regular", size: 14))
            .foregroundColor(.black)
            .padding(.horizontal, 10)
            .padding(.vertical, 7)
            .background(
                       Capsule()
                           .fill(Color.white)
                   )
            .overlay(
                RoundedRectangle(cornerRadius: 22)
                    .stroke(Color(hex: "#4338CA"), lineWidth: 1)
            )
    }
}

struct MedicationsContextMenu: View {
    let proposedPosition: CGPoint
    let onDismiss: () -> Void
    let onAction: (MedicationMenuAction) -> Void
    private let menuWidth: CGFloat = 100
    private let menuHeight: CGFloat = 80

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

                VStack(alignment: .center, spacing: 14) {
                    menuRow("Pencil", "Edit")
                        .contentShape(Rectangle())
                        .onTapGesture {
                            onAction(.edit)
                        }

                    menuRow("Delete", "Delete", isDestructive: true)
                        .contentShape(Rectangle())
                        .onTapGesture {
                            onAction(.delete)
                        }
                }
                .frame(width: menuWidth)
                .padding()
                .background(Color.white)
                .cornerRadius(20)
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

        HStack(spacing: 10) {
            Image(icon)
                .resizable()
                .renderingMode(.template)
                .frame(width: 20, height: 20)
                .foregroundColor(isDestructive ? .red : Color(hex: "#374151"))

            Text(title)
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundColor(isDestructive ? .red : Color(hex: "#374151"))

            Spacer()
        }
        .padding(.vertical, 6)
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

//#Preview {
//    ScheduleMedicationsView()
//}
