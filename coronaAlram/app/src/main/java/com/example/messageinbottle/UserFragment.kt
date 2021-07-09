package com.example.messageinbottle

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messageinbottle.base.BaseFragment
import com.example.messageinbottle.base.BaseRecyclerAdapter
import com.example.messageinbottle.base.UtilClass
import com.example.messageinbottle.databinding.FragmentUserBinding
import com.example.messageinbottle.databinding.ItemCoronalistBinding
import com.example.messageinbottle.datamodel.GpsDTO
import com.example.messageinbottle.datamodel.GpsModel
import com.example.messageinbottle.datamodel.UserDTO
import com.example.messageinbottle.generated.callback.OnClickListener
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class UserFragment() : BaseFragment<FragmentUserBinding>(R.layout.fragment_user) {

    var recyclerViewType : ObservableField<Boolean> = ObservableField<Boolean>(true)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.userFragment = this

        MainActivity.userDTO.userObservedList.observe(this, dataObserver)
        DangerCount.observe(this, dangerObserver)

        binding.userMoveRoot.adapter = UserListAdapter(R.layout.item_coronalist,MainActivity.userDTO.userLoactoinList)
        binding.userMoveRoot.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)

        return binding.root
    }

    val dataObserver: Observer<ArrayList<GpsModel>> =
        Observer { livedata ->
            MainActivity.userDTO.userLoactoinList = livedata

            println("바뀐 데이터 : " + livedata)

            Collections.sort(MainActivity.userDTO.userLoactoinList, object : Comparator<GpsModel>{
                override fun compare(o1: GpsModel, o2: GpsModel): Int {
                    return DateFormat.parse(o2.leaveData).compareTo(DateFormat.parse(o1.leaveData))
                }
            })

            UtilClass().checkDanger()

            binding.userMoveRoot.adapter = UserListAdapter(R.layout.item_coronalist,MainActivity.userDTO.userLoactoinList)
        }

    val dangerObserver: Observer<Int> =
        Observer { livedata ->  binding.dangerCount.setText(livedata.toString()) }


    inner class UserListAdapter(layoutId: Int,var itemlist : ArrayList<GpsModel>) : BaseRecyclerAdapter<GpsModel,ItemCoronalistBinding>(layoutId,itemlist){

        init {
            if(recyclerViewType.get() == true){
                this.itemList = itemlist
            }else{
                this.itemList = fillterDanger();
            }
        }

        fun ChangeItemList(){
            if(recyclerViewType.get() == true){
                this.itemList = itemlist
            }else{
                this.itemList = fillterDanger();
            }
            println("아이템 리스트 : "+this.itemList);
            notifyDataSetChanged();
        }

        fun fillterDanger() : ArrayList<GpsModel>{
            var list = ArrayList<GpsModel>()
            itemlist.forEach {
                if(it.danger.get() == true){
                    list.add(it);
                }
            }
            return list
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            holder.binding.listItem.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    var activity : MainActivity = getActivity() as MainActivity
                    var date = itemList[position].leaveData
                    var location : LatLng? = LatLng(itemList[position].locationX!!,itemList[position].locationY!!)
                    activity.showMap(date,location);
                }
            })
        }
    }

    fun changeRecyclerType(view: View){
        if(recyclerViewType.get() == true){
            recyclerViewType.set(false)
        }else{
            recyclerViewType.set(true)
        }
        var adapter = binding.userMoveRoot.adapter as UserListAdapter
        adapter.ChangeItemList()
    }


}
