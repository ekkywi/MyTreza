package com.trezanix.mytreza.presentation.features.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Category // [PENTING] Import Icon Category
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.presentation.theme.AccentGreen
import com.trezanix.mytreza.presentation.theme.BrandBlue

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToCategory: () -> Unit, // [BARU] Callback untuk navigasi ke Kategori
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val name by viewModel.userName.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteInput by remember { mutableStateOf("") }

    val initial = if (name?.isNotEmpty() == true) name!!.first().toString() else "?"

    LaunchedEffect(deleteState) {
        if (deleteState is ProfileViewModel.UiState.Error) {
            val msg = (deleteState as ProfileViewModel.UiState.Error).message
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .verticalScroll(scrollState)
    ) {
        // 1. MODERN HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(16.dp, CircleShape, spotColor = BrandBlue.copy(alpha = 0.5f))
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(BrandBlue, AccentGreen)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = name ?: "User",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = email ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        // 2. OPTIONS GROUPS
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Group: Account
            ProfileGroupCard(title = "Akun Saya") {
                ProfileOptionItem(icon = Icons.Default.Person, title = "Edit Profil", color = BrandBlue) {}
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
                ProfileOptionItem(icon = Icons.Default.Lock, title = "Ubah Kata Sandi", color = BrandBlue) {}
                // [UBAH] Item "Pengaturan Aplikasi" dihapus dari sini
            }

            // [BARU] Group: Pengaturan Aplikasi
            ProfileGroupCard(title = "Pengaturan Aplikasi") {
                ProfileOptionItem(
                    icon = Icons.Default.Category,
                    title = "Atur Kategori",
                    color = Color(0xFFE91E63) // Warna Pink/Ungu biar beda
                ) {
                    onNavigateToCategory() // Panggil navigasi saat diklik
                }
            }

            // Group: Info
            ProfileGroupCard(title = "Informasi") {
                ProfileOptionItem(icon = Icons.AutoMirrored.Filled.Help, title = "Pusat Bantuan", color = AccentGreen) {}
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
                ProfileOptionItem(icon = Icons.Default.Info, title = "Syarat & Ketentuan", color = AccentGreen) {}
            }

            // Group: Actions
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Logout Button
                Button(
                    onClick = { viewModel.logout(onLogout) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color.Gray.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Red)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Keluar Aplikasi", fontWeight = FontWeight.Bold)
                    }
                }

                // Delete Account
                OutlinedButton(
                    onClick = {
                        deleteInput = ""
                        showDeleteDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red.copy(alpha = 0.7f)),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.2f))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeleteForever, null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Hapus Akun Permanen")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "MyTreza v1.0.0",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Dialog Hapus Akun (Sama seperti sebelumnya)
    if (showDeleteDialog) {
        val isLoading = deleteState is ProfileViewModel.UiState.Loading

        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = {
                if (!isLoading) showDeleteDialog = false
            },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red) },
            title = {
                Text(
                    text = "Hapus Akun Permanen?",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column {
                    Text(
                        text = "Semua data transaksi dan dompet akan hilang selamanya. Ketik email Anda untuk konfirmasi:",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = email ?: "",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(8.dp),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = deleteInput,
                        onValueChange = { deleteInput = it },
                        label = { Text("Ketik ulang email di atas") },
                        singleLine = true,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            cursorColor = Color.Red,
                            focusedLabelColor = Color.Red
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccount(deleteInput.trim()) {
                            showDeleteDialog = false
                            Toast.makeText(context, "Akun berhasil dihapus. Sampai jumpa!", Toast.LENGTH_LONG).show()
                            onLogout()
                        }
                    },
                    enabled = (deleteInput.trim() == (email ?: "")) && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        disabledContainerColor = Color.Red.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Hapus Selamanya")
                    }
                }
            },
            dismissButton = {
                if (!isLoading) {
                    TextButton(
                        onClick = { showDeleteDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Batal", color = Color.Gray)
                    }
                }
            }
        )
    }
}

@Composable
fun ProfileGroupCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.dp),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color.Gray.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.8f)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color.Gray.copy(alpha = 0.4f),
            modifier = Modifier.size(14.dp)
        )
    }
}