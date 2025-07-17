import java.util.*;

public class Enumeration {

    /*
     * As discussed for part 2A over here.
     */
    public static List<AssignmentResult> generateAllAssignments(int[][] costMatrix) {
        List<Integer> initialAssignment = new ArrayList<>();
        int numTasks = costMatrix.length;
        for (int col = 0; col < numTasks; col++) {
            initialAssignment.add(col);
        }

        List<AssignmentResult> allResults = new ArrayList<>();
        generatePermutations(initialAssignment, 0, costMatrix, allResults);

        allResults.sort(Comparator.comparingDouble(a -> a.totalCost));
        return allResults;
    }

    private static void generatePermutations(List<Integer> currentAssignment, int rowIndex,
                                             int[][] costMatrix, List<AssignmentResult> results) {
        int numTasks = costMatrix.length;
        if (rowIndex == numTasks) {
            double totalCost = 0;
            for (int row = 0; row < numTasks; row++) {
                int col = currentAssignment.get(row);
                totalCost += costMatrix[row][col];
            }
            results.add(new AssignmentResult(new ArrayList<>(currentAssignment), totalCost));
        } else {
            for (int i = rowIndex; i < numTasks; i++) {
                Collections.swap(currentAssignment, rowIndex, i);
                generatePermutations(currentAssignment, rowIndex + 1, costMatrix, results);
                Collections.swap(currentAssignment, rowIndex, i);
            }
        }
    }

    
    public static boolean isValid(List<Integer> assignments, List<int[]> exclusions, List<int[]> inclusions) {
        for (int i = 0; i < assignments.size(); i++) {
            for (int[] val : exclusions) {
                if (val[0] == i && val[1] == assignments.get(i)) {
                    return false;
                }
            }
            
            for(int[] val : inclusions) {
                if(assignments.get(val[0]) != val[1]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static AssignmentResult getBestValidAssignment(List<AssignmentResult> results, List<int[]> exclusions, List<int[]> inclusions) {
        for (AssignmentResult result : results) {
            if (isValid(result.assignments, exclusions, inclusions)) {
                return result;
            }
        }
        return null;
    }


    /*
     * part 2B begins here.
     */
    /**
     * 
     * @param matrix
     * @param exclusions: A list of the pairs of exclusions to be excluded out from the matrix.
     * @return the cost matrix after the excluded pairs have been changed to maximums.
     * 
     */
    public static int[][] finalCostMatrixAfterExclusionsAndInclusions(int[][] matrix, List<int[]> exclusions, List<int[]> inclusions, boolean includeOrNot) {
        int replacingValue = totalSum(matrix) + 1;
        int[][] workingMatrix = deepCopy(matrix);
        if (includeOrNot) {
        for (int[] pair : inclusions) {
            workingMatrix[pair[0]][pair[1]] = 0;
            for (int i = 0; i < workingMatrix.length; i++) {
                if (i != pair[1]) {
                    workingMatrix[pair[0]][i] = replacingValue + 1;
                }
            }
            for (int j = 0; j < workingMatrix.length; j++) {
                if (j != pair[0]) {
                    workingMatrix[j][pair[1]] = replacingValue + 1;
                }
            }
        }
        }

        for (int[] pair : exclusions) {
            workingMatrix[pair[0]][pair[1]] = replacingValue;
        }
        
        return workingMatrix;
    
    }

    private static int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++)
            copy[i] = original[i].clone();
        return copy;
    }

    /**
     * 
     * @param results
     * Method to print out the results.
     */
    public static void printResults(List<AssignmentResult> results) {
        int count = 0 ;
        for (AssignmentResult result : results) {
            count++;
            System.out.println(result.assignments+ " and the total cost of it is: "+ result.totalCost);
        }
        System.out.println("The total amount of results: "+count);
    }

    // public static int[][] toIntMatrix(double[][] matrix) {
    //     int[][] intMatrix = new int[matrix.length][matrix.length];
    //     for(int i = 0; i < matrix.length; i++) {
    //         for(int j = 0 ; j<matrix.length; j++) {
    //             intMatrix[i][j] = (int) Math.round(matrix[i][j]);
    //         }
    //     }
    //     return intMatrix;
    // }

    public static int totalSum(int[][] matrix) {
        int totalSum = 0;
        for(int i = 0 ; i < matrix.length ; i++) {
            for(int j = 0 ; j < matrix.length ; j++) {
                totalSum += matrix[i][j];
            }
        }
        return totalSum;
    }

    public static List<int[]> exclusionsListFromHungarian(int[] assignments) {
        List<int[]> returningList = new ArrayList<>();
        for(int i= 0; i <assignments.length;i++) {
            returningList.add(new int[]{i, assignments[i]});
        }
        return returningList;
    } 

    /*
     * Method to get the top k paths.
     */
    public static List<AssignmentResult> getTopKMurtys(int[][] costMatrix, int k) {
        List<AssignmentResult> results = new ArrayList<>();
        PriorityQueue<MurtyNode> pq = new PriorityQueue<>();
        Set<List<Integer>> seen = new HashSet<>();

        int[] baseAssign = hungarianAlgo.solveHungarian(costMatrix);
        List<Integer> baseList = toList(baseAssign);
        double baseCost = calculateCost(costMatrix, baseAssign);
        AssignmentResult baseResult = new AssignmentResult(baseList, baseCost);
        pq.offer(new MurtyNode(baseResult, new ArrayList<>(), new ArrayList<>()));
        seen.add(baseList);

        while (!pq.isEmpty() && results.size() < k) {
            MurtyNode current = pq.poll();
            results.add(current.result);

            for (int i = 0; i < current.result.assignments.size(); i++) {
                int col = current.result.assignments.get(i);

                boolean alreadyExcluded = false;
                for (int[] ex : current.exclusions) {
                    if (ex[0] == i && ex[1] == col) {
                        alreadyExcluded = true;
                        break;
                    }
                }
                if (alreadyExcluded) continue;

                List<int[]> newExclusions = new ArrayList<>(current.exclusions);
                newExclusions.add(new int[] { i, col });

                List<int[]> newInclusions = new ArrayList<>(current.inclusions);
                for (int j = 0; j < i; j++) {
                    newInclusions.add(new int[] { j, current.result.assignments.get(j) });
                }

                int[][] modifiedMatrix = finalCostMatrixAfterExclusionsAndInclusions(costMatrix, newExclusions, newInclusions, true);

                // Solve subproblem
                int[] newAssign = hungarianAlgo.solveHungarian(modifiedMatrix);
                if (newAssign == null) continue;

                List<Integer> newList = toList(newAssign);
                if (seen.contains(newList)) continue;

                double newCost = calculateCost(costMatrix, newAssign);
                AssignmentResult newResult = new AssignmentResult(newList, newCost);
                pq.offer(new MurtyNode(newResult, newExclusions, newInclusions));
                seen.add(newList);
            }
        }
        return results;
    }
    public static void main(String[] args) {
        int n = 8;
        int[][] matrix = generateMatrix(n, 1000, 9999);

        System.out.println("Matrix Size: " + n + "x" + n);
        System.out.println("Expected Permutations (n!): " + factorial(n));

        System.out.println("Generating brute-force permutations...");
        List<AssignmentResult> brute = generateAllAssignments(matrix);
        Set<List<Integer>> bruteSet = new HashSet<>();
        for (AssignmentResult r : brute) bruteSet.add(r.assignments);
        System.out.println("Brute-force complete. Unique assignments: " + bruteSet.size());

        System.out.println("Running Murty’s algorithm...");
        int k = (int) (factorial(n) + 5000);
        List<AssignmentResult> murty = getTopKMurtys(matrix, k);
        Set<List<Integer>> murtySet = new HashSet<>();
        for (AssignmentResult r : murty) murtySet.add(r.assignments);
        System.out.println("Murty complete. Unique assignments: " + murtySet.size());

        if (bruteSet.equals(murtySet)) {
            System.out.println("Murty matches brute-force. All permutations captured.");
        } else {
            System.out.println("Mismatch found between Murty and brute-force.");

            System.out.println("Missing from Murty:");
            for (List<Integer> perm : bruteSet) {
                if (!murtySet.contains(perm)) {
                    System.out.println("  " + perm);
                }
            }

            System.out.println("Extra in Murty (not in brute-force):");
            for (List<Integer> perm : murtySet) {
                if (!bruteSet.contains(perm)) {
                    System.out.println("  " + perm);
                }
            }
        }
        
    }
    
    //Helper methods below, to print, and other stuffs.

    public static long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }


    public static int[][] generateMatrix(int size, int min, int max) {
        Random rand = new Random();
        int[][] returnMatrix = new int[size][size];
        for(int i = 0 ; i < size; i++) {
            for(int j = 0 ; j < size; j++) {
                returnMatrix[i][j] = rand.nextInt(max - min + 1) + min;
            }
        }
        return returnMatrix;
    }

    public static void printMatrix(int[][] matrix) {
        for(int i = 0 ; i < matrix.length;i++) {
            System.out.print("[");
            for(int j = 0 ; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ") ;
            }
            System.out.print("\b]");
            System.out.println("");
        }
    }

    public static List<Integer> toList(int[] array) {
        List<Integer> list = new ArrayList<>();
        for (int num : array) {
            list.add(num);
        }
        return list;
    }

    public static double calculateCost(int[][] matrix, int[] assignment) {
        double cost = 0;
        for(int i=0;i < assignment.length; i++) {
            cost += matrix[i][assignment[i]];
        }
        return cost;
    }

    public static String toStringList(List<int[]> list) {
    StringBuilder sb = new StringBuilder();
    for (int[] arr : list) {
        sb.append("(").append(arr[0]).append(",").append(arr[1]).append(") ");
    }
    return sb.toString().trim();
}
}


