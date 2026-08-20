package com.jeevifood.customer.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.jeevifood.customer.JeeviFoodApplication
import com.jeevifood.customer.ui.screens.*
import com.jeevifood.customer.viewmodel.*
import kotlinx.coroutines.launch

/**
 * Top-level nav: decides whether to show the auth flow (login/register/otp)
 * or the main app (bottom-tab shell) based on the saved Sanctum token.
 */
@Composable
fun AppNavHost(app: JeeviFoodApplication) {
    val factory = remember { ViewModelFactory(app) }
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val catalogViewModel: CatalogViewModel = viewModel(factory = factory)
    val cartViewModel: CartViewModel = viewModel(factory = factory)
    val addressViewModel: AddressViewModel = viewModel(factory = factory)
    val orderViewModel: OrderViewModel = viewModel(factory = factory)

    var loggedIn by remember { mutableStateOf<Boolean?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        scope.launch { loggedIn = authViewModel.let { app.tokenManager.isLoggedIn() } }
    }

    when (loggedIn) {
        null -> Unit // splash / loading, could show a progress indicator
        false -> AuthNavHost(navController, authViewModel) { loggedIn = true }
        true -> MainShell(app, factory, authViewModel, catalogViewModel, cartViewModel, addressViewModel, orderViewModel) {
            loggedIn = false
        }
    }
}

@Composable
private fun AuthNavHost(navController: NavHostController, authViewModel: AuthViewModel, onLoggedIn: () -> Unit) {
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = onLoggedIn,
                onGoToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegistered = { phone -> navController.navigate(Routes.otp(phone)) },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.OTP,
            arguments = listOf(navArgument("phone") { type = NavType.StringType })
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            OtpScreen(
                phone = phone,
                viewModel = authViewModel,
                onVerified = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.LOGIN) { inclusive = true } } }
            )
        }
    }
}

private sealed class BottomTab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Home : BottomTab(Routes.HOME, "Home", Icons.Filled.Home)
    data object Cart : BottomTab(Routes.CART, "Cart", Icons.Filled.ShoppingCart)
    data object Orders : BottomTab(Routes.ORDERS, "Orders", Icons.Filled.ListAlt)
    data object Profile : BottomTab(Routes.PROFILE, "Profile", Icons.Filled.Person)
}

@Composable
private fun MainShell(
    app: JeeviFoodApplication,
    factory: ViewModelFactory,
    authViewModel: AuthViewModel,
    catalogViewModel: CatalogViewModel,
    cartViewModel: CartViewModel,
    addressViewModel: AddressViewModel,
    orderViewModel: OrderViewModel,
    onLoggedOut: () -> Unit
) {
    val navController = rememberNavController()
    val tabs = listOf(BottomTab.Home, BottomTab.Cart, BottomTab.Orders, BottomTab.Profile)

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            if (currentRoute in tabs.map { it.route }) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(catalogViewModel) { restaurantId ->
                    navController.navigate(Routes.restaurantMenu(restaurantId))
                }
            }
            composable(
                route = Routes.RESTAURANT_MENU,
                arguments = listOf(navArgument("restaurantId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("restaurantId") ?: return@composable
                RestaurantMenuScreen(
                    restaurantId = id,
                    viewModel = catalogViewModel,
                    onBack = { navController.popBackStack() },
                    onFoodItemClick = { foodId -> navController.navigate(Routes.foodDetail(foodId)) },
                    onCartClick = { navController.navigate(Routes.CART) }
                )
            }
            composable(
                route = Routes.FOOD_DETAIL,
                arguments = listOf(navArgument("foodId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("foodId") ?: return@composable
                FoodItemDetailScreen(
                    foodId = id,
                    catalogViewModel = catalogViewModel,
                    cartViewModel = cartViewModel,
                    onBack = { navController.popBackStack() },
                    onAddedToCart = { navController.navigate(Routes.CART) }
                )
            }
            composable(Routes.CART) {
                CartScreen(
                    viewModel = cartViewModel,
                    onBack = { navController.popBackStack() },
                    onCheckout = { navController.navigate(Routes.CHECKOUT) }
                )
            }
            composable(Routes.CHECKOUT) {
                CheckoutScreen(
                    cartViewModel = cartViewModel,
                    addressViewModel = addressViewModel,
                    orderViewModel = orderViewModel,
                    onBack = { navController.popBackStack() },
                    onManageAddresses = { navController.navigate(Routes.ADDRESSES) },
                    onOrderPlaced = { orderId ->
                        navController.navigate(Routes.orderDetail(orderId)) {
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }
            composable(Routes.ADDRESSES) {
                AddressScreen(
                    viewModel = addressViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.ORDERS) {
                OrdersScreen(orderViewModel) { orderId -> navController.navigate(Routes.orderDetail(orderId)) }
            }
            composable(
                route = Routes.ORDER_DETAIL,
                arguments = listOf(navArgument("orderId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("orderId") ?: return@composable
                OrderDetailScreen(orderId = id, viewModel = orderViewModel, onBack = { navController.popBackStack() })
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    viewModel = authViewModel,
                    onAddresses = { navController.navigate(Routes.ADDRESSES) },
                    onOrders = { navController.navigate(Routes.ORDERS) },
                    onLoggedOut = onLoggedOut
                )
            }
        }
    }
}
