package com.example.stipa

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api

class RegisterRequest(val login: String, val password: String)

class RegisterActivity : AppCompatActivity() {

    private val api = Api()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etLogin = findViewById<EditText>(R.id.etLogin)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnCreateAccount)

        btnRegister.setOnClickListener {
            val login = etLogin.text.toString().trim()
            val password = etPassword.text.toString()
            val confirm = etConfirm.text.toString()

            if (login.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RegisterRequest(login, password)

            api.post<RegisterRequest>("https://polyhome.lesmoulinsdudev.com/api/users/register", request, { code ->
                runOnUiThread {
                    when (code ) {
                        200 -> {
                            Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        409 -> {
                            Toast.makeText(this, "Ce nom d'utilisateur existe déjà", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this, "Erreur lors de l'inscription (Code: $code)", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
        }

}
