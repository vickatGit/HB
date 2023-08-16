package com.example.habit.data.local.Pref

import android.content.Context
import android.content.SharedPreferences

class AuthPref(context:Context) {


    private val AUTH_KEY: String?="access_token"
    private var authPref: SharedPreferences? = null

    init {
        authPref = context.getSharedPreferences("AUTH_PREF",Context.MODE_PRIVATE)
    }

    fun setToken(token:String){
        authPref?.let {
            it.edit().putString(AUTH_KEY,token)
        }
    }
    fun  getToken():String{
        return authPref?.getString(AUTH_KEY,null)?:""
    }
}