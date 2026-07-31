//
//  FilterMembersViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 31/12/25.
//

import Foundation

final class FilterMembersViewModel: ObservableObject {

    @Published var members: [FilterMembersModel] = [
        FilterMembersModel(id: UUID(), name: "James", relation: "Myself"),
        FilterMembersModel(id: UUID(), name: "Rose Logan", relation: "Spouse"),
        FilterMembersModel(id: UUID(), name: "Peter Logan", relation: "Son")
    ]

    @Published var selectedMemberID: UUID?

    func select(_ member: FilterMembersModel) {
        selectedMemberID = member.id
    }

    func isSelected(_ member: FilterMembersModel) -> Bool {
        selectedMemberID == member.id
    }
}
