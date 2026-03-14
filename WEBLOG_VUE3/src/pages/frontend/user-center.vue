<template>
    <div class="min-h-screen bg-[#F5F7FA] pb-20">
        <div class="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-b border-gray-200/50">
            <div class="max-w-7xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
                <div class="flex items-center cursor-pointer group" @click="goHome">
                    <div
                        class="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center group-hover:bg-blue-50 text-gray-500 group-hover:text-blue-600 transition-colors mr-3">
                        <el-icon>
                            <ArrowLeft />
                        </el-icon>
                    </div>
                    <span
                        class="text-sm font-medium text-gray-600 group-hover:text-blue-600 transition-colors">返回首页</span>
                </div>
                <div class="flex items-center gap-2 text-xs text-gray-400">
                    <span>个人中心</span>
                    <span class="text-gray-300">/</span>
                    <span class="text-gray-600 font-medium">{{ activeTabName }}</span>
                </div>
            </div>
        </div>

        <div class="relative h-64 md:h-80 w-full group overflow-hidden">
            <img src="https://picsum.photos/1920/600?random=1"
                class="w-full h-full object-cover transition duration-700 group-hover:scale-105" alt="Cover" />
            <div class="absolute inset-0 bg-gradient-to-t from-black/30 to-transparent"></div>
            <div
                class="absolute top-6 right-6 opacity-0 group-hover:opacity-100 transition-all duration-300 transform translate-y-[-10px] group-hover:translate-y-0">
                <el-button type="info" size="small" round icon="Camera"
                    class="!bg-black/30 !border-white/30 !text-white backdrop-blur-sm hover:!bg-black/50">更换封面</el-button>
            </div>
        </div>

        <div class="max-w-7xl mx-auto px-4 sm:px-6 relative -mt-20 z-10">
            <div class="bg-white rounded-2xl shadow-lg mb-8 p-6 md:p-8 relative overflow-visible">
                <div class="flex flex-col md:flex-row items-start justify-between">
                    <div class="flex flex-col md:flex-row items-center md:items-end w-full">
                        <div class="relative -mt-20 md:-mt-24 mb-4 md:mb-0 md:mr-6 flex-shrink-0">
                            <div
                                class="w-32 h-32 md:w-40 md:h-40 rounded-full border-[6px] border-white bg-white shadow-md overflow-hidden group cursor-pointer relative">
                                <img :src="user.avatar || '/pics/default-avatar.png'"
                                    class="w-full h-full object-cover" />
                                <div
                                    class="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                                    <span
                                        class="text-white text-xs font-medium border border-white/50 px-3 py-1 rounded-full backdrop-blur-sm">修改头像</span>
                                </div>
                            </div>
                        </div>

                        <div class="flex-1 text-center md:text-left">
                            <div class="flex flex-col md:flex-row md:items-center gap-3 mb-2">
                                <h1 class="text-2xl md:text-3xl font-bold text-gray-800 tracking-tight">
                                    {{ user.nickname || user.username || '未登录' }}
                                </h1>
                                <div class="flex items-center justify-center md:justify-start gap-2">
                                    <el-tag v-if="hasRole('ROLE_ADMIN')" effect="dark" type="danger" size="small" round
                                        class="!border-0">管理员</el-tag>
                                    <el-tag v-else effect="light" type="primary" size="small" round
                                        class="!bg-blue-50 !text-blue-600 !border-blue-100">Lv.3 作者</el-tag>
                                    <span class="text-xs text-gray-400 bg-gray-100 px-2 py-0.5 rounded-md">ID:{{
                                        user.userId
                                        }}</span>
                                </div>
                            </div>

                            <div
                                class="text-gray-500 text-sm mb-4 flex items-center justify-center md:justify-start gap-4">
                                <span class="flex items-center gap-1"><el-icon class="text-gray-400">
                                        <Location />
                                    </el-icon> {{ province || '未知' }}·{{ city || '未知' }}</span>
                                <span class="flex items-center gap-1"><el-icon class="text-gray-400">
                                        <Calendar />
                                    </el-icon> 加入于 {{ formatDate(user.registerTime) }}</span>
                            </div>

                            <p
                                class="text-gray-600 text-sm leading-relaxed max-w-2xl mx-auto md:mx-0 bg-gray-50 md:bg-transparent p-3 md:p-0 rounded-lg">
                                {{ user.introduction || '这个人很懒，什么都没有写...' }}
                            </p>
                        </div>

                        <div class="flex flex-col items-center md:items-end gap-6 mt-6 md:mt-0 w-full md:w-auto">
                            <div class="flex items-center gap-8 md:gap-10">
                                <div class="text-center group cursor-pointer">
                                    <div
                                        class="text-2xl font-bold text-gray-800 group-hover:text-blue-600 transition-colors">
                                        {{ articleCount }}</div>
                                    <div class="text-xs text-gray-400 mt-1">文章</div>
                                </div>
                                <div class="text-center group cursor-pointer">
                                    <div
                                        class="text-2xl font-bold text-gray-800 group-hover:text-green-600 transition-colors">
                                        {{ favoriteCount }}</div>
                                    <div class="text-xs text-gray-400 mt-1">收藏</div>
                                </div>
                                <div class="text-center group cursor-pointer">
                                    <div
                                        class="text-2xl font-bold text-gray-800 group-hover:text-purple-600 transition-colors">
                                        {{ commentCount }}</div>
                                    <div class="text-xs text-gray-400 mt-1">评论</div>
                                </div>
                            </div>
                            <div class="flex gap-3">
                                <el-button type="primary" size="large" round
                                    class="!px-8 !font-medium shadow-sm hover:shadow-md transition-shadow"
                                    @click="openEditDialog">
                                    编辑个人资料
                                </el-button>
                                <el-button v-if="hasRole('ROLE_ADMIN')" type="warning" size="large" round
                                    class="!px-8 !font-medium shadow-sm hover:shadow-md transition-shadow"
                                    @click="goToAdminPanel">
                                    <el-icon class="mr-1">
                                        <Setting />
                                    </el-icon>
                                    后台管理系统
                                </el-button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="grid grid-cols-1 lg:grid-cols-12 gap-8">

                <div class="lg:col-span-3">
                    <div class="bg-white rounded-2xl shadow-sm p-4 sticky top-24">
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
                    <div class="bg-white rounded-2xl shadow-sm min-h-[600px] p-6 md:p-8">

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
                                            <span class="activity-type">{{ getActivityTypeName(activity.type) }}</span>
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

                                        <div class="relative w-full h-48 border-b border-gray-100">
                                            <div class="flex items-end h-full overflow-x-auto pb-2 custom-scrollbar"
                                                v-loading="activityLoading">

                                                <div class="activity-chart mt-6 relative">
                                                    <div class="relative w-full h-48 border-b border-gray-200">
                                                        <div class="flex items-end h-full overflow-x-auto pb-3 custom-scrollbar"
                                                            v-loading="activityLoading">
                                                            <div v-for="(item, index) in activityData.trend"
                                                                :key="index"
                                                                class="h-full flex flex-col justify-end items-center flex-shrink-0 px-2 min-w-[90px]">
                                                                <!-- 柱子容器 -->
                                                                <div
                                                                    class="relative w-full h-full flex items-end justify-center">

                                                                    <!-- 实际柱子 -->
                                                                    <div class="w-full max-w-[40px] rounded-t shadow-sm transition-all duration-300"
                                                                        :class="item.score > 0
                                                                            ? 'bg-gradient-to-t from-blue-500 to-blue-400'
                                                                            : 'bg-gray-200'"
                                                                        :style="{ height: calculateBarHeight(item.score) + '%' }">
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
                                                                    {{ formatDateByRange(item.date, selectedTimeRange)
                                                                    }}
                                                                </div>
                                                            </div>

                                                            <div v-if="!activityLoading && activityData.trend.length === 0"
                                                                class="absolute inset-0 flex items-center justify-center text-gray-400 text-sm">
                                                                暂无活跃度数据
                                                            </div>
                                                        </div>
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
                                    </div>

                                    <div class="activity-stats mt-6 pt-6 border-t border-gray-200">
                                        <h4 class="text-sm font-medium text-gray-700 mb-3">活跃度统计</h4>
                                        <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                                            <div
                                                class="stat-card bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                                                <div class="text-xs text-gray-500 mb-1">日均活跃度</div>
                                                <div class="text-xl font-bold text-blue-600">{{
                                                    activityStatistics.dailyAverage }}</div>
                                            </div>
                                            <div
                                                class="stat-card bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                                                <div class="text-xs text-gray-500 mb-1">周均活跃度</div>
                                                <div class="text-xl font-bold text-green-600">{{
                                                    activityStatistics.weeklyAverage }}</div>
                                            </div>
                                            <div
                                                class="stat-card bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                                                <div class="text-xs text-gray-500 mb-1">月均活跃度</div>
                                                <div class="text-xl font-bold text-purple-600">{{
                                                    activityStatistics.monthlyAverage }}</div>
                                            </div>
                                            <div
                                                class="stat-card bg-white p-4 rounded-lg shadow-sm border border-gray-100">
                                                <div class="text-xs text-gray-500 mb-1">最佳表现</div>
                                                <div class="text-xl font-bold text-yellow-600">{{
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
                                        <span class="w-1 h-5 bg-green-500 rounded-full"></span> 最新动态
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
                                                    'bg-blue-50/50 border-blue-100': dynamic.type === 1,
                                                    'bg-purple-50/50 border-purple-100': dynamic.type === 2,
                                                    'bg-yellow-50/50 border-yellow-100': dynamic.type === 3
                                                }" @click="handleDynamicClick(dynamic)">
                                                <h4 class="text-sm text-gray-600 mb-1">
                                                    {{ getDynamicTypeName(dynamic.type) }}
                                                </h4>
                                                <div class="font-medium mt-1 hover:underline" :class="{
                                                    'text-blue-700': dynamic.type === 1,
                                                    'text-purple-700': dynamic.type === 2,
                                                    'text-yellow-700': dynamic.type === 3
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

                        <div v-if="activeTab === 'articles'" class="animate-fade-in">
                            <div class="flex justify-between items-center mb-6">
                                <h3 class="text-lg font-bold text-gray-800">我的文章 <span
                                        class="text-gray-400 font-normal text-sm ml-2">共 {{ articles.length }} 篇</span>
                                </h3>
                                <el-button v-if="siteConfig.isFeatureEnabled('userPublishEnabled')" type="primary"
                                    icon="EditPen" round class="shadow-sm" @click="goToPublish">写文章</el-button>
                            </div>

                            <el-empty v-if="!articles.length" description="暂无文章" />
                            <div v-else class="space-y-4">
                                <div v-for="item in articles" :key="item.id"
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

                            <div v-if="articles.length > 0" class="mt-8 flex justify-center">
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
                                            <div class="text-xs text-gray-500 mt-1">账号注销后无法恢复，请谨慎操作</div>
                                        </div>
                                        <el-popconfirm title="确定要注销吗？此操作无法撤销。" confirm-button-text="确认注销"
                                            cancel-button-text="取消" confirm-button-type="danger" @confirm="logout">
                                            <template #reference>
                                                <el-button type="danger" plain size="small">注销</el-button>
                                            </template>
                                        </el-popconfirm>
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
                            </div>·
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
                        <el-button type="primary" @click="saveProfile" round>保存修改</el-button>
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
import { removeToken } from "@/composables/cookie";
import { useUserStore } from '@/stores/user'
import { useSiteConfigStore } from '@/stores/siteConfig'
import { getArticlePageList } from "@/api/frontend/article";
import { getCollectedArticles, uncollectArticle } from "@/api/frontend/favorite";
import { getUserCenterStatistics, getUserCenterComments, getUserCenterOverview, getActivityScore, getActivityStatistics, getActivityTrend } from "@/api/frontend/user";
import { deleteComment as deleteCommentApi } from "@/api/frontend/comment";
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Pagination from '@/components/frontend/Pagination.vue'
import { getCityLocation } from '@/api/frontend/cityLocation.js'


const city = ref(null)
const province = ref(null)
getCityLocation().then(res => {
    city.value = res.city
    province.value = res.province
})

const userStore = useUserStore()
const siteConfig = useSiteConfigStore()
const user = computed(() => userStore.frontendUserInfo)
const router = useRouter()

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
    const minHeight = 12
    const maxHeight = 90

    if (score <= 0) return minHeight
    return Math.max(minHeight, (score / maxScore) * maxHeight)
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
        collections: '我的收藏',
        comments: '评论历史',
        security: '安全设置'
    }
    return map[activeTab.value]
})

const showEditProfile = ref(false)
const editForm = reactive({
    nickname: '',
    introduction: ''
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

onMounted(() => {
    // Load frontend user info first, then load articles
    userStore.setFrontendUserInfo().then(() => {
        loadStatistics()
        loadArticles()
    }).catch((error) => {
        console.error('Failed to load user info:', error)
        // Even if user info fails to load, we still try to load articles
        loadArticles()
    })
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

const logout = () => {
    // Call the store's logout function to clear user info
    userStore.logout()
    // Redirect to home page
    router.push('/')
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
    }
    showEditProfile.value = true
}

// 修改：保存逻辑 (这里需要你补充实际调用后端接口的代码)
const saveProfile = () => {
    // 1. TODO: 这里应该调用后端 API 更新用户信息
    // await updateUserProfile(editForm) 

    // 2. 模拟更新成功
    user.value.nickname = editForm.nickname
    user.value.introduction = editForm.introduction

    // 3. 关闭弹窗
    showEditProfile.value = false
}

// 处理 tab 选择
const handleTabSelect = (index) => {
    activeTab.value = index
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
    } else if (newTab === 'articles') {
        loadArticles()
    } else if (newTab === 'comments') {
        loadCommentHistory()
    } else if (newTab === 'overview') {
        loadDynamics()
        loadActivityScore()
    }
})

// 初始化加载统计数据
onMounted(() => {
    loadStatistics()
    // 如果默认 tab 是 overview，则加载动态数据和活跃度数据
    if (activeTab.value === 'overview') {
        loadDynamics()
        loadActivityScore()
    }
})

</script>

<style scoped>
.animate-fade-in {
    animation: fadeIn 0.4s ease-out;
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
    transition: all 0.2s;
}

.custom-menu .el-menu-item:hover {
    background-color: #f1f5f9;
    color: #0f172a;
}

.custom-menu .el-menu-item.is-active {
    background-color: #eff6ff;
    color: #2563eb;
    font-weight: 600;
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
    background: #f8fafc;
    border-radius: 0.75rem;
    border: 1px solid #e2e8f0;
}

.activity-summary {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 1.5rem 0;
    padding: 1rem;
    background: white;
    border-radius: 0.5rem;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.score-display {
    display: flex;
    align-items: baseline;
    gap: 0.5rem;
}

.score-number {
    font-size: 2rem;
    font-weight: bold;
    color: #0ea5e9;
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
    background: white;
    border-radius: 0.5rem;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
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
    color: #0ea5e9;
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
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
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

activity-section {
    padding: 1.5rem;
    background: #f8fafc;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
}

.activity-summary {
    display: flex;
    justify-content: space-between;
    background: white;
    padding: 1rem;
    border-radius: 8px;
}

.score-number {
    font-size: 2rem;
    font-weight: bold;
    color: #0ea5e9;
}

.custom-scrollbar::-webkit-scrollbar {
    height: 6px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 4px;
}
</style>