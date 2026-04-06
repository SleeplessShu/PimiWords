package com.sleeplessdog.pimi.utils

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sleeplessdog.pimi.R

// ===== Font Families =====

val openSansFamily = FontFamily(
    Font(R.font.open_sans_bold, FontWeight.Bold)
)

val mursGothicFamily = FontFamily(
    Font(R.font.murs_gothic_wide_dark, FontWeight.ExtraBold)
)

// ===== H — Murs Gothic Wide Dark =====

// H-1 Display Large — 62/62
val h1Display = TextStyle(
    fontFamily = mursGothicFamily,
    fontSize = 62.sp,
    lineHeight = 62.sp,
    fontWeight = FontWeight.ExtraBold,
    letterSpacing = 0.sp,
)

// H-2 Headline Large — 26/30
val h2Headline = TextStyle(
    fontFamily = mursGothicFamily,
    fontSize = 26.sp,
    lineHeight = 30.sp,
    fontWeight = FontWeight.ExtraBold,
    letterSpacing = (0.03 * 26).sp,
)

// H-3 Headline Small — 20/24
val h3Headline = TextStyle(
    fontFamily = mursGothicFamily,
    fontSize = 20.sp,
    lineHeight = 24.sp,
    fontWeight = FontWeight.ExtraBold,
    letterSpacing = 0.sp,
)

// ===== T — Open Sans Bold =====

// T-1 Title Large — 24/30
val t1Title = TextStyle(
    fontFamily = openSansFamily,
    fontSize = 24.sp,
    lineHeight = 30.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.sp,
)

// T-2 Title Small — 20/24
val t2Title = TextStyle(
    fontFamily = openSansFamily,
    fontSize = 20.sp,
    lineHeight = 24.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.sp,
)

// T-3 Text Large — 16/20
val t3Text = TextStyle(
    fontFamily = openSansFamily,
    fontSize = 16.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.sp,
)

// T-4 Text Small — 14/20
val t4Text = TextStyle(
    fontFamily = openSansFamily,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.sp,
)

val t4TextNumbers = TextStyle(
    fontFamily = openSansFamily,
    fontSize = 20.sp,
    lineHeight = 24.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.sp,
)


/*
val myFontFamily = FontFamily(
    Font(R.font.a_bee_zee_regular, FontWeight.W400, FontStyle.Normal),
    Font(R.font.open_sans_bold, FontWeight.W700, FontStyle.Normal),
    Font(R.font.open_sans_bold, FontWeight.W600, FontStyle.Normal),
)

val openSansFamily = FontFamily(
    Font(R.font.open_sans_bold, FontWeight.Bold)
)
val mursGothicFamily = FontFamily(
    Font(R.font.murs_gothic_wide_dark, FontWeight.ExtraBold)
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

val textSize24Bold = TextStyle(
    fontStyle = FontStyle.Normal,
    fontWeight = FontWeight.Bold,
    fontFamily = myFontFamily,
    letterSpacing = 0.sp,
    lineHeight = 30.sp,
    fontSize = 24.sp,
)

val h1Header = TextStyle(
    fontFamily = mursGothicFamily,
    fontSize = 30.sp,
    lineHeight = 24.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.sp,
    fontStyle = FontStyle.Normal
)
val h2Header = TextStyle(
    fontFamily = mursGothicFamily,
    fontSize = 24.sp,
    lineHeight = 24.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.sp,
    fontStyle = FontStyle.Normal
)
val t4Text = TextStyle(
    fontFamily = openSansFamily,
    fontSize = 18.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Thin,
    letterSpacing = 0.sp,
    fontStyle = FontStyle.Normal
)
val t4TextNumbers = TextStyle(
    fontFamily = openSansFamily,
    fontSize = 20.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.sp,
    fontStyle = FontStyle.Normal
)

val t5Text = TextStyle(
    fontFamily = openSansFamily,
    fontSize = 12.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.sp,
    fontStyle = FontStyle.Normal
)*/
