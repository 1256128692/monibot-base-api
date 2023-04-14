package cn.shmedo.monitor.monibotbaseapi;

import cn.hutool.core.stream.CollectorUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnActionMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnAction;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import cn.shmedo.monitor.monibotbaseapi.model.param.engine.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.WtEngineInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnActionService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnRuleService;
import cn.shmedo.monitor.monibotbaseapi.util.JsonUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MonibotBaseApiApplication.class)
public class TbWarnRuleServiceTest {
    @Autowired
    private ITbWarnRuleService tbWarnRuleService;

//    @Test
//    public void testDeleteWtEngine() {
//    }
//
//    @Test
//    public void testUpdateWtEngineEnable() {
//    }
//
//    @Test
//    public void testQueryWtEngineDetail() {   //TODO need get appKey and appSecret
//        QueryWtEngineDetailParam param = new QueryWtEngineDetailParam();
//        param.setEngineID(1);
//        System.out.println(JsonUtil.toJson(tbWarnRuleService.queryWtEngineDetail(param, )));
//    }


//    ------ has been tested ------
//    @Test
//    public void testQueryWtEnginePage() {
//        QueryWtEnginePageParam param = new QueryWtEnginePageParam();
//        param.setCompanyID(138);
//        param.setCurrentPage(1);
//        param.setPageSize(20);
//        PageUtil.Page<WtEngineInfo> wtEngineInfoPage = tbWarnRuleService.queryWtEnginePage(param);
//        Assert.assertEquals(1, wtEngineInfoPage.totalPage());
//    }
//
//    @Test
//    public void testQueryWtEnginePage2() {
//        QueryWtEnginePageParam param = new QueryWtEnginePageParam();
//        param.setCompanyID(138);
//        param.setCurrentPage(2);
//        param.setPageSize(10);
//        PageUtil.Page<WtEngineInfo> wtEngineInfoPage = tbWarnRuleService.queryWtEnginePage(param);
//        Assert.assertEquals(0, wtEngineInfoPage.currentPageData().size());
//    }
//
//    @Test
//    public void testQueryWtEnginePageEN1() {
//        QueryWtEnginePageParam param = new QueryWtEnginePageParam();
//        param.setCompanyID(138);
//        param.setCurrentPage(1);
//        param.setPageSize(10);
//        param.setEngineName("stRuleEngine230");
//        PageUtil.Page<WtEngineInfo> wtEngineInfoPage = tbWarnRuleService.queryWtEnginePage(param);
//        Assert.assertEquals(1, wtEngineInfoPage.totalPage());
//    }
//
//    @Test
//    public void testQueryWtEnginePageEN0() {
//        QueryWtEnginePageParam param = new QueryWtEnginePageParam();
//        param.setCompanyID(138);
//        param.setCurrentPage(1);
//        param.setPageSize(10);
//        param.setEngineName("stRuleEng5ine230");
//        PageUtil.Page<WtEngineInfo> wtEngineInfoPage = tbWarnRuleService.queryWtEnginePage(param);
//        Assert.assertEquals(0, wtEngineInfoPage.totalPage());
//    }
//
//    @Test
//    public void testQueryWtEnginePageE0() {
//        QueryWtEnginePageParam param = new QueryWtEnginePageParam();
//        param.setCompanyID(138);
//        param.setCurrentPage(1);
//        param.setPageSize(10);
//        param.setEnable(true);
//        PageUtil.Page<WtEngineInfo> wtEngineInfoPage = tbWarnRuleService.queryWtEnginePage(param);
//        Assert.assertEquals(0, wtEngineInfoPage.totalPage());
//    }
//
//    @Test
//    public void testQueryWtEnginePageE1() {
//        QueryWtEnginePageParam param = new QueryWtEnginePageParam();
//        param.setCompanyID(138);
//        param.setCurrentPage(1);
//        param.setPageSize(10);
//        param.setEnable(false);
//        PageUtil.Page<WtEngineInfo> wtEngineInfoPage = tbWarnRuleService.queryWtEnginePage(param);
//        Assert.assertEquals(1, wtEngineInfoPage.totalPage());
//    }
//
//    @Test
//    public void testQueryWtEnginePagePID0() {
//        QueryWtEnginePageParam param = new QueryWtEnginePageParam();
//        param.setCompanyID(138);
//        param.setCurrentPage(1);
//        param.setPageSize(10);
//        param.setProjectID(123);
//        PageUtil.Page<WtEngineInfo> wtEngineInfoPage = tbWarnRuleService.queryWtEnginePage(param);
//        Assert.assertEquals(0, wtEngineInfoPage.totalPage());
//    }
//
//    @Test
//    public void testQueryWtEnginePagePID1() {
//        QueryWtEnginePageParam param = new QueryWtEnginePageParam();
//        param.setCompanyID(138);
//        param.setCurrentPage(1);
//        param.setPageSize(10);
//        param.setProjectID(221);
//        PageUtil.Page<WtEngineInfo> wtEngineInfoPage = tbWarnRuleService.queryWtEnginePage(param);
//        Assert.assertEquals(1, wtEngineInfoPage.totalPage());
//    }
//
//    @Test
//    public void testAddWtEngine() {
//        AddWtEngineParam param = new AddWtEngineParam();
//        param.setCompanyID(138);
//        param.setProjectID(221);
//        param.setEngineName("TestRuleEngine230413");
//        param.setEngineDesc("I am a test rule engine");
//        param.setMonitorItemID(19);
//        param.setMonitorPointID(55);
//        tbWarnRuleService.addWtEngine(param, 100937);
//    }
//
//    @Test
//    public void testUpdateWtEngine() {
//        String updateDesc = "this desc has been changed while you see this desc.";
//        UpdateWtEngineParam param = new UpdateWtEngineParam();
//        param.setEngineID(1);
//        param.setEngineDesc(updateDesc);
//        tbWarnRuleService.updateWtEngine(param);
//        TbWarnRule rule = tbWarnRuleService.getById(1);
//        Assert.assertEquals(updateDesc, rule.getDesc());
//    }
}
