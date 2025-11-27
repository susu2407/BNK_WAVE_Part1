package kr.co.wave.controller.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping ("/api/session")
public class SessionController {

    @GetMapping("/info")
    public ResponseEntity<?> getSessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            // 세션의 최대 비활성 시간 (초 단위)
            int maxInactiveInterval = session.getMaxInactiveInterval();

            // 마지막 접근 시간
            long lastAccessedTime = session.getLastAccessedTime();

            // 현재 시간
            long currentTime = System.currentTimeMillis();

            // 남은 시간 계산 (초 단위)
            long elapsedTime = (currentTime - lastAccessedTime) / 1000;
            long remainingTime = maxInactiveInterval - elapsedTime;

            return ResponseEntity.ok(Map.of(
                    "maxTimeout", maxInactiveInterval,      // 전체 타임아웃 시간
                    "remaining", Math.max(0, remainingTime), // 남은 시간
                    "lastAccessed", lastAccessedTime
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "세션이 없습니다."));
    }

    @PostMapping("/extend")
    public ResponseEntity<?> extendSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            // 현재 설정된 타임아웃 값을 그대로 사용하여 세션 갱신
            int maxInactiveInterval = session.getMaxInactiveInterval();
            session.setMaxInactiveInterval(maxInactiveInterval);

            return ResponseEntity.ok(Map.of(
                    "message", "세션이 연장되었습니다.",
                    "timeout", maxInactiveInterval,
                    "remaining", maxInactiveInterval
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "유효한 세션이 없습니다."));
    }
}