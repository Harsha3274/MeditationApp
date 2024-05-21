package com.wodo.meditationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val relaxCard: CardView = findViewById(R.id.relax_card)
        val meditateCard: CardView = findViewById(R.id.meditate_card)
//        val stressCard: CardView = findViewById(R.id.stress_card)

        relaxCard.setOnClickListener {
            startActivity(Intent(this, relax::class.java))
        }

        meditateCard.setOnClickListener{
            startActivity(Intent(this,meditate::class.java))
        }

//        stressCard.setOnClickListener{
//            startActivity(Intent(this,stress::class.java))
//        }
    }
}
