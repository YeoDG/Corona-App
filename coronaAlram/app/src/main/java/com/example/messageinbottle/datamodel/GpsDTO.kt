package com.example.messageinbottle.datamodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.example.messageinbottle.gps.GpsBackgroundService

class GpsDTO {
    var mService: GpsBackgroundService? = null

    var mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            var binder: GpsBackgroundService.LocalBinder =
                service as GpsBackgroundService.LocalBinder
            mService = binder.getservice()
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService = null
            mBound = false
        }

    }

    var mBound: Boolean = false
}