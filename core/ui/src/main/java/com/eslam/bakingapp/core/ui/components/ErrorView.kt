package com.eslam.bakingapp.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eslam.bakingapp.core.ui.theme.BakingAppTheme

/**
 * Types of errors that can be displayed.
 */
enum class ErrorType {
    NETWORK,
    SERVER,
    GENERAL
}

/**
 * Standard error view with icon, message, and retry button.
 */
@Composable
fun ErrorView(
    modifier: Modifier = Modifier,
    errorType: ErrorType = ErrorType.GENERAL,
    title: String = "Oops! Something went wrong",
    message: String = "We couldn't load the content. Please try again.",
    onRetry: (() -> Unit)? = null,
    onSecondaryAction: (() -> Unit)? = null,
    secondaryActionLabel: String = "Go Back"
) {
    val icon: ImageVector = when (errorType) {
        ErrorType.NETWORK -> Icons.Default.WifiOff
        ErrorType.SERVER -> Icons.Default.CloudOff
        ErrorType.GENERAL -> Icons.Default.Error
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (onRetry != null) {
            Button(
                onClick = onRetry
            ) {
                Text(text = "Try Again")
            }
        }
        
        if (onSecondaryAction != null) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onSecondaryAction
            ) {
                Text(text = secondaryActionLabel)
            }
        }
    }
}

/**
 * Compact error view for inline display.
 */
@Composable
fun CompactErrorView(
    modifier: Modifier = Modifier,
    message: String = "Something went wrong",
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onRetry
            ) {
                Text(text = "Retry")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorViewPreview() {
    BakingAppTheme {
        ErrorView(
            errorType = ErrorType.NETWORK,
            title = "No Internet Connection",
            message = "Please check your network settings and try again.",
            onRetry = {},
            onSecondaryAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompactErrorViewPreview() {
    BakingAppTheme {
        CompactErrorView(
            message = "Failed to load recipes",
            onRetry = {}
        )
    }
}




