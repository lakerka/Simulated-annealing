package root;

import java.util.Arrays;

public class Main {

    
    public static void main(String[] args) {
        MainWorker mainWorker = new MainWorker();
//        System.out.println(Arrays.toString(args));
        if (args.length == 1) {
            mainWorker.start(Integer.parseInt(args[0]), 1000.0, 0.995, 10000, 1000);
        }
    }
}
