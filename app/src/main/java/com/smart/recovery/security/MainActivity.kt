
package com.smart.recovery.security

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.text.DecimalFormat

private val Navy = Color(0xFF081426)
private val Panel = Color(0xFF12243B)
private val Cyan = Color(0xFF42D7F5)
private val Muted = Color(0xFF9CB0C8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Cyan,
                    background = Navy,
                    surface = Panel,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                RecoveryApp()
            }
        }
    }
}

@Composable
fun RecoveryApp() {
    val context = LocalContext.current

    var selected by remember { mutableIntStateOf(0) }
    var mediaCount by remember { mutableIntStateOf(0) }
    var status by remember {
        mutableStateOf("অনুমতি দিলে মিডিয়া স্ক্যান করতে পারবেন।")
    }

    val permissions = remember {
        if (Build.VERSION.SDK_INT >= 33) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    fun scan() {
        val granted = permissions.any {
            ContextCompat.checkSelfPermission(
                context, it
            ) == PackageManager.PERMISSION_GRANTED
        }

        if (granted) {
            mediaCount = countMedia(context)
            status = "স্ক্যান সম্পন্ন হয়েছে।"
        } else {
            status = "মিডিয়া অনুমতি প্রয়োজন।"
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            scan()
        }

    Scaffold(
        containerColor = Navy,
        bottomBar = {
            NavigationBar(
                containerColor = Panel
            ) {
                val tabs = listOf(
                    "Home", "Recovery", "Security", "Storage"
                )
                val icons = listOf(
                    Icons.Default.Home,
                    Icons.Default.Image,
                    Icons.Default.Security,
                    Icons.Default.Storage
                )

                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selected == index,
                        onClick = { selected = index },
                        icon = {
                            Icon(icons[index], contentDescription = title)
                        },
                        label = { Text(title) }
                    )
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Navy),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Column {
                    Text(
                        "SMART",
                        color = Cyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Recovery & Security",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            when (selected) {

                0 -> {
                    item { HeroCard() }

                    item {
                        ToolCard(
                            "Memory Recovery",
                            "ছবি, ভিডিও ও অডিও স্ক্যান করুন",
                            Icons.Default.Image
                        ) { selected = 1 }
                    }

                    item {
                        ToolCard(
                            "Security Check",
                            "ফোনের নিরাপত্তা নির্দেশনা",
                            Icons.Default.Security
                        ) { selected = 2 }
                    }

                    item {
                        ToolCard(
                            "Storage Analyzer",
                            "ফোনের স্টোরেজ দেখুন",
                            Icons.Default.Storage
                        ) { selected = 3 }
                    }
                }

                1 -> {
                    item {
                        PageTitle(
                            "Memory Recovery",
                            "অ্যাক্সেসযোগ্য মিডিয়া ফাইল পরীক্ষা করুন"
                        )
                    }

                    item {
                        ToolCard(
                            "Start Media Scan",
                            "ছবি, ভিডিও ও অডিও খুঁজুন",
                            Icons.Default.Search
                        ) {
                            val granted = permissions.any {
                                ContextCompat.checkSelfPermission(
                                    context, it
                                ) == PackageManager.PERMISSION_GRANTED
                            }

                            if (granted) {
                                scan()
                            } else {
                                permissionLauncher.launch(permissions)
                            }
                        }
                    }

                    item {
                        InfoCard("Scan Status", status)
                    }

                    item {
                        InfoCard(
                            "Media Items Found",
                            "$mediaCount টি"
                        )
                    }

                    item {
                        Text(
                            "স্থায়ীভাবে মুছে ফেলা ফাইল উদ্ধার করার নিশ্চয়তা নেই। Gallery Trash এবং Cloud Backup পরীক্ষা করুন।",
                            color = Muted,
                            fontSize = 12.sp
                        )
                    }
                }

                2 -> {
                    item {
                        PageTitle(
                            "Security Check",
                            "ফোনের নিরাপত্তা সম্পর্কে জানুন"
                        )
                    }

                    item {
                        ToolCard(
                            "Open Android Settings",
                            "অ্যাপ ও নিরাপত্তা সেটিংস পরীক্ষা করুন",
                            Icons.Default.Settings
                        ) {
                            context.startActivity(
                                Intent(Settings.ACTION_SETTINGS)
                            )
                        }
                    }

                    item {
                        InfoCard(
                            "Security Tip",
                            "Google Play Protect চালু রাখুন।"
                        )
                    }

                    item {
                        InfoCard(
                            "Important",
                            "এটি পূর্ণাঙ্গ অ্যান্টিভাইরাস নয়। স্বয়ংক্রিয়ভাবে ভাইরাস মুছে না।"
                        )
                    }
                }

                3 -> {
                    val stats = remember { storageStats() }

                    item {
                        PageTitle(
                            "Storage Analyzer",
                            "আপনার ফোনের স্টোরেজ তথ্য"
                        )
                    }

                    item {
                        InfoCard("Total Storage", stats.first)
                    }

                    item {
                        InfoCard("Available Storage", stats.second)
                    }

                    item {
                        InfoCard("Used Storage", stats.third)
                    }
                }
            }

            item {
                Text(
                    "Version 1.0.0  •  Version Code 1",
                    color = Muted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun HeroCard() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF123B60),
                        Color(0xFF10233C),
                        Color(0xFF173A50)
                    ),
                    RoundedCornerShape(24.dp)
                )
            )
            .padding(22.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(
                Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = Cyan,
                modifier = Modifier.size(36.dp)
            )

            Text(
                "Your Phone, Your Security",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Recovery • Security • Storage",
                color = Muted,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun PageTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            title,
            color = Color.White,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )
        Text(subtitle, color = Muted, fontSize = 13.sp)
    }
}

@Composable
fun ToolCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Panel
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Cyan,
                modifier = Modifier.size(30.dp)
            )

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    desc,
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Muted
            )
        }
    }
}

@Composable
fun InfoCard(title: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Panel
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(title, color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                value,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun countMedia(context: Context): Int {
    val resolver = context.contentResolver

    val uris = listOf(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    )

    return uris.sumOf { uri ->
        try {
            resolver.query(
                uri,
                arrayOf(MediaStore.MediaColumns._ID),
                null,
                null,
                null
            )?.use { it.count } ?: 0
        } catch (_: SecurityException) {
            0
        } catch (_: Exception) {
            0
        }
    }
}

private fun storageStats(): Triple<String, String, String> {
    return try {
        val stat = StatFs(Environment.getDataDirectory().path)
        val total = stat.totalBytes
        val free = stat.availableBytes

        Triple(
            formatBytes(total),
            formatBytes(free),
            formatBytes((total - free).coerceAtLeast(0))
        )
    } catch (_: Exception) {
        Triple("Unavailable", "Unavailable", "Unavailable")
    }
}

private fun formatBytes(bytes: Long): String {
    val gb = bytes / (1024.0 * 1024.0 * 1024.0)
    return "${DecimalFormat("#,##0.00").format(gb)} GB"
}
