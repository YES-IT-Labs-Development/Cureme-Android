package com.bussiness.curemegptapp.ui.dialog



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.rememberNavController
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.ui.component.GradientButton
import com.bussiness.curemegptapp.ui.component.GradientButton3

@Composable
fun MemberProfileUpdatedDialog(
    title : String,
    description: String,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit
) {

    Dialog(onDismissRequest = { onDismiss() },   properties = DialogProperties(
        dismissOnClickOutside = false, // 🔴 IMPORTANT
        dismissOnBackPress = false       // back press se band chahiye to true
    )) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth().padding(vertical = 10.dp),
                //horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth().padding(horizontal = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Row (Title + Close Button)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Image(
                                painter = painterResource(id = R.drawable.ic_check), // add your check icon
                                contentDescription = "Success",
                                modifier = Modifier.size(42.dp)
                            )


                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = title,
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    modifier = Modifier.width(160.dp)
                                )
                            }
                        }


                        Image(
                            painter = painterResource(id = R.drawable.ic_close), // your close icon
                            contentDescription = "Close",
                            modifier = Modifier.size(45.dp).clickable(interactionSource = remember { MutableInteractionSource() },
                       indication = null){onDismiss()}
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
                Text(
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    color = Color(0xFF050505),
                    modifier = Modifier
                        .padding(start = 18.dp,end = 10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                GradientButton3(
                    text = "OK",
                    onClick = {
                        onOkClick()
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun MemberProfileUpdatedDialogPreview() {
    MemberProfileUpdatedDialog(title = "Password updated \nSuccessfully!", description = "Your password has been updated.", onDismiss ={},onOkClick={} )
}
