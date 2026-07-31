//
//  LinkEncryptionHelper.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 27/07/26.
//

import Foundation

/// Utility helper to encrypt and decrypt deep link parameters (IDs)
struct LinkEncryptionHelper {
    private static let cipherKey: UInt8 = 0x5A
    
    /// Encrypts an integer ID into a URL-safe encrypted string token
    static func encrypt(id: Int) -> String {
        let stringVal = "\(id)"
        guard let data = stringVal.data(using: .utf8) else { return "\(id)" }
        let encryptedBytes = data.map { $0 ^ cipherKey }
        let base64 = Data(encryptedBytes).base64EncodedString()
        return base64
            .replacingOccurrences(of: "+", with: "-")
            .replacingOccurrences(of: "/", with: "_")
            .replacingOccurrences(of: "=", with: "")
    }
    
    /// Decrypts a URL-safe encrypted token back into an integer ID
    /// Tries decryption first, with fallback to direct integer parsing for unencrypted legacy links
    static func decrypt(string: String) -> Int? {
        let trimmed = string.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.isEmpty { return nil }
        
        // 1. Base64 URL-safe decoding & XOR decryption
        var base64 = trimmed
            .replacingOccurrences(of: "-", with: "+")
            .replacingOccurrences(of: "_", with: "/")
        
        let remainder = base64.count % 4
        if remainder > 0 {
            base64.append(String(repeating: "=", count: 4 - remainder))
        }
        
        if let data = Data(base64Encoded: base64) {
            let decryptedBytes = data.map { $0 ^ cipherKey }
            if let decryptedString = String(bytes: decryptedBytes, encoding: .utf8),
               let decryptedInt = Int(decryptedString) {
                return decryptedInt
            }
        }
        
        // 2. Direct integer fallback for unencrypted legacy links
        return Int(trimmed)
    }
    
    /// Encrypts a string into a URL-safe encrypted string token
    static func encryptString(_ string: String) -> String {
        guard let data = string.data(using: .utf8) else { return string }
        let encryptedBytes = data.map { $0 ^ cipherKey }
        let base64 = Data(encryptedBytes).base64EncodedString()
        return base64
            .replacingOccurrences(of: "+", with: "-")
            .replacingOccurrences(of: "/", with: "_")
            .replacingOccurrences(of: "=", with: "")
    }
    
    /// Decrypts a URL-safe encrypted token back into a string
    static func decryptString(_ string: String) -> String? {
        let trimmed = string.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.isEmpty { return nil }
        
        var base64 = trimmed
            .replacingOccurrences(of: "-", with: "+")
            .replacingOccurrences(of: "_", with: "/")
        
        let remainder = base64.count % 4
        if remainder > 0 {
            base64.append(String(repeating: "=", count: 4 - remainder))
        }
        
        if let data = Data(base64Encoded: base64) {
            let decryptedBytes = data.map { $0 ^ cipherKey }
            if let decryptedString = String(bytes: decryptedBytes, encoding: .utf8), !decryptedString.isEmpty {
                return decryptedString
            }
        }
        
        return string.removingPercentEncoding ?? string
    }
}
