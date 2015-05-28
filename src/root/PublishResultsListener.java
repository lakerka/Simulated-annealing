package root;

public interface PublishResultsListener {

    public void onPublish(double shortestPathLen, int[] shortestPath, String threadName);
}
