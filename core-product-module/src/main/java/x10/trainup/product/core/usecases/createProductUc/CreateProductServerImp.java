package x10.trainup.product.core.usecases.createProductUc;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import x10.trainup.category.core.repositories.ICategoryReposity;
import x10.trainup.commons.domain.entities.FlavorVariantEntity;
import x10.trainup.commons.domain.entities.ProductEntity;
import x10.trainup.commons.domain.entities.SizeVariantEntity;
import x10.trainup.commons.exceptions.BusinessException;
import x10.trainup.commons.util.IMediaUrlResolver;
import x10.trainup.product.core.errors.ProductError;
import x10.trainup.product.core.repositories.IRepositoryProduct;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateProductServerImp implements ICreateProductUc {

    private final IMediaUrlResolver mediaUrlResolver;
    private final IRepositoryProduct repositoryProduct;
    private final ICategoryReposity categoryReposity;

    @Override
    @Transactional
    public ProductEntity process(@Valid @NotNull CreateProductReq req) {
        log.info("🚀 Starting product creation: name={}, brand={}, categoryId={}",
                req.getName(), req.getBrand(), req.getCategoryId());

        try {
            // Validate business rules
            validateCategory(req.getCategoryId());
            validateProductName(req.getName());
            validateSizeVariants(req.getSizes());

            // Build product entity
            ProductEntity product = buildProductEntity(req);

            // Save to database
            ProductEntity saved = repositoryProduct.save(product);

            log.info("✅ Successfully created product [id={}, name={}, sizes={}, totalFlavors={}]",
                    saved.getId(), saved.getName(),
                    saved.getSizes() != null ? saved.getSizes().size() : 0,
                    countTotalFlavors(saved.getSizes()));

            return saved;

        } catch (BusinessException e) {
            log.error("❌ Business error creating product: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ Unexpected error creating product: name={}", req.getName(), e);
            throw new BusinessException(ProductError.PRODUCT_CREATION_FAILED,
                    "Lỗi không xác định khi tạo sản phẩm: " + e.getMessage());
        }
    }

    // ========================================
    // 🔹 BUILD METHODS
    // ========================================

    private ProductEntity buildProductEntity(CreateProductReq req) {
        List<SizeVariantEntity> sizeEntities = mapSizeVariants(req.getSizes());
        Instant now = Instant.now();

        return ProductEntity.builder()
                .name(sanitizeString(req.getName()))
                .description(sanitizeString(req.getDescription()))
                .brand(sanitizeString(req.getBrand()))
                .categoryId(req.getCategoryId())
                .sizes(sizeEntities)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private List<SizeVariantEntity> mapSizeVariants(List<SizeVariantReq> sizeReqs) {
        if (CollectionUtils.isEmpty(sizeReqs)) {
            return Collections.emptyList();
        }

        return sizeReqs.stream()
                .map(this::buildSizeVariantEntity)
                .collect(Collectors.toList());
    }

    private SizeVariantEntity buildSizeVariantEntity(SizeVariantReq sizeReq) {
        return SizeVariantEntity.builder()
                .id(generateUUID())
                .size(sanitizeString(sizeReq.getSize()))
                .price(sizeReq.getPrice())
                .discountPrice(sizeReq.getDiscountPrice())
                .weight(sanitizeString(sizeReq.getWeight()))
                .imageUrl(resolveImageUrl(sizeReq.getImageUrl()))
                .imageUrls(resolveImageUrls(sizeReq.getImageUrls()))
                .flavors(mapFlavors(sizeReq.getFlavors()))
                .build();
    }

    private List<FlavorVariantEntity> mapFlavors(List<FlavorVariantReq> flavorReqs) {
        if (CollectionUtils.isEmpty(flavorReqs)) {
            return Collections.emptyList();
        }

        return flavorReqs.stream()
                .map(this::buildFlavorVariantEntity)
                .collect(Collectors.toList());
    }

    private FlavorVariantEntity buildFlavorVariantEntity(FlavorVariantReq flavorReq) {
        return FlavorVariantEntity.builder()
                .id(generateUUID())
                .flavor(sanitizeString(flavorReq.getFlavor()))
                .quantityInStock(Optional.ofNullable(flavorReq.getQuantityInStock()).orElse(0))
                .quantitySold(0)
                .active(flavorReq.isActive())
                .build();
    }

    // ========================================
    // 🔹 VALIDATION METHODS
    // ========================================

    private void validateCategory(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            throw new BusinessException(ProductError.INVALID_CATEGORY);
        }

        if (!categoryReposity.existsById(categoryId)) {
            log.warn("⚠️ Category not found: categoryId={}", categoryId);
            throw new BusinessException(ProductError.CATEGORY_NOT_FOUND,
                    "Danh mục không tồn tại với ID: " + categoryId);
        }
    }

    private void validateProductName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(ProductError.INVALID_PRODUCT_NAME);
        }

        String sanitizedName = sanitizeString(name);
        boolean exists = repositoryProduct.findByNameContainingIgnoreCase(sanitizedName)
                .stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(sanitizedName));

        if (exists) {
            log.warn("⚠️ Product name already exists: name={}", sanitizedName);
            throw new BusinessException(ProductError.PRODUCT_ALREADY_EXISTS,
                    "Sản phẩm với tên '" + sanitizedName + "' đã tồn tại");
        }
    }

    private void validateSizeVariants(List<SizeVariantReq> sizes) {
        if (CollectionUtils.isEmpty(sizes)) {
            throw new BusinessException(ProductError.INVALID_SIZE_VARIANTS
                    );
        }

        // Check for duplicate sizes
        long uniqueSizes = sizes.stream()
                .map(SizeVariantReq::getSize)
                .map(this::sanitizeString)
                .distinct()
                .count();

        if (uniqueSizes < sizes.size()) {
            throw new BusinessException(ProductError.DUPLICATE_SIZE_VARIANTS
                  );
        }

        // Validate price logic for each size
        sizes.forEach(this::validatePriceLogic);

        // Validate flavors within each size
        sizes.forEach(this::validateFlavorsInSize);
    }

    private void validatePriceLogic(SizeVariantReq sizeReq) {
        if (sizeReq.getPrice() == null) {
            throw new BusinessException(ProductError.INVALID_PRICE,
                    "Giá của size '" + sizeReq.getSize() + "' không được null");
        }

        if (sizeReq.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ProductError.INVALID_PRICE,
                    "Giá của size '" + sizeReq.getSize() + "' phải lớn hơn 0");
        }

        // Validate discount price logic
        if (sizeReq.getDiscountPrice() != null) {
            if (sizeReq.getDiscountPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(ProductError.INVALID_DISCOUNT_PRICE,
                        "Giá giảm của size '" + sizeReq.getSize() + "' phải lớn hơn 0");
            }

            if (sizeReq.getDiscountPrice().compareTo(sizeReq.getPrice()) >= 0) {
                throw new BusinessException(ProductError.INVALID_DISCOUNT_PRICE,
                        "Giá giảm của size '" + sizeReq.getSize() +
                                "' phải nhỏ hơn giá gốc");
            }
        }
    }

    private void validateFlavorsInSize(SizeVariantReq sizeReq) {
        if (CollectionUtils.isEmpty(sizeReq.getFlavors())) {
            return; // Flavors are optional
        }

        // Check for duplicate flavors
        long uniqueFlavors = sizeReq.getFlavors().stream()
                .map(FlavorVariantReq::getFlavor)
                .map(this::sanitizeString)
                .distinct()
                .count();

        if (uniqueFlavors < sizeReq.getFlavors().size()) {
            throw new BusinessException(ProductError.DUPLICATE_FLAVORS,
                    "Size '" + sizeReq.getSize() + "' có hương vị trùng lặp");
        }

        // Validate quantity
        sizeReq.getFlavors().forEach(flavor -> {
            if (flavor.getQuantityInStock() != null && flavor.getQuantityInStock() < 0) {
                throw new BusinessException(ProductError.INVALID_QUANTITY,
                        "Số lượng tồn kho của hương vị '" + flavor.getFlavor() +
                                "' không được âm");
            }
        });
    }

    // ========================================
    // 🔹 MEDIA URL HANDLING
    // ========================================

    private String resolveImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        try {
            return mediaUrlResolver.resolvePublicUrl(imagePath.trim());
        } catch (Exception e) {
            log.warn("⚠️ Failed to resolve image URL: path={}, error={}",
                    imagePath, e.getMessage());
            return null;
        }
    }

    private List<String> resolveImageUrls(List<String> imagePaths) {
        if (CollectionUtils.isEmpty(imagePaths)) {
            return Collections.emptyList();
        }

        return imagePaths.stream()
                .filter(path -> path != null && !path.isBlank())
                .map(String::trim)
                .map(this::resolveImageUrl)
                .filter(url -> url != null)
                .collect(Collectors.toList());
    }

    // ========================================
    // 🔹 UTILITY METHODS
    // ========================================

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        return input.trim();
    }

    private int countTotalFlavors(List<SizeVariantEntity> sizes) {
        if (CollectionUtils.isEmpty(sizes)) {
            return 0;
        }

        return sizes.stream()
                .map(SizeVariantEntity::getFlavors)
                .filter(flavors -> !CollectionUtils.isEmpty(flavors))
                .mapToInt(List::size)
                .sum();
    }
}