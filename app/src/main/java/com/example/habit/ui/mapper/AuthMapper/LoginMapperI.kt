package com.example.habit.ui.mapper.AuthMapper


/**
 * A : Login (domain)
 * V : LoginView
 * D : LoginResponseView
 * L : LoginResponse (domain)
 */

interface LoginMapperI<A,V,D,L> {
    fun fromLogin(type:V):A
    fun toLoginResponse(type:L):D
}