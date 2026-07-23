package x10.trainup.product.core.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.commons.ports.IProductQueryService;
import x10.trainup.product.core.repositories.IRepositoryProduct;

@Service
@RequiredArgsConstructor
public class ProductQueryServiceImpl implements IProductQueryService {

    private final IRepositoryProduct productRepository;

    @Override
    public boolean existsByCategoryId(String categoryId) {
        return productRepository.existsByCategoryId(categoryId);
    }
}