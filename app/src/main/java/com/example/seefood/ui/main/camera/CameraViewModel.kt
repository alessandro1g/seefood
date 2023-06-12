package com.example.seefood.ui.main.camera

import android.graphics.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.text.Text

class CameraViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is camera Fragment"
    }
    val text: LiveData<String> = _text
    fun toGrayscale(bmpOriginal: Bitmap): Bitmap {
        val width: Int
        val height: Int
        height = bmpOriginal.height
        width = bmpOriginal.width
        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        c.drawBitmap(bmpOriginal, 0f, 0f, paint)
        return bmpGrayscale
    }
    fun parseText(visionText: Text): HashMap<String, Float>{
        val hashMap =  hashMapOf<String, Float>()
//        All the pattern matching needed to extract all the necessary dietary info
        val fatPattern = Regex("Total Fat (\\d+|O)n?m?g?9?").find(visionText.text)
        val sodiumPattern = Regex("Sodium (\\d+|O)n?m?g?9?").find(visionText.text)
        val carbsPattern = Regex("Total Carbohydrate (\\d+|O)n?m?g?9?").find(visionText.text)
        val sugarPattern = Regex("Total Sugars (\\d+|O)n?m?g?9?").find(visionText.text)
        val proteinPattern = Regex("Protein (\\d+|O)n?m?g?9?").find(visionText.text)
        val potassiumPattern = Regex("Potassium|Potasium (\\d+|O)n?m?g?9?").find(visionText.text)
        val vitaminPattern = Regex("Vitamin [A-Z] (\\d+|O)n?m?g?9?").find(visionText.text)
        val servingSizePattern = Regex("Serving size (\\d+(\\.\\d+)?) .*").find(visionText.text)
//        Extract information and add it to hashMap
        if(fatPattern != null){
            var (amount,) = fatPattern.destructured
            if(amount.endsWith("9")){
                amount = amount.dropLast(1)
            } else if(amount == "O" || amount == ""){
                amount = "0"
            }
            println("Amount: $amount")
            hashMap["Total Fat"] = amount.toFloat()
        } else {
            println("COUDLN'T FIND FAT")
        }

        if(sodiumPattern != null){
            var (amount,) = sodiumPattern.destructured
            if(amount.endsWith("9")){
                amount = amount.dropLast(1)
            } else if(amount == "O" || amount == ""){
                amount = "0"
            }
            println("Amount: $amount")
            hashMap["Sodium"] = amount.toFloat()
        } else {
            println("COUDLN'T FIND SODIUM")
        }

        if(carbsPattern != null){
            var (amount,) = carbsPattern.destructured
            if(amount.endsWith("9")){
                amount = amount.dropLast(1)
            } else if(amount == "O" || amount == ""){
                amount = "0"
            }
            println("Amount: $amount")
            hashMap["Total Carbohydrates"] = amount.toFloat()
        } else {
            println("COUDLN'T FIND CARBS")
        }

        if(sugarPattern != null){
            var (amount,) = sugarPattern.destructured
            if(amount.endsWith("9")){
                amount = amount.dropLast(1)
            } else if(amount == "O" || amount == ""){
                amount = "0"
            }
            println("Amount: $amount")
            hashMap["Total Sugars"] = amount.toFloat()
        } else {
            println("COUDLN'T FIND SUGARS")
        }

        if(proteinPattern != null){
            var (amount,) = proteinPattern.destructured
            if(amount.endsWith("9")){
                amount = amount.dropLast(1)
            } else if(amount == "O" || amount == ""){
                amount = "0"
            }
            println("Amount: $amount")
            hashMap["Proteins"] = amount.toFloat()
        } else {
            println("COUDLN'T FIND PROTEIN")
        }

        if(potassiumPattern != null){
            var (amount,) = potassiumPattern.destructured
            if(amount.endsWith("9")){
                amount = amount.dropLast(1)
            } else if(amount == "O" || amount == ""){
                amount = "0"
            }
            println("Amount: $amount")
            hashMap["Potassium"] = amount.toFloat()
        } else {
            println("COUDLN'T FIND POTASSIUM")
        }

        if(vitaminPattern != null){
            var (amount,) = vitaminPattern.destructured
            if(amount.endsWith("9")){
                amount = amount.dropLast(1)
            } else if(amount == "O" || amount == ""){
                amount = "0"
            }
            println("Amount: $amount")
            hashMap["Vitamin"] = amount.toFloat()
        } else {
            println("COUDLN'T FIND VITAMIN")
        }

        if(servingSizePattern != null){
            var (amount,_) = servingSizePattern.destructured
            if(amount == ""){
               amount = "1"
            }
            println("Amount: $amount")
            hashMap["Serving Size"] = amount.toFloat()
        } else {
            println("COUDLN'T FIND Serving Size")
        }
        return hashMap
    }

}