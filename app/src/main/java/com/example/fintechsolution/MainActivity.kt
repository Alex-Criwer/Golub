package com.example.fintechsolution


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fintechselection.ImageFragment
import timber.log.Timber
import tools.DESCRIPTION_SHARED_PREFS
import tools.MY_SHARED_PREFS

class MainActivity : AppCompatActivity() {

    private var count: Int = 0
    var sharedPrefs: SharedPreferences? = null
    var descriptionSharedPrefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        sharedPrefs = this.getSharedPreferences(MY_SHARED_PREFS, Context.MODE_PRIVATE)
        descriptionSharedPrefs = this.getSharedPreferences(DESCRIPTION_SHARED_PREFS, Context.MODE_PRIVATE)

        findViewById<Button>(R.id.btn_next).setOnClickListener {
            count++
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ImageFragment.newInstance(count))
                .addToBackStack(null)
                .commit()
        }

        findViewById<Button>(R.id.btn_back).setOnClickListener {
            if (count > 1) {
                onBackPressed()
                count--
            }
        }
    }

    override fun onDestroy() {
        sharedPrefs?.edit()?.clear()?.apply()
        descriptionSharedPrefs?.edit()?.clear()?.apply()
        super.onDestroy()
    }
}