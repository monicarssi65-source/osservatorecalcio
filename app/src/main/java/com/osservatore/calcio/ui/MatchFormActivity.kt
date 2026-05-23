package com.osservatore.calcio.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.osservatore.calcio.R
import com.osservatore.calcio.data.Match
import com.osservatore.calcio.databinding.ActivityMatchFormBinding
import com.osservatore.calcio.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class MatchFormActivity : AppCompatActivity() {
    private lateinit var b: ActivityMatchFormBinding
    private val vm: MainViewModel by viewModels()

    data class Marcatore(var squadra: String, var numero: String, var giocatore: String, var minuto: String)
    data class Sostituzione(var squadra: String, var out: String, var inp: String, var minuto: String)

    private val marcatori = mutableListOf<Marcatore>()
    private val sostituzioni = mutableListOf<Sostituzione>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMatchFormBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "⚽ Scheda Partita"

        // Set today's date
        b.etData.setText(SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(Date()))

        b.btnAddMarcatore.setOnClickListener { addMarcatoreRow() }
        b.btnAddSostituzione.setOnClickListener { addSostituzioneRow() }
        b.btnSave.setOnClickListener { save() }
        b.btnReset.setOnClickListener { resetForm() }
    }

    private fun addMarcatoreRow() {
        val row = LayoutInflater.from(this).inflate(R.layout.row_marcatore, b.containerMarcatori, false)
        b.containerMarcatori.addView(row)
        row.findViewById<ImageButton>(R.id.btnRemove).setOnClickListener {
            b.containerMarcatori.removeView(row)
        }
    }

    private fun addSostituzioneRow() {
        val row = LayoutInflater.from(this).inflate(R.layout.row_sostituzione, b.containerSostituzioni, false)
        b.containerSostituzioni.addView(row)
        row.findViewById<ImageButton>(R.id.btnRemove).setOnClickListener {
            b.containerSostituzioni.removeView(row)
        }
    }

    private fun collectMarcatori(): List<Marcatore> {
        val list = mutableListOf<Marcatore>()
        for (i in 0 until b.containerMarcatori.childCount) {
            val row = b.containerMarcatori.getChildAt(i)
            val squadra = row.findViewById<Spinner>(R.id.spSquadra)?.selectedItem?.toString() ?: "Casa"
            val numero = row.findViewById<EditText>(R.id.etNumero)?.text?.toString() ?: ""
            val giocatore = row.findViewById<EditText>(R.id.etGiocatore)?.text?.toString() ?: ""
            val minuto = row.findViewById<EditText>(R.id.etMinuto)?.text?.toString() ?: ""
            if (giocatore.isNotEmpty()) list.add(Marcatore(squadra, numero, giocatore, minuto))
        }
        return list
    }

    private fun collectSostituzioni(): List<Sostituzione> {
        val list = mutableListOf<Sostituzione>()
        for (i in 0 until b.containerSostituzioni.childCount) {
            val row = b.containerSostituzioni.getChildAt(i)
            val squadra = row.findViewById<Spinner>(R.id.spSquadra)?.selectedItem?.toString() ?: "Casa"
            val out = row.findViewById<EditText>(R.id.etOut)?.text?.toString() ?: ""
            val inp = row.findViewById<EditText>(R.id.etIn)?.text?.toString() ?: ""
            val minuto = row.findViewById<EditText>(R.id.etMinuto)?.text?.toString() ?: ""
            if (out.isNotEmpty() || inp.isNotEmpty()) list.add(Sostituzione(squadra, out, inp, minuto))
        }
        return list
    }

    private fun save() {
        val casa = b.etCasa.text.toString().trim()
        val ospite = b.etOspite.text.toString().trim()
        if (casa.isEmpty() || ospite.isEmpty()) {
            Toast.makeText(this, "⚠️ Inserisci le squadre!", Toast.LENGTH_SHORT).show()
            return
        }

        val gson = Gson()
        val match = Match(
            id = UUID.randomUUID().toString(),
            data = b.etData.text.toString().trim(),
            casa = casa, ospite = ospite,
            risultato = b.etRisultato.text.toString().trim(),
            competizione = b.etCompetizione.text.toString().trim(),
            stadio = b.etStadio.text.toString().trim(),
            arbitro = b.etArbitro.text.toString().trim(),
            allCasa = b.etAllCasa.text.toString().trim(),
            allOspite = b.etAllOspite.text.toString().trim(),
            angCasa = b.etAngCasa.text.toString().toIntOrNull() ?: 0,
            angOspite = b.etAngOspite.text.toString().toIntOrNull() ?: 0,
            cronaca = b.etCronaca.text.toString().trim(),
            marcatori = gson.toJson(collectMarcatori()),
            sostituzioni = gson.toJson(collectSostituzioni())
        )

        vm.saveMatch(match)
        Toast.makeText(this, "✅ Partita $casa vs $ospite salvata!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun resetForm() {
        b.etCasa.setText("")
        b.etOspite.setText("")
        b.etRisultato.setText("")
        b.etCompetizione.setText("")
        b.etStadio.setText("")
        b.etArbitro.setText("")
        b.etAllCasa.setText("")
        b.etAllOspite.setText("")
        b.etAngCasa.setText("0")
        b.etAngOspite.setText("0")
        b.etCronaca.setText("")
        b.containerMarcatori.removeAllViews()
        b.containerSostituzioni.removeAllViews()
        b.etData.setText(SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(Date()))
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
