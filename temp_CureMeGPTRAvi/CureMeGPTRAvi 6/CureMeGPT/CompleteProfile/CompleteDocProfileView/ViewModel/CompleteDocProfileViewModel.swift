//
//  CompleteDocProfileViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/12/25.


import SwiftUI
import Combine

final class UploadDocumentsViewModel: ObservableObject {

    @Published var showActivity = false
    private var cancellables = Set<AnyCancellable>()
    
    @Published var errorMessage: String?
    @Published var isPresentAlert = false
    
    @Published var uploadedFiles: [UploadedFile] = []
    
    @Published var existingDocumentPaths: [String] = []
    
    var familyMemberID: String? = UserDetail.shared.getID()

    // MARK: - API CALL
    func completeDocumentsProfileAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true

        var documents: [Data] = []

        for file in uploadedFiles {
            if let data = file.data {
                documents.append(data)
            }
        }

        APIManager.shared.completeDocumentsProfileAPI(documents: documents)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in

                guard let self = self else { return }
                self.showActivity = false

                if case .failure(let error) = result {
                    self.errorMessage = error.localizedDescription
                    self.isPresentAlert = true
                    completion(false)
                }

            } receiveValue: { [weak self] response in

                guard let self = self else { return }
                self.showActivity = false

                if response.success ?? false {
                    completion(true)
                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                    completion(false)
                }

            }
            .store(in: &cancellables)
    }
    
    
    // MARK: - API CALL
    func addfamilymembermedicaldocumentsAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true

        var documents: [Data] = []

        for file in uploadedFiles {
            if let data = file.data {
                documents.append(data)
            }
        }

        APIManager.shared.addfamilymembermedicaldocumentsAPI(familyMemberId: UserDetail.shared.getID(), documents: documents)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in

                guard let self = self else { return }
                self.showActivity = false

                if case .failure(let error) = result {
                    self.errorMessage = error.localizedDescription
                    self.isPresentAlert = true
                    completion(false)
                }

            } receiveValue: { [weak self] response in

                guard let self = self else { return }
                self.showActivity = false

                if response.success ?? false {
                    completion(true)
                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                    completion(false)
                }

            }
            .store(in: &cancellables)
    }
    
    
    
//    // MARK: -EditFamilyDocumentAPI CALL
    
    func updateFamilyDocumentAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true

        var newDocuments: [Data] = []

        for file in uploadedFiles {
            if let data = file.data {
                newDocuments.append(data) //  only new files
            }
        }

        APIManager.shared.updateFamilyDocumentAPI(
            documents: newDocuments, family_member_id: familyMemberID ?? "",
            existingDocuments: existingDocumentPaths //  old files
        )
        .receive(on: DispatchQueue.main)
        .sink { [weak self] result in

            guard let self = self else { return }
            self.showActivity = false

            if case .failure(let error) = result {
                self.errorMessage = error.localizedDescription
                self.isPresentAlert = true
                completion(false)
            }

        } receiveValue: { [weak self] response in

            guard let self = self else { return }
            self.showActivity = false

            if response.success ?? false {
                completion(true)
            } else {
                self.errorMessage = response.message
                self.isPresentAlert = true
                completion(false)
            }
        }
        .store(in: &cancellables)
    }
    
    
    
//    // MARK: -UpdateProfileDocument API CALL
    
    func UpdateProfileDocumentAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true

        var newDocuments: [Data] = []

        for file in uploadedFiles {
            if let data = file.data {
                newDocuments.append(data) // 👈 only new files
            }
        }

        APIManager.shared.updateDocumentsProfileAPI(
            documents: newDocuments,
            existingDocuments: existingDocumentPaths // 👈 old files
        )
        .receive(on: DispatchQueue.main)
        .sink { [weak self] result in

            guard let self = self else { return }
            self.showActivity = false

            if case .failure(let error) = result {
                self.errorMessage = error.localizedDescription
                self.isPresentAlert = true
                completion(false)
            }

        } receiveValue: { [weak self] response in

            guard let self = self else { return }
            self.showActivity = false

            if response.success ?? false {
                completion(true)
            } else {
                self.errorMessage = response.message
                self.isPresentAlert = true
                completion(false)
            }
        }
        .store(in: &cancellables)
    }

    // MARK: - get Document API CALL
    func  getProfileDocumentAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true

        var documents: [Data] = []

        for file in uploadedFiles {
            if let data = file.data {
                documents.append(data)
            }
        }

        APIManager.shared.getProfileDocumentsAPI()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in

                guard let self = self else { return }
                self.showActivity = false

                if case .failure(let error) = result {
                    self.errorMessage = error.localizedDescription
                    self.isPresentAlert = true
                    completion(false)
                }

            } receiveValue: { [weak self] response in

                guard let self = self else { return }
                self.showActivity = false

                if response.success ?? false {
                    
                    let data = response.data
                 
                    print(data ,"Document Data coming here")
                    
                    self.setDocumentData(data: data,from: "profile")
                    
                    completion(true)
                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                    completion(false)
                }

            }
            .store(in: &cancellables)
    }
    
    
    // MARK: - get Document API CALL
    func  getfamilyDocumentAPI(completion: @escaping (Bool) -> Void) {

        showActivity = true

        var documents: [Data] = []

        for file in uploadedFiles {
            if let data = file.data {
                documents.append(data)
            }
        }

        APIManager.shared.getfamilyDocumentAPI(family_member_id: familyMemberID ?? "")
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in

                guard let self = self else { return }
                self.showActivity = false

                if case .failure(let error) = result {
                    self.errorMessage = error.localizedDescription
                    self.isPresentAlert = true
                    completion(false)
                }

            } receiveValue: { [weak self] response in

                guard let self = self else { return }
                self.showActivity = false

                if response.success ?? false {
                    
                    let data = response.data
                 
                    print(data ,"FamilyDocument Data coming here")
                    
                    self.setDocumentData(data: data, from: "family")
                    
                    completion(true)
                } else {
                    self.errorMessage = response.message
                    self.isPresentAlert = true
                    completion(false)
                }

            }
            .store(in: &cancellables)
    }
    
    func setDocumentData(data: DocumentModelData?, from: String) {
       
        
        if from != "family"  {
        let documents = data?.medicalDocuments ?? data?.Med_Docs ?? []

        uploadedFiles.removeAll()
        existingDocumentPaths = documents   // 👈 IMPORTANT

        for path in documents {
            let fileName = (path as NSString).lastPathComponent
            let fullURL = path.imgFullPath()

            let file = UploadedFile(
                name: fileName,
                typeIcon: iconName(for: fileName),
                data: nil,
                fileURL: fullURL
            )
                uploadedFiles.append(file)
            }
            
        } else if from != "profile"  {
            let documents = data?.medical_documents ?? data?.Med_Docs ?? []

            uploadedFiles.removeAll()
            existingDocumentPaths = documents   // 👈 IMPORTANT

            for path in documents {
                let fileName = (path as NSString).lastPathComponent
                let fullURL = path.imgFullPath()

                let file = UploadedFile(
                    name: fileName,
                    typeIcon: iconName(for: fileName),
                    data: nil,
                    fileURL: fullURL
                )
                    uploadedFiles.append(file)
                }
                
            } else {
           
            let documents = data?.medicalDocuments ?? data?.medicalDocuments ?? []

            uploadedFiles.removeAll()
            existingDocumentPaths = documents   // 👈 IMPORTANT

            for path in documents {
                let fileName = (path as NSString).lastPathComponent
                let fullURL = path.imgFullPath()

                let file = UploadedFile(
                    name: fileName,
                    typeIcon: iconName(for: fileName),
                    data: nil,
                    fileURL: fullURL
                )
                  uploadedFiles.append(file)
                }
        }
    }
    
    func removeFile(_ file: UploadedFile) {

        // 👇 agar API wala doc delete hua
        if let url = file.fileURL {
            existingDocumentPaths.removeAll { path in
                url.contains(path)   // match path
            }
        }

        uploadedFiles.removeAll { $0.id == file.id }
    }

    // MARK: - Add Document
    func addFile(_ url: URL) {

        guard let data = try? Data(contentsOf: url) else { return }

        let maxLimit = 10 * 1024 * 1024 // 10 MB in bytes
        if data.count > maxLimit {
            errorMessage = "File size exceeds the 10 MB limit. Please select a smaller file."
            isPresentAlert = true
            return
        }

        let file = UploadedFile(
            name: url.lastPathComponent,
            typeIcon: iconName(for: url.lastPathComponent),
            data: data
        )

        uploadedFiles.append(file)
    }

    // MARK: - Add Image
    
    func addImage(_ image: UIImage) {

        guard let data = compressImageTo2MB(image: image) else { return }

        let fileName = "image_\(Int(Date().timeIntervalSince1970)).jpg"

        let file = UploadedFile(
            name: fileName,
            typeIcon: iconName(for: fileName),
            data: data
        )

        uploadedFiles.append(file)
    }
    
    func compressImageTo2MB(image: UIImage) -> Data? {
        
        let maxSize = 2 * 1024 * 1024 // 2MB
        
        var compression: CGFloat = 0.9
        guard var data = image.jpegData(compressionQuality: compression) else { return nil }
        
        // 🔁 reduce quality until under 2MB
        while data.count > maxSize && compression > 0.1 {
            compression -= 0.1
            if let compressedData = image.jpegData(compressionQuality: compression) {
                data = compressedData
            }
        }
        
        return data
    }


    // MARK: - Icon Detection
    func iconName(for fileName: String) -> String {

        let ext = (fileName as NSString).pathExtension.lowercased()

        switch ext {

        case "jpg", "jpeg", "png", "heic":
            return "ImageIcon"

        case "doc", "docx":
            return "DocIcon"

        case "pdf":
            return "FileIcon"

        default:
            return "FileIcon"
        }
    }
}



