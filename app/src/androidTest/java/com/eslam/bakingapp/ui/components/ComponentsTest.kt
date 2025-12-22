package com.eslam.bakingapp.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.eslam.bakingapp.core.ui.components.BakingTextField
import com.eslam.bakingapp.core.ui.components.ErrorView
import com.eslam.bakingapp.core.ui.components.ErrorType
import com.eslam.bakingapp.core.ui.components.FullScreenLoading
import com.eslam.bakingapp.core.ui.components.LoadingIndicator
import com.eslam.bakingapp.core.ui.components.PasswordTextField
import com.eslam.bakingapp.core.ui.components.PrimaryButton
import com.eslam.bakingapp.core.ui.components.RecipeCard
import com.eslam.bakingapp.core.ui.components.SecondaryButton
import com.eslam.bakingapp.core.ui.theme.BakingAppTheme
import org.junit.Rule
import org.junit.Test

class ComponentsTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    // ==================== Button Tests ====================
    
    @Test
    fun primaryButton_displaysText() {
        composeTestRule.setContent {
            BakingAppTheme {
                PrimaryButton(text = "Click Me", onClick = {})
            }
        }
        
        composeTestRule.onNodeWithText("Click Me").assertIsDisplayed()
    }
    
    @Test
    fun primaryButton_isClickable() {
        var clicked = false
        
        composeTestRule.setContent {
            BakingAppTheme {
                PrimaryButton(text = "Click Me", onClick = { clicked = true })
            }
        }
        
        composeTestRule.onNodeWithText("Click Me").performClick()
        assert(clicked)
    }
    
    @Test
    fun primaryButton_disabledWhenLoading() {
        composeTestRule.setContent {
            BakingAppTheme {
                PrimaryButton(text = "Loading", onClick = {}, isLoading = true)
            }
        }
        
        composeTestRule.onNodeWithText("Loading").assertIsNotEnabled()
    }
    
    @Test
    fun primaryButton_disabledWhenEnabledFalse() {
        composeTestRule.setContent {
            BakingAppTheme {
                PrimaryButton(text = "Disabled", onClick = {}, enabled = false)
            }
        }
        
        composeTestRule.onNodeWithText("Disabled").assertIsNotEnabled()
    }
    
    @Test
    fun secondaryButton_displaysText() {
        composeTestRule.setContent {
            BakingAppTheme {
                SecondaryButton(text = "Secondary", onClick = {})
            }
        }
        
        composeTestRule.onNodeWithText("Secondary").assertIsDisplayed()
    }
    
    // ==================== TextField Tests ====================
    
    @Test
    fun bakingTextField_displaysLabel() {
        composeTestRule.setContent {
            BakingAppTheme {
                BakingTextField(
                    value = "",
                    onValueChange = {},
                    label = "Email"
                )
            }
        }
        
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
    }
    
    @Test
    fun bakingTextField_displaysPlaceholder() {
        composeTestRule.setContent {
            BakingAppTheme {
                BakingTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = "Enter your email"
                )
            }
        }
        
        composeTestRule.onNodeWithText("Enter your email").assertIsDisplayed()
    }
    
    @Test
    fun bakingTextField_acceptsInput() {
        var text = ""
        
        composeTestRule.setContent {
            BakingAppTheme {
                BakingTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = "Input"
                )
            }
        }
        
        composeTestRule.onNodeWithText("Input").performTextInput("Hello")
    }
    
    @Test
    fun bakingTextField_displaysError() {
        composeTestRule.setContent {
            BakingAppTheme {
                BakingTextField(
                    value = "invalid",
                    onValueChange = {},
                    label = "Email",
                    isError = true,
                    errorMessage = "Invalid email format"
                )
            }
        }
        
        composeTestRule.onNodeWithText("Invalid email format").assertIsDisplayed()
    }
    
    @Test
    fun passwordTextField_displaysLabel() {
        composeTestRule.setContent {
            BakingAppTheme {
                PasswordTextField(
                    value = "",
                    onValueChange = {},
                    label = "Password"
                )
            }
        }
        
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
    }
    
    // ==================== Loading Indicator Tests ====================
    
    @Test
    fun loadingIndicator_displaysMessage() {
        composeTestRule.setContent {
            BakingAppTheme {
                LoadingIndicator(message = "Loading...")
            }
        }
        
        composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
    }
    
    @Test
    fun fullScreenLoading_displaysMessage() {
        composeTestRule.setContent {
            BakingAppTheme {
                FullScreenLoading(message = "Please wait...")
            }
        }
        
        composeTestRule.onNodeWithText("Please wait...").assertIsDisplayed()
    }
    
    // ==================== Error View Tests ====================
    
    @Test
    fun errorView_displaysTitle() {
        composeTestRule.setContent {
            BakingAppTheme {
                ErrorView(
                    title = "Error Title",
                    message = "Something went wrong"
                )
            }
        }
        
        composeTestRule.onNodeWithText("Error Title").assertIsDisplayed()
    }
    
    @Test
    fun errorView_displaysMessage() {
        composeTestRule.setContent {
            BakingAppTheme {
                ErrorView(
                    title = "Error",
                    message = "Network connection failed"
                )
            }
        }
        
        composeTestRule.onNodeWithText("Network connection failed").assertIsDisplayed()
    }
    
    @Test
    fun errorView_displaysRetryButton() {
        composeTestRule.setContent {
            BakingAppTheme {
                ErrorView(
                    title = "Error",
                    message = "Error message",
                    onRetry = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
    }
    
    @Test
    fun errorView_retryButtonClickable() {
        var retryClicked = false
        
        composeTestRule.setContent {
            BakingAppTheme {
                ErrorView(
                    title = "Error",
                    message = "Error message",
                    onRetry = { retryClicked = true }
                )
            }
        }
        
        composeTestRule.onNodeWithText("Try Again").performClick()
        assert(retryClicked)
    }
    
    // ==================== Recipe Card Tests ====================
    
    @Test
    fun recipeCard_displaysName() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeCard(
                    name = "Chocolate Cookies",
                    description = "Delicious cookies",
                    imageUrl = null,
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 12,
                    servings = 24,
                    difficulty = "Easy",
                    isFavorite = false,
                    onCardClick = {},
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Chocolate Cookies").assertIsDisplayed()
    }
    
    @Test
    fun recipeCard_displaysDescription() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeCard(
                    name = "Cookies",
                    description = "Delicious homemade cookies",
                    imageUrl = null,
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 12,
                    servings = 24,
                    difficulty = "Easy",
                    isFavorite = false,
                    onCardClick = {},
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Delicious homemade cookies").assertIsDisplayed()
    }
    
    @Test
    fun recipeCard_displaysCookingTime() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeCard(
                    name = "Cookies",
                    description = "Description",
                    imageUrl = null,
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 12,
                    servings = 24,
                    difficulty = "Easy",
                    isFavorite = false,
                    onCardClick = {},
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("27 min").assertIsDisplayed()
    }
    
    @Test
    fun recipeCard_displaysServings() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeCard(
                    name = "Cookies",
                    description = "Description",
                    imageUrl = null,
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 12,
                    servings = 24,
                    difficulty = "Easy",
                    isFavorite = false,
                    onCardClick = {},
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("24 servings").assertIsDisplayed()
    }
    
    @Test
    fun recipeCard_displaysDifficulty() {
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeCard(
                    name = "Cookies",
                    description = "Description",
                    imageUrl = null,
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 12,
                    servings = 24,
                    difficulty = "Easy",
                    isFavorite = false,
                    onCardClick = {},
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Easy").assertIsDisplayed()
    }
    
    @Test
    fun recipeCard_isClickable() {
        var clicked = false
        
        composeTestRule.setContent {
            BakingAppTheme {
                RecipeCard(
                    name = "Clickable Card",
                    description = "Description",
                    imageUrl = null,
                    prepTimeMinutes = 15,
                    cookTimeMinutes = 12,
                    servings = 24,
                    difficulty = "Easy",
                    isFavorite = false,
                    onCardClick = { clicked = true },
                    onFavoriteClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Clickable Card").performClick()
        assert(clicked)
    }
}




