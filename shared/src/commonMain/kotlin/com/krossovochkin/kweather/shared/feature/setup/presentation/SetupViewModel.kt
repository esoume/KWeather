package com.krossovochkin.kweather.shared.feature.setup.presentation

import com.krossovochkin.kweather.shared.common.presentation.BaseViewModel
import com.krossovochkin.kweather.shared.common.presentation.ViewModel
import com.krossovochkin.kweather.shared.common.router.Router
import com.krossovochkin.kweather.shared.common.router.RouterDestination
import com.krossovochkin.kweather.shared.feature.setup.domain.SetupInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface SetupViewModel : ViewModel<SetupState, SetupAction>

class SetupViewModelImpl(
    private val router: Router,
    private val setupInteractor: SetupInteractor
) : BaseViewModel<SetupState, SetupAction, SetupActionResult>(SetupState.Loading),
    SetupViewModel {

    init {
        performAction(SetupAction.Load)
    }

    override suspend fun reduceState(state: SetupState, result: SetupActionResult): SetupState {
        return SetupState.Loading
    }

    override fun performAction(action: SetupAction) {
        when (action) {
            SetupAction.Load -> {
                scope.launch {
                    setupInteractor.setup()
                    withContext(Dispatchers.Main) {
                        router.navigateTo(RouterDestination.WeatherDetails)
                    }
                }
            }
        }
    }
}
