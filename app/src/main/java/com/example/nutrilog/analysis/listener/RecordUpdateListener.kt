package com.example.nutrilog.analysis.listener

import com.example.nutrilog.analysis.analysis.AnalysisCache
import com.example.nutrilog.analysis.service.CompleteAnalysisService
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.repository.MealRecordRepository
import kotlinx.coroutines.flow.Flow

sealed class RecordUpdate {
    data class RecordAdded(val record: MealRecord) : RecordUpdate()
    data class RecordUpdated(val record: MealRecord) : RecordUpdate()
    data class RecordDeleted(val date: String) : RecordUpdate()
}

class RecordUpdateListener(
    private val analysisService: CompleteAnalysisService,
    private val cache: AnalysisCache
) {
    suspend fun setup(recordRepository: MealRecordRepository) {
        recordRepository.recordUpdates().collect { update ->
            when (update) {
                is RecordUpdate.RecordAdded -> {
                    cache.clearDailyCache(update.record.date)
                    analysisService.notifyAnalysisUpdated(update.record.date)
                }
                is RecordUpdate.RecordUpdated -> {
                    cache.clearDailyCache(update.record.date)
                    analysisService.notifyAnalysisUpdated(update.record.date)
                }
                is RecordUpdate.RecordDeleted -> {
                    cache.clearDailyCache(update.date)
                    analysisService.notifyAnalysisUpdated(update.date)
                }
            }
        }
    }
}