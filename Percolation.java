import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    /*
    https://www.coursera.org/learn/algorithms-part1/discussions/all/threads/_6EKfcc8Eemg6RK_7kEwrg
    * An idea, how to improve memory usage (not mine).
    * Puah Hong Peng · 6 month ago
    * Instead of using virtual sites to detect if a site is full and if the structure percolates,
    * I used an n x n array (let's call it Component Map) to keep track of the state of each component.
    * The states are BLOCK, OPEN, EMPTY and FULL.
    * An EMPTY component refers to a component that connects to the bottom
    * (this term is made up to ease the description that follows).
    * The position of the Component Map corresponds to the ID of the component.
    * Initially all components are BLOCK, and each site has a unique component ID. When two sites are joined,
    * they will share the same component ID (so are all sites connected to them).
    *
    * When a site is opened, its state will be FULL if it is in the first row or EMPTY if it is in the last row.
    * Otherwise, its state is OPEN. When it is joined with a site that is FULL or EMPTY,
    * the state of the joined component will also be FULL or EMPTY, respectively.
    * When an EMPTY component joins a FULL component, the structure percolates.
    *
    * Each site in the Union Find object takes up 8 bytes.
    * Only one byte is needed to store the state.
    * So, the method described above only takes 9 n^2 bytes.
    *
    * Hope that helps.
    */

    private final int side;
    private final WeightedQuickUnionUF percolationConnectGrid;
    private final WeightedQuickUnionUF connectGrid;
    private final boolean[][] stateGrid;
    private int numOfOpenSites = 0;
    private final int upVNode = 0;
    private final int botVNode;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("The argument is <= 0");
        }
        side = n;
        stateGrid = new boolean[n][n];

        for (int i = 0; i < side; ++i) {
            for (int j = 0; j < side; ++j) {
                stateGrid[i][j] = false;
            }
        }
        connectGrid = new WeightedQuickUnionUF(side * side + 1);
        percolationConnectGrid = new WeightedQuickUnionUF(side * side + 2);
        botVNode = side * side + 1;
    }


    private void indexCheck(int row, int col) {
        if (row <= 0 || row > side) {
            throw new IllegalArgumentException("The row index is out of range");
        }
        if (col <= 0 || col > side) {
            throw new IllegalArgumentException("The col index is out of range");
        }
    }

    // get index to connect 2D [n][n]array with 1D [n*n]array
    private int getIndex(int row, int col) {
        return side * (row - 1) + col;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        indexCheck(row, col);

        if (stateGrid[row - 1][col - 1]) {
            return;
        }

        stateGrid[row - 1][col - 1] = true;
        numOfOpenSites += 1;

        if (side > 1) {
            if (row - 1 > 0) {
                if (row - 1 < side - 1) {
                    if (col - 1 > 0) {
                        if (col - 1 < side - 1) {
                            // inside
                            if (isOpen(row + 1, col)) {
                                percolationConnectGrid.union(getIndex(row, col), getIndex(row + 1, col));
                                connectGrid.union(getIndex(row, col), getIndex(row + 1, col));
                            }
                            if (isOpen(row - 1, col)) {
                                percolationConnectGrid.union(getIndex(row, col), getIndex(row - 1, col));
                                connectGrid.union(getIndex(row, col), getIndex(row - 1, col));
                            }
                            if (isOpen(row, col - 1)) {
                                percolationConnectGrid.union(getIndex(row, col), getIndex(row, col - 1));
                                connectGrid.union(getIndex(row, col), getIndex(row, col - 1));
                            }
                            if (isOpen(row, col + 1)) {
                                percolationConnectGrid.union(getIndex(row, col), getIndex(row, col + 1));
                                connectGrid.union(getIndex(row, col), getIndex(row, col + 1));
                            }
                        } // [1..n-2][n-1]
                        else {
                            if (isOpen(row + 1, col)) {
                                percolationConnectGrid.union(getIndex(row, col), getIndex(row + 1, col));
                                connectGrid.union(getIndex(row, col), getIndex(row + 1, col));
                            }
                            if (isOpen(row - 1, col)) {
                                percolationConnectGrid.union(getIndex(row, col), getIndex(row - 1, col));
                                connectGrid.union(getIndex(row, col), getIndex(row - 1, col));
                            }
                            if (isOpen(row, col - 1)) {
                                percolationConnectGrid.union(getIndex(row, col), getIndex(row, col - 1));
                                connectGrid.union(getIndex(row, col), getIndex(row, col - 1));
                            }
                        }
                    }//[1..n-2][0]
                    else {
                        if (isOpen(row + 1, col)) {
                            percolationConnectGrid.union(getIndex(row, col), getIndex(row + 1, col));
                            connectGrid.union(getIndex(row, col), getIndex(row + 1, col));
                        }
                        if (isOpen(row - 1, col)) {
                            percolationConnectGrid.union(getIndex(row, col), getIndex(row - 1, col));
                            connectGrid.union(getIndex(row, col), getIndex(row - 1, col));
                        }
                        if (isOpen(row, col + 1)) {
                            percolationConnectGrid.union(getIndex(row, col), getIndex(row, col + 1));
                            connectGrid.union(getIndex(row, col), getIndex(row, col + 1));
                        }
                    }
                } // row - 1 == side - 1
                else {
                    percolationConnectGrid.union(getIndex(row, col), botVNode);
                    if (col - 1 > 0) {
                        if (col - 1 < side - 1) {
                            if (isOpen(row, col - 1)) {
                                percolationConnectGrid.union(getIndex(row, col), getIndex(row, col - 1));
                                connectGrid.union(getIndex(row, col), getIndex(row, col - 1));
                            }
                            if (isOpen(row, col + 1)) {
                                percolationConnectGrid.union(getIndex(row, col), getIndex(row, col + 1));
                                connectGrid.union(getIndex(row, col), getIndex(row, col + 1));
                            }
                            if (isOpen(row - 1, col)) {
                                percolationConnectGrid.union(getIndex(row, col), getIndex(row - 1, col));
                                connectGrid.union(getIndex(row, col), getIndex(row - 1, col));
                            }
                        } // [n-1][n-1]
                        else {
                            if (isOpen(row, col - 1)) {
                                percolationConnectGrid.union(getIndex(row, col), getIndex(row, col - 1));
                                connectGrid.union(getIndex(row, col), getIndex(row, col - 1));
                            }
                            if (isOpen(row - 1, col)) {
                                percolationConnectGrid.union(getIndex(row, col), getIndex(row - 1, col));
                                connectGrid.union(getIndex(row, col), getIndex(row - 1, col));
                            }
                        }
                    } // [n-1][0]
                    else {
                        if (isOpen(row - 1, col)) {
                            percolationConnectGrid.union(getIndex(row, col), getIndex(row - 1, col));
                            connectGrid.union(getIndex(row, col), getIndex(row - 1, col));
                        }
                        if (isOpen(row, col + 1)) {
                            percolationConnectGrid.union(getIndex(row, col), getIndex(row, col + 1));
                            connectGrid.union(getIndex(row, col), getIndex(row, col + 1));
                        }
                    }
                }
            } // [0][i]
            else {
                percolationConnectGrid.union(upVNode, getIndex(row, col));
                connectGrid.union(upVNode, getIndex(row, col));
                // [0][0..n-1]
                if (col - 1 > 0) {
                    // [0][1..n-2]
                    if (col - 1 < side - 1) {
                        if (isOpen(row, col - 1)) {
                            percolationConnectGrid.union(getIndex(row, col), getIndex(row, col - 1));
                            connectGrid.union(getIndex(row, col), getIndex(row, col - 1));
                        }
                        if (isOpen(row, col + 1)) {
                            percolationConnectGrid.union(getIndex(row, col), getIndex(row, col + 1));
                            connectGrid.union(getIndex(row, col), getIndex(row, col + 1));
                        }
                        if (isOpen(row + 1, col)) {
                            percolationConnectGrid.union(getIndex(row, col), getIndex(row + 1, col));
                            connectGrid.union(getIndex(row, col), getIndex(row + 1, col));
                        }
                    } // element [0][n-1]
                    else {
                        if (isOpen(row, col - 1)) {
                            percolationConnectGrid.union(getIndex(row, col), getIndex(row, col - 1));
                            connectGrid.union(getIndex(row, col), getIndex(row, col - 1));
                        }
                        if (isOpen(row + 1, col)) {
                            percolationConnectGrid.union(getIndex(row, col), getIndex(row + 1, col));
                            connectGrid.union(getIndex(row, col), getIndex(row + 1, col));
                        }
                    }
                } // element [0][0]
                else {
                    if (isOpen(row, col + 1)) {
                        percolationConnectGrid.union(getIndex(row, col), getIndex(row, col + 1));
                        connectGrid.union(getIndex(row, col), getIndex(row, col + 1));
                    }
                    if (isOpen(row + 1, col)) {
                        percolationConnectGrid.union(getIndex(row, col), getIndex(row + 1, col));
                        connectGrid.union(getIndex(row, col), getIndex(row + 1, col));
                    }
                }
            }
        } else {
            percolationConnectGrid.union(upVNode, getIndex(1, 1));
            percolationConnectGrid.union(getIndex(1, 1), 2);
            connectGrid.union(upVNode, getIndex(1, 1));
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        indexCheck(row, col);
        return stateGrid[row - 1][col - 1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        indexCheck(row, col);
        return stateGrid[row - 1][col - 1] && connectGrid.connected(upVNode, getIndex(row, col));

    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numOfOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return percolationConnectGrid.connected(upVNode, botVNode);
    }


    public static void main(String[] args) {
        Percolation test = new Percolation(10);
        test.open(1, 1);
        System.out.println(test.isOpen(1, 1));
        System.out.println(test.numberOfOpenSites());
    }
}
