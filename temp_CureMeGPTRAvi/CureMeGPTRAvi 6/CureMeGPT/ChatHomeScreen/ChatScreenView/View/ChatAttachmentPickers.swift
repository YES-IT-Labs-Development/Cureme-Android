//
//  ChatAttachmentPickers.swift
//  CureMeGPT
//
//  Created by Antigravity on 09/07/26.
//

import SwiftUI
import UIKit

struct AttachmentSheetView: View {
    let onGallery: () -> Void
    let onDocument: () -> Void

    var body: some View {
        VStack(spacing: 24) {
            Capsule()
                .frame(width: 40, height: 5)
                .foregroundColor(.gray.opacity(0.4))

            Button {
                onGallery()
            } label: {
                attachmentRow(icon: "photo", title: "Gallery")
            }

            Button {
                onDocument()
            } label: {
                attachmentRow(icon: "doc", title: "Documents")
            }

            Spacer()
        }
        .padding()
        .presentationDetents([.height(220)])
    }

    private func attachmentRow(icon: String, title: String) -> some View {
        HStack(spacing: 14) {
            Image(systemName: icon)
                .font(.system(size: 22))
                .foregroundColor(.white)
                .frame(width: 44, height: 44)
                .background(Color(hex: "#4F46E5"))
                .clipShape(Circle())

            Text(title)
                .font(.system(size: 16, weight: .medium))

            Spacer()
        }
    }
}

struct ImagePicker: UIViewControllerRepresentable {
    let onImagePicked: (UIImage) -> Void

    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.sourceType = .photoLibrary
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(onImagePicked: onImagePicked)
    }

    final class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        let onImagePicked: (UIImage) -> Void

        init(onImagePicked: @escaping (UIImage) -> Void) {
            self.onImagePicked = onImagePicked
        }

        func imagePickerController(
            _ picker: UIImagePickerController,
            didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]
        ) {
            if let image = info[.originalImage] as? UIImage {
                onImagePicked(image)
            }
            picker.dismiss(animated: true)
        }
    }
}

struct ChatDocumentPicker: UIViewControllerRepresentable {
    let onPick: (URL) -> Void

    func makeUIViewController(context: Context) -> UIDocumentPickerViewController {
        let picker = UIDocumentPickerViewController(forOpeningContentTypes: [.pdf, .image, .text])
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: UIDocumentPickerViewController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(onPick: onPick)
    }

    final class Coordinator: NSObject, UIDocumentPickerDelegate {
        let onPick: (URL) -> Void

        init(onPick: @escaping (URL) -> Void) {
            self.onPick = onPick
        }

        func documentPicker(
            _ controller: UIDocumentPickerViewController,
            didPickDocumentsAt urls: [URL]
        ) {
            guard let url = urls.first else { return }
            
            let fileManager = FileManager.default
            let tempDirectory = fileManager.temporaryDirectory
            
            // Check if the file starts with the PDF magic bytes
            var isPDF = false
            if let fileHandle = try? FileHandle(forReadingFrom: url) {
                if let firstBytes = try? fileHandle.read(upToCount: 4) {
                    isPDF = firstBytes == Data([0x25, 0x50, 0x44, 0x46]) // %PDF
                }
                try? fileHandle.close()
            }
            
            var destinationURL = tempDirectory.appendingPathComponent(url.lastPathComponent)
            if isPDF && destinationURL.pathExtension.lowercased() != "pdf" {
                let nameWithoutExtension = destinationURL.deletingPathExtension().lastPathComponent
                destinationURL = tempDirectory.appendingPathComponent(nameWithoutExtension + ".pdf")
                print("DEBUG PICKER: Enforced .pdf extension based on file header")
            }
            
            print("DEBUG PICKER: Original Imported URL is \(url)")
            print("DEBUG PICKER: Target Destination is \(destinationURL)")
            
            do {
                if fileManager.fileExists(atPath: destinationURL.path) {
                    try fileManager.removeItem(at: destinationURL)
                }
                try fileManager.copyItem(at: url, to: destinationURL)
                
                if let attr = try? fileManager.attributesOfItem(atPath: destinationURL.path) {
                    print("DEBUG PICKER: Copied file size: \(attr[.size] ?? 0) bytes")
                }
                onPick(destinationURL)
            } catch {
                print("DEBUG PICKER: Error copying file: \(error.localizedDescription)")
                onPick(url)
            }
        }
    }
}
