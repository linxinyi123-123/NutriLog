package com.example.nutrilog.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.nutrilog.shared.NutritionFacts
import com.example.nutrilog.ui.theme.AppColors
import com.example.nutrilog.ui.theme.AppShapes
import com.example.nutrilog.ui.theme.AppTypography
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// 图表组件库

// 数据模型

// 饼图数据项
data class PieSlice(
    val label: String,
    val value: Float,
    val color: Color
)

// 雷达图数据点
data class RadarDataPoint(
    val label: String,
    val actual: Double,
    val target: Double
)

// 图表容器组件
@Composable
fun ChartContainer(
    modifier: Modifier = Modifier,
    title: String? = null,
    chart: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = AppShapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            title?.let {
                Text(
                    text = it,
                    style = AppTypography.h2,
                    color = AppColors.OnSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                chart()
            }
            
            // 图例（如果需要）
            ChartLegend()
        }
    }
}

// 图表图例组件
@Composable
fun ChartLegend(entries: List<Pair<String, Color>> = emptyList()) {
    if (entries.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            entries.forEach { (label, color) ->
                LegendItem(label = label, color = color)
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

// 图例项组件
@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = AppTypography.caption,
            color = AppColors.OnSurfaceVariant
        )
    }
}

// 营养环形图组件
@Composable
fun NutritionPieChart(nutrition: NutritionFacts) {
    val slices = listOf(
        PieSlice("蛋白质", nutrition.protein.toFloat(), AppColors.Protein),
        PieSlice("碳水", nutrition.carbs.toFloat(), AppColors.Carbs),
        PieSlice("脂肪", nutrition.fat.toFloat(), AppColors.Fat)
    )
    
    ChartContainer(
        modifier = Modifier.fillMaxWidth(),
        title = "三大营养素比例"
    ) {
        SimplePieChart(slices = slices)
    }
}

// 简易饼图实现
@Composable
fun SimplePieChart(
    slices: List<PieSlice>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val total = slices.sumOf { it.value.toDouble() }.toFloat()
        var startAngle = -90f // 从12点方向开始
        val center = Offset(size.width / 2, size.height / 2)
        val radius = minOf(size.width, size.height) * 0.35f
        val innerRadius = radius * 0.5f // 环形图的内半径
        
        slices.forEach { slice ->
            val sweepAngle = (slice.value / total) * 360f
            
            // 绘制环形
            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = radius - innerRadius)
            )
            
            // 绘制标签线和标签
            val labelAngle = startAngle + sweepAngle / 2
            val labelRadius = radius + 20f
            val cosValue = cos(labelAngle * Math.PI / 180.0).toFloat()
            val sinValue = sin(labelAngle * Math.PI / 180.0).toFloat()
            val labelX = center.x + labelRadius * cosValue
            val labelY = center.y + labelRadius * sinValue
            
            drawLine(
                color = slice.color,
                start = Offset(
                    center.x + radius * cosValue,
                    center.y + radius * sinValue
                ),
                end = Offset(labelX, labelY),
                strokeWidth = 2f
            )
            
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "${slice.label} ${((slice.value / total) * 100).toInt()}%",
                    labelX,
                    labelY,
                    android.graphics.Paint().apply {
                        color = slice.color.toArgb()
                        textSize = 14.0f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
            
            startAngle += sweepAngle
        }
    }
}

// 营养素雷达图组件
@Composable
fun NutrientRadarChart(actual: NutritionFacts, target: NutritionFacts) {
    val dataPoints = listOf(
        RadarDataPoint("热量", actual.calories, target.calories),
        RadarDataPoint("蛋白质", actual.protein, target.protein),
        RadarDataPoint("碳水", actual.carbs, target.carbs),
        RadarDataPoint("脂肪", actual.fat, target.fat),
        RadarDataPoint("纤维", actual.fiber ?: 0.0, target.fiber ?: 30.0),
        RadarDataPoint("糖", actual.sugar ?: 0.0, target.sugar ?: 50.0)
    )
    
    ChartContainer(
        modifier = Modifier.fillMaxWidth(),
        title = "营养素达成率"
    ) {
        SimpleRadarChart(
            dataPoints = dataPoints,
            maxValue = 150f, // 显示0-150%
            gridSteps = 3
        )
    }
}

// 简易雷达图实现
@Composable
fun SimpleRadarChart(
    dataPoints: List<RadarDataPoint>,
    maxValue: Float = 100f,
    gridSteps: Int = 3
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = minOf(size.width, size.height) * 0.4f
        val pointCount = dataPoints.size
        
        // 绘制网格
        for (i in 1..gridSteps) {
            val gridRadius = radius * i / gridSteps
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.2f),
                center = center,
                radius = gridRadius,
                style = Stroke(width = 1f)
            )
        }
        
        // 绘制轴线
        for (i in 0 until pointCount) {
            val angle = 2f * Math.PI.toFloat() * i / pointCount
            val x = center.x + radius * cos(angle)
            val y = center.y + radius * sin(angle)
            
            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = center,
                end = Offset(x, y),
                strokeWidth = 1f
            )
        }
        
        // 绘制数据区域
        val path = Path()
        dataPoints.forEachIndexed { index, point ->
            val ratio = minOf((point.actual / point.target * 100.0), maxValue.toDouble()).toFloat() / maxValue
            val angle = 2f * Math.PI.toFloat() * index / pointCount
            val x = center.x + radius * ratio * cos(angle).toFloat()
            val y = center.y + radius * ratio * sin(angle).toFloat()
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        
        drawPath(
            path = path,
            color = AppColors.Primary.copy(alpha = 0.5f),
            style = Fill
        )
        drawPath(
            path = path,
            color = AppColors.Primary,
            style = Stroke(width = 2f)
        )
        
        // 绘制数据点
        dataPoints.forEachIndexed { index, point ->
            val ratio = minOf((point.actual / point.target * 100.0), maxValue.toDouble()).toFloat() / maxValue
            val angle = 2f * Math.PI.toFloat() * index / pointCount
            val x = center.x + radius * ratio * cos(angle).toFloat()
            val y = center.y + radius * ratio * sin(angle).toFloat()
            
            drawCircle(
                color = AppColors.Primary,
                center = Offset(x, y),
                radius = 4f
            )
        }
        
        // 绘制标签
        dataPoints.forEachIndexed { index, point ->
            val angle = 2f * Math.PI.toFloat() * index / pointCount
            val labelRadius = radius * 1.1f
            val x = center.x + labelRadius * cos(angle).toFloat()
            val y = center.y + labelRadius * sin(angle).toFloat()
            
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    point.label,
                    x,
                    y,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 14.0f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

// 类别柱状图组件
@Composable
fun CategoryBarChart(varietyData: Map<String, Double>) {
    ChartContainer(
        modifier = Modifier.fillMaxWidth(),
        title = "食物类别分布"
    ) {
        SimpleBarChart(data = varietyData)
    }
}

// 简易柱状图实现
@Composable
fun SimpleBarChart(data: Map<String, Double>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (data.isEmpty()) return@Canvas
        
        val maxValue = data.values.maxOrNull() ?: 100.0
        val barWidth = size.width / (data.size * 2)
        val maxBarHeight = size.height * 0.7f
        
        data.entries.forEachIndexed { index, (category, value) ->
            val barHeight = (value / maxValue).toFloat() * maxBarHeight
            val x = barWidth * (index * 2 + 0.5f)
            val y = size.height - barHeight
            
            // 绘制柱状
            drawRect(
                color = getCategoryColor(category),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
            
            // 绘制数值标签
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "${value.toInt()}%",
                    x + barWidth / 2,
                    y - 10,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 16.0f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
                
                // 绘制类别标签
                drawText(
                    getShortCategoryName(category),
                    x + barWidth / 2,
                    size.height - 5,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 14.0f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

// 辅助函数：获取类别颜色
fun getCategoryColor(category: String): Color {
    return when (category) {
        "谷薯类" -> Color(0xFF8BC34A)
        "蔬菜类" -> Color(0xFF4CAF50)
        "水果类" -> Color(0xFFFF9800)
        "蛋白质类" -> Color(0xFFF44336)
        "奶制品" -> Color(0xFF2196F3)
        "坚果类" -> Color(0xFF795548)
        "油脂类" -> Color(0xFFFFC107)
        else -> Color.Gray
    }
}

// 辅助函数：获取类别简称
fun getShortCategoryName(category: String): String {
    return when (category) {
        "谷薯类" -> "谷薯"
        "蔬菜类" -> "蔬菜"
        "水果类" -> "水果"
        "蛋白质类" -> "蛋白"
        "奶制品" -> "奶类"
        "坚果类" -> "坚果"
        "油脂类" -> "油脂"
        else -> category
    }
}
