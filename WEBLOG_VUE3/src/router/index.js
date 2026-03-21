import Admin from "@/layouts/admin/admin.vue";
import { createRouter, createWebHistory } from "vue-router";
import { useSiteConfigStore } from '@/stores/siteConfig'
import { useUserStore } from '@/stores/user'
import { getToken } from '@/composables/cookie'
import { hasAccess } from '@/composables/permission'
import { ElMessage } from 'element-plus'

const Index = () => import("@/pages/frontend/index.vue")
const CategoryList = () => import("@/pages/frontend/category-list.vue")
const TagList = () => import("@/pages/frontend/tag-list.vue")
const ArchiveList = () => import("@/pages/frontend/archive-list.vue")
const ArticleDetail = () => import("@/pages/frontend/article-detail.vue")
const CategoryArticles = () => import("@/pages/frontend/category-articles.vue")
const TagArticles = () => import("@/pages/frontend/tag-articles.vue")
const UserCenter = () => import('@/pages/frontend/user-center.vue')
const ArticlePublish = () => import('@/pages/frontend/article-publish.vue')
const Login = () => import("@/pages/admin/login.vue")
const Register = () => import("@/pages/frontend/register.vue")
const AdminIndex = () => import('@/pages/admin/index.vue')
const AdminArticleList = () => import('@/pages/admin/article-list.vue')
const AdminArticleDetail = () => import('@/pages/admin/article-detail.vue')
const AdminCategoryList = () => import('@/pages/admin/category-list.vue')
const AdminTagList = () => import('@/pages/admin/tag-list.vue')
const AdminBlogSetting = () => import('@/pages/admin/blog-setting.vue')

const frontendFavicon = `${import.meta.env.BASE_URL}thoughtflow-frontend.svg`
const adminFavicon = `${import.meta.env.BASE_URL}thoughtflow-admin-tab.svg`

function setFavicon(href) {
    let favicon = document.querySelector('link[data-app-favicon="true"]')
    if (!favicon) {
        favicon = document.createElement('link')
        favicon.setAttribute('rel', 'icon')
        favicon.setAttribute('data-app-favicon', 'true')
        document.head.appendChild(favicon)
    }
    favicon.setAttribute('type', href.endsWith('.svg') ? 'image/svg+xml' : 'image/png')
    favicon.setAttribute('href', href)
}

// 统一在这里声明所有路由
const routes = [
    {
        path: "/",// 路由地址
        component: Index,//对应组件
        meta: { //meta 信息
            title: "首页" //标题
        }
    }, {
        path: "/category",//分类页
        component: CategoryList,
        meta: {
            title: "分类"
        }
    }, {
        path: "/category/:id",//分类文章页
        component: CategoryArticles,
        meta: {
            title: "分类文章"
        }
    }, {
        path: "/tag",//标签页
        component: TagList,
        meta: {
            title: "标签"
        }
    }, {
        path: "/tag/:id",//标签文章页
        component: TagArticles,
        meta: {
            title: "标签文章"
        }
    }, {
        path: "/user",
        component: UserCenter,
        meta: {
            title: "用户中心"
        }
    }, {
        path: "/article/publish",
        component: ArticlePublish,
        meta: {
            title: "发布文章"
        }
    }, {
        path: "/article/edit/:id",
        component: ArticlePublish,
        meta: {
            title: "编辑文章"
        }
    }, {
        path: "/archive",//归档页
        component: ArchiveList,
        meta: {
            title: "归档"
        }
    }, {
        path: "/article/:id",//文章详情页
        component: ArticleDetail,
        meta: {
            title: "文章详情"
        }
    }, {
        path: "/login",//登录页
        component: Login,
        meta: {
            title: "登录"
        }
    }, {
        path: "/register",//注册页
        component: Register,
        meta: {
            title: "WebLog 注册页"
        }
    },
    {
        path: "/admin", // 后台首页
        component: Admin,
        redirect: '/admin/index', // 默认重定向到仪表盘
        //使用到 admin.vue 布局组件 都需要放置到其子路由下面
        children: [
            {
                path: "/admin/index",
                component: AdminIndex,
                meta: {
                    title: '仪表盘',
                    permission: 'admin:dashboard:view'
                }
            },
            {
                path: "/admin/article/list",
                component: AdminArticleList,
                meta: {
                    title: '文章管理',
                    permission: 'admin:article:list'
                }
            },
            {
                path: "/admin/article/publish",
                component: AdminArticleDetail,
                meta: {
                    title: '发布文章',
                    permission: 'admin:article:publish'
                }
            },
            {
                path: "/admin/article/edit/:id",
                component: AdminArticleDetail,
                meta: {
                    title: '编辑文章',
                    permission: 'admin:article:update'
                }
            },
            {
                path: "/admin/category/list",
                component: AdminCategoryList,
                meta: {
                    title: '分类管理',
                    permission: 'admin:category:list'
                }
            },
            {
                path: "/admin/tag/list",
                component: AdminTagList,
                meta: {
                    title: '标签管理',
                    permission: 'admin:tag:list'
                }
            },
            {
                path: "/admin/user/list",
                component: () => import('@/pages/admin/user-list.vue'),
                meta: {
                    title: '用户管理',
                    permission: 'admin:user:list'
                }
            },
            {
                path: "/admin/role/list",
                component: () => import('@/pages/admin/role-list.vue'),
                meta: {
                    title: '角色管理',
                    permission: 'admin:role:list'
                }
            },
            {
                path: "/admin/blog/setting",
                component: AdminBlogSetting,
                meta: {
                    title: '博客设置',
                    permission: 'admin:setting:view'
                }
            },
            {
                path: "/admin/visitor/list",
                component: () => import('@/pages/admin/visitor-list.vue'),
                meta: {
                    title: '访客记录',
                    permission: 'admin:visitor:list'
                }
            },
        ]

    }
];

// 创建路由实例
const router = createRouter({
    // 指定路由的历史管理方式，hash 模式指的是 URL 的路径是通过 hash 符号（#）进行标识
    history: createWebHistory(import.meta.env.BASE_URL),
    // routes: routes 的缩写
    routes
});

// 添加全局路由守卫
router.beforeEach(async (to, from, next) => {
    const siteConfig = useSiteConfigStore()
    const userStore = useUserStore()
    
    // 检查是否允许用户注册
    if (to.path === '/register') {
        await siteConfig.ensureConfigReady()
        if (!siteConfig.isFeatureEnabled('userRegisterEnabled')) {
            ElMessage.warning('系统暂时关闭了用户注册功能')
            next('/')
            return
        }
    }
    
    // 检查是否允许用户发布文章
    if (to.path === '/article/publish' || to.path.startsWith('/article/edit/')) {
        await siteConfig.ensureConfigReady()
        if (!siteConfig.isFeatureEnabled('userPublishEnabled')) {
            ElMessage.warning('系统暂时关闭了用户发布文章功能')
            next('/')
            return
        }
    }

    if (to.path.startsWith('/admin')) {
        const token = getToken()
        if (!token) {
            next('/login')
            return
        }

        try {
            await userStore.ensureUserInfoReady()
        } catch (error) {
            next('/login')
            return
        }

        if (to.meta.permission && !hasAccess(userStore.userInfo, to.meta.permission)) {
            ElMessage.warning('当前账号没有访问该页面的权限')
            next('/admin/index')
            return
        }
    }
    
    next()
})

// 动态设置页面标题
router.afterEach((to) => {
    const pageTitle = to.meta.title || 'ThoughtFlow'
    const isAdminRoute = to.path.startsWith('/admin')
    document.title = isAdminRoute
        ? `${pageTitle} - ThoughtFlow 管理台`
        : `${pageTitle} - ThoughtFlow`
    setFavicon(isAdminRoute ? adminFavicon : frontendFavicon)
})

// ES6 模块导出语句，它用于将 router 对象导出，以便其他文件可以导入和使用这个对象
export default router;

