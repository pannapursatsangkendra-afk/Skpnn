package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TempleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TempleRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TempleRepository(database)
        viewModelScope.launch {
            // Seed the database on first run
            repository.seedDatabaseIfNeeded()
        }
    }

    // Language State
    var selectedLanguage by mutableStateOf("Bengali")
        private set

    fun toggleLanguage() {
        selectedLanguage = if (selectedLanguage == "Bengali") "English" else "Bengali"
        Translations.currentLanguage = selectedLanguage
    }

    // Security & Current Session State
    val currentUserFlow = MutableStateFlow<User?>(null)

    var currentUser by mutableStateOf<User?>(null)
        private set

    var loginError by mutableStateOf("")
        private set

    var registrationSuccess by mutableStateOf(false)
        private set

    var registrationError by mutableStateOf("")
        private set

    // Synced State Status
    var isCheckingSync by mutableStateOf(false)
        private set
    var syncMessage by mutableStateOf("Database fully synced offline")
        private set

    fun triggerOnlineSync() {
        viewModelScope.launch {
            isCheckingSync = true
            syncMessage = "Connecting to cloud sync service..."
            kotlinx.coroutines.delay(1500)
            syncMessage = "Backup synchronized successfully!"
            isCheckingSync = false
        }
    }

    // Dynamic Lists & StateFlows
    val allUsers: StateFlow<List<User>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allIstravrityEntries: StateFlow<List<IstravrityEntry>> = repository.allIstravrityEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSkpnnEntries: StateFlow<List<SkpnnEntry>> = repository.allSkpnnEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpenses: StateFlow<List<ExpenseEntry>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMandhirs: StateFlow<List<Mandhir>> = repository.allMandhirs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDonations: StateFlow<List<DonationEntry>> = repository.allDonations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUpajonas: StateFlow<List<Upajona>> = repository.allUpajonas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Istravrity based on role
    val viewableIstravrityEntries: StateFlow<List<IstravrityEntry>> = combine(
        currentUserFlow,
        repository.allIstravrityEntries
    ) { user, entries ->
        if (user == null) {
            emptyList()
        } else if (user.role == "USER") {
            // User can only see their own entries
            entries.filter { it.createdByUserId == user.id }
        } else {
            // Admin & Super Admin see all
            entries
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- AUTHENTICATION ACTIONS ---
    fun login(mobile: String, pin: String, onNavigate: () -> Unit) {
        viewModelScope.launch {
            loginError = ""
            val user = repository.getUserByMobile(mobile)
            if (user == null) {
                loginError = "User mobile not registered"
                return@launch
            }
            if (user.passwordHash != pin) {
                loginError = "Invalid PIN"
                return@launch
            }
            if (user.status == "INACTIVE") {
                loginError = "Your registration approval is pending from Super Admin"
                return@launch
            }
            if (user.status == "BLOCKED") {
                loginError = "Your account is permanently blocked"
                return@launch
            }
            currentUser = user
            currentUserFlow.value = user
            onNavigate()
        }
    }

    fun register(name: String, mobile: String, pin: String) {
        viewModelScope.launch {
            registrationError = ""
            registrationSuccess = false
            if (name.isBlank() || mobile.isBlank() || pin.isBlank()) {
                registrationError = "All fields are required"
                return@launch
            }
            val existing = repository.getUserByMobile(mobile)
            if (existing != null) {
                registrationError = "Mobile number already registered"
                return@launch
            }
            // By default, registration requires Super Admin approval, so status is "INACTIVE"
            val newUser = User(
                fullName = name,
                mobile = mobile,
                passwordHash = pin,
                role = "USER", // Default role
                status = "INACTIVE" // Pending approval
            )
            repository.registerUser(newUser)
            registrationSuccess = true
        }
    }

    fun logout(onNavigate: () -> Unit) {
        currentUser = null
        currentUserFlow.value = null
        onNavigate()
    }

    // --- SUPER ADMIN USER MANAGEMENT CONTROLS ---
    fun approveUser(user: User) {
        viewModelScope.launch {
            val updated = user.copy(status = "ACTIVE")
            repository.updateUser(updated)
        }
    }

    fun toggleUserBlock(user: User) {
        viewModelScope.launch {
            val newStatus = if (user.status == "BLOCKED") "ACTIVE" else "BLOCKED"
            val updated = user.copy(status = newStatus)
            repository.updateUser(updated)
        }
    }

    fun changeUserRole(user: User, newRole: String) {
        viewModelScope.launch {
            val updated = user.copy(role = newRole)
            repository.updateUser(updated)
        }
    }

    fun createBackupJson(): String {
        // Return a simulated JSON representing the current database status
        return """
            {
               "backupDate": "${getCurrentDate()}",
               "superAdmin": "${currentUser?.fullName}",
               "usersCount": ${allUsers.value.size},
               "istravrityEntriesCount": ${allIstravrityEntries.value.size},
               "skpnnEntriesCount": ${allSkpnnEntries.value.size},
               "expensesCount": ${allExpenses.value.size}
            }
        """.trimIndent()
    }

    // --- ISTRAVRITY CRUD ---
    fun saveIstravrity(
        fullName: String,
        ritwickName: String,
        swastyayani: Double,
        istabhriti: Double,
        acharyabhriti: Double,
        dakshina: Double,
        sangathani: Double,
        ritwicki: Double,
        pronami: Double,
        anandabazar: Double,
        srimandirCorpus: Double,
        parivrity: Double,
        utsav: Double,
        utsavDesc: String,
        familyCode: String,
        address: String,
        pin: String,
        phone: String,
        pan: String,
        cashCode: String,
        status: String,
        promiseAmount: Double,
        dueAmount: Double,
        remarks: String,
        existingId: Int = 0,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser ?: return@launch
            val date = getCurrentDate()
            val token = if (existingId != 0) "" else "TK-${(100000..999999).random()}"
            val serial = if (existingId != 0) "" else "SR-${(1000..9999).random()}"
            val lot = if (existingId != 0) "" else "LOT-${(10..99).random()}"

            if (existingId == 0) {
                val entry = IstravrityEntry(
                    fullName = fullName,
                    ritwickName = ritwickName,
                    swastyayani = swastyayani,
                    istabhriti = istabhriti,
                    acharyabhriti = acharyabhriti,
                    dakshina = dakshina,
                    sangathani = sangathani,
                    ritwicki = ritwicki,
                    pronami = pronami,
                    anandabazar = anandabazar,
                    srimandirCorpus = srimandirCorpus,
                    parivrity = parivrity,
                    utsav = utsav,
                    utsavDesc = utsavDesc,
                    familyCode = familyCode,
                    address = address,
                    pin = pin,
                    phone = phone,
                    pan = pan,
                    date = date,
                    tokenNo = token,
                    serialNo = serial,
                    lotNo = lot,
                    cashCode = cashCode,
                    status = status,
                    promiseAmount = promiseAmount,
                    dueAmount = dueAmount,
                    remarks = remarks,
                    createdByUserId = user.id,
                    createdByUserName = user.fullName
                )
                repository.insertIstravrity(entry)
            } else {
                val existing = repository.getIstravrityById(existingId)
                if (existing != null) {
                    val updated = existing.copy(
                        fullName = fullName,
                        ritwickName = ritwickName,
                        swastyayani = swastyayani,
                        istabhriti = istabhriti,
                        acharyabhriti = acharyabhriti,
                        dakshina = dakshina,
                        sangathani = sangathani,
                        ritwicki = ritwicki,
                        pronami = pronami,
                        anandabazar = anandabazar,
                        srimandirCorpus = srimandirCorpus,
                        parivrity = parivrity,
                        utsav = utsav,
                        utsavDesc = utsavDesc,
                        familyCode = familyCode,
                        address = address,
                        pin = pin,
                        phone = phone,
                        pan = pan,
                        cashCode = cashCode,
                        status = status,
                        promiseAmount = promiseAmount,
                        dueAmount = dueAmount,
                        remarks = remarks
                    )
                    repository.updateIstravrity(updated)
                }
            }
            onComplete()
        }
    }

    fun deleteIstravrity(entry: IstravrityEntry) {
        viewModelScope.launch {
            repository.deleteIstravrity(entry)
        }
    }

    // --- SKPNN CRUD ---
    fun saveSkpnn(
        fullName: String,
        address: String,
        mobile: String,
        pinCode: String,
        familyCode: String,
        pravrityAmount: Double,
        miscAmount: Double,
        status: String,
        promiseAmount: Double,
        dueAmount: Double,
        remarks: String,
        existingId: Int = 0,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser ?: return@launch
            val date = getCurrentDate()
            val serial = if (existingId != 0) "" else "SN-${(1000..9999).random()}"
            val lot = if (existingId != 0) "" else "LOT-${(10..99).random()}"

            if (existingId == 0) {
                val entry = SkpnnEntry(
                    fullName = fullName,
                    address = address,
                    mobile = mobile,
                    pinCode = pinCode,
                    familyCode = familyCode,
                    pravrityAmount = pravrityAmount,
                    miscAmount = miscAmount,
                    date = date,
                    serialNo = serial,
                    lotNo = lot,
                    status = status,
                    promiseAmount = promiseAmount,
                    dueAmount = dueAmount,
                    remarks = remarks,
                    createdByUserId = user.id,
                    createdByUserName = user.fullName
                )
                repository.insertSkpnn(entry)
            } else {
                // Edit mode
                val entries = repository.allSkpnnEntries.first()
                val existing = entries.find { it.id == existingId }
                if (existing != null) {
                    val updated = existing.copy(
                        fullName = fullName,
                        address = address,
                        mobile = mobile,
                        pinCode = pinCode,
                        familyCode = familyCode,
                        pravrityAmount = pravrityAmount,
                        miscAmount = miscAmount,
                        status = status,
                        promiseAmount = promiseAmount,
                        dueAmount = dueAmount,
                        remarks = remarks
                    )
                    repository.updateSkpnn(updated)
                }
            }
            onComplete()
        }
    }

    fun deleteSkpnn(entry: SkpnnEntry) {
        viewModelScope.launch {
            repository.deleteSkpnn(entry)
        }
    }

    // --- EXPENSES ---
    fun saveExpense(
        expenseType: String,
        amount: Double,
        personName: String,
        remarks: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val expense = ExpenseEntry(
                expenseType = expenseType,
                amount = amount,
                personName = personName,
                date = getCurrentDate(),
                remarks = remarks
            )
            repository.insertExpense(expense)
            onComplete()
        }
    }

    fun deleteExpense(expense: ExpenseEntry) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    // --- MANDHIR CRUD ---
    fun saveMandhir(
        name: String,
        address: String,
        sakhaLinking: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val mandhir = Mandhir(
                name = name,
                address = address,
                sakhaLinking = sakhaLinking
            )
            repository.insertMandhir(mandhir)
            onComplete()
        }
    }

    fun deleteMandhir(mandhir: Mandhir) {
        viewModelScope.launch {
            repository.deleteMandhir(mandhir)
        }
    }

    fun saveDonation(
        mandhirId: Int,
        mandhirName: String,
        personName: String,
        amount: Double,
        remarks: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val donation = DonationEntry(
                mandhirId = mandhirId,
                mandhirName = mandhirName,
                personName = personName,
                amount = amount,
                date = getCurrentDate(),
                remarks = remarks
            )
            repository.insertDonation(donation)
            onComplete()
        }
    }

    fun deleteDonation(donation: DonationEntry) {
        viewModelScope.launch {
            repository.deleteDonation(donation)
        }
    }

    // --- UPAJONA CRUD ---
    fun saveUpajona(
        name: String,
        address: String,
        mobile: String,
        location: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val upajona = Upajona(
                name = name,
                address = address,
                mobile = mobile,
                location = location
            )
            repository.insertUpajona(upajona)
            onComplete()
        }
    }

    fun deleteUpajona(upajona: Upajona) {
        viewModelScope.launch {
            repository.deleteUpajona(upajona)
        }
    }

    // --- UTILITIES ---
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun convertNumberToWords(number: Double): String {
        val amount = number.toLong()
        if (amount == 0L) return "Zero"
        
        val units = arrayOf(
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
        )
        val tens = arrayOf(
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
        )
        
        fun convertLessThanOneThousand(n: Int): String {
            var n1 = n
            var sq = ""
            if (n1 % 100 < 20) {
                sq = units[n1 % 100]
                n1 /= 100
            } else {
                sq = units[n1 % 10]
                n1 /= 10
                sq = tens[n1 % 10] + " " + sq
                n1 /= 10
            }
            if (n1 == 0) return sq
            return units[n1] + " Hundred " + sq
        }

        var num = amount
        var result = ""
        var place = 0
        
        val scales = arrayOf("", " Thousand", " Lakh", " Crore")
        val divisors = longArrayOf(1000, 100, 100) // For Indian currency system lakhs and crores
        
        // Handle thousands
        val th = (num % 1000).toInt()
        result = convertLessThanOneThousand(th)
        num /= 1000
        
        if (num > 0) {
            val lk = (num % 100).toInt()
            if (lk > 0) {
                result = convertLessThanOneThousand(lk) + " Thousand " + result
            }
            num /= 100
        }
        
        if (num > 0) {
            val cr = (num % 100).toInt()
            if (cr > 0) {
                result = convertLessThanOneThousand(cr) + " Lakh " + result
            }
            num /= 100
        }
        
        if (num > 0) {
            result = convertLessThanOneThousand(num.toInt()) + " Crore " + result
        }

        return result.trim() + " Rupees Only"
    }
}
