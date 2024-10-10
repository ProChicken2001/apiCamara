package com.api.apicamara.routes

const val ROOT_MAIN_PAGE = "main"

sealed class Routes(
    val route: String
){
    object MainPage : Routes(route = ROOT_MAIN_PAGE)
}