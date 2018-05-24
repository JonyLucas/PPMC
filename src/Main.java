import ppm.PPMEncoder;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        int context;
        String filePath;
        Scanner scan = new Scanner(System.in);

        System.out.println("Digite o caminho do arquivo:");
        filePath = scan.nextLine();

        System.out.println("Digite o tamanho do contexto do PPM: ");
        context = scan.nextInt();

        PPMEncoder encoder = new PPMEncoder(filePath, context);
        encoder.readAndCodify();

    }
}
