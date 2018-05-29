package ppm.decoder;

import arithmeticCoding.decoder.ArithmeticDecoder;
import infra.BitInputStream;
import infra.BitOutputStream;
import ppm.decoder.model.PPMTree;

import java.io.*;

public class PPMDecoder {

    private DataInputStream in;
    private DataOutputStream out;
    private ArithmeticDecoder decoder;

    public PPMDecoder(String inputFile, String outputFile) throws IOException{
        System.out.println("Input: " + inputFile);
        this.out = new DataOutputStream( new FileOutputStream(outputFile));
        this.in = new DataInputStream( new FileInputStream(inputFile) );
        this.decoder = new ArithmeticDecoder(new BitInputStream(in));
    }

    public void readAndDecodefy() throws IOException {

        System.out.println(in.read());
        System.out.println(in.read());
        try {

        }catch (Exception e){

        }


    }


}
