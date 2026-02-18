package com.example.stipa

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api

data class AccesUtilisateur(val loginUtilisateur: String)

class ShareAccessActivity : AppCompatActivity() {

    private val api = Api()
    private val urlBase = "https://polyhome.lesmoulinsdudev.com/api"
    private lateinit var gestionSession: SessionManager

    private lateinit var flecheRetour: TextView
    private lateinit var champIdMaison: EditText
    private lateinit var champLoginUtilisateur: EditText
    private lateinit var boutonPartager: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_access)

        gestionSession = SessionManager(this)

        flecheRetour = findViewById(R.id.tvBack)
        champIdMaison = findViewById(R.id.editHouseId)
        champLoginUtilisateur = findViewById(R.id.editUserLogin)
        boutonPartager = findViewById(R.id.btnShare)

        configurerListeners()
    }

    private fun configurerListeners() {
        flecheRetour.setOnClickListener { finish() }

        boutonPartager.setOnClickListener {
            val idMaisonTexte = champIdMaison.text.toString().trim()
            val loginUtilisateurTexte = champLoginUtilisateur.text.toString().trim()

            if (idMaisonTexte.isEmpty()) {
                Toast.makeText(this, "Entrez l'ID de la maison", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (loginUtilisateurTexte.isEmpty()) {
                Toast.makeText(this, "Entrez un login utilisateur", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idMaison = idMaisonTexte.toIntOrNull()
            if (idMaison == null) {
                Toast.makeText(this, "ID maison invalide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = gestionSession.getToken()
            if (token == null) {
                Toast.makeText(this, "Session expirée", Toast.LENGTH_SHORT).show()
                finish()
                return@setOnClickListener
            }

            partagerAcces(idMaison, loginUtilisateurTexte, token)
        }
    }

    private fun partagerAcces(idMaison: Int, loginUtilisateur: String, token: String) {
        val acces = AccesUtilisateur(loginUtilisateur)

        api.post<AccesUtilisateur>(
            "$urlBase/houses/$idMaison/users",
            acces,
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
