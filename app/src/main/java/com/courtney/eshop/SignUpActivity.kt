package com.courtney.eshop

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.alert
import org.jetbrains.anko.info
import org.jetbrains.anko.okButton

class SignUpActivity : AppCompatActivity(), AnkoLogger {

    private val RC_GOOGLE_SIGN_IN: Int = 200
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        google_sign_in.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, RC_GOOGLE_SIGN_IN)
        }

        btn_signup.setOnClickListener {
            signUp()
        }

        btn_signin.setOnClickListener {
            signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            info { "onActivityResult: ${account?.id}" }
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .addOnCompleteListener {task ->
                    if(task.isSuccessful) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        alert {
                            title = "Sign In"
                            message = task.exception?.message.toString()
                            okButton {  }
                        }.show()
                    }
                }
        }
    }

    private fun signUp() {
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
                        message = it.exception?.message.toString()
                        okButton { }
                    }.show()
                }
            }
    }

    private fun signIn() {
        val sEmail = edt_email.text.toString()
        val sPassword = edt_password.text.toString()
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(sEmail, sPassword)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    alert {
                        title = "Sign In"
                        message = it.exception?.message.toString()
                        okButton { }
                    }.show()
                }
            }
    }
}
