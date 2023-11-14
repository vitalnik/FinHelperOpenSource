package com.aripuca.finhelper.services.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.aripuca.finhelper.services.analytics.AnalyticsClient
import com.aripuca.finhelper.services.analytics.FirebaseAnalyticsClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

fun List<Purchase?>.checkPurchases(): Boolean =
    this.any {
        it?.products?.contains(BillingManager.REMOVE_ADS_INAPP_PRODUCT) ?: false ||
                it?.products?.contains(BillingManager.BUY_ME_COFFEE_INAPP_PRODUCT) ?: false
    }

fun List<Purchase?>.checkRemoveAdsPurchase(): Boolean =
    this.any {

        Log.d("TAG", ">>> checkRemoveAdsPurchase Purchase: $it")

        it?.products?.contains(BillingManager.REMOVE_ADS_INAPP_PRODUCT) ?: false
    }

fun List<Purchase?>.checkBuyMeCoffeePurchase(): Boolean =
    this.any {

        Log.d("TAG", ">>> checkBuyMeCoffeePurchase Purchase: $it")

        it?.products?.contains(BillingManager.BUY_ME_COFFEE_INAPP_PRODUCT) ?: false
    }

fun Int.getResponseCodeString(): String =
    when (this) {
        BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> "SERVICE_TIMEOUT"
        BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> "FEATURE_NOT_SUPPORTED"
        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> "SERVICE_DISCONNECTED"
        BillingClient.BillingResponseCode.OK -> "OK"
        BillingClient.BillingResponseCode.USER_CANCELED -> "USER_CANCELED"
        BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> "SERVICE_UNAVAILABLE"
        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> "BILLING_UNAVAILABLE"
        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> "ITEM_UNAVAILABLE"
        BillingClient.BillingResponseCode.DEVELOPER_ERROR -> "DEVELOPER_ERROR"
        BillingClient.BillingResponseCode.ERROR -> "ERROR"
        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> "ITEM_ALREADY_OWNED"
        BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> "ITEM_NOT_OWNED"
        else -> ""
    }

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext val context: Context,
    private val analyticsClient: AnalyticsClient
) : CoroutineScope {

    private var connectionRetryCount = 0

    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Default + job

    private val _purchasesFlow: MutableStateFlow<List<Purchase?>> = MutableStateFlow(mutableListOf())
    val purchasesFlow = _purchasesFlow.asStateFlow()

    private val _productDetailsFlow: MutableStateFlow<List<ProductDetails>> =
        MutableStateFlow(mutableListOf())

    val productDetailsFlow = _productDetailsFlow.asStateFlow()

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->

            Log.d(
                "TAG",
                ">>> PurchasesUpdatedListener: billingResult: $billingResult purchases: $purchases"
            )

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                logInAppPurchaseSuccess(purchases[0].products[0])
                launch {
                    queryPurchases()
                }
            } else {
                analyticsClient.log(
                    FirebaseAnalyticsClient.INAPP_PURCHASE_ERROR,
                    mapOf("responseCode" to billingResult.responseCode.getResponseCodeString())
                )
            }
        }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    fun startConnection() {

        connectionRetryCount++

        analyticsClient.log(
            FirebaseAnalyticsClient.BILLING_SERVICE_CONNECTION_START,
            mapOf("connectionRetryCount" to connectionRetryCount.toString())
        )

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                connectionRetryCount = 0
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    analyticsClient.log(
                        FirebaseAnalyticsClient.BILLING_SERVICE_CONNECTED
                    )
                    launch(context = Dispatchers.IO) {
                        queryPurchases()
                        queryProductDetails()
                    }
                } else {
                    Log.d(
                        "TAG",
                        ">>> onBillingSetupFinished: " + billingResult.responseCode.getResponseCodeString()
                    )
                    analyticsClient.log(
                        FirebaseAnalyticsClient.BILLING_SERVICE_CONNECTION_ERROR,
                        mapOf("responseCode" to billingResult.responseCode.getResponseCodeString())
                    )
                }
            }

            override fun onBillingServiceDisconnected() {

                analyticsClient.log(
                    FirebaseAnalyticsClient.BILLING_SERVICE_DISCONNECTED,
                    mapOf("connectionRetryCount" to connectionRetryCount.toString())
                )

                Log.d("TAG", ">>> onBillingServiceDisconnected")

                if (connectionRetryCount < 3) {
                    Log.d("TAG", ">>> Billing client connection retry: $connectionRetryCount")
                    startConnection()
                } else {
                    analyticsClient.log(FirebaseAnalyticsClient.BILLING_SERVICE_CONNECTION_UNAVAILABLE)
                }
            }
        })
    }

    suspend fun queryProductDetails() {

        val productList = ArrayList<QueryProductDetailsParams.Product>()
        productList.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(REMOVE_ADS_INAPP_PRODUCT)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        productList.add(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(BUY_ME_COFFEE_INAPP_PRODUCT)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params.build())
        }
        Log.d("TAG", ">>> queryProductDetails: $productDetailsResult")
        productDetailsResult.productDetailsList?.let {
            _productDetailsFlow.emit(it)
        }
    }

    private suspend fun queryPurchases() {
        val purchasesResult = withContext(Dispatchers.IO) {
            billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType("inapp").build()
            )
        }
        Log.d("TAG", ">>> queryPurchases: $purchasesResult")
        _purchasesFlow.emit(purchasesResult.purchasesList)
    }

//    private suspend fun consumePurchase(purchase: Purchase) {
//        val consumeParams =
//            ConsumeParams.newBuilder()
//                .setPurchaseToken(purchase.purchaseToken)
//                .build()
//        val consumeResult = withContext(Dispatchers.IO) {
//            billingClient.consumePurchase(consumeParams)
//        }
//    }

    fun launchBillingFlow(activity: Activity, flowParams: BillingFlowParams): Int {
        return billingClient.launchBillingFlow(activity, flowParams).responseCode
    }

    private fun logInAppPurchaseSuccess(productId: String) {
        when (productId) {
            REMOVE_ADS_INAPP_PRODUCT ->
                analyticsClient.log(FirebaseAnalyticsClient.REMOVE_ADS_PURCHASE_SUCCESS)
            BUY_ME_COFFEE_INAPP_PRODUCT ->
                analyticsClient.log(FirebaseAnalyticsClient.BUY_ME_COFFEE_PURCHASE_SUCCESS)
        }
    }

    companion object {
        const val BUY_ME_COFFEE_INAPP_PRODUCT = "buy_me_coffee"
        const val REMOVE_ADS_INAPP_PRODUCT = "remove_ads"
    }

}