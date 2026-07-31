//
//  ImagePicker.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 12/03/26.
//

import SwiftUI

struct ImagePicker12: UIViewControllerRepresentable {

    var sourceType: UIImagePickerController.SourceType
    var completion: (UIImage) -> Void

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    func makeUIViewController(context: Context) -> UIImagePickerController {

        let picker = UIImagePickerController()
        picker.sourceType = sourceType
        picker.delegate = context.coordinator

        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}

    class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {

        let parent: ImagePicker12

        init(_ parent: ImagePicker12) {
            self.parent = parent
        }

        func imagePickerController(
            _ picker: UIImagePickerController,
            didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]
        ) {

            if let image = info[.originalImage] as? UIImage {
                parent.completion(image)
            }

            picker.dismiss(animated: true)
        }
    }
}
