package com.aripuca.finhelper.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.aripuca.finhelper.ui.screens.about.AboutScreen
import com.aripuca.finhelper.ui.screens.home.HomeScreen
import com.aripuca.finhelper.ui.theme.FinHelperTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreenContentTest() {
        // Start the app
        composeTestRule.setContent {
            FinHelperTheme {
                HomeScreen("Ver. 1.0.0")
            }
        }

        composeTestRule.onNodeWithText("Ver. 1.0.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mortgage").assertIsDisplayed()
        composeTestRule.onNodeWithText("Investments").assertIsDisplayed()
        composeTestRule.onNodeWithText("My 1st Million").assertIsDisplayed()
    }

    @Test
    fun navigateToAboutTest() {
        // Start the app
        composeTestRule.setContent {
            val navController = rememberAnimatedNavController()
            FinHelperTheme {
                AnimatedNavHost(navController = navController, startDestination = "home_screen") {
                    composable(route = "home_screen") {
                        HomeScreen("Ver. 1.0.0", navigateToAbout = {
                            navController.navigate("about_screen")
                        })
                    }

//                    navigation(
//                        startDestination = "about_screen",
//                        route = "about_flow"
//                    ) {
                        composable(route = "about_screen") {
                            AboutScreen("Ver. 1.0.0")
                        }
                 //   }
                }
            }
        }

        composeTestRule.onNodeWithText("Ver. 1.0.0").performClick()

        composeTestRule.waitUntil {
            composeTestRule
                .onAllNodesWithText("About")
                .fetchSemanticsNodes().size == 1
        }

        //composeRule.onRoot().printToLog("TAG")

        composeTestRule.onNodeWithText("Financial Helper").assertIsDisplayed()

    }

}