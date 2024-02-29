package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbBulletinDataMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinAttachment;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinData;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinPlatformRelation;
import cn.shmedo.monitor.monibotbaseapi.model.param.bulletin.AddBulletinDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.bulletin.UpdateBulletinData;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserInDeptListNoPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserNoPageInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbBulletinAttachmentService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbBulletinDataService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbBulletinPlatformRelationService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-27 17:42
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbBulletinDataServiceImpl extends ServiceImpl<TbBulletinDataMapper, TbBulletinData> implements ITbBulletinDataService {
    private final FileConfig fileConfig;
    private final UserService userService;
    private final ITbBulletinAttachmentService tbBulletinAttachmentService;
    private final ITbBulletinPlatformRelationService tbBulletinPlatformRelationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBulletinData(AddBulletinDataParam param, Integer userID) {
        final String currentUserStr = getCurrentUserStr(param.getCompanyID(), userID);

        // save bulletin
        TbBulletinData tbBulletinData = param.getTbBulletinData();
        tbBulletinData.setCreateUser(currentUserStr);
        tbBulletinData.setUpdateUser(currentUserStr);
        this.save(tbBulletinData);
        Integer bulletinId = tbBulletinData.getId();

        // save platform relation
        List<TbBulletinPlatformRelation> platformRelationList = param.getPlatformRelationList().stream()
                .peek(u -> u.setBulletinID(bulletinId)).toList();
        this.tbBulletinPlatformRelationService.saveBatch(platformRelationList);

        // save attachments
        saveAttachmentList(param.getTbBulletinAttachmentList(), bulletinId, currentUserStr);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBulletinData(UpdateBulletinData param, Integer userID) {
        final Integer bulletinID = param.getBulletinID();
        final String currentUserStr = getCurrentUserStr(param.getCompanyID(), userID);

        // update bulletin
        TbBulletinData tbBulletinData = param.getTbBulletinData();
        tbBulletinData.setUpdateUser(currentUserStr);
        this.updateById(tbBulletinData);

        // update platform relation
        List<TbBulletinPlatformRelation> platformRelationList = param.getPlatformRelationList();
        if (CollUtil.isNotEmpty(platformRelationList)) {
            this.tbBulletinPlatformRelationService.remove(new LambdaQueryWrapper<TbBulletinPlatformRelation>()
                    .eq(TbBulletinPlatformRelation::getBulletinID, bulletinID));
            this.tbBulletinPlatformRelationService.saveBatch(platformRelationList);
        }

        // save attachments
        saveAttachmentList(param.getTbBulletinAttachmentList(), bulletinID, currentUserStr);
    }

    /**
     * @return 部门名称+" "+用户名,部门取所属部门里第一个
     * @see #getCurrentUserInfo(Integer, Integer)
     */
    private String getCurrentUserStr(final Integer companyID, final Integer userID) {
        return Optional.of(getCurrentUserInfo(companyID, userID)).map(u -> u.getDepartments().stream().findFirst()
                .map(UserNoPageInfo.TbDepartment::getName).orElseThrow() + " " + u.getUser().getName()).orElseThrow();
    }

    /**
     * 获取用户信息
     *
     * @param companyID 公司ID
     * @param userID    用户ID
     */
    private UserNoPageInfo getCurrentUserInfo(final Integer companyID, final Integer userID) {
        return Optional.of(new QueryUserInDeptListNoPageParam()).map(u -> {
            u.setCompanyID(companyID);
            u.setUserIDList(List.of(userID));
            return userService.queryUserInDeptListNoPage(u, fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());
        }).filter(ResultWrapper::apiSuccess).map(ResultWrapper::getData).flatMap(u -> u.stream().filter(w ->
                Objects.nonNull(w.getUser()) && CollUtil.isNotEmpty(w.getDepartments())).findFirst()).orElseThrow();
    }

    private void saveAttachmentList(List<TbBulletinAttachment> tbBulletinAttachmentList, Integer bulletinId, String currentUserStr) {
        if (CollUtil.isNotEmpty(tbBulletinAttachmentList)) {
            tbBulletinAttachmentList.forEach(u -> {
                u.setBulletinID(bulletinId);
                u.setCreateUser(currentUserStr);
            });
            this.tbBulletinAttachmentService.saveBatch(tbBulletinAttachmentList);
        }
    }
}
