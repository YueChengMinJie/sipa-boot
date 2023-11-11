package com.sipa.boot.core.tool.tree.util;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.tool.tree.model.TreeNode;
import com.sipa.boot.core.tool.tree.model.TreeNodeDetail;

/**
 * @author caszhou
 * @date 2021/8/14
 */
public class TreeUtil {
    public static <T extends TreeNodeDetail> List<TreeNode<T>> getFilterTree(List<T> ol, List<T> fl) {
        Map<String, T> originMap = ol.stream().collect(Collectors.toMap(TreeNodeDetail::getItemId, e -> e));
        Map<String, T> filterMap = fl.stream().collect(Collectors.toMap(TreeNodeDetail::getItemId, e -> e));
        filterMap = supplementParent(originMap, filterMap);

        List<TreeNode<T>> tree = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ol)) {
            ol.sort(Comparator.comparing(TreeNodeDetail::getSequence));
            for (T detail : ol) {
                if (isRoot(detail) && filterMap.containsKey(detail.getItemId())) {
                    TreeNode<T> treeNode = TreeNode.TreeNodeBuilder.<T>aTreeNode()
                        .withTreeNodes(children(ol, detail, filterMap))
                        .withTreeNodeDetail(detail)
                        .build();

                    tree.add(treeNode);
                }
            }
        }
        return tree;
    }

    private static <T extends TreeNodeDetail> Map<String, T> supplementParent(Map<String, T> originMap,
        Map<String, T> filterMap) {
        Map<String, T> rs = new HashMap<>(filterMap);
        for (Map.Entry<String, T> entry : filterMap.entrySet()) {
            doSupplementParent(originMap, entry.getValue(), rs);
        }
        return rs;
    }

    private static <T extends TreeNodeDetail> void doSupplementParent(Map<String, T> originMap, T detail,
        Map<String, T> rs) {
        if (!isRoot(detail)) {
            String pid = detail.getParentId();
            T parent = originMap.get(pid);
            if (Objects.nonNull(parent)) {
                rs.putIfAbsent(pid, parent);
                doSupplementParent(originMap, parent, rs);
            }
        }
    }

    public static <T extends TreeNodeDetail> List<TreeNode<T>> getTree(List<T> ol) {
        List<TreeNode<T>> tree = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ol)) {
            ol.sort(Comparator.comparing(TreeNodeDetail::getSequence));
            for (T detail : ol) {
                if (isRoot(detail)) {
                    TreeNode<T> treeNode = TreeNode.TreeNodeBuilder.<T>aTreeNode()
                        .withTreeNodes(children(ol, detail))
                        .withTreeNodeDetail(detail)
                        .build();

                    tree.add(treeNode);
                }
            }
        }
        return tree;
    }

    private static <T extends TreeNodeDetail> List<TreeNode<T>> children(List<T> ol, T detail) {
        return children(ol, detail, null);
    }

    private static <T extends TreeNodeDetail> List<TreeNode<T>> children(List<T> ol, T detail,
        Map<String, T> filterMap) {
        String pid = detail.getItemId();

        List<TreeNode<T>> children = new ArrayList<>();
        for (T t : ol) {
            if (StringUtils.equals(pid, t.getParentId())
                && (MapUtils.isEmpty(filterMap) || filterMap.containsKey(t.getItemId()))) {
                TreeNode<T> treeNode = TreeNode.TreeNodeBuilder.<T>aTreeNode()
                    .withTreeNodes(children(ol, t))
                    .withTreeNodeDetail(t)
                    .build();

                children.add(treeNode);
            }
        }
        return CollectionUtils.isNotEmpty(children) ? children : null;
    }

    public static boolean isRoot(TreeNodeDetail detail) {
        // trim pid to avoid dirty data
        return Objects.nonNull(detail) && SipaConstant.Symbol.ACROSS.equals(StringUtils.trim(detail.getParentId()));
    }
}
