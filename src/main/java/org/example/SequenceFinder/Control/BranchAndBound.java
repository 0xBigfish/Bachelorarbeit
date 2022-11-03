package org.example.SequenceFinder.Control;

import org.example.SequenceFinder.Control.Cost.CostFunction;
import org.example.SequenceFinder.Model.GeometricObjects.AABB;
import org.example.SequenceFinder.Model.Graph.Graph;
import org.example.SequenceFinder.Model.Graph.GraphNode;
import org.example.SequenceFinder.OperatingDirection;

import java.util.*;

import static org.example.SequenceFinder.OperatingDirection.TOP;

/**
 * Controller to find the optimum sequence to build the stack of boxes, using a Branch And Bound algorithm. <br>
 * <br>
 * First, the tree of all possible sequences is created based on the given {@link Graph}. The BnB assigns cost
 * on-the-fly, based on the cost function, to each traversed edge in the tree. The path with the lowest cost is the
 * optimum.<br>
 * Second, the tree is traversed down to a single leaf using depth-first. Hereby the BnB gets a reference value to
 * compare against. <br>
 * Third, the tree is traversed using breadth-first. Each path which costs get equal or higher than the reference cost
 * are pruned, because this path can only get worse in terms of cost. <br>
 * Fourth, the paths, that were traversed down to a leaf, are compared based on their cost. The path with the lowest
 * cost is the global optimum.
 *
 * @param <T> the type of objects that are stored in the graph
 */
public class BranchAndBound<T extends AABB> {

    private final Map<OperatingDirection, Graph<T>> graphsMap;
    private final CostAssigner<T> costAssigner;

    private final int numberOfObjectsInStack;

    /**
     * The current lower bound of the tree. Gets updated everytime a full sequence is calculated.
     */
    private double lowerTreeBound;

    /**
     * The (currently) best sequence
     */
    private List<T> bestSolution;

    /**
     * The active nodes. These nodes can potentially lead to a better sequence than the currently best.
     */
    private PriorityQueue<TreeNode> activeNodes;

    /**
     * Branch And Bound algorithm to find the global optimum sequence. <br>
     * <br>
     * First, the tree of all possible sequences (decision tree) is created based on the given {@link Graph}. The BnB
     * creates the tree on-the-fly and assigns cost to each traversed edge in the tree, based on the cost functions. The
     * path from root to a leaf with the lowest cost is an optimum sequence.
     * <p>
     * Second, the tree is traversed down to a single leaf using a greedy depth-first approach. Hereby the BnB gets a
     * first solution and thereby a reference value to compare against future results.
     * <p>
     * Third, the tree is traversed using breadth-first. Each path which costs get equal or higher than the reference
     * cost are pruned (bound), because this path can only get worse in terms of cost.
     * <p>
     * Fourth, the paths, that were traversed down to a leaf, are compared based on their cost. The path with the lowest
     * cost is the global optimum.
     *
     * @param graphsMap the map of graphs, one graph for each operating direction that is allowed. The graphs define the
     *                  order in which the boxes can be removed
     */
    public BranchAndBound(Map<OperatingDirection, Graph<T>> graphsMap, Collection<CostFunction<T>> costFunctions) {
        this.graphsMap = graphsMap;
        this.costAssigner = new CostAssigner<>(costFunctions);
        this.bestSolution = new ArrayList<>();

        // each object in the stack is represented by a node in the graph
        this.numberOfObjectsInStack = graphsMap.get(TOP).getCopyOfNodes().size();
    }

    /**
     * Calculate the global optimum sequence to build the stack of boxes.
     *
     * @return the optimum sequence to build the stack of boxes
     */
    public List<T> findGlobalOptimumSequence() {
        // initialize branch and bound
        lowerTreeBound = Double.POSITIVE_INFINITY;
        TreeNode root = new TreeNode(0, new ArrayList<>(), graphsMap);
        TreeNode currentNode = root;
        // sort nodes based on their lower bound (the lowest value is the head of the queue)
        activeNodes = new PriorityQueue<>(Comparator.comparingDouble((TreeNode treeNode) -> treeNode.lowerBound));
        activeNodes.add(root);

        // while active nodes exist, a potentially better solution exists
        while (!activeNodes.isEmpty()) {

            // branch the current node
            Collection<TreeNode> childNodes;
            childNodes = branch(currentNode);

            // bound the new nodes
            for (TreeNode childNode : childNodes) {
                bound(childNode);
            }

            // choose the next node for the next iteration
            currentNode = chooseNextNode(childNodes);
        }

        return bestSolution;
    }

    /**
     * Branch the given {@linkplain TreeNode} based on the branching rules.
     * <p>
     * Rule: Each {@linkplain TreeNode} gets split up based on the removable objects in the node's graphs. For each
     * removable object <i>Q</i> a new node is created. The newly created node represent the decision to remove object
     * <i>Q</i> after the current object.
     * <p>
     * Example: Let the current node have the sequence {@code A -> B -> C} and removable objects D and E. The current
     * node gets split into two nodes containing the sequences {@code A -> B -> C -> D} and {@code A -> B -> C -> E}
     * respectively.
     *
     * @param currentNode the current {@linkplain TreeNode}
     * @return the created child nodes of the given node
     */
    private Set<TreeNode> branch(TreeNode currentNode) {
        Set<TreeNode> generatedChildren = new HashSet<>();

        // each removable object may not have an object on top of it. The objects are represented by the nodes
        Collection<GraphNode<T>> noObjectsOnTop = currentNode.graphs.get(TOP).getCopyOfRemovableNodes();

        // for each edge set E_Z (here, there are multiple graphs, each with a single edge set)
        for (Graph<T> graph : currentNode.graphs.values()) {
            if (graph.equals(graphsMap.get(TOP))) {
                // FIXME: need separate set for the top edge set. It must be part of every graph, but if it is only
                //  part of the graphsMap, one can not decide whether TOP was given OperatingDirection
                continue;
            }

            // create a new node for each removable object in the stack
            for (GraphNode<T> removableNode : graph.getCopyOfRemovableNodes()) {

                // object has no object on top and is therefore truly removable. Generate the node that represents
                // the removal of the object
                if (noObjectsOnTop.contains(removableNode)) {

                    // add the removed object to the dismantling sequence
                    List<T> sequence = currentNode.sequence;
                    sequence.add(removableNode.getContent());

                    // update graphs
                    Map<OperatingDirection, Graph<T>> graphsMap = new HashMap<>();
                    for (OperatingDirection opDir : currentNode.graphs.keySet()) {
                        Graph<T> graphFromParent = currentNode.graphs.get(opDir);
                        Graph<T> adjustedGraph = new Graph<>(graphFromParent);
                        adjustedGraph.removeNode(removableNode);

                        graphsMap.put(opDir, adjustedGraph);
                    }

                    TreeNode childNode = new TreeNode(currentNode.depthInTree + 1, sequence, graphsMap);
                    generatedChildren.add(childNode);
                }
            }
        }

        return generatedChildren;
    }


    /**
     * Bound the given {@linkplain TreeNode} if the node's subtree can not yield a better solution than the currently
     * best.
     * <p>
     * If the node's lower bound is >= the current upper bound of the tree, the node will be terminated. The best
     * possible sequence of the node's subtree will not be better than the currently best sequence. Therefore, the node
     * needs not be further examined and can be terminated.
     *
     * @param nodeToBeChecked the node that will be checked and possible terminated
     */
    private void bound(TreeNode nodeToBeChecked) {
        List<T> nodeSequence = nodeToBeChecked.sequence;

        // check if the node's subtree can potentially yield a better sequence than the currently best
        if (nodeToBeChecked.lowerBound < lowerTreeBound) {

            // if the node has a complete sequence it is a valid dismantling sequence of the stack.
            if (nodeSequence.size() == numberOfObjectsInStack) {

                // the node describes a better sequence than the currently best, otherwise it would have been terminated
                lowerTreeBound = fitness(nodeSequence);
                nodeToBeChecked.lowerBound = fitness(nodeSequence);
                bestSolution = nodeSequence;

                // terminate all other nodes that have a worse lower bound than the new best solution. They can not
                // yield a better sequence than the newly calculated one.
                activeNodes.removeIf(activeNode -> activeNode.lowerBound >= lowerTreeBound);

                // Also terminate the current node as it describes a complete sequence and needs no further processing.
                activeNodes.remove(nodeToBeChecked);

            } else {
                // make the node active for future inspections
                activeNodes.add(nodeToBeChecked);
            }

        } else {
            // terminate the node
            activeNodes.remove(nodeToBeChecked);
        }
    }


    /**
     * Choose the node for the next iteration.
     * <p>
     * When no solution has been found yet, use a greedy approach. Hereby the newly generated child node with the lowest
     * lower bound will be chosen. This approach is a modified <i>LIFO</i> method to find an initial solution and
     * therefore be able to {@linkplain BranchAndBound#bound} nodes and prune the tree.
     * <p>
     * When a solution has already been found, the active node with the lowest lower bound will be chosen. The idea
     * being, that this node potentially leads to a better solution.
     *
     * @param childNodes the child nodes generated in the current iteration
     * @return the node that will be branched in the next iteration
     */
    private TreeNode chooseNextNode(Collection<TreeNode> childNodes) {
        TreeNode currentNode;

        // check if a solution has already been found
        if (bestSolution != null) {

            // choose the active node with the lowest lower bound. The idea being that this node potentially
            // results in the sequence with the lowest (=best) fitness-value
            currentNode = activeNodes.poll();
        } else {

            // choose the child nodes with the lowest lower bound -> greedy approach
            double lowestLowerBound = childNodes.stream()
                    .mapToDouble(childNode -> childNode.lowerBound)
                    .min()
                    .orElseThrow();
            currentNode = childNodes.stream()
                    .filter(childNode -> childNode.lowerBound == lowestLowerBound)
                    .findFirst()
                    .orElseThrow();
        }
        return currentNode;
    }

    /**
     * Calculates the lower bound for a given (sub-)sequence
     *
     * @param sequence the given sequence
     * @return the lower bound of the sequence
     */
    private double lowerBound(List<T> sequence) {
        double sum = 0;

        // sum up all current costs
        int i;
        for (i = 0; i < sequence.size() - 1; i++) {
            sum += costAssigner.calcCost(sequence.get(i), sequence.get(i + 1));
        }

        // sum up all lower bounds of future costs
        for (int j = i; j < numberOfObjectsInStack - 1; j++) {
            sum += costAssigner.lowerBound();
        }

        return sum;
    }

    /**
     * Calculate the fitness of the given sequence. May not be a subsequence.
     * <p>
     * The fitness-value is a value to evaluate and compare sequences. A lower value is better.
     *
     * @param sequence a complete sequence to dismantle the stack
     * @return the fitness value of the sequence
     */
    private double fitness(List<T> sequence) {
        if (sequence.size() != numberOfObjectsInStack) {
            throw new IllegalArgumentException("Fitness value is only defined for complete sequences, not for " +
                    "subsequences! \n" +
                    "sequence size: + " + sequence.size() + ", stack size: " + numberOfObjectsInStack);
        }

        double sum = 0;
        for (int i = 0; i < numberOfObjectsInStack - 1; i++) {
            sum += costAssigner.calcCost(sequence.get(i), sequence.get(i + 1));
        }

        return sum;
    }

    /**
     * A node in the decision tree.
     * <p>
     * Each node represents the model of the stack, where all objects of the node's internal sequence have been removed
     * from the stack. When the node's depth is equal to the number of objects in the stack, the node describes a
     * complete sequence.
     */
    private class TreeNode {

        int depthInTree;
        double lowerBound;
        List<T> sequence;
        Map<OperatingDirection, Graph<T>> graphs;

        /**
         * Create a new TreeNode
         */
        TreeNode(int depthInTree, List<T> sequence, Map<OperatingDirection, Graph<T>> graphs) {
            this.depthInTree = depthInTree;
            this.sequence = sequence;
            this.graphs = graphs;
            this.lowerBound = lowerBound(this.sequence);
        }
    }
}
