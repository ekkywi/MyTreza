package com.trezanix.mytreza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.trezanix.mytreza.presentation.MainScreen
import com.trezanix.mytreza.presentation.features.auth.LoginScreen
import com.trezanix.mytreza.presentation.features.auth.RegisterScreen
import com.trezanix.mytreza.presentation.features.splash.SplashScreen
import com.trezanix.mytreza.presentation.features.wallet.add.AddWalletScreen
import com.trezanix.mytreza.presentation.features.wallet.detail.WalletDetailScreen
import com.trezanix.mytreza.presentation.features.wallet.edit.EditWalletScreen
import com.trezanix.mytreza.presentation.theme.MyTrezaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTrezaTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val rootNavController = rememberNavController()

                    NavHost(navController = rootNavController, startDestination = "splash") {

                        composable("splash") {
                            SplashScreen(
                                onNavigateTo = { dest ->
                                    rootNavController.navigate(dest) {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    rootNavController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = { rootNavController.navigate("register") }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = { },
                                onNavigateToLogin = {
                                    rootNavController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("main") {
                            MainScreen(
                                onNavigateToLogin = {
                                    rootNavController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigateToWalletDetail = { walletId ->
                                    rootNavController.navigate("wallet_detail/$walletId")
                                },
                                onNavigateToAddWallet = {
                                    rootNavController.navigate("add_wallet")
                                }
                            )
                        }

                        composable(
                            route = "wallet_detail/{walletId}",
                            arguments = listOf(navArgument("walletId") { type = NavType.StringType })
                        ) {
                            WalletDetailScreen(
                                onNavigateUp = { rootNavController.popBackStack() },
                                onNavigateToEdit = { id ->
                                    rootNavController.navigate("edit_wallet/$id")
                                }
                            )
                        }

                        composable("add_wallet") {
                            AddWalletScreen(
                                onNavigateUp = { rootNavController.popBackStack() }
                            )
                        }

                        composable(
                            route = "edit_wallet/{walletId}",
                            arguments = listOf(navArgument("walletId") { type = NavType.StringType })
                        ) {
                            EditWalletScreen(
                                onNavigateUp = { rootNavController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}