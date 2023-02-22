package cn.shmedo.monitor.monibotbaseapi.model.param.response;

import cn.shmedo.monitor.monibotbaseapi.model.Company;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;
import lombok.Data;

import java.util.List;

/**
 * @Author cyf
 * @Date 2023/2/22 11:21
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.data.response
 * @ClassName: TbProjectInfoResult
 * @Description: TODO
 * @Version 1.0
 */

@Data
public class ProjectInfoResult extends TbProjectInfo {

    private Company company;
    private List<TbTag> tagInfo;
    private List<TbProperty> propertyList;

}
