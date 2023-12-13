package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-02 10:31
 **/
@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class Model4Web extends TbPropertyModel {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    private String groupName;
    private boolean edit = true;

    private List<TbProperty> propertyList;

    public static Model4Web valueOf(TbPropertyModel tbPropertyModel, List<TbProperty> properties) {
        Model4Web model4Web = JSONUtil.toBean(JSONUtil.toJsonStr(tbPropertyModel), Model4Web.class);
        model4Web.setPropertyList(properties);
        return model4Web;
    }
}
