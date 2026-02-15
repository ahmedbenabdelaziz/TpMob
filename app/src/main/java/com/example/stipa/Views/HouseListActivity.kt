package com.example.stipa

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api
import House
class HouseListActivity : AppCompatActivity() {

    private val api = Api()
    private val baseUrl = "https://polyhome.lesmoulinsdudev.com/api"
    private lateinit var sessionManager: SessionManager
    private var selectedHouseId: Int = 0   // ✅ on garde la maison sélectionnée

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_houslist)

        sessionManager = SessionManager(this)

        val housesList = findViewById<ListView>(R.id.housesList)
        val addHouseBtn = findViewById<Button>(R.id.addHouseBtn)
        val btnGoToShareAccess = findViewById<Button>(R.id.btnGoToShareAccess)

        val token = sessionManager.getToken()

        if (token == null) {
            Toast.makeText(this, "Session expirée", Toast.LENGTH_LONG).show()
            return
        }

        // ✅ Charger les maisons
        api.get<Array<House>>(
            "$baseUrl/houses",
            onSuccess = { code, response ->

                if (code == 200 && response != null && response.isNotEmpty()) {

                    val housesNames = response.map { "Maison ID : ${it.houseId}" }
                    val houseIds = response.map { it.houseId }

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        housesNames
                    )

                    runOnUiThread {
                        housesList.adapter = adapter
                    }

                    housesList.setOnItemClickListener { _, _, position, _ ->

                        selectedHouseId = houseIds[position]

                        val intent = Intent(this, DevicesActivity::class.java)
                        intent.putExtra("houseId", selectedHouseId)
                        startActivity(intent)
                    }

                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Aucune maison trouvée", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            securityToken = token
        )

        // ✅ Bouton partage accès
        btnGoToShareAccess.setOnClickListener {
                val intent = Intent(this, ShareAccessActivity::class.java)
                intent.putExtra("houseId", selectedHouseId)
                startActivity(intent)
            
        }

        // ✅ Bouton nouvelle maison (simple message)
        addHouseBtn.setOnClickListener {
            Toast.makeText(this, "Fonction non implémentée", Toast.LENGTH_SHORT).show()
        }
    }
}