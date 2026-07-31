//
//  AddMedicationView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 02/01/26.
//

import SwiftUI
import Foundation

struct AddMedicationView: View {
    @EnvironmentObject private var coordinator: Coordinator
   // @Environment(\.dismiss) private var dismiss
    @State private var showStartDatePopup = false
    @State private var showEndDatePopup = false
    @State private var showTimePopup = false
    @StateObject private var viewModel: AddMedicationViewModel
    @State private var showPicker = false
   // @State private var isDateSelected = false
    @State private var isTimeSelected = false
    @State private var showPopup: Bool = false
    @State private var popupAction: (() -> Void)? = nil
    let flow: MedicationFlow
    @State private var showToast = false
    @State private var toastMessage = ""
    
    
    @State private var isStartDateSelected = false
    @State private var isEndDateSelected = false
    @State private var selectedDateType: DateSelectionType = .start
    @State private var editingIndex: Int? = nil
    @FocusState private var isNotesFocused: Bool
   
    let editModel: AddMedicationModel?   // ADD THIS
    
    init(flow: MedicationFlow, editModel: AddMedicationModel? = nil) {
        self.flow = flow
        self.editModel = editModel
        _viewModel = StateObject(
            wrappedValue: AddMedicationViewModel(editModel: editModel)
        )
    }
    
    var body: some View {
        ZStack {
            // MAIN CONTENT
            VStack(spacing: 18) {
                // Header
                HStack {
                    Button {
                        closeScreen()
                    } label: {
                        Image("backIcon")
                            .resizable()
                            .frame(width: 45, height: 45)
                    }
                    
                    Text(
                        flow == .mediReschedule
                        ? "Edit Medication"
                        : "Add Medication"
                    )
                    .font(.custom("Urbanist-Medium", size: 20))
                    Spacer()
                }
                .padding(.top, 10)
                .padding(.leading, 10)
                
                Divider()
                
                ScrollView {
                    VStack(spacing: 20) {
                    // Dropdowns
                       // labeledDropdown(title: "For Family Member", placeholder: "Myself", selection: $viewModel.form.familyMember, items: viewModel.familyMembers)
                        
                        labeledDropdown(
                            title: "For Family Member",
                            placeholder: "Select Member",
                            selection: $viewModel.form.familyMember,
                            items: viewModel.membersListDetails.map { $0.name ?? "" },
                            onTap: {
                                if viewModel.membersListDetails.isEmpty {
                                    viewModel.userWithFamilyDetailsAPI { _ in }
                                }
                            }
                        )
//                        
//                        .onChange(of: viewModel.form.familyMember) { selectedName in
//                            if let selected = viewModel.membersList.first(where: { $0.name == selectedName }) {
//                                
//                                viewModel.selectedMember = selected
//                                
//                                print("✅ Member Selected:")
//                                print("ID:", selected.id)
//                                print("Name:", selected.name)
//                                print("Relation:", selected.relation)
//                            }
//                        }
                        
                        .onChange(of: viewModel.form.familyMember) { selectedName in

                            if let selected = viewModel.membersListDetails.first(where: {
                                ($0.name ?? "") == selectedName
                            }) {
                                viewModel.selectedMember = FamilyMembers(
                                    id: selected.id ?? 0,
                                    name: selected.name ?? "",
                                    relation: selected.relationship ?? ""
                                )

                                if selected.relationship == "MySelf" || selected.name == "MySelf" {
                                    viewModel.for_whom_id = ""
                                } else {
                                    viewModel.for_whom_id = "\(selected.id ?? 0)"
                                }

                                print("✅ Member Selected")
                                print("ID:", selected.id ?? 0)
                                print("Name:", selected.name ?? "")
                            }
                        }

                        labeledDropdown(title: "Medication Type", placeholder: "Select Medication type", selection: $viewModel.form.medicationType, items: viewModel.medicationTypes)
                    
                    VStack(alignment: .leading, spacing: 6) {
                        HStack{
                            TextFieldBox(
                                title: "Medication Name",
                                placeholder: "e.g., Amoxicillin",
                                text: $viewModel.form.medicationName
                            )
                            
                            TextFieldBox(
                                title: "Dosage",
                                placeholder: "e.g., 500mg",
                                text: $viewModel.form.dosage
                            )
                        }
                        
                        labeledDropdown(title: "Frequency", placeholder: "Select Frequency", selection: $viewModel.form.frequency, items: viewModel.frequency)
                            .padding(.top, 8)
                             .onChange(of: viewModel.form.frequency) { value in
                                 if value.lowercased() != "weekly" {
                                     viewModel.form.days = ""
                                 }
                             }
                         
                         if viewModel.form.frequency.lowercased() == "weekly" {

                            labeledDropdown(
                                title: "Days",
                                placeholder: "Select",
                                selection: $viewModel.form.days,
                                items: viewModel.days
                            )
                            .padding(.top, 8)
                        }
                    }
                        
                    // Time Selection View
                        timeSelectionView
                        
                    // Date & Time
                    HStack(spacing: 12) {

                        DateFields(
                            title: "Start Date",
                            placeholder: "MM-DD-YYYY",
                            text: dateText
                        ) {
                            selectedDateType = .start   // ✅ IMPORTANT
                            showStartDatePopup = true
                        }

                        DateFields(
                            title: "End Date",
                            placeholder: "MM-DD-YYYY",
                            text: endDateText
                        ) {
                            selectedDateType = .end     // ✅ IMPORTANT
                            showEndDatePopup = true
                        }
 
                    }
                        
    //       MARK:- Upload FIles Section View
                        uploadBox
                        
                        VStack(alignment: .leading, spacing: 6) {
                            Text("Notes")
                                .font(.custom("Urbanist-Regular", size: 16))
                                .padding(.leading,5)
                                
                                ZStack(alignment: .topLeading) {
                                    if viewModel.form.notes.isEmpty {
                                        Text("Special instructions, side effects to watch for....")
                                            .foregroundColor(.gray)
                                            .font(.custom("Urbanist-Regular", size: 16))
                                            .padding(.horizontal, 14)
                                            .padding(.vertical, 14)
                                    }

                                    TextEditor(text: $viewModel.form.notes)
                                        .font(.custom("Urbanist-Regular", size: 16))
                                        .frame(height: 90)
                                        .padding(8)
                                        .background(Color.clear)   // IMPORTANT
                                        .scrollContentBackground(.hidden)
                                        .focused($isNotesFocused)
                                    
                                }
                                .overlay(
                                    RoundedRectangle(cornerRadius: 20)
                                        .stroke(Color(hex: "#697383"), lineWidth: 0.6)
                                )
                            
                            Spacer()
                            Toggle("Enable Reminder", isOn: $viewModel.enableReminder)
                                .font(.custom("Urbanist-Regular", size: 16))
                                .toggleStyle(CheckboxToggleStyle())
                                .padding(.leading, 5)
                        }
                        .padding(.horizontal,10)
                    
                        HStack(spacing: 12) {
                            Button(action: closeScreen) {
                                Text("Cancel")
                                    .foregroundColor(.black)
                                    .frame(width: 132, height: 67)
                                    .overlay(
                                        Capsule()
                                            .stroke(Color.black, lineWidth: 1)
                                            .allowsHitTesting(false)
                                    )
                                    .contentShape(Capsule())
                            }
                            .buttonStyle(.plain)
                            .zIndex(1)

                            Button(action: {
                                if flow == .mediReschedule {
                                    if viewModel.validateFields() {
                                        if !viewModel.enableReminder {
                                            toastMessage = "Please enable reminder"
                                            withAnimation {
                                                showToast = true
                                            }
                                            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                                                withAnimation {
                                                    showToast = false
                                                }
                                            }
                                            return
                                        }
                                        viewModel.medicationID = UserDetail.shared.getID()
                                        viewModel.updateMedicationAPI{ success in
                                            if success {
                                                showPopup = true
                                            }
                                        }
                                    }
                                } else {
                                    // ✅ First validate (this also assigns values)
                                    if viewModel.validateFields() {
                                        if !viewModel.enableReminder {
                                            toastMessage = "Please enable reminder"
                                            withAnimation {
                                                showToast = true
                                            }
                                            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                                                withAnimation {
                                                    showToast = false
                                                }
                                            }
                                            return
                                        }
                                        // 🪵 Debug Print AFTER assignment
                                        print("📤 Sending Medication Data:")
                                        print("Member ID:", viewModel.for_whom_id ?? "")
                                        print("Type:", viewModel.medication_type ?? "")
                                        print("Name:", viewModel.medication_name ?? "")
                                        print("Dosage:", viewModel.dosage ?? "")
                                        print("Frequency:", viewModel.frequencyy ?? "")
                                        print("Days:", viewModel.dayss ?? "")
                                        print("Start Date:", viewModel.start_date ?? "")
                                        print("End Date:", viewModel.end_date ?? "")
                                        print("Reminder Status:", viewModel.reminder_status ?? "")
                                        print("Reminder Times:", viewModel.reminder_time)
                                        print("Notes:", viewModel.notes ?? "")
                                        
                                        // ✅ API Call
                                        viewModel.addMedicationAPI { success in
                                            if success {
                                                showPopup = true
                                            }
                                        }
                                    }
                                }
                            })  {
                                Text(
                                    flow == .mediReschedule
                                    ? "Update"
                                    : "Add Medication"
                                )
                                .frame(maxWidth: .infinity)
                                .frame(height: 67)
                                .background {
                                    Image("BackgroundBtn")
                                        .resizable()
                                        .scaledToFill()
                                }
                                .clipShape(Capsule())
                                .contentShape(Capsule())
                                .foregroundColor(.white)
                            }
                            .buttonStyle(.plain)
                            .frame(maxWidth: .infinity)
                        }
                        .padding(.top, 10)
                        .padding(.horizontal, 10)
                }
            }
                .scrollIndicators(.hidden)
             
                .disableScrollBounce()
                .customAlert(
                          isPresented: $viewModel.isPresentAlert,
                          message: viewModel.errorMessage ?? "Error"
                      ) {
                          print("OK tapped")
                      }
//                .alert(isPresented: $viewModel.isPresentAlert) {
//                    Alert(title: Text(viewModel.errorMessage ?? "Error"))
//                }
            }
 
            .sheet(isPresented: $showPicker) {
                DocumentPicker(onPick: { url in
                    print(url, "Selected File URL")
                    do {
                        let data = try Data(contentsOf: url)
                        let file =  UploadedFile(
                            name: url.lastPathComponent, typeIcon: "",
                            data: data
                        )
                        viewModel.selectedFile = file   // ✅ IMPORTANT
                        print("File stored:", file.name)

                    } catch {
                        print("File read error:", error.localizedDescription)
                    }
                })
            }
            .onAppear {
                print("AddMedicationView flow =", flow)
                if flow == .mediReschedule {
                    viewModel.medicationID = UserDetail.shared.getID()
                    viewModel.getMedicationDetails { success in
                        if success {
                            print("getMedicationDetails API called successfully")
                        } else {
                            print("API failed")
                        }
                    }
                }
            }
            .blur(radius: showPopup ? 3 : 0)
            //.ignoresSafeArea()

                // POPUP LAYER
            if showPopup {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture {
                        withAnimation { showPopup = false
                            // Navigate AFTER popup close animation finishes
//                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
//                                coordinator.push(.login)
//                            }
                        }
                    }
                SuccessPopupView(
                    title: popupTitle,
                    message: popupMessage,
                    onClose: {
                        withAnimation(.easeOut(duration: 0.25)) {
                            showPopup = false
                            coordinator.pop()
                        }
                        // Navigate AFTER popup close animation finishes
//                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
//                            coordinator.push(.newAppointmentScheduleView(flow: .new)
//                            )
//                        }
                    }
                )
                .transition(.scale.combined(with: .opacity))
            }
            
            // TIME POPUP (CENTER)
            if showTimePopup {
                TimePopup(
                    selectedTime: $viewModel.form.time,
                    isPresented: $showTimePopup
                ) {
                    isTimeSelected = true
                }
                .zIndex(1)
            }
            
            
            // MARK: POPUP OVERLAY
            if showStartDatePopup {
                // Background dim
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture {
                        withAnimation {  showStartDatePopup = false }
                    }
                    .transition(.opacity)
                CustomCalendarView1(
                    selectedDate: $viewModel.form.startDate,
                    onClose: {
                        showStartDatePopup = false
                        viewModel.isStartDateSelected = true
                    },
                    allowFutureDates: true
                )
                .zIndex(1)
            }
            
            
            // MARK: POPUP OVERLAY
            if showEndDatePopup {
                // Background dim
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture {
                        withAnimation {  showEndDatePopup = false }
                    }
                    .transition(.opacity)
                CustomCalendarView1(
                    selectedDate: $viewModel.form.endDate,
                    onClose: {
                        showEndDatePopup = false
                        viewModel.isEndDateSelected = true
                    },
                    allowFutureDates: true
                )
                .zIndex(1)
            }
            
            if viewModel.showActivity {
                CustomLoderView(isVisible: $viewModel.showActivity)
                    .ignoresSafeArea()
            }
            
            // ✅ TOAST VIEW
            if showToast {
                VStack {
                    Spacer()
                    
                    ToastView(message: toastMessage)
                        .padding(.bottom, 100)
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .zIndex(9999) // 🔥 sabse upar
            }
        }
        .animation(.easeInOut, value: showStartDatePopup || showTimePopup)
        .animation(.easeInOut, value: showToast)

        .onChange(of: viewModel.form.endDate) { newDate in

            if newDate < viewModel.form.startDate {

                viewModel.errorMessage = "End date must be greater than start date"
                viewModel.isPresentAlert = true

                // Reset to start date
                viewModel.form.endDate = viewModel.form.startDate
            }
        }
        .toolbar {
            ToolbarItemGroup(placement: .keyboard) {
                Spacer()
                Button("Done") {
                    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                }
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundColor(Color(hex: "#4338CA"))
            }
        }
        .onDisappear {
            UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
        }
        
       
        
    }
    
    private func closeScreen() {
        UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
        coordinator.pop()
    }

    private var popupTitle: String {
        flow == .mediReschedule
        ? "Medication Updated Successfully"
        : "Medication Added Successfully"
    }

    private var popupMessage: String {
        flow == .mediReschedule
        ? "Your changes have been saved."
        : "Your medication has been saved and reminder is set."
    }
    
    // MARK: - Helpers
    private var dateText: String {
        viewModel.isStartDateSelected
        ? viewModel.form.startDate.toAppDateString()
        : "DD - MM - YYYY"
    }
    
    
   
    
    private var endDateText: String {
        viewModel.isEndDateSelected
        ? viewModel.form.endDate.toAppDateString()
        : "DD - MM - YYYY"
    }

    private var timeText: String {
        isTimeSelected
        ? viewModel.form.time.formatted("hh:mm a")
        : "00:00"
    }


    
    // MARK: - Labeled Dropdown (corrected)
    @ViewBuilder
    func labeledDropdown(
        title: String,
        placeholder: String,
        selection: Binding<String>,
        items: [String],
        onTap: (() -> Void)? = nil   // 👈 ADD
    ) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.custom("Urbanist-Regular", size: 16))
                .padding(.leading, 5)

            DropdownField(
                selected: selection,
                options: items,
                placeholder: placeholder,
                onTap: onTap
            )
        }
        .padding(.horizontal,10)
    }
}

private extension AddMedicationView {
    var uploadBox: some View {
        VStack{
            HStack {
                Text("Upload Prescription")
                    .font(.custom("Urbanist-Regular", size: 15))
                    .foregroundColor(.black)
                    .padding(.leading,5)
                //.frame(maxWidth: .infinity, alignment: .leading)
                
                Text("(Optional)")
                    .font(.custom("Urbanist-Regular", size: 15))
                    .foregroundColor(.black)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    
            }
            .padding(.bottom, 4)
            
            HStack(spacing: 8) {
                Button("Choose File") {
                    showPicker = true
                }
                .font(.custom("Urbanist-Medium", size: 12))
                .foregroundColor(.white)
                .frame(width: 90)
                .frame(height: 30)
                .background {
                    Image("BackgroundBtn")
                        .resizable()
                        .scaledToFill()
                }
                .clipShape(Capsule())
                .contentShape(Capsule())
                .foregroundColor(.white)
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
                
                .cornerRadius(30)
                
                //Text("No File Chosen")
                    .font(.custom("Urbanist-Regular", size: 15))
                    .foregroundColor(Color(hex: "#697383"))
                
                Text(
                    viewModel.selectedFile == nil
                    ? "No File Chosen"
                    : (viewModel.selectedFile?.name ?? "1 File Selected")
                )
                .font(.custom("Urbanist-Regular", size: 15))
                .foregroundColor(
                    viewModel.selectedFile == nil ? Color(hex: "#697383") : .black
                )
                Spacer()
            }
            
            .frame(maxWidth: .infinity)
            .padding()
            
            .overlay(
                RoundedRectangle(cornerRadius: 56)
                    .stroke(Color(hex: "#697383"), lineWidth: 0.6)
            )
            
            Text("PDF, JPG, PNG, DICOM supported")
                .font(.custom("Urbanist-Regular", size: 12))
                .foregroundColor(Color(hex: "#697383"))
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.top, 4)
                .padding(.leading,5)
        }
        .padding(.horizontal, 10)
    }
}

private extension AddMedicationView {

    var timeSelectionView: some View {
        VStack(alignment: .leading, spacing: 12) {

            Text("Reminder Time")
                .font(.custom("Urbanist-Regular", size: 16))
                .padding(.leading, 5)

            VStack(spacing: 12) {
                ForEach(Array(viewModel.reminderTimes.enumerated()), id: \.element.id) { index, item in

                    HStack(spacing: 10) {

                        // TIME FIELD
                        Button {
                            editingIndex = index
                            viewModel.selectedTime = item.time
                            viewModel.showTimePicker = true
                        } label: {
                            TimeCapsuleView(time: item.time)
                        }
                        .buttonStyle(.plain)

                        // ACTION BUTTON
                        if index == 0 {
                            // PLUS BUTTON
                            Button {
                                editingIndex = nil
                                viewModel.selectedTime = Date()
                                viewModel.showTimePicker = true
                            } label: {
                                actionCircle(
                                    systemName: "plus",
                                    gradient: true
                                )
                            }
                        } else {
                            // MINUS BUTTON
                            Button {
                                viewModel.removeTime(at: index)
                            } label: {
                                actionCircle(
                                    systemName: "minus",
                                    gradient: false
                                )
                            }
                        }
                    }
                }
            }

//            Toggle("Every Day", isOn: $viewModel.isEveryDay)
//                .font(.custom("Urbanist-Regular", size: 16))
//                .toggleStyle(CheckboxToggleStyle())
//                .padding(.leading, 5)
        }
        .padding(.horizontal,10)

        .sheet(isPresented: $viewModel.showTimePicker) {
            TimePickerSheet(viewModel: viewModel, editingIndex: $editingIndex)
                .presentationDetents([.height(275)])   // 👈 bottom height
                .presentationDragIndicator(.hidden)   // 👈 top drag line
                .presentationDetents([.fraction(0.35)])
                
        }
    }
}

struct TimeCapsuleView: View {
    let time: Date

    var body: some View {
        HStack {
            Text(timeFormatted)
                .font(.custom("Urbanist-Regular", size: 16))
                .foregroundColor(.gray)

            Spacer()

            Image("TimeIcon")
                .foregroundColor(.gray)
        }
        .padding(.horizontal, 14)
        .frame(height: 55)
        .overlay(
            RoundedRectangle(cornerRadius: 46)
                .stroke(Color(hex: "#697383"), lineWidth: 0.6)
        )
    }

    private var timeFormatted: String {
        let f = DateFormatter()
        f.timeStyle = .short
        return f.string(from: time)
    }
}

func actionCircle(systemName: String, gradient: Bool) -> some View {
    Group {
        if gradient {
            Image("AddMedicineBtn")
                .frame(width: 40, height: 55)
                
        } else {
            Image("DeleteMedicineBtn")
                .frame(width: 40, height: 55)
        }
    }
    //.clipShape(Circle())
}

struct TimePickerSheet: View {

    @ObservedObject var viewModel: AddMedicationViewModel
    @Binding var editingIndex: Int?
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        VStack(spacing: 24) {

            DatePicker(
                "Select Time",
                selection: $viewModel.selectedTime,
                displayedComponents: .hourAndMinute
            )
            .datePickerStyle(.wheel)
            .labelsHidden()

            Button(editingIndex == nil ? "Add Time" : "Update Time") {
                if let index = editingIndex {
                    viewModel.reminderTimes[index].time = viewModel.selectedTime
                } else {
                    viewModel.addTime()
                }
                dismiss()
            }
            .frame(maxWidth: .infinity)
            .padding()
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
            .foregroundColor(.white)
            Spacer()
        }
        .padding()
    }
}

struct CheckboxToggleStyle: ToggleStyle {
    func makeBody(configuration: Configuration) -> some View {
        HStack(spacing: 8) {
            Button {
                configuration.isOn.toggle()
            } label: {
                Image(configuration.isOn
                      ? "FilledCheckBox"
                      : "CheckBox")
                .resizable()
                .frame(width: 23, height: 23)
            }
            configuration.label
        }
    }
}

// MARK: - Time Popup
struct MedicationTimePopup: View {
    @Binding var selectedTime: Date
    @Binding var isPresented: Bool
    var onApply: () -> Void

    @State private var tempTime = Date()

    var body: some View {
        ZStack {
            Color.black.opacity(0.4)
                .ignoresSafeArea()
                .onTapGesture { isPresented = false }

            VStack(spacing: 20) {
                header("Select Time", icon: "clock")

                DatePicker("", selection: $tempTime, displayedComponents: .hourAndMinute)
                    .datePickerStyle(.wheel)
                    .labelsHidden()

                popupButtons(
                    onCancel: {
                        isPresented = false   // no change
                        isPresented = false   // no chang
                    },
                    onApply: {
                        selectedTime = tempTime   // apply only here
                        onApply()
                        isPresented = false
                    }
                )
            }
            .popupStyle()
        }
    }
}


// MARK: - Shared UI Helpers
func Mediheader(_ title: String, icon: String) -> some View {
    HStack {
        Image(systemName: icon).foregroundColor(.purple)
        Text(title).font(.headline)
        Spacer()
    }
}

//func popupMedicationButtons(onApply: @escaping () -> Void) -> some View {
//    HStack(spacing: 12) {
//        Button(action: {
//         //   onApply()
//           
//        }) {
//            Text("Cancel")
//                .foregroundColor(.black)
//                .frame(maxWidth: .infinity)
//                .padding()
//                .overlay(
//                    RoundedRectangle(cornerRadius: 45)
//                        .stroke(lineWidth: 1)
//                        .foregroundColor(.black)
//                    )
//                .cornerRadius(45)
//        }
//        
//        Button(action: {
//            onApply()
//        }) {
//            Text("Apply")
//                .frame(maxWidth: .infinity)
//                .padding()
//                .foregroundColor(.white)
//                .clipShape(Capsule())
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
//                .cornerRadius(25)
//        }
//    }
//}

#Preview {
    AddMedicationView(flow: .mediNew)
        .environmentObject(Coordinator())
}


enum DateSelectionType {
    case start
    case end
}
