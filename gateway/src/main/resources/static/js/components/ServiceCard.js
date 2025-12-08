/**
 * 微服务卡片组件
 */
export default {
    name: 'ServiceCard',
    props: {
        service: {
            type: Object,
            required: true
        },
        active: {
            type: Boolean,
            default: false
        }
    },
    template: `
        <div class="service-card" :class="{ active: active }" @click="$emit('select', service.id)">
            <div class="card-header">
                <div class="card-icon">
                    <img :src="service.logo" :alt="service.title">
                </div>
                <div>
                    <div class="card-title">{{ service.title }}</div>
                    <div class="card-subtitle">{{ service.subtitle }}</div>
                </div>
            </div>
            <div class="card-desc">{{ service.description }}</div>
        </div>
    `
};
