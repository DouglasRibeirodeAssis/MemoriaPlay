package br.com.memoriaplay.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import br.com.memoriaplay.features.config.model.PreferenciasJogo

private val EsquemaNormal = lightColorScheme()

private val EsquemaAltoContraste = lightColorScheme(
    primary = ContrasteAzul,
    onPrimary = ContrasteBranco,
    primaryContainer = ContrasteAzulClaro,
    onPrimaryContainer = ContrastePreto,
    inversePrimary = ContrasteAzulSuave,
    secondary = ContrasteVerde,
    onSecondary = ContrasteBranco,
    secondaryContainer = ContrasteAmarelo,
    onSecondaryContainer = ContrastePreto,
    tertiary = ContrasteRoxo,
    onTertiary = ContrasteBranco,
    tertiaryContainer = ContrasteAzulClaro,
    onTertiaryContainer = ContrastePreto,
    background = ContrasteBranco,
    onBackground = ContrastePreto,
    surface = ContrasteBranco,
    onSurface = ContrastePreto,
    surfaceVariant = ContrasteCinzaClaro,
    onSurfaceVariant = ContrastePreto,
    surfaceTint = ContrasteAzul,
    inverseSurface = ContrastePreto,
    inverseOnSurface = ContrasteBranco,
    error = ContrasteVermelho,
    onError = ContrasteBranco,
    errorContainer = ContrasteVermelhoClaro,
    onErrorContainer = ContrasteVermelhoEscuro,
    outline = ContrastePreto,
    outlineVariant = ContrasteCinzaEscuro,
    scrim = ContrastePreto,
    surfaceBright = ContrasteBranco,
    surfaceDim = ContrasteCinzaMedio,
    surfaceContainerLowest = ContrasteBranco,
    surfaceContainerLow = ContrasteCinzaMuitoClaro,
    surfaceContainer = ContrasteCinzaSuave,
    surfaceContainerHigh = ContrasteCinzaClaro,
    surfaceContainerHighest = ContrasteCinzaMedio
)

/** Tema do app: aplica o alto contraste e o tamanho do texto escolhidos. */
@Composable
fun MemoriaPlayTheme(
    preferencias: PreferenciasJogo = PreferenciasJogo(),
    content: @Composable () -> Unit
) {
    val cores = if (preferencias.altoContraste) EsquemaAltoContraste else EsquemaNormal
    val tipografia = remember(preferencias.escalaTexto) {
        tipografiaEscalada(preferencias.escalaTexto.fator)
    }
    MaterialTheme(
        colorScheme = cores,
        typography = tipografia,
        content = content
    )
}
