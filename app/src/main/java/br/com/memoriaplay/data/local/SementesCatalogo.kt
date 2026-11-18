package br.com.memoriaplay.data.local

import androidx.sqlite.db.SupportSQLiteDatabase

/** Executada apenas na criação do banco ou na migração 2->3. */
internal object SementesCatalogo {
    private val baralhos = mapOf(
        "animais" to listOf(
            "animal_borboleta" to "Borboleta",
            "animal_cachorro" to "Cachorro",
            "animal_coelho" to "Coelho",
            "animal_coruja" to "Coruja",
            "animal_elefante" to "Elefante",
            "animal_gato" to "Gato",
            "animal_girafa" to "Girafa",
            "animal_leao" to "Leão",
            "animal_macaco" to "Macaco",
            "animal_peixe" to "Peixe",
            "animal_tartaruga" to "Tartaruga",
            "animal_urso" to "Urso"
        ),
        "frutas" to listOf(
            "fruta_abacaxi" to "Abacaxi",
            "fruta_banana" to "Banana",
            "fruta_cereja" to "Cereja",
            "fruta_kiwi" to "Kiwi",
            "fruta_laranja" to "Laranja",
            "fruta_limao" to "Limão",
            "fruta_maca" to "Maçã",
            "fruta_manga" to "Manga",
            "fruta_melancia" to "Melancia",
            "fruta_morango" to "Morango",
            "fruta_pera" to "Pera",
            "fruta_uva" to "Uva"
        ),
        "formas" to listOf(
            "forma_circulo" to "Círculo",
            "forma_coracao" to "Coração",
            "forma_cruz" to "Cruz",
            "forma_estrela" to "Estrela",
            "forma_hexagono" to "Hexagono",
            "forma_losango" to "Losango",
            "forma_lua" to "Lua",
            "forma_oval" to "Oval",
            "forma_pentagono" to "Pentagono",
            "forma_quadrado" to "Quadrado",
            "forma_seta" to "Seta",
            "forma_triangulo" to "Triângulo"
        ),
        "natureza" to listOf(
            "natureza_arcobaleno" to "Arco-íris",
            "natureza_arvore" to "Árvore",
            "natureza_flor" to "Flor",
            "natureza_fogo" to "Fogo",
            "natureza_folha" to "Folha",
            "natureza_gota" to "Gota",
            "natureza_montanha" to "Montanha",
            "natureza_neve" to "Neve",
            "natureza_nuvem" to "Nuvem",
            "natureza_pedra" to "Pedra",
            "natureza_raio" to "Raio",
            "natureza_sol" to "Sol"
        ),
        "objetos" to listOf(
            "objeto_bola" to "Bola",
            "objeto_carro" to "Carro",
            "objeto_chave" to "Chave",
            "objeto_guardachuva" to "Guarda-chuva",
            "objeto_lampada" to "Lâmpada",
            "objeto_livro" to "Livro",
            "objeto_mochila" to "Mochila",
            "objeto_oculos" to "Óculos",
            "objeto_relogio" to "Relógio",
            "objeto_telefone" to "Telefone",
            "objeto_tesoura" to "Tesoura",
            "objeto_xicara" to "Xícara"
        ),
        "profissões" to listOf(
            "profissao_bombeiro" to "Bombeiro",
            "profissao_cientista" to "Cientista",
            "profissao_cozinheiro" to "Cozinheiro",
            "profissao_dentista" to "Dentista",
            "profissao_engenheiro" to "Engenheiro",
            "profissao_fazendeiro" to "Fazendeiro",
            "profissao_medico" to "Médico",
            "profissao_musico" to "Músico",
            "profissao_piloto" to "Piloto",
            "profissao_pintor" to "Pintor",
            "profissao_policial" to "Policial",
            "profissao_professor" to "Professor"
        )
    )

    fun popular(db: SupportSQLiteDatabase) {
        val temas = db.query("SELECT id, nome FROM temas").use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(cursor.getLong(0) to cursor.getString(1))
                }
            }
        }
        temas.forEach { (temaId, nomeTema) ->
                db.execSQL(
                    "INSERT INTO categorias (temaId, nome, ativo, ordem) VALUES (?, ?, 1, 0)",
                    arrayOf(temaId, "Principal")
                )
                val categoriaId = db.query("SELECT last_insert_rowid()").use { idCursor ->
                    idCursor.moveToFirst()
                    idCursor.getLong(0)
                }
                // Temas personalizados anteriores à migração já eram jogáveis
                // com imagens de Objetos: preserve esse comportamento em dados reais.
                val cartas = baralhos[nomeTema.trim().lowercase()]
                    ?: baralhos.getValue("objetos")
                cartas.forEachIndexed { ordem, (recurso, nomeCarta) ->
                    db.execSQL(
                        "INSERT INTO cartas (categoriaId, nome, imagemRecurso, ativo, ordem) " +
                            "VALUES (?, ?, ?, 1, ?)",
                        arrayOf(categoriaId, nomeCarta, recurso, ordem)
                    )
                }
        }
    }
}
