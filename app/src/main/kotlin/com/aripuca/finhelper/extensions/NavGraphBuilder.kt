package com.aripuca.finhelper.extensions

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import kotlin.reflect.KClass

fun NavGraphBuilder.navigationFlow(
    startDestination: Any,
    route: KClass<*>,
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

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideIntoLeft() = slideIntoContainer(
    towards = AnimatedContentTransitionScope.SlideDirection.Left,
    animationSpec = tween(),
    initialOffset = { it }
)

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutOfLeft() = slideOutOfContainer(
    towards = AnimatedContentTransitionScope.SlideDirection.Left,
    animationSpec = tween(),
    targetOffset = { it }
)

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideIntoRight() = slideIntoContainer(
    towards = AnimatedContentTransitionScope.SlideDirection.Right,
    animationSpec = tween(),
    initialOffset = { it }
)

fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutOfRight() = slideOutOfContainer(
    towards = AnimatedContentTransitionScope.SlideDirection.Right,
    animationSpec = tween(),
    targetOffset = { it }
)