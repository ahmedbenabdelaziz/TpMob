package com.example.stipa

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api

data class Appareil(
    val id: String,
    val type: String,
    val availableCommands: List<String>,
    val opening: Int? = null,
    val power: Int? = null
)

data class DevicesResponse(val devices: List<Appareil>)
data class CommandRequest(val command: String)

class DevicesActivity : AppCompatActivity() {

    private val api = Api()
    private val baseUrl = "https://polyhome.lesmoulinsdudev.com/api"
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)

        sessionManager = SessionManager(this)
        val devicesList = findViewById<ListView>(R.id.devicesList)
        val btnBack = findViewById<TextView>(R.id.btnBack)

        btnBack.setOnClickListener {
            onBackPressed()
        }

        val houseId = intent.getIntExtra("houseId", -1)
        if (houseId == -1) {
            Toast.makeText(this, "ID maison invalide", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val token = sessionManager.getToken()

        api.get<DevicesResponse>(
            "$baseUrl/houses/$houseId/devices",
            onSuccess = { code, response ->
                runOnUiThread {
                    if (code == 200 && response != null) {
                        val deviceNames = response.devices.map { "${it.type} (${it.id})" }
                        devicesList.adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_list_item_1,
                            deviceNames
                        )

                        devicesList.setOnItemClickListener { _, _, position, _ ->
                            val appareil = response.devices[position]
                            if (appareil.availableCommands.isNotEmpty()) {
                                sendCommand(houseId, appareil.id, appareil.availableCommands[0])
                            } else {
                                Toast.makeText(this, "Pas de commande disponible", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else if (code == 500) {
                        Toast.makeText(this, "Erreur serveur, réessayez plus tard", Toast.LENGTH_SHORT).show()
                    } else if (code == 403) {
                        Toast.makeText(this, "Accès interdit, token invalide", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erreur chargement périphériques (HTTP $code)", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            securityToken = token
        )
    }

    private fun sendCommand(houseId: Int, deviceId: String, command: String) {
        val token = sessionManager.getToken()
        val body = CommandRequest(command)

        api.post(
            "$baseUrl/houses/$houseId/devices/$deviceId/command",
            body,
            onSuccess = { code ->
                runOnUiThread {
                    when (code) {
                        200 -> Toast.makeText(this, "Commande envoyée", Toast.LENGTH_SHORT).show()
                        403 -> Toast.makeText(this, "Accès interdit", Toast.LENGTH_SHORT).show()
                        500 -> Toast.makeText(this, "Erreur serveur, réessayez plus tard", Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this, "Erreur commande (HTTP $code)", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            securityToken = token
        )
    }
}
