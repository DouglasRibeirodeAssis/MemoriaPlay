package br.com.memoriaplay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partidas")
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jogador: String,
    val modo: String,
    val quantidadePares: Int,
    val tempoSegundos: Int,
    val tentativas: Int,
    val dataHora: Long,
    val nomeTema: String,
    val temaId: Long? = null,
    val venceu: Boolean = true
)
