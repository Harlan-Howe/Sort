public class BozoSort extends SortAlgorithm
{
    public BozoSort(AlgorithmDelegate del, int[] array)
    {
        super(del,array);
    }

    /**
     * determines whether the list is sorted now, an O(N) process. Necessary for BozoSort, but not recommeneded for
     * other sorts.
     * @return true, if variable "array" is in ascending order.
     */
    private boolean isSorted()
    {
        for (int i=1; i< array.length; i++)
            if (array[i] < array[i-1])
                return false;
        return true;
    }


    /**
     * algorithm that does this particular sort, in this case, the Bozo sort.
     */
    public void run()
    {
        int N = array.length;
        while(true)
        {
            int i = (int)(Math.random()*N);
            int j = (int)(Math.random()*N);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;

            // -------------------------------------------------------------
            // include this in the middle of your algorithm to happen OFTEN.
            delegate.visualizeData(array);
            if (!keepRunning)
                return;
            // -------------------------------------------------------------

            if (isSorted()) // NOTE: this method is O(N). Don't use this as part of _your_ algorithms, - it can turn a O(N^2) to an O(N^3).
                            //       (In this case, we're talking O(N!) already, so how much worse can it get?)
                            //       The point is that you should leave your loops when your loops are finished sorting.
                break;


        }
        tellDelegateSortIsComplete();
    }

}
