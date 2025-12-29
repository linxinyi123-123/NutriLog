package com.example.nutrilog.features.recommendation.algorithm

import com.example.nutrilog.features.recommendation.model.Recommendation
import kotlin.random.Random

/**
 * 基础推荐器，包含公共方法和工具函数
 */
abstract class BaseRecommender {

    /**
     * 生成唯一的推荐ID
     */
    protected fun generateRecommendationId(): Long {
        return System.currentTimeMillis() + Random.nextLong(1000)
    }

    /**
     * 计算置信度（基于严重程度）
     */
    protected fun calculateConfidence(severity: com.example.nutrilog.features.recommendation.interfaces.Severity): Float {
        return when (severity) {
            com.example.nutrilog.features.recommendation.interfaces.Severity.SEVERE -> 0.9f
            com.example.nutrilog.features.recommendation.interfaces.Severity.MODERATE -> 0.7f
            com.example.nutrilog.features.recommendation.interfaces.Severity.MILD -> 0.4f
        }
    }

    /**
     * 计算优先级（基于严重程度）
     */
    protected fun calculatePriority(severity: com.example.nutrilog.features.recommendation.interfaces.Severity): com.example.nutrilog.features.recommendation.model.Priority {
        return when (severity) {
            com.example.nutrilog.features.recommendation.interfaces.Severity.SEVERE -> com.example.nutrilog.features.recommendation.model.Priority.HIGH
            com.example.nutrilog.features.recommendation.interfaces.Severity.MODERATE -> com.example.nutrilog.features.recommendation.model.Priority.MEDIUM
            com.example.nutrilog.features.recommendation.interfaces.Severity.MILD -> com.example.nutrilog.features.recommendation.model.Priority.LOW
        }
    }

    /**
     * 推荐去重（基于标题和类型）
     */
    protected fun deduplicateRecommendations(recommendations: List<Recommendation>): List<Recommendation> {
        val seen = mutableSetOf<String>()
        return recommendations.filter { recommendation ->
            val key = "${recommendation.type}:${recommendation.title}:${recommendation.reason}"
            if (seen.contains(key)) {
                false
            } else {
                seen.add(key)
                true
            }
        }
    }

    /**
     * 排序推荐（优先级高 -> 低，置信度高 -> 低）
     */
    protected fun sortRecommendations(recommendations: List<Recommendation>): List<Recommendation> {
        return recommendations.sortedWith(
            compareByDescending<Recommendation> { it.priority }
                .thenByDescending { it.confidence }
        )
    }

    /**
     * 获取推荐理由模板
     */
    protected fun getReasonTemplate(days: Int): String {
        return "基于最近${days}天的营养分析"
    }
}