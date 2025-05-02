package com.powluiz.cashflow_app.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.powluiz.cashflow_app.adapters.TransactionAdapter
import com.powluiz.cashflow_app.database.CashTransaction
import com.powluiz.cashflow_app.database.DatabaseHelper
import com.powluiz.cashflow_app.databinding.ActivityTransactionHistoryBinding

class TransactionHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionHistoryBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var transactionAdapter: TransactionAdapter
    private var transactions = mutableListOf<CashTransaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        binding.recyclerViewTransactions.layoutManager = LinearLayoutManager(this)
        binding.buttonBack.setOnClickListener {
            finish()
        }
        transactionAdapter = TransactionAdapter(this, transactions) { transactionId ->
            deleteTransaction(transactionId)
        }
        binding.recyclerViewTransactions.adapter = transactionAdapter
        loadTransactions()
    }

    private fun loadTransactions() {
        transactions.clear()
        transactions.addAll(databaseHelper.getAllTransactions())

        if (transactions.isEmpty()) {
            binding.recyclerViewTransactions.visibility = View.GONE
            binding.textViewEmptyList.visibility = View.VISIBLE
        } else {
            binding.recyclerViewTransactions.visibility = View.VISIBLE
            binding.textViewEmptyList.visibility = View.GONE
            transactionAdapter.notifyDataSetChanged()
        }
    }

    private fun deleteTransaction(transactionId: Int) {
        databaseHelper.deleteTransaction(transactionId)

        val position = transactions.indexOfFirst { it.id == transactionId }

        if (position != -1) {
            transactions.removeAt(position)
            transactionAdapter.notifyItemRemoved(position)

            if (transactions.isEmpty()) {
                binding.recyclerViewTransactions.visibility = View.GONE
                binding.textViewEmptyList.visibility = View.VISIBLE
            }
        }
    }
    override fun onResume() {
        super.onResume()
        loadTransactions()
    }
}