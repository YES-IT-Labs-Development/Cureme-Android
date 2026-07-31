//
//  KeyboardManager.swift
//  CureMeGPT
//
//  Created by Antigravity on 09/07/26.
//

import UIKit
import SwiftUI

final class KeyboardDoneToolbar: UIToolbar {
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }
 
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setup()
    }
 
    private func setup() {
        sizeToFit()
 
        let flex = UIBarButtonItem(systemItem: .flexibleSpace)
 
        let done = UIBarButtonItem(
            title: "Done",
            style: .done,
            target: self,
            action: #selector(doneAction)
        )
 
        items = [flex, done]
    }
 
    @objc private func doneAction() {
        UIApplication.shared.sendAction(
            #selector(UIResponder.resignFirstResponder),
            to: nil, from: nil, for: nil
        )
    }
}

private weak var _currentFirstResponder: UIResponder?
 
extension UIResponder {
    static var currentFirstResponder: UIResponder? {
        _currentFirstResponder = nil
        UIApplication.shared.sendAction(#selector(findFirstResponder),
                                        to: nil, from: nil, for: nil)
        return _currentFirstResponder
    }
 
    @objc private func findFirstResponder() {
        _currentFirstResponder = self
    }
}
 
final class KeyboardManager {
    static let shared = KeyboardManager()
    private init() {}
 
    func start() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardWillShow),
            name: UIResponder.keyboardWillShowNotification,
            object: nil
        )
    }
 
    @objc private func keyboardWillShow(_ notification: Notification) {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) {
            guard let responder = UIResponder.currentFirstResponder else { return }
 
            if let textField = responder as? UITextField,
               textField.inputAccessoryView == nil {
                textField.inputAccessoryView = KeyboardDoneToolbar()
                textField.reloadInputViews()
            }
 
            if let textView = responder as? UITextView,
               textView.inputAccessoryView == nil {
                textView.inputAccessoryView = KeyboardDoneToolbar()
                textView.reloadInputViews()
            }
        }
    }
}
