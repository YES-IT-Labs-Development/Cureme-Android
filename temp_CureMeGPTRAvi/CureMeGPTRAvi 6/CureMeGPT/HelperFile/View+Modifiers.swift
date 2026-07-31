//
//  View+Modifiers.swift
//  CureMeGPT
//
//  Created by Antigravity on 09/07/26.
//

import SwiftUI

extension View {
    @ViewBuilder func `if`<Content: View>(_ condition: Bool, transform: (Self) -> Content) -> some View {
        if condition {
            transform(self)
        } else {
            self
        }
    }
}

extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape(RoundedCorner(radius: radius, corners: corners))
    }
}

struct RoundedCorner: Shape {
    var radius: CGFloat = 12.0
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect,
                                byRoundingCorners: corners,
                                cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
    }
}

struct DisableScrollBounce: ViewModifier {
    func body(content: Content) -> some View {
        content
            .onAppear {
                UIScrollView.appearance().bounces = false
            }
            .onDisappear {
                UIScrollView.appearance().bounces = true
            }
    }
}

extension View {
    func disableScrollBounce() -> some View {
        self.modifier(DisableScrollBounce())
    }
}

struct CurvedTopPopupShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        let curveHeight: CGFloat = 28

        path.move(to: CGPoint(x: 0, y: curveHeight))

        path.addQuadCurve(
            to: CGPoint(x: rect.width / 2, y: 0),
            control: CGPoint(x: rect.width / 4, y: 0)
        )

        path.addQuadCurve(
            to: CGPoint(x: rect.width, y: curveHeight),
            control: CGPoint(x: rect.width * 3 / 4, y: 0)
        )

        path.addLine(to: CGPoint(x: rect.width, y: rect.height))
        path.addLine(to: CGPoint(x: 0, y: rect.height))
        path.closeSubpath()

        return path
    }
}

struct DisableTabScrollBounce: ViewModifier {
    func body(content: Content) -> some View {
        content
            .onAppear {
                UIScrollView.appearance().bounces = false
                UIScrollView.appearance().alwaysBounceVertical = false
            }
            .onDisappear {
                UIScrollView.appearance().bounces = false
                UIScrollView.appearance().alwaysBounceVertical = false
            }
    }
}

extension View {
    func disableTabScrollBounce() -> some View {
        self.modifier(DisableScrollBounce())
    }
}
