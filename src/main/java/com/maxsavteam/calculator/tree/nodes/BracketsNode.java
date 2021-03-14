package com.maxsavteam.calculator.tree.nodes;

public class BracketsNode extends TreeNode {

    private final int type;

    public BracketsNode(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}