//
//  HomeScreenViewModel.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/12/25.
//

import SwiftUI
import Combine

final class HomeHealthViewModel: ObservableObject {

    // MARK: - Mood
    @Published var selectedMoodID: UUID?
    @Published var isVisible: Bool = true
    @Published var selectedMemberID: String?
   
    private var cancellables = Set<AnyCancellable>()

    let moods: [MoodOption] = [
        MoodOption(emojiAsset: "Emojy 4", title: "Low"),
        MoodOption(emojiAsset: "Emojy 1", title: "Down"),
        MoodOption(emojiAsset: "Emojy 2", title: "Neutral"),
        MoodOption(emojiAsset: "Emojy 3", title: "Good"),
        MoodOption(emojiAsset: "Emojy 5", title: "Great")
    ]

    @Published var isAnimatingReaction: Bool = false
    @Published var moodTitle: String? = nil
    @Published var moodSummary: String? = nil
    @Published var isLoadingMood: Bool = false

    private let moodDateKey = "saved_daily_mood_date"
    private let moodTitleKey = "saved_daily_mood_title"
    private let moodSummaryTitleKey = "saved_daily_mood_summary_title"
    private let moodSummaryTextKey = "saved_daily_mood_summary_text"

    init() {
        checkAndLoadSavedTodayMood()
    }

    private var todayDateString: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: Date())
    }

    func checkAndLoadSavedTodayMood() {
        let savedDate = UserDefaults.standard.string(forKey: moodDateKey)
        if savedDate == todayDateString {
            if let savedTitle = UserDefaults.standard.string(forKey: moodTitleKey),
               let matched = moods.first(where: { $0.title.lowercased() == savedTitle.lowercased() }) {
                self.selectedMoodID = matched.id
                self.isAnimatingReaction = false
                self.moodTitle = UserDefaults.standard.string(forKey: moodSummaryTitleKey)
                self.moodSummary = UserDefaults.standard.string(forKey: moodSummaryTextKey)
                self.isLoadingMood = false
            }
        } else if savedDate != nil {
            // New day: reset saved mood
            clearSavedMood()
        }
    }

    func saveTodayMood(moodTitleString: String, summaryTitle: String?, summaryText: String?) {
        UserDefaults.standard.set(todayDateString, forKey: moodDateKey)
        UserDefaults.standard.set(moodTitleString, forKey: moodTitleKey)
        if let sTitle = summaryTitle { UserDefaults.standard.set(sTitle, forKey: moodSummaryTitleKey) }
        if let sText = summaryText { UserDefaults.standard.set(sText, forKey: moodSummaryTextKey) }
    }

    func clearSavedMood() {
        UserDefaults.standard.removeObject(forKey: moodDateKey)
        UserDefaults.standard.removeObject(forKey: moodTitleKey)
        UserDefaults.standard.removeObject(forKey: moodSummaryTitleKey)
        UserDefaults.standard.removeObject(forKey: moodSummaryTextKey)
        selectedMoodID = nil
        isAnimatingReaction = false
        moodTitle = nil
        moodSummary = nil
        isLoadingMood = false
    }

    var selectedMood: MoodOption? {
        moods.first(where: { $0.id == selectedMoodID })
    }

    func selectMood(_ mood: MoodOption) {
        selectedMoodID = mood.id
        isAnimatingReaction = true
        moodTitle = nil
        moodSummary = nil
        isLoadingMood = true
        
        saveTodayMood(moodTitleString: mood.title, summaryTitle: nil, summaryText: nil)
        fetchTodayMood(mood.title.lowercased())
        
        // Show snappy reaction pop animation (0.45s), then transition immediately to compact view
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.45) {
            withAnimation(.spring(response: 0.35, dampingFraction: 0.7)) {
                self.isAnimatingReaction = false
            }
        }
    }

    func fetchTodayMood(_ moodText: String) {
        isLoadingMood = true
        APIServices<MoodDataModel>()
            .post(endpoint: .get_today_mood, parameters: ["today_mood": moodText])
            .sink { [weak self] completion in
                DispatchQueue.main.async {
                    self?.isLoadingMood = false
                }
                if case .failure(let err) = completion {
                    print("get_today_mood API Error: \(err.localizedDescription)")
                }
            } receiveValue: { [weak self] response in
                DispatchQueue.main.async {
                    self?.isLoadingMood = false
                    if let data = response.data {
                        self?.moodTitle = data.title
                        self?.moodSummary = data.summary
                        self?.saveTodayMood(moodTitleString: moodText, summaryTitle: data.title, summaryText: data.summary)
                        print("Fetched Mood Title: \(data.title ?? ""), Summary: \(data.summary ?? "")")
                    }
                }
            }
            .store(in: &cancellables)
    }

    func close() {
        isVisible = false
    }

    // MARK: - Profile Completion
    @Published var completion: Double = 0.0
    @Published var medications: [String] = []
    @Published var allergies: [String] = []
    @Published var steps: [String] = []
    @Published var showProfileCompletionPopup = false
    private var hasCheckedProfileCompletion = false

    @Published var isLoading = false
    @Published var errorMessage: String?
    
    @Published var familyMembers: [FamilyMember] = []
    
    @Published var homeData: HomeDataModel?
    
    private var hasLoaded = false
    
    func fetchHomeDataIfNeeded(completion: @escaping (Bool) -> Void) {
//        guard !hasLoaded else { return }
//        hasLoaded = true
        fetchHomeData(completion: completion)
    }
    
    
    func fetchHomeData(completion: @escaping (Bool) -> Void) {

        isLoading = true
        errorMessage = nil

        APIManager.shared.getHomeApi()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                
                guard let self = self else { return }
                self.isLoading = false

                if case .failure(let error) = result {
                    self.errorMessage = error.localizedDescription

                    if self.errorMessage?.contains("no local endpoint") == true {
                        self.errorMessage = "Internal Server Error.\nPlease try again."
                    }

                    completion(false)
                }

            } receiveValue: { [weak self] response in
                
                guard let self = self else { return }
                self.isLoading = false

                if response.success ?? false {
                    
                    guard let data = response.data else {
                        completion(false)
                        return
                    }

                    print("Home Data:", data)
                    
                    self.homeData = data
                    self.setHomeData(data)
                    
                    let completionPct = data.userContext?.profileCompletion ?? 0
                    if completionPct < 100 && !self.hasCheckedProfileCompletion {
                        DispatchQueue.main.async {
                            withAnimation {
                                self.showProfileCompletionPopup = true
                            }
                        }
                        self.hasCheckedProfileCompletion = true
                    }
                    
                    completion(true)

                } else {
                    self.errorMessage = response.message
                    completion(false)
                }
            }
            .store(in: &cancellables)
    }
    
    func setHomeData(_ data: HomeDataModel) {

        // MARK: - User Info
        if let user = data.userContext {
            self.completion = Double(user.profileCompletion ?? 0) / 100
        }

        // MARK: - Health Summary
        self.medications = data.healthSummary?.currentMedications ?? []
        self.allergies = data.healthSummary?.allergies ?? []
        
        
       

        // MARK: - Steps
        self.steps = data.recommendedNextSteps ?? []

        // MARK: - Alerts
        var tempAlerts: [HealthAlert] = []

        // Myself
        data.thingsNeedAttention?.myself?.forEach {
            tempAlerts.append(
                HealthAlert(
                    title: $0,
                    subtitle: "Self",
                    type: .critical
                )
            )
        }

        // Family
        data.thingsNeedAttention?.family?.forEach { family in
            family.symptoms?.forEach { symptom in
                tempAlerts.append(
                    HealthAlert(
                        title: symptom,
                        subtitle: family.name ?? "",
                        type: .warning
                    )
                )
            }
        }

        self.alerts = tempAlerts

        // MARK: - Family Members
        var members: [FamilyMember] = []

        if let me = data.membersDetails?.myself {
            members.append(
                mapMember(me, relation: "Self")
            )
        }

        data.membersDetails?.family?.forEach { familyMember in
            let rel = (familyMember.relationship?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty == false) ? familyMember.relationship! : "Family"
            members.append(
                mapMember(familyMember, relation: rel)
            )
        }
        
        // Set default selected member
        self.selectedMemberIndex = 0
        self.selectedMemberID = "\(members.first?.idNumber ?? 0)"
        print("Initially selected id:- ",self.selectedMemberID ?? "0")
      //  UserDetail.shared.setID("\(members.first?.idNumber ?? 0)")
       
        self.familyMembers = members
       
    }
    
    private func mapMember(_ member: Myself, relation: String) -> FamilyMember {

        return FamilyMember(
            idNumber: member.id ?? 0,
            name: member.name ?? "",
            dob:    member.dob ?? "",
            relation: relation,
            imageName: member.profileImage ?? "",
            lastCheckupDays: member.lastAppointmentDaysAgo ?? 0,
            alerts: mapMemberAlerts(member)
        )
    }
    
//    private func mapMemberAlerts(_ member: Myself) -> [HealthAlert]
    
    private func mapMemberAlerts(_ member: Myself) -> [HealthAlert] {

        var alerts: [HealthAlert] = []
        
        // Medication Alerts
        member.activeAlerts?.medications?.forEach { medication in
            let medicationName = medication.medicationName ?? "Medication"
            
            alerts.append(
                HealthAlert(
                    title: "\(medicationName) Reminder",
                    subtitle: member.name ?? "",
                    type: .warning
                )
            )
        }

        // Appointment Alerts
        member.activeAlerts?.appointments?.forEach { appointment in
            let appointmentName = appointment.appointmentType ?? "Appointment"
            
            alerts.append(
                HealthAlert(
                    title: "Appointment: \(appointmentName) Due",
                    subtitle: member.name ?? "",
                    type: .critical
                )
            )
        }

//        // Medication Alerts
//        member.activeAlerts?.medications?.forEach { medication in
//            alerts.append(
//                HealthAlert(
//                    title: medication.medicationName ?? "Medication Reminder",
//                    subtitle: member.name ?? "",
//                    type: .warning
//                )
//            )
//        }
//
//        // Appointment Alerts
//        member.activeAlerts?.appointments?.forEach { appointment in
//            alerts.append(
//                HealthAlert(
//                    title: appointment.description ?? "Appointment Reminder",
//                    subtitle: member.name ?? "",
//                    type: .critical
//                )
//            )
//        }

        return alerts
    }
    
    // MARK: - Upcoming Appointment Calculation
    func getUpcomingAppointmentText() -> String {
        guard let selectedIDStr = selectedMemberID,
              let selectedID = Int(selectedIDStr),
              let homeData = homeData else {
            return "No Appointments"
        }
        
        var dateStrings: [String] = []
        
        // 1. Check if selected member is "Myself"
        if let myself = homeData.membersDetails?.myself, myself.id == selectedID {
            dateStrings = myself.activeAlerts?.appointments?.compactMap { $0.date } ?? []
        }
        // 2. Check if selected member is in "Family"
        else if let familyMember = homeData.membersDetails?.family?.first(where: { $0.id == selectedID }) {
            dateStrings = familyMember.activeAlerts?.appointments?.compactMap { $0.date } ?? []
        }
        
        // Agar koi appointment nahi hai
        guard !dateStrings.isEmpty else {
            return "No Appointments"
        }
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM-dd-yyyy" // JSON ka date format
        let today = Calendar.current.startOfDay(for: Date())
        
        var upcomingDays: [Int] = []
        
        // Sabhi dates ka difference nikalna
        for dateStr in dateStrings {
            if let apptDate = dateFormatter.date(from: dateStr) {
                let startOfAppt = Calendar.current.startOfDay(for: apptDate)
                let components = Calendar.current.dateComponents([.day], from: today, to: startOfAppt)
                
                if let days = components.day, days >= 0 {
                    upcomingDays.append(days)
                }
            }
        }
        
        // Sabse nazdeek wali appointment (closest) ko sort karke nikalna
        if let closest = upcomingDays.sorted().first {
            if closest == 0 {
                return "Appointment Today"
            } else if closest == 1 {
                return "Appointment in 1 Day"
            } else {
                return "Appointment in \(closest) Days"
            }
        }
        
        return "No Upcoming Appts"
    }


    func fetchProfileOverview() {
        isLoading = true

        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            let mockResponse = ProfileOverviewResponse(
                profileCompletion: 50,
                currentMedications: ["Lisinopril 10mg", "Vitamin D"],
                allergies: ["Penicillin", "Shellfish"],
                recommendedSteps: [
                    "Set a reminder for your blood pressure medication",
                    "Schedule your annual checkup",
                    "Complete emergency contact information"
                ]
            )

            self.completion = Double(mockResponse.profileCompletion) / 100
            self.medications = mockResponse.currentMedications
            self.allergies = mockResponse.allergies
            self.steps = mockResponse.recommendedSteps
            self.isLoading = false
        }
    }

    // MARK: - Alerts
    @Published var alerts: [HealthAlert] = [
        HealthAlert(
            title: "Tooth Pain Symptoms Detected",
            subtitle: "James Logan",
            type: .critical
        ),
        HealthAlert(
            title: "Overdue Dental Cleaning",
            subtitle: "Rosy Logan",
            type: .warning
        )
    ]

    // MARK: - Health Overview Cards
    @Published var overviewItems: [HealthOverviewItem] = [
        HealthOverviewItem(
            title: "Active Alerts",
            subtitle: "3",
            icon: "exclamationmark.triangle.fill"
        ),
        HealthOverviewItem(
            title: "Blood pressure medication reminder",
            subtitle: "",
            icon: "heart.fill"
        ),
        HealthOverviewItem(
            title: "Annual checkup due",
            subtitle: "",
            icon: "calendar"
        )
    ]

 // MARK: - Family Health Overview
    @Published var selectedMemberIndex: Int = 0

    var selectedMember: FamilyMember? {
        guard familyMembers.indices.contains(selectedMemberIndex) else { return nil }
        return familyMembers[selectedMemberIndex]
    }
}


