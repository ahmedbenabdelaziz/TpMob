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
    private val urlBase = "https://polyhome.lesmoulinsdudev.com/api"
    private lateinit var gestionSession: SessionManager
    private var maisonSelectionneeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_houslist)

        gestionSession = SessionManager(this)

        val listeMaisons = findViewById<ListView>(R.id.housesList)
        val boutonPiloterAll = findViewById<Button>(R.id.addHouseBtn)
        val boutonPartagerAcces = findViewById<Button>(R.id.btnGoToShareAccess)

        val token = gestionSession.getToken()
        if (token == null) {
            Toast.makeText(this, "Session expirée", Toast.LENGTH_LONG).show()
            return
        }

        api.get<Array<House>>(
            "$urlBase/houses",
            onSuccess = { code, reponse ->

                if (code == 200 && reponse != null && reponse.isNotEmpty()) {

                    val nomsMaisons = reponse.map { "Maison ID : ${it.houseId}" }
                    val idsMaisons = reponse.map { it.houseId }

                    val adaptateur = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        nomsMaisons
                    )

                    runOnUiThread {
                        listeMaisons.adapter = adaptateur
                    }

                    listeMaisons.setOnItemClickListener { _, _, position, _ ->
                        maisonSelectionneeId = idsMaisons[position]
                        val intent = Intent(this, DevicesActivity::class.java)
                        intent.putExtra("houseId", maisonSelectionneeId)
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

        boutonPartagerAcces.setOnClickListener {
            val intent = Intent(this, ShareAccessActivity::class.java)
            intent.putExtra("houseId", maisonSelectionneeId)
            startActivity(intent)
        }

        boutonPiloterAll.setOnClickListener {
            val intent = Intent(this, PilotageMultipleActivity::class.java)
            startActivity(intent)
        }
    }
}
