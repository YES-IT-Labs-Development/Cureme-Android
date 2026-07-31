//
//  GeneralProfileView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 04/12/25.
//

import SwiftUI

struct GeneralProfileView: View {
    @EnvironmentObject private var coordinator: Coordinator
    @StateObject private var vm = GeneralProfileViewModel()
    let flow: ProfileFlowType
    
    var body: some View {
        ZStack {
            VStack(spacing: 22) {
                // MARK: HEADER
                ProfileHeaderView(
                    title: flow.title,
                    showSkip: flow.showSkipButton,
                    onBack: {
                        flow.onBackAction(coordinator)
                    },
                    onSkip: {
                        coordinator.push(.historyProfileView(flow: flow))
                    }
                )
                
                ScrollView(showsIndicators: false) {
                    VStack{
                        if flow.title == "Edit Profile" || flow.title == "Edit Family Member Details" {
                            
                            HStack(spacing: 45) {
                                StepTab(icon: "RightCheckMark", title: "Personal", selected: true)
                                StepTab(icon: "RightCheckMark", title: "General", selected: true)
                                StepTab(icon: "RightCheckMark", title: "History", selected: true)
                                StepTab(icon: "RightCheckMark", title: "Documents", selected: true)
                            }
                            .padding(.top, 10)
                            .padding(.bottom, 26)
                            
                        }else{
                            HStack(spacing: 45) {
                                StepTab(icon: "RightCheckMark", title: "Personal", selected: true)
                                StepTab(icon: "GeneralSelected", title: "General", selected: true)
                                StepTab(icon: "HistoryUnSelected", title: "History")
                                StepTab(icon: "DocumentsUnSelected", title: "Documents")
                            }
                            .padding(.top, 10)
                            .padding(.bottom, 0)
                        }
                        VStack(spacing: 28) {
                            // MARK: - Section 1: Blood Group
                            Section {
                                VStack(alignment: .leading, spacing: 8) {
                                    CustomTitle("Blood Group *")
                                        .font(.custom("Urbanist-Regular", size: 15))
                                        .foregroundColor(.black)
                                    
                                    CustomDropdown(selected: $vm.selectedBloodGroup,
                                                   options: vm.bloodGroups)
                                    .onChange(of: vm.selectedBloodGroup) { _ in
                                        vm.bloodGroupError = nil
                                    }
                                    if let error = vm.bloodGroupError {
                                        Text(error)
                                            .font(.caption)
                                            .foregroundColor(.red)
                                    }
                                    
                                }
                            }
                            
                            // MARK: - Section 2: Known Allergies
                            Section {
                                VStack(alignment: .leading, spacing: 8) {
                                    CustomTitle("Known Allergies *")
                                        .font(.custom("Urbanist-Regular", size: 15))
                                        .foregroundColor(.black)
                                        .padding(.leading, 1)
                                    
                                    WrapChipsView(vm: vm)
                                    if let error = vm.allergyError {
                                        Text(error)
                                            .font(.caption)
                                            .foregroundColor(.red)
                                    }
                                }
                            }
                            
                            // MARK: - Section 3: Emergency Contact Name
                            Section {
                                VStack(alignment: .leading, spacing: 8) {
                                    HStack{
                                        Text("Emergency Contact Name")
                                        
                                            .font(.custom("Urbanist-Regular", size: 15))
                                            .foregroundColor(.black)
                                        
                                        Text("(Optional)")
                                            .font(.custom("Urbanist-Italic", size: 15))
                                            .foregroundColor(.black)
                                    }
                                    
                                    TextField("e.g., Bob Dsouza", text: $vm.emergencyName)
                                        .font(.custom("Urbanist-Regular", size: 15))
                                        .padding()
                                        .background(RoundedRectangle(cornerRadius: 50).stroke(Color(hex: "#697383"), lineWidth: 0.6))
                                        .onChange(of: vm.emergencyName) { _ in
                                            vm.nameError = nil
                                        }
                                    if let error = vm.nameError {
                                        Text(error)
                                            .font(.caption)
                                            .foregroundColor(.red)
                                    }
                                }
                            }
                            
                            // MARK: - Section 4: Emergency Phone Number
                            Section {
                                VStack(alignment: .leading, spacing: 8) {
                                    HStack{
                                        Text("Emergency Phone Number")
                                            .font(.custom("Urbanist-Regular", size: 15))
                                            .foregroundColor(.black)
                                        
                                        Text("(Optional)")
                                            .font(.custom("Urbanist-Italic", size: 15))
                                            .foregroundColor(.black)
                                    }
                                    TextField("e.g., 555 945 325", text: $vm.emergencyPhone)
                                        .font(.custom("Urbanist-Regular", size: 15))
                                        .keyboardType(.numberPad)
                                        .padding()
                                        .background(RoundedRectangle(cornerRadius: 50).stroke(Color(hex: "#697383"), lineWidth: 0.6))
                                        .onChange(of: vm.emergencyPhone) { newValue in
                                            let filtered = newValue.filter { $0.isNumber }
                                            let limited = String(filtered.prefix(10))
                                            if vm.emergencyPhone != limited {
                                                vm.emergencyPhone = limited
                                            }
                                            vm.contactError = nil
                                        }
                                    if let error = vm.contactError {
                                        Text(error)
                                            .font(.caption)
                                            .foregroundColor(.red)
                                    }
                                }
                            }
                            
                            Button(action: {
                                
                                print("\n Button Clicked")
                                print(" Flow Type: \(flow)")
                                
                                switch flow {
                                    
                                case .profileSetup:
                                    print("Action: Setup General Profile शुरू")
                                    
                                    if vm.validateForm() {
                                        print("Form Validated")
                                        
                                        vm.completeGeneralProfileAPI { success in
                                            print("API: Complete Profile → \(success ? "Success" : "Failure")")
                                            
                                            if success {
                                                print("Navigation: History Profile (Profile Setup)")
                                                coordinator.push(.historyProfileView(flow: flow))
                                            }
                                        }
                                    } else {
                                        print("Validation Failed")
                                    }
                                    
                                case .addFamilyMember:
                                    print(" Action: Add Family Member Triggered")
                                    
                                    if vm.validateForm() {
                                        print("Form Validated")
                                        // Future API
                                        vm.addfamilymembergeneralAPI { success in
                                            print("API: Add Member → \(success)")
                                            coordinator.push(.historyProfileView(flow: flow))
                                        }
                                    }
                                    
                                case .editProfile:
                                    print("Action: Edit General Profile शुरू")
                                    
                                    if vm.validateForm() {
                                        print("Form Validated")
                                        
                                        vm.ApiforupdateGeneralprofile { success in
                                            print("API: Update Profile → \(success ? "Success" : "Failure")")
                                            
                                            if success {
                                                print("Navigation: History Profile (Edit Profile)")
                                                coordinator.push(.historyProfileView(flow: flow))
                                            }
                                        }
                                    }
                                case .editFamilyMember:
                                    print("Action: Edit Family Member Triggered")
                                    
                                    if vm.validateForm() {
                                        print("Form Validated")
                                        
                                        vm.ApiforUpdateMemberGeneralprofile{ success in
                                            print("API: Update Member General Profile → \(success ? "Success" : "Failure")")
                                            
                                            if success == true {
                                                coordinator.push(.historyProfileView(flow: flow))
                                            }
                                        }
                                    }
                                }
                                
                            }) {
                                Text("Save & Continue")
                                    .font(.custom("Urbanist-SemiBold", size: 16))
                                    .foregroundColor(.white)
                                    .frame(maxWidth: .infinity)
                                    .padding()
                                    .background(
                                        Image("BackgroundBtn") // Asset name
                                             .resizable()
                                           .scaledToFill() )
                                    .cornerRadius(30)
                            }
                            //.padding(.horizontal)
                            .padding(.vertical)
                            
                        }
                        .padding(.horizontal)
                        .padding(.top, 20)
                    }
                }
                .disableScrollBounce()
            }
            
            .customAlert(
                      isPresented: $vm.isPresentAlert,
                      message: vm.errorMessage ?? "Error"
                  ) {
                      print("OK tapped")
                  }
//            .alert(isPresented: $vm.isPresentAlert) {
//                Alert(title: Text(vm.errorMessage ?? ""))
//            }
     
            .onAppear {
                if flow == .profileSetup  {
                    
                } else if flow == .editProfile {
                    
                    vm.apiGetGeneralProfile { success in
                        if success {
                            print("Persoal Profile Data Loaded")
                        }
                    }
                } else if flow == .editFamilyMember {
                    
                    vm.getFamilyGeneralProfileAPI{ success in
                        if success {
                            print("Family General Profile Data Loaded")
                        }
                    }
                }
                print("Current Flow: \(flow)")
            }
            if vm.showActivity {
                CustomLoderView(isVisible: $vm.showActivity)
                    .ignoresSafeArea()
            }
        }
        .keyboardDoneButton()
        .onDisappear {
            UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
        }
    }
}

struct AllergyChip: View {
    let text: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Text(text)
            .padding(.horizontal, 14)
            .padding(.vertical, 8)
            .font(.custom("Urbanist-Regular", size: 14))
        
            .background(
                RoundedRectangle(cornerRadius: 20)
                    .stroke(isSelected ? Color(hex: "#697383") : Color(hex: "#697383"), lineWidth: 0.6)
                    .background(
                        isSelected ? Color(hex: "#996BFE").opacity(0.10) : Color.clear
                    )
                    .cornerRadius(56)
            )
            .foregroundColor(isSelected ? Color(hex: "#4338CA") : Color(hex: "#697383"))
            .onTapGesture { action() }
    }
}

struct CustomDropdown: View {
    @Binding var selected: String
    let options: [String]
    
    @State private var isExpanded = false
    
    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            
            Button(action: { withAnimation { isExpanded.toggle() } }) {
                HStack {
                    Text(selected.isEmpty ? "Select" : selected)
                        .foregroundColor(selected.isEmpty ? .gray : .black)
                        .font(.custom("Urbanist-Regular", size: 15))
                    Spacer()
                    
                    Image("DropDown")
                        .rotationEffect(.degrees(isExpanded ? 180 : 0))
                }
                .padding()
                .overlay(
                        RoundedRectangle(cornerRadius: isExpanded ? 50 : 50)
                            .stroke(Color.gray.opacity(0.4))
                    )
            }
            
            if isExpanded {
                VStack(alignment: .leading, spacing: 8) {
                    ForEach(options, id: \.self) { item in
                        HStack {
                            Text(item)
                                .font(.custom("Urbanist-Regular", size: 14))
                                .foregroundColor(selected == item ? .white : .black)
                            Spacer()
                        }
                        .padding(.horizontal, 12)
                        .frame(height: 40)
                        .background(
                            Group {
                                if selected == item {
                                    LinearGradient(
                                        colors: [
                                            Color(red: 67/255, green: 56/255, blue: 202/255),
                                            Color(red: 33/255, green: 28/255, blue: 100/255)
                                        ],
                                        startPoint: .leading,
                                        endPoint: .trailing
                                    )
                                } else {
                                    Color.clear
                                }
                            }
                        )
                        .cornerRadius(20)
                        .contentShape(Rectangle())
                        .onTapGesture {
                            selected = item
                            withAnimation {
                                isExpanded = false
                            }
                        }
                    }
                }
                .padding(8)
                .background(Color.white)
                .clipShape(
                    RoundedRectangle(cornerRadius: 20)
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(Color.gray.opacity(0.4))
                )
                .padding(.top, 10)
                .padding(.horizontal, 2)
            }
//            if isExpanded {
//                VStack(alignment: .leading, spacing: 0) {
//                    ForEach(options, id: \.self) { item in
//                        Text(item)
//                            .padding()
//                            .frame(maxWidth: .infinity, alignment: .leading)
//                            .background(Color.white)
//                            .onTapGesture {
//                                selected = item
//                                withAnimation { isExpanded = false }
//                            }
//                    }
//                }
//                .background(RoundedRectangle(cornerRadius: 5).stroke(Color.gray.opacity(0.4)))
//                .padding(.top, 4)
//                .padding(.horizontal, 10)
//            }
        }
       
    }
}

struct WrapChipsView: View {
    @ObservedObject var vm: GeneralProfileViewModel
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Chips
            FlexibleView(
                availableWidth: UIScreen.main.bounds.width - 40,
                data: vm.allergyOptions,
                spacing: 12,
                alignment: .leading
                
            ) { item in
                AllergyChip(
                    text: item,
                    isSelected: vm.selectedAllergies.contains(item)
                ) {
                    vm.toggleAllergy(item)
                }
                
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            
            if vm.selectedAllergies.contains("Others") {
                
                TextField("Write allergy", text: $vm.otherAllergyInput)
                    .padding()
                    .background(
                        RoundedRectangle(cornerRadius: 50)
                            .stroke(Color(hex: "#697383"), lineWidth: 0.6)
                    )
                    .onChange(of: vm.otherAllergyInput) { newValue in
                        vm.updateOtherAllergyFromInput(newValue)
                        vm.otherAllergyError = nil
                    }
                
                if let error = vm.otherAllergyError {
                    Text(error)
                        .font(.caption)
                        .foregroundColor(.red)
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.leading, 0)
    }
}

struct FlexibleView<Data: Collection, Content: View>: View where Data.Element: Hashable {
    
    let availableWidth: CGFloat
    let data: Data
    let spacing: CGFloat
    let alignment: HorizontalAlignment
    let content: (Data.Element) -> Content
    
    @State private var elementsSize: [Data.Element: CGSize] = [:]
    
    var body: some View {
        VStack(alignment: alignment, spacing: spacing) {
            ForEach(computeRows(), id: \.self) { row in
                HStack(spacing: spacing) {
                    ForEach(row, id: \.self) { item in
                        content(item)
                            .fixedSize()
                            .background(
                                GeometryReader { geo in
                                    Color.clear.onAppear {
                                        elementsSize[item] = geo.size
                                    }
                                }
                            )
                    }
                }
            }
        }
    }
    
    private func computeRows() -> [[Data.Element]] {
        var rows: [[Data.Element]] = [[]]
        var currentWidth: CGFloat = 0
        
        for item in data {
            let size = elementsSize[item, default: CGSize(width: availableWidth, height: 40)]
            
            if currentWidth + size.width + spacing > availableWidth {
                rows.append([item])
                currentWidth = size.width + spacing
            } else {
                rows[rows.count - 1].append(item)
                currentWidth += size.width + spacing
            }
        }
        return rows
    }
}

#Preview {
    GeneralProfileView(flow: .profileSetup)
}
