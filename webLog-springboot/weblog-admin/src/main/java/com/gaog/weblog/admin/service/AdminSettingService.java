package com.gaog.weblog.admin.service;

import com.gaog.weblog.admin.model.vo.setting.FindSiteSettingRspVO;
import com.gaog.weblog.admin.model.vo.setting.UpdateSiteSettingReqVO;
import com.gaog.weblog.common.utils.Response;

public interface AdminSettingService {

    Response<FindSiteSettingRspVO> findSiteSetting();

    Response<Void> updateSiteSetting(UpdateSiteSettingReqVO reqVO);
}
