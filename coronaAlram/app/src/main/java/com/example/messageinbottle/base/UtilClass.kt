package com.example.messageinbottle.base

import com.example.messageinbottle.DangerCount
import com.example.messageinbottle.DateFormat
import com.example.messageinbottle.MainActivity

class UtilClass() {

    //위험군 확인
    public fun checkDanger() {
        println(" 위험 검사 ")

        var dangerCount = 0

        for (i in MainActivity.userDTO.userLoactoinList) {
            for (j in MainActivity.coronaDTO.coronaLocationList) {
                var width: Double = (Math.abs(i.locationX!! - j.locationX!!) * 10000)
                var height: Double = (Math.abs(i.locationY!! - j.locationY!!) * 10000)
                var distance = Math.sqrt(width * width + height * height)

                var userArrive = DateFormat.parse(i.arrivalDate)
                var userleave = DateFormat.parse(i.leaveData)
                var coronaArrive = DateFormat.parse(j.arrivalDate)
                var coronaleave = DateFormat.parse(j.leaveData)

                if (!(userArrive > coronaleave) || (userleave < coronaArrive)) {
                    if (distance < 3.5) {
                        i.danger.set(true)
                        dangerCount++
                    }
                }
            }
        }
        DangerCount.value = dangerCount
        println(" 값 : " + DangerCount.value)
    }
}