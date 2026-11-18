package br.com.memoriaplay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val usuario: String,
    val senha: String,
    val tipo: String // "ADMIN" ou "JOGADOR"
)
