import api from './axios'

export const refreshCompetitorPrices = (productId) =>
  api.post('/competitor-prices/refresh', null, { params: { productId } })
