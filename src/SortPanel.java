import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Date;

public class SortPanel extends JPanel implements AlgorithmDelegate
{
    private BufferedImage myCanvas;
    private boolean dirtyCanvas;
    private SortFrame myFrame;
    int visual_count;
    private int delay_ms;
    private double sines[], cosines[];
    private Color colors[];
    private Date lastUpdate; // ADDED

    public SortPanel(SortFrame myFrame)
    {
        super();
        dirtyCanvas = true;
        setBackground(Color.lightGray);
        this.myFrame = myFrame;
        visual_count = 0;
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

    }

    /**
     * draw a represenatation of the array on the screen.
     * Note: you'll want this to be very fast, so if you are using time-intensive functions, like generating 10,000
     * colors or calling sine and cosine for 10,000 angles, it might make sense to pre-calculate them in "prepForArrayWithSizeN"
     * (above), store them in an array, and then access items in that array here.
     *
     * @param array - an array of N integers, from 0 -> (n-1), inclusive.
     */
    public void visualizeData(Integer[] array)
    {
        //Date now = new Date();
        boolean fancy = true;

//        if ((now.getTime() - lastUpdate.getTime())>17) // 33 ms = 1/30s
//        {
            // visual_count++;
            getCanvas();
            if (myCanvas == null)
                return;
            synchronized (myCanvas)
            {
                Graphics g = myCanvas.getGraphics();
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());

                int N = array.length;

                //TODO: Enter your code here!

                // ----------------------------------  Temporary visualizer code -- Delete this, Harlan!
                int bottomMargin = 10;
                int leftMargin = 10;
                //g.setColor(Color.BLACK);
                double height_factor = (1.0*getHeight()-bottomMargin)/(4*N);
                double width = (1.0*getWidth()-leftMargin)/N;
                for (int i=0; i<N; i++)
                {
                    g.setColor(colors[array[i]/4]);
                    if (width > 1)
                    {
                        g.fillRect((int)(leftMargin+i*width),
                                (int)(height_factor*(4*N-array[i])),
                                (int)(width+1),
                                (int)(height_factor*array[i]));
                    }
                    else
                    {
                        g.drawLine((int)(leftMargin+i*width),
                                (int)(height_factor*(4*N-array[i])),
                                (int)(leftMargin+i*width),
                                (int)(getHeight()-bottomMargin));
                    }
                }
                g.setColor(Color.black);
                g.drawLine(leftMargin,0,leftMargin,getHeight()-bottomMargin);
                g.drawLine(leftMargin,getHeight()-bottomMargin,getWidth(),getHeight()-bottomMargin);
                // ------------------------------------------------------------------------------------


            }
            repaint();
            lastUpdate = new Date();
//        }
//        try
//        {
//            Thread.sleep(delay_ms);
//        }catch (InterruptedException iExp)
//        {
//            return;
//        }
    }

    /**
     * tell this panel that it needs to make a new canvas, most likely because we have resized the window.
     */
    public void setDirtyCanvas()
    {
        dirtyCanvas = true;
    }



}
