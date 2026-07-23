package x10.trainup.mailbox.infra.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import x10.trainup.mailbox.infra.datasources.mongodb.ChatMessageDocument;
import x10.trainup.mailbox.infra.datasources.mongodb.ChatMessageMongoRepository;
import x10.trainup.mailbox.infra.datasources.mongodb.ConversationDocument;
import x10.trainup.mailbox.infra.datasources.mongodb.ConversationMongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationMongoRepository conversationRepository;
    private final ChatMessageMongoRepository messageRepository;

    public ChatMessageDocument sendMessage(
            String userId,
            String userName,
            String userEmail,
            String userAvatar,
            String senderType,
            String text
    ) {
        // Find existing conversation or create a new one
        Optional<ConversationDocument> existingOpt = conversationRepository.findByUserId(userId);
        ConversationDocument conv;

        if (existingOpt.isPresent()) {
            conv = existingOpt.get();
            conv.setLastMessage(text);
            conv.setLastSender(senderType);
            conv.setUpdatedAt(LocalDateTime.now());
            if ("closed".equals(conv.getStatus())) {
                conv.setStatus("pending");
            }
            if (userName != null && !userName.isBlank()) conv.setUserName(userName);
            if (userEmail != null && !userEmail.isBlank()) conv.setUserEmail(userEmail);
            if (userAvatar != null && !userAvatar.isBlank()) conv.setUserAvatar(userAvatar);
        } else {
            conv = ConversationDocument.builder()
                    .userId(userId)
                    .userName(userName != null && !userName.isBlank() ? userName : "Khách hàng")
                    .userEmail(userEmail)
                    .userAvatar(userAvatar)
                    .status("pending")
                    .lastMessage(text)
                    .lastSender(senderType)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

        conv = conversationRepository.save(conv);

        // Create Message document
        String senderName = "user".equalsIgnoreCase(senderType)
                ? (userName != null ? userName : "Khách hàng")
                : "Chuyên viên tư vấn";

        ChatMessageDocument msg = ChatMessageDocument.builder()
                .conversationId(conv.getId())
                .senderId(userId)
                .senderType(senderType)
                .senderName(senderName)
                .senderAvatar(userAvatar)
                .text(text)
                .createdAt(LocalDateTime.now())
                .build();

        return messageRepository.save(msg);
    }

    public List<ConversationDocument> getConversations(String status) {
        if (status != null && !status.isBlank() && !"all".equalsIgnoreCase(status)) {
            return conversationRepository.findByStatusOrderByUpdatedAtDesc(status);
        }
        return conversationRepository.findAllByOrderByUpdatedAtDesc();
    }

    public ConversationDocument getUserConversation(String userId) {
        return conversationRepository.findByUserId(userId)
                .orElse(null);
    }

    public List<ChatMessageDocument> getMessages(String conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    public ConversationDocument assignConversation(String conversationId, String adminId, String adminName) {
        Optional<ConversationDocument> opt = conversationRepository.findById(conversationId);
        if (opt.isPresent()) {
            ConversationDocument conv = opt.get();
            conv.setStatus("active");
            conv.setAssignedAdminId(adminId);
            conv.setAssignedAdminName(adminName);
            conv.setUpdatedAt(LocalDateTime.now());
            return conversationRepository.save(conv);
        }
        return null;
    }

    public ConversationDocument closeConversation(String conversationId) {
        Optional<ConversationDocument> opt = conversationRepository.findById(conversationId);
        if (opt.isPresent()) {
            ConversationDocument conv = opt.get();
            conv.setStatus("closed");
            conv.setUpdatedAt(LocalDateTime.now());
            return conversationRepository.save(conv);
        }
        return null;
    }
}
