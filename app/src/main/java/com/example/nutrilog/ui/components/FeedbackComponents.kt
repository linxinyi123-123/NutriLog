package com.example.nutrilog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.nutrilog.ui.theme.AppColors
import com.example.nutrilog.ui.theme.AppTypography

// 反馈浮动按钮组件
@Composable
fun FeedbackButton(
    onFeedbackRequested: () -> Unit
) {
    FloatingActionButton(
        onClick = onFeedbackRequested,
        modifier = Modifier
            .size(56.dp)
            .semantics {
                contentDescription = "提供反馈"
            },
        containerColor = AppColors.Secondary
    ) {
        Icon(Icons.Default.Feedback, "反馈", tint = Color.White)
    }
}

// 反馈对话框组件
@Composable
fun FeedbackDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (feedback: String, rating: Int) -> Unit
) {
    if (isVisible) {
        val feedbackTextState = remember { mutableStateOf("") }
        val ratingState = remember { mutableStateOf(3) }
        
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("提供反馈", style = AppTypography.h2)
            },
            text = {
                Column {
                    // 评分
                    Text("请评价此功能:", style = AppTypography.body1)
                    
                    StarRating(
                        rating = ratingState.value,
                        onRatingChanged = { ratingState.value = it },
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    
                    // 反馈输入
                    TextField(
                        value = feedbackTextState.value,
                        onValueChange = { feedbackTextState.value = it },
                        label = { Text("具体意见或建议") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        singleLine = false
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSubmit(feedbackTextState.value, ratingState.value)
                        onDismiss()
                    },
                    enabled = feedbackTextState.value.isNotEmpty()
                ) {
                    Text("提交")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("取消")
                }
            }
        )
    }
}

// 星级评分组件
@Composable
fun StarRating(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.Star,
                contentDescription = "评分 $i",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        onRatingChanged(i)
                    },
                tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray
            )
        }
    }
}
