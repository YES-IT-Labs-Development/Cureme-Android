//
//  CompleteDocProfileView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/12/25.
//

import SwiftUI
import UniformTypeIdentifiers

struct CompleteDocProfileView: View {
    @StateObject private var viewModel = UploadDocumentsViewModel()
    @EnvironmentObject private var coordinator: Coordinator
    @State private var showPicker = false
    @State private var showPopup: Bool = false
    @State private var popupAction: (() -> Void)? = nil
    
    @State private var showOptions = false
    @State private var showImagePicker = false
    @State private var showCamera = false
    @State private var showDocumentPicker = false
    
    let flow: ProfileFlowType
    
    var body: some View {
        ZStack {
            VStack(spacing: 24) {
                ProfileHeaderView(
                    title: flow.title,
                    showSkip: flow.showSkipButton,
                    onBack: {
                        coordinator.pop()
                    },
                    onSkip: nil
                )
                
                ScrollView {
                    headerSection
                    uploadBox
                    attachedFilesSection
                    infoSection
                    Spacer()
                }
                .disableScrollBounce()
                getStartedButton
            }
            .padding()
            //            .sheet(isPresented: $showPicker) {
            //                DocumentPicker()
            //            }
            
            
            .onAppear {
                if flow == .profileSetup  {
                    
                } else if flow == .editProfile {
                    
                    viewModel.getProfileDocumentAPI { success in
                        if success {
                            print("Document Profile Data Loaded")
                        }
                    }
                    
                } else if flow == .editFamilyMember {
                    
                    viewModel.getfamilyDocumentAPI{ success in
                        if success {
                            print("Famly Document Data Loaded")
                        }
                    }
                }
                print("Current Flow: \(flow)")
            }
            
            // POPUP LAYER
            if showPopup {
                popupOverlay
            }
            if viewModel.showActivity {
                CustomLoderView(isVisible: $viewModel.showActivity)
                    .ignoresSafeArea()
            }
        }
        .sheet(isPresented: $showImagePicker) {
            ImagePicker12(sourceType: .photoLibrary) { image in
                // viewModel.addImage(image)
                // viewModel.addImage(image)
                viewModel.addImage(image)
            }
        }
        .sheet(isPresented: $showCamera) {
            ImagePicker12(sourceType: .camera) { image in
                viewModel.addImage(image)
            }
        }
        .sheet(isPresented: $showDocumentPicker) {
            DocumentPicker { url in
                print(url, "Saved URL")
                // viewModel.addFile(url)
                viewModel.addFile(url)
            }
        }
        .animation(.easeInOut, value: showPopup)
        .keyboardDoneButton()
        .onDisappear {
            UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
        }
        .sheet(isPresented: $showOptions) {
            
            FileSourceBottomSheet(
                onCamera: {
                    showOptions = false
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                        if UIImagePickerController.isSourceTypeAvailable(.camera) {
                            showCamera = true
                        } else {
                            viewModel.errorMessage = "Camera is not available on this device."
                            viewModel.isPresentAlert = true
                        }
                    }
                },
                onGallery: {
                    showOptions = false
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                        showImagePicker = true
                    }
                },
                onDocument: {
                    showOptions = false
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
                        showDocumentPicker = true
                    }
                }
            )
        }
        .customAlert(
                  isPresented: $viewModel.isPresentAlert,
                  message: viewModel.errorMessage ?? "Error"
              ) {
                  print("OK tapped")
              }
        
//        .alert(
//            viewModel.errorMessage ?? "Error",
//            isPresented: $viewModel.isPresentAlert
//        ) {
//            Button("OK", role: .cancel) { }
//        }
        
        
    }
}

// MARK: - UI Sections
private extension CompleteDocProfileView {
    var headerSection: some View {
        VStack(spacing: 22) {
            // MARK: HEADER
            
            
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
                    StepTab(icon: "RightCheckMark", title: "History", selected: true)
                    StepTab(icon: "DocumentsSelected", title: "Documents", selected: true)
                }
                .padding(.top, 10)
                .padding(.bottom, 40)
            }
        }
    }
    
    var uploadBox: some View {
        VStack{
            HStack(spacing: 0) {
                Text("Upload Files ")
                    .font(.custom("Urbanist-Regular", size: 15))
                    .foregroundColor(.black)
                //.frame(maxWidth: .infinity, alignment: .leading)
                
                Text("(X-Rays, Dental Scans, Prescriptions, Lab Reports)")
                    .font(.custom("Urbanist-Italic", size: 12))
                    .foregroundColor(.black)
                    .frame(maxWidth: .infinity, alignment: .leading)
                
            }
            .padding(.bottom, 4)
            
            HStack(spacing: 8) {
                Button("Choose File") {
                    //showPicker = true
                    
                    showOptions = true
                }
                .font(.custom("Urbanist-Medium", size: 12))
                .foregroundColor(.white)
                .frame(width: 90)
                .frame(height: 30)
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
                
                Text(viewModel.uploadedFiles.isEmpty ? "No File Chosen" : "\(viewModel.uploadedFiles.count) File Selected")
                    .font(.custom("Urbanist-Regular", size: 15))
                    .foregroundColor(Color(hex: "#697383"))
                Spacer()
            }
            
            .frame(maxWidth: .infinity)
            .padding()
            
            .overlay(
                RoundedRectangle(cornerRadius: 30)
                    .stroke(style: StrokeStyle(lineWidth: 1, dash: [6]))
                    .foregroundColor(Color(hex: "#697383"))
                    .frame(height: 70)
            )
            
            Text("(PDF, JPG, PNG, DICOM supported)")
                .font(.custom("Urbanist-Regular", size: 12))
                .foregroundColor(Color(hex: "#697383"))
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.top, 4)
        }
    }
    
    var attachedFilesSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Attached Files")
                .font(.custom("Urbanist-SemiBold", size: 16))
                .foregroundColor(Color(hex: "#4338CA"))
                .padding(.leading, 10)
            
            ForEach(viewModel.uploadedFiles) { file in
                HStack {

                    
                    if let urlString = file.fileURL,
                       let url = URL(string: urlString),
                       file.name.lowercased().contains(".jpeg") ||
                       file.name.lowercased().contains(".jpg") ||
                       file.name.lowercased().contains(".png") {

                        AsyncImage(url: url) { image in
                            image
                                .resizable()
                                .scaledToFill()

                        } placeholder: {
                            ProgressView()
                        }
                        .frame(width: 40, height: 50)
                        .clipped()
                        .cornerRadius(8)

                    } else {

                        Image(viewModel.iconName(for: file.name))
                            .resizable()
                            .scaledToFit()
                            .frame(width: 35, height: 45)
                            .padding(.leading, -7)
                    }
                    Text(file.name)
                        .font(.custom("Urbanist-Regular", size: 16))
                        .foregroundColor(Color(hex: "#4338CA"))
                    
                    Spacer()
                    Button {
                        viewModel.removeFile(file)
                    } label: {
                        Image("FileDeleteIcon")
                            .frame(width: 35, height: 45)
                    }
                    .padding(.trailing, -7)
                }
                .frame(height: 30)
                
                .padding()
                
                .background(Color(hex: "#4338CA").opacity(0.20))
                .cornerRadius(25)
            }
            .padding(.horizontal, 10)
           
        }
        .frame(maxWidth: .infinity)
        //.frame(height: 50)
        .padding()
        //.background(Color(.systemGray6))
        .background(Color(hex: "#4338CA").opacity(0.03))
        .cornerRadius(28)
        .clipShape(RoundedRectangle(cornerRadius: 28))
        // .padding(.top, 20)
        .overlay(
            RoundedRectangle(cornerRadius: 28).stroke(Color(hex: "#4338CA").opacity(0.10), lineWidth: 1)
        )
        .padding(.top, 20)
    }
    
    var infoSection: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text("You're almost ready!")
                .font(.custom("Urbanist-Medium", size: 18))
                .foregroundColor(Color(hex: "#4338CA"))
            Text("You can always add more details, upload documents, or update your profile later from the settings menu.")
                .font(.custom("Urbanist-Medium", size: 14))
                .foregroundColor(Color(hex: "#697383"))
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding()
    }
    
    private var buttonTitle: String {
        switch flow {
        case .editProfile, .editFamilyMember:
            return "Update"
        default:
            return "Get Started"
        }
    }
    
    var getStartedButton: some View {
        
        Button(action: {
            
            
            // ✅ Validation
              if viewModel.uploadedFiles.isEmpty {
                  viewModel.errorMessage = "Please select at least one file."
                  viewModel.isPresentAlert = true
                  return
              }

            
            print("\n🔘 Button Clicked")
            print("👉 Flow Type: \(flow)")
            
            switch flow {
                
            case .profileSetup:
                print("Action: Setup General Profile शुरू")
                viewModel.completeDocumentsProfileAPI { success in
                    if success {
                        showPopup = true
                        print("Saved")
                    }
                    
                }
                
            case .addFamilyMember:
                print(" Action: Add Family Member Triggered")
                // Future API
                viewModel.addfamilymembermedicaldocumentsAPI { success in
                    if success {
                        showPopup = true
                        print("Saved")
                    }
                }
                
            case .editProfile:
                print("Action: Edit General Profile")
                
                viewModel.UpdateProfileDocumentAPI{ success in
                    if success {
                        showPopup = true
                        print("Saved")
                    }
                    
                }
                
            case .editFamilyMember:
                print("Action: Edit Family Member Triggered")
                
                viewModel.updateFamilyDocumentAPI{ success in
                    if success {
                        showPopup = true
                        print("Saved")
                    }
                }
            }
            
        })   {
            Text(buttonTitle)
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
    }
}


struct DocumentPicker: UIViewControllerRepresentable {
    
    var onPick: (URL) -> Void
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    func makeUIViewController(context: Context) -> UIDocumentPickerViewController {
        
        let picker = UIDocumentPickerViewController(
            forOpeningContentTypes: [
                .pdf,
                .image,
                UTType(filenameExtension: "doc")!,
                UTType(filenameExtension: "docx")!,
                UTType(filenameExtension: "dcm")!   // DICOM
            ],
            asCopy: true
        )
        picker.delegate = context.coordinator
        return picker
    }
    
    func updateUIViewController(_ uiViewController: UIDocumentPickerViewController, context: Context) {}
    
    class Coordinator: NSObject, UIDocumentPickerDelegate {
        
        let parent: DocumentPicker
        
        init(_ parent: DocumentPicker) {
            self.parent = parent
        }
        
        func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
            
            guard let url = urls.first else { return }
            parent.onPick(url)
        }
    }
}

private extension CompleteDocProfileView {
    var popupOverlay: some View {
        ZStack {
            // Dark Background
            Color.black.opacity(0.4)
                .ignoresSafeArea()
                .onTapGesture {
                    withAnimation {
                        showPopup = false
                    }
                }
            
            if flow == .editProfile {
                SuccessPopupView(
                    title: "Profile Updated Successfully",
                    message: "Your profile has been updated.",
                    onClose: {
                        withAnimation(.easeOut(duration: 0.25)) {
                            showPopup = false
                        }
                        
                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
                            coordinator.path = [.tabBarView, .personProfileView]
                        }
                    }
                )
                .transition(.scale.combined(with: .opacity))
            }else if flow == .profileSetup{
                // Centered opup
                ProfileSetupCompletePopUpView(
                    title: "Profile Setup Completed!",
                    message: "You can now add your family members or start asking AI for help.",
                    onClose: {
                        withAnimation {
                            showPopup = false
                            coordinator.path = [.tabBarView]
                        }
                    }, onAddMember: {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
                            coordinator.path = [.tabBarView, .completeProfileView(flow: .addFamilyMember)]
                        }
                    }
                )
                .frame(maxWidth: .infinity)
                .transition(.scale(scale: 0.9).combined(with: .opacity))
                
            }else if flow == .addFamilyMember{
                MemberAddedSuccPopUpView(
                    title: "Member Added Successfully",
                    message: "Your family member has been added.", message2: "Would you like to add another member?",
                    onClose: {
                        withAnimation {
                            showPopup = false
                            coordinator.path = [.tabBarView]
                        }
                    }, onAddMember: {
                        withAnimation {
                            coordinator.path = [.tabBarView, .completeProfileView(flow: .addFamilyMember)]
                        }
                    }
                )
                .frame(maxWidth: .infinity)
                .transition(.scale(scale: 0.9).combined(with: .opacity))
                
            } else if flow == .editFamilyMember{
                SuccessPopupView(
                    title: "Member’s Profile Updated Successfully",
                    message: "Member’s Profile has been updated.",
                    onClose: {
                        withAnimation(.easeOut(duration: 0.25)) {
                            showPopup = false
                        }
                        
                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
                            coordinator.path = [.tabBarView, .familyPersonDetailView]
                        }
                    }
                )
                .transition(.scale.combined(with: .opacity))
            }
        }
    }
}

struct FileSourceBottomSheet: View {
    
    var onCamera: () -> Void
    var onGallery: () -> Void
    var onDocument: () -> Void
    
    var body: some View {
        VStack(spacing: 20) {
            
            Capsule()
                .fill(Color.gray.opacity(0.4))
                .frame(width: 40, height: 5)
                .padding(.top, 10)
            
            Text("Select File")
                .font(.headline)
            
            Button {
                onCamera()
            } label: {
                HStack {
                    Image(systemName: "camera")
                    Text("Camera")
                    Spacer()
                }
                .padding()
            }
            
            Button {
                onGallery()
            } label: {
                HStack {
                    Image(systemName: "photo")
                    Text("Gallery")
                    Spacer()
                }
                .padding()
            }
            
            Button {
                onDocument()
            } label: {
                HStack {
                    Image(systemName: "doc")
                    Text("Documents (PDF / Word)")
                    Spacer()
                }
                .padding()
            }
            
            Spacer()
        }
        .padding()
        .presentationDetents([.height(280)])
    }
}
// MARK: - Preview
#Preview {
    CompleteDocProfileView(flow: .profileSetup)
}
