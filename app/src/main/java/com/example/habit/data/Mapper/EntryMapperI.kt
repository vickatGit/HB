package com.example.habit.data.Mapper

interface EntryMapperI<A,V> {
    fun mapToEntry(type : A):V

    fun mapFromEntry(type : V) : A

}