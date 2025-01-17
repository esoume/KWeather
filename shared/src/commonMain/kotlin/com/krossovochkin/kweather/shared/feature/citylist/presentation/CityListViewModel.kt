package com.krossovochkin.kweather.shared.feature.citylist.presentation

import com.krossovochkin.kweather.shared.common.localization.LocalizationManager
import com.krossovochkin.kweather.shared.common.localization.LocalizedStringKey
import com.krossovochkin.kweather.shared.common.presentation.BaseViewModel
import com.krossovochkin.kweather.shared.common.presentation.ViewModel
import com.krossovochkin.kweather.shared.common.router.Router
import com.krossovochkin.kweather.shared.common.router.RouterDestination
import com.krossovochkin.kweather.shared.feature.citylist.domain.GetCityListInteractor
import com.krossovochkin.kweather.shared.feature.citylist.domain.SelectCityInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface CityListViewModel : ViewModel<CityListState, CityListAction>

class CityListViewModelImpl(
    private val getCityListInteractor: GetCityListInteractor,
    private val selectCityInteractor: SelectCityInteractor,
    private val router: Router,
    private val localizationManager: LocalizationManager
) : BaseViewModel<CityListState, CityListAction, CityListActionResult>(CityListState.Loading),
    CityListViewModel {

    private val queryChanges = MutableStateFlow("")

    init {
        queryChanges
            .debounce(timeoutMillis = 300L)
            .onEach {
                performAction(CityListAction.Load)
            }
            .launchIn(scope)

        performAction(CityListAction.Load)
    }

    override suspend fun reduceState(
        state: CityListState,
        result: CityListActionResult
    ): CityListState = when (state) {
        CityListState.Loading -> {
            when (result) {
                is CityListActionResult.Loaded -> CityListState.Data(
                    queryText = "",
                    cityList = result.cityList,
                    cityNameHintText = localizationManager.getString(LocalizedStringKey.CityList_CityNameHint)
                )
                is CityListActionResult.CityNameQueryChanged -> reducerError(
                    state,
                    result
                )
            }
        }
        is CityListState.Data -> {
            when (result) {
                is CityListActionResult.Loaded -> state.copy(
                    cityList = result.cityList
                )
                is CityListActionResult.CityNameQueryChanged -> state.copy(
                    queryText = result.query
                )
            }
        }
        is CityListState.Error -> {
            when (result) {
                is CityListActionResult.Loaded -> reducerError(state, result)
                is CityListActionResult.CityNameQueryChanged -> reducerError(
                    state,
                    result
                )
            }
        }
    }

    override fun performAction(action: CityListAction) {
        when (action) {
            CityListAction.Load -> {
                scope.launch {
                    val query = (state as? CityListState.Data)?.queryText.orEmpty()
                    onActionResult(CityListActionResult.Loaded(getCityListInteractor.get(query)))
                }
            }
            is CityListAction.SelectCity -> {
                scope.launch {
                    selectCityInteractor.select(action.city)
                    withContext(Dispatchers.Main) {
                        router.navigateTo(RouterDestination.WeatherDetails)
                    }
                }
            }
            is CityListAction.ChangeCityNameQuery -> {
                scope.launch {
                    queryChanges.value = action.query
                    onActionResult(CityListActionResult.CityNameQueryChanged(action.query))
                }
            }
        }
    }
}
