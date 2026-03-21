package com.gaog.weblog.admin.service;

import com.gaog.weblog.admin.model.vo.role.AddRoleReqVO;
import com.gaog.weblog.admin.model.vo.role.AssignRoleReqVO;
import com.gaog.weblog.admin.model.vo.role.DeleteRoleReqVO;
import com.gaog.weblog.admin.model.vo.role.FindRolePageListReqVO;
import com.gaog.weblog.admin.model.vo.role.FindRolePermissionsReqVO;
import com.gaog.weblog.admin.model.vo.role.FindRoleSelectListReqVO;
import com.gaog.weblog.admin.model.vo.role.UpdateRolePermissionsReqVO;
import com.gaog.weblog.admin.model.vo.role.UpdateRoleReqVO;
import com.gaog.weblog.common.utils.Response;

/**
 * Role management service interface
 *
 * @Author: gaoge
 * @Date: 2025/12/18
 * @Version: 1.0
 * @Description: Role management service for admin backend
 */
public interface AdminRoleService {

    /**
     * Create a new role
     *
     * @param addRoleReqVO add role request VO
     * @return Response
     */
    Response addRole(AddRoleReqVO addRoleReqVO);

    /**
     * Query role page list
     *
     * @param findRolePageListReqVO find role page list request VO
     * @return Response
     */
    Response findRolePageList(FindRolePageListReqVO findRolePageListReqVO);

    /**
     * Update role information
     *
     * @param updateRoleReqVO update role request VO
     * @return Response
     */
    Response updateRole(UpdateRoleReqVO updateRoleReqVO);

    /**
     * Delete role (logical delete)
     *
     * @param deleteRoleReqVO delete role request VO
     * @return Response
     */
    Response deleteRole(DeleteRoleReqVO deleteRoleReqVO);

    /**
     * Query role select list (all enabled roles)
     *
     * @param findRoleSelectListReqVO find role select list request VO
     * @return Response
     */
    Response findRoleSelectList(FindRoleSelectListReqVO findRoleSelectListReqVO);

    /**
     * Assign roles to user
     *
     * @param assignRoleReqVO assign role request VO
     * @return Response
     */
    Response assignRole(AssignRoleReqVO assignRoleReqVO);

    Response findRolePermissions(FindRolePermissionsReqVO reqVO);

    Response updateRolePermissions(UpdateRolePermissionsReqVO reqVO);
}
