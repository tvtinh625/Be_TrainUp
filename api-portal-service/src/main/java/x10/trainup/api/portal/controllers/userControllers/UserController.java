package x10.trainup.api.portal.controllers.userControllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.security.core.principal.UserPrincipal;
import x10.trainup.user.core.usecases.ICoreUserSerivce;
import x10.trainup.user.core.usecases.changePasswordUc.ChangePasswordReq;
import x10.trainup.user.core.usecases.createUser.CreateUserReq;
import x10.trainup.user.core.usecases.updateUser.UpdateUserReq;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api/users")
@AllArgsConstructor
public class UserController {

    private final ICoreUserSerivce userService;
    private final HttpServletRequest request;

    private String path() {
        return request.getRequestURI();
    }

    private String traceId() {
        var id = (String) request.getAttribute("traceId");
        if (id == null) id = MDC.get("traceId");
        return id;
    }
    // Create User
    @PostMapping
    public ResponseEntity<ApiResponse<UserEntity>> createUser(@RequestBody @Valid CreateUserReq req) {
        var created = userService.createUser(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(201, "SUCCESS", "User created", created, path(), traceId()));
    }
    @PutMapping()
    public ResponseEntity<ApiResponse<UserEntity>> updateUser(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody UpdateUserReq req
    ) {
        // userId được lấy từ token (UserPrincipal)
        var updatedUser = userService.updateUser(user.getId(),req);

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "USR.UPDATED",
                        "Cập nhật thông tin người dùng thành công",
                        updatedUser,
                        path(),
                        traceId()
                )
        );
    }


    // Get profile (current user)
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserEntity>> getProfile(
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest request) {

        String userId = user.getId();

        return userService.getUserById(userId)
                .map(userEntity -> ResponseEntity.ok(
                        ApiResponse.of(
                                200,
                                "USR.FOUND",
                                "Lấy thông tin người dùng thành công",
                                userEntity,
                                request.getRequestURI(),
                                "trace-" + System.currentTimeMillis()
                        )
                ))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.of(
                                404,
                                "USR.NOT_FOUND",
                                "Không tìm thấy người dùng",
                                null,
                                request.getRequestURI(),
                                "trace-" + System.currentTimeMillis()
                        )));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid ChangePasswordReq req
    ) {
        userService.changePassword(user.getId(), req);
        return ResponseEntity.ok(
                ApiResponse.of(200, "USR.PASSWORD_CHANGED", "Đổi mật khẩu thành công", null, path(), traceId())
        );
    }

    // Get all users
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserEntity>>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        return ResponseEntity.ok(
                ApiResponse.of(200, "SUCCESS", "Users retrieved successfully", users, path(), traceId())
        );
    }


    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
                ApiResponse.of(200, "SUCCESS", "User deleted successfully", null, path(), traceId())
        );
    }
}
