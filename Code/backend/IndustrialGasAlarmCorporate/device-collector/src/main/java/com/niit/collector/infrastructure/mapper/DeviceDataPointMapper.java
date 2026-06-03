package com.niit.collector.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.niit.collector.infrastructure.po.DeviceDataPointPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceDataPointMapper extends BaseMapper<DeviceDataPointPO> {
}
