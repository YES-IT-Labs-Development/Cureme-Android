package com.bussiness.curemegptapp.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.viewModel.main.Document


@Composable
fun SettingHeader(title: String, onBackClick: () -> Unit) {

    Column(modifier = Modifier.background(Color.White)) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp,
                bottom = 20.dp
            )
        ) {
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image( painter = painterResource(R.drawable.ic_back_nav_icon),
                    contentDescription = "", modifier = Modifier
                        .clickable( interactionSource = remember { MutableInteractionSource() },
                            indication = null) { onBackClick() }
                        .wrapContentSize())
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    color = Color.Black
                )
            }



        }
       // HorizontalDivider(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color(0xFFEBE1FF)))
        Divider(color = Color(0xFFEBE1FF), thickness = 2.dp)
    }
}



@Composable
fun CommonHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.background(Color.White)) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium
            )
        }
        //HorizontalDivider(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color(0xFFEBE1FF)))
        Divider(color = Color(0xFFEBE1FF), thickness = 1.dp)
    }
}


@Composable
fun AppointmentMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onMarkComplete: () -> Unit,
    onReschedule: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        containerColor = Color.Unspecified,
        modifier = Modifier
            .width(200.dp).wrapContentHeight()
            .background(
                Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .border(1.dp, Color(0xFFE7E6F8), RoundedCornerShape(20.dp))
    ) {

        // Mark as Complete
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RoundedCustomCheckbox(
                        checked = checked,
                        onCheckedChange = { onCheckedChange(it) }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Mark As Complete",
                        fontSize = 15.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                modifier =Modifier.padding(end = 10.dp)
                    )
                }
            },
            onClick = {
                onMarkComplete()
                onDismiss()
            }
        )

        // Reschedule
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_note_edit_icon),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Reschedule",
                        fontSize = 15.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        modifier =Modifier.padding(end = 10.dp)
                    )
                }
            },
            onClick = {
                onReschedule()
                onDismiss()
            }
        )

        // Delete
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_delete_icon2),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Delete",
                        fontSize = 15.sp,
                        color = Color.Red,
                        fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                        modifier =Modifier.padding(end = 10.dp)
                    )
                }
            },
            onClick = {
                onDelete()
                onDismiss()
            }
        )
    }
}


@Composable
fun CancelButton(title : String = "Skip",onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .height(55.dp),
        shape = RoundedCornerShape(55),
        border = BorderStroke(1.dp, Color(0xFF697383)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF181B1A)
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF181B1A),
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily(Font(R.font.urbanist_medium))
        )
    }
}


@Composable
fun PriorityImageTag(label: String, color: Color,borderColor : Color,backgroundColor : Color,icon : Int) {
    Box(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(50.dp))
            .background( backgroundColor, RoundedCornerShape(50.dp))
            .padding( horizontal = 8.dp),
        contentAlignment  = Alignment.Center
    ) {

        Row( verticalAlignment = Alignment.CenterVertically){

            Image(painter = painterResource( id= icon),
                contentDescription = null,
            )
            Spacer(Modifier.width(5.dp))

            Text(
                text = label,
                color = color,
                fontSize = 10.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium
            )
        }


    }
}


@Composable
fun DocumentItem(
    document: Document,
    onDownloadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 19.dp, vertical = 4.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D7F4))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_files_icon),
                    contentDescription = "File Icon",
                    modifier = Modifier.size(41.dp, 55.dp)
                )
                Text(
                    text = document.fileName,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4338CA)
                )
            }

            IconButton(
                onClick = onDownloadClick,
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_download_icon),
                    contentDescription = "Download"
                )
            }
        }
    }
}





/*@Composable
fun DocumentItem2(
    label: String,
    uri : String = "",
    onDownloadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
        ,
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D7F4))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_files_icon),
                    contentDescription = "File Icon",
                    modifier = Modifier.size(41.dp, 55.dp)
                )
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4338CA)
                )
            }

            IconButton(
                onClick = onDownloadClick,
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_download_icon),
                    contentDescription = "Download"
                )
            }
        }
    }
}*/


@Composable
fun DocumentItem2(
    label: String,
    uri: String = "",
    onDownloadClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D7F4))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEFT SIDE (icon + text)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_files_icon),
                    contentDescription = "File Icon",
                    modifier = Modifier.size(41.dp, 55.dp)
                )

                Text(
                    text = label.substringAfterLast("/"),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF4338CA),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // RIGHT SIDE (download icon always visible)
            IconButton(
                onClick = onDownloadClick,
                modifier = Modifier.size(48.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_download_icon),
                    contentDescription = "Download"
                )
            }
        }
    }
}


@Composable
fun SettingOptionItem(
    icon: Int,
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White)
            .clickable( interactionSource = remember { MutableInteractionSource() },
                indication = null) { onClick() }
            .border(width = 1.dp, color = Color(0xFFCED4DA), shape = RoundedCornerShape(30.dp))
            .padding(horizontal = 10.dp, vertical = 13.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )


            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Image(
                painter = painterResource(id= R.drawable.ic_next_arrow_calender),
                contentDescription = null,

                modifier = Modifier.size(19.dp)
            )
        }
    }
}

