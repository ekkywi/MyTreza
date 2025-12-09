package com.trezanix.mytreza.presentation.features.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trezanix.mytreza.presentation.components.CustomOutlinedTextField
import com.trezanix.mytreza.presentation.theme.BrandBlue
import com.trezanix.mytreza.presentation.theme.BrandBlueDark

enum class LegalSheetType {
    NONE, TERMS, PRIVACY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.loginState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isAgreed by remember { mutableStateOf(false) }

    var activeSheet by remember { mutableStateOf(LegalSheetType.NONE) }
    val sheetState = rememberModalBottomSheetState()

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        when (state) {
            is LoginState.Success -> {
                Toast.makeText(context, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show()
                onNavigateToLogin()
            }
            is LoginState.Error -> {
                Toast.makeText(context, (state as LoginState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(BrandBlue, BrandBlueDark)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 12.dp,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.PersonAdd,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bergabunglah untuk masa depan finansial yang lebih baik",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        CustomOutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = "Full Name",
                            icon = Icons.Default.Person,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CustomOutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email Address",
                            icon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CustomOutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            icon = Icons.Default.Lock,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next,
                            isPassword = true,
                            isPasswordVisible = isPasswordVisible,
                            onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CustomOutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm Password",
                            icon = Icons.Default.LockReset,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                            isPassword = true,
                            isPasswordVisible = isConfirmPasswordVisible,
                            onVisibilityChange = { isConfirmPasswordVisible = !isConfirmPasswordVisible }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Checkbox(
                                checked = isAgreed,
                                onCheckedChange = { isAgreed = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BrandBlue,
                                    checkmarkColor = Color.White
                                ),
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            val annotatedString = buildAnnotatedString {
                                append("Saya setuju dengan ")

                                pushStringAnnotation(tag = "TERMS", annotation = "terms")
                                withStyle(style = SpanStyle(color = BrandBlue, fontWeight = FontWeight.Bold)) {
                                    append("Syarat & Ketentuan")
                                }
                                pop()

                                append(" serta ")

                                pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                                withStyle(style = SpanStyle(color = BrandBlue, fontWeight = FontWeight.Bold)) {
                                    append("Kebijakan Privasi")
                                }
                                pop()

                                append(".")
                            }

                            ClickableText(
                                text = annotatedString,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                ),
                                onClick = { offset ->
                                    annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                                        .firstOrNull()?.let {
                                            activeSheet = LegalSheetType.TERMS
                                        }

                                    annotatedString.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                                        .firstOrNull()?.let {
                                            activeSheet = LegalSheetType.PRIVACY
                                        }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                                    Toast.makeText(context, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (password != confirmPassword) {
                                    Toast.makeText(context, "Password tidak cocok!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (password.length < 8) {
                                    Toast.makeText(context, "Password minimal 8 karakter", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (!isAgreed) {
                                    Toast.makeText(context, "Harap setujui Syarat & Ketentuan untuk melanjutkan.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                viewModel.register(fullName, email, password)
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            enabled = state !is LoginState.Loading
                        ) {
                            if (state is LoginState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Daftar Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sudah punya akun? ", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    Text(
                        text = "Login",
                        color = BrandBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onNavigateToLogin() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (activeSheet != LegalSheetType.NONE) {
            ModalBottomSheet(
                onDismissRequest = { activeSheet = LegalSheetType.NONE },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                if (activeSheet == LegalSheetType.TERMS) {
                    LegalContent(
                        title = "Syarat & Ketentuan",
                        content = termsAndConditionsContent
                    )
                } else {
                    LegalContent(
                        title = "Kebijakan Privasi",
                        content = privacyPolicyContent
                    )
                }
            }
        }
    }
}

@Composable
fun LegalContent(title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .heightIn(max = 400.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = BrandBlue)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = content, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

const val termsAndConditionsContent = """
1. PENDAHULUAN
Selamat datang di aplikasi MyTreza. Dengan mendaftar dan menggunakan layanan kami, Anda dianggap telah membaca, memahami, dan menyetujui Syarat dan Ketentuan ini.

2. LAYANAN KAMI
MyTreza adalah aplikasi manajemen keuangan pribadi yang menyediakan fitur pencatatan pemasukan, pengeluaran, dan analisis anggaran. Layanan ini disediakan "sebagaimana adanya".

3. AKUN PENGGUNA
a. Anda bertanggung jawab penuh atas keamanan kata sandi dan akun Anda.
b. Anda menjamin bahwa data yang diberikan saat registrasi adalah akurat dan terkini.
c. Kami berhak menangguhkan akun jika terindikasi adanya aktivitas mencurigakan atau pelanggaran hukum.

4. PENAFIAN KEUANGAN (DISCLAIMER)
MyTreza bukanlah penasihat keuangan, konsultan investasi, atau lembaga perbankan.
a. Segala informasi atau analisis yang ditampilkan di aplikasi hanya untuk tujuan informasi umum.
b. Kami tidak bertanggung jawab atas kerugian finansial yang timbul akibat keputusan yang Anda ambil berdasarkan data dalam aplikasi ini.

5. HAK KEKAYAAN INTELEKTUAL
Seluruh desain, logo, kode sumber, dan konten dalam aplikasi MyTreza adalah milik kami dan dilindungi oleh undang-undang hak cipta.

6. PERUBAHAN KETENTUAN
Kami berhak mengubah syarat dan ketentuan ini sewaktu-waktu. Perubahan akan diberitahukan melalui pembaruan aplikasi atau email.
"""

const val privacyPolicyContent = """
1. INFORMASI YANG KAMI KUMPULKAN
Untuk memberikan layanan terbaik, kami mengumpulkan data berikut:
a. Informasi Pribadi: Nama lengkap dan alamat email saat registrasi.
b. Data Keuangan: Riwayat transaksi, nominal pemasukan/pengeluaran, dan kategori yang Anda input secara manual.
c. Data Perangkat: Informasi teknis seperti model perangkat dan versi OS untuk keperluan pemecahan masalah (debugging).

2. PENGGUNAAN DATA
Data Anda digunakan untuk:
a. Menyajikan dashboard dan analisis keuangan pribadi Anda.
b. Mengamankan akses akun (autentikasi).
c. Meningkatkan performa dan fitur aplikasi.

3. KEAMANAN DATA
Kami memprioritaskan keamanan data Anda.
a. Kata sandi Anda disimpan menggunakan enkripsi satu arah (hashing) standar industri.
b. Transaksi data antara aplikasi dan server dilindungi menggunakan protokol enkripsi SSL/TLS.

4. PEMBAGIAN DATA
Kami TIDAK AKAN menjual, menyewakan, atau membagikan data pribadi maupun data keuangan Anda kepada pihak ketiga manapun untuk tujuan pemasaran tanpa izin eksplisit dari Anda, kecuali diwajibkan oleh hukum.

5. HAK PENGGUNA
Anda berhak untuk:
a. Mengakses dan memperbarui informasi profil Anda.
b. Meminta penghapusan akun dan seluruh data terkait dengan menghubungi tim dukungan kami.

6. HUBUNGI KAMI
Jika ada pertanyaan mengenai privasi ini, silakan hubungi tim support MyTreza.
"""