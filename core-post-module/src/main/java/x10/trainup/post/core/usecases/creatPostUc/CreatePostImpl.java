package x10.trainup.post.core.usecases.creatPostUc;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import x10.trainup.commons.domain.entities.PostEntity;
import x10.trainup.media.core.usecases.ICoreMediaService;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaReq;
import x10.trainup.media.core.usecases.uploadMedia.UploadMediaRes;
import x10.trainup.post.core.repositories.IPostRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CreatePostImpl implements ICreatePostUc {

    private final IPostRepository postRepository;
    private final ICoreMediaService coreMediaService;
    private final ExecutorService executor;

    private static final long UPLOAD_TIMEOUT_MS = 15_000;

    public CreatePostImpl(IPostRepository postRepository, ICoreMediaService coreMediaService) {
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
    public PostEntity process(CreatePostReq req, String authorId, String authorName) {
//        List<UploadMediaRes> uploadedMedia = new ArrayList<>();
//
//        if (req.getFiles() != null && !req.getFiles().isEmpty()) {
//            log.info("📤 Bắt đầu upload {} file media...", req.getFiles().size());
//            uploadedMedia = uploadFilesParallel(req.getFiles(), authorId);
//        } else {
//            log.info("⚠️ Không có file media nào để upload.");
//        }
//
//        // ✅ Lấy danh sách URL để lưu vào post
//        List<String> mediaUrls = uploadedMedia.stream()
//                .map(UploadMediaRes::getSecureUrl)
//                .toList();
//
//        PostEntity post = buildPost(req, authorId, authorName, mediaUrls);
//        PostEntity savedPost = postRepository.save(post);
//
//        log.info("✅ Tạo bài viết thành công: ID={}, tổng số media={}",
//                savedPost.getId(), mediaUrls.size());
        return null ;
    }

    // ------------------------------------------------------------------------

    private PostEntity buildPost(CreatePostReq req, String authorId, String authorName, List<String> mediaUrls) {
        Instant now = Instant.now();
        return PostEntity.builder()
                .authorId(authorId)
                .authorName(authorName)
                .content(req.getContent())
                .mediaUrls(mediaUrls)
                .likeUserIds(new ArrayList<>())
                .likeCount(0)
                .commentCount(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

//    private List<UploadMediaRes> uploadFilesParallel(List<MultipartFile> files, String authorId) {
//        List<CompletableFuture<UploadMediaRes>> futures = new ArrayList<>();
//
//        files.forEach(file -> {
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
//        });
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
