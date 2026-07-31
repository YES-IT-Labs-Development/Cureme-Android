//
//  ImagePicker.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 09/02/26.
//

 

//
//import SwiftUI
//import UIKit
//import CropViewController
//
//struct ImagePicker1: UIViewControllerRepresentable {
//
//    var sourceType: UIImagePickerController.SourceType = .photoLibrary
//    @Binding var selectedImage: UIImage?
//
//    @Environment(\.presentationMode) private var presentationMode
//
//    func makeCoordinator() -> Coordinator {
//        Coordinator(self)
//    }
//
//    func makeUIViewController(context: Context) -> UIImagePickerController {
//
//        let picker = UIImagePickerController()
//        picker.delegate = context.coordinator
//        picker.sourceType = sourceType
//        picker.allowsEditing = false
//
//        return picker
//    }
//
//    func updateUIViewController(
//        _ uiViewController: UIImagePickerController,
//        context: Context
//    ) {}
//
//    final class Coordinator: NSObject,
//                             UINavigationControllerDelegate,
//                             UIImagePickerControllerDelegate,
//                             CropViewControllerDelegate {
//
//        let parent: ImagePicker1
//
//        init(_ parent: ImagePicker1) {
//            self.parent = parent
//        }
//
//        func imagePickerController(
//            _ picker: UIImagePickerController,
//            didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]
//        ) {
//
//            guard let image = info[.originalImage] as? UIImage else {
//                return
//            }
//
//            let cropVC = CropViewController(croppingStyle: .default, image: image)
//
//            cropVC.delegate = self
//          //  cropVC.aspectRatioPreset = .presetSquare
//            cropVC.aspectRatioLockEnabled = true
//            cropVC.resetAspectRatioEnabled = false
//            cropVC.rotateButtonsHidden = false
//
//            picker.pushViewController(cropVC, animated: true)
//        }
//
//        func cropViewController(
//            _ cropViewController: CropViewController,
//            didCropToCircularImage image: UIImage,
//            withRect cropRect: CGRect,
//            angle: Int
//        ) {
//
//            parent.selectedImage = image
//
//            parent.presentationMode.wrappedValue.dismiss()
//        }
//
//        func cropViewController(
//            _ cropViewController: CropViewController,
//            didCropTo image: UIImage,
//            withRect cropRect: CGRect,
//            angle: Int
//        ) {
//
//            parent.selectedImage = image
//
//            parent.presentationMode.wrappedValue.dismiss()
//        }
//
//        func imagePickerControllerDidCancel(
//            _ picker: UIImagePickerController
//        ) {
//            parent.presentationMode.wrappedValue.dismiss()
//        }
//    }
//}


import SwiftUI
import UIKit
import CropViewController

struct ImagePicker1: UIViewControllerRepresentable {

    var sourceType: UIImagePickerController.SourceType = .photoLibrary
    @Binding var selectedImage: UIImage?

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    func makeUIViewController(context: Context) -> UIImagePickerController {

        let picker = UIImagePickerController()
        picker.delegate = context.coordinator
        picker.sourceType = sourceType
        picker.allowsEditing = false

        return picker
    }

    func updateUIViewController(
        _ uiViewController: UIImagePickerController,
        context: Context
    ) { }

    final class Coordinator: NSObject,
                             UINavigationControllerDelegate,
                             UIImagePickerControllerDelegate,
                             CropViewControllerDelegate {

        let parent: ImagePicker1

        init(_ parent: ImagePicker1) {
            self.parent = parent
        }

        // MARK: - Image Picked

        func imagePickerController(
            _ picker: UIImagePickerController,
            didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]
        ) {

            guard let image = info[.originalImage] as? UIImage else {
                print("❌ No image found")
                return
            }

            print("✅ Image Picked")

            let cropVC = CropViewController(
                croppingStyle: .default,
                image: image
            )

            cropVC.delegate = self

            // Rectangle crop
            cropVC.aspectRatioLockEnabled = false
            cropVC.resetAspectRatioEnabled = true
            cropVC.rotateButtonsHidden = false

            picker.pushViewController(cropVC, animated: true)
        }

        // MARK: - Rectangle Crop Success

        func cropViewController(
            _ cropViewController: CropViewController,
            didCropTo image: UIImage,
            withRect cropRect: CGRect,
            angle: Int
        ) {

            print("✅ didCropTo called")

            DispatchQueue.main.async {
                self.parent.selectedImage = image

                if let picker = cropViewController.navigationController as? UIImagePickerController {
                    picker.dismiss(animated: true)
                } else {
                    cropViewController.dismiss(animated: true)
                }
            }
        }
        
        func cropViewController(_ cropViewController: CropViewController,
                                didCropToImage image: UIImage,
                                withRect cropRect: CGRect,
                                angle: Int) {

            print("🔥 OLD didCropToImage CALLED")

            DispatchQueue.main.async {
                self.parent.selectedImage = image
                cropViewController.dismiss(animated: true)
            }
        }

        // MARK: - Circular Crop Success

        func cropViewController(
            _ cropViewController: CropViewController,
            didCropToCircularImage image: UIImage,
            withRect cropRect: CGRect,
            angle: Int
        ) {

            print("✅ didCropToCircularImage called")

            DispatchQueue.main.async {
                self.parent.selectedImage = image

                if let picker = cropViewController.navigationController as? UIImagePickerController {
                    picker.dismiss(animated: true)
                } else {
                    cropViewController.dismiss(animated: true)
                }
            }
        }

        // MARK: - Crop Cancel

        func cropViewController(
            _ cropViewController: CropViewController,
            didFinishCancelled cancelled: Bool
        ) {

            print("❌ Crop Cancelled")

            if let picker = cropViewController.navigationController as? UIImagePickerController {
                picker.dismiss(animated: true)
            } else {
                cropViewController.dismiss(animated: true)
            }
        }

        // MARK: - Picker Cancel

        func imagePickerControllerDidCancel(
            _ picker: UIImagePickerController
        ) {

            print("❌ Picker Cancelled")
            picker.dismiss(animated: true)
        }
    }
}
