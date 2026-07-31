//
//  PDFViewer.swift
//  AWPL
//
//  Created by YATIN  KALRA on 12/06/25.
//



import SwiftUI
import PDFKit

struct PDFViewer: View {
    let fileURL: URL
    @State private var showShareSheet = false

    var body: some View {
        VStack(spacing: 0) {
            PDFKitRepresentedView(url: fileURL)
                .edgesIgnoringSafeArea(.all)

            Button(action: {
                showShareSheet = true
            }) {
                Label("Download PDF", image: "DownloadBtnImg")
                    .font(.headline)
                    .padding()
                    .frame(maxWidth: .infinity)
                    .background(Color("F47820"))
                    .foregroundColor(.white)
            }
        }
        .sheet(isPresented: $showShareSheet) {
            ShareSheet(activityItems: [fileURL])
        }
    }
}
struct PDFKitRepresentedView: UIViewRepresentable {
    let url: URL

    func makeUIView(context: Context) -> PDFView {
        let pdfView = PDFView()
        pdfView.autoScales = true
        if let document = PDFDocument(url: url) {
            pdfView.document = document
        }
        return pdfView
    }

    func updateUIView(_ pdfView: PDFView, context: Context) {}
}
struct ShareSheet: UIViewControllerRepresentable {
    let activityItems: [Any]
    let applicationActivities: [UIActivity]? = nil

    func makeUIViewController(context: Context) -> UIActivityViewController {
        let controller = UIActivityViewController(activityItems: activityItems, applicationActivities: applicationActivities)
        return controller
    }

    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}
