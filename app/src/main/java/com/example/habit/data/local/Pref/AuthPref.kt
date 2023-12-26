package com.example.habit.data.local.Pref

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast

class AuthPref(val context:Context) {


    private val IS_ANY_HABIT_MODIFIED: String = "is_any_habit_modified"
    private val IS_ANY_GROUP_HABIT_MODIFIED: String = "is_any_group_habit_modified"
    private val FCM_TOKEN: String="fcm_token_for_messaging"
    private val AUTH_KEY: String?="access_token"
    private val USER_ID: String?="user_id"
    private val USER_NAME: String?="user_name"
    private val API_SHOULD_BE_CALLED: String?="is_api_should_be_called"
    private lateinit var authPref: SharedPreferences

    init {
        authPref = context.getSharedPreferences("AUTH_PREF",Context.MODE_PRIVATE)
    }

    fun setToken(token:String){
//        Toast.makeText(context.applicationContext,"token set : $token",Toast.LENGTH_SHORT).show()
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

    fun setUserName(userName: String) {
        authPref.edit().putString(USER_NAME,userName).commit()
    }
    fun getUserName(): String {
        return authPref!!.getString(USER_NAME,null)?:""
    }

    fun putApiShouldBeCalled(shouldCall: Boolean) {
        authPref.edit().putBoolean(API_SHOULD_BE_CALLED,shouldCall).commit()
    }
    fun getApiShouldBeCalled(): Boolean {
        return authPref!!.getBoolean(API_SHOULD_BE_CALLED,false)
    }

    fun setHabitsModified(modified: Boolean) {
        authPref.edit().putBoolean(IS_ANY_HABIT_MODIFIED,modified).commit()
    }
    fun getHabitsModified(): Boolean {
        return authPref!!.getBoolean(IS_ANY_HABIT_MODIFIED,false)
    }

    fun setGroupHabitsModified(modified: Boolean) {
        authPref.edit().putBoolean(IS_ANY_GROUP_HABIT_MODIFIED,modified).commit()
    }

    fun getGroupHabitsModified(): Boolean {
        return authPref!!.getBoolean(IS_ANY_GROUP_HABIT_MODIFIED,false)
    }
}