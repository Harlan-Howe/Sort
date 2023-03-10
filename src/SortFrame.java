import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class SortFrame extends JFrame implements ActionListener, ChangeListener
{
    // CONSTANTS
    private final static int SORT_STATUS_NULL = -1;
    private final static int SORT_STATUS_SORTED = 0;
    private final static int SORT_STATUS_DUPLICATES = 1;
    private final static int SORT_STATUS_UNSORTED = 2;

    // Arrays for GUI
    private final String[] statusMessages = {"Array Sorted","Has Adjacent Duplicates","Array Unsorted"};
    private JRadioButton[] sortTypeButtons;
    private final String[] sortNames = {"Bozo","Bubble","Selection","Insertion",
                          "Merge","Quick"};
    private final int [] possibleNValues = {3,4,5,6,7,8,9,10,15,20,50,100,500,1000,5000,10000,50000};

    //GUI items
    private JComboBox<Integer> nMenu;
    private JButton runButton,resetButton;
    private JSlider delaySlider;
    private JLabel statusLabel;
    private JLabel timeLabel;
    private JLabel latestRunLabel;
    private SortPanel rightPanel;

    // core variables to run sort
    private boolean isRunning;
    private SortAlgorithm currentAlgorithm;
    private Integer[] arr; // the underlying array of ints
    private DelayedArray<Integer> currentArray; // the DelayedArray that the user can access during sorts.

    // timing variables and updates
    private Date startTime;
    private Timer timeUpdater, visualizeTimer;
    private ClockUpdater clockUpdater;
    private VisualizerUpdater visualizerUpdater;


    public SortFrame()
    {
        super("Sort");
        isRunning = false;
        initializeArray(100);
        currentAlgorithm = null;
        getContentPane().setLayout(new BorderLayout());
        setupLeftPanel(); // the list of controls
        setupRightPanel(); // the sort panel with visualizer
        setSize(800,600);
        setVisible(true);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rightPanel.prepForArrayWithSizeN(currentArray.length());
        rightPanel.visualizeData(arr);
        statusLabel.setText(statusMessages[SORT_STATUS_UNSORTED]);
        addComponentListener(new ResizeListener());

        clockUpdater = new ClockUpdater();
        visualizerUpdater = new VisualizerUpdater();
        visualizeTimer = new Timer();
        visualizeTimer.schedule(visualizerUpdater,0,16);

        repaint();
    }

    /**
     * creates the panel of controls on the left side of the window.
     * You should not need to modify this method. If you wish to change which sorts are offered or options for N,
     * see the member variables at the top of this class.
     */
    private void setupLeftPanel()
    {
        // display the radio buttons for the various sorts.
        Box leftPanel = Box.createVerticalBox();
        Box buttonColumn = Box.createVerticalBox();
        Box nameDelayBox = Box.createHorizontalBox();
        buttonColumn.setBorder(BorderFactory.createTitledBorder("Algorithms"));
        ButtonGroup bg = new ButtonGroup();
        sortTypeButtons = new JRadioButton[sortNames.length];  // to change which sorts are available, look at variables
                                                                // at the top of the class.
        for (int i=0; i<sortNames.length; i++)
        {
            sortTypeButtons[i] = new JRadioButton(sortNames[i]);
            buttonColumn.add(sortTypeButtons[i]);
            bg.add(sortTypeButtons[i]);
            sortTypeButtons[i].addActionListener(this);
        }
        sortTypeButtons[0].setSelected(true);
        nameDelayBox.add(buttonColumn);

        // display the delay slider.
        delaySlider = new JSlider(JSlider.VERTICAL,0,20,1);
        delaySlider.addChangeListener(this);
        delaySlider.setMajorTickSpacing(1);
        delaySlider.setPaintTicks(true);
        Hashtable<Integer,JLabel> labelTable = new Hashtable<>();
        labelTable.put(0,new JLabel("0"));
        labelTable.put(5,new JLabel("5"));
        labelTable.put(10,new JLabel("10"));
        labelTable.put(15,new JLabel("15"));
        labelTable.put(20,new JLabel("20"));
        delaySlider.setLabelTable(labelTable);
        delaySlider.setPaintLabels(true);
        delaySlider.setSnapToTicks(true);
        delaySlider.setBorder(new TitledBorder("Delay"));
        nameDelayBox.add(delaySlider);
        leftPanel.add(nameDelayBox);

        // display the run/cancel and reset buttons.
        Box runResetPanel = Box.createHorizontalBox();
        runButton = new JButton("Run");
        runButton.addActionListener(this);
        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        runResetPanel.add(runButton);
        runResetPanel.add(resetButton);
        leftPanel.add(runResetPanel);

        // shows popup menu of possible values of N.
        nMenu = new JComboBox<>();
        for (int v:possibleNValues) // see the variables at the top of the class to modify options shown.
            nMenu.addItem(v);
        nMenu.addActionListener(this);
        nMenu.setSelectedItem(100);
        nMenu.setBorder(new TitledBorder("Array Size (N)"));
        leftPanel.add(nMenu);

        // label that indicates whether this list is sorted correctly
        leftPanel.add(new JLabel("Status"));
        statusLabel = new JLabel("");
        leftPanel.add(statusLabel);
        leftPanel.add(Box.createVerticalStrut(10));

        // label that indicates how much time (was/has been) spent on the most recent run
        leftPanel.add(new JLabel("Time"));
        timeLabel = new JLabel("00.000");
        leftPanel.add(timeLabel);
        leftPanel.add(Box.createVerticalStrut(10));

        // label that indicates which sort was run most recently.
        leftPanel.add(new JLabel("Latest Run"));
        latestRunLabel = new JLabel("None");
        leftPanel.add(latestRunLabel);
        leftPanel.add(Box.createVerticalGlue());

        // update alignment so things look nice.
        buttonColumn.setAlignmentY(Component.TOP_ALIGNMENT);
        nameDelayBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        runResetPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        delaySlider.setAlignmentY(Component.TOP_ALIGNMENT);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        latestRunLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        getContentPane().add(leftPanel,BorderLayout.WEST);

    }

    private void setupRightPanel()
    {
        rightPanel= new SortPanel(this);
//        rightPanel.setPreferredSize(new Dimension(600,568));
        getContentPane().add(rightPanel,BorderLayout.CENTER);
    }

    /**
     * gets the number of the algorithm selected by the user, or -1 if one is not selected.
     * @return number of the selected radio button, or -1.
     */
    public int getSelectedAlgorithmNum()
    {
        for (int i=0;i<sortTypeButtons.length;i++)
            if (sortTypeButtons[i].isSelected())
                return i;
        return -1;
    }

    /**
     * returns the name of the button that is currently selected.
     * @return the name on the selected JRadioButton, or null, if none selected.
     */
    public String getSelectedAlgorithmName()
    {
        int selected = getSelectedAlgorithmNum();
        if (selected == -1)
            return null;
        return sortNames[selected];
    }
    public void handleArrayResize(int newN)
    {
        initializeArray(newN);

    }

    private void initializeArray(int N)
    {
        System.out.println("initializing array.");
        arr = new Integer[N];
        int n2 = 0;
        for (int i=0; i<N; i++)
        {
            n2+= (int)Math.sqrt(36*Math.random());
            // a bit of sneakiness here. Statistically, n2 should max out at 4N, by my calculations.
            //   however, since we're dealing with randomness, there is the possibility that we might
            //   draw a lot of high numbers and go over 4N-1 (particularly if N is small). So
            //   if that happens, we cheat and pick a random number, instead.
            if (n2<4*N)
                arr[i] = n2;
            else
                arr[i] = (int)(4*N*Math.random());
        }
        shuffleArray();
        if (delaySlider == null)
            currentArray = new DelayedArray<Integer>(arr);
        else
            currentArray = new DelayedArray<Integer>(arr,delaySlider.getValue());
        if (rightPanel != null)
        {
            rightPanel.prepForArrayWithSizeN(N);
            //rightPanel.visualizeData(arr);
        }
        if (statusLabel != null)
            statusLabel.setText(statusMessages[checkStatusOfCurrentArray()]);


    }

    /**
     * scrambles the order of the items in arr
     */
    private void shuffleArray()
    {
        int N = arr.length;
        for (int i = 0; i<2* N; i++)
        {
            int a = (int)(Math.random()* N);
            int b = (int)(Math.random()* N);
            int temp = arr[a];
            arr[a] = arr[b];
            arr[b] = temp;
        }
    }

    public void endRunGUI()
    {
        for (JRadioButton rb:sortTypeButtons)
            rb.setEnabled(true);
        nMenu.setEnabled(true);
        runButton.setText("Run");
        resetButton.setEnabled(true);
        isRunning = false;
        int statusCode = checkStatusOfCurrentArray();
        if (statusCode != -1)
            statusLabel.setText(statusMessages[statusCode]);
        if (timeUpdater!=null)
            timeUpdater.cancel();
        if (startTime != null)
            updateTimeLabel();
    }

    public void handleRunButton()
    {
        if (isRunning)
        {

            currentAlgorithm.cancel();
            endRunGUI();
        } else
        {
            startRunGUI();
            startAlgorithm();

        }
    }

    public void startRunGUI()
    {
        for (JRadioButton rb : sortTypeButtons)
            rb.setEnabled(false);
        nMenu.setEnabled(false);
        runButton.setText("Cancel");
        resetButton.setEnabled(false);
        isRunning = true;
        latestRunLabel.setText(getSelectedAlgorithmName()+" @ N= "+nMenu.getSelectedItem());
        statusLabel.setText("Sorting...");
    }

    public void startAlgorithm()
    {
      try
      {    // start the algorithm!
          Class<SortAlgorithm> theAlgClass = (Class<SortAlgorithm>) Class.forName(getSelectedAlgorithmName() + "Sort");
          Class[] parameterList = {AlgorithmDelegate.class, DelayedArray.class};
          currentAlgorithm = theAlgClass.getDeclaredConstructor(parameterList).newInstance(rightPanel,currentArray);

          currentAlgorithm.start();
          startTime = new Date();
          timeUpdater = new Timer();
          clockUpdater = new ClockUpdater();
          timeUpdater.schedule(clockUpdater,0,50);


      }catch (ClassNotFoundException cnfExp)
      {
//          System.out.println("You tried to create a "+getSelectedAlgorithmName()+"Sort object, but that class does not yet exist.");
          JOptionPane.showMessageDialog(this,"You tried to create a "+getSelectedAlgorithmName()+"Sort object, but that class does not yet exist.");
//          cnfExp.printStackTrace();
          endRunGUI();
      }
      catch (Exception exp)
      {
          exp.printStackTrace();
      }

    }

    public int checkStatusOfCurrentArray()
    {
        if (currentArray == null)
            return SORT_STATUS_NULL;
        for (int i=1; i<currentArray.length(); i++)
        {
//            if (currentArray.getValueAtLocation(i) == currentArray.getValueAtLocation(i - 1))
//                return SORT_STATUS_DUPLICATES;
            if (currentArray.getValueAtLocation(i) < currentArray.getValueAtLocation(i - 1))
                return SORT_STATUS_UNSORTED;
        }
        return SORT_STATUS_SORTED;
    }

    public void updateTimeLabel()
    {
        Date now = new Date();
        long diff = now.getTime() - startTime.getTime();
        int hours = (int) (diff/1000/3600);
        int mins  = (int) (diff/1000/60)%60;
        int secs  = (int) (diff/1000)%60;
        int ms = (int)(diff%1000);
        if (hours>0)
            timeLabel.setText(String.format("%02d:%02d:%02d.%03d",hours,mins,secs,ms));
        else if (mins>0)
            timeLabel.setText(String.format("%02d:%02d.%03d",mins,secs,ms));
        else
            timeLabel.setText(String.format("%02d.%03d",secs,ms));
    }

    /**
     * Invoked when an action occurs.
     *
     * @param actEvt the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent actEvt)
    {
        if (actEvt.getSource()==runButton)
        {
            handleRunButton();
        }
        if (actEvt.getSource() == resetButton)
        {
            System.out.println("Resetting.");
            handleArrayResize((Integer)(nMenu.getSelectedItem()));
        }
        if (actEvt.getSource()==nMenu)
        {
            handleArrayResize((Integer)(nMenu.getSelectedItem()));
        }
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    @Override
    public void stateChanged(ChangeEvent e)
    {
        if (e.getSource()==delaySlider)
        {
//            System.out.println(delaySlider.getValue());
            //rightPanel.setDelayMS(delaySlider.getValue());
            currentArray.setMs_delay(delaySlider.getValue());
        }
    }

    class ResizeListener extends ComponentAdapter
    {
        public void componentResized(ComponentEvent compEvt)
        {
            System.out.println("Resized window.");
            rightPanel.setDirtyCanvas();
            rightPanel.visualizeData(arr);
            repaint();
        }
    }

    class ClockUpdater extends TimerTask
    {
        public void run()
        {
            if (isRunning)
            {
                updateTimeLabel();
            }
        }
    }

    class VisualizerUpdater extends TimerTask
    {
        public void run()
        {
            rightPanel.visualizeData(arr);
        }
    }

}


