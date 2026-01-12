package com.example.nutrilog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrilog.ui.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class ReportViewModel : ViewModel() {
    // 报告类型选择
    private val _selectedReportType = MutableStateFlow(ReportType.DAILY)
    val selectedReportType: StateFlow<ReportType> = _selectedReportType.asStateFlow()
    
    // 开始日期
    private val _startDate = MutableStateFlow(LocalDate.now())
    val startDate: StateFlow<LocalDate> = _startDate.asStateFlow()
    
    // 结束日期
    private val _endDate = MutableStateFlow(LocalDate.now())
    val endDate: StateFlow<LocalDate> = _endDate.asStateFlow()
    
    // 内容选项
    private val _contentOptions = MutableStateFlow(ContentOptions())
    val contentOptions: StateFlow<ContentOptions> = _contentOptions.asStateFlow()
    
    // 样式选项
    private val _styleOptions = MutableStateFlow(StyleOptions())
    val styleOptions: StateFlow<StyleOptions> = _styleOptions.asStateFlow()
    
    // 生成状态
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()
    
    // 是否可以生成报告
    private val _canGenerate = MutableStateFlow(true)
    val canGenerate: StateFlow<Boolean> = _canGenerate.asStateFlow()
    
    // 选择报告类型
    fun selectReportType(type: ReportType) {
        _selectedReportType.value = type
        // 根据报告类型自动调整日期范围
        adjustDateRangeByType(type)
    }
    
    // 设置开始日期
    fun setStartDate(date: LocalDate) {
        _startDate.value = date
        validateDateRange()
    }
    
    // 设置结束日期
    fun setEndDate(date: LocalDate) {
        _endDate.value = date
        validateDateRange()
    }
    
    // 更新内容选项
    fun updateContentOptions(options: ContentOptions) {
        _contentOptions.value = options
    }
    
    // 更新样式选项
    fun updateStyleOptions(options: StyleOptions) {
        _styleOptions.value = options
    }
    
    // 根据报告类型调整日期范围
    private fun adjustDateRangeByType(type: ReportType) {
        val today = LocalDate.now()
        when (type) {
            ReportType.DAILY -> {
                _startDate.value = today
                _endDate.value = today
            }
            ReportType.WEEKLY -> {
                val weekStart = today.minusDays(today.dayOfWeek.value.toLong())
                _startDate.value = weekStart
                _endDate.value = weekStart.plusDays(6)
            }
            ReportType.MONTHLY -> {
                val monthStart = today.withDayOfMonth(1)
                _startDate.value = monthStart
                _endDate.value = monthStart.plusMonths(1).minusDays(1)
            }
            ReportType.CUSTOM -> {
                // 保持当前选择的日期
            }
        }
        validateDateRange()
    }
    
    // 验证日期范围
    private fun validateDateRange() {
        _canGenerate.value = _startDate.value <= _endDate.value
    }
    
    // 生成报告
    fun generateReport(): GeneratedReport {
        // 模拟报告生成过程
        _isGenerating.value = true
        
        // 这里可以添加实际的报告生成逻辑，例如从数据库获取数据、分析数据等
        
        val report = GeneratedReport(
            id = "report_${System.currentTimeMillis()}",
            type = _selectedReportType.value,
            startDate = _startDate.value,
            endDate = _endDate.value,
            contentOptions = _contentOptions.value,
            styleOptions = _styleOptions.value
        )
        
        _isGenerating.value = false
        return report
    }
}
