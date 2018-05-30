package ppm.decoder;

import arithmeticCoding.ArithmeticDecoder;
import infra.BitInputStream;
import ppm.decoder.model.PPMTree;

import java.io.*;

public class PPMDecoder {

    private int context, fileSize;
    private DataInputStream in;
    private DataOutputStream out;
    private ArithmeticDecoder decoder;

    public PPMDecoder(String inputFile, String outputFile) throws IOException{
        this.out = new DataOutputStream( new FileOutputStream(outputFile));
        this.in = new DataInputStream( new FileInputStream(inputFile) );

        this.context = in.read();
        this.fileSize = in.read();

        this.decoder = new ArithmeticDecoder(new BitInputStream(in));
    }

    public void readAndDecode() throws IOException {

        try {
            PPMTree tree = new PPMTree(decoder, out, context);

            for (int i = 0; i <= fileSize; i++){
                tree.searchAndDecode();
            }

            tree.showTree();

        }catch (Exception e){

        }


    }


}
