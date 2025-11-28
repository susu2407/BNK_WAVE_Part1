// 세션 정보를 서버에서 가져와 초기화
let sessionTimeout = 0;
let maxSessionTimeout = 0;
let timerInterval;

// 서버에서 세션 정보 가져오기
async function fetchSessionInfo() {
    try {
        console.log('fetchSessionInfo 실행됨');
        console.log('BASE_URL:', window.BASE_URL);
        console.log('요청 URL:', `${window.BASE_URL}api/session/info`);

        const response = await fetch(`${window.BASE_URL}api/session/info`);
        console.log('응답 상태:', response.status);

        if (response.ok) {
            const data = await response.json();
            console.log('받은 데이터:', data);

            sessionTimeout = data.remaining;
            maxSessionTimeout = data.maxTimeout;
            return true;
        } else if (response.status === 401) {
            console.error('인증 실패');
            handleSessionExpired();
            return false;
        } else {
            console.error('예상치 못한 응답:', response.status);
            return false;
        }
    } catch (error) {
        console.error('세션 정보 조회 오류:', error);
        return false;
    }
}

// 타이머 시작
function startSessionTimer() {
    console.log('타이머 시작');

    // 기존 타이머가 있으면 정리
    if (timerInterval) {
        clearInterval(timerInterval);
    }

    // 1초마다 업데이트
    timerInterval = setInterval(() => {
        sessionTimeout--;
        updateTimerDisplay();

        if (sessionTimeout <= 0) {
            clearInterval(timerInterval);
            handleSessionExpired();
            return;
        }

        if (sessionTimeout === 5 * 60) {
            showWarning('세션이 5분 후 만료됩니다.');
        }

        if (sessionTimeout === 60) {
            showWarning('세션이 1분 후 만료됩니다. 로그인을 연장해주세요.');
        }
    }, 1000);
}

// 타이머 표시 업데이트
function updateTimerDisplay() {
    const minutes = Math.floor(sessionTimeout / 60);
    const seconds = sessionTimeout % 60;
    const display = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;

    const timerElement = document.getElementById('sessionTimer');
    if (timerElement) {
        timerElement.textContent = display;

        if (sessionTimeout <= 5 * 60) {
            timerElement.style.color = 'red';
            timerElement.style.fontWeight = 'bold';
        } else {
            timerElement.style.color = '';
            timerElement.style.fontWeight = 'normal';
        }
    } else {
        console.warn('sessionTimer 엘리먼트를 찾을 수 없음');
    }
}

// 세션 연장 요청
async function extendSession() {
    try {
        console.log('세션 연장 요청');

        const response = await fetch(`${window.BASE_URL}api/session/extend`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            console.log('연장 성공:', data);

            sessionTimeout = data.remaining || data.timeout;
            maxSessionTimeout = data.timeout;
            updateTimerDisplay();

            const minutes = Math.floor(sessionTimeout / 60);
            showSuccess(`로그인 시간이 ${minutes}분으로 연장되었습니다.`);
        } else {
            console.error('연장 실패:', response.status);
            showError('세션 연장에 실패했습니다.');
        }
    } catch (error) {
        console.error('세션 연장 오류:', error);
        showError('세션 연장 중 오류가 발생했습니다.');
    }
}

// 세션 만료 처리
function handleSessionExpired() {
    alert('세션이 만료되었습니다. 다시 로그인해주세요.');
    window.location.href = `${window.BASE_URL}/?session=expired`;
}

// 알림 표시 함수들
function showWarning(message) {
    if (confirm(message + '\n\n지금 연장하시겠습니까?')) {
        extendSession();
    }
}

function showSuccess(message) {
    alert(message);
}

function showError(message) {
    alert(message);
}

// 서버와 주기적 동기화 (1분마다)
async function syncWithServer() {
    console.log('서버 동기화 시작');
    const success = await fetchSessionInfo();
    if (success) {
        updateTimerDisplay();
        console.log('동기화 완료');
    }
}

// 초기화 및 시작
async function initSessionTimer() {
    console.log('initSessionTimer 호출됨');

    // 로그인 여부 확인
    const sessionBar = document.querySelector('.myp-session-bar');
    if (!sessionBar) {
        console.log('비로그인 상태 - 타이머 비활성화');
        return;
    }

    console.log('세션바 찾음 - 로그인 상태');

    // 서버에서 초기 세션 정보 가져오기
    const success = await fetchSessionInfo();

    if (success) {
        console.log('세션 정보 가져오기 성공');
        updateTimerDisplay();
        startSessionTimer();

        // 1분마다 서버와 동기화
        setInterval(syncWithServer, 60 * 1000);
        console.log('1분마다 동기화 설정 완료');
    } else {
        console.error('세션 정보 가져오기 실패');
    }
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOMContentLoaded 이벤트 발생');
    console.log('window.BASE_URL:', window.BASE_URL);

    // 연장 버튼 이벤트
    const btnExtend = document.getElementById('btnExtend');
    if (btnExtend) {
        console.log('연장 버튼 찾음');
        btnExtend.addEventListener('click', extendSession);
    } else {
        console.warn('연장 버튼을 찾을 수 없음');
    }

    // 타이머 초기화 및 시작
    initSessionTimer();
});