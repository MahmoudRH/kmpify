package mahmoud.habib.kmpify.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import mahmoud.habib.kmpify.ui.theme.DarkBluePrimary
import mahmoud.habib.kmpify.ui.theme.LightBluePrimary

class BoldSlashVisualTransformation(val isDarkTheme: Boolean) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val segments = text.text.split("/")
        val annotatedString = buildAnnotatedString {
            segments.forEachIndexed { index, segment ->
                append(segment)
                if (index < segments.size - 1) {
                    withStyle(
                        SpanStyle(
                            color = if (isDarkTheme) DarkBluePrimary else LightBluePrimary,
                            fontWeight = FontWeight(500),
                            fontSize = 18.sp
                        )
                    ) {
                        append("/")
                    }
                }
            }
        }

        return TransformedText(annotatedString, OffsetMapping.Identity)
    }
}