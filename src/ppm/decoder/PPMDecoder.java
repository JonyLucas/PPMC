package ppm.decoder;

import arithmeticCoding.decoder.ArithmeticDecoder;
import infra.BitInputStream;
import infra.BitOutputStream;
import ppm.decoder.model.PPMTree;

import java.io.*;

public class PPMDecoder {

    private int context, fileSize;
    private DataInputStream in;
    private DataOutputStream out;
    private ArithmeticDecoder decoder;

    public PPMDecoder(String inputFile, String outputFile) throws IOException{
        System.out.println("Input: " + inputFile);
        this.out = new DataOutputStream( new FileOutputStream(outputFile));
        this.in = new DataInputStream( new FileInputStream(inputFile) );

        this.context = in.read();
        this.fileSize = in.read();

        this.decoder = new ArithmeticDecoder(new BitInputStream(in));
    }

    public void readAndDecode() throws IOException {

        try {

            for (int i = 0; i <= fileSize; i++){

            }

        }catch (Exception e){

        }


    }


}
