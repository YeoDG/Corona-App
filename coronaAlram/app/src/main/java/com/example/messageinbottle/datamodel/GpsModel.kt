package com.example.messageinbottle.datamodel

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData

data class fbGpsModel (
    var arrivalDate : String="X",
    var leaveData : String="X",
    var locationX : Double?=null,
    var locationY: Double?=null,
    var adress : String? = null
)

data class GpsModel (
    var arrivalDate : String="X",
    var leaveData : String="X",
    var locationX : Double?=null,
    var locationY: Double?=null,
    var adress : String? = null,
    var danger: ObservableField<Boolean> = ObservableField<Boolean>(false)
)