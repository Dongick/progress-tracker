package mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mission.dto.authentication.*;
import mission.exception.ErrorResponse;
import mission.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "인증글 작성",
            description = "해당 미션의 오늘의 인증글 작성"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "인증글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류 \t\n 2. VALIDATION_FAILED : 유효성 검사 실패 \t\n " +
                    "3. DUPLICATE_AUTHENTICATION : 일일 인증글 중복 \t\n 4. MISSION_NOT_STARTED : 시작되지 않은 미션 \t\n 5. MISSION_ALREADY_COMPLETED : 이미 끝난 미션",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403",
                    description = "1. EXCEEDED_AUTHENTICATION_LIMIT : 인증글 작성 횟수 초과",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "1. MISSION_NOT_FOUND : 해당 미션을 찾을 수 없음 \t\n 2. PARTICIPANT_NOT_FOUND : 해당 미션의 참여자가 아님",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<String> createAuthentication(
            @Valid @RequestPart(value="textData") AuthenticationCreateRequest authenticationCreateRequest,
            @RequestPart(value = "photoData", required = false) MultipartFile photoData,
            @PathVariable String id) throws IOException {

        authenticationService.createAuthentication(authenticationCreateRequest, photoData, id);

        return ResponseEntity.status(HttpStatus.CREATED).body("good");
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "인증글 수정",
            description = "해당 미션의 오늘의 인증글 수정"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류 \t\n 2. VALIDATION_FAILED : 유효성 검사 실패 \t\n " +
                    "3. MISSION_NOT_STARTED : 시작되지 않은 미션 \t\n 4. MISSION_ALREADY_COMPLETED : 이미 끝난 미션",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "1. MISSION_NOT_FOUND : 해당 미션을 찾을 수 없음 \t\n 2. PARTICIPANT_NOT_FOUND : 해당 미션의 참여자가 아님 \t\n 2. AUTHENTICATION_NOT_FOUND : 당일 인증한 인증글이 존재하지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<String> updateAuthentication(
            @Valid @RequestPart(value="textData") AuthenticationUpdateRequest authenticationUpdateRequest,
            @RequestPart(value = "photoData", required = false) MultipartFile photoData,
            @PathVariable String id) throws IOException {
        authenticationService.updateAuthentication(authenticationUpdateRequest, photoData, id);

        return ResponseEntity.ok("good");
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "인증글 삭제",
            description = "해당 미션의 오늘의 인증글 삭제"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "206", description = "인증글 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류 \t\n 2. MISSION_NOT_STARTED : 시작되지 않은 미션 \t\n 3. MISSION_ALREADY_COMPLETED : 이미 끝난 미션",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "1. MISSION_NOT_FOUND : 해당 미션을 찾을 수 없음 \t\n 2. PARTICIPANT_NOT_FOUND : 해당 미션의 참여자가 아님 \t\n 2. AUTHENTICATION_NOT_FOUND : 당일 인증한 인증글이 존재하지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<String> deleteAuthentication(@PathVariable String id) throws IOException {
        authenticationService.deleteAuthentication(id);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body("good");
    }

    @GetMapping("/{id}/{num}")
    @Operation(
            summary = "인증글 보기",
            description = "해당 미션의 오늘의 인증글 보기"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증글 보기 성공",
                    content = @Content(
                            schema = @Schema(implementation = AuthenticationListResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"authenticationData\": [{\"date\": \"2024-03-09\", \"photoData\": \"string\", \"textData\": \"string\", \"userEmail\": \"string\", \"username\": \"string\"} ]}"
                            )
                    )),
            @ApiResponse(responseCode = "400", description = "1. ACCESS_TOKEN_INVALID : access token 값 오류 \t\n 2. MISSION_NOT_STARTED : 시작되지 않은 미션",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "1. ACCESS_TOKEN_EXPIRED : access token 만료 \t\n 2. UNAUTHORIZED : 토큰 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "1. MISSION_NOT_FOUND : 해당 미션을 찾을 수 없음 \t\n 2. PARTICIPANT_NOT_FOUND : 해당 미션의 참여자가 아님",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<AuthenticationListResponse> authenticationList(@PathVariable String id, @PathVariable int num) {
        AuthenticationListResponse result = authenticationService.authenticationList(id, num);
        return ResponseEntity.ok(result);
    }
}
