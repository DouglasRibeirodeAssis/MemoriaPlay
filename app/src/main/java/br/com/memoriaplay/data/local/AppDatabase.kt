package br.com.memoriaplay.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.memoriaplay.data.local.dao.PartidaDao
import br.com.memoriaplay.data.local.dao.TemaDao
import br.com.memoriaplay.data.local.dao.UsuarioDao
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity
import br.com.memoriaplay.data.local.entity.UsuarioEntity

@Database(
    entities = [TemaEntity::class, PartidaEntity::class, UsuarioEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun temaDao(): TemaDao
    abstract fun partidaDao(): PartidaDao
    abstract fun usuarioDao(): UsuarioDao

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
            )
                .fallbackToDestructiveMigration(true)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        popularTemas(db)
                        popularUsuarios(db)
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        val cursorTemas = db.query("SELECT COUNT(*) FROM temas")
                        if (cursorTemas.moveToFirst() && cursorTemas.getInt(0) == 0) {
                            popularTemas(db)
                        }
                        cursorTemas.close()

                        val cursorUsuarios = db.query("SELECT COUNT(*) FROM usuarios")
                        if (cursorUsuarios.moveToFirst() && cursorUsuarios.getInt(0) == 0) {
                            popularUsuarios(db)
                        }
                        cursorUsuarios.close()
                    }

                    private fun popularTemas(db: SupportSQLiteDatabase) {
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

                    private fun popularUsuarios(db: SupportSQLiteDatabase) {
                        db.execSQL("INSERT INTO usuarios (usuario, senha, tipo) VALUES ('admin', '1234', 'ADMIN')")
                        db.execSQL("INSERT INTO usuarios (usuario, senha, tipo) VALUES ('jogador', '1234', 'JOGADOR')")
                    }
                }).build()
        }
    }
}
