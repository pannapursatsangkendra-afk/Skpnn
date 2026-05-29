package com.example.data

import androidx.room.*

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val mobile: String, // Login ID
    val passwordHash: String,
    val role: String, // "USER", "ADMIN", "SUPER_ADMIN"
    val status: String // "ACTIVE", "INACTIVE", "BLOCKED"
)

@Entity(tableName = "istravrity_entries")
data class IstravrityEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val ritwickName: String,
    val swastyayani: Double = 0.0,
    val istabhriti: Double = 0.0,
    val acharyabhriti: Double = 0.0,
    val dakshina: Double = 0.0,
    val sangathani: Double = 0.0,
    val ritwicki: Double = 0.0,
    val pronami: Double = 0.0,
    val anandabazar: Double = 0.0,
    val srimandirCorpus: Double = 0.0,
    val parivrity: Double = 0.0,
    val utsav: Double = 0.0,
    val utsavDesc: String = "",
    val familyCode: String = "",
    val address: String = "",
    val pin: String = "",
    val phone: String = "",
    val pan: String = "",
    val date: String = "",
    val tokenNo: String = "",
    val serialNo: String = "",
    val lotNo: String = "",
    val cashCode: String = "CASH", // "CASH" or "CODE"
    val status: String = "PAID", // "PAID", "PROMISED"
    val promiseAmount: Double = 0.0,
    val dueAmount: Double = 0.0,
    val remarks: String = "",
    val createdByUserId: Int = 0,
    val createdByUserName: String = ""
) {
    val totalAmount: Double
        get() = swastyayani + istabhriti + acharyabhriti + dakshina + sangathani + ritwicki + pronami + anandabazar + srimandirCorpus + parivrity + utsav
}

@Entity(tableName = "skpnn_entries")
data class SkpnnEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val address: String = "",
    val mobile: String = "",
    val pinCode: String = "",
    val familyCode: String = "",
    val pravrityAmount: Double = 0.0,
    val miscAmount: Double = 0.0, // System will write S.K.P.N.N descriptions
    val date: String = "",
    val serialNo: String = "",
    val lotNo: String = "",
    val status: String = "PAID", // "PAID", "PROMISED"
    val promiseAmount: Double = 0.0,
    val dueAmount: Double = 0.0,
    val remarks: String = "",
    val createdByUserId: Int = 0,
    val createdByUserName: String = ""
) {
    val totalAmount: Double
        get() = pravrityAmount + miscAmount
}

@Entity(tableName = "expense_entries")
data class ExpenseEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expenseType: String, // "Labour Payment", "Material Purchase", "Transport", "Decoration", "Other Expenses"
    val amount: Double,
    val personName: String,
    val date: String,
    val remarks: String = ""
)

@Entity(tableName = "mandhirs")
data class Mandhir(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String = "",
    val sakhaLinking: String = ""
)

@Entity(tableName = "donation_entries")
data class DonationEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mandhirId: Int, // Refers to the Mandhir id
    val mandhirName: String = "",
    val personName: String,
    val amount: Double,
    val date: String,
    val remarks: String = ""
)

@Entity(tableName = "upajonas")
data class Upajona(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String = "",
    val mobile: String = "",
    val location: String = "" // Location Coordinates or Link
)
