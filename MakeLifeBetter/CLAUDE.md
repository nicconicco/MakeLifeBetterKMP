# Instruções de Trabalho - Claude Code

## Fluxo obrigatório para todas as tarefas

1. **Criar branch antes de qualquer edição de código**
   - Sempre criar uma nova branch a partir da branch atual antes de modificar qualquer arquivo.
   - O nome da branch deve ser descritivo em relação à tarefa (ex: `feature/nome-da-tarefa`, `fix/descricao-do-bug`, `refactor/descricao`).
   - Nunca editar código diretamente na branch principal de trabalho.

2. **Executar a tarefa na nova branch**
   - Realizar todas as alterações de código, criação de arquivos, etc. somente após a criação da branch.

3. **Aguardar revisão do usuário**
   - Após concluir a tarefa, aguardar o usuário revisar as mudanças.
   - O usuário decidirá se faz merge na branch de trabalho.
   - Não fazer merge automaticamente.

## Trabalho em paralelo com Git Worktree

Quando houver múltiplas sessões de Claude trabalhando ao mesmo tempo, usar `git worktree` para evitar conflitos entre sessões que compartilham o mesmo diretório.

### Como funciona

- Cada sessão trabalha em uma **pasta separada** com sua própria branch, mas compartilhando o mesmo repositório Git.
- Isso evita que um `git checkout` em uma sessão quebre o contexto da outra.

### Fluxo

1. **Criar o worktree** ao iniciar uma tarefa em paralelo:
   ```bash
   git worktree add ../NomePasta-descritivo nome-da-branch
   ```
2. **Trabalhar normalmente** na pasta do worktree.
3. **Após o usuário autorizar o merge:**
   - Fazer o merge da branch na branch de trabalho.
   - **Remover o worktree** logo em seguida:
     ```bash
     git worktree remove ../NomePasta-descritivo
     ```

### Regras

- Cada branch só pode estar em 1 worktree por vez.
- Sempre remover o worktree após o merge ser concluído.
- Se o usuário não autorizar o merge, manter o worktree até nova decisão.

## Idioma

- Comunicar sempre em português (PT-BR).
