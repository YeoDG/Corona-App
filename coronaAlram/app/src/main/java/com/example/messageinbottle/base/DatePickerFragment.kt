package com.example.messageinbottle.base

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.messageinbottle.MainActivity
import com.example.messageinbottle.R
import java.util.*


class DatePickerFragment() : DialogFragment(), DatePickerDialog.OnDateSetListener  {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_date_picker, container, false)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        var activity: MainActivity? = activity as MainActivity?
        var date = (month+1).toString() + "." + dayOfMonth.toString() + " " + "00:00"
        activity!!.showMap(date);
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        println("onCreateDialog")
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(activity!!, this, year, month, day)
    }
}