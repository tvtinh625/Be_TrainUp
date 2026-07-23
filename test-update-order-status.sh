#!/bin/bash

# Test UPDATE ORDER STATUS - PATCH /api/orders/{orderId}/status

# Ví dụ: Cập nhật đơn hàng với ID "order-123" sang trạng thái CONFIRMED

curl -X PATCH http://localhost:3000/api/orders/order-123/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CONFIRMED",
    "note": "Đã xác nhận đơn hàng"
  }'

# Hoặc cập nhật sang CANCELLED
curl -X PATCH http://localhost:3000/api/orders/order-123/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "CANCELLED",
    "note": "Hủy đơn hàng do khách yêu cầu"
  }'

# Postman: 
# Method: PATCH
# URL: http://localhost:3000/api/orders/order-123/status
# Body (raw JSON):
# {
#   "status": "CONFIRMED",
#   "note": "Xác nhận đơn hàng"
# }
