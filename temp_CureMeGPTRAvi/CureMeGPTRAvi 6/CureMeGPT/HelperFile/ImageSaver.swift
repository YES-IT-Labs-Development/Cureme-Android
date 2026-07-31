//
//  ImageSaver.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 18/06/26.
//


import UIKit

final class ImageSaver: NSObject {
    
    static let shared = ImageSaver()
    var completion: ((Bool, String) -> Void)?

    func writeToPhotoAlbum(image: UIImage) {
        UIImageWriteToSavedPhotosAlbum(
            image,
            self,
            #selector(saveCompleted),
            nil
        )
    }

    @objc private func saveCompleted(
        _ image: UIImage,
        didFinishSavingWithError error: Error?,
        contextInfo: UnsafeRawPointer
    ) {
        if let error = error {
            print("Save failed: \(error.localizedDescription)")
            self.completion?(false, error.localizedDescription)
        } else {
            print("Image saved successfully")
            self.completion?(true, "File saved successfully")
        }
    }
}
//

