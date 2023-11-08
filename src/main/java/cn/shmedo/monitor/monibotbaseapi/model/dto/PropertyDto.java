package cn.shmedo.monitor.monibotbaseapi.model.dto;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.catalina.LifecycleState;

import java.util.List;

/**
 * @author Chengfs on 2023/3/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PropertyDto extends TbProperty {

    private Integer projectID;


    private Integer propertyID;

    private String value;
    private List<String> ossList;
    private List<FileInfoResponse> fileList;
}

    
    