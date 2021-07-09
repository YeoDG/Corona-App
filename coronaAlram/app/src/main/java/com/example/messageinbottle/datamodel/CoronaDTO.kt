package com.example.messageinbottle.datamodel

import android.app.Activity
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.messageinbottle.DateFormat
import com.example.messageinbottle.base.UtilClass
import org.jetbrains.anko.doAsync
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class CoronaDTO {

    var coronaMutableList : MutableLiveData<ArrayList<GpsModel>> =MutableLiveData<ArrayList<GpsModel>>()
    var coronaLocationList : ArrayList<GpsModel> = ArrayList<GpsModel>()

    val dataObserver: Observer<ArrayList<GpsModel>> =
        Observer { livedata ->  coronaLocationList = livedata
            println("코로나 확진자 데이터 획득 완료")
            UtilClass().checkDanger()
        }

    fun setInit(activity : Activity){
        var geocoder : Geocoder  = Geocoder(activity)

        doAsync {
            println("코로나 확진자 확인")

            //대구시 코로나 확진자 url
            var url: String = "http://covid19.daegu.go.kr/00937400.html"
            var doc = Jsoup.connect(url).get()
            val tbody = doc.select("tbody")[1]
            val rows = tbody.select("tr")
            var start : String? = null
            var end : String? = null

            var list : ArrayList<GpsModel> = ArrayList<GpsModel>()

            for (i in 1..rows.size-1){

                var gpsModel: GpsModel = GpsModel();
                gpsModel.adress = rows[i].select("td")[3].toString().split("<p>")[1].split("<br>")[0].split("</p>")[0]

                //경도위도
                var loaction = rows[i].select("td")[4].toString().split("<p>")[1].split("<br>")[0].split("</p>")[0]
                gpsModel.locationX = geocoder.getFromLocationName(loaction, 1)?.get(0)?.latitude!!
                gpsModel.locationY = geocoder.getFromLocationName(loaction, 1)?.get(0)?.longitude!!


                var time = rows[i].select("td")[5].toString().split("<p>")[1].split("<br>")[0].split("</p>")[0].replace("&nbsp;", "").replace(" ", "")
                    .replace(".(","(").replace("(월)","").replace("(화)","").replace("(수)","").replace("(목)","").replace("(금)","").replace("(토)","").replace("(일)","")
                    .split(":")[0].split(",")[0]

                start = time.replaceAfter("~", "").replace("~", "")
                end = time.replaceBefore("~", "").replace("~", "")

                start = start.replace("월중",".1").replace("이후","") + " 00:00"
                if(end.contains("이후")){
                    end = end.split(".")[0]+".31"
                }
                end = end.replace("월중",".31") + " 24:00"


                if(end == ""){
                    end = start
                }

                gpsModel.arrivalDate = DateFormat.format(DateFormat.parse(start))
                gpsModel.leaveData = DateFormat.format(DateFormat.parse(end))

                list.add(gpsModel)
                println("확진자 정보 획득 : " + gpsModel.adress)
            }

            coronaMutableList.postValue(list)
        }
    }

}