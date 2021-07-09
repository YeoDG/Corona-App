package com.example.messageinbottle.datamodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class UserDTO {

    //관측 체크용 리스트
    var userObservedList  : MutableLiveData<ArrayList<GpsModel>> = MutableLiveData<ArrayList<GpsModel>>()
//    //어댑터에 들어갈 리스트
    var userLoactoinList : ArrayList<GpsModel> = ArrayList<GpsModel>()

}