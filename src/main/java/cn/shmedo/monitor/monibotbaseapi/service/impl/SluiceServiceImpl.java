package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroupPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.PropWithValue;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceSimple;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.TokenAndMsgID;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sluice.ControlCmd;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sluice.SluiceData;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sluice.SluiceLog;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sluice.SluiceStatus;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertySubjectType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlActionKind;
import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlActionType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlType;
import cn.shmedo.monitor.monibotbaseapi.model.param.sluice.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.BatchDispatchRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceSimpleByUniqueTokensParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserIDNameParameter;
import cn.shmedo.monitor.monibotbaseapi.model.response.sluice.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import cn.shmedo.monitor.monibotbaseapi.service.SluiceService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import cn.shmedo.monitor.monibotbaseapi.util.influx.SimpleQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.shmedo.monitor.monibotbaseapi.constants.SluiceConstant.*;
import static cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlActionKind.OPEN;

/**
 * @author Chengfs on 2023/11/21
 */
@Service
@RequiredArgsConstructor
public class SluiceServiceImpl implements SluiceService {

    private final TbProjectPropertyMapper propertyMapper;
    private final TbSensorMapper sensorMapper;
    private final TbMonitorGroupPointMapper monitorGroupPointMapper;
    private final TbProjectInfoMapper projectInfoMapper;
    private final TbMonitorPointMapper monitorPointMapper;
    private final InfluxDB influxDb;
    private final IotService iotService;
    private final UserService userService;
    private final FileConfig config;

    private final Lock controlLock = new ReentrantLock();

    @Override
    public ControlRecord singleControlRecord(SingleControlRecordRequest request) {
        SluiceLog log = request.getSluiceLog();
        TbSensor sensor = request.getSensor();

        if (log != null) {
            //获取字典
            Map<Integer, Map<String, String>> propMap = getPropDict(List.of(sensor), TbSensor::getProjectID,
                    Set.of(CANAL_NAME, SLUICE_TYPE, MANAGE_UNIT));
            Map<Integer, String> projectMap = getProjectDict(List.of(sensor), TbSensor::getProjectID);
            Map<Integer, String> userMap = getUserDict(List.of(log), SluiceLog::getUserID);

            ControlRecord record = new ControlRecord();
            record.setId(log.getTime().toEpochSecond(ZoneOffset.of("+8")));
            record.setProjectID(sensor.getProjectID());
            Optional.ofNullable(projectMap.get(sensor.getProjectID())).ifPresent(p -> {
                record.setProjectName(p);
                Optional.ofNullable(propMap.get(sensor.getProjectID())).ifPresent(prop -> {
                    record.setCanal(prop.get(CANAL_NAME));
                    record.setSluiceType(prop.get(SLUICE_TYPE));
                    record.setMmUnit(prop.get(MANAGE_UNIT));
                });
            });
            record.setGateID(sensor.getID());
            record.setGateName(sensor.getAlias());
            record.setControlType(ControlType.formDeviceCode(log.getHardware()));
            record.setActionType(log.getSoftware());
            Optional.ofNullable(log.getUserID()).ifPresent(u -> {
                record.setOperationUserID(u);
                record.setOperationUser(userMap.get(u));
            });
            record.setOperationTime(log.getTime());
            return record;
        }
        return null;
    }

    @Override
    public List<Long> addSluiceControlRecord(AddControlRecordRequest request) {
        Long count = SimpleQuery.of(SluiceLog.TABLE).eq(DbConstant.TIME_FIELD, request.getTime())
                .in(DbConstant.SENSOR_ID_TAG, request.getSensorIDList().stream().map(Object::toString).toList())
                .count(influxDb, SluiceLog.HARDWARE);
        Assert.isTrue(count <=0, "控制记录已存在");

        BatchPoints batchPoints = BatchPoints.database(config.getInfluxDatabase()).build();
        request.getSensorIDList().forEach(item -> {
            Point.Builder builder = Point.measurement(SluiceLog.TABLE);
            builder.addField(SluiceLog.USER_ID, request.getUserID());
            Optional.ofNullable(request.getRunningSta()).ifPresent(e -> builder.addField(SluiceLog.RUNNING_STA, e));
            Optional.ofNullable(request.getSoftware()).ifPresent(e -> builder.addField(SluiceLog.SOFTWARE, e));
            Optional.ofNullable(request.getHardware()).ifPresent(e -> builder.addField(SluiceLog.HARDWARE, e));
            Optional.ofNullable(request.getMsg()).ifPresent(e -> builder.addField(SluiceLog.MSG, e));
            Optional.ofNullable(request.getLogLevel()).ifPresent(e -> builder.addField(SluiceLog.LOG_LEVEL, e));
            batchPoints.point(builder.tag(DbConstant.SENSOR_ID_TAG, item.toString())
                    .time(request.getTime().toEpochSecond(ZoneOffset.of("+8")), TimeUnit.SECONDS).build());
        });
        influxDb.write(batchPoints);
        return request.getSensorIDList().stream().map(e -> Long.parseLong(request.getTime()
                .toEpochSecond(ZoneOffset.of("+8")) + StrUtil.EMPTY + e)).toList();
    }

    @Override
    public PageUtil.Page<ControlRecord> controlRecordPage(BaseSluiceQuery request) {
        if (request.getProjectIDs().isEmpty()) {
            return PageUtil.Page.empty();
        }

        if (StringUtils.hasText(request.getKeyword())) {
            //按渠道名称模糊查询
            List<Tuple3<Collection<String>, String, Boolean>> props = List.of(Tuples.of(List.of(CANAL_NAME), request.getKeyword(), Boolean.TRUE));
            List<Integer> canal_res = propertyMapper.queryPidByProps(request.getProjectIDs(), PropertySubjectType.Project, props);

            //按项目名称(水闸名称)模糊查询
            List<Integer> name_res = projectInfoMapper.selectList(Wrappers.<TbProjectInfo>lambdaQuery()
                    .in(TbProjectInfo::getID, request.getProjectIDs())
                    .like(TbProjectInfo::getProjectName, request.getKeyword())
                    .select(TbProjectInfo::getID)).stream().map(TbProjectInfo::getID).distinct().toList();

            //整合结果
            if (!canal_res.isEmpty() || !name_res.isEmpty()) {
                request.setProjectIDs(CollUtil.union(canal_res, name_res));
            } else {
                return PageUtil.Page.empty();
            }
        }

        //将项目ID转换为传感器ID
        List<String> sensorIds = sensorMapper.selectList(Wrappers.<TbSensor>lambdaQuery()
                .in(TbSensor::getProjectID, request.getProjectIDs())
                .eq(TbSensor::getMonitorType, SluiceLog.MONITOR_TYPE)
                .select(TbSensor::getID)).stream().map(e -> e.getID().toString()).distinct().toList();

        //分页查询
        SimpleQuery query = SimpleQuery.of(SluiceLog.TABLE).in(DbConstant.SENSOR_ID_TAG, sensorIds).orderByDesc(DbConstant.TIME_FIELD);
        Optional.ofNullable(request.getControlType()).ifPresent(e -> query.eq(SluiceLog.HARDWARE, e.getDeviceCode()));

        Long count = query.count(influxDb, SluiceLog.HARDWARE);
        int totalPage = PageUtil.totalPage(count.intValue(), request.getPageSize());

        List<SluiceLog> data = query.select(SimpleQuery.SQLSymbol.WILDCARD)
                .limit((request.getCurrentPage() - 1) * request.getPageSize(), request.getPageSize())
                .query(influxDb, SluiceLog.class);

        if (data.isEmpty()) {
            return PageUtil.Page.empty();
        }

        //获取字典
        Map<Integer, TbSensor> sensorMap = getSensorDict(data, SluiceLog::getSid);
        Map<Integer, Map<String, String>> propMap = getPropDict(sensorMap.values(), TbSensor::getProjectID,
                Set.of(CANAL_NAME, SLUICE_TYPE, MANAGE_UNIT));
        Map<Integer, String> projectMap = getProjectDict(sensorMap.values(), TbSensor::getProjectID);
        Map<Integer, String> userMap = getUserDict(data, SluiceLog::getUserID);

        //处理结果
        List<ControlRecord> list = data.stream().map(e -> {
            ControlRecord record = new ControlRecord();
            //由于 influxdb 不存在id，通过 sid 和 time 确立唯一；返回的id为 时间戳+sid
            String id = e.getTime().toEpochSecond(ZoneOffset.of("+8")) + StrUtil.EMPTY + e.getSid();
            record.setId(Long.parseLong(id));
            Optional.ofNullable(sensorMap.get(e.getSid())).ifPresent(s -> {
                record.setProjectID(s.getProjectID());
                Optional.ofNullable(projectMap.get(s.getProjectID())).ifPresent(p -> {
                    record.setProjectName(p);
                    Optional.ofNullable(propMap.get(s.getProjectID())).ifPresent(prop -> {
                        record.setCanal(prop.get(CANAL_NAME));
                        record.setSluiceType(prop.get(SLUICE_TYPE));
                        record.setMmUnit(prop.get(MANAGE_UNIT));
                    });
                });
                record.setGateID(s.getID());
                record.setGateName(s.getAlias());
                record.setControlType(ControlType.formDeviceCode(e.getHardware()));
                record.setActionType(e.getSoftware());
                record.setRunningSta(e.getRunningSta());
                Optional.ofNullable(e.getUserID()).ifPresent(u -> {
                    record.setOperationUserID(u);
                    record.setOperationUser(userMap.get(u));
                });

            });

            record.setOperationTime(e.getTime());
            record.setProjectID(sensorMap.get(e.getSid()).getProjectID());
            return record;
        }).toList();
        return new PageUtil.Page<>(totalPage, list, count);

    }


    @Override
    public PageUtil.Page<Sluice> sluicePage(QuerySluicePageRequest request) {

        //渠道、水闸类型、管理单位过滤
        List<Tuple3<Collection<String>, String, Boolean>> props = new ArrayList<>();
        Optional.ofNullable(request.getKeyword()).filter(e -> !e.isBlank()).ifPresent(e ->
                props.add(Tuples.of(List.of(CANAL_NAME), request.getKeyword(), Boolean.TRUE)));
        Optional.ofNullable(request.getSluiceType()).filter(e -> !e.isBlank()).ifPresent(e ->
                props.add(Tuples.of(List.of(SLUICE_TYPE), request.getSluiceType(), Boolean.FALSE)));
        Optional.ofNullable(request.getManageUnit()).filter(e -> !e.isBlank()).ifPresent(e ->
                props.add(Tuples.of(List.of(MANAGE_UNIT), request.getManageUnit(), Boolean.FALSE)));
        if (!request.getProjectIDs().isEmpty() && !props.isEmpty()) {
            List<Integer> result = propertyMapper.queryPidByProps(request.getProjectIDs(), PropertySubjectType.Project, props);
            if (StrUtil.isNotBlank(request.getSluiceType()) || StrUtil.isNotBlank(request.getManageUnit())) {
                request.setProjectIDs(result);
            } else if (!result.isEmpty()) {
                request.setProjectIDs(result);
            }
        }

        //控制方式过滤
        if (!request.getProjectIDs().isEmpty() && request.getControlType() != null) {
            List<Integer> sids = SimpleQuery.of(SluiceStatus.TABLE)
                    .eq(SluiceStatus.HARDWARE, request.getControlType().getDeviceCode())
                    .column(influxDb, DbConstant.SENSOR_ID_TAG, Integer.class);
            if (sids == null || sids.isEmpty()) {
                return PageUtil.Page.empty();
            }
            request.getSensorList().addAll(sids);
        }


        //查询项目信息
        IPage<Integer> page = sensorMapper.sluicePage(new Page<>(request.getCurrentPage(), request.getPageSize()), request);
        if (page.getRecords().isEmpty()) {
            return PageUtil.Page.empty();
        }
        List<Sluice> data = sensorMapper.listSluice(page.getRecords());


        //获取字典
        Map<Integer, Map<String, String>> propMap = getPropDict(data, Sluice::getProjectID,
                Set.of(CANAL_NAME, SLUICE_TYPE, MANAGE_UNIT, SLUICE_HOLE_NUM));

        //采集传感器（水情数据）
        List<Gate> dataGates = data.stream().map(e -> e.getGates().stream()
                        .filter(i -> SluiceData.MONITOR_TYPE.equals(i.getMonitorType())).findFirst().orElse(null))
                .filter(Objects::nonNull).toList();
        Map<Integer, SluiceData> dataMap;
        Map<Integer, Integer> videoMGMap;
        if (!dataGates.isEmpty()) {
            dataMap = SimpleQuery.of(SluiceData.TABLE)
                    .in(DbConstant.SENSOR_ID_TAG, dataGates.stream().map(e -> e.getId().toString()).distinct().toList())
                    .orderByDesc(DbConstant.TIME_FIELD).groupBy(DbConstant.SENSOR_ID_TAG)
                    .limit(1).query(influxDb, SluiceData.class).stream().collect(Collectors.toMap(SluiceData::getSid, e -> e));

            videoMGMap = monitorGroupPointMapper.selectList(Wrappers.<TbMonitorGroupPoint>lambdaQuery()
                            .in(TbMonitorGroupPoint::getMonitorPointID, dataGates.stream().map(Gate::getMonitorPointID).distinct().toList()))
                    .stream().collect(Collectors.toMap(TbMonitorGroupPoint::getMonitorPointID, TbMonitorGroupPoint::getMonitorGroupID));
        } else {
            dataMap = Map.of();
            videoMGMap = Map.of();
        }

        //状态传感器（闸门状态）
        List<Gate> statusGates = data.stream().flatMap(e -> e.getGates().stream())
                .filter(e -> SluiceStatus.MONITOR_TYPE.equals(e.getMonitorType())).toList();
        Map<Integer, SluiceStatus> statusMap;
        if (!statusGates.isEmpty()) {
            statusMap = SimpleQuery.of(SluiceStatus.TABLE)
                    .in(DbConstant.SENSOR_ID_TAG, statusGates.stream().map(e -> e.getId().toString()).distinct().toList())
                    .orderByDesc(DbConstant.TIME_FIELD).groupBy(DbConstant.SENSOR_ID_TAG)
                    .limit(1).query(influxDb, SluiceStatus.class).stream().collect(Collectors.toMap(SluiceStatus::getSid, e -> e));
        } else {
            statusMap = Map.of();
        }

        //组装数据
        data.forEach(item -> {
            item.getGates().stream().filter(e -> SluiceData.MONITOR_TYPE.equals(e.getMonitorType())).findFirst().ifPresent(sensor -> {
                Optional.ofNullable(dataMap.get(sensor.getId())).ifPresent(s -> {
                    item.setFrontWaterLevel(s.getFrontwater());
                    item.setBackWaterLevel(s.getAfterwater());
                    item.setFlowRate(s.getFlowRate());
                    item.setLastCollectTime(s.getTime());
                });

                TbMonitorPoint point = monitorPointMapper.selectOne(Wrappers.<TbMonitorPoint>lambdaQuery()
                        .eq(TbMonitorPoint::getID, sensor.getMonitorPointID())
                        .eq(TbMonitorPoint::getMonitorType, SluiceData.MONITOR_TYPE)
                        .select(TbMonitorPoint::getMonitorItemID));
                Optional.ofNullable(point).ifPresent(p ->
                        item.setWaterData(new Sluice.WaterData(SluiceData.MONITOR_TYPE, p.getMonitorItemID(),
                                List.of(sensor.getMonitorPointID()))));
                Optional.ofNullable(videoMGMap.get(sensor.getMonitorPointID())).ifPresent(item::setVideoMonitorGroupID);
            });

            //闸门列表
            List<Gate> list = item.getGates().stream()
                    .filter(i -> SluiceStatus.MONITOR_TYPE.equals(i.getMonitorType()))
                    .peek(sensor -> Optional.ofNullable(statusMap.get(sensor.getId())).ifPresent(s -> {
                        sensor.setOpenStatus(s.getGateSta());
                        sensor.setControlType(ControlType.formDeviceCode(s.getHardware()));
                    })).toList();
            item.setGates(list);
            item.setOpenStatus(list.stream().anyMatch(i -> i.getOpenStatus() == 1) ? 1 : 0);

            Optional.ofNullable(propMap.get(item.getProjectID())).ifPresent(p -> {
                item.setCanal(p.get(CANAL_NAME));
                item.setSluiceType(p.get(SLUICE_TYPE));
                item.setManageUnit(p.get(MANAGE_UNIT));
                item.setSluiceHoleNum(MapUtil.getInt(p, SLUICE_HOLE_NUM));
            });
        });
        return new PageUtil.Page<>(page.getPages(), data, page.getTotal());
    }


    @Override
    public void sluiceControl(SluiceControlRequest request) {
        ControlActionKind kind = request.getActionKind();
        ControlActionType type = request.getActionType();
        BatchDispatchRequest dispatchRequest = new BatchDispatchRequest();
        Map<Integer, Tuple2<String, Integer>> dict = getGateIotDeviceMap(request.getProjectID(), request.getGateID());
        if (request.getGateID() == null) {
            //一键控制
            List<SluiceStatus> gates = SimpleQuery.of(SluiceStatus.TABLE)
                    .in(DbConstant.SENSOR_ID_TAG, dict.keySet())
                    //全开则查询当前状态为关闭的闸门，反之亦然
                    .eq(SluiceStatus.GATE_STA, OPEN.equals(kind) ? 0 : 1)
                    .orderByDesc(DbConstant.TIME_FIELD)
                    .groupBy(DbConstant.SENSOR_ID_TAG).limit(1).query(influxDb, SluiceStatus.class);
            if (!gates.isEmpty()) {
                List<BatchDispatchRequest.RawCmd> list = gates.stream().filter(e -> dict.containsKey(e.getSid()))
                        .map(e -> buildRawCmd(dict.get(e.getSid()), builder -> builder
                                .type(kind.getCode())
                                .crackLevel(OPEN.equals(kind) ? e.getGateOpenMax() : 0))).toList();
                dispatchRequest.setRawCmdList(list);
            }
        } else {
            Assert.isTrue(dict.containsKey(request.getGateID()), "闸门不存在");
            SluiceStatus gate = SimpleQuery.of(SluiceStatus.TABLE).eq(DbConstant.SENSOR_ID_TAG, request.getGateID().toString())
                    .orderByDesc(DbConstant.TIME_FIELD).limit(1).row(influxDb, SluiceStatus.class);

            org.springframework.util.Assert.isTrue(gate != null, "闸门不在线");
            BatchDispatchRequest.RawCmd rawCmd = buildRawCmd(dict.get(request.getGateID()), builder -> {
                switch (kind) {
                    case STOP, RISE, FALL -> builder.type(kind.getDeviceCode()).motorDir(kind.getDeviceCode());
                    case AUTO -> {
                        builder.type(type.getDeviceCode());
                        switch (type) {
                            case CONSTANT_WATER_LEVEL -> builder.waterLevel(request.getTarget().getWaterLevel());
                            case CONSTANT_FLOW -> {
                                //TODO: 设备暂不支持此模式
                            }
                            case CONSTANT_SLUICE_LEVEL -> builder.crackLevel(request.getTarget().getGateDegree());
                            case TIME_CONTROL -> builder.startTime(request.getTarget().getOpenTime())
                                    .fixedTime(request.getTarget().getDuration());
                            case TIME_PERIOD_CONTROL -> builder.startTime(request.getTarget().getBeginTime())
                                    .stopTime(request.getTarget().getEndTime());
                            case TOTAL_CONTROL -> builder.totalFlow(request.getTarget().getTotalFlow());
                        }
                    }
                    case OPEN -> {
                        Assert.isTrue(gate.getGateSta() == 1, "闸门已开启");
                        builder.type(kind.getDeviceCode()).crackLevel(gate.getGateOpenMax());
                    }
                    case CLOSE -> {
                        Assert.isTrue(gate.getGateSta() == 0, "闸门已关闭");
                        builder.type(kind.getDeviceCode()).crackLevel(0D);
                    }
                }
            });
            dispatchRequest.setRawCmdList(List.of(rawCmd));
        }
        dispatchRequest.setUserID(CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        executeControl(dispatchRequest);
    }

    @Override
    public SluiceInfo sluiceSingle(SingleSluiceRequest request) {
        SluiceInfo result = new SluiceInfo();
        List<TbSensor> list = sensorMapper.selectList(Wrappers.<TbSensor>lambdaQuery()
                .eq(TbSensor::getProjectID, request.getProjectID())
                .in(TbSensor::getMonitorType, SluiceStatus.MONITOR_TYPE, SluiceData.MONITOR_TYPE)
                .select(TbSensor::getID, TbSensor::getMonitorType, TbSensor::getAlias));

        list.stream().filter(e -> SluiceData.MONITOR_TYPE.equals(e.getMonitorType())).findFirst().ifPresent(sensor -> {
            //采集传感器 (水情数据)
            SluiceData data = SimpleQuery.of(SluiceData.TABLE).eq(DbConstant.SENSOR_ID_TAG, sensor.getID().toString())
                    .orderByDesc(DbConstant.TIME_FIELD).limit(1).row(influxDb, SluiceData.class);
            Optional.ofNullable(data).ifPresent(d -> {
                result.setFrontWaterLevel(d.getFrontwater());
                result.setBackWaterLevel(d.getAfterwater());
                result.setFlowRate(d.getFlowRate());
                result.setFlowTotal(d.getTotalFlow());
            });
        });

        if (list.stream().anyMatch(e -> SluiceStatus.MONITOR_TYPE.equals(e.getMonitorType()))) {
            Map<Integer, SluiceStatus> statusMap = SimpleQuery.of(SluiceStatus.TABLE)
                    .in(DbConstant.SENSOR_ID_TAG, list.stream().filter(e -> SluiceStatus.MONITOR_TYPE.equals(e.getMonitorType()))
                            .map(e -> e.getID().toString()).distinct().toList())
                    .groupBy(DbConstant.SENSOR_ID_TAG)
                    .orderByDesc(DbConstant.TIME_FIELD).limit(1)
                    .query(influxDb, SluiceStatus.class)
                    .stream().collect(Collectors.toMap(SluiceStatus::getSid, e -> e));

            List<GateInfo> gates = list.stream().filter(e -> statusMap.containsKey(e.getID()))
                    .map(e -> {
                        SluiceStatus status = statusMap.get(e.getID());
                        GateInfo gate = new GateInfo();
                        gate.setId(e.getID());
                        gate.setAlias(e.getAlias());
                        gate.setControlType(ControlType.formDeviceCode(status.getHardware()));
                        gate.setOpenStatus(status.getGateSta());
                        gate.setOpenDegree(status.getGateOpen());
                        gate.setMaxOpenDegree(status.getGateOpenMax());
                        gate.setPowerCurrent(status.getGateCurrent());
                        gate.setPowerVoltage(status.getGateVolt());
                        gate.setRunningState(status.getMotorSta());
                        gate.setLimitSwSta(status.getLimitSwSta());
                        return gate;
                    }).toList();
            result.setGates(gates);
            statusMap.clear();
        }

        List<PropWithValue> props = propertyMapper.queryPropByPids(Set.of(request.getProjectID()),
                PropertySubjectType.Project, List.of(MAX_FLOW, MAX_WATER_LEVEL));

        Optional.ofNullable(props).ifPresent(e -> e.forEach(p -> {
            switch (p.getName()) {
                case MAX_FLOW -> result.setMaxFlowRate(Convert.toDouble(p.getValue()));
                case MAX_WATER_LEVEL -> result.setMaxBackWaterLevel(Convert.toDouble(p.getValue()));
            }
        }));
        return result;
    }

    @Override
    public List<SluiceSimple> listSluiceSimple(ListSluiceRequest request) {

        LambdaQueryWrapper<TbSensor> query = Wrappers.<TbSensor>lambdaQuery()
                .in(TbSensor::getProjectID, request.getProjectList())
                .eq(TbSensor::getMonitorType, SluiceStatus.MONITOR_TYPE)
                .select(TbSensor::getProjectID);

        if (request.getControlType() != null) {
            List<Integer> sid = SimpleQuery.of(SluiceStatus.TABLE)
                    .eq(SluiceStatus.HARDWARE, request.getControlType().getDeviceCode())
                    .groupBy(DbConstant.SENSOR_ID_TAG)
                    .orderByDesc(DbConstant.TIME_FIELD).limit(1).column(influxDb, DbConstant.SENSOR_ID_TAG, Integer.class);
            if (sid.isEmpty()) {
                return List.of();
            }
            query.in(TbSensor::getID, sid);
        }

        Collection<Integer> pidList = sensorMapper.selectList(query)
                .stream().map(TbSensor::getProjectID).distinct().toList();

        if (pidList.isEmpty()) {
            return List.of();
        }

        //查询项目属性
        List<String> propNames = List.of(CANAL_NAME, SLUICE_TYPE, MANAGE_UNIT, SLUICE_HOLE_NUM);
        Map<Integer, Map<String, String>> propMap = propertyMapper.queryPropByPids(pidList, PropertySubjectType.Project, propNames)
                .stream().collect(Collectors.groupingBy(PropWithValue::getProjectID,
                        Collectors.toMap(PropWithValue::getName, PropWithValue::getValue)));

        return projectInfoMapper.selectList(Wrappers.<TbProjectInfo>lambdaQuery()
                        .in(TbProjectInfo::getID, pidList)
                        .select(TbProjectInfo::getID, TbProjectInfo::getProjectName))
                .stream().map(e -> SluiceSimple.builder()
                        .projectID(e.getID())
                        .projectName(e.getProjectName())
                        .canal(MapUtil.getStr(propMap.get(e.getID()), CANAL_NAME))
                        .sluiceType(MapUtil.getStr(propMap.get(e.getID()), SLUICE_TYPE))
                        .manageUnit(MapUtil.getStr(propMap.get(e.getID()), MANAGE_UNIT))
                        .sluiceHoleNum(MapUtil.getInt(propMap.get(e.getID()), SLUICE_HOLE_NUM))
                        .build()).toList();
    }

    @Override
    public List<String> listSluiceManageUnit(ListSluiceManageUnitRequest request) {
        //查询所有水闸项目ID
        List<Integer> pidList = sensorMapper.selectList(Wrappers.<TbSensor>lambdaQuery()
                .in(TbSensor::getProjectID, request.getProjectList())
                .in(TbSensor::getMonitorType, SluiceStatus.MONITOR_TYPE, SluiceData.MONITOR_TYPE)
                .select(TbSensor::getProjectID)).stream().map(TbSensor::getProjectID).distinct().toList();
        if (!pidList.isEmpty()) {
            return propertyMapper.queryPropByPids(pidList, PropertySubjectType.Project, List.of("管理单位"))
                    .stream().filter(e -> MANAGE_UNIT.equals(e.getName())).map(PropWithValue::getValue).distinct().toList();
        }
        return List.of();
    }

    @Override
    public List<GateSimple> listSluiceGate(ListSluiceGateRequest request) {
        return sensorMapper.selectList(Wrappers.<TbSensor>lambdaQuery()
                        .in(TbSensor::getProjectID, request.getProjectList())
                        .in(TbSensor::getMonitorType, SluiceLog.MONITOR_TYPE)
                        .select(TbSensor::getID, TbSensor::getAlias)).stream()
                .map(e -> new GateSimple(e.getID(), e.getAlias())).distinct().toList();
    }

    /**
     * 获取闸门和其对应物联网设备信息字典
     *
     * @param projectID 项目ID (水闸ID)
     * @param sensorID  传感器ID (闸门ID)
     * @return 结果 key: 闸门ID value: Tuple2<String, Integer> 物联网设备Token、电机序号
     */
    private Map<Integer, Tuple2<String, Integer>> getGateIotDeviceMap(Integer projectID, Integer sensorID) {
        List<Tuple3<Integer, String, String>> list = sensorMapper.queryGateIotToken(projectID, sensorID);
        if (sensorID != null) {
            Assert.isTrue(list.size() == 1, "闸门不存在");
        } else {
            Assert.notEmpty(list, "闸门不存在");
        }

        ResultWrapper<List<DeviceSimple>> wrapper = iotService
                .queryDeviceSimpleByUniqueTokens(QueryDeviceSimpleByUniqueTokensParam.builder()
                        .uniqueTokens(list.stream().map(Tuple3::getT3).collect(Collectors.toSet())).build());
        if (wrapper.getData() != null && CollUtil.isNotEmpty(wrapper.getData())) {
            Map<String, DeviceSimple> dict = wrapper.getData().stream()
                    .collect(Collectors.toMap(DeviceSimple::getUniqueToken, v -> v));
            return list.stream().filter(e -> dict.containsKey(e.getT3()))
                    .filter(e -> NumberUtil.isInteger(e.getT2()))
                    .map(e -> {
                        DeviceSimple device = dict.get(e.getT3());
                        return Map.entry(e.getT1(), Tuples.of(device.getDeviceSN(), Integer.parseInt(e.getT2())));
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        return Map.of();
    }

    /**
     * 加锁下发闸门控制指令<br/>
     * TODO: 可以考虑切换为分布式锁
     *
     * @param request {@link BatchDispatchRequest}
     */
    private void executeControl(BatchDispatchRequest request) {
        if (CollUtil.isNotEmpty(request.getRawCmdList())) {
            try {
                if (controlLock.tryLock(1, TimeUnit.SECONDS)) {
                    //调用物联网接口下发指令
                    ResultWrapper<List<TokenAndMsgID>> wrapper = iotService.batchDispatchCmd(request);
                    //释放锁再检查结果
                    controlLock.unlock();
                    if (!wrapper.apiSuccess() || wrapper.getData().isEmpty()) {
                        throw new CustomBaseException(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR.toInt(), wrapper.getMsg());
                    }
                }
            } catch (InterruptedException e) {
                throw new CustomBaseException(ResultCode.REQUEST_TIME_OUT.toInt(), "其他用户正在操作，请稍后再试");
            }
        }
    }

    /**
     * 构建透传指令
     *
     * @param device   物联网设备信息
     * @param consumer 指令构建器
     * @return {@link BatchDispatchRequest.RawCmd}
     */
    private BatchDispatchRequest.RawCmd buildRawCmd(Tuple2<String, Integer> device,
                                                    Consumer<ControlCmd.ControlCmdBuilder> consumer) {
        ControlCmd.ControlCmdBuilder builder = ControlCmd.builder();
        consumer.accept(builder);
        return new BatchDispatchRequest.RawCmd(device.getT1(), builder.index(device.getT2()).build().toRaw());
    }

    protected <T> Map<Integer, TbSensor> getSensorDict(Collection<T> data, Function<T, Integer> sidFunc) {
        List<Integer> sidSet = data.stream().map(sidFunc).distinct().toList();
        if (!sidSet.isEmpty()) {
            return sensorMapper.selectList(Wrappers.<TbSensor>lambdaQuery()
                            .in(TbSensor::getID, sidSet)
                            .select(TbSensor::getID, TbSensor::getProjectID, TbSensor::getAlias))
                    .stream().collect(Collectors.toMap(TbSensor::getID, e -> e));
        }
        return Map.of();
    }

    protected <T> Map<Integer, Map<String, String>> getPropDict(Collection<T> data, Function<T, Integer> pidFunc,
                                                                Collection<String> propNames) {
        Set<Integer> pidSet = data.stream().map(pidFunc).collect(Collectors.toSet());
        if (!pidSet.isEmpty()) {
            return propertyMapper.queryPropByPids(pidSet, PropertySubjectType.Project, propNames)
                    .stream().collect(Collectors.groupingBy(PropWithValue::getProjectID,
                            Collectors.toMap(PropWithValue::getName, PropWithValue::getValue)));
        }
        return Map.of();
    }

    protected <T> Map<Integer, String> getProjectDict(Collection<T> data, Function<T, Integer> pidFunc) {
        List<Integer> pidSet = data.stream().map(pidFunc).filter(Objects::nonNull).distinct().toList();
        if (!pidSet.isEmpty()) {
            return projectInfoMapper.selectList(Wrappers.<TbProjectInfo>lambdaQuery()
                            .in(TbProjectInfo::getID, pidSet)
                            .select(TbProjectInfo::getID, TbProjectInfo::getProjectName))
                    .stream().collect(Collectors.toMap(TbProjectInfo::getID, TbProjectInfo::getProjectName));
        }
        return Map.of();
    }

    protected <T> Map<Integer, String> getUserDict(Collection<T> data, Function<T, Integer> uidFunc) {
        List<Integer> uidSet = data.stream().map(uidFunc).filter(Objects::nonNull).distinct().toList();
        if (!uidSet.isEmpty()) {
            ResultWrapper<List<UserIDName>> wrapper = userService.queryUserIDName(new QueryUserIDNameParameter(uidSet), config.getAuthAppKey(), config.getAuthAppSecret());
            wrapper.checkApi();
            return Optional.ofNullable(wrapper.getData()).orElse(List.of()).stream()
                    .collect(Collectors.toMap(UserIDName::getUserID, UserIDName::getUserName));
        }
        return Map.of();
    }
}