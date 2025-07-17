import java.util.*;
// Class to hold the assignment and its cost
class AssignmentResult {
    List<Integer> assignments;
    double totalCost;

    public AssignmentResult(List<Integer> assignments, double totalCost) {
        this.assignments = assignments;
        this.totalCost = totalCost;
    }
}