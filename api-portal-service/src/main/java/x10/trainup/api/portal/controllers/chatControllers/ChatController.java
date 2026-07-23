package x10.trainup.api.portal.controllers.chatControllers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import x10.trainup.mailbox.infra.datasources.mongodb.ChatMessageDocument;
import x10.trainup.mailbox.infra.datasources.mongodb.ConversationDocument;
import x10.trainup.mailbox.infra.services.ChatService;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendMessageRequest {
        private String userId;
        private String userName;
        private String userEmail;
        private String userAvatar;
        private String senderType; // "user", "admin"
        private String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignConversationRequest {
        private String adminId;
        private String adminName;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessageDocument> sendMessage(@RequestBody SendMessageRequest request) {
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (request.getText() == null || request.getText().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        String senderType = request.getSenderType() != null ? request.getSenderType() : "user";
        ChatMessageDocument msg = chatService.sendMessage(
                request.getUserId(),
                request.getUserName(),
                request.getUserEmail(),
                request.getUserAvatar(),
                senderType,
                request.getText()
        );
        return ResponseEntity.ok(msg);
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDocument>> getConversations(
            @RequestParam(value = "status", required = false) String status
    ) {
        List<ConversationDocument> list = chatService.getConversations(status);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/conversations/user/{userId}")
    public ResponseEntity<ConversationDocument> getUserConversation(@PathVariable("userId") String userId) {
        ConversationDocument conv = chatService.getUserConversation(userId);
        if (conv == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(conv);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<ChatMessageDocument>> getMessages(
            @PathVariable("conversationId") String conversationId
    ) {
        List<ChatMessageDocument> messages = chatService.getMessages(conversationId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/conversations/{conversationId}/assign")
    public ResponseEntity<ConversationDocument> assignConversation(
            @PathVariable("conversationId") String conversationId,
            @RequestBody AssignConversationRequest req
    ) {
        String adminId = req != null && req.getAdminId() != null ? req.getAdminId() : "admin_1";
        String adminName = req != null && req.getAdminName() != null ? req.getAdminName() : "Chuyên viên Hỗ trợ";
        ConversationDocument conv = chatService.assignConversation(conversationId, adminId, adminName);
        if (conv == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(conv);
    }

    @PostMapping("/conversations/{conversationId}/close")
    public ResponseEntity<ConversationDocument> closeConversation(
            @PathVariable("conversationId") String conversationId
    ) {
        ConversationDocument conv = chatService.closeConversation(conversationId);
        if (conv == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(conv);
    }
}
