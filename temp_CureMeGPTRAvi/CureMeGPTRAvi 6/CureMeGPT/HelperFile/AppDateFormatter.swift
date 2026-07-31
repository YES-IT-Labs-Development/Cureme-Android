//
//  AppDateFormatter.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 27/07/26.
//

import Foundation

/// Standardized Date Formats across the CureMeGPT Application
enum AppDateFormatStyle {
    /// Format: `24 July 2026`
    case fullMonth
    /// Format: `24 - 07 - 2026`
    case dashed
    /// Format: `24 Jul 2026`
    case shortMonth
    
    var dateFormatString: String {
        switch self {
        case .fullMonth:
            return "dd MMMM yyyy"
        case .dashed:
            return "dd - MM - yyyy"
        case .shortMonth:
            return "dd MMM yyyy"
        }
    }
}

struct AppDateFormatter {
    static let shared = AppDateFormatter()
    
    private let commonInputFormats = [
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd",
        "MM-dd-yyyy",
        "dd-MM-yyyy",
        "dd/MM/yyyy",
        "yyyy/MM/dd",
        "E, dd MMM yyyy HH:mm:ss Z"
    ]
    
    /// Formats a Date object to the app's standard date format
    static func format(date: Date, style: AppDateFormatStyle = .fullMonth) -> String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = style.dateFormatString
        return formatter.string(from: date)
    }
    
    /// Parses any common raw date string and returns formatted app date string
    static func format(string: String, style: AppDateFormatStyle = .fullMonth) -> String {
        let trimmed = string.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.isEmpty { return "" }
        
        let parser = DateFormatter()
        parser.locale = Locale(identifier: "en_US_POSIX")
        
        for inputFormat in shared.commonInputFormats {
            parser.dateFormat = inputFormat
            if let parsedDate = parser.date(from: trimmed) {
                return format(date: parsedDate, style: style)
            }
        }
        
        // If unparseable, return original trimmed string
        return trimmed
    }
}

// MARK: - Extensions for Convenient Usage
extension Date {
    /// Formats Date to App Standard (Default: `24 July 2026`)
    func toAppDateString(style: AppDateFormatStyle = .fullMonth) -> String {
        return AppDateFormatter.format(date: self, style: style)
    }
}

extension String {
    /// Formats Date String to App Standard (Default: `24 July 2026`)
    func toAppDateString(style: AppDateFormatStyle = .fullMonth) -> String {
        return AppDateFormatter.format(string: self, style: style)
    }
}
