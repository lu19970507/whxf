package com.authine.cloudpivot.web.api.mapper;


import com.authine.cloudpivot.web.api.entity.EduTrainPaln;
import com.authine.cloudpivot.web.api.entity.StationEduTrainPaln;

import java.util.List;

/**
 * 教育训练计划mapper
 *
 * @author Ke Longhai
 * @time 2020/5/14
 */

public interface EduTrainPalnMapper {

    /**
     * 插入消防站的今日教育训练计划
     *
     * @param eduTrainPaln  今日教育训练计划
     * @author Ke Longhai
     */
    void insertStationEduTrainPaln(StationEduTrainPaln eduTrainPaln);

    /**
     * 根据消防站id获取今日教育训练计划
     *
     * @param stationId 消防站id
     * @return 今日教育训练计划
     * @author Ke Longhai
     */
    StationEduTrainPaln getStationEduTrainPalnByStationId(String stationId);

    /**
     * 更新消防站的今日教育训练计划
     *
     * @param eduTrainPaln 今日教育训练计划
     * @author Ke Longhai
     */
    void updateStationEduTrainPalnStationId(StationEduTrainPaln eduTrainPaln);

    List<EduTrainPaln> getEduTrainPalnWeek(String stationId);

}
