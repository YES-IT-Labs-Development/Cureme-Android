//
//  FilterMembersView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 31/12/25.
//


import SwiftUI

struct FilterMembersView: View {

    @ObservedObject var viewModel: FilterAppointmentViewModel
    @Environment(\.dismiss) private var dismiss
    var onSelect: (FamilyDetail) -> Void

    var body: some View {
        VStack(spacing: 16) {

            Capsule()
                .fill(Color(hex: "#D9D9D9"))
                .frame(width: 80, height: 5)
                .padding(.top, 8)

            Text("Filter Family Members")
                .font(.custom("Urbanist-Medium", size: 18))
                .foregroundColor(Color(hex: "#374151"))

            Divider()

            // SCROLLABLE LIST
            ScrollView {
                VStack(spacing: 16) {
                    ForEach(viewModel.membersListDetails, id: \.id) { member in
                        memberRow(member)
                    }
                }
                .padding(.horizontal)
                .padding(.bottom, 20)
            }
        }
        .frame(height: 400) // FIXED HEIGHT
        .padding(.horizontal)
        .presentationDetents([.height(400)]) // ✅ FIXED SHEET HEIGHT
        .presentationCornerRadius(24)

        .onAppear {
            viewModel.userWithFamilyDetailsAPI{ success in
                print(success ? "Members loaded" : "Failed")
            }
        }
    }

    // MARK: - Member Row
    private func memberRow(_ member: FamilyDetail) -> some View {
        let isSelected = viewModel.selectedMemberDetail?.id == member.id

        return Button {
            viewModel.selectedMemberDetail = member

            // FILTER APPLY
            onSelect(member)

            // SHEET CLOSE
            dismiss()

        } label: {
            HStack {
                Text("\(member.name ?? "") (\(member.relationship ?? ""))")
                    .font(.custom("Urbanist-Medium", size: 18))
                    .foregroundColor(
                        isSelected
                        ? Color(hex: "#4338CA")
                        : Color(hex: "#374151")
                    )

                Spacer()

                if isSelected {
                    Image("Checkmark")
                        .resizable()
                        .frame(width: 24, height: 24)
                }
            }
            .padding(.vertical, 6)
        }
    }
}
