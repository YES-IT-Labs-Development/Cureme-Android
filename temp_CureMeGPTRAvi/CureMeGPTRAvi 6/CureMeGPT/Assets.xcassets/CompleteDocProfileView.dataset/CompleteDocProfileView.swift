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
    
    var body: some View {
        ZStack {
            VStack(spacing: 24) {
                ScrollView {
                    headerSection
                    uploadBox
                    attachedFilesSection
                    infoSection
                    Spacer()
                }
                getStartedButton
            }
            .padding()
            .sheet(isPresented: $showPicker) {
                DocumentPicker()
            }
            
            // ✅ POPUP LAYER
            if showPopup {
                popupOverlay
            }
        }
        .animation(.easeInOut, value: showPopup)
    }
}

// MARK: - UI Sections
private extension CompleteDocProfileView {
    var headerSection: some View {
        VStack(spacing: 22) {
            // MARK: HEADER
            HStack {
                Button(action: {
                    //coordinator.pop()
                }){
                    Image("backIcon")
                        .resizable()
                        .frame(width: 45, height: 45)
                    //.padding(.leading, 10)
                }
                // Spacer()
                
                Text("Complete Your Profile")
                    .font(.custom("Urbanist-Medium", size: 20))
                    .foregroundColor(.black)
                
                Spacer()
            }
            //.padding(.horizontal)
            .padding(.top, 10)
            
            HStack(spacing: 40) {
                StepTab(icon: "RightCheckMark", title: "Personal", selected: true)
                StepTab(icon: "RightCheckMark", title: "General", selected: true)
                StepTab(icon: "RightCheckMark", title: "History", selected: true)
                StepTab(icon: "DocumentsSelected", title: "Documents", selected: true)
            }
            .padding(.top, 10)
            .padding(.bottom, 40)
            
        }
    }
    
    var uploadBox: some View {
        VStack{
            HStack {
                Text("Upload Files ")
                    .font(.custom("Urbanist-Medium", size: 12))
                    .foregroundColor(.black)
                //.frame(maxWidth: .infinity, alignment: .leading)
                
                Text("(X-Rays, Dental Scans, Prescriptions, Lab Reports)")
                    .font(.custom("Urbanist-Medium", size: 12))
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
                
                Text("No File Chosen")
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
                .foregroundColor(.black)
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
                    Image("FileIcon")
                        .frame(width: 40, height: 50)
                    Text(file.name)
                        .font(.subheadline)
                    Spacer()
                    Button {
                        viewModel.removeFile(file)
                    } label: {
                        Image("FileDeleteIcon")
                            .frame(width: 40, height: 50)
                    }
                }
                
                .padding()
                .background(Color(hex: "#4338CA").opacity(0.10))
                .cornerRadius(28)
            }
            .padding(.horizontal, 10)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(28)
        .clipShape(RoundedRectangle(cornerRadius: 28))
       // .padding(.top, 20)
        .overlay(
            RoundedRectangle(cornerRadius: 28).stroke(Color(hex: "#4338CA").opacity(0.10), lineWidth: 2)
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
    
    var getStartedButton: some View {
        Button(action: {
            //coordinator.push(.tabBarView)
            self.showPopup = true
            print("Saved")
        }){
            Text("Get Started")
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
        //.padding(.horizontal)
        .padding(.vertical)
    }
}

// MARK: - Document Picker
struct DocumentPicker: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIDocumentPickerViewController {
        UIDocumentPickerViewController(forOpeningContentTypes: [UTType.pdf, UTType.image])
    }
    
    func updateUIViewController(_ uiViewController: UIDocumentPickerViewController, context: Context) {}
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

            // Centered Popup
            ProfileSetupCompletePopUpView(
                title: "Profile Setup Completed!",
                message: "You can now add your family members or start asking AI for help.",
                onClose: {
                    withAnimation {
                        showPopup = false
                        coordinator.push(.tabBarView)
                    }
                }, onAddMember: {
                    withAnimation {
                        coordinator.push(.generalProfileView)
                    }
                }
            )
            .frame(maxWidth: .infinity)
            .transition(.scale(scale: 0.9).combined(with: .opacity))
        }
    }
}


// MARK: - Preview
#Preview {
    CompleteDocProfileView()
}
