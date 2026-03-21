import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMenuStore } from '@/stores/menu'
import { setTabList, getTabList } from '@/composables/cookie'


const tabList = ref([
    {
        title: '仪表盘',
        path: "/admin/index"
    },
])

export function useTagList() {
    // 当前被选中的 tab
    const route = useRoute()
    const router = useRouter()
    const activeTab = ref(route.path)
    const menuStore = useMenuStore()

    function normalizeTabs(tabs) {
        const safeTabs = Array.isArray(tabs) ? tabs : []
        const lastTab = safeTabs[safeTabs.length - 1]

        if (lastTab?.path) {
            return [{
                title: lastTab.title || '未命名页面',
                path: lastTab.path
            }]
        }

        return [{
            title: '仪表盘',
            path: '/admin/index'
        }]
    }

    //添加标签页
    function addTab(tab) {
        if (!tab?.path) {
            return
        }

        tabList.value = normalizeTabs([{
            title: tab.title || '未命名页面',
            path: tab.path
        }])
        setTabList(tabList.value)
    }



    function initTabList() {
        // 从 cookie 中获取缓存起来的标签导航栏数据
        let tabs = getTabList()
        // 若不为空，则赋值
        if (tabs) {
            tabList.value = normalizeTabs(tabs)
        } else {
            tabList.value = normalizeTabs(tabList.value)
        }

        setTabList(tabList.value)
    }

    // // 初始化标签导航栏
    initTabList()

    // 标签页切换事件
    const tabChange = (pathOrTab) => {
        const path = typeof pathOrTab === 'string' ? pathOrTab : pathOrTab?.path
        if (!path) {
            return
        }
        // 设置被激活的 Tab 标签
        activeTab.value = path
        if (route.path !== path) {
            router.push(path)
        }
    }

    watch(() => route.path, (newPath) => {
        activeTab.value = newPath
        addTab({
            title: route.meta.title,
            path: newPath
        })
    }, {
        immediate: true
    })


    //移除标签
    const removeTab = (path,isUpdate) => {
        return
    }


    // 处理关闭标签菜单事件
    const handleCloseTab = (command) => {
        // 首页路由
        let indexPath = '/admin/index'
        // 处理关闭其他
        if (command == 'closeOthers') {
            tabList.value = normalizeTabs(tabList.value.filter((tab) => tab.path == activeTab.value))
        } else if (command == 'closeAll') { // 处理关闭全部
            // 切换回首页
            activeTab.value = indexPath
            tabList.value = normalizeTabs([{
                title: '仪表盘',
                path: indexPath
            }])
            // 切换标签页
            tabChange(activeTab.value)
        }

        // 设置到 cookie 中
        setTabList(tabList.value)
    }
    return {
        menuStore,
        activeTab,
        tabList,
        tabChange,
        removeTab,
        handleCloseTab
    }
}

