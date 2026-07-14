package com.bussiness.curemegptapp.ui.component

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import com.bussiness.curemegptapp.R
import com.bussiness.curemegptapp.data.model.StepItem
import com.bussiness.curemegptapp.ui.component.input.CustomPowerSpinner


@Composable
fun ProfileInputField(
    label: String,
    isImportant: Boolean = false,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    enable :Boolean = true,
    showVerifyBadge: Boolean = false,
    onVerifyClick: () -> Unit = {},
    isBold :Boolean = false
) {

    val shape = RoundedCornerShape(30.dp)

    Column(modifier = modifier.fillMaxWidth()) {

        // 🔹 Label Text (top of field)

        Row {
            Text(
                text = buildLabelWithOptional(label),
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)
            )
            if (isImportant) {
                Text(
                    text = "*",
                    fontSize = 15.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

        }

        // 🔹 TextField
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enable,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp)
                .clip(shape)
                .border(1.dp, Color(0xFF697383), shape)
                .background(Color.White),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color(0xFFB8B9BD),
                                fontSize = 13.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                                fontWeight = FontWeight.Normal,
                            )
                        }
                        innerTextField()
                    }
                    if (showVerifyBadge) {
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF4CAF50))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable(interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onVerifyClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "VERIFY",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}



@Composable
fun ProfileInputField2(
    label: String,
    isImportant: Boolean = false,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {

    val shape = RoundedCornerShape(30.dp)

    Column(modifier = modifier.fillMaxWidth()) {
        Row {
            Text(
                text = buildLabelWithOptional(label),
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)
            )
            if (isImportant) {
                Text(
                    text = "*",
                    fontSize = 15.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

        }

        // 🔹 TextField
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF697383),
                    fontSize = 15.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                )
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp)
                .clip(shape)
                .border(1.dp, Color(0xFF697383), shape),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color(0xFFB8B9BD),
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

@Composable
fun ProfileInputMultipleLineField(
    label: String,
    isImportant: Boolean = false,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isBold: Boolean =false
) {

    val shape = RoundedCornerShape(30.dp)

    Column(modifier = modifier.fillMaxWidth()) {

        // 🔹 Label Text (top of field)

        Row {
            Text(
                text = buildLabelWithOptional(label),
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)
            )
            if (isImportant) {
                Text(
                    text = "*",
                    fontSize = 15.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

        }

        // 🔹 TextField
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFFB8B9BD),
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp
                )
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(horizontal = 8.dp)
                .clip(shape)
                .border(1.dp, Color(0xFFC3C6CB), shape),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

@Composable
fun ProfileInputMultipleLineField2(
    label: String,
    isImportant: Boolean = false,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    heightOfEditText: Dp = 90.dp,
    paddingHorizontal: Dp = 90.dp,
    keyboardType: KeyboardType = KeyboardType.Text,
    borderColor: Color = Color(0xFFC3C6CB),
    textColor: Color = Color(0xFFB8B9BD),
    textStartPadding : Dp = 12.dp
) {

    val shape = RoundedCornerShape(30.dp)

    Column(modifier = modifier.fillMaxWidth()) {

        // 🔹 Label Text (top of field)

        Row {
            Text(
                text = label,
                fontSize = 15.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = textStartPadding, bottom = 6.dp)
            )
            if (isImportant) {
                Text(
                    text = "*",
                    fontSize = 15.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

        }

        // 🔹 TextField
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = textColor,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp
                )
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(heightOfEditText)
                .padding(horizontal = paddingHorizontal)
                .clip(shape)
                .border(1.dp, borderColor, shape),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

@Composable
fun ProfileInputWithoutLabelField(
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {

    val shape = RoundedCornerShape(30.dp)

    Column(modifier = modifier.fillMaxWidth()) {


        // 🔹 TextField
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFB8B9BD),
                    fontSize = 16.sp
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp)
                .clip(shape)
                .border(1.dp, Color(0xFFC3C6CB), shape),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}


@Composable
fun GenderDropdown(
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row {


            Text(
                text = "Gender",
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = " *",
                color = Color.Red,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 6.dp)
            )

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFF697383),
                    shape = RoundedCornerShape(50.dp)
                )
                .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { expanded = true }
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    text = if (selected.isEmpty()) "Select" else selected,
                    color = if (selected.isEmpty()) Color(0xFF697383) else Color(0xFF697383),
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_dropdown_icon),
                    contentDescription = null,
                    modifier = Modifier.size(8.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            DropdownMenuItem(
                text = { Text("Male") },
                onClick = {
                    onSelected("Male")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Female") },
                onClick = {
                    onSelected("Female")
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = { Text("Other") },
                onClick = {
                    onSelected("Other")
                    expanded = false
                }
            )
        }
    }
}


@Composable
fun Dropdown1(
    label: String,
    isImportant: Boolean = false,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    dropdownItems: List<String>,
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Number
) {

    Log.d("TESTING_PROFILE_COMPLETION","Value is "+value)

    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(30.dp)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 🔹 Label Row
        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = buildLabelWithOptional(label),
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            if (isImportant) {

                Text(
                    text = "*",
                    fontSize = 15.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

            }

        }

        // 🔹 INPUT + DROPDOWN ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEFT TEXTFIELD
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeholder,
                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF697383),
                        fontSize = 15.sp
                    )
                },
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal
                ),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(shape)
                    .border(1.dp, Color(0xFF697383), shape),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.width(10.dp))

//            // RIGHT SMALL DROPDOWN
//            Box(
//                modifier = Modifier
//                    .width(90.dp)
//                    .height(56.dp)
//                    .clip(RoundedCornerShape(30.dp))
//                    .border(
//                        1.dp,
//                        Color(0xFFBFC5D2),
//                        RoundedCornerShape(30.dp)
//                    )
//                    .clickable( interactionSource = remember { MutableInteractionSource() },
            //                     indication = null){ expanded = true }
//                    .padding(horizontal = 16.dp),
//                contentAlignment = Alignment.CenterStart
//            ) {
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//
//                    Text(
//                        text = selectedUnit,
//                        fontFamily = FontFamily(Font(R.font.urbanist_regular)),
//                        fontWeight = FontWeight.Normal,
//                        color = Color.Black,
//                        fontSize = 15.sp
//                    )
//
//                    Image(
//                        painter =painterResource(id = R.drawable.ic_dropdown_icon),
//                        contentDescription = null,
//                    )
//                }
//            }

            CustomPowerSpinner(
                modifier = Modifier
                    .width(90.dp)
                    .height(56.dp),
                modifierDropDown = Modifier.width(140.dp),
                selectedText = selectedUnit,
                onSelectionChanged = { reason ->
                    onUnitSelected(reason)
                },
                horizontalPadding = 24.dp,
                reasons = dropdownItems // Pass the list of options here
            )
        }

        // 🔹 DROPDOWN MENU
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            dropdownItems.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onUnitSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun StepTabs(
    steps: List<StepItem>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onStepClick: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, step ->

            val isSelected = index <= selectedIndex - 1
            val isSelectedPrevious = index <= selectedIndex

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onStepClick(index) }
            ) {

                // Circle with icon
                val iconRes = if (isSelected) {
                    step.selectedIcon
                } else if (index == selectedIndex) {
                    step.icon
                } else {
                    if (step.icon == R.drawable.ic_histry) {
                        R.drawable.ic_fad_histrory
                    } else if (step.icon == R.drawable.ic_docum) {
                        R.drawable.ic_fad_doc
                    } else {
                        step.icon
                    }
                }
                val isCustomGeneralIcon = iconRes == R.drawable.ic_gnrl_icn
                val isHistoryOrDoc = (
                    iconRes == R.drawable.ic_histry ||
                    iconRes == R.drawable.ic_docum ||
                    iconRes == R.drawable.ic_fad_histrory ||
                    iconRes == R.drawable.ic_fad_doc
                )

                if (isHistoryOrDoc) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = step.title,
                        modifier = Modifier.size(42.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color(0xFF3D35EC) else if (isSelectedPrevious) Color(
                                    0xFF2D2587
                                )
                                else Color(0xFFF2F2F2)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = step.title,
                            modifier = if (isCustomGeneralIcon) Modifier.size(24.dp) else Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Title
                Text(
                    text = step.title,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    color = if (isSelected) Color(0xFF4338CA) else if (isSelectedPrevious) Color(
                        0xFF4338CA
                    ) else Color(0xFFCED4DA)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TopBarHeader(
    currentStep: Int,
    onBackClick: () -> Unit,
    title: String = "Complete Your Profile",
    skipDisplay: Boolean = true,
    onSkipClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_cross_icon),
                contentDescription = "Back Icon",
                modifier = Modifier
                    .size(42.dp)
                    .clickable(interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBackClick() }
            )
            Spacer(Modifier.width(19.dp))

            Text(
                text = title,
                fontSize = 17.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            if (skipDisplay && onSkipClick != null) {
                Text(
                    text = "Skip for now",
                    color = Color(0xFF211C64),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSkipClick() }
                )
            } else if (skipDisplay && currentStep > 3) {
                Text(
                    text = "Skip for Now",
                    color = Color(0xFF211C64),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { }
                )
            }


        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step tabs using the new StepTabs component
        val steps = listOf(
            StepItem("Personal", R.drawable.ic_person_complete_icon, R.drawable.ic_check),
            StepItem("General", R.drawable.ic_gnrl_icn, R.drawable.ic_check),
            StepItem("History", R.drawable.ic_histry, R.drawable.ic_check),
            StepItem("Documents", R.drawable.ic_docum, R.drawable.ic_check)
        )

        StepTabs(
            steps = steps,
            selectedIndex = currentStep,
            onStepClick = { /* You can handle step click if needed */ }
        )
    }
}


@Composable
fun ProfilePhotoPicker(
    label: String,
    fileName: String = "No file chosen",
    buttonBgResId: Int? = null,
    onChooseClick: () -> Unit
) {
    Column {

        Text(
            text = buildLabelWithOptional1(label),
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            fontWeight = FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clip(RoundedCornerShape(40.dp))
                .drawBehind {
                    val stroke = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
                    )
                    drawRoundRect(
                        color = Color(0xFFBFC5D2),
                        style = stroke,
                        cornerRadius = CornerRadius(40.dp.toPx(), 40.dp.toPx())
                    )
                }
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (buttonBgResId != null) {
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(33.dp)
                            .clip(RoundedCornerShape(45.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onChooseClick
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = buttonBgResId),
                            contentDescription = null,
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.FillBounds
                        )
                        Text(
                            text = "Choose File",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily(Font(R.font.urbanist_medium))
                        )
                    }
                } else {
                    GradientButton2(
                        text = "Choose File",
                        onClick = onChooseClick,
                        modifier = Modifier.width(130.dp),
                        paddingHorizontal = 0.dp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = fileName,
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    color = Color(0xFF697383)
                )
            }
        }
    }
}

@Composable
fun BloodGroupDropdown(
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {

        Text(
            text = "Blood Group *",
            color = Color.Black,
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFFBFC5D2),
                    shape = RoundedCornerShape(50.dp)
                )
                .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { expanded = true }
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal,
                    text = if (selected.isEmpty()) "Select" else selected,
                    color = if (selected.isEmpty()) Color.Gray else Color.Black
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_dropdown_icon),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-").forEach { group ->
                DropdownMenuItem(
                    text = { Text(group) },
                    onClick = {
                        onSelected(group)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun FileAttachment(
    fileName: String,
    onDeleteClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
            .background(Color(0xFFDCD6FF))  // Light purple background like screenshot
            .padding(horizontal = 5.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Image(
            painter = painterResource(id = R.drawable.ic_files_icon),
            contentDescription = "File Icon",
            modifier = Modifier.size(48.dp)
        )


        Spacer(modifier = Modifier.width(16.dp))

        // FILE NAME
        Text(
            text = fileName,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.urbanist_regular)),
            fontWeight = FontWeight.Normal,
            color = Color(0xFF4A3AFF),   // Purple file name
            modifier = Modifier.weight(1f)
        )

        // RIGHT delete button

        Image(
            painter = painterResource(id = R.drawable.ic_delete_icon),
            contentDescription = "Delete",
            modifier = Modifier
                .size(48.dp)
                .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDeleteClick() }
        )

    }
}

@Composable
fun UniversalInputField(
    wholePadding : Dp = 5.dp,
    textStartPadding: Dp =  5.dp,
    title: String,
    isImportant: Boolean = false,
    placeholder: String,
    value: String,
    rightIcon: Int? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight().padding(wholePadding)
    ) {
        // ----- Title -----
        Row {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = textStartPadding, bottom = 8.dp)
            )

            if (isImportant)
                Text(
                    text = " *",
                    fontSize = 13.sp,
                    color = Color.Red,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
        }

        // ----- Clickable Input Box -----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.Transparent)
                .border(
                    width = 1.dp,
                    color = Color(0xFF697383),
                    shape = RoundedCornerShape(56.dp)
                )
                .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() },
            contentAlignment = Alignment.CenterStart
        ) {

            // Placeholder or Value
            Text(
                text = if (value.isEmpty()) placeholder else value,
                fontSize = 13.sp,
                color = if (value.isEmpty()) Color(0xFF697383) else Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Right Icon (optional)
            rightIcon?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(19.dp)
                        .clickable(interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onClick() }
                )
            }
        }
    }
}

@Composable
fun CancelButton(
    cancelText: String = "Cancel",
    modifier: Modifier = Modifier.width(120.dp).height(45.dp),
    paddingHorizontal: Dp = 18.dp,
    fontSize: TextUnit = 14.sp,
    fontFamily: FontFamily = FontFamily(Font(R.font.onest_medium)),
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .border(
                1.dp,
                Color(0xFF181B1A),
                RoundedCornerShape(28.dp)
            )
            .clickable(interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = paddingHorizontal),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = cancelText,
            fontFamily = FontFamily(Font(R.font.urbanist_medium)),
            color = Color.Black,
            fontSize = fontSize
        )
    }
}


@Composable
fun TopBarHeader1(title: String, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_cross_icon),
                contentDescription = "Back Icon",
                modifier = Modifier
                    .size(42.dp)
                    .clickable(interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBackClick() }
            )
            Spacer(Modifier.width(19.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium
            )

        }


        // HorizontalDivider(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFEBE1FF)))
        Divider(color = Color(0xFFEBE1FF), thickness = 1.dp)
    }
}

@Composable
fun TopBarHeader2(title: String, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_cross_icon),
                contentDescription = "Back Icon",
                modifier = Modifier
                    .size(42.dp)
                    .clickable(interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBackClick() }
            )
            Spacer(Modifier.width(19.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                fontWeight = FontWeight.Medium
            )

        }

    }
}


@Composable
fun ProfileInputSmallField(
    label: String,
    isImportant: Boolean = false,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {

    val shape = RoundedCornerShape(30.dp)

    Column(modifier = modifier.fillMaxWidth()) {

        // 🔹 Label Text (top of field)

        Row {
            Text(
                text = label,
                fontSize = 15.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            if (isImportant) {
                Text(
                    text = "*",
                    fontSize = 15.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

        }

        // 🔹 TextField
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFF697383),
                    fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                    fontWeight = FontWeight.Normal, fontSize = 13.sp
                )
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 13.sp,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(shape)
                .border(1.dp, Color(0xFF697383), shape),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}


@Composable
fun UniversalInputField1(
    title: String,
    textSize : TextUnit = 14.sp,
    isImportant: Boolean = false,
    placeholder: String,
    value: String,
    rightIcon: Int? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight().padding(5.dp)
    ) {
        // ----- Title -----
        Row {
            Text(
                text = title,
                fontSize = textSize,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = 5.dp, bottom = 8.dp)
            )

            if (isImportant)
                Text(
                    text = " *",
                    fontSize = 13.sp,
                    color = Color.Red,
                    fontFamily = FontFamily(Font(R.font.urbanist_medium)),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
        }

        // ----- Clickable Input Box -----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.Transparent)
                .border(
                    width = 1.dp,
                    color = Color(0xFF697383),
                    shape = RoundedCornerShape(56.dp)
                )
                .clickable(interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() },
            contentAlignment = Alignment.CenterStart
        ) {

            // Placeholder or Value
            Text(
                text = if (value.isEmpty()) placeholder else value,
                fontSize = 13.sp,
                color = if (value.isEmpty()) Color(0xFF697383) else Color.Black,
                fontFamily = FontFamily(Font(R.font.urbanist_regular)),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Right Icon (optional)
            rightIcon?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(19.dp)
                        .clickable(interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onClick() }
                )
            }
        }
    }
}


fun buildLabelWithOptional(label: String): AnnotatedString {
    return buildAnnotatedString {
        val optionalText = "(Optional)"

        if (label.contains(optionalText)) {
            val start = label.indexOf(optionalText)
            val end = start + optionalText.length

            // Normal text before Optional
            append(label.substring(0, start))

            // Italic Optional
            withStyle(
                SpanStyle(
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF000000)   // optional ko thoda light bhi kar diya
                )
            ) {
                append(optionalText)
            }

            // Text after Optional (if any)
            if (end < label.length) {
                append(label.substring(end))
            }
        } else {
            append(label)
        }
    }
}

fun buildLabelWithOptional1(label: String): AnnotatedString {
    return buildAnnotatedString {
        val optionalText = "(X-Rays, Dental Scans, Prescriptions, Lab Reports)"

        if (label.contains(optionalText)) {
            val start = label.indexOf(optionalText)
            val end = start + optionalText.length

            // Normal text before Optional
            append(label.substring(0, start))

            // Italic Optional
            withStyle(
                SpanStyle(
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF000000)   // optional ko thoda light bhi kar diya
                )
            ) {
                append(optionalText)
            }

            // Text after Optional (if any)
            if (end < label.length) {
                append(label.substring(end))
            }
        } else {
            append(label)
        }
    }
}

