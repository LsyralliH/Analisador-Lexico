/*
Esta é a classe principal do Analisador Léxico (também chamado de "Lexer" ou "Scanner").
A responsabilidade desta classe é receber o código-fonte completo como uma
string e quebrá-lo em uma lista de Tokens (definidos em Token.java).
Ele faz isso lendo o código caractere por caractere, decidindo onde um
token começa e termina, e qual é o seu tipo (TokenType).
Esta classe também é responsável por:
    - Ignorar espaços em branco, tabulações e quebras de linha.
    - Ignorar comentários (como // ...).
    - Identificar palavras-chave (como "if", "while") e diferenciá-las de identificadores (nomes de variáveis).
    - Reportar erros léxicos (caracteres inesperados).
*/

package analisador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import analisador.core.TokenType;

public class Lexer {
    private final String codigoFonte;
    private final List<Token> tokens = new ArrayList<>();
    private static final Set<String> palavrasChave = new HashSet<>(Arrays.asList(
        "abstract", "assert", "boolean", "break", "byte",
        "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else",
        "enum", "extends", "final", "finally", "float",
        "for", "goto", "if", "implements", "import",
        "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super",
        "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while"
));

    // Controle da leitura
    private int inicio = 0;
    private int atual = 0;
    private int linha = 1;
    private int coluna = 1;
    private int colunaInicio = 1; 

    public Lexer(String codigoFonte) {
        this.codigoFonte = codigoFonte; //Armazena e inicializa
    }

    public List<Token> scanTokens() { //processa todo o texto.
        while (!isAtEnd()) {
            inicio = atual;
            colunaInicio = coluna;
            scanToken();
        }
        addToken(TokenType.EOF, "EOF", getTokenDescription("EOF", TokenType.EOF));
        return tokens;
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
            case ' ': case '\r': case '\t':
                break; // Ignora espaços em branco

            case '\n':
                linha++;
                coluna = 1;
                break;

            // Símbolos especiais
            case ';': addToken(TokenType.SIMBOLO_ESPECIAL, ";", getTokenDescription(";", TokenType.SIMBOLO_ESPECIAL)); break;
            case ',': addToken(TokenType.SIMBOLO_ESPECIAL, ",", getTokenDescription(",", TokenType.SIMBOLO_ESPECIAL)); break;
            case '(': addToken(TokenType.SIMBOLO_ESPECIAL, "(", getTokenDescription("(", TokenType.SIMBOLO_ESPECIAL)); break;
            case ')': addToken(TokenType.SIMBOLO_ESPECIAL, ")", getTokenDescription(")", TokenType.SIMBOLO_ESPECIAL)); break;
            case '{': addToken(TokenType.SIMBOLO_ESPECIAL, "{", getTokenDescription("{", TokenType.SIMBOLO_ESPECIAL)); break;
            case '}': addToken(TokenType.SIMBOLO_ESPECIAL, "}", getTokenDescription("}", TokenType.SIMBOLO_ESPECIAL)); break;
            case '[': addToken(TokenType.SIMBOLO_ESPECIAL, "[", getTokenDescription("[", TokenType.SIMBOLO_ESPECIAL)); break;
            case ']': addToken(TokenType.SIMBOLO_ESPECIAL, "]", getTokenDescription("]", TokenType.SIMBOLO_ESPECIAL)); break;
            case '.': addToken(TokenType.SIMBOLO_ESPECIAL, ".", getTokenDescription(".", TokenType.SIMBOLO_ESPECIAL)); break;

            // Operadores aritméticos
            case '+': addToken(TokenType.OPERADOR_ARITMETICO, "+", getTokenDescription("+", TokenType.OPERADOR_ARITMETICO)); break;
            case '-': addToken(TokenType.OPERADOR_ARITMETICO, "-", getTokenDescription("-", TokenType.OPERADOR_ARITMETICO)); break;
            case '*': addToken(TokenType.OPERADOR_ARITMETICO, "*", getTokenDescription("*", TokenType.OPERADOR_ARITMETICO)); break;
            case '/':
                if (match('/')) { 
                    while (peek() != '\n' && !isAtEnd()) advance();
                    addToken(TokenType.COMENTARIO, "// comentário de linha", getTokenDescription("// comentário de linha", TokenType.COMENTARIO));
                } else if (match('*')) { 
                    while (!(peek() == '*' && peekNext() == '/') && !isAtEnd()) {
                        if (peek() == '\n') linha++;
                        advance();
                    }
                    advance(); advance(); // Fechar comentário de bloco
                    addToken(TokenType.COMENTARIO, "/* comentário de bloco */", getTokenDescription("/* comentário de bloco */", TokenType.COMENTARIO));
                } else {
                    addToken(TokenType.OPERADOR_ARITMETICO, "/", getTokenDescription("/", TokenType.OPERADOR_ARITMETICO));
                }
                break;

            // Atribuição e comparação
            case '=': 
                addToken(match('=') ? TokenType.COMPARACAO : TokenType.ATRIBUICAO,
                         match('=') ? "==" : "=",
                         match('=') ? getTokenDescription("==", TokenType.COMPARACAO)
                                    : getTokenDescription("=", TokenType.ATRIBUICAO));
                break;

            case '!': 
                addToken(match('=') ? TokenType.COMPARACAO : TokenType.OPERADOR_LOGICO,
                         match('=') ? "!=" : "!",
                         match('=') ? getTokenDescription("!=", TokenType.COMPARACAO)
                                    : getTokenDescription("!", TokenType.OPERADOR_LOGICO));
                break;

            case '<': 
                if (match('=')) addToken(TokenType.COMPARACAO, "<=", getTokenDescription("<=", TokenType.COMPARACAO));
                else if (match('<')) addToken(TokenType.OPERADOR_BIT_A_BIT, "<<", getTokenDescription("<<", TokenType.OPERADOR_BIT_A_BIT));
                else addToken(TokenType.COMPARACAO, "<", getTokenDescription("<", TokenType.COMPARACAO));
                break;

            case '>': 
                if (match('=')) addToken(TokenType.COMPARACAO, ">=", getTokenDescription(">=", TokenType.COMPARACAO));
                else if (match('>')) addToken(TokenType.OPERADOR_BIT_A_BIT, ">>", getTokenDescription(">>", TokenType.OPERADOR_BIT_A_BIT));
                else addToken(TokenType.COMPARACAO, ">", getTokenDescription(">", TokenType.COMPARACAO));
                break;

            // Operadores lógicos e bit a bit
            case '&': addToken(match('&') ? TokenType.OPERADOR_LOGICO : TokenType.OPERADOR_BIT_A_BIT,
                               match('&') ? "&&" : "&",
                               match('&') ? getTokenDescription("&&", TokenType.OPERADOR_LOGICO)
                                          : getTokenDescription("&", TokenType.OPERADOR_BIT_A_BIT)); break;

            case '|': addToken(match('|') ? TokenType.OPERADOR_LOGICO : TokenType.OPERADOR_BIT_A_BIT,
                               match('|') ? "||" : "|",
                               match('|') ? getTokenDescription("||", TokenType.OPERADOR_LOGICO)
                                          : getTokenDescription("|", TokenType.OPERADOR_BIT_A_BIT)); break;

            case '^': addToken(TokenType.OPERADOR_BIT_A_BIT, "^", getTokenDescription("^", TokenType.OPERADOR_BIT_A_BIT)); break;
            case '~': addToken(TokenType.OPERADOR_BIT_A_BIT, "~", getTokenDescription("~", TokenType.OPERADOR_BIT_A_BIT)); break;

            // Literais
            case '"': string(); break;
            case '\'': character(); break;

            default:
                if (isDigit(c)) number();
                else if (isAlpha(c)) identifier();
                else System.err.println("Erro Léxico na linha " + linha + ", coluna " + coluna +
                                        ": Caractere inesperado '" + c + "'");
                break;
        }
    }

    //MÉTODOS AUXILIARES
    private void identifier() { // distingui Palavras chaves e identificadores em situaçoes especiais (int "valorint"  ≠ int valor  )
        while (isAlphaNumeric(peek())) advance();
        String texto = codigoFonte.substring(inicio, atual);
        addToken(palavrasChave.contains(texto) ? TokenType.PALAVRA_CHAVE : TokenType.IDENTIFICADOR,
                 texto,
                 getTokenDescription(texto, palavrasChave.contains(texto) ? TokenType.PALAVRA_CHAVE : TokenType.IDENTIFICADOR));
    }

    private void number() { //consome dígitos inteiros e consome o ponto e os dígitos subsequentes (3.14)
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) advance();
        }
        String texto = codigoFonte.substring(inicio, atual);
        addToken(TokenType.LITERAL_NUMERICO, texto, getTokenDescription(texto, TokenType.LITERAL_NUMERICO));
    }

    private void string() { //Absorção de dados caso não se encontre o fechamento de " e \n dentro de Strings
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') linha++;
            advance();
        }
        advance(); 
        String texto = codigoFonte.substring(inicio + 1, atual - 1);
        addToken(TokenType.LITERAL_STRING, texto, getTokenDescription(texto, TokenType.LITERAL_STRING));
    }

    private void character() { //Absorção de dados '\n', '\'', '\t' se houver escape.
        if (peek() == '\\') advance();
        if (peek() != '\'' && !isAtEnd()) advance();
        if (peek() == '\'') advance();
        String texto = codigoFonte.substring(inicio, atual);
        addToken(TokenType.LITERAL_CHAR, texto, getTokenDescription(texto, TokenType.LITERAL_CHAR));
    }

    // Controle de leitura
    private boolean match(char esperado) {
        if (isAtEnd()) return false;
        if (codigoFonte.charAt(atual) != esperado) return false;
        atual++;
        return true;
    }

    private char peek() { return isAtEnd() ? '\0' : codigoFonte.charAt(atual); }  // peek() "espia" o caractere atual, sem andar para a frente'
    private char peekNext() { return (atual + 1 >= codigoFonte.length()) ? '\0' : codigoFonte.charAt(atual + 1); } // peekNext() "espia" o caractere que vem *depois* do atual'
    private boolean isAlpha(char c) { return Character.isLetter(c) || c == '_'; } //isAlpha() pergunta se é (A-Z) ou um sublinhado (_)?".
    private boolean isAlphaNumeric(char c) { return isAlpha(c) || isDigit(c); } // isAlphaNumeric() pergunta se é uma letra OU um número?".
    private boolean isDigit(char c) { return c >= '0' && c <= '9'; } // isDigit() pergunta se é um número (0-9)?".
    private boolean isAtEnd() { return atual >= codigoFonte.length(); } // isAtEnd() verrifica se o codigo chegou ao fim.
    private char advance() { coluna++; return codigoFonte.charAt(atual++); } // advance() retorna o caractere atual e avança para o próximo.

    private void addToken(TokenType tipo, String lexema, String descricao) {//addToken() guarda o token na lista de tokens com seu tipo, lexema, descrição, linha e coluna.'
        if (tipo == TokenType.EOF) {
            tokens.add(new Token(tipo, lexema, descricao, linha, coluna));
        } else {
            tokens.add(new Token(tipo, lexema, descricao, linha, colunaInicio));
        }
    }

    private String getTokenDescription(String lexema, TokenType tipo) {
        switch (tipo) {
            case PALAVRA_CHAVE: return "Palavra-chave -> " + lexema;
            case IDENTIFICADOR: return "Identificador -> " + lexema;
            case OPERADOR_ARITMETICO:
                switch (lexema) {
                    case "+": return "Operador -> Mais ('+')";
                    case "-": return "Operador -> Menos ('-')";
                    case "*": return "Operador -> Multiplicação ('*')";
                    case "/": return "Operador -> Divisão ('/')";
                }
                break;
            case OPERADOR_LOGICO:
                switch (lexema) {
                    case "&&": return "Operador -> E lógico ('&&')";
                    case "||": return "Operador -> OU lógico ('||')";
                    case "!": return "Operador -> Negação ('!')";
                }
                break;
            case OPERADOR_BIT_A_BIT:
                switch (lexema) {
                    case "&": return "Operador -> AND bit a bit ('&')";
                    case "|": return "Operador -> OR bit a bit ('|')";
                    case "^": return "Operador -> XOR bit a bit ('^')";
                    case "~": return "Operador -> NOT bit a bit ('~')";
                    case "<<": return "Operador -> Deslocamento à esquerda ('<<')";
                    case ">>": return "Operador -> Deslocamento à direita ('>>')";
                }
                break;
            case ATRIBUICAO: return "Atribuição -> '='";
            case COMPARACAO: return "Comparação -> '" + lexema + "'";
            case SIMBOLO_ESPECIAL:
                switch (lexema) {
                    case ";": return "Símbolo especial -> Ponto e vírgula (';')";
                    case ",": return "Símbolo especial -> Vírgula (',')";
                    case "(": return "Símbolo especial -> Parêntese esquerdo ('(')";
                    case ")": return "Símbolo especial -> Parêntese direito (')')";
                    case "{": return "Símbolo especial -> Chave esquerda ('{')";
                    case "}": return "Símbolo especial -> Chave direita ('}')";
                    case "[": return "Símbolo especial -> Colchete esquerdo ('[')";
                    case "]": return "Símbolo especial -> Colchete direito (']')";
                    case ".": return "Símbolo especial -> Ponto ('.')";
                }
                break;
            case LITERAL_NUMERICO: return "Literal numérico -> " + lexema;
            case LITERAL_STRING: return "Literal string -> \"" + lexema + "\"";
            case LITERAL_CHAR: return "Literal de Char -> '" + lexema + "'";
            case COMENTARIO: return "Comentário -> " + lexema;
            case EOF: return "Fim do arquivo";
            default: return "Token desconhecido -> " + lexema;
        }
        return lexema;
    }
}