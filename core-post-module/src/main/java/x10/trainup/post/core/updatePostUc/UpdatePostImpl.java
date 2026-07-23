package x10.trainup.post.core.updatePostUc;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import x10.trainup.commons.domain.entities.PostEntity;
import x10.trainup.media.core.usecases.ICoreMediaService;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaReq;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaRes;
import x10.trainup.post.core.repositories.IPostRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UpdatePostImpl implements IUpdatePostUc {

    private final IPostRepository postRepository;
    private final ICoreMediaService coreMediaService;
    private final ExecutorService executor;

    private static final long UPLOAD_TIMEOUT_MS = 15_000;

    public UpdatePostImpl(IPostRepository postRepository, ICoreMediaService coreMediaService) {
        this.postRepository = postRepository;
        this.coreMediaService = coreMediaService;
        this.executor = Executors.newWorkStealingPool(10);
    }

    @PreDestroy
    public void cleanup() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public PostEntity process(UpdatePostReq req, String authorId) {
        Objects.requireNonNull(req, "UpdatePostReq không được null");
        Objects.requireNonNull(authorId, "AuthorId không được null");

        PostEntity post = postRepository.findById(req.getPostId());
        if (post == null) {
            throw new RuntimeException("❌ Không tìm thấy bài viết cần cập nhật");
        }

        if (!post.getAuthorId().equals(authorId)) {
            throw new RuntimeException("❌ Bạn không có quyền chỉnh sửa bài viết này");
        }

        log.info("🔧 Bắt đầu cập nhật bài viết ID={} bởi user={}", post.getId(), authorId);

        // ✅ Danh sách media giữ lại
        List<String> updatedMediaUrls = new ArrayList<>();
        if (!CollectionUtils.isEmpty(req.getKeepUrls())) {
            updatedMediaUrls.addAll(req.getKeepUrls());
            log.info("📎 Giữ lại {} media cũ", req.getKeepUrls().size());
        }

        // ✅ Upload file mới song song
        if (!CollectionUtils.isEmpty(req.getNewFiles())) {
            log.info("📤 Bắt đầu upload {} file mới", req.getNewFiles().size());
//            List<UploadMediaRes> uploadResults = uploadFilesParallel(req.getNewFiles(), authorId);
//
//            for (UploadMediaRes res : uploadResults) {
//                if (res != null) {
//                    updatedMediaUrls.add(res.getSecureUrl());
//                }
//            }
        }

        // ✅ Cập nhật nội dung
        if (req.getContent() != null && !req.getContent().isBlank()) {
            post.setContent(req.getContent().trim());
        }

        post.setMediaUrls(updatedMediaUrls);
        post.setUpdatedAt(Instant.now());

        PostEntity savedPost = postRepository.save(post);
        log.info("✅ Cập nhật bài viết thành công, ID={}, tổng số media={}", savedPost.getId(), updatedMediaUrls.size());

        return savedPost;
    }

    // ------------------------------------------------------------------------

//    private List<UploadMediaRes> uploadFilesParallel(List<MultipartFile> files, String authorId) {
//        List<CompletableFuture<UploadMediaRes>> futures = new ArrayList<>();
//
//        for (MultipartFile file : files) {
//            CompletableFuture<UploadMediaRes> future = CompletableFuture.supplyAsync(() -> {
//                        try {
//                            byte[] bytes = file.getBytes();
//                            UploadMediaReq uploadReq = UploadMediaReq.builder()
//                                    .file(file)
//                                    .folder("trainup/posts/" + authorId)
//                                    .mediaType(getMediaType(file.getContentType()))
//                                    .fileBytes(bytes)
//                                    .build();
//
//                            uploadReq.validate();
//                            log.debug("🔹 Upload file: {}", file.getOriginalFilename());
//
//                            UploadMediaRes uploadRes = coreMediaService.uploadMedia(uploadReq);
//                            log.debug("✅ Upload xong: {} => {}", file.getOriginalFilename(), uploadRes.getSecureUrl());
//                            return uploadRes;
//
//                        } catch (Exception e) {
//                            log.error("❌ Lỗi upload file {}: {}", file.getOriginalFilename(), e.getMessage());
//                            return null;
//                        }
//                    }, executor)
//                    .orTimeout(UPLOAD_TIMEOUT_MS, TimeUnit.MILLISECONDS)
//                    .exceptionally(ex -> {
//                        log.warn("⚠️ Timeout hoặc lỗi async ({}): {}", file.getOriginalFilename(), ex.getMessage());
//                        return null;
//                    });
//
//            futures.add(future);
//        }
//
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
//
//        List<UploadMediaRes> results = new ArrayList<>();
//        for (CompletableFuture<UploadMediaRes> future : futures) {
//            UploadMediaRes res = future.getNow(null);
//            if (res != null && res.getSecureUrl() != null) {
//                results.add(res);
//            }
//        }
//
//        log.info("🎯 Upload thành công {} file", results.size());
//        return results;
//    }
//
//    private String getMediaType(String contentType) {
//        if (contentType == null) return "auto";
//        if (contentType.startsWith("image/")) return "image";
//        if (contentType.startsWith("video/")) return "video";
//        return "raw";
//    }
}
