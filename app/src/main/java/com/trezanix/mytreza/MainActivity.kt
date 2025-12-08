package com.trezanix.mytreza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trezanix.mytreza.presentation.MainScreen
import com.trezanix.mytreza.presentation.features.auth.LoginScreen
import com.trezanix.mytreza.presentation.features.dashboard.DashboardScreen
import com.trezanix.mytreza.presentation.components.CustomOutlinedTextField
import com.trezanix.mytreza.presentation.features.auth.RegisterScreen
import dagger.hilt.android.AndroidEntryPoint
import com.trezanix.mytreza.presentation.theme.MyTrezaTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTrezaTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    val rootNavController = rememberNavController()

                    NavHost(navController = rootNavController, startDestination = "login") {

                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    rootNavController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    rootNavController.navigate("register")
                                }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                },
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("main") {
                            MainScreen()
                        }
                    }

                }
            }
        }
    }
}