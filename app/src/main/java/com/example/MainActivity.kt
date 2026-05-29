package com.example

import java.util.Locale
import androidx.compose.ui.text.TextStyle
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.data.*
import com.example.ui.TempleViewModel
import com.example.ui.Translations
import com.example.ui.theme.*

val LightGreen = Color(0xFF81C784)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: TempleViewModel = viewModel()
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("login") { LoginScreen(navController, viewModel) }
                    composable("register") { RegisterScreen(navController, viewModel) }
                    composable("dashboard") { DashboardScreen(navController, viewModel) }
                    composable("istravrity") { IstravrityScreen(navController, viewModel) }
                    composable("skpnn") { SkpnnScreen(navController, viewModel) }
                    composable("mandhir") { MandhirScreen(navController, viewModel) }
                    composable("expenses") { ExpensesScreen(navController, viewModel) }
                    composable("upajona") { UpajonaScreen(navController, viewModel) }
                    composable("user_control") { UserControlScreen(navController, viewModel) }
                }
            }
        }
    }
}

// --- COMMON COMPOSABLES & LAYOUT HELPERS ---

@Composable
fun AppHeader(
    titleKey: String,
    viewModel: TempleViewModel,
    onBack: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    val syncMsg = viewModel.syncMessage
    val isSyncing = viewModel.isCheckingSync

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = Translations.get(titleKey),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                // Language Toggle
                Button(
                    onClick = { viewModel.toggleLanguage() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .height(32.dp)
                        .testTag("lang_toggle")
                ) {
                    Text(
                        text = if (viewModel.selectedLanguage == "Bengali") "English" else "বাংলা",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                actions?.invoke(this)
            }

            // Sync System Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp)
                    .background(Color(0xFF222222), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isSyncing) Icons.Default.Sync else Icons.Default.CloudQueue,
                        contentDescription = "Sync state",
                        tint = if (isSyncing) MaterialTheme.colorScheme.secondary else LightGreen,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = syncMsg,
                        fontSize = 10.sp,
                        color = Color.LightGray
                    )
                }

                Text(
                    text = "Sync Now",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .clickable { viewModel.triggerOnlineSync() }
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AppNavigationDrawer(
    currentRoute: String,
    navController: NavController,
    user: User?,
    onLogout: () -> Unit
) {
    if (user == null) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = user.fullName,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${Translations.get("role")}: ${Translations.get(user.role.lowercase())}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = onLogout, modifier = Modifier.testTag("logout_btn")) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Log Out",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation Buttons Row (Scrollable horizontally for compactness)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NavigationItem(
                    label = "dashboard",
                    icon = Icons.Default.Dashboard,
                    active = currentRoute == "dashboard",
                    onClick = { navController.navigate("dashboard") }
                )
                NavigationItem(
                    label = "monthly_istravrity",
                    icon = Icons.Default.Receipt,
                    active = currentRoute == "istravrity",
                    onClick = { navController.navigate("istravrity") }
                )
                if (user.role != "USER") {
                    NavigationItem(
                        label = "skpnn",
                        icon = Icons.Default.Payments,
                        active = currentRoute == "skpnn",
                        onClick = { navController.navigate("skpnn") }
                    )
                }
                NavigationItem(
                    label = "mandhir",
                    icon = Icons.Default.Store,
                    active = currentRoute == "mandhir",
                    onClick = { navController.navigate("mandhir") }
                )
                if (user.role != "USER") {
                    NavigationItem(
                        label = "expenses",
                        icon = Icons.Default.AccountBalance,
                        active = currentRoute == "expenses",
                        onClick = { navController.navigate("expenses") }
                    )
                }
                NavigationItem(
                    label = "upajona",
                    icon = Icons.Default.Map,
                    active = currentRoute == "upajona",
                    onClick = { navController.navigate("upajona") }
                )
                if (user.role == "SUPER_ADMIN") {
                    NavigationItem(
                        label = "user_control",
                        icon = Icons.Default.SupervisorAccount,
                        active = currentRoute == "user_control",
                        onClick = { navController.navigate("user_control") }
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationItem(
    label: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (active) MaterialTheme.colorScheme.primary else Color(0xFF2E2E2E),
            contentColor = if (active) MaterialTheme.colorScheme.onPrimary else Color.White
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier
            .height(34.dp)
            .testTag("nav_item_$label")
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = Translations.get(label), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

// --- (1) LOGIN SCREEN ---

@Composable
fun LoginScreen(navController: NavController, viewModel: TempleViewModel) {
    var mobile by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A0F2A), DarkBackground)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // High Contrast Devotional Header Image Simulation
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = "Temple logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = Translations.get("app_title"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = Translations.get("login_title"),
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 6.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = mobile,
                            onValueChange = { mobile = it },
                            label = { Text(Translations.get("mobile")) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_mobile_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = pin,
                            onValueChange = { pin = it },
                            label = { Text(Translations.get("password") + " / PIN") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_pin_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        if (viewModel.loginError.isNotEmpty()) {
                            Text(
                                text = viewModel.loginError,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                viewModel.login(mobile, pin) {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(Translations.get("login_btn"), fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = Translations.get("dont_have_account"),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clickable { navController.navigate("register") }
                        .padding(8.dp)
                )

                // Demo accounts tip
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131313)),
                    border = BorderStroke(1.dp, Color(0xFF222222))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "💡 Super Admin Login: Mobile 1234567890 | PIN admin123",
                            fontSize = 11.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// --- (2) REGISTRATION SCREEN ---

@Composable
fun RegisterScreen(navController: NavController, viewModel: TempleViewModel) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A0F2A), DarkBackground)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = Translations.get("register_title"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(Translations.get("full_name")) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = mobile,
                            onValueChange = { mobile = it },
                            label = { Text(Translations.get("mobile")) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_mobile_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = pin,
                            onValueChange = { pin = it },
                            label = { Text(Translations.get("password") + " / PIN") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_pin_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        if (viewModel.registrationError.isNotEmpty()) {
                            Text(
                                text = viewModel.registrationError,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        if (viewModel.registrationSuccess) {
                            Text(
                                text = Translations.get("pending_approvals") + "! Super Admin approval required.",
                                color = LightGreen,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.register(name, mobile, pin) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("reg_submit_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(Translations.get("register_btn"), fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = Translations.get("already_have_account"),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clickable { navController.navigate("login") }
                        .padding(8.dp)
                )
            }
        }
    }
}

// --- (3) DASHBOARD SCREEN ---

@Composable
fun DashboardScreen(navController: NavController, viewModel: TempleViewModel) {
    val user = viewModel.currentUser ?: return
    val mandhirs by viewModel.allMandhirs.collectAsState()
    val donations by viewModel.allDonations.collectAsState()
    val skpnnEntries by viewModel.allSkpnnEntries.collectAsState()
    val expenses by viewModel.allExpenses.collectAsState()

    // Analytical sums
    val totalMandhirCollection = donations.sumOf { it.amount }
    val totalExpenseSummary = expenses.sumOf { it.amount }
    
    // SKPNN sums
    val totalSkpnnCollection = skpnnEntries.sumOf { it.pravrityAmount + it.miscAmount }
    val skpnnPaidSummary = skpnnEntries.filter { it.status == "PAID" }.sumOf { it.pravrityAmount + it.miscAmount }
    val skpnnPromiseSummary = skpnnEntries.filter { it.status == "PROMISED" }.sumOf { it.promiseAmount }
    val skpnnDueSummary = skpnnEntries.sumOf { it.dueAmount }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            AppHeader(titleKey = "dashboard", viewModel = viewModel)

            AppNavigationDrawer(
                currentRoute = "dashboard",
                navController = navController,
                user = user,
                onLogout = { viewModel.logout { navController.navigate("login") } }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. MANDHIR SECTION REPORT ON DASHBOARD
                item {
                    SectionHeader(titleKey = "mandhir_details", icon = Icons.Default.Store)
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            DashboardStatRow(
                                title = Translations.get("total_collection"),
                                value = "Rs. ${String.format(Locale.US, "%.2f", totalMandhirCollection)}",
                                color = MaterialTheme.colorScheme.primary
                            )
                            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 10.dp))
                            DashboardStatRow(
                                title = Translations.get("expenses"),
                                value = "Rs. ${String.format(Locale.US, "%.2f", totalExpenseSummary)}",
                                color = MaterialTheme.colorScheme.error
                            )
                            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 10.dp))
                            DashboardStatRow(
                                title = "Registers Established",
                                value = "${mandhirs.size} Mandhirs",
                                color = Color.White
                            )
                        }
                    }
                }

                // 2. SKPNN DETAILS ON DASHBOARD
                item {
                    SectionHeader(titleKey = "skpnn_details", icon = Icons.Default.Payments)
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            DashboardStatRow(
                                title = Translations.get("total_collection"),
                                value = "Rs. ${String.format(Locale.US, "%.1f", totalSkpnnCollection)}",
                                color = MaterialTheme.colorScheme.primary
                            )
                            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 8.dp))
                            DashboardStatRow(
                                title = Translations.get("paid_summary"),
                                value = "Rs. ${String.format(Locale.US, "%.1f", skpnnPaidSummary)}",
                                color = LightGreen
                            )
                            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 8.dp))
                            DashboardStatRow(
                                title = Translations.get("promise_summary"),
                                value = "Rs. ${String.format(Locale.US, "%.1f", skpnnPromiseSummary)}",
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 8.dp))
                            DashboardStatRow(
                                title = Translations.get("due_summary"),
                                value = "Rs. ${String.format(Locale.US, "%.1f", skpnnDueSummary)}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // 3. GRAPH ANALYTICS
                item {
                    SectionHeader(titleKey = "analytics", icon = Icons.Default.TrendingUp)
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Expense Breakdown vs. Temple Contributions",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            // Custom Canvas Graph
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .drawBehind {
                                        // Draw gridlines
                                        val gridRows = 4
                                        val rowHeight = size.height / gridRows
                                        for (i in 0..gridRows) {
                                            drawLine(
                                                color = BorderColor,
                                                start = Offset(0f, i * rowHeight),
                                                end = Offset(size.width, i * rowHeight),
                                                strokeWidth = 1f,
                                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                                            )
                                        }

                                        // Draw sample trends using Saffron and Gold
                                        val points1 = listOf(40f, 65f, 50f, 85f, 110f, 95f) // Collection
                                        val points2 = listOf(20f, 30f, 45f, 25f, 70f, 55f) // Expense

                                        val stepX = size.width / (points1.size - 1)
                                        val maxVal = 120f
                                        val scaleY = size.height / maxVal

                                        // Draw line 1 (collection)
                                        for (i in 0 until points1.size - 1) {
                                            drawLine(
                                                color = SaffronPrimary,
                                                start = Offset(i * stepX, size.height - points1[i] * scaleY),
                                                end = Offset((i + 1) * stepX, size.height - points1[i + 1] * scaleY),
                                                strokeWidth = 4f
                                            )
                                            drawCircle(
                                                color = GoldAccent,
                                                radius = 6f,
                                                center = Offset(i * stepX, size.height - points1[i] * scaleY)
                                            )
                                        }

                                        // Draw line 2 (expense)
                                        for (i in 0 until points2.size - 1) {
                                            drawLine(
                                                color = ErrorRed,
                                                start = Offset(i * stepX, size.height - points2[i] * scaleY),
                                                end = Offset((i + 1) * stepX, size.height - points2[i + 1] * scaleY),
                                                strokeWidth = 3f,
                                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                                            )
                                        }
                                    }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(SaffronPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Income", fontSize = 10.sp, color = Color.Gray)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(ErrorRed)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Expenses", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }

                // Super Admin backup trigger
                if (user.role == "SUPER_ADMIN") {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141F1B)),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🔒 Database Security & Offline Backups",
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "As Super Admin, you can run instant backups of the local SQLite database state and system logs.",
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                val context = LocalContext.current
                                Button(
                                    onClick = {
                                        val backupJson = viewModel.createBackupJson()
                                        Toast.makeText(context, "Backup Generated Successfully!", Toast.LENGTH_LONG).show()
                                        android.util.Log.d("Backup", backupJson)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = Color(0xFF0F0F0F)),
                                    modifier = Modifier.testTag("backup_db_btn")
                                ) {
                                    Text("Generate Backups Logs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(titleKey: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = Translations.get(titleKey),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray
        )
    }
}

@Composable
fun DashboardStatRow(title: String, value: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = title, fontSize = 13.sp, color = Color.LightGray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

// --- (4) MONTHLY ISTRAVRITY SCREEN ---

@Composable
fun IstravrityScreen(navController: NavController, viewModel: TempleViewModel) {
    val user = viewModel.currentUser ?: return
    val entries by viewModel.viewableIstravrityEntries.collectAsState()

    var showFormDialog by remember { mutableStateOf(false) }
    var searchWord by remember { mutableStateOf("") }
    
    // Form Inputs State
    var recordIdToEdit by remember { mutableStateOf(0) }
    var fullName by remember { mutableStateOf("") }
    var ritwickName by remember { mutableStateOf("") }
    var swastyayani by remember { mutableStateOf("0") }
    var istabhriti by remember { mutableStateOf("0") }
    var acharyabhriti by remember { mutableStateOf("0") }
    var dakshina by remember { mutableStateOf("0") }
    var sangathani by remember { mutableStateOf("0") }
    var ritwicki by remember { mutableStateOf("0") }
    var pronami by remember { mutableStateOf("0") }
    var anandabazar by remember { mutableStateOf("0") }
    var srimandirCorpus by remember { mutableStateOf("0") }
    var parivrity by remember { mutableStateOf("0") }
    var utsav by remember { mutableStateOf("0") }
    var utsavDesc by remember { mutableStateOf("") }
    var familyCode by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var pan by remember { mutableStateOf("") }
    var cashCode by remember { mutableStateOf("CASH") }
    var statusState by remember { mutableStateOf("PAID") }
    var promiseAmount by remember { mutableStateOf("0") }
    var dueAmount by remember { mutableStateOf("0") }
    var remarks by remember { mutableStateOf("") }

    // Printable Copy states
    var selectedPrintEntry by remember { mutableStateOf<IstravrityEntry?>(null) }
    var showLotFormPreview by remember { mutableStateOf(false) }

    val filteredList = remember(entries, searchWord) {
        if (searchWord.isBlank()) entries else {
            entries.filter {
                it.fullName.contains(searchWord, ignoreCase = true) ||
                it.familyCode.contains(searchWord, ignoreCase = true) ||
                it.phone.contains(searchWord, ignoreCase = true)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    recordIdToEdit = 0
                    fullName = ""
                    ritwickName = ""
                    swastyayani = "0"
                    istabhriti = "0"
                    acharyabhriti = "0"
                    dakshina = "0"
                    sangathani = "0"
                    ritwicki = "0"
                    pronami = "0"
                    anandabazar = "0"
                    srimandirCorpus = "0"
                    parivrity = "0"
                    utsav = "0"
                    utsavDesc = ""
                    familyCode = ""
                    address = ""
                    pin = ""
                    phone = ""
                    pan = ""
                    cashCode = "CASH"
                    statusState = "PAID"
                    promiseAmount = "0"
                    dueAmount = "0"
                    remarks = ""
                    showFormDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("add_istravrity_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Slip", tint = Color.Black)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            AppHeader(titleKey = "monthly_istravrity", viewModel = viewModel)

            AppNavigationDrawer(
                currentRoute = "istravrity",
                navController = navController,
                user = user,
                onLogout = { viewModel.logout { navController.navigate("login") } }
            )

            // Search Bar & Actions Block
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchWord,
                    onValueChange = { searchWord = it },
                    label = { Text("Search by name/code/mobile") },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("search_field"),
                    textStyle = TextStyle(fontSize = 12.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                // Lot Form Trigger BUTTON
                Button(
                    onClick = { showLotFormPreview = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.height(48.dp).testTag("lot_form_btn")
                ) {
                    Icon(Icons.Default.FactCheck, null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lot Form", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            // Entries List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (user.role != "USER") {
                                    // Edit pre-fill
                                    recordIdToEdit = entry.id
                                    fullName = entry.fullName
                                    ritwickName = entry.ritwickName
                                    swastyayani = entry.swastyayani.toString()
                                    istabhriti = entry.istabhriti.toString()
                                    acharyabhriti = entry.acharyabhriti.toString()
                                    dakshina = entry.dakshina.toString()
                                    sangathani = entry.sangathani.toString()
                                    ritwicki = entry.ritwicki.toString()
                                    pronami = entry.pronami.toString()
                                    anandabazar = entry.anandabazar.toString()
                                    srimandirCorpus = entry.srimandirCorpus.toString()
                                    parivrity = entry.parivrity.toString()
                                    utsav = entry.utsav.toString()
                                    utsavDesc = entry.utsavDesc
                                    familyCode = entry.familyCode
                                    address = entry.address
                                    pin = entry.pin
                                    phone = entry.phone
                                    pan = entry.pan
                                    cashCode = entry.cashCode
                                    statusState = entry.status
                                    promiseAmount = entry.promiseAmount.toString()
                                    dueAmount = entry.dueAmount.toString()
                                    remarks = entry.remarks
                                    showFormDialog = true
                                }
                            }
                            .testTag("istravrity_card_${entry.id}"),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = entry.fullName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Amt: Rs. ${entry.totalAmount}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LightGreen
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Family Code: ${entry.familyCode}", fontSize = 11.sp, color = Color.Gray)
                                Text(text = entry.date, fontSize = 11.sp, color = Color.Gray)
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Status: ${entry.status}", fontSize = 11.sp, color = if(entry.status == "PAID") Color.Green else MaterialTheme.colorScheme.secondary)
                                Text(text = "Srl: ${entry.serialNo}", fontSize = 11.sp, color = Color.LightGray)
                            }

                            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 8.dp))

                            // Interactive actions row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // PDF / PRINT PREVIEW TRIGGER
                                OutlinedButton(
                                    onClick = { selectedPrintEntry = entry },
                                    modifier = Modifier.padding(end = 8.dp).height(30.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.Print, null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("A4 Print Duplicates", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                }

                                if (user.role != "USER") {
                                    IconButton(
                                        onClick = { viewModel.deleteIstravrity(entry) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- FORM DIALOG (Temple Arghya Deposit Slip replica columns) ---
    if (showFormDialog) {
        Dialog(
            onDismissRequest = { showFormDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = CardBackground
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (recordIdToEdit == 0) "NEW ARGHYA DEPOSIT SLIP" else "EDIT ARGHYA DEPOSIT SLIP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { showFormDialog = false }) {
                            Icon(Icons.Default.Close, null, tint = Color.LightGray)
                        }
                    }

                    Divider(color = BorderColor, modifier = Modifier.padding(vertical = 8.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Original structure form headers inputs
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text(Translations.get("full_name") + " / Depositor Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("form_name"),
                            textStyle = TextStyle(fontSize = 12.sp)
                        )

                        OutlinedTextField(
                            value = ritwickName,
                            onValueChange = { ritwickName = it },
                            label = { Text(Translations.get("ritwick_name")) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 12.sp)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = familyCode,
                                onValueChange = { familyCode = it },
                                label = { Text(Translations.get("family_code")) },
                                modifier = Modifier.weight(1f),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                            OutlinedTextField(
                                value = pan,
                                onValueChange = { pan = it },
                                label = { Text(Translations.get("pan_no")) },
                                modifier = Modifier.weight(1f),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                        }

                        // Form Columns
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = swastyayani,
                                onValueChange = { swastyayani = it },
                                label = { Text(Translations.get("swastyayani")) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                            OutlinedTextField(
                                value = istabhriti,
                                onValueChange = { istabhriti = it },
                                label = { Text(Translations.get("istabhriti")) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = acharyabhriti,
                                onValueChange = { acharyabhriti = it },
                                label = { Text(Translations.get("acharyabhriti")) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                            OutlinedTextField(
                                value = dakshina,
                                onValueChange = { dakshina = it },
                                label = { Text(Translations.get("dakshina")) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = sangathani,
                                onValueChange = { sangathani = it },
                                label = { Text(Translations.get("sangathani")) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                            OutlinedTextField(
                                value = ritwicki,
                                onValueChange = { ritwicki = it },
                                label = { Text(Translations.get("ritwicki")) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = pronami,
                                onValueChange = { pronami = it },
                                label = { Text(Translations.get("pronami")) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                            OutlinedTextField(
                                value = anandabazar,
                                onValueChange = { anandabazar = it },
                                label = { Text(Translations.get("anandabazar")) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = srimandirCorpus,
                                onValueChange = { srimandirCorpus = it },
                                label = { Text(Translations.get("srimandir_corpus")) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                            OutlinedTextField(
                                value = parivrity,
                                onValueChange = { parivrity = it },
                                label = { Text(Translations.get("parivrity")) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = utsav,
                                onValueChange = { utsav = it },
                                label = { Text(Translations.get("utsav_amount")) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                            OutlinedTextField(
                                value = utsavDesc,
                                onValueChange = { utsavDesc = it },
                                label = { Text(Translations.get("utsav_desc")) },
                                modifier = Modifier.weight(1f),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                        }

                        Divider(color = Color(0xFF333333))

                        // Additional Address & details mapping
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text(Translations.get("address")) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 12.sp)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = pin,
                                onValueChange = { pin = it },
                                label = { Text(Translations.get("pin_code")) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text(Translations.get("phone_mobile")) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                        }

                        // Cash/Code & Paid state mapping
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Payment Mode:", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                            Row(modifier = Modifier.weight(2f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = { cashCode = "CASH" },
                                    colors = ButtonDefaults.buttonColors(containerColor = if(cashCode == "CASH") MaterialTheme.colorScheme.primary else Color(0xFF2B2B2B)),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("CASH", fontSize = 10.sp, color = if(cashCode == "CASH") Color.Black else Color.White)
                                }
                                Button(
                                    onClick = { cashCode = "CODE" },
                                    colors = ButtonDefaults.buttonColors(containerColor = if(cashCode == "CODE") MaterialTheme.colorScheme.primary else Color(0xFF2B2B2B)),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("CODE / BANK", fontSize = 10.sp, color = if(cashCode == "CODE") Color.Black else Color.White)
                                }
                            }
                        }

                        // Totals calculations displays
                        val parsedVal = (swastyayani.toDoubleOrNull() ?: 0.0) +
                                (istabhriti.toDoubleOrNull() ?: 0.0) +
                                (acharyabhriti.toDoubleOrNull() ?: 0.0) +
                                (dakshina.toDoubleOrNull() ?: 0.0) +
                                (sangathani.toDoubleOrNull() ?: 0.0) +
                                (ritwicki.toDoubleOrNull() ?: 0.0) +
                                (pronami.toDoubleOrNull() ?: 0.0) +
                                (anandabazar.toDoubleOrNull() ?: 0.0) +
                                (srimandirCorpus.toDoubleOrNull() ?: 0.0) +
                                (parivrity.toDoubleOrNull() ?: 0.0) +
                                (utsav.toDoubleOrNull() ?: 0.0)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131313))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Auto Total Sum:", fontSize = 12.sp, color = Color.LightGray)
                                    Text("Rs. $parsedVal", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LightGreen)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Words: ${viewModel.convertNumberToWords(parsedVal)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    maxLines = 2
                                )
                            }
                        }
                    }

                    // Bottom controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showFormDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(Translations.get("cancel"))
                        }
                        Button(
                            onClick = {
                                if (fullName.isBlank()) return@Button
                                viewModel.saveIstravrity(
                                    fullName = fullName,
                                    ritwickName = ritwickName,
                                    swastyayani = swastyayani.toDoubleOrNull() ?: 0.0,
                                    istabhriti = istabhriti.toDoubleOrNull() ?: 0.0,
                                    acharyabhriti = acharyabhriti.toDoubleOrNull() ?: 0.0,
                                    dakshina = dakshina.toDoubleOrNull() ?: 0.0,
                                    sangathani = sangathani.toDoubleOrNull() ?: 0.0,
                                    ritwicki = ritwicki.toDoubleOrNull() ?: 0.0,
                                    pronami = pronami.toDoubleOrNull() ?: 0.0,
                                    anandabazar = anandabazar.toDoubleOrNull() ?: 0.0,
                                    srimandirCorpus = srimandirCorpus.toDoubleOrNull() ?: 0.0,
                                    parivrity = parivrity.toDoubleOrNull() ?: 0.0,
                                    utsav = utsav.toDoubleOrNull() ?: 0.0,
                                    utsavDesc = utsavDesc,
                                    familyCode = familyCode,
                                    address = address,
                                    pin = pin,
                                    phone = phone,
                                    pan = pan,
                                    cashCode = cashCode,
                                    status = statusState,
                                    promiseAmount = promiseAmount.toDoubleOrNull() ?: 0.0,
                                    dueAmount = dueAmount.toDoubleOrNull() ?: 0.0,
                                    remarks = remarks,
                                    existingId = recordIdToEdit
                                ) {
                                    showFormDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f).testTag("form_submit_btn")
                        ) {
                            Text(Translations.get("save"), color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // --- (A4 LANDSCAPE OVERLAY) PRINT PREVIEW OF DEPOSIT SLIP WITH TOP & BOTTOM COPIES ---
    selectedPrintEntry?.let { entry ->
        Dialog(
            onDismissRequest = { selectedPrintEntry = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White // STRICT HIGH CONTRAST PRINT WHITE BACKGROUND AS SPECIFIED
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    // Header control bar inside printing screen (so we can go back / trigger share)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF2F2F2), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "A4 Landscape Print Duplicate Setup (High Contrast)",
                            fontSize = 12.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // Whatsapp PDF share simulation
                            val context = LocalContext.current
                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Satsang Philanthropy Arghya slip replica generated digitally for ${entry.fullName} totaling Rs.${entry.totalAmount}. Digitized via SKPNN System.")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Slip"))
                                }
                            ) {
                                Icon(Icons.Default.Share, "Share WhatsApp", tint = Color.Green)
                            }
                            IconButton(onClick = { selectedPrintEntry = null }) {
                                Icon(Icons.Default.Close, null, tint = Color.DarkGray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated Landscape A4 Copy Layout
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .border(BorderStroke(1.dp, Color.Black))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. TOP COPY (For Computer / Office Use)
                        PrintSlipSubReplica(entry = entry, copyLabel = "OFFICE COPY (Top Copy)")

                        // Dotted Separator line mimicking page cutting scissor mark
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .drawBehind {
                                    drawLine(
                                        color = Color.Black,
                                        start = Offset(0f, 0f),
                                        end = Offset(size.width, 0f),
                                        strokeWidth = 2f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f))
                                    )
                                }
                        )

                        // 2. BOTTOM COPY (For Depositor)
                        PrintSlipSubReplica(entry = entry, copyLabel = "DEPOSITOR COPY (Bottom Copy)")
                    }
                }
            }
        }
    }

    // --- (LOT FORM DIALOG) RENDER MULTIPLE SELECTED RECORDS INTO EXCEL-STYLE 50-ROW GRID ---
    if (showLotFormPreview) {
        Dialog(
            onDismissRequest = { showLotFormPreview = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White // Standard print layout
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEEEEEE))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "A4 LOT FORM DUPLICATOR (Page Replica)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        IconButton(onClick = { showLotFormPreview = false }) {
                            Icon(Icons.Default.Close, null, tint = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Replica LOT PDF layout
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "SATSANG PHILANTHROPY, SATSANG-DEOGHAR",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Lot Wise Auto-Generated Accumulation Sheet (For Computer Use)",
                            fontSize = 10.sp,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )

                        // Header Block
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrintLabelVal(lbl = "Lot No", valStr = "LOT-${(10..99).random()}")
                            PrintLabelVal(lbl = "Batch No", valStr = "BATCH-042")
                            PrintLabelVal(lbl = "Deposit Date", valStr = viewModel.getCurrentDate())
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            PrintLabelVal(lbl = "Start Srl No", valStr = "SR-1001")
                            PrintLabelVal(lbl = "End Srl No", valStr = "SR-1025")
                            PrintLabelVal(lbl = "Total Record Count", valStr = "${entries.size}")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Grid Table replica
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(BorderStroke(1.dp, Color.Black))
                        ) {
                            // Table Headers
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.LightGray)
                            ) {
                                LotTableCell(text = "SI No", weight = 1f, isHdr = true)
                                LotTableCell(text = "Name", weight = 4f, isHdr = true)
                                LotTableCell(text = "Address", weight = 4f, isHdr = true)
                                LotTableCell(text = "Family Code", weight = 3f, isHdr = true)
                                LotTableCell(text = "Amount Paid", weight = 3f, isHdr = true)
                            }

                            // Active items (auto-filled digital data)
                            var sl = 1
                            entries.forEach { entry ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    LotTableCell(text = "${sl++}", weight = 1f)
                                    LotTableCell(text = entry.fullName, weight = 4f)
                                    LotTableCell(text = entry.address, weight = 4f)
                                    LotTableCell(text = entry.familyCode, weight = 3f)
                                    LotTableCell(text = "Rs. ${entry.totalAmount}", weight = 3f)
                                }
                            }

                            // Empty decorative placeholder rows to fill A4 sheet structure (upto 12 rows)
                            for (i in (sl..12)) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    LotTableCell(text = "$i", weight = 1f)
                                    LotTableCell(text = "", weight = 4f)
                                    LotTableCell(text = "", weight = 4f)
                                    LotTableCell(text = "", weight = 3f)
                                    LotTableCell(text = "", weight = 3f)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bottom Signature rows
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Deposited By: ...........................", fontSize = 10.sp, color = Color.Black)
                            Text("Received By: ...........................", fontSize = 10.sp, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Family Code Block: ________________________ (Office Seal Placeholder with dynamic verification logs)",
                            fontSize = 9.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showLotFormPreview = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier.fillMaxWidth().testTag("close_lot_preview_btn")
                    ) {
                        Text("Save and Close Document Layout", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun PrintLabelVal(lbl: String, valStr: String) {
    Row(
        modifier = Modifier
            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(4.dp))
            .padding(6.dp)
    ) {
        Text(text = "$lbl: ", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
        Text(text = valStr, fontSize = 10.sp, color = Color.DarkGray)
    }
}

@Composable
fun LotTableCell(text: String, weight: Float, isHdr: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier
            .border(BorderStroke(0.5.dp, Color.Black))
            .padding(4.dp),
        fontSize = if (isHdr) 9.sp else 8.sp,
        fontWeight = if (isHdr) FontWeight.Bold else FontWeight.Normal,
        color = Color.Black,
        textAlign = TextAlign.Center
    )
}

@Composable
fun PrintSlipSubReplica(entry: IstravrityEntry, copyLabel: String) {
    Column {
        Text(
            text = copyLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "SATSANG PHILANTHROPY, SATSANG, DEOGHAR (Computerized Replica)",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Arghya Deposit Slip",
            fontSize = 10.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Full Name: ${entry.fullName}", fontSize = 10.sp, color = Color.Black, modifier = Modifier.weight(1f))
            Text("Ritwick's Name in full: ${entry.ritwickName}", fontSize = 10.sp, color = Color.Black, modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Family Code No: ${entry.familyCode}", fontSize = 9.sp, color = Color.Black, modifier = Modifier.weight(1f))
            Text("Token No: ${entry.tokenNo}", fontSize = 9.sp, color = Color.Black, modifier = Modifier.weight(1f))
            Text("Date: ${entry.date}", fontSize = 9.sp, color = Color.Black, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Table Rows layout
        Column(modifier = Modifier.border(BorderStroke(1.dp, Color.Black))) {
            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFEEEEEE))) {
                LotTableCell("Swastyayani", 1f, true)
                LotTableCell("Istavrity", 1f, true)
                LotTableCell("Acharyavrity", 1f, true)
                LotTableCell("Dakshina", 1f, true)
                LotTableCell("Sangathani", 1f, true)
                LotTableCell("Ritwicki", 1f, true)
                LotTableCell("Pronami", 1f, true)
                LotTableCell("Anandabazar", 1f, true)
                LotTableCell("Srimandir (Corpus)", 1f, true)
                LotTableCell("Parivrity", 1f, true)
                LotTableCell("Utsav", 2f, true)
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                LotTableCell("${entry.swastyayani}", 1f)
                LotTableCell("${entry.istabhriti}", 1f)
                LotTableCell("${entry.acharyabhriti}", 1f)
                LotTableCell("${entry.dakshina}", 1f)
                LotTableCell("${entry.sangathani}", 1f)
                LotTableCell("${entry.ritwicki}", 1f)
                LotTableCell("${entry.pronami}", 1f)
                LotTableCell("${entry.anandabazar}", 1f)
                LotTableCell("${entry.srimandirCorpus}", 1f)
                LotTableCell("${entry.parivrity}", 1f)
                LotTableCell("${entry.utsav} (${entry.utsavDesc})", 2f)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Rupees in words: ${entry.totalAmount} (SIMULATED)", fontSize = 9.sp, color = Color.Black)
            Text("TOTAL: Rs. ${entry.totalAmount}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Cash Code: ${entry.cashCode}", fontSize = 9.sp, color = Color.Black)
            Text("Signature of Depositor: ........................", fontSize = 9.sp, color = Color.Black)
        }
    }
}

// --- (5) S.K.P.N.N SCREEN ---

@Composable
fun SkpnnScreen(navController: NavController, viewModel: TempleViewModel) {
    val user = viewModel.currentUser ?: return
    val entries by viewModel.allSkpnnEntries.collectAsState()

    var showForm by remember { mutableStateOf(false) }
    var searchWord by remember { mutableStateOf("") }

    // Inputs form
    var fullName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var pinCode by remember { mutableStateOf("") }
    var familyCode by remember { mutableStateOf("") }
    var pravrityAmount by remember { mutableStateOf("0") }
    var miscAmount by remember { mutableStateOf("0") }
    var statusState by remember { mutableStateOf("PAID") }
    var promiseAmount by remember { mutableStateOf("0") }
    var dueAmount by remember { mutableStateOf("0") }
    var remarks by remember { mutableStateOf("") }

    val filteredList = remember(entries, searchWord) {
        if (searchWord.isBlank()) entries else {
            entries.filter { it.fullName.contains(searchWord, ignoreCase = true) || it.familyCode.contains(searchWord, ignoreCase = true) }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    fullName = ""
                    address = ""
                    mobile = ""
                    pinCode = ""
                    familyCode = ""
                    pravrityAmount = "0"
                    miscAmount = "0"
                    statusState = "PAID"
                    promiseAmount = "0"
                    dueAmount = "0"
                    remarks = ""
                    showForm = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("add_skpnn_fab")
            ) {
                Icon(Icons.Default.Add, null, tint = Color.Black)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            AppHeader(titleKey = "skpnn", viewModel = viewModel)

            AppNavigationDrawer(
                currentRoute = "skpnn",
                navController = navController,
                user = user,
                onLogout = { viewModel.logout { navController.navigate("login") } }
            )

            // Search Block
            OutlinedTextField(
                value = searchWord,
                onValueChange = { searchWord = it },
                label = { Text("Search SKPNN Records") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .height(52.dp),
                textStyle = TextStyle(fontSize = 12.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )

            // Dynamic grid layout
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("skpnn_card_${item.id}"),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.fullName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Total: Rs. ${item.totalAmount}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LightGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Family Code: ${item.familyCode} | PIN: ${item.pinCode}", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "Pravrity System: Rs. ${item.pravrityAmount} | Misc Heading (Writes: S.K.P.N.N): Rs. ${item.miscAmount}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            Text(text = "Remarks: ${item.remarks}", fontSize = 10.sp, color = Color.LightGray)

                            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = { viewModel.deleteSkpnn(item) }) {
                                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        Dialog(onDismissRequest = { showForm = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(12.dp),
                color = CardBackground
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "NEW SKPNN HARIPADA SLIP RECORD",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("skpnn_form_name"),
                        textStyle = TextStyle(fontSize = 12.sp)
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 12.sp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = mobile,
                            onValueChange = { mobile = it },
                            label = { Text("Mobile") },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(fontSize = 11.sp)
                        )
                        OutlinedTextField(
                            value = pinCode,
                            onValueChange = { pinCode = it },
                            label = { Text("PIN") },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(fontSize = 11.sp)
                        )
                    }

                    OutlinedTextField(
                        value = familyCode,
                        onValueChange = { familyCode = it },
                        label = { Text("Family Code") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 12.sp)
                    )

                    // Pravrity Heading Value Input
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = pravrityAmount,
                            onValueChange = { pravrityAmount = it },
                            label = { Text("(A) Pravrity Amt") },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(fontSize = 11.sp)
                        )

                        OutlinedTextField(
                            value = miscAmount,
                            onValueChange = { miscAmount = it },
                            label = { Text("(B) Misc (SKPNN)") },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(fontSize = 11.sp)
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Black)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                "⚠️ Note: Under \"Misc.\" heading block, system dynamically writes \"S.K.P.N.N\" automatically to keep provided print structure untouched.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = { Text("Remarks") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(fontSize = 12.sp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(onClick = { showForm = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (fullName.isBlank()) return@Button
                                viewModel.saveSkpnn(
                                    fullName = fullName,
                                    address = address,
                                    mobile = mobile,
                                    pinCode = pinCode,
                                    familyCode = familyCode,
                                    pravrityAmount = pravrityAmount.toDoubleOrNull() ?: 0.0,
                                    miscAmount = miscAmount.toDoubleOrNull() ?: 0.0,
                                    status = statusState,
                                    promiseAmount = promiseAmount.toDoubleOrNull() ?: 0.0,
                                    dueAmount = dueAmount.toDoubleOrNull() ?: 0.0,
                                    remarks = remarks
                                ) {
                                    showForm = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f).testTag("skpnn_submit_btn")
                        ) {
                            Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- (6) MANDHIR SECTION ---

@Composable
fun MandhirScreen(navController: NavController, viewModel: TempleViewModel) {
    val user = viewModel.currentUser ?: return
    val mandhirs by viewModel.allMandhirs.collectAsState()
    val donations by viewModel.allDonations.collectAsState()

    var showMandhirForm by remember { mutableStateOf(false) }
    var showDonationForm by remember { mutableStateOf(false) }

    // Inputs mandhirs
    var mName by remember { mutableStateOf("") }
    var mAddress by remember { mutableStateOf("") }
    var mLink by remember { mutableStateOf("") }

    // Donation inputs
    var dPerson by remember { mutableStateOf("") }
    var dAmount by remember { mutableStateOf("500") }
    var dRemarks by remember { mutableStateOf("") }
    var selectedMandhirIdx by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            AppHeader(titleKey = "mandhir", viewModel = viewModel)

            AppNavigationDrawer(
                currentRoute = "mandhir",
                navController = navController,
                user = user,
                onLogout = { viewModel.logout { navController.navigate("login") } }
            )

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (user.role != "USER") {
                    Button(
                        onClick = { showMandhirForm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f).testTag("add_mandhir_btn")
                    ) {
                        Icon(Icons.Default.Add, null)
                        Text("Add Mandhir", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                Button(
                    onClick = { showDonationForm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.weight(1f).testTag("add_donation_btn")
                ) {
                    Icon(Icons.Default.VolunteerActivism, null, tint = Color.Black)
                    Text("Collect Donation", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            // Tabs / Side-by-side lists scrollable
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    SectionHeader(titleKey = "mandhir_details", icon = Icons.Default.Store)
                }

                items(mandhirs) { m ->
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("mandhir_card_${m.id}"),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = m.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Address: ${m.address}", fontSize = 11.sp, color = Color.LightGray)
                            Text(text = "Sakha Circle Link: ${m.sakhaLinking}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (user.role != "USER") {
                                    IconButton(onClick = { viewModel.deleteMandhir(m) }) {
                                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    SectionHeader("Donations & Contributions Log", icon = Icons.Default.ReceiptLong)
                }

                items(donations) { d ->
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("donation_card_${d.id}"),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = d.personName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = "Mandhir: ${d.mandhirName}", fontSize = 10.sp, color = Color.Gray)
                                Text(text = "Date: ${d.date} | Remarks: ${d.remarks}", fontSize = 10.sp, color = Color.LightGray)
                            }
                            Text(text = "Rs. ${d.amount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LightGreen)
                        }
                    }
                }
            }
        }
    }

    if (showMandhirForm) {
        Dialog(onDismissRequest = { showMandhirForm = false }) {
            Surface(modifier = Modifier.padding(12.dp), shape = RoundedCornerShape(12.dp), color = CardBackground) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add Established Devotional Mandhir Vihar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    OutlinedTextField(value = mName, onValueChange = { mName = it }, label = { Text("Mandhir Name *") }, modifier = Modifier.fillMaxWidth().testTag("m_name"), textStyle = TextStyle(fontSize = 12.sp))
                    OutlinedTextField(value = mAddress, onValueChange = { mAddress = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(fontSize = 12.sp))
                    OutlinedTextField(value = mLink, onValueChange = { mLink = it }, label = { Text("Sakha Circle Head Link") }, modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(fontSize = 12.sp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showMandhirForm = false }, modifier = Modifier.weight(1f)) { Text("Cancel") }
                        Button(
                            onClick = {
                                if(mName.isBlank()) return@Button
                                viewModel.saveMandhir(mName, mAddress, mLink) { showMandhirForm = false }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f).testTag("m_save")
                        ) {
                            Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showDonationForm && mandhirs.isNotEmpty()) {
        Dialog(onDismissRequest = { showDonationForm = false }) {
            Surface(modifier = Modifier.padding(12.dp), shape = RoundedCornerShape(12.dp), color = CardBackground) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Collect Mandhir Contribution", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    OutlinedTextField(value = dPerson, onValueChange = { dPerson = it }, label = { Text("Person Name *") }, modifier = Modifier.fillMaxWidth().testTag("d_person"), textStyle = TextStyle(fontSize = 12.sp))
                    OutlinedTextField(value = dAmount, onValueChange = { dAmount = it }, label = { Text("Amount *") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), textStyle = TextStyle(fontSize = 12.sp))
                    OutlinedTextField(value = dRemarks, onValueChange = { dRemarks = it }, label = { Text("Remarks") }, modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(fontSize = 12.sp))

                    Text("Attach Contribution To Selected Vihar:", fontSize = 11.sp, color = Color.LightGray)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        mandhirs.forEachIndexed { index, mandhir ->
                            Button(
                                onClick = { selectedMandhirIdx = index },
                                colors = ButtonDefaults.buttonColors(containerColor = if (selectedMandhirIdx == index) MaterialTheme.colorScheme.primary else Color(0xFF2C2C2C)),
                                modifier = Modifier.padding(end = 4.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text(mandhir.name, fontSize = 9.sp, color = if(selectedMandhirIdx == index) Color.Black else Color.White)
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showDonationForm = false }, modifier = Modifier.weight(1f)) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (dPerson.isBlank() || dAmount.toDoubleOrNull() == null) return@Button
                                val m = mandhirs[selectedMandhirIdx]
                                viewModel.saveDonation(m.id, m.name, dPerson, dAmount.toDouble(), dRemarks) { showDonationForm = false }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f).testTag("d_save")
                        ) {
                            Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- (7) EXPENSES SYSTEM SCREEN ---

@Composable
fun ExpensesScreen(navController: NavController, viewModel: TempleViewModel) {
    val user = viewModel.currentUser ?: return
    val expenses by viewModel.allExpenses.collectAsState()

    var showForm by remember { mutableStateOf(false) }

    // Inputs
    var exType by remember { mutableStateOf("Labour Payment") }
    var exAmount by remember { mutableStateOf("1000") }
    var exPerson by remember { mutableStateOf("") }
    var exRemarks by remember { mutableStateOf("") }

    val types = listOf("Labour Payment", "Material Purchase", "Transport", "Decoration", "Other Expenses")

    Scaffold(
        floatingActionButton = {
            if (user.role == "SUPER_ADMIN") {
                FloatingActionButton(
                    onClick = {
                        exPerson = ""
                        exRemarks = ""
                        showForm = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("add_expense_fab")
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.Black)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            AppHeader(titleKey = "expenses", viewModel = viewModel)

            AppNavigationDrawer(
                currentRoute = "expenses",
                navController = navController,
                user = user,
                onLogout = { viewModel.logout { navController.navigate("login") } }
            )

            // Expense List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(expenses) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("expense_card_${item.id}"),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = item.expenseType, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(text = "Paid to: ${item.personName}", fontSize = 11.sp, color = Color.White)
                                Text(text = "Date: ${item.date} | Remarks: ${item.remarks}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Rs. ${item.amount}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF5350)
                                )
                                if (user.role == "SUPER_ADMIN") {
                                    IconButton(onClick = { viewModel.deleteExpense(item) }) {
                                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        Dialog(onDismissRequest = { showForm = false }) {
            Surface(modifier = Modifier.padding(12.dp), shape = RoundedCornerShape(12.dp), color = CardBackground) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Log S.K.P.N.N Temple Expense", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    Text("Select Expense Type:", fontSize = 11.sp, color = Color.LightGray)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        types.forEach { t ->
                            Button(
                                onClick = { exType = t },
                                colors = ButtonDefaults.buttonColors(containerColor = if(exType == t) MaterialTheme.colorScheme.primary else Color(0xFF2C2C2C)),
                                modifier = Modifier.padding(end = 4.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text(t, fontSize = 9.sp, color = if(exType == t) Color.Black else Color.White)
                            }
                        }
                    }

                    OutlinedTextField(value = exAmount, onValueChange = { exAmount = it }, label = { Text("Amount *") }, modifier = Modifier.fillMaxWidth().testTag("ex_amt"), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), textStyle = TextStyle(fontSize = 12.sp))
                    OutlinedTextField(value = exPerson, onValueChange = { exPerson = it }, label = { Text("Person paid to *") }, modifier = Modifier.fillMaxWidth().testTag("ex_person"), textStyle = TextStyle(fontSize = 12.sp))
                    OutlinedTextField(value = exRemarks, onValueChange = { exRemarks = it }, label = { Text("Remarks") }, modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(fontSize = 12.sp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showForm = false }, modifier = Modifier.weight(1f)) { Text("Cancel") }
                        Button(
                            onClick = {
                                if (exPerson.isBlank() || exAmount.toDoubleOrNull() == null) return@Button
                                viewModel.saveExpense(exType, exAmount.toDouble(), exPerson, exRemarks) { showForm = false }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f).testTag("ex_sumbit")
                        ) {
                            Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- (8) UPAJONA SYSTEM SCREEN ---

@Composable
fun UpajonaScreen(navController: NavController, viewModel: TempleViewModel) {
    val user = viewModel.currentUser ?: return
    val upajonas by viewModel.allUpajonas.collectAsState()

    var showForm by remember { mutableStateOf(false) }

    // Inputs
    var upName by remember { mutableStateOf("") }
    var upAddress by remember { mutableStateOf("") }
    var upMobile by remember { mutableStateOf("") }
    var upLocBlock by remember { mutableStateOf("https://maps.google.com/?q=") }

    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            if (user.role != "USER") {
                FloatingActionButton(
                    onClick = {
                        upName = ""
                        upAddress = ""
                        upMobile = ""
                        upLocBlock = "https://maps.google.com/?q="
                        showForm = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("add_upajona_fab")
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.Black)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            AppHeader(titleKey = "upajona", viewModel = viewModel)

            AppNavigationDrawer(
                currentRoute = "upajona",
                navController = navController,
                user = user,
                onLogout = { viewModel.logout { navController.navigate("login") } }
            )

            // Upajona Listing grid
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(upajonas) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upajona_card_${item.id}"),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                // Location launcher
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.location))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(Icons.Default.Map, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Map Loc", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Address: ${item.address}", fontSize = 11.sp, color = Color.White)
                            Text(text = "Contact: ${item.mobile}", fontSize = 11.sp, color = Color.Gray)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (user.role != "USER") {
                                    IconButton(onClick = { viewModel.deleteUpajona(item) }) {
                                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        Dialog(onDismissRequest = { showForm = false }) {
            Surface(modifier = Modifier.padding(12.dp), shape = RoundedCornerShape(12.dp), color = CardBackground) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add Upajona Location Center", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    OutlinedTextField(value = upName, onValueChange = { upName = it }, label = { Text("Upajona Name *") }, modifier = Modifier.fillMaxWidth().testTag("u_name"), textStyle = TextStyle(fontSize = 12.sp))
                    OutlinedTextField(value = upAddress, onValueChange = { upAddress = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(fontSize = 12.sp))
                    OutlinedTextField(value = upMobile, onValueChange = { upMobile = it }, label = { Text("Mobile Phone") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), textStyle = TextStyle(fontSize = 12.sp))
                    OutlinedTextField(value = upLocBlock, onValueChange = { upLocBlock = it }, label = { Text("Maps Location URL *") }, modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(fontSize = 12.sp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showForm = false }, modifier = Modifier.weight(1f)) { Text("Cancel") }
                        Button(
                            onClick = {
                                if(upName.isBlank()) return@Button
                                viewModel.saveUpajona(upName, upAddress, upMobile, upLocBlock) { showForm = false }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f).testTag("u_save")
                        ) {
                            Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- (9) SUPER ADMIN USER CONTROL SCREEN ---

@Composable
fun UserControlScreen(navController: NavController, viewModel: TempleViewModel) {
    val user = viewModel.currentUser ?: return
    val users by viewModel.allUsers.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            AppHeader(titleKey = "user_control", viewModel = viewModel)

            AppNavigationDrawer(
                currentRoute = "user_control",
                navController = navController,
                user = user,
                onLogout = { viewModel.logout { navController.navigate("login") } }
            )

            // Listing registered users
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(users) { u ->
                    // Skip self
                    if (u.id != user.id) {
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("user_perm_card_${u.id}"),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = u.fullName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Text(text = "Mobile: ${u.mobile} | Active Role: ${u.role}", fontSize = 11.sp, color = Color.LightGray)
                                        Text(text = "Status: ${u.status}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if(u.status == "ACTIVE") Color.Green else if(u.status == "BLOCKED") Color.Red else Color.Yellow)
                                    }

                                    // Approve registration triggers
                                    if (u.status == "INACTIVE") {
                                        Button(
                                            onClick = { viewModel.approveUser(u) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp).testTag("approve_btn_${u.id}")
                                        ) {
                                            Text("Approve", fontSize = 10.sp, color = Color.White)
                                        }
                                    }
                                }

                                Divider(color = BorderColor, modifier = Modifier.padding(vertical = 8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Change Roles actions
                                    Button(
                                        onClick = { viewModel.changeUserRole(u, "ADMIN") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131313)),
                                        border = BorderStroke(1.dp, if(u.role == "ADMIN") MaterialTheme.colorScheme.primary else Color.Gray),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text("Set Admin", fontSize = 9.sp, color = Color.White)
                                    }

                                    Button(
                                        onClick = { viewModel.changeUserRole(u, "USER") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131313)),
                                        border = BorderStroke(1.dp, if(u.role == "USER") MaterialTheme.colorScheme.primary else Color.Gray),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text("Set User", fontSize = 9.sp, color = Color.White)
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    // Permanently Block Action
                                    Button(
                                        onClick = { viewModel.toggleUserBlock(u) },
                                        colors = ButtonDefaults.buttonColors(containerColor = if(u.status == "BLOCKED") Color.Gray else MaterialTheme.colorScheme.error),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(26.dp).testTag("block_btn_${u.id}")
                                    ) {
                                        Text(text = if(u.status == "BLOCKED") "Unblock" else "Block Device", fontSize = 9.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
