package x10.trainup.cart.core.usecases;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.cart.core.usecases.RemoveListItemsUC.IRemoveListItemsUc;
import x10.trainup.cart.core.usecases.RemoveListItemsUC.RemoveListItemsReq;
import x10.trainup.cart.core.usecases.RemoveListItemsUC.RemoveListItemsResp;
import x10.trainup.cart.core.usecases.cleanCartUc.IClearCartUc;
import x10.trainup.cart.core.usecases.createCartUc.CreateCartReq;
import x10.trainup.cart.core.usecases.createCartUc.CreateCartRes;
import x10.trainup.cart.core.usecases.createCartUc.ICreateCartUc;
import x10.trainup.cart.core.usecases.deCreaseCartUc.DecreaseReq;
import x10.trainup.cart.core.usecases.deCreaseCartUc.IDecreaseCartUc;
import x10.trainup.cart.core.usecases.getCartUc.GetCartRes;
import x10.trainup.cart.core.usecases.getCartUc.IGerCartUc;
import x10.trainup.cart.core.usecases.inCreaseCartUc.IInCreaseCartUC;
import x10.trainup.cart.core.usecases.inCreaseCartUc.IncreaseCartReq;
import x10.trainup.cart.core.usecases.removeItemUc.IRemoveItemUc;
import x10.trainup.cart.core.usecases.removeItemUc.RemoveItemReq;
import x10.trainup.cart.core.usecases.updateCartItemUc.IUpdateQuantityUc;
import x10.trainup.cart.core.usecases.updateCartItemUc.UpdateQuantityReq;

@Service
@AllArgsConstructor
public class CoreCartServiceImpl implements ICoreCartService{

    private final ICreateCartUc iCreateCartUc;
    private final IInCreaseCartUC inCreaseCartUC;
    private final IDecreaseCartUc iDecreaseCartUc;
    private final IGerCartUc iGerCartUc;
    private final IClearCartUc iClearCartUc;
    private final IRemoveItemUc iRemoveItemUc;
    private final IUpdateQuantityUc iUpdateQuantityUc;
    private final IRemoveListItemsUc removeListItemsUc;


    @Override
    public void removeListItems(RemoveListItemsReq req, String userId) {
         removeListItemsUc.execute(req, userId);
    }


    @Override
    public CreateCartRes createCart(CreateCartReq req, String userId){
        return iCreateCartUc.addToCart(req, userId);
    }

    @Override
    public void inCreaseCart(IncreaseCartReq req, String userId){
        inCreaseCartUC.increase(req, userId);
    }

    @Override
    public void deCreaseCart(DecreaseReq req, String userId){
        iDecreaseCartUc.excute(req, userId);
    }

    @Override
    public GetCartRes getCart( String userId){
        return iGerCartUc.excute(userId);
    }

    @Override
    public void clearCart( String userId) {
        iClearCartUc.execute( userId);
    }

    @Override
    public void removeItemFromCart(RemoveItemReq req, String userId) {
        iRemoveItemUc.execute(req, userId);
    }

    @Override
    public void updateCartItemQuantity(UpdateQuantityReq req, String userId) {
        iUpdateQuantityUc.execute(req, userId);
    }
}