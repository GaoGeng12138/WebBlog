<template>
    <div class="min-h-screen bg-transparent pb-20">
        <div class="sticky top-0 z-50 border-b border-[rgba(129,158,196,0.16)] bg-white/70 backdrop-blur-xl">
            <div class="max-w-7xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
                <div class="flex items-center cursor-pointer group" @click="goHome">
                    <div
                        class="mr-3 flex h-8 w-8 items-center justify-center rounded-full bg-[rgba(240,245,251,0.94)] text-slate-500 transition-colors group-hover:bg-[rgba(116,149,195,0.12)] group-hover:text-[var(--theme-primary)]">
                        <el-icon>
                            <ArrowLeft />
                        </el-icon>
                    </div>
                    <span
                        class="text-sm font-medium text-slate-600 transition-colors group-hover:text-[var(--theme-primary)]">返回首页</span>
                </div>
                <div class="flex items-center gap-2 text-xs text-gray-400">
                    <span>个人中心</span>
                    <span class="text-gray-300">/</span>
                    <span class="text-gray-600 font-medium">{{ activeTabName }}</span>
                </div>
            </div>
        </div>

        <div class="relative h-64 w-full overflow-hidden group md:h-80">
            <img src="https://picsum.photos/1920/600?random=1"
                class="w-full h-full object-cover transition duration-700 group-hover:scale-105" alt="Cover" />
            <div class="absolute inset-0 bg-[linear-gradient(180deg,rgba(238,245,252,0.1),rgba(57,92,145,0.2)),linear-gradient(0deg,rgba(15,23,42,0.22),rgba(15,23,42,0.04))]"></div>
            <div
                class="absolute top-6 right-6 opacity-0 group-hover:opacity-100 transition-all duration-300 transform translate-y-[-10px] group-hover:translate-y-0">
                <el-button size="small" round icon="Camera"
                    class="theme-btn-secondary !border-white/45 !bg-white/18 !px-4 !text-white backdrop-blur-sm hover:!bg-white/28">更换封面</el-button>
            </div>
        </div>

        <div class="max-w-7xl mx-auto px-4 sm:px-6 relative -mt-20 z-10">
            <div class="relative mb-8 overflow-visible rounded-[30px] border border-[rgba(129,158,196,0.18)] bg-white/92 p-6 shadow-[0_28px_80px_rgba(120,146,184,0.16)] backdrop-blur-xl md:p-8">
                <div class="flex flex-col md:flex-row items-start justify-between">
                    <div class="flex flex-col md:flex-row items-center md:items-end w-full">
                        <div class="relative -mt-20 md:-mt-24 mb-4 md:mb-0 md:mr-6 flex-shrink-0">
                            <div
                                class="relative h-32 w-32 cursor-pointer overflow-hidden rounded-full border-[6px] border-white bg-white shadow-[0_18px_40px_rgba(120,146,184,0.18)] group md:h-40 md:w-40"
                                @click="triggerAvatarUpload">
                                <img :src="displayAvatar" @error="handleAvatarError"
                                    class="w-full h-full object-cover" />
                                <div
                                    class="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                                    <span
                                        class="text-white text-xs font-medium border border-white/50 px-3 py-1 rounded-full backdrop-blur-sm">{{ avatarUploading ? '上传中...' : '修改头像' }}</span>
                                </div>
                            </div>
                            <input
                                ref="avatarInputRef"
                                type="file"
                                accept="image/png,image/jpeg,image/jpg"
                                class="hidden"
                                @change="handleAvatarChange" />
                        </div>

                        <div class="flex-1 text-center md:text-left">
                            <div class="flex flex-col md:flex-row md:items-center gap-3 mb-2">
                                <h1 class="text-2xl md:text-3xl font-bold text-slate-900 tracking-tight drop-shadow-[0_1px_0_rgba(255,255,255,0.35)]">
                                    {{ user.nickname || user.username || '未登录' }}
                                </h1>
                                <div class="flex items-center justify-center md:justify-start gap-2">
                                    <span v-if="hasRole('ROLE_ADMIN')" class="admin-identity-badge">
                                        <span class="admin-identity-badge__glow"></span>
                                        <el-icon class="admin-identity-badge__icon">
                                            <Setting />
                                        </el-icon>
                                        <span class="admin-identity-badge__text">管理员</span>
                                    </span>
                                    <el-tag v-else effect="light" type="primary" size="small" round
                                        class="!border-[rgba(116,149,195,0.18)] !bg-[rgba(116,149,195,0.12)] !text-[var(--theme-primary-deep)]">
                                        {{ roleDisplayName }}
                                    </el-tag>
                                    <el-tag effect="light" type="success" size="small" round
                                        class="!border-[rgba(116,149,195,0.18)] !bg-[rgba(116,149,195,0.12)] !text-[var(--theme-primary-deep)]">
                                        {{ activityLevelLabel }}
                                    </el-tag>
                                    <span class="rounded-md bg-[rgba(240,245,251,0.95)] px-2 py-0.5 text-xs text-slate-400">ID:{{
                                        user.userId
                                        }}</span>
                                </div>
                            </div>

                            <div
                                class="mb-4 flex flex-wrap items-center justify-center gap-x-4 gap-y-2 text-sm text-slate-600 md:justify-start">
                                <span class="flex items-center gap-1.5 rounded-full bg-white/55 px-3 py-1 shadow-[0_8px_20px_rgba(120,146,184,0.08)]">
                                    <el-icon class="text-[var(--theme-primary)]">
                                        <Location />
                                    </el-icon> {{ currentLocationText }}</span>
                                <span class="flex items-center gap-1.5 rounded-full bg-white/55 px-3 py-1 shadow-[0_8px_20px_rgba(120,146,184,0.08)]">
                                    <el-icon class="text-[var(--theme-primary-deep)]">
                                        <Calendar />
                                    </el-icon> 加入于 {{ formatDate(user.registerTime) }}</span>
                            </div>

                            <p
                                class="max-w-2xl rounded-2xl bg-[linear-gradient(135deg,rgba(255,255,255,0.9),rgba(244,248,253,0.82))] px-4 py-3 text-sm leading-relaxed text-slate-600 shadow-[0_12px_30px_rgba(120,146,184,0.08)] md:mx-0 md:bg-transparent md:p-0 md:shadow-none">
                                {{ user.introduction || '这个人很懒，什么都没有写...' }}
                            </p>
                        </div>

                        <div class="mt-6 flex w-full flex-col items-stretch gap-4 md:mt-0 md:w-auto md:min-w-[390px] md:items-end">
                            <div class="profile-panel-shell">
                                <div class="profile-panel-heading">个人数据</div>
                                <div class="grid w-full grid-cols-3 gap-3">
                                    <div class="profile-stat-card group cursor-pointer">
                                        <div class="profile-stat-accent"></div>
                                        <div
                                            class="text-[30px] font-bold leading-none text-slate-900 transition-colors group-hover:text-[var(--theme-primary)]">
                                            {{ articleCount }}</div>
                                        <div class="mt-2 text-[11px] font-semibold tracking-[0.18em] text-slate-500">文章</div>
                                    </div>
                                    <div class="profile-stat-card group cursor-pointer">
                                        <div class="profile-stat-accent"></div>
                                        <div
                                            class="text-[30px] font-bold leading-none text-slate-900 transition-colors group-hover:text-[var(--theme-primary-deep)]">
                                            {{ favoriteCount }}</div>
                                        <div class="mt-2 text-[11px] font-semibold tracking-[0.18em] text-slate-500">收藏</div>
                                    </div>
                                    <div class="profile-stat-card group cursor-pointer">
                                        <div class="profile-stat-accent"></div>
                                        <div
                                            class="text-[30px] font-bold leading-none text-slate-900 transition-colors group-hover:text-[var(--theme-primary-soft)]">
                                            {{ commentCount }}</div>
                                        <div class="mt-2 text-[11px] font-semibold tracking-[0.18em] text-slate-500">评论</div>
                                    </div>
                                </div>
                            </div>
                            <div class="grid w-full grid-cols-1 gap-3 sm:grid-cols-2">
                                <el-button type="primary" size="large" round
                                    class="theme-btn-primary profile-action-btn profile-action-btn--primary !ml-0 !w-full !px-8 !font-semibold"
                                    @click="openEditDialog">
                                    编辑个人资料
                                </el-button>
                                <el-button v-if="hasRole('ROLE_ADMIN')" size="large" round
                                    class="theme-btn-secondary profile-action-btn profile-action-btn--secondary !ml-0 !w-full !px-8 !font-semibold"
                                    @click="goToAdminPanel">
                                    <el-icon class="mr-1">
                                        <Setting />
                                    </el-icon>
                                    后台管理系统
                                </el-button>
                            </div>
                            <div class="profile-quick-panel">
                                <div class="profile-quick-panel__title">快捷操作</div>
                                <div class="profile-quick-links">
                                    <button v-for="item in quickActionItems" :key="item.label" type="button"
                                        class="profile-quick-link"
                                        :class="{ 'profile-quick-link--primary': item.type === 'primary' }"
                                        @click="item.action">
                                        <span class="profile-quick-link__title">{{ item.label }}</span>
                                        <span class="profile-quick-link__desc">{{ item.desc }}</span>
                                    </button>
                                </div>
                                <div v-if="!canUserPublish" class="mt-3 rounded-2xl border border-amber-100 bg-amber-50/70 px-3 py-2 text-xs text-amber-700">
                                    前台投稿已关闭，写文章入口已隐藏
                                </div>
                            </div>
                            <div class="profile-quick-note">近 {{ recentActiveDays }} 天有活跃记录</div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">

                <div class="lg:col-span-3">
                    <div class="sticky top-24 rounded-[26px] border border-[rgba(129,158,196,0.18)] bg-white/88 p-4 shadow-[0_20px_50px_rgba(120,146,184,0.12)] backdrop-blur-xl">
                        <el-menu :default-active="activeTab" class="!border-none custom-menu" @select="handleTabSelect">
                            <el-menu-item index="overview">
                                <el-icon>
                                    <Odometer />
                                </el-icon>
                                <span>总览 / 动态</span>
                            </el-menu-item>
                            <el-menu-item index="articles">
                                <el-icon>
                                    <Document />
                                </el-icon>
                                <span>我的文章</span>
                            </el-menu-item>
                            <el-menu-item index="drafts">
                                <el-icon>
                                    <EditPen />
                                </el-icon>
                                <span>草稿箱</span>
                            </el-menu-item>
                            <el-menu-item index="collections">
                                <el-icon>
                                    <Star />
                                </el-icon>
                                <span>我的收藏</span>
                            </el-menu-item>
                            <el-menu-item index="comments">
                                <el-icon>
                                    <ChatLineRound />
                                </el-icon>
                                <span>评论历史</span>
                            </el-menu-item>
                            <div class="h-px bg-gray-100 my-3 mx-4"></div>
                            <el-menu-item index="security">
                                <el-icon>
                                    <Lock />
                                </el-icon>
                                <span>安全设置</span>
                            </el-menu-item>
                        </el-menu>
                    </div>
                </div>

                <div class="lg:col-span-9">
                    <div class="min-h-[600px] rounded-[30px] border border-[rgba(129,158,196,0.18)] bg-white/90 p-6 shadow-[0_24px_70px_rgba(120,146,184,0.12)] backdrop-blur-xl md:p-8">

                        <div v-if="activeTab === 'overview'" class="animate-fade-in space-y-8">
                            <section>
                                <div class="activity-section">
                                    <div class="flex justify-between items-center mb-4">
                                        <h3 class="text-lg font-bold text-gray-800">贡献活跃度</h3>
                                        <div class="flex gap-2">
                                            <el-button size="small"
                                                :type="selectedTimeRange === 'week' ? 'primary' : 'default'"
                                                @click="changeTimeRange('week')">
                                                近一周
                                            </el-button>
                                            <el-button size="small"
                                                :type="selectedTimeRange === 'month' ? 'primary' : 'default'"
                                                @click="changeTimeRange('month')">
                                                近一月
                                            </el-button>
                                            <el-button size="small"
                                                :type="selectedTimeRange === 'year' ? 'primary' : 'default'"
                                                @click="changeTimeRange('year')">
                                                近一年
                                            </el-button>
                                        </div>
                                    </div>
                                    <div class="activity-summary">
                                        <div class="score-display">
                                            <span class="score-number">{{ activityData.totalScore }}</span>
                                            <span class="score-label">活跃度分数</span>
                                        </div>
                                        <div class="rank-info">
                                            <span>排名: {{ activityData.rank }} / {{ activityData.totalUsers }}</span>
                                        </div>
                                    </div>

                                    <div class="activity-breakdown">
                                        <div v-for="activity in activityData.activities" :key="activity.type"
                                            class="activity-item">
                                            <span class="activity-type">{{ activity.name || getActivityTypeName(activity.type) }}</span>
                                            <span class="activity-count">{{ activity.count }}次</span>
                                            <span class="activity-item-score">+{{ activity.score }}分</span>
                                        </div>
                                    </div>

                                    <div class="activity-chart mt-8">
                                        <div class="flex justify-between items-center mb-4">
                                            <h4 class="text-sm font-medium text-gray-700">活跃度趋势</h4>
                                            <div class="flex items-center text-xs text-gray-500">
                                                <div class="flex items-center mr-3">
                                                    <div class="w-3 h-3 bg-blue-500 rounded mr-1"></div>
                                                    <span>有活跃度</span>
                                                </div>
                                                <div class="flex items-center">
                                                    <div class="w-3 h-3 bg-gray-200 rounded mr-1"></div>
                                                    <span>无活跃度</span>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="relative w-full h-48 border-b border-gray-200">
                                            <div class="flex items-end h-full overflow-x-auto pb-3 custom-scrollbar"
                                                v-loading="activityLoading">
                                                <div v-for="(item, index) in activityData.trend"
                                                    :key="index"
                                                    class="h-full flex flex-col justify-end items-center flex-shrink-0 px-2 min-w-[90px]">
                                                    <!-- 柱子容器 -->
                                                    <div class="relative w-full h-full flex items-end justify-center">
                                                        <span v-if="item.score > 0" class="activity-score-badge">
                                                            {{ item.score }}
                                                        </span>

                                                        <!-- 实际柱子 -->
                                                        <div class="w-full max-w-[40px] rounded-t shadow-sm transition-all duration-300 activity-bar"
                                                            :class="item.score > 0 ? 'activity-bar-positive' : 'activity-bar-negative'"
                                                            :style="{
                                                              height: calculateBarHeight(item.score) + '%',
                                                              minHeight: item.score > 0 ? '18px' : '10px'
                                                            }">
                                                        </div>

                                                        <!-- 透明命中层（关键） -->
                                                        <div class="absolute inset-0 cursor-pointer"
                                                            @mouseenter="showTooltip($event, index, true)"
                                                            @mouseleave="showTooltip($event, index, false)"
                                                            @click="showTooltip($event, index, !tooltipVisible[index])">
                                                        </div>
                                                    </div>

                                                    <!-- X 轴 -->
                                                    <div class="text-[11px] text-gray-500 mt-2 font-medium">
                                                        {{ formatDateByRange(item.date, selectedTimeRange) }}
                                                    </div>
                                                </div>

                                                <div v-if="!activityLoading && activityData.trend.length === 0"
                                                    class="absolute inset-0 flex items-center justify-center text-gray-400 text-sm">
                                                    暂无活跃度数据
                                                </div>
                                            </div>

                                            <Teleport to="body">
                                                <div v-if="activeTooltipIndex !== null"
                                                    class="fixed z-[9999] bg-gray-800 text-white text-xs rounded px-2 py-1.5 shadow-lg pointer-events-none"
                                                    :style="tooltipPosition">
                                                    <div class="flex items-center gap-1 font-medium">
                                                        <el-icon class="text-yellow-300">
                                                            <Star />
                                                        </el-icon>
                                                        {{ activityData.trend[activeTooltipIndex]?.score }} 积分
                                                    </div>
                                                </div>
                                            </Teleport>
                                        </div>
                                    </div>

                                    <div class="activity-stats mt-6 pt-6 border-t border-gray-200">
                                        <h4 class="text-sm font-medium text-gray-700 mb-3">活跃度统计</h4>
                                        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                                            <div
                                                class="stat-card bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                                                <div class="text-xs text-gray-500 mb-1">日均活跃度</div>
                                                <div class="text-xl font-bold text-[var(--theme-primary)]">{{
                                                    activityStatistics.dailyAverage }}</div>
                                            </div>
                                            <div
                                                class="stat-card bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                                                <div class="text-xs text-gray-500 mb-1">周均活跃度</div>
                                                <div class="text-xl font-bold text-[var(--theme-primary-deep)]">{{
                                                    activityStatistics.weeklyAverage }}</div>
                                            </div>
                                            <div
                                                class="stat-card bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                                                <div class="text-xs text-gray-500 mb-1">月均活跃度</div>
                                                <div class="text-xl font-bold text-[var(--theme-primary-soft)]">{{
                                                    activityStatistics.monthlyAverage }}</div>
                                            </div>
                                            <div
                                                class="stat-card bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                                                <div class="text-xs text-gray-500 mb-1">最佳表现</div>
                                                <div class="text-xl font-bold text-[#89A9D6]">{{
                                                    activityStatistics.bestDayScore }}</div>
                                                <div class="text-xs text-gray-500 mt-1">{{
                                                    activityStatistics.bestDay }}</div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </section>

                            <section>
                                <div class="flex justify-between items-center mb-4">
                                    <h3 class="text-lg font-bold text-gray-800 flex items-center gap-2">
                                        <span class="h-5 w-1 rounded-full bg-[var(--theme-primary)]"></span> 最新动态
                                    </h3>
                                    <el-button link type="primary" @click="loadDynamics">刷新</el-button>
                                </div>

                                <div v-loading="dynamicsLoading">
                                    <div v-if="dynamics.length === 0" class="text-center py-8 text-gray-500">
                                        暂无动态
                                    </div>

                                    <el-timeline v-else>
                                        <el-timeline-item v-for="dynamic in dynamics" :key="dynamic.id"
                                            :timestamp="dynamic.createTime" placement="top" type="primary" size="large">
                                            <div class="p-4 rounded-lg border transition-all cursor-pointer hover:shadow-md"
                                                :class="{
                                                    'bg-[rgba(116,149,195,0.1)] border-[rgba(116,149,195,0.18)]': dynamic.type === 1,
                                                    'bg-[rgba(137,169,214,0.12)] border-[rgba(137,169,214,0.22)]': dynamic.type === 2,
                                                    'bg-[rgba(209,223,244,0.4)] border-[rgba(171,192,222,0.28)]': dynamic.type === 3
                                                }" @click="handleDynamicClick(dynamic)">
                                                <h4 class="text-sm text-gray-600 mb-1">
                                                    {{ getDynamicTypeName(dynamic.type) }}
                                                </h4>
                                                <div class="font-medium mt-1 hover:underline" :class="{
                                                    'text-[var(--theme-primary-deep)]': dynamic.type === 1,
                                                    'text-[#6E8EB9]': dynamic.type === 2,
                                                    'text-[#5878A6]': dynamic.type === 3
                                                }">
                                                    {{ dynamic.title }}
                                                </div>
                                                <div v-if="dynamic.contentPreview"
                                                    class="text-gray-500 text-sm mt-2 line-clamp-2">
                                                    {{ dynamic.contentPreview }}
                                                </div>
                                            </div>
                                        </el-timeline-item>
                                    </el-timeline>
                                </div>

                                <div v-if="dynamics.length > 0" class="mt-6 flex justify-center">
                                    <Pagination v-model:current-page="dynamicsPagination.current"
                                        v-model:page-size="dynamicsPagination.size" :total="dynamicsPagination.total"
                                        @page-change="loadDynamics" @size-change="loadDynamics" />
                                </div>
                            </section>
                        </div>

                        <div v-if="activeTab === 'articles' || activeTab === 'drafts'" class="animate-fade-in">
                            <div class="flex justify-between items-center mb-6">
                                <h3 class="text-lg font-bold text-gray-800">{{ activeTab === 'drafts' ? '草稿箱' : '我的文章' }} <span
                                        class="text-gray-400 font-normal text-sm ml-2">共 {{ articlePagination.total || articles.length }} 篇</span>
                                </h3>
                                <div class="flex flex-wrap items-center justify-end gap-3">
                                    <span v-if="!siteConfig.isFeatureEnabled('userPublishEnabled')" class="text-xs text-amber-600 bg-amber-50 border border-amber-100 rounded-full px-3 py-1">
                                        当前已关闭前台投稿
                                    </span>
                                    <el-button v-if="canUserPublish" type="primary" icon="EditPen" round class="shadow-sm"
                                        @click="goToPublish">
                                        写文章
                                    </el-button>
                                </div>
                            </div>

                            <div class="mb-5 flex flex-wrap items-center gap-2">
                                <button v-for="option in articleFilters" :key="option.key" type="button"
                                    class="article-filter-chip"
                                    :class="{ 'article-filter-chip--active': articleStatusFilter === option.key }"
                                    @click="setArticleStatusFilter(option.key)">
                                    {{ option.label }}
                                    <span class="article-filter-chip__count">{{ articleStatusCounts[option.key] || 0 }}</span>
                                </button>
                            </div>

                            <el-empty v-if="!filteredArticles.length" :description="articleEmptyDescription" />
                            <div v-else class="space-y-4">
                                <div v-for="item in filteredArticles" :key="item.id"
                                    class="group flex flex-col md:flex-row justify-between items-start md:items-center p-5 rounded-xl border border-gray-100 hover:border-blue-200 hover:shadow-md hover:bg-blue-50/30 transition-all duration-300">
                                    <div class="flex-1">
                                        <div class="flex items-center gap-3 mb-2">
                                            <span
                                                class="font-bold text-lg text-gray-800 group-hover:text-blue-600 transition cursor-pointer">{{
                                                    item.title }}</span>
                                            <el-tag size="small" :type="getStatusInfo(item.status).type" effect="plain"
                                                class="!bg-transparent">
                                                {{ getStatusInfo(item.status).text }}
                                            </el-tag>
                                        </div>
                                        <div class="text-gray-400 text-sm flex items-center gap-4">
                                            <span>{{ item.createTime }}</span>
                                            <div class="w-px h-3 bg-gray-300"></div>
                                            <span class="flex items-center gap-1"><el-icon>
                                                    <View />
                                                </el-icon> {{ item.readNum }}</span>
                                            <span class="flex items-center gap-1"><el-icon>
                                                    <ChatDotSquare />
                                                </el-icon> 12</span>
                                        </div>
                                    </div>
                                    <div
                                        class="mt-4 md:mt-0 flex gap-3 opacity-0 group-hover:opacity-100 transition-all transform translate-x-4 group-hover:translate-x-0">
                                        <el-tooltip content="编辑" placement="top">
                                            <el-button size="small" circle icon="Edit" @click="editArticle(item.id)"
                                                class="!border-blue-200 !text-blue-600 hover:!bg-blue-600 hover:!text-white"></el-button>
                                        </el-tooltip>
                                        <el-tooltip content="删除" placement="top">
                                            <el-button size="small" circle icon="Delete"
                                                class="!border-red-200 !text-red-600 hover:!bg-red-600 hover:!text-white"></el-button>
                                        </el-tooltip>
                                    </div>
                                </div>
                            </div>

                            <div v-if="filteredArticles.length > 0" class="mt-8 flex justify-center">
                                <Pagination v-model:current-page="articlePagination.current"
                                    v-model:page-size="articlePagination.size" :total="articlePagination.total"
                                    @page-change="loadArticles" @size-change="loadArticles" />
                            </div>
                        </div>

                        <div v-if="activeTab === 'collections'" class="animate-fade-in">
                            <h3 class="text-lg font-bold text-gray-800 mb-6">我的收藏</h3>
                            <div v-if="collectedArticles.length === 0" class="text-center py-12">
                                <div
                                    class="bg-gray-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                                    <el-icon class="text-2xl text-gray-400">
                                        <Star />
                                    </el-icon>
                                </div>
                                <p class="text-gray-500 mb-2">暂无收藏</p>
                                <p class="text-sm text-gray-400 mb-4">快去发现更多优质内容吧</p>
                                <div class="flex justify-center gap-3">
                                    <el-button type="primary" @click="$router.push('/')">去探索</el-button>
                                    <el-button v-if="!isLoggedIn" @click="$router.push('/register')">注册账户</el-button>
                                </div>
                            </div>
                            <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div v-for="article in collectedArticles" :key="article.id"
                                    class="group bg-white rounded-xl border border-gray-100 overflow-hidden hover:shadow-lg transition-all duration-300 cursor-pointer"
                                    @click="$router.push(`/article/${article.id}`)">
                                    <div v-if="article.cover" class="h-48 overflow-hidden">
                                        <img :src="article.cover" :alt="article.title"
                                            class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300">
                                    </div>

                                    <div class="p-5">
                                        <h4
                                            class="font-bold text-gray-800 line-clamp-2 group-hover:text-blue-600 transition mb-3 text-lg">
                                            {{ article.title }}
                                        </h4>

                                        <div class="flex flex-wrap items-center gap-2 mb-3">
                                            <el-tag v-if="article.category" size="small" type="primary" round
                                                class="!text-xs">
                                                {{ typeof article.category === 'string' ? article.category :
                                                    article.category.name }}
                                            </el-tag>
                                            <el-tag v-for="tag in getArticleTags(article)" :key="tag.id || tag.name"
                                                size="small" type="success" round class="!text-xs">
                                                {{ tag.name || tag }}
                                            </el-tag>
                                        </div>

                                        <p v-if="article.summary" class="text-gray-500 text-sm mb-4 line-clamp-2">
                                            {{ article.summary }}
                                        </p>

                                        <div class="flex items-center justify-between text-sm text-gray-500">
                                            <div class="flex items-center gap-3">
                                                <span>{{ formatDate(article.createTime) }}</span>
                                                <span class="flex items-center">
                                                    <el-icon class="mr-1 text-xs">
                                                        <View />
                                                    </el-icon>
                                                    {{ article.views }}
                                                </span>
                                                <span>{{ article.author }}</span>
                                            </div>

                                            <el-button type="danger" size="small" round
                                                @click.stop="uncollectArticleHandler(article.id)"
                                                :loading="article.uncollecting">
                                                取消收藏
                                            </el-button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div v-if="collectedArticles.length > 0" class="mt-8 flex justify-center">
                                <Pagination v-model:current-page="collectedArticlesPagination.current"
                                    v-model:page-size="collectedArticlesPagination.size"
                                    :total="collectedArticlesPagination.total" @page-change="loadCollectedArticles"
                                    @size-change="loadCollectedArticles" />
                            </div>
                        </div>

                        <div v-if="activeTab === 'security'" class="animate-fade-in">
                            <h3 class="text-lg font-bold text-gray-800 mb-6">账号安全</h3>

                            <div
                                class="bg-orange-50 border border-orange-100 p-4 rounded-xl mb-8 flex items-start gap-3">
                                <el-icon class="text-orange-500 mt-1 text-lg">
                                    <WarningFilled />
                                </el-icon>
                                <div class="text-sm text-orange-800 leading-relaxed">
                                    <p class="font-bold mb-1">安全提示</p>
                                    建议定期修改密码以保护账号安全。如果您发现账号异常，请立即联系管理员。
                                </div>
                            </div>

                            <div class="max-w-xl space-y-6">
                                <div class="flex items-center justify-between py-4 border-b border-gray-50">
                                    <div>
                                        <div class="text-gray-800 font-medium">绑定邮箱</div>
                                        <div class="text-gray-400 text-sm mt-1">已绑定: {{ user.email }}</div>
                                    </div>
                                    <el-button link type="primary">更换邮箱</el-button>
                                </div>

                                <div class="flex items-center justify-between py-4 border-b border-gray-50">
                                    <div>
                                        <div class="text-gray-800 font-medium">登录密码</div>
                                        <div class="text-gray-400 text-sm mt-1">建议每 3 个月修改一次</div>
                                    </div>
                                    <el-button link type="primary">修改密码</el-button>
                                </div>

                                <div class="pt-6">
                                    <h4 class="text-red-600 font-bold text-sm mb-4">危险区域</h4>
                                    <div
                                        class="border border-red-100 bg-red-50/30 rounded-xl p-4 flex items-center justify-between">
                                        <div>
                                            <div class="font-medium text-gray-800">退出登录</div>
                                            <div class="text-xs text-gray-500 mt-1">退出当前账号，需要重新登录</div>
                                        </div>
                                        <el-button round size="small" @click="logout">退出</el-button>
                                    </div>
                                     <div
                                         class="border border-red-100 bg-red-50/30 rounded-xl p-4 flex items-center justify-between mt-3">
                                         <div>
                                             <div class="font-medium text-gray-800">注销账号</div>
                                             <div class="text-xs text-gray-500 mt-1">输入登录密码确认后，将注销账号并逻辑删除你的文章</div>
                                         </div>
                                         <el-button type="danger" plain size="small" @click="openDeleteAccountDialog">注销</el-button>
                                     </div>
                                 </div>
                             </div>
                         </div>

                        <div v-if="activeTab === 'comments'" class="animate-fade-in">
                            <h3 class="text-lg font-bold text-gray-800 mb-6">评论历史</h3>

                            <div v-loading="commentHistoryLoading" class="space-y-4">
                                <div v-if="commentHistory.length === 0" class="text-center py-12">
                                    <div class="text-gray-400 mb-2">
                                        <el-icon class="text-3xl mb-2">
                                            <ChatDotSquare />
                                        </el-icon>
                                        <p>暂无评论记录</p>
                                    </div>
                                </div>

                                <div v-for="comment in commentHistory" :key="comment.id"
                                    class="bg-white border border-gray-100 rounded-xl p-4 hover:shadow-sm transition-shadow">
                                    <div class="flex justify-between items-start mb-2">
                                        <h4 class="font-medium text-gray-800 truncate flex-1 mr-2">
                                            评论文章：<span class="text-blue-600 hover:underline cursor-pointer"
                                                @click="router.push('/article/' + comment.articleId)">
                                                {{ comment.articleTitle }}
                                            </span>
                                        </h4>
                                        <span class="text-xs text-gray-400 whitespace-nowrap">{{ comment.createTime
                                        }}</span>
                                    </div>
                                    <div class="text-gray-600 text-sm bg-gray-50 p-3 rounded-lg mt-2">
                                        {{ comment.content }}
                                    </div>
                                    <div class="flex items-center justify-between mt-3 pt-3 border-t border-gray-50">
                                        <div class="flex items-center gap-4 text-xs text-gray-500">
                                            <span class="flex items-center gap-1">
                                                <el-icon>
                                                    <ChatDotSquare />
                                                </el-icon>
                                                {{ comment.likeCount }} 点赞
                                            </span>
                                        </div>
                                        <el-button type="danger" link size="small" @click="deleteComment(comment.id)">
                                            <el-icon>
                                                <Delete />
                                            </el-icon>
                                            删除
                                        </el-button>
                                    </div>
                                </div>
                            </div>

                            <div v-if="commentHistory.length > 0" class="mt-8 flex justify-center">
                                <Pagination v-model:current-page="commentHistoryPagination.current"
                                    v-model:page-size="commentHistoryPagination.size"
                                    :total="commentHistoryPagination.total" @page-change="loadCommentHistory"
                                    @size-change="loadCommentHistory" />
                            </div>
                        </div>

                    </div>
                </div>
            </div>

            <el-dialog v-model="showEditProfile" title="编辑个人资料" width="500px" align-center class="!rounded-2xl">
                <el-form-item label="昵称">
                    <el-input v-model="editForm.nickname" maxlength="20" show-word-limit placeholder="请输入昵称" />
                </el-form-item>
                <el-form-item label="简介">
                    <el-input v-model="editForm.introduction" type="textarea" :rows="4" maxlength="100" show-word-limit
                        placeholder="写一段话介绍自己..." resize="none" />
                </el-form-item>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="showEditProfile = false" round>取消</el-button>
                        <el-button type="primary" :loading="profileSaving" @click="saveProfile" round>保存修改</el-button>
                    </span>
                </template>
            </el-dialog>

            <el-dialog v-model="showDeleteAccountDialog" title="注销账号" width="460px" align-center class="!rounded-2xl"
                :before-close="closeDeleteAccountDialog">
                <div class="mb-4 rounded-2xl border border-red-100 bg-red-50/40 px-4 py-3 text-sm text-red-700">
                    该操作会逻辑删除当前账号及其文章，且无法恢复。请输入登录密码确认。
                </div>
                <el-form @submit.prevent="confirmDeleteAccount">
                    <el-form-item label="登录密码">
                        <el-input v-model="deleteAccountForm.password" type="password" show-password
                            placeholder="请输入当前登录密码" autocomplete="current-password" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="closeDeleteAccountDialog" round>取消</el-button>
                        <el-button type="danger" :loading="deleteAccountSaving" @click="confirmDeleteAccount" round>确认注销</el-button>
                    </span>
                </template>
            </el-dialog>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed, watch } from 'vue'

import {
    Document, Star, ChatLineRound, Lock, Edit, Delete,
    ArrowLeft, Camera, Location, EditPen, View, ChatDotSquare,
    Odometer, WarningFilled, Calendar, Setting
} from '@element-plus/icons-vue'
import moment from 'moment'
import { useUserStore } from '@/stores/user'
import { useSiteConfigStore } from '@/stores/siteConfig'
import { getActivityLevelLabel } from '@/composables/activityLevel'
import { getArticlePageList } from "@/api/frontend/article";
import { getCollectedArticles, uncollectArticle } from "@/api/frontend/favorite";
import { getUserCenterStatistics, getUserCenterComments, getUserCenterOverview, getActivityScore, getActivityStatistics, getActivityTrend, getCurrentUserLocation, updateUserProfile, deleteUserAccount } from "@/api/frontend/user";
import { deleteComment as deleteCommentApi } from "@/api/frontend/comment";
import { uploadFile } from "@/api/frontend/file";
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Pagination from '@/components/frontend/Pagination.vue'
const city = ref('')
const province = ref('')
const locationLabel = ref('')

const loadCurrentLocation = async () => {
    try {
        const res = await getCurrentUserLocation()
        const data = res?.data || {}
        city.value = data.city || ''
        province.value = data.province || ''
        locationLabel.value = data.location || ''
    } catch (error) {
        city.value = ''
        province.value = ''
        locationLabel.value = '未知位置'
    }
}

const userStore = useUserStore()
const siteConfig = useSiteConfigStore()
const user = computed(() => userStore.frontendUserInfo)
const canUserPublish = computed(() => siteConfig.isFeatureEnabled('userPublishEnabled') === true)
const roleDisplayName = computed(() => {
    const roles = user.value?.roles || []

    if (Array.isArray(roles)) {
        if (roles.includes('ROLE_ADMIN')) {
            return '管理员'
        }

        if (roles.includes('ROLE_EDITOR')) {
            return '编辑'
        }

        if (roles.includes('ROLE_VISITOR')) {
            return '访客'
        }
    }

    return '用户'
})
const currentLocationText = computed(() => {
    const parts = [province.value, city.value].filter(Boolean)
    return parts.length ? parts.join(' · ') : (locationLabel.value || '未知位置')
})
const router = useRouter()
const defaultAvatar = `${import.meta.env.BASE_URL}default-avatar.svg`
const displayAvatar = computed(() => user.value?.avatar || defaultAvatar)

const articleCount = ref(0)
const articlePagination = reactive({
    current: 1,
    size: 10,
    total: 0,
    pages: 0
})
const collectedArticlesPagination = reactive({
    current: 1,
    size: 10,
    total: 0,
    pages: 0
})
const favoriteCount = ref(0)
const commentCount = ref(0)
const articleStatusFilter = ref('all')

const articleFilters = computed(() => [
    { key: 'all', label: '全部' },
    { key: '3', label: '草稿' },
    { key: '0', label: '待审核' },
    { key: '1', label: '审核通过' },
    { key: '4', label: '已发布' },
    { key: '2', label: '未通过' }
])

const articleStatusCounts = computed(() => {
    const counts = {
        all: articles.value.length,
        '0': 0,
        '1': 0,
        '2': 0,
        '3': 0,
        '4': 0
    }

    articles.value.forEach((article) => {
        const key = String(article.status)
        if (Object.prototype.hasOwnProperty.call(counts, key)) {
            counts[key] += 1
        }
    })

    return counts
})

const filteredArticles = computed(() => {
    if (articleStatusFilter.value === 'all') {
        return articles.value
    }

    return articles.value.filter((article) => String(article.status) === articleStatusFilter.value)
})

const articleEmptyDescription = computed(() => {
    if (activeTab.value === 'drafts' && articleStatusFilter.value === '3') {
        return '暂无草稿，先写一篇内容试试吧'
    }

    if (articleStatusFilter.value === 'all') {
        return '暂无文章'
    }

    return `暂无${articleFilters.value.find((item) => item.key === articleStatusFilter.value)?.label || '该状态'}文章`
})

// 评论历史相关
const commentHistory = ref([])
const commentHistoryLoading = ref(false)
const commentHistoryPagination = reactive({
    current: 1,
    size: 10,
    total: 0,
    pages: 0
})

// 动态相关
const dynamics = ref([])
const dynamicsLoading = ref(false)
const dynamicsPagination = reactive({
    current: 1,
    size: 10,
    total: 0,
    pages: 0
})

// 活跃度相关
const activityData = ref({
    totalScore: 0,
    rank: 0,
    totalUsers: 0,
    activities: [],
    trend: []
})
const activityLevelLabel = computed(() => {
    return getActivityLevelLabel(activityData.value.totalScore, siteConfig.siteInfo.activityLevelRules)
})
const activityStatistics = ref({
    dailyAverage: 0,
    weeklyAverage: 0,
    monthlyAverage: 0,
    bestDay: '',
    bestDayScore: 0
})
const activityLoading = ref(false)
const selectedTimeRange = ref('week')
const tooltipVisible = ref({})
const activeTooltipIndex = ref(null)

const tooltipPosition = ref({
    left: '0px',
    top: '0px',
    transform: 'translate(-50%, 0)'
})

const showTooltip = (event, index, visible) => {
    tooltipVisible.value[index] = visible

    if (visible && event) {
        const rect = event.currentTarget.getBoundingClientRect()
        activeTooltipIndex.value = index
        tooltipPosition.value = {
            left: `${rect.left + rect.width / 2}px`,
            top: `${rect.bottom + 8}px`,
            transform: 'translate(-50%, 0)'
        }
    }

    if (!visible) {
        activeTooltipIndex.value = null
    }
}

/* ======================= 高度计算（关键修复） ======================= */

const getMaxScore = () => {
    const scores = activityData.value.trend.map(i => i.score)
    return Math.max(...scores, 1)
}

const calculateBarHeight = (score) => {
    const maxScore = getMaxScore()
    const minHeight = 10
    const positiveMinHeight = 18
    const maxHeight = 88

    if (score <= 0) return minHeight
    return Math.max(positiveMinHeight, (score / maxScore) * maxHeight)
}

/* ======================= 其他 ======================= */


const formatDateByRange = (date) => {
    const d = new Date(date)
    return `${d.getMonth() + 1}/${d.getDate()}`
}



// Function to check if user has a specific role
const hasRole = (role) => {
    if (!user.value) return false

    // Handle case where roles is an array
    if (Array.isArray(user.value.roles)) {
        return user.value.roles.includes(role)
    }

    // Handle case where role is a single string
    if (typeof user.value.role === 'string') {
        return user.value.role === role
    }

    return false
}

const activeTab = ref('overview')

const activeTabName = computed(() => {
    const map = {
        overview: '总览',
        articles: '我的文章',
        drafts: '草稿箱',
        collections: '我的收藏',
        comments: '评论历史',
        security: '安全设置'
    }
    return map[activeTab.value]
})

const showEditProfile = ref(false)
const showDeleteAccountDialog = ref(false)
const profileSaving = ref(false)
const deleteAccountSaving = ref(false)
const avatarUploading = ref(false)
const avatarInputRef = ref(null)
const editForm = reactive({
    nickname: '',
    introduction: '',
    avatar: ''
})
const deleteAccountForm = reactive({
    password: ''
})

const articles = ref([]) // 初始为空数组
const collectedArticles = ref([]) // 收藏的文章

// 1. 定义状态映射逻辑
const getStatusInfo = (status) => {
    const map = {
        0: { text: '待审核', type: 'warning' },      // 黄色
        1: { text: '审核通过', type: 'primary' },    // 蓝色 (或绿色)
        2: { text: '审核未通过', type: 'danger' },  // 红色
        3: { text: '未发布', type: 'info' },        // 灰色
        4: { text: '已发布', type: 'success' }      // 绿色
    }
    // 默认为 待审核 或 未知
    return map[status] || { text: '未知状态', type: 'info' }
}

// 检查用户是否已登录
const isLoggedIn = computed(() => {
    return !!userStore.frontendUserInfo && !!userStore.frontendUserInfo.userId
})

// 3. 增加 status 字段的映射
const normalizeArticles = (list) => list.map(a => ({
    id: a.id || a.articleId || a._id,
    title: a.title || a.articleTitle,
    createTime: a.createTime || a.createDate,
    author: a.author,
    readNum: a.readNum || 0,
    status: a.status !== undefined ? a.status : 0, // 默认为0
    cover: a.cover,
    category: a.category,
    tags: a.tags,
    summary: a.summary || a.excerpt
}))

// 获取文章标签
const getArticleTags = (article) => {
    if (!article || !article.tags) return []
    if (Array.isArray(article.tags)) return article.tags
    if (typeof article.tags === 'string') return [{ name: article.tags }]
    return []
}

const loadArticles = (page = 1, size = 10) => {
    const userId = user.value.userId || 0
    // 如果没有userId，可能不应该请求，或者后端支持查当前登录用户
    getArticlePageList({ current: page, size, name: '', userId }).then(res => {
        if (res && res.success && res.data) {
            let list = []
            let total = 0
            let pages = 0

            if (Array.isArray(res.data)) {
                list = res.data
                total = res.total || 0
                pages = res.pages || 0
            }
            else if (Array.isArray(res.data.records)) {
                list = res.data.records
                total = res.data.total || res.total || 0
                pages = res.data.pages || res.pages || 0
            }
            else if (Array.isArray(res.data.list)) {
                list = res.data.list
                total = res.data.total || res.total || 0
                pages = res.data.pages || res.pages || 0
            }
            else if (Array.isArray(res.data.rows)) {
                list = res.data.rows
                total = res.data.total || res.total || 0
                pages = res.data.pages || res.pages || 0
            }

            articles.value = normalizeArticles(list)
            articlePagination.total = total
            articlePagination.pages = pages
            articlePagination.current = page
            articlePagination.size = size
        } else if (res && !res.success && res.errorCode === '20002') {
            // 特殊处理认证错误
            ElMessage.warning('请先登录后再查看文章列表')
            router.push('/login')
        }
    }).catch((error) => {
        console.error('加载文章失败:', error)
        // 检查是否是权限错误
        if (error.response && error.response.data) {
            const errorMsg = error.response.data.message || error.response.data.errorMsg
            if (errorMsg && (errorMsg.includes('无访问权限') || errorMsg.includes('请先登录'))) {
                ElMessage.warning('请先登录后再查看文章列表')
                router.push('/login')
                return
            }
        }
        ElMessage.error('加载文章失败')
    })
}

// 加载收藏的文章
const loadCollectedArticles = (page = 1, size = 10) => {
    // 检查用户是否已登录
    if (!isLoggedIn.value) {
        ElMessage.warning('请先登录后再查看收藏文章')
        router.push('/login')
        return
    }

    getCollectedArticles({ current: page, size }).then(res => {
        if (res && res.success && res.data) {
            let list = []
            let total = 0
            let pages = 0

            if (Array.isArray(res.data)) {
                list = res.data
                total = res.total || 0
                pages = res.pages || 0
            }
            else if (Array.isArray(res.data.records)) {
                list = res.data.records
                total = res.data.total || res.total || 0
                pages = res.data.pages || res.pages || 0
            }
            else if (Array.isArray(res.data.list)) {
                list = res.data.list
                total = res.data.total || res.total || 0
                pages = res.data.pages || res.pages || 0
            }
            else if (Array.isArray(res.data.rows)) {
                list = res.data.rows
                total = res.data.total || res.total || 0
                pages = res.data.pages || res.pages || 0
            }

            collectedArticles.value = normalizeArticles(list)
            collectedArticlesPagination.total = total
            collectedArticlesPagination.pages = pages
            collectedArticlesPagination.current = page
            collectedArticlesPagination.size = size
        } else if (res && !res.success && res.errorCode === '20002') {
            // 特殊处理认证错误
            ElMessage.warning('请先登录后再查看收藏文章')
            router.push('/login')
        }
    }).catch((error) => {
        console.error('加载收藏文章失败:', error)
        // 检查是否是权限错误
        if (error.response && error.response.data) {
            const errorMsg = error.response.data.message || error.response.data.errorMsg
            if (errorMsg && (errorMsg.includes('无访问权限') || errorMsg.includes('请先登录'))) {
                ElMessage.warning('请先登录后再查看收藏文章')
                router.push('/login')
                return
            }
        }
        ElMessage.error('加载收藏文章失败')
    })
}

const loadStatistics = () => {
    getUserCenterStatistics().then(res => {
        if (res && res.success && res.data) {
            articleCount.value = res.data.articleCount || 0
            favoriteCount.value = res.data.favoriteCount || 0
            commentCount.value = res.data.commentCount || 0
        }
    }).catch((error) => {
        console.error('加载用户统计数据失败:', error)
    })
}

// 加载评论历史
const loadCommentHistory = () => {
    commentHistoryLoading.value = true

    // 使用查询参数而不是请求体
    const params = {
        current: commentHistoryPagination.current,
        size: commentHistoryPagination.size
    }

    getUserCenterComments(params).then(res => {
        if (res && res.success) {
            commentHistory.value = res.data || []
            commentHistoryPagination.total = res.total || 0
            commentHistoryPagination.pages = res.pages || 0
        }
    }).catch((error) => {
        console.error('加载评论历史失败:', error)
        ElMessage.error('加载评论历史失败')
    }).finally(() => {
        commentHistoryLoading.value = false
    })
}

// 加载动态
const loadDynamics = () => {
    dynamicsLoading.value = true

    const params = {
        current: dynamicsPagination.current,
        size: dynamicsPagination.size
    }

    getUserCenterOverview(params).then(res => {
        if (res && res.success) {
            dynamics.value = res.data || []
            dynamicsPagination.total = res.total || 0
            dynamicsPagination.pages = res.pages || 0
        }
    }).catch((error) => {
        console.error('加载动态失败:', error)
        ElMessage.error('加载动态失败')
    }).finally(() => {
        dynamicsLoading.value = false
    })
}

// 加载活跃度数据
const loadActivityScore = (timeRange = 'week') => {
    activityLoading.value = true

    const data = {
        timeRange: timeRange
    }

    getActivityScore(data).then(res => {
        if (res && res.success) {
            activityData.value = res.data || {
                totalScore: 0,
                rank: 0,
                totalUsers: 0,
                activities: [],
                trend: []
            }
            // 同时加载活跃度统计数据
            loadActivityStatistics()
            // 同时加载活跃度趋势数据
            loadActivityTrend(timeRange)
        } else {
            ElMessage.error(res.message || '获取活跃度数据失败')
        }
    }).catch((error) => {
        console.error('获取活跃度数据失败:', error)
        ElMessage.error('获取活跃度数据失败')
    }).finally(() => {
        activityLoading.value = false
    })
}

// 更改时间范围
const changeTimeRange = (range) => {
    selectedTimeRange.value = range
    loadActivityScore(range)
    loadActivityTrend(range)
}

// 加载活跃度统计数据
const loadActivityStatistics = () => {
    getActivityStatistics().then(res => {
        if (res && res.success) {
            activityStatistics.value = res.data || {
                dailyAverage: 0,
                weeklyAverage: 0,
                monthlyAverage: 0,
                bestDay: '',
                bestDayScore: 0
            }
        } else {
            ElMessage.error(res.message || '获取活跃度统计数据失败')
        }
    }).catch((error) => {
        console.error('获取活跃度统计数据失败:', error)
        ElMessage.error('获取活跃度统计数据失败')
    })
}

// 加载活跃度趋势数据
const loadActivityTrend = (timeRange = 'week') => {
    const params = {
        timeRange: timeRange
    }

    getActivityTrend(params).then(res => {
        if (res && res.success) {
            // 更新activityData中的trend数据
            activityData.value.trend = res.data || []
        } else {
            ElMessage.error(res.message || '获取活跃度趋势数据失败')
        }
    }).catch((error) => {
        console.error('获取活跃度趋势数据失败:', error)
        ElMessage.error('获取活跃度趋势数据失败')
    })
}


const goHome = () => {
    router.push('/')
}

const handleAvatarError = (event) => {
    if (event.target.src.endsWith('default-avatar.svg')) {
        return
    }
    event.target.src = defaultAvatar
}

const logout = () => {
    // Call the store's logout function to clear user info
    userStore.logout()
    // Redirect to home page
    router.push('/')
}

const openDeleteAccountDialog = () => {
    deleteAccountForm.password = ''
    showDeleteAccountDialog.value = true
}

const closeDeleteAccountDialog = (done) => {
    if (deleteAccountSaving.value) {
        return
    }

    showDeleteAccountDialog.value = false
    deleteAccountForm.password = ''
    done?.()
}

// Navigate to admin panel
const goToAdminPanel = () => {
    router.push('/admin')
}

// 新增：打开弹窗时，把当前用户信息赋值给表单，实现回显
const openEditDialog = () => {
    // 确保从 user store 中获取最新数据
    if (user.value) {
        editForm.nickname = user.value.nickname || ''
        editForm.introduction = user.value.introduction || ''
        editForm.avatar = user.value.avatar || ''
    }
    showEditProfile.value = true
}

const setArticleStatusFilter = (status) => {
    articleStatusFilter.value = status
}

const jumpToDrafts = () => {
    articleStatusFilter.value = '3'
    activeTab.value = 'drafts'
}

const refreshOverview = () => {
    if (activeTab.value !== 'overview') {
        activeTab.value = 'overview'
        return
    }

    loadDynamics()
    loadActivityScore()
}

const saveProfile = async () => {
    if (profileSaving.value) {
        return
    }

    profileSaving.value = true
    try {
        const res = await updateUserProfile({
            nickname: editForm.nickname,
            introduction: editForm.introduction,
            avatar: editForm.avatar
        })

        if (!res?.success) {
            ElMessage.error(res?.message || '保存失败')
            return
        }

        await userStore.ensureFrontendUserInfoReady(true)
        showEditProfile.value = false
        ElMessage.success('个人资料已更新')
    } catch (error) {
        console.error('保存个人资料失败:', error)
        ElMessage.error(error?.response?.data?.message || '保存失败')
    } finally {
        profileSaving.value = false
    }
}

const confirmDeleteAccount = async () => {
    if (deleteAccountSaving.value) {
        return
    }

    const password = deleteAccountForm.password?.trim()
    if (!password) {
        ElMessage.error('请输入登录密码')
        return
    }

    deleteAccountSaving.value = true
    try {
        const res = await deleteUserAccount({ password })
        if (!res?.success) {
            ElMessage.error(res?.message || '账号注销失败')
            return
        }

        userStore.logout()
        showDeleteAccountDialog.value = false
        deleteAccountForm.password = ''
        ElMessage.success('账号已注销')
        await router.push('/')
    } catch (error) {
        console.error('注销账号失败:', error)
        ElMessage.error(error?.response?.data?.message || '账号注销失败')
    } finally {
        deleteAccountSaving.value = false
    }
}

const triggerAvatarUpload = () => {
    if (avatarUploading.value) {
        return
    }
    avatarInputRef.value?.click()
}

const handleAvatarChange = async (event) => {
    const file = event?.target?.files?.[0]
    if (!file) {
        return
    }

    if (!['image/jpeg', 'image/png'].includes(file.type)) {
        ElMessage.error('头像图片只能是 JPG/PNG 格式')
        event.target.value = ''
        return
    }

    if (file.size / 1024 / 1024 > 2) {
        ElMessage.error('头像图片大小不能超过 2MB')
        event.target.value = ''
        return
    }

    avatarUploading.value = true
    try {
        const formData = new FormData()
        formData.append('file', file)

        const uploadRes = await uploadFile(formData)
        const avatarUrl = uploadRes?.data?.url
        if (!uploadRes?.success || !avatarUrl) {
            ElMessage.error(uploadRes?.message || '头像上传失败')
            return
        }

        const saveRes = await updateUserProfile({ avatar: avatarUrl })
        if (!saveRes?.success) {
            ElMessage.error(saveRes?.message || '头像保存失败')
            return
        }

        editForm.avatar = avatarUrl
        await userStore.ensureFrontendUserInfoReady(true)
        ElMessage.success('头像已更新')
    } catch (error) {
        console.error('修改头像失败:', error)
        ElMessage.error(error?.response?.data?.message || '修改头像失败')
    } finally {
        avatarUploading.value = false
        event.target.value = ''
    }
}

// 处理 tab 选择
const handleTabSelect = (index) => {
    activeTab.value = index

    if (index === 'articles') {
        articleStatusFilter.value = 'all'
    } else if (index === 'drafts') {
        articleStatusFilter.value = '3'
    }
}

// 获取动态类型名称
const getDynamicTypeName = (type) => {
    const typeMap = {
        1: '发布了文章',
        2: '评论了文章',
        3: '收藏了文章'
    }
    return typeMap[type] || '未知动态'
}

// 获取活动类型名称
const getActivityTypeName = (type) => {
    const typeMap = {
        'article': '发布文章',
        'comment': '发表评论',
        'favorite': '收藏文章',
        'like': '点赞评论',
        'login': '每日登录'
    }
    return typeMap[type] || '其他活动'
}



// 处理动态点击
const handleDynamicClick = (dynamic) => {
    switch (dynamic.type) {
        case 1: // 文章
            router.push(`/article/${dynamic.relatedId}`)
            break
        case 2: // 评论
        case 3: // 收藏
            router.push(`/article/${dynamic.relatedId}`)
            break
        default:
            console.log('未知动态类型:', dynamic.type)
    }
}

function formatDate(timestamp) {
    if (!timestamp) return ''
    return moment(timestamp).format('YYYY-MM-DD')
}

// 跳转到写文章页面
const goToPublish = () => {
    router.push('/article/publish')
}

// 编辑文章
const editArticle = (id) => {
    router.push(`/article/edit/${id}`)
}

// 取消收藏文章
const uncollectArticleHandler = async (articleId) => {
    try {
        // 在收藏列表中找到对应的文章并设置加载状态
        const article = collectedArticles.value.find(a => a.id === articleId)
        if (article) {
            article.uncollecting = true
        }

        await uncollectArticle(articleId)
        ElMessage.success('已取消收藏')

        // 从收藏列表中移除
        collectedArticles.value = collectedArticles.value.filter(a => a.id !== articleId)
    } catch (error) {
        console.error('取消收藏失败:', error)
        // 检查是否是权限错误
        if (error.response && error.response.data) {
            const errorMsg = error.response.data.message || error.response.data.errorMsg
            if (errorMsg && (errorMsg.includes('无访问权限') || errorMsg.includes('请先登录'))) {
                ElMessage.warning('请先登录后再进行收藏操作')
                router.push('/login')
                return
            }
        }
        ElMessage.error('取消收藏失败')
    } finally {
        // 重置加载状态
        const article = collectedArticles.value.find(a => a.id === articleId)
        if (article) {
            article.uncollecting = false
        }
    }
}

// 删除评论
const deleteComment = async (commentId) => {
    try {
        await deleteCommentApi(commentId)
        ElMessage.success('评论删除成功')
        // 重新加载评论列表
        loadCommentHistory()
    } catch (error) {
        console.error('删除评论失败:', error)
        ElMessage.error('删除评论失败')
    }
}

// 监听 tab 切换，加载相应数据
watch(activeTab, (newTab) => {
    if (newTab === 'collections') {
        loadCollectedArticles()
    } else if (newTab === 'articles' || newTab === 'drafts') {
        loadArticles()
    } else if (newTab === 'comments') {
        loadCommentHistory()
    } else if (newTab === 'overview') {
        loadDynamics()
        loadActivityScore()
    }
})

const recentActiveDays = computed(() => activityData.value.trend.filter((item) => item.score > 0).length)

const quickActionItems = computed(() => [
    { label: '刷新数据', desc: '重新加载概览', action: refreshOverview },
    { label: '编辑资料', desc: '修改昵称和简介', action: openEditDialog },
    ...(canUserPublish.value ? [{ label: '写文章', desc: '开始一篇新内容', action: goToPublish, type: 'primary' }] : [])
])

// 初始化加载统计数据和概览模块
onMounted(() => {
    loadCurrentLocation()
    siteConfig.fetchPermissions().catch((error) => {
        console.error('Failed to load publish permissions:', error)
    })

    userStore.setFrontendUserInfo().then(() => {
        loadStatistics()
        if (activeTab.value === 'overview') {
            loadDynamics()
            loadActivityScore()
        }
    }).catch((error) => {
        console.error('Failed to load user info:', error)
        loadStatistics()
        if (activeTab.value === 'overview') {
            loadDynamics()
            loadActivityScore()
        }
    })
})

</script>

<style scoped>
.animate-fade-in {
    animation: fadeIn 0.4s ease-out;
}

.admin-identity-badge {
    position: relative;
    display: inline-flex;
    align-items: center;
    gap: 0.42rem;
    overflow: hidden;
    border-radius: 999px;
    border: 1px solid rgba(53, 82, 122, 0.16);
    background: linear-gradient(135deg, rgba(44, 71, 109, 0.96), rgba(74, 110, 165, 0.94));
    padding: 0.34rem 0.8rem 0.34rem 0.52rem;
    box-shadow: 0 14px 28px rgba(66, 93, 136, 0.18);
    color: #f8fbff;
}

.admin-identity-badge__glow {
    position: absolute;
    inset: auto auto -16px -10px;
    width: 56px;
    height: 32px;
    border-radius: 999px;
    background: radial-gradient(circle, rgba(174, 214, 255, 0.42), transparent 70%);
    pointer-events: none;
}

.admin-identity-badge__icon {
    position: relative;
    z-index: 1;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 20px;
    height: 20px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.16);
    box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.12);
    font-size: 11px;
}

.admin-identity-badge__text {
    position: relative;
    z-index: 1;
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 0.08em;
}

.profile-stat-card {
    position: relative;
    display: flex;
    min-height: 100px;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    border-radius: 20px;
    border: 1px solid rgba(129, 158, 196, 0.18);
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(244, 248, 253, 0.96));
    box-shadow: 0 14px 28px rgba(120, 146, 184, 0.08);
    backdrop-filter: blur(16px);
    transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
}

.profile-stat-card:hover {
    transform: translateY(-2px);
    border-color: rgba(116, 149, 195, 0.28);
    box-shadow: 0 18px 32px rgba(108, 137, 184, 0.12);
}

.profile-panel-shell {
    width: 100%;
    border-radius: 24px;
    border: 1px solid rgba(129, 158, 196, 0.18);
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(247, 250, 255, 0.92));
    padding: 14px;
    box-shadow: 0 18px 36px rgba(120, 146, 184, 0.1);
    backdrop-filter: blur(18px);
}

.profile-panel-heading {
    margin-bottom: 12px;
    padding-left: 4px;
    font-size: 11px;
    font-weight: 700;
    letter-spacing: 0.22em;
    color: #64748b;
}

.profile-stat-accent {
    position: absolute;
    top: 12px;
    left: 50%;
    width: 34px;
    height: 4px;
    border-radius: 999px;
    transform: translateX(-50%);
    background: linear-gradient(90deg, rgba(107, 148, 228, 0.18), rgba(85, 127, 210, 0.72), rgba(107, 148, 228, 0.18));
}

.profile-action-btn {
    min-height: 46px;
    box-shadow: 0 12px 26px rgba(108, 137, 184, 0.1);
    transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease, background 0.22s ease;
}

.profile-action-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 16px 30px rgba(108, 137, 184, 0.14);
}

.profile-action-btn--primary {
    background: linear-gradient(135deg, #6f97e7, #5a82d4) !important;
    border-color: rgba(98, 136, 214, 0.88) !important;
    color: #ffffff !important;
}

.profile-action-btn--secondary {
    border: 1px solid rgba(129, 158, 196, 0.28) !important;
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(246, 249, 253, 0.94)) !important;
    color: #35527a !important;
}

.profile-quick-links {
    display: grid;
    width: 100%;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 12px;
}

.profile-quick-panel {
    width: 100%;
    border-radius: 24px;
    border: 1px solid rgba(129, 158, 196, 0.16);
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.66), rgba(247, 250, 255, 0.9));
    padding: 14px;
    box-shadow: 0 16px 34px rgba(120, 146, 184, 0.08);
    backdrop-filter: blur(18px);
}

.profile-quick-panel__title {
    margin-bottom: 12px;
    padding-left: 4px;
    font-size: 11px;
    font-weight: 700;
    letter-spacing: 0.22em;
    color: #64748b;
}

.profile-quick-link {
    display: flex;
    min-height: 72px;
    flex-direction: column;
    justify-content: center;
    gap: 4px;
    border-radius: 16px;
    border: 1px solid rgba(129, 158, 196, 0.18);
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(246, 249, 253, 0.92));
    padding: 12px 14px;
    text-align: left;
    box-shadow: 0 12px 26px rgba(120, 146, 184, 0.08);
    transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
}

.profile-quick-link:hover {
    transform: translateY(-2px);
    border-color: rgba(116, 149, 195, 0.28);
    box-shadow: 0 18px 30px rgba(108, 137, 184, 0.12);
}

.profile-quick-link--primary {
    background: linear-gradient(135deg, rgba(109, 145, 226, 0.98), rgba(83, 122, 206, 0.94));
    color: #ffffff;
}

.profile-quick-link--primary .profile-quick-link__desc {
    color: rgba(255, 255, 255, 0.84);
}

.profile-quick-link__title {
    font-size: 14px;
    font-weight: 700;
    color: inherit;
}

.profile-quick-link__desc {
    font-size: 12px;
    color: #6b7d97;
}

.profile-quick-note {
    font-size: 12px;
    color: #8a97ab;
    text-align: center;
}

.article-filter-chip {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    border-radius: 999px;
    border: 1px solid rgba(129, 158, 196, 0.18);
    background: rgba(255, 255, 255, 0.82);
    padding: 8px 14px;
    font-size: 13px;
    font-weight: 600;
    color: #58749c;
    transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease, background 0.2s ease;
}

.article-filter-chip:hover {
    transform: translateY(-1px);
    border-color: rgba(116, 149, 195, 0.28);
    box-shadow: 0 10px 22px rgba(120, 146, 184, 0.1);
}

.article-filter-chip--active {
    background: linear-gradient(135deg, rgba(111, 151, 231, 0.18), rgba(90, 130, 212, 0.18));
    border-color: rgba(90, 130, 212, 0.3);
    color: #35527a;
}

.article-filter-chip__count {
    min-width: 20px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.72);
    padding: 0 6px;
    font-size: 11px;
    font-weight: 700;
    line-height: 18px;
    color: #5e77a0;
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(10px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.custom-menu .el-menu-item {
    border-radius: 12px;
    margin-bottom: 8px;
    height: 50px;
    color: #64748b;
    font-weight: 500;
    background: transparent;
    transition: all 0.2s;
}

.custom-menu .el-menu-item:hover {
    background-color: rgba(240, 245, 251, 0.92);
    color: #1e3a5f;
}

.custom-menu .el-menu-item.is-active {
    background: linear-gradient(135deg, rgba(116, 149, 195, 0.14), rgba(209, 223, 244, 0.58));
    color: #4e6d97;
    font-weight: 600;
    box-shadow: inset 0 0 0 1px rgba(116, 149, 195, 0.16);
}

.custom-scrollbar::-webkit-scrollbar {
    height: 4px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
    background-color: #cbd5e1;
    border-radius: 4px;
}

.custom-scrollbar::-webkit-scrollbar-track {
    background-color: transparent;
}


/* 文章卡片中的文本截断 */
.line-clamp-2 {
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

/* 活跃度部分样式 */
.activity-section {
    padding: 1.5rem;
    background: linear-gradient(180deg, rgba(248, 251, 255, 0.98), rgba(242, 247, 252, 0.96));
    border-radius: 1.1rem;
    border: 1px solid rgba(129, 158, 196, 0.16);
}

.activity-summary {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 1.5rem 0;
    padding: 1rem;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 0.9rem;
    border: 1px solid rgba(129, 158, 196, 0.14);
    box-shadow: 0 16px 40px rgba(120, 146, 184, 0.08);
}

.score-display {
    display: flex;
    align-items: baseline;
    gap: 0.5rem;
}

.score-number {
    font-size: 2rem;
    font-weight: bold;
    color: #6584b1;
}

.score-label {
    color: #64748b;
    font-size: 0.875rem;
}

.rank-info {
    color: #64748b;
    font-size: 0.875rem;
}

.activity-breakdown {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 1rem;
    margin: 1.5rem 0;
}

.activity-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.75rem;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 0.9rem;
    border: 1px solid rgba(129, 158, 196, 0.14);
    box-shadow: 0 14px 36px rgba(120, 146, 184, 0.08);
}

.activity-type {
    font-size: 0.875rem;
    color: #334155;
}

.activity-count {
    font-size: 0.875rem;
    color: #64748b;
}

.activity-item-score {
    font-weight: 600;
    color: #6584b1;
}

/* 活跃度统计数据样式 */
.activity-stats {
    margin-top: 1.5rem;
}

.stat-card {
    transition: all 0.2s ease;
}

.stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 16px 36px rgba(120, 146, 184, 0.14);
}

/* 自定义滚动条样式 */
.custom-scrollbar::-webkit-scrollbar {
    height: 6px;
    /* 滚动条高度 */
}

.custom-scrollbar::-webkit-scrollbar-thumb {
    background-color: #cbd5e1;
    /* 滚动条颜色 */
    border-radius: 4px;
}

.custom-scrollbar::-webkit-scrollbar-track {
    background-color: #f1f5f9;
    /* 轨道颜色 */
}

.activity-summary {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 1.5rem 0;
    padding: 1rem;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 0.9rem;
    border: 1px solid rgba(129, 158, 196, 0.14);
    box-shadow: 0 16px 40px rgba(120, 146, 184, 0.08);
}

.score-display {
    display: flex;
    align-items: baseline;
    gap: 0.5rem;
}

.score-number {
    font-size: 2rem;
    font-weight: bold;
    color: #6584b1;
}

.score-label {
    color: #64748b;
    font-size: 0.875rem;
}

.rank-info {
    color: #64748b;
    font-size: 0.875rem;
}

.activity-breakdown {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 1rem;
    margin: 1.5rem 0;
}

.activity-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.75rem;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 0.9rem;
    border: 1px solid rgba(129, 158, 196, 0.14);
    box-shadow: 0 14px 36px rgba(120, 146, 184, 0.08);
}

.activity-type {
    font-size: 0.875rem;
    color: #334155;
}

.activity-count {
    font-size: 0.875rem;
    color: #64748b;
}

.activity-item-score {
    font-weight: 600;
    color: #6584b1;
}

.activity-score-badge {
    position: absolute;
    top: -28px;
    left: 50%;
    transform: translateX(-50%);
    min-width: 28px;
    border-radius: 999px;
    background: #1f2937;
    padding: 3px 9px;
    font-size: 11px;
    font-weight: 700;
    line-height: 16px;
    color: #ffffff;
    box-shadow: 0 10px 18px rgba(31, 41, 55, 0.16);
    pointer-events: none;
}

.activity-bar {
    border-radius: 12px 12px 0 0;
}

.activity-bar-positive {
    background: linear-gradient(180deg, #7da6ff 0%, #4f7cff 100%);
    box-shadow: 0 10px 18px rgba(79, 124, 255, 0.18);
}

.activity-bar-negative {
    background: #e5e7eb;
}

@media (max-width: 640px) {
    .profile-quick-links {
        grid-template-columns: 1fr;
    }

    .profile-quick-panel {
        padding: 12px;
    }

    .profile-quick-link {
        min-height: 64px;
    }
}

/* 活跃度统计数据样式 */
.activity-stats {
    margin-top: 1.5rem;
}

.stat-card {
    transition: all 0.2s ease;
}

.stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 16px 36px rgba(120, 146, 184, 0.14);
}
</style>
