import java.awt.image.BufferedImage;

public abstract class SortAlgorithm extends Thread
{
    protected AlgorithmDelegate delegate;
    protected DelayedArray<Integer> array;
    protected boolean keepRunning = true;

    /**
     * Constructor - indicates which class should handle 1) the per-iteration updates and 2) notification that the
     * algorithm is done.; and the array to sort.
     * @param del - An object implementing the AlgorithmDelegate interface, either SortPanel or TextRunner.
     * @param array - An array of N integers, from 0 -> (N-1), inclusive, initially in random order.
     */
    public SortAlgorithm(AlgorithmDelegate del, DelayedArray<Integer> array)
    {
        delegate = del;
        this.array = array;
    }

    public void cancel()
    {
        keepRunning = false;
    }

    /**
     * This is the method that you will write that actually sorts the array of integers.
     */
    public abstract void run();


    public void tellDelegateSortIsComplete()
    {
        if (delegate != null)
        {
            delegate.SortIsFinished();
//            delegate.visualizeData(array);
        }
    }


}
