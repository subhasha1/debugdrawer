package com.androidbolts.debugdrawer.sample

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.androidbolts.debugdrawer.BaseActivity
import com.androidbolts.debugdrawer.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Subhash Acharya on 6/5/17.
 */
class SampleActivity : BaseActivity() {
    var call: Call<HashMap<String, String>>? = null
    lateinit var hello: TextView
    lateinit var progress: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        hello = findViewById(R.id.hello) as TextView
        progress = findViewById(R.id.progress) as ProgressBar
    }

    override fun onResume() {
        super.onResume()
        call = data.helloWorld()
        call!!.enqueue(object : Callback<HashMap<String, String>> {
            override fun onResponse(call: Call<HashMap<String, String>>?, response: Response<HashMap<String, String>>?) {
                hello.text = response!!.body().toString()
                hello.visibility = VISIBLE
                progress.visibility = GONE
            }

            override fun onFailure(call: Call<HashMap<String, String>>?, t: Throwable?) {
                Toast.makeText(this@SampleActivity, "Failed to load hello world.", Toast.LENGTH_LONG).show()
                hello.text = "Failed to load hello world."
                hello.visibility = VISIBLE
                progress.visibility = GONE
            }
        })
    }

    override fun onPause() {
        super.onPause()
        call?.cancel()
    }
}