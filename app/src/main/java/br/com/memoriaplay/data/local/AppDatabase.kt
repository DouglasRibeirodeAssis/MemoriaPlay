package br.com.memoriaplay.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.memoriaplay.data.local.dao.CartaDao
import br.com.memoriaplay.data.local.dao.CategoriaDao
import br.com.memoriaplay.data.local.dao.PartidaDao
import br.com.memoriaplay.data.local.dao.TemaDao
import br.com.memoriaplay.data.local.dao.UsuarioDao
import br.com.memoriaplay.data.local.entity.CartaEntity
import br.com.memoriaplay.data.local.entity.CategoriaEntity
import br.com.memoriaplay.data.local.entity.PartidaEntity
import br.com.memoriaplay.data.local.entity.TemaEntity
import br.com.memoriaplay.data.local.entity.UsuarioEntity

@Database(
    entities = [TemaEntity::class, PartidaEntity::class, UsuarioEntity::class,
        CategoriaEntity::class, CartaEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriaDao(): CategoriaDao
    abstract fun cartaDao(): CartaDao
    abstract fun temaDao(): TemaDao
    abstract fun partidaDao(): PartidaDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        /** Atualiza a base do Integrante 3 (v1) sem apagar temas ou partidas. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `usuarios` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `usuario` TEXT NOT NULL,
                        `senha` TEXT NOT NULL,
                        `tipo` TEXT NOT NULL
                    )""".trimIndent()
                )
                popularUsuarios(db)
            }
        }

        /** Mantém intactos temas, partidas e usuários já existentes. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS `categorias` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `temaId` INTEGER NOT NULL,
                    `nome` TEXT NOT NULL,
                    `ativo` INTEGER NOT NULL,
                    `ordem` INTEGER NOT NULL,
                    FOREIGN KEY(`temaId`) REFERENCES `temas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )""".trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_categorias_temaId` ON `categorias` (`temaId`)")
                db.execSQL("""CREATE TABLE IF NOT EXISTS `cartas` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `categoriaId` INTEGER NOT NULL,
                    `nome` TEXT NOT NULL,
                    `imagemRecurso` TEXT NOT NULL,
                    `ativo` INTEGER NOT NULL,
                    `ordem` INTEGER NOT NULL,
                    FOREIGN KEY(`categoriaId`) REFERENCES `categorias`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )""".trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_cartas_categoriaId` ON `cartas` (`categoriaId`)")
                SementesCatalogo.popular(db)
            }
        }

        fun obter(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: criar(context).also { instancia = it }
            }
        }

        private fun criar(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "memoria_play.db"
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        popularTemas(db)
                        popularUsuarios(db)
                        SementesCatalogo.popular(db)
                    }
                })
                .build()

        // Executar somente na criação de um banco novo. Nunca repovoar no onOpen.
        private fun popularTemas(db: SupportSQLiteDatabase) {
            listOf(
                "Animais", "Frutas", "Formas",
                "Natureza", "Objetos", "Profissões"
            ).forEachIndexed { index, nome ->
                db.execSQL(
                    "INSERT INTO temas (nome, ativo, ordem) VALUES (?, 1, ?)",
                    arrayOf(nome, index)
                )
            }
        }

        private fun popularUsuarios(db: SupportSQLiteDatabase) {
            // Credenciais apenas para a demonstração acadêmica local.
            db.execSQL(
                "INSERT INTO usuarios (usuario, senha, tipo) VALUES ('admin', '1234', 'ADMIN')"
            )
            db.execSQL(
                "INSERT INTO usuarios (usuario, senha, tipo) VALUES ('jogador', '1234', 'JOGADOR')"
            )
        }
    }
}
