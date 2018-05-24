package ppm;

import ppm.model.PPMTree;

import java.io.*;

public class PPMEncoder {

    private BufferedReader br; // Uso provisório de um leitor de texto, para fins de testes. Remover posteriormente.
    private int context;
    private PPMTree tree;

    public PPMEncoder(String filePath, int context) throws Exception {
        try {
            this.br = new BufferedReader(new FileReader(filePath));
            this.context = context;
            this.tree = new PPMTree(context);
        } catch (FileNotFoundException e) {
            throw new Exception("Arquivo não encontrado!");
        }
    }

    public void readAndCodify(){
        try {

            int [] subString = new int[context];

            for (int i = 0; i < context; i++){
                subString[i] = -1;
            }

            int currentSymbol =  br.read();
            while (currentSymbol != -1){
                subString = addAndShift(subString, currentSymbol);
                tree.findByContext(subString);
                currentSymbol = br.read();
            }

            tree.showTree();

            close();

        } catch (IOException e) {
            close();
            e.printStackTrace(); // Adicionar uma mensagem de exceção
        }
    }

    private void close(){
        try {
            this.br.close();
        } catch (IOException e) {
            System.out.println("Ocorreu um erro ao tentar fechar o arquivo.");
        }
    }

    /**
     * Realiza o deslocamento dos símbolos para à esquerda (eliminando o primeiro elemento do array)
     * e adiciona o símbolo passado como argumento na ultima posição.
     * Caso o array possua posição vazia (indicada pelo valor "-1"), o símbolo é adicionado nesta posição
     * e retorna-se o array.
     * @param subString
     * @param symbol
     * @return
     */
    private int[] addAndShift(int[] subString, int symbol){
        int i, size = subString.length;

        // Verifica se há posição vazia (-1) no array, e insere o símbolo em questão nesta posição.
        for (i = 0; i < size; i++){
            if (subString[i] == -1){
                subString[i] = symbol;
                return subString;
            }
        }

        // Realiza o deslocamento para à esquerda das posições e insere o símbolo em questão no final.
        for (i = 0; i < size-1; i++){
            subString[i] = subString[i+1];
        }
        subString[i] = symbol;

        return subString;
    }

}
