// features/recommendation/viewmodel/RecommendationViewModel.kt
package com.example.nutrilog.features.recommendation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrilog.features.recommendation.factory.ServiceFactory
import com.example.nutrilog.features.recommendation.service.RecommendationService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecommendationViewModel : ViewModel() {

    private val recommendationService: RecommendationService =
        ServiceFactory.createRecommendationService()

    private val _recommendations = MutableStateFlow<List<com.example.nutrilog.features.recommendation.model.Recommendation>>(emptyList())
    val recommendations: StateFlow<List<com.example.nutrilog.features.recommendation.model.Recommendation>> = _recommendations

    private val _challenges = MutableStateFlow<List<com.example.nutrilog.features.recommendation.challenge.DailyChallenge>>(emptyList())
    val challenges: StateFlow<List<com.example.nutrilog.features.recommendation.challenge.DailyChallenge>> = _challenges

    private val _achievements = MutableStateFlow<List<com.example.nutrilog.features.recommendation.model.gamification.Achievement>>(emptyList())
    val achievements: StateFlow<List<com.example.nutrilog.features.recommendation.model.gamification.Achievement>> = _achievements

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        // 初始加载数据
        loadData(1L) // 假设用户ID为1
    }

    fun loadData(userId: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                // 并行加载所有数据
                val recommendationsDeferred = launch { loadRecommendations(userId) }
                val challengesDeferred = launch { loadChallenges(userId) }
                val achievementsDeferred = launch { loadAchievements(userId) }

                // 等待所有加载完成
                recommendationsDeferred.join()
                challengesDeferred.join()
                achievementsDeferred.join()

                _error.value = null
            } catch (e: Exception) {
                _error.value = "加载失败: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
    // 在 RecommendationViewModel 类中添加这个方法
    fun dismissRecommendation(recommendationId: Long) {
        viewModelScope.launch {
            try {
                // 如果有服务端接口，调用它
                // recommendationService.dismissRecommendation(recommendationId)

                // 本地移除：直接从当前列表中过滤掉
                _recommendations.update { currentList ->
                    currentList.filter { it.id != recommendationId }
                }

                println("已移除推荐: $recommendationId")
            } catch (e: Exception) {
                _error.value = "移除推荐失败: ${e.message}"
            }
        }
    }
    fun loadRecommendations(userId: Long) {
        viewModelScope.launch {
            try {
                val recommendations = recommendationService.getDailyRecommendations(userId)
                _recommendations.value = recommendations
            } catch (e: Exception) {
                _error.value = "加载推荐失败: ${e.message}"
            }
        }
    }

    fun loadChallenges(userId: Long) {
        viewModelScope.launch {
            try {
                val challenges = recommendationService.getDailyChallenges(userId)
                _challenges.value = challenges
            } catch (e: Exception) {
                _error.value = "加载挑战失败: ${e.message}"
            }
        }
    }

    fun loadAchievements(userId: Long) {
        viewModelScope.launch {
            try {
                val achievements = recommendationService.getUserAchievements(userId)
                _achievements.value = achievements
            } catch (e: Exception) {
                _error.value = "加载成就失败: ${e.message}"
            }
        }
    }

    fun markRecommendationApplied(recommendationId: Long) {
        viewModelScope.launch {
            try {
                recommendationService.markRecommendationApplied(recommendationId)
                // 重新加载推荐以更新列表
                loadRecommendations(1L)
            } catch (e: Exception) {
                _error.value = "应用推荐失败: ${e.message}"
            }
        }
    }

    fun updateChallengeProgress(challengeId: Long, progress: Float) {
        viewModelScope.launch {
            try {
                recommendationService.updateChallengeProgress(challengeId, progress)
                // 重新加载挑战
                loadChallenges(1L)
            } catch (e: Exception) {
                _error.value = "更新挑战进度失败: ${e.message}"
            }
        }
    }

}