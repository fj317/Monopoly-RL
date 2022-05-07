package Tree;

import Monopoly.State;

public class SelectedNode {
    Node selectedNode;
    State selectedNodeState;

    public SelectedNode(Node selectedNode, State selectedNodeState) {
        this.selectedNode = selectedNode;
        this.selectedNodeState = selectedNodeState;
    }

    public State getSelectedNodeState() {
        return selectedNodeState;
    }

    public Node getSelectedNode() {
        return selectedNode;
    }
}
