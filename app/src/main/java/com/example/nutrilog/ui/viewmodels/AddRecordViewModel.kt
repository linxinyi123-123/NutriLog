package com.example.nutrilog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrilog.data.entities.ComboFood
import com.example.nutrilog.data.entities.FoodCombo
import com.example.nutrilog.data.entities.FoodItem
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.repository.FoodRepository
import com.example.nutrilog.data.repository.MealRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddRecordViewModel(
    private val mealRecordRepository: MealRecordRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {
    
    // 当前选中的食物列表
    private val _selectedFoods = MutableStateFlow<List<Pair<FoodItem, Double>>>(emptyList())
    val selectedFoods: StateFlow<List<Pair<FoodItem, Double>>> = _selectedFoods.asStateFlow()
    
    // 所有食物组合
    private val _foodCombos = MutableStateFlow<List<FoodCombo>>(emptyList())
    val foodCombos: StateFlow<List<FoodCombo>> = _foodCombos.asStateFlow()
    
    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // 错误消息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    // 保存组合对话框状态
    private val _showSaveComboDialog = MutableStateFlow(false)
    val showSaveComboDialog: StateFlow<Boolean> = _showSaveComboDialog.asStateFlow()
    
    // 组合名称输入
    private val _comboName = MutableStateFlow("")
    val comboName: StateFlow<String> = _comboName.asStateFlow()
    
    // 组合描述输入
    private val _comboDescription = MutableStateFlow("")
    val comboDescription: StateFlow<String> = _comboDescription.asStateFlow()
    
    // 当前选择的标签
    private val _selectedTag = MutableStateFlow("未定义")
    val selectedTag: StateFlow<String> = _selectedTag.asStateFlow()
    
    init {
        loadFoodCombos()
    }
    
    // 加载所有食物组合
    private fun loadFoodCombos() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                foodRepository.getAllFoodCombos().collect { combos ->
                    _foodCombos.value = combos
                }
            } catch (e: Exception) {
                _errorMessage.value = "加载组合失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 添加食物到当前选择
    fun addFood(food: FoodItem, portion: Double = 100.0) {
        val currentFoods = _selectedFoods.value.toMutableList()
        currentFoods.add(food to portion)
        _selectedFoods.value = currentFoods
    }
    
    // 更新食物份量
    fun updateFoodPortion(food: FoodItem, portion: Double) {
        val currentFoods = _selectedFoods.value.toMutableList()
        val index = currentFoods.indexOfFirst { it.first.id == food.id }
        if (index != -1) {
            currentFoods[index] = food to portion
            _selectedFoods.value = currentFoods
        }
    }
    
    // 移除食物
    fun removeFood(food: FoodItem) {
        val currentFoods = _selectedFoods.value.toMutableList()
        currentFoods.removeAll { it.first.id == food.id }
        _selectedFoods.value = currentFoods
    }
    
    // 清空所有选择
    fun clearSelectedFoods() {
        _selectedFoods.value = emptyList()
    }
    
    // 保存饮食记录
    fun saveMealRecord(record: MealRecord, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // 创建包含标签的记录（热量已经在UI层计算并传入）
                val recordWithTag = record.copy(
                    tag = _selectedTag.value
                )
                
                val recordId = mealRecordRepository.addMealRecordWithFoods(recordWithTag, _selectedFoods.value)
                clearSelectedFoods()
                onSuccess() // 保存成功后回调
            } catch (e: Exception) {
                _errorMessage.value = "保存记录失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 更新饮食记录
    fun updateMealRecord(record: MealRecord, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // 创建包含标签的记录（热量已经在UI层计算并传入）
                val recordWithTag = record.copy(
                    tag = _selectedTag.value
                )
                
                mealRecordRepository.updateMealRecordWithFoods(recordWithTag, _selectedFoods.value)
                onSuccess() // 更新成功后回调
            } catch (e: Exception) {
                _errorMessage.value = "更新记录失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 组合相关功能
    
    // 显示保存组合对话框
    fun showSaveComboDialog() {
        _showSaveComboDialog.value = true
    }
    
    // 隐藏保存组合对话框
    fun hideSaveComboDialog() {
        _showSaveComboDialog.value = false
        _comboName.value = ""
        _comboDescription.value = ""
    }
    
    // 更新组合名称
    fun updateComboName(name: String) {
        _comboName.value = name
    }
    
    // 更新组合描述
    fun updateComboDescription(description: String) {
        _comboDescription.value = description
    }
    
    // 更新选择的标签
    fun updateSelectedTag(tag: String) {
        _selectedTag.value = tag
    }
    
    // 保存为组合
    fun saveAsCombo() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // 生成组合名称：如果用户输入了名称，使用输入的名称；否则使用前5个食物名称
                val comboName = if (_comboName.value.isNotBlank()) {
                    _comboName.value
                } else {
                    val foodNames = _selectedFoods.value.take(5).map { it.first.name }
                    if (foodNames.isEmpty()) {
                        "未命名组合"
                    } else {
                        foodNames.joinToString("、")
                    }
                }
                
                val combo = FoodCombo(
                    name = comboName,
                    description = _comboDescription.value
                )
                
                val comboFoods = _selectedFoods.value.map { (food, portion) ->
                    ComboFood(
                        comboId = 0, // 会在插入时自动生成
                        foodId = food.id,
                        foodName = food.name,
                        portion = portion,
                        unit = "克"
                    )
                }
                
                foodRepository.createFoodCombo(combo, comboFoods)
                hideSaveComboDialog()
                loadFoodCombos() // 重新加载组合列表
            } catch (e: Exception) {
                _errorMessage.value = "保存组合失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 应用组合到当前选择
    fun applyCombo(comboId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val comboWithFoods = foodRepository.getFoodComboWithFoods(comboId)
                if (comboWithFoods != null) {
                    // 清空当前选择
                    clearSelectedFoods()
                    
                    // 添加组合中的食物
                    comboWithFoods.foods.forEach { comboFood ->
                        val food = foodRepository.getFoodById(comboFood.foodId)
                        if (food != null) {
                            addFood(food, comboFood.portion)
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "应用组合失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 删除组合
    fun deleteCombo(combo: FoodCombo) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                foodRepository.deleteFoodCombo(combo)
                loadFoodCombos() // 重新加载组合列表
            } catch (e: Exception) {
                _errorMessage.value = "删除组合失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // 清除错误消息
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    // 搜索食物
    suspend fun searchFoods(query: String): List<FoodItem> {
        return foodRepository.searchFoods(query)
    }
    
    // 获取最近使用的食物
    suspend fun getRecentlyUsedFoods(): List<FoodItem> {
        return foodRepository.getRecentlyUsedFoods()
    }
    
    // 加载编辑记录的数据
    fun loadRecordForEditing(recordId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                // 获取记录信息
                val record = mealRecordRepository.getMealRecordById(recordId)
                if (record != null) {
                    // 获取记录关联的食物
                    val foods = mealRecordRepository.getFoodsForRecord(recordId)
                    _selectedFoods.value = foods
                    // 设置记录的标签
                    _selectedTag.value = record.tag
                }
            } catch (e: Exception) {
                _errorMessage.value = "加载记录失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}