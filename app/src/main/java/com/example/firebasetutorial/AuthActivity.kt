package com.example.firebasetutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.google.firebase.analytics.FirebaseAnalytics

class AuthActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    //Splash
    Thread.sleep(200)//HACK
    setTheme(R.style.SlashTheme)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_auth)

    //Analytics event
    val analytics = FirebaseAnalytics.getInstance(this)
    val bundle = Bundle()
    bundle.putString("message", "Integracion Firebase Completa ")
    analytics.logEvent("InitScreen", bundle)
  }



}


