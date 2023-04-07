package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-02 10:31
 **/
public class Model4Web extends TbPropertyModel {


    private List<TbProperty> propertyList;

    public static Model4Web valueOf(TbPropertyModel tbPropertyModel, List<TbProperty> properties) {
        Model4Web model4Web = JSONUtil.toBean(JSONUtil.toJsonStr(tbPropertyModel), Model4Web.class);
        model4Web.setPropertyList(properties);
        return model4Web;
    }


    public List<TbProperty> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<TbProperty> propertyList) {
        this.propertyList = propertyList;
    }
}
