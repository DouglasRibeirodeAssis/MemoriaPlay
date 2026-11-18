package br.com.memoriaplay.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categorias",
    foreignKeys = [ForeignKey(
        entity = TemaEntity::class,
        parentColumns = ["id"],
        childColumns = ["temaId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["temaId"])]
)
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val temaId: Long,
    val nome: String,
    val ativo: Boolean = true,
    val ordem: Int = 0
)
