package com.example.seefood.ui.main.home

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.seefood.databinding.FragmentHomeBinding
import com.example.seefood.utils.Food
import com.example.seefood.utils.GRAPH_GENERATOR
import com.github.mikephil.charting.charts.PieChart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private final val TAG = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var sumNutrients: HashMap<String,Float>
    private lateinit var avgNutrients: HashMap<String,Float>
    private lateinit var pieChart: PieChart
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        dbRef = FirebaseDatabase.getInstance().reference.child("Users").child(auth.uid!!).child("foods")
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        pieChart = binding.pieChart

        val textView: TextView = binding.welcomeText
        homeViewModel.text.observe(viewLifecycleOwner) {
            //textView.text = it
        }

        val displayName = auth.currentUser!!.displayName
        Log.d("HF", "displayName: $displayName")
        textView.text = SpannableStringBuilder("Welcome")
        dbRef.get().addOnCompleteListener {
            if(it.result.value != null){
                var dbList = it.result.value as List<HashMap<String,*>>
                //dbList = dbList.filter.
                 dbList = dbList.filterNotNull()
                val foodLst = ArrayList<Food>()

                Log.i(TAG,"RAW DATA --> $dbList")

                for (i in dbList){
                   //Log.d(TAG,i.get("nutrients")as HashMap<String,Float>)
                    val food = Food(i.get("name") as String,
                        i.get("nutrients") as HashMap<String,Float>)
                    foodLst.add(food)
                }


                sumNutrients = homeViewModel.getSum(foodLst)
                avgNutrients = homeViewModel.getAverages(sumNutrients, foodLst.size)
                GRAPH_GENERATOR.generatePieGraph(pieChart, avgNutrients)


            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}



