package com.krossovochkin.kweather

import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.krossovochkin.kweather.shared.common.router.Router
import com.krossovochkin.kweather.shared.common.router.RouterDestination
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

class AndroidRouter(
    private val navController: NavController
) : Router {

    override fun navigateTo(destination: RouterDestination) {
        val currentDestinationId = navController.currentDestination?.id
        if (currentDestinationId != null) {
            navController.popBackStack(currentDestinationId, true)
        }
        navController.navigate(destination.route)
    }
}

fun routerModule(navController: NavController) = DI.Module("RouterModule") {
    bind<Router>() with singleton { AndroidRouter(navController) }
}
