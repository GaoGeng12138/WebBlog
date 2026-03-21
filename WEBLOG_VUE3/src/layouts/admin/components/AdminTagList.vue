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
                </div>
            </div>
            
        </div>
    </div>
</template>

<script setup>
import { useTagList } from '@/composables/useTagList';

const { activeTab, tabList, tabChange } = useTagList()
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
