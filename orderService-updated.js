// src/services/orderService.js
import axiosClient from "../config/axios/axiosClient";

const orderService = {
  // Lấy tất cả đơn hàng (sắp xếp mới nhất trước)
  getAllOrders: () => {
    return axiosClient.get("/orders").then((res) => res.data?.data ?? res.data ?? []);
  },

  // Tạo đơn hàng mới
  createOrder: (data) => {
    return axiosClient.post("/orders", data).then((res) => res.data?.data ?? res.data);
  },

  // Cập nhật trạng thái đơn hàng
  updateOrderStatus: (orderId, status, note = "") => {
    return axiosClient.patch(`/orders/${orderId}/status`, {
      status: status,
      note: note
    }).then((res) => res.data?.data ?? res.data);
  },

  // Lấy đơn hàng theo userId
  getOrdersByUserId: (userId) => {
    return axiosClient.get(`/orders/user/${userId}`).then((res) => res.data?.data ?? res.data ?? []);
  }
};

export default orderService;
