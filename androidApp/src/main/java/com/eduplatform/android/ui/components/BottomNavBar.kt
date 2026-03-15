package com.eduplatform.android.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.eduplatform.android.navigation.Screen

@Composable
fun BottomNavBar(navController: NavHostController, currentRoute: String?, isAdmin: Boolean = false) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = mutableListOf(
            NavigationItem("Eğitimler", Screen.Home.route, Icons.Default.Home),
            NavigationItem("Sertifikalar", Screen.Certificates.route, Icons.Default.CardMembership),
            NavigationItem("Profil", Screen.Profile.route, Icons.Default.Person)
        )
        
        if (isAdmin) {
            items.add(NavigationItem("Admin", Screen.Admin.route, Icons.Default.AdminPanelSettings))
        }

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF3B82F6),
                    selectedTextColor = Color(0xFF3B82F6),
                    unselectedIconColor = Color(0xFF64748B),
                    unselectedTextColor = Color(0xFF64748B),
                    indicatorColor = Color(0xFFEFF6FF)
                ),
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

data class NavigationItem(val label: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
