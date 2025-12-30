package com.example.nutrilog.analysis.analyzer

import com.example.nutrilog.analysis.analysis.VarietyAnalysis
import com.example.nutrilog.shared.FoodCategory
import com.example.nutrilog.shared.MealRecord

class FoodVarietyAnalyzer {
    fun analyzeVariety(records: List<MealRecord>): VarietyAnalysis {
        // 统计各类别食物出现的天数
        val categoryDays = mutableMapOf<FoodCategory, Int>()
        val days = records.map { it.date }.distinct()

        days.forEach { date ->
            val dayRecords = records.filter { it.date == date }
            val dayCategories = mutableSetOf<FoodCategory>()

            dayRecords.forEach { record ->
                record.foods.forEach { (food, _) ->
                    val category = FoodCategory.fromFoodName(food.name)
                    dayCategories.add(category)
                }
            }

            dayCategories.forEach { category ->
                categoryDays[category] = categoryDays.getOrDefault(category, 0) + 1
            }
        }

        // 计算多样性得分
        val totalDays = days.size
        val coverageScores = categoryDays.mapValues { (_, count) ->
            (count.toDouble() / totalDays) * 100
        }

        val totalScore = calculateTotalScore(coverageScores)
        val suggestions = generateVarietySuggestions(coverageScores, totalDays)

        return VarietyAnalysis(
            totalScore = totalScore,
            coverage = coverageScores,
            suggestions = suggestions
        )
    }

    private fun calculateTotalScore(coverage: Map<FoodCategory, Double>): Double {
        // 核心类别（谷薯、蔬菜、蛋白质）权重更高
        val weights = mapOf(
            FoodCategory.GRAINS to 0.25,
            FoodCategory.VEGETABLES to 0.25,
            FoodCategory.FRUITS to 0.15,
            FoodCategory.PROTEIN to 0.25,
            FoodCategory.DAIRY to 0.10
        )

        return weights.entries.sumOf { (category, weight) ->
            (coverage[category] ?: 0.0) * weight
        }
    }

    private fun generateVarietySuggestions(
        coverage: Map<FoodCategory, Double>,
        totalDays: Int
    ): List<String> {
        val suggestions = mutableListOf<String>()

        // 核心类别定义和中文名称映射
        val categoryNames = mapOf(
            FoodCategory.GRAINS to "谷薯类",
            FoodCategory.VEGETABLES to "蔬菜类",
            FoodCategory.FRUITS to "水果类",
            FoodCategory.PROTEIN to "蛋白质类",
            FoodCategory.DAIRY to "奶制品类"
        )

        // 分析核心类别的覆盖情况
        val coreCategories = listOf(
            FoodCategory.GRAINS,
            FoodCategory.VEGETABLES,
            FoodCategory.PROTEIN
        )

        val coreCoverage = coreCategories.map { category ->
            category to (coverage[category] ?: 0.0)
        }

        // 核心类别建议
        coreCoverage.forEach { (category, score) ->
            val categoryName = categoryNames[category] ?: category.name

            when {
                score < 30 -> {
                    suggestions.add("您的${categoryName}摄入严重不足，仅${score.toInt()}%的天数有摄入")
                    suggestions.add(getSpecificAdviceForCategory(category, "严重不足"))
                }
                score < 60 -> {
                    suggestions.add("您的${categoryName}摄入不够规律，${score.toInt()}%的天数有摄入")
                    suggestions.add(getSpecificAdviceForCategory(category, "需要改善"))
                }
                score < 80 -> {
                    suggestions.add("您的${categoryName}摄入基本良好，${score.toInt()}%的天数有摄入，可以继续提升")
                }
                else -> {
                    suggestions.add("太棒了！您的${categoryName}摄入非常规律，${score.toInt()}%的天数都有摄入")
                }
            }
        }

        // 辅助类别建议
        val auxiliaryCategories = listOf(
            FoodCategory.FRUITS,
            FoodCategory.DAIRY
        )

        auxiliaryCategories.forEach { category ->
            val score = coverage[category] ?: 0.0
            val categoryName = categoryNames[category] ?: category.name

            when {
                score < 40 -> {
                    suggestions.add("建议增加${categoryName}的摄入，目前仅${score.toInt()}%的天数有摄入")
                }
                score < 70 -> {
                    suggestions.add("您的${categoryName}摄入还有提升空间，${score.toInt()}%的天数有摄入")
                }
                else -> {
                    suggestions.add("您的${categoryName}摄入习惯很好，${score.toInt()}%的天数都有摄入")
                }
            }
        }

        // 总体多样性分析
        val overallScore = coverage.values.average()
        val categoriesWithGoodCoverage = coverage.count { it.value >= 70 }
        val categoriesWithPoorCoverage = coverage.count { it.value < 40 }

        when {
            overallScore < 40 -> {
                suggestions.add("您的整体饮食多样性较差，建议制定每日食物多样化计划")
                suggestions.add("尝试每天至少包含3-4个不同类别的食物")
            }
            overallScore < 65 -> {
                suggestions.add("您的饮食多样性中等，建议重点关注摄入不足的类别")
                suggestions.add("可以参考膳食指南，确保每餐都包含不同颜色的食物")
            }
            else -> {
                suggestions.add("恭喜！您的饮食多样性很好，继续保持这种均衡的饮食习惯")
            }
        }

        // 基于天数的个性化建议
        when {
            totalDays < 7 -> {
                suggestions.add("记录天数较少(${totalDays}天)，建议持续记录更多天数以获得更准确的分析")
            }
            totalDays < 14 -> {
                suggestions.add("已记录${totalDays}天，数据逐渐具有参考价值，建议继续坚持记录")
            }
            else -> {
                suggestions.add("基于${totalDays}天的详细记录，分析结果具有较高的参考价值")
            }
        }

        // 添加具体的改进行动计划
        if (coreCoverage.any { it.second < 60 }) {
            suggestions.add("行动建议：制定一周食物轮换计划，确保每类核心食物都能规律摄入")
            suggestions.add("可以准备食材清单，避免总是选择相同类型的食物")
        }

        return suggestions.distinct() // 去重，避免重复建议
    }

    // 辅助函数：为特定类别提供具体建议
    private fun getSpecificAdviceForCategory(category: FoodCategory, level: String): String {
        return when (category) {
            FoodCategory.GRAINS -> when (level) {
                "严重不足" -> "建议每天摄入全谷物、杂粮等，如燕麦、糙米、全麦面包，替代精制米面"
                "需要改善" -> "可以增加粗粮比例，尝试红薯、玉米、藜麦等多样化主食"
                else -> ""
            }
            FoodCategory.VEGETABLES -> when (level) {
                "严重不足" -> "每天至少摄入300-500g蔬菜，深色蔬菜应占一半以上，如菠菜、西兰花、胡萝卜"
                "需要改善" -> "建议每餐都要有蔬菜，尝试不同颜色和种类的蔬菜搭配"
                else -> ""
            }
            FoodCategory.PROTEIN -> when (level) {
                "严重不足" -> "保证优质蛋白质摄入，如瘦肉、鱼类、蛋类、豆制品，每天一个鸡蛋"
                "需要改善" -> "可以增加白肉（鸡鸭鱼）比例，适量摄入坚果和豆制品"
                else -> ""
            }
            FoodCategory.FRUITS -> "建议每天吃200-350g新鲜水果，种类要多样化，避免只吃1-2种"
            FoodCategory.DAIRY -> "每天摄入300ml牛奶或相当量的奶制品，乳糖不耐受可选择酸奶或奶酪"
            else -> "注意该类别食物的摄入均衡性"
        }
    }
}