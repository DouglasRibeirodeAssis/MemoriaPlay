# Memória Play — correções na integração (18/11/2026)

Este projeto é **uma cópia integral** de `MemoriaPlay_GitBase_Integrante2_Integrado.zip`,
com ajustes localizados. Não apagar dados do app nem substituir arquivos isoladamente.

## Correções realizadas

1. `AppDatabase.kt`: migração explícita Room 1→2; a atualização preserva
   temas e partidas; a criação de temas e usuários de demonstração ocorre
   somente na criação de um banco novo. Não há mais repovoamento automático
   quando o administrador exclui todos os temas.
2. `SessaoRepository.kt`: a senha do jogador existente agora é comparada
   com a senha armazenada. Um usuário novo ainda pode criar uma conta local
   ao informar nome e senha, como previsto pela implementação original.
3. `NavegacaoApp.kt`: tela de Login como entrada; jogador chega à Home,
   administrador chega ao AdminScreen; Configurações provisórias permitem
   sair/trocar jogador; Home, Catálogo, Ranking, Desafios e Jogo continuam
   ligados. As partidas recebem o nome do jogador que iniciou sessão.
4. `JogoRepository.kt`: para temas cadastrados pelo administrador sem imagens
   específicas, utiliza temporariamente as 12 imagens de **Objetos**.
   Isso permite jogar, mas não configura um sistema completo de gerenciamento
   de cartas/imagens personalizadas; será necessário implementá-lo à parte.
5. `AdminScreen.kt`: explica o uso do baralho padrão em temas personalizados.
6. Os dois testes vazios do Integrante 1 (`HomeViewModelTest.kt` e
   `RotasTest.kt`) foram recuperados de `Integrante 1.zip`.
7. `AdminViewModelTest.kt`: teste adicional para jogador existente com senha incorreta.

## Atenções

- Os acessos `admin/1234` e `jogador/1234` são **apenas de demonstração**.
  As senhas permanecem em texto simples no banco local: não usar esta
  autenticação como segurança de produção ou para dados sensíveis.
- A migração 1→2 é voltada especificamente para o esquema v1 do projeto
  enviado. Fazer **backup** do banco de dados antes de instalar sobre
  versões que tenham tido mudanças manuais no esquema.
- Configurações e acessibilidade globais ainda são módulos pendentes da
  equipe. Não foram criados conteúdos fictícios para simular implementação.
- O APK e os testes instrumentados **não foram executados nesta análise**:
  validar no Android Studio da equipe antes de substituir a versão estável.

## Como validar no Android Studio

1. Extrair o ZIP completo numa **pasta nova**, sem apagar o projeto atual.
2. Abrir a pasta no Android Studio e sincronizar o Gradle.
3. Executar **Build > Assemble Project**; depois os testes unitários.
4. No emulador: entrar com `jogador/1234`, abrir Home, jogar, concluir e
   verificar no Ranking que o nome é `jogador`.
5. Sair pela aba Configurações, entrar com `admin/1234`, abrir Administração,
   cadastrar/editar/excluir temas e conferir as mudanças no Catálogo.
6. Testar que `jogador` com senha errada **não entra**.
7. Para migração real: instalar sobre uma base v1 **com backup dos dados**
   e confirmar que o histórico e os temas permanecem após o upgrade.

Evite fazer `git add .` antes de verificar as diferenças com os colegas.
