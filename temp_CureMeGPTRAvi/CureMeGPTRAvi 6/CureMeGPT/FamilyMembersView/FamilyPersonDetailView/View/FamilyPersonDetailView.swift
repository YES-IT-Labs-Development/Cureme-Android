//
//  FamilyPersonDetailView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 14/01/26.
//

import SwiftUI
import SDWebImageSwiftUI

struct FamilyPersonDetailView: View {
    @EnvironmentObject private var coordinator: Coordinator
    @StateObject private var viewModel = FamilyPersonDetailViewModel()
    @State private var showPopup: Bool = false
    @State private var popupAction: (() -> Void)? = nil
    
    @State private var showPhotoSheet = false
    @State private var showImagePicker = false
    @State private var activePicker: PickerType?
    @State private var selectedImage: UIImage?
    @State private var showAlert = false
    @State private var alertMessage = ""
    
    var body: some View {
        ZStack{
            VStack(spacing: 0) {
                headerSection
                Spacer()
                
                ScrollView{
                    PersonalInformationsSection(items: viewModel.personalInfoItems)
                    
                    PersonGeneralHealthSection(items: viewModel.healthItems)
                    
                    PersonMedicalHistorySections(items: viewModel.medicalItems)
                    .padding(.top)
                    
                    PersonAttachedFileSection(doc: viewModel.uploadedPersonFiles)
                    
                   // AttachedFilesSection(doc: viewModel.uploadedFiles)
                    Spacer(minLength: 30)
                }
                .disableScrollBounce()
                .padding(.top, 70)
                
                .onAppear {
                    viewModel.get_family_member_profileAPI { success in
                        if success {
                            print("Family Member Profile Loaded")
                        }
                    }
                }

               
            }
            profileImageSection
                //.padding(.top, 90)
           // Spacer()
                //.padding(.bottom, 100)
            
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
                MemberDeletedSuccPopUpView(
                            title: "Delete Member?",
                            message: "Are you sure you want to delete \(viewModel.memberProfileData?.fullName ?? "") profile? This action cannot be undone.",
                            onClose: { showPopup = false },
                            cancel: { showPopup = false },

                            deleteMember: {
                                guard let id = viewModel.memberProfileData?.id  else { return }
                                viewModel.deleteFamilyMemberAPI(memberId: id) { success in
                                    if success {
                                        print("Deleted \(viewModel.memberProfileData?.fullName ?? "")")
                                        coordinator.push(.tabBarView)
                                    }
                                }
                            }
                        )
                        .transition(.scale.combined(with: .opacity))
                        .zIndex(10)
                }
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
                    viewModel.deletefamilyProfilePhoto(memberId: UserDetail.shared.getID()) { success in
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
        
        .onChange(of: selectedImage) { newImage in
            guard let image = newImage,
                  let id = viewModel.memberProfileData?.id else { return }
            
            viewModel.updateMemberProfilePhotoAPI(memberID: id, image: image) { success in
                if success {
                    print("✅ Profile photo updated")
                    
                    // OPTIONAL: refresh profile
                    viewModel.get_family_member_profileAPI { _ in }
                }
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

extension FamilyPersonDetailView {
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

extension FamilyPersonDetailView {
    private var topBar: some View {
        HStack {
            // Back Button
            Button(action: {
                coordinator.pop()
            }){
                Image("BackB")
                    .foregroundColor(.white)
                    .frame(width: 45, height: 45)
                    //.background(Color.white.opacity(0.15))
                    //.border(Color.white, width: 1)
                    .clipShape(Circle())
            }
            
           // Text("Rose Logan")//viewModel.profile.name)
            Text(viewModel.memberProfileData?.fullName ?? "Loading...")
                .font(.custom("Urbanist-Medium", size: 20))
                .foregroundColor(.white)
            Spacer()
            // Edit Button
            Button(action: {
                coordinator.push(.completeProfileView(flow: .editFamilyMember))
            }) {
                Image("EditButton")
                    .foregroundColor(.white)
                    .frame(width: 45, height: 45)
                    .clipShape(Circle())
            }
            
            // Delete Button
            Button(action: {
                showPopup = true
            }) {
                Image("DeleteIcon")
                    .foregroundColor(.white)
                    .frame(width: 45, height: 45)
                    .clipShape(Circle())
            }
        }
    }
}

extension FamilyPersonDetailView {
    var profileImageSection: some View {
        ZStack {
            Group {
//                if let image = selectedImage {
//                    Image(uiImage: image)
//                        .resizable()
//                        .scaledToFill()
//                } else {
//                    Image.loadImage(viewModel.memberProfileData?.profileImage?.imgFullPath())
//                        .scaledToFill()
//                }
                
//                if let image = selectedImage {
//                    Image(uiImage: image)
//                        .resizable()
//                        .scaledToFill()
//                } else if let imagePath = viewModel.memberProfileData?.profileImage,
//                          !imagePath.isEmpty {
//                   // Image.loadImage(imagePath.imgFullPath())
//                       // .scaledToFill()
//                    
//                    WebImage(url: URL(string: imagePath))
//                        { image in
//                            image
//                                .resizable()
//                                .scaledToFill()
//                        } placeholder: {
//                            Image("Frame 1272638625")
//                                .resizable()
//                                .scaledToFill()
//                        }
//                        .frame(width: 136, height: 136)
//                        .clipShape(Circle())
//                } else {
//                    Circle()
//                        .fill(Color.gray.opacity(0.75))
//                        .overlay(
//                            Image(systemName: "person.fill")
//                                .foregroundColor(.gray)
//                        )
//                }
                if let image = selectedImage {
                    Image(uiImage: image)
                        .resizable()
                        .scaledToFill()
                        .frame(width: 136, height: 136)
                        .clipShape(Circle())
                }
                else {

                    let img = viewModel.memberProfileData?.profileImage?.imgFullPath()

                    if URL(string: img ?? "" ) != nil {
//                          WebImage(url: url)
//                              .resizable()
//                              .indicator(.activity)
//                              .scaledToFill()
//                              .frame(width: 136, height: 136)
//                              .clipShape(Circle())
                        
                        WebImage(url: URL(string: img ?? ""))
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
        .offset(y: -190)
    }

}

struct PersonalInformationsSection: View {
    let items: [FamilyPersonInfoItem]

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Personal Information")
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
            .background(
                RoundedRectangle(cornerRadius: 30)
                    .fill(Color(hex: "#F4F4F4"))
            )
            .overlay(
                RoundedRectangle(cornerRadius: 30)
                    .stroke(Color(hex: "#996BFE").opacity(0.3), lineWidth: 1)
            )
        }
        .padding(.horizontal)

    }
}

struct PersonGeneralHealthSection: View {
    let items: [PersonHealthInfoItem]

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
            .background(
                RoundedRectangle(cornerRadius: 30)
                    .fill(Color(hex: "#F4F4F4"))
            )
            .overlay(
                RoundedRectangle(cornerRadius: 30)
                    .stroke(Color(hex: "#996BFE").opacity(0.3), lineWidth: 1)
            )
            
        }
        .padding(.horizontal)
    }
}

struct PersonMedicalHistorySections: View {
    let items: [PersonMedicalInfoItem]

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
            .background(
                RoundedRectangle(cornerRadius: 30)
                    .fill(Color(hex: "#F4F4F4"))
            )
            .overlay(
                RoundedRectangle(cornerRadius: 30)
                    .stroke(Color(hex: "#996BFE").opacity(0.3), lineWidth: 1)
            )
            
        }
        .padding(.horizontal)
    }
}

struct PersonAttachedFileSection: View {
    let doc: [PersonDocumentsItem]
    
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
                        .font(.subheadline)
                    Spacer()
                    Button {
                        
                        let path = item.filePath.imgFullPath()
                        downloadAndSaveImage(from: path)
                        
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
    FamilyPersonDetailView()
}
