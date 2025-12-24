package com.example.nutrilog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrilog.data.entities.FoodCategory
import com.example.nutrilog.data.entities.FoodItem
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.repository.FoodRepository
import com.example.nutrilog.data.repository.MealRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(
    private val mealRecordRepository: MealRecordRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {
    
    // 当前选中的日期
    private val _selectedDate = MutableStateFlow(LocalDate.now().toString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()
    
    // 当天的饮食记录
    private val _todayMealRecords = MutableStateFlow<List<MealRecord>>(emptyList())
    val todayMealRecords: StateFlow<List<MealRecord>> = _todayMealRecords.asStateFlow()
    
    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // 错误消息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadTodayMealRecords()
    }
    
    fun loadTodayMealRecords() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val records = mealRecordRepository.getMealRecordsByDate(_selectedDate.value)
                _todayMealRecords.value = records
            } catch (e: Exception) {
                _errorMessage.value = "加载记录失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun selectDate(date: String) {
        _selectedDate.value = date
        loadTodayMealRecords()
    }
    
    fun deleteMealRecord(recordId: Long) {
        viewModelScope.launch {
            try {
                mealRecordRepository.deleteMealRecordById(recordId)
                loadTodayMealRecords() // 重新加载数据
            } catch (e: Exception) {
                _errorMessage.value = "删除记录失败: ${e.message}"
            }
        }
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    fun refreshData() {
        loadTodayMealRecords()
    }
    
    // 添加饮食记录
    fun addMealRecord(record: MealRecord) {
        viewModelScope.launch {
            try {
                mealRecordRepository.insertMealRecord(record)
                loadTodayMealRecords() // 重新加载数据
            } catch (e: Exception) {
                _errorMessage.value = "添加记录失败: ${e.message}"
            }
        }
    }
    
    // 食物相关功能
    suspend fun searchFoods(query: String): List<FoodItem> {
        return foodRepository.searchFoods(query)
    }
    
    suspend fun getFoodsByCategory(category: FoodCategory): List<FoodItem> {
        return foodRepository.getFoodsByCategory(category)
    }
    
    suspend fun getRecentlyUsedFoods(): List<FoodItem> {
        return foodRepository.getRecentlyUsedFoods()
    }
    
    suspend fun getAllCategories(): List<FoodCategory> {
        return foodRepository.getAllCategories()
    }
}