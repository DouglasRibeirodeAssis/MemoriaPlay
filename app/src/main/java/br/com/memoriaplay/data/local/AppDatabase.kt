package br.com.memoriaplay.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.memoriaplay.data.local.dao.PartidaDao
import br.com.memoriaplay.data.local.dao.TemaDao
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity

@Database(
    entities = [TemaEntity::class, PartidaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun temaDao(): TemaDao
    abstract fun partidaDao(): PartidaDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obter(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: criar(context).also { instancia = it }
            }
        }

        private fun criar(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "memoria_play.db"
            ).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val nomes = listOf(
                        "Animais", "Frutas", "Formas",
                        "Natureza", "Objetos", "Profissões"
                    )
                    nomes.forEachIndexed { index, nome ->
                        db.execSQL(
                            "INSERT INTO temas (nome, ativo, ordem) VALUES (?, 1, ?)",
                            arrayOf(nome, index)
                        )
                    }
                }
            }).build()
        }
    }
}
