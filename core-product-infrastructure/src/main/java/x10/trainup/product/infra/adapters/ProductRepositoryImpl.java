package x10.trainup.product.infra.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import x10.trainup.commons.domain.entities.ProductEntity;
import x10.trainup.product.core.repositories.IRepositoryProduct;
import x10.trainup.product.infra.adapters.mapper.ProductMapper;
import x10.trainup.product.infra.datascoures.mongodb.MongoProductRepository;
import x10.trainup.product.infra.datascoures.mongodb.ProductDocument;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements IRepositoryProduct {

    private final MongoProductRepository mongoRepo;

    @Override
    public ProductEntity save(ProductEntity productEntity) {
        ProductDocument document = ProductMapper.toDocument(productEntity);
        ProductDocument saved = mongoRepo.save(document);
        return ProductMapper.toEntity(saved);
    }

    @Override
    public Optional<ProductEntity> findById(String id) {
        return mongoRepo.findById(id)
                .map(ProductMapper::toEntity);
    }

    @Override
    public List<ProductEntity> findAll() {
        return mongoRepo.findAll()
                .stream()
                .map(ProductMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        mongoRepo.deleteById(id);
    }

    @Override
    public List<ProductEntity> findByNameContainingIgnoreCase(String name) {
        return mongoRepo.findByNameContainingIgnoreCase(name)
                .stream()
                .map(ProductMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductEntity> findByCategoryId(String categoryId) {
        return mongoRepo.findByCategoryId(categoryId)
                .stream()
                .map(ProductMapper::toEntity)
                .collect(Collectors.toList());
    }
    @Override
    public boolean existsByCategoryId(String categoryId) {
        return mongoRepo.existsByCategoryId(categoryId);
    }

    @Override
    public boolean existsSize(String productId, String sizeId) {
        return mongoRepo.findById(productId)
                .map(product -> product.getSizes().stream()
                        .anyMatch(size -> size.getId().equals(sizeId)))
                .orElse(false);
    }

    @Override
    public boolean existsFlavor(String productId, String sizeId, String flavorId) {
        return mongoRepo.findById(productId)
                .map(product -> product.getSizes().stream()
                        .filter(size -> size.getId().equals(sizeId))
                        .flatMap(size -> size.getFlavors().stream())
                        .anyMatch(flavor -> flavor.getId().equals(flavorId)))
                .orElse(false);
    }

}
