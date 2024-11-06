package com.android.sample.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.android.sample.R

val Montserrat =
    FontFamily(
        Font(R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_bold, FontWeight.Bold),
        Font(R.font.montserrat_light, FontWeight.Light))

val Typography =
    Typography(
        bodySmall =
            TextStyle(
                fontFamily = Montserrat,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            ),
        bodyMedium =
            TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.Normal, fontSize = 16.sp),
        bodyLarge =
            TextStyle(fontFamily = Montserrat, fontWeight = FontWeight.Normal, fontSize = 24.sp),
        titleLarge =
            TextStyle(
                fontFamily = Montserrat,
                fontWeight = FontWeight.Bold,
                fontSize = 60.sp,
                lineHeight = 20.sp,
                letterSpacing = 2.sp),
        titleMedium =
            TextStyle(
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.sp),
        titleSmall =
            TextStyle(
                fontFamily = Montserrat,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp),
        labelSmall =
            TextStyle(
                fontFamily = Montserrat,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp),
    )
