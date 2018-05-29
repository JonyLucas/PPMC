package ppm.decoder.model;

import arithmeticCoding.decoder.ArithmeticDecoder;
import arithmeticCoding.tables.SimpleFrequencyTable;

import java.io.IOException;

public class PPMTree {

    private int MAX_SIZE = 10, maxContext;
    private int[] equivalentProbabilitySymbols, subString;
    private PPMNode root;
    private ArithmeticDecoder decoder;

    public PPMTree(ArithmeticDecoder decoder, int maxContext) throws Exception {
        if (maxContext < 0 || maxContext > MAX_SIZE)
            throw new Exception("Valor do contexto inválido, este valor deve estar entre 0 e 10");

        this.maxContext = maxContext;
        this.root = new PPMNode();
        this.decoder = decoder;

        equivalentProbabilitySymbols = new int[256];
        for (int i = 0; i < 256; i++)
            equivalentProbabilitySymbols[i] = i;

    }

    public int decode(){
        int symbol = 0, maxLevel = maxContext;
        int[] frequencies = new int[equivalentProbabilitySymbols.length];
        PPMNode searchNode = root;

        for (int i = 0; i < equivalentProbabilitySymbols.length; i++){
            frequencies[i] = 1;
        }

        try {
            symbol = decoder.read(new SimpleFrequencyTable(frequencies));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return symbol;
    }


}
