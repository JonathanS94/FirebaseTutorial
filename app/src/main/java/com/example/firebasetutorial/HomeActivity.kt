package com.example.firebasetutorial

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
  BASIC,
  GOOGLE,
}

class HomeActivity : AppCompatActivity() {

  private lateinit var emailTextView: TextView
  private lateinit var providerTextView: TextView
  private lateinit var logOutButton: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)

    // Inicializamos las vistas
    emailTextView = findViewById(R.id.emailTextView)
    providerTextView = findViewById(R.id.providerTextView)
    logOutButton = findViewById(R.id.logOutButton)

    // Setup
    val bundle = intent.extras
    val email = bundle?.getString("email") ?: ""
    val provider = bundle?.getString("provider") ?: ""

    setup(email, provider)
    //Guardado de datos
    val prefs = getSharedPreferences(
      getString(R.string.prefers_file),
      Context.MODE_PRIVATE
    )

    prefs.edit()
      .putString("email", email)
      .putString("provider", provider)
      .apply()
  }



  private fun setup(email: String, provider: String) {
    title = "Inicio"

    emailTextView.text = email
    providerTextView.text = provider

    logOutButton.setOnClickListener {
      //Borrar datos
      val prefs = getSharedPreferences(
        getString(R.string.prefers_file),
        Context.MODE_PRIVATE
      )

      prefs.edit()
        .putString("email", email)
        .putString("provider", provider)
        .apply()

      FirebaseAuth.getInstance().signOut()
      onBackPressedDispatcher.onBackPressed()
    }
  }
}

private fun SharedPreferences.putString(string: String, email: String) {}
