package com.powluiz.cashflow_app

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import com.powluiz.cashflow_app.database.CashTransaction
import com.powluiz.cashflow_app.database.DatabaseHelper
import com.powluiz.cashflow_app.database.TransactionDetail
import com.powluiz.cashflow_app.database.TransactionType
import com.powluiz.cashflow_app.databinding.ActivityMainBinding
import com.powluiz.cashflow_app.ui.TransactionHistoryActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper

    private val incomeOptions = TransactionDetail.getDetailOptionsForType(TransactionType.INCOME)
    private val expenseOptions = TransactionDetail.getDetailOptionsForType(TransactionType.EXPENSE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editTextDate.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    this@MainActivity,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                        setText(formattedDate)
                    },
                    year, month, day
                )
                datePickerDialog.show()
            }
        }

        binding.buttonSubmit.setOnClickListener { onClickSubmit(it) }
        binding.buttonSeeHistory.setOnClickListener { onClickSeeHistory() }
        binding.buttonSeeCash.setOnClickListener { onClickSeeCash() }
        binding.radioGroupPaymentType.setOnCheckedChangeListener { _, checkedId ->
            val details = when (checkedId) {
                R.id.radioOptionIncome -> incomeOptions
                R.id.radioOptionExpense -> expenseOptions
                else -> emptyList()
            }
            updateSpinnerOptions(details)
        }

        binding.radioOptionIncome.isChecked = true
        updateSpinnerOptions(incomeOptions)
    }

    private fun updateSpinnerOptions(options: List<TransactionDetail>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDetail.adapter = adapter
    }

    private fun areFieldsValid(): Boolean {
        val selectedRadioButtonId = binding.radioGroupPaymentType.checkedRadioButtonId
        val detailSelected = binding.spinnerDetail.selectedItem
        val valueText = binding.editTextValue.text.toString()
        val dateText = binding.editTextDate.text.toString()

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

        val value = binding.editTextValue.text.toString().toDouble()
        val date = binding.editTextDate.text.toString()

        val type = when (binding.radioGroupPaymentType.checkedRadioButtonId) {
            binding.radioOptionIncome.id -> TransactionType.INCOME
            binding.radioOptionExpense.id -> TransactionType.EXPENSE
            else -> null
        }

        val detailLabel = binding.spinnerDetail.selectedItem.toString()
        val detail = TransactionDetail.fromLabel(detailLabel)

        if (type == null || detail == null) {
            return
        }

        val transaction = CashTransaction(
            id = 0,
            value = value,
            type = type,
            detail = detail,
            date = date
        )

        val transactionId = dbHelper.createTransaction(transaction)
        if (transactionId > 0) {
            Snackbar.make(view, "Item adicionado com sucesso!", Snackbar.LENGTH_LONG).show()
            binding.editTextValue.text.clear()
            binding.editTextDate.text.clear()
            binding.spinnerDetail.setSelection(0)
        } else {
            Snackbar.make(view, "Erro ao adicionar o item. Por favor, tente novamente.", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun onClickSeeHistory() {
        val intent = Intent(this, TransactionHistoryActivity::class.java)
        startActivity(intent)
    }

    private fun onClickSeeCash() {
        val totalIncomes = dbHelper.getTotalCashIncome()
        val totalExpenses = dbHelper.getTotalCashExpense()
        val balance = dbHelper.getBalance()

        val currencyFormatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR"))
        val formattedIncome = currencyFormatter.format(totalIncomes)
        val formattedExpense = currencyFormatter.format(totalExpenses)
        val formattedBalance = currencyFormatter.format(balance)

        val balanceColor = if (balance >= 0) android.graphics.Color.GREEN else android.graphics.Color.RED
        val dialogView = layoutInflater.inflate(R.layout.dialog_cash_balance, null)

        val tvIncome = dialogView.findViewById<android.widget.TextView>(R.id.textViewTotalIncome)
        val tvExpense = dialogView.findViewById<android.widget.TextView>(R.id.textViewTotalExpense)
        val tvBalance = dialogView.findViewById<android.widget.TextView>(R.id.textViewBalance)

        tvIncome.text = "Total de Créditos: $formattedIncome"
        tvExpense.text = "Total de Débitos: $formattedExpense"
        tvBalance.text = "Saldo: $formattedBalance"

        tvBalance.setTextColor(balanceColor)
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Resumo Financeiro")
            .setView(dialogView)
            .setPositiveButton("Voltar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

}