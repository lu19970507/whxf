package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.dto.VehicleInfoDto;
import com.authine.cloudpivot.web.api.entity.VehicleInfo;

import java.util.List;

/**
 * 车辆信息mapper
 *
 * @author wangyong
 * @time 2020/5/11 9:17
 */
public interface CarsInfoMapper {

    /**
     * 根据消防站id获取该消防站的车辆信息
     *
     * @param stationId 消防站id
     * @return 消防站车辆信息
     * @author wangyong
     */
    VehicleInfoDto getCarsInfosByStationId(String stationId);

    /**
     * 根据父id查询消防车辆信息
     *
     * @param id 父id
     * @return 消防车辆信息
     * @author wangyong
     */
    List<VehicleInfo> getVehicleInfoByParentId(String id);

    /**
     * 根据id更新消防车的状态
     *
     * @param id     消防车id
     * @param status 消防车状态 在位 保修 出动
     * @author wangyong
     */
    void updateCarsStatusById(String id, String status);

}
