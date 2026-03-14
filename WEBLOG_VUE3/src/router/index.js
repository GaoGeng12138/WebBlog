import Index from "@/pages/frontend/index.vue";
import CategoryList from "@/pages/frontend/category-list.vue";
import TagList from "@/pages/frontend/tag-list.vue";
import ArchiveList from "@/pages/frontend/archive-list.vue";
import ArticleDetail from "@/pages/frontend/article-detail.vue";
import CategoryArticles from "@/pages/frontend/category-articles.vue";  // Added
import TagArticles from "@/pages/frontend/tag-articles.vue";  // Added
import UserCenter from '@/pages/frontend/user-center.vue'
import ArticlePublish from '@/pages/frontend/article-publish.vue'
import login from "@/pages/admin/login.vue";
import register from "@/pages/frontend/register.vue"; // Added registration page
import AdminIndex from '@/pages/admin/index.vue'
import Admin from "@/layouts/admin/admin.vue";
import AdminArticleList from '@/pages/admin/article-list.vue'
import AdminArticleDetail from '@/pages/admin/article-detail.vue'
import AdminCategoryList from '@/pages/admin/category-list.vue'
import AdminTagList from '@/pages/admin/tag-list.vue'
import AdminBlogSetting from '@/pages/admin/blog-setting.vue'
import { createRouter, createWebHistory } from "vue-router";
import { useSiteConfigStore } from '@/stores/siteConfig'
import { ElMessage } from 'element-plus'


// 统一在这里声明所有路由
const routes = [
    {
        path: "/",// 路由地址
        component: Index,//对应组件
        meta: { //meta 信息
            title: "WebLog 首页" //标题
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
        component: login,
        meta: {
            title: "WebLog 登录页"
        }
    }, {
        path: "/register",//注册页
        component: register,
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
                    title: '仪表盘'
                }
            },
            {
                path: "/admin/article/list",
                component: AdminArticleList,
                meta: {
                    title: '文章管理'
                }
            },
            {
                path: "/admin/article/publish",
                component: AdminArticleDetail,
                meta: {
                    title: '发布文章'
                }
            },
            {
                path: "/admin/article/edit/:id",
                component: AdminArticleDetail,
                meta: {
                    title: '编辑文章'
                }
            },
            {
                path: "/admin/category/list",
                component: AdminCategoryList,
                meta: {
                    title: '分类管理'
                }
            },
            {
                path: "/admin/tag/list",
                component: AdminTagList,
                meta: {
                    title: '标签管理'
                }
            },
            {
                path: "/admin/user/list",
                component: () => import('@/pages/admin/user-list.vue'),
                meta: {
                    title: '用户管理'
                }
            },
            {
                path: "/admin/role/list",
                component: () => import('@/pages/admin/role-list.vue'),
                meta: {
                    title: '角色管理'
                }
            },
            {
                path: "/admin/blog/setting",
                component: AdminBlogSetting,
                meta: {
                    title: '博客设置'
                }
            },
        ]

    }
];

// 创建路由实例
const router = createRouter({
    // 指定路由的历史管理方式，hash 模式指的是 URL 的路径是通过 hash 符号（#）进行标识
    history: createWebHistory(),
    // routes: routes 的缩写
    routes
});

// 添加全局路由守卫
router.beforeEach((to, from, next) => {
    const siteConfig = useSiteConfigStore()
    
    // 检查是否允许用户注册
    if (to.path === '/register') {
        if (!siteConfig.isFeatureEnabled('userRegisterEnabled')) {
            ElMessage.warning('系统暂时关闭了用户注册功能')
            next('/')
            return
        }
    }
    
    // 检查是否允许用户发布文章
    if (to.path === '/article/publish' || to.path.startsWith('/article/edit/')) {
        if (!siteConfig.isFeatureEnabled('userPublishEnabled')) {
            ElMessage.warning('系统暂时关闭了用户发布文章功能')
            next('/')
            return
        }
    }
    
    next()
})

// ES6 模块导出语句，它用于将 router 对象导出，以便其他文件可以导入和使用这个对象
export default router;