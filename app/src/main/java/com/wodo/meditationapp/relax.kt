package com.wodo.meditationapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class relax : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relax)

        val stressedCard: CardView = findViewById(R.id.stressed_card)
        val anxietyCard: CardView = findViewById(R.id.anxiety_card)
        val agitatedCard: CardView = findViewById(R.id.agitated_card)

//        stressedCard.setOnClickListener {
//            startActivity(Intent(this, stress::class.java))
//        }

//        anxietyCard.setOnClickListener{
//            startActivity(Intent(this,anxiety::class.java))
//        }

//        agitatedCard.setOnClickListener{
//            startActivity(Intent(this,agitated::class.java))
//        }

    }
}