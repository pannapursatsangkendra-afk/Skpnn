package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TempleRepository(private val db: AppDatabase) {

    val allUsers: Flow<List<User>> = db.userDao().getAllUsers()
    val allIstravrityEntries: Flow<List<IstravrityEntry>> = db.istravrityDao().getAllEntries()
    val allSkpnnEntries: Flow<List<SkpnnEntry>> = db.skpnnDao().getAllEntries()
    val allExpenses: Flow<List<ExpenseEntry>> = db.expenseDao().getAllExpenses()
    val allMandhirs: Flow<List<Mandhir>> = db.mandhirDao().getAllMandhirs()
    val allDonations: Flow<List<DonationEntry>> = db.mandhirDao().getAllDonations()
    val allUpajonas: Flow<List<Upajona>> = db.upajonaDao().getAllUpajonas()

    fun getOwnIstravrityEntries(userId: Int): Flow<List<IstravrityEntry>> =
        db.istravrityDao().getOwnEntries(userId)

    suspend fun getUserByMobile(mobile: String): User? = withContext(Dispatchers.IO) {
        db.userDao().getUserByMobile(mobile)
    }

    suspend fun registerUser(user: User): Long = withContext(Dispatchers.IO) {
        db.userDao().registerUser(user)
    }

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        db.userDao().updateUser(user)
    }

    suspend fun deleteUser(user: User) = withContext(Dispatchers.IO) {
        db.userDao().deleteUser(user)
    }

    // Istravrity
    suspend fun insertIstravrity(entry: IstravrityEntry): Long = withContext(Dispatchers.IO) {
        db.istravrityDao().insertEntry(entry)
    }

    suspend fun updateIstravrity(entry: IstravrityEntry) = withContext(Dispatchers.IO) {
        db.istravrityDao().updateEntry(entry)
    }

    suspend fun deleteIstravrity(entry: IstravrityEntry) = withContext(Dispatchers.IO) {
        db.istravrityDao().deleteEntry(entry)
    }

    suspend fun getIstravrityCount(): Int = withContext(Dispatchers.IO) {
        db.istravrityDao().getEntryCount()
    }

    suspend fun getIstravrityById(id: Int): IstravrityEntry? = withContext(Dispatchers.IO) {
        db.istravrityDao().getEntryById(id)
    }

    // SKPNN
    suspend fun insertSkpnn(entry: SkpnnEntry): Long = withContext(Dispatchers.IO) {
        db.skpnnDao().insertEntry(entry)
    }

    suspend fun updateSkpnn(entry: SkpnnEntry) = withContext(Dispatchers.IO) {
        db.skpnnDao().updateEntry(entry)
    }

    suspend fun deleteSkpnn(entry: SkpnnEntry) = withContext(Dispatchers.IO) {
        db.skpnnDao().deleteEntry(entry)
    }

    // Expenses
    suspend fun insertExpense(expense: ExpenseEntry): Long = withContext(Dispatchers.IO) {
        db.expenseDao().insertExpense(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntry) = withContext(Dispatchers.IO) {
        db.expenseDao().updateExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntry) = withContext(Dispatchers.IO) {
        db.expenseDao().deleteExpense(expense)
    }

    // Mandhir
    suspend fun insertMandhir(mandhir: Mandhir): Long = withContext(Dispatchers.IO) {
        db.mandhirDao().insertMandhir(mandhir)
    }

    suspend fun updateMandhir(mandhir: Mandhir) = withContext(Dispatchers.IO) {
        db.mandhirDao().updateMandhir(mandhir)
    }

    suspend fun deleteMandhir(mandhir: Mandhir) = withContext(Dispatchers.IO) {
        db.mandhirDao().deleteMandhir(mandhir)
    }

    // Donations
    suspend fun insertDonation(donation: DonationEntry): Long = withContext(Dispatchers.IO) {
        db.mandhirDao().insertDonation(donation)
    }

    suspend fun updateDonation(donation: DonationEntry) = withContext(Dispatchers.IO) {
        db.mandhirDao().updateDonation(donation)
    }

    suspend fun deleteDonation(donation: DonationEntry) = withContext(Dispatchers.IO) {
        db.mandhirDao().deleteDonation(donation)
    }

    // Upajona
    suspend fun insertUpajona(upajona: Upajona): Long = withContext(Dispatchers.IO) {
        db.upajonaDao().insertUpajona(upajona)
    }

    suspend fun updateUpajona(upajona: Upajona) = withContext(Dispatchers.IO) {
        db.upajonaDao().updateUpajona(upajona)
    }

    suspend fun deleteUpajona(upajona: Upajona) = withContext(Dispatchers.IO) {
        db.upajonaDao().deleteUpajona(upajona)
    }

    // Seed Data if DB is empty
    suspend fun seedDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        val userCount = db.userDao().getUserCount()
        if (userCount == 0) {
            // Register default Super Admin
            db.userDao().registerUser(
                User(
                    fullName = "Pannapur Satsang Kendra Admin",
                    mobile = "1234567890",
                    passwordHash = "admin123", // Simple plain or hashed
                    role = "SUPER_ADMIN",
                    status = "ACTIVE"
                )
            )

            // Seed normal admin and normal user
            db.userDao().registerUser(
                User(
                    fullName = "Debasish Bhattacharya",
                    mobile = "9876543210",
                    passwordHash = "admin123",
                    role = "ADMIN",
                    status = "ACTIVE"
                )
            )

            db.userDao().registerUser(
                User(
                    fullName = "Pranab Roy",
                    mobile = "5555555555",
                    passwordHash = "user123",
                    role = "USER",
                    status = "ACTIVE"
                )
            )

            // Seed initial Mandhirs
            db.mandhirDao().insertMandhir(
                Mandhir(
                    name = "Pannapur Satsang Temple",
                    address = "Pannapur Village, West Bengal",
                    sakhaLinking = "Satsang Deoghar Sakha 104"
                )
            )
            db.mandhirDao().insertMandhir(
                Mandhir(
                    name = "Moyna Mandhir Vihar",
                    address = "Purba Medinipur, West Bengal",
                    sakhaLinking = "Satsang Deoghar Sakha 210"
                )
            )

            // Seed initial Upajonas
            db.upajonaDao().insertUpajona(
                Upajona(
                    name = "Pannapur Upajona",
                    address = "Pannapur, West Bengal",
                    mobile = "9876543210",
                    location = "https://maps.google.com/?q=22.4,87.8"
                )
            )
            db.upajonaDao().insertUpajona(
                Upajona(
                    name = "Contai Upajona Vihar",
                    address = "Contai Town, West Bengal",
                    mobile = "9432134567",
                    location = "https://maps.google.com/?q=21.78,87.75"
                )
            )

            // Seed demo expenses to make graphs look nice
            db.expenseDao().insertExpense(
                ExpenseEntry(
                    expenseType = "Material Purchase",
                    amount = 12500.0,
                    personName = "Anindya Sen",
                    date = "2026-05-15",
                    remarks = "Purchased bricks and cement for temple expansion"
                )
            )
            db.expenseDao().insertExpense(
                ExpenseEntry(
                    expenseType = "Labour Payment",
                    amount = 4800.0,
                    personName = "Gobinda Patra",
                    date = "2026-05-18",
                    remarks = "Construction labor payment"
                )
            )
            db.expenseDao().insertExpense(
                ExpenseEntry(
                    expenseType = "Transport",
                    amount = 2300.0,
                    personName = "Lorry Association",
                    date = "2026-05-20",
                    remarks = "Carrying sand and gravel"
                )
            )
            db.expenseDao().insertExpense(
                ExpenseEntry(
                    expenseType = "Decoration",
                    amount = 6500.0,
                    personName = "Star Decorators",
                    date = "2026-05-25",
                    remarks = "Lights and stage for Utsav"
                )
            )

            // Seed initial donations
            db.mandhirDao().insertDonation(
                DonationEntry(
                    mandhirId = 1,
                    mandhirName = "Pannapur Satsang Temple",
                    personName = "Sujit Kumar Das",
                    amount = 5000.0,
                    date = "2026-05-12",
                    remarks = "General Temple fund donation"
                )
            )
            db.mandhirDao().insertDonation(
                DonationEntry(
                    mandhirId = 1,
                    mandhirName = "Pannapur Satsang Temple",
                    personName = "Minati Banerjee",
                    amount = 2500.0,
                    date = "2026-05-22",
                    remarks = "Anandabazar contribution"
                )
            )

            // Seed some SKPNN entries
            db.skpnnDao().insertEntry(
                SkpnnEntry(
                    fullName = "Gouranga Chandra Mandal",
                    address = "Pannapur",
                    mobile = "9832049583",
                    pinCode = "721644",
                    familyCode = "FAM-903",
                    pravrityAmount = 200.0,
                    miscAmount = 50.0,
                    date = "2026-05-10",
                    serialNo = "SN-1025",
                    lotNo = "LOT-04",
                    status = "PAID"
                )
            )
            db.skpnnDao().insertEntry(
                SkpnnEntry(
                    fullName = "Haripada Adhikari",
                    address = "Moyna",
                    mobile = "9933456789",
                    pinCode = "721629",
                    familyCode = "FAM-344",
                    pravrityAmount = 150.0,
                    miscAmount = 0.0,
                    date = "2026-05-14",
                    serialNo = "SN-1026",
                    lotNo = "LOT-04",
                    status = "PROMISED",
                    promiseAmount = 150.0,
                    dueAmount = 150.0
                )
            )

            // Seed some Istravrity Entries
            db.istravrityDao().insertEntry(
                IstravrityEntry(
                    fullName = "Protap Roy",
                    ritwickName = "Pratap Adhikari da",
                    swastyayani = 100.0,
                    istabhriti = 250.0,
                    acharyabhriti = 50.0,
                    dakshina = 30.0,
                    sangathani = 20.0,
                    ritwicki = 50.0,
                    pronami = 100.0,
                    anandabazar = 50.0,
                    srimandirCorpus = 40.0,
                    parivrity = 10.0,
                    utsav = 100.0,
                    utsavDesc = "Anukul Chandra Janmautsav",
                    familyCode = "FAM-512",
                    address = "Pannapur Block A",
                    pin = "721644",
                    phone = "9876543210",
                    pan = "ABCDE1234F",
                    date = "2026-05-28",
                    tokenNo = "TK-98213",
                    serialNo = "SN-5089",
                    lotNo = "LOT-03",
                    cashCode = "CASH",
                    status = "PAID"
                )
            )
            db.istravrityDao().insertEntry(
                IstravrityEntry(
                    fullName = "Nirmala Devi",
                    ritwickName = "Pratap Adhikari da",
                    swastyayani = 50.0,
                    istabhriti = 100.0,
                    acharyabhriti = 20.0,
                    dakshina = 10.0,
                    sangathani = 10.0,
                    ritwicki = 20.0,
                    pronami = 50.0,
                    anandabazar = 30.0,
                    srimandirCorpus = 20.0,
                    parivrity = 10.0,
                    utsav = 50.0,
                    familyCode = "FAM-512",
                    address = "Pannapur Block A",
                    pin = "721644",
                    phone = "9876543210",
                    date = "2026-05-28",
                    tokenNo = "TK-98214",
                    serialNo = "SN-5090",
                    lotNo = "LOT-03",
                    cashCode = "CASH",
                    status = "PAID"
                )
            )
        }
    }
}
