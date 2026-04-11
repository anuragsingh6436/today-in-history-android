package com.bajrangi.todayinhistory.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bajrangi.todayinhistory.presentation.theme.Paper

/**
 * Giant year watermark — editorial style.
 *
 * Rendered in near-invisible white (not ice-blue) so it reads as
 * architectural typography rather than a colored decoration.
 * Positioned above center for visual weight at the top of the reel.
 */
@Composable
fun YearWatermark(
    year: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$year",
            fontSize = 180.sp,
            fontWeight = FontWeight.Black,
            color = Paper,
            letterSpacing = (-8).sp,
            modifier = Modifier
                .alpha(0.04f) // Even subtler — barely perceptible
                .offset(y = (-80).dp),
        )
    }
}
