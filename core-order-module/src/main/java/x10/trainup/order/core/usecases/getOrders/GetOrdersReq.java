package x10.trainup.order.core.usecases.getOrders;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetOrdersReq {
    private Integer page;
    private Integer size;
    private String sortBy; // "createdDate", "updatedDate", etc.
    private String sortDirection; // "ASC" hoặc "DESC"
}