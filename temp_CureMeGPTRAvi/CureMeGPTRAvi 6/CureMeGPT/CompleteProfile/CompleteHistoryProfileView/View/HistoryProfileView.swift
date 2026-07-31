//
//  HistoryProfileView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 04/12/25.
//

import SwiftUI

struct HistoryProfileView: View {
    @EnvironmentObject private var coordinator: Coordinator
    @StateObject private var vm = HistoryProfileViewModel()
    let flow: ProfileFlowType
    
    var body: some View {
        ZStack {
            VStack(spacing: 22) {
            // MARK: HEADER
                
                ProfileHeaderView(
                    title: flow.title,
                    showSkip: flow.showSkipButton,
                    onBack: {
                        coordinator.pop()
                    },
                    onSkip: {
                        coordinator.push(.documentsView(flow: flow))
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
                                StepTab(icon: "RightCheckMark", title: "General", selected: true)
                                StepTab(icon: "HistorySelected", title: "History", selected: true)
                                StepTab(icon: "DocumentsUnSelected", title: "Documents")
                            }
                            .padding(.top, 10)
                            .padding(.bottom, 40)
                        }
                    }
                    // MARK: Chronic Conditions
                    HStack(spacing: 8) {
                        CustomTitle("Chronic Conditions *")
                            .foregroundColor(.black)
                            .font(.custom("Urbanist-Regular", size: 15))
                        Spacer()
                    }
                    
                    conditionsSection
                        .padding(.horizontal, 1)
                    if let error = vm.chronicError {
                        Text(error)
                            .foregroundColor(.red)
                            .font(.caption)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.leading, 4)
                    }
                    
                    // MARK: Surgical History
                    VStack(alignment: .leading, spacing: 6) {
                        HStack {
                            Text("Surgical History")
                                .font(.custom("Urbanist-Regular", size: 15))
                                .foregroundColor(.black)

                            Text("(Optional)")
                                .font(.custom("Urbanist-Italic", size: 15))
                                .foregroundColor(.black)
                        }
                        .padding(.top, 16)
                        .padding(.bottom, 10)

                        VStack(alignment: .leading, spacing: 6) {
                            ZStack(alignment: .topLeading) {
                                if vm.form.surgicalHistory.isEmpty{
                                    Text("Any previous surgeries or major medical procedures...")
                                        .foregroundColor(.gray)
                                        .font(.custom("Urbanist-Regular", size: 15))
                                        
                                        
                                        .padding(.horizontal, 10)
                                        .padding(.vertical, 14)
                                }
                                
                                TextEditor(text: $vm.form.surgicalHistory)
                                    .frame(height: 90)
                                    .font(.custom("Urbanist-Regular", size: 15))
                                    .padding(8)
                                    .background(Color.clear)   // IMPORTANT
                                    .scrollContentBackground(.hidden)
                               
                            }
                            
                            .overlay(
                                RoundedRectangle(cornerRadius: 20)
                                    .stroke(Color(hex: "#697383"), lineWidth: 0.6)
                            )
                            if let error = vm.surgicalError {
                                  Text(error)
                                      .foregroundColor(.red)
                                      .font(.caption)
                                      .frame(maxWidth: .infinity, alignment: .leading)
                                      .padding(.leading, 4)
                              }
                        }
                        .padding(.horizontal, 1)
                        .frame(height: 110)
                        
                    }
                    
                    // MARK: Current Medications
                    medicationsSection
                        .padding(.horizontal, 1)
                    if let error = vm.medicationError {
                          Text(error)
                              .foregroundColor(.red)
                              .font(.caption)
                              .frame(maxWidth: .infinity, alignment: .leading)
                              .padding(.leading, 4)
                      }
                    
                    // MARK: Current Supplements
                    supplementsSection
                        .padding(.horizontal, 1)
                    if let error = vm.supplementError {
                          Text(error)
                              .foregroundColor(.red)
                              .font(.caption)
                              .frame(maxWidth: .infinity, alignment: .leading)
                              .padding(.leading, 4)
                      }
                    
                    getStartedButton
                        .padding(.horizontal, 3)
                }
                .padding()
                .disableScrollBounce()
            }
            
            .onAppear {
                if flow == .profileSetup  {
                    
                } else if flow == .editProfile {
                    
                    vm.getHistoryProfile { success in
                        if success {
                            print("History Profile Data Loaded")
                        }
                    }
                    
                } else if flow == .editFamilyMember {
                    
                    vm.getFamilyHistoryProfile { success in
                        if success {
                            print("History family Data Loaded")
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

    var getStartedButton: some View {

        Button(action: {
            
            print("\n🔘 Button Clicked")
            print("👉 Flow Type: \(flow)")
            
            switch flow {
                
            case .profileSetup:
                print("Action: Setup General Profile शुरू")
                            if vm.validateForm() {
                
                                vm.completeHistoryProfileAPI { success in
                                    if success {
                                        print("Saved")
                                        coordinator.push(.documentsView(flow: flow))
                                    }
                                }
                
                            }
            case .addFamilyMember:
                print(" Action: Add Family Member Triggered")
                if vm.validateForm() {
    
                    vm.addFamilyMemberHistoryProfileAPI { success in
                        if success {
                            print("Saved")
                            coordinator.push(.documentsView(flow: flow))
                        }
                    }
    
                }

            case .editProfile:
                print("Action: Edit General Profile")
                if vm.validateForm() {
    
                    vm.updateGeneralProfileHistoryAPI { success in
                        if success {
                            print("Saved")
                            coordinator.push(.documentsView(flow: flow))
                        }
                    }
    
                }

                
            case .editFamilyMember:
                print("Action: Edit Family Member Triggered")
                
                if vm.validateForm() {
    
                    vm.updateFamilyHistoryProfileAPI { success in
                        if success {
                            print("Saved")
                            coordinator.push(.documentsView(flow: flow))
                        }
                    }
    
                }
            }
            
        }){
            Text("Save & Continue")
                .font(.custom("Urbanist-SemiBold", size: 16))
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .padding(.horizontal)
                .padding()
                .background(
                    Image("BackgroundBtn") // Asset name
                         .resizable()
                       .scaledToFill() )
                .cornerRadius(30)
//                .background(
//                    LinearGradient(
//                        colors: [
//                            Color(red: 67/255, green: 56/255, blue: 202/255),
//                            Color(red: 33/255, green: 28/255, blue: 100/255)
//                        ],
//                        startPoint: .leading,
//                        endPoint: .trailing
//                    )
//                )
               
                
        }
        
        .padding(.vertical)
    
    }
    
    struct ChronicConditionChip: View {
        let text: String
        let isSelected: Bool
        let action: () -> Void

        var body: some View {
            Text(text)
                .font(.custom("Urbanist-Medium", size: 14))
                .padding(.horizontal, 10)
                .padding(.vertical, 8)
                .foregroundColor(
                    isSelected
                    ? Color(hex: "#211C64")
                    : Color(hex: "#697383")
                )
                .background(
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(
                            isSelected
                            ? Color(hex: "#211C64")
                            : Color(hex: "#697383"),
                            lineWidth: 0.6
                        )
                        .background(
                            RoundedRectangle(cornerRadius: 20)
                                .fill(
                                    isSelected
                                    ? Color(hex: "#E8EDFF")
                                    : Color.white
                                )
                        )
                )
                .onTapGesture { action() }
        }
    }
}

extension HistoryProfileView {
    private var supplementsSection: some View {
        VStack(alignment: .leading, spacing: 10) {

            HStack {
                Text("Current Supplements")
                    .font(.custom("Urbanist-Regular", size: 15))
                    .foregroundColor(.black)

                Text("(Optional)")
                    .font(.custom("Urbanist-Italic", size: 15))
                    .foregroundColor(.black)
            }
            .padding(.top, 16)

            // FIXED INPUT ROW
            HStack {
                TextField(
                    "Any supplements you're currently taking",
                    text: $vm.form.supplements[0]
                )
                .font(.custom("Urbanist-Regular", size: 15))
                .padding()
                .background(
                    RoundedRectangle(cornerRadius: 50)
                        .stroke(Color(hex: "#697383"), lineWidth: 0.6)
                )

                addButton {
                    vm.addSupplement()
                }
            }

            // 📋 ADDED SUPPLEMENTS (READ-ONLY)
            ForEach(Array(vm.form.supplements.enumerated()), id: \.offset) { index, supplement in
                if index > 0 {
                    HStack {
                        TextField("", text: .constant(supplement))
                            .font(.custom("Urbanist-Regular", size: 15))
                            .padding()
                            .background(
                                RoundedRectangle(cornerRadius: 50)
                                    .stroke(Color(hex: "#697383"), lineWidth: 0.6)
                            )
                            .disabled(true)

                        removeButton {
                            vm.removeSupplement(at: index)
                        }
                    }
                }
            }
        }
    }
}


extension HistoryProfileView {
    private var conditionsSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            FlexibleView(
                availableWidth: UIScreen.main.bounds.width - 40,
                data: vm.chronicConditionOptions,
                spacing: 10,
                alignment: .leading
            ) { item in
                ChronicConditionChip(
                    text: item,
                    isSelected: vm.form.chronicConditions.contains(item)
                ) {
                    vm.toggleCondition(item)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.top, 8)
            
            if vm.form.chronicConditions.contains("Others") {
                TextField("Write chronic condition", text: $vm.otherChronicInput)
                    .padding()
                    .background(
                        RoundedRectangle(cornerRadius: 50)
                            .stroke(Color(hex: "#697383"), lineWidth: 0.6)
                    )
                    .onChange(of: vm.otherChronicInput) { newValue in
                        vm.updateOtherChronicFromInput(newValue)
                        vm.otherChronicError = nil
                    }
                
                if let error = vm.otherChronicError {
                    Text(error)
                        .font(.caption)
                        .foregroundColor(.red)
                        .padding(.leading, 4)
                }
            }
        }
    }
}

extension HistoryProfileView {
    private var medicationsSection: some View {
        VStack(alignment: .leading, spacing: 10) {

            HStack {
                Text("Current Medications")
                    .font(.custom("Urbanist-Regular", size: 15))
                    .foregroundColor(.black)

                Text("(Optional)")
                    .font(.custom("Urbanist-Italic", size: 15))
                    .foregroundColor(.black)
            }
            .padding(.top, 16)

            // ➕ FIXED INPUT ROW (ALWAYS ON TOP)
            HStack {
                TextField(
                    "Any medications you're currently taking",
                    text: $vm.form.medications[0]
                )
                .font(.custom("Urbanist-Regular", size: 15))
                
                
                .padding()
                .background(
                    RoundedRectangle(cornerRadius: 50)
                        .stroke(Color(hex: "#697383"), lineWidth: 0.6)
                )

                addButton {
                    vm.addMedication()
                }
            }

            // 📋 ADDED MEDICATIONS (READ-ONLY + MINUS)
            ForEach(Array(vm.form.medications.enumerated()), id: \.offset) { index, medication in
                if index > 0 {
                    HStack {
                        TextField("", text: .constant(medication))
                       
                            .font(.custom("Urbanist-Regular", size: 15))
                            .padding()
                            .background(
                                RoundedRectangle(cornerRadius: 50)
                                    .stroke(Color(hex: "#697383"), lineWidth: 0.6)
                            )
                            .disabled(true)

                        removeButton {
                            vm.removeMedication(at: index)
                        }
                    }
                }
            }
        }
    }

}

extension HistoryProfileView {
    private func addButton(action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Image("AddMedicineBtn")
                .resizable()
                .frame(width: 40, height: 55)
                .foregroundColor(.white)
                .padding(10)
        }
    }
    
    private func removeButton(action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Image("DeleteMedicineBtn")
                .resizable()
                .frame(width: 40, height: 55)
                .padding(10)
        }
    }
}

#Preview {
    HistoryProfileView(flow: .profileSetup)
}
