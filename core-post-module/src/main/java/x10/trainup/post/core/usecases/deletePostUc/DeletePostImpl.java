package x10.trainup.post.core.usecases.deletePostUc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import x10.trainup.commons.domain.entities.PostEntity;
import x10.trainup.media.core.usecases.ICoreMediaService;
import x10.trainup.media.core.usecases.deleteMedia.DeleteMediaReq;
import x10.trainup.post.core.repositories.IPostRepository;

@Service
@RequiredArgsConstructor
public class DeletePostImpl implements IDeletePostUc {

    private final ICoreMediaService coreMediaService;
    private final IPostRepository postRepository;

    @Override
    public void delete(String postId, String authorId) {
        // 1️⃣ Tìm bài viết theo ID
        PostEntity post = postRepository.findById(postId);
        if (post == null) {
            System.out.println("Bài viết với ID " + postId + " không tồn tại.");
        }

        // 2️⃣ Kiểm tra quyền xoá
        if (!post.getAuthorId().equals(authorId)) {
            System.out.println(" Người dùng với ID " + authorId + " không có quyền xoá bài viết này.");
        }

//        if (post.getMediaUrls() != null && !post.getMediaUrls().isEmpty()) {
//            for (String publicId : post.getMediaUrls()) {
//                try {
//                    DeleteMediaReq req = DeleteMediaReq.builder()
//                            .publicId(publicId)
//                            .build();
//                    coreMediaService.deleteMedia(req);
//                } catch (Exception e) {
//                    System.err.println("Không thể xoá media: " + publicId + " - " + e.getMessage());
//                }
//            }
//        }

        postRepository.deleteById(postId);
    }
}
