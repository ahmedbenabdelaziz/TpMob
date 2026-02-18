package com.example.stipa
import com.example.androidtp2.Api
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
class LoginRequest(val login: String, val password: String)
class LoginResponse(val token: String)

class LoginActivity : AppCompatActivity() {

    private val api = Api()
    // Nous utilisons SharedPreferences pour stocker le token
// car nous avons rencontré un problème avec Jetpack DataStore
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        val etLogin = findViewById<EditText>(R.id.etLogin)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnConnect = findViewById<Button>(R.id.btnConnect)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        tvRegister.setOnClickListener {
            Toast.makeText(this, "CLICK OK", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        btnConnect.setOnClickListener {
            val login = etLogin.text.toString()
            val password = etPassword.text.toString()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(login, password)

            api.post<LoginRequest, LoginResponse>("https://polyhome.lesmoulinsdudev.com/api/users/auth", request, { code, response ->
                runOnUiThread {
                    if (code == 200 && response != null ) {
                        sessionManager.saveToken(response.token)
                        val intent = Intent(this@LoginActivity, HouseListActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erreur: $code", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}


