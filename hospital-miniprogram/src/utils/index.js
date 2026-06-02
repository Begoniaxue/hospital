const maskIdCard = (idCard) => {
    if (!idCard) return ''
    if (idCard.length === 18) {
        return idCard.substring(0, 6) + '********' + idCard.substring(14)
    } else if (idCard.length === 15) {
        return idCard.substring(0, 6) + '*****' + idCard.substring(11)
    }
    return idCard
}

const maskPhone = (phone) => {
    if (!phone) return ''
    if (phone.length === 11) {
        return phone.substring(0, 3) + '****' + phone.substring(7)
    }
    return phone
}

const formatDate = (date, format = 'YYYY-MM-DD') => {
    if (!date) return ''
    const d = new Date(date)
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hour = String(d.getHours()).padStart(2, '0')
    const minute = String(d.getMinutes()).padStart(2, '0')
    const second = String(d.getSeconds()).padStart(2, '0')

    return format
        .replace('YYYY', year)
        .replace('MM', month)
        .replace('DD', day)
        .replace('HH', hour)
        .replace('mm', minute)
        .replace('ss', second)
}

const getGenderText = (gender) => {
    if (gender === 1) return '男'
    if (gender === 0) return '女'
    return '未知'
}

const getRelationText = (relation) => {
    const map = {
        '本人': '本人',
        '父亲': '父亲',
        '母亲': '母亲',
        '配偶': '配偶',
        '子女': '子女',
        '儿子': '儿子',
        '女儿': '女儿',
        '其他': '其他'
    }
    return map[relation] || relation
}

const getCategoryText = (category) => {
    const map = {
        'CT': 'CT检查',
        '验血': '验血报告',
        '体检': '体检报告',
        '病历': '病历资料',
        '其他': '其他资料'
    }
    return map[category] || category
}

const validateIdCard = (idCard) => {
    const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
    return reg.test(idCard)
}

const validatePhone = (phone) => {
    const reg = /^1[3-9]\d{9}$/
    return reg.test(phone)
}

export {
    maskIdCard,
    maskPhone,
    formatDate,
    getGenderText,
    getRelationText,
    getCategoryText,
    validateIdCard,
    validatePhone
}
