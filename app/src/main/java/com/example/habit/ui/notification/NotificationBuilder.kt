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
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.habit.R
import com.example.habit.data.common.recievers.UpdateHabitEntryBroadRecieve
import com.example.habit.domain.UseCases.GetHabitThumbUseCase
import com.example.habit.domain.UseCases.ScheduleAlarmUseCase
import com.example.habit.ui.mapper.HabitMapper
import com.example.habit.ui.model.EntryView
import com.example.habit.ui.services.UpdateHabitEntriesService
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class NotificationBuilder @Inject constructor(
    val getHabitThumbUseCase: GetHabitThumbUseCase,
    val scheduleAlarmUseCase: ScheduleAlarmUseCase,
    val habitMapper: HabitMapper
) {
    private val HABIT_UPDATE_NOTI_CHAN_NAME: CharSequence="habit_status_update_channel_name"
    private val HABIT_UPDATE_NOTI_CHAN_ID: String="habit_status_update_channel_id"

    fun sendNotification(app:Context, habitId: Int){
        Log.e("TAG", "sendNotification: ", )

        CoroutineScope(Dispatchers.Default).launch {
            val habit = habitMapper.mapToHabit(getHabitThumbUseCase(habitId))
            withContext(Dispatchers.Main){
                val collapsedView=RemoteViews(app.packageName, R.layout.collapsed_notification_layout)
                habit.entries?.let {
                    if(it.size>3) {
                        val chartImage =
                            buildLineChartAndExportBitmap(app.applicationContext, habit.entries)
                        collapsedView.setImageViewBitmap(R.id.consistency, chartImage)
                        collapsedView.setViewVisibility(R.id.consistency,View.VISIBLE)
                    }

                }
                val completeIntent=Intent(app,UpdateHabitEntryBroadRecieve::class.java).apply {
                    putExtra("isUpgrade",true)
                    putExtra("habitId",habit.id)
                    putExtra("todayDate",LocalDate.now().toString())
                }
                val incompleteIntent=Intent(app,UpdateHabitEntryBroadRecieve::class.java).apply {
                    putExtra("isUpgrade",false)
                    putExtra("habitId",habit.id)
                    putExtra("todayDate",LocalDate.now().toString())
                }

                val completePendingIntent=PendingIntent.getBroadcast(app,0,completeIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                val incompletePendingIntent=PendingIntent.getBroadcast(app,1,incompleteIntent,PendingIntent.FLAG_UPDATE_CURRENT)
                collapsedView.setOnClickPendingIntent(R.id.completed,completePendingIntent)
                collapsedView.setOnClickPendingIntent(R.id.not_completed,incompletePendingIntent)



                val notificationBuilder=NotificationCompat.Builder(app).apply {
                    setSmallIcon(R.drawable.habits_icon)
                    setAutoCancel(true)
                    setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    setCustomContentView(collapsedView)
                }
                val notificationManager= app.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationChannel = NotificationChannel(HABIT_UPDATE_NOTI_CHAN_ID,HABIT_UPDATE_NOTI_CHAN_NAME,NotificationManager.IMPORTANCE_DEFAULT)
                    notificationBuilder.setChannelId(HABIT_UPDATE_NOTI_CHAN_ID)
                    notificationManager.createNotificationChannel(notificationChannel)
                }
                notificationManager.notify(0,notificationBuilder.build())
                if (habit.isReminderOn!!) {
                    scheduleAlarmUseCase(
                        habitId,
                        habit.reminderTime!!.plusMinutes(1),
                        app
                    )
                }

            }
        }
    }

    private fun buildLineChartAndExportBitmap(
        context: Context,
        mapEntries: HashMap<LocalDate, EntryView>?
    ): Bitmap {

        //values for single line chart on the graph
        val lineChart=LineChart(context)
        val layoutParams = ViewGroup.LayoutParams(200, 100)
        lineChart.layoutParams = layoutParams
        val entries:MutableList<Entry> = mutableListOf()
        mapEntries?.mapValues {
            entries.add(Entry(it.value.timestamp?.dayOfMonth!!.toFloat(),it.value.score!!.toFloat()))
        }
        if(entries.size>0) {
            //Each LineDateSet Represents data for sing line chart on Graph
            val dataset = LineDataSet(entries, "")
            val startColor = context.resources.getColor(R.color.orange)
            val midColor = context.resources.getColor(R.color.orange_op_20)
            val endColor = context.resources.getColor(R.color.transparent)
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(startColor,midColor, endColor)
            )

            dataset.setDrawFilled(true)
            dataset.fillDrawable = gradientDrawable

            dataset.color=context.resources.getColor(R.color.orange)
            dataset.lineWidth=3f
            dataset.setDrawCircleHole(false)
            dataset.setDrawCircles(false)
            dataset.setDrawValues(false)
            dataset.mode= LineDataSet.Mode.CUBIC_BEZIER

            val xtAxis=lineChart.xAxis
            val ylAxis=lineChart.axisLeft
            val yrAxis=lineChart.axisRight
            xtAxis.labelCount=7
            xtAxis.isEnabled=false
            ylAxis.isEnabled=false
            yrAxis.isEnabled=false

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

            return createBitmapFromView(context,lineChart)

    }

    fun createBitmapFromView(context: Context,view: View): Bitmap {
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
            canvas.drawColor(Color.WHITE) // Default background color if no background set
        }

        view.layout(0, 0, width, heightInPixels)
        view.draw(canvas)

        return bitmap
    }






}