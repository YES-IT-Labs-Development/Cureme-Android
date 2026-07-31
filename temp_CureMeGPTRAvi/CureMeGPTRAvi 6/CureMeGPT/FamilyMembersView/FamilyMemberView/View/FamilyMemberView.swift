//
//  FamilyMemberView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 05/01/26.
//

import SwiftUI
import SDWebImageSwiftUI

struct FamilyMembersView: View {
   // @State private var searchText = ""
    @EnvironmentObject private var coordinator: Coordinator
    @StateObject private var viewModel = FilterAppointmentViewModel()//FilterMembersCategoryViewModel()
   
    @EnvironmentObject var tabVM: TabViewModel
    @State private var showPopup = false
    @State private var showMenu = false
    @State private var selectedMemberID: Int?
    @State private var menuPosition: CGPoint = .zero
    @State private var showDeletePopup = false
    @State private var memberToDelete: FamilyMemberModels?
    @State private var refreshID = UUID()
    
    var body: some View {
        ZStack{
            ScrollView {
                VStack(spacing: 24) {
                    headerView
                    SummaryCardsView(appointments: viewModel.result?.totalFamilyAppointmentCount ?? 0, medications: viewModel.result?.totalFamilyMedicationCount ?? 0)
                    searchBar
                    
                    // Family Members List
                    VStack(spacing: 16) {

                       // ForEach(viewModel.memberList) { member in
                        ForEach(viewModel.filteredMembers) { member in
                            FamilyMemberCardView(
                                member: member,
                                isMenuOpen: showMenu && selectedMemberID == member.id
                            ) { point in
                                menuPosition = point
                                selectedMemberID = member.id
                                showMenu = true
                            }
                        }
                        
                        if viewModel.filteredMembers.isEmpty {
                            Text("No members found")
                                .foregroundColor(.gray)
                                .padding()
                        }
                    }
                }
                .padding()
                .padding(.bottom, 100)
            }
            .disableScrollBounce()
            .sheet(isPresented: $showPopup) {
                FilterMembersCategoryView(
                    viewModel: viewModel,

                    onSelect: { selectedMember in
                        print("Selected filter:", selectedMember.name ?? "")
                        viewModel.selectedMemberDetail = selectedMember
                        viewModel.searchText = selectedMember.name ?? ""  //CLEAR SEARCH
                    }
                                    )
                .presentationDetents([.height(380)])
                .presentationCornerRadius(24)
            }
          
            .onAppear {
//                DispatchQueue.main.async {
//                    tabVM.isTabBarHidden = false
//                }
               
                viewModel.searchText = ""
                refreshID = UUID()   // 🔥 force full refresh
                viewModel.family_member_listAPI { success in
                    if success {
                        print("family_member_list Loaded")
                    }
                }
            }
            
//            .onChange(of: viewModel.searchText) { newValue in
//                if !newValue.isEmpty {
//                    viewModel.selectedMember = nil
//                }
//            }
           
            if showMenu {
                familyMemberContextMenu(
                    proposedPosition: menuPosition,
                    onDismiss: {
                        withAnimation {
                            showMenu = false
                            selectedMemberID = nil
                        }
                    },
                    onViewTap: {
                        if let id = selectedMemberID {
                            print("View Profile ID:", id)
                            UserDetail.shared.setID("\(id)")
                           }
                        coordinator.push(.familyPersonDetailView)
                        showMenu = false
                        selectedMemberID = nil
                    },
                    onShareTap: {  // DELETE BUTTON
                        if let id = selectedMemberID {
                               print("View Profile ID:", id)
                           }
                        memberToDelete = viewModel.memberList.first { $0.id == selectedMemberID }
                        showDeletePopup = true
                        showMenu = false
                        selectedMemberID = nil
                    }
                )
            }
            
            if showDeletePopup, let member = memberToDelete {
                Color.black.opacity(0.4)  // Dimmed background
                    .ignoresSafeArea()
                    .onTapGesture {
                        showDeletePopup = false
                        memberToDelete = nil
                    }
                
                MemberDeletedSuccPopUpView(
                    title: "Delete Member?",
                    message: "Are you sure you want to delete \(member.fullName ?? "")’s profile? This action cannot be undone.",
                    onClose: {
                        showDeletePopup = false
                        memberToDelete = nil
                    },
                    cancel: {
                        showDeletePopup = false
                        memberToDelete = nil
                    },

                    deleteMember: {
                        guard let id = member.id else { return }
                        
                        viewModel.deleteFamilyMemberAPI(memberId: id) { success in
                            if success {
                                print("Deleted \(member.fullName ?? "")")
                            }
                        }
                        
                        showDeletePopup = false
                        memberToDelete = nil
                    }
                )
                .transition(.scale)
                .zIndex(1)
               
            }
            if viewModel.showActivity {
                CustomLoderView(isVisible: $viewModel.showActivity)
                    .ignoresSafeArea()
            }
              
        }
    }
}

extension FamilyMembersView {
    private var headerView: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("Family Members")
                .font(.custom("Urbanist-Medium", size: 20))
                .foregroundColor(.black)
                .padding(.bottom)
            HStack(spacing: 10) {
                Image("FamilyIcon")
                    .resizable()
                    .frame(width: 48, height: 63)
                
                VStack(alignment: .leading, spacing: 4) {
                    Text("Family Profiles")
                        .font(.custom("Urbanist-Regular", size: 26))
                        .foregroundColor(.black)
                    
                    Text("\(viewModel.memberList.count) Members Total")
                        .font(.custom("Urbanist-Regular", size: 16))
                        .foregroundColor(Color(hex: "#909090"))
                }
                
                Spacer()
                
                Button(action: {
                    coordinator.push(.completeProfileView(flow: .addFamilyMember))
                }) {
                    Image("AddMember")
                        .resizable()
                        .frame(width: 67, height: 67)
                        .clipShape(Circle())
                }
            }
        }
    }
}
struct SummaryCardsView: View {
    
    var appointments: Int
       var medications: Int

    var body: some View {
        HStack(spacing: 16) {

            summaryCard(
                image: "ScheduleCalendar",
                title: "Appointments",
                description: "Track and manage your doctor visits.",
                count: "\(appointments)",
                color: Color(hex: "#4338CA").opacity(0.20),
                countColor: Color(hex: "#4338CA")
            )

            summaryCard(
                image: "Capsule",
                title: "Medications",
                description: "Stay on top of your prescriptions.",
                count: "\(medications)",
                color: Color(hex: "#1ABC9C").opacity(0.20),
                countColor: Color(hex: "#1ABC9C")
            )
        }
    }

    private func summaryCard(image: String, title: String,description: String, count: String, color: Color, countColor: Color) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(image)
                    .resizable()
                    .frame(width: 44, height: 44)
                
                Spacer()
                
                Text(count)
                    .font(.custom("Urbanist-SemiBold", size: 56))
                    .foregroundColor(countColor)
            }
            Text(title)
                .font(.custom("Urbanist-Medium", size: 14))
                .foregroundColor(.black)
            
            Text(description)
                .font(.custom("Urbanist-Italic", size: 12))
                .foregroundColor(Color(hex: "#3C3C3C"))
        }
        .padding()
        .frame(maxWidth: .infinity)
        .background(color)
        .cornerRadius(20)
    }
}

extension FamilyMembersView {
    private var searchBar: some View {
        HStack(spacing: 4) {
//            HStack {
//                Image(systemName: "magnifyingglass")
//                    .foregroundColor(.gray)
//                TextField("Search", text: $viewModel.searchText)
//                    .foregroundColor(.black)
//                
//                Spacer()
//            }
            
            TextField("Search", text: $viewModel.searchText)
                .textFieldStyle(.plain)
                .overlay(
                    HStack {
                        Spacer()
                        if !viewModel.searchText.isEmpty {
                            Button {
                                viewModel.resetSearch()
                            } label: {
                                Image(systemName: "xmark.circle.fill")
                                    .foregroundColor(.gray)
                            }
                        }
                    }
                )
            .padding()
            .background(Color(hex: "#F4F4F4"))
            .cornerRadius(40)
            
            Spacer()
            Button(action: {
                showPopup = true
            }) {
                Image("Filter")
                    .resizable()
                    .frame(width: 40, height: 56)
            }
        }
    }
}

struct FamilyMemberCardView: View {
    let member: FamilyMemberModels
    let isMenuOpen: Bool
    let onMenuTap: (CGPoint) -> Void

    var body: some View {
        HStack(spacing: 16) {
            let hasValidImage = member.profileImage != nil && !(member.profileImage?.isEmpty ?? true) && member.profileImage != "profile_image"

            if hasValidImage, let url = URL(string: member.profileImage?.imgFullPath() ?? "") {
                AsyncImage(url: url) { phase in
                    if let image = phase.image {
                        image
                            .resizable()
                            .scaledToFill()
                    } else {
                        Color.gray.opacity(0.2)
                    }
                }
                .frame(width: 95, height: 100)
                .cornerRadius(25)
                .clipped()
            } else {
                Color.gray.opacity(0.2)
                    .frame(width: 95, height: 100)
                    .cornerRadius(25)
            }

            VStack(alignment: .leading, spacing: 6) {
                // Name + Age
                HStack(spacing: 8) {
                    Text(member.fullName ?? "")
                        .font(.custom("Urbanist-Medium", size: 18))
                        .foregroundColor(.black)
                        .lineLimit(1)

                    if let age = member.age, !age.isEmpty {
                        Text(ageDisplay(age))
                            .font(.custom("Urbanist-Medium", size: 12))
                            .foregroundColor(Color(hex: "#4338CA"))
                            .padding(.vertical, 4)
                            .padding(.horizontal, 10)
                            .background(Color(hex: "#4338CA").opacity(0.15))
                            .cornerRadius(10)
                    }
                }

                Text(member.relationship ?? "")
                    .font(.custom("Urbanist-Regular", size: 16))
                    .foregroundColor(Color(hex: "#697383"))
                    .lineLimit(1)

                HStack(spacing: 16) {
                    HStack(spacing: 8) {
                        Circle()
                            .fill(Color.white)
                            .frame(width: 36, height: 36)
                            .overlay(
                                Image("ScheduleCalendar")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 35, height: 35)
                            )
                        
                        Text("\(member.appointmentCount ?? 0)")
                            .font(.custom("Urbanist-Medium", size: 18))
                            .foregroundColor(.black)
                    }

                    HStack(spacing: 8) {
                        Circle()
                            .fill(Color.white)
                            .frame(width: 36, height: 36)
                            .overlay(
                                Image("Capsule")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 35, height: 35)
                            )

                        Text(medicationDisplay)
                            .font(.custom("Urbanist-Medium", size: 18))
                            .foregroundColor(.black)
                    }
                }
                .padding(.top, 4)
            }

            Spacer()

            // Menu dot button
//            GeometryReader { geo in
//                Button {
//                    let frame = geo.frame(in: .global)
//                    onMenuTap(
//                        CGPoint(
//                            x: frame.midX - 110,
//                            y: frame.maxY + 22
//                        )
//                    )
//                } label: {
//                    Image(isMenuOpen ? "DotButtonActive" : "DotButton")
//                       // .resizable()
//                        //.scaledToFit()
//                        .padding(.trailing, -10)
//                        .padding(.top, -30)
//                        .frame(width: 40, height: 40)
//                                           
//                       
//                }
//            }
           // .frame(width: 56, height: 56)
            
            GeometryReader { geo in
                Button {
                    let frame = geo.frame(in: .global)
                    onMenuTap(
                        CGPoint(
                            x: frame.midX - 60,
                            y: frame.maxY + 25
                        )
                    )
                } label: {
                    Image(isMenuOpen ? "DotButtonActive" : "DotButton")
                }
            }
            .frame(width: 24, height: 24)
            .padding(.trailing)
            .padding(.top, -50)
        }
        .padding(16)
        .background(Color(hex: "#4338CA").opacity(0.08))
        .cornerRadius(28)
    }

    private func ageDisplay(_ age: String) -> String {
        if age.lowercased().contains("yrs") || age.lowercased().contains("year") {
            return age
        }
        return "\(age) yrs"
    }

    private var medicationDisplay: String {
        let total = member.medicationCount ?? 0
        if total > 0 {
            let completed = max(0, total - 1)
            return "\(completed)/\(total)"
        }
        return "0/0"
    }
}

extension FamilyMemberModel {
    static let previewData: [FamilyMemberModel] = [
        FamilyMemberModel(
            name: "James Logan",
            age: 40,
            relationship: .selfUser,
            imageName: "profile",
            progress: "2/2",
            date: "2",
        ),
        FamilyMemberModel(
            name: "Rose Logan",
            age: 35,
            relationship: .spouse,
            imageName: "Profile1",
            progress: "2/4",
            date: "2",
        ),
        FamilyMemberModel(
            name: "Peter Logan",
            age: 17,
            relationship: .son,
            imageName: "Profile2",
            progress: "2/3",
            date: "2",
        )
    ]
}

struct familyMemberContextMenu: View {
    let proposedPosition: CGPoint
    let onDismiss: () -> Void
    let onViewTap: () -> Void
    let onShareTap: () -> Void

    private let menuWidth: CGFloat = 180
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
                        title: "View Profile",
                        action: onViewTap
                    )

                    menuRow(
                        icon: "Delete",
                        title: "Delete",
                        isDestructive: true,
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
        isDestructive: Bool = false,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            HStack(spacing: 12) {
                Image(icon)
                Text(title)
                    .font(.custom("Urbanist-Medium", size: 16))
                    .foregroundColor(isDestructive ? .red : Color(hex: "#374151"))
                Spacer()
            }
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
    FamilyMembersView()
}


