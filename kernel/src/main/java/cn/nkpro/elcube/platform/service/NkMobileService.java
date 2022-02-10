///*
// * This file is part of ELCube.
// *
// * ELCube is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Affero General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * ELCube is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Affero General Public License for more details.
// *
// * You should have received a copy of the GNU Affero General Public License
// * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
// */
//package cn.nkpro.elcube.platform.service;
//
//import cn.nkpro.elcube.platform.model.MobileOfficeAccProperties;
//import com.alibaba.fastjson.JSONObject;
//
//import java.util.Map;
//
///**
// * @Author: wf
// * @Description 移动端服务接口
// * @date 2022/1/14 14:52
// */
//public interface NkMobileService {
//
//    /**
//     * 发送手机验证码
//     * @param phone
//     * @return
//     */
//    String sendVerificationCode(String phone);
//
//    Map<String,Object> appBind(String nkApp, String phone, String verCode, String openId, String appleId);
//
//    /**
//      *@description:查询openId
//      *@Author:YF
//      *@date:2022/1/27 10:52
//      *@param:[mobileOfficeAccBo]
//      *@return:com.alibaba.fastjson.JSONObject
//     **/
//    JSONObject findOpenId(MobileOfficeAccProperties mobileOfficeAccProperties);
//
//    /**
//      *@description:微信登录小程序获取access_token
//      *@Author:YF
//      *@date:2022/1/27 10:53
//      *@param:
//      *@return:
//     **/
//    String queryToken(MobileOfficeAccProperties mobileOfficeAccProperties);
//
//
//    /**
//      *@description:获取微信手机号
//      *@Author:YF
//      *@date:2022/1/27 11:02
//      *@param:[code]
//      *@return:com.alibaba.fastjson.JSONObject
//     **/
//    JSONObject getPhoneInfo(String code);
//
//
//    /**
//      *@description:微信手机号一键登录小程序
//      *@Author:YF
//      *@date:2022/1/28 11:24
//      *@param:[phone]
//      *@return:java.util.Map<java.lang.String,java.lang.Object>
//     **/
//    Map<String,Object> weChatLogin(String phone);
//}
