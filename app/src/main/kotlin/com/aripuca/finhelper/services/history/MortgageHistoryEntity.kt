package com.aripuca.finhelper.services.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mortgage_history")
data class MortgageHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    @ColumnInfo(name = "principal_amount") val principalAmount: String,
    @ColumnInfo(name = "interest_rate") val interestRate: String,
    @ColumnInfo(name = "number_of_years") val numberOfYears: String,
    @ColumnInfo(name = "payments_per_year") val paymentsPerYear: String,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
)