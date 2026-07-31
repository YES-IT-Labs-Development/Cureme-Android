//
//  FilterMembersCategoryViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 31/12/25.
//

import Foundation

final class FilterMembersCategoryViewModel: ObservableObject {
    @Published var members: [FilterMembersCategoryModel] = [
        FilterMembersCategoryModel(name: "AllMembers"),
        FilterMembersCategoryModel(name: "Children"),
        FilterMembersCategoryModel(name: "Adults"),
        FilterMembersCategoryModel(name: "Seniors"),
        FilterMembersCategoryModel(name: "Friends"),
        FilterMembersCategoryModel(name: "Relatives")
    ]
    
    @Published var selectedMember: FilterMembersCategoryModel?
    
    func select(_ member: FilterMembersCategoryModel) {
        selectedMember = member
    }
    
    func isSelected(_ member: FilterMembersCategoryModel) -> Bool {
        selectedMember?.id == member.id
    }
}
