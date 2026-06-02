<template>
    <view class="edit-container" v-if="family">
        <view class="form">
            <view class="form-item">
                <view class="label">姓名</view>
                <input
                    class="input"
                    placeholder="请输入就诊人真实姓名"
                    v-model="form.name"
                />
            </view>

            <view class="form-item">
                <view class="label">身份证号</view>
                <input
                    class="input"
                    placeholder="请输入18位身份证号"
                    maxlength="18"
                    v-model="form.idCard"
                    :disabled="family.hasVisitRecord === 1"
                />
                <view class="tip" v-if="family.hasVisitRecord === 1">已有就诊记录，不可修改身份证号</view>
            </view>

            <view class="form-item">
                <view class="label">性别</view>
                <view class="gender-group">
                    <view
                        class="gender-item"
                        :class="{ active: form.gender === 1 }"
                        @click="form.gender = 1"
                    >
                        男
                    </view>
                    <view
                        class="gender-item"
                        :class="{ active: form.gender === 0 }"
                        @click="form.gender = 0"
                    >
                        女
                    </view>
                </view>
            </view>

            <view class="form-item">
                <view class="label">出生日期</view>
                <picker
                    mode="date"
                    :value="form.birthday"
                    @change="onDateChange"
                >
                    <view class="picker-text">
                        {{ form.birthday || '请选择出生日期' }}
                    </view>
                </picker>
            </view>

            <view class="form-item">
                <view class="label">手机号</view>
                <input
                    class="input"
                    type="number"
                    placeholder="请输入联系电话"
                    maxlength="11"
                    v-model="form.phone"
                />
            </view>

            <view class="form-item">
                <view class="label">与本人关系</view>
                <picker
                    :range="relationList"
                    range-key="label"
                    @change="onRelationChange"
                >
                    <view class="picker-text">
                        {{ currentRelationLabel || '请选择关系' }}
                    </view>
                </picker>
            </view>

            <view class="form-item" v-if="family.status === 0">
                <view class="label">状态</view>
                <view class="status-text text-danger">已禁用（可启用）</view>
            </view>
        </view>

        <button
            class="btn-submit"
            :disabled="loading"
            @click="handleSubmit"
        >
            {{ loading ? '保存中...' : '保存修改' }}
        </button>

        <button
            class="btn-enable"
            v-if="family.status === 0"
            @click="handleEnable"
        >
            启用来宾
        </button>
    </view>
</template>

<script>
import { updateFamilyMember } from '../../api/family.js'
import { getFamilyList } from '../../api/family.js'
import { validatePhone } from '../../utils/index.js'

export default {
    data() {
        return {
            family: null,
            form: {
                id: null,
                name: '',
                idCard: '',
                gender: 1,
                birthday: '',
                phone: '',
                relation: '',
                status: 1
            },
            relationList: [
                { value: '本人', label: '本人' },
                { value: '父亲', label: '父亲' },
                { value: '母亲', label: '母亲' },
                { value: '配偶', label: '配偶' },
                { value: '儿子', label: '儿子' },
                { value: '女儿', label: '女儿' },
                { value: '子女', label: '子女' },
                { value: '其他', label: '其他' }
            ],
            currentRelationIndex: -1,
            loading: false
        }
    },
    computed: {
        currentRelationLabel() {
            const item = this.relationList.find(r => r.value === this.form.relation)
            return item ? item.label : ''
        }
    },
    onLoad(options) {
        this.loadFamilyDetail(options.id)
    },
    methods: {
        async loadFamilyDetail(id) {
            const wechatUserId = this.$store.state.wechatUser?.id || uni.getStorageSync('wechatUser')?.id
            try {
                const res = await getFamilyList(wechatUserId)
                if (res.code === 200) {
                    const family = res.data.find(f => f.id === parseInt(id))
                    if (family) {
                        this.family = family
                        this.form = {
                            id: family.id,
                            name: family.name,
                            idCard: family.idCard,
                            gender: family.gender,
                            birthday: family.birthday,
                            phone: family.phone,
                            relation: family.relation,
                            status: family.status
                        }
                        const idx = this.relationList.findIndex(r => r.value === family.relation)
                        if (idx >= 0) {
                            this.currentRelationIndex = idx
                        }
                    }
                }
            } catch (e) {
                console.error('加载就诊人详情失败', e)
            }
        },

        onDateChange(e) {
            this.form.birthday = e.detail.value
        },

        onRelationChange(e) {
            this.currentRelationIndex = e.detail.value
            this.form.relation = this.relationList[e.detail.value].value
        },

        async handleSubmit() {
            if (!this.form.name.trim()) {
                uni.showToast({
                    title: '请输入姓名',
                    icon: 'none'
                })
                return
            }
            if (!validatePhone(this.form.phone)) {
                uni.showToast({
                    title: '请输入正确的手机号',
                    icon: 'none'
                })
                return
            }

            this.loading = true
            uni.showLoading({ title: '保存中...' })

            try {
                const res = await updateFamilyMember(this.form)

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
        },

        async handleEnable() {
            this.form.status = 1
            try {
                const res = await updateFamilyMember(this.form)
                if (res.code === 200) {
                    this.family.status = 1
                    uni.showToast({
                        title: '已启用',
                        icon: 'success'
                    })
                }
            } catch (e) {
                console.error('启用失败', e)
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
    padding-bottom: 280rpx;
    box-sizing: border-box;
}

.form {
    background: #fff;
    border-radius: 16rpx;
    padding: 0 32rpx;
    margin-bottom: 24rpx;
}

.form-item {
    padding: 32rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
}

.form-item:last-child {
    border-bottom: none;
}

.label {
    width: 160rpx;
    font-size: 28rpx;
    color: #333;
    flex-shrink: 0;
}

.input {
    flex: 1;
    font-size: 30rpx;
    color: #333;
    height: 56rpx;
    line-height: 56rpx;
}

.input[disabled] {
    color: #999;
    background: transparent;
}

.tip {
    width: 100%;
    font-size: 22rpx;
    color: #ff976a;
    margin-top: 12rpx;
}

.gender-group {
    display: flex;
    gap: 24rpx;
    flex: 1;
}

.gender-item {
    flex: 1;
    height: 72rpx;
    line-height: 72rpx;
    text-align: center;
    border: 2rpx solid #e0e0e0;
    border-radius: 12rpx;
    font-size: 28rpx;
    color: #666;
}

.gender-item.active {
    border-color: #1989fa;
    color: #1989fa;
    background: rgba(25, 137, 250, 0.05);
}

.picker-text {
    flex: 1;
    font-size: 30rpx;
    color: #333;
    height: 56rpx;
    line-height: 56rpx;
}

.status-text {
    flex: 1;
    font-size: 28rpx;
}

.text-danger {
    color: #ee0a24;
}

.btn-submit {
    position: fixed;
    left: 32rpx;
    right: 32rpx;
    bottom: 180rpx;
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

.btn-enable {
    position: fixed;
    left: 32rpx;
    right: 32rpx;
    bottom: 60rpx;
    height: 96rpx;
    background: #fff;
    color: #07c160;
    border: 2rpx solid #07c160;
    border-radius: 48rpx;
    font-size: 32rpx;
}

.btn-enable:active {
    background: rgba(7, 193, 96, 0.05);
}
</style>
