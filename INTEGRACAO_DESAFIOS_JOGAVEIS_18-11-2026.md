# Memória Play — Desafios jogáveis (18/11/2026)

## Objetivo

Foi integrada a aba Desafios (Integrante 5/Firebase) ao jogo de memória (Integrante 2), reutilizando o fluxo de resultado e gravação no ranking (Integrante 3/5) e a navegação existente (Integrante 1).

## Funcionamento

1. O jogador entra na aba **Desafios**, que continua carregando as missões do Firestore.
2. Cada missão válida (3 a 12 pares) apresenta **Iniciar desafio**.
3. Ao selecionar, o aplicativo guarda uma cópia das regras da missão na entrada de navegação (SavedStateHandle) e abre a partida sem apresentar outra seleção de dificuldade.
4. A dificuldade é escolhida a partir da quantidade de pares: 3=Fácil, 8=Médio, 12=Difícil, outras quantidades de 3 a 12=Personalizado.
5. A partida normal continua usando o Room, e suas vitórias continuam sendo gravadas no Ranking.
6. Ao terminar, o resultado da missão exibe **Desafio concluído!** ou **Desafio não concluído**, conforme as regras da missão. É possível jogar novamente ou voltar à aba Desafios.

## Campos no Firestore

Coleção `desafios` (a mesma já configurada):

- `titulo`: string (obrigatório para exibir um nome)
- `descricao`: string
- `quantidadePares`: número inteiro de 3 a 12
- `tempoLimiteSegundos`: número inteiro positivo opcional; se ausente, não há limite de tempo
- `tentativasMaximas`: número inteiro positivo opcional; se ausente, não há limite de tentativas

O ID do próprio documento é usado como identificador. Os documentos existentes com apenas `titulo`, `descricao` e `quantidadePares` continuam válidos.

### Exemplo: desafio de 6 pares

Documento `desafio001`:

- titulo: `Desafio da Memória`
- descricao: `Encontre 6 pares de cartas.`
- quantidadePares: `6`

### Exemplo: contra o tempo

Documento `rapido001`:

- titulo: `Contra o Tempo`
- descricao: `Encontre 3 pares em até 60 segundos.`
- quantidadePares: `3`
- tempoLimiteSegundos: `60`

### Exemplo: perfeição

Documento `perfeito001`:

- titulo: `Perfeição`
- descricao: `Encontre 3 pares em até 3 tentativas.`
- quantidadePares: `3`
- tentativasMaximas: `3`

## Regras e limitações

- A missão é avaliada quando a partida termina, não interrompida automaticamente se o limite de tempo ou tentativas for ultrapassado. A tela de jogo sinaliza o limite excedido.
- Uma missão não concluída pode continuar sendo uma vitória válida na partida normal e ser registrada no Ranking.
- O estado **concluído** aparece no resultado da missão; **a conclusão não é gravada no Firebase nem marcada de forma persistente na lista de Desafios**. Isso exigiria outro passo de persistência, que não faz parte desta integração.
- Se o tema padrão ativo não possuir a quantidade necessária de imagens, o jogo apresenta a mensagem de insuficiência de cartas que já existia.
- Foi preservado o banco Room versão 3; **não foi adicionada migração**.
- Configurações e acessibilidade do Integrante 6 continuam intocados.

## Arquivos alterados para esta entrega

- `features/desafios/model/Desafio.kt`
- `features/desafios/model/AvaliadorDesafio.kt` (novo)
- `data/remote/FirebaseDesafiosSource.kt`
- `features/desafios/ui/DesafiosScreen.kt`
- `features/jogo/model/ConfiguracaoPartida.kt`
- `features/jogo/ui/JogoScreen.kt`
- `features/jogo/ui/ResultadoScreen.kt`
- `navigation/NavegacaoApp.kt`
- `navigation/Rotas.kt`
- `app/src/test/.../desafios/AvaliadorDesafioTest.kt` (novo)
- `app/src/androidTest/.../desafios/ui/DesafiosIniciarTest.kt` (novo)
- `app/src/test/.../navigation/RotasTest.kt`

Preservados também os arquivos coloridos de Login (`LoginScreen.kt`) e Administração (`AdminScreen.kt`) e a correção já aprovada em `CrudAdminTest.kt`, que estavam mais recentes do que o ZIP-base.

## Validação

- Lógica Kotlin independente: compilação e verificações de pares, tempo, tentativas e rotas passaram no ambiente de preparação.
- Compilação completa do Android, instrumentação e testes do dispositivo **precisam ser executados no Android Studio**.

## Roteiro de validação no Android Studio

1. Build → Assemble Project.
2. Acesse Jogador → Desafios. Confirme que a lista vinda do Firebase mostra **Iniciar desafio**.
3. Selecione a missão de 6 pares, encontre os 6 pares e confira o texto **Desafio concluído!** e o registro no Ranking.
4. Crie no Firestore a missão `Contra o Tempo` (3 pares, limite 60s). Termine após 60s e confira **Desafio não concluído**.
5. Entre na Home → Iniciar nova partida; confirme que o jogo normal e o Ranking seguem funcionando.
6. Rode `AvaliadorDesafioTest.kt`, `RotasTest.kt` e `DesafiosIniciarTest.kt` no emulador.
