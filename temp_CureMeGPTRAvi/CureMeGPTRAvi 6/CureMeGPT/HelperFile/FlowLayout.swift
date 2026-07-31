//
//  FlowLayout.swift
//  CureMeGPT
//
//  Created by Antigravity on 09/07/26.
//

import SwiftUI

struct FlowLayout<Data: RandomAccessCollection, Content: View>: View
where Data.Element: Hashable {

    let items: Data
    let spacing: CGFloat
    let content: (Data.Element) -> Content

    init(
        items: Data,
        spacing: CGFloat = 8,
        @ViewBuilder content: @escaping (Data.Element) -> Content
    ) {
        self.items = items
        self.spacing = spacing
        self.content = content
    }

    var body: some View {
        GeometryReader { geometry in
            self.generateContent(in: geometry)
        }
    }

    private func generateContent(in geometry: GeometryProxy) -> some View {
        var width: CGFloat = 0
        var height: CGFloat = 0

        return ZStack(alignment: .topLeading) {
            ForEach(items, id: \.self) { item in
                content(item)
                    .padding(.horizontal, spacing / 2)
                    .padding(.vertical, spacing / 2)
                    .alignmentGuide(.leading) { dimension in
                        if abs(width - dimension.width) > geometry.size.width {
                            width = 0
                            height -= dimension.height
                        }
                        let result = width
                        width -= dimension.width
                        return result
                    }
                    .alignmentGuide(.top) { _ in
                        let result = height
                        return result
                    }
            }
        }
        .frame(
            width: geometry.size.width,
            alignment: .topLeading
        )
    }
}
