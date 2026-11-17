# Memória Play — Integrante 3

## Responsabilidade

Catálogo de temas e banco de dados local Room. Esta implementação usa a
estrutura inicial do projeto e não altera o código dos demais integrantes.

## Arquivos implementados

- `TemaEntity.kt`: tabela `temas`.
- `PartidaEntity.kt`: tabela `partidas` e histórico.
- `TemaDao.kt`: consultas, filtros e CRUD de temas.
- `PartidaDao.kt`: consultas, filtros, CRUD e estatísticas das partidas.
- `AppDatabase.kt`: banco Room único com dados iniciais de temas.
- `TemaCatalogo.kt`: modelo utilizado pelo catálogo.
- `CatalogoRepository.kt`: acesso aos temas e operações para a administração.
- `CatalogoViewModel.kt`: busca, filtros, ordenação e seleção do tema.
- `CatalogoUiState.kt`: estado da tela.
- `CatalogoScreen.kt`: interface Jetpack Compose.
- `CatalogoViewModelTest.kt`: testes de busca, filtros e seleção.
- `TemaDaoTest.kt`: teste do CRUD de temas.
- `PartidaDaoTest.kt`: CRUD e persistência após reabrir o banco.

## Para integrar com os demais módulos

- Integrante 1: usar `CatalogoScreen` com uma instância de
  `CatalogoViewModel` criada por `CatalogoViewModel.fabrica(repositorio)`.
- Integrante 2: receber o ID pelo callback `onSelecionarTema` e buscar o tema
  em `CatalogoRepository.buscarTema(id)` ou `TemaDao.buscarPorId(id)`.
- Integrante 4: usar `CatalogoRepository` para cadastrar, editar e excluir
  temas; `TemaDao` também oferece CRUD completo.
- Integrante 5: usar `AppDatabase.obter(context).partidaDao()` para ranking.
- Integrante 1: usar os fluxos `totalPartidas`, `totalVitorias` e `melhorTempo`
  de `PartidaDao` para o resumo da Home.

## Configuração compartilhada

Foram adicionadas dependências do Room, KSP, ViewModel e testes no Gradle.
Para compatibilidade com a escola, o projeto usa AGP 9.0.0 e Gradle 9.1.0.

## Testes e validação

Execute a sincronização do Gradle, o build e os testes no Android Studio.
`CatalogoViewModelTest` é teste de unidade. `TemaDaoTest` e `PartidaDaoTest`
são testes instrumentados, que precisam de dispositivo ou emulador Android.

O código ainda precisa de validação de compilação e execução no Android Studio.
A navegação e a `MainActivity` permanecem na condição original e deverão
ser implementadas pelo Integrante 1.

## Exemplo para o Integrante 1 conectar a tela

```kotlin
val banco = AppDatabase.obter(context)
val repositorio = CatalogoRepository(banco.temaDao())
val vm: CatalogoViewModel = viewModel(
    factory = CatalogoViewModel.fabrica(repositorio)
)
CatalogoScreen(vm) { idTema ->
    println(idTema)
}
```

Esse exemplo é apenas documentação; a navegação não foi editada neste pacote.
