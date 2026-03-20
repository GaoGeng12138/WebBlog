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
        linear-gradient(135deg, rgba(249, 252, 255, 0.94), rgba(240, 246, 253, 0.88)),
        radial-gradient(circle at top left, rgba(148, 176, 231, 0.18), transparent 35%);
    color: var(--cosmic-muted);
    border-top: 1px solid rgba(149, 171, 210, 0.16);
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
    background: linear-gradient(135deg, var(--cosmic-gold), var(--cosmic-blue));
    box-shadow: 0 0 0 6px rgba(148, 176, 231, 0.14);
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
    color: var(--cosmic-text-strong);
}

.brand-meta {
    font-size: 12px;
    color: var(--cosmic-muted);
}

.footer-center {
    gap: 8px;
    font-size: 12px;
    color: var(--cosmic-muted);
    padding: 8px 14px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.7);
    border: 1px solid rgba(149, 171, 210, 0.16);
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
    color: var(--cosmic-text-light);
    background: rgba(255, 255, 255, 0.78);
    border: 1px solid rgba(149, 171, 210, 0.18);
    box-shadow: 0 8px 20px rgba(120, 146, 186, 0.08);
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
