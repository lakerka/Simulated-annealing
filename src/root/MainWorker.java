package root;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainWorker implements IThreadFinishedListener, IRequestShortestPathListener {

    private double globalShortestPathLen = 1e9;
    private List<Integer> globalShortestPath = Collections.synchronizedList(new ArrayList<Integer>());
    private int runningThreadCount;
    
    String dirPath = "/home/zilva/WebstormProjects/Viz/src/bakalaur/";
//    String dirPath = "/home/ubuntu/data/";
    String graphFilenameSuffix = "kroA100.tsp";
    String outputFilename = "out.tsp";
    
    public void start(int threadCount, double initialTemperature, 
            double temperatureCoolingRate, double equilibriumTemperature,
            int resetCount, IIterationCountGetter iterationCountGetter) {
            
        this.runningThreadCount = threadCount;
        for (int i = 0; i < threadCount; i++) {  
            String threadName = "Thread_" + String.valueOf(i+1);
            (new Worker(dirPath + graphFilenameSuffix, threadName, this, this,
                    resetCount/threadCount, initialTemperature,
                    temperatureCoolingRate, equilibriumTemperature, 
                    iterationCountGetter)).start();
        }
    }

    @Override
    synchronized public void onThreadFinished(String threadName) {
        runningThreadCount -= 1;
        if (runningThreadCount == 0) {
            System.out.println("Shortest path len: " + String.valueOf(this.globalShortestPathLen));
            Utils.savePathToFile(dirPath + outputFilename, this.globalShortestPath);
        }
    }
    
    @Override
    synchronized public void onRequestShortestPath(int[] localShortestPath,
            double localShortestPathLen) {
        if (localShortestPathLen < globalShortestPathLen) {
            globalShortestPathLen = localShortestPathLen;
            Utils.copy(localShortestPath, globalShortestPath);
        }else {
            Utils.copy(globalShortestPath, localShortestPath);   
        }
    }
}
