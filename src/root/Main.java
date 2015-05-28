package root;

public class Main {

    
    public static void main(String[] args) {
        MainWorker mainWorker = new MainWorker();
        if (args.length == 1) {
            mainWorker.start(Integer.parseInt(args[0]), 1000.0, 0.9995, 0.0001);
        }
    }
}
