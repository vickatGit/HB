package com.example.habit.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.habit.R
import com.example.habit.recievers.UpdateHabitEntryBroadRecieve
import com.example.habit.domain.UseCases.HabitUseCase.GetHabitThumbUseCase
import com.example.habit.domain.UseCases.HabitUseCase.ScheduleAlarmUseCase
import com.example.habit.ui.mapper.HabitMapper.HabitMapper
import com.example.habit.ui.model.EntryView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.notify
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.random.Random

class NotificationBuilder @Inject constructor(
    val getHabitThumbUseCase: GetHabitThumbUseCase,
    val scheduleAlarmUseCase: ScheduleAlarmUseCase,
    val habitMapper: HabitMapper
) {
    private val HABIT_UPDATE_NOTI_CHAN_NAME: CharSequence = "habit_status_update_channel_name"
    private val HABIT_UPDATE_NOTI_CHAN_ID: String = "habit_status_update_channel_id"

    fun sendNotification(app: Context, habitId: String, habitServerId: String?) {
        Log.e("TAG", "sendNotification: ")

        CoroutineScope(Dispatchers.Default).launch {
            val habit = habitMapper.mapToHabit(getHabitThumbUseCase(habitId))
            Log.e("TAG", "sendNotification: habit retrieved $habit" )
            Log.e("TAG", "sendNotification: alarm  ${compareLocalDateTime(habit.reminderTime!!,LocalDateTime.now())}" )
            if(compareLocalDateTime(habit.reminderTime!!,LocalDateTime.now())) {
                withContext(Dispatchers.Main) {
                    val collapsedView =
                        RemoteViews(app.packageName, R.layout.collapsed_notification_layout)
                    collapsedView.setTextViewText(R.id.title, habit.title)
                    habit.entries?.let {
                        if (it.size > 3) {
                            val chartImage =
                                buildLineChartAndExportBitmap(app.applicationContext, habit.entries)
                            collapsedView.setImageViewBitmap(R.id.consistency, chartImage)
                            collapsedView.setViewVisibility(R.id.consistency, View.VISIBLE)
                        }

                    }
                    val progress =
                        initialiseProgress(habit.startDate!!, habit.endDate!!, habit.entries)
                    collapsedView.setTextViewText(
                        R.id.progress_percentage,
                        formatProgress(progress)
                    )
                    collapsedView.setTextViewText(R.id.habit_title, habit.title)
//                val progressIndicator = getProgressView(progress.roundToInt(),app.applicationContext)
//                val progressBitmap = createBitmapFromView(app.applicationContext,progressIndicator)
//                collapsedView.setImageViewBitmap(R.id.progress_img, progressBitmap)


                    val completeIntent =
                        Intent(app, UpdateHabitEntryBroadRecieve::class.java).apply {
                            putExtra("isUpgrade", true)
                            putExtra("habitId", habit.id)
                            putExtra("habitServerId", habit.serverId)
                            putExtra("todayDate", LocalDate.now().toString())
                        }
                    val incompleteIntent =
                        Intent(app, UpdateHabitEntryBroadRecieve::class.java).apply {
                            putExtra("isUpgrade", false)
                            putExtra("habitId", habit.id)
                            putExtra("habitServerId", habit.serverId)
                            putExtra("todayDate", LocalDate.now().toString())
                        }

                    val completePendingIntent = PendingIntent.getBroadcast(
                        app,
                        0,
                        completeIntent,
                        PendingIntent.FLAG_MUTABLE
                    )
                    val incompletePendingIntent = PendingIntent.getBroadcast(
                        app,
                        1,
                        incompleteIntent,
                        PendingIntent.FLAG_MUTABLE
                    )
                    collapsedView.setOnClickPendingIntent(R.id.completed, completePendingIntent)
                    collapsedView.setOnClickPendingIntent(
                        R.id.not_completed,
                        incompletePendingIntent
                    )


                    val notificationBuilder = NotificationCompat.Builder(app).apply {
                        setSmallIcon(R.drawable.habits_icon)
                        setAutoCancel(true)
                        setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        setCustomContentView(collapsedView)
                    }
                    val notificationManager =
                        app.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val notificationChannel = NotificationChannel(
                            HABIT_UPDATE_NOTI_CHAN_ID,
                            HABIT_UPDATE_NOTI_CHAN_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT
                        )
                        notificationBuilder.setChannelId(HABIT_UPDATE_NOTI_CHAN_ID)
                        notificationManager.createNotificationChannel(notificationChannel)
                    }
                    val habitId = Random(10).nextInt()
                    notificationManager.notify(habitId, notificationBuilder.build())

                    Log.e("TAG", "sendNotification: send",)
                    if (habit.isReminderOn!!) {
//                    scheduleAlarmUseCase(
//                        habitId,
//                        habit.reminderTime!!.plusDays(1),
//                        app
//                    )
                    }

                }
            }
        }
    }

    private fun getProgressView(progress: Int, context: Context): ProgressBar {
        val progressIndicator = ProgressBar(context)
        val layoutParams = FrameLayout.LayoutParams(50,50)
        layoutParams.setMargins(0, 0, 0, 0)
        progressIndicator.layoutParams = layoutParams
        progressIndicator.setPadding(0, 0, 0, 0)
        progressIndicator.rotation=-90f
        progressIndicator.setProgress(progress,false)
        progressIndicator.progressDrawable=context.resources.getDrawable(R.drawable.progress_draw)
//        progressIndicator.indeterminateDrawable=context.resources.getDrawable(R.drawable.progress_draw)
        progressIndicator.isIndeterminate=false
        return progressIndicator
    }

    private fun buildLineChartAndExportBitmap(
        context: Context,
        mapEntries: HashMap<LocalDate, EntryView>?
    ): Bitmap {

        //values for single line chart on the graph
        val lineChart = LineChart(context)
        val layoutParams = ViewGroup.LayoutParams(200, 100)
        lineChart.layoutParams = layoutParams
        val entries: MutableList<Entry> = mutableListOf()
        mapEntries?.mapValues {
            entries.add(
                Entry(
                    it.value.timestamp?.dayOfMonth!!.toFloat(),
                    it.value.score!!.toFloat()
                )
            )
        }
        if (entries.size > 0) {
            //Each LineDateSet Represents data for sing line chart on Graph
            val dataset = LineDataSet(entries, "")
            val startColor = context.resources.getColor(R.color.orange)
            val midColor = context.resources.getColor(R.color.orange_op_20)
            val endColor = context.resources.getColor(R.color.transparent)
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(startColor, midColor, endColor)
            )

            dataset.setDrawFilled(true)
            dataset.fillDrawable = gradientDrawable

            dataset.color = context.resources.getColor(R.color.orange)
            dataset.lineWidth = 3f
            dataset.setDrawCircleHole(false)
            dataset.setDrawCircles(false)
            dataset.setDrawValues(false)
            dataset.mode = LineDataSet.Mode.CUBIC_BEZIER

            val xtAxis = lineChart.xAxis
            val ylAxis = lineChart.axisLeft
            val yrAxis = lineChart.axisRight
            xtAxis.labelCount = 7
            xtAxis.isEnabled = false
            ylAxis.isEnabled = false
            yrAxis.isEnabled = false

            //LineData object is Needed by Graph and to create LineData() object we Need to Pass list ILineDataSet objects
            // since it has capability to show multiple Line chart on single graph whereas LineDataSet Object Represents one chart in a Graph
            val datasets = mutableListOf<ILineDataSet>(dataset)
            val chartLineData = LineData(datasets)

            lineChart.description.isEnabled = false
            lineChart.legend.isEnabled = false
            lineChart.setTouchEnabled(false)
            lineChart.isDragEnabled = false
            lineChart.setScaleEnabled(false)
            lineChart.setPinchZoom(false)
            lineChart.isDoubleTapToZoomEnabled = false
            lineChart.isHighlightPerTapEnabled = false
            lineChart.isHighlightPerDragEnabled = false


            lineChart.data = chartLineData


            lineChart.invalidate()

        }

        return createBitmapFromView(context, lineChart)

    }

    private fun initialiseProgress(
        startDate: LocalDate,
        endDate: LocalDate,
        habitEntries: HashMap<LocalDate, EntryView>?
    ): Float {
        val totalHabitDuration = ChronoUnit.DAYS.between(startDate, endDate)+1
        var daysCompleted = 0
        habitEntries?.mapValues {
            if (it.value.completed) ++daysCompleted
        }
        return (daysCompleted.toFloat() / totalHabitDuration.toFloat()) * 100f

    }

    private fun formatProgress(progress: Float): String {
        return "${DecimalFormat("#.#").format(progress)}%"
    }


    fun createBitmapFromView(context: Context, view: View): Bitmap {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val width = displayMetrics.widthPixels
        val heightInDp = 100

        val density = displayMetrics.density
        val heightInPixels = (heightInDp * density).toInt()

        view.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(heightInPixels, View.MeasureSpec.EXACTLY)
        )

        val bitmap = Bitmap.createBitmap(width, heightInPixels, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val background = view.background
        if (background != null) {
            background.draw(canvas)
        } else {
            canvas.drawColor(context.resources.getColor(R.color.transparent)) // Default background color if no background set
        }

        view.layout(0, 0, width, heightInPixels)
        view.draw(canvas)

        return bitmap
    }

    fun compareLocalDateTime(dateTime1: LocalDateTime, dateTime2: LocalDateTime): Boolean {
        val differenceInMinutes = ChronoUnit.MINUTES.between(dateTime1, dateTime2)

        return when {
            differenceInMinutes == 0L -> true // If both dates are equal
            differenceInMinutes < 1 -> true // If the first date is before the second date by less than one minute
            differenceInMinutes > 1 -> false // If the first date is after the second date by more than one minute
            else -> false
        }
    }


}