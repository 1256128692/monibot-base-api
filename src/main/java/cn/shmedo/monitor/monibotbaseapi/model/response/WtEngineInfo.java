package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class WtEngineInfo {
    private Integer engineID;
    private String engineName;
    private Boolean enable;
    private Integer projectID;
    private String projectName;
    private Integer monitorItemID;
    private String monitorItemName;
    private Integer monitorPointID;
    private String monitorPointName;
    private List<WtWarnStatusInfo> dataList;

    public static WtEngineInfo build(TbWarnRule tbWarnRule) {
        WtEngineInfo res = new WtEngineInfo();
        BeanUtils.copyProperties(tbWarnRule, res);
        return res.setEngineID(tbWarnRule.getID()).setEngineName(tbWarnRule.getName());
    }
}
