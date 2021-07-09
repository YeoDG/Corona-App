package com.example.messageinbottle.gps

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.messageinbottle.DateFormat
import com.example.messageinbottle.MainActivity
import com.example.messageinbottle.MainActivity.Companion.checkDead
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import org.greenrobot.eventbus.EventBus
import java.lang.Exception
import com.example.messageinbottle.R
import com.example.messageinbottle.datamodel.GpsModel
import com.example.messageinbottle.datamodel.fbGpsModel
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.spec.GCMParameterSpec
import kotlin.collections.ArrayList


public const val CHANNEL_ID = "my_channel"
public const val EXTRA_STARTED_FROM_NOTYFICATION= "com.example.gpstest3" + ".started_from_notification"
public const val UPDATE_INTEVAL_IN_MUL : Long = 10000
public const val FASTEST_UPDATE_INTEVAL_IN_MUL : Long = UPDATE_INTEVAL_IN_MUL/2
public const val NOTI_ID = 1223
public const val KEY_REQUSETING_LOCATION_UPDATE : String = "LocationUpdateEnable"


class GpsBackgroundService : Service() {

    var localbinder : LocalBinder = LocalBinder()

    var mChangeingConfiguration = false

    lateinit var mNotificationManager : NotificationManager

    lateinit var locationRequest : LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    lateinit var mServiceHandler: Handler
    lateinit var mLocation : Location

    var firestore = FirebaseFirestore.getInstance()

    var CheckGps : fbGpsModel = fbGpsModel();

    var distance : Double = 0.0

    var geocoder : Geocoder? = null;

    fun uploadlocation(location  : fbGpsModel) {
        if(checkDead==true) {
            MainActivity.checkLife = false
            firestore.collection("user").document("dongguk").collection("Location").document()
                .set(location)
            println("데이터 전송 완료")
        }
    }

    override fun onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                onNewLocation(result.lastLocation)
            }
        }

        createLocationRequset()
        getLastLocation()

        geocoder = Geocoder(baseContext)

        var handlerThread : HandlerThread = HandlerThread("EDMTDev")
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)
        mNotificationManager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var mChannel : NotificationChannel = NotificationChannel(CHANNEL_ID,getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW)
            mNotificationManager.createNotificationChannel(mChannel)

        }

    }



    fun getLastLocation(){
        try {
            var lastLocation : Task<Location> =  fusedLocationProviderClient.lastLocation

            var listener : OnCompleteListener<Location> = object : OnCompleteListener<Location> {
                override fun onComplete(task: Task<Location>) {
                    if(task.isSuccessful() && task.getResult() !=null){
                        mLocation = task.getResult();
                    }else{
                        Log.e("EDMT_DEV","FAIL TO GET LOCATION")
                    }
                }
            }

            lastLocation.addOnCompleteListener( listener)
        }catch (error : Exception){
            Log.e("EDMT_DEV","LOST LOCATION PERMISSION " + error)
        }

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var startedFromNotification :Boolean = intent?.getBooleanExtra(EXTRA_STARTED_FROM_NOTYFICATION,false) ?: false

        if(startedFromNotification){
            removeLocaionUpdate()
            stopSelf()
        }

        return Service.START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mChangeingConfiguration = true
    }

    fun  removeLocaionUpdate(){
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            Common().setRequestionLoctionUpdate(this,false)
            stopSelf()
        }catch (erroe : Exception){
            Common().setRequestionLoctionUpdate(this,false)
            Log.e("EDMT","LOST LOCTION PERMISSION.COULD NOT REMOVE UPDATE")
        }
    }

    fun createLocationRequset() {
        locationRequest = LocationRequest()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTEVAL_IN_MUL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTEVAL_IN_MUL);
    }

    fun onNewLocation(lastlocation: Location) {
        mLocation = lastlocation
        EventBus.getDefault().postSticky(SendLocationToActivity(mLocation))
        if(serviceIsRunninbgInForeeGround(this)){
            mNotificationManager.notify(NOTI_ID,getNotification())
        }
    }

    fun getNotification(): Notification {
        var intent : Intent = Intent(this,GpsBackgroundService::class.java)
        var context : String = Common().getLocationText(mLocation).toString() + " 거리 : " + distance

        intent.putExtra(EXTRA_STARTED_FROM_NOTYFICATION,context)

        var servicePendingIntent : PendingIntent = PendingIntent.getService(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT)

        var activityPendingIntent : PendingIntent = PendingIntent.getActivity(this,0,
            Intent(this, MainActivity::class.java), 0)

        var builder : NotificationCompat.Builder = NotificationCompat.Builder(this)
            .addAction(R.drawable.launch_ic,"Launch",activityPendingIntent )
            .addAction(R.drawable.cancle_ic,"Cancle",activityPendingIntent )
            .setContentText(context)
            .setContentTitle( Common().getLocationTitle(this,CheckGps))
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker(context)
            .setWhen(System.currentTimeMillis())

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            builder.setChannelId(CHANNEL_ID)
        }

        //업로드 할지 안할지 체크
        checkUpload(mLocation)

        return builder.build()
    }



    fun serviceIsRunninbgInForeeGround(context: Context) : Boolean{
        var manager : ActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if(service.foreground){
                return true
            }
        }
        return false
    }

    override fun onBind(intent: Intent): IBinder {
        stopForeground(true)
        mChangeingConfiguration = false
        return localbinder
    }

    override fun onRebind(intent: Intent?) {
        stopForeground(true)
        mChangeingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if(!mChangeingConfiguration && Common().requestionLoctionUpdate(this)){
            startForeground(NOTI_ID,getNotification())
        }
        return super.onUnbind(intent)
    }


    override fun onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    fun requestLocationUpdate() {
        Common().setRequestionLoctionUpdate(this,true)
        startService(Intent(applicationContext,GpsBackgroundService::class.java))

        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper())
        }catch (error : SecurityException){
            Log.e("EDMT","LOSTT LOCATION PERMISSION : " + error)
        }

    }

    fun checkUpload(location: Location){

        val now: Long = System.currentTimeMillis()
        val mDate = Date(now)
        val simpleDate = SimpleDateFormat("MM.dd kk:mm")
        val Time: String =simpleDate.format(mDate)


        var latitude :Double = location.latitude
        var longitude :Double = location.longitude

        if(CheckGps.arrivalDate != "X"){

            //위치이동이 감지되고 시간이 15분 이상 지낫다면 업로드
            //위치이동만 감지되면 checkGps 변경
            //거리가 0.0003이상 차이나는지 확인 +-0.0003

            var width : Double = (Math.abs(CheckGps.locationX!!-latitude)*10000)
            var height : Double = (Math.abs(CheckGps.locationY!!-longitude)*10000)
            var distance = Math.sqrt(width*width +height*height)
            Log.d("측정","latitude  : " + latitude + " CheckGps.locationX : " + CheckGps.locationX + " CheckGps.locationY : " + CheckGps.locationY + " longitude : " + longitude )

            Log.d("측정","width  : " + width + " width*width : " + width*width + " height : " + height + " height*height : " + height*height + " distance : " + distance )

            this.distance = distance
            if(  distance > 3.5 ){
                Log.d("범위 내 " ,"" + distance + " : " + this.distance + ": " +(distance > 3.5))
                if((Common().parsingTime(CheckGps!!.arrivalDate!!)+900000)  <=  Common().parsingTime(Time)) {
                    //종료
                    CheckGps.leaveData = Time
                    CheckGps.locationX = latitude
                    CheckGps.locationY = longitude

                    CheckGps.adress  = geocoder?.getFromLocation(latitude, longitude,1)?.get(0)?.getAddressLine(0).toString()

                    uploadlocation(CheckGps!!)
                    CheckGps.arrivalDate = Time
                    CheckGps.leaveData = Time
                }else{
                    //초기화
                    CheckGps.arrivalDate = Time
                    CheckGps.leaveData = "X"
                    CheckGps.locationX = latitude
                    CheckGps.locationY = longitude
                }
            }else{
                //갱신
                CheckGps.leaveData = Time
            }

        }else{
            //데이터가 없으면 시작 데이터로 제작
            CheckGps = fbGpsModel(Time,"X",latitude,longitude);
        }




    }

    inner class LocalBinder : Binder(){
        fun getservice() : GpsBackgroundService{
            return this@GpsBackgroundService
        }
    }

    inner class SendLocationToActivity(var location: Location){
        fun getLoction(): Location {
            return  location
        }
        fun setLoction(location: Location){
            this.location = location
        }
    }




}