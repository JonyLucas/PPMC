import ppm.PPMEncoder;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        int context;
        String inputFile, outputFile;
        Scanner scan = new Scanner(System.in);

        System.out.println("Digite o caminho do arquivo de entrada:");
        inputFile = scan.nextLine();

        System.out.println("Digite o caminho do arquivo de saida");
        outputFile = scan.nextLine();

        System.out.println("Digite o tamanho do contexto do PPM: ");
        context = scan.nextInt();

        PPMEncoder encoder = new PPMEncoder(inputFile, outputFile, context);
        encoder.readAndCodify();

    }
}
