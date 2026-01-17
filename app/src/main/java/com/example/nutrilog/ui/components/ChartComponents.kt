package com.example.nutrilog.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.delay
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.shared.NutritionFacts
import com.example.nutrilog.shared.TrendAnalysis
import com.example.nutrilog.ui.theme.AppColors
import com.example.nutrilog.ui.theme.AppShapes
import com.example.nutrilog.ui.theme.AppTypography
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlinx.coroutines.awaitCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.time.LocalDate
import kotlin.math.atan2
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

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

// 折线图数据点
data class LineDataPoint(
    val label: String,
    val value: Float,
    val extraData: Map<String, Float> = emptyMap()
)

// 环状图切片
data class DonutSlice(
    val label: String,
    val value: Float,
    val color: Color
)

// 图表容器组件
@Composable
fun ChartContainer(
    modifier: Modifier = Modifier,
    title: String? = null,
    legendEntries: List<Pair<String, Color>> = emptyList(),
    chartHeight: Dp = 250.dp,
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
                    .height(chartHeight)
            ) {
                chart()
            }
            
            // 图例（如果需要）
            ChartLegend(entries = legendEntries)
        }
    }
}

// 水平滚动包装器组件
@Composable
fun HorizontalScrollbarWrapper(content: @Composable () -> Unit) {
    val state = rememberScrollState()
    
    // 使用Row实现水平滚动
    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(state)
    ) {
        // 增加内容宽度，确保可以滚动
        Box(modifier = Modifier.width(1000.dp)) {
            content()
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
    
    // 定义对话框状态
    val showDialog = remember { mutableStateOf(false) }
    val selectedSlice = remember { mutableStateOf<PieSlice?>(null) }
    val total = slices.sumOf { it.value.toDouble() }.toFloat()
    
    ChartContainer(
        modifier = Modifier.fillMaxWidth(),
        title = "三大营养素比例",
        legendEntries = slices.map { Pair(it.label, it.color) }
    ) {
        SimplePieChart(
            slices = slices,
            onClick = { slice ->
                selectedSlice.value = slice
                showDialog.value = true
            }
        )
    }
    
    // 营养素详情对话框
    if (showDialog.value && selectedSlice.value != null) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "营养素详情") },
            text = {
                Column {
                    Text(text = "营养素: ${selectedSlice.value?.label}")
                    Text(text = "摄入量: ${selectedSlice.value?.value} 克")
                    Text(text = "占比: ${((selectedSlice.value?.value ?: 0f) / total * 100).toInt()}%")
                }
            },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text(text = "确定")
                }
            }
        )
    }
}

// 简易饼图实现
@Composable
fun SimplePieChart(
    slices: List<PieSlice>,
    modifier: Modifier = Modifier,
    onClick: (PieSlice) -> Unit = {}
) {
    // 定义选中的扇形索引
    val selectedIndex = remember { mutableStateOf(-1) }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {offset ->
                    val total = slices.sumOf { it.value.toDouble() }.toFloat()
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = minOf(size.width, size.height) * 0.35f
                    
                    // 计算点击位置与圆心的角度
                    val dx = offset.x - center.x
                    val dy = offset.y - center.y
                    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                    if (angle < 0) angle += 360f
                    
                    // 从12点方向开始计算
                    angle = (angle + 90) % 360
                    
                    // 查找点击的扇形
                    var currentAngle = 0f
                    slices.forEachIndexed { index, slice ->
                        val sweepAngle = (slice.value / total) * 360f
                        if (angle >= currentAngle && angle < currentAngle + sweepAngle) {
                            selectedIndex.value = index
                            onClick(slice)
                        }
                        currentAngle += sweepAngle
                    }
                }
            }
    ) {
        val total = slices.sumOf { it.value.toDouble() }.toFloat()
        var startAngle = -90f // 从12点方向开始
        val center = Offset(size.width / 2, size.height / 2)
        val radius = minOf(size.width, size.height) * 0.35f
        val innerRadius = radius * 0.5f // 环形图的内半径
        
        // 避免除以零，当total为0时不绘制
        if (total > 0) {
            slices.forEachIndexed { index, slice ->
                val sweepAngle = (slice.value / total) * 360f
                
                // 如果是选中的扇形，稍微扩大半径
                val currentRadius = if (index == selectedIndex.value) radius * 1.1f else radius
                
                // 绘制环形
                drawArc(
                    color = slice.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - currentRadius, center.y - currentRadius),
                    size = Size(currentRadius * 2, currentRadius * 2),
                    style = Stroke(width = currentRadius - innerRadius)
                )
                
                // 绘制标签线和标签
                val labelAngle = startAngle + sweepAngle / 2
                val labelRadius = currentRadius + 20f
                val cosValue = cos(labelAngle * Math.PI / 180.0).toFloat()
                val sinValue = sin(labelAngle * Math.PI / 180.0).toFloat()
                val labelX = center.x + labelRadius * cosValue
                val labelY = center.y + labelRadius * sinValue
                
                drawLine(
                    color = slice.color,
                    start = Offset(
                        center.x + currentRadius * cosValue,
                        center.y + currentRadius * sinValue
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
                            textSize = 18.0f
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

// Y轴标签组件
@Composable
fun YAxisLabels(maxValue: Float, modifier: Modifier) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(5) {
            val value = maxValue - (maxValue / 4) * it
            Text(
                text = "${value.toInt()}",
                style = AppTypography.caption,
                color = AppColors.OnSurfaceVariant
            )
        }
    }
}

// 绘制网格
fun DrawScope.drawGrid() {
    val gridColor = AppColors.OnSurfaceVariant.copy(alpha = 0.1f)
    val strokeWidth = 1f
    
    // 垂直网格线
    repeat(5) {
        val x = (size.width / 4) * it
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = strokeWidth
        )
    }
    
    // 水平网格线苹果
    repeat(5) {
        val y = (size.height / 4) * it
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = strokeWidth
        )
    }
}

// 预处理数据点，减少绘制时的计算
data class ProcessedPoint(
    val x: Float,
    val y: Float,
    val originalPoint: LineDataPoint
)

// 预处理数据，减少绘制时的计算
fun processDataForRendering(dataPoints: List<LineDataPoint>, width: Float, height: Float): List<ProcessedPoint> {
    if (dataPoints.isEmpty()) return emptyList()
    
    val maxValue = dataPoints.maxOf { it.value }
    val minValue = dataPoints.minOf { it.value }
    val valueRange = if (maxValue > minValue) maxValue - minValue else 1f
    
    return dataPoints.mapIndexed { index, point ->
        val x = (index.toFloat() / (dataPoints.size - 1)) * width
        val y = height - ((point.value - minValue) / valueRange) * height
        ProcessedPoint(x, y, point)
    }
}

// 采样数据，减少绘制点数
fun <T> List<T>.sampleEveryN(n: Int): List<T> {
    if (isEmpty()) return this
    return this.filterIndexed { index, _ -> index % n == 0 }
}

// 绘制折线
fun DrawScope.drawOptimizedLine(
    processedPoints: List<ProcessedPoint>,
    lineColor: Color,
    fillColor: Color
) {
    if (processedPoints.isEmpty()) return
    
    // 绘制填充区域
    val path = Path().apply {
        moveTo(processedPoints.first().x, size.height)
        for (point in processedPoints) {
            lineTo(point.x, point.y)
        }
        lineTo(processedPoints.last().x, size.height)
        close()
    }
    drawPath(path, fillColor)
    
    // 绘制折线
    drawPath(
        path = Path().apply {
            moveTo(processedPoints.first().x, processedPoints.first().y)
            for (point in processedPoints.drop(1)) {
                lineTo(point.x, point.y)
            }
        },
        color = lineColor,
        style = Stroke(width = 2f)
    )
}

// 绘制数据点
fun DrawScope.drawOptimizedDataPoints(processedPoints: List<ProcessedPoint>, color: Color) {
    if (processedPoints.isEmpty()) return
    
    for (point in processedPoints) {
        drawCircle(
            color = color,
            center = Offset(point.x, point.y),
            radius = 4f
        )
        drawCircle(
            color = AppColors.Surface,
            center = Offset(point.x, point.y),
            radius = 2f
        )
    }
}

// 绘制折线
fun DrawScope.drawLine(
    dataPoints: List<LineDataPoint>,
    lineColor: Color,
    fillColor: Color
) {
    if (dataPoints.isEmpty()) return
    
    val maxValue = dataPoints.maxOf { it.value }
    val minValue = dataPoints.minOf { it.value }
    val valueRange = maxValue - minValue
    
    // 计算坐标
    val points = dataPoints.mapIndexed { index, point ->
        val x = (index.toFloat() / (dataPoints.size - 1)) * size.width
        val y = size.height - ((point.value - minValue) / valueRange) * size.height
        Offset(x, y)
    }
    
    // 绘制填充区域
    val path = Path().apply {
        moveTo(points.first().x, size.height)
        for (point in points) {
            lineTo(point.x, point.y)
        }
        lineTo(points.last().x, size.height)
        close()
    }
    drawPath(path, fillColor)
    
    // 绘制折线
    drawPath(
        path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (point in points.drop(1)) {
                lineTo(point.x, point.y)
            }
        },
        color = lineColor,
        style = Stroke(width = 2f)
    )
}

// 绘制数据点
fun DrawScope.drawDataPoints(dataPoints: List<LineDataPoint>, color: Color) {
    if (dataPoints.isEmpty()) return
    
    val maxValue = dataPoints.maxOf { it.value }
    val minValue = dataPoints.minOf { it.value }
    val valueRange = maxValue - minValue
    
    for ((index, point) in dataPoints.withIndex()) {
        val x = (index.toFloat() / (dataPoints.size - 1)) * size.width
        val y = size.height - ((point.value - minValue) / valueRange) * size.height
        
        drawCircle(
            color = color,
            center = Offset(x, y),
            radius = 4f
        )
        drawCircle(
            color = AppColors.Surface,
            center = Offset(x, y),
            radius = 2f
        )
    }
}

// 优化的折线图组件
@Composable
fun OptimizedLineChart(
    dataPoints: List<LineDataPoint>,
    lineColor: Color,
    fillColor: Color,
    showPoints: Boolean,
    showGrid: Boolean,
    onClick: (LineDataPoint) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val padding = 32.dp
        val chartHeight = maxHeight - padding * 2
        val chartWidth = maxWidth - padding * 2
        
        // 使用remember缓存处理后的数据
        val processedData = with(LocalDensity.current) {
            remember(dataPoints, chartWidth, chartHeight) {
                val widthPx = chartWidth.toPx()
                val heightPx = chartHeight.toPx()
                processDataForRendering(dataPoints, widthPx, heightPx)
            }
        }
        
        // 使用derivedStateOf减少重组，根据数据量动态采样
        val visibleData = remember(processedData) {
            if (processedData.size > 100) {
                processedData.sampleEveryN(2) // 大数据量时采样显示
            } else {
                processedData
            }
        }
        
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val x = (offset.x - padding.toPx()) / chartWidth.toPx()
                        val index = (x * (dataPoints.size - 1)).toInt().coerceIn(0, dataPoints.size - 1)
                        onClick(dataPoints[index])
                    }
                }
        ) {
            // 绘制网格
            if (showGrid) {
                drawGrid()
            }
            
            // 使用优化的绘制函数
            drawOptimizedLine(
                processedPoints = visibleData,
                lineColor = lineColor,
                fillColor = fillColor
            )
            
            // 绘制数据点
            if (showPoints) {
                drawOptimizedDataPoints(visibleData, lineColor)
            }
        }
        
        // Y轴标签
        YAxisLabels(
            maxValue = dataPoints.maxOf { it.value },
            modifier = Modifier
                .align(Alignment.TopStart)
                .height(chartHeight)
        )
    }
}

// 简易折线图实现 - 保持兼容
@Composable
fun SimpleLineChart(
    dataPoints: List<LineDataPoint>,
    lineColor: Color,
    fillColor: Color,
    showPoints: Boolean,
    showGrid: Boolean,
    onClick: (LineDataPoint) -> Unit
) {
    OptimizedLineChart(
        dataPoints = dataPoints,
        lineColor = lineColor,
        fillColor = fillColor,
        showPoints = showPoints,
        showGrid = showGrid,
        onClick = onClick
    )
}

// 折线图组件 - 保持兼容
@Composable
fun LineChart(
    dataPoints: List<LineDataPoint>,
    lineColor: Color,
    fillColor: Color,
    showPoints: Boolean,
    showGrid: Boolean,
    onClick: (LineDataPoint) -> Unit
) {
    OptimizedLineChart(
        dataPoints = dataPoints,
        lineColor = lineColor,
        fillColor = fillColor,
        showPoints = showPoints,
        showGrid = showGrid,
        onClick = onClick
    )
}

// 健康评分趋势折线图
@Composable
fun TrendLineChart(trendData: com.example.nutrilog.analysis.analysis.TrendAnalysis) {
    // 定义对话框状态
    val showDialog = remember { mutableStateOf(false) }
    val selectedPoint = remember { mutableStateOf<LineDataPoint?>(null) }
    
    ChartContainer(
        modifier = Modifier.fillMaxWidth(),
        title = "健康评分趋势"
    ) {
        LineChart(
            dataPoints = trendData.dailyPoints.map {
                LineDataPoint(
                    label = it.date.substring(5), // 显示月-日
                    value = it.score.toFloat(),
                    extraData = mapOf(
                        "calories" to it.nutrition.calories.toFloat(),
                        "protein" to it.nutrition.protein.toFloat()
                    )
                )
            },
            lineColor = AppColors.Primary,
            fillColor = AppColors.Primary.copy(alpha = 0.1f),
            showPoints = true,
            showGrid = true,
            onClick = { point ->
                // 点击时显示详细信息
                selectedPoint.value = point
                showDialog.value = true
            }
        )
    }
    
    // 数据点详情对话框
    if (showDialog.value && selectedPoint.value != null) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "数据详情") },
            text = {
                Column {
                    Text(text = "日期: ${selectedPoint.value?.label}")
                    Text(text = "健康评分: ${selectedPoint.value?.value}")
                    Text(text = "卡路里: ${selectedPoint.value?.extraData?.get("calories")}")
                    Text(text = "蛋白质: ${selectedPoint.value?.extraData?.get("protein")}")
                }
            },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text(text = "确定")
                }
            }
        )
    }
}

// 显示数据点详情的函数
fun showDataPointDialog(point: LineDataPoint) {
    // 这里可以实现一个对话框来显示详细信息
    // 由于项目中可能没有现成的对话框组件，这里暂时留空
    // 实际使用时可以使用AlertDialog或自定义对话框
    // 注意：这个函数不应该是@Composable的，因为它是从非Composable上下文中调用的
}

// 饮食时间热力图组件
@Composable
fun MealTimeHeatmap(records: List<com.example.nutrilog.shared.MealRecord>) {
    ChartContainer(
        modifier = Modifier.fillMaxWidth(),
        title = "饮食时间分布",
        chartHeight = 350.dp // 增加高度，使热力图布满整个卡片
    ) {
        // 准备数据：24小时 x 7天
        val hourlyData = Array(7) { Array(24) { 0 } }
        
        records.forEach { record ->
            val date = LocalDate.parse(record.date)
            val dayOfWeek = date.dayOfWeek.value - 1 // 0=周一, 6=周日
            val hour = record.time.substring(0, 2).toInt()
            hourlyData[dayOfWeek][hour]++
        }
        
        // 添加水平滚动功能
        HorizontalScrollbarWrapper {
            Heatmap(
                data = hourlyData,
                dayLabels = listOf("一", "二", "三", "四", "五", "六", "日"),
                hourLabels = (0..23).map { "$it" }
            )
        }
    }
}

// 热力图实现
@Composable
fun Heatmap(
    data: Array<Array<Int>>,
    dayLabels: List<String>,
    hourLabels: List<String>
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val cellSize = with(LocalDensity.current) {
            min(maxWidth.toPx() / 8, maxHeight.toPx() / 26)
        }
        val maxValue = data.flatten().maxOrNull() ?: 1
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 绘制标签
            // 绘制星期标签
            for (day in 0 until 7) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        dayLabels[day],
                        0f,
                        (day + 1) * cellSize + 6f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 12f
                            textAlign = android.graphics.Paint.Align.LEFT
                        }
                    )
                }
            }
            
            // 绘制小时标签
            for (hour in 0 until 24) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        hourLabels[hour],
                        (hour + 1) * cellSize,
                        12f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 12f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
            
            // 绘制热力图
            for (day in 0 until 7) {
                for (hour in 0 until 24) {
                    val value = data[day][hour]
                    val intensity = value.toFloat() / maxValue.toFloat()
                    
                    val color = when {
                        intensity > 0.7 -> Color(0xFF4CAF50)
                        intensity > 0.4 -> Color(0xFF8BC34A)
                        intensity > 0.1 -> Color(0xFFCDDC39)
                        else -> Color(0xFFE8F5E8)
                    }
                    
                    drawRect(
                        color = color,
                        topLeft = Offset(
                            (hour + 1) * cellSize,
                            (day + 1) * cellSize
                        ),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}

// 餐次分布环状图组件
@Composable
fun MealDistributionChart(records: List<com.example.nutrilog.shared.MealRecord>) {
    val mealCounts = mapOf(
        "早餐" to records.count { it.mealType.name == "BREAKFAST" },
        "午餐" to records.count { it.mealType.name == "LUNCH" },
        "晚餐" to records.count { it.mealType.name == "DINNER" },
        "零食" to records.count { it.mealType.name == "SNACK" }
    )
    
    ChartContainer(
        title = "餐次分布"
    ) {
        DonutChart(
            slices = mealCounts.map { (mealType, count) ->
                DonutSlice(
                    label = mealType,
                    value = count.toFloat(),
                    color = getMealTypeColor(mealType)
                )
            },
            holeSize = 0.5f,
            showLabels = true
        )
    }
}

// 环状图实现
@Composable
fun DonutChart(
    slices: List<DonutSlice>,
    holeSize: Float,
    showLabels: Boolean
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) * 0.4f
        val holeRadius = radius * holeSize
        
        var startAngle = -90f
        // 过滤掉value为0的切片，避免绘制问题
        val validSlices = slices.filter { it.value > 0 }
        val total = validSlices.sumOf { it.value.toDouble() }.toFloat()
        
        // 如果没有有效切片，显示提示信息
        if (validSlices.isEmpty()) {
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    "暂无数据",
                    center.x,
                    center.y,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
            return@Canvas
        }
        
        validSlices.forEach { slice ->
            val sweepAngle = (slice.value / total) * 360f
            
            // 绘制外环
            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = center - Offset(radius, radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = radius - holeRadius)
            )
            
            // 绘制标签
            if (showLabels) {
                val midAngle = startAngle + sweepAngle / 2
                val labelRadius = radius + 20
                val labelX = center.x + cos(Math.toRadians(midAngle.toDouble())).toFloat() * labelRadius
                val labelY = center.y + sin(Math.toRadians(midAngle.toDouble())).toFloat() * labelRadius
                
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        slice.label,
                        labelX,
                        labelY,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 24f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
            
            startAngle += sweepAngle
        }
    }
}

// 获取餐次类型颜色
fun getMealTypeColor(mealType: String): Color {
    return when (mealType) {
        "早餐" -> Color(0xFF4CAF50)
        "午餐" -> Color(0xFF2196F3)
        "晚餐" -> Color(0xFFFF9800)
        "零食" -> Color(0xFF9C27B0)
        else -> Color.Gray
    }
}

// 计算从中心点到点击位置的角度
fun calculateAngleFromCenter(offset: Offset, center: Offset): Float {
    val dx = offset.x - center.x
    val dy = offset.y - center.y
    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    if (angle < 0) angle += 360f
    return angle
}

// 根据角度查找对应的扇形索引
fun findSliceIndex(angle: Float, slices: List<PieSlice>): Int {
    var currentAngle = -90f // 从12点方向开始
    slices.forEachIndexed { index, slice ->
        val sweepAngle = (slice.value / slices.sumOf { it.value.toDouble() } * 360f).toFloat()
        if (angle >= currentAngle && angle < currentAngle + sweepAngle) {
            return index
        }
        currentAngle += sweepAngle
    }
    return -1
}

// 图片懒加载组件
@Composable
fun LazyImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    placeholder: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray.copy(alpha = 0.1f))
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        }
    }
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    
    LaunchedEffect(url) {
        isLoading = true
        hasError = false
        
        try {
            // 这里使用模拟数据，实际项目中应该使用真实的图片加载逻辑
            // 例如使用Coil、Glide等库，或者使用Android的BitmapFactory
            // 由于是模拟，我们延迟一下模拟网络请求
            delay(500)
            
            // 模拟成功加载
            imageBitmap = null // 实际项目中这里应该是真实的ImageBitmap
        } catch (e: Exception) {
            hasError = true
        } finally {
            isLoading = false
        }
    }
    
    Box(modifier = modifier) {
        when {
            isLoading -> placeholder()
            hasError -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.BrokenImage,
                        contentDescription = "加载失败",
                        tint = Color.Gray
                    )
                }
            }
            imageBitmap != null -> {
                androidx.compose.foundation.Image(
                    bitmap = imageBitmap!!,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
            else -> {
                // 加载完成但没有图片，显示占位符
                placeholder()
            }
        }
    }
}

// 动画饼图组件
@Composable 
fun AnimatedPieChart( 
    slices: List<PieSlice>, 
    selectedSlice: Int? = null, 
    onSliceSelected: (Int) -> Unit 
) { 
    val animationProgressState = remember { mutableStateOf(0f) }
    val animationProgress = animationProgressState.value
    
    LaunchedEffect(slices) { 
        animationProgressState.value = 0f 
        animate( 
            initialValue = 0f, 
            targetValue = 1f, 
            animationSpec = tween( 
                durationMillis = 1000, 
                easing = FastOutSlowInEasing 
            ) 
        ) { value, _ -> 
            animationProgressState.value = value 
        } 
    }
    
    Canvas( 
        modifier = Modifier 
            .fillMaxSize() 
            .pointerInput(Unit) { 
                detectTapGestures { offset -> 
                    // 计算点击的是哪个扇形 
                    val center = Offset(size.width / 2f, size.height / 2f) 
                    val radius = min(size.width, size.height) * 0.4f 
                    val angle = calculateAngleFromCenter(offset, center) 
                    val sliceIndex = findSliceIndex(angle, slices) 
                    if (sliceIndex != -1) { 
                        onSliceSelected(sliceIndex) 
                    } 
                } 
            } 
    ) { 
        drawAnimatedPieChart( 
            slices = slices, 
            progress = animationProgress, 
            selectedSlice = selectedSlice 
        ) 
    } 
}

// 绘制动画饼图
fun DrawScope.drawAnimatedPieChart( 
    slices: List<PieSlice>, 
    progress: Float, 
    selectedSlice: Int? 
) { 
    val center = Offset(size.width / 2, size.height / 2) 
    val radius = min(size.width, size.height) * 0.4f 
    
    var startAngle = -90f 
    val total = slices.sumOf { it.value.toDouble() }.toFloat() 
    
    slices.forEachIndexed { index, slice -> 
        val sweepAngle = (slice.value / total) * 360f * progress 
        
        // 如果是选中的扇形，稍微偏移 
        val offset = if (index == selectedSlice) { 
            val midAngle = startAngle + sweepAngle / 2 
            Offset( 
                cos(Math.toRadians(midAngle.toDouble())).toFloat() * 10f, 
                sin(Math.toRadians(midAngle.toDouble())).toFloat() * 10f 
            ) 
        } else { 
            Offset.Zero 
        } 
        
        drawArc( 
            color = slice.color, 
            startAngle = startAngle, 
            sweepAngle = sweepAngle, 
            useCenter = true, 
            topLeft = center - Offset(radius, radius) + offset, 
            size = Size(radius * 2, radius * 2) 
        ) 
        
        startAngle += sweepAngle 
    } 
}

// 每日类别数据类
data class DailyCategoryData(
    val date: String,
    val categories: Map<String, Float>,
    val total: Float
) {
    fun getCategoryValue(category: String): Float {
        return categories[category] ?: 0f
    }
}

// 食物类别堆叠柱状图组件
@Composable
fun CategoryStackedBarChart(dailyData: List<DailyCategoryData>) {
    ChartContainer(
        title = "食物类别摄入趋势"
    ) {
        StackedBarChart(
            data = dailyData,
            categories = listOf("谷薯", "蔬菜", "水果", "蛋白质", "奶制品", "其他"),
            colors = listOf(
                Color(0xFF8BC34A),
                Color(0xFF4CAF50),
                Color(0xFFFF9800),
                Color(0xFFF44336),
                Color(0xFF2196F3),
                Color(0xFF9E9E9E)
            )
        )
    }
}

// 堆叠柱状图实现
@Composable
fun StackedBarChart(
    data: List<DailyCategoryData>,
    categories: List<String>,
    colors: List<Color>
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val padding = 32.dp
        val barWidth = maxWidth / (data.size * 2)
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (data.isEmpty()) return@Canvas
            
            val actualPadding = padding.toPx()
            val actualBarWidth = barWidth.toPx()
            val actualHeight = size.height - actualPadding * 2
            
            data.forEachIndexed { dayIndex, dayData ->
                val x = actualPadding + actualBarWidth * (dayIndex * 2 + 0.5f)
                var accumulatedHeight = 0f
                
                categories.forEachIndexed { categoryIndex, category ->
                    val value = dayData.getCategoryValue(category)
                    val barHeight = if (dayData.total > 0) {
                        (value / dayData.total) * actualHeight
                    } else {
                        0f
                    }
                    
                    drawRect(
                        color = colors[categoryIndex],
                        topLeft = Offset(
                            x,
                            size.height - actualPadding - accumulatedHeight - barHeight
                        ),
                        size = Size(actualBarWidth, barHeight)
                    )
                    
                    accumulatedHeight += barHeight
                }
            }
        }
    }
}

