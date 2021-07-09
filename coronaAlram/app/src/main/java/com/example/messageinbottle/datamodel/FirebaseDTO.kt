package com.example.messageinbottle.datamodel

import android.util.Log
import com.example.messageinbottle.MainActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow



class FirebaseDTO {

    //파베 저장소
    var firestore = FirebaseFirestore.getInstance()
    var uid : String ="dongguk"

    //모든 게시글 다 가져오기
    fun getUser() = callbackFlow<ArrayList<GpsModel>> {

        val databaseReference = firestore.collection("user").document("dongguk")

        val eventListener1 = databaseReference.get().addOnCompleteListener {
            println("파베 db 진입 시도")
            if(it.isSuccessful){
                if(it.result != null) {
                    if (it.result.data == null) {
                        println("유저정보 없읍")
                        databaseReference.set(uid).addOnSuccessListener {
                            val eventListener2 = databaseReference.collection("Location")
                                .addSnapshotListener { value, error ->
                                    var gpsList: ArrayList<GpsModel> = ArrayList()
                                    value!!.documents.forEach {
                                        gpsList.add(it.toObject(GpsModel::class.java)!!)
                                    }
                                    println("makeUser : 성공")
                                    if(MainActivity.checkLife == true) {

                                        this@callbackFlow.sendBlocking(gpsList)
                                    }
                                }
                        }
                    } else {
                        println("유저정보 있음")
                        val eventListener2 = databaseReference.collection("Location")
                            .addSnapshotListener { value, error ->
                                var gpsList: ArrayList<GpsModel> = ArrayList()
                                value!!.documents.forEach {
                                    gpsList.add(it.toObject(GpsModel::class.java)!!)
                                }
                                println("기존 데이터 가져오기 : " + MainActivity.checkLife)
                                if(MainActivity.checkLife == true) {
                                    this@callbackFlow.sendBlocking(gpsList)
                                }
                            }
                    }

                }
            }
        }.addOnFailureListener {
            Log.d("getuser : " , "실패")
        }

        awaitClose()

    }





}