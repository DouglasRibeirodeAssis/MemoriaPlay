# Memória Play — integração complementar dos Integrantes 3 e 4

**Data:** 18/11/2026
**Base:** `MemoriaPlay_Integrado_Corrigido_18-11-2026.zip`

## Escopo

Esta ampliação implementa o cadastro e gerenciamento individual de **categorias e cartas**, como previsto no planejamento detalhado do grupo. A ficha individual do Integrante 3 já exigia, como mínimo, as entidades Tema e Partida, o catálogo reativo e a persistência Room; essas funcionalidades foram preservadas.

## Funcionalidades acrescentadas

- **Room v3:** entidades `CategoriaEntity` (relacionada a Tema) e `CartaEntity` (relacionada a Categoria), DAOs com `Flow` e CRUD completo, índices e exclusão em cascata.
- **Migração 2 → 3 sem exclusão:** os registros existentes de temas, partidas e jogadores são mantidos; seis baralhos predefinidos recebem suas respectivas cartas. Temas personalizados anteriores à migração recebem as 12 imagens genéricas anteriormente utilizadas para jogar, agora como registros editáveis no banco.
- **Administração (I4):** formulários para cadastrar/editar/excluir categorias e cartas, seleção de temas/categorias, escolha entre 72 imagens presentes no aplicativo, controle de ativação, confirmação antes de exclusões de categoria/carta. Somente o perfil Administrador tem acesso à tela e às ações por ViewModel.
- **Catálogo (I3):** botão "Ver categorias e cartas" para cada tema, nova tela de detalhes com as cartas presentes no Room. As listagens são reativas via Repository/ViewModel.
- **Jogo (I2):** consulta as cartas ativas do tema no banco. Preserva o motor de pares e as dificuldades existentes. As cartas são deduplicadas por imagem para evitar pares visualmente idênticos. O ViewModel exibe mensagem explicativa se a dificuldade escolhida exigir mais imagens que as disponíveis, sem interromper o aplicativo.
- **Testes novos:** `CategoriaCartaDaoTest.kt` (instrumentado) e `RecursosCartasTest.kt` (unitário).

## Observações

- **Novos temas:** requerem cadastro de categoria e pelo menos 3 cartas com imagens distintas para iniciar uma partida; para Fácil, Médio e Difícil precisam, respectivamente, 3, 8 e 12 imagens distintas. Um tema sem cartas não utiliza imagens improvisadas automaticamente.
- O administrador escolhe imagens entre os **72 recursos já incluídos**. O upload de fotos externas não foi implementado, pois exigiria outro fluxo de Storage/seleção de arquivos.
- A exclusão de categoria apaga suas cartas; a exclusão de tema também apaga categorias e cartas associadas. O histórico de partidas continua preservado.
- As funcionalidades de Configurações/Acessibilidade do Integrante 6 **não foram implementadas nem modificadas**.
- Mantidos o código e as rotas existentes da Home, Ranking, Firebase e motor de jogo. O código do Integrante 2 mudou somente para consultar os novos registros de cartas quando disponíveis.

## Verificação realizada nesta preparação

1. Simulação SQLite da migração e da preservação dos dados anteriores: **aprovada**.
2. Verificação de 72 recursos de imagens reais, sem referências ausentes: **aprovada**.
3. Compilação Kotlin isolada das novas entidades, DAOs e dos Repositories (com anotações Room simuladas): **aprovada**.
4. Compilação Kotlin isolada da rotina de semeadura: **aprovada**.
5. Build Android completo e execução dos testes no emulador: **pendentes**; executar no Android Studio.

## Teste manual recomendado no emulador

1. Criar cópia de segurança do projeto atual e abrir o ZIP novo em outra pasta.
2. Executar **Build → Assemble Project**.
3. Entrar como Administrador de demonstração, cadastrar um tema, criar sua categoria e três cartas com imagens diferentes.
4. Entrar como Jogador, acessar Catálogo, tocar em "Ver categorias e cartas", conferir as imagens e jogar no nível Fácil.
5. Registrar a vitória e conferir Ranking/Histórico.
6. Retornar ao Administrador, editar e excluir uma carta e verificar a atualização do Catálogo.
7. Testar atualização a partir de um banco anterior com dados existentes, sem desinstalar o aplicativo.

**Não fazer commit antes de validar a compilação, os testes e a navegação no Android Studio.**
