package com.bussiness.curemegptapp.ui.screen.settingsScreens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.TopBarHeader2
import com.bussiness.curemegptapp.viewmodel.setting.AboutUsViewModel

@Composable
fun AboutScreen(navController: NavHostController,
                viewModel : AboutUsViewModel = hiltViewModel()
  ) {
    val titleFromNav = navController.previousBackStackEntry?.savedStateHandle?.get<String>("title") ?: ""
    val contentFromNav = navController.previousBackStackEntry?.savedStateHandle?.get<String>("content") ?: ""

    val screenTitle = titleFromNav.ifEmpty { stringResource(R.string.settings_about) }
    val fullText = contentFromNav.ifEmpty { viewModel.privacyPolicy.collectAsState().value }

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
fun AboutScreenPreview() {
    val navController = rememberNavController()
    AboutScreen(navController)
}