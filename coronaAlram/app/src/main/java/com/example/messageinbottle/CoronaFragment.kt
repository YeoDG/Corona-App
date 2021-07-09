package com.example.messageinbottle

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import com.example.messageinbottle.base.BaseFragment
import com.example.messageinbottle.databinding.FragmentCoronaBinding
import com.example.messageinbottle.datamodel.GoogleMapDTO
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class CoronaFragment : BaseFragment<FragmentCoronaBinding>(R.layout.fragment_corona),OnMapReadyCallback {
    var mgoogleMap : GoogleMap?= null
    var mapData : GoogleMapDTO = MainActivity.googleMapDTO

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.coronafragment = this

        binding.map.onCreate(savedInstanceState)
        binding.map.getMapAsync(this);

        binding.dateShow.setText(NonHour.format(mapData.selectedDate))

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        mgoogleMap = googleMap


        //지도 위치 이동
        googleMap?.setOnMapLoadedCallback(OnMapLoadedCallback {
            val Pocheon = mapData.selectedLacation // 마커 추가
            mgoogleMap?.moveCamera(CameraUpdateFactory.newLatLng(Pocheon))
            mgoogleMap?.animateCamera(CameraUpdateFactory.zoomTo(mapData.expand))
        })

        //마커 추가
        //alpha : 마커의 투명도
        //anchor : 마커의 위치
        //title : 마커를 눌렀을 때 나타나는 제목
        //Snippet : 마커의 제목 아래 부가 텍스트
        //icon : 기본 마커 대신 사용할 이미지를 설정

        //유저 마커 추가
        mapData.userLocation.forEach(){
            val location = LatLng(it.locationX!!,it.locationY!!)
            val markerOptions = MarkerOptions()
            markerOptions.position(location).apply {
                title(it.adress)
                snippet("활동 기록 : " + it.arrivalDate + " ~ " + it.leaveData)
                if(it.danger.get() == false){
                    icon( BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(resources.getDrawable(R.drawable.usersaveicon).toBitmap(),90,150,false)))
                }else{
                    icon( BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(resources.getDrawable(R.drawable.userdangericon).toBitmap(),90,150,false)))
                }
            }
            mgoogleMap!!.addMarker(markerOptions)
        }

        //유저 마커 추가
        mapData.coronaLocation.forEach(){
            val location = LatLng(it.locationX!!,it.locationY!!)
            val markerOptions = MarkerOptions()
            markerOptions.position(location).apply {
                title(it.adress)
                snippet("활동 기록 : " + it.arrivalDate + " ~ " + it.leaveData)
                icon( BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(resources.getDrawable(R.drawable.coronaicon).toBitmap(),165,165,false)))

            }
            mgoogleMap!!.addMarker(markerOptions)
        }
    }

    override fun onResume() {
        binding.map.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        binding.map.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        binding.map.onLowMemory()
        super.onLowMemory()
    }
}
