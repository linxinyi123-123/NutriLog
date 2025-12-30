package com.example.nutrilog.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.nutrilog.data.entities.ComboFood

@Dao
interface ComboFoodDao {
    // 插入组合食物
    @Insert
    suspend fun insert(comboFood: ComboFood)

    // 批量插入
    @Insert
    suspend fun insertAll(vararg comboFoods: ComboFood)

    // 删除组合食物
    @Delete
    suspend fun delete(comboFood: ComboFood)

    // 根据组合ID查询所有食物
    @Query("SELECT * FROM ComboFood WHERE comboId = :comboId")
    suspend fun getFoodsByComboId(comboId: Long): List<ComboFood>

    // 删除组合的所有食物
    @Query("DELETE FROM ComboFood WHERE comboId = :comboId")
    suspend fun deleteByComboId(comboId: Long)
}