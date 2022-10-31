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

    /**
     * Branch And Bound algorithm to find the global optimum sequence. <br>
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
     * @param graphsMap the map of graphs, one graph for each operating direction that is allowed. The graphs define the
     *                  order in which the boxes can be removed
     */
    public BranchAndBound(Map<OperatingDirection, Graph<T>> graphsMap, Collection<CostFunction<T>> costFunctions) {
        this.graphsMap = graphsMap;
        this.costAssigner = new CostAssigner<>(costFunctions);
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
