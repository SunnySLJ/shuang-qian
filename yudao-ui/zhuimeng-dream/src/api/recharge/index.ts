/**
 * 充值与支付 API
 * 对接后端: /trade/order, /pay
 */

import request, { get, post } from '@/utils/request'

// ========== 请求 VO ==========

/** 创建充值订单请求 */
export interface CreateRechargeOrderReqVO {
  packageId: number    // 套餐 ID
}

/** 自定义充值请求 */
export interface CustomRechargeReqVO {
  points: number       // 积分数量
  payMethod: string   // 支付方式：wechat, alipay
}

// ========== 响应 VO ==========

/** 充值套餐 */
export interface RechargePackage {
  id: number
  name: string
  price: number        // 价格（元）
  points: number       // 基础积分
  bonusPoints: number   // 赠送积分
  isHot?: boolean
  isBest?: boolean
}

/** 充值订单 */
export interface RechargeOrder {
  id: number
  orderNo: string
  name: string
  points: number
  bonusPoints: number
  totalPoints: number
  price: number        // 支付金额（元）
  payMethod: string
  status: number     // 支付状态：0=待支付，1=已支付，2=已取消
  createTime: string
}

/** 支付参数（唤起支付的必要参数） */
export interface PayParams {
  // 微信支付
  wechat?: {
    appId: string
    timeStamp: string
    nonceStr: string
    package: string
    signType: string
    paySign: string
  }
  // 支付宝
  alipay?: {
    orderString: string
  }
}

// ========== API 方法 ==========

/** 获取充值套餐列表 */
export function getRechargePackages() {
  return get<RechargePackage[]>('/trade/order/package/list')
}

/** 创建充值订单 */
export function createRechargeOrder(data: CreateRechargeOrderReqVO) {
  return post<{ orderId: number; orderNo: string }>('/trade/order/recharge/create', data)
}

/** 创建自定义充值订单 */
export function createCustomRechargeOrder(data: CustomRechargeReqVO) {
  return post<{ orderId: number; orderNo: string; payParams: PayParams }>('/trade/order/recharge/create-custom', data)
}

/** 获取订单支付参数（用于唤起支付） */
export function getOrderPayParams(orderId: number) {
  return get<PayParams>('/pay/order/get-pay-params', { orderId })
}

/** 获取我的充值记录（分页） */
export function getMyRechargeOrders(params: { pageNo?: number; pageSize?: number }) {
  return get<{ list: RechargeOrder[]; total: number }>('/trade/order/recharge/page', params)
}
