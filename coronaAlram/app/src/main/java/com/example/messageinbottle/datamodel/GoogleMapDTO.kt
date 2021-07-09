package com.example.messageinbottle.datamodel

import com.example.messageinbottle.DateFormat
import com.example.messageinbottle.MainActivity
import com.example.messageinbottle.NonHour
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.collections.ArrayList

class GoogleMapDTO {

    // 선택된 위치 디폴트 반월당 역
    var selectedLacation : LatLng = LatLng(35.86550938543891, 128.59341985297792)

    // 선택된 일자
    var selectedDate : Date = Date()
    
    // 유저 이동경로
    var userLocation : ArrayList<GpsModel> = ArrayList()

    // 확진자들 위치
    var expand : Float = 15f


    // 확대 정도
    var coronaLocation : ArrayList<GpsModel> = ArrayList()

    fun setDate(date : String,location : LatLng? = null){

        //리스트 초기화
        userLocation.clear()
        coronaLocation.clear()

        //받아온 날자에서 시간을 제거
        var time : Date = NonHour.parse(NonHour.format(DateFormat.parse(date)))
        selectedDate = time


        println("비교 시간 : " + time)
        var cal = Calendar.getInstance()
        cal.setTime(time)
        cal.add(Calendar.DATE,+1)
        var lastTime : Date = cal.time

        //조건에 맞는 케이스만 리스트에 넣기
        MainActivity.userDTO.userLoactoinList.forEach(){
            if((DateFormat.parse(it.leaveData) > time) && (DateFormat.parse(it.arrivalDate) < lastTime)){
                userLocation.add(it)
            }
        }

        MainActivity.coronaDTO.coronaLocationList.forEach(){
            if( (DateFormat.parse(it.leaveData) > time) && (DateFormat.parse(it.arrivalDate) < lastTime)){
                coronaLocation.add(it)
            }
        }
        //정해진 장소가 없으면 반월당으로
        //정해진 장소가없고 유저기록이 0이아니라면 마지막 위치로 이동
        if (location == null) {
            if (userLocation.size != 0) {
                selectedLacation = LatLng(userLocation[userLocation.count()-1].locationX!!,userLocation[0].locationY!!)
                expand = 15f
            } else {
                selectedLacation = LatLng(35.86550938543891, 128.59341985297792)
                expand = 15f
            }
        }else{
            selectedLacation = location
            expand = 20f
        }

    }
}