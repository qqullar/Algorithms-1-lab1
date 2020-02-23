import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private Percolation Grid;
    final private int gridSize;
    final private int trialNum;
    private double[] thresholds;


    private double getThreshold(Percolation grid) {
        int randRow;
        int randCol;

        while (!grid.percolates()) {
            randRow = StdRandom.uniform(gridSize);
            randCol = StdRandom.uniform(gridSize);
            ++randRow;
            ++randCol;
            if (!grid.isOpen(randRow, randCol))
                grid.open(randRow, randCol);
        }

        return (double) grid.numberOfOpenSites() / (gridSize * gridSize);
    }


    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("Negative numbers in arguments");
        }
        gridSize = n;
        trialNum = trials;

        thresholds = new double[trials];
        for (int i = 0; i < trials; ++i) {
            Percolation iGrid = new Percolation(n);
            thresholds[i] = getThreshold(iGrid);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(thresholds);
    }


    //
    // sample standard deviation of percolation threshold
    public double stddev() {

        if (trialNum == 1) {
            return Double.NaN;
        }
        return StdStats.stddev(thresholds);
    }

    //
//    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - (1.96 * stddev()) /
                Math.sqrt(trialNum);
    }

    //    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + (1.96 * stddev()) /
                Math.sqrt(trialNum);
    }


    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        PercolationStats test = new PercolationStats(n, trials);
        System.out.print("mean                        = ");
        System.out.print(test.mean());
        System.out.println("");

        System.out.print("stddev                      = ");
        System.out.print(test.stddev());
        System.out.println("");

        System.out.print("95% confidence interval     = [");
        System.out.print(test.confidenceLo());
        System.out.print(", ");
        System.out.print(test.confidenceHi());
        System.out.println("]");
    }
}