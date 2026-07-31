//
//  FilterMembersCategoryView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 31/12/25.
//

import SwiftUI

struct FilterMembersCategoryView: View {
    
    @ObservedObject var viewModel: FilterAppointmentViewModel //FilterMembersCategoryViewModel
    @Environment(\.dismiss) private var dismiss
    
    var onSelect: (FamilyDetail) -> Void
    
    var body: some View {
            VStack(spacing: 0) {

                // Drag indicator
                Capsule()
                    .fill(Color.gray.opacity(0.3))
                    .frame(width: 40, height: 5)
                    .padding(.top, 8)

                // Title
                Text("Filter Family Members")
                    .font(.custom("Urbanist-Medium", size: 18))
                    .padding(.top, 12)

                Divider()
                    .padding(.vertical, 10)

                // Scrollable list
                ScrollView {
                    VStack(spacing: 8) {
//                        ForEach(viewModel.membersListDetails) { member in
//                            memberRow(member)
//                        }
                        
                        ForEach(Array(viewModel.membersListDetails.enumerated()), id: \.offset) { _, member in
                                   memberRow(member)
                               }

                    }
                    .padding(.horizontal)
                    .padding(.bottom, 20)
                }
                .disableScrollBounce()
            }

                .onAppear {
                    viewModel.userWithFamilyDetailsAPI { success in
                        print("membersList =", viewModel.membersList.count)
                        print("membersListDetails =", viewModel.membersListDetails.count)
                    }
                }
            
        .padding()
        .presentationDetents([.height(380)])
        .presentationCornerRadius(24)
        .background(Color.white)
        .cornerRadius(24)
    }
    

    
    // MARK: - Member Row
    private func memberRow(_ member: FamilyDetail) -> some View {
       // let isSelected = viewModel.selectedMemberDetail?.id == member.id
        let isSelected =
            viewModel.selectedMemberDetail?.name == member.name &&
            viewModel.selectedMemberDetail?.relationship == member.relationship

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
