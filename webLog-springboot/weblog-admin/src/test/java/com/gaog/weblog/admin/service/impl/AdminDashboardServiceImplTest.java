package com.gaog.weblog.admin.service.impl;

import com.gaog.weblog.admin.model.vo.dashboard.UserActivityTrendReqVO;
import com.gaog.weblog.admin.model.vo.dashboard.UserActivityTrendRspVO;
import com.gaog.weblog.admin.service.AdminDashboardService;
import com.gaog.weblog.common.domain.mapper.VisitorLogMapper;
import com.gaog.weblog.common.domain.dos.VisitorLogDO;
import com.gaog.weblog.common.utils.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdminDashboardServiceImplTest {

    @Mock
    private VisitorLogMapper visitorLogMapper;

    @InjectMocks
    private AdminDashboardServiceImpl adminDashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserActivityTrendDaily() {
        // Mock visitor logs
        List<VisitorLogDO> visitorLogs = new ArrayList<>();
        VisitorLogDO log1 = new VisitorLogDO();
        log1.setIpAddress("192.168.1.1");
        visitorLogs.add(log1);
        
        when(visitorLogMapper.selectList(any())).thenReturn(visitorLogs);

        UserActivityTrendReqVO reqVO = UserActivityTrendReqVO.builder()
                .timeRange("daily")
                .build();

        Response<List<UserActivityTrendRspVO>> response = adminDashboardService.getUserActivityTrend(reqVO);
        
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(7, response.getData().size()); // Should have 7 days of data
    }

    @Test
    void getUserActivityTrendWeekly() {
        // Mock visitor logs
        List<VisitorLogDO> visitorLogs = new ArrayList<>();
        VisitorLogDO log1 = new VisitorLogDO();
        log1.setIpAddress("192.168.1.1");
        visitorLogs.add(log1);
        
        when(visitorLogMapper.selectList(any())).thenReturn(visitorLogs);

        UserActivityTrendReqVO reqVO = UserActivityTrendReqVO.builder()
                .timeRange("weekly")
                .build();

        Response<List<UserActivityTrendRspVO>> response = adminDashboardService.getUserActivityTrend(reqVO);
        
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(7, response.getData().size()); // Should have 7 days of data for weekly
    }

    @Test
    void getUserActivityTrendMonthly() {
        // Mock visitor logs
        List<VisitorLogDO> visitorLogs = new ArrayList<>();
        VisitorLogDO log1 = new VisitorLogDO();
        log1.setIpAddress("192.168.1.1");
        visitorLogs.add(log1);
        
        when(visitorLogMapper.selectList(any())).thenReturn(visitorLogs);

        UserActivityTrendReqVO reqVO = UserActivityTrendReqVO.builder()
                .timeRange("monthly")
                .build();

        Response<List<UserActivityTrendRspVO>> response = adminDashboardService.getUserActivityTrend(reqVO);
        
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(30, response.getData().size()); // Should have 30 days of data for monthly
    }

    @Test
    void getUserActivityTrendYearly() {
        // Mock visitor logs
        List<VisitorLogDO> visitorLogs = new ArrayList<>();
        VisitorLogDO log1 = new VisitorLogDO();
        log1.setIpAddress("192.168.1.1");
        visitorLogs.add(log1);
        
        when(visitorLogMapper.selectList(any())).thenReturn(visitorLogs);

        UserActivityTrendReqVO reqVO = UserActivityTrendReqVO.builder()
                .timeRange("yearly")
                .build();

        Response<List<UserActivityTrendRspVO>> response = adminDashboardService.getUserActivityTrend(reqVO);
        
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(12, response.getData().size()); // Should have 12 months of data for yearly
    }
}