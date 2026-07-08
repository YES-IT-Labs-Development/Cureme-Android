package com.bussiness.curemegptapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.bussiness.curemegptapp.ui.screen.main.schedule.HealthScheduleScreen
import com.bussiness.curemegptapp.ui.screen.main.thingNeedingAttention.ThingNeedingAttentionScreen
import com.bussiness.curemegptapp.ui.screen.main.medication.AddMedicationScreen
import com.bussiness.curemegptapp.ui.screen.main.alert.AlertScreen
import com.bussiness.curemegptapp.ui.screen.main.familyMembersScreen.FamilyMembersScreen
import com.bussiness.curemegptapp.ui.screen.main.familyPersonProfile.FamilyPersonProfileScreen
import com.bussiness.curemegptapp.ui.screen.main.scheduleNewAppointment.ScheduleNewAppointmentScreen

import com.bussiness.curemegptapp.ui.screen.main.ChatAI.AIChatScreen
import com.bussiness.curemegptapp.ui.screen.main.home.HomeScreen
import com.bussiness.curemegptapp.ui.screen.main.ChatAI.ChatScreen
import com.bussiness.curemegptapp.ui.screen.auth.NewPasswordScreen
import com.bussiness.curemegptapp.ui.screen.auth.ResetScreen
import com.bussiness.curemegptapp.ui.screen.auth.VerifyOtpScreen
import com.bussiness.curemegptapp.ui.screen.main.addFamilyMemberScreen.AddFamilyMemberScreen
import com.bussiness.curemegptapp.ui.screen.main.chat.ChatDataScreen
import com.bussiness.curemegptapp.ui.screen.main.chat.OpenChatScreen
import com.bussiness.curemegptapp.ui.screen.main.editFamilyMemberDetails.EditFamilyMemberDetailsScreen
import com.bussiness.curemegptapp.ui.screen.main.editProfile.EditProfileScreen
import com.bussiness.curemegptapp.ui.screen.main.healthReports.HealthReportsScreen
import com.bussiness.curemegptapp.ui.screen.main.medication.EditMedicationScreen
import com.bussiness.curemegptapp.ui.screen.main.myProfile.MyProfileScreen
import com.bussiness.curemegptapp.ui.screen.main.reports.ReportScreen
import com.bussiness.curemegptapp.ui.screen.main.rescheduleAppointment.RescheduleAppointmentScreen
import com.bussiness.curemegptapp.ui.screen.settings.SettingsScreen
import com.bussiness.curemegptapp.ui.screen.settingsScreens.AboutScreen
import com.bussiness.curemegptapp.ui.screen.settingsScreens.AccountPrivacyScreen
import com.bussiness.curemegptapp.ui.screen.settingsScreens.DeleteAccountScreen
import com.bussiness.curemegptapp.ui.screen.settingsScreens.FrequentlyAskQuestionsScreen
import com.bussiness.curemegptapp.ui.screen.settingsScreens.HelpSupportScreen
import com.bussiness.curemegptapp.ui.screen.settingsScreens.PrivacyPolicyScreen
import com.bussiness.curemegptapp.ui.screen.settingsScreens.TermsAndConditionsScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavGraph(
    authNavController: NavController,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = AppDestination.Home,
        modifier = modifier.background(Color.Transparent)
    ) {


        composable<AppDestination.Home> {
            HomeScreen(navController)
        }

        composable<AppDestination.Schedule> {
            HealthScheduleScreen(navController)
        }
        composable<AppDestination.Family> {
            FamilyMembersScreen(navController)
        }
        composable<AppDestination.Reports> {
            HealthReportsScreen(navController)
        }


        composable<AppDestination.AIChatScreen> { AIChatScreen(navController) }
        composable<AppDestination.ChatScreen> { ChatScreen(navController) }


        composable<AppDestination.ThingNeedingAttention> {
            ThingNeedingAttentionScreen(navController)
        }

        composable<AppDestination.ScheduleNewAppointment> {
            ScheduleNewAppointmentScreen(navController)
        }
        composable<AppDestination.AddMedication> {
            AddMedicationScreen(navController)
        }

        composable<AppDestination.AlertScreen> {
            AlertScreen(navController)
        }
        composable<AppDestination.FamilyPersonProfile> {

            val id = navController
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<Int>("id")

            FamilyPersonProfileScreen(
                navController = navController,
                id = id
            )

        }

        composable<AppDestination.MyProfileScreen> {

            MyProfileScreen(navController)
        }

       /* composable<AppDestination.ReportScreen> {
            ReportScreen(navController)
        }*/

        composable(
            route = "${AppDestination.ReportScreen}?id={id}",
            arguments = listOf(
                navArgument("id") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ReportScreen(navController, id)
        }





        composable<AppDestination.SettingsScreen> {
            SettingsScreen(navController,authNavController)
        }
        composable<AppDestination.AboutScreen> {
            AboutScreen(navController)
        }
        composable<AppDestination.FrequentlyAskQuestionsScreen> {
            FrequentlyAskQuestionsScreen(navController)
        }
        composable<AppDestination.PrivacyPolicyScreen> {
            PrivacyPolicyScreen(navController)
        }
        composable<AppDestination.TermsAndConditionsScreen> {
            TermsAndConditionsScreen(navController)
        }
        composable<AppDestination.AccountPrivacyScreen> {
            AccountPrivacyScreen(navController)
        }

        composable(
            route = "reset?from={from}",
            arguments = listOf(
                navArgument("from") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val from = backStackEntry.arguments?.getString("from") ?: ""
            ResetScreen(navController, from)
        }
       // composable<AppDestination.Reset> { ResetScreen(navController) }
        // composable<AppDestination.VerifyOtp> { VerifyOtpScreen(navController) }
        composable(
            route = "verifyOtp?from={from}&email={email}",
            arguments = listOf(
                navArgument("from") { defaultValue = "" },
                navArgument("email") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val from = backStackEntry.arguments?.getString("from") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            VerifyOtpScreen(navController, from, email)
        }
        composable(
            route = "${AppDestination.NewPassword}?from={from}&email={email}",
            arguments = listOf(
                navArgument("from") { defaultValue = "" },
                navArgument("email") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val from = backStackEntry.arguments?.getString("from") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            NewPasswordScreen(navController, from, email)
        }

        composable<AppDestination.DeleteAccountScreen> { DeleteAccountScreen(navController,authNavController) }

        composable<AppDestination.HelpSupportScreen> { HelpSupportScreen(navController) }
        composable<AppDestination.EditProfileScreen> { EditProfileScreen(navController) }

       // composable<AppDestination.AddFamilyMemberScreen> { AddFamilyMemberScreen(navController) }
        composable(
            route = "addFamilyMember?from={from}",
            arguments = listOf(
                navArgument("from") { defaultValue = "" },
            )
        ) { backStackEntry ->
            val from = backStackEntry.arguments?.getString("from") ?: ""
            AddFamilyMemberScreen(navController, from)
        }

        composable<AppDestination.EditFamilyMemberDetailsScreen> { EditFamilyMemberDetailsScreen(navController) }

        composable<AppDestination.RescheduleAppointmentScreen> {
            val appointment = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("appointmentId")
            RescheduleAppointmentScreen(navController, appointmentId = appointment?:0)
        }

        composable<AppDestination.EditMedicationScreen> {
            val medicationId = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("medicationId")?:0
            EditMedicationScreen(navController,medicationId)
        }

        composable(
            route = "openChat?from={from}",
            arguments = listOf(
                navArgument("from") { defaultValue = "" },
            )
        ) { backStackEntry ->
            val from = backStackEntry.arguments?.getString("from") ?: ""
            OpenChatScreen(navController, from)
        }
        composable<AppDestination.ChatDataScreen> { ChatDataScreen(navController) }
    }
}
