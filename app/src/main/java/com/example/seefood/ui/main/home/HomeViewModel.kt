package com.example.seefood.ui.main.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.seefood.utils.Food

class HomeViewModel : ViewModel() {

    private final var TAG = "HOMEVIEWMODEL"
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
    fun getSum(foodLst:ArrayList<Food>): HashMap<String,Float>{

        val sumNutrients = HashMap<String,Float>()
        for (food in foodLst){
            val nutrients = food.nutrients
            for (k in nutrients.keys){
                if(sumNutrients[k] != null){
                    // Log.i(TAG, "prev total = $" )
                    sumNutrients[k] = sumNutrients[k]!!.plus(nutrients[k]!!)

                }
                else{

                    sumNutrients[k] = nutrients[k]!!
                }
            }
        }

        Log.i(TAG, "sumNutrients --> $sumNutrients")
        return sumNutrients
    }


    fun getAverages(sumNutrients:HashMap<String,Float>, foodsLen:Int):HashMap<String,Float>{
        val avgNutrients = HashMap<String,Float>()
        for (k in sumNutrients.keys){
            avgNutrients[k] = sumNutrients[k]!!/foodsLen
        }
        Log.d("HF", "avgNutrients: $avgNutrients")
        return avgNutrients
    }
}