package com.sleeplessdog.matchthewords.payments

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import com.sleeplessdog.matchthewords.backend.data.repository.AppPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BillingManager(
    private val context: Context,
    private val appPrefs: AppPrefs,
) {
    private var billingClient: BillingClient? = null

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium

    companion object {
        const val PRODUCT_SUBSCRIPTION_MONTHLY = "premium_monthly"
        const val PRODUCT_SUBSCRIPTION_YEARLY = "premium_yearly"
        const val PRODUCT_LIFETIME = "premium_lifetime"
    }

    fun init() {
        billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    purchases?.forEach { handlePurchase(it) }
                }
            }
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    checkExistingPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                // попробуем переподключиться при следующем запросе
            }
        })
    }

    private fun checkExistingPurchases() {
        // проверяем подписки
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { _, purchases ->
            val hasActiveSub = purchases.any {
                it.purchaseState == Purchase.PurchaseState.PURCHASED
            }
            if (hasActiveSub) {
                _isPremium.value = true
                appPrefs.setPremium(true)
                return@queryPurchasesAsync
            }
        }

        // проверяем разовую покупку
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { _, purchases ->
            val hasLifetime = purchases.any {
                it.purchaseState == Purchase.PurchaseState.PURCHASED &&
                        it.products.contains(PRODUCT_LIFETIME)
            }
            if (hasLifetime) {
                _isPremium.value = true
                appPrefs.setPremium(true)
            }
        }
    }

    suspend fun getProducts(): List<ProductDetails> {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_SUBSCRIPTION_MONTHLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_SUBSCRIPTION_YEARLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_LIFETIME)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        val result = billingClient?.queryProductDetails(params)
        return result?.productDetailsList ?: emptyList()
    }

    fun launchPurchase(activity: Activity, productDetails: ProductDetails) {
        val offerToken = productDetails.subscriptionOfferDetails
            ?.firstOrNull()?.offerToken

        val productDetailsParams = if (offerToken != null) {
            // подписка
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        } else {
            // разовая покупка
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        }

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        billingClient?.launchBillingFlow(activity, flowParams)
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            _isPremium.value = true
            appPrefs.setPremium(true)

            // подтверждаем покупку
            if (!purchase.isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient?.acknowledgePurchase(params) {}
            }
        }
    }
}