package x10.trainup.cart.core.usecases.updateCartItemUc;

public interface IUpdateQuantityUc {
    void execute(UpdateQuantityReq req,String userId);
}