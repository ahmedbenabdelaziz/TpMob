package com.example.stipa

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api

data class UserAccess(
    val userLogin: String
)

class ShareAccessActivity : AppCompatActivity() {

    private val api = Api()
    private val baseUrl = "https://polyhome.lesmoulinsdudev.com/api"
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_access)

        sessionManager = SessionManager(this)

        // Références UI
        val btnBack = findViewById<Button>(R.id.btnBack)
        val editHouseId = findViewById<EditText>(R.id.editHouseId)
        val editUserLogin = findViewById<EditText>(R.id.editUserLogin)
        val btnShare = findViewById<Button>(R.id.btnShare)

        // Bouton retour
        btnBack.setOnClickListener {
            finish() // retourne à la page précédente
        }

        // Bouton partager l'accès
        btnShare.setOnClickListener {
            val houseIdInput = editHouseId.text.toString().trim()
            val userLogin = editUserLogin.text.toString().trim()

            if (houseIdInput.isEmpty()) {
                Toast.makeText(this, "Entrez l'ID de la maison", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userLogin.isEmpty()) {
                Toast.makeText(this, "Entrez un login utilisateur", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val houseId = houseIdInput.toIntOrNull()
            if (houseId == null) {
                Toast.makeText(this, "ID maison invalide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = sessionManager.getToken()
            if (token == null) {
                Toast.makeText(this, "Session expirée", Toast.LENGTH_LONG).show()
                finish()
                return@setOnClickListener
            }

            // Appel API pour partager l'accès
            val access = UserAccess(userLogin)

            api.post<UserAccess>(
                "$baseUrl/houses/$houseId/users",
                access,
                onSuccess = { code ->
                    runOnUiThread {
                        when (code) {
                            200 -> Toast.makeText(this, "Accès partagé avec succès", Toast.LENGTH_LONG).show()
                            403 -> Toast.makeText(this, "Accès refusé", Toast.LENGTH_LONG).show()
                            409 -> Toast.makeText(this, "Utilisateur déjà ajouté", Toast.LENGTH_LONG).show()
                            500 -> Toast.makeText(this, "Erreur serveur", Toast.LENGTH_LONG).show()
                            else -> Toast.makeText(this, "Erreur ($code)", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                securityToken = token
            )
        }
    }
}
