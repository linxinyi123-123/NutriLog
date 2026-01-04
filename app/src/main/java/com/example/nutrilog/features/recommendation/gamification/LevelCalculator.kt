// app/src/main/java/com/nutrilog/features/recommendation/gamification/LevelCalculator.kt
package com.example.nutrilog.features.recommendation.gamification

/**
 * 等级计算器
 */
object LevelCalculator {

    /**
     * 根据总积分计算等级
     * 等级公式：level = floor(sqrt(points / 100))
     */
    fun calculateLevel(totalPoints: Int): Int {
        return when {
            totalPoints >= 10000 -> 10
            totalPoints >= 5000 -> 9
            totalPoints >= 2500 -> 8
            totalPoints >= 1200 -> 7
            totalPoints >= 600 -> 6
            totalPoints >= 300 -> 5
            totalPoints >= 150 -> 4
            totalPoints >= 75 -> 3
            totalPoints >= 30 -> 2
            totalPoints >= 10 -> 1
            else -> 0
        }
    }

    /**
     * 计算当前等级进度（0-1）
     */
    fun calculateLevelProgress(totalPoints: Int): Float {
        val currentLevel = calculateLevel(totalPoints)
        val nextLevelPoints = getPointsForLevel(currentLevel + 1)
        val currentLevelPoints = getPointsForLevel(currentLevel)

        if (nextLevelPoints == currentLevelPoints) return 1f

        val pointsInCurrentLevel = totalPoints - currentLevelPoints
        val pointsNeededForNextLevel = nextLevelPoints - currentLevelPoints

        return (pointsInCurrentLevel.toFloat() / pointsNeededForNextLevel).coerceIn(0f, 1f)
    }

    /**
     * 获取指定等级所需的最低积分
     */
    private fun getPointsForLevel(level: Int): Int {
        return when (level) {
            0 -> 0
            1 -> 10
            2 -> 30
            3 -> 75
            4 -> 150
            5 -> 300
            6 -> 600
            7 -> 1200
            8 -> 2500
            9 -> 5000
            10 -> 10000
            else -> 10000 + (level - 10) * 5000
        }
    }
}