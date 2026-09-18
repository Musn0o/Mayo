package com.musno.mayo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.musno.mayo.R

val ArabicFontFamily = FontFamily(
    Font(R.font.noto_naskh_arabic_regular, FontWeight.Normal),
)

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = ArabicFontFamily, fontSize = 57.sp),
    displayMedium = TextStyle(fontFamily = ArabicFontFamily, fontSize = 45.sp),
    displaySmall = TextStyle(fontFamily = ArabicFontFamily, fontSize = 36.sp),
    headlineLarge = TextStyle(fontFamily = ArabicFontFamily, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = ArabicFontFamily, fontSize = 28.sp),
    headlineSmall = TextStyle(fontFamily = ArabicFontFamily, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = ArabicFontFamily, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = ArabicFontFamily, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = ArabicFontFamily, fontSize = 14.sp),
    bodyLarge = TextStyle(fontFamily = ArabicFontFamily, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = ArabicFontFamily, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = ArabicFontFamily, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = ArabicFontFamily, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = ArabicFontFamily, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = ArabicFontFamily, fontSize = 11.sp),
)