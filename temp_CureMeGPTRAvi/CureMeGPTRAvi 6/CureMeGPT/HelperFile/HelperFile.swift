//
//  HelperFile.swift
//  AWPL
//
//  Created by YATIN  KALRA on 07/05/25.
//

import Foundation
import UIKit
import SwiftUI
import SDWebImageSwiftUI

struct HTMLText: UIViewRepresentable {
    let html: String

    func makeUIView(context: Context) -> UILabel {
        let label = UILabel()
        label.numberOfLines = 0
        label.attributedText = html.toAttributedString()
        return label
    }

    func updateUIView(_ uiView: UILabel, context: Context) {
        uiView.attributedText = html.toAttributedString()
    }
}

extension UIScreen {
    static let messageMaxWidth = UIScreen.main.bounds.width * 0.8
}

extension String {
    func toAttributedString() -> NSAttributedString? {
        guard let data = self.data(using: .utf16, allowLossyConversion: false) else { return nil }
        let options: [NSAttributedString.DocumentReadingOptionKey: Any] = [
            .documentType: NSAttributedString.DocumentType.html,
            .characterEncoding: String.Encoding.utf16.rawValue
        ]
        return try? NSAttributedString(data: data, options: options, documentAttributes: nil)
    }
}
// MARK: - Extension
extension String {
    func localized(_ bundle: Bundle = .main) -> String {
        NSLocalizedString(self, bundle: bundle, comment: "")
    }
}

extension Image {
    static func loadImage(
        _ urlString: String?,
        width: CGFloat = 160,
        height: CGFloat = 170,
        cornerRadius: CGFloat = 0,
        contentMode: ContentMode = .fill
    ) -> some View {
        WebImage(url: URL(string: urlString ?? ""))
            .resizable()
            .indicator(.activity)
            .aspectRatio(contentMode: contentMode)
            .frame(width: width, height: height)
            .if(cornerRadius > 0) { $0.cornerRadius(cornerRadius) }
            .clipped()
    }
    
    static func loadProfileImage(
        _ urlString: String?,
        width: CGFloat = 28,
        height: CGFloat = 28,
        cornerRadius: CGFloat = 14,
        contentMode: ContentMode = .fill
    ) -> some View {
        WebImage(url: URL(string: urlString ?? "")) { image in
            image
                .resizable()
                .aspectRatio(contentMode: contentMode)
        } placeholder: {
            Image("Frame 1")
                .resizable()
                .aspectRatio(contentMode: contentMode)
        }
        .frame(width: width, height: height)
        .if(cornerRadius > 0) { $0.cornerRadius(cornerRadius) }
        .clipped()
    }
    
    
//   
//        static func loadImage3(
//            _ urlString: String?,
//            width: CGFloat = 160,
//            height: CGFloat = 170,
//            cornerRadius: CGFloat = 0,
//            contentMode: ContentMode = .fill
//        ) -> some View {
//
//            ZStack {
//                // 🔥 Shimmer background
//                ShimmerView()
//                    .frame(width: width, height: height)
//                    .background(Color.gray.opacity(0.1))
//                    .cornerRadius(cornerRadius)
//
//                // ✅ Actual image
//                WebImage(url: URL(string: urlString ?? ""))
//                    .resizable()
//                    .scaledToFill()
//                    //.aspectRatio(contentMode: contentMode)
//                    //.frame(width: width, height: height)
//                    .if(cornerRadius > 0) { $0.cornerRadius(cornerRadius) }
//                    .clipped()
//            }
//        }
//
    
    static func loadImage3(
        _ urlString: String?,
        width: CGFloat = 160,
        height: CGFloat = 170,
        cornerRadius: CGFloat = 0,
        contentMode: ContentMode = .fill
    ) -> some View {
        
        ImageLoaderView(
            urlString: urlString,
            width: width,
            height: height,
            cornerRadius: cornerRadius
        )
    }

    private struct ImageLoaderView: View {
        
        let urlString: String?
        let width: CGFloat
        let height: CGFloat
        let cornerRadius: CGFloat
        
        var body: some View {
            ZStack {
                
               ShimmerView()
                        .frame(width: width, height: height)
                        .background(Color.gray.opacity(0.1))
                        .cornerRadius(cornerRadius)
                
                WebImage(url: URL(string: urlString ?? ""))
                    .resizable()
                    .scaledToFill()
                    .frame(width: width, height: height)
                    .cornerRadius(cornerRadius)
                    .clipped()
                   
            }
        }
    }
    static func loadImage5(
        _ urlString: String?,
        width: CGFloat = 206,
        height: CGFloat = 210,
        cornerRadius: CGFloat = 30,
        contentMode: ContentMode = .fill
    ) -> some View {
        WebImage(url: URL(string: urlString ?? ""))
            .resizable()
            .indicator(.activity)
            .aspectRatio(contentMode: contentMode)
            .frame(width: width, height: height)
            .if(cornerRadius > 0) { $0.cornerRadius(cornerRadius) }
            .clipped()
    }
    
   

    struct ShimmerView: View {
        @State private var move: CGFloat = -1

        var body: some View {
            GeometryReader { geo in
                let width = geo.size.width

                LinearGradient(
                    gradient: Gradient(colors: [
                        Color.gray.opacity(0.3),
                        Color.gray.opacity(0.1),
                        Color.gray.opacity(0.3)
                    ]),
                    startPoint: .leading,
                    endPoint: .trailing
                )
                .mask(
                    Rectangle()
                        .fill(
                            LinearGradient(
                                gradient: Gradient(colors: [
                                    .black.opacity(0.3),
                                    .black,
                                    .black.opacity(0.3)
                                ]),
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        //.rotationEffect(.degrees(20))
                        .offset(x: move * width)
                )
                .onAppear {
                    withAnimation(
                       // .linear(duration: 1.2)
                       // .repeatForever(autoreverses: false)
                    ) {
                        move = 1
                    }
                }
            }
        }
    }
}



// Color extension moved to Color+Extensions.swift



// View extensions and KeyboardManager moved to separate files

// Scroll modifiers and shapes moved to View+Modifiers.swift

extension String {
    func htmlStripped() -> String {
        guard let data = self.data(using: .utf8) else { return self }
        if let attributed = try? NSAttributedString(
            data: data,
            options: [.documentType: NSAttributedString.DocumentType.html,
                      .characterEncoding: String.Encoding.utf8.rawValue],
            documentAttributes: nil
        ) {
            return attributed.string
        }
        return self
    }
}

extension URL {
    func fileSizeString() -> String {
        do {
            let resourceValues = try self.resourceValues(forKeys: [.fileSizeKey])
            if let size = resourceValues.fileSize {
                let formatter = ByteCountFormatter()
                formatter.allowedUnits = [.useMB]
                formatter.countStyle = .file
                return formatter.string(fromByteCount: Int64(size))
            }
        } catch {
            print(error.localizedDescription)
        }
        return "N/A"
    }
}

struct NoEffectButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label // just show the label, no opacity or scaling
    }
}

// ValidationManager and FlowLayout moved to separate files

