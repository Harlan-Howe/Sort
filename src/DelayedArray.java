/**
 * A wrapper class for an array of the given type, with a delay, ms_delay, incurred when reading or writing to the array.
 * @param <T>
 */
public class DelayedArray<T>
{

    private T[] theArray;
    private int ms_delay;

    /**
     * Constructor
     * @param arr - array of values to be managed by this class
     * @param ms_delay - millisecond delay per get or set command.
     */
    public DelayedArray(T[] arr, int ms_delay)
    {
        setTheArray(arr);
        setMs_delay(ms_delay);
    }

    /**
     * Constructor with default delay of 1 ms.
     * @param arr - array of values to be managed by this class.
     */
    public DelayedArray(T[] arr)
    {
        this(arr,1);
    }


    private void setTheArray(T[] arr)
    {
        theArray = arr;
    }

    /**
     * set the delay in milliseconds for each get or set instruction.
     * @param msd - the millisecond delay.
     */
    public void setMs_delay(int msd)
    {
        ms_delay = msd;
    }

    public int getMs_delay() { return ms_delay;}

    /**
     * retrieves the number of values in the managed array. Identical to size().
     * @return - the number of values
     */
    public int length() {return theArray.length;}

    /**
     * retrieves the number of values in the managed array. Identical to length().
     * @return - the number of values
     */
    public int size() {return theArray.length;}

    /**
     * Alters the value of the managed array at loc to become val, delaying for the current ms_delay after doing so.
     * @param val - the new value of one element in the array.
     * @param loc - where in the array to alter
     */
    public void setValueAtLocation(T val, int loc)
    {
        if (loc < 0 || loc >= theArray.length)
            throw new ArrayIndexOutOfBoundsException("You attempted to change item at index "+loc+" but this array is only "+theArray.length+" items long.");
        try
        {
            theArray[loc] = val;
            Thread.sleep(ms_delay);
        }
        catch (InterruptedException iExp)
        {
            return;
        }
    }

    /**
     * Identifies the value of the managed array at the given loc, delays for the current ms_delay, and then returns that value.
     * @param loc - the location in question.
     * @return - the value that was at that location.
     */
    public T getValueAtLocation(int loc)
    {
        if (loc < 0 || loc >= theArray.length)
            throw new ArrayIndexOutOfBoundsException("You attempted to access item at index "+loc+" but this array is only "+theArray.length+" items long.");
        T result;
        try
        {
            result = theArray[loc];
            Thread.sleep(ms_delay);
        }
        catch (InterruptedException iExp)
        {
            return null;
        }
        return result;
    }


}
