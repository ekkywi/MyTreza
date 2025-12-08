package com.trezanix.mytreza.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.trezanix.mytreza.presentation.features.dashboard.DashboardScreen
import com.trezanix.mytreza.presentation.features.placeholder.HistoryScreen
import com.trezanix.mytreza.presentation.features.placeholder.ProfileScreen
import com.trezanix.mytreza.presentation.features.placeholder.WalletScreen
import com.trezanix.mytreza.presentation.navigation.BottomNavItem
import com.trezanix.mytreza.presentation.theme.BrandBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    BottomMenuItem(
                        icon = Icons.Default.Home,
                        label = "Beranda",
                        isSelected = currentRoute == BottomNavItem.Home.route,
                        onClick = {
                            navController.navigate(BottomNavItem.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    BottomMenuItem(
                        icon = Icons.Default.History,
                        label = "Riwayat",
                        isSelected = currentRoute == BottomNavItem.History.route,
                        onClick = {
                            navController.navigate(BottomNavItem.History.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(BrandBlue)
                            .clickable { showBottomSheet = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    BottomMenuItem(
                        icon = Icons.Default.Wallet,
                        label = "Dompet",
                        isSelected = currentRoute == BottomNavItem.Wallet.route,
                        onClick = {
                            navController.navigate(BottomNavItem.Wallet.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    BottomMenuItem(
                        icon = Icons.Default.Person,
                        label = "Profil",
                        isSelected = currentRoute == BottomNavItem.Profile.route,
                        onClick = {
                            navController.navigate(BottomNavItem.Profile.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { DashboardScreen() }
            composable(BottomNavItem.History.route) { HistoryScreen() }
            composable(BottomNavItem.Wallet.route) { WalletScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                AddActionSheetContent { showBottomSheet = false }
            }
        }
    }
}

@Composable
fun BottomMenuItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) BrandBlue else Color.LightGray,
            modifier = Modifier.size(26.dp)
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(BrandBlue)
            )
        }
    }
}

@Composable
fun AddActionSheetContent(onActionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mau buat apa?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        ActionItem(
            icon = Icons.Default.ReceiptLong,
            color = BrandBlue,
            title = "Transaksi Baru",
            desc = "Catat pemasukan atau pengeluaran",
            onClick = { onActionClick("transaction") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ActionItem(
            icon = Icons.Rounded.AccountBalanceWallet,
            color = Color(0xFFE91E63),
            title = "Tambah Dompet",
            desc = "Buat dompet cash atau bank baru",
            onClick = { onActionClick("wallet") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ActionItem(
            icon = Icons.Default.PieChart,
            color = Color(0xFF00C853),
            title = "Atur Budget",
            desc = "Batasi pengeluaran bulananmu",
            onClick = { onActionClick("budget") }
        )
    }
}

@Composable
fun ActionItem(
    icon: ImageVector,
    color: Color,
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}