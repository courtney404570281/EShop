package com.courtney.eshop

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btn_signup.setOnClickListener {

            val sEmail = edt_email.text.toString()
            val sPassword = edt_password.text.toString()

            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        alert {
                            title = "Sign Up"
                            message = "Account Created!"
                            okButton {
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }.show()
                    } else {
                        alert {
                            title = "Sign Up"
                            message = it.exception.toString()
                            okButton {  }
                        }.show()
                    }
                }
        }
    }
}
