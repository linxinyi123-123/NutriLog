package com.example.nutrilog.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.nutrilog.data.entities.FoodCombo
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodComboDao {
    // 插入组合，返回生成的ID
    @Insert
    suspend fun insert(combo: FoodCombo): Long

    // 更新组合
    @Update
    suspend fun update(combo: FoodCombo)

    // 删除组合
    @Delete
    suspend fun delete(combo: FoodCombo)

    // 查询所有组合（实时更新）
    @Query("SELECT * FROM FoodCombo ORDER BY name")
    fun getAllCombos(): Flow<List<FoodCombo>>

    // 根据ID查询组合
    @Query("SELECT * FROM FoodCombo WHERE id = :id")
    suspend fun getComboById(id: Long): FoodCombo?
}