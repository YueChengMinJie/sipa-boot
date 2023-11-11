package com.sipa.boot.core.exception.manager;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author caszhou
 * @date 2023/4/24
 */
@Getter
@ToString
@EqualsAndHashCode(of = "code")
public class TreeNode {
    String code;

    String name;

    List<TreeNode> nodes;

    public TreeNode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public void setNodes(List<TreeNode> nodes) {
        this.nodes = nodes;
    }
}
