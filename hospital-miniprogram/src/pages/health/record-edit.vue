<template>
    <view class="edit-container">
        <view class="form">
            <view class="form-section">
                <view class="section-title">健康信息</view>

                <view class="form-item">
                    <view class="label">过敏史</view>
                    <input
                        class="input"
                        placeholder="如：青霉素、海鲜过敏等"
                        v-model="form.allergyHistory"
                    />
                </view>

                <view class="form-item">
                    <view class="label">既往病史</view>
                    <input
                        class="input"
                        placeholder="如：高血压、糖尿病等"
                        v-model="form.medicalHistory"
                    />
                </view>

                <view class="form-item">
                    <view class="label">慢病史</view>
                    <input
                        class="input"
                        placeholder="如：原发性高血压、2型糖尿病等"
                        v-model="form.chronicDisease"
                    />
                </view>

                <view class="form-item">
                    <view class="label">手术史</view>
                    <input
                        class="input"
                        placeholder="如：阑尾切除术、胆囊切除术等"
                        v-model="form.operationHistory"
                    />
                </view>

                <view class="form-item">
                    <view class="label">药物禁忌</view>
                    <input
                        class="input"
                        placeholder="如：阿司匹林、磺胺类药物等"
                        v-model="form.drugContraindication"
                    />
                </view>
            </view>

            <view class="form-section">
                <view class="section-title">体格信息</view>

                <view class="form-item-row">
                    <view class="form-item-half">
                        <view class="label">血型</view>
                        <picker :range="bloodTypes" @change="onBloodTypeChange">
                            <view class="picker-text">
                                {{ form.bloodType || '请选择' }}
                            </view>
                        </picker>
                    </view>
                    <view class="form-item-half">
                        <view class="label">身高(cm)</view>
                        <input
                            class="input"
                            type="digit"
                            placeholder="如：175"
                            v-model="form.height"
                        />
                    </view>
                </view>

                <view class="form-item-row">
                    <view class="form-item-half">
                        <view class="label">体重(kg)</view>
                        <input
                            class="input"
                            type="digit"
                            placeholder="如：70"
                            v-model="form.weight"
                        />
                    </view>
                    <view class="form-item-half">
                        <view class="label">婚姻状况</view>
                        <picker :range="maritalStatuses" @change="onMaritalChange">
                            <view class="picker-text">
                                {{ form.maritalStatus || '请选择' }}
                            </view>
                        </picker>
                    </view>
                </view>

                <view class="form-item">
                    <view class="label">职业</view>
                    <input
                        class="input"
                        placeholder="请输入职业"
                        v-model="form.occupation"
                    />
                </view>
            </view>

            <view class="form-section">
                <view class="section-title">生活习惯</view>

                <view class="form-item-row">
                    <view class="form-item-half">
                        <view class="label">吸烟情况</view>
                        <picker :range="smokingStatuses" @change="onSmokingChange">
                            <view class="picker-text">
                                {{ form.smokingStatus || '请选择' }}
                            </view>
                        </picker>
                    </view>
                    <view class="form-item-half">
                        <view class="label">饮酒情况</view>
                        <picker :range="drinkingStatuses" @change="onDrinkingChange">
                            <view class="picker-text">
                                {{ form.drinkingStatus || '请选择' }}
                            </view>
                        </picker>
                    </view>
                </view>

                <view class="form-item">
                    <view class="label">运动习惯</view>
                    <input
                        class="input"
                        placeholder="如：每周3次，每次30分钟"
                        v-model="form.exerciseHabit"
                    />
                </view>

                <view class="form-item">
                    <view class="label">饮食习惯</view>
                    <input
                        class="input"
                        placeholder="如：清淡饮食，低盐低脂"
                        v-model="form.dietaryHabit"
                    />
                </view>

                <view class="form-item">
                    <view class="label">睡眠习惯</view>
                    <input
                        class="input"
                        placeholder="如：每晚7-8小时，作息规律"
                        v-model="form.sleepHabit"
                    />
                </view>

                <view class="form-item">
                    <view class="label">排便习惯</view>
                    <input
                        class="input"
                        placeholder="如：每日1次，规律"
                        v-model="form.defecationHabit"
                    />
                </view>
            </view>
        </view>

        <view class="notice">
            <text class="notice-icon">⚠️</text>
            <text>过敏史、药物禁忌等重要信息会在医生开处方时弹窗提醒，请务必如实填写，以保障医疗安全。</text>
        </view>

        <button
            class="btn-submit"
            :disabled="loading"
            @click="handleSubmit"
        >
            {{ loading ? '保存中...' : '保存' }}
        </button>
    </view>
</template>

<script>
import { getHealthRecord, saveHealthRecord } from '../../api/health.js'

export default {
    data() {
        return {
            form: {
                id: null,
                patientId: null,
                allergyHistory: '',
                medicalHistory: '',
                chronicDisease: '',
                operationHistory: '',
                drugContraindication: '',
                bloodType: '',
                height: '',
                weight: '',
                maritalStatus: '',
                occupation: '',
                smokingStatus: '',
                drinkingStatus: '',
                exerciseHabit: '',
                dietaryHabit: '',
                sleepHabit: '',
                defecationHabit: ''
            },
            bloodTypes: ['A型', 'B型', 'AB型', 'O型', 'Rh阳性', 'Rh阴性', '不详'],
            maritalStatuses: ['未婚', '已婚', '离异', '丧偶', '其他'],
            smokingStatuses: ['不吸烟', '已戒烟', '偶尔吸烟', '经常吸烟', '大量吸烟'],
            drinkingStatuses: ['不饮酒', '已戒酒', '偶尔饮酒', '经常饮酒', '大量饮酒'],
            loading: false
        }
    },
    onLoad(options) {
        this.form.patientId = options.patientId
        this.loadRecord()
    },
    methods: {
        async loadRecord() {
            if (!this.form.patientId) return

            try {
                const res = await getHealthRecord(this.form.patientId)
                if (res.code === 200 && res.data) {
                    this.form = { ...this.form, ...res.data }
                }
            } catch (e) {
                console.error('加载健康档案失败', e)
            }
        },

        onBloodTypeChange(e) {
            this.form.bloodType = this.bloodTypes[e.detail.value]
        },

        onMaritalChange(e) {
            this.form.maritalStatus = this.maritalStatuses[e.detail.value]
        },

        onSmokingChange(e) {
            this.form.smokingStatus = this.smokingStatuses[e.detail.value]
        },

        onDrinkingChange(e) {
            this.form.drinkingStatus = this.drinkingStatuses[e.detail.value]
        },

        async handleSubmit() {
            if (!this.form.patientId) {
                uni.showToast({
                    title: '参数错误',
                    icon: 'none'
                })
                return
            }

            this.loading = true
            uni.showLoading({ title: '保存中...' })

            try {
                const res = await saveHealthRecord(this.form)
                if (res.code === 200) {
                    uni.hideLoading()
                    uni.showToast({
                        title: '保存成功',
                        icon: 'success'
                    })

                    setTimeout(() => {
                        uni.navigateBack()
                    }, 1500)
                }
            } catch (e) {
                uni.hideLoading()
                uni.showToast({
                    title: e.message || '保存失败',
                    icon: 'none'
                })
            } finally {
                this.loading = false
            }
        }
    }
}
</script>

<style scoped>
.edit-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding: 24rpx;
    padding-bottom: 160rpx;
    box-sizing: border-box;
}

.form {
    margin-bottom: 24rpx;
}

.form-section {
    background: #fff;
    border-radius: 16rpx;
    padding: 0 32rpx;
    margin-bottom: 24rpx;
}

.section-title {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
    padding: 28rpx 0 16rpx;
    border-bottom: 1rpx solid #f0f0f0;
}

.form-item {
    padding: 28rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
}

.form-item:last-child {
    border-bottom: none;
}

.form-item-row {
    display: flex;
    border-bottom: 1rpx solid #f0f0f0;
}

.form-item-row:last-child {
    border-bottom: none;
}

.form-item-half {
    flex: 1;
    padding: 28rpx 0;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
}

.form-item-half:first-child {
    padding-right: 16rpx;
    border-right: 1rpx solid #f0f0f0;
}

.form-item-half:last-child {
    padding-left: 16rpx;
}

.label {
    width: 160rpx;
    font-size: 28rpx;
    color: #333;
    flex-shrink: 0;
}

.form-item-half .label {
    width: 120rpx;
}

.input {
    flex: 1;
    font-size: 28rpx;
    color: #333;
    height: 56rpx;
    line-height: 56rpx;
}

.picker-text {
    flex: 1;
    font-size: 28rpx;
    color: #333;
    height: 56rpx;
    line-height: 56rpx;
}

.picker-text:empty::before {
    content: '请选择';
    color: #999;
}

.notice {
    display: flex;
    align-items: flex-start;
    background: #fff1f0;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-bottom: 48rpx;
    font-size: 24rpx;
    color: #cf1322;
    line-height: 1.6;
}

.notice-icon {
    margin-right: 12rpx;
    flex-shrink: 0;
}

.btn-submit {
    position: fixed;
    left: 32rpx;
    right: 32rpx;
    bottom: 60rpx;
    height: 96rpx;
    background: #1989fa;
    color: #fff;
    border-radius: 48rpx;
    font-size: 32rpx;
    border: none;
}

.btn-submit[disabled] {
    background: #ccc;
}

.btn-submit:active:not([disabled]) {
    opacity: 0.85;
}
</style>
