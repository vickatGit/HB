package com.example.habit.ui.mapper.AuthMapper

/**
 * A : Signup (domain)
 * V : SignupView
 * D : SignupResponseView
 * L : SignupResponse (domain)
 */

interface SignupMapperI<A,V,D,L> {
    fun fromSignup(type:V):A
    fun toSignupResponse(type:L):D
}