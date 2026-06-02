import { createStore } from 'vuex'
import { getPatientInfo } from '../api/health.js'

export default createStore({
    state: {
        token: '',
        wechatUser: null,
        currentPatient: null,
        familyList: []
    },
    mutations: {
        SET_TOKEN(state, token) {
            state.token = token
        },
        SET_WECHAT_USER(state, user) {
            state.wechatUser = user
        },
        SET_CURRENT_PATIENT(state, patient) {
            state.currentPatient = patient
        },
        SET_FAMILY_LIST(state, list) {
            state.familyList = list
        },
        UPDATE_CURRENT_PATIENT_ID(state, patientId) {
            if (state.wechatUser) {
                state.wechatUser.currentPatientId = patientId
                uni.setStorageSync('wechatUser', state.wechatUser)
            }
        },
        LOGOUT(state) {
            state.token = ''
            state.wechatUser = null
            state.currentPatient = null
            state.familyList = []
            uni.removeStorageSync('token')
            uni.removeStorageSync('wechatUser')
            uni.removeStorageSync('currentPatient')
        }
    },
    actions: {
        async getCurrentPatient({ commit }, patientId) {
            try {
                const res = await getPatientInfo(patientId)
                if (res.code === 200) {
                    commit('SET_CURRENT_PATIENT', res.data)
                    uni.setStorageSync('currentPatient', res.data)
                }
            } catch (e) {
                console.error('获取当前患者信息失败', e)
            }
        }
    },
    getters: {
        isLoggedIn: (state) => !!state.token,
        hasRealName: (state) => !!state.wechatUser?.currentPatientId,
        currentPatientId: (state) => state.wechatUser?.currentPatientId
    }
})
