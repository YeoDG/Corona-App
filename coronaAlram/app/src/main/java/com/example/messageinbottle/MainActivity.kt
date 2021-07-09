package com.example.messageinbottle


import android.content.*
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.messageinbottle.base.BaseActivity
import com.example.messageinbottle.base.DatePickerFragment
import com.example.messageinbottle.databinding.ActivityMainBinding
import com.example.messageinbottle.datamodel.*
import com.example.messageinbottle.gps.GpsBackgroundService
import com.google.android.gms.maps.model.LatLng
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*


class MainActivity() : BaseActivity<ActivityMainBinding>(R.layout.activity_main) , SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        var gpsDTO: GpsDTO = GpsDTO()
        var userDTO:UserDTO = UserDTO()
        var firebaseDTO:FirebaseDTO = FirebaseDTO()
        var coronaDTO:CoronaDTO = CoronaDTO()
        var googleMapDTO:GoogleMapDTO = GoogleMapDTO()
        var checkLife:Boolean = false
        var checkDead:Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mainactivity = this;

        checkLife = true
        checkDead = false

        coronaDTO.coronaMutableList.observe(this, coronaDTO.dataObserver)
        coronaDTO.setInit(this);

        Dexter.withActivity(this).withPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) // 위치
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) { // 권한 여부를 다 묻고 실행되는 메소드
                    println("퍼미션 전부 허용 완료")
                    bindService(Intent(this@MainActivity, GpsBackgroundService::class.java),gpsDTO.mServiceConnection, Context.BIND_AUTO_CREATE)
                    lifecycleScope.launchWhenStarted {
                        try {
                            firebaseDTO.getUser().collect {
                                Log.d("it ? : ", "" + it);
                                userDTO.userObservedList.postValue(it)
                                gpsDTO.mService!!.requestLocationUpdate()
                            }
                        }finally {
                            println("코루틴 종료")
                            checkDead = true
                            cancel();
                        }
                    }
                }
                override fun onPermissionRationaleShouldBeShown(list: List<PermissionRequest>, permissionToken: PermissionToken) {
                    // 이전 권한 여부를 거부한 권한이 있으면 실행되는 메소드
                    // 파라미터로 전달된 list : 거부한 권한 이름이 저장되어 있습니다.
                }
            }).check()



        supportFragmentManager.beginTransaction().replace(R.id.mainFragment, UserFragment()).commit()

    }

    //카테고리 이동 함수
    fun changeFragment(view : View,value : Int){
        when(value){
            1 -> supportFragmentManager.beginTransaction().replace(R.id.mainFragment, UserFragment()).commit()
            2 -> {
                showMap(DateFormat.format(Date(System.currentTimeMillis())))
            }
            3 -> openNaver()
        }
    }

    fun openNaver(){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://nid.naver.com/login/privacyQR?term=on"))
        startActivity(intent)
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onListenLocation(event : GpsBackgroundService.SendLocationToActivity){
//        if(event != null){
//            var date : String = StringBuilder("" + event.getLoction().latitude).append("/").append(event.getLoction().longitude).toString()
//            Toast.makeText(gpsDTO.mService,date, Toast.LENGTH_SHORT).show()
//        }
    }

    fun showMap(Date: String,location : LatLng? = null){
        googleMapDTO.setDate(Date,location)
        supportFragmentManager.beginTransaction().replace(R.id.mainFragment, CoronaFragment()).commit()
    }

    override fun onStart() {
        super.onStart()
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
        EventBus.getDefault().register(this)
    }


    override fun onStop() {
        if(gpsDTO.mBound){
            unbindService(gpsDTO.mServiceConnection)
            gpsDTO.mBound = false
        }
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
        EventBus.getDefault().unregister(this)
        super.onStop()
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {}


    fun showDatePicker(view: View?) {
        val newFragment: DialogFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
    }

}


var DangerCount: MutableLiveData<Int> = MutableLiveData()

val DateFormat = SimpleDateFormat("MM.dd kk:mm")
var NonHour = SimpleDateFormat("MM.dd")



