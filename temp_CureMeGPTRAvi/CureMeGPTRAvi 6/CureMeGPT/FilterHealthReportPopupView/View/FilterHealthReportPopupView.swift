//
//  FilterHealthReportPopupView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 07/01/26.
//
//
import SwiftUI

import SwiftUI

struct FilterHealthReportPopupView: View {

    @ObservedObject var viewModel: FilterHealthReportPopupViewModel
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject var tabVM: TabViewModel
    
    var onApply: (FilterHealthReportPopupViewModel) -> Void

    var body: some View {
        VStack(spacing: 20) {

            Capsule()
                .fill(Color(hex: "#D9D9D9"))
                .frame(width: 82, height: 4)
                .padding(.top, 8)

            Text("Filter Health Reports")
                .font(.custom("Urbanist-Medium", size: 18))
                .foregroundColor(Color(hex: "#374151"))
            
            Divider()

            // Member Dropdown
            VStack(alignment: .leading, spacing: 10) {
                Text("Member")
                    .font(.custom("Urbanist-Medium", size: 15))

                Menu {
                    ForEach(viewModel.membersList, id: \.id) { member in
                        Button {
                            viewModel.selectedMember = member
                        } label: {
                            HStack {
                                Text("\(member.name) (\(member.relation))")
                                    .foregroundColor(
                                        viewModel.selectedMember?.id == member.id
                                        ? Color(hex: "#211C64")   // selected color
                                        : .black
                                    )

                                Spacer()

                                if viewModel.selectedMember?.id == member.id {
                                    Image("Checkmark")   // ✅ show tick
                                        .resizable()
                                        .frame(width: 18, height: 18)
                                }
                            }
                        }
                    }
                } label: {
                    HStack {
//                        Text((viewModel.selectedMember?.name.isEmpty == false
//                              ? ("\(viewModel.selectedMember!.name)\(viewModel.selectedMember!.relation)")
//                              : "Select") ?? "")
//                            .foregroundColor(Color(hex: "#697383"))
                        
                        Text(
                            viewModel.selectedMember.map {
                                "\($0.name) (\($0.relation))"
                            } ?? "Select"
                        )
                        .foregroundColor(Color(hex: "#697383"))
                        
                        Spacer()
                        Image("Down")
                    }
                    .padding()
                    .overlay(
                        RoundedRectangle(cornerRadius: 56)
                            .stroke(lineWidth: 0.6)
                            .foregroundColor(Color(hex: "#697383"))
                    )
                    .cornerRadius(56)
                }
            }

            // Buttons
            HStack(spacing: 12) {
                Button(action:{
                    dismiss()
                }){
                    Text("Cancel")
                        .foregroundColor(.black)
                }
                .frame(width: 100, height: 55)
                //.padding()
                .overlay(
                    RoundedRectangle(cornerRadius: 45)
                        .stroke(lineWidth: 1)
                        .foregroundColor(.black)
                    )
                .cornerRadius(45)

                Button(action:{
                    onApply(viewModel)
                    dismiss()
                }){
                   Text("Apply")
                  
                }
                .frame(maxWidth: .infinity)
                .frame(height: 55)
                //.padding()
                .background(
                    LinearGradient(
                        colors: [
                            Color(red: 67/255, green: 56/255, blue: 202/255),
                            Color(red: 33/255, green: 28/255, blue: 100/255)
                        ],
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                )
                .cornerRadius(45)
                .foregroundColor(.white)
            }
            .padding(.top, 10)

        }
        .padding()
        .presentationDetents([.height(360)])
        .presentationCornerRadius(24)
        .onAppear {
            viewModel.userWithFamilyDetailsAPI { success in
                print("Members loaded: \(success)")
            }
        }
    }

    private func filterRow(
        title: String,
        isSelected: Bool,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            HStack {
                Text(title)
                    .font(.custom("Urbanist-Medium", size: 16))
                    .foregroundColor(
                        isSelected
                        ? Color(hex: "#211C64")   // selected color
                        : Color(hex: "#374151")   // normal color
                    )

                Spacer()

                if isSelected {
                    Image("Checkmark")
                        .resizable()
                        .frame(width: 24, height: 24)
                }
            }
            .contentShape(Rectangle())
        }
    }
}



