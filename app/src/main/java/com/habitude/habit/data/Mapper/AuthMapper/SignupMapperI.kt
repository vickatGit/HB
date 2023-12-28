package com.habitude.habit.data.Mapper.AuthMapper

/**
 * A : Signup (domain)
 * V : SignupModel
 * D : SignupResponseModel
 * L : SignupModel (domain)
 */

interface SignupMapperI<A,V,D,L> {
    fun fromSignup(type:A):V
    fun toSignupResponse(type:D):L
}