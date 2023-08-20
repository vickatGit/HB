package com.example.habit.ui.mapper.GroupHabitMapper

/**
 * A : GroupHabitView
 * V : GroupHabit (domain)
 * D : HabitView
 * L : GroupHabitsWithHabit  (domain)
 * E : GroupHabitsWithHabitView
 * F : Member  (domain)
 * G : MemberView
 */
interface GroupHabitMapperI<A,V,D,L,E,F,G> {
    fun toGroupHabit(type : A):V
    fun toGroupHabitView(type : V):A
    fun toGroupHabitFromHabit(type: D):V

    fun fromGroupHabitsWithHabit(type: L):E

    fun fromMember(type:F):G
}