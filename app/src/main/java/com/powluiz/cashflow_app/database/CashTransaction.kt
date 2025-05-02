package com.powluiz.cashflow_app.database

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class TransactionDetail(val label: String) {
    FOOD("Alimentação"),
    TRANSPORT("Transporte"),
    HEALTH("Saúde"),
    HOUSING("Moradia"),
    SALARY("Salário"),
    EXTRAS("Extras");

    override fun toString(): String = label

    companion object {
        fun fromLabel(label: String): TransactionDetail? {
            return entries.find { it.label == label }
        }

        fun getDetailOptionsForType(type: TransactionType): List<TransactionDetail> {
            return when (type) {
                TransactionType.INCOME -> listOf(SALARY, EXTRAS)
                TransactionType.EXPENSE -> listOf(FOOD, TRANSPORT, HEALTH, HOUSING)
            }
        }
    }
}

data class CashTransaction (
    val id: Int = 0,
    val value: Double,
    val type: TransactionType,
    val detail: TransactionDetail,
    val date: String
)