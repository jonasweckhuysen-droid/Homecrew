package com.dreammania.homecrew

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dreammania.homecrew.agenda.AgendaScreen
import com.dreammania.homecrew.auth.GoogleAuthUiClient
import com.dreammania.homecrew.emergency.EmergencyScreen
import com.dreammania.homecrew.house.HouseScreen
import com.dreammania.homecrew.location.LocationScreen
import com.dreammania.homecrew.location.LocationService
import com.dreammania.homecrew.shopping.ShoppingScreen
import com.dreammania.homecrew.tasks.TasksScreen
import com.dreammania.homecrew.ui.login.LoginScreen
import com.dreammania.homecrew.ui.splash.SplashScreen
import com.dreammania.homecrew.ui.theme.*
import com.dreammania.homecrew.weather.WeatherForecastScreen
import com.dreammania.homecrew.weather.WeatherViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(applicationContext)
    }

    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val backgroundColor = BackgroundColor
            var activeEmergency by remember { mutableStateOf<Map<String, Any>?>(null) }
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                val db = FirebaseDatabase.getInstance().getReference("emergencies")
                db.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val latest = snapshot.children.lastOrNull()
                        @Suppress("UNCHECKED_CAST")
                        val data = latest?.value as? Map<String, Any>
                        if (data != null && data["active"] == true) {
                            val timestamp = data["timestamp"] as? Long ?: 0L
                            if (System.currentTimeMillis() - timestamp < 300000) {
                                activeEmergency = data
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            SideEffect {
                window.statusBarColor = backgroundColor.toArgb()
                window.navigationBarColor = backgroundColor.toArgb()
            }
            
            HomecrewTheme {
                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()
                val credentialManager = CredentialManager.create(this)

                Box(modifier = Modifier.fillMaxSize().background(BackgroundColor)) {
                    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundColor) {
                        NavHost(navController = navController, startDestination = "splash") {
                            composable("splash") {
                                SplashScreen(onTimeout = {
                                    if (googleAuthUiClient.getSignedInUser() == null) {
                                        navController.navigate("login") { popUpTo("splash") { inclusive = true } }
                                    } else {
                                        navController.navigate("home") { popUpTo("splash") { inclusive = true } }
                                    }
                                })
                            }
                            composable("login") {
                                LoginScreen(onLoginClick = {
                                    coroutineScope.launch {
                                        val googleIdOption = GetGoogleIdOption.Builder()
                                            .setFilterByAuthorizedAccounts(false)
                                            .setServerClientId(getString(R.string.default_web_client_id))
                                            .build()

                                        val request = GetCredentialRequest.Builder()
                                            .addCredentialOption(googleIdOption)
                                            .build()

                                        try {
                                            val result = credentialManager.getCredential(this@MainActivity, request)
                                            handleSignInResponse(result, navController)
                                        } catch (e: GetCredentialException) {
                                            Log.e("MainActivity", "GetCredentialException", e)
                                            Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                })
                            }
                            composable("home") {
                                val familyMember = googleAuthUiClient.getSignedInUser()?.username?.let { FamilyMember(it) }
                                familyMember?.let { member ->
                                    val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
                                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                                    val currentDate = dateFormat.format(Date())
                                    val currentTime = timeFormat.format(Date())
                                    HomeScreen(
                                        familyMember = member, 
                                        weatherText = weatherViewModel.weatherText, 
                                        navController = navController,
                                        currentDate = currentDate,
                                        currentTime = currentTime
                                    )
                                }
                            }
                            composable("shopping") {
                                HeaderContentWrapper(navController) { ShoppingScreen() }
                            }
                            composable("agenda") {
                                HeaderContentWrapper(navController) { AgendaScreen() }
                            }
                            composable("tasks") {
                                HeaderContentWrapper(navController) { TasksScreen() }
                            }
                            composable("emergency") {
                                HeaderContentWrapper(navController) { EmergencyScreen() }
                            }
                            composable("house") {
                                HeaderContentWrapper(navController) { HouseScreen() }
                            }
                            composable("location") {
                                val permissionLauncher = rememberLauncherForActivityResult(
                                    ActivityResultContracts.RequestMultiplePermissions()
                                ) { permissions ->
                                    if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                                        Intent(context, LocationService::class.java).apply {
                                            action = LocationService.ACTION_START
                                            context.startService(this)
                                        }
                                    }
                                }

                                LaunchedEffect(Unit) {
                                    permissionLauncher.launch(arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ))
                                }

                                HeaderContentWrapper(navController) { LocationScreen() }
                            }
                            composable("weather") {
                                HeaderContentWrapper(navController) { WeatherForecastScreen(weatherViewModel) }
                            }
                        }
                    }

                    activeEmergency?.let { emergency ->
                        EmergencyAlert(
                            userName = emergency["userName"] as? String ?: "Onbekend",
                            lat = emergency["latitude"] as? Double ?: 0.0,
                            lng = emergency["longitude"] as? Double ?: 0.0,
                            onDismiss = { activeEmergency = null }
                        )
                    }
                }

                LaunchedEffect(key1 = Unit) {
                    if (googleAuthUiClient.getSignedInUser() != null) {
                        weatherViewModel.fetchWeather("GENT", "4a993261a71ece889094805bab5b864a")
                    }
                }
            }
        }
    }

    @Composable
    private fun HeaderContentWrapper(navController: NavController, content: @Composable () -> Unit) {
        val familyMemberName = googleAuthUiClient.getSignedInUser()?.username ?: "Gebruiker"
        val member = FamilyMember(familyMemberName)
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        
        Column(modifier = Modifier.fillMaxSize().safeDrawingPadding().background(BackgroundColor)) {
            HeaderSection(member, weatherViewModel.weatherText, dateFormat.format(Date()), timeFormat.format(Date()), navController, true)
            Spacer(modifier = Modifier.height(8.dp))
            BackButton(onClick = { navController.popBackStack() })
            content()
        }
    }

    private fun handleSignInResponse(result: GetCredentialResponse, navController: NavController) {
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                handleSignIn(googleIdTokenCredential, navController)
            } catch (e: GoogleIdTokenParsingException) {
                Log.e("MainActivity", "Received an invalid google id token response", e)
            }
        } else {
            Log.e("MainActivity", "Unexpected type of credential")
        }
    }
    
    private fun handleSignIn(credential: GoogleIdTokenCredential, navController: NavController) {
        lifecycleScope.launch {
            val signInResult = googleAuthUiClient.signInWithCredential(credential)
            if (signInResult.errorMessage != null) {
                Toast.makeText(applicationContext, signInResult.errorMessage, Toast.LENGTH_LONG).show()
            } else {
                navController.navigate("home") { popUpTo("login") { inclusive = true } }
            }
        }
    }
}

@Composable
fun BackButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 16.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF0288D1))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Terug")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Terug naar vorige pagina", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EmergencyAlert(userName: String, lat: Double, lng: Double, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp)) },
        title = { Text("NOODGEVAL!", color = Color.Red, fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$userName heeft hulp nodig!",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Coördinaten: $lat, $lng", fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Gezien", color = Color.White)
            }
        }
    )
}

data class FamilyMember(val name: String)

enum class AppScreen(
    val title: String,
    val iconRes: Int?,
    val vectorIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val color: Color
) {
    AGENDA("Agenda", R.drawable.agenda, null, ButtonRed),
    BOODSCHAPPEN("Boodschappen", R.drawable.winkel, null, ButtonGreen),
    LOCATIE("Locatie", R.drawable.home, null, ButtonPurple),
    NOODKNOP("Noodknop", R.drawable.sos, null, ButtonYellow),
    TAKEN("Taken", R.drawable.task, null, ButtonBlue),
    WEER("Weer", null, Icons.Default.Cloud, ButtonCyan),
    HUIS("Mijn Huis", null, Icons.Default.HomeWork, ButtonTeal)
}

@Composable
fun HeaderSection(
    familyMember: FamilyMember,
    weatherText: String,
    currentDate: String,
    currentTime: String,
    navController: NavController,
    showHomeButton: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(32.dp)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeaderBrush)
                .padding(16.dp)
        ) {
            if (showHomeButton) {
                IconButton(
                    onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = "Home",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(top = if(showHomeButton) 40.dp else 0.dp)) {
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White, fontFamily = FontFamily.Cursive, fontSize = 28.sp)) {
                        append("Hallo, ")
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)) {
                        append(familyMember.name)
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 28.sp)) {
                        append("!")
                    }
                }, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (weatherText.contains("zonnig")) Icons.Default.WbSunny else Icons.Default.Cloud, contentDescription = "Weather", tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = weatherText, style = MaterialTheme.typography.bodyLarge, color = Color.White, fontFamily = FontFamily.SansSerif)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Date", tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "$currentDate - $currentTime", style = MaterialTheme.typography.bodyMedium, color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(familyMember: FamilyMember, weatherText: String, navController: NavController, modifier: Modifier = Modifier, currentDate: String, currentTime: String) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .safeDrawingPadding()
    ) {
        HeaderSection(
            familyMember = familyMember,
            weatherText = weatherText,
            currentDate = currentDate,
            currentTime = currentTime,
            navController = navController
        )
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .weight(1f)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(AppScreen.entries.toTypedArray()) { screen ->
                    FamilyAppButton(
                        screen = screen,
                        onClick = { 
                            when (screen) {
                                AppScreen.BOODSCHAPPEN -> navController.navigate("shopping")
                                AppScreen.AGENDA -> navController.navigate("agenda")
                                AppScreen.TAKEN -> navController.navigate("tasks")
                                AppScreen.NOODKNOP -> navController.navigate("emergency")
                                AppScreen.LOCATIE -> navController.navigate("location")
                                AppScreen.WEER -> navController.navigate("weather")
                                AppScreen.HUIS -> navController.navigate("house")
                                else -> {}
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FamilyAppButton(screen: AppScreen, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier.background(screen.color).fillMaxSize().padding(12.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (screen.iconRes != null) {
                    Image(
                        painter = painterResource(id = screen.iconRes), 
                        contentDescription = screen.title, 
                        modifier = Modifier.size(52.dp)
                    )
                } else if (screen.vectorIcon != null) {
                    Icon(
                        imageVector = screen.vectorIcon,
                        contentDescription = screen.title,
                        tint = TextColor,
                        modifier = Modifier.size(52.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = screen.title, 
                    color = TextColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp, // Slightly smaller font to fit better
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomecrewTheme {
        val navController = rememberNavController()
        val familyMember = FamilyMember("Voorbeeld")
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        HomeScreen(
            familyMember = familyMember,
            weatherText = "Het is momenteel zonnig, 23°C.",
            navController = navController,
            currentDate = dateFormat.format(Date()),
            currentTime = timeFormat.format(Date())
        )
    }
}
