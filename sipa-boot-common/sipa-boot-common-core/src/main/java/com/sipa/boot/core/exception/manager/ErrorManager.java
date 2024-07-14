package com.sipa.boot.core.exception.manager;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBiMap;
import com.sipa.boot.core.exception.api.IErrorCode;
import com.sipa.boot.core.exception.api.IProjectModule;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2023/4/24
 */
@Slf4j
public class ErrorManager {
    private final static HashBiMap<String, IErrorCode> GLOBAL_ERROR_CODE_MAP = HashBiMap.create();

    private final static Map<IErrorCode, IProjectModule> ERROR_PROJECT_MODULE_MAP = new ConcurrentHashMap<>();

    private static final Comparator<IProjectModule> PROJECT_MODULE_COMPARATOR =
        Comparator.comparing(IProjectModule::getProjectCode).thenComparing(IProjectModule::getModuleCode);

    private static final Comparator<IErrorCode> ERROR_CODE_COMPARATOR = Comparator.comparing(IErrorCode::getCode);

    public static String getAllCode(IErrorCode errorCode) {
        return GLOBAL_ERROR_CODE_MAP.inverse().get(errorCode);
    }

    public static IProjectModule projectModule(IErrorCode errorCode) {
        return ERROR_PROJECT_MODULE_MAP.get(errorCode);
    }

    public static void register(IProjectModule projectModule, IErrorCode errorCode) {
        Preconditions.checkNotNull(projectModule);
        String projectCode = projectModule.getProjectCode();
        Preconditions.checkNotNull(projectCode);
        String moduleCode = projectModule.getModuleCode();
        Preconditions.checkNotNull(moduleCode);
        String code = errorCode.getCode();
        Preconditions.checkNotNull(code);
        String allCode = genCode(projectModule, errorCode);
        try {
            Preconditions.checkArgument(!GLOBAL_ERROR_CODE_MAP.containsKey(allCode), "错误码重复: " + allCode);
        } catch (Exception e) {
            log.error(e.getMessage());
            // 合并部署的时候需要忽略，如果没有注册上，使用就会报错
            return;
        }
        CharMatcher cm = CharMatcher.anyOf("oOiI");
        if (cm.matchesAnyOf(allCode)) {
            throw new IllegalArgumentException("错误码不能包含 \"oO\" 或 \"iI\": " + allCode);
        }
        GLOBAL_ERROR_CODE_MAP.put(allCode, errorCode);
        ERROR_PROJECT_MODULE_MAP.put(errorCode, projectModule);
    }

    private static String genCode(IProjectModule projectModule, IErrorCode errorCode) {
        return projectModule.getProjectCode() + projectModule.getModuleCode() + errorCode.getCode();
    }

    public static List<TreeNode> getAllErrorCodes() {
        return ERROR_PROJECT_MODULE_MAP.entrySet()
            .stream()
            .sorted((it1, it2) -> ERROR_CODE_COMPARATOR.compare(it1.getKey(), it2.getKey()))
            .collect(
                Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())))
            .entrySet()
            .stream()
            .sorted((it1, it2) -> PROJECT_MODULE_COMPARATOR.compare(it1.getKey(), it2.getKey()))
            .collect(Collectors.groupingBy(e -> new TreeNode(e.getKey().getProjectCode(), e.getKey().getProjectName()),
                Collectors.groupingBy(it -> new TreeNode(it.getKey().getModuleCode(), it.getKey().getModuleName()),
                    Collectors.mapping(Map.Entry::getValue, Collectors.toList()))))
            .entrySet()
            .stream()
            .map(e -> {
                TreeNode top = e.getKey();
                List<TreeNode> middleNode = e.getValue().entrySet().stream().map(e1 -> {
                    TreeNode key = e1.getKey();
                    List<TreeNode> leftNode = e1.getValue()
                        .stream()
                        .flatMap(Collection::stream)
                        .map(errorCode -> new TreeNode(errorCode.getCode(), errorCode.getMsg()))
                        .collect(Collectors.toList());
                    key.setNodes(leftNode);
                    return key;
                }).collect(Collectors.toList());
                top.setNodes(middleNode);
                return top;
            })
            .collect(Collectors.toList());
    }
}
