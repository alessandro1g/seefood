package com.example.seefood

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase


private const val TAG = "SettingsActivity"
class SettingsActivity : AppCompatActivity() {
    private lateinit var builder: AlertDialog.Builder
    private lateinit var updateName: LinearLayoutCompat
    private lateinit var updateEmail: LinearLayoutCompat
    private lateinit var updatePassword: LinearLayoutCompat
    private lateinit var logoutBtn: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        auth = Firebase.auth

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings1, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // updating the username
        updateName = findViewById(R.id.nameSetting)
        updateName.setOnClickListener {
            Toast.makeText(this, "clicked update name", Toast.LENGTH_SHORT).show()
            // edittext to store the new name value
            val resetName = EditText(this)
            // builder for the dialog box
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Update Name")
            builder.setCancelable(true)
            builder.setMessage("Enter a name of with at least 2 characters.")
            builder.setView(resetName)


            builder.setPositiveButton("UPDATE") { _, _ ->
                // overriden below in order to keep dialog box active when input is invalid
            }

            builder.setNegativeButton("Cancel") { _, _ ->
                // Toast.makeText(this,"update cancelled", Toast.LENGTH_SHORT).show()
                // do nothing
            }


            // actual pop up box
            val nameDialog = builder.create()
            nameDialog.show()

            val submit = nameDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            submit.setOnClickListener{
                val newName = resetName.text.toString()

                if (newName.isEmpty() || newName.length < 2){
                    Snackbar.make(
                        (findViewById(R.id.settings_layout)) ,
                        "Name must be at least 2 characters",
                        Snackbar.LENGTH_LONG).show()
                }
                else {
                    val user = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = newName
                    }

                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                //Log.d(TAG, "User profile updated with new name: $newName")
                                Snackbar.make(
                                    (findViewById(R.id.settings_layout)) ,
                                    "Profile successfully updated",
                                    Snackbar.LENGTH_LONG).show()

                                user.reload()
                                nameDialog.dismiss()
                            }
                            else {
                                // it must be a database error
                                Snackbar.make(
                                    (findViewById(R.id.settings_layout)) ,
                                    "Error updating username in the database. Try again",
                                    Snackbar.LENGTH_LONG).show()
                            }
                        }
                }
            }

        }



        // updating the email
        updateEmail = findViewById(R.id.emailSetting)
        updateEmail.setOnClickListener {
            val confirmPass = EditText(this)
            confirmPass.transformationMethod = PasswordTransformationMethod.getInstance()

            val passBuilder = AlertDialog.Builder(this)
            passBuilder.setTitle("Authentication")
            passBuilder.setCancelable(true)
            passBuilder.setMessage("Enter your password again to authenticate your account.")
            passBuilder.setView(confirmPass)

            passBuilder.setPositiveButton("SUBMIT") {_, _ ->

                // retrieve current user email
                val user = FirebaseAuth.getInstance().currentUser
                val currEmail = user?.email.toString()

                //retrieve current password from edittext in the layout
                val currPass = confirmPass.text.toString()

                // check if edittext is empty
                if (currPass.isEmpty()){
                    Toast.makeText(this,
                        "Your current password is required to continue.",
                        Toast.LENGTH_SHORT).show()

                    confirmPass.error = "This field cannot be blank."
                    confirmPass.requestFocus()

                }
                else {
                    // re-authenticateWithCredentials
                    val credentials = EmailAuthProvider.getCredential(currEmail, currPass)
                    user?.reauthenticate(credentials)?.addOnCompleteListener{
                        if (it.isSuccessful){
                            // create 2nd dialog box
                            val resetEmail = EditText(this)
                            resetEmail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

                            val emailBuilder = AlertDialog.Builder(this)
                            emailBuilder.setTitle("Update Email")
                            emailBuilder.setCancelable(true)
                            emailBuilder.setMessage("Enter your new email.")
                            emailBuilder.setView(resetEmail)

                            emailBuilder.setPositiveButton("UPDATE") { _, _ ->
                                // do nothing

                            }
                            emailBuilder.setNegativeButton("CANCEL") { _, _ ->
                                // dismiss
                            }

                            val emailDialog = emailBuilder.create()
                            emailDialog.show()

                            // keep dialog active until valid email is provided
                            val submit = emailDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            submit.setOnClickListener{
                                val newEmail = resetEmail.text.toString()
                                if (newEmail.isEmpty()){
                                    Toast.makeText(this,
                                        "Your new email is required to continue.",
                                        Toast.LENGTH_SHORT).show()

                                    resetEmail.error = "This field cannot be blank."
                                    resetEmail.requestFocus()
                                }
                                else if (newEmail == user.email){
                                    // check that new email provided is different from currEmail
                                    Toast.makeText(this,
                                        "Your new email must NOT match your current email.",
                                        Toast.LENGTH_SHORT).show()

                                    resetEmail.error = "New email was the same as current email."
                                    resetEmail.requestFocus()
                                }
                                else {
                                    // update user data
                                    user.updateEmail(newEmail).addOnCompleteListener(this) { task->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this,
                                                "Your email was successfully updated to $newEmail.",
                                                Toast.LENGTH_SHORT).show()

                                            emailDialog.dismiss()
                                        }
                                        else {
                                            // then it must be a firebase error
                                            Toast.makeText(this,
                                                "Email update failed.",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }

                            }
                        }
                        else {
                            Toast.makeText(this,
                                "The password you entered is incorrect. Try again.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            passBuilder.setNegativeButton("CANCEL") {_, _ ->
                // dismiss
            }

            val passDialog = passBuilder.create()
            passDialog.show()
        }

        // update password feature
        // enter password twice before update
        updatePassword = findViewById(R.id.passwordSetting)
        updatePassword.setOnClickListener{
            // create 1st dialog box for authentication
            val confirmPass = EditText(this)
            confirmPass.transformationMethod = PasswordTransformationMethod.getInstance()

            val passBuilder = AlertDialog.Builder(this)
            passBuilder.setTitle("Authentication")
            passBuilder.setCancelable(true)
            passBuilder.setMessage("Enter your password again to authenticate your account.")
            passBuilder.setView(confirmPass)

            passBuilder.setNegativeButton("CANCEL") {_, _ ->
                // dismiss
            }

            passBuilder.setPositiveButton("SUBMIT") { _, _ ->
                val user = FirebaseAuth.getInstance().currentUser

                val currEmail = user?.email.toString()
                val currPass = confirmPass.text.toString()

                if (currPass.isEmpty()){
                    Toast.makeText(this,
                        "Your current password is required to continue.",
                        Toast.LENGTH_SHORT).show()

                    confirmPass.error = "This field cannot be blank."
                    confirmPass.requestFocus()

                }
                else {
                    val credentials = EmailAuthProvider.getCredential(currEmail, currPass)
                    user?.reauthenticate(credentials)?.addOnCompleteListener{ task ->
                        if (task.isSuccessful) {
                            // 2nd dialog box
                            val resetPass = EditText(this)
                            resetPass.transformationMethod = PasswordTransformationMethod.getInstance()

                            val newPassBuilder = AlertDialog.Builder(this)
                            newPassBuilder.setTitle("Update Password")
                            newPassBuilder.setCancelable(true)
                            newPassBuilder.setMessage("Enter your new password. Must be at least 6 characters.")
                            newPassBuilder.setView(resetPass)

                            newPassBuilder.setPositiveButton("UPDATE") { _, _ ->
                                // do nothing

                            }
                            newPassBuilder.setNegativeButton("CANCEL") { _, _ ->
                                // dismiss
                            }

                            val newPassDialog = newPassBuilder.create()
                            newPassDialog.show()

                            // keep dialog active until valid email is provided
                            val submit = newPassDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            submit.setOnClickListener{
                                val newPass = resetPass.text.toString()

                                // check if edittext is empty
                                if (newPass.isEmpty() || newPass.length < 6) {
                                    Toast.makeText(this,
                                        "Your new password must must have 6 or more characters.",
                                        Toast.LENGTH_SHORT).show()

                                    resetPass.error = "New password must have 6 or more characters."
                                    resetPass.requestFocus()
                                }
                                // check if new password is the same as old password
                                else if (newPass == currPass){
                                    Toast.makeText(this,
                                        "Your new password must NOT match your current password.",
                                        Toast.LENGTH_SHORT).show()

                                    resetPass.error = "New password was the same as current password."
                                    resetPass.requestFocus()
                                }
                                // finally update password
                                else {
                                    user.updatePassword(newPass).addOnCompleteListener (this) {task ->
                                        if (task.isSuccessful){
                                            Toast.makeText(this,
                                                "Your password was successfully updated.",
                                                Toast.LENGTH_SHORT).show()

                                            newPassDialog.dismiss()
                                        }
                                        else {
                                            // must be a database error
                                            Toast.makeText(this,
                                                "Email update failed.",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }

                            }
                        }
                        else {
                            Toast.makeText(this,
                                "The password you entered is incorrect. Try again.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            val passDialog = passBuilder.create()
            passDialog.show()
        }

        // logout
        logoutBtn = findViewById(R.id.settings_logout)
        logoutBtn.setOnClickListener{
            // create alert dialog to confirm logout
            builder = AlertDialog.Builder(this)

            builder.setTitle("LOGOUT")
            builder.setMessage("Are you sure you want to log out of this app?")
            builder.setCancelable(true)
            builder.setPositiveButton("Yes") {
                    _, _ ->
                Toast.makeText(this,"Logout successful", Toast.LENGTH_SHORT).show()
                // log out and return to auth activity
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, AuthActivity::class.java))
            }

            builder.setNegativeButton("No") {
                    _, _ ->
                // Toast.makeText(this,"user remain logged in", Toast.LENGTH_SHORT).show()

            }

            builder.show()
        }

    }




    // logout menu inside of settings
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_user_btn -> {
                //Toast.makeText(this, "CLICKED DELETE USER", Toast.LENGTH_SHORT).show()

                Log.d(TAG, "user initiated request to delete Account")

                builder = AlertDialog.Builder(this)
                builder.setTitle("DELETE ACCOUNT")
                builder.setCancelable(true)
                builder.setPositiveButton("Yes") { _,_ ->

                    // delete from firebase
                    // https://firebase.google.com/docs/auth/android/manage-users#delete_a_user
                    val user = Firebase.auth.currentUser!!

                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User account deleted.")
                                Toast.makeText(this,"Your account has been deleted.", Toast.LENGTH_SHORT).show()
                            }
                        }

                    // start authActivity
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, AuthActivity::class.java))
                }

                builder.setNegativeButton("No") { _,_ ->
                    //Toast.makeText(this,"user account remains active", Toast.LENGTH_SHORT).show()
                    // do nothing
                }

                builder.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }


}