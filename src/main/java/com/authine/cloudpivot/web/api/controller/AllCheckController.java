package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.entity.UserInfoByCheck;
import com.authine.cloudpivot.web.api.service.AllCheckService;
import com.authine.cloudpivot.web.api.utils.DingDingUtil;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import com.dingtalk.api.response.OapiAttendanceGetattcolumnsResponse;
import com.dingtalk.api.response.OapiAttendanceGetcolumnvalResponse;
import com.dingtalk.api.response.OapiSmartworkHrmEmployeeListResponse;
import com.dingtalk.api.response.OapiUserGetResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: weiyao
 * @time: 2020/8/19
 * @Description: 全员考评
 */
@RestController
@Slf4j
@RequestMapping("/controller/allCheck")
public class AllCheckController extends BaseController {

    @Autowired
    AllCheckService allCheckService;

    /**
     * 查询支队，大队等部门
     */
    @GetMapping("/getDeptName")
    public ResponseResult<Object> getDeptName(String name) {
        if (StringUtils.isNotEmpty(name)) {
            List<Map<String,String>> list=allCheckService.getDeptListByName(name);
            return this.getErrResponseResult(list, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
        } else {
            return this.getErrResponseResult(null, 404L, "没有名称");
        }
    }

    /**
     * 根据部门查询用户信息
     */
    @GetMapping("/getUserByDept")
    public ResponseResult<Object> getUserByDept(String deptId,String userId) {
            List<UserInfoByCheck> list=allCheckService.getUserListByDept(deptId,userId);
            return this.getErrResponseResult(list, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    /**
     * 返回用户详细信息年龄，职务
     */
    @GetMapping("/getUserDetailInfoByDd")
    public ResponseResult<Object> getUserDetailInfoByDd(String dduserId) {
        if (StringUtils.isNotEmpty(dduserId)) {
            Map<String,Object> map=new HashMap<>();
            //获取用户详情，部门信息
            OapiUserGetResponse userDetail=DingDingUtil.getUserDetail(dduserId,DingDingUtil.getToken());
            map.put("position",userDetail.getPosition());//职务
            map.put("age","");//职务

            OapiSmartworkHrmEmployeeListResponse us = DingDingUtil.getEmployeeInfo(dduserId, DingDingUtil.getToken(),"sys02-birthTime");
            List<OapiSmartworkHrmEmployeeListResponse.EmpFieldInfoVO> result = us.getResult();
            String birthday="";
            if (result.get(0).getFieldList().size() > 0) {
                List<OapiSmartworkHrmEmployeeListResponse.EmpFieldVO>  lis=result.get(0).getFieldList();
                for(OapiSmartworkHrmEmployeeListResponse.EmpFieldVO li :lis){
                    //sys02-birthTime,sys02-sexType,sys02-certNo
                    if("sys02-birthTime".equals(li.getFieldCode())){
                        birthday=li.getLabel();
                        if(birthday !=null && birthday.length()>4){
                           int byear=Integer.parseInt(birthday.substring(0,4));
                            Calendar cal = Calendar.getInstance();
                            int  age = cal.get(Calendar.YEAR)-byear;//当前年份

                            map.put("age",age);
                        }
                    }
                }
            }

            return this.getErrResponseResult(map, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
        }else{
            return this.getErrResponseResult(null, 404L, "钉钉Id为空");
        }

    }

    /**
     * 获取考勤数据
     * @param type:7代表过去一周 30，代表最近一个月（默认）
     * @param dduserId 用户钉钉Id
     */
    @GetMapping("/getClockInfo")
    public ResponseResult<Object> getClockInfo(String dduserId,String type) {
        if (StringUtils.isNotEmpty(dduserId)) {
            Map<String,Object> map = new HashMap<>();
            String token=DingDingUtil.getToken();
            OapiAttendanceGetattcolumnsResponse ros=DingDingUtil.getAttcolumns(token);
            List<OapiAttendanceGetattcolumnsResponse.ColumnForTopVo> columList=ros.getResult().getColumns();
          /*  应出勤天数 ==Id为：82834769
            补卡次数 ==Id为：82834770 -
            出勤班次 ==Id为：82834771
            出勤天数 ==Id为：82834772 -
            休息天数 ==Id为：82834773 -
            工作时长 ==Id为：82834774
            迟到次数 ==Id为：82834775 -
            迟到时长 ==Id为：82834776
            严重迟到次数 ==Id为：82834777 -
            严重迟到时长 ==Id为：82834778
            旷工迟到天数 ==Id为：82834779
            早退次数 ==Id为：82834780
            早退时长 ==Id为：82834781
            上班缺卡次数 ==Id为：82834782
            下班缺卡次数 ==Id为：82834783
            旷工天数 ==Id为：82834784
            出差时长 ==Id为：82834785
            外出时长 ==Id为：82834786
            考勤结果 ==Id为：82834792
            班次 ==Id为：82834793   */
            String ids="82834769,82834770,82834771,82834772,82834773,82834774,82834775,82834776,82834777," +
                    "82834778,82834779,82834780,82834781,82834782,82834783,82834784,82834785,82834786,82834792";
//            for(OapiAttendanceGetattcolumnsResponse.ColumnForTopVo id:columList){
//               // ids=ids+id.getId()+",";
//                System.out.println("=====name:==="+id.getName()+" ==Id为："+id.getId());
//            }
           // ids=ids.substring(0,ids.length()-1);
            Calendar da = Calendar.getInstance();
            //过去七天
            da.setTime(new Date());
            if(StringUtils.isNotEmpty(type) && "7".equals(type)){
                da.add(Calendar.DATE, - 7);
            }else{
                da.add(Calendar.DATE, - 30);
            }

            Date startDate = da.getTime();
            OapiAttendanceGetcolumnvalResponse cloList=DingDingUtil.getAttcolumnInfo(dduserId,ids,token,startDate,new Date());
            if(cloList.getResult() !=null && cloList.getResult().getColumnVals().size()>0){
                List<OapiAttendanceGetcolumnvalResponse.ColumnValForTopVo> vo= cloList.getResult().getColumnVals();
                for(OapiAttendanceGetcolumnvalResponse.ColumnValForTopVo list :vo){
                 //   System.out.println("=====id值："+list.getColumnVo().getId());
                    if("82834772".equals(list.getColumnVo().getId().toString())){
                         Float count=0F;
                        //出勤天数
                        for(OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal valist :list.getColumnVals()){
                            count=count+Float.valueOf(valist.getValue());
                        }
                        map.put("chuqingtianshu",count);
                    }else if("82834769".equals(list.getColumnVo().getId().toString())){
                        //应出勤天数
                        Float count=0F;
                        for(OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal valist :list.getColumnVals()){
                            count=count+Float.valueOf(valist.getValue());
                        }
                        map.put("yingchuqingtianshu",count);

                    }else if("82834773".equals(list.getColumnVo().getId().toString())){
                        //休息天数
                        Float count=0F;
                        for(OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal valist :list.getColumnVals()){
                            count=count+Float.valueOf(valist.getValue());
                        }
                        map.put("xiuxitianshu",count);

                    }else if("82834770".equals(list.getColumnVo().getId().toString())){
                        //补卡次数
                        Integer count=0;
                        for(OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal valist :list.getColumnVals()){
                            count=count+Integer.valueOf(valist.getValue());
                        }
                        map.put("bukacishu",count);

                    }else if("82834775".equals(list.getColumnVo().getId().toString())){
                        //迟到次数
                        Integer count=0;
                        for(OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal valist :list.getColumnVals()){
                            count=count+Integer.valueOf(valist.getValue());
                        }
                        map.put("chidaocishu",count);

                    }else if("82834777".equals(list.getColumnVo().getId().toString())){
                        //严重迟到次数
                        Integer count=0;
                        for(OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal valist :list.getColumnVals()){
                            count=count+Integer.valueOf(valist.getValue());
                        }
                        map.put("yanzhongchidao",count);

                    }else if("82834779".equals(list.getColumnVo().getId().toString())){
                        //旷工迟到天数
                        Integer count=0;
                        for(OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal valist :list.getColumnVals()){
                            count=count+Integer.valueOf(valist.getValue());
                        }
                        map.put("kuanggongchidao",count);

                    }else if("82834780".equals(list.getColumnVo().getId().toString())){
                        //早退次数
                        Integer count=0;
                        for(OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal valist :list.getColumnVals()){
                            count=count+Integer.valueOf(valist.getValue());
                        }
                        map.put("zaotuicishu",count);

                    }else if("82834784".equals(list.getColumnVo().getId().toString())){
                        //旷工天数
                        Integer count=0;
                        for(OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal valist :list.getColumnVals()){
                            count=count+Integer.valueOf(valist.getValue());
                        }
                        map.put("kuanggongtianshu",count);

                    }else if("82834785".equals(list.getColumnVo().getId().toString())){
                        //出差时长
                        Float count=0F;
                        for(OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal valist :list.getColumnVals()){
                            count=count+Float.valueOf(valist.getValue());
                        }
                        map.put("chuchaishichang",count);

                    }else if("82834786".equals(list.getColumnVo().getId().toString())){
                        //外出时长
                        Float count=0F;
                        for(OapiAttendanceGetcolumnvalResponse.ColumnDayAndVal valist :list.getColumnVals()){
                            count=count+Float.valueOf(valist.getValue());
                        }
                        map.put("waichushichang",count);

                    }
                }
            }

            return this.getErrResponseResult(map, ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
        } else {
            return this.getErrResponseResult(null, 404L, "没有用户Id");
        }
    }


}
