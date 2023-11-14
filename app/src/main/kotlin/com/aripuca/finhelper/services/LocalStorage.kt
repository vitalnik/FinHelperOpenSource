package com.aripuca.finhelper.services

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStorage @Inject constructor(
    private val preferences: SharedPreferences,
) {

    // investment data

    fun getInvestmentHistorySelectedIndex(defaultValue: Int): Int =
        getInt(INVESTMENT_HISTORY_SELECTED_INDEX, defaultValue)

    fun getInvestmentInitialBalance(defaultValue: String): String =
        getNonEmptyString(INVESTMENT_INITIAL_BALANCE, defaultValue)

    fun getInvestmentRegularAddition(defaultValue: String): String = getNonEmptyString(
        INVESTMENT_REGULAR_ADDITION, defaultValue
    )

    fun getInvestmentRegularAdditionFrequency(defaultValue: Int): Int = getNonEmptyString(
        INVESTMENT_REGULAR_ADDITION_FREQUENCY, defaultValue.toString()
    ).toIntOrNull() ?: defaultValue

    fun getInvestmentInterestRate(defaultValue: String): String = getNonEmptyString(
        INVESTMENT_INTEREST_RATE, defaultValue
    )

    fun getInvestmentYearsToGrow(defaultValue: String): String = getNonEmptyString(
        INVESTMENT_YEARS_TO_GROW, defaultValue
    )

    // mortgage data

    fun getMortgageHistorySelectedIndex(defaultValue: Int): Int =
        getInt(MORTGAGE_HISTORY_SELECTED_INDEX, defaultValue)

    fun getMortgagePrincipalAmount(defaultValue: String): String =
        getNonEmptyString(MORTGAGE_PRINCIPAL_AMOUNT, defaultValue)

    fun getMortgageInterestRate(defaultValue: String): String = getNonEmptyString(
        MORTGAGE_INTEREST_RATE, defaultValue
    )

    fun getMortgageNumberOfYears(defaultValue: String): String = getNonEmptyString(
        MORTGAGE_NUMBER_OF_YEARS, defaultValue
    )

    fun getMortgagePaymentsPerYear(defaultValue: String): String = getNonEmptyString(
        MORTGAGE_PAYMENTS_PER_YEAR, defaultValue
    )

    //--------------------------------------------------------------------------
    fun getGoalInitialBalance(defaultValue: String): String =
        getNonEmptyString(GOAL_INITIAL_BALANCE, defaultValue)

    fun getGoalRegularAddition(defaultValue: String): String = getNonEmptyString(
        GOAL_REGULAR_ADDITION, defaultValue
    )

    fun getGoalRegularAdditionFrequency(defaultValue: Int): Int = getNonEmptyString(
        GOAL_REGULAR_ADDITION_FREQUENCY, defaultValue.toString()
    ).toIntOrNull() ?: defaultValue

    fun getGoalInterestRate(defaultValue: String): String = getNonEmptyString(
        GOAL_INTEREST_RATE, defaultValue
    )

    //--------------------------------------------------------------------------
    fun getAffordabilityMonthlyIncome(defaultValue: String): String =
        getNonEmptyString(AFFORDABILITY_MONTHLY_INCOME, defaultValue)

    fun getAffordabilityMonthlyDebtPayment(defaultValue: String): String =
        getNonEmptyString(AFFORDABILITY_MONTHLY_DEBT_PAYMENT, defaultValue)

    fun getAffordabilityDownPayment(defaultValue: String): String =
        getNonEmptyString(AFFORDABILITY_DOWN_PAYMENT, defaultValue)

    fun getAffordabilityInterestRate(defaultValue: String): String =
        getNonEmptyString(AFFORDABILITY_INTEREST_RATE, defaultValue)

    fun getAffordabilityNumberOfYears(defaultValue: String): String =
        getNonEmptyString(AFFORDABILITY_NUMBER_OF_YEARS, defaultValue)

    //--------------------------------------------------------------------------

    fun saveString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    private fun getString(key: String, defaultValue: String = ""): String {
        return preferences.getString(key, defaultValue) ?: ""
    }

    private fun getNonEmptyString(key: String, defaultValue: String = ""): String {
        var value = preferences.getString(key, defaultValue) ?: ""
        if (value.isEmpty()) {
            value = defaultValue
        }
        return value
    }

    fun saveLong(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    private fun getLong(key: String, defaultValue: Long = 0L): Long {
        return preferences.getLong(key, defaultValue)
    }

    fun saveInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }
    private fun getInt(key: String, defaultValue: Int = 0): Int {
        return preferences.getInt(key, defaultValue)
    }

    companion object {
        const val MORTGAGE_PRINCIPAL_AMOUNT = "MORTGAGE_PRINCIPAL_AMOUNT"
        const val MORTGAGE_INTEREST_RATE = "MORTGAGE_INTEREST_RATE"
        const val MORTGAGE_NUMBER_OF_YEARS = "MORTGAGE_NUMBER_OF_YEARS"
        const val MORTGAGE_PAYMENTS_PER_YEAR = "MORTGAGE_PAYMENTS_PER_YEAR"
        const val MORTGAGE_HISTORY_SELECTED_INDEX = "MORTGAGE_HISTORY_SELECTED_INDEX"

        const val INVESTMENT_INITIAL_BALANCE = "INVESTMENT_INITIAL_BALANCE"
        const val INVESTMENT_REGULAR_ADDITION = "INVESTMENT_REGULAR_ADDITION"
        const val INVESTMENT_REGULAR_ADDITION_FREQUENCY = "INVESTMENT_REGULAR_ADDITION_FREQUENCY"
        const val INVESTMENT_INTEREST_RATE = "INVESTMENT_INTEREST_RATE"
        const val INVESTMENT_YEARS_TO_GROW = "INVESTMENT_YEARS_TO_GROW"
        const val INVESTMENT_HISTORY_SELECTED_INDEX = "INVESTMENT_HISTORY_SELECTED_INDEX"

        const val GOAL_INITIAL_BALANCE = "GOAL_INITIAL_BALANCE"
        const val GOAL_REGULAR_ADDITION = "GOAL_REGULAR_ADDITION"
        const val GOAL_REGULAR_ADDITION_FREQUENCY = "GOAL_REGULAR_ADDITION_FREQUENCY"
        const val GOAL_INTEREST_RATE = "GOAL_INTEREST_RATE"

        const val AFFORDABILITY_MONTHLY_INCOME = "AFFORDABILITY_MONTHLY_INCOME"
        const val AFFORDABILITY_MONTHLY_DEBT_PAYMENT = "AFFORDABILITY_MONTHLY_DEBT_PAYMENT"
        const val AFFORDABILITY_DOWN_PAYMENT = "AFFORDABILITY_DOWN_PAYMENT"
        const val AFFORDABILITY_INTEREST_RATE = "AFFORDABILITY_INTEREST_RATE"
        const val AFFORDABILITY_NUMBER_OF_YEARS = "AFFORDABILITY_NUMBER_OF_YEARS"
    }
}