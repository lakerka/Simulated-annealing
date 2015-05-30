package root;

public class Main {
   
    public static void main(String[] args) {
        MainWorker mainWorker = new MainWorker();
        if (args.length == 1) {
            mainWorker.start(Integer.parseInt(args[0]),
                    1000.0, // initial temp
                    0.95, // temp cooling rate
                    0.001, // equilibrium temp
                    100, // reset count
                    (new IIterationCountGetter() {
                        @Override
                        public int getIterationCount(double temperature) {
                            return 100;
                        }
                    })
                    ); 
        }
    }
}
