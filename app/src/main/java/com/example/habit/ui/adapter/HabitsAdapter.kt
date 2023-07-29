package com.example.habit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.habit.R
import com.example.habit.databinding.HabitItemLayoutBinding
import com.example.habit.ui.callback.HabitClick
import com.example.habit.ui.model.EntryView
import com.example.habit.ui.model.HabitThumbView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

class HabitsAdapter(val habits: MutableList<HabitThumbView>, val habitClick: HabitClick) : RecyclerView.Adapter<HabitsAdapter.HabitHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitHolder {
        return HabitHolder(
            HabitItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return  habits.size
    }

    override fun onBindViewHolder(holder: HabitHolder, position: Int) {
        val habit= habits[holder.absoluteAdapterPosition]
        holder.binding?.let {
            initialiseProgress(habit,it)
            it.habit.text = habit.title
            initialiseConsistencyGraph(habit.entries, it)
            it.habitContainer.setOnClickListener {
                habitClick.habitClick(habitId = habit.id.toString())
            }
        }

    }
    private fun initialiseProgress(habit: HabitThumbView,binding:HabitItemLayoutBinding?) {
        val totalHabitDuration = ChronoUnit.DAYS.between(habit.startDate, habit.endDate)
        var daysCompleted = 0
        habit.entries?.mapValues {
//            if (it.key.isBefore(LocalDate.now()) && it.key.isEqual(LocalDate.now())) {
            if (it.value.completed) ++daysCompleted
//            }
        }
        val progress = (totalHabitDuration!! / 100f) * daysCompleted!!
        binding?.progress?.progress = progress.roundToInt()
        binding?.progressPercentage?.text = "${DecimalFormat("#.#").format(progress)}%"
    }

    inner class HabitHolder(var binding: HabitItemLayoutBinding?): RecyclerView.ViewHolder(binding!!.root)

    private fun initialiseConsistencyGraph(mapEntries: HashMap<LocalDate, EntryView>?, binding:HabitItemLayoutBinding) {
        //values for single line chart on the graph
        val entries:MutableList<Entry> = mutableListOf()
        mapEntries?.mapValues {
            entries.add(Entry(it.value.timestamp!!.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli().toFloat(),it.value.score!!.toFloat()))
        }
        if(entries.size>3) {
            //Each LineDateSet Represents data for sing line chart on Graph
            val dataset = LineDataSet(entries, "")
            dataset.color=binding.root.resources.getColor(R.color.orange)
            dataset.lineWidth=3f
            dataset.setDrawCircleHole(false)
            dataset.setDrawCircles(false)
            dataset.setDrawFilled(false)
            dataset.setDrawValues(false)
            dataset.mode=LineDataSet.Mode.CUBIC_BEZIER

            val xtAxis=binding.consistency.xAxis
            val ylAxis=binding.consistency.axisLeft
            val yrAxis=binding.consistency.axisRight
            xtAxis.labelCount=7
            xtAxis.isEnabled=false
            ylAxis.isEnabled=false
            yrAxis.isEnabled=false

            //LineData object is Needed by Graph and to create LineData() object we Need to Pass list ILineDataSet objects
            // since it has capability to show multiple Line chart on single graph whereas LineDataSet Object Represents one chart in a Graph
            val datasets = mutableListOf<ILineDataSet>(dataset)
            val chartLineData = LineData(datasets)

            binding.consistency.description.isEnabled = false
            binding.consistency.legend.isEnabled = false
            binding.consistency.setTouchEnabled(false)
            binding.consistency.isDragEnabled = false
            binding.consistency.setScaleEnabled(false)
            binding.consistency.setPinchZoom(false)
            binding.consistency.isDoubleTapToZoomEnabled = false
            binding.consistency.isHighlightPerTapEnabled = false
            binding.consistency.isHighlightPerDragEnabled = false
            binding.consistency.animateX(1000)

            binding.consistency.data = chartLineData
            binding.consistency.invalidate()
        }else{
            binding.consistency.isVisible=false
            binding.consistencyFg.isVisible=false
            binding.graphTitle.isVisible=false
        }

    }

}