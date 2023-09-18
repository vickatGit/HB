package com.example.habit.data.Mapper.NotificationMapper

import com.example.habit.data.Mapper.SocialMapper.UserMapper.toUser
import com.example.habit.data.network.model.HabitRequestModel.HabitRequestModel
import com.example.habit.domain.models.notification.HabitRequest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object NotificationMapper {

    fun HabitRequestModel.toHabitRequest(): HabitRequest {
        return HabitRequest(
            this.from.toUser(),
            to,
            localDateConverter(startDate)!!,
            localDateConverter(endDate)!!,
            habitTitle,
            groupHabitId
        )
    }

    private fun localDateConverter(date:String): LocalDate? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            LocalDate.parse(date, formatter)
        } catch (e: Exception) {
            System.err.println("Error parsing the date-time string: " + e.message)
            null
        }
    }
}