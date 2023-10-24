package cn.shmedo.monitor.monibotbaseapi.model.response.third.workflow;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @Author wuxl
 * @Date 2023/10/19 11:15
 * @PackageName:cn.shmedo.workflowengine.model.response.template
 * @ClassName: DescribeWorkFlowTemplateResponse
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ToString
public class DescribeWorkFlowTemplateResponse {
    @JsonProperty("ID")
    private Long ID;

    private Integer companyID;

    private String bpmnFile;

    private String name;

    private Integer platform;

    private String platformName;

    private Integer workFlowType;

    private String workFlowTypeName;

    private Long groupID;

    private String groupName;

    /** camunda中的流程定义id **/
    private String workFlowID;

    /** camunda中的流程定义key **/
    private String workFlowKey;

    /** camunda中的流程定义版本 **/
    private Integer workFlowVersion;

    /** 是否是最新版本（0-不是；1-是）**/
    private Integer isLatest;

    /** 流程定义的状态，0-禁用 1-启用 **/
    private Boolean status;

    /** 流程定义中节点的信息 **/
    private List<TemplateNode> templateNodeList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer createUserID;

    private String createName;

    @Data
    public static class TemplateNode{

        private Long nodeID;
        private String nodeName;
        private Integer status;
        private List<NodeHandler> nodeHandlerList;
    }
    @Data
    public static class NodeHandler{
        private Integer nodeHandlerID;
        private String nodeHandlerName;
    }
}
