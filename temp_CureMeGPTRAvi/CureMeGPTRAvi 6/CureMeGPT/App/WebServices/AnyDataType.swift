//
//  AnyDataType.swift
//  AWPL
//
//  Created by YATIN  KALRA on 13/06/25.
//

import Foundation

struct AnyDataType: Codable,Hashable {
    var value: String = ""

    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()

       
        if let stringValue = try? container.decode(String.self) {
            if let doubleValue = Double(stringValue) {
                if stringValue.contains(".") {
                    value = String(format: "%.2f", doubleValue)
                } else {
                    value = stringValue
                }
            } else {
                value = stringValue
            }
            return
            
        }
        if let intValue = try? container.decode(Int.self) {
            value = String(intValue)
            return
        }
        
        if let DoubleValue = try? container.decode(Double.self) {
            let roundedValue = String(format: "%.2f", DoubleValue)
            value = String(roundedValue)
            return
        }
      
        throw DecodingError.typeMismatch(
            AnyDataType.self,
            DecodingError.Context(
                codingPath: decoder.codingPath,
                debugDescription: "Expected String or Int for FlexibleString"
            )
        )
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(value)
    }
}
