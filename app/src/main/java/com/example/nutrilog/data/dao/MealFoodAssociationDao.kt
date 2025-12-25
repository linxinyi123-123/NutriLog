package com.example.nutrilog.data.dao

import androidx.room.*
import com.example.nutrilog.data.entities.FoodItem
import com.example.nutrilog.data.entities.MealFoodAssociation
import kotlinx.coroutines.flow.Flow

@Dao
interface MealFoodAssociationDao {
    
    // 基础CRUD操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(association: MealFoodAssociation): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(associations: List<MealFoodAssociation>)
    
    @Update
    suspend fun update(association: MealFoodAssociation)
    
    @Delete
    suspend fun delete(association: MealFoodAssociation)
    
    @Query("DELETE FROM meal_food_associations WHERE meal_record_id = :mealRecordId")
    suspend fun deleteByMealRecordId(mealRecordId: Long)
    
    @Query("DELETE FROM meal_food_associations")
    suspend fun deleteAll()
    
    // 查询操作
    @Query("SELECT * FROM meal_food_associations WHERE id = :id")
    suspend fun getById(id: Long): MealFoodAssociation?
    
    @Query("SELECT * FROM meal_food_associations WHERE meal_record_id = :mealRecordId")
    suspend fun getByMealRecordId(mealRecordId: Long): List<MealFoodAssociation>
    
    @Query("SELECT * FROM meal_food_associations WHERE food_id = :foodId")
    suspend fun getByFoodId(foodId: Long): List<MealFoodAssociation>
    
    @Query("""
        SELECT mfa.* FROM meal_food_associations mfa
        JOIN meal_records mr ON mfa.meal_record_id = mr.id
        WHERE mr.date = :date
    """)
    suspend fun getByDate(date: String): List<MealFoodAssociation>
    
    // 获取记录的食物详情
    @Query("""
        SELECT fi.*, mfa.quantity, mfa.unit 
        FROM food_items fi
        JOIN meal_food_associations mfa ON fi.id = mfa.food_id
        WHERE mfa.meal_record_id = :mealRecordId
    """)
    suspend fun getFoodsWithQuantityByMealRecordId(mealRecordId: Long): List<FoodWithQuantity>
    
    // 统计操作
    @Query("SELECT COUNT(*) FROM meal_food_associations WHERE meal_record_id = :mealRecordId")
    suspend fun countByMealRecordId(mealRecordId: Long): Int
    
    @Query("SELECT COUNT(*) FROM meal_food_associations")
    suspend fun count(): Int
}

data class FoodWithQuantity(
    val food: FoodItem,
    val quantity: Double,
    val unit: String
)