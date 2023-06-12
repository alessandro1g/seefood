package com.example.seefood.ui.main.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.seefood.utils.Food

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<MutableList<Food>>().apply {
        value = mutableListOf()
    }
    val text: LiveData<MutableList<Food>> = _text


    fun sync(fd:ArrayList<Food>){

        _text.value!!.addAll(fd)
        Log.d("live data", "foodLst not null: ${_text.value}")

    }

    fun delete(delPos: Int){
        if (_text.value!!.size >=1){
            _text.value?.removeAt(delPos)
        }else{
            _text.value = mutableListOf()
        }


    }


    fun click(newItem: Food){
        //this will need to change to be access firebase
        _text.value!!.add(newItem)
    }
}