package br.com.memoriaplay.features.jogo.data

import br.com.memoriaplay.R
import br.com.memoriaplay.data.local.dao.CartaDao
import br.com.memoriaplay.data.local.dao.PartidaDao
import br.com.memoriaplay.data.local.dao.TemaDao
import br.com.memoriaplay.data.local.entity.CartaEntity
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity
import br.com.memoriaplay.features.catalogo.data.RecursosCartas
import br.com.memoriaplay.features.jogo.model.ConfiguracaoPartida
import br.com.memoriaplay.features.jogo.model.ImagemCarta
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

interface JogoDataSource {
    suspend fun buscarTema(temaId: Long?): TemaEntity?
    suspend fun inserirPartida(partida: PartidaEntity): Long
    // Implementação padrão mantém compatibilidade com fontes falsas dos testes do jogo.
    suspend fun buscarCartas(temaId: Long): List<CartaEntity>? = null
}

class RoomJogoDataSource(
    private val temaDao: TemaDao,
    private val partidaDao: PartidaDao,
    private val cartaDao: CartaDao? = null
) : JogoDataSource {
    override suspend fun buscarTema(temaId: Long?): TemaEntity? {
        return if (temaId == null) {
            temaDao.observarAtivos().first().firstOrNull()
        } else {
            temaDao.buscarPorId(temaId)
        }
    }

    override suspend fun buscarCartas(temaId: Long): List<CartaEntity> =
        cartaDao?.buscarAtivasPorTema(temaId).orEmpty()

    override suspend fun inserirPartida(partida: PartidaEntity): Long {
        return partidaDao.inserir(partida)
    }
}

data class TemaPartida(
    val id: Long,
    val nome: String,
    val imagens: List<ImagemCarta>
)

class JogoRepository(private val fonte: JogoDataSource) {
    suspend fun carregarTema(temaId: Long?): Result<TemaPartida> {
        return executar {
            val tema = fonte.buscarTema(temaId)
                ?: error("Nenhum tema disponível para jogar.")

            check(tema.ativo) {
                "Este tema não está disponível para jogar."
            }

            val cartasDoBanco = fonte.buscarCartas(tema.id)
            // Fonte em memória dos testes antigos usa baralhos fixos; Room usa somente
            // cartas cadastradas, de forma que a exclusão no Admin afeta o jogo.
            val imagens = if (cartasDoBanco == null) imagensDoTema(tema.nome)
            else cartasDoBanco.mapNotNull { carta ->
                RecursosCartas.buscar(carta.imagemRecurso)?.let { recurso ->
                    ImagemCarta(recurso.chave, carta.nome, recurso.drawable)
                }
            }.distinctBy { it.simbolo }.take(12)
            check(imagens.size >= 3) {
                "Cadastre pelo menos 3 cartas diferentes para jogar neste tema."
            }

            TemaPartida(tema.id, tema.nome, imagens)
        }
    }

    suspend fun salvarResultado(
        configuracao: ConfiguracaoPartida,
        nomeTema: String,
        tempoSegundos: Int,
        tentativas: Int
    ): Result<Long> {
        return executar {
            fonte.inserirPartida(
                PartidaEntity(
                    jogador = configuracao.jogador,
                    modo = configuracao.nivel.titulo,
                    quantidadePares = configuracao.quantidadePares,
                    tempoSegundos = tempoSegundos,
                    tentativas = tentativas,
                    dataHora = System.currentTimeMillis(),
                    nomeTema = nomeTema,
                    temaId = configuracao.temaId,
                    venceu = true
                )
            )
        }
    }

    private suspend fun <T> executar(
        operacao: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(operacao())
        } catch (erro: CancellationException) {
            throw erro
        } catch (erro: Exception) {
            Result.failure(erro)
        }
    }

    private fun imagensDoTema(nome: String): List<ImagemCarta> {
        return when (nome.trim().lowercase()) {
            "animais" -> listOf(
                ImagemCarta("animal_borboleta", "Borboleta", R.drawable.animal_borboleta),
                ImagemCarta("animal_cachorro", "Cachorro", R.drawable.animal_cachorro),
                ImagemCarta("animal_coelho", "Coelho", R.drawable.animal_coelho),
                ImagemCarta("animal_coruja", "Coruja", R.drawable.animal_coruja),
                ImagemCarta("animal_elefante", "Elefante", R.drawable.animal_elefante),
                ImagemCarta("animal_gato", "Gato", R.drawable.animal_gato),
                ImagemCarta("animal_girafa", "Girafa", R.drawable.animal_girafa),
                ImagemCarta("animal_leao", "Leão", R.drawable.animal_leao),
                ImagemCarta("animal_macaco", "Macaco", R.drawable.animal_macaco),
                ImagemCarta("animal_peixe", "Peixe", R.drawable.animal_peixe),
                ImagemCarta("animal_tartaruga", "Tartaruga", R.drawable.animal_tartaruga),
                ImagemCarta("animal_urso", "Urso", R.drawable.animal_urso)
            )
            "frutas" -> listOf(
                ImagemCarta("fruta_abacaxi", "Abacaxi", R.drawable.fruta_abacaxi),
                ImagemCarta("fruta_banana", "Banana", R.drawable.fruta_banana),
                ImagemCarta("fruta_cereja", "Cereja", R.drawable.fruta_cereja),
                ImagemCarta("fruta_kiwi", "Kiwi", R.drawable.fruta_kiwi),
                ImagemCarta("fruta_laranja", "Laranja", R.drawable.fruta_laranja),
                ImagemCarta("fruta_limao", "Limão", R.drawable.fruta_limao),
                ImagemCarta("fruta_maca", "Maçã", R.drawable.fruta_maca),
                ImagemCarta("fruta_manga", "Manga", R.drawable.fruta_manga),
                ImagemCarta("fruta_melancia", "Melancia", R.drawable.fruta_melancia),
                ImagemCarta("fruta_morango", "Morango", R.drawable.fruta_morango),
                ImagemCarta("fruta_pera", "Pera", R.drawable.fruta_pera),
                ImagemCarta("fruta_uva", "Uva", R.drawable.fruta_uva)
            )
            "formas" -> listOf(
                ImagemCarta("forma_circulo", "Círculo", R.drawable.forma_circulo),
                ImagemCarta("forma_coracao", "Coração", R.drawable.forma_coracao),
                ImagemCarta("forma_cruz", "Cruz", R.drawable.forma_cruz),
                ImagemCarta("forma_estrela", "Estrela", R.drawable.forma_estrela),
                ImagemCarta("forma_hexagono", "Hexagono", R.drawable.forma_hexagono),
                ImagemCarta("forma_losango", "Losango", R.drawable.forma_losango),
                ImagemCarta("forma_lua", "Lua", R.drawable.forma_lua),
                ImagemCarta("forma_oval", "Oval", R.drawable.forma_oval),
                ImagemCarta("forma_pentagono", "Pentagono", R.drawable.forma_pentagono),
                ImagemCarta("forma_quadrado", "Quadrado", R.drawable.forma_quadrado),
                ImagemCarta("forma_seta", "Seta", R.drawable.forma_seta),
                ImagemCarta("forma_triangulo", "Triângulo", R.drawable.forma_triangulo)
            )
            "natureza" -> listOf(
                ImagemCarta("natureza_arcobaleno", "Arco-íris", R.drawable.natureza_arcobaleno),
                ImagemCarta("natureza_arvore", "Árvore", R.drawable.natureza_arvore),
                ImagemCarta("natureza_flor", "Flor", R.drawable.natureza_flor),
                ImagemCarta("natureza_fogo", "Fogo", R.drawable.natureza_fogo),
                ImagemCarta("natureza_folha", "Folha", R.drawable.natureza_folha),
                ImagemCarta("natureza_gota", "Gota", R.drawable.natureza_gota),
                ImagemCarta("natureza_montanha", "Montanha", R.drawable.natureza_montanha),
                ImagemCarta("natureza_neve", "Neve", R.drawable.natureza_neve),
                ImagemCarta("natureza_nuvem", "Nuvem", R.drawable.natureza_nuvem),
                ImagemCarta("natureza_pedra", "Pedra", R.drawable.natureza_pedra),
                ImagemCarta("natureza_raio", "Raio", R.drawable.natureza_raio),
                ImagemCarta("natureza_sol", "Sol", R.drawable.natureza_sol)
            )
            "objetos" -> listOf(
                ImagemCarta("objeto_bola", "Bola", R.drawable.objeto_bola),
                ImagemCarta("objeto_carro", "Carro", R.drawable.objeto_carro),
                ImagemCarta("objeto_chave", "Chave", R.drawable.objeto_chave),
                ImagemCarta("objeto_guardachuva", "Guarda-chuva", R.drawable.objeto_guardachuva),
                ImagemCarta("objeto_lampada", "Lâmpada", R.drawable.objeto_lampada),
                ImagemCarta("objeto_livro", "Livro", R.drawable.objeto_livro),
                ImagemCarta("objeto_mochila", "Mochila", R.drawable.objeto_mochila),
                ImagemCarta("objeto_oculos", "Óculos", R.drawable.objeto_oculos),
                ImagemCarta("objeto_relogio", "Relógio", R.drawable.objeto_relogio),
                ImagemCarta("objeto_telefone", "Telefone", R.drawable.objeto_telefone),
                ImagemCarta("objeto_tesoura", "Tesoura", R.drawable.objeto_tesoura),
                ImagemCarta("objeto_xicara", "Xícara", R.drawable.objeto_xicara)
            )
            "profissões", "profissoes" -> listOf(
                ImagemCarta("profissao_bombeiro", "Bombeiro", R.drawable.profissao_bombeiro),
                ImagemCarta("profissao_cientista", "Cientista", R.drawable.profissao_cientista),
                ImagemCarta("profissao_cozinheiro", "Cozinheiro", R.drawable.profissao_cozinheiro),
                ImagemCarta("profissao_dentista", "Dentista", R.drawable.profissao_dentista),
                ImagemCarta("profissao_engenheiro", "Engenheiro", R.drawable.profissao_engenheiro),
                ImagemCarta("profissao_fazendeiro", "Fazendeiro", R.drawable.profissao_fazendeiro),
                ImagemCarta("profissao_medico", "Médico", R.drawable.profissao_medico),
                ImagemCarta("profissao_musico", "Músico", R.drawable.profissao_musico),
                ImagemCarta("profissao_piloto", "Piloto", R.drawable.profissao_piloto),
                ImagemCarta("profissao_pintor", "Pintor", R.drawable.profissao_pintor),
                ImagemCarta("profissao_policial", "Policial", R.drawable.profissao_policial),
                ImagemCarta("profissao_professor", "Professor", R.drawable.profissao_professor)
            )
            // Temas criados no painel ainda não possuem um pacote próprio de imagens.
            // Usamos o baralho genérico existente para permitir partidas com 3 a 12 pares.
            else -> imagensDoTema("objetos")
        }
    }
}
