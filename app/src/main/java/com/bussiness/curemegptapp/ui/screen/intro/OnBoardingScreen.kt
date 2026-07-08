package com.bussiness.curemegptapp.ui.screen.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.OnboardingPage
import com.bussiness.curemegptapp.navigation.AppDestination
import com.bussiness.curemegptapp.ui.component.ContinueButton
import com.bussiness.curemegptapp.ui.component.SkipButton
import com.bussiness.curemegptapp.util.AppConstant
import com.bussiness.curemegptapp.viewmodel.onboardingViewModel.onBoardingViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch




val onboardingPagesDummy = listOf(
    OnboardingPage(
        imageRes = R.drawable.onb1,
        title = "Never Miss a Dose",
        description = "Smart reminders keep your medications and prescriptions on track, so your health stays in control."
    ),
    OnboardingPage(
        imageRes = R.drawable.onb2,
        title = " See What Matters, Fast",
        description = "Get instant answers, health summaries, and personalized insights at a glance."
    ),
    OnboardingPage(
        imageRes = R.drawable.onb3,
        title = "Family Health, Connected",
        description = "Add family members, manage profiles, and track everyone’s care in one place."
    ),
    OnboardingPage(
        imageRes = R.drawable.onb4,
        title = "Arrive Prepared",
        description = "AI organizes your symptoms and records into a simple summary for every doctor visit."
    ),
    OnboardingPage(
        imageRes = R.drawable.onb5,
        title = " Ask, Understand, Act.",
        description = "From quick questions to daily guidance, CureMeGPT helps you make informed health choices."
    )
)



@Composable
fun OnboardingScreen(navController: NavHostController,viewModel: onBoardingViewModel = hiltViewModel()) {


    val onboardingPages by viewModel.pages.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { onboardingPages.size }
    )
    
    if (onboardingPages.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->

            OnboardingPageContent(
                page = onboardingPages[page],
                currentPage = pagerState.currentPage,
                totalPages = onboardingPages.size
            )
        }

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 10.dp)) {
            val isLastPage = pagerState.currentPage == onboardingPages.lastIndex
            val isFirstPage = pagerState.currentPage == 0
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isLastPage){
                    SkipButton(onClick = {
                        navController.navigate(AppDestination.Login)
                    })
                    Spacer(modifier = Modifier.width(16.dp))
                }

                ContinueButton(text = if (isLastPage) stringResource(R.string.get_started)/*"Get Started"*/ else if (isFirstPage ) stringResource(R.string.next_button)/*"Next"*/  else stringResource(R.string.continue_button)/*"Continue"*/, onClick = {
                    if (isLastPage) {
                        navController.navigate(AppDestination.Login)
                    } else {
//                        pagerState.currentPage + 1
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                })
            }


            Spacer(modifier = Modifier.height(20.dp))
        }

    }
}

@Composable
fun OnboardingPageContent(
    page: com.bussiness.curemegptapp.apimodel.OnBoardingModel.OnboardingPage,
    currentPage: Int,
    totalPages: Int
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .clip(RoundedCornerShape(20.dp))
        ) {
//            Image(
//                painter = painterResource(id = page.imageRes),
//                contentDescription = "Onboarding Image",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(500.dp),
//                contentScale = ContentScale.Crop
//            )

            AsyncImage(
                model = AppConstant.IMAGE_BASE_URL+page.imageUrl,
                contentDescription = "Onboarding Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                contentScale = ContentScale.Crop
            )
        }

        // Bottom Rounded White Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .align(Alignment.BottomCenter)
                .background(Color.White)
                .padding(horizontal = 15.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Spacer(Modifier.height(8.dp))
                // Custom Page Indicator ABOVE the title
                PageIndicator(currentPage = currentPage, totalPages = totalPages)

                Spacer(Modifier.height(13.dp))

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.urbanist_bold))
                    ),
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                    lineHeight = 23.sp,
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun PageIndicator(currentPage: Int, totalPages: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        repeat(totalPages) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .height(9.dp)
                    .width(if (isSelected) 50.dp else 18.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isSelected) Color(0xFF4338CA) else Color(0xFFC3C6CB)
                    )
            )
        }
    }
}