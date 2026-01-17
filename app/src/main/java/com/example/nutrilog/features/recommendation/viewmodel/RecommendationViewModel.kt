package com.example.nutrilog.features.recommendation.viewmodel

// features/recommendation/viewmodel/RecommendationViewModel.kt
class RecommendationViewModel : ViewModel() {
    private val recommendationService = ServiceFactory.createRecommendationService()

    private val _recommendations = MutableStateFlow<List<Recommendation>>(emptyList())
    val recommendations: StateFlow<List<Recommendation>> = _recommendations

    private val _challenges = MutableStateFlow<List<DailyChallenge>>(emptyList())
    val challenges: StateFlow<List<DailyChallenge>> = _challenges

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements

    fun loadRecommendations(userId: Long) {
        viewModelScope.launch {
            val recs = recommendationService.getDailyRecommendations(userId)
            _recommendations.value = recs
        }
    }

    fun loadChallenges(userId: Long) {
        viewModelScope.launch {
            val chals = recommendationService.getDailyChallenges(userId)
            _challenges.value = chals
        }
    }

    fun loadAchievements(userId: Long) {
        viewModelScope.launch {
            val achieves = recommendationService.getUserAchievements(userId)
            _achievements.value = achieves
        }
    }

    fun markRecommendationApplied(recommendationId: Long) {
        viewModelScope.launch {
            recommendationService.markRecommendationApplied(recommendationId)
            // 刷新推荐列表
            loadRecommendations(1L) // 假设userId=1
        }
    }
}