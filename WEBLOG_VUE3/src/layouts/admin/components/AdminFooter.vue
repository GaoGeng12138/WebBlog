<template>
    <footer class="admin-footer">
        <div class="footer-left">
            <span class="brand-mark"></span>
            <div class="brand-copy">
                <span class="brand-title">ThoughtFlow Admin</span>
                <span class="brand-meta">© {{ currentYear }} WebLog 内容后台</span>
            </div>
        </div>

        <div class="footer-center">
            <span class="status-dot"></span>
            <span>内容管理已连接，注意保护敏感数据传输</span>
        </div>

        <div class="footer-right">
            <span class="version-badge">v1.0.0</span>
        </div>
    </footer>
</template>

<script setup>
const currentYear = new Date().getFullYear()
</script>

<style scoped>
.admin-footer {
    min-height: 60px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    padding: 14px 24px;
    background:
        linear-gradient(135deg, rgba(248, 250, 252, 0.98), rgba(255, 251, 235, 0.95)),
        radial-gradient(circle at top left, rgba(251, 191, 36, 0.18), transparent 35%);
    color: #475569;
    border-top: 1px solid rgba(226, 232, 240, 0.95);
}

.footer-left,
.footer-center,
.footer-right {
    display: flex;
    align-items: center;
}

.footer-left {
    gap: 12px;
    min-width: 0;
}

.brand-mark {
    width: 10px;
    height: 10px;
    border-radius: 999px;
    flex-shrink: 0;
    background: linear-gradient(135deg, #f59e0b, #0ea5e9);
    box-shadow: 0 0 0 6px rgba(245, 158, 11, 0.12);
}

.brand-copy {
    display: flex;
    flex-direction: column;
    gap: 2px;
    min-width: 0;
}

.brand-title {
    font-size: 13px;
    font-weight: 600;
    letter-spacing: 0.02em;
    color: #0f172a;
}

.brand-meta {
    font-size: 12px;
    color: #64748b;
}

.footer-center {
    gap: 8px;
    font-size: 12px;
    color: #64748b;
    padding: 8px 14px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.78);
    border: 1px solid rgba(226, 232, 240, 0.9);
}

.status-dot {
    width: 8px;
    height: 8px;
    border-radius: 999px;
    background: #22c55e;
    box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.14);
}

.version-badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    height: 32px;
    padding: 0 14px;
    border-radius: 999px;
    font-size: 12px;
    font-weight: 600;
    color: #0f172a;
    background: rgba(255, 255, 255, 0.82);
    border: 1px solid rgba(203, 213, 225, 0.95);
    box-shadow: 0 8px 20px rgba(15, 23, 42, 0.04);
}

@media (max-width: 960px) {
    .admin-footer {
        flex-direction: column;
        align-items: flex-start;
    }

    .footer-center {
        width: 100%;
        border-radius: 16px;
    }

    .footer-right {
        align-self: flex-end;
    }
}
</style>
