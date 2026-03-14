<template>
    <div class="border-b border-gray-200 bg-white">
        <div class="flex items-center justify-between px-4 py-2">
            <div class="flex items-center gap-2 overflow-x-auto">
                <div 
                    v-for="tab in tabList" 
                    :key="tab.path"
                    class="flex items-center gap-2 px-3 py-2 rounded-lg cursor-pointer transition-all whitespace-nowrap"
                    :class="activeTab === tab.path ? 'bg-blue-100 text-blue-600' : 'hover:bg-gray-100'"
                    @click="tabChange(tab)"
                >
                    <span>{{ tab.title }}</span>
                    <el-icon 
                        v-if="tabList.length > 1" 
                        class="text-gray-500 hover:text-red-500"
                        @click.stop="removeTab(tab.path)"
                    >
                        <Close />
                    </el-icon>
                </div>
            </div>
            
            <div class="flex items-center">
                <el-dropdown @command="handleDropdownCommand">
                    <el-icon class="cursor-pointer text-gray-500 hover:text-gray-700 p-1 rounded hover:bg-gray-100">
                        <ArrowDown />
                    </el-icon>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="closeAll">关闭所有</el-dropdown-item>
                            <el-dropdown-item command="closeOthers">关闭其他</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </div>
    </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router';
import { Close, ArrowDown } from '@element-plus/icons-vue'
import { useTagList } from '@/composables/useTagList';

const route = useRoute()
const router = useRouter()

const { menuStore, activeTab, tabList, tabChange, removeTab, handleCloseTab } = useTagList()

// 下拉菜单命令处理
const handleDropdownCommand = (command) => {
    if (command === 'closeAll') {
        // 关闭所有标签页，但保留当前激活的标签页
        const activeTabItem = tabList.value.find(tab => tab.path === activeTab.value)
        if (activeTabItem) {
            tabList.value = [activeTabItem]
        }
    } else if (command === 'closeOthers') {
        // 关闭其他标签页，只保留当前激活的标签页
        tabList.value = tabList.value.filter(tab => tab.path === activeTab.value)
    }
}
</script>

<style scoped>
/* 滚动条样式 */
::-webkit-scrollbar {
    height: 6px;
}

::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
}

::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
}
</style>