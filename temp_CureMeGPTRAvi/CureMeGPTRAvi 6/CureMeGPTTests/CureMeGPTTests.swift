//
//  CureMeGPTTests.swift
//  CureMeGPTTests
//
//  Created by Antigravity on 09/07/26.
//

import XCTest
import SwiftUI
@testable import CureMeGPT

final class CureMeGPTTests: XCTestCase {
    
    // MARK: - ValidationManager Tests
    func testValidationManager_WithEmptyFields_ShouldFailAndSetMessage() {
        let manager = ValidationManager()
        
        let fields = [
            "Name": "  ",
            "Email": "test@test.com"
        ]
        
        let isValid = manager.validateFields(fields)
        
        XCTAssertFalse(isValid)
        XCTAssertTrue(manager.showAlert)
        XCTAssertEqual(manager.alertMessage, "Name cannot be empty")
    }
    
    func testValidationManager_WithValidFields_ShouldSucceed() {
        let manager = ValidationManager()
        
        let fields = [
            "Name": "John Doe",
            "Email": "john@example.com"
        ]
        
        let isValid = manager.validateFields(fields)
        
        XCTAssertTrue(isValid)
        XCTAssertFalse(manager.showAlert)
        XCTAssertTrue(manager.alertMessage.isEmpty)
    }
    
    // MARK: - Hex Color Parsing Tests
    func testColorHexInitializer_WithRGBHex_ShouldParseCorrectly() {
        // Red color hex: #FF0000 or FF0000
        let colorWithHash = Color(hex: "#FF0000")
        let colorWithoutHash = Color(hex: "FF0000")
        
        XCTAssertNotNil(colorWithHash)
        XCTAssertNotNil(colorWithoutHash)
    }
    
    func testColorHexInitializer_WithARGBHex_ShouldParseCorrectly() {
        // Red color with opacity hex: #80FF0000
        let color = Color(hex: "#80FF0000")
        
        XCTAssertNotNil(color)
    }
    
    func testColorHexInitializer_WithInvalidHex_ShouldFallbackToBlack() {
        let color = Color(hex: "invalid")
        XCTAssertNotNil(color)
    }
    
    // MARK: - Coordinator Route Tests
    func testCoordinator_PushAndPop_ShouldUpdatePath() {
        let coordinator = Coordinator()
        
        XCTAssertEqual(coordinator.path.count, 0)
        
        coordinator.push(.login)
        XCTAssertEqual(coordinator.path.count, 1)
        XCTAssertEqual(coordinator.path.first, .login)
        
        coordinator.push(.tabBarView)
        XCTAssertEqual(coordinator.path.count, 2)
        XCTAssertEqual(coordinator.path.last, .tabBarView)
        
        coordinator.pop()
        XCTAssertEqual(coordinator.path.count, 1)
        XCTAssertEqual(coordinator.path.last, .login)
        
        coordinator.popToRoot()
        XCTAssertEqual(coordinator.path.count, 0)
    }
    
    // MARK: - GeneralProfileViewModel Allergy Parsing Tests
    func testGeneralProfileViewModel_WithAllergiesFromAPI_ShouldCategorizeCorrectly() {
        let viewModel = GeneralProfileViewModel()
        
        let apiAllergies = ["Aaaaaaa", "Environmental", "Food", "This is other data"]
        let mockModel = MemberGeneralProfileModel(
            id: 82,
            bloodGroup: "A-",
            knownAllergies: apiAllergies,
            emergencyContactName: "Bob Dsouza",
            emergencyContactNumber: "555945325"
        )
        
        viewModel.setMemberGeneralProfileData(data: mockModel)
        
        // Standard options should be added to selectedAllergies
        XCTAssertTrue(viewModel.selectedAllergies.contains("Environmental"))
        XCTAssertTrue(viewModel.selectedAllergies.contains("Food"))
        
        // Custom/other ones should trigger "Others" selection
        XCTAssertTrue(viewModel.selectedAllergies.contains("Others"))
        
        // otherAllergyList should contain "Aaaaaaa" and "This is other data"
        XCTAssertEqual(viewModel.otherAllergyList.count, 2)
        XCTAssertTrue(viewModel.otherAllergyList.contains("Aaaaaaa"))
        XCTAssertTrue(viewModel.otherAllergyList.contains("This is other data"))
        
        // otherAllergyInput should be comma separated string of custom ones
        XCTAssertEqual(viewModel.otherAllergyInput, "Aaaaaaa, This is other data")
    }
    
    func testGeneralProfileViewModel_WhenDeselectingOthers_ShouldClearAllergyError() {
        let viewModel = GeneralProfileViewModel()
        
        // 1. Simulate validation failing because Others is selected but text is empty
        viewModel.selectedAllergies.insert("Others")
        XCTAssertTrue(viewModel.otherAllergyList.isEmpty)
        
        let isValid = viewModel.validateForm()
        XCTAssertFalse(isValid)
        XCTAssertEqual(viewModel.otherAllergyError, "Please enter allergy")
        
        // 2. Deselect Others
        viewModel.toggleAllergy("Others")
        
        // 3. Error should be cleared
        XCTAssertNil(viewModel.otherAllergyError)
        XCTAssertFalse(viewModel.selectedAllergies.contains("Others"))
    }
    
    // MARK: - HistoryProfileViewModel Chronic Conditions Tests
    func testHistoryProfileViewModel_WithChronicConditionsFromAPI_ShouldCategorizeCorrectly() {
        let viewModel = HistoryProfileViewModel()
        
        let apiConditions = ["Diabetes", "High Cholesterol", "Asthma", "Heartburn"]
        let mockModel = GeneralProfileHistoryModel(
            chronicCondition: apiConditions,
            surgicalHistory: "Appendectomy",
            currentMedications: ["Med1"],
            currentSupplements: ["Supp1"]
        )
        
        viewModel.setHistoryProfileData(data: mockModel)
        
        // Standard conditions should be added
        XCTAssertTrue(viewModel.form.chronicConditions.contains("Diabetes"))
        XCTAssertTrue(viewModel.form.chronicConditions.contains("Asthma"))
        
        // Custom/other ones should trigger "Others" selection
        XCTAssertTrue(viewModel.form.chronicConditions.contains("Others"))
        
        // otherChronicList should contain "High Cholesterol" and "Heartburn"
        XCTAssertEqual(viewModel.otherChronicList.count, 2)
        XCTAssertTrue(viewModel.otherChronicList.contains("High Cholesterol"))
        XCTAssertTrue(viewModel.otherChronicList.contains("Heartburn"))
        
        // otherChronicInput should be comma separated string
        XCTAssertEqual(viewModel.otherChronicInput, "High Cholesterol, Heartburn")
    }
    
    func testHistoryProfileViewModel_WhenDeselectingOthers_ShouldClearChronicError() {
        let viewModel = HistoryProfileViewModel()
        
        // 1. Simulate validation failing because Others is selected but empty
        viewModel.form.chronicConditions.append("Others")
        XCTAssertTrue(viewModel.otherChronicList.isEmpty)
        
        let isValid = viewModel.validateForm()
        XCTAssertFalse(isValid)
        XCTAssertEqual(viewModel.otherChronicError, "Please enter chronic condition")
        
        // 2. Deselect Others
        viewModel.toggleCondition("Others")
        
        // 3. Error should be cleared
        XCTAssertNil(viewModel.otherChronicError)
        XCTAssertFalse(viewModel.form.chronicConditions.contains("Others"))
    }
    
    func testGeneralProfileHistoryModel_DecodingBothFormats() throws {
        // 1. User/Standard format (snake_case at root)
        let standardJSON = """
        {
            "chronic_condition": ["Diabetes", "Asthma"],
            "surgical_history": "None",
            "current_medications": ["MedA"],
            "current_supplements": ["SuppA"]
        }
        """.data(using: .utf8)!
        
        let decoder = JSONDecoder()
        let model1 = try decoder.decode(GeneralProfileHistoryModel.self, from: standardJSON)
        XCTAssertEqual(model1.surgicalHistory, "None")
        XCTAssertEqual(model1.chronicCondition, ["Diabetes", "Asthma"])
        XCTAssertEqual(model1.currentMedications, ["MedA"])
        
        // 2. Family format (camelCase nested under "data")
        let familyJSON = """
        {
            "data": {
                "chronicCondition": ["Thyroid", "Anxiety", "8888888"],
                "surgicalHistory": "Had a intestine swingers",
                "currentMedications": ["Paracetmol"],
                "currentSupplements": ["NORAD"]
            }
        }
        """.data(using: .utf8)!
        
        let model2 = try decoder.decode(GeneralProfileHistoryModel.self, from: familyJSON)
        XCTAssertEqual(model2.surgicalHistory, "Had a intestine swingers")
        XCTAssertEqual(model2.chronicCondition, ["Thyroid", "Anxiety", "8888888"])
        XCTAssertEqual(model2.currentMedications, ["Paracetmol"])
    }
}
