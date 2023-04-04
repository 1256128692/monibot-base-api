package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.enums.SensorDataSource;

public record DataSource(SensorDataSource type, Integer value) {
}