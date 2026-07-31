//
//  PDFDownloader.swift
//  AWPL
//
//  Created by YATIN  KALRA on 12/06/25.
//


import SwiftUI
import PDFKit

class PDFDownloader: ObservableObject {
    @Published var downloadedFileURL: URL? = nil
    @Published var isDownloading: Bool = false
    @Published var errorMessage: String? = nil

    func downloadPDF(from urlString: String,completion: @escaping (URL?) -> Void) {
        guard let url = URL(string: urlString) else {
            self.errorMessage = "Invalid URL"
            completion(nil)
            return
        }

        let fileName = url.lastPathComponent
        let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let destinationURL = documentsDirectory.appendingPathComponent(fileName)

        // ✅ Check if file already exists
        if FileManager.default.fileExists(atPath: destinationURL.path) {
            self.downloadedFileURL = destinationURL
            completion(destinationURL)
            print("File already exists at: \(destinationURL)")
            return
        }

        isDownloading = true
        errorMessage = nil

        let task = URLSession.shared.downloadTask(with: url) { tempURL, response, error in
            DispatchQueue.main.async {
                self.isDownloading = false
            }

            if let error = error {
                DispatchQueue.main.async {
                    self.errorMessage = "Download error: \(error.localizedDescription)"
                    completion(nil)
                }
                return
            }

            guard let tempURL = tempURL else {
                DispatchQueue.main.async {
                    self.errorMessage = "No file found"
                    completion(nil)
                }
                return
            }

            do {
                try FileManager.default.moveItem(at: tempURL, to: destinationURL)

                DispatchQueue.main.async {
                    self.downloadedFileURL = destinationURL
                    completion(destinationURL)
                }
            } catch {
                DispatchQueue.main.async {
                    self.errorMessage = "File save error: \(error.localizedDescription)"
                    completion(nil)
                }
            }
        }

        task.resume()
    }
}
