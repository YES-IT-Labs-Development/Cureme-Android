//
//  PersonProfileView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 06/01/26.
//

import SwiftUI
import SDWebImageSwiftUI
import Photos

enum PickerType: Identifiable {
    case camera
    case gallery

    var id: Int {
        hashValue
    }

    var sourceType: UIImagePickerController.SourceType {
        switch self {
        case .camera:
            return .camera
        case .gallery:
            return .photoLibrary
        }
    }
}

struct PersonProfileView: View {
    
    @EnvironmentObject private var coordinator: Coordinator
    @StateObject private var viewModel = PersonProfileViewModel()
    @State private var showPhotoSheet = false
    @State private var activePicker: PickerType?
    @State private var selectedImage: UIImage?
    @State private var showAlert = false
    @State private var alertMessage = ""
    
    var body: some View {
        ZStack(alignment: .top) {
            VStack(spacing: 0) {
                headerSection
                Spacer()
                
                ScrollView{
                    
                    PersonalInformationSection(viewModel: viewModel)
                    
                    GeneralHealthSection(items: viewModel.healthItems)
                    
                    MedicalHistorySection(items: viewModel.medicalItems)
                        .padding(.top)
                    
                    AttachedFilesSection(doc: viewModel.uploadedFiles)
                    Spacer(minLength: 30)
                }
                .padding(.top, 70)
                .disableScrollBounce()
            }
            .onAppear {
                viewModel.getMyProfile { success in
                    if success {
                        print("User Profile Data Loaded")
                    }
                }
                }
            profileImageSection
            
            if viewModel.showActivity {
                CustomLoderView(isVisible: $viewModel.showActivity)
                    .ignoresSafeArea()
            }
            // ✅🔥 ADD THIS (TOAST)
              if viewModel.showToast {
                  VStack {
                      Spacer()
                      
                      ToastView(message: viewModel.toastMessage)
                          .padding(.bottom, 40)
                          .transition(.move(edge: .bottom).combined(with: .opacity))
                  }
                  .zIndex(999) // 🔥 VERY IMPORTANT
                  .animation(.easeInOut, value: viewModel.showToast)
              }
        }
        .ignoresSafeArea(edges: .top)
        
        
        .customAlert(
                  isPresented: $viewModel.isPresentAlert,
                  message: viewModel.errorMessage ?? "Error"
              ) {
                  print("OK tapped")
              }
//        .alert(isPresented: $viewModel.isPresentAlert) {
//                    Alert(title: Text(viewModel.errorMessage ?? ""))
//                }
        
        .overlay(
            ProfilePhotoSheet(
                isPresented: $showPhotoSheet,
                onCameraTap: {
                    guard UIImagePickerController.isSourceTypeAvailable(.camera) else {
                        alertMessage = "Camera not available"
                        showAlert = true
                        return
                    }
                    showPhotoSheet = false
                    activePicker = .camera
                    
                },
                onGalleryTap: {
                    guard UIImagePickerController.isSourceTypeAvailable(.photoLibrary) else {
                        alertMessage = "Gallery not available"
                        showAlert = true
                        return
                    }
                    showPhotoSheet = false
                    activePicker = .gallery
                },
                onDeleteTap: {
                    viewModel.deleteProfilePhoto { success in
                        if success {
                            selectedImage = nil
                        }
                    }
                }
            )
        )
        .sheet(item: $activePicker) { picker in
            ImagePicker1(
                sourceType: picker.sourceType,
                selectedImage: $selectedImage
            )
        }
        .alert("Camera Unavailable", isPresented: $showAlert) {
            Button("OK", role: .cancel) {}
        } message: {
            Text(alertMessage)
        }
    }
    
    struct UploadImageView: View {
        @State private var activePicker: PickerType?
        
        @State private var selectedImage: UIImage?
        
       
        @State private var showAlert = false
        @State private var alertMessage = ""

        var body: some View {
            VStack {
                Button("Camera") {
                    if UIImagePickerController.isSourceTypeAvailable(.camera) {
                        activePicker = .camera
                    } else {
                        alertMessage = "Camera not available"
                        showAlert = true
                    }
                }

                Button("Gallery") {
                    if UIImagePickerController.isSourceTypeAvailable(.photoLibrary) {
                        activePicker = .gallery
                    } else {
                        alertMessage = "Gallery not available"
                        showAlert = true
                    }
                }
            }
            .sheet(item: $activePicker) { picker in
                ImagePicker1(
                    sourceType: picker.sourceType,
                    selectedImage: $selectedImage
                )
            }
            .alert(alertMessage, isPresented: $showAlert) {
                Button("OK", role: .cancel) { }
            }
        }
    }
    
        struct ProfilePhotoSheet: View {
            @Binding var isPresented: Bool
            
            let onCameraTap: () -> Void
            let onGalleryTap: () -> Void
            let onDeleteTap: () -> Void

            var body: some View {
                if isPresented {
                    ZStack {
                        // Background dim
                        Color.black.opacity(0.4)
                            .ignoresSafeArea()
                            .onTapGesture {
                                isPresented = false
                            }

                        VStack {
                            Spacer()

                            VStack(spacing: 0) {
                                Capsule()
                                    .fill(Color(hex: "#D9D9D9"))
                                    .frame(width: 80, height: 5)
                                    .padding(.top, 20)
                                // Header
                                HStack {
                                    Button {
                                        isPresented = false
                                    } label: {
                                        Image("iconoir_cancel")
                                            .resizable()
                                            .frame(width: 32, height: 32)
                                            .padding(.leading)
                                    }

                                    Spacer()

                                    Text("Profile Photo")
                                        .font(.custom("Urbanist-Medium", size: 20))
                                        .foregroundColor(Color(hex: "#374151"))

                                    Spacer()

                                    Image("Delete")
                                        .resizable()
                                        .frame(width: 26, height: 26)
                                        .padding(.trailing)
                                        .onTapGesture {
                                            onDeleteTap()
                                            isPresented = false
                                        }
                                }
                                .padding()
                                
                                Divider()

                                // Camera
                                sheetRow(
                                    icon: "CameraIcon",
                                    title: "Camera",
                                    action: {
                                        onCameraTap()
                                        isPresented = false
                                    }
                                )
                                .padding(.leading, 10)
                                
                                Divider()

                                // Gallery
                                sheetRow(
                                    icon: "GalleryIcon",
                                    title: "Gallery",
                                    action: {
                                        onGalleryTap()
                                        isPresented = false
                                    }
                                )
                                .padding(.leading, 10)
                            }
                            .padding(.bottom, 30)
                            .background(Color.white)
                            .cornerRadius(22)
                            .padding(.bottom, -34)
                        }
                    }
                    .transition(.move(edge: .bottom))
                    .animation(.easeInOut, value: isPresented)
                }
            }

            private func sheetRow(icon: String, title: String, action: @escaping () -> Void) -> some View {
                Button(action: action) {
                    HStack(spacing: 16) {
                        Image(icon)
                            .resizable()
                            .frame(width: 50, height: 50)

                        Text(title)
                            .font(.custom("Urbanist-Regular", size: 14))
                            .foregroundColor(.black)

                        Spacer()
                    }
                    .padding()
                }
            }
        }
    }

extension PersonProfileView {
        var headerSection: some View {
            LinearGradient(
                colors: [
                    Color(red: 67/255, green: 56/255, blue: 202/255),
                    Color(red: 33/255, green: 28/255, blue: 100/255)
                ],
                startPoint: .leading,
                endPoint: .trailing
            )
            .frame(height: 240)
            .clipShape(
                RoundedCorner(radius: 60, corners: [.bottomLeft, .bottomRight])
            )
            .overlay(
                topBar
                    .padding(.top, 50)
                    .padding(.horizontal),
                alignment: .top
            )
        }
    }

extension PersonProfileView {
    private var topBar: some View {
        HStack {
            // Back Button
            Button {
                coordinator.pop()
            } label: {
                Image("BackB")
                    .resizable()
                    .frame(width: 45, height: 45)
            }
            
            Text("My Profile")//viewModel.profile.name)
                .font(.custom("Urbanist-Medium", size: 20))
                .foregroundColor(.white)
            
            Spacer()
            
            // Edit Button
            Button(action: {
                coordinator.push(.completeProfileView(flow: .editProfile))
            }) {
                Image("EditButton")
                    .foregroundColor(.white)
                    .frame(width: 45, height: 45)
                    .clipShape(Circle())
            }
            
            // Delete Button
            Button {
                viewModel.onSettingTap()
                coordinator.push(.settingView)
            } label: {
                Image("SettingB")
                    .frame(width: 45, height: 45)
                    .foregroundColor(.white)
                    .frame(width: 45, height: 45)
                    .clipShape(Circle())
            }
        }
    }
}

extension PersonProfileView {
    var profileImageSection: some View {
        ZStack {
            Group {
//                if let image = selectedImage {
//                    Image(uiImage: image)
//                        .resizable()
//                        .scaledToFill()
//                }
                if let image = selectedImage {
                    Image(uiImage: image)
                        .resizable()
                        .scaledToFill()
                        .frame(width: 136, height: 136)
                        .clipShape(Circle())
                } 
                else {

                    let img = UserDetail.shared.getProfileImg()

                    if let url = URL(string: img ) {

                        WebImage(url: URL(string: img))
                            { image in
                                image
                                    .resizable()
                                    .scaledToFill()
                            } placeholder: {
                                Image("Frame 1272638625")
                                    .resizable()
                                    .scaledToFill()
                            }
                            .frame(width: 136, height: 136)
                            .clipShape(Circle())
                      } else {
                          Image("Frame 1272638625") // Static placeholder image
                              .resizable()
                              .scaledToFill()
                              .frame(width: 136, height: 136)
                              .clipShape(Circle())
                      }
                }
            }
            .frame(width: 136, height: 136)
            .clipShape(Circle())
            .overlay(
                Circle()
                    .stroke(Color.white, lineWidth: 10)
            )
            .shadow(radius: 8)
            VStack {
                Spacer()
                HStack {
                    Spacer()
                    Button {
                        showPhotoSheet = true
                    } label: {
                        Image("UploadImg")
                            .frame(width: 40, height: 40)
                    }
                }
            }
            .frame(width: 134, height: 138)
        }
        .offset(y: 160)
        
//        .onChange(of: selectedImage) { newImage in
//            if let image = newImage {
//                viewModel.updateProfilePicAPI(image: image) { success in
//                    
//                    if success {
//                        // Reset local image → server image show hoga
//                        //selectedImage = nil
//                    }
//                }
//            }
        
        .onChange(of: selectedImage) { image in
            print("Selected Image Changed:", image != nil)

            if let image {
                viewModel.updateProfilePicAPI(image: image) { success in
                    print("Upload Success:", success)
                }
            }
        
        }
        }

    }

extension PersonProfileView {
    struct PersonalInformationSection: View {
        @ObservedObject var viewModel: PersonProfileViewModel

        private let columns = [
            GridItem(.flexible(), spacing: 16),
            GridItem(.flexible(), spacing: 16)
        ]

        var body: some View {
            VStack(alignment: .leading, spacing: 16) {
                Text("Personal Information")
                     .font(.custom("Urbanist-Medium", size: 18))

                LazyVGrid(columns: columns, spacing: 16) {
                    ForEach(viewModel.items.prefix(6)) { item in
                        PersonalInfoCardView(item: item)
                    }
                }
                
                VStack(spacing: 20) {
                    HStack {
                        Image("Frame 7")
                            .frame(width: 55, height: 55)
                        Spacer()
                    }

                    HStack {
                        Spacer()
                       // Text("james@gmail.com")
                        Text(viewModel.profile?.email ?? "")
                            .font(.custom("Urbanist-Medium", size: 18))
                            .foregroundColor(Color(hex: "#000000"))
                    }
                }
                .padding()
                .frame(maxWidth: .infinity)
                .frame(height: 135)
                .background(
                    RoundedRectangle(cornerRadius: 30)
                        .fill(Color(hex: "#4338CA").opacity(0.010))
                )
                .overlay(
                    RoundedRectangle(cornerRadius: 30)
                        .stroke(Color(hex: "#4338CA").opacity(0.4), lineWidth: 0.6)
                )
                .background(Color(hex: "#F9F9FD"))
                //.cornerRadius(36)
            }
            .padding()
        }
    }
}

struct PersonalInfoCardView: View {
    let item: PersonalInfoItem

    var body: some View {
        VStack(spacing: 20){
            HStack{
                if item.isHighlighted {
                    ZStack {
                        Circle()
                            .fill(Color.white.opacity(0.15))
                            .frame(width: 55, height: 55)
                        
                        Image(systemName: "person")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 22, height: 22)
                            .foregroundColor(.white)
                    }
                } else {
                    Image(item.icon)
                        .renderingMode(.original)
                        .frame(width: 55, height: 55)
                }

                Spacer()
            }
            
            HStack{
                Spacer()
                Text(item.value)
                    .font(.custom("Urbanist-Medium", size: 18))
                    .foregroundColor(item.isHighlighted ? .white : .black)
                
            }
        }
        .padding()
        .frame(maxWidth: .infinity)
        .frame(height: 135)
        .background(
            Group {
                if item.isHighlighted {
                    LinearGradient(
                        colors: [
                            Color(red: 67/255, green: 56/255, blue: 202/255),
                            Color(red: 33/255, green: 28/255, blue: 100/255)
                        ],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                } else {
                    LinearGradient(
                        colors: [Color(hex: "#4338CA").opacity(0.04)],
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                }
            }
            .cornerRadius(30)
        )
        .overlay(
            RoundedRectangle(cornerRadius: 30)
                .stroke(
                    item.isHighlighted ? Color.clear : Color(hex: "#996BFE").opacity(0.3),
                    lineWidth: 1
                )
        )
    }
}

struct GeneralHealthSection: View {
    let items: [HealthInfoItem]

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {

            Text("General Health")
                .font(.custom("Urbanist-SemiBold", size: 18))
                .foregroundColor(.black)

            VStack(spacing: 14) {
                ForEach(items) { item in
                    HStack {
                        Text(item.title)
                            .font(.custom("Urbanist-Medium", size: 15))
                            .foregroundColor(Color(hex: "#4338CA"))
                            .frame(maxWidth: .infinity, alignment: .leading)
                        Spacer()

                        Text(item.value.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? "-" : item.value)
                            .font(.custom("Urbanist-Medium", size: 15))
                            .foregroundColor(Color(hex: "#3C3C3C"))
                            .frame(maxWidth: .infinity, alignment: .leading)
                       
                    }
                }
            }
            .padding()
            .background(Color(hex: "#F9F9FD"))
            .overlay(
                RoundedRectangle(cornerRadius: 18)
                    .stroke(Color(hex: "#996BFE").opacity(0.3), lineWidth: 1)
            )
            
        }
        .padding(.horizontal)
    }
}

struct MedicalHistorySection: View {
    let items: [MedicalInfoItem]

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {

            Text("General Health")
                .font(.custom("Urbanist-SemiBold", size: 18))
                .foregroundColor(.black)

            VStack(spacing: 14) {
                ForEach(items) { item in
                    HStack {
                        Text(item.title)
                            .font(.custom("Urbanist-Medium", size: 15))
                            .foregroundColor(Color(hex: "#4338CA"))
                            .frame(maxWidth: .infinity, alignment: .leading)
                        Spacer()

                        Text(item.value)
                            .font(.custom("Urbanist-Medium", size: 15))
                            .foregroundColor(Color(hex: "#3C3C3C"))
                            .frame(maxWidth: .infinity, alignment: .leading)
                       
                    }
                }
            }
            .padding()
            .background(Color(hex: "#F9F9FD"))
            .overlay(
                RoundedRectangle(cornerRadius: 18)
                    .stroke(Color(hex: "#4338CA").opacity(0.3), lineWidth: 1)
            )
            
        }
        .padding(.horizontal)
    }
}

struct AttachedFilesSection: View {
    let doc: [DocumentsItem]
    
    @State private var showAlert = false
    @State private var alertMessage = ""
    
    var body: some View {
        
        VStack(alignment: .leading, spacing: 12) {
            Text("Documents")
                .font(.custom("Urbanist-SemiBold", size: 18))
                .foregroundColor(.black)
            
            ForEach(doc) { item in
                HStack {
                    Image("FileIcon")
                        .frame(width: 40, height: 50)
                    Text(item.name)
                        .font(.custom("Urbanist-Regular", size: 16))
                        .foregroundColor(Color(hex: "#4338CA"))
                    Spacer()
                    Button {
                        let path = item.path.imgFullPath()
                        downloadAndSaveImage(
                               from: path)
                    } label: {
                        Image("DownloadDocIcon")
                            .frame(width: 40, height: 50)
                    }
                }
                .padding()
                .background(Color(hex: "#4338CA").opacity(0.20))
                .cornerRadius(28)
            }
        }
        .padding(.top, 10)
        .padding(.horizontal)
        
        .alert("Message", isPresented: $showAlert) {
             Button("OK", role: .cancel) { }
         } message: {
             Text(alertMessage)
         }
    }
 
    func downloadAndSaveImage(from urlString: String) {
        
        guard let url = URL(string: urlString) else { return }
        let ext = url.pathExtension.lowercased()
        let isImage = ext == "jpg" || ext == "jpeg" || ext == "png"
        
        if isImage {
            URLSession.shared.dataTask(with: url) { data, _, error in
                guard let data = data,
                      let image = UIImage(data: data) else {
                    print("Failed to load image, falling back to share sheet")
                    downloadAndShareFile(from: url)
                    return
                }
                DispatchQueue.main.async {
                    let saver = ImageSaver()
                    saver.completion = { success, message in
                        alertMessage = message
                        showAlert = true
                    }
                    saver.writeToPhotoAlbum(image: image)
                }
            }.resume()
        } else {
            downloadAndShareFile(from: url)
        }
    }
    
    private func downloadAndShareFile(from url: URL) {
        URLSession.shared.downloadTask(with: url) { tempURL, response, error in
            guard let tempURL = tempURL, error == nil else {
                print("Download failed:", error?.localizedDescription ?? "")
                DispatchQueue.main.async {
                    alertMessage = "Failed to download document"
                    showAlert = true
                }
                return
            }

            do {
                let documentsDirectory = FileManager.default.urls(
                    for: .documentDirectory,
                    in: .userDomainMask
                ).first!

                let fileName = url.lastPathComponent
                let destinationURL = documentsDirectory.appendingPathComponent(fileName)

                if FileManager.default.fileExists(atPath: destinationURL.path) {
                    try FileManager.default.removeItem(at: destinationURL)
                }

                try FileManager.default.moveItem(at: tempURL, to: destinationURL)

                DispatchQueue.main.async {
                    print("File downloaded to:", destinationURL)
                    let activityVC = UIActivityViewController(
                        activityItems: [destinationURL],
                        applicationActivities: nil
                    )
                    UIApplication.shared.windows.first?.rootViewController?
                        .present(activityVC, animated: true)
                }
            } catch {
                print("File save error:", error.localizedDescription)
                DispatchQueue.main.async {
                    alertMessage = "Failed to save document"
                    showAlert = true
                }
            }
        }.resume()
    }
}
#Preview {
    PersonProfileView()
}

