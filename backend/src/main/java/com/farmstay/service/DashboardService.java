package com.farmstay.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    Map<String, Object> getStatistics();

    List<Map<String, Object>> getReservationTrend(int days);

    List<Map<String, Object>> getRevenueTrend(int days);

    List<Map<String, Object>> getTopFarmhouses(int limit);

    List<Map<String, Object>> getRatingDistribution();
}
