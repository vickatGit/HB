package com.example.habit.data.Mapper

/**
 * A = EntryEntity
 * V = Entry
 * D = EntryModel (Network)
 */
interface EntryMapperI<A,V,D> {
    fun mapToEntry(type : A):V

    fun mapFromEntry(type : V) : A
    fun mapFromEntryModel(type : D) : A
    fun mapToEntryModel(type : A) : D

}