package com.example.seefood.ui.main.dashboard

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.seefood.R
import com.example.seefood.utils.Food
import kotlin.reflect.KFunction2

class CustomAdapter(private val context: Context,
                    val posts: MutableList<Food>,
                    private val listener: OnItemClickListener,
                    val buzz: KFunction2<Int, String, Unit>,
) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        lateinit var name: TextView
        lateinit var mMen: ImageView


        fun bind(s: String) {
            itemView.findViewById<TextView>(R.id.itemName).text = s

        }
        val tv: TextView
        init {
            tv = itemView.findViewById(R.id.itemName)
            /*
            mMen = view.findViewById(R.id.mMenus)
            mMen.setOnClickListener{
                popupMenus(it)
            }*/

            itemView.setOnClickListener(this)
        }

        /*private fun popupMenus(v:View) {
            val position = posts[adapterPosition]



            val popupMenus = PopupMenu(context,v)
            popupMenus.inflate(R.menu.show_menu)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.delete -> {
                        AlertDialog.Builder(context)
                            .setTitle("Enter Food Name")
                            .setPositiveButton("Yes"){
                                    dialog,_->


                                buzz(adapterPosition,posts[adapterPosition].name)
                                notifyDataSetChanged()
                                Toast.makeText(context,"Deleted this Information",Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No"){
                                    dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                        true
                    }
                    else -> true
                }
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)

        }*/

        // this is the onclick listener that will tell the app what to do when clicked
        // we will need to extract info through firebase either through an id or a name

        // this will forward the click to the screen that will hold the data which will be the
        // dashboard fragment

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position].name)
    }

    override fun getItemCount()= posts.size


    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }


}