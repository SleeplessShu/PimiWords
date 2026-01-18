package com.sleeplessdog.matchthewords.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sleeplessdog.matchthewords.R

val myFontFamily = FontFamily(
    Font(R.font.a_bee_zee_regular, FontWeight.W400, FontStyle.Normal),
    Font(R.font.open_sans_bold, FontWeight.W700, FontStyle.Normal),
    Font(R.font.open_sans_bold, FontWeight.W600, FontStyle.Normal),
)
val textSize24Medium = TextStyle(
    fontStyle = FontStyle.Normal,
    fontWeight = FontWeight.Normal,
    fontFamily = myFontFamily,
    letterSpacing = 0.sp,
    lineHeight = 24.sp,
    fontSize = 24.sp,
)

val textSize20Medium = TextStyle(
    fontStyle = FontStyle.Normal,
    fontWeight = FontWeight.Normal,
    fontFamily = myFontFamily,
    letterSpacing = 0.sp,
    lineHeight = 20.sp,
    fontSize = 20.sp,
)
val textSize16Bold = TextStyle(
    fontStyle = FontStyle.Normal,
    fontWeight = FontWeight.Bold,
    fontFamily = myFontFamily,
    letterSpacing = 0.sp,
    lineHeight = 21.sp,
    fontSize = 16.sp,
)
val textSize16SemiBold = TextStyle(
    fontStyle = FontStyle.Normal,
    fontWeight = FontWeight.SemiBold,
    fontFamily = myFontFamily,
    letterSpacing = 0.sp,
    lineHeight = 19.2.sp,
    fontSize = 16.sp,
)
val textSize14SemiBold = TextStyle(
    fontStyle = FontStyle.Normal,
    fontWeight = FontWeight.SemiBold,
    fontFamily = myFontFamily,
    letterSpacing = 0.sp,
    lineHeight = 18.sp,
    fontSize = 14.sp,
)