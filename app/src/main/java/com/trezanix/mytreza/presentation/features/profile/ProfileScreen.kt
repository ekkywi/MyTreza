package com.trezanix.mytreza.presentation.features.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.presentation.theme.BrandBlue

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(top = 40.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(BrandBlue.copy(alpha = 0.1f), CircleShape)
                        .border(2.dp, BrandBlue.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = name ?: "User",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = email ?: "-",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        ProfileSectionTitle("Akun Saya")
        ProfileOptionItem(icon = Icons.Default.Person, title = "Edit Profil") {}
        ProfileOptionItem(icon = Icons.Default.Lock, title = "Ubah Kata Sandi") {}
        ProfileOptionItem(icon = Icons.Default.Settings, title = "Pengaturan Aplikasi") {}

        Spacer(modifier = Modifier.height(20.dp))

        ProfileSectionTitle("Informasi")
        ProfileOptionItem(icon = Icons.AutoMirrored.Filled.Help, title = "Pusat Bantuan") {}
        ProfileOptionItem(icon = Icons.Default.Info, title = "Syarat & Ketentuan") {}

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            onClick = { viewModel.logout(onLogout) }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Keluar Aplikasi",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Danger Zone",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Red,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFFFEBEE),
            onClick = {
                deleteInput = ""
                showDeleteDialog = true
            }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = Color.Red
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Hapus Akun",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                    Text(
                        text = "Tindakan ini permanen",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Red.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "MyTreza v1.0.0",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(80.dp))
    }

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
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(start = 56.dp),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}