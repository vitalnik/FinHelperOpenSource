package com.aripuca.finhelper.services.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "investment_history")
data class InvestmentHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    @ColumnInfo(name = "initial_investment") val initialInvestment: String,
    @ColumnInfo(name = "interest_rate") val interestRate: String,
    @ColumnInfo(name = "number_of_years") val numberOfYears: String,
    @ColumnInfo(name = "regular_contribution") val regularContribution: String,
    @ColumnInfo(name = "contribution_frequency") val contributionFrequency: String,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "updated_at") val updatedAt: String,
)