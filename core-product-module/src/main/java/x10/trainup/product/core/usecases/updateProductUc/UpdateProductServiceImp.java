package x10.trainup.product.core.usecases.updateProductUc;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProductServiceImp implements IUpdateProductUc {

    private final IMediaUrlResolver mediaUrlResolver;
    private final IRepositoryProduct repositoryProduct;
    private final ICategoryReposity categoryReposity;

    @Override
    @Transactional
    public ProductEntity process(
            @NotBlank String productId,
            @Valid @NotNull UpdateProductReq req) {

        log.info("🔄 Starting product update: id={}, name={}, categoryId={}",
                productId, req.getName(), req.getCategoryId());

        try {
            ProductEntity existingProduct = findProductById(productId);

            validateCategory(req.getCategoryId());
            validateProductNameForUpdate(productId, req.getName());
            validateSizeVariants(req.getSizes());

            updateProductFields(existingProduct, req);

            ProductEntity updated = repositoryProduct.save(existingProduct);

            log.info("✅ Successfully updated product [id={}, name={}, sizes={}, totalFlavors={}]",
                    updated.getId(), updated.getName(),
                    updated.getSizes() != null ? updated.getSizes().size() : 0,
                    countTotalFlavors(updated.getSizes()));

            return updated;

        } catch (BusinessException e) {
            log.error("❌ Business error updating product: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ Unexpected error updating product: id={}", productId, e);
            throw new BusinessException(ProductError.PRODUCT_UPDATE_FAILED,
                    "Lỗi không xác định khi cập nhật sản phẩm: " + e.getMessage());
        }
    }

    // ========================================
    // 🔹 FIND & UPDATE METHODS
    // ========================================

    private ProductEntity findProductById(String productId) {
        return repositoryProduct.findById(productId)
                .orElseThrow(() -> {
                    log.warn("⚠️ Product not found: id={}", productId);
                    return new BusinessException(ProductError.PRODUCT_NOT_FOUND,
                            "Không tìm thấy sản phẩm với ID: " + productId);
                });
    }

    private void updateProductFields(ProductEntity product, UpdateProductReq req) {
        product.setName(sanitizeString(req.getName()));
        product.setDescription(sanitizeString(req.getDescription()));
        product.setBrand(sanitizeString(req.getBrand()));
        product.setCategoryId(req.getCategoryId());

        List<SizeVariantEntity> updatedSizes = mergeSizeVariants(
                product.getSizes(),
                req.getSizes()
        );
        product.setSizes(updatedSizes);

        product.setUpdatedAt(Instant.now());
    }

    // ========================================
    // 🔹 MERGE LOGIC
    // ========================================

    private List<SizeVariantEntity> mergeSizeVariants(
            List<SizeVariantEntity> existingSizes,
            List<UpdateSizeVariantReq> newSizeReqs) {

        Map<String, SizeVariantEntity> existingSizeMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(existingSizes)) {
            existingSizeMap = existingSizes.stream()
                    .collect(Collectors.toMap(SizeVariantEntity::getId, s -> s));
        }

        List<SizeVariantEntity> mergedSizes = new ArrayList<>();

        for (UpdateSizeVariantReq sizeReq : newSizeReqs) {
            if (sizeReq.getId() != null && existingSizeMap.containsKey(sizeReq.getId())) {
                SizeVariantEntity existingSize = existingSizeMap.get(sizeReq.getId());
                updateSizeVariantEntity(existingSize, sizeReq);
                mergedSizes.add(existingSize);
            } else {
                SizeVariantEntity newSize = buildNewSizeVariantEntity(sizeReq);
                mergedSizes.add(newSize);
            }
        }

        return mergedSizes;
    }

    private void updateSizeVariantEntity(SizeVariantEntity existing, UpdateSizeVariantReq req) {
        existing.setSize(sanitizeString(req.getSize()));
        existing.setPrice(req.getPrice());
        existing.setDiscountPrice(req.getDiscountPrice());
        existing.setWeight(sanitizeString(req.getWeight()));

        // 🧠 Cập nhật ảnh chính chỉ khi user gửi field này
        if (req.getImageUrl() != null) {
            if (!req.getImageUrl().isBlank()) {
                existing.setImageUrl(resolveImageUrl(req.getImageUrl()));
            } else {
                existing.setImageUrl(null);
            }
        }

        // 🧠 Cập nhật ảnh phụ chỉ khi user gửi field này
        if (req.getImageUrls() != null) {
            if (!req.getImageUrls().isEmpty()) {
                existing.setImageUrls(resolveImageUrls(req.getImageUrls()));
            } else {
                existing.setImageUrls(Collections.emptyList());
            }
        }

        List<FlavorVariantEntity> mergedFlavors = mergeFlavors(
                existing.getFlavors(),
                req.getFlavors()
        );
        existing.setFlavors(mergedFlavors);
    }

    private SizeVariantEntity buildNewSizeVariantEntity(UpdateSizeVariantReq req) {
        return SizeVariantEntity.builder()
                .id(generateUUID())
                .size(sanitizeString(req.getSize()))
                .price(req.getPrice())
                .discountPrice(req.getDiscountPrice())
                .weight(sanitizeString(req.getWeight()))
                .imageUrl(resolveImageUrl(req.getImageUrl()))
                .imageUrls(resolveImageUrls(req.getImageUrls()))
                .flavors(buildNewFlavors(req.getFlavors()))
                .build();
    }

    private List<FlavorVariantEntity> mergeFlavors(
            List<FlavorVariantEntity> existingFlavors,
            List<UpdateFlavorVariantReq> newFlavorReqs) {

        if (CollectionUtils.isEmpty(newFlavorReqs)) {
            return Collections.emptyList();
        }

        Map<String, FlavorVariantEntity> existingFlavorMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(existingFlavors)) {
            existingFlavorMap = existingFlavors.stream()
                    .collect(Collectors.toMap(FlavorVariantEntity::getId, f -> f));
        }

        List<FlavorVariantEntity> mergedFlavors = new ArrayList<>();

        for (UpdateFlavorVariantReq flavorReq : newFlavorReqs) {
            if (flavorReq.getId() != null && existingFlavorMap.containsKey(flavorReq.getId())) {
                FlavorVariantEntity existingFlavor = existingFlavorMap.get(flavorReq.getId());
                updateFlavorVariantEntity(existingFlavor, flavorReq);
                mergedFlavors.add(existingFlavor);
            } else {
                FlavorVariantEntity newFlavor = buildNewFlavorVariantEntity(flavorReq);
                mergedFlavors.add(newFlavor);
            }
        }

        return mergedFlavors;
    }

    private void updateFlavorVariantEntity(FlavorVariantEntity existing, UpdateFlavorVariantReq req) {
        existing.setFlavor(sanitizeString(req.getFlavor()));

        if (req.getQuantityInStock() != null) {
            existing.setQuantityInStock(req.getQuantityInStock());
        }

        if (req.getActive() != null) {
            existing.setActive(req.getActive());
        }
    }

    private List<FlavorVariantEntity> buildNewFlavors(List<UpdateFlavorVariantReq> flavorReqs) {
        if (CollectionUtils.isEmpty(flavorReqs)) {
            return Collections.emptyList();
        }

        return flavorReqs.stream()
                .map(this::buildNewFlavorVariantEntity)
                .collect(Collectors.toList());
    }

    private FlavorVariantEntity buildNewFlavorVariantEntity(UpdateFlavorVariantReq req) {
        return FlavorVariantEntity.builder()
                .id(generateUUID())
                .flavor(sanitizeString(req.getFlavor()))
                .quantityInStock(Optional.ofNullable(req.getQuantityInStock()).orElse(0))
                .quantitySold(0)
                .active(Optional.ofNullable(req.getActive()).orElse(true))
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

    private void validateProductNameForUpdate(String productId, String newName) {
        if (newName == null || newName.isBlank()) {
            throw new BusinessException(ProductError.INVALID_PRODUCT_NAME);
        }

        String sanitizedName = sanitizeString(newName);

        Optional<ProductEntity> existingProduct = repositoryProduct
                .findByNameContainingIgnoreCase(sanitizedName)
                .stream()
                .filter(p -> p.getName().equalsIgnoreCase(sanitizedName))
                .findFirst();

        if (existingProduct.isPresent() && !existingProduct.get().getId().equals(productId)) {
            log.warn("⚠️ Product name already exists: name={}", sanitizedName);
            throw new BusinessException(ProductError.PRODUCT_ALREADY_EXISTS,
                    "Sản phẩm với tên '" + sanitizedName + "' đã tồn tại");
        }
    }

    private void validateSizeVariants(List<UpdateSizeVariantReq> sizes) {
        if (CollectionUtils.isEmpty(sizes)) {
            throw new BusinessException(ProductError.INVALID_SIZE_VARIANTS,
                    "Sản phẩm phải có ít nhất 1 size");
        }

        long uniqueSizes = sizes.stream()
                .map(UpdateSizeVariantReq::getSize)
                .map(this::sanitizeString)
                .distinct()
                .count();

        if (uniqueSizes < sizes.size()) {
            throw new BusinessException(ProductError.DUPLICATE_SIZE_VARIANTS,
                    "Có size bị trùng lặp");
        }

        sizes.forEach(this::validatePriceLogic);
        sizes.forEach(this::validateFlavorsInSize);
    }

    private void validatePriceLogic(UpdateSizeVariantReq sizeReq) {
        if (sizeReq.getPrice() == null) {
            throw new BusinessException(ProductError.INVALID_PRICE,
                    "Giá của size '" + sizeReq.getSize() + "' không được null");
        }

        if (sizeReq.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ProductError.INVALID_PRICE,
                    "Giá của size '" + sizeReq.getSize() + "' phải lớn hơn 0");
        }

        if (sizeReq.getDiscountPrice() != null) {
            if (sizeReq.getDiscountPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(ProductError.INVALID_DISCOUNT_PRICE,
                        "Giá giảm của size '" + sizeReq.getSize() + "' phải lớn hơn 0");
            }

            if (sizeReq.getDiscountPrice().compareTo(sizeReq.getPrice()) >= 0) {
                throw new BusinessException(ProductError.INVALID_DISCOUNT_PRICE,
                        "Giá giảm phải nhỏ hơn giá gốc cho size '" + sizeReq.getSize() + "'");
            }
        }
    }

    private void validateFlavorsInSize(UpdateSizeVariantReq sizeReq) {
        if (CollectionUtils.isEmpty(sizeReq.getFlavors())) {
            return;
        }

        long uniqueFlavors = sizeReq.getFlavors().stream()
                .map(UpdateFlavorVariantReq::getFlavor)
                .map(this::sanitizeString)
                .distinct()
                .count();

        if (uniqueFlavors < sizeReq.getFlavors().size()) {
            throw new BusinessException(ProductError.DUPLICATE_FLAVORS,
                    "Size '" + sizeReq.getSize() + "' có hương vị trùng lặp");
        }

        sizeReq.getFlavors().forEach(flavor -> {
            if (flavor.getQuantityInStock() != null && flavor.getQuantityInStock() < 0) {
                throw new BusinessException(ProductError.INVALID_QUANTITY,
                        "Số lượng tồn kho không được âm cho flavor '" + flavor.getFlavor() + "'");
            }
        });
    }

    // ========================================
    // 🔹 MEDIA URL HANDLING — FIXED
    // ========================================

    private String resolveImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        // ⚠️ Nếu đã là URL đầy đủ rồi thì giữ nguyên
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath.trim();
        }

        try {
            return mediaUrlResolver.resolvePublicUrl(imagePath.trim());
        } catch (Exception e) {
            log.warn("⚠️ Failed to resolve image URL: path={}, error={}", imagePath, e.getMessage());
            return imagePath;
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
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ========================================
    // 🔹 UTILITY METHODS
    // ========================================

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String sanitizeString(String input) {
        if (input == null) return null;
        return input.trim();
    }

    private int countTotalFlavors(List<SizeVariantEntity> sizes) {
        if (CollectionUtils.isEmpty(sizes)) return 0;
        return sizes.stream()
                .map(SizeVariantEntity::getFlavors)
                .filter(f -> !CollectionUtils.isEmpty(f))
                .mapToInt(List::size)
                .sum();
    }
}
