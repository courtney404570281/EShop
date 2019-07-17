package com.courtney.eshop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.info
import java.util.*

class MainActivity : AppCompatActivity(), AnkoLogger, FirebaseAuth.AuthStateListener {

    private val RC_SIGNUP: Int = 100
    private  val TAG = MainActivity::class.java.simpleName
    var categories = mutableListOf<Category>()
    lateinit var adapter: ItemAdapter
    lateinit var itemViewModel: ItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            view.snackbar("Replace with your own action")
                .setAction("Action", null)
                .show()
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

        FirebaseFirestore.getInstance().collection("categories")
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let {
                        categories.add(Category("", "不分類"))
                        for (doc in it) {
                            categories.add(Category(doc.id, doc.data["name"].toString()))
                        }
                        spinner.adapter = ArrayAdapter<Category>(
                            this,
                            android.R.layout.simple_spinner_item,
                            categories
                        ).apply {
                            setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
                        }
                        spinner.setSelection(0, false)
                        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            }

                        }
                    }
                }
            }

        // setupRecyclerView
        recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ItemAdapter(mutableListOf())
            recycler.adapter = adapter
            itemViewModel = ViewModelProviders.of(this@MainActivity)
                .get(ItemViewModel::class.java)
            itemViewModel.getItems().observe(this@MainActivity, androidx.lifecycle.Observer {
                Log.d(TAG, "observe: ${it.size}")
                (adapter as ItemAdapter).items = it
                (adapter as ItemAdapter).notifyDataSetChanged()
            })
        }

    }

    inner class ItemAdapter(var items: List<Item>) : RecyclerView.Adapter<ItemHolder> () {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return ItemHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.onBind(items[position])
            holder.itemView.setOnClickListener {
                itemClicked(items[position], position)
            }
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
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
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