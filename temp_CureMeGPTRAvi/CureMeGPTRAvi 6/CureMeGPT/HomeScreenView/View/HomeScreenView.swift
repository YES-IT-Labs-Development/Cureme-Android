//
//  HomeScreenView.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 29/12/25.
//

import SwiftUI
import SDWebImageSwiftUI

struct HomeHealthView: View {
    
    @StateObject private var viewModel = HomeHealthViewModel()
    @EnvironmentObject private var coordinator: Coordinator
    @EnvironmentObject private var tabVM: TabViewModel
    
    var body: some View {
        ZStack {
            
            VStack {
                HomeHeaderView()
                
                ScrollView {
                    mainContent
                }
                .disableScrollBounce()
            }
            .onAppear {
                tabVM.isTabBarHidden = false
            }
            .padding(.bottom, 20)
            
            // ✅ LOADER ON TOP
            if viewModel.isLoading {
                ZStack {
                    Color.black.opacity(0.3)
                        .ignoresSafeArea()
                    
                    CustomLoderView(isVisible: $viewModel.isLoading)
                }
                .zIndex(999) // 🔥 important
            }
            
            // ✅ Profile Completion Popup
            if viewModel.showProfileCompletionPopup {
                ProfileCompletionPopupView(viewModel: viewModel)
                    .zIndex(100)
            }
        }
    }
    
    private var mainContent: some View {
        VStack(spacing: 20) {
            //  HeaderView()
            HeaderView(viewModel: viewModel)
            MoodCheckView(viewModel: viewModel)
            ProfileCompletionView(viewModel: viewModel)
            AlertsSection(alerts: viewModel.alerts)
            
            HealthFamilyOverviewSection(viewModel: viewModel)
            
        }
        .padding(.bottom, 20)
    }
}


// MARK: - Header

struct HeaderView: View {
    @ObservedObject var viewModel: HomeHealthViewModel
    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                HStack{
                    Text("Hello")
                        .font(.custom("Urbanist-Medium", size: 36))
                        .foregroundColor(Color(hex: "#000000"))
                    
                    // Text("James")
                    Text(viewModel.homeData?.userContext?.name ?? "User")
                        .foregroundColor(Color(hex: "#4338CA"))
                        .font(.custom("Urbanist-Medium", size: 36))
                        .foregroundColor(Color(hex: "#4338CA"))
                }
                
                Text("Here's your health overview for today")
                    .font(.custom("Urbanist-Regular", size: 18))
                    .foregroundColor(Color(hex: "#000000"))
            }
            Spacer()
        }
        .padding()
    }
}

struct HomeHeaderView: View {
    @EnvironmentObject private var coordinator: Coordinator
    
    var body: some View {
        HStack {
            // MARK: - Left Logo Section
            HStack(spacing: 8) {
                Image("HomeLogo")
                    .resizable()
                    .frame(width: 30, height: 30)
                    .foregroundColor(.purple)
                
                HStack(spacing: 0){
                    Text("CureMe")
                        .font(.custom("Urbanist-SemiBold", size: 22))
                        .foregroundColor(Color(hex: "#4338CA"))
                    
                    Text("GPT")
                        .font(.custom("Urbanist-SemiBold", size: 22))
                        .foregroundColor(Color(hex: "#3C3C3C"))
                }
            }
            
            Spacer()
            
            // MARK: - Right Actions Section
            HStack(spacing: 12) {
                // Notification Button
                Button(action: {
                    coordinator.push(.alertView)
                }) {
                    Image("Close Button")
                    //.foregroundColor(.purple)
                        .frame(width: 45, height: 45)
                        .background(
                            Circle()
                                .fill(Color.purple.opacity(0.1))
                        )
                }
                
                // Profile Section
                Button(action: {
                    coordinator.push(.personProfileView)
                }) {
                    HStack(spacing: 6) {
                        
                        let img = "\(UserDetail.shared.getProfileImg())"
                        
                        if let url = URL(string: img){
                            WebImage(url: url)
                                .resizable()
                                .indicator(.activity)
                                .scaledToFill()
                                .frame(width: 42, height: 42)
                                .clipShape(Circle())
                        } else {
                            Image("Frame 1272638625") // Static placeholder image
                                .resizable()
                                .scaledToFill()
                                .frame(width: 42, height: 42)
                                .clipShape(Circle())
                        }
                        Image(systemName: "chevron.right")
                            .font(.system(size: 14, weight: .semibold))
                            .foregroundColor(Color(hex: "#211C64"))
                    }
                    
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(
                        Capsule()
                            .fill(Color(hex: "#EBE1FF"))
                    )
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 10)
        .background(Color.white)
    }
}

// MARK: - Mood Check

struct MoodCheckView: View {
    @ObservedObject var viewModel: HomeHealthViewModel
    
    var body: some View {
        Group {
            if viewModel.isVisible {
                content
                    .transition(.scale.combined(with: .opacity))
            }
        }
    }
    
    private var content: some View {
        VStack(spacing: 16) {
            header
            
            if viewModel.selectedMoodID != nil && !viewModel.isAnimatingReaction, let selectedMood = viewModel.selectedMood {
                selectedOnlyRow(selectedMood)
            } else {
                moodsRow
                if viewModel.selectedMoodID == nil {
                    skipButton
                }
            }
        }
        .padding()
        .background(Color(hex: "#4338CA"))
        .cornerRadius(22)
        .padding(.horizontal)
    }
    
    private var header: some View {
        HStack {
            Image("Frame 12726386263333")
                .resizable()
                .frame(width: 42, height: 42)
            Text("Daily Mood Check")
                .font(.custom("Urbanist-Medium", size: 18))
                .foregroundColor(.white)
            
            Spacer()
            
            Button(action: {
                withAnimation(.easeInOut(duration: 0.25)) {
                    viewModel.close()
                }
            }) {
                Image("Cancel")
                    .resizable()
                    .frame(width: 42, height: 42)
            }
        }
    }
    
    private func selectedOnlyRow(_ mood: MoodOption) -> some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack(spacing: 14) {
                // Selected Emoji on Left with unique mood-based micro-animation
                SelectedEmojiAnimatedView(moodTitle: mood.title, emojiAsset: mood.emojiAsset)
                
                VStack(alignment: .leading, spacing: 2) {
                    Text("Selected Mood")
                        .font(.custom("Urbanist-Medium", size: 13))
                        .foregroundColor(.white.opacity(0.8))
                    
                    Text(mood.title)
                        .font(.custom("Urbanist-Bold", size: 20))
                        .foregroundColor(.white)
                }
                
                Spacer()
                
                Button(action: {
                    withAnimation(.spring(response: 0.35, dampingFraction: 0.65)) {
                        viewModel.clearSavedMood()
                    }
                }) {
                    Text("Change")
                        .font(.custom("Urbanist-Medium", size: 14))
                        .foregroundColor(.white)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 8)
                        .background(Color.white.opacity(0.2))
                        .cornerRadius(16)
                }
            }
            
            // API Response: Loader or Title & Summary
            if viewModel.isLoadingMood {
                HStack(spacing: 12) {
                    Spacer()
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        .scaleEffect(1.15)
                    
                    Text("Loading mood insights...")
                        .font(.custom("Urbanist-Medium", size: 14))
                        .foregroundColor(.white.opacity(0.9))
                    Spacer()
                }
                .padding(14)
                .background(Color.white.opacity(0.12))
                .cornerRadius(16)
                .transition(.opacity)
            } else if viewModel.moodTitle != nil || viewModel.moodSummary != nil {
                VStack(alignment: .leading, spacing: 8) {
                    if let title = viewModel.moodTitle {
                        Text(title)
                            .font(.custom("Urbanist-Bold", size: 18))
                            .foregroundColor(.white)
                    }
                    
                    if let summary = viewModel.moodSummary {
                        Text(summary)
                            .font(.custom("Urbanist-Regular", size: 14))
                            .foregroundColor(.white.opacity(0.95))
                            .fixedSize(horizontal: false, vertical: true)
                            .lineSpacing(4)
                    }
                }
                .padding(.top, 4)
                .frame(maxWidth: .infinity, alignment: .leading)
                .transition(.opacity.combined(with: .move(edge: .top)))
            }
        }
        .padding(.horizontal, 4)
        .transition(.scale.combined(with: .opacity))
    }
    
    private var moodsRow: some View {
        HStack(spacing: 4) {
            ForEach(viewModel.moods) { mood in
                let isSelected = viewModel.selectedMoodID == mood.id
                MoodEmojiView(
                    mood: mood,
                    isSelected: isSelected
                ) {
                    withAnimation(.spring(response: 0.35, dampingFraction: 0.65)) {
                        viewModel.selectMood(mood)
                    }
                }
            }
        }
    }
    
    private var skipButton: some View {
        Button(action: {
            withAnimation(.easeInOut(duration: 0.25)) {
                viewModel.close()
            }
        }) {
            Text("Skip for Now")
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundColor(.white.opacity(0.85))
        }
    }
}

// MARK: - Mood Animation Style Enum
enum MoodAnimStyle {
    case heartbeat      // Low
    case pendulum       // Down
    case floatingWave   // Neutral
    case joyfulHop      // Good
    case energyPulse    // Great
}

// MARK: - Selected Animated Emoji (Unique Animation Per Mood)
struct SelectedEmojiAnimatedView: View {
    let moodTitle: String
    let emojiAsset: String
    
    @State private var isAnimating: Bool = false
    @State private var initialPop: Bool = true
    
    private var animStyle: MoodAnimStyle {
        switch moodTitle.lowercased() {
        case "low": return .heartbeat
        case "down": return .pendulum
        case "neutral": return .floatingWave
        case "good": return .joyfulHop
        case "great": return .energyPulse
        default: return .floatingWave
        }
    }
    
    private var auraColor: Color {
        switch animStyle {
        case .heartbeat: return Color(hex: "#6366F1")
        case .pendulum: return Color(hex: "#F59E0B")
        case .floatingWave: return Color(hex: "#10B981")
        case .joyfulHop: return Color(hex: "#34D399")
        case .energyPulse: return Color(hex: "#FBBF24")
        }
    }
    
    var body: some View {
        ZStack {
            // Unique Glowing Backdrop Aura per Mood
            Circle()
                .fill(
                    RadialGradient(
                        colors: [auraColor.opacity(0.55), auraColor.opacity(0.0)],
                        center: .center,
                        startRadius: 6,
                        endRadius: isAnimating ? 38 : 18
                    )
                )
                .frame(width: 76, height: 76)
                .scaleEffect(isAnimating ? 1.25 : 0.92)
                .opacity(isAnimating ? 0.9 : 0.4)
            
            // Unique Animated Emoji Icon per Mood
            Image(emojiAsset)
                .resizable()
                .scaledToFit()
                .frame(width: 48, height: 48)
                .scaleEffect(scaleForStyle)
                .rotationEffect(rotationForStyle)
                .offset(y: offsetForStyle)
                .padding(10)
                .background(
                    RoundedRectangle(cornerRadius: 20)
                        .fill(Color.white)
                        .shadow(color: auraColor.opacity(0.35), radius: isAnimating ? 10 : 4, x: 0, y: isAnimating ? 5 : 2)
                )
        }
        .onAppear {
            withAnimation(.spring(response: 0.35, dampingFraction: 0.5)) {
                initialPop = false
            }
            
            withAnimation(animationForStyle) {
                isAnimating = true
            }
        }
    }
    
    // MARK: - Refined Style Modifiers for Ideal Motion
    private var scaleForStyle: CGFloat {
        if initialPop { return 1.25 }
        switch animStyle {
        case .heartbeat: return isAnimating ? 1.15 : 0.96
        case .pendulum: return isAnimating ? 1.06 : 0.98
        case .floatingWave: return isAnimating ? 1.12 : 0.96
        case .joyfulHop: return isAnimating ? 1.16 : 0.95
        case .energyPulse: return isAnimating ? 1.20 : 0.94
        }
    }
    
    private var rotationForStyle: Angle {
        switch animStyle {
        case .heartbeat: return .degrees(isAnimating ? 3 : -3)
        case .pendulum: return .degrees(isAnimating ? 8 : -8)
        case .floatingWave: return .degrees(isAnimating ? 3 : -3)
        case .joyfulHop: return .degrees(isAnimating ? -6 : 6)
        case .energyPulse: return .degrees(isAnimating ? 8 : -8)
        }
    }
    
    private var offsetForStyle: CGFloat {
        switch animStyle {
        case .heartbeat: return isAnimating ? -2 : 2
        case .pendulum: return isAnimating ? 3 : -1
        case .floatingWave: return isAnimating ? -4 : 4
        case .joyfulHop: return isAnimating ? -6 : 2
        case .energyPulse: return isAnimating ? -3 : 3
        }
    }
    
    private var animationForStyle: Animation {
        switch animStyle {
        case .heartbeat:
            return .easeInOut(duration: 1.1).repeatForever(autoreverses: true)
        case .pendulum:
            return .easeInOut(duration: 1.6).repeatForever(autoreverses: true)
        case .floatingWave:
            return .easeInOut(duration: 1.8).repeatForever(autoreverses: true)
        case .joyfulHop:
            return .spring(response: 0.55, dampingFraction: 0.65).repeatForever(autoreverses: true)
        case .energyPulse:
            return .easeInOut(duration: 1.2).repeatForever(autoreverses: true)
        }
    }
}

// MARK: - Mood Emoji
struct MoodEmojiView: View {
    let mood: MoodOption
    let isSelected: Bool
    let onTap: () -> Void
    
    @State private var animateBounce: Bool = false
    
    var body: some View {
        VStack(spacing: 6) {
            Image(mood.emojiAsset)
                .resizable()
                .scaledToFit()
                .frame(width: isSelected ? 48 : 42, height: isSelected ? 48 : 42)
                .scaleEffect(animateBounce ? 1.35 : (isSelected ? 1.2 : 1.0))
                .rotationEffect(.degrees(animateBounce ? -10 : 0))
                .animation(.spring(response: 0.3, dampingFraction: 0.5), value: animateBounce)
                .animation(.spring(response: 0.35, dampingFraction: 0.65), value: isSelected)
            
            Text(mood.title)
                .font(.custom(isSelected ? "Urbanist-Bold" : "Urbanist-Medium", size: isSelected ? 14 : 13))
                .foregroundColor(isSelected ? Color(hex: "#4338CA") : .white.opacity(0.9))
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 10)
        .padding(.horizontal, 4)
        .background(
            ZStack {
                if isSelected {
                    RoundedRectangle(cornerRadius: 18)
                        .fill(Color.white)
                        .shadow(color: Color.black.opacity(0.18), radius: 8, x: 0, y: 4)
                }
            }
        )
        .contentShape(Rectangle())
        .onTapGesture {
            // Trigger MS Teams style reaction animation & haptics
            animateBounce = true
            let generator = UIImpactFeedbackGenerator(style: .medium)
            generator.impactOccurred()
            
            onTap()
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.25) {
                withAnimation(.spring(response: 0.3, dampingFraction: 0.6)) {
                    animateBounce = false
                }
            }
        }
    }
}

// MARK: - Profile Completion
struct ProfileCompletionView: View {
    @ObservedObject var viewModel: HomeHealthViewModel
    
    private let columns = [
        GridItem(.adaptive(minimum: 100), spacing: 6)
    ]
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            header
            progress
            card
        }
        //        .onAppear {
        //            viewModel.fetchProfileOverview()
        //        }
        
        .onAppear {
            viewModel.fetchHomeData { success in
                print("Home API:", success)
            }
        }
    }
    
    private var header: some View {
        HStack {
            Text("Profile Completion")
                .font(.custom("Urbanist-Regular", size: 17))
                .foregroundColor(Color(hex: "#697383"))
            Spacer()
            Text("\(Int(viewModel.completion * 100))%")
                .font(.custom("Urbanist-Regular", size: 16))
                .foregroundColor(.black)
        }
        .padding(.horizontal)
    }
    
    private var progress: some View {
        ProgressView(value: viewModel.completion)
            .tint(
                LinearGradient(
                    colors: [
                        Color(hex: "#4338CA"),
                        Color(hex: "#211C64")
                    ],
                    startPoint: .leading,
                    endPoint: .trailing
                )
            )
            .scaleEffect(x: 1, y: 2.5, anchor: .center)
            .padding(.horizontal)
        
    }
    
    private var card: some View {
        VStack(alignment: .leading, spacing: 16) {
            medications
            allergies
            steps
        }
        .padding()
        .background(RoundedRectangle(cornerRadius: 14).fill(Color.white))
        //.padding(.horizontal)
    }
    
    private var medications: some View {
        VStack(alignment: .leading, spacing: 8) {
            SectionHeader(title: "Current Medications", showEdit: true)
            HStack(spacing: 8) {
                ForEach(viewModel.medications, id: \.self) { item in
                    TagView(text: item, color: Color(hex: "#211C64"))
                }
            }
        }
    }
    
    private var allergies: some View {
        VStack(alignment: .leading, spacing: 8) {
            SectionHeader(title: "Allergies", showEdit: false)
            
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(viewModel.allergies, id: \.self) { item in
                        TagView1(text: item, color: Color(hex: "#F31D1D"))
                        
                    }
                }
                .padding(.top, 10) // adjust as needed
            }
        }
    }
    
    private var steps: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text("Recommended Next Steps")
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundColor(Color(hex: "#697383"))
            ForEach(viewModel.steps, id: \.self) { step in
                HStack(spacing: 10) {
                    Image("asterisk")
                        .resizable()
                        .frame(width: 18, height: 18)
                    Text(step)
                        .font(.custom("Urbanist-Medium", size: 14))
                        .foregroundColor(Color(hex: "#3C3C3C"))
                        .lineLimit(3)
                }
            }
        }
        .padding(.top, 10)
        .padding(.bottom, 10)
    }
}


// MARK: - Alerts

struct AlertsSection: View {
    @EnvironmentObject private var coordinator: Coordinator
    let alerts: [HealthAlert]
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            header
                .padding(.top, -20)
            alertList
        }
        .padding(.horizontal)
    }
    
    private var header: some View {
        HStack {
            Text("Things Needing Attention")
                .font(.custom("Urbanist-Medium", size: 20))
            Spacer()
            if !(alerts.isEmpty)  {
                Button( action:{
                    coordinator.push(.needAttentionListView(alerts: alerts))
                }){
                    Text("View All")
                        .font(.custom("Urbanist-Medium", size: 14))
                        .foregroundColor(.white)
                        .padding(.vertical, 12)
                        .padding(.horizontal, 16)
                        .background(
                            LinearGradient(
                                colors: [
                                    Color(hex: "#4338CA"),
                                    Color(hex: "#211C64")
                                ],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .cornerRadius(56)
                        .frame(height: 72)
                        .frame(width: 88)
                }
            }
        }
    }
    
    private var alertList: some View {
        Group {
            if alerts.isEmpty {
                Text("No data found")
                    .font(.custom("Urbanist-Medium", size: 14))
                    .foregroundColor(.gray)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.vertical, 20)
            } else {
                ForEach(Array(alerts.prefix(3))) { alert in
                    AlertCard(alert: alert) {
                        coordinator.push(.newAppointmentScheduleView(flow: .new, chatId: nil))
                    }
                }
            }
        }
    }
}

struct AlertCard: View {
    
    let alert: HealthAlert
    let onScheduleTap: () -> Void
    
    var body: some View {
        HStack {
            info
            Spacer()
            scheduleButton
        }
        .padding()
        .background(cardBackground)
    }
    
    private var info: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(alert.title)
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundColor(Color(hex: "#F31D1D"))
            Text("For: \(alert.subtitle)")
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundColor(Color(hex: "#4338CA"))
        }
    }
    
    private var scheduleButton: some View {
        Button(action: onScheduleTap) {
            Image("Schedule")
        }
    }
    
    private var cardBackground: some View {
        RoundedRectangle(cornerRadius: 16)
            .fill(Color(hex: "#F31D1D").opacity(0.1))
            .overlay(
                RoundedRectangle(cornerRadius: 30)
                    .stroke(Color(hex: "#F31D1D").opacity(0.4))
            )
            .cornerRadius(30)
    }
}

// MARK: - Reusable
struct SectionHeader: View {
    @EnvironmentObject private var coordinator: Coordinator
    let title: String
    let showEdit: Bool
    
    var body: some View {
        HStack {
            Text(title)
                .font(.custom("Urbanist-Medium", size: 16))
                .foregroundColor(Color(hex: "#697383"))
            Spacer()
            Button(action:  {
                
                UserDetail.shared.removeID() // Remove selected family member ID

                let userId = UserDetail.shared.getUserId()
                UserDetail.shared.setID(userId)
                coordinator.push(.completeProfileView(flow: .editProfile))
            }){
                if showEdit {
                    Image("PencilIcons")
                        .resizable()
                        .frame(width: 45, height: 45)
                }
            }
        }
    }
}

struct TagView: View {
    let text: String
    let color: Color
    
    var body: some View {
        Text(text)
            .font(.custom("Urbanist-Medium", size: 14))
            .foregroundColor(color)
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background(Color(hex: "#4338CA").opacity(0.20))
            .clipShape(Capsule())
            .fixedSize()   // ONLY once
    }
}

struct TagView1: View {
    let text: String
    let color: Color
    
    var body: some View {
        Text(text)
            .font(.custom("Urbanist-Medium", size: 14))
            .foregroundColor(color)
            .padding(.horizontal, 12)
            .padding(.vertical, 8)
            .background(Color(hex: "#F31D1D").opacity(0.10))
            .clipShape(Capsule())
            .fixedSize()   // ONLY once
    }
}


struct HealthFamilyOverviewSection: View {
    @ObservedObject var viewModel: HomeHealthViewModel
    @EnvironmentObject private var coordinator: Coordinator
    @EnvironmentObject private var tabVM: TabViewModel
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            header
            memberTabs
                .padding(.top, -10)
            profileCard
        }
        .padding(.horizontal)
        
    }
}

private extension HealthFamilyOverviewSection {
    
    var actions: some View {
        HStack(spacing: 5) {
            
            // Schedule
            Button {
                coordinator.push(.newAppointmentScheduleView(flow: .new, chatId: nil))
            } label: {
                Image("Schedule")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 110, height: 55)
            }
            
            // Ask AI
            Button {
                coordinator.selectedAppTab = .magic
                tabVM.selectedTab = .magic
                tabVM.isTabBarHidden = false
//                if !UserDefaults.standard.bool(forKey: "hasAcceptedPrivacyConsent") {
//                    coordinator.push(.ChatHomeScreenView())
//                  
//                }
            } label: {
                HStack(spacing: 5) {
                    Image("ChatIcon")
                        .resizable()
                        .frame(width: 18, height: 18)
                    
                    Text("Ask AI")
                        .font(.custom("Urbanist-Medium", size: 12))
                        .foregroundColor(.white)
                }
                .frame(width: 80, height: 42)
                .background(
                    LinearGradient(
                        colors: [
                            Color(hex: "#4338CA"),
                            Color(hex: "#211C64")
                        ],
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                )
                .clipShape(Capsule())
            }
            
            // ✅ Appointment Date Dynamic UI
            HStack(spacing: 8) {
                Image(systemName: "clock")
                
                Text(viewModel.getUpcomingAppointmentText()) // <-- YAHAN UPDATE KIYA HAI
                    .font(.custom("Urbanist-Medium", size: 12))
                    .lineLimit(2)
                Spacer(minLength: 0)
            }
            .padding(.horizontal, 8)
            .frame(width: 145, height: 42)
            .background(Color.clear)
            .overlay(
                Capsule()
                    .stroke(Color(hex: "#697383"), lineWidth: 0.6)
            )
        }
        .padding(.horizontal, 0)
    }
}

private extension HealthFamilyOverviewSection {
    
    var header: some View {
        HStack {
            Text("Health Overview")
                .font(.custom("Urbanist-Medium", size: 20))
                .foregroundColor(Color.black)
            
            Spacer()
            
            Button {
                coordinator.push(.completeProfileView(flow: .addFamilyMember))
            } label: {
                HStack {
                    Image("PlusIcon")
                        .resizable()
                        .frame(width: 12, height: 12)
                    
                    Text("Add")
                }
                
                .font(.custom("Urbanist-Medium", size: 14))
                .foregroundColor(.white)
                .padding(.vertical, 14)
                .padding(.horizontal, 16)
                .frame(width: 88)
                .background(
                    LinearGradient(
                        colors: [
                            Color(hex: "#4338CA"),
                            Color(hex: "#211C64")
                        ],
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                )
                .cornerRadius(56)
                .frame(height: 72)
            }
        }
    }
}

//private extension HealthFamilyOverviewSection {
//    var memberTabs: some View {
//        ScrollView(.horizontal, showsIndicators: false) {
//            LazyHStack(spacing: 10) {
//                ForEach(Array(viewModel.familyMembers.enumerated()), id: \.element.idNumber) { index, member in
//
//                    Button {
//                        viewModel.selectedMemberIndex = index
//                        viewModel.selectedMemberID = "\(viewModel.familyMembers[index].idNumber)"
//                    } label: {
//                        Text(tabTitle(index))
//                            .padding(.horizontal, 14)
//                            .padding(.vertical, 12)
//                            .background(
//                                viewModel.selectedMemberIndex == index
//                                ? Color(hex: "#3C3C3C")
//                                : Color.white
//                            )
//                            .foregroundColor(
//                                viewModel.selectedMemberIndex == index
//                                ? .white
//                                : Color(hex: "#697383")
//                            )
//                            .clipShape(Capsule())
//                            .overlay(
//                                Capsule().stroke(Color.gray.opacity(0.3))
//                            )
//                    }
//                    .buttonStyle(.plain)
//                    .contentShape(Rectangle())
//                }
//            }
//            .padding(.vertical, 4)
//        }
//    }
//
//    func tabTitle(_ index: Int) -> String {
//        index == 0
//        ? "\(viewModel.familyMembers[index].name) (Myself)"
//        : viewModel.familyMembers[index].name
//    }
//}
private extension HealthFamilyOverviewSection {
    var memberTabs: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            LazyHStack(spacing: 10) {
                // 🛠 FIX: Changed id from \.element.idNumber to \.offset to prevent duplicate ID crashes
                ForEach(Array(viewModel.familyMembers.enumerated()), id: \.offset) { index, member in
                    
                    Button {
                        viewModel.selectedMemberIndex = index
                        viewModel.selectedMemberID = "\(viewModel.familyMembers[index].idNumber)"
                        print("selectedIndex:- \(index)")
                        print("selectedMemberID:", "\(viewModel.familyMembers[index].idNumber)")
                    } label: {
                        Text(tabTitle(index))
                            .padding(.horizontal, 14)
                            .padding(.vertical, 12)
                            .background(
                                viewModel.selectedMemberIndex == index
                                ? Color(hex: "#3C3C3C")
                                : Color.white
                            )
                            .foregroundColor(
                                viewModel.selectedMemberIndex == index
                                ? .white
                                : Color(hex: "#697383")
                            )
                            .clipShape(Capsule())
                            .overlay(
                                Capsule().stroke(Color.gray.opacity(0.3))
                            )
                    }
                    .buttonStyle(.plain)
                    .contentShape(Rectangle())
                }
            }
            .padding(.vertical, 4)
        }
    }
    
    func tabTitle(_ index: Int) -> String {
        index == 0
        ? "\(viewModel.familyMembers[index].name) (Myself)"
        : viewModel.familyMembers[index].name
    }
}


private extension HealthFamilyOverviewSection {
    var profileCard: some View {
        VStack(alignment: .leading, spacing: 16) {
            profileHeader
            activeAlerts
            actions
        }
        .padding()
        .background(Color(hex: "#4338CA").opacity(0.12))
        .clipShape(RoundedRectangle(cornerRadius: 22))
    }
}

private extension HealthFamilyOverviewSection {
    
    var profileHeader: some View {
        HStack(spacing: 14) {
            //Image(viewModel.selectedMember?.imageName ?? "")
            let img = "\(viewModel.selectedMember?.imageName ?? "")"
            
            
            
            //            Image.loadImage3(
            //                viewModel.selectedMember?.imageName,
            //                placeholder: "Frame 1272638625",
            //                width: 73,
            //                height: 70,
            //                cornerRadius: 20
            //            )
            
            //            Image.loadImage3(img.imgFullPath())
            //                //.resizable()
            //                .scaledToFill()
            //                .frame(width: 73, height: 70)
            //                .cornerRadius(20)
            
            Image.loadImage3(
                img.imgFullPath(),
                width: 73,
                height: 70,
                cornerRadius: 20,
                contentMode: .fill
            )
            
            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text(viewModel.selectedMember?.name ?? "")
                        .font(.custom("Urbanist-Medium", size: 14))
                        .foregroundColor(Color.black)
                    
                    Text("\(viewModel.selectedMember?.dob ?? "")")
                        .font(.custom("Urbanist-Medium", size: 10))
                        .foregroundColor(Color(hex: "#4338CA"))
                        .padding(.vertical, 4)
                        .padding(.horizontal, 8)
                        .background(Color(hex: "#4338CA").opacity(0.20))
                        .clipShape(Capsule())
                }
                
                Text(viewModel.selectedMember?.relation ?? "")
                    .font(.custom("Urbanist-Medium", size: 14))
                    .foregroundColor(Color(hex: "#374151"))
                
               // Text("Last checkup: \(viewModel.selectedMember?.lastCheckupDays ?? 0) days ago")
                let days = viewModel.selectedMember?.lastCheckupDays ?? 0

                Text("Last checkup: \(days) \(days <= 1 ? "day" : "days") ago")
                    .font(.custom("Urbanist-Medium", size: 14))
                    .foregroundColor(Color(hex: "#374151"))
            }
            
            Spacer()
            
            Button(action: {
                
                if viewModel.selectedMemberIndex == 0 {
                    // Myself
                    if let memberId = viewModel.selectedMemberID {
                        print("Edit Profile ID:- \(memberId)")
                        UserDetail.shared.setUserId(memberId)
                        UserDetail.shared.setID(memberId)
                        coordinator.push(
                            .completeProfileView(
                                flow: .editProfile
                            )
                        )
                    }
                } else {
                    // Family members
                    let memberID = viewModel.selectedMemberID
                    print("Edit Profile ID:- \(memberID ?? "")")
                    UserDetail.shared.setID("\(memberID ?? "")")
                    coordinator.push(.completeProfileView(flow: .editFamilyMember))
                }
            }){
                Image("PencilIcons")
                    .resizable()
                    .frame(width: 45, height: 45)
            }
        }
    }
}

private extension HealthFamilyOverviewSection {
    
    var activeAlerts: some View {
        VStack(alignment: .leading, spacing: 10) {
            
            Text("Active Alerts")
                .font(.custom("Urbanist-Medium", size: 14))
                .foregroundColor(Color(hex: "#697383"))
                .padding(.leading, 2)
            
            if let alerts = viewModel.selectedMember?.alerts,
               !alerts.isEmpty {
                
                ForEach(alerts) { alert in
                    HStack {
                        Image("NotificationIcon")
                            .resizable()
                            .frame(width: 29, height: 29)
                        
                        Text(alert.title)
                            .font(.custom("Urbanist-Medium", size: 14))
                            .foregroundColor(Color(hex: "#3C3C3C"))
                        
                        Spacer()
                    }
                    .frame(height: 40)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 8)
                    .background(Color(hex: "#996BFE").opacity(0.20))
                    .clipShape(RoundedRectangle(cornerRadius: 50))
                }
                
            } else {
                
                HStack {
                    Image(systemName: "bell.slash")
                        .foregroundColor(Color(hex: "#697383"))
                    
                    Text("No active alerts yet !")
                        .font(.custom("Urbanist-Medium", size: 14))
                        .foregroundColor(Color(hex: "#697383"))
                    
                    Spacer()
                }
                .frame(height: 40)
                .padding(.horizontal, 10)
                .padding(.vertical, 8)
                .background(Color(hex: "#996BFE").opacity(0.20))
                .clipShape(RoundedRectangle(cornerRadius: 50))
            }
        }
        .padding(.horizontal, 0)
    }
}

struct ProfileCompletionPopupView: View {
    @ObservedObject var viewModel: HomeHealthViewModel
    @EnvironmentObject private var coordinator: Coordinator
    
    var body: some View {
        ZStack {
            // Background Dim
            Color.black.opacity(0.4)
                .ignoresSafeArea()
                .onTapGesture {
                    withAnimation {
                        viewModel.showProfileCompletionPopup = false
                    }
                }
            
            // Card Content
            VStack(alignment: .leading, spacing: 20) {
                // Header row with Icon, Title, and Close Button
                HStack(alignment: .center, spacing: 12) {
                    ZStack {
                        Circle()
                            .fill(Color(hex: "#4338CA"))
                            .frame(width: 44, height: 44)
                        
                        Image(systemName: "person")
                            .font(.system(size: 20, weight: .medium))
                            .foregroundColor(.white)
                    }
                    
                    Text("Complete Your Profile")
                        .font(.custom("Urbanist-semibold", size: 20))
                        .foregroundColor(.black)
                    
                    Spacer()
                    
                    Button {
                        withAnimation {
                            viewModel.showProfileCompletionPopup = false
                        }
                    } label: {
                        Image(systemName: "xmark")
                            .font(.system(size: 14, weight: .semibold))
                            .foregroundColor(.black)
                            .frame(width: 36, height: 36)
                            .background(Color.white)
                            .clipShape(Circle())
                            .overlay(
                                Circle()
                                    .stroke(Color(hex: "#E5E7EB"), lineWidth: 1)
                            )
                    }
                }
                
                // Subtitle
                Text("Finish setting up your details for better, personalized care")
                    .font(.custom("Urbanist-Regular", size: 16))
                    .foregroundColor(Color(hex: "#1F2937"))
                    .lineSpacing(4)
                
                // Checklist
                VStack(alignment: .leading, spacing: 14) {
                    HStack(alignment: .center, spacing: 10) {
                        Image(systemName: "checkmark.square.fill")
                            .font(.system(size: 20))
                            .foregroundColor(Color(hex: "#10B981"))
                        
                        Text("Faster AI answers tailored to you")
                            .font(.custom("Urbanist-Medium", size: 16))
                            .foregroundColor(Color(hex: "#374151"))
                    }
                    
                    HStack(alignment: .center, spacing: 10) {
                        Image(systemName: "checkmark.square.fill")
                            .font(.system(size: 20))
                            .foregroundColor(Color(hex: "#10B981"))
                        
                        Text("Safer medication & allergy checks")
                            .font(.custom("Urbanist-Medium", size: 16))
                            .foregroundColor(Color(hex: "#374151"))
                    }
                    
                    HStack(alignment: .center, spacing: 10) {
                        Image(systemName: "checkmark.square.fill")
                            .font(.system(size: 20))
                            .foregroundColor(Color(hex: "#10B981"))
                        
                        Text("Quicker reminders & records")
                            .font(.custom("Urbanist-Medium", size: 16))
                            .foregroundColor(Color(hex: "#374151"))
                    }
                }
                .padding(.vertical, 8)
                
                // Action Buttons
                HStack(spacing: 12) {
                    // Remind Me Later
                    Button {
                        withAnimation {
                            viewModel.showProfileCompletionPopup = false
                        }
                    } label: {
                        Text("Remind Me Later")
                            .font(.custom("Urbanist-SemiBold", size: 16))
                            .foregroundColor(.black)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(Color(hex: "#F9FAFB"))
                            .cornerRadius(25)
                            .overlay(
                                RoundedRectangle(cornerRadius: 25)
                                    .stroke(Color.black, lineWidth: 1)
                            )
                    }
                    
                    // Complete Now
                    Button {
                        withAnimation {
                            viewModel.showProfileCompletionPopup = false
                        }
                        coordinator.push(.completeProfileView(flow: .editProfile))
                    } label: {
                        Text("Complete Now")
                            .font(.custom("Urbanist-SemiBold", size: 16))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background( Image("BackgroundBtn") // Asset name
                                        .resizable()
                                         .scaledToFill())
                            .cornerRadius(25)
                    }
                }
            }
            .padding(24)
            .background(Color.white)
            .cornerRadius(24)
            .shadow(color: Color.black.opacity(0.15), radius: 15, x: 0, y: 5)
            .padding(.horizontal, 20)
        }
    }
}

#Preview {
    HomeHealthView()
}
