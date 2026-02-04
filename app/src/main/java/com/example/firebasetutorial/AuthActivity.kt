package com.example.firebasetutorial

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


private val GOOGLE_SIGN_IN = 100

class AuthActivity : ComponentActivity() {

  // Declaramos las vistas
  private lateinit var emailEditText: EditText
  private lateinit var passwordEditText: EditText
  private lateinit var signUpButton: Button
  private lateinit var logInButton: Button
  private lateinit var authLayout: View
  private lateinit var googleButton: Button

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
    authLayout = findViewById(R.id.authLayout)
    googleButton = findViewById(R.id.googleButton)

    //Analytics event
    val analytics = FirebaseAnalytics.getInstance(this)
    val bundle = Bundle()
    bundle.putString("message", "Integracion Firebase Completa ")
    analytics.logEvent("InitScreen", bundle)

    //Setup
    setup()
    session()
  }

  override fun onStart() {
    super.onStart()
    authLayout.visibility = android.view.View.VISIBLE
  }

  private fun session() {
    val prefs: android.content.SharedPreferences = getSharedPreferences(
      getString(R.string.prefers_file),
      MODE_PRIVATE
    )
    val email = prefs.getString("email", null)
    val provider = prefs.getString("provider", null)
    if (email != null && provider != null) {

      showHome(email, ProviderType.valueOf(provider))
    }
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
    //Configuracion
    googleButton.setOnClickListener {
      //Google Auth
      val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
      val googleClient = GoogleSignIn.getClient(this, googleConf)
      googleClient.signOut()
      startActivityForResult(
        googleClient.signInIntent,
        GOOGLE_SIGN_IN
      )

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

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == GOOGLE_SIGN_IN) {

      val task = GoogleSignIn.getSignedInAccountFromIntent(data)
      try {
        val account = task.getResult(Exception::class.java)

        if (account != null) {

          val credential = GoogleAuthProvider.getCredential(account.idToken, null)

          FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
              showHome(account.email ?: "", ProviderType.GOOGLE)
            } else {
              showAlert()
            }
          }

        }
      } catch (e: Exception) {
        showAlert()
      }

    }


  }
}


