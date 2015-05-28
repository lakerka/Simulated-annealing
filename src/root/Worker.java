package root;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Random;


public class Worker extends Thread {
    
    public static DecimalFormat decimalFormat = new DecimalFormat("0.00"); 

    String dirPath = "/home/zilva/WebstormProjects/Viz/src/bakalaur/";
    String vertexFilename = "kroA100.tsp"; // opt 21282
    String outputFilename = "out.tsp";
    String bestPathLenLogFilename = "info.log";
    
    public String graphFilename;
    public int maxIterCount = 2;
    public int maxTime = 1000;
    public double initialTemp = 1000.0;
    public double tempCoolingRate = 0.995;
    
    PublishResultsListener publishResultsListener;
    String threadName;
    
    public static final boolean SAVE_TO_FILE = false;
    public static final boolean SAVE_TO_FILE_BEST = false;
    public static final boolean LOG = false;
    
    public Worker(double initialTemperature, double temperatureCoolingRate,
            int maxIterCount, int maxTime,
            PublishResultsListener publishResultsListener, String threadName,
            String graphFilename) {
        this.initialTemp = initialTemperature;
        this.tempCoolingRate = temperatureCoolingRate;
        this.maxIterCount = maxIterCount;
        this.maxTime = maxTime;
        this.publishResultsListener = publishResultsListener;
        this.threadName = threadName;
        this.graphFilename = graphFilename;
    }
    
    public void run() {
               
        Logger infoLogger;
//        new Logger(dirPath + bestPathLenLogFilename);
        double shortestPathLen = 1e9;
        double shortestPathOfIterationLen = 1e9;
        
        Graph graph = new Graph(graphFilename);

        int vertexCount = graph.getVertexCount();
        int[] shortestPath = new int[vertexCount];
        int[] shortestIterationPath = new int[vertexCount];
        int[] path = new int[vertexCount];
        int[] tmpPath = new int[vertexCount];
       
        for (int it = 0; it < maxIterCount; it++) {
         
            double curTemp = initialTemp;
            graph.clearVisited();
            initPath(path, graph);
            
            for (int time = 0; time < maxTime; time++) {
                Utils.copy(path, tmpPath);
                double curPathLen = getPathLen(graph, path);
                curPathLen = probabalisticSwap(curPathLen, curTemp, path, tmpPath, graph);
                if (curPathLen < shortestPathOfIterationLen) {
                    Utils.copy(path, shortestIterationPath);
                    shortestPathOfIterationLen = curPathLen;
                }
                // decrease temperature
                curTemp = getNewTemperature1(curTemp, tempCoolingRate);
                if (SAVE_TO_FILE) {
                    infoLogger.logInfo(String.valueOf(time) + "\t" + decimalFormat.format(curTemp) + "\t" + decimalFormat.format(curPathLen) + "\t" + decimalFormat.format(shortestPathLen));
                }
            }
            if (shortestPathOfIterationLen < shortestPathLen) {
                shortestPathLen = shortestPathOfIterationLen;
                Utils.copy(shortestIterationPath, shortestPath);
            }
        }
//        savePathToFile(dirPath + outputFilename, shortestIterationPath);
        publishResultsListener.onPublish(shortestPathLen, shortestPath, threadName);
//        System.out.println(shortestPathLen);
    }
    

    public static double getNewTemperature3(double curTemp, int time) {
        // inverse log decrease
        return Math.max(curTemp/Math.log((double)(time + 2)), 0.0);
    }

    public static double getNewTemperature2(double curTemp, double cooledOff) {
        // additive decrease
        return Math.max(curTemp - cooledOff, 0.0);
    }
    
    public static double getNewTemperature1(double curTemp, double coolingRate) {
        // decreases temperature by multiplying by constant
        return Math.max(curTemp*coolingRate, 0.0);
    }
    
    public static boolean isNewPathAccepted(double oldPathLen, double newPathLen, double temperature) {
        double pathLenDiff = newPathLen - oldPathLen;
        double acceptProb = Math.exp(-(pathLenDiff)/temperature);
        
        double uniformRandom = (new Random()).nextDouble();
//        System.out.print("old pathLen: " + decimalFormat.format(oldPathLen));
//        System.out.print(" new pathLen: " + decimalFormat.format(newPathLen));
//        System.out.print(" temp: " + decimalFormat.format(temperature));
////        System.out.print(" e^" + decimalFormat.format(-(newPathLen - oldPathLen)/temperature));
//        System.out.print(" accept prob: " + decimalFormat.format(acceptProb));
//        System.out.print(" random prob: " + decimalFormat.format(uniformRandom));
//        System.out.println(" accept: " + String.valueOf(newPathLen < oldPathLen) + " || " + String.valueOf(uniformRandom < acceptProb));
        
//        System.out.println( " is accepted: " + String.valueOf((newPathLen < oldPathLen) || (uniformRandom < acceptProb)));
//        System.out.println();
        if ((newPathLen < oldPathLen) || (uniformRandom < acceptProb)) {
            return true;
        }
        return false;
    }
    
    
    public static double probabalisticSwap(double curPathLen, double temperature, int[] curPath, int[] tmpPath, Graph graph) {
        int vCount = graph.getVertexCount();
        int rnd1 = 0, rnd2 = 0;
        while (rnd1 == rnd2) {
            rnd1 = Utils.randInt(0, vCount - 1);
            rnd2 = Utils.randInt(0, vCount - 1);
        }
        // swap vertexes
        tmpPath[rnd1] = curPath[rnd2];
        tmpPath[rnd2] = curPath[rnd1];
        double newPathLen = getPathLen(graph, tmpPath);
        
//        System.out.print("cur pathLen: " + String.valueOf(curPathLen));
//        System.out.print(" new pathLen: " + String.valueOf(newPathLen));
//        System.out.print(" temp: " + String.valueOf(temp));
//        System.out.println( " is accepted: " + isNewPathAccepted(curPathLen, newPathLen, temp));
        
        if (isNewPathAccepted(curPathLen, newPathLen, temperature)) {
            // save new path
            Utils.swap(curPath, rnd1, rnd2);
            return newPathLen;
        }
        return curPathLen;
    }
    
    public static void initPath(int[] path, Graph graph) {
        int visitedCount = 0;
        int vertexCount = graph.getVertexCount();
        while (visitedCount < vertexCount) {
            Vertex randVertex = graph.getRandomUnvisitedVertex();
            int id =randVertex.getId();
            graph.setVisited(id, true);
            path[visitedCount] = id;
            visitedCount += 1;
        }
    }
    
    public static double getPathLen(Graph g, int[] path) {
        double distance = 0.0;
        for (int i = 0; i < path.length - 1; i++) {
            int id1 = path[i];
            int id2 = path[i + 1];
            distance += g.getDist(id1, id2);
        }
        distance += g.getDist(path[path.length - 1], path[0]);
        return distance;
    }
}
