import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;

public class SortPanel extends JPanel implements AlgorithmDelegate
{
    private BufferedImage myCanvas;
    private boolean dirtyCanvas;
    private SortFrame myFrame;
    private int delay_ms;
    private double sines[], cosines[];
    private Color colors[];
    private Date lastUpdate; // ADDED
    private int expected_N;

    public SortPanel(SortFrame myFrame)
    {
        super();
        dirtyCanvas = true;
        setBackground(Color.lightGray);
        this.myFrame = myFrame;
        lastUpdate = new Date(); // ADDED
        delay_ms = 1; // ADDED
    }


    /**
     * gets a copy of the BufferedImage into which we are drawing our visualization. If the canvas is "dirty," that means
     * we need to generate a new one. Note: "Dirty" does not mean that it has been written on, like a dirty whiteboard;
     * instead, it means that the current one is the wrong size, or hasn't been created yet.
     * @return a BufferedImage the size of this panel.
     */
    public BufferedImage getCanvas()
    {
        if (dirtyCanvas)
        {
            System.out.println("(" + getWidth() + ", " + getHeight() + ")");
            myCanvas = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            dirtyCanvas = false;
        }
        return myCanvas;
    }

    /**
     * Passes along to the parent Window that the algorithm is done and it should update its GUI accordingly.
     */
    public void SortIsFinished()
    {
        lastUpdate = new Date(0); // set the last update to 1970!

        myFrame.endRunGUI();
    }

    public void setDelayMS(int delay)
    {
        delay_ms = delay;
    }

    /**
     * draws the current visualization to the screen. This is separate from visualizeData, because visualizeData is
     * running as a background thread, perhaps more often than we can update the screen. This just copies the latest
     * version of the "canvas" image.
     * @param g - the Graphics object that represents the panel window.
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        getCanvas();
        if (myCanvas == null)
            return;
        synchronized (myCanvas)
        {
            g.drawImage(myCanvas,0,0,this);
        }

    }

    /**
     * do any setup for drawing an array of N integers that only needs to be done once, rather than repeatedly
     * @param N - the size of the array you'll be asked to visualize
     */
    public void prepForArrayWithSizeN(int N)
    {
        System.out.println("Prepping - new array size "+N);

        // TODO: Optional. If there are any pre-calculations (such as generating a list of sines and cosines) you wish
        //  to do when N changes, before the algorithm runs, add them here.
        /*
        // for example....
        sines = new double[N+1];
        cosines = new double[N+1];
        colors = new Color[N];

        double delta = 2*Math.PI/N;
        for (int i=0; i<N; i++)
        {
            sines[i] = Math.sin(delta*i);
            cosines[i] = Math.cos(delta*i);
            colors[i]=Color.getHSBColor((float)(1.0*i/N),1.0f,1.0f);
        }
        sines[N] = 0;
        cosines[N] = 1;
        */

        // -----------------------
        expected_N = N; // you must have this.
    }

    /**
     * Draw a representation of the array on the screen.
     * Note: you'll want this to be very fast, so if you are using time-intensive functions, like generating 10,000
     *    colors or calling sine and cosine for 10,000 angles, it might make sense to pre-calculate them in
     *    "prepForArrayWithSizeN" (above), store them in arrays, and then access items in those arrays here.
     * Note#2: (This note will only make sense once you start writing the sorts...) Because we are trying to go as fast
     *    as possible and this isn't the part that we really want to affect our timing, we are using "array" and not the
     *    DelayedArray, so this won't be affected by the delay selected in the slider.
     * @param array - an array of N integers, from 0 -> (n-1), inclusive.
     */
    public void visualizeData(Integer[] array)
    {
        getCanvas();
        if (myCanvas == null)
            return;

        int N = array.length;
        waitForNtoMatchExpectedN(N);
        int w = getWidth();
        int h = getHeight();

        synchronized (myCanvas)
        {
            Graphics g = myCanvas.getGraphics();
            g.setColor(getBackground());
            g.fillRect(0, 0, w, h); // clear the canvas.

            // -------------------
            //TODO: Enter your code here!
            //Note: the width of the bars should depend on the width of the canvas (w) and the number of bars (N).
            //      (However, the width of the bars cannot be less than one - if it would be, just draw lines, even if
            //      they overlap.)
            //      The height of each bar should depend on the actual value of array[i], as well as
            //      the height of the canvas (h) and the maximum possible value of the bars (4*N).


            // ------------------
        }
        repaint();
        lastUpdate = new Date();

    }

    /**
     * It is possible that N and expected_N are out of synch because of multithreading. This method will
     * force the program to wait for as long as 5 seconds to let them match up. If it has been over
     * 5 seconds and they still don't match, it pops up an alert and quits the program.
     * @param N - the size of the array, which should match the one calcuated by prepForArrayWIthSizeN.
     */
    private void waitForNtoMatchExpectedN(int N)
    {
        int delayCountForExpectedN = 0;
        while (N != expected_N) // if we aren't quite synched up with prepForArrayWithSize(), which could happen in
                             // a multithreaded program....
        {
            System.out.println("Waiting for expected_N to update. N = "+ N +" expected_N = "+expected_N);
            try { Thread.sleep(100);} // wait for 0.1 seconds
            catch (InterruptedException iExp)
                { iExp.printStackTrace(); }

            if (delayCountForExpectedN > 50) // had to wait over five seconds to start...
            {
                JOptionPane.showMessageDialog(this, "I had to wait over 5 seconds to synch N and expected_N. " +
                        "Quitting program.");
                throw new RuntimeException("I had to wait over 5 seconds to synch N ("+N+") and expected_N ("+
                        expected_N+"). Quitting program.");
            }

            delayCountForExpectedN++;
        }
    }

    /**
     * tell this panel that it needs to make a new canvas, most likely because we have resized the window.
     */
    public void setDirtyCanvas()
    {
        dirtyCanvas = true;
    }



}
