package com.example.stipa

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api

data class Commande(val command: String)
data class MaisonDevicesResponse(val devices: List<Device>)
data class Device(val id: String, val type: String, val availableCommands: List<String>)

class PilotageMultipleActivity : AppCompatActivity() {

    private val api = Api()
    private val urlBase = "https://polyhome.lesmoulinsdudev.com/api"
    private lateinit var gestionSession: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilotagecomplet)

        gestionSession = SessionManager(this)

        val etMaisonId = findViewById<EditText>(R.id.etMaisonId)
        val btnRetour = findViewById<Button>(R.id.btnRetour)
        val btnAllumerTout = findViewById<Button>(R.id.btnAllumerTout)
        val btnEteindreTout = findViewById<Button>(R.id.btnEteindreTout)

        btnRetour.setOnClickListener { finish() }

        btnAllumerTout.setOnClickListener {
            val maisonId = etMaisonId.text.toString().toIntOrNull()
            if (maisonId == null) {
                Toast.makeText(this, "Entrez un ID de maison valide", Toast.LENGTH_SHORT).show()
            } else {
                piloterTousLesPeripheriques(maisonId, "on")
            }
        }

        btnEteindreTout.setOnClickListener {
            val maisonId = etMaisonId.text.toString().toIntOrNull()
            if (maisonId == null) {
                Toast.makeText(this, "Entrez un ID de maison valide", Toast.LENGTH_SHORT).show()
            } else {
                piloterTousLesPeripheriques(maisonId, "off")
            }
        }
    }

    private fun piloterTousLesPeripheriques(maisonId: Int, commande: String) {
        val token = gestionSession.getToken() ?: run {
            Toast.makeText(this, "Session expirée", Toast.LENGTH_SHORT).show()
            return
        }

        api.get<MaisonDevicesResponse>(
            "$urlBase/houses/$maisonId/devices",
            onSuccess = { code, response ->
                if (code == 200 && response != null) {
                    val cmd = Commande(commande)
                    response.devices.forEach { appareil ->
                        api.post<Commande>(
                            "$urlBase/houses/$maisonId/devices/${appareil.id}/command",
                            cmd,
                            onSuccess = { _ -> },
                            securityToken = token
                        )
                    }
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Commande '$commande' envoyée à tous les périphériques",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Impossible de récupérer les périphériques",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            securityToken = token
        )
    }
}
