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
    private FileOutputStream out;
    private DataOutputStream dataOut;
    private ArithmeticEncoder encoder;

    public PPMEncoder(String inputFile, String outputFile, int context) throws FileNotFoundException {
        this.context = context;
        this.inputFile = inputFile;

        this.out = new FileOutputStream(outputFile);
        this.dataOut = new DataOutputStream(out);
        this.encoder = new ArithmeticEncoder(new BitOutputStream(out));
    }

    /**
     * Método que realiza a construção inicial da árvore do PPM, a escrita do cabeçalho (K do contexto e conjunto do alfabeto),
     * e, posteriormente, realiza a segunda leitura do arquivo, utilizando um buffer (substring) de tamanho máximo K (tamanho
     * do contexto), e faz a codificação na árvore (por meio do método findByContext da PPMTree).
     * @throws Exception
     */
    public void readAndCodify() throws Exception {

        int alphabet[] = getAlphabet(inputFile);
        writeReader(context, alphabet);
        PPMTree tree = new PPMTree(context, alphabet);

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) { //Leitura com Buffered Reader
            int[] subString = new int[context];

            for (int i = 0; i < context; i++) {
                subString[i] = -1;
            }

            int currentSymbol = br.read();
            while (currentSymbol != -1) {
                subString = addAndShift(subString, currentSymbol);
                tree.findByContext(subString, encoder);
                currentSymbol = br.read();
            }

            encoder.finish();
            dataOut.close();
            out.close();
            tree.showTree();

        } catch (FileNotFoundException e) {
            throw new Exception("Erro: Arquivo não encontrado!");
        } catch (Exception e){
            throw new Exception("Erro: " + e.getMessage());
        }
    }


    /**
     * Método estático que realiza a primeira leitura do arquivo e retorna um array
     * contendo todos os símbolos do alfabeto do arquivo de entrada.
     * @param filePath
     * @return
     * @throws Exception
     */
    public int[] getAlphabet(String filePath) throws Exception {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) { // Leitura com Buffered Reader (Provisório)
            int alphabetSize = 0;
            int auxAlphabet[] = new int[257]; // Alfabeto com os 256 símbolos do ASCII e -1 para indicar o fim do conjunto (Para facilitar a leitura no decodificador)
            fill(auxAlphabet, -1); // Preenche o alfabeto com -1

            int currentSymbol = br.read();
            while (currentSymbol != -1) {

                for (int i = 0; i < 256; i++){
                    if(auxAlphabet[i] == currentSymbol){
                        break;
                    }
                    if(auxAlphabet[i] == -1){
                        auxAlphabet[i] = currentSymbol;
                        alphabetSize++;
                        break;
                    }
                }
                currentSymbol = br.read();
            }

            int alphabet[] = new int[alphabetSize];

            for (int i = 0; i < alphabetSize; i++){
                alphabet[i] = auxAlphabet[i];
            }

            return alphabet;

        } catch (FileNotFoundException e) {
            throw new Exception("Erro: Arquivo não encontrado!");
        } catch (Exception e){
            throw new Exception("Erro: " + e.getMessage());
        }
    }

    /**
     * @param context
     * @param alphabet
     * @throws FileNotFoundException
     */
    private void writeReader(int context, int[] alphabet){
        try{
            int size = alphabet.length;
            String header = context + " ";

            for (int i = 0; i < size; i++)
                header += alphabet[i] + " ";

            System.out.println("Header: " + header);
            dataOut.writeBytes(header + "\n");

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
