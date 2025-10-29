/**
Esta é a classe principal que "amarra" todo o projeto.
Suas responsabilidades são:
    1. Ser o ponto de entrada do programa (o método 'public static void main').
    2. Verificar se o usuário passou o caminho do arquivo .c como argumento.
    3. Ler o conteúdo completo do arquivo de código-fonte (ex: "teste.c") 
para uma string. (Cumprindo o Pré-Requisito).
    4. Criar uma instância do Lexer, passando o código-fonte para ele.
    5. Chamar o Lexer para obter a lista de Tokens.
    6. Criar uma instância da SymbolTable.
    7. Imprimir a "Lista de Tokens" (Requisito do Barema).
    8. Popular a Tabela de Símbolos com os tokens encontrados.
    9. Imprimir a "Tabela de Símbolos" (Requisito do Barema).
*/
package principal;

import analisador.Lexer;
import analisador.Token;
import analisador.SymbolTable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
    
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Uso: java principal.Main <caminho_para_arquivo.java>");
            System.exit(1);
        }

        try {
            String filePath = args[0];
            String sourceCode = Files.readString(Paths.get(filePath), java.nio.charset.StandardCharsets.UTF_8);

            Lexer lexer = new Lexer(sourceCode);
            List<Token> tokens = lexer.scanTokens();

            SymbolTable tabela = new SymbolTable();
            for (Token t : tokens) tabela.add(t);

        
            // PASSO 1: IMPRIMIR NO CONSOLE 
            System.out.println("--- Código Fonte Sendo Analisado ---");
            System.out.println(sourceCode);
            System.out.println("------------------------------------");

            System.out.println("\n--- Lista de Tokens (Console) ---");
            for (Token t : tokens) {
                System.out.println(t);
            }
            tabela.print(); 
            
           
            // PASSO 2: GERAR O PDF 
            try (PDDocument doc = new PDDocument()) {
                File fontFile = new File("Roboto-VariableFont_wdth,wght.ttf");
                if (!fontFile.exists()) {
                     System.err.println("AVISO: Fonte 'Roboto-VariableFont_wdth,wght.ttf' não encontrada. Use uma fonte padrão.");
                     throw new IOException("Fonte Roboto não encontrada na pasta do projeto. Baixe-a e coloque-a lá.");
                }
                
                PDType0Font font = PDType0Font.load(doc, fontFile);

                PDPage page = new PDPage(PDRectangle.LETTER);
                doc.addPage(page);
                PDPageContentStream content = new PDPageContentStream(doc, page);
                float y = 700;
                final float MARGEM = 50;

                //CÓDIGO FONTE
                List<List<String>> codigoFonteLinhas = new ArrayList<>();
                int linhaNum = 1;
                for (String linha : sourceCode.replace("\r", "").split("\n")) {
                    codigoFonteLinhas.add(List.of(String.valueOf(linhaNum), linha));
                    linhaNum++;
                }

                //TABELA 1 DO PDF: CÓDIGO FONTE
                TabelaResult resultado = escreverCodigoFonte(doc, content, font, "Código Fonte",
                                                        codigoFonteLinhas, y, MARGEM);
                content = resultado.content;
                y = resultado.y;

                //LISTA DE TOKENS
                List<List<String>> tokensLinhas = new ArrayList<>();
                for (Token t : tokens) {
                    if (t.tipo == analisador.core.TokenType.EOF) continue; 
                    tokensLinhas.add(List.of(
                            String.valueOf(t.linha),
                            String.valueOf(t.coluna),
                            t.lexema,
                            t.descricao
                    ));
                }

                // TABELA 2 DO PDF: LISTA DE TOKENS
                resultado = escreverTabela(doc, content, font, "Lista de Tokens",
                    List.of("Linha", "Col", "Lexema", "Descrição"), tokensLinhas, y - 30, MARGEM);
                content = resultado.content;
                y = resultado.y;

                //TABELA DE SÍMBOLOS
                List<List<String>> simbolosLinhas = tabela.getLinhasParaPDF(); 
                
                // TABELA 3 DO PDF: TABELA DE SÍMBOLOS
                resultado = escreverTabela(doc, content, font, "Tabela de Símbolos",
                    List.of("ID", "Lexema", "Posição (Linha, Coluna)"), simbolosLinhas, y - 30, MARGEM); 
                content = resultado.content;
                y = resultado.y;

                content.close();
                doc.save("saida_analisador_lexico.pdf");
            }

            System.out.println("\nPDF gerado com sucesso: saida_analisador_lexico.pdf");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static class TabelaResult {
        PDPageContentStream content;
        float y;
        TabelaResult(PDPageContentStream content, float y) {
            this.content = content;
            this.y = y;
        }
    }

    // MÉTODO DE DESIGN DA "CAIXA DE CÓDIGO"
    private static TabelaResult escreverCodigoFonte(PDDocument doc, PDPageContentStream content, PDType0Font font,
                                                    String titulo, List<List<String>> linhasCodigo, float y, float margem) throws IOException {
        
        
        final float ALTURA_LINHA = 12; // Espaçamento entre linhas do código
        final float ESPACO_NUM_CODIGO = 10; // Espaço reduzido entre número e código
        final float PADDING_CAIXA = 12; // Espaço interno da caixa

        content.beginText();
        content.setFont(font, 14);
        content.newLineAtOffset(margem, y);
        content.showText(titulo);
        content.endText();
        y -= 10;

        float yInicioCaixa = y; // Guarda o Y inicial antes de desenhar o código
        float xInicioCaixa = margem - PADDING_CAIXA; // X da caixa começa um pouco antes da margem do texto
        float larguraCaixa = PDRectangle.LETTER.getWidth() - (margem - PADDING_CAIXA) * 2; // Largura da caixa até a outra margem
        float yAtual = y - PADDING_CAIXA; // Y inicial do texto dentro da caixa

        content.setFont(font, 9);
        for (List<String> linha : linhasCodigo) {
            if (yAtual - ALTURA_LINHA < margem) { 
                content.addRect(xInicioCaixa, yAtual, larguraCaixa, yInicioCaixa - yAtual);
                content.stroke(); 
                
                content.close();
                PDPage page = new PDPage(PDRectangle.LETTER);
                doc.addPage(page);
                content = new PDPageContentStream(doc, page);
                content.setFont(font, 9); 
                y = 750; 
                yInicioCaixa = y; 
                yAtual = y - PADDING_CAIXA; 
            }

            String numLinha = linha.get(0);
            String conteudoLinha = linha.get(1);

            content.beginText();
            content.newLineAtOffset(margem, yAtual); 
            content.showText(numLinha);
            
            float xCodigo = margem + font.getStringWidth(numLinha) / 1000 * 9 + ESPACO_NUM_CODIGO;
            content.newLineAtOffset(xCodigo - margem, 0); 
            
            float larguraDisponivelTexto = larguraCaixa - (xCodigo - xInicioCaixa) - PADDING_CAIXA * 2;
            int maxChars = (int) (larguraDisponivelTexto / (font.getStringWidth("W") / 1000 * 9)); // Estimativa de caracteres
            String textoLinha = (conteudoLinha != null && conteudoLinha.length() > maxChars && maxChars > 3) ?
                                 conteudoLinha.substring(0, maxChars - 3) + "..." : conteudoLinha;
            content.showText(textoLinha);
            content.endText();
            
            yAtual -= ALTURA_LINHA; 
        }
        
        content.addRect(xInicioCaixa, yAtual - PADDING_CAIXA, larguraCaixa, yInicioCaixa - (yAtual - PADDING_CAIXA));
        content.stroke(); 
      
        return new TabelaResult(content, yAtual - PADDING_CAIXA); 
    }

    // MÉTODO DE DESIGN DAS TABELAS (Tokens e Símbolos)
    private static TabelaResult escreverTabela(PDDocument doc, PDPageContentStream content, PDType0Font font,
                                               String titulo, List<String> cabecalho, List<List<String>> linhas, float y, float margem) throws IOException {

        final float ALTURA_CELULA = 15; 
        final float PADDING_CELULA = 3; 

        float larguraTotalPagina = PDRectangle.LETTER.getWidth() - 2 * margem;
        int numColunas = cabecalho.size();
        
        float[] largurasColunas = new float[numColunas];
       
        if (titulo.equals("Lista de Tokens")) {
             largurasColunas[0] = larguraTotalPagina * 0.1f; // Linha
             largurasColunas[1] = larguraTotalPagina * 0.1f; // Col
             largurasColunas[2] = larguraTotalPagina * 0.3f; // Lexema
             largurasColunas[3] = larguraTotalPagina * 0.5f; // Descrição
        } else if (titulo.equals("Tabela de Símbolos")) {
             largurasColunas[0] = larguraTotalPagina * 0.1f; // ID
             largurasColunas[1] = larguraTotalPagina * 0.4f; // Lexema
             largurasColunas[2] = larguraTotalPagina * 0.5f; // Primeira Aparição
        } else { 
             float larguraPadrao = larguraTotalPagina / numColunas;
             for(int i=0; i<numColunas; i++) largurasColunas[i] = larguraPadrao;
        }

        float xStart = margem;
        final Color AZUL_ESCURO = new Color(0x051b2b);

        // Título
        content.beginText();
        content.setFont(font, 14);
        content.newLineAtOffset(margem, y);
        content.showText(titulo);
        content.endText();
        y -= 10;
        
        //Cabeçalho 
        content.setNonStrokingColor(AZUL_ESCURO);
        content.addRect(xStart, y - ALTURA_CELULA, larguraTotalPagina, ALTURA_CELULA);
        content.fill();
        content.setNonStrokingColor(Color.WHITE);
        desenharLinhasVerticaisCorrigido(content, xStart, y, ALTURA_CELULA, largurasColunas);
        content.moveTo(xStart, y - ALTURA_CELULA);
        content.lineTo(xStart + larguraTotalPagina, y - ALTURA_CELULA);
        content.stroke();
        content.setFont(font, 10); 
        float xAtual = xStart;
        for (int i = 0; i < numColunas; i++) {
             content.beginText();
             content.newLineAtOffset(xAtual + PADDING_CELULA, y - ALTURA_CELULA + 4); 
             content.showText(cabecalho.get(i));
             content.endText();
             xAtual += largurasColunas[i]; 
        }
        y -= ALTURA_CELULA;

        content.setFont(font, 8); 
        for (List<String> linha : linhas) {
            if (y < margem) { 
                content.close();
                PDPage page = new PDPage(PDRectangle.LETTER);
                doc.addPage(page);
                content = new PDPageContentStream(doc, page);
                content.setFont(font, 8);
                y = 750; 
            }

            content.setNonStrokingColor(Color.BLACK); 
            desenharLinhasVerticaisCorrigido(content, xStart, y, ALTURA_CELULA, largurasColunas);
            content.moveTo(xStart, y - ALTURA_CELULA);
            content.lineTo(xStart + larguraTotalPagina, y - ALTURA_CELULA);
            content.stroke();

            xAtual = xStart;
            for (int i = 0; i < numColunas; i++) {
                 String celula = (i < linha.size()) ? linha.get(i) : ""; 
                 content.beginText();
                 content.newLineAtOffset(xAtual + PADDING_CELULA, y - ALTURA_CELULA + 4); 
                 int maxChars = (int) (largurasColunas[i] / 4); 
                 String textoCelula = (celula != null && celula.length() > maxChars) ? 
                                      celula.substring(0, maxChars - 3) + "..." : celula;
                 content.showText(textoCelula);
                 content.endText();
                 xAtual += largurasColunas[i]; 
            }
            y -= ALTURA_CELULA;
        }
        return new TabelaResult(content, y);
    }
    
    // MÉTODO PARA DESENHAR LINHAS 
    private static void desenharLinhasVerticaisCorrigido(PDPageContentStream content, float xStart, float y, float altura, float[] largurasColunas) throws IOException {
        content.moveTo(xStart, y); 
        content.lineTo(xStart, y - altura);
        content.stroke();
        float xAtual = xStart;
        for (int i = 0; i < largurasColunas.length; i++) {
            xAtual += largurasColunas[i];
            content.moveTo(xAtual, y);
            content.lineTo(xAtual, y - altura);
            content.stroke();
        }
    }
}