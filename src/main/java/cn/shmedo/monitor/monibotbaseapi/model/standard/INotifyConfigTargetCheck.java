package cn.shmedo.monitor.monibotbaseapi.model.standard;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryDeptSimpleListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserInDeptListNoPageParam;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import jakarta.annotation.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-12 15:30
 */
public interface INotifyConfigTargetCheck {
    List<Integer> getDeptList();

    List<Integer> getUserList();

    Integer getCompanyID();

    String getExValue();

    /**
     * check only version.
     *
     * @see #validTarget(Consumer, Consumer, Consumer)
     */
    default ResultWrapper<?> validTarget() {
        return validTarget(null, null, null);
    }

    /**
     * 配置的被通知对象的合法性校验(提供{@link Consumer}让调用方可以获取校验后的值)<br>
     * 注意: 仅校验{@link List}里元素的合法性,不校验{@link List}本身是否<b>必须存在、非空</b>
     */
    default ResultWrapper<?> validTarget(@Nullable final Consumer<List<Integer>> deptsConsumer,
                                         @Nullable final Consumer<List<Integer>> usersConsumer,
                                         @Nullable final Consumer<String> exValueConsumer) {
        String exValue = getExValue();
        if (ObjectUtil.isNotEmpty(exValue)) {
            try {
                JSONArray array = JSONUtil.parseArray(exValue);
            } catch (JSONException e) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "配置的扩展信息不合法");
            }
            Optional.ofNullable(exValueConsumer).ifPresent(u -> u.accept(exValue));
        }
        List<Integer> deptList = Optional.ofNullable(getDeptList()).filter(CollUtil::isNotEmpty).map(u ->
                u.stream().distinct().toList()).orElse(null);
        List<Integer> userList = Optional.ofNullable(getUserList()).filter(CollUtil::isNotEmpty).map(u ->
                u.stream().distinct().toList()).orElse(null);
        if (CollUtil.isNotEmpty(deptList) || CollUtil.isNotEmpty(userList)) {
            Integer companyID = getCompanyID();
            UserService userService = getUserService();
            FileConfig fileConfig = getFileConfig();
            String authAppKey = fileConfig.getAuthAppKey();
            String authAppSecret = fileConfig.getAuthAppSecret();
            if (Optional.ofNullable(deptList).filter(CollUtil::isNotEmpty).map(u -> {
                        Optional.ofNullable(deptsConsumer).ifPresent(w -> w.accept(u));
                        return new QueryDeptSimpleListParam(companyID, u);
                    }).map(u -> userService.queryDeptSimpleList(u, authAppKey, authAppSecret)).filter(ResultWrapper::apiSuccess)
                    .map(ResultWrapper::getData).map(u -> !Objects.equals(u.size(), deptList.size())).orElse(false)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有部门不存在");
            }
            if (Optional.ofNullable(userList).filter(CollUtil::isNotEmpty).map(u -> {
                        Optional.ofNullable(usersConsumer).ifPresent(w -> w.accept(u));
                        QueryUserInDeptListNoPageParam param = new QueryUserInDeptListNoPageParam();
                        param.setCompanyID(companyID);
                        param.setUserIDList(u);
                        param.setIncludeExternal(true);
                        return param;
                    }).map(u -> userService.queryUserInDeptListNoPage(u, authAppKey, authAppSecret)).filter(ResultWrapper::apiSuccess)
                    .map(ResultWrapper::getData).map(u -> !Objects.equals(u.size(), userList.size())).orElse(false)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有用户不存在");
            }
        }
        return null;
    }

    /**
     * offered a default method to allow caller reuse {@link UserService}.
     */
    default UserService getUserService() {
        return ContextHolder.getBean(UserService.class);
    }

    /**
     * offered a default method to allow caller reuse {@link FileConfig}.
     */
    default FileConfig getFileConfig() {
        return ContextHolder.getBean(FileConfig.class);
    }
}
