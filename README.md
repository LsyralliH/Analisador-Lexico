# 🚀 Analisador Léxico (Java)

Este projeto é um Analisador Léxico desenvolvido em Java. Ele lê um arquivo de código-fonte, processa seu conteúdo para identificar tokens e, ao final, gera um arquivo PDF (`saida_analisador_lexico.pdf`) contendo a Lista de Tokens e a Tabela de Símbolos.

Este workspace foi configurado para o VS Code, mas pode ser compilado e executado manualmente via terminal.

## 📂 Estrutura de Pastas

A estrutura de pastas padrão para este projeto é:

* `src`: Contém todos os arquivos de código-fonte (`.java`).
    * `src/main/java/analisador`
    * `src/main/java/analisador/core`
    * `src/main/java/principal`
* `lib`: Contém as dependências (`.jar`), como o `pdfbox-app-3.0.6.jar`.
* `bin`: Contém os arquivos compilados (`.class`) gerados pelo `javac`.

---


## ☕ Como Executar (Manualmente)

Para compilar e executar o projeto, abra um terminal na pasta raiz (`Analisador_Lexico`) e siga os passos:

### Passo 1: Compilar

Use o `javac` para compilar todos os seus arquivos-fonte. Este comando irá ler os fontes da pasta `src` e salvar os `.class` compilados dentro da pasta `bin`:

```bash
javac -encoding UTF-8 -cp "lib\*" -d bin src/main/java/analisador/*.java src/main/java/analisador/core/*.java src/main/java/principal/*.java
```

**O que este comando faz:**
* `-cp "lib\*"`: Inclui todas as bibliotecas (`.jar`) da pasta `lib` (necessário para o PDFBox).
* `-d bin`: Define o **destino** da compilação. Todos os `.class` irão para a pasta `bin`.
* `src/...`: Os arquivos-fonte a serem compilados.

### Passo 2: Executar

Agora, use o `java` para rodar o programa. O `classpath` (`-cp`) deve incluir tanto a pasta `bin` (onde estão seus `.class`) quanto a pasta `lib` (onde está o PDFBox):

```bash
java -cp "bin;lib\*" principal.Main "CodigoFonte.java"
```

**O que este comando faz:**
* `-cp "bin;lib\*"`: Define o *classpath*. O Java procurará classes na pasta `bin` (seus arquivos) e em todos os `.jar` da pasta `lib` (dependências).
* `principal.Main`: A classe principal a ser executada.
* `"CodigoFonte.java"`: O argumento `args[0]` passado ao seu programa, indicando qual arquivo deve ser analisado. (Substitua pelo caminho do seu arquivo de teste, se necessário).




---
### ⚠️ Observação Importante sobre a Compilação

O comando `java` (Passo 2) **lê apenas os arquivos `.class`** da pasta `bin`. Ele não lê os arquivos `.java` da pasta `src`.

Isso significa que **toda vez que você fizer qualquer alteração no código-fonte** (em qualquer arquivo `.java` na pasta `src`), você **deve** executar o comando `javac` (Passo 1) novamente.

Se você modificar o código e esquecer de recompilar, o comando `java` irá executar a versão *antiga* do seu programa, e suas alterações não aparecerão.




## 📦 Gerenciamento de Dependências

Este projeto gerencia dependências manualmente através da pasta `lib`.

Para adicionar uma nova dependência:
1.  Baixe o arquivo `.jar`.
2.  Coloque-o dentro da pasta `lib`.
3.  O comando de compilação e execução (`-cp "lib\*"`) irá incluí-lo automaticamente.