package com.healerjean.proj.util;

import com.healerjean.proj.dto.system.MenuDTO;
import com.healerjean.proj.dto.user.LoginUserDTO;
import com.healerjean.proj.dto.user.UcenterFrontMenuDTO;
import com.healerjean.proj.dto.user.UcenterFrontMenuMetaDTO;
import com.healerjean.proj.shiro.AuthConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.LinkedList;
import java.util.List;


/**
 * @author HealerJean
 * @ClassName UserUtils
 * @Date 2019-11-02  00:38.
 * @Description 用户工具
 */

@Slf4j
public class UserUtils {

    /**
     * 获取基本用户信息
     */
    public static LoginUserDTO getLoginUser() {
        Subject subject = null;
        try {
            subject = SecurityUtils.getSubject();
        } catch (UnavailableSecurityManagerException e) {
            log.debug("没有开启权限管理配置");
            return null;
        }
        if (subject != null) {
            Session session = subject.getSession();
            if (session != null) {
                Object attribute = session.getAttribute(AuthConstants.AUTH_USER);
                if (attribute != null && attribute instanceof LoginUserDTO) {
                    return (LoginUserDTO) attribute;
                }
            }
        }
        return null;
    }




    /**
     * 获取用户菜单
     */
    public static List<MenuDTO> getMenus() {
        Subject subject = null;
        try {
            subject = SecurityUtils.getSubject();
        } catch (UnavailableSecurityManagerException e) {
            log.debug("没有开启权限管理配置");
            return null;
        }
        if (subject != null) {
            Session session = subject.getSession();
            if (session != null) {
                Object attribute = session.getAttribute(AuthConstants.AUTH_MENU);
                if (attribute != null && attribute instanceof List) {
                    return (List<MenuDTO>) attribute;
                }
            }
        }
        return null;
    }

    /**
     * 递归菜单
     *
     * @param menus
     * @param result
     * @return
     */
    public static List<UcenterFrontMenuDTO> recursionMenus(List<MenuDTO> menus, List<UcenterFrontMenuDTO> result) {
        if (result == null) {
            result = new LinkedList<>();
        }
        if (menus != null) {
            for (MenuDTO menuDTO : menus) {
                UcenterFrontMenuDTO ucenterFrontMenuDTO = new UcenterFrontMenuDTO();
                List<MenuDTO> submenus = menuDTO.getSubmenus();
                if (submenus != null && !submenus.isEmpty()) {
                    List<UcenterFrontMenuDTO> ucenterFrontMenuDTOs = recursionMenus(submenus, null);
                    ucenterFrontMenuDTO.setChildren(ucenterFrontMenuDTOs);
                }
                ucenterFrontMenuDTO.setName(menuDTO.getFrontKey());
                ucenterFrontMenuDTO.setPath(menuDTO.getUrl());
                ucenterFrontMenuDTO.setSort(menuDTO.getSort() == null ? 0 : Integer.valueOf(menuDTO.getSort()));
                UcenterFrontMenuMetaDTO meta = new UcenterFrontMenuMetaDTO();
                meta.setIcon(menuDTO.getIcon());
                meta.setTitle(menuDTO.getMenuName());
                ucenterFrontMenuDTO.setMeta(meta);
                result.add(ucenterFrontMenuDTO);
            }
        }
        result.sort((o1, o2) -> {
            if (o1.getSort() == null) {
                return 1;
            }
            if (o2.getSort() == null) {
                return -1;
            }
            if (Integer.valueOf(o1.getSort()).equals(Integer.valueOf(o2.getSort()))) {
                return 0;
            } else if (Integer.valueOf(o1.getSort()) < Integer.valueOf(o2.getSort())) {
                return -1;
            }
            return 1;

        });
        return result;
    }


}
