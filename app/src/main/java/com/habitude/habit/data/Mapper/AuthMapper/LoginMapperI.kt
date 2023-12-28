package com.habitude.habit.data.Mapper.AuthMapper


/**
 * A : Login (domain)
 * V : LoginModel
 * D : LoginResponseModel
 * L : LoginModel (domain)
 */

interface LoginMapperI<A,V,D,L> {
    fun fromLogin(type:A):V
    fun toLoginResponse(type:D):L
}