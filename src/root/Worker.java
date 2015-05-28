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
    public double equilibriumTemperature;
    public double initialTemperature;
    public double tempCoolingRate;
    
    PublishResultsListener publishResultsListener;
    String threadName;
    
    public static final boolean SAVE_TO_FILE = false;
    public static final boolean SAVE_TO_FILE_BEST = false;
    public static final boolean LOG = false;
    
    public Worker(double initialTemperature, double temperatureCoolingRate,
            double equilibriumTemperature,
            PublishResultsListener publishResultsListener, String threadName,
            String graphFilename) {
        this.initialTemperature = initialTemperature;
        this.tempCoolingRate = temperatureCoolingRate;
        this.equilibriumTemperature = equilibriumTemperature;
        this.publishResultsListener = publishResultsListener;
        this.threadName = threadName;
        this.graphFilename = graphFilename;
    }
    
    public void run() {
               
        Logger infoLogger;
//        new Logger(dirPath + bestPathLenLogFilename);
        
        Graph graph = new Graph(graphFilename);
        int vertexCount = graph.getVertexCount();
        
        double shortestPathLen = 1e9;
        int[] shortestPath = new int[vertexCount];
        
        int[] tmpPath = new int[vertexCount];
        int iteration = 0;
        double curTemperature = initialTemperature;
        
        graph.clearVisited();
        initPath(shortestPath, graph);
        
        while (curTemperature > equilibriumTemperature) {    
            iteration++;
            Utils.copy(shortestPath, tmpPath);
            double curPathLen = probabalisticSwap(shortestPathLen, curTemperature, shortestPath, tmpPath, graph);
            shortestPathLen = Math.min(curPathLen, shortestPathLen);
            // decrease temperature
            curTemperature = getNewTemperature1(curTemperature, tempCoolingRate);
            if (SAVE_TO_FILE) {
                infoLogger.logInfo(String.valueOf(iteration) + "\t" + decimalFormat.format(curTemperature) + "\t" + decimalFormat.format(curPathLen) + "\t" + decimalFormat.format(shortestPathLen));
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
