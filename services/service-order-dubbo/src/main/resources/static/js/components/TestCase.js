/**
 * 测试用例组件
 */
export default {
    name: 'TestCase',
    props: {
        testCase: {
            type: Object,
            required: true
        },
        modelValue: {
            type: Object,
            default: () => ({})
        }
    },
    emits: ['update:modelValue', 'action'],
    computed: {
        resultDisplay() {
            return this.$parent.getResultDisplay?.(this.testCase.id);
        }
    },
    methods: {
        updateField(key, value) {
            this.$emit('update:modelValue', { ...this.modelValue, [key]: value });
        },
        executeAction(button) {
            this.$emit('action', {
                action: button.action,
                params: button.params || []
            });
        }
    },
    template: `
        <div class="control-group">
            <div class="control-label">{{ testCase.label }}</div>
            <div class="test-description" v-if="testCase.description">
                🔹 {{ testCase.description }}
            </div>
            
            <!-- API 地址 -->
            <div class="config-panel api-address-panel" v-if="testCase.endpoint">
                <div class="config-item api-address-item">
                    <span class="config-label">📍 API 地址:</span>
                    <span class="config-value api-address-value">GET {{ testCase.endpoint }}</span>
                </div>
            </div>
            
            <!-- 输入字段 -->
            <div class="input-row" v-if="testCase.inputFields && testCase.inputFields.length">
                <input 
                    v-for="field in testCase.inputFields" 
                    :key="field.key"
                    class="input" 
                    :type="field.type"
                    :placeholder="field.placeholder"
                    :value="modelValue[field.key] || field.default"
                    @input="updateField(field.key, $event.target.value)"
                >
            </div>
            
            <!-- 操作按钮 -->
            <div class="input-row" v-if="testCase.buttons">
                <button 
                    v-for="(button, index) in testCase.buttons" 
                    :key="index"
                    class="btn"
                    :class="'btn-' + (button.variant || 'primary')"
                    @click="executeAction(button)"
                >
                    <span v-if="!button.variant || button.variant === 'primary'">▶</span>
                    {{ button.label }}
                </button>
            </div>
            
            <!-- 结果显示 -->
            <div 
                v-if="resultDisplay"
                class="result-display" 
                :class="resultDisplay.status"
                @click="$emit('show-detail', testCase.id)"
                style="cursor: pointer;"
            >
                <div class="result-header">
                    <span class="result-title">测试结果</span>
                    <span class="result-status" :class="resultDisplay.status">
                        {{ resultDisplay.status === 'success' ? '✔ 成功' : 
                           resultDisplay.status === 'error' ? '✘ 失败' : '⏳ 执行中' }}
                    </span>
                </div>
                <div class="result-content">{{ resultDisplay.message || '无返回信息' }}</div>
                <div class="result-meta">
                    <span class="result-timestamp">{{ resultDisplay.timestamp }}</span>
                    <span class="result-response-time">{{ resultDisplay.responseTime }}ms</span>
                    <span v-if="resultDisplay.code">HTTP {{ resultDisplay.code }}</span>
                </div>
                <div style="text-align: center; margin-top: 8px; font-size: 10px; color: var(--text-secondary); opacity: 0.7;">
                    点击查看详情
                </div>
            </div>
        </div>
    `
};
