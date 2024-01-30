package cn.shmedo.monitor.monibotbaseapi.util.depts;

import cn.hutool.core.collection.CollUtil;
import jakarta.validation.constraints.NotNull;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static cn.shmedo.monitor.monibotbaseapi.model.response.third.DepartmentIncludeUserInfo.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-30 09:58
 */
public class DeptUtils {
    // 超时值
    private static final long TIMEOUT_LIMIT = 50L;

    /**
     * 获取到部门下的全部用户（包括子部门），进行相应处理
     *
     * @param depts       部门数据
     * @param judgeOpt    判断条件
     * @param resConsumer 处理逻辑,{@code TbDepartment}实例可能为{@code null},此时没找到符合{@code judgeOpt}的部门,因此对应的{@code List<UsersInDept>}实例是{@code emptyList}
     */
    public static void processDepartWithAllUser(@NotNull final List<DeptResponse> depts, @NotNull final Function<TbDepartment, Boolean> judgeOpt,
                                                @NotNull final BiConsumer<TbDepartment, List<UsersInDept>> resConsumer) throws TimeoutException {
        final long start = System.currentTimeMillis();
        DeptResponse executeDept = null;
        Deque<Iterator<DeptResponse>> deque = new ArrayDeque<>();
        Iterator<DeptResponse> cur = depts.iterator();

        // select
        for (; ; ) {
            if (System.currentTimeMillis() - start > TIMEOUT_LIMIT) {
                throw new TimeoutException("处理超时...");
            } else if (cur.hasNext()) {
                DeptResponse next = cur.next();
                TbDepartment tbDepartment = Optional.ofNullable(next.getDeptInfo()).filter(u -> Objects.nonNull(u.getId()))
                        .orElseThrow(() -> new IllegalArgumentException("部门信息缺少部门数据或id"));
                if (judgeOpt.apply(tbDepartment)) {
                    executeDept = next;
                    break;
                } else {
                    List<DeptResponse> subDepts = next.getSubDepts();
                    if (Objects.nonNull(subDepts)) {
                        deque.push(cur);
                        cur = subDepts.iterator();
                    }
                }
            } else {
                if (deque.isEmpty()) {
                    break;
                }
                cur = deque.pop();
            }
        }

        // execute
        if (Objects.nonNull(executeDept)) {
            deque.clear();
            cur = List.of(executeDept).iterator();
            List<UsersInDept> users = new ArrayList<>();
            for (; ; ) {
                if (System.currentTimeMillis() - start > TIMEOUT_LIMIT) {
                    throw new TimeoutException("处理超时...");
                } else if (cur.hasNext()) {
                    DeptResponse next = cur.next();
                    if (Objects.nonNull(next.getSubDepts())) {
                        deque.push(cur);
                        cur = next.getSubDepts().iterator();
                    }
                    Optional.ofNullable(next.getUsersInDept()).filter(CollUtil::isNotEmpty).ifPresent(users::addAll);
                } else {
                    if (deque.isEmpty()) {
                        break;
                    }
                    cur = deque.pop();
                }
            }
            resConsumer.accept(executeDept.getDeptInfo(), users);
        } else {
            resConsumer.accept(null, List.of());
        }
    }
}
