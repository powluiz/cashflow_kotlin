package com.powluiz.cashflow_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.powluiz.cashflow_app.R
import com.powluiz.cashflow_app.database.CashTransaction
import com.powluiz.cashflow_app.database.TransactionType
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(
    private val context: Context,
    private val transactions: MutableList<CashTransaction>,
    private val onDeleteClickListener: (Int) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDetail: TextView = itemView.findViewById(R.id.textViewTransactionDetail)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewTransactionDate)
        val textViewValue: TextView = itemView.findViewById(R.id.textViewTransactionValue)
        val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDeleteTransaction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val currentTransaction = transactions[position]

        holder.textViewDetail.text = currentTransaction.detail.label
        holder.textViewDate.text = currentTransaction.date

        val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        val formattedValue = formatter.format(currentTransaction.value)
        holder.textViewValue.text = formattedValue

        if (currentTransaction.type == TransactionType.INCOME) {
            holder.textViewValue.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
        } else {
            holder.textViewValue.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
        }

        holder.buttonDelete.setOnClickListener {
            onDeleteClickListener(currentTransaction.id)
        }
    }

    override fun getItemCount() = transactions.size
}