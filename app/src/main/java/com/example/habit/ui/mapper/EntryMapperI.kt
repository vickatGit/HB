package com.example.habit.ui.mapper

interface EntryMapperI <A,V> {
    fun mapToEntry(type : A):V

    fun mapFromEntry(type : V) : A
}