//
//  Extension.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 09/02/26.
//

import UIKit
 
extension UITextField {
 
    open override func awakeFromNib() {
        super.awakeFromNib()
        addDoneButton()
    }
 
    open override func didMoveToWindow() {
        super.didMoveToWindow()
        addDoneButton()
    }
 
    private func addDoneButton() {
        guard inputAccessoryView == nil else { return }
        inputAccessoryView = KeyboardDoneToolbar()
    }
}
 
extension UIApplication {
 
    private static let swizzleOnce: Void = {
        let original = class_getInstanceMethod(UITextField.self,
                                               #selector(UITextField.becomeFirstResponder))
        let swizzled = class_getInstanceMethod(UITextField.self,
                                               #selector(UITextField.swizzled_becomeFirstResponder))
 
        if let original = original, let swizzled = swizzled {
            method_exchangeImplementations(original, swizzled)
        }
    }()
 
    static func enableGlobalDoneButton() {
        _ = swizzleOnce
    }
}

extension UITextField {
 
    @objc func swizzled_becomeFirstResponder() -> Bool {
 
        if inputAccessoryView == nil {
            inputAccessoryView = KeyboardDoneToolbar()
        }
 
        return swizzled_becomeFirstResponder()
    }
}

import SwiftUI
 
struct KeyboardDoneModifier: ViewModifier {
 
    @FocusState private var isFocused: Bool
 
    func body(content: Content) -> some View {
        content
            .toolbar {
                ToolbarItemGroup(placement: .keyboard) {
                    Spacer()
 
                    Button("Done") {
                        UIApplication.shared.sendAction(
                            #selector(UIResponder.resignFirstResponder),
                            to: nil, from: nil, for: nil
                        )
                    }
                    .font(.system(size: 17, weight: .semibold))
                }
            }
    }
}
 
extension View {
    func keyboardDoneButton() -> some View {
        modifier(KeyboardDoneModifier())
    }
}
