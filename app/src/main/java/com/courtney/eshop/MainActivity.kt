package com.courtney.eshop

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.info
import java.util.*

class MainActivity : AppCompatActivity(), AnkoLogger, FirebaseAuth.AuthStateListener {

    private val RC_SIGNUP: Int = 100
    private lateinit var firestoreAdapter: FirestoreRecyclerAdapter<Item, ItemHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        btn_verify.setOnClickListener {
            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        it.snackbar("Verify Email Sent!").show()
                    } else {

                    }
                }
        }

        // setupRecyclerView
        recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            val query = FirebaseFirestore.getInstance()
                .collection("items")
                .limit(10)
            val options = FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item::class.java)
                .build()
            firestoreAdapter = object : FirestoreRecyclerAdapter<Item, ItemHolder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
                    return ItemHolder(view)
                }

                override fun onBindViewHolder(holder: ItemHolder, position: Int, item: Item) {
                    item.id = snapshots.getSnapshot(position).id
                    holder.onBind(item)
                    holder.itemView.setOnClickListener {
                        itemClicked(item, position)
                    }
                }
            }
            adapter = firestoreAdapter
        }

    }

    private fun itemClicked(item: Item, position: Int) {
        info { "itemClicked: ${item.title} $position" }
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("ITEM", item)
        startActivity(intent)
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        var user = auth.currentUser
        info { "onAuthStateChanged: ${user?.uid}" }
        if (user != null) {
            txt_user_info.text = "${user.email} / ${user.isEmailVerified}"
            //btn_verify.visibility = if (user.isEmailVerified) View.GONE else View.VISIBLE
        } else {
            txt_user_info.text = "Not Sign In"
            btn_verify.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(this)
        firestoreAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
        firestoreAdapter.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_signup -> {

                val whiteList = listOf("tw", "hk", "cn", "au")
                val myLayout = AuthMethodPickerLayout.Builder(R.layout.sign_up)
                    .setEmailButtonId(R.id.img_email)
                    .setGoogleButtonId(R.id.img_google)
                    .setFacebookButtonId(R.id.img_fb)
                    .setPhoneButtonId(R.id.img_sms)
                    .build()
                startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(
                        Arrays.asList(
                            AuthUI.IdpConfig.EmailBuilder().build(),
                            AuthUI.IdpConfig.GoogleBuilder().build(),
                            AuthUI.IdpConfig.FacebookBuilder().build(),
                            AuthUI.IdpConfig.PhoneBuilder()
                                .setWhitelistedCountries(whiteList)
                                .setDefaultCountryIso("tw")
                                .build()
                        )
                    )
                    .setIsSmartLockEnabled(false)
                    .setLogo(R.drawable.shop)
                    .setTheme(R.style.SignUp)
                    .setAuthMethodPickerLayout(myLayout)
                    .build(), RC_SIGNUP)

                /*val intent = Intent(this, SignUpActivity::class.java)
                startActivityForResult(intent, RC_SIGNUP)*/
                true
            }
            R.id.action_signout -> {
                FirebaseAuth.getInstance().signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}