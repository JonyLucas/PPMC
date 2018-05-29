package ppm;

import arithmeticCoding.encoder.ArithmeticEncoder;
import arithmeticCoding.tables.SimpleFrequencyTable;
import infra.BitOutputStream;
import ppm.model.PPMTree;

import java.io.*;

import static java.util.Arrays.fill;

public class PPMEncoder {

    private int context;
    private String inputFile;
    private DataOutputStream out;
    private ArithmeticEncoder encoder;

    public PPMEncoder(String inputFile, String outputFile, int context) throws FileNotFoundException {
        this.context = context;
        this.inputFile = inputFile;

        this.out = new DataOutputStream(new FileOutputStream(outputFile));
        this.encoder = new ArithmeticEncoder(new BitOutputStream(out));
    }

    /**
     * Método que realiza a construção inicial da árvore do PPM, a escrita do cabeçalho (K do contexto e conjunto do alfabeto),
     * e, posteriormente, realiza a segunda leitura do arquivo, utilizando um buffer (substring) de tamanho máximo K (tamanho
     * do contexto), e faz a codificação na árvore (por meio do método findByContext da PPMTree).
     * @throws Exception
     */
    public void readAndCodify() throws Exception {

        writeReader(context, getFileSize(inputFile));
        PPMTree tree = new PPMTree(encoder, context);

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) { //Leitura com Buffered Reader
            int[] subString = new int[context];

            for (int i = 0; i < context; i++) {
                subString[i] = -1;
            }

            int currentSymbol = br.read();
            while (currentSymbol != -1) {
                subString = addAndShift(subString, currentSymbol);
                tree.findByContext(subString);
                currentSymbol = br.read();
            }

            encoder.finish();
            out.close();
            tree.showTree();

        } catch (FileNotFoundException e) {
            throw new Exception("Erro: Arquivo não encontrado!");
        } catch (Exception e){
            throw new Exception("Erro: " + e.getMessage());
        }
    }


    /**
     * Método que percorre o arquivo de entrada e calcula o número de bytes deste arquivo.
     * @param filePath
     * @return
     * @throws Exception
     */
    public int getFileSize(String filePath) throws Exception {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) { // Leitura com Buffered Reader (Provisório)
            int fileSize = 0;

            int currentSymbol = br.read();
            while (currentSymbol != -1) {
                fileSize++;
                currentSymbol = br.read();
            }

            return fileSize;

        } catch (FileNotFoundException e) {
            throw new Exception("Erro: Arquivo não encontrado!");
        } catch (Exception e){
            throw new Exception("Erro: " + e.getMessage());
        }
    }

    /**
     * @param context
     * @param fileSize
     * @throws FileNotFoundException
     */
    private void writeReader(int context, int fileSize){
        try{
            out.writeByte(context);
            out.writeByte(fileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Realiza o deslocamento dos símbolos para à esquerda (eliminando o primeiro elemento do array)
     * e adiciona o símbolo passado como argumento na ultima posição.
     * Caso o array possua posição vazia (indicada pelo valor "-1"), o símbolo é adicionado nesta posição
     * e retorna-se o array.
     *
     * @param subString
     * @param symbol
     * @return
     */
    private int[] addAndShift(int[] subString, int symbol) {
        int i, size = subString.length;

        // Realiza o deslocamento para à esquerda das posições e insere o símbolo em questão no final.
        for (i = 0; i < size - 1; i++) {
            subString[i] = subString[i + 1];
        }
        subString[i] = symbol;

        return subString;
    }

}
