//
//  NewAppointmentScheduleView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 30/12/25.
//

import SwiftUI
import Foundation

struct NewAppointmentScheduleView: View {
    @EnvironmentObject private var coordinator: Coordinator
    @StateObject private var viewModel = NewAppointmentScheduleViewModel()
    @Environment(\.dismiss) private var dismiss
    @State private var showDatePopup = false
    @State private var showTimePopup = false
    @State private var showPopup: Bool = false
    @State private var popupAction: (() -> Void)? = nil
  
    @State private var isTimeSelected = false
    
    let flow: AppointmentFlow
    let chatId: Int?
    
    var body: some View {
        ZStack {
            // MAIN CONTENT
            VStack(spacing: 18) {
                // Header
                HStack {
                    Button {
                        UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
                        coordinator.pop()
                    } label: {
                        Image("backIcon")
                            .resizable()
                            .frame(width: 45, height: 45)
                    }
                    
                    Text(
                        flow == .reschedule
                        ? "Reschedule Appointment"
                        : "Schedule New Appointment"
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
 
                        
                        labeledDropdown(
                            title: "For Whom",
                            placeholder: "Select Member",
                            selection: $viewModel.form.member,
                            items: viewModel.membersListDetails.map { $0.name ?? "" },
                            onTap: {
                                if viewModel.membersListDetails.isEmpty {
                                    viewModel.userWithFamilyDetailsAPI { _ in }
                                }
                            },
                            disabled: flow == .reschedule
                        )
                        
                        .onChange(of: viewModel.form.member) { selectedName in
                            if let selected = viewModel.membersListDetails.first(where: { ($0.name ?? "") == selectedName }) {
                                viewModel.selectedMember = FamilyMembers(
                                    id: selected.id ?? 0,
                                    name: selected.name ?? "",
                                    relation: selected.relationship ?? ""
                                )
                                
                                print("✅ Member Selected:")
                                print("ID:", selected.id ?? 0)
                                print("Name:", selected.name ?? "")
                                print("Relation:", selected.relationship ?? "")
                            }
                        }

                        labeledDropdown(
                            title: "Appointment Type",
                            placeholder: "Select Appointment Type",
                            selection: $viewModel.form.appointmentType,
                            items: viewModel.appointmentTypesList.map { $0.name },
                            onTap: {
                                if viewModel.appointmentTypesList.isEmpty {
                                    viewModel.get_appointment_type_API { _ in }
                                }
                            }
                        )
                        
                        .onChange(of: viewModel.form.appointmentType) { selectedName in
                            if let selected = viewModel.appointmentTypesList.first(where: { $0.name == selectedName }) {
                                viewModel.selectedAppointmentType = selected
                                
                                print("✅ Appointment Type Selected:")
                                print("ID:", selected.id)
                                print("Name:", selected.name)
                            }
                        }
                        
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Description")
                            .font(.custom("Urbanist-Regular", size: 16))
                            .padding(.leading, 5)
                        ZStack(alignment: .topLeading) {
                            if viewModel.form.description.isEmpty {
                                Text("Type here....")
                                    .font(.custom("Urbanist-Regular", size: 16))
                                    .foregroundColor(.gray)
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 14)
                            }

                            TextEditor(text: $viewModel.form.description)
                                .font(.custom("Urbanist-Regular", size: 16))
                                .frame(height: 90)
                                .padding(8)
                                .background(Color.clear)   // IMPORTANT
                                .scrollContentBackground(.hidden)
                        }
                        .overlay(
                            RoundedRectangle(cornerRadius: 20)
                                .stroke(Color(hex: "#697383"), lineWidth: 0.6)
                        )
                    }
                    .padding(.horizontal, 10)
                    
                    // Date & Time
                    HStack(spacing: 12) {
                        DateFields(
                            title: "Date",
                            placeholder: "MM-DD-YYYY",
                            text: dateText
                        ) {
                            showDatePopup = true
                        }
                        
                        TimeField(
                            title: "Time",
                            placeholder: "00:00",
                            text: timeText
                        ) {
                            showTimePopup = true
                        }
                    }
                    
                    TextFieldBox(
                        title: "Preferred Doctor",
                        placeholder: "e.g. Dr. John Doe",
                        text: $viewModel.form.preferredDoctor
                    )
                    
                    TextFieldBox(
                        title: "Preferred Clinic",
                        placeholder: "e.g. Bright Smile Dental",
                        text: $viewModel.form.preferredClinic
                    )
                    
                        labeledDropdown(title: "Appointment Reminder", placeholder: "Select Appointment Reminder",
                                        selection: $viewModel.form.reminder, items: viewModel.reminders)
                    
                    HStack(spacing: 12) {
                        Button("Cancel") {
                            dismiss()
                        }
                        .foregroundColor(.black)
                        .frame(width: 100, height: 35)
                        .padding()
                        .overlay(
                            RoundedRectangle(cornerRadius: 45)
                                .stroke(lineWidth: 1)
                                .foregroundColor(.black)
                            )
                        .cornerRadius(45)
 
                        Button(action:{
                            if flow == .new {
                                
                                // Assign selected values to API params
                                if viewModel.selectedMember?.relation == "MySelf" || viewModel.selectedMember?.name == "MySelf" {
                                    viewModel.for_whom_id = ""
                                } else {
                                    viewModel.for_whom_id = "\(viewModel.selectedMember?.id ?? 0)"
                                }
                                viewModel.appointment_type_id = "\(viewModel.selectedAppointmentType?.id ?? 0)"
                                viewModel.description = viewModel.form.description
                                viewModel.date = viewModel.form.date.formatted("MM-dd-yyyy")
                                viewModel.time = viewModel.form.time.formatted("HH:mm")
                                viewModel.preferred_doctor = viewModel.form.preferredDoctor
                                viewModel.preferred_clinic = viewModel.form.preferredClinic
                                viewModel.appointment_reminder = viewModel.form.reminder
                                
                                print("Sending Data:")
                                print("Member ID:", viewModel.for_whom_id ?? "")
                                print("Appointment Type ID:", viewModel.appointment_type_id ?? "")
                                print("Description:", viewModel.description ?? "")
                                print("Date:", viewModel.date ?? "")
                                print("Time:", viewModel.time ?? "")
                                print("Doctor:", viewModel.preferred_doctor ?? "")
                                print("Clinic:", viewModel.preferred_clinic ?? "")
                                print("Reminder:", viewModel.appointment_reminder ?? "")
                                
                                if viewModel.validateForm() {
                                    // API CALL
                                    viewModel.schedule_appointmentAPI { success in
                                        if success {
                                            showPopup = true   // success popup
                                        }
                                    }
                                }
                            }
                            else {
                                if viewModel.validateForm() {
                                    let appointmentID = UserDetail.shared.getID()
                                    viewModel.appointmentID = appointmentID
                                    
                                    viewModel.rescheduleAppointmentAPI { success in
                                        if success {
                                            showPopup = true   // success popup
                                        }
                                    }
                                }
                            }
                        })  {
                            Text(
                                flow == .reschedule
                                ? "Update"
                                : "Schedule"
                            )
                                .frame(maxWidth: .infinity)
                                .frame(height: 35)
                                .padding()
                                .background( Image("BackgroundBtn") // Asset name
                                              .resizable()
                                              .scaledToFill()
                                )
                                .cornerRadius(45)
                                .foregroundColor(.white)
                        }
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
            .onAppear {
                if flow == .reschedule {
                    let appointmentID = UserDetail.shared.getID()
                    viewModel.appointmentID = appointmentID
                    viewModel.getScheduleAppointmentDetails { _ in }
                } else if let chatId = chatId {
                    viewModel.recommended_chat_id = "\(chatId)"
                    print("✅ Set recommended_chat_id from chat screen view:", chatId)
                }
            }
            //.padding(.horizontal, 30)
            .blur(radius: showPopup ? 3 : 0)   // blur background when popup opens
        // -------------------
        // POPUP OVERLAY
        // -------------------
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
            SuccessPopupView(
                title: popupTitle,
                message: popupMessage,
                onClose: {
                    withAnimation(.easeOut(duration: 0.25)) {
                        showPopup = false
                        coordinator.pop()
                    }
 
                }
            )
            .transition(.scale.combined(with: .opacity))
        }
            // MARK: POPUP OVERLAY
            if showDatePopup {
                // Background dim
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture {
                        withAnimation {  showDatePopup = false }
                    }
                    .transition(.opacity)
                CustomCalendarView1(
                    selectedDate: $viewModel.form.date,
                    onClose: {
                        showDatePopup = false
                        viewModel.isDateSelected = true
                        if viewModel.isTimeSelected && viewModel.isPastTimeSelected() {
                            viewModel.toastMessage = "You cannot select a past time for today."
                            viewModel.showToast = true
                            viewModel.isTimeSelected = false
                            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                                viewModel.showToast = false
                            }
                        }
                    },
                    allowFutureDates: true
                )
                .zIndex(1)
            }
               
            // TIME POPUP (CENTER)
            if showTimePopup {
                TimePopup(
                    selectedTime: $viewModel.form.time,
                    isPresented: $showTimePopup
                ) {
                    viewModel.isTimeSelected = true
                    if viewModel.isPastTimeSelected() {
                        viewModel.toastMessage = "You cannot select a past time for today."
                        viewModel.showToast = true
                        viewModel.isTimeSelected = false
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                            viewModel.showToast = false
                        }
                    }
                }
                .zIndex(1)
            }
               
            if viewModel.showActivity {
                CustomLoderView(isVisible: $viewModel.showActivity)
                    .ignoresSafeArea()
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
        }
        .animation(.easeOut, value: showPopup)
        .animation(.easeInOut, value: showDatePopup || showTimePopup || viewModel.showToast)
        .onChange(of: viewModel.form.date) { newValue in
            print("DOB Changed ", newValue)
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

    private var popupTitle: String {
        flow == .reschedule
        ? "Appointment Updated Successfully"
        : "Appointment Scheduled Successfully"
    }

    private var popupMessage: String {
        flow == .reschedule
        ? "Your changes have been saved."
        : "Your appointment reminder is set."
    }

    // MARK: - Helpers
    private var dateText: String {
        viewModel.isDateSelected
        ? viewModel.form.date.toAppDateString()
        : "DD - MM - YYYY"
    }

    private var timeText: String {
        viewModel.isTimeSelected
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
        onTap: (() -> Void)? = nil,
        disabled: Bool = false
    ) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.custom("Urbanist-Regular", size: 16))
                .padding(.leading, 5)
            DropdownField(
                selected: selection,
                options: items,
                placeholder: placeholder,
                onTap: onTap,
                disabled: disabled
            )
        }
        .padding(.horizontal,10)
    }
}

struct TextFieldBox: View {
    let title: String
    let placeholder: String
    @Binding var text: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.custom("Urbanist-Regular", size: 16)) .foregroundColor(.black)
                .padding(.leading, 5)
            TextField(placeholder, text: $text)
                .font(.custom("Urbanist-Regular", size: 16))
                .padding()
                .background(RoundedRectangle(cornerRadius: 56)
                    .stroke(Color(hex: "#697383"), lineWidth: 0.6))
            
        }
        .padding(.horizontal, 10)
    }
}
//
// MARK: - Date Field

struct DateFields: View {
    let title: String
    let placeholder: String
    let text: String
    let onTap: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.custom("Urbanist-Regular", size: 16))
                .padding(.leading, 5)
            Button(action: onTap) {
                HStack {
                    Text(text)
                        .font(.custom("Urbanist-Regular", size: 16))
                        .foregroundColor(text == placeholder ? Color(hex: "#697383") : .black)

                    Spacer()

                    Image(systemName: "calendar")
                        .foregroundColor(.gray)
                }
                .padding(.horizontal, 16)
                .frame(height: 55)
                .background(Color.white) // ✅ IMPORTANT
                .overlay(
                    RoundedRectangle(cornerRadius: 27.5) // ✅ HEIGHT / 2
                        .stroke(Color(hex: "#697383"), lineWidth: 0.6)
                )
            }
        }
        .padding(.horizontal, 10)
    }
}

// MARK: - Time Field
struct TimeField: View {
    let title: String
    let placeholder: String
    let text: String
    let onTap: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.custom("Urbanist-Regular", size: 16))

            Button(action: onTap) {
                HStack {
                    Text(text)
                        .font(.custom("Urbanist-Regular", size: 16))
                        .foregroundColor(text == placeholder ? Color(hex: "#697383") : .black)
                    Spacer()
                    Image(systemName: "clock")
                        .foregroundColor(.gray)
                }
                .padding(.horizontal, 14)
                .frame(height: 55)
//                .overlay(
//                    Capsule().stroke(Color(hex: "#697383"))
//                )
                .background(Color.white) // ✅ IMPORTANT
                .overlay(
                    RoundedRectangle(cornerRadius: 27.5) // ✅ HEIGHT / 2
                        .stroke(Color(hex: "#697383"), lineWidth: 0.6)
                )
            }
        }
        .padding(.trailing, 10)
    }
}

// MARK: - Time Popup
struct TimePopup: View {
    @Binding var selectedTime: Date
    @Binding var isPresented: Bool
    var onApply: () -> Void

    @State private var selectedHour: Int = 10
    @State private var selectedMinute: Int = 0
    @State private var isAM: Bool = true

    var body: some View {
        ZStack {
            Color.black.opacity(0.4)
                .ignoresSafeArea()
                .onTapGesture { isPresented = false }

            VStack(spacing: 24) {
                // Header (Select Time and Close Cross)
                HStack {
                    HStack(spacing: 8) {
                        ZStack {
                            Circle()
                                .fill(Color(hex: "#3D2DB7"))
                                .frame(width: 40, height: 40)
                            Image(systemName: "clock")
                                .foregroundColor(.white)
                                .font(.system(size: 18, weight: .bold))
                        }
                        Text("Select Time")
                            .font(.custom("Urbanist-Medium", size: 18))
                            .foregroundColor(.black)
                    }
                    
                    Spacer()
                    
                    Button {
                        isPresented = false
                    } label: {
                        ZStack {
                            Circle()
                                .stroke(Color.black.opacity(0.1), lineWidth: 1)
                                .background(Circle().fill(Color.white))
                                .frame(width: 38, height: 38)
                                .shadow(color: .black.opacity(0.05), radius: 2)
                            Image(systemName: "xmark")
                                .foregroundColor(.black)
                                .font(.system(size: 14, weight: .semibold))
                        }
                    }
                }
                .padding(.horizontal, 4)
                
                // Selectors (Hours, Minutes, AM/PM)
                HStack(spacing: 8) {
                    // Hours Picker
                    Menu {
                        ForEach(1...12, id: \.self) { hour in
                            Button(String(format: "%02d", hour)) {
                                selectedHour = hour
                            }
                        }
                    } label: {
                        HStack {
                            Text(String(format: "%02d", selectedHour))
                                .font(.custom("Urbanist-Regular", size: 24))
                                .foregroundColor(.black)
                            Spacer()
                            Image("DropDown")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 10, height: 10) // Increase size
                                .foregroundColor(Color(hex: "#697383"))
                                .padding(.trailing, 5)
                        }
                        .padding(.horizontal, 8)
                        .frame(width: 100, height: 44)
                        .background(Color(hex: "#F1F1F1"))
                        .cornerRadius(22)
                    }
                    
                    Text(":")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(.black)
                    
                    // Minutes Picker
                    Menu {
                        ForEach(0...59, id: \.self) { minute in
                            Button(String(format: "%02d", minute)) {
                                selectedMinute = minute
                            }
                        }
                    } label: {
                        HStack {
                            Text(String(format: "%02d", selectedMinute))
                                .font(.custom("Urbanist-Regular", size: 24))
                                .foregroundColor(.black)
                            Spacer()
                           
                            Image("DropDown")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 10, height: 10) // Increase size
                                .foregroundColor(Color(hex: "#697383"))
                                .padding(.trailing, 5)
                        }
                        .padding(.horizontal, 8)
                        .frame(width: 100, height: 44)
                        .background(Color(hex: "#F1F1F1"))
                        .cornerRadius(22)
                    }
                    
                    // AM/PM Toggle Pill
                    HStack(spacing: 0) {
                        Button {
                            isAM = true
                        } label: {
                            Text("AM")
                                .font(.custom("Urbanist-Medium", size: 14))
                                .foregroundColor(isAM ? .white : .black)
                                .frame(width: 45, height: 32)
                                .background(isAM ? Color(hex: "#3D2DB7") : Color.clear)
                                .clipShape(Capsule())
                        }
                        Button {
                            isAM = false
                        } label: {
                            Text("PM")
                                .font(.custom("Urbanist-Medium", size: 14))
                                .foregroundColor(!isAM ? .white : .black)
                                .frame(width: 45, height: 32)
                                .background(!isAM ? Color(hex: "#3D2DB7") : Color.clear)
                                .clipShape(Capsule())
                        }
                    }
                    .padding(2)
                    .background(Color(hex: "#F1F1F1"))
                    .clipShape(Capsule())
                }
                .padding(.horizontal, 4)
                
                // Footer buttons (Cancel and Apply)
                HStack(spacing: 12) {
                    Spacer()
                    
                    Button {
                        isPresented = false
                    } label: {
                        Text("Cancel")
                            .font(.custom("Urbanist-Medium", size: 16))
                            .foregroundColor(.black)
                            .frame(width: 110, height: 44)
                            .background(
                                RoundedRectangle(cornerRadius: 30)
                                    .stroke(Color.black, lineWidth: 1)
                            )
                    }
                    
                    Button {
                        var components = Calendar.current.dateComponents([.year, .month, .day], from: selectedTime)
                        var hour24 = selectedHour
                        if isAM {
                            if hour24 == 12 { hour24 = 0 }
                        } else {
                            if hour24 != 12 { hour24 += 12 }
                        }
                        components.hour = hour24
                        components.minute = selectedMinute
                        components.second = 0
                        if let newDate = Calendar.current.date(from: components) {
                            selectedTime = newDate
                        }
                        onApply()
                        isPresented = false
                    } label: {
                        Text("Apply")
                            .font(.custom("Urbanist-SemiBold", size: 16))
                            .foregroundColor(.white)
                            .frame(width: 110, height: 44)
//                            .background(
//                                LinearGradient(
//                                    colors: [
//                                        Color(red: 67/255, green: 56/255, blue: 202/255),
//                                        Color(red: 33/255, green: 28/255, blue: 100/255)
//                                    ],
//                                    startPoint: .leading,
//                                    endPoint: .trailing
//                                )
//                            )
                            .background {
                                Image("BackgroundBtn")
                                    .resizable()
                                    .scaledToFill()
                            }
                            .clipShape(Capsule())
                            .contentShape(Capsule())
                            .foregroundColor(.white)
                            .cornerRadius(30)
                    }
                }
                .padding(.horizontal, 4)
            }
            .padding(20)
            .background(Color.white)
            .cornerRadius(24)
            .padding(.horizontal, 0)
            .frame(maxWidth: 360)
        }
        
        .onAppear {
            let calendar = Calendar.current
            let hour24 = calendar.component(.hour, from: selectedTime)
            let minute = calendar.component(.minute, from: selectedTime)
            
            selectedMinute = minute
            if hour24 >= 12 {
                isAM = false
                selectedHour = hour24 == 12 ? 12 : hour24 - 12
            } else {
                isAM = true
                selectedHour = hour24 == 0 ? 12 : hour24
            }
        }
    }
}



// MARK: - Shared UI Helpers
func header(_ title: String, icon: String) -> some View {
    HStack {
        Image(icon).foregroundColor(.purple)
        Text(title)
            .font(.custom("Urbanist-Medium", size: 15))
        Spacer()
    }
}

func popupButtons(
    onCancel: @escaping () -> Void,
    onApply: @escaping () -> Void
) -> some View {
    
    HStack(spacing: 12) {
        
        // ❌ CANCEL (no data change)
        Button(action: {
            onCancel()
        }) {
            Text("Cancel")
                .foregroundColor(.black)
                .frame(maxWidth: .infinity)
                .padding()
                .overlay(
                    RoundedRectangle(cornerRadius: 45)
                        .stroke(lineWidth: 1)
                        .foregroundColor(.black)
                )
        }
        
        // ✅ APPLY (data update)
        Button(action: {
            onApply()
        }) {
            Text("Apply")
                .frame(maxWidth: .infinity)
                .padding()
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
                .background {
                    Image("BackgroundBtn")
                        .resizable()
                        .scaledToFill()
                }
                .clipShape(Capsule())
                .contentShape(Capsule())
                .foregroundColor(.white)
                .cornerRadius(25)
        }
    }
    
}

extension View {
    func popupStyle() -> some View {
        self.padding()
            .background(Color.white)
            .cornerRadius(20)
            .frame(maxWidth: 340)
    }
}

extension Date {
    func formatted(_ format: String) -> String {
        let f = DateFormatter()
        f.dateFormat = format
        return f.string(from: self)
    }
}

#Preview {
    NewAppointmentScheduleView(flow: .new, chatId: nil)
        .environmentObject(Coordinator())
}


import SwiftUI

struct CustomCalendarView1: View {
    
    // MARK: - PROPERTIES FROM PARENT
    @Binding var selectedDate: Date
    var onClose: () -> Void
    let cellSize: CGFloat = 30
    
    var allowFutureDates: Bool = false   // NEW
    
    // MARK: - STATES
    @State private var currentMonth: Int = 0
    @State private var showYearPicker = false
    @State private var selectedYear: Int = Calendar.current.component(.year, from: Date())

    var body: some View {
        
        VStack(spacing: 22) {
            
            // 🔵 HEADER
            HStack {
                HStack(spacing: 8) {
                    Image(systemName: "calendar")
                        .foregroundColor(Color(hex: "#3D2DB7"))
                    Text("Select Date")
                        .font(.system(size: 18, weight: .semibold))
                }
                
                Spacer()
                
                Button {
                    onClose()
                } label: {
                    ZStack {
                        Circle()
                            .fill(Color.white)
                            .frame(width: 38, height: 38)
                            .shadow(color: .black.opacity(0.1), radius: 2)
                        Image(systemName: "xmark")
                            .foregroundColor(.black)
                    }
                }
            }
            .padding(.horizontal)
            .padding(.top, 30)
            
            
            // 🔵 MONTH + YEAR SELECTOR
            HStack {
                Button {
                    showYearPicker.toggle()
                } label: {
                    HStack(spacing: 6) {
                        Text(dateString(format: "MMMM yyyy"))
                            .font(.system(size: 17, weight: .semibold))
                            .foregroundColor(.black)
                        
                        Image(systemName: "chevron.right")
                            .font(.system(size: 14))
                            .foregroundColor(Color(hex: "#3D2DB7"))
                    }
                }
                
                Spacer()
                
                Button {
                    if currentMonth > 0 {
                        currentMonth -= 1
                    }
                } label: {
                    arrowButton(imageName: "leftArrow")
                        .opacity(currentMonth == 0 ? 0.3 : 1)
                }
                .disabled(currentMonth == 0)
 
                Button {
                    currentMonth += 1
                } label: {
                    arrowButton(imageName: "rightArrow")
                }
            }
            .padding(.horizontal)
            
            
            // 🔵 WEEK ROW
            HStack {
                ForEach(["SUN","MON","TUE","WED","THU","FRI","SAT"], id: \.self) { day in
                    Text(day)
                        .font(.system(size: 11, weight: .semibold))
                        .foregroundColor(.gray)
                        .frame(maxWidth: .infinity)
                }
            }
            .padding(.horizontal)
 
            // 🔵 FIXED HEIGHT CALENDAR GRID (ALWAYS 6 ROWS)
            let columns = Array(repeating: GridItem(.flexible()), count: 7)
            
            LazyVGrid(columns: columns, spacing: 12) {
                ForEach(extractDates1()) { value in
                    dateCell1(value: value)
                        .frame(width: cellSize, height: cellSize)
                }
            }
            .frame(height: (cellSize * 6) + (12 * 5)) // Fixed 6-row height
            .padding(.horizontal)
            
            
            // 🔵 BUTTONS
            HStack(spacing: 20) {
                Spacer()
                
                // Cancel
                Button {
                    onClose()
                } label: {
                    Text("Cancel")
                        .font(.system(size: 16))
                        .foregroundColor(.black)
                        .frame(height: 48)
                        .frame(width: 100)
                      //  .frame(maxWidth: .infinity)
                        .background(
                            RoundedRectangle(cornerRadius: 30)
                                .stroke(Color.black.opacity(0.4), lineWidth: 1)
                        )
                }
                
                // Apply
                Button {
                    onClose()
                } label: {
                    Text("Apply")
                        .font(.system(size: 16, weight: .semibold))
                        .foregroundColor(.white)
                        .frame(height: 48)
                        .frame(width: 100)
                        //.frame(maxWidth: .infinity)
//                        .background(
//                            LinearGradient( colors: [
//                                Color(red: 67/255, green: 56/255, blue: 202/255),
//                                Color(red: 33/255, green: 28/255, blue: 100/255)
//                            ], startPoint: .leading, endPoint: .trailing)
//                        )
                        .background {
                            Image("BackgroundBtn")
                                .resizable()
                                .scaledToFill()
                        }
                        .clipShape(Capsule())
                        .contentShape(Capsule())
                        .foregroundColor(.white)
                        .cornerRadius(30)
                }
            }
            .padding(.horizontal)
            .padding(.bottom, 30)
            
        }
        .frame(maxWidth: 380)
        .background(Color.white)
        .cornerRadius(25)
        .shadow(radius: 10)
        .sheet(isPresented: $showYearPicker) {
            YearPickerView(selectedYear: $selectedYear) { year in
                let diff = year - Calendar.current.component(.year, from: Date())
                currentMonth = diff * 12
                showYearPicker = false
            }
        }
    }
    

    // MARK: - DATE CELL

    @ViewBuilder
    func dateCell1(value: CalendarDate1) -> some View {

        if value.day == -1 {
            Text("")
                .frame(width: 32, height: 32)
        } else {

            let calendar = Calendar.current

            let isSelected = calendar.isDate(
                value.date,
                inSameDayAs: selectedDate
            )

            let today = calendar.startOfDay(for: Date())
            let cellDate = calendar.startOfDay(for: value.date)

            // Disable only past dates
            let isDisabled = cellDate < today

            Text("\(value.day)")
                .font(.system(size: 17))
                .foregroundColor(
                    isDisabled
                    ? .gray
                    : (isSelected ? .white : .black)
                )
                .frame(width: 32, height: 32)
                .background(
                    Circle()
                        .fill(
                            isSelected && !isDisabled
                            ? Color(hex: "#3D2DB7")
                            : .clear
                        )
                )
                .onTapGesture {

                    guard !isDisabled else { return }

                    selectedDate = value.date

                    print("Selected Date:", value.date)
                }
        }
    }
    
//    // MARK: - ARROWS
//    func arrowButton(system: String) -> some View {
//        ZStack {
//            RoundedRectangle(cornerRadius: 10)
//                .fill(Color(hex: "#E6E0FA"))
//                .frame(width: 32, height: 32)
//            Image(systemName: system)
//                .foregroundColor(Color(hex: "#3D2DB7"))
//        }
//    }
    
    // MARK: - ARROWS
    func arrowButton(imageName: String) -> some View {
        ZStack {
//            RoundedRectangle(cornerRadius: 10)
//                .fill(Color(hex: "#E6E0FA"))
//                .frame(width: 32, height: 32)

            Image(imageName)
                .resizable()
                .scaledToFit()
                .frame(width: 25, height: 35)
        }
    }
    
    
    // MARK: - DATE FORMAT
    func dateString(format: String) -> String {
        let calendar = Calendar.current
        let date = calendar.date(byAdding: .month, value: currentMonth, to: Date())!
        
        let formatter = DateFormatter()
        formatter.dateFormat = format
        return formatter.string(from: date)
    }
    
    
    // MARK: - DATE GRID
//    func extractDates() -> [CalendarDate1] {
//        let calendar = Calendar.current
//        let current = calendar.date(byAdding: .month, value: currentMonth, to: Date())!
//        
//        var days = current.getAllDates().map {
//            CalendarDate1(day: calendar.component(.day, from: $0), date: $0)
//        }
//        
//        // Insert blanks for starting weekday
//        let firstWeekday = calendar.component(.weekday, from: days.first!.date)
//        for _ in 1..<firstWeekday {
//            days.insert(CalendarDate1(day: -1, date: Date()), at: 0)
//        }
//        
//        return days
//    }
    
    func extractDates1() -> [CalendarDate1] {
        
        let calendar = Calendar.current

        // Current displayed month
        let current = calendar.date(
            byAdding: .month,
            value: currentMonth,
            to: Date()
        )!

     
        
        var days = current.getAllDates1().map {
            CalendarDate1(
                day: calendar.component(.day, from: $0),
                date: $0
            )
        }

        let firstWeekday = calendar.component(.weekday, from: days.first!.date)

        for _ in 1..<firstWeekday {
            days.insert(
                CalendarDate1(day: -1, date: Date()),
                at: 0
            )
        }

        // Always show 6 rows (42 cells)
        while days.count < 42 {
            days.append(
                CalendarDate1(day: -1, date: Date())
            )
        }

        return days
    }
}


// MARK: - CALENDAR MODEL
struct CalendarDate1: Identifiable {
    var id = UUID()
    var day: Int
    var date: Date
}


// MARK: - DATE EXTENSIONS
extension Date {
    func getAllDates1() -> [Date] {
        let calendar = Calendar.current
        let range = calendar.range(of: .day, in: .month, for: self)!
        
        return range.compactMap { day -> Date in
            var components = calendar.dateComponents([.year, .month], from: self)
            components.day = day
            return calendar.date(from: components)!
        }
    }
}




// MARK: - YEAR PICKER VIEW
struct YearPickerView1: View {
    @Binding var selectedYear: Int
    var onYearSelected: (Int) -> Void
    
    var body: some View {
        NavigationView {
            List {
                ForEach(1980...2050, id: \.self) { year in
                    Button {
                        selectedYear = year
                        onYearSelected(year)
                    } label: {
                        HStack {
                            Text("\(year)")
                                .foregroundColor(.primary)
                            Spacer()
                            if selectedYear == year {
                                Image("Checkmark")
                                    .resizable()
                                    .frame(width: 24, height: 24)
                            }
                        }
                    }
                }
            }
            .navigationTitle("Select Year")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}


#Preview {
    CustomCalendarView1(
        selectedDate: .constant(Date()),
        onClose: {}
    )
}
