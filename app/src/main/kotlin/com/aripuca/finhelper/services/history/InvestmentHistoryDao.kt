package com.aripuca.finhelper.services.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentHistoryDao {

    @Query("SELECT * FROM investment_history ORDER BY title ASC")
    suspend fun getAll(): List<InvestmentHistoryEntity>

    @Query("SELECT * FROM investment_history ORDER BY title ASC")
    fun getAllAsFlow(): Flow<List<InvestmentHistoryEntity>>

    @Query("SELECT * FROM investment_history WHERE id = :id")
    fun getById(id: Int): Flow<InvestmentHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: InvestmentHistoryEntity)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(entity: InvestmentHistoryEntity)

    @Query("DELETE FROM investment_history")
    suspend fun deleteAll()

    @Query("DELETE FROM investment_history WHERE id = :id")
    suspend fun deleteById(id: Int)
}