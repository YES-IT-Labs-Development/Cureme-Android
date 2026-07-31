//
//  DropDownView.swift
//  CureMeGpt APP
//
//  Created by YES IT Labs on 21/11/25.
//

import SwiftUI
import Foundation

struct OptionItem: Identifiable, Hashable {
    let id = UUID()
    let title: String
}

struct PersonalInfoModel {
    var selectedServiceSize: OptionItem?
    var selectedGender: OptionItem?
    var dob: Date = Date()
}


struct DropdownView: View {
    let title: String
    let items: [OptionItem]
    @Binding var selectedItem: OptionItem?
    @State private var isOpen = false
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            
            // LABEL
            Text(title)
                .font(.system(size: 14, weight: .semibold))
            
            // SELECTED FIELD
            Button {
                withAnimation { isOpen.toggle() }
            } label: {
                HStack {
                    Text(selectedItem?.title ?? "Select \(title)")
                        .foregroundColor(selectedItem == nil ? .gray : .black)
                        .font(.custom("PlusJakartaSans-Regular", size: 16))
                    
                    Spacer()
                    Image(isOpen ? "UPIcon" : "DownIcon")
                        .foregroundColor(.gray)
                }
                .padding()
                .background(
                    RoundedRectangle(cornerRadius: 30)
                        .stroke(Color.gray.opacity(0.25), lineWidth: 1)
                )
            }
            
            // WHEN OPEN → LIST OF OPTIONS
            if isOpen {
                VStack(spacing: 0) {
                    ForEach(items) { item in
                        Button {
                            withAnimation {
                                selectedItem = item
                                isOpen = false
                            }
                        } label: {
                            HStack {
                                Text(item.title)
                                    .foregroundColor(.black)
                                    .font(.custom("PlusJakartaSans-Regular", size: 16))
                                Spacer()
                            }
                            .padding()
                            
                        }
                        Divider()
                            .cornerRadius(30)
                    }
                }
                .background(
                    RoundedRectangle(cornerRadius: 14)
                        .stroke(Color.gray.opacity(0.2))
                        .background(Color.gray.opacity(0.2))
                )
                .shadow(radius: 4)
                .cornerRadius(30)
            }
        }
        .animation(.easeInOut, value: isOpen)
    }
}
