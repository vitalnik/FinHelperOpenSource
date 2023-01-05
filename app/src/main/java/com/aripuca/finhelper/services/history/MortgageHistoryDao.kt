package com.aripuca.finhelper.services.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MortgageHistoryDao {

    @Query("SELECT * FROM mortgage_history ORDER BY title ASC")
    fun getAllAsFlow(): Flow<List<MortgageHistoryEntity>>

    @Query("SELECT * FROM mortgage_history ORDER BY title ASC")
    suspend fun getAll(): List<MortgageHistoryEntity>

    @Query("SELECT * FROM mortgage_history WHERE id = :id")
    fun getById(id: Int): Flow<MortgageHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MortgageHistoryEntity)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(entity: MortgageHistoryEntity)

    @Query("DELETE FROM mortgage_history")
    suspend fun deleteAll()

    @Query("DELETE FROM mortgage_history WHERE id = :id")
    suspend fun deleteById(id: Int)
}