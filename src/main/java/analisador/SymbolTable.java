package analisador;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import analisador.core.TokenType;

public class SymbolTable {
    private final Map<String, Token> simbolos = new LinkedHashMap<>();

    public void add(Token token) {
        if (token.tipo == TokenType.IDENTIFICADOR && !simbolos.containsKey(token.lexema)) {
            simbolos.put(token.lexema, token);
        }
    }

    //IMPRESSÃO NO CONSOLE
    public void print() {
        System.out.println("\n=== Tabela de Símbolos ===");
        System.out.printf("%-5s | %-20s | %s\n", "ID", "Lexema", "Posição (Linha, Coluna)");
        System.out.println("--------------------------------------------");
        int id = 1;
        for (Token token : simbolos.values()) {
            String pos = "Linha " + token.linha + ", Col " + token.coluna;
            System.out.printf("%-5d | %-20s | %s\n", id++, token.lexema, pos);
        }
        System.out.println("--------------------------------------------");
    }

    // MÉTODO PARA GERAR LINHAS PARA O PDF
    public List<String> getLinhas() {
        List<String> linhas = new ArrayList<>();
        int id = 1;
        for (Token token : simbolos.values()) {
            String pos = "Linha " + token.linha + ", Col " + token.coluna;
            linhas.add(String.format("%-5d | %-20s | %s", id++, token.lexema, pos));
        }
        return linhas;
    }

    public List<List<String>> getLinhasParaPDF() {
        List<List<String>> linhas = new ArrayList<>();
        int id = 1;
        for (Token token : simbolos.values()) {
            String pos = "Linha " + token.linha + ", Col " + token.coluna;
            linhas.add(List.of(
                String.valueOf(id++), // Coluna 1: ID
                token.lexema,         // Coluna 2: Lexema
                pos                  // Coluna 3: Posição
            ));
        }
        return linhas;
    }
}