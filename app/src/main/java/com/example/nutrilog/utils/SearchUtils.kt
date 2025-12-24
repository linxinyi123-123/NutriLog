package com.example.nutrilog.utils

import com.example.nutrilog.data.entities.FoodItem

object SearchUtils {
    
    /**
     * 高级搜索功能，支持多种搜索方式
     */
    fun advancedSearch(foods: List<FoodItem>, query: String): List<FoodItem> {
        if (query.isEmpty()) return foods
        
        val lowerQuery = query.lowercase()
        
        return foods.filter { food ->
            // 1. 精确匹配（中文名称、英文名称）
            food.name.contains(query, ignoreCase = true) ||
            food.englishName?.contains(query, ignoreCase = true) == true ||
            food.pinyin?.contains(lowerQuery, ignoreCase = true) == true
        }.sortedBy { food ->
            // 优先级排序
            when {
                // 最高优先级：名称完全匹配或开头匹配
                food.name.equals(query, ignoreCase = true) -> 1
                food.name.startsWith(query, ignoreCase = true) -> 2
                food.englishName?.equals(query, ignoreCase = true) == true -> 3
                food.englishName?.startsWith(query, ignoreCase = true) == true -> 4
                food.pinyin?.startsWith(lowerQuery, ignoreCase = true) == true -> 5
                
                // 中等优先级：包含匹配
                food.name.contains(query, ignoreCase = true) -> 6
                food.englishName?.contains(query, ignoreCase = true) == true -> 7
                food.pinyin?.contains(lowerQuery, ignoreCase = true) == true -> 8
                
                // 最低优先级：拼音首字母匹配
                food.pinyin?.let { pinyin ->
                    pinyin.split(" ").any { word ->
                        word.startsWith(lowerQuery, ignoreCase = true)
                    }
                } == true -> 9
                
                else -> 10
            }
        }
    }
    
    /**
     * 生成搜索建议
     */
    fun generateSearchSuggestions(foods: List<FoodItem>, query: String): List<String> {
        if (query.isEmpty()) return emptyList()
        
        val suggestions = mutableSetOf<String>()
        
        foods.forEach { food ->
            // 基于名称的建议
            if (food.name.contains(query, ignoreCase = true)) {
                suggestions.add(food.name)
            }
            
            // 基于拼音的建议
            food.pinyin?.let { pinyin ->
                if (pinyin.contains(query.lowercase())) {
                    suggestions.add(food.name)
                }
            }
            
            // 基于英文名称的建议
            food.englishName?.let { englishName ->
                if (englishName.contains(query, ignoreCase = true)) {
                    suggestions.add(englishName)
                }
            }
        }
        
        return suggestions.toList().take(5)
    }
    
    /**
     * 检查搜索查询是否可能是拼音
     */
    fun isLikelyPinyin(query: String): Boolean {
        // 如果查询只包含字母和数字，且没有中文字符，可能是拼音
        return query.all { it.isLetterOrDigit() } && 
               !PinyinUtils.containsChinese(query)
    }
    
    /**
     * 获取搜索关键词的多种形式（用于更灵活的搜索）
     */
    fun getSearchVariants(query: String): List<String> {
        val variants = mutableListOf<String>()
        
        // 原始查询
        variants.add(query)
        
        // 转换为小写
        variants.add(query.lowercase())
        
        // 转换为拼音（如果包含中文字符）
        if (PinyinUtils.containsChinese(query)) {
            variants.add(PinyinUtils.toPinyin(query))
            variants.add(PinyinUtils.toPinyinAbbr(query))
        }
        
        return variants.distinct()
    }
    
    /**
     * 智能搜索评分系统
     */
    fun calculateSearchScore(food: FoodItem, query: String): Int {
        var score = 0
        val lowerQuery = query.lowercase()
        
        // 名称匹配评分
        when {
            food.name.equals(query, ignoreCase = true) -> score += 100
            food.name.startsWith(query, ignoreCase = true) -> score += 80
            food.name.contains(query, ignoreCase = true) -> score += 60
        }
        
        // 英文名称匹配评分
        food.englishName?.let { englishName ->
            when {
                englishName.equals(query, ignoreCase = true) -> score += 90
                englishName.startsWith(query, ignoreCase = true) -> score += 70
                englishName.contains(query, ignoreCase = true) -> score += 50
            }
        }
        
        // 拼音匹配评分
        food.pinyin?.let { pinyin ->
            when {
                pinyin.equals(lowerQuery, ignoreCase = true) -> score += 85
                pinyin.startsWith(lowerQuery, ignoreCase = true) -> score += 65
                pinyin.contains(lowerQuery, ignoreCase = true) -> score += 45
                pinyin.split(" ").any { it.startsWith(lowerQuery) } -> score += 30
            }
        }
        
        // 常用食物加分
        if (food.isCommon) score += 10
        
        return score
    }
}