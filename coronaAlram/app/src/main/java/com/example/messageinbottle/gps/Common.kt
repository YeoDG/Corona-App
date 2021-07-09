package com.example.messageinbottle.gps

import android.content.Context
import android.location.Location
import android.preference.PreferenceManager
import com.example.messageinbottle.DateFormat
import com.example.messageinbottle.datamodel.GpsModel
import com.example.messageinbottle.datamodel.fbGpsModel
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Common{

    fun getLocationText(mLocation: Location) : String{
        if(mLocation == null){
            return  "nuKnown Location"
        }else{

            mLocation.latitude

            var latitude :Double =Math.floor(mLocation.latitude*10000)/10000.0
            var longitude :Double =Math.floor(mLocation.longitude*10000)/10000.0


            var date : String = StringBuilder("" + latitude)
                .append("/")
                .append(longitude)
                .toString()
            return date
        }
    }


    fun getLocationTitle(myBackgroundService: GpsBackgroundService,model: fbGpsModel?) : CharSequence{
        val now: Long = System.currentTimeMillis()
        val mDate = Date(now)
        val Time: String =DateFormat.format(mDate)

        var date: String = StringBuilder("기록 :")
            .append(model!!.arrivalDate!!)
            .append(" ~ ")
            .append(Time)
            .toString()

        return date
    }

    fun parsingTime(date : String) : Long{

        var LongTime = DateFormat.parse(date).time

        return LongTime;
    }

    fun setRequestionLoctionUpdate(context: Context, flags: Boolean){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_REQUSETING_LOCATION_UPDATE,flags).apply()
    }

    fun requestionLoctionUpdate(context: Context) : Boolean{
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_REQUSETING_LOCATION_UPDATE,false)
    }
}