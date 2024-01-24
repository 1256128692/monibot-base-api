package cn.shmedo.monitor.monibotbaseapi.model.response.third.workflow;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 11:57
 */
public class DescribeWorkFlowTemplateResponseV2 {
    /**
     * 主键ID（模板ID）
     */
    private Integer ID;
    /**
     * 公司ID
     */
    private Integer companyID;
    /**
     * bpmn文件字符串
     */
    private String bpmnFile;
    /**
     * 模板名称
     */
    private String name;
    /**
     * 所属平台标识
     */
    private Integer platform;

    /**
     * 所属平台名称
     */
    private String platformName;

    /**
     * 分组ID
     */
    private Integer groupID;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 工作流模板分类
     */
    private Integer workFlowType;

    /**
     * 工作流模板分类名称
     */
    private String workFlowTypeName;
    /**
     * 模板开始节点的bpmn节点ID
     */
    private String startBpmnNodeID;

    private List<TemplateNodeInfo> nodeList;
    /**
     * 工作流ID
     */
    private String workFlowID;
    /**
     * 工作流key
     */
    private String workFlowKey;
    /**
     * 工作流模板版本
     */
    private Integer workFlowVersion;
    /**
     * 是否是最新版本（0-不是；1-是）
     */
    private Integer isLatest;
    /**
     * 工作流模板状态（0-停用；1-启用）
     */
    private Integer status;
    /**
     * 创建人ID
     */
    private Integer createUserID;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建时间
     */
    private String createTime;


    public DescribeWorkFlowTemplateResponseV2() {
    }

    public DescribeWorkFlowTemplateResponseV2(Integer ID, Integer companyID, String bpmnFile, String name,
                                              Integer platform, String platformName, Integer groupID, String groupName,
                                              Integer workFlowType, String workFlowTypeName, String startBpmnNodeID,
                                              List<TemplateNodeInfo> nodeList, String workFlowID, String workFlowKey,
                                              Integer workFlowVersion, Integer isLatest, Integer status, Integer createUserID,
                                              String createName, String createTime) {
        this.ID = ID;
        this.companyID = companyID;
        this.bpmnFile = bpmnFile;
        this.name = name;
        this.platform = platform;
        this.platformName = platformName;
        this.groupID = groupID;
        this.groupName = groupName;
        this.workFlowType = workFlowType;
        this.workFlowTypeName = workFlowTypeName;
        this.startBpmnNodeID = startBpmnNodeID;
        this.nodeList = nodeList;
        this.workFlowID = workFlowID;
        this.workFlowKey = workFlowKey;
        this.workFlowVersion = workFlowVersion;
        this.isLatest = isLatest;
        this.status = status;
        this.createUserID = createUserID;
        this.createName = createName;
        this.createTime = createTime;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public String getBpmnFile() {
        return bpmnFile;
    }

    public void setBpmnFile(String bpmnFile) {
        this.bpmnFile = bpmnFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void setPlatform(Integer platform) {
        this.platform = platform;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getWorkFlowType() {
        return workFlowType;
    }

    public void setWorkFlowType(Integer workFlowType) {
        this.workFlowType = workFlowType;
    }

    public String getWorkFlowTypeName() {
        return workFlowTypeName;
    }

    public void setWorkFlowTypeName(String workFlowTypeName) {
        this.workFlowTypeName = workFlowTypeName;
    }

    public String getStartBpmnNodeID() {
        return startBpmnNodeID;
    }

    public void setStartBpmnNodeID(String startBpmnNodeID) {
        this.startBpmnNodeID = startBpmnNodeID;
    }

    public List<TemplateNodeInfo> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<TemplateNodeInfo> nodeList) {
        this.nodeList = nodeList;
    }

    public String getWorkFlowID() {
        return workFlowID;
    }

    public void setWorkFlowID(String workFlowID) {
        this.workFlowID = workFlowID;
    }

    public String getWorkFlowKey() {
        return workFlowKey;
    }

    public void setWorkFlowKey(String workFlowKey) {
        this.workFlowKey = workFlowKey;
    }

    public Integer getWorkFlowVersion() {
        return workFlowVersion;
    }

    public void setWorkFlowVersion(Integer workFlowVersion) {
        this.workFlowVersion = workFlowVersion;
    }

    public Integer getIsLatest() {
        return isLatest;
    }

    public void setIsLatest(Integer isLatest) {
        this.isLatest = isLatest;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCreateUserID() {
        return createUserID;
    }

    public void setCreateUserID(Integer createUserID) {
        this.createUserID = createUserID;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    private static class TemplateNodeInfo {

        /**
         * 节点ID
         */
        private Integer nodeID;

        /**
         * 节点名称
         */
        private String nodeName;

        /**
         * bpmnNodeID
         */
        private String bpmnNodeID;

        /**
         * bpmn节点类型
         */
        private String bpmnNodeType;

        /**
         * 节点人员类型
         */
        private Integer nodePeopleType;

        /**
         * 节点人员类型子分类
         */
        private Integer subType;

        /**
         * 表单ID
         */
        private Integer formID;

        /**
         * 节点状态（0-未执行；1-执行中；2-已执行）
         */
        private Integer status;

        /**
         * 节点负责人列表
         */
        private List<TemplateNodeHandlerInfo> nodeHandlerList;


        public TemplateNodeInfo() {
        }

        public TemplateNodeInfo(Integer nodeID, String nodeName, String bpmnNodeID, String bpmnNodeType,
                                Integer nodePeopleType, Integer subType, Integer formID, Integer status,
                                List<TemplateNodeHandlerInfo> nodeHandlerList) {
            this.nodeID = nodeID;
            this.nodeName = nodeName;
            this.bpmnNodeID = bpmnNodeID;
            this.bpmnNodeType = bpmnNodeType;
            this.nodePeopleType = nodePeopleType;
            this.subType = subType;
            this.formID = formID;
            this.status = status;
            this.nodeHandlerList = nodeHandlerList;
        }

        public Integer getNodeID() {
            return nodeID;
        }

        public void setNodeID(Integer nodeID) {
            this.nodeID = nodeID;
        }

        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        public String getBpmnNodeID() {
            return bpmnNodeID;
        }

        public void setBpmnNodeID(String bpmnNodeID) {
            this.bpmnNodeID = bpmnNodeID;
        }

        public String getBpmnNodeType() {
            return bpmnNodeType;
        }

        public void setBpmnNodeType(String bpmnNodeType) {
            this.bpmnNodeType = bpmnNodeType;
        }

        public Integer getNodePeopleType() {
            return nodePeopleType;
        }

        public void setNodePeopleType(Integer nodePeopleType) {
            this.nodePeopleType = nodePeopleType;
        }

        public Integer getSubType() {
            return subType;
        }

        public void setSubType(Integer subType) {
            this.subType = subType;
        }

        public Integer getFormID() {
            return formID;
        }

        public void setFormID(Integer formID) {
            this.formID = formID;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public List<TemplateNodeHandlerInfo> getNodeHandlerList() {
            return nodeHandlerList;
        }

        public void setNodeHandlerList(List<TemplateNodeHandlerInfo> nodeHandlerList) {
            this.nodeHandlerList = nodeHandlerList;
        }
    }

    private static class TemplateNodeHandlerInfo {
        private Integer nodeHandlerID;
        private String nodeHandlerName;

        public TemplateNodeHandlerInfo() {
        }

        public TemplateNodeHandlerInfo(Integer nodeHandlerID, String nodeHandlerName) {
            this.nodeHandlerID = nodeHandlerID;
            this.nodeHandlerName = nodeHandlerName;
        }

        public Integer getNodeHandlerID() {
            return nodeHandlerID;
        }

        public void setNodeHandlerID(Integer nodeHandlerID) {
            this.nodeHandlerID = nodeHandlerID;
        }

        public String getNodeHandlerName() {
            return nodeHandlerName;
        }

        public void setNodeHandlerName(String nodeHandlerName) {
            this.nodeHandlerName = nodeHandlerName;
        }
    }
}
