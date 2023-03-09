import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class TextRunner implements AlgorithmDelegate
{

    int currentN;
    String sortName;
    int trialSize;
    SortAlgorithm currentAlgorithm;
    Date startTime;
    Integer[] array;

    public static void main(String[] args)
    {
        TextRunner app = new TextRunner();
        app.doRun();
    }

    /**
     * This method asks the user which runs to do and then performs them.
     */
    public void doRun()
    {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Which Sort do you want? For example, Bozo or Bubble");
        sortName = keyboard.nextLine();
        Class<SortAlgorithm> theAlgClass = null;
        try
        {
            theAlgClass = (Class<SortAlgorithm>) Class.forName(sortName + "Sort");
        }catch (ClassNotFoundException cnfExp)
        {
            System.out.println("You tried to create a "+sortName+"Sort object, but that class does not yet exist.");
            cnfExp.printStackTrace();
            System.exit(1);
        }


        System.out.println("What values of N do you want to run? Enter a list of integers, separated by spaces, commas or tabs.");
        String Nstrings = keyboard.nextLine();
        String[] splits = Nstrings.split("[, /\t]+");
        ArrayList<Integer> NList = new ArrayList<>();
        for (String s: splits)
        {
            try
            {
                NList.add(Integer.parseInt(s));
            }catch (NumberFormatException nfExp)
            {
                System.out.println("Skipped invalid number: '"+s+"'");
            }
        }
        for (int N: NList)
            System.out.println(N);
        System.out.println("How many iterations per N value?");
        trialSize = keyboard.nextInt();

        // We're done asking questions - now do the runs!
        System.out.println(sortName);
        for (int N : NList)
        {
            currentN = N;
            for (int iteration=0; iteration < trialSize; iteration++)
            {
                array = new Integer[N];
                for (int i=0 ; i<N; i++)
                    array[i]=i;
                for (int i=0; i<2*N; i++)
                {
                    int a = (int)(Math.random()*N);
                    int b = (int)(Math.random()*N);
                    int temp = array[a];
                    array[a] = array[b];
                    array[b] = temp;
                }
                DelayedArray da = new DelayedArray<Integer>(array,0);
                startTime = new Date();
                Class[] parameterList = {AlgorithmDelegate.class, DelayedArray.class};
                try
                {
                    currentAlgorithm = theAlgClass.getDeclaredConstructor(parameterList).newInstance(this, da);
                }
                catch (Exception exp)
                {
                    exp.printStackTrace();
                }
                currentAlgorithm.run();
            }

        }

    }

    @Override
    public void visualizeData(Integer[] array)
    {
        // I've commented this out - it would be a way to add a millisecond delay to each step of your algorithm.
        // Reactivate if your sorts are happening too quickly to time! (But be consistent....)
//        try
//        {
//            Thread.sleep(1);
//        }catch(InterruptedException iExp)
//        {
//            iExp.printStackTrace();
//        }
    }

    @Override
    /**
     * the algorithm is done, now we wish to print the result of the algorithm.
     */
    public void SortIsFinished()
    {
        Date now = new Date();
        long millisecondsExpired = (now.getTime()-startTime.getTime());
        System.out.print(currentN+"\t"+millisecondsExpired);
        for (int i=1; i<currentN; i++)
        {
            if (array[i]<array[i-1])
            {
                System.out.print("\tERROR-not sorted.");
                break;
            }
        }
        System.out.print("\n");
    }
}
