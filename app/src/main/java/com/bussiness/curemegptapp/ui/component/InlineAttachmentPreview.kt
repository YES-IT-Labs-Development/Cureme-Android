package com.bussiness.curemegptapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.AsyncImage
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.bussiness.curemegptapp.data.model.PdfData
import android.net.Uri
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.bussiness.curemegptapp.R

@Composable
fun InlineAttachmentPreview(
    images: List<Uri>,
    pdfs: List<PdfData>,
    onRemoveImage: (Uri) -> Unit,
    onRemovePdf: (PdfData) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp,start = 6.dp, end = 6.dp)
            .wrapContentHeight(),

        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        images.forEach { uri ->
            Box(modifier = Modifier.size(64.dp)) {
               // AsyncImage(model = uri, contentDescription = null, modifier = Modifier.height(108.dp).width(106.dp).clip(RoundedCornerShape(12.dp),))
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .height(108.dp)
                        .width(106.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop      // ⭐ सबसे जरूरी लाइन
                )

                //                Surface(
//                    modifier = Modifier.align(Alignment.TopEnd).size(20.dp).clickable(  interactionSource = remember { MutableInteractionSource() },
//                        indication = null ){ onRemoveImage(uri) },
//                    shape = CircleShape,
//                    color = Color.Black.copy(alpha = 0.6f)
//                ) {
                  Icon(painter = painterResource(id = R.drawable.ic_close),
                      contentDescription = null,
                      tint = Color.Unspecified,
                      modifier = Modifier.padding(2.dp).align(Alignment.TopEnd).size(20.dp).clickable(  interactionSource = remember { MutableInteractionSource() },
                      indication = null ){ onRemoveImage(uri) })
               // }
            }
        }

        pdfs.forEach { pdf ->
            Surface(modifier = Modifier.size(64.dp), shape = RoundedCornerShape(12.dp), color = Color(0xFFEDEDED)) {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
//                    Icon(painter = painterResource(R.drawable.), contentDescription = null)
                    Text(text = pdf.name, maxLines = 1, fontSize = 5.sp)
                }
                Box(modifier = Modifier.fillMaxSize()) {
//                    Surface(modifier = Modifier.align(Alignment.TopEnd).size(20.dp).clickable( interactionSource = remember { MutableInteractionSource() },
//                        indication = null){ onRemovePdf(pdf) }, shape = CircleShape, color = Color.Black.copy(alpha = 0.6f)) {
////                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.padding(2.dp))
//                    }
                    Icon(painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(2.dp).align(Alignment.TopEnd).size(20.dp).clickable(  interactionSource = remember { MutableInteractionSource() },
                            indication = null ){ onRemovePdf(pdf)})
                }
            }
        }
    }
}