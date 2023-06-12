package com.example.seefood.ui.main.dashboard

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seefood.DetailActivity
import com.example.seefood.R
import com.example.seefood.databinding.FragmentDashboardBinding
import com.example.seefood.utils.Food
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


private  const val TAG = "dashAct"
class DashboardFragment : Fragment(),CustomAdapter.OnItemClickListener {

    private  var _binding: FragmentDashboardBinding? = null
    private lateinit var adapter: CustomAdapter
    var counter = 0
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dashboardViewModel: DashboardViewModel
    var item: MutableList<Food> = mutableListOf()
    private lateinit var auth: FirebaseAuth
    private lateinit var foodRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val foodLst = ArrayList<Food>()
        auth = Firebase.auth
        //Reference to a list of Food data objects
        foodRef = FirebaseDatabase.getInstance().reference.child("Users").child(auth.uid!!).child("foods")

        dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        Log.d("Food Nut", "foodLst not null: $foodLst")
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            item.addAll(it)
        }
        Log.i(TAG, item.toString())


        val recyclerview: RecyclerView = binding.recyclerview
        adapter = CustomAdapter(requireContext(),item,this,::fdelete)
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        Log.d("in Event listener", "food list: $foodLst")
        foodRef.addValueEventListener(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    item.clear()
                    for (userSnapshot in snapshot.children){
                        //userSnapshot.getRef().removeValue();

                        if (userSnapshot.child("nutrients").value!= null){
                            val foodhm = userSnapshot.child("nutrients").value as HashMap<String, Float>
                            val foodnm = userSnapshot.child("name").value as String

                            Log.i("retrieve data", foodnm)
                            Log.i("retrieve data", foodhm.toString())

                            val hm = HashMap<String, Float>()
                            for ((key,value)in foodhm){
                                hm[key] = value.toString().toFloat()
                            }
                            val temp = Food(foodnm,hm)
                            var flag = true
                            for (valu in item){
                                if (valu.nutrients == hm && valu.name== foodnm){
                                    flag = false
                                }
                            }
                            Log.i("retrieve item", item.toString())
                            if (flag) {
                                item.add(temp);
                            }
                        }

                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })




        binding.fab.setOnClickListener{
            addItemtoRec()
        }

        //fdelete(0,"Food")
        return root
    }






    private fun addItem(itm: Food) {
        //dashboardViewModel.click(itm)
        item.add(itm)

        adapter.notifyDataSetChanged()
        foodRef.get()

    }

    fun fdelete(itm: Int,id:String){
        //dashboardViewModel.delete(itm)
        val dbr = FirebaseDatabase.getInstance().reference.child("Users").child(auth.uid!!).child("foods")
        dbr.orderByChild("name").equalTo(id).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children){
                    data.ref.removeValue()
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })



    }
    private fun saveToFB(newFood: Food){
        var foodLst = ArrayList<Food>()
        foodRef.get().addOnCompleteListener { it ->
            Log.d("Saving", "Data: ${it.result.value}")
            Log.d("Saving", "Children count: ${it.result.childrenCount}")
            if (it.result.value != null){
                foodLst = it.result.value as ArrayList<Food>
                Log.d("Saving", "foodLst not null: $foodLst")
            }
            foodLst.add(newFood)
            Log.d("Saving", "New foodLst: $foodLst")
            foodRef.setValue(foodLst)
        }
        Toast.makeText(
            requireContext(),
            "Adding User Information Success",
            Toast.LENGTH_SHORT
        ).show()
    }
    private fun addItemtoRec(){
        val inflter = LayoutInflater.from(requireContext())
        val v = inflter.inflate(R.layout.add_item,null)

        val addName = v.findViewById<EditText>(R.id.add_name)

        val addDialog = AlertDialog.Builder(requireContext())
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->

            if (addName.text.isEmpty()) {
                Toast.makeText(requireContext(), "please add string for name", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }

            val nutrients = getNutrients(v)

            //*
            val newFood = Food(
                addName.text.toString(),
                nutrients
            )
            Log.d("new Food", "$newFood")
            saveToFB(newFood)
            addItem(newFood)
            // add other things that will be saved
            adapter.notifyItemInserted(item.size - 1);

            dialog.dismiss()
            //*/
        }

        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(requireContext(),"Cancel",Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()
    }

    override fun onItemClick(position: Int) {
        val clickedItem = item[position]
        Toast.makeText(requireContext(), "item $clickedItem clicked", Toast.LENGTH_SHORT).show()
        //*
        val intent = Intent(activity, DetailActivity::class.java)
        val fd = clickedItem
        //val args =  Bundle();
        //args.putSerializable("NAMELIST", (Serializable)nameValue)
        intent.putExtra("namelst",fd);

        startActivity(intent)
        // this is the onclick listener for each card in the recycler view,
        // we will need to go to whatever activity or fragment will be used to display the data
        // with the firebase information

        //*/
    }


    private fun getNutrients(view:View):HashMap<String,Float> {
        val inflter = LayoutInflater.from(requireContext())
        val addName = view.findViewById<EditText>(R.id.add_name)
        val addCalories = view.findViewById<EditText>(R.id.add_calories)
        val addTotalFat = view.findViewById<EditText>(R.id.add_totFat)
        val addCholesterol = view.findViewById<EditText>(R.id.add_cholesterol)
        val addSodium = view.findViewById<EditText>(R.id.add_sodium)
        val addCarbs = view.findViewById<EditText>(R.id.add_carbs)
        val addDietaryFiber = view.findViewById<EditText>(R.id.add_dFiber)
        val addSugar = view.findViewById<EditText>(R.id.add_sugar)
        val addProtein = view.findViewById<EditText>(R.id.add_protein)
        val addServingSize = view.findViewById<EditText>(R.id.add_ServingSize)

        val nutrients: HashMap<String, Float> = HashMap<String, Float>();



        if (addServingSize.text.isNotEmpty() && addServingSize.text.isDigitsOnly()) {
            nutrients["Serving Size"] = addServingSize.text.toString().toFloat();
        }


        if (addCalories.text.isNotEmpty() && addCalories.text.isDigitsOnly()) {
            nutrients["Calories"] = addCalories.text.toString().toFloat();
        }

        if (addTotalFat.text.isNotEmpty() && (addTotalFat.text.isDigitsOnly())) {
            nutrients["Total Fat"] = addTotalFat.text.toString().toFloat();
        }

        if (addCholesterol.text.isNotEmpty() && (addCholesterol.text.isDigitsOnly())) {
            nutrients["Cholesterol"] = addCholesterol.text.toString().toFloat();
        }

        if (addSodium.text.isNotEmpty() && (addSodium.text.isDigitsOnly())) {
            nutrients["Sodium"] = addSodium.text.toString().toFloat();
        }

        if (addCarbs.text.isNotEmpty() && (addCarbs.text.isDigitsOnly())) {
            nutrients["Total Carbohydrates"] = addCarbs.text.toString().toFloat();
        }

        if (addDietaryFiber.text.isNotEmpty() && (addDietaryFiber.text.isDigitsOnly())) {
            nutrients["Dietary Fibers"] = addDietaryFiber.text.toString().toFloat();
        }

        if (addSugar.text.isNotEmpty() && (addSugar.text.isDigitsOnly())) {
            nutrients["Total Sugar"] = addSugar.text.toString().toFloat()
        }
        if (addProtein.text.isNotEmpty() && (addProtein.text.isDigitsOnly())) {
            nutrients["Protein"] = addProtein.text.toString().toFloat()
        }
        return nutrients
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}