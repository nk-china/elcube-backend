/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.platform.service.impl;

import cn.nkpro.elcube.data.redis.RedisSupport;
import cn.nkpro.elcube.platform.model.MobileOfficeAccBo;
import cn.nkpro.elcube.platform.service.NkAbstractMobile;
import cn.nkpro.elcube.platform.service.NkAccountOperationService;
import cn.nkpro.elcube.platform.service.NkDocOperationService;
import cn.nkpro.elcube.security.UserAccountService;
import cn.nkpro.elcube.security.bo.UserAccountBO;
import cn.nkpro.elcube.security.bo.UserDetails;
import cn.nkpro.elcube.security.validate.NkAppLoginAuthentication;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: wf
 * @Description 移动端服务实现类
 * @date 2022/1/14 15:15
 */
public class NkMobileServiceImpl extends NkAbstractMobile {

    @Autowired@SuppressWarnings("all")
    private RedisSupport<Object> redisSupport;
    @Autowired@SuppressWarnings("all")
    private UserAccountService userAccountService;
    @Autowired@SuppressWarnings("all")
    private NkDocOperationService nkDocOperationService;
    @Autowired@SuppressWarnings("all")
    private NkAccountOperationService nkAccountOperationService;

    @Override
    public String sendVerificationCode(String phone) {
        //todo 发送验证码到手机
        String verCode = "123456";
        redisSupport.set(phone,verCode);
        return verCode;
    }

    @Override
    @Transactional
    public Map<String,Object> appBind(String nkApp, String phone, String verCode, String openId, String appleId) {
        // 校验验证码
        Object code = redisSupport.get(phone);

        if(null==code)
            throw new BadCredentialsException("验证码已过期");

        if(!Objects.equals(code, verCode)){
            throw new BadCredentialsException("验证码不正确");
        }
        UserDetails userDetails = userAccountService.getAccountByMobileTerminal(phone, openId, appleId);
        if(null == userDetails){
            // 新建用户
            UserAccountBO user = nkAccountOperationService.createAccount(phone, openId, appleId);
            userAccountService.update(user);
            // 用户授权
            nkAccountOperationService.addAccountFromGroup(user.getId());
            userDetails = userAccountService.getAccountByMobileTerminal(phone, openId, appleId);
            AbstractAuthenticationToken auth = new NkAppLoginAuthentication(
                    nkApp, phone,
                    verCode, openId,
                    appleId);
            auth.setDetails(userDetails);
            SecurityContextHolder.getContext().setAuthentication(auth);
            // 保存客户信息到单据
            Map<String,String> dataMap = new HashMap<>();
            dataMap.put("phone",phone);
            //dataMap.put("verCode",verCode);
            dataMap.put("openId",openId);
            dataMap.put("appleId",appleId);
            nkDocOperationService.createDoc(dataMap);
        }
        return userAccountService.createTokenMobileTerminal(phone, openId, appleId);
    }

    @Override
    public JSONObject findOpenId(MobileOfficeAccBo mobileOfficeAccBo) {
        /*String url = mobileOfficeAccBo.getJscode2sessionUrl();
        url += url+"?appid="+mobileOfficeAccBo.getAppid()+"&secret="+mobileOfficeAccBo.getSecret()+"&js_code="+mobileOfficeAccBo.getJsCode()
                +"&grant_type="+mobileOfficeAccBo.getGrantType();
        String resut = HttpClientUtil.sendGetRequest(url,null);*/

        String resut = "{'opendId':'1111111'}";
        JSONObject jsonObject = JSONObject.parseObject(resut);
        return jsonObject;
    }
}
