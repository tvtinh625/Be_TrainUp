package x10.trainup.api.cms.controller.userController;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import x10.trainup.commons.domain.entities.UserEntity;
import x10.trainup.commons.response.ApiResponse;
import x10.trainup.user.core.usecases.ICoreUserSerivce;
import x10.trainup.user.core.usecases.updateStatusUserUc.UpdateUserStatusReq;

import java.util.List;

@RestController
@RequestMapping("/api/users/")
@AllArgsConstructor
public class UserController {
    private final ICoreUserSerivce iCoreUserSerivce;



    // Creat
    @GetMapping("/all")
    public ApiResponse<List<UserEntity>> getAll(HttpServletRequest request) {
        List<UserEntity> users = iCoreUserSerivce.getAllUsers();
        return ApiResponse.of(
                HttpStatus.OK.value(),
                "USER.GET_ALL_SUCCESS",
                "Retrieved all users successfully",
                users,
                request.getRequestURI(),
                request.getHeader("X-Trace-Id")
        );
    }

    @PatchMapping("/{userId}/status")
    public ApiResponse<UserEntity> updateUserStatus(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserStatusReq req,
            HttpServletRequest request) {

        UserEntity user = iCoreUserSerivce.updateUserStatus(userId, req);

        return ApiResponse.of(
                HttpStatus.OK.value(),
                "USER.STATUS_UPDATE_SUCCESS",
                "User status updated successfully",
                user,
                request.getRequestURI(),
                request.getHeader("X-Trace-Id")
        );
    }

}
