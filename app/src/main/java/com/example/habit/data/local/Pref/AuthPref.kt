package com.example.habit.data.local.Pref

import android.content.Context
import android.content.SharedPreferences

class AuthPref(context:Context) {


    private val FCM_TOKEN: String="fcm_token_for_messaging"
    private val AUTH_KEY: String?="access_token"
    private val USER_ID: String?="user_id"
    private lateinit var authPref: SharedPreferences

    init {
        authPref = context.getSharedPreferences("AUTH_PREF",Context.MODE_PRIVATE)
    }

    fun setToken(token:String){
        authPref.edit().putString(AUTH_KEY,token).commit()

    }
    fun  getToken():String{
        return authPref!!.getString(AUTH_KEY,null)?:""
    }

    fun setUserId(userId:String){
        authPref.edit().putString(USER_ID,userId).commit()
    }
    fun getUserId(): String {
        return authPref!!.getString(USER_ID,null)?:""
    }

    fun setFcmToken(fcmToken: String) {
        authPref.edit().putString(FCM_TOKEN,fcmToken).commit()
    }
    fun getFcmToken(): String {
        return authPref!!.getString(FCM_TOKEN,null)?:""
    }
}