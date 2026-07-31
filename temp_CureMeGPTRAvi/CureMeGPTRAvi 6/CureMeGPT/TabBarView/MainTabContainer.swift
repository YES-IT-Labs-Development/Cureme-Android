//
//  MainTabContainer.swift
//  CureMeGPT
//
//  Created by YES IT Labs on 02/12/25.
//

import SwiftUI

struct MainTabContainer: View {
   
    
    @StateObject private var vm = TabViewModel()
    @EnvironmentObject private var coordinator: Coordinator

    var body: some View {
        ZStack(alignment: .bottom) {

            TabView(selection: $vm.selectedTab) {

                HomeHealthView()
                    .tag(AppTab.home)
                    .tag(0)
                    .scrollBounceBehavior(.automatic)

                ScheduleAppointmentView()
                    .tag(AppTab.schedule)
                    .tag(1)
                    .scrollBounceBehavior(.automatic)

                ChatHomeScreenView()
                    .environmentObject(vm)
                    .tag(AppTab.magic)
                    .scrollBounceBehavior(.basedOnSize)

               // FamilyMembersView()
                FamilyMembersView()
                    .tag(2)
                    .environmentObject(FamilyMemberViewModel())
                    .tag(AppTab.family)
                    .scrollBounceBehavior(.automatic)

                HealthReportView()
                    .tag(3)
                    .tag(AppTab.reports)
                    .scrollBounceBehavior(.automatic)
            }
            .environmentObject(vm)   // ✅ VERY IMPORTANT (ADD HERE)
            //.tabViewStyle(.page(indexDisplayMode: .never)) // hides system tab bar

            if !vm.isTabBarHidden {
                CustomTabBar(vm: vm)
                    .ignoresSafeArea(edges: .bottom)
            }
        }
       // .edgesIgnoringSafeArea(.bottom)
        .onAppear {
            vm.selectedTab = coordinator.selectedAppTab
        }
        .onChange(of: coordinator.selectedAppTab) { newTab in
            vm.selectedTab = newTab
        }
        .onChange(of: vm.selectedTab) { newTab in
            coordinator.selectedAppTab = newTab
        }
    }
}

