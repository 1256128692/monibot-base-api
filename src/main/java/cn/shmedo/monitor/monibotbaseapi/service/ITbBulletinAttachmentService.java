package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbBulletinAttachment;
import cn.shmedo.monitor.monibotbaseapi.model.param.bulletin.QueryBulletinAttachmentPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.bulletin.BulletinAttachmentPageInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-27 17:55
 */
public interface ITbBulletinAttachmentService extends IService<TbBulletinAttachment> {
    PageUtil.Page<BulletinAttachmentPageInfo> queryBulletinAttachmentPage(QueryBulletinAttachmentPageParam param);
}
