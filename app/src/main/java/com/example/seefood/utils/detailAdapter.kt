package com.example.seefood.utils

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.seefood.R

class detailAdapter(private val nutitem: MutableList<MutableList<String>>):
    RecyclerView.Adapter<detailAdapter.MyViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.detail_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = nutitem[position]
        //idx 0 is name
        //idx 1 is value
        holder.nutName.text = currentItem[0]
        holder.nutVal.text = currentItem[1]
        val caloriesRecommded = 2000
        val totalFatRecommended= 78
        val cholesterolRecomended = 300
        val sodiumRecomended = 2300
        val carbsRecomended = 275
        val dfibersRecomended = 28
        val proteinRecomende = 50
        val addSugarsRec = 50

        when(currentItem[0]){
            "Calories" -> holder.pg.max = caloriesRecommded
            "Total Fat" -> holder.pg.max = totalFatRecommended
            "Cholesterol" -> holder.pg.max = cholesterolRecomended
            "Sodium"->holder.pg.max = sodiumRecomended
            "Total Carbohydrates"->holder.pg.max = carbsRecomended
            "Dietary Fibers"->holder.pg.max = dfibersRecomended
            "Protein"->holder.pg.max = proteinRecomende
            "TotalSugar"->holder.pg.max = addSugarsRec
            else ->{
                holder.pg.max = 100000
            }
        }
        holder.pg.progress = currentItem[1].toFloat().toInt()


    }

    override fun getItemCount(): Int {
        Log.d("sizing", "New foodLst: ${nutitem.size}")
        return nutitem.size
    }
    class MyViewHolder(itemview: View):RecyclerView.ViewHolder(itemview){




        val nutName : TextView = itemView.findViewById(R.id.NutritionName)
        val nutVal : TextView = itemView.findViewById(R.id.nutritionvalue)
        val pg : ProgressBar = itemView.findViewById(R.id.progressBar)



    }

}