package com.bussiness.curemegptapp.ui.screen.settingsScreens

//TermsAndConditionsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.ui.component.TopBarHeader2
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.screen.settings.SettingsScreen
import com.bussiness.curemegptapp.viewmodel.setting.TermConditionViewModel

@Composable
fun TermsAndConditionsScreen(navController: NavHostController,
                             viewModel: TermConditionViewModel = hiltViewModel()
) {
    val titleFromNav = navController.previousBackStackEntry?.savedStateHandle?.get<String>("title") ?: ""
    val contentFromNav = navController.previousBackStackEntry?.savedStateHandle?.get<String>("content") ?: ""

    val screenTitle = titleFromNav.ifEmpty { stringResource(R.string.settings_terms_conditions) }
    val fullText = contentFromNav.ifEmpty { viewModel.termCondition.collectAsState().value }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .statusBarsPadding()
    ) {
        TopBarHeader2(title = screenTitle, onBackClick = { navController.popBackStack() })

        val keyword = "CureMeGPT"
        val keywordColor = Color(0xFF4338CA)   // Purple

        val annotatedText = buildAnnotatedString {

            var startIndex = 0
            while (true) {
                val index = fullText.indexOf(keyword, startIndex, ignoreCase = false)

                if (index == -1) {
                    append(fullText.substring(startIndex))
                    break
                }

                // Add text before the keyword
                append(fullText.substring(startIndex, index))

                // Add colored keyword
                withStyle(style = SpanStyle(color = keywordColor, fontWeight = FontWeight.Medium)) {
                    append(fullText.substring(index, index + keyword.length))
                }

                startIndex = index + keyword.length
            }
        }

        Text(
            text = annotatedText,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(20.dp)
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TermsAndConditionsScreenPreview() {
    val navController = rememberNavController()
    TermsAndConditionsScreen(navController)
}