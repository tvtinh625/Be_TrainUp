package x10.trainup.cart.core.usecases.RemoveListItemsUC;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemoveListItemsResp {
    private int removedCount;           // Số items đã xóa thành công
    private int totalRequested;         // Tổng số items yêu cầu xóa
    private int notFoundCount;          // Số items không tìm thấy
    private String message;             // Thông báo
    private List<CartItemIdentifier> notFoundItems; // Danh sách items không tìm thấy

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartItemIdentifier {
        private String productId;
        private String sizeId;
        private String flavorId;
    }
}