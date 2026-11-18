# Memória Play — Integrante 2

## Implementação

O módulo do Integrante 2 contém os quatro modos, regras de combinação,
cronômetro, tentativas, resultado, cartas acessíveis e salvamento no Room.

Arquivos de produção: `features/jogo/` (11 arquivos).

Testes de unidade:
- `app/src/test/java/br/com/memoriaplay/jogo/RegrasJogoTest.kt`
- `app/src/test/java/br/com/memoriaplay/jogo/JogoViewModelTest.kt`

Teste instrumentado de interface:
- `app/src/androidTest/java/br/com/memoriaplay/jogo/FluxoJogoTest.kt`

Arquivos compartilhados ajustados:
- `navigation/Rotas.kt`: rotas do modo, número de pares e tema.
- `navigation/NavegacaoApp.kt`: fluxo da partida e do resultado.
- `br/com/memoriaplay/MainActivity.kt`: fechamento de chaves da base.
- `app/build.gradle.kts`: dependência para telas de login/admin.

A base utilizada foi a versão mais recente enviada do GitHub.
A tabela `usuarios`, o banco Room versão 2, o Gradle wrapper,
o login, a administração e o restante dos módulos foram preservados.
As referências aos recursos da Parte 2 usam o namespace da base,
`br.com.memoriaplay`.

## Como testar no Android Studio

1. Abrir a pasta `MemoriaPlay-master` no Android Studio.
2. Fazer Sync Project with Gradle Files.
3. Executar Build > Make Project.
4. Executar os testes de unidade pelo diretório `jogo` em `app/src/test`.
5. Abrir um emulador/dispositivo e executar `FluxoJogoTest` em `app/src/androidTest`.
6. Executar o aplicativo e fazer os testes manuais abaixo.

No terminal do projeto, os comandos equivalentes são:

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest
```

No Windows, usar `gradlew.bat` em lugar de `./gradlew`.
O segundo comando exige um emulador ou dispositivo conectado.

## Checklist manual

- [ ] Início > Iniciar partida > Fácil: 3 pares e 6 cartas.
- [ ] Selecionar Médio: 8 pares e 16 cartas.
- [ ] Selecionar Difícil: 12 pares e 24 cartas.
- [ ] Personalizado: testar os extremos 3 e 12 pares.
- [ ] Virar duas cartas diferentes: terceira fica bloqueada.
- [ ] Par diferente volta a ficar fechado após uma pausa.
- [ ] Par igual permanece aberto; tentativas são contadas.
- [ ] Cronômetro avança e para após completar o tabuleiro.
- [ ] Resultado exibe modo, tema, pares, tempo e tentativas.
- [ ] Resultado indica que foi salvo no ranking.
- [ ] Abrir Ranking e verificar o resultado registrado.
- [ ] Fechar e reabrir o app e verificar o mesmo resultado.
- [ ] Jogar novamente e conferir que as cartas são reembaralhadas.
- [ ] Catálogo > Jogar num tema diferente > conferir as imagens.
- [ ] TalkBack: carta fechada não anuncia o símbolo escondido.
- [ ] Girar o aparelho durante a partida sem perder o progresso.

## Validação realizada neste pacote

O núcleo Kotlin da versão integrada foi compilado isoladamente com
dependências simuladas para Android/Room e testado localmente. Essa validação verificou
formação de pares (3 a 12), bloqueio durante comparação, acertos, vitória,
salvamento único e reinício da partida. Não substitui o build Android completo,
que depende do SDK e do Gradle no Android Studio.

## Integrações não alteradas

O restante do aplicativo continua pertencendo aos demais integrantes.
O resumo da tela Início, login, administração e configuração global
de acessibilidade não foram reimplementados pelo Integrante 2.
As telas de login e administração continuam com o fluxo original
da versão enviada; a navegação do Integrante 2 não substitui esse fluxo.
