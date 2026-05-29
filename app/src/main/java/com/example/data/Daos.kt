package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE mobile = :mobile LIMIT 1")
    suspend fun getUserByMobile(mobile: String): User?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}

@Dao
interface IstravrityDao {
    @Query("SELECT * FROM istravrity_entries ORDER BY id DESC")
    fun getAllEntries(): Flow<List<IstravrityEntry>>

    @Query("SELECT * FROM istravrity_entries WHERE createdByUserId = :userId ORDER BY id DESC")
    fun getOwnEntries(userId: Int): Flow<List<IstravrityEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: IstravrityEntry): Long

    @Update
    suspend fun updateEntry(entry: IstravrityEntry)

    @Delete
    suspend fun deleteEntry(entry: IstravrityEntry)

    @Query("SELECT COUNT(*) FROM istravrity_entries")
    suspend fun getEntryCount(): Int

    @Query("SELECT * FROM istravrity_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: Int): IstravrityEntry?
}

@Dao
interface SkpnnDao {
    @Query("SELECT * FROM skpnn_entries ORDER BY id DESC")
    fun getAllEntries(): Flow<List<SkpnnEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: SkpnnEntry): Long

    @Update
    suspend fun updateEntry(entry: SkpnnEntry)

    @Delete
    suspend fun deleteEntry(entry: SkpnnEntry)
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense_entries ORDER BY id DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntry): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntry)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntry)
}

@Dao
interface MandhirDao {
    @Query("SELECT * FROM mandhirs ORDER BY name ASC")
    fun getAllMandhirs(): Flow<List<Mandhir>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMandhir(mandhir: Mandhir): Long

    @Update
    suspend fun updateMandhir(mandhir: Mandhir)

    @Delete
    suspend fun deleteMandhir(mandhir: Mandhir)

    // Donations
    @Query("SELECT * FROM donation_entries ORDER BY id DESC")
    fun getAllDonations(): Flow<List<DonationEntry>>

    @Query("SELECT * FROM donation_entries WHERE mandhirId = :mandhirId ORDER BY id DESC")
    fun getDonationsByMandhir(mandhirId: Int): Flow<List<DonationEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonation(donation: DonationEntry): Long

    @Update
    suspend fun updateDonation(donation: DonationEntry)

    @Delete
    suspend fun deleteDonation(donation: DonationEntry)
}

@Dao
interface UpajonaDao {
    @Query("SELECT * FROM upajonas ORDER BY name ASC")
    fun getAllUpajonas(): Flow<List<Upajona>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpajona(upajona: Upajona): Long

    @Update
    suspend fun updateUpajona(upajona: Upajona)

    @Delete
    suspend fun deleteUpajona(upajona: Upajona)
}
