package com.habitude.habit.recievers

import android.util.Log
import com.habitude.habit.data.local.Pref.AuthPref
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class HabitRequestNotificationService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        AuthPref(this).setFcmToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("TAG", "onMessageReceived: ", )
    }
}