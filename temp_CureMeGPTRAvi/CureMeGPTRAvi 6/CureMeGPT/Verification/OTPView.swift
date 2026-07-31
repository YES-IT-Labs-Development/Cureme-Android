//
//  OTPView.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 18/11/25.
//

import SwiftUI
import Combine

// A SwiftUI view for entering OTP (One-Time Password).
struct OTPFieldView: View {
    @FocusState private var pinFocusState: FocusPin?
    @Binding private var otp: String
    @State private var pins: [String]
    
    var numberOfFields: Int
    
    enum FocusPin: Hashable {
        case pin(Int)
    }
    
    init(numberOfFields: Int, otp: Binding<String>) {
        self.numberOfFields = numberOfFields
        self._otp = otp
        self._pins = State(initialValue: Array(repeating: "", count: numberOfFields))
    }
    
    var body: some View {
        HStack(spacing: 21) {
            ForEach(0..<numberOfFields, id: \.self) { index in
                CustomOTPTextField(
                    text: $pins[index],
                    index: index,
                    isFocused: Binding(
                        get: { pinFocusState == .pin(index) },
                        set: { if $0 { pinFocusState = .pin(index) } }
                    ),
                    onBackspace: {
                        // Move focus to previous field and clear it
                        if index > 0 {
                            pins[index - 1] = ""
                            pinFocusState = .pin(index - 1)
                        }
                        updateOTPString()
                    },
                    onTextChange: { newVal in
                        if newVal.count == 1 {
                            // Move to next field
                            if index < numberOfFields - 1 {
                                pinFocusState = .pin(index + 1)
                            } else {
                                pinFocusState = nil
                            }
                        }
                        updateOTPString()
                    }
                )
                .focused($pinFocusState, equals: .pin(index))
                .frame(height: 50)
            }
        }
        .onAppear {
            updatePinsFromOTP()
            pinFocusState = .pin(0) // focus first field automatically
        }
        .onChange(of: otp) { newValue in
                 // This ensures clearing works
                 if newValue.isEmpty {
                     pins = Array(repeating: "", count: numberOfFields)
                 }
             }
    }
    
    private func updatePinsFromOTP() {
        let otpArray = Array(otp.prefix(numberOfFields))
        for (index, char) in otpArray.enumerated() {
            pins[index] = String(char)
        }
    }
    
    private func updateOTPString() {
        otp = pins.joined()
    }
}

// MARK: - Custom textfield that detects backspace
struct CustomOTPTextField: UIViewRepresentable {
    @Binding var text: String
    var index: Int
    @Binding var isFocused: Bool
    var onBackspace: () -> Void
    var onTextChange: (String) -> Void
    
    func makeUIView(context: Context) -> UITextField {
            let textField = BackspaceDetectingTextField()
            textField.delegate = context.coordinator
            textField.textAlignment = .center
            textField.keyboardType = .numberPad

            // Safe font (no crash)
            let mainFont = UIFont(name: "PlusJakartaSans-Medium", size: 16)
                ?? UIFont.systemFont(ofSize: 16, weight: .medium)
            textField.font = mainFont

            // Placeholder "0"
            textField.attributedPlaceholder = NSAttributedString(
                string: "0",
                attributes: [
                    .foregroundColor: UIColor(Color(hex: "#ADB5BD")),
                    .font: mainFont
                ]
            )

            // Circular UI
            textField.layer.borderColor = UIColor(Color(hex: "#CED4DA")).cgColor
            textField.layer.borderWidth = 1
            textField.backgroundColor = .clear
            textField.layer.cornerRadius = 25
            textField.clipsToBounds = true

            textField.translatesAutoresizingMaskIntoConstraints = false
            NSLayoutConstraint.activate([
                textField.widthAnchor.constraint(equalToConstant: 50),
                textField.heightAnchor.constraint(equalToConstant: 50)
            ])

            textField.tag = index

            textField.onBackspace = {
                if textField.text?.isEmpty ?? true {
                    onBackspace()
                }
            }

            return textField
        }
    
    func updateUIView(_ uiView: UITextField, context: Context) {
        uiView.text = text
        if isFocused && !uiView.isFirstResponder {
            uiView.becomeFirstResponder()
        } else if !isFocused && uiView.isFirstResponder {
            uiView.resignFirstResponder()
        }
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, UITextFieldDelegate {
        var parent: CustomOTPTextField
        init(_ parent: CustomOTPTextField) {
            self.parent = parent
        }
        func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
            if string.isEmpty { // backspace handled separately
                parent.text = ""
                parent.onTextChange("")
                return true
            } else if string.count == 1 {
                parent.text = string
                parent.onTextChange(string)
                return false
            }
            return true
        }
    }
}
    
// MARK: - Detect backspace key
class BackspaceDetectingTextField: UITextField {
    var onBackspace: (() -> Void)?
    override func deleteBackward() {
        onBackspace?()
        super.deleteBackward()
    }
}
