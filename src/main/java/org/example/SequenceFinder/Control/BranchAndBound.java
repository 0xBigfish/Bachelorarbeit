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

    Map<OperatingDirection, Graph<T>> graphsMap;
    CostAssigner<T> costAssigner;

    int numberOfObjectsInStack;

    /**
     * The current lower bound of the tree. Gets updated everytime a full sequence is calculated.
     */
    double lowerTreeBound;

    /**
     * The active nodes. These nodes can potentially lead to a better sequence than the currently best.
     */
    Set<TreeNode> activeNodes;

    /**
     * Branch And Bound algorithm to find the global optimum sequence. <br>
     * <br>
     * First, the tree of all possible sequences is created based on the given {@link Graph}. The BnB assigns cost
     * on-the-fly, based on the cost function, to each traversed edge in the tree. The path with the lowest cost is the
     * optimum.<br> Second, the tree is traversed down to a single leaf using depth-first. Hereby the BnB gets a
     * reference value to compare against. <br> Third, the tree is traversed using breadth-first. Each path which costs
     * get equal or higher than the reference cost are pruned, because this path can only get worse in terms of cost.
     * <br> Fourth, the paths, that were traversed down to a leaf, are compared based on their cost. The path with the
     * lowest cost is the global optimum.
     *
     * @param graphsMap the map of graphs, one graph for each operating direction that is allowed. The graphs define the
     *                  order in which the boxes can be removed
     */
    public BranchAndBound(Map<OperatingDirection, Graph<T>> graphsMap, Collection<CostFunction<T>> costFunctions) {
        this.graphsMap = graphsMap;
        this.costAssigner = new CostAssigner<>(costFunctions);

        // each object in the stack is represented by a node in the graph
        this.numberOfObjectsInStack = graphsMap.get(TOP).getCopyOfNodes().size();
    }

    /**
     * Calculate the global optimum sequence to build the stack of boxes.
     *
     * @return the optimum sequence to build the stack of boxes
     */
    public LinkedHashSet<T> findGlobalOptimumSequence() {
        // to be implemented
        // return a LinkedHashSet because no duplicate entries are allowed and the sequence fixed
        return null;
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
     * Evaluate and bound the given {@linkplain TreeNode} if the node's subtree can not yield a better solution than the
     * currently best.
     * <p>
     * If the node's lower bound is >= the current upper bound of the tree, the node will be terminated. The best
     * possible sequence of the node's subtree will not be better than the currently best sequence. Therefore, the node
     * needs not be further examined and can be terminated.
     *
     * @param nodeToBeChecked the node that will be checked and possible terminated
     */
    private void bound(TreeNode nodeToBeChecked) {
        List<T> nodeSequence = nodeToBeChecked.sequence;
        nodeToBeChecked.lowerBound = lowerBound(nodeSequence);

        // check if the node's subtree can potentially yield a better sequence than the currently best
        if (nodeToBeChecked.lowerBound < lowerTreeBound) {

            // if the node has a complete sequence it is a valid dismantling sequence of the stack.
            if (nodeSequence.size() == numberOfObjectsInStack) {

                // the node describes a better sequence than the currently best, otherwise it would have been terminated
                lowerTreeBound = fitness(nodeSequence);
                nodeToBeChecked.lowerBound = fitness(nodeSequence);

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
     * The fitness-value is a value to compare sequences. A lower value is better.
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
        }
    }
}
