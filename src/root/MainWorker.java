package root;

import java.util.ArrayList;
import java.util.List;


public class MainWorker implements PublishResultsListener {

    double shortestPathLen = 1e9;
    List<Integer> shortestPath = new ArrayList<Integer>();
    int runningThreadCount;
    
    String dirPath = "/home/zilva/WebstormProjects/Viz/src/bakalaur/";
//    String dirPath = "/home/ubuntu/data/";
    String graphFilenameSuffix = "kroA100.tsp";
    String outputFilename = "out.tsp";
    
    public void start(int threadCount, double initialTemperature, double temperatureCoolingRate,
            double equilibriumTemperature) {
            
        this.runningThreadCount = threadCount;
        
        for (int i = 0; i < threadCount; i++) {
            (new Worker(initialTemperature, temperatureCoolingRate, 
                    equilibriumTemperature, this,
                    "Thread_" + String.valueOf(i+1),
                    dirPath + graphFilenameSuffix
                    )).start();
        }
    }
    
    public void onPublish(double shortestPathLen, int[] shortestPath, String threadName) {
        
        if (shortestPathLen < this.shortestPathLen) {
//            System.out.println("Shorter from: " + threadName + " "
//                    + String.valueOf(shortestPathLen)
//                    + " < " + String.valueOf(this.shortestPathLen));
            this.shortestPathLen = shortestPathLen;
            Utils.copy(shortestPath, this.shortestPath);
        }
        runningThreadCount -= 1;
        if (runningThreadCount == 0) {
            System.out.println("Shortest path len: " + String.valueOf(this.shortestPathLen));
            Utils.savePathToFile(dirPath + outputFilename, this.shortestPath);
        }
    }
}
