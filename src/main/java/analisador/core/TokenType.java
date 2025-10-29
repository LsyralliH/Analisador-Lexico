/*
Este arquivo define um 'enum' (um conjunto de constantes) para todos 
os tipos de tokens que o nosso Analisador Léxico pode reconhecer.
Cada item aqui é um "tipo" de etiqueta que podemos colocar em um pedaço
do código-fonte. Por exemplo, "int" será etiquetado como INT, 
"minhaVariavel" como IDENTIFIER, e "+" como MAIS.
 */
package analisador.core;

public enum TokenType {

    PALAVRA_CHAVE, 
    
    IDENTIFICADOR,

    LITERAL_NUMERICO,
    LITERAL_STRING,
    LITERAL_CHAR,

    ATRIBUICAO,          // =
    COMPARACAO,          // ==, !=, <, <=, >, >=
    OPERADOR_ARITMETICO, // +, -, *, /
    OPERADOR_LOGICO,     // &&, ||
    OPERADOR_BIT_A_BIT,   // &, |, ^, ~, <<, >>


    SIMBOLO_ESPECIAL,  // ; , ( ) { } [ ]

    COMENTARIO,

    EOF  // Fim do arquivo
}
