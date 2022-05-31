public interface AlgorithmDelegate
{

    /**
     * performs the process of updating the visualization on screen (if any) and possibly adds a delay between "frames."
     * @param array - the list of N integers, from 0 -> (N-1), potentially in random order.
     */
    public void visualizeData(DelayedArray<Integer> array);

    /**
     * handle what to do when the sort is completed.
     */
    public void SortIsFinished();
}
