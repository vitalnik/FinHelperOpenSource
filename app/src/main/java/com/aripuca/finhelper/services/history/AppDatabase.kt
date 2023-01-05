package com.aripuca.finhelper.services.history

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MortgageHistoryEntity::class, InvestmentHistoryEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mortgageHistoryDao(): MortgageHistoryDao
    abstract fun investmentHistoryDao(): InvestmentHistoryDao
}