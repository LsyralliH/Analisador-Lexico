/*
Esta classe representa um Token individual.
Um Token é a menor unidade de significado em um programa, como uma palavra-chave,
um nome de variável, um número ou um operador. 
 Cada objeto Token armazena:
    - O seu 'tipo' (do nosso enum TokenType)
    - O 'lexema' (o texto original do código, ex: "minhaVariavel")
    - A 'linha' e 'coluna' onde ele foi encontrado no arquivo.
 */

package analisador;

import analisador.core.TokenType;

public class Token {
    public final TokenType tipo;
    public final String lexema;
    public final String descricao;
    public final int linha;
    public final int coluna;

    public Token(TokenType tipo, String lexema, String descricao, int linha, int coluna){
        this.tipo = tipo;
        this.lexema = lexema;
        this.descricao = descricao;
        this.linha =linha;
        this.coluna = coluna;
    }
    @Override
    public String toString() {
        return String.format("[Linha: %-3d, Col: %-3d] %-20s : %s",
                            linha, coluna, lexema, descricao);
    }
}

