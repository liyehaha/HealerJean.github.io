package com.healerjean.proj.service.identity;


import com.healerjean.proj.dto.user.LoginUserDTO;

/**
 * @ClassName IdentityService
 * @Author HealerJean
 * @Date 2019/6/3 20:54
 * @Description 用户认证信息接口
 */
public interface IdentityService {

    /**
     * 根据用户ID获取当前登陆用户信息
     * @param userId
     * @return
     */
    LoginUserDTO getUserInfo(Long userId);
}
