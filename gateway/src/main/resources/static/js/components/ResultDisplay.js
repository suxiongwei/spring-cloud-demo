export default {
    props: {
        result: {
            type: Object,
            default: null
        },
        title: {
            type: String,
            default: '测试结果'
        },
        testId: {
            type: String,
            default: ''
        }
    },
    computed: {
        statusText() {
            if (!this.result) return ''
            return this.result.status === 'success' ? '✔ 成功' :
                   this.result.status === 'error' ? '✘ 失败' : '⏳ 执行中'
        }
    },
    methods: {
        showDetail() {
            this.$emit('show-detail', this.testId)
        }
    },
    template: `
        <div v-if="result" 
             class="result-display" 
             :class="result.status" 
             @click="showDetail"
             style="cursor: pointer;">
            <div class="result-header">
                <span class="result-title">{{ title }}</span>
                <span class="result-status" :class="result.status">
                    {{ statusText }}
                </span>
            </div>
            <div class="result-content">{{ result.message || '无返回信息' }}</div>
            <div v-if="result.endpoint" class="result-endpoint">
                <span class="result-endpoint-label">API</span>
                <code class="result-endpoint-value">{{ result.endpoint }}</code>
            </div>
            <div class="result-meta">
                <span class="result-timestamp">{{ result.timestamp }}</span>
                <span class="result-response-time">{{ result.responseTime }}ms</span>
                <span v-if="result.code">HTTP {{ result.code }}</span>
            </div>
            <div class="result-link-hint">
                点击查看详情
            </div>
        </div>
    `
}
