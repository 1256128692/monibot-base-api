package cn.shmedo.monitor.monibotbaseapi.model.param.third.workflow;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Date;
import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 11:34
 */
public class StartWorkFlowTaskParam {
    /**
     * 公司ID
     */
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须是正值")
    private Integer companyID;
    /**
     * 工程ID
     */
    @NotNull(message = "工程ID不能为空")
    @Positive(message = "工程ID必须是正值")
    private Integer projectID;
    /**
     * 模板ID
     */
    @NotNull(message = "模板ID不能为空")
    @Positive(message = "模板ID必须是正值")
    private Integer templateID;
    /**
     * 模板开始节点的bpmn节点ID
     */
    @NotEmpty(message = "模板开始节点的bpmn节点ID不能为空")
    private String startBpmnNodeID;
    /**
     * 流程名称（默认工作流名称）
     */
    private String name;
    /**
     * 到期时间（用于节点任务超时，后期会用）
     */
    private Date dueTime;
    /**
     * 表单属性值列表
     */
    private List<PropertyValue> propertyValueList;
    /**
     * 流程模板中节点上的扩展信息
     */
    private List<TemplateNodeExtension> nodeList;

    public StartWorkFlowTaskParam() {
    }

    public StartWorkFlowTaskParam(Integer companyID, Integer projectID, Integer templateID, String startBpmnNodeID) {
        this.companyID = companyID;
        this.projectID = projectID;
        this.templateID = templateID;
        this.startBpmnNodeID = startBpmnNodeID;
    }

    public StartWorkFlowTaskParam(Integer companyID, Integer projectID, Integer templateID, String startBpmnNodeID,
                                  String name, Date dueTime, List<PropertyValue> propertyValueList,
                                  List<TemplateNodeExtension> nodeList) {
        this.companyID = companyID;
        this.projectID = projectID;
        this.templateID = templateID;
        this.startBpmnNodeID = startBpmnNodeID;
        this.name = name;
        this.dueTime = dueTime;
        this.propertyValueList = propertyValueList;
        this.nodeList = nodeList;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public Integer getTemplateID() {
        return templateID;
    }

    public void setTemplateID(Integer templateID) {
        this.templateID = templateID;
    }

    public String getStartBpmnNodeID() {
        return startBpmnNodeID;
    }

    public void setStartBpmnNodeID(String startBpmnNodeID) {
        this.startBpmnNodeID = startBpmnNodeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }

    public List<TemplateNodeExtension> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<TemplateNodeExtension> nodeList) {
        this.nodeList = nodeList;
    }

    private static class PropertyValue {
        /**
         * 表单属性ID
         */
        private Integer ID;
        /**
         * 表单属性值
         */
        private String value;

        public PropertyValue() {
        }

        public PropertyValue(Integer ID, java.lang.String value) {
            this.ID = ID;
            this.value = value;
        }

        public Integer getID() {
            return ID;
        }

        public void setID(Integer ID) {
            this.ID = ID;
        }

        public java.lang.String getValue() {
            return value;
        }

        public void setValue(java.lang.String value) {
            this.value = value;
        }
    }

    private static class TemplateNodeExtension {
        /**
         * 节点ID，对应设计编辑器定义节点的ID
         */
        private String bpmnNodeID;
        /**
         * 节点处理人的类型(0-用户id或者角色id；1-或签；2-并行会签；3-串行会签；4-动态负责人)
         */
        private Integer nodePeopleType;
        /**
         * 节点处理人列表
         */
        private List<Integer> nodeHandlerIDList;
        /**
         * 节点处理人角色列表
         */
        private List<Integer> nodeHandlerRoleIDList;
        /**
         * 节点处理人部门列表
         */
        private List<Integer> nodeHandlerDeptIDList;

        public TemplateNodeExtension() {
        }

        public TemplateNodeExtension(String bpmnNodeID, Integer nodePeopleType, List<Integer> nodeHandlerIDList,
                                     List<Integer> nodeHandlerRoleIDList, List<Integer> nodeHandlerDeptIDList) {
            this.bpmnNodeID = bpmnNodeID;
            this.nodePeopleType = nodePeopleType;
            this.nodeHandlerIDList = nodeHandlerIDList;
            this.nodeHandlerRoleIDList = nodeHandlerRoleIDList;
            this.nodeHandlerDeptIDList = nodeHandlerDeptIDList;
        }

        public String getBpmnNodeID() {
            return bpmnNodeID;
        }

        public void setBpmnNodeID(String bpmnNodeID) {
            this.bpmnNodeID = bpmnNodeID;
        }

        public Integer getNodePeopleType() {
            return nodePeopleType;
        }

        public void setNodePeopleType(Integer nodePeopleType) {
            this.nodePeopleType = nodePeopleType;
        }

        public List<Integer> getNodeHandlerIDList() {
            return nodeHandlerIDList;
        }

        public void setNodeHandlerIDList(List<Integer> nodeHandlerIDList) {
            this.nodeHandlerIDList = nodeHandlerIDList;
        }

        public List<Integer> getNodeHandlerRoleIDList() {
            return nodeHandlerRoleIDList;
        }

        public void setNodeHandlerRoleIDList(List<Integer> nodeHandlerRoleIDList) {
            this.nodeHandlerRoleIDList = nodeHandlerRoleIDList;
        }

        public List<Integer> getNodeHandlerDeptIDList() {
            return nodeHandlerDeptIDList;
        }

        public void setNodeHandlerDeptIDList(List<Integer> nodeHandlerDeptIDList) {
            this.nodeHandlerDeptIDList = nodeHandlerDeptIDList;
        }
    }
}
