<template>
    <div class="admin-dashboard-page p-4">
        <!-- 页面标题 -->
        <div class="mb-6">
            <h1 class="text-2xl font-bold text-gray-900">仪表盘</h1>
            <p class="text-gray-600 mt-1">欢迎回来！这里是您的博客管理系统概览。</p>
        </div>

        <!-- 统计卡片 -->
        <el-row :gutter="20" class="mb-6">
            <el-col :span="6">
                <el-card shadow="hover" class="stat-card">
                    <div class="stat-content">
                        <div class="stat-icon bg-blue-100 text-blue-600 rounded-lg">
                            <el-icon>
                                <Document />
                            </el-icon>
                        </div>
                        <div class="stat-info">
                            <div class="stat-number">{{ stats.articlesCount }}</div>
                            <div class="stat-label">文章总数</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover" class="stat-card">
                    <div class="stat-content">
                        <div class="stat-icon bg-green-100 text-green-600 rounded-lg">
                            <el-icon>
                                <FolderOpened />
                            </el-icon>
                        </div>
                        <div class="stat-info">
                            <div class="stat-number">{{ stats.categoriesCount }}</div>
                            <div class="stat-label">分类数量</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover" class="stat-card">
                    <div class="stat-content">
                        <div class="stat-icon bg-purple-100 text-purple-600 rounded-lg">
                            <el-icon>
                                <PriceTag />
                            </el-icon>
                        </div>
                        <div class="stat-info">
                            <div class="stat-number">{{ stats.tagsCount }}</div>
                            <div class="stat-label">标签数量</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover" class="stat-card">
                    <div class="stat-content">
                        <div class="stat-icon bg-orange-100 text-orange-600 rounded-lg">
                            <el-icon>
                                <View />
                            </el-icon>
                        </div>
                        <div class="stat-info">
                            <div class="stat-number">{{ stats.totalViews }}</div>
                            <div class="stat-label">总浏览量</div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 快速访问 -->
        <el-card shadow="hover" class="admin-card mb-6">
            <template #header>
                <div class="flex items-center">
                    <span class="font-medium">快速访问</span>
                </div>
            </template>
            <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
                <div class="quick-access-item" @click="goToArticles">
                    <div class="flex flex-col items-center p-4 rounded-lg bg-blue-50 hover:bg-blue-100 cursor-pointer transition-colors">
                        <el-icon class="text-2xl text-blue-600 mb-2">
                            <Document />
                        </el-icon>
                        <span class="text-sm text-gray-700">文章管理</span>
                    </div>
                </div>
                <div class="quick-access-item" @click="goToCategories">
                    <div class="flex flex-col items-center p-4 rounded-lg bg-green-50 hover:bg-green-100 cursor-pointer transition-colors">
                        <el-icon class="text-2xl text-green-600 mb-2">
                            <FolderOpened />
                        </el-icon>
                        <span class="text-sm text-gray-700">分类管理</span>
                    </div>
                </div>
                <div class="quick-access-item" @click="goToTags">
                    <div class="flex flex-col items-center p-4 rounded-lg bg-purple-50 hover:bg-purple-100 cursor-pointer transition-colors">
                        <el-icon class="text-2xl text-purple-600 mb-2">
                            <PriceTag />
                        </el-icon>
                        <span class="text-sm text-gray-700">标签管理</span>
                    </div>
                </div>
                <div class="quick-access-item" @click="goToUsers">
                    <div class="flex flex-col items-center p-4 rounded-lg bg-orange-50 hover:bg-orange-100 cursor-pointer transition-colors">
                        <el-icon class="text-2xl text-orange-600 mb-2">
                            <User />
                        </el-icon>
                        <span class="text-sm text-gray-700">用户管理</span>
                    </div>
                </div>
            </div>
        </el-card>

        <!-- 图表和最新文章 -->
        <el-row :gutter="20" class="mb-6">
            <!-- 文章趋势图 -->
            <el-col :xs="24" :sm="24" :md="16">
                <el-card shadow="hover" class="admin-card">
                    <template #header>
                        <div class="flex items-center">
                            <span class="font-medium">文章发布趋势</span>
                        </div>
                    </template>
                    <div class="chart-container">
                        <div ref="chartRef" style="width: 100%; height: 300px;"></div>
                    </div>
                </el-card>
            </el-col>

            <!-- 浏览量趋势图 -->
            <el-col :xs="24" :sm="24" :md="8">
                <el-card shadow="hover" class="admin-card">
                    <template #header>
                        <div class="flex items-center">
                            <span class="font-medium">浏览量趋势</span>
                        </div>
                    </template>
                    <div class="chart-container">
                        <div ref="viewsChartRef" style="width: 100%; height: 300px;"></div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 用户活动趋势图 -->
        <el-row :gutter="20" class="mb-6">
            <el-col :xs="24">
                <el-card shadow="hover" class="admin-card">
                    <template #header>
                        <div class="flex items-center justify-between">
                            <span class="font-medium">用户活动趋势</span>
                            <el-select v-model="activityTimeRange" @change="loadUserActivityTrend" style="width: 120px;">
                                <el-option label="日" value="day"></el-option>
                                <el-option label="周" value="week"></el-option>
                                <el-option label="月" value="month"></el-option>
                                <el-option label="年" value="year"></el-option>
                            </el-select>
                        </div>
                    </template>
                    <div class="chart-container">
                        <div ref="activityChartRef" style="width: 100%; height: 300px;"></div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 最新文章 -->
        <el-card shadow="hover" class="admin-card">
            <template #header>
                <div class="flex items-center justify-between">
                    <span class="font-medium">最新文章</span>
                    <el-button type="text" @click="goToArticles">查看更多</el-button>
                </div>
            </template>
            <!-- 使用卡片列表替代表格 -->
            <div class="articles-list">
                <div v-for="article in latestArticles" :key="article.id" 
                     class="article-card"
                     @click="goToArticleDetail(article.id)">
                    <div class="flex items-start p-4 border-b border-gray-100 last:border-0">
                        <div class="flex-1 min-w-0">
                            <h3 class="text-base font-medium text-gray-900 truncate mb-1">
                                {{ article.title }}
                            </h3>
                            <div class="flex items-center text-sm text-gray-500 space-x-3">
                                <span>{{ formatDate(article.createTime) }}</span>
                                <span class="flex items-center">
                                    <el-icon class="mr-1"><View /></el-icon>
                                    {{ article.readNum || 0 }}
                                </span>
                                <span v-if="article.category" class="bg-blue-100 text-blue-800 text-xs px-2 py-0.5 rounded-full">
                                    {{ article.category }}
                                </span>
                            </div>
                        </div>
                        <div v-if="article.cover" class="ml-4 flex-shrink-0">
                            <img :src="article.cover" alt="文章封面" 
                                 class="w-16 h-12 object-cover rounded-md" />
                        </div>
                    </div>
                </div>
                <!-- 当没有文章时显示 -->
                <div v-if="latestArticles.length === 0" class="text-center py-8 text-gray-500">
                    暂无文章
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { getArticlePageList } from '@/api/admin/article'
import { getCategoryPageList } from '@/api/admin/category'
import { getArticleStats, getDashboardStats, getLatestArticles, getPvTrend, getUserActivityTrend } from '@/api/admin/statistics'
import { getTagPageList } from '@/api/admin/tag'
import {
    Document,
    FolderOpened,
    PriceTag,
    User,
    View
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import moment from 'moment'
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 图表引用
const chartRef = ref(null)
const viewsChartRef = ref(null)
const activityChartRef = ref(null)
let chartInstance = null
let viewsChartInstance = null
let activityChartInstance = null

// 数据
const stats = ref({
    articlesCount: 0,
    categoriesCount: 0,
    tagsCount: 0,
    totalViews: 0
})

const latestArticles = ref([])

// 用户活动趋势相关
const activityTimeRange = ref('day')

// 获取统计数据
const loadStats = () => {
    // 使用 getDashboardStats API 获取所有统计数据
    getDashboardStats().then(res => {
        if (res.success && res.data) {
            // 根据实际API返回的数据结构设置值
            stats.value.articlesCount = res.data.articleTotal !== undefined ? res.data.articleTotal : 0
            stats.value.categoriesCount = res.data.categoryTotal !== undefined ? res.data.categoryTotal : 0
            stats.value.tagsCount = res.data.tagTotal !== undefined ? res.data.tagTotal : 0
            stats.value.totalViews = res.data.pvTotal !== undefined ? res.data.pvTotal : 0
        }
    }).catch(error => {
        // 如果获取失败，使用原来的备选方案
        // 获取文章总数
        getArticlePageList({ current: 1, size: 1000 }).then(res => {
            if (res.success) {
                stats.value.articlesCount = res.total
            }
        }).catch(() => {
            stats.value.articlesCount = 0
        })

        // 获取总浏览量
        getPvTrend().then(res => {
            if (res.success && res.data && res.data.total) {
                stats.value.totalViews = res.data.total
            } else {
                // 如果获取失败，使用文章浏览量之和作为备选方案
                getArticlePageList({ current: 1, size: 1000 }).then(res => {
                    if (res.success) {
                        if (res.data && Array.isArray(res.data)) {
                            const totalViews = res.data.reduce((sum, article) => sum + (article.viewCount || 0), 0)
                            stats.value.totalViews = totalViews
                        }
                    }
                })
            }
        }).catch(() => {
            stats.value.totalViews = 0
        })

        // 获取分类总数
        getCategoryPageList({ current: 1, size: 1 }).then(res => {
            if (res.success) {
                stats.value.categoriesCount = res.total
            }
        }).catch(() => {
            stats.value.categoriesCount = 0
        })

        // 获取标签总数
        getTagPageList({ current: 1, size: 1 }).then(res => {
            if (res.success) {
                stats.value.tagsCount = res.total
            }
        }).catch(() => {
            stats.value.tagsCount = 0
        })
    })
}

// 初始化文章趋势图表
const initArticleChart = () => {
    if (chartRef.value) {
        chartInstance = echarts.init(chartRef.value)

        // 获取文章发布趋势数据
        getArticleStats().then(res => {
            if (res.success) {
                // 处理API返回的数据结构，x 轴显示为 年-月-日
                const dates = res.data.map(item => {
                    // 支持多种后端返回字段：date / day / createDate / dayOfWeek / dayOfMonth
                    const raw = item.date || item.day || item.createDate || item.dayOfMonth || item.dayOfWeek
                    // 如果 raw 是数组 [year, month, day]
                    if (Array.isArray(raw)) {
                        if (raw.length >= 3) return `${raw[0]}-${String(raw[1]).padStart(2,'0')}-${String(raw[2]).padStart(2,'0')}`
                        if (raw.length === 2) return `${raw[0]}-${String(raw[1]).padStart(2,'0')}-01`
                    }
                    // 排除代表周几或非日期的值（比如数字 0-7、'周一'等），以便使用默认最近7天日期
                    if ((typeof raw === 'number' && raw >= 0 && raw <= 7) || (typeof raw === 'string' && (/^[0-7]$/.test(raw) || raw.indexOf('周') !== -1))) {
                        return ''
                    }
                    // 尝试使用 moment 解析
                    const m = moment(raw)
                    if (m.isValid && m.isValid()) {
                        return m.format('YYYY-MM-DD')
                    }
                    // fallback: use the raw value as string
                    return String(raw || '')
                })
                const counts = res.data.map(item => Math.round(Number(item.pv || item.count || item.value || 0)));

                    // 默认最近 7 天日期
                    const defaultDates = Array.from({length:7}).map((_,i)=> moment().subtract(6-i,'days').format('YYYY-MM-DD'))
                    const defaultCounts = [12, 23, 18, 25, 15, 19, 22]
                const option = {
                    tooltip: {
                        trigger: 'axis'
                    },
                    xAxis: {
                        type: 'category',
                        data: (dates && dates.length) ? dates : defaultDates
                    },
                    yAxis: {
                        type: 'value',
                        name: '文章数量'
                    },
                    series: [{
                        data: (counts && counts.length) ? counts : defaultCounts,
                        type: 'line',
                        smooth: true,
                        areaStyle: {
                            color: '#409eff'
                        },
                        lineStyle: {
                            color: '#409eff'
                        }
                    }],
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    }
                }
                chartInstance.setOption(option)
            } else {
                // 如果获取数据失败，使用默认数据
                    const defaultDates = Array.from({length:7}).map((_,i)=> moment().subtract(6-i,'days').format('YYYY-MM-DD'))
                    const defaultCounts = [12, 23, 18, 25, 15, 19, 22]
                const option = {
                    tooltip: {
                        trigger: 'axis'
                    },
                    xAxis: {
                        type: 'category',
                        data: defaultDates
                    },
                    yAxis: {
                        type: 'value',
                        name: '文章数量'
                    },
                    series: [{
                        data: defaultCounts,
                        type: 'line',
                        smooth: true,
                        areaStyle: {
                            color: '#409eff'
                        },
                        lineStyle: {
                            color: '#409eff'
                        }
                    }],
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    }
                }
                chartInstance.setOption(option)
            }
        }).catch(() => {
            // 如果请求失败，使用默认数据
                const defaultDates = Array.from({length:7}).map((_,i)=> moment().subtract(6-i,'days').format('YYYY-MM-DD'))
                const defaultCounts = [12, 23, 18, 25, 15, 19, 22]
            const option = {
                tooltip: {
                    trigger: 'axis'
                },
                xAxis: {
                    type: 'category',
                    data: defaultDates
                },
                yAxis: {
                    type: 'value',
                    name: '文章数量'
                },
                series: [{
                    data: defaultCounts,
                    type: 'line',
                    smooth: true,
                    areaStyle: {
                        color: '#409eff'
                    },
                    lineStyle: {
                        color: '#409eff'
                    }
                }],
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                }
            }
            chartInstance.setOption(option)
        })
    }
}

// 初始化浏览量趋势图表
const initViewsChart = () => {
    if (viewsChartRef.value) {
        viewsChartInstance = echarts.init(viewsChartRef.value)

        // 获取浏览量趋势数据
        getPvTrend().then(res => {
            if (res.success) {
                // 处理API返回的数据结构（假设与getArticleStats相似）
                const dates = res.data.map(item => {
                    const raw = item.date
                    if (Array.isArray(raw)) {
                        if (raw.length >= 3) return `${raw[0]}-${String(raw[1]).padStart(2,'0')}-${String(raw[2]).padStart(2,'0')}`
                        if (raw.length === 2) return `${raw[0]}-${String(raw[1]).padStart(2,'0')}-01`
                    }
                    if ((typeof raw === 'number' && raw >= 0 && raw <= 7) || (typeof raw === 'string' && (/^[0-7]$/.test(raw) || raw.indexOf('周') !== -1))) {
                        return ''
                    }
                    const m = moment(raw)
                    if (m.isValid && m.isValid()) {
                        return m.format('YYYY-MM-DD')
                    }
                    return String(raw || '')
                })
                const counts = res.data.map(item => Math.round(Number(item.pv|| 0)));

                    const defaultDates = Array.from({length:7}).map((_,i)=> moment().subtract(6-i,'days').format('YYYY-MM-DD'))
                    const defaultCounts = [12, 23, 18, 25, 15, 19, 22]
                const option = {
                    tooltip: {
                        trigger: 'axis'
                    },
                    xAxis: {
                        type: 'category',
                        data: (dates && dates.length) ? dates : defaultDates
                    },
                    yAxis: {
                        type: 'value',
                        name: '浏览量'
                    },
                    series: [{
                        data: (counts && counts.length) ? counts : defaultCounts,
                        type: 'line',
                        smooth: true,
                        areaStyle: {
                            color: '#f56c6c'
                        },
                        lineStyle: {
                            color: '#f56c6c'
                        }
                    }],
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    }
                }
                viewsChartInstance.setOption(option)
            } else {
                // 如果获取数据失败，使用默认数据
                    const defaultDates = Array.from({length:7}).map((_,i)=> moment().subtract(6-i,'days').format('YYYY-MM-DD'))
                    const defaultCounts = [12, 23, 18, 25, 15, 19, 22]
                const option = {
                    tooltip: {
                        trigger: 'axis'
                    },
                    xAxis: {
                        type: 'category',
                        data: defaultDates
                    },
                    yAxis: {
                        type: 'value',
                        name: '浏览量'
                    },
                    series: [{
                        data: [120, 180, 150, 220, 190, 250, 310].slice(0, defaultCounts.length).map((v,i)=> defaultCounts[i] || v),
                        type: 'line',
                        smooth: true,
                        areaStyle: {
                            color: '#f56c6c'
                        },
                        lineStyle: {
                            color: '#f56c6c'
                        }
                    }],
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    }
                }
                viewsChartInstance.setOption(option)
            }
        }).catch(() => {
            // 如果请求失败，使用默认数据
                const defaultDates = Array.from({length:7}).map((_,i)=> moment().subtract(6-i,'days').format('YYYY-MM-DD'))
                const defaultCounts = [12, 23, 18, 25, 15, 19, 22]
            const option = {
                tooltip: {
                    trigger: 'axis'
                },
                xAxis: {
                    type: 'category',
                    data: defaultDates
                },
                yAxis: {
                    type: 'value',
                    name: '浏览量'
                },
                series: [{
                    data: defaultCounts,
                    type: 'line',
                    smooth: true,
                    areaStyle: {
                        color: '#f56c6c'
                    },
                    lineStyle: {
                        color: '#f56c6c'
                    }
                }],
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                }
            }
            viewsChartInstance.setOption(option)
        })
    }
}

// 初始化用户活动趋势图表
const initActivityChart = (data, timeRangeText) => {
    if (!activityChartRef.value) return

    if (activityChartInstance) {
        activityChartInstance.dispose()
    }

    activityChartInstance = echarts.init(activityChartRef.value)

    const xData = data.map(item => {
        const raw = item.date
        if (Array.isArray(raw) && raw.length >= 3) {
            return `${raw[0]}-${String(raw[1]).padStart(2,'0')}-${String(raw[2]).padStart(2,'0')}`
        }
        const m = moment(raw)
        return m.isValid() ? m.format('YYYY-MM-DD') : String(raw || '')
    })

    const yData = data.map(item =>
        Number(item.activeUserCount || item.count || item.value || 0)
    )

    const option = {
        tooltip: {
            trigger: 'item',
            appendToBody: true,
            confine: false,
            extraCssText: 'z-index:9999;',
            backgroundColor: 'rgba(50,50,50,0.92)',
            borderWidth: 0,
            textStyle: { color: '#fff', fontSize: 12 },
            formatter: (params) => `
                <div style="padding:6px 10px;line-height:1.6;">
                    <div style="font-weight:600;margin-bottom:4px;">
                        ${params.name}
                    </div>
                    <div>
                        ${timeRangeText}：
                        <span style="color:#67c23a;font-weight:700;">
                            ${params.value}
                        </span>
                    </div>
                </div>
            `
        },

        grid: {
            left: '2%',
            right: '2%',
            bottom: '6%',
            top: '8%',
            containLabel: true
        },

        xAxis: {
            type: 'category',
            data: xData,
            axisLine: { show: false },
            axisTick: { show: false },
            axisLabel: { color: '#909399' }
        },

        yAxis: {
            type: 'value',
            name: '活跃度',
            splitLine: {
                lineStyle: { type: 'dashed', color: '#ebeef5' }
            },
            axisLine: { show: false },
            axisTick: { show: false },
            axisLabel: { color: '#909399' }
        },

        series: [{
            type: 'bar',
            data: yData,
            barWidth: '42%',
            itemStyle: {
                color: new echarts.graphic.LinearGradient(0,0,0,1,[
                    { offset: 0, color: '#67c23a' },
                    { offset: 1, color: '#b3e19d' }
                ]),
                borderRadius: [6,6,0,0]
            },
            label: {
                show: true,
                position: 'top',
                color: '#606266'
            },
            emphasis: {
                itemStyle: {
                    shadowBlur: 12,
                    shadowColor: 'rgba(0,0,0,0.25)'
                }
            }
        }]
    }

    activityChartInstance.setOption(option)
}



// 获取最新文章
const loadLatestArticles = () => {
    getLatestArticles({ current: 1, size: 5 }).then(res => {
        if (res.success) {
            latestArticles.value = res.data || []
        }
    }).catch(error => {
        console.error('获取最新文章失败:', error)
        latestArticles.value = []
    })
}

// 获取用户活动趋势数据
const loadUserActivityTrend = () => {
    const timeRangeMap = {
        'day': '日活跃度',
        'week': '周活跃度',
        'month': '月活跃度',
        'year': '年活跃度'
    }
    
    getUserActivityTrend({
        timeRange: activityTimeRange.value
    }).then(res => {
        if (res.success) {
            initActivityChart(res.data, timeRangeMap[activityTimeRange.value])
        } else {
            // 如果获取数据失败，使用默认数据
            const defaultData = [
                { date: '2023-01-01', activeUserCount: 10 },
                { date: '2023-01-02', activeUserCount: 15 },
                { date: '2023-01-03', activeUserCount: 12 },
                { date: '2023-01-04', activeUserCount: 18 },
                { date: '2023-01-05', activeUserCount: 22 }
            ]
            initActivityChart(defaultData, timeRangeMap[activityTimeRange.value])
        }
    }).catch(error => {
        console.error('获取用户活动趋势失败:', error)
        // 如果请求失败，使用默认数据
        const defaultData = [
            { date: '2023-01-01', activeUserCount: 10 },
            { date: '2023-01-02', activeUserCount: 15 },
            { date: '2023-01-03', activeUserCount: 12 },
            { date: '2023-01-04', activeUserCount: 18 },
            { date: '2023-01-05', activeUserCount: 22 }
        ]
        initActivityChart(defaultData, timeRangeMap[activityTimeRange.value])
    })
}

// 跳转到文章列表
const goToArticles = () => {
    router.push('/admin/article/list')
}

// 跳转到分类管理
const goToCategories = () => {
    router.push('/admin/category/list')
}

// 跳转到标签管理
const goToTags = () => {
    router.push('/admin/tag/list')
}

// 跳转到用户管理
const goToUsers = () => {
    router.push('/admin/user/list')
}

// 跳转到文章详情
const goToArticleDetail = (id) => {
    router.push(`/admin/article/edit/${id}`)
}

// 格式化日期
const formatDate = (timestamp) => {
    if (!timestamp) return ''
    const date = new Date(timestamp)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 组件挂载时初始化
onMounted(() => {
    loadStats()
    initArticleChart()
    initViewsChart()
    loadLatestArticles()
    // 初始化用户活动趋势图表
    loadUserActivityTrend()
})

// 组件卸载前清理
onBeforeUnmount(() => {
    if (chartInstance) {
        chartInstance.dispose()
    }
    if (viewsChartInstance) {
        viewsChartInstance.dispose()
    }
    if (activityChartInstance) {
        activityChartInstance.dispose()
    }
})
</script>

<style scoped>
.stat-card {
    height: 120px;
}

.stat-content {
    display: flex;
    align-items: center;
    gap: 16px;
}

.stat-icon {
    width: 50px;
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
}

.stat-info {
    flex: 1;
}

.stat-number {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 4px;
}

.stat-label {
    font-size: 14px;
    color: #666;
}

.chart-container {
    height: 300px;
}

.quick-access-item {
    transition: all 0.2s ease;
}

.quick-access-item:hover {
    transform: translateY(-2px);
}

@media (max-width: 768px) {
    .admin-dashboard-page {
        padding: 0.75rem !important;
    }

    .admin-dashboard-page .mb-6 h1 {
        font-size: 1.5rem;
        line-height: 2rem;
    }

    .admin-dashboard-page .stat-card {
        height: auto;
    }

    .admin-dashboard-page .stat-content {
        gap: 12px;
    }

    .admin-dashboard-page .stat-number {
        font-size: 22px;
    }

    .admin-dashboard-page .chart-container {
        height: 220px;
    }

    .admin-dashboard-page .chart-container > div {
        height: 220px !important;
    }

    .admin-dashboard-page .articles-list .article-card .flex {
        flex-direction: column;
        gap: 0.75rem;
    }

    .admin-dashboard-page .articles-list .article-card img {
        width: 100%;
        height: 180px;
    }
}
</style>
