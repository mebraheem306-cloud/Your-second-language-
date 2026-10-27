package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OriginalPdfBadge
import com.example.ui.theme.SupplementaryBadge

@Composable
fun SourceBadge(
    isOriginal: Boolean,
    page: Int? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isOriginal) OriginalPdfBadge.copy(alpha = 0.12f) else SupplementaryBadge.copy(alpha = 0.12f),
        contentColor = if (isOriginal) OriginalPdfBadge else SupplementaryBadge,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = if (isOriginal) OriginalPdfBadge else SupplementaryBadge,
                        shape = RoundedCornerShape(3.dp)
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isOriginal) {
                    if (page != null) "Original Course PDF (p.$page)" else "Original Course PDF"
                } else "Supplementary",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
