package br.com.memoriaplay.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.isSpecified

private fun TextStyle.escalar(fator: Float): TextStyle = copy(
    fontSize = if (fontSize.isSpecified) fontSize * fator else fontSize,
    lineHeight = if (lineHeight.isSpecified) lineHeight * fator else lineHeight
)

/** Estilos de texto do Material com o tamanho multiplicado pelo fator (1.0 = normal). */
fun tipografiaEscalada(fator: Float): Typography {
    val base = Typography()
    return Typography(
        displayLarge = base.displayLarge.escalar(fator),
        displayMedium = base.displayMedium.escalar(fator),
        displaySmall = base.displaySmall.escalar(fator),
        headlineLarge = base.headlineLarge.escalar(fator),
        headlineMedium = base.headlineMedium.escalar(fator),
        headlineSmall = base.headlineSmall.escalar(fator),
        titleLarge = base.titleLarge.escalar(fator),
        titleMedium = base.titleMedium.escalar(fator),
        titleSmall = base.titleSmall.escalar(fator),
        bodyLarge = base.bodyLarge.escalar(fator),
        bodyMedium = base.bodyMedium.escalar(fator),
        bodySmall = base.bodySmall.escalar(fator),
        labelLarge = base.labelLarge.escalar(fator),
        labelMedium = base.labelMedium.escalar(fator),
        labelSmall = base.labelSmall.escalar(fator)
    )
}
