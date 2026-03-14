# User Activity Trend Implementation

## Overview
This document describes the implementation of the user activity trend feature in the admin dashboard, which displays user engagement data based on different time periods.

## Requirements
The dashboard should display user activity data according to these rules:
- **近一周 (Last Week)**: Show data for the last 7 days (e.g., 2025-12-14 to 2025-12-20)
- **近一个月 (Last Month)**: Show data for the last 30 days (e.g., 2025-11-20 to 2025-12-20)
- **近一年 (Last Year)**: Show data for the last 12 months (January to December)

## Implementation Details

### 1. Time Range Mapping
The system maps the frontend time range values to specific data retrieval methods:

| Frontend Value | Backend Method | Data Range |
|----------------|----------------|------------|
| `daily` | `getUserActivityTrendDaily()` | Last 7 days |
| `weekly` | `getUserActivityTrendWeekly()` | Last 7 days |
| `monthly` | `getUserActivityTrendMonthly()` | Last 30 days |
| `yearly` | `getUserActivityTrendYearly()` | Last 12 months |

### 2. Data Retrieval Logic

#### Daily/Weekly Trends
- Retrieves data for exactly 7 days
- Shows each day's activity count
- Date format: `yyyy-MM-dd`

#### Monthly Trend
- Retrieves data for exactly 30 days
- Shows each day's activity count
- Date format: `yyyy-MM-dd`

#### Yearly Trend
- Retrieves data for the last 12 months
- Aggregates data by month
- Date format: `yyyy-MM`

### 3. API Endpoint
```
POST /admin/dashboard/user-activity-trend
```

Request Body:
```json
{
  "timeRange": "daily|weekly|monthly|yearly"
}
```

Response Format:
```json
{
  "success": true,
  "code": "00000",
  "message": "成功",
  "data": [
    {
      "date": "2025-12-14",
      "activeUserCount": 42
    },
    // ... more entries
  ]
}
```

## Code Changes

### AdminDashboardServiceImpl.java
Modified the four methods to implement the correct date ranges:
1. `getUserActivityTrendDaily()` - 7 days of daily data
2. `getUserActivityTrendWeekly()` - 7 days of daily data (changed from weekly aggregation)
3. `getUserActivityTrendMonthly()` - 30 days of daily data (changed from monthly aggregation)
4. `getUserActivityTrendYearly()` - 12 months of monthly aggregated data

### AdminDashboardController.java
Changed the endpoint from `@GetMapping` to `@PostMapping` since it receives request body data.

## Testing
Unit tests have been created to verify that each time range returns the correct number of data points:
- Daily: 7 data points
- Weekly: 7 data points
- Monthly: 30 data points
- Yearly: 12 data points

## Notes
- All date calculations are inclusive of the current date
- Data is retrieved from the `visitor_log` table
- Activity is measured by counting unique IP addresses per time period