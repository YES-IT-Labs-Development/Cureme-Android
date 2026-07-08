package com.bussiness.curemegptapp.ui.screen.settingsScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.base.TextResources
import com.bussiness.curemegptapp.data.model.ExpandableItem
import com.bussiness.curemegptapp.ui.component.TopBarHeader2
import com.bussiness.curemegptapp.ui.theme.AppGradientColors
import com.bussiness.curemegptapp.ui.theme.gradientBrush
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.bussiness.curemegptapp.viewmodel.setting.FaqViewModel


@Composable
fun FrequentlyAskQuestionsScreen(navController: NavHostController,
                      viewModel : FaqViewModel = hiltViewModel()
                                 ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
            .statusBarsPadding()
    ) {
        TopBarHeader2(title = stringResource(R.string.settings_faq1)/*"Frequently Ask Questions"*/, onBackClick = { navController.popBackStack() })


        val items by viewModel.faqList.collectAsState()


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            items(items) { item ->
                ExpandableCard(
                    item = item,
                    onToggle = { toggledItem ->
                        viewModel.toggleItem(toggledItem)
                    }
                )
            }
        }

    }
}


@Composable
fun ExpandableCard(
    item: ExpandableItem,
    onToggle: (ExpandableItem) -> Unit
) {

    Column {

        // ============ TOP HEADER (changes style based on expanded state) ============
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip( if (item.isExpanded) RoundedCornerShape(topStart = 30.dp,
                    topEnd = 30.dp) else RoundedCornerShape(30.dp))
                .background(
                    brush = if (item.isExpanded) gradientBrush else Brush.horizontalGradient(
                        colors = listOf(Color(0xFFF3F1FF), Color(0xFFF3F1FF))
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onToggle(item) }
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = item.title,
                    color = if (item.isExpanded) Color.White else Color(0xFF101010),
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_semibold)),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = if (item.isExpanded)
                        painterResource(R.drawable.ic_show_drop_down_icon)
                    else
                        painterResource(R.drawable.ic_hide_drop_down_icon),
                    contentDescription = null,
                    tint = if (item.isExpanded) Color.White else Color(0xFF101010),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // ============ EXPANDED CONTENT SECTION ============
        AnimatedVisibility(
            visible = item.isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 30.dp,
                            bottomEnd = 30.dp
                        )
                    )
                    .background(Color(0xFFF3F1FF))
                    .padding(horizontal = 20.dp, vertical = 15.dp)
            ) {
                Text(
                    text = item.content,
                    color = Color(0xFF000000),
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FrequentlyAskQuestionsScreenPreview() {
    val navController = rememberNavController()
    FrequentlyAskQuestionsScreen(navController)
}