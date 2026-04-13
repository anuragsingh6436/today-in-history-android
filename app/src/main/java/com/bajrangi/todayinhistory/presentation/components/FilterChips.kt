package com.bajrangi.todayinhistory.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bajrangi.todayinhistory.presentation.theme.IceBlue
import com.bajrangi.todayinhistory.presentation.theme.IceBlueDeep

/**
 * Horizontal scrollable filter chip row.
 *
 * Selected chip: filled IceBlue background.
 * Unselected chip: outlined with subtle border.
 */
@Composable
fun FilterChipRow(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.padding(start = 4.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { option ->
                FilterChip(
                    text = option,
                    isSelected = option == selected,
                    onClick = { onSelect(option) },
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) IceBlue.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = tween(200),
        label = "chipBg",
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) IceBlue.copy(alpha = 0.4f)
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
        animationSpec = tween(200),
        label = "chipBorder",
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) IceBlue
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        animationSpec = tween(200),
        label = "chipText",
    )

    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        ),
        color = textColor,
        modifier = modifier
            .clip(shape)
            .background(bgColor)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
