import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class ThreeSum{

    // opting to use one project so I do not keep three instances of IntelliJ open if changes are necessary

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
    /* define constants */
    static long MAXVALUE =  2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 10;
    static int MAXINPUTSIZE  = (int) Math.pow(2,13); // 24 is largest due to heap
    static int MININPUTSIZE  =  1;
    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time

    static String ResultsFolderPath = "/home/ryan/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {

        long[] newList = {30, -10, 111, 7, 190, 3, 75, 40, -23, -17, -100, 70, 85, 19, 4000};

        long[] newList2 = { 41, 42, -83, 90, 1000, -750, -250, -1999, -8909, 100, 170, 1, 7, 12, -20};

        // testing the validity of the three sum functions
        long counted1 = verifyThreeSumBruteForceCount(newList);
        System.out.println("Count for ThreeSumBruteForce1 " + counted1);
        long counted1a = verifyThreeSumBruteForceCount(newList2);
        System.out.println("Count for ThreeSumBruteForce2 is " + counted1a);
        long counted2 = verifyThreeSumFastCount(newList);
        System.out.println("Count for ThreeSumFast1 is " + counted2);
        long counted2a = verifyThreeSumFastCount(newList2);
        System.out.println("Count for ThreeSumFast2 is " + counted2a);
        long counted3 = verifyThreeSumFasterCount(newList);
        System.out.println("Count for ThreeSumFaster1 is " + counted3);
        long counted3a = verifyThreeSumFasterCount(newList2);
        System.out.println("Count for ThreeSumFaster2 is " + counted3a);

        // testing to make sure the lists have both negative and positive values
        long [] list1 = createRandomListOfIntegers(10);
        printList(list1, list1.length);
        long [] list2 = createRandomListOfIntegers(20);
        printList(list2, list2.length);


        // running three tests so java can *hopefully* optimize and provide nice data by round 2 or 3
        /* **********************************************UNCOMMENT ONE******************************************/
/*
        System.out.println("Running first full experiment ...");
        runFullExperiment("ThreeSumBruteForce-Exp1-ThrowAway.txt");
        System.out.println("Running second full experiment ...");
        runFullExperiment("ThreeSumBruteForce-Exp2.txt");
        System.out.println("Running third full experiment ...");
        runFullExperiment("ThreeSumBruteForce-Exp3.txt");
*/

/*
        System.out.println("Running first full experiment ...");
        runFullExperiment("ThreeSumFast-Exp1-ThrowAway.txt");
        System.out.println("Running second full experiment ...");
        runFullExperiment("ThreeSumFast-Exp2.txt");
        System.out.println("Running third full experiment ...");
        runFullExperiment("ThreeSumFast-Exp3.txt");
*/

        System.out.println("Running first full experiment ...");
        runFullExperiment("ThreeSumFaster-Exp1-ThrowAway.txt");
        System.out.println("Running second full experiment ...");
        runFullExperiment("ThreeSumFaster-Exp2.txt");
        System.out.println("Running third full experiment ...");
        runFullExperiment("ThreeSumFaster-Exp3.txt");

    }

    static void runFullExperiment(String resultsFileName){

        // making sure that we have results files available or can create new
        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return;
        }

        //ThreadCPUStopWatch BatchStopwatch = new ThreadCPUStopWatch(); // for timing an entire set of trials
        ThreadCPUStopWatch TrialStopwatch = new ThreadCPUStopWatch(); // for timing an individual trial

        resultsWriter.println("#InputSize    AverageTime"); // # marks a comment in gnuplot data
        resultsWriter.flush();

        /* for each size of input we want to test: in this case starting small and doubling the size each time */

        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*=2) {

            System.out.println("Running test for input size "+inputSize+" ... ");
            /* repeat for desired number of trials (for a specific size of input)... */
            // will hold total amount of time
            // will reset after each batch of trials
            long batchElapsedTime = 0;

            /* force garbage collection before each batch of trials run so it is not included in the time */
            System.gc();

            //BatchStopwatch.start(); // comment this line if timing trials individually

            // run the trials
            for (int trial = 0; trial < numberOfTrials; trial++) {
                // variable to store number of triplets
                int counted = 0;

                System.out.print("    Generating test data...");
                // creating our testlist
                long[] testList = createRandomListOfIntegers(inputSize);
                // some status messages
                System.out.print("...done.");
                System.out.print("    Running trial batch...");
                /* force garbage collection before each trial run so it is not included in the time */
                System.gc();
                //actual beginning of trial
                TrialStopwatch.start(); // *** uncomment this line if timing trials individually
                /* run the function we're testing on the trial input */
                /* **********************************************UNCOMMENT ONE******************************************/
                //counted = ThreeSumBruteForce(testList);
                //counted = ThreeSumFast(testList);
                counted = ThreeSumFaster(testList);
                /* **********************************************UNCOMMENT ONE^^^******************************************/
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // end of trial
                // time is added to
                System.out.print("....done.");// *** uncomment this line if timing trials individually
                System.out.println("  Count = " + counted);
            }

            //batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually

            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch
            /* print data for this size of input */
            resultsWriter.printf("%12d  %15.2f \n",inputSize, (double)averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    // counts sets of three that sum to 0
    public static int ThreeSumBruteForce(long[] a)
    {
        int n = a.length;
        int count = 0;
        for (int i = 0; i < n; i++)
            for (int j = i+1; j < n; j++)
                for (int k = j+1; k < n; k++)
                    if (a[i] + a[j] + a[k] == 0)
                        count++;
        return count;
    }


    // n^2 log(n) time
    // from https://gist.github.com/st0le/5893435
    public static int ThreeSumFast(long[] a)
    {
        int count = 0;  // count variable
        Arrays.sort(a); // quick sort so we can binary search later
        for ( int i = 0, l = a.length; i < l && a[i] < 0; i++) { // starting with negative values
            for ( int j = i+1; j < l && a[i] + a[j] < 0; j++) { // adding a[j] to a[i] is still negative
                int k = Arrays.binarySearch(a, j+1, l, -a[i] - a[j]); // binary search for the desired value
                if (k > j) count++; // if k < j then the value is not in the array
            }
        }
        return count;
    }

    // n^2 time
    // from https://letstalkalgorithms.com/three-sum-3sum-leetcode/
    public static int ThreeSumFaster(long[] a)
    {
        int count = 0; // counting the triplets
        Arrays.sort(a); // performing quick sort for binary search later

        for (int i = 0; i < a.length-2; i++){
            if ( i == 0 || (i > 0 && a[i] != a[i-1])) { // making sure there are no duplicates
                int lo = i+1;                           // current index plus 1
                int hi = a.length-1;                    // last index
                long sum = (0 - a[i]);                  // summation value we are looking for
                while ( lo < hi) {                      // while there are still values for comparison
                    if ( a[lo] + a[hi] == sum) {        // if two values equal the sum then count is incremented by 1
                        count++;
                        while ( lo < hi && a[lo] == a[lo+1])
                            lo++;                       // if there is a duplicate increment lo by 1
                        while ( lo < hi && a[hi] == a[hi-1])
                            hi--;                       // if there is a duplicate hi is decreased by 1
                        lo++;                           // alternatively, increment each for next iteration of loop
                        hi--;
                    } else if (a[lo] + a[hi] < sum)     // if the sum of values at lo and hi is less than sum lo++
                        lo++;
                    else hi--;                          // if it is greater than we decrease hi by 1
                }
            }
        }
        return count; // returns the number of sets of three
    }

    // creates random unsorted list
    public static long[] createRandomListOfIntegers(int size)
    {
        Random ran = new Random();
        long[] newList = new long[size];
        for(int j=0; j<size; j++){
            newList[j] = ran.nextInt();
        }
        return newList;
    }

    // printing lists
    public static void printList(long[] list, int listSize)
    {
        int i = 0;
        for (; i < listSize; i++)
        {
            System.out.print(list[i] + " ");
        }
        System.out.println();
    }

    // making sure that ThreeSumBruteForce works by using a known list
    public static int verifyThreeSumBruteForceCount(long[] a)
    {
        int counted = 0;

        System.out.println("List:");
        // print list before it is sorted
        printList(a, a.length);
        // run the algo
        counted = ThreeSumBruteForce(a);

        // print list after it has been sorted

        return counted;
    }

    // making sure that ThreeSumFast works by using a known list
    public static int verifyThreeSumFastCount(long[] a)
    {
        int counted = 0;

        System.out.println("List:");
        // print list before it is sorted
        printList(a, a.length);
        // run the algo
        counted = ThreeSumFast(a);

        // print list after it has been sorted

        return counted;
    }

    // making sure that ThreeSumFaster works by using a known list
    public static int verifyThreeSumFasterCount(long[] a)
    {
        int counted = 0;

        System.out.println("List:");
        // print list before it is sorted
        printList(a, a.length);
        // run the algo
        counted = ThreeSumFaster(a);

        // print list after it has been sorted

        return counted;
    }
}
