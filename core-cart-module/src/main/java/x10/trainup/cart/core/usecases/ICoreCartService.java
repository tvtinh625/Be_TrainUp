package x10.trainup.cart.core.usecases;

import x10.trainup.cart.core.usecases.RemoveListItemsUC.RemoveListItemsReq;
import x10.trainup.cart.core.usecases.RemoveListItemsUC.RemoveListItemsResp;
import x10.trainup.cart.core.usecases.createCartUc.CreateCartReq;
import x10.trainup.cart.core.usecases.createCartUc.CreateCartRes;
import x10.trainup.cart.core.usecases.deCreaseCartUc.DecreaseReq;
import x10.trainup.cart.core.usecases.getCartUc.GetCartRes;
import x10.trainup.cart.core.usecases.inCreaseCartUc.IncreaseCartReq;
import x10.trainup.cart.core.usecases.removeItemUc.RemoveItemReq;
import x10.trainup.cart.core.usecases.updateCartItemUc.UpdateQuantityReq;

public interface ICoreCartService {
    CreateCartRes createCart(CreateCartReq req, String userId);
    void inCreaseCart(IncreaseCartReq req, String userId);
    void deCreaseCart(DecreaseReq req, String userId);
    GetCartRes getCart( String userId);
    void removeItemFromCart(RemoveItemReq req, String userId);
    void updateCartItemQuantity(UpdateQuantityReq req, String userId);
    void clearCart( String userId);

    void removeListItems(RemoveListItemsReq req, String userId);

}