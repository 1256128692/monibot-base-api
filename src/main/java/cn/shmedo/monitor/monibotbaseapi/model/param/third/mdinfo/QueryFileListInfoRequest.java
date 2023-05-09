package cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QueryFileListInfoRequest  {

    @NotNull
    private Integer companyID;
    @NotNull
    private String bucketName;


    private Long validityPeriod;

    @NotEmpty
    private List<String> filePathList;


}
