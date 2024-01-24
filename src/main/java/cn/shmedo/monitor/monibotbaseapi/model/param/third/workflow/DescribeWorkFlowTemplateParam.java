package cn.shmedo.monitor.monibotbaseapi.model.param.third.workflow;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 11:57
 */
public class DescribeWorkFlowTemplateParam {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须是正值")
    private Integer companyID;
    @NotNull(message = "模板ID不能为空")
    @Positive(message = "模板ID必须是正值")
    private Integer templateID;

    public DescribeWorkFlowTemplateParam() {
    }

    public DescribeWorkFlowTemplateParam(Integer companyID, Integer templateID) {
        this.companyID = companyID;
        this.templateID = templateID;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getTemplateID() {
        return templateID;
    }

    public void setTemplateID(Integer templateID) {
        this.templateID = templateID;
    }
}
