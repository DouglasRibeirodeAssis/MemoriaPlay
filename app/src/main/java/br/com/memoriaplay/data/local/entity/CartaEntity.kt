package br.com.memoriaplay.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cartas",
    foreignKeys = [ForeignKey(
        entity = CategoriaEntity::class,
        parentColumns = ["id"],
        childColumns = ["categoriaId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["categoriaId"])]
)
data class CartaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoriaId: Long,
    val nome: String,
    /** Chave de um recurso drawable disponível no app; não é caminho externo. */
    val imagemRecurso: String,
    val ativo: Boolean = true,
    val ordem: Int = 0
)
