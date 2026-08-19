package com.hooshkar.materialcolorpickercompose.internal

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

/**
 * A single-line text field that mirrors [displayValue] whenever it is not focused (so external
 * color changes update it), but leaves the user's in-progress input alone while they're typing,
 * calling [onValueChange] with the raw (already-filtered) text on every keystroke.
 */
@Composable
internal fun SyncedTextField(
    displayValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    keyboardType: KeyboardType = KeyboardType.Text,
    textStyle: TextStyle = TextStyle.Default,
    filter: (String) -> String = { it }
) {
    var isFocused by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(displayValue) }

    LaunchedEffect(displayValue, isFocused) {
        if (!isFocused) text = displayValue
    }

    BasicTextField(
        value = text,
        onValueChange = { raw ->
            val filtered = filter(raw).take(maxLength)
            text = filtered
            onValueChange(filtered)
        },
        modifier = modifier.onFocusChanged { isFocused = it.isFocused },
        enabled = enabled,
        singleLine = true,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Done)
    )
}

internal fun digitsOnly(text: String): String = text.filter { it.isDigit() }

internal fun hexDigitsOnly(text: String): String =
    text.filter { it.isDigit() || it.uppercaseChar() in 'A'..'F' }.uppercase()
