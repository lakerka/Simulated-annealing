package root;

public class Worker extends Thread {
    
    private Graph graph;
    
    private String threadName;
    private IThreadFinishedListener threadFinishedListener;
    private IRequestShortestPathListener requestShortestPathListener;
    //worker algo parameters
    private int resetCount;
    private int[] workerShortestPath;
    
    
    //simulated annealing algo parameters
    private double initialTemperature;
    private double temperatureCoolingRate;
    private double equilibriumTemperature;
    private IIterationCountGetter itCountGetter;
    
    
    public Worker(String graphFilename, String threadName,
            IThreadFinishedListener threadFinishedListener,
            IRequestShortestPathListener requestShortestPathListener,
            int resetCount,
            double initialTemperature, double temperatureCoolingRate,
            double equilibriumTemperature,
            IIterationCountGetter iterationCountGetter) {
        super();
        
        this.graph = new Graph(graphFilename);
        this.workerShortestPath = new int[graph.getVertexCount()];
        
        this.threadName = threadName;
        this.threadFinishedListener = threadFinishedListener;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
        this.requestShortestPathListener = requestShortestPathListener;
        this.resetCount = resetCount;
        this.initialTemperature = initialTemperature;
        this.temperatureCoolingRate = temperatureCoolingRate;
        this.equilibriumTemperature = equilibriumTemperature;
        this.itCountGetter = iterationCountGetter;
    }

    @Override
    public void run() {
        SimulatedAnnealing sa = new SimulatedAnnealing(initialTemperature, 
                temperatureCoolingRate, equilibriumTemperature, 
                itCountGetter, graph);
        int[] initializedPath = sa.runSimulatedAnnealing(null);
        Utils.copy(initializedPath, workerShortestPath);
        
        for (int i = 0; i < resetCount; i++) {
            graph.clearVisited();
            sa.runSimulatedAnnealing(workerShortestPath);
            requestShortestPathListener.onRequestShortestPath(workerShortestPath,
                    graph.getPathLen(workerShortestPath));
        }
        threadFinishedListener.onThreadFinished(threadName);
    }
}
