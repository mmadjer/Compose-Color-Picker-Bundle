package com.smarttoolfactory.colorpicker.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colorpicker.ui.Grey400
import com.smarttoolfactory.colorpicker.ui.Red400
import com.smarttoolfactory.colorpicker.util.hexRegexSingleChar
import com.smarttoolfactory.colorpicker.util.hexToColor
import com.smarttoolfactory.colorpicker.util.hexWithAlphaRegex

@Composable
fun HexAlphaTextField(
    modifier: Modifier = Modifier,
    hexString: String,
    textStyle: TextStyle = TextStyle(fontSize = 24.sp),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    ),
    shape: Shape = RoundedCornerShape(25),
    onTextChange: (String) -> Unit,
    onColorChange: (Color) -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .widthIn(min = 80.dp)
            .drawBehind {
                drawLine(
                    if (hexWithAlphaRegex.matches(hexString)) Grey400 else Red400,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 5f
                )
            },
        visualTransformation = HexVisualTransformation(),
        textStyle = textStyle,
        colors = colors,
        shape = shape,
        label = { Text("Hex", color = Grey400) },
        placeholder = { Text("Enter a color", fontSize = textStyle.fontSize) },
        value = hexString.removePrefix("#"),
        onValueChange = {

            if (it.length <= 8) {
                if (it.isNotEmpty()) {
                    val lastChar = it.last()
                    val isHexChar = hexRegexSingleChar.matches(lastChar.toString())
                    if (isHexChar) {
                        onTextChange(it)
                    }
                } else {
                    onTextChange(it)
                }

                // Hex String with 6 or 8 chars matches a Color
                if (hexWithAlphaRegex.matches(it)) {
                    onColorChange(hexToColor(it))
                }
            }
        }
    )
}

@Composable
private fun BasicHexTextField(
    modifier: Modifier = Modifier,
    value: String,
    textStyle: TextStyle = TextStyle(
        fontSize = 20.sp,
    ),
    textColor: Color = Color.Black,
    backgroundColor: Color = Color.LightGray,
    cursorColor: Color = MaterialTheme.colors.primary,
    visualTransformation: VisualTransformation,
    hint: String,
    shape: Shape = RoundedCornerShape(25),
    onValueChange: (String) -> Unit,
) {

    Surface(
        modifier = modifier
            .wrapContentHeight()
            .widthIn(min = 60.dp),
        shape = shape,
        color = backgroundColor,
        contentColor = textColor
    ) {
        BasicTextField(
            textStyle = textStyle,
            value = value,
            singleLine = true,
            visualTransformation = visualTransformation,
            cursorBrush = SolidColor(cursorColor),
            onValueChange = onValueChange
        ) {
            if (value.isEmpty()) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = hint,
                    style = textStyle.copy(textStyle.color.copy(.7f))
                )
            } else {
                it()
            }
        }
    }
}

private class HexVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {

        val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text

        val output = if (trimmed.isEmpty()) {
            trimmed
        } else {
            "#${trimmed.uppercase()}"
        }

        return TransformedText(AnnotatedString(output), hexOffsetMapping)
    }

    private val hexOffsetMapping = object : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {

            // when empty return only 1 char for #
            if (offset == 0) return offset
            if (offset <= 7) return offset + 1
            return 9
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset == 0) return offset
            // #ffABCABC
            if (offset <= 8) return offset - 1
            return 8
        }
    }
}
