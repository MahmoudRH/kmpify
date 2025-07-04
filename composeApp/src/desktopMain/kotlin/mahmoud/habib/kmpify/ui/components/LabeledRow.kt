package mahmoud.habib.kmpify.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mahmoud.habib.kmpify.utils.BoldSlashVisualTransformation

@Composable
fun LabeledRow(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    supportingText: String? = null,
    onBrowseClick: (() -> Unit)? = null
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),

            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            visualTransformation = BoldSlashVisualTransformation(isSystemInDarkTheme()),
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            supportingText = supportingText?.let {
                {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light)
                    )
                }
            },
            placeholder = placeholder?.let {
                {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light)
                    )
                }
            },

            )

        if (onBrowseClick != null) {
            Button(onClick = onBrowseClick) {
                Text("Browse")
            }
        }
    }
}