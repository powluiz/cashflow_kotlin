package com.powluiz.cashflow_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import com.google.android.material.snackbar.Snackbar
import com.powluiz.cashflow_app.database.DatabaseHelper

class MainActivity : AppCompatActivity() {


    private lateinit var dbHelper: DatabaseHelper

    // components
    private lateinit var radioGroupPaymentType: RadioGroup
    private lateinit var spinnerDetail: Spinner
    private lateinit var editTextValue: EditText
    private lateinit var editTextDate: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var buttonSeeHistory: Button
    private lateinit var buttonSeeCash: Button




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        radioGroupPaymentType = findViewById(R.id.radioGroupPaymentType)
        spinnerDetail = findViewById(R.id.spinnerDetail)
        editTextValue = findViewById(R.id.editTextValue)
        editTextDate = findViewById(R.id.editTextDate)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        buttonSeeHistory = findViewById(R.id.buttonSeeHistory)
        buttonSeeCash = findViewById(R.id.buttonSeeCash)

        buttonSubmit.setOnClickListener { view -> onClickSubmit(view) }
        buttonSeeHistory.setOnClickListener { onClickSeeHistory() }
        buttonSeeCash.setOnClickListener { onClickSeeCash() }
    }

    private fun areFieldsValid(): Boolean {
        val selectedRadioButtonId = radioGroupPaymentType.checkedRadioButtonId
        val detailSelected = spinnerDetail.selectedItem
        val valueText = editTextValue.text.toString()
        val dateText = editTextDate.text.toString()

        return selectedRadioButtonId != -1 &&
                detailSelected.toString().isNotEmpty() &&
                valueText.toFloatOrNull() != null &&
                dateText.isNotBlank()
    }

    private fun onClickSubmit(view: View) {
        if (!areFieldsValid()) {
            Snackbar.make(view, "Preencha todos os campos corretamente!", Snackbar.LENGTH_LONG).show()
            return
        }
    }
    private fun onClickSeeHistory() {}
    private fun onClickSeeCash() {}

}