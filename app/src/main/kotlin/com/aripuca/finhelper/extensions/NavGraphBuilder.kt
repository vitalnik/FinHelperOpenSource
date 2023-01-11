package com.aripuca.finhelper.extensions

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.navigation

fun NavGraphBuilder.navigationFlow(
    startDestination: String,
    route: String,
    builder: NavGraphBuilder.() -> Unit,
) = navigation(
    startDestination = startDestination,
    route = route,
    enterTransition = { slideIntoLeft() },
    exitTransition = { slideOutOfLeft() },
    popEnterTransition = { slideIntoRight() },
    popExitTransition = { slideOutOfRight() },
    builder = builder
)

fun AnimatedContentScope<NavBackStackEntry>.slideIntoLeft() = slideIntoContainer(
    towards = AnimatedContentScope.SlideDirection.Left,
    animationSpec = tween(),
    initialOffset = { it }
)

fun AnimatedContentScope<NavBackStackEntry>.slideOutOfLeft() = slideOutOfContainer(
    towards = AnimatedContentScope.SlideDirection.Left,
    animationSpec = tween(),
    targetOffset = { it }
)

fun AnimatedContentScope<NavBackStackEntry>.slideIntoRight() = slideIntoContainer(
    towards = AnimatedContentScope.SlideDirection.Right,
    animationSpec = tween(),
    initialOffset = { it }
)

fun AnimatedContentScope<NavBackStackEntry>.slideOutOfRight() = slideOutOfContainer(
    towards = AnimatedContentScope.SlideDirection.Right,
    animationSpec = tween(),
    targetOffset = { it }
)