package cn.shmedo.monitor.monibotbaseapi.util.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 *  自定义元数据对象处理器
 *  自动设置创建人创建时间字段值
 *  自动设置更新人更新时间字段值
 */

@Component
@Slf4j
public class MyMetaObjecthandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("新增操作：",metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        /*metaObject.setValue("createUser", BaseContext.getUserID());
        metaObject.setValue("updateUser", BaseContext.getUserID());*/
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("更新操作：",metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        /*metaObject.setValue("updateUser", BaseContext.getUserID());*/
    }
}
