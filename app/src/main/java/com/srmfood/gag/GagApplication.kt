package com.srmfood.gag

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.srmfood.gag.core.constants.AppConstants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GagApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val ordersChannel = NotificationChannel(
                AppConstants.NOTIFICATION_CHANNEL_ORDERS,
                getString(R.string.order_notification_channel),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.order_notification_channel_desc)
                enableVibration(true)
                setShowBadge(true)
            }

            val promosChannel = NotificationChannel(
                AppConstants.NOTIFICATION_CHANNEL_PROMOS,
                "Promotions",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            manager.createNotificationChannels(listOf(ordersChannel, promosChannel))
        }
    }
}
