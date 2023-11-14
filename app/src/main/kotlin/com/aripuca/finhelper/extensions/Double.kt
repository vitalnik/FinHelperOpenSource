package com.aripuca.finhelper.extensions

import java.text.NumberFormat
import kotlin.math.abs

fun Double.roundTo(fractionDigits: Int): String {
    return "%.${fractionDigits}f".format(this)
}

fun Double.checkRange(min: Double = 0.0, max: Double): Boolean {
    return this in min..max
}

//fun Double.toCurrency(): String = "$${this.roundTo(2)}"

fun Double.toCurrency(): String {

    val format = NumberFormat.getNumberInstance()
    format.minimumFractionDigits = 2
    format.maximumFractionDigits = 2
    format.isGroupingUsed = true

    val currencyValue = abs(this)
    return "\$${Typography.nbsp}${format.format(currencyValue)}"
}

fun String.fromCurrencyToDouble(): Double {
    return this.replace("$", "")
        .replace("${Typography.nbsp}", "")
        .replace(" ", "")
        .replace(",", "")
        .toDoubleOrNull() ?: 0.0
}