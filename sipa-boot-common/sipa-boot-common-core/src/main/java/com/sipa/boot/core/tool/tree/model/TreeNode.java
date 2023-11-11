package com.sipa.boot.core.tool.tree.model;

import java.util.List;

/**
 * @author caszhou
 * @date 2021/8/14
 */
public class TreeNode<T extends TreeNodeDetail> {
    private List<TreeNode<T>> treeNodes;

    private T treeNodeDetail;

    public List<TreeNode<T>> getNodes() {
        return treeNodes;
    }

    public void setNodes(List<TreeNode<T>> treeNodes) {
        this.treeNodes = treeNodes;
    }

    public T getNodeDetail() {
        return treeNodeDetail;
    }

    public void setNodeDetail(T treeNodeDetail) {
        this.treeNodeDetail = treeNodeDetail;
    }

    public static final class TreeNodeBuilder<S extends TreeNodeDetail> {
        private List<TreeNode<S>> treeNodes;

        private S treeNodeDetail;

        private TreeNodeBuilder() {}

        public static <T extends TreeNodeDetail> TreeNodeBuilder<T> aTreeNode() {
            return new TreeNodeBuilder<>();
        }

        public TreeNodeBuilder<S> withTreeNodes(List<TreeNode<S>> treeNodes) {
            this.treeNodes = treeNodes;
            return this;
        }

        public TreeNodeBuilder<S> withTreeNodeDetail(S treeNodeDetail) {
            this.treeNodeDetail = treeNodeDetail;
            return this;
        }

        public TreeNode<S> build() {
            TreeNode<S> treeNode = new TreeNode<>();
            treeNode.treeNodes = this.treeNodes;
            treeNode.treeNodeDetail = this.treeNodeDetail;
            return treeNode;
        }
    }
}
