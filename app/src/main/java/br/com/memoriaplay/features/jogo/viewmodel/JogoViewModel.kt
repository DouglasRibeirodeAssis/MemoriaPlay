package br.com.memoriaplay.features.jogo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.memoriaplay.features.jogo.data.JogoRepository
import br.com.memoriaplay.features.jogo.domain.RegrasJogo
import br.com.memoriaplay.features.jogo.model.ConfiguracaoPartida
import br.com.memoriaplay.features.jogo.ui.JogoUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class JogoViewModel(
    private val repository: JogoRepository,
    private val agoraMillis: () -> Long = {
        System.nanoTime() / 1_000_000L
    }
) : ViewModel() {
    private val _uiState = MutableStateFlow(JogoUiState())
    val uiState = _uiState.asStateFlow()

    private var inicioMillis = 0L
    private var versaoPartida = 0
    private var cronometroJob: Job? = null
    private var comparacaoJob: Job? = null
    private var carregamentoJob: Job? = null
    private var salvamentoJob: Job? = null
    private var salvamentoIniciado = false
    private var ultimaEntrada: String? = null

    fun iniciarSeNecessario(
        entradaId: String,
        configuracao: ConfiguracaoPartida
    ) {
        if (ultimaEntrada == entradaId) return
        ultimaEntrada = entradaId
        iniciarPartida(configuracao)
    }

    fun iniciarPartida(configuracao: ConfiguracaoPartida) {
        versaoPartida++
        val versaoAtual = versaoPartida
        cronometroJob?.cancel()
        comparacaoJob?.cancel()
        carregamentoJob?.cancel()
        salvamentoJob?.cancel()
        salvamentoIniciado = false

        _uiState.value = JogoUiState(
            carregando = true,
            configuracao = configuracao
        )

        carregamentoJob = viewModelScope.launch {
            val resultado = repository.carregarTema(configuracao.temaId)
            if (versaoAtual != versaoPartida) return@launch

            resultado.fold(
                onSuccess = { tema ->
                    val disponiveis = tema.imagens.distinctBy { it.simbolo }.size
                    if (disponiveis < configuracao.quantidadePares) {
                        _uiState.update { it.copy(
                            carregando = false,
                            nomeTema = tema.nome,
                            mensagemErro = "Este tema possui $disponiveis imagens diferentes; " +
                                "o nível escolhido exige ${configuracao.quantidadePares}. " +
                                "Cadastre mais cartas no painel administrativo."
                        ) }
                    } else {
                        val cartas = RegrasJogo.criarCartas(
                            tema.imagens,
                            configuracao.quantidadePares
                        )
                        _uiState.value = JogoUiState(
                            configuracao = configuracao.copy(temaId = tema.id),
                            nomeTema = tema.nome,
                            cartas = cartas,
                            mensagem = "Encontre os pares iguais!"
                        )
                        iniciarCronometro()
                    }
                },
                onFailure = { erro ->
                    _uiState.update {
                        it.copy(
                            carregando = false,
                            mensagemErro = erro.message
                                ?: "Erro ao carregar o tema."
                        )
                    }
                }
            )
        }
    }

    private fun iniciarCronometro() {
        inicioMillis = agoraMillis()
        cronometroJob = viewModelScope.launch {
            while (isActive && !_uiState.value.finalizado) {
                delay(250L)
                _uiState.update {
                    it.copy(tempoSegundos = tempoAtual())
                }
            }
        }
    }

    private fun tempoAtual(): Int {
        return ((agoraMillis() - inicioMillis)
            .coerceAtLeast(0L) / 1_000L).toInt()
    }

    fun selecionarCarta(id: Int) {
        val estado = _uiState.value
        if (estado.carregando || estado.finalizado ||
            estado.bloqueado || estado.cartas.isEmpty()
        ) return

        val carta = estado.cartas.firstOrNull { it.id == id } ?: return
        if (carta.revelada || carta.combinada) return

        val atualizadas = estado.cartas.map {
            if (it.id == id) it.copy(revelada = true) else it
        }

        val primeiraId = estado.primeiraCartaId
        if (primeiraId == null) {
            _uiState.value = estado.copy(
                cartas = atualizadas,
                primeiraCartaId = id,
                mensagem = "Escolha mais uma carta."
            )
            return
        }

        val primeira = atualizadas.first { it.id == primeiraId }
        val segunda = atualizadas.first { it.id == id }
        val acertou = RegrasJogo.formamPar(primeira, segunda)

        _uiState.value = estado.copy(
            cartas = atualizadas,
            bloqueado = true,
            tentativas = estado.tentativas + 1,
            mensagem = if (acertou) "Par encontrado!" else "Tente novamente!"
        )

        if (acertou) {
            resolverTentativa(primeiraId, id, true)
        } else {
            comparacaoJob = viewModelScope.launch {
                delay(700L)
                resolverTentativa(primeiraId, id, false)
            }
        }
    }

    private fun resolverTentativa(
        primeiraId: Int,
        segundaId: Int,
        acertou: Boolean
    ) {
        val atual = _uiState.value
        val ids = setOf(primeiraId, segundaId)
        val cartas = atual.cartas.map { carta ->
            if (carta.id !in ids) carta
            else if (acertou) carta.copy(combinada = true)
            else carta.copy(revelada = false)
        }

        _uiState.update {
            it.copy(
                cartas = cartas,
                primeiraCartaId = null,
                bloqueado = false,
                paresEncontrados = cartas.count { carta ->
                    carta.combinada
                } / 2
            )
        }

        if (RegrasJogo.venceu(cartas)) finalizarPartida()
    }

    private fun finalizarPartida() {
        cronometroJob?.cancel()
        _uiState.update {
            it.copy(
                tempoSegundos = tempoAtual(),
                finalizado = true,
                mensagem = "Você encontrou todos os pares!"
            )
        }
        salvarResultado()
    }

    fun tentarSalvarNovamente() {
        val estado = _uiState.value
        if (estado.finalizado && !estado.resultadoSalvo &&
            !estado.salvando
        ) {
            salvarResultado()
        }
    }

    private fun salvarResultado() {
        if (salvamentoIniciado) return
        val estado = _uiState.value
        val configuracao = estado.configuracao ?: return
        salvamentoIniciado = true

        _uiState.update {
            it.copy(salvando = true, mensagemErro = null)
        }

        salvamentoJob = viewModelScope.launch {
            val resultado = repository.salvarResultado(
                configuracao = configuracao,
                nomeTema = estado.nomeTema,
                tempoSegundos = estado.tempoSegundos,
                tentativas = estado.tentativas
            )
            resultado.fold(
                onSuccess = {
                    _uiState.update { atual ->
                        atual.copy(
                            salvando = false,
                            resultadoSalvo = true
                        )
                    }
                },
                onFailure = { erro ->
                    salvamentoIniciado = false
                    _uiState.update { atual ->
                        atual.copy(
                            salvando = false,
                            mensagemErro = erro.message
                                ?: "Não foi possível salvar a partida."
                        )
                    }
                }
            )
        }
    }

    companion object {
        fun fabrica(repository: JogoRepository) = viewModelFactory {
            initializer { JogoViewModel(repository) }
        }
    }
}
