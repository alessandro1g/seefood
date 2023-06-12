package com.example.seefood.ui.main.camera

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.*
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.seefood.R
import com.example.seefood.databinding.FragmentCameraBinding
import com.example.seefood.utils.Food
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CameraFragment : Fragment() {

    private var _binding:  FragmentCameraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    //Added the following instance variables
    private lateinit var imageView: ImageView
    private lateinit var currentPhotoPath: String
    private lateinit var photoFile: File
    private lateinit var cameraViewModel:CameraViewModel
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    //private var nName =stringOf

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cameraViewModel =
            ViewModelProvider(this)[CameraViewModel::class.java]

        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        val root: View = binding.root

        this.imageView = binding.picture
        Log.d("ONCLICK HANDLER", "BEFORE SETTING HANDLER")
        Log.d("ONCLICK HANDLER", "BEFORE SETTING HANDLER")
        binding.button.setOnClickListener {
            takePicture()
        }
        //autoTest()
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        auth = Firebase.auth
        //Reference to a list of Food data objects
        dbRef = FirebaseDatabase.getInstance().reference.child("Users").child(auth.uid!!).child("foods")
        Log.d("CF", "User: ${auth.currentUser?.email} ${auth.uid}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun takePicture(){
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        val uri= FileProvider.getUriForFile(this.requireContext(),"com.example.seefood.fileprovider", photoFile)
        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(pictureIntent, 1)
    }
    private fun createImageFile(): File {
        val timeStamp: String= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        val storageDir: File?=this.activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply{currentPhotoPath = absolutePath}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 1){
                val uri = FileProvider.getUriForFile(this.requireContext(), "com.example.seefood.fileprovider", photoFile)
                imageView.setImageURI(uri)
                var photo = MediaStore.Images.Media.getBitmap(this.requireActivity().contentResolver, uri)
                photo = cameraViewModel.toGrayscale(photo)
                val input = photo?.let{ InputImage.fromBitmap(it, 0)}
                val result = input?.let {
                    recognizer.process(it).addOnSuccessListener { visionText ->

                        onRecognizerSuccess(visionText, photo)
                    }.addOnFailureListener{ e ->
                        Log.d("RECOGNIZER", "FAILED! ${e.localizedMessage}")
                    }
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun onRecognizerSuccess(visionText: Text, photo:Bitmap){
        //Do what needs to get done
        Log.d("RECOGNIZER", "TEXT: ${visionText.text}\n")
        val nutrients = cameraViewModel.parseText(visionText)
        Log.d("RESULT", "nutrients: ${nutrients}\n")
        val name = "Food"
        val bytes = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.PNG,100, bytes)
        val food = Food(name, nutrients)
        Log.d("Saving", "Food Recorded $food\n")

        setName(food)
        //saveNutrition(food)



    }

    private fun setName(food:Food){
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edit_name_layout, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.editNameCam)

        with(builder){
            setTitle("Enter the food name!")
            setPositiveButton("Ok"){dialog,which->
                food.name = editText.text.toString()
                onClicki(food)

            }
            setNegativeButton("Cancel"){dialog,which->

            }
            setView(dialogLayout)
            show()
        }
        //food.name = nName


    }

    private fun onClicki(food:Food){
        saveNutrition(food)
    }


    private fun saveNutrition(food: Food){
        var foodLst = ArrayList<Food>()
        Log.d("Saving", "In saveNutrition")
        Log.d("Saving", "User: ${auth.currentUser?.email} ${auth.uid}")
        dbRef.get().addOnCompleteListener { it ->

            Log.d("Saving", "Data: ${it.result.value}")
            Log.d("Saving", "Children count: ${it.result.childrenCount}")
            if (it.result.value != null){
                foodLst = it.result.value as ArrayList<Food>
                Log.d("Saving", "foodLst not null: $foodLst")

            }

            foodLst.add(food)
            Log.d("Saving", "New foodLst: $foodLst")
            dbRef.setValue(foodLst)


        }
        dbRef.get().addOnCompleteListener {
            Log.d("Saving", "New Data: ${it.result.value}")
        }



    }
    private fun autoTest(){
        val photo = BitmapFactory.decodeResource(resources, R.drawable.grayscale)
        val input = photo?.let{ InputImage.fromBitmap(it, 0)}
        val result = input?.let {
            recognizer.process(it).addOnSuccessListener { visionText ->

                onRecognizerSuccess(visionText, photo)
            }.addOnFailureListener{ e ->
                Log.d("RECOGNIZER", "FAILED! ${e.localizedMessage}")
            }
        }
    }
}