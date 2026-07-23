package x10.trainup.product.core.errors;

import x10.trainup.commons.exceptions.ErrorDescriptor;

public enum ProductError implements ErrorDescriptor {

    // 🔹 Product validation
    PRODUCT_UPDATE_FAILED(400, "PRD.NAME_REQUIRED", "Failed to update product"),
    INVALID_PRODUCT_NAME(400, "PRD.NAME_INVALID",  "Tên sản phẩm không được để trống"),
    PRODUCT_DESCRIPTION_REQUIRED(400, "PRD.DESCRIPTION_REQUIRED", "Product description is required"),
    PRODUCT_BRAND_REQUIRED(400, "PRD.BRAND_REQUIRED", "Product brand is required"),
    PRODUCT_ALREADY_EXISTS(409, "PRD.ALREADY_EXISTS", "Product with this name already exists"),
    PRODUCT_NOT_FOUND(404, "PRD.NOT_FOUND", "Product not found"),
    PRODUCT_INACTIVE(403, "PRD.INACTIVE", "Product is inactive"),
    PRODUCT_CREATION_FAILED(500, "PRD.CREATION_FAILED", "Failed to create product"),

    // 📁 Category validation
    CATEGORY_ID_REQUIRED(400, "PRD.CATEGORY_ID_REQUIRED", "Category ID is required"),
    CATEGORY_NOT_FOUND(404, "PRD.CATEGORY_NOT_FOUND", "Category not found"),
    INVALID_CATEGORY(400, "PRD.CATEGORY_INVALID", "Category ID is invalid"),

    // 📏 Size variant validation
    SIZE_REQUIRED(400, "PRD.SIZE_REQUIRED", "At least one size variant is required"),
    SIZE_NAME_REQUIRED(400, "PRD.SIZE_NAME_REQUIRED", "Size name is required"),
    INVALID_SIZE_VARIANTS(400, "PRD.SIZE_NAME_INVALID", "Sản phẩm phải có ít nhất 1 size"),
    SIZE_DUPLICATE(400, "PRD.SIZE_DUPLICATE", "Duplicate size variants found"),
    DUPLICATE_SIZE_VARIANTS(400, "PRD.SIZE_LIMIT_EXCEEDED",   "Không được có size trùng lặp"),
    SIZE_INVALID(400, "PRD.SIZE_INVALID", "Size variant data is invalid"),

    // 💰 Price & discount validation
    PRICE_REQUIRED(400, "PRD.PRICE_REQUIRED", "Price is required"),
    INVALID_PRICE(400, "PRD.PRICE_INVALID", "Price must be greater than zero"),
    PRICE_FORMAT_INVALID(400, "PRD.PRICE_FORMAT_INVALID", "Price format is invalid"),
    INVALID_DISCOUNT_PRICE(400, "PRD.DISCOUNT_INVALID", "Discount price must be less than original price"),
    DISCOUNT_NEGATIVE(400, "PRD.DISCOUNT_NEGATIVE", "Discount price cannot be negative"),
    DISCOUNT_FORMAT_INVALID(400, "PRD.DISCOUNT_FORMAT_INVALID", "Discount price format is invalid"),

    // 🎨 Flavor variant validation
    FLAVOR_NAME_REQUIRED(400, "PRD.FLAVOR_NAME_REQUIRED", "Flavor name is required"),
    FLAVOR_NAME_INVALID(400, "PRD.FLAVOR_NAME_INVALID", "Flavor name is invalid"),
    DUPLICATE_FLAVORS(400, "PRD.FLAVOR_DUPLICATE", "Duplicate flavors found in size variant"),
    FLAVOR_LIMIT_EXCEEDED(400, "PRD.FLAVOR_LIMIT_EXCEEDED", "Flavor variants limit exceeded"),

    // 📦 Stock & quantity
    INVALID_QUANTITY(400, "PRD.STOCK_INVALID", "Quantity in stock must be zero or positive"),
    STOCK_NEGATIVE(400, "PRD.STOCK_NEGATIVE", "Quantity in stock cannot be negative"),
    QUANTITY_INVALID(400, "PRD.QUANTITY_INVALID", "Quantity value is invalid"),
    QUANTITY_EXCEEDS_LIMIT(400, "PRD.QUANTITY_EXCEEDS_LIMIT", "Quantity exceeds maximum limit"),

    // ⚖️ Weight validation
    WEIGHT_INVALID(400, "PRD.WEIGHT_INVALID", "Weight format is invalid"),
    WEIGHT_NEGATIVE(400, "PRD.WEIGHT_NEGATIVE", "Weight cannot be negative"),

    // 🖼️ Image / Media
    IMAGE_URL_INVALID(400, "PRD.IMAGE_URL_INVALID", "Image URL is invalid or too long"),
    IMAGE_URL_REQUIRED(400, "PRD.IMAGE_URL_REQUIRED", "At least one image URL is required"),
    IMAGE_UPLOAD_FAILED(500, "PRD.IMAGE_UPLOAD_FAILED", "Failed to upload product image"),
    IMAGE_LIMIT_EXCEEDED(400, "PRD.IMAGE_LIMIT_EXCEEDED", "Image limit exceeded"),
    IMAGE_RESOLUTION_FAILED(500, "PRD.IMAGE_RESOLUTION_FAILED", "Failed to resolve image URL"),

    // 🔒 Business logic validation
    INVALID_PRODUCT_STATE(400, "PRD.INVALID_STATE", "Product state is invalid for this operation"),
    VALIDATION_FAILED(400, "PRD.VALIDATION_FAILED", "Product validation failed"),
    DUPLICATE_ENTRY(409, "PRD.DUPLICATE_ENTRY", "Duplicate product entry detected");

    private final int http;
    private final String code;
    private final String msg;

    ProductError(int http, String code, String msg) {
        this.http = http;
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int httpStatus() {
        return http;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultMessage() {
        return msg;
    }
}