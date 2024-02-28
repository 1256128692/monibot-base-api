package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbBulletinAttachmentMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinAttachment;
import cn.shmedo.monitor.monibotbaseapi.model.param.bulletin.QueryBulletinAttachmentPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.bulletin.BulletinAttachmentPageInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbBulletinAttachmentService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-27 17:55
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbBulletinAttachmentServiceImpl extends ServiceImpl<TbBulletinAttachmentMapper, TbBulletinAttachment> implements ITbBulletinAttachmentService {
    private final FileService fileService;

    @Override
    public PageUtil.Page<BulletinAttachmentPageInfo> queryBulletinAttachmentPage(QueryBulletinAttachmentPageParam param) {
        return Optional.of(this.list(new LambdaQueryWrapper<TbBulletinAttachment>()
                .eq(TbBulletinAttachment::getBulletinID, param.getBulletinID()))).filter(CollUtil::isNotEmpty).map(dataList -> {
            List<String> filePathList = dataList.stream().map(TbBulletinAttachment::getFilePath).distinct().toList();
            Map<String, FileInfoResponse> filePathMap = Optional.ofNullable(fileService.getFileUrlList(filePathList, param.getCompanyID()))
                    .map(u -> u.stream().collect(Collectors.toMap(FileInfoResponse::getFilePath, Function.identity()))).orElse(Map.of());
            List<BulletinAttachmentPageInfo> resultList = dataList.stream().map(u ->
                    Optional.ofNullable(u.getFilePath()).filter(filePathMap::containsKey).map(filePathMap::get)
                            .map(w -> BulletinAttachmentPageInfo.build(u, w)).filter(w ->
                                    Optional.ofNullable(param.getFileName()).filter(StrUtil::isNotEmpty)
                                            .map(s -> w.getFileName().contains(s)).orElse(true))
                            .orElse(null)).filter(Objects::nonNull).toList();
            return PageUtil.page(resultList, param.getPageSize(), param.getCurrentPage());
        }).orElse(PageUtil.Page.empty());
    }
}
