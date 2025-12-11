package com.example.firebasetutorial

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : ComponentActivity() {

  // Declaramos las vistas
  private lateinit var emailEditText: EditText
  private lateinit var passwordEditText: EditText
  private lateinit var signUpButton: Button
  private lateinit var logInButton: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    //Splash
    Thread.sleep(200)//HACK
    setTheme(R.style.SlashTheme)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_auth)

    // 🔹 Vinculamos las vistas ANTES de usarlas
    emailEditText = findViewById(R.id.emailEditText)
    passwordEditText = findViewById(R.id.passwordEditText)
    signUpButton = findViewById(R.id.signUpButton)
    logInButton = findViewById(R.id.loginButton)

    //Analytics event
    val analytics = FirebaseAnalytics.getInstance(this)
    val bundle = Bundle()
    bundle.putString("message", "Integracion Firebase Completa ")
    analytics.logEvent("InitScreen", bundle)

    //Setup
    setup()
  }


  private fun setup() {
    title = "Autenticacion"
    signUpButton.setOnClickListener {
      if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
        FirebaseAuth.getInstance()
          .createUserWithEmailAndPassword(
            emailEditText.text.toString(),
            passwordEditText.text.toString()
          ).addOnCompleteListener {
            if (it.isSuccessful) {
              showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
            } else {
              showAlert()
            }
          }
      }
    }
      logInButton.setOnClickListener {
        if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
          FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(
              emailEditText.text.toString(),
              passwordEditText.text.toString()
            ).addOnCompleteListener {
              if (it.isSuccessful) {
                showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
              } else {
                showAlert()
              }
            }
        }
      }
  }

  private fun showAlert() {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("Error")
    builder.setMessage("Se ha producido un error autenticando al usuario")
    builder.setPositiveButton("Aceptar", null)
    builder.create().show()
  }


  private fun showHome(email: String, provider: ProviderType) {
    val homeIntent = Intent(this, HomeActivity::class.java).apply {
      putExtra("email", email)
      putExtra("provider", provider.name)
    }
    startActivity(homeIntent)
  }

}


