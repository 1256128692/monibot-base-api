package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMultipleDeviceParam  {
    /**
     * 公司ID
     */
    @NotNull
    private Integer companyID;
    /**
     * 设备列表
     */
    @NotEmpty
    @Valid
    @Size(max = 100)
    private List<@NotNull CreateMultipleDeviceItem> deviceList = new LinkedList<>();



    @Override
    public String toString() {
        return "CreateMultipleDeviceParam{" +
                "companyID=" + companyID +
                ", deviceList=" + deviceList +
                '}';
    }
}
