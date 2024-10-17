package com.jayanth.jtv

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainActivity : Activity() {

    private val httpClient = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkServerStatus()
    }

    private fun checkServerStatus() {
        val request = Request.Builder()
            .url("http://localhost:5001")
            .build()

        httpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                openTermuxAndThenTvApp()
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                val statusCode = response.code
                if (statusCode == 200) {
                    openTvApp()
                } else {
                    openTermuxAndThenTvApp()
                }
            }
        })
    }

    private fun openTvApp() {
        val intent = Intent().apply {
            component = ComponentName("ar.tvplayer.tv", "ar.tvplayer.tv.ui.MainActivity")
        }
        startActivity(intent)
        finish()
    }

    private fun openTermuxAndThenTvApp() {
        val termuxIntent = packageManager.getLaunchIntentForPackage("com.termux")
        if (termuxIntent != null) {
            startActivity(termuxIntent)
            Handler(Looper.getMainLooper()).postDelayed({
                openTvApp()
            }, 2500)
        } else {
            finish()
        }
    }
}
