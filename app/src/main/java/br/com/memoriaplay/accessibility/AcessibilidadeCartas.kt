package br.com.memoriaplay.accessibility

/** Estados da carta para o leitor de tela. */
enum class EstadoCartaAcessivel { FECHADA, REVELADA, COMBINADA }

/** Textos que o TalkBack lê nas cartas e nos avisos. Carta fechada nunca mostra o que tem dentro. */
object AcessibilidadeCartas {

    fun descricao(posicao: Int): String = "Carta $posicao"

    fun estado(estado: EstadoCartaAcessivel, conteudo: String): String = when (estado) {
        EstadoCartaAcessivel.FECHADA -> "fechada"
        EstadoCartaAcessivel.REVELADA -> "revelada: $conteudo"
        EstadoCartaAcessivel.COMBINADA -> "par encontrado: $conteudo"
    }

    fun avisoParEncontrado(conteudo: String): String = "Par encontrado: $conteudo."

    fun avisoParDiferente(): String = "Cartas diferentes. Elas foram viradas de volta."

    fun avisoProgresso(paresEncontrados: Int, totalPares: Int): String =
        "$paresEncontrados de $totalPares pares encontrados."

    fun avisoVitoria(tentativas: Int, tempoSegundos: Int): String {
        val tentativasTexto = if (tentativas == 1) "1 tentativa" else "$tentativas tentativas"
        return "Parabéns! Você encontrou todos os pares em $tentativasTexto e ${tempoPorExtenso(tempoSegundos)}."
    }

    private fun tempoPorExtenso(totalSegundos: Int): String {
        val minutos = totalSegundos / 60
        val segundos = totalSegundos % 60
        val minutosTexto = if (minutos == 1) "1 minuto" else "$minutos minutos"
        val segundosTexto = if (segundos == 1) "1 segundo" else "$segundos segundos"
        return when {
            minutos == 0 -> segundosTexto
            segundos == 0 -> minutosTexto
            else -> "$minutosTexto e $segundosTexto"
        }
    }
}
