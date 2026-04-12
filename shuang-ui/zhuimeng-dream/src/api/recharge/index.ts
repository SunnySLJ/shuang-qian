import { get, post } from '@/utils/request'

export interface RechargePackage {
  id: number
  name: string
  payPrice: number
  bonusPrice: number
  isHot?: boolean
  isBest?: boolean
}

export interface RechargeCreateReqVO {
  packageId?: number
  payPrice?: number
}

export interface RechargeCreateRespVO {
  id: number
  payOrderId: number
}

export interface PayOrderSubmitReqVO {
  id: number
  channelCode: string
  displayMode?: string
  returnUrl?: string
  channelExtras?: Record<string, string>
}

export interface PayOrderSubmitRespVO {
  orderId: number
  orderNo: string
  status: number
  displayMode: string
  displayContent: string
  channelExtras?: Record<string, string>
}

export interface RechargeRecord {
  id: number
  totalPrice: number
  payPrice: number
  bonusPrice: number
  payChannelCode?: string
  payChannelName?: string
  payOrderId?: number
  payOrderChannelOrderNo?: string
  payTime?: string
  refundStatus?: number
}

export interface PayOrder {
  id: number
  no?: string
  status: number
  displayMode?: string
  displayContent?: string
  channelOrderNo?: string
}

export const CHANNEL_CODE_MAP: Record<string, string> = {
  wx_lite: '微信小程序',
  wx_pub: '微信公众号',
  wx_native: '微信扫码',
  alipay_qr: '支付宝',
  wallet: '钱包支付',
}

export const CHANNEL_NAME_MAP = CHANNEL_CODE_MAP

export function getRechargePackages() {
  return get<RechargePackage[]>('/pay/wallet-recharge-package/list')
}

export function createRecharge(data: RechargeCreateReqVO) {
  return post<RechargeCreateRespVO>('/pay/wallet-recharge/create', data)
}

export function submitPayOrder(data: PayOrderSubmitReqVO) {
  return post<PayOrderSubmitRespVO>('/pay/order/submit', data)
}

export function getPayOrder(no?: string, sync = false, id?: number) {
  return get<PayOrder | null>('/pay/order/get', {
    no,
    sync,
    id,
  })
}

export function getMyRechargeOrders(params: { pageNo?: number; pageSize?: number }) {
  return get<{ list: RechargeRecord[]; total: number }>('/pay/wallet-recharge/page', params)
}
