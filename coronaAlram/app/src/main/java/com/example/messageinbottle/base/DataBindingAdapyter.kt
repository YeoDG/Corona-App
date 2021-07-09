package com.example.messageinbottle.base

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.messageinbottle.R

object DataBindingAdapyter {
    //뷰보이기 안보이기
    @JvmStatic
    @BindingAdapter("dangercheck")
    fun setDanger(view: View, danger: Boolean) {
        if(danger == true){
            view.setBackgroundColor(Color.parseColor("#f2c3c6"))
        }else{
            view.setBackgroundColor(Color.WHITE)
        }
    }

    @JvmStatic
    @BindingAdapter("dangerIconcheck")
    fun setDangerIcon(view: ImageView, danger: Boolean) {
        if(danger == true){
            view.scaleType = ImageView.ScaleType.FIT_XY
            view.setImageResource(R.drawable.userdangericon)
        }else{
            view.scaleType = ImageView.ScaleType.FIT_XY
            view.setImageResource(R.drawable.usersaveicon)
        }
    }

    @JvmStatic
    @BindingAdapter("dangerVisiblecheck")
    fun setDangerVisible(view: View, danger: Boolean) {
        if(danger == true){
            view.visibility = View.VISIBLE
        }else{
            view.visibility = View.GONE

        }
    }



    @JvmStatic
    @BindingAdapter("removeTime")
    fun setRemoveTime(view: TextView, time: String) {
        view.setText(time.split(" ")[0])
    }

    @JvmStatic
    @BindingAdapter("removeDAY")
    fun setRemoveDAY(view: TextView, time: String) {
        view.setText(time.split(" ")[1])
    }



    @JvmStatic
    @BindingAdapter("visiable","danger")
    fun setvisiable(view: View, visiable: Boolean,danger: Boolean) {
        if(visiable==true){
            view.visibility = View.VISIBLE
        }else{
            if(danger == true){
                view.visibility = View.VISIBLE
            }else{
                view.visibility = View.GONE
            }
        }
    }

}