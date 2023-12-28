package com.habitude.habit.ui.fragment.time;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.habitude.habit.databinding.TimeLayoutBinding;

import java.util.Calendar;

public class CustomTimePicker extends FrameLayout {
    

    private Calendar selectedTime;
    private TimeLayoutBinding binding;

    public CustomTimePicker(Context context) {
        super(context);
        initControl(context);
    }

    public CustomTimePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initControl(context);
    }

    private void initControl(Context context){
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        binding= TimeLayoutBinding.inflate(inflater,this,true);
        selectedTime=Calendar.getInstance();
        binding.hour.setMinValue(1);
        binding.hour.setMaxValue(12);
        binding.hour.setValue(selectedTime.get(Calendar.HOUR_OF_DAY));
        binding.minute.setMinValue(0);
        binding.minute.setMaxValue(59);
        binding.minute.setValue(selectedTime.get(Calendar.MINUTE));
        binding.second.setMinValue(0);
        binding.second.setMaxValue(59);
        binding.second.setValue(selectedTime.get(Calendar.SECOND));
        binding.amPm.setMinValue(0);
        binding.amPm.setMaxValue(1);
        binding.amPm.setDisplayedValues(new String[]{"AM","PM"});
        binding.amPm.setValue(selectedTime.get(Calendar.AM_PM));

        NumberPicker.Formatter numberFormatter=new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        };
        binding.hour.setFormatter(numberFormatter);
        binding.minute.setFormatter(numberFormatter);
        binding.second.setFormatter(numberFormatter);

        binding.hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.e("TAG", "onValueChange: binding.hour "+newVal );
                selectedTime.set(Calendar.HOUR_OF_DAY,newVal);
            }
        });

        binding.minute.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.e("TAG", "onValueChange: binding.hour "+newVal );
                selectedTime.set(Calendar.MINUTE,newVal);
            }
        });

        binding.second.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.e("TAG", "onValueChange: binding.hour "+newVal );
                selectedTime.set(Calendar.SECOND,newVal);
            }
        });
        binding.amPm.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.e("TAG", "onValueChange: binding.hour "+newVal );
                if(newVal==0)
                    selectedTime.set(Calendar.AM_PM,Calendar.AM);
                else
                    selectedTime.set(Calendar.HOUR_OF_DAY,Calendar.PM);
            }
        });



    }
    public Calendar getSelectedTime(){
        Log.e("TAG", "getSelectedTime: "+selectedTime.getTime().toString() );
        return selectedTime;
    }

}
