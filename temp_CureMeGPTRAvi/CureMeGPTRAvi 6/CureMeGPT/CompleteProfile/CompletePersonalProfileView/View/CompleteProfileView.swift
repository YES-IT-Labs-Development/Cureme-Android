//
//  CompleteProfileView.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 02/12/25.
//

import SwiftUI
import PhotosUI

struct CompleteProfileView: View {
    @EnvironmentObject private var coordinator: Coordinator
   @StateObject private var vm = ProfileViewModel()
   // @ObservedObject var vm: ProfileViewModel
    @State private var closeAllDropdowns = false
    let flow: ProfileFlowType
    @State private var showCalendar = false
    @State private var selectedImage: UIImage? = nil
    @State private var showImageOptions = false
    @State private var showCamera = false
    @State private var showGallery = false
    @State private var selectedItem: PhotosPickerItem?
    
    @State private var showPhoneOTP = false
    @State private var showEmailOTP = false
    
    @State private var showPhoneAlert = false
    @State private var showEmailAlert = false
    @State private var showImageSheet = false
    var body: some View {
        ZStack() {
            VStack(spacing: 22) {
                // MARK: HEADER
                ProfileHeaderView(
                    title: flow.title,
                    showSkip: flow.showSkipButton,
                    onBack: {
                        saveDraftProfile()
                        flow.onBackAction(coordinator)
                    },
                    onSkip: {
                        flow.onDoneAction(coordinator)
                    }
                )
                
                ScrollView {
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
                            StepTab(icon: "PersonalSelected", title: "Personal", selected: true)
                            StepTab(icon: "GeneralUnSelected", title: "General")
                            StepTab(icon: "HistoryUnSelected", title: "History")
                            StepTab(icon: "DocumentsUnSelected", title: "Documents")
                        }
                        .padding(.top, 10)
                        .padding(.bottom, 26)
                    }
                    VStack(alignment: .leading, spacing: 14) {
                        CustomTitle("Full Name *")
                        CustomTextFieldProfile(placeholder: "James Carter", text: $vm.profile.name)
                        
                        if let error = vm.fullNameError {
                            ValidationText(message: error)
                        }
                        HStack {
                            CustomTitle("Contact Number")
                            
//                            Text("(Optional)")
//                                .font(.custom("Urbanist-Italic", size: 15))
//                                .foregroundColor(.black)
                        }
                        ZStack(alignment: .trailing) {
                            
                            CustomTextFieldProfile(
                                placeholder: "555 123 456",
                                text: $vm.profile.phone
                            )
                            .keyboardType(.numberPad)
                            if flow == .profileSetup && !vm.profile.phone.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                                Button {
                                    hideKeyboard()   // ✅ keyboard band
                                    
                                    let cleanedPhone = vm.profile.phone.trimmingCharacters(in: .whitespacesAndNewlines)
                                    if cleanedPhone.count != 10 || !cleanedPhone.allSatisfy(\.isNumber) {
                                        vm.errorMessage = "Please enter a valid 10-digit contact number."
                                        vm.isPresentAlert = true
                                        return
                                    }
                                    
                                    vm.emailPhone = cleanedPhone
                                    vm.apiforVerifyPhoneEmail { success in
                                        if success {
                                            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                                                vm.showToast = false
                                                showPhoneOTP = true
                                            }
                                        }
                                    }
                                    
                                }label: {
                                    Text(vm.isPhoneVerified ? "Verified" : "Verify")
                                        .font(.custom("Urbanist-Medium", size: 14))
                                        .foregroundColor(.white)
                                        .padding(.horizontal, 14)
                                        .padding(.vertical, 8)
                                        .background(
                                            Group {
                                                if vm.isPhoneVerified {
                                                    Color.green
                                                } else {
                                                    LinearGradient(
                                                        colors: [
                                                            Color(red: 67/255, green: 56/255, blue: 202/255),
                                                            Color(red: 33/255, green: 28/255, blue: 100/255)
                                                        ],
                                                        startPoint: .leading,
                                                        endPoint: .trailing
                                                    )
                                                }
                                            }
                                        )
                                        .cornerRadius(8)
                                }
                                .padding(.trailing, 8)
                                .disabled(vm.isPhoneVerified)
                            }
                        }
                        
//                        if let error = vm.contactError {
//                            ValidationText(message: error)
//                        }
                        
                        HStack {
                            CustomTitle("Email Address")
                            
//                            Text("(Optional)")
//                                .font(.custom("Urbanist-Italic", size: 15))
//                                .foregroundColor(.black)
                        }
                        ZStack(alignment: .trailing) {
                            
                            CustomTextFieldProfile(
                                placeholder: "Email Address",
                                text: $vm.profile.email
                            )
                            .keyboardType(.emailAddress)
                            if flow == .profileSetup {
                                Button {
                                    hideKeyboard()   //keyboard band
                                    
                                    let cleanedEmail = vm.profile.email.trimmingCharacters(in: .whitespacesAndNewlines)
                                    let emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
                                    let emailPredicate = NSPredicate(format:"SELF MATCHES %@", emailRegex)
                                    let isValid = emailPredicate.evaluate(with: cleanedEmail)
                                    
                                    if cleanedEmail.isEmpty || !isValid {
                                        vm.errorMessage = "Please enter a valid email address."
                                        vm.isPresentAlert = true
                                        return
                                    }
                                    
                                    vm.emailPhone = cleanedEmail
                                    vm.apiforVerifyPhoneEmail { success in
                                        if success {
                                            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                                                vm.showToast = false
                                                showEmailOTP = true
                                            }
                                        }
                                    }
                                    
                                }  label: {
                                    Text(vm.isEmailVerified ? "Verified" : "Verify")
                                        .font(.custom("Urbanist-Medium", size: 14))
                                        .foregroundColor(.white)
                                        .padding(.horizontal, 14)
                                        .padding(.vertical, 8)
                                        .background(
                                            Group {
                                                if vm.isEmailVerified {
                                                    Color.green
                                                } else {
                                                    LinearGradient(
                                                        colors: [
                                                            Color(red: 67/255, green: 56/255, blue: 202/255),
                                                            Color(red: 33/255, green: 28/255, blue: 100/255)
                                                        ],
                                                        startPoint: .leading,
                                                        endPoint: .trailing
                                                    )
                                                }
                                            }
                                        )
                                        .cornerRadius(8)
                                }
                                .padding(.trailing, 8)
                                .disabled(vm.isEmailVerified)
                            }
                        }
//                        if let error = vm.emailError {
//                            ValidationText(message: error)
//                        }
                        
                        CustomTitle("Date Of Birth *")
                        DateField(date: $vm.dob) {
                            withAnimation { showCalendar = true }
                        }
                        
                        if let error = vm.dobError {
                            ValidationText(message: error)
                        }
                        
                        CustomTitle("Gender *")
                        
                        DropdownField(
                            selected: $vm.profile.gender,
                            options: vm.genderOptions,
                            placeholder: "Gender"
                        )
                        
                        if let error = vm.genderError {
                            ValidationText(message: error)
                        }
                        
                        if flow == .addFamilyMember {
                            CustomTitle("Relation *")
                            
                            DropdownField(
                                selected: $vm.relation,
                                options: vm.relationOptions,
                                placeholder: "Relation"
                            )
                            
                            if let error = vm.relationError {
                                ValidationText(message: error)
                            }
                        }
                        
                        CustomTitle("Height (Cm/Ft) *")
                            .font(.custom("Urbanist-Regular", size: 15))
                        
                        HStack {
                            CustomTextFieldProfile(
                                placeholder: "Height",
                                text: $vm.profile.height
                            )
                            .keyboardType(.numberPad)
                            
                            UnitDropdown(
                                selectedUnit: $vm.profile.heightUnit,
                                units: vm.heightUnits
                            )
                        }
                        .zIndex(2)
                        
                        if let error = vm.heightError {
                            ValidationText(message: error)
                        }
                        
                        CustomTitle("Weight (Kg/Lb) *")
                        
                        HStack {
                            CustomTextFieldProfile(
                                placeholder: "Weight",
                                text: $vm.profile.weight
                            )
                            .keyboardType(.numberPad)
                            
                            UnitDropdown(
                                selectedUnit: $vm.profile.weightUnit,
                                units: vm.weightUnits
                            )
                        }
                        .zIndex(1)
                        
                        if let error = vm.weightError {
                            ValidationText(message: error)
                        }
                        HStack {
                            CustomTitle("Profile Photo")
                            
                            Text("(Optional)")
                                .font(.custom("Urbanist-Italic", size: 15))
                                .foregroundColor(.black)
                        }
                        
                        //                        UploadPicker(fileChosen: vm.profile.profileImageData != nil) {
                        //                            showImageSheet = true
                        //                        }
                        
                        UploadPicker(
                            fileChosen: vm.profile.profileImageData != nil ||
                            !(vm.profile.profileImage ?? "").isEmpty
                        ) {
                            showImageSheet = true
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.horizontal, 20)
                    
                    Button(action: {
                        
                        print("\n🔘 Button Clicked")
                        print("Flow Type: \(flow)")
                        
                        // ✅ OTP Validation (sirf profileSetup me)
                        if flow == .profileSetup {
                            let hasPhone = !vm.profile.phone.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
                            
                            if hasPhone && vm.isPhoneVerified == false {
                                print("Phone not verified")
                                showPhoneAlert = true
                                return
                                
                            } else if vm.isEmailVerified == false {
                                print("Email not verified")
                                showEmailAlert = true
                                return
                            }
                            
                            print("✅ Phone & Email Verified")
                        }
                        
                        vm.showValidationErrors = true
                        print("🧾 Form validation started")
                        
                        if vm.isFormValid() {
                            print("✅ Form is valid")
                            
                            saveDraftProfile()
                            
                            switch flow {
                                
                            case .profileSetup:
                                print("🧩 Action: Complete Personal Profile API calling...")
                                
                                vm.completePersonalProfileAPI { success in
                                    print("📡 API Response (Complete Profile): \(success ? "Success" : "Failure")")
                                    
                                    if success {
                                        print("🚀 Navigating to General Profile (Profile Setup)")
                                        coordinator.push(.generalProfileView(flow: flow))
                                    }
                                }
                                

                                
                            case .addFamilyMember:

                                vm.addFamilyMemberAPI { success in

                                    print("📡 API Response (Add Member): \(success)")

                                    guard success else { return }

                                    coordinator.push(.generalProfileView(flow: flow))
                                }
                                
                            case .editProfile:
                                print("✏️ Action: Update Personal Profile API calling...")
                                
                                vm.apiforUpdatePersonalProfile { success in
                                    print("📡 API Response (Update Profile): \(success ? "Success" : "Failure")")
                                    
                                    if success {
                                        print("🚀 Navigating to General Profile (Edit Profile)")
                                        coordinator.push(.generalProfileView(flow: flow))
                                    }
                                }
                                
                            case .editFamilyMember:
                                print("🛠 Action: Edit Family Member (Not implemented)")
                                
                                vm.apiforUpdateMemberPersonalProfile { success in
                                    print("📡 API Response (Edit Member): \(success)")
                                    
                                    coordinator.push(.generalProfileView(flow: flow))
                                }
                            }
                            
                        } else {
                            print("❌ Form validation failed")
                        }
                        
                    }) {
                        Text("Save & Continue")
                            .font(.custom("Urbanist-SemiBold", size: 16))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background( Image("BackgroundBtn") // Asset name
                                        .resizable()
                                         .scaledToFill()   )
                            .cornerRadius(30)
                    }
                    
                    .padding(.horizontal, 20)
                    .padding(.vertical)
                    
                }
                .disableScrollBounce()
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .blur(radius: showCalendar ? 3 : 0)
            if vm.showActivity {
                CustomLoderView(isVisible: $vm.showActivity)
                    .ignoresSafeArea()
            }
            
            // MARK: POPUP OVERLAY
            if showCalendar {
                
                // Background dim
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture {
                        withAnimation { showCalendar = false }
                    }
                    .transition(.opacity)
                
                // Center Popup Calendar
                CustomCalendarView(
                    selectedDate: $vm.dob ,
                    onClose: {
                        withAnimation { showCalendar = false }
                    }
                )
                .transition(.scale)
                .zIndex(10)
            }
            
            // MARK: EMAIL OTP POPUP
       
            
            if showEmailOTP {
                OTPVerificationView(
                    vm: vm,
                    type: .email,
                    serverOTP: vm.otpFromServer,
                    completion: { success in
                        
                        if success {
                            vm.isEmailVerified = true
                        }
                        
                        showEmailOTP = false
                    },
                    onClose: {
                        showEmailOTP = false
                    }
                    
                )
            }
            
            // MARK: PHONE OTP POPUP
            if showPhoneOTP {
                OTPVerificationView(
                    vm: vm,
                    type: .phone,
                    serverOTP: vm.otpFromServer,
                    completion: { success in
                        
                        if success {
                            vm.isPhoneVerified = true
                        }
                        showPhoneOTP = false
                        
                    },
                    onClose: {
                        showPhoneOTP = false
                    }
                )
            }
            
        }
        .overlay(alignment: .bottom) {
            
            if vm.showToast {
                ToastView(message: vm.toastMessage)
                    .padding(.top, 60)
                    .transition(.move(edge: .top).combined(with: .opacity))
                    .zIndex(9999)
            }
            
        }
        .animation(.easeInOut, value: vm.showToast)
        .photosPicker(
            isPresented: $showGallery,
            selection: $selectedItem,
            matching: .images
        )
        .sheet(isPresented: $showCamera) {
            CameraPicker { image in
                selectedImage = image
                if let data = image.jpegData(compressionQuality: 0.8) {
                    vm.imgData = data
                    vm.profile.profileImageData = data
                }
            }
        }
        .sheet(isPresented: $showImageSheet) {
            
            VStack(spacing: 20) {
                
                Text("Choose Photo")
                    .font(.headline)
                
                Button("Camera") {
                    showImageSheet = false
                    if UIImagePickerController.isSourceTypeAvailable(.camera) {
                        showCamera = true
                    } else {
                        vm.errorMessage = "Camera is not available on this device."
                        vm.isPresentAlert = true
                    }
                }
                
                Button("Gallery") {
                    showImageSheet = false
                    showGallery = true
                }
                
                Button("Cancel") {
                    showImageSheet = false
                }
            }
            .padding()
            .presentationDetents([.height(200)])
        }
        
        .onAppear {
            
            if flow == .profileSetup  {
                
                loadUserProfileData()
                
                
            } else if flow == .editProfile {
                
                vm.apiMyGetPersoalProfile { success in
                    if success {
                        print("Personal Profile Data Loaded")
                    }
                }
                
            } else if flow == .editFamilyMember {
                
                vm.apiMyGetMemberPersoalProfile { success in
                    if success {
                        print("Member Personal Profile Data Loaded")
                    }
                }
            } else if flow == .addFamilyMember {
               // loadDraftProfile()
            }
            print("Current Flow: \(flow)")
        }
        
        .onChange(of: selectedItem) { newItem in
            guard let newItem else { return }
            
            Task {
                if let data = try? await newItem.loadTransferable(type: Data.self) {
                    vm.imgData = data
                    //vm.profile.profileImage = data
                    vm.profile.profileImageData = data
                }
            }
        }
        
        .alert("Enter Phone Number", isPresented: $showPhoneAlert) {
            Button("OK", role: .cancel) { }
        }
        
        .alert("Enter Email", isPresented: $showEmailAlert) {
            Button("OK", role: .cancel) { }
        }
        
        .customAlert(
            isPresented: $vm.isPresentAlert,
            message: vm.errorMessage ?? ""
        ) {
            print("OK tapped")
        }
        
        //        .alert(isPresented: $vm.isPresentAlert) {
        //            Alert(title: Text(vm.errorMessage ?? ""))
        //        }
        
        
        
        .animation(.easeOut, value: showCalendar)
        .contentShape(Rectangle())
        .onTapGesture {
            closeAllDropdowns.toggle()   // triggers dropdown close
            showCalendar = false         // optional: also close calendar
        }
        .keyboardDoneButton()
        .onDisappear {
            hideKeyboard()
        }
        
        .onChange(of: vm.dob) { newValue in
            print("DOB Changed ", newValue)
        }
        
      
    }
    
   
    
    private func loadDraftProfile() {

        vm.profile.name = UserDefaults.standard.string(forKey: "draft_name") ?? ""
        vm.profile.phone = UserDefaults.standard.string(forKey: "draft_phone") ?? ""
        vm.profile.email = UserDefaults.standard.string(forKey: "draft_email") ?? ""

        vm.profile.gender = UserDefaults.standard.string(forKey: "draft_gender") ?? ""

        vm.profile.height = UserDefaults.standard.string(forKey: "draft_height") ?? ""
        vm.profile.weight = UserDefaults.standard.string(forKey: "draft_weight") ?? ""

        vm.profile.heightUnit = UserDefaults.standard.string(forKey: "draft_height_unit") ?? "Cm"
        vm.profile.weightUnit = UserDefaults.standard.string(forKey: "draft_weight_unit") ?? "Kg"

        vm.isPhoneVerified = UserDefaults.standard.bool(forKey: "draft_phone_verified")
        vm.isEmailVerified = UserDefaults.standard.bool(forKey: "draft_email_verified")

        let dobTime = UserDefaults.standard.double(forKey: "draft_dob")
        if dobTime > 0 {
            vm.dob = Date(timeIntervalSince1970: dobTime)
        }
    }
    
    func hideKeyboard() {
        UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder),
                                        to: nil,
                                        from: nil,
                                        for: nil)
    }
    
//    func loadUserProfileData() {
//        
//        if let data = UserDefaults.standard.data(forKey: "user_data"),
//           let user = try? JSONDecoder().decode(User.self, from: data) {
//            
//            vm.profile.name = user.name ?? ""
//            vm.profile.phone = user.phone ?? ""
//            vm.profile.email = user.email ?? ""
//            
//            if let phone = user.phone, !phone.isEmpty {
//                vm.isPhoneVerified = true
//            }
//            
//            if let email = user.email, !email.isEmpty {
//                vm.isEmailVerified = true
//            }
//        }
//    }
    
    func loadUserProfileData() {

        // First load draft data if available
        if let draftPhone = UserDefaults.standard.string(forKey: "draft_phone"),
           !draftPhone.isEmpty {

            vm.profile.name = UserDefaults.standard.string(forKey: "draft_name") ?? ""
            vm.profile.phone = draftPhone
            vm.profile.email = UserDefaults.standard.string(forKey: "draft_email") ?? ""

            vm.isPhoneVerified = UserDefaults.standard.bool(forKey: "draft_phone_verified")
            vm.isEmailVerified = UserDefaults.standard.bool(forKey: "draft_email_verified")

            return
        }

        // Otherwise load original user data
        if let data = UserDefaults.standard.data(forKey: "user_data"),
           let user = try? JSONDecoder().decode(User.self, from: data) {

            vm.profile.name = user.name ?? ""
            vm.profile.phone = user.phone ?? ""
            vm.profile.email = user.email ?? ""

            if let phone = user.phone, !phone.isEmpty {
                vm.isPhoneVerified = true
            }

            if let email = user.email, !email.isEmpty {
                vm.isEmailVerified = true
            }
        }
    }
    
    
    private func saveDraftProfile() {

        UserDefaults.standard.set(vm.profile.name, forKey: "draft_name")
        UserDefaults.standard.set(vm.profile.phone, forKey: "draft_phone")
        UserDefaults.standard.set(vm.profile.email, forKey: "draft_email")

        UserDefaults.standard.set(vm.profile.gender, forKey: "draft_gender")

        UserDefaults.standard.set(vm.profile.height, forKey: "draft_height")
        UserDefaults.standard.set(vm.profile.weight, forKey: "draft_weight")

        UserDefaults.standard.set(vm.profile.heightUnit, forKey: "draft_height_unit")
        UserDefaults.standard.set(vm.profile.weightUnit, forKey: "draft_weight_unit")

        UserDefaults.standard.set(vm.relation, forKey: "draft_relation")

        UserDefaults.standard.set(vm.dob?.timeIntervalSince1970 ?? 0, forKey: "draft_dob")

        UserDefaults.standard.set(vm.isPhoneVerified, forKey: "draft_phone_verified")
        UserDefaults.standard.set(vm.isEmailVerified, forKey: "draft_email_verified")
    }
    
    private var headerView: some View {
        HStack {
            Button {
                coordinator.pop()
            } label: {
                Image("backIcon")
                    .resizable()
                    .frame(width: 45, height: 45)
            }
            
            Text(flow.title)
                .font(.custom("Urbanist-Medium", size: 20))
                .foregroundColor(.black)
            
            Spacer()
            
            if flow.showSkipButton {
                Button("Skip for Now") {
                    coordinator.selectedAppTab = .home
                    coordinator.push(.tabBarView)
                }
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundStyle(
                    LinearGradient(
                        colors: [
                            Color(hex: "#4338CA"),
                            Color(hex: "#211C64")
                        ],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                )
            }
        }
        .padding(.horizontal)
        .padding(.top, 10)
    }
    
    private var bottomButton: some View {
        Button {
            handlePrimaryAction()
        } label: {
            Text(flow.primaryButtonTitle)
                .font(.custom("Urbanist-SemiBold", size: 16))
                .foregroundColor(.white)
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
                .cornerRadius(30)
        }
        .padding(.horizontal)
        .padding(.vertical)
    }
    
    private func handlePrimaryAction() {
        switch flow {
        case .profileSetup:
            coordinator.push(.generalProfileView(flow: .profileSetup))
            
        case .addFamilyMember:
            coordinator.pop()
            
        case .editProfile:
            coordinator.pop()
        case .editFamilyMember:
            coordinator.pop()
        }
    }
}

struct CustomTitle: View {
    var text: String
    
    init(_ text: String) {
        self.text = text
    }
    
    var body: some View {
        HStack(spacing: 0) {
            Text(mainText)
               
                .font(.custom("Urbanist-Regular", size: 15))
                .foregroundColor(.black)
            
            if hasStar {
                Text("*")
                    .font(.custom("Urbanist-Regular", size: 15))
                    .foregroundColor(Color(hex: "#F31D1D"))
            }
        }
    }
    
    private var hasStar: Bool {
        text.contains("*")
    }
    
    private var mainText: String {
        text.replacingOccurrences(of: "*", with: "")
    }
}


struct CustomTextFieldProfile: View {
    var placeholder: String
    @Binding var text: String
    var disabled: Bool = false
    
    var body: some View {
        TextField(placeholder, text: $text)
            .disabled(disabled)
            .padding(16)
            .font(.custom("Urbanist-Regular", size: 15))
            .foregroundColor(disabled ? .gray : .black)
            .opacity(disabled ? 0.6 : 1.0)
            .frame(height: 50)
            .background(disabled ? Color.gray.opacity(0.08) : Color.clear)
            .overlay(
                RoundedRectangle(cornerRadius: 25)
                    .stroke(disabled ? Color.gray.opacity(0.3) : Color(hex: "#697383"), lineWidth: 1)
            )
            .cornerRadius(25)
    }
}

struct StepTab: View {
    var icon: String
    var title: String
    var selected: Bool = false
    
    var body: some View {
        VStack {
            Image(icon)
                .resizable()
                .frame(width: 42, height: 42)
                .scaledToFit()
                .clipShape(Circle())
            
            Text(title)
            //.font(.system(size: 12))
                .font(.custom("Urbanist-Regular", size: 14))
                .foregroundColor(selected ? Color(hex: "#4338CA") : Color(hex: "#CED4DA"))
        }
    }
}

//struct DropdownField: View

struct DropdownField: View {
    @Binding var selected: String
    let options: [String]
    let placeholder: String
    var onTap: (() -> Void)?   // API call
    var disabled: Bool = false
    
    @State private var isOpen = false
    
    var body: some View {
        VStack(spacing: 6) {
            
            // 🔹 MAIN FIELD
            Button {
                guard !disabled else { return }
                onTap?() // ✅ API CALL
                
                withAnimation {
                    isOpen.toggle() // ✅ OPEN / CLOSE FIX
                }
            } label: {
                HStack {
                    Text(displayText)
                        .foregroundColor(disabled ? .gray : (selected.isEmpty ? .gray : .black))
                        .font(.custom("Urbanist-Regular", size: 16))
                    
                    Spacer()
                    
                    if !disabled {
                        Image(isOpen ? "DropUp" : "DropDown")
                    }
                }
                .padding(.horizontal, 16)
                .frame(height: 55) // ✅ FIX HEIGHT
                .background(disabled ? Color.gray.opacity(0.08) : Color.clear)
                .opacity(disabled ? 0.6 : 1.0)
                .overlay(
                    RoundedRectangle(cornerRadius: 27.5) // ✅ PERFECT PILL
                        .stroke(disabled ? Color.gray.opacity(0.3) : Color(hex: "#697383"), lineWidth: disabled ? 0.4 : 0.6)
                )
                .cornerRadius(27.5)
            }
            .disabled(disabled)
            
            // 🔽 DROPDOWN LIST
            if isOpen {
                ScrollView {
                    VStack(spacing: 8) {
                        ForEach(options, id: \.self) { option in
                            dropdownRow(option)
                        }
                    }
                    .padding(8)
                }
                .background(Color.white)
                .cornerRadius(16)
                .shadow(color: .black.opacity(0.1), radius: 5)
                .padding(.top, 4)
                .transition(.opacity.combined(with: .move(edge: .bottom)))
            }
        }
        
    }
    
    private var displayText: String {
        selected.isEmpty ? placeholder : selected
    }
    
    private func dropdownRow(_ option: String) -> some View {
        HStack {
            Text(option)
                .font(.custom("Urbanist-Regular", size: 16))
                .foregroundColor(selected == option ? .white : .black)
            Spacer()
        }
        .padding(.horizontal, 12)
        .frame(height: 40)
        .background(
            Group {
                if selected == option {
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
            selected = option
            
            withAnimation {
                isOpen = false // ✅ CLOSE AFTER SELECT
            }
        }
    }
}

// HELPER TO READ FIELD WIDTH
struct WidthReader: View {
    @Binding var width: CGFloat
    
    var body: some View {
        GeometryReader { geo in
            Color.clear
                .preference(key: WidthKey.self, value: geo.size.width)
        }
        .onPreferenceChange(WidthKey.self) { value in
            self.width = value
        }
    }
}

struct WidthKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) {
        value = nextValue()
    }
}


import SwiftUI

struct UnitDropdown: View {
    @Binding var selectedUnit: String
    var units: [String]
    
    @State private var showDropdown = false
    
    var body: some View {
        Button {
            withAnimation(.easeInOut) {
                showDropdown.toggle()
            }
        } label: {
            HStack {
                Text(selectedUnit)
                    .font(.custom("Urbanist-Regular", size: 16))
                    .foregroundColor(.black)
                
                Spacer()
                
                Image("DropDown")
            }
            .padding()
            .frame(width: 120, height: 50)
            .background(Color.white)
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .stroke(Color.gray.opacity(0.3), lineWidth: 1.2)
            )
        }
        .overlay(
            dropdownOverlay,
            alignment: .top
        )
    }
    
    // MARK: - Dropdown Overlay (OPEN UP)
    private var dropdownOverlay: some View {
        Group {
            if showDropdown {
                VStack(spacing: 0) {
                    ForEach(units, id: \.self) { unit in
                        Button {
                            selectedUnit = unit
                            withAnimation { showDropdown = false }
                        } label: {
                            HStack {
                                Text(unit)
                                    .foregroundColor(.black)
                                Spacer()
                            }
                            .padding()
                        }
                        
                        if unit != units.last {
                            Divider()
                        }
                    }
                }
                .frame(width: 120)
                .background(Color.white)
                .cornerRadius(12)
                .shadow(color: .black.opacity(0.15), radius: 8, y: 4)
                .offset(y: 58)
                .transition(.opacity.combined(with: .move(edge: .bottom)))
                .zIndex(100)
            }
        }
    }
}

struct DateField: View {
    @Binding var date: Date?
    var onTap: () -> Void
    
    var body: some View {
        Button(action: onTap) {
            HStack {
                Text(displayText)
                    .foregroundColor(date == nil ? .gray : .black)
                    .font(.custom("Urbanist-Regular", size: 16))
                
                Spacer()
                
                Image("CalenderIcon")
                    .foregroundColor(.gray)
            }
            .padding()
            .frame(height: 50)
            .overlay(
                RoundedRectangle(cornerRadius: 25)
                    .stroke(Color(hex: "#697383"), lineWidth: 1)
            )
            .cornerRadius(25)
        }
    }
    
    private var displayText: String {
        if let date = date {
            return dateFormatted(date)
        } else {
            return "Date of birth"
        }
    }
    
    func dateFormatted(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd MMM yyyy"
        return formatter.string(from: date)
    }
}

struct UploadPicker: View {
    var fileChosen: Bool
    var action: () -> Void
    var body: some View {
        HStack {
            Text("Choose File")
                .font(.custom("Urbanist-Medium", size: 12))
                .foregroundColor(.white)
                .padding(10)
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
                .cornerRadius(25)
            
            Text(fileChosen ? "File Selected" : "No file chosen")
                .font(.custom("Urbanist-Regular", size: 15))
                .foregroundColor(Color(hex: "#697383"))
            Spacer()
        }
        .padding()
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(style: StrokeStyle(lineWidth: 1, dash: [6]))
                .foregroundColor(Color.gray.opacity(0.5))
        )
        .onTapGesture {
            action()
        }
    }
}

//struct CompleteProfileView_Previews: PreviewProvider {
//    static var previews: some View {
//        CompleteProfileView(vm: vm, flow: .profileSetup)
//            .previewDisplayName("Light Mode")
//            .preferredColorScheme(.light)
//        
//    }
//}

struct CameraPicker: UIViewControllerRepresentable {
    
    var completion: (UIImage) -> Void
    
    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.sourceType = .camera
        picker.delegate = context.coordinator
        return picker
    }
    
    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        
        let parent: CameraPicker
        
        init(_ parent: CameraPicker) {
            self.parent = parent
        }
        
        func imagePickerController(_ picker: UIImagePickerController,
                                   didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
            
            if let image = info[.originalImage] as? UIImage {
                parent.completion(image)
            }
            
            picker.dismiss(animated: true)
        }
    }
}
