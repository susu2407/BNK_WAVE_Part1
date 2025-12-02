/*
*   내용 : 관리자 메인의 통계
*/

// 숫자 입력할 때에, 3자리마다 쉽표 생성
const formatNumber = (num) => {
    if (num === null || num === undefined) {
        return '0';
    }
    return  num.toLocaleString();
}

// --- 1. 핵심 지표 ------------------------------------------------------
const loadCurrentStats = async () => {
    const apiUrl = '/wave/api/dashboard/current';    // 컨트롤러의 @RequestMapping 경로

    try {
        // API 호출 (GET 요청)
        const response = await fetch(apiUrl);
        // HTTP 상태 코드가 200 (OK)이 아니면 예외 발생
        if (!response.ok) {
            throw new Error(`HTTP 오류! 상태 : ${response.status}`);
        }

        // 응답을 JSON 객체로 변환
        const data = await response.json();

        // HTML 요소에 데이터 바인딩 (ID를 사용)
        document.getElementById('new-products-value').textContent = formatNumber(data.currentNewProducts);
        document.getElementById('approval-requests-value').textContent = formatNumber(data.currentApprovalRequests);
        document.getElementById('issue-completed-value').textContent = formatNumber(data.currentIssueCompleted);
        document.getElementById('selling-products-value').textContent = formatNumber(data.totalSellingProducts);

    } catch (e) {
        console.error('핵심 지표 데이터 로드 실패:', e);
        document.getElementById('current-stats-container')
            .innerHTML = '<p style="color: red;">데이터를 불러오는 데 실패했습니다.</p>';
    }
};


// --- 2. Stacked Column Chart (월별 발급 추이) ------------------------------------------------
const loadStackedChart = async () => {

    const apiUrl = '/wave/api/dashboard/chart/stacked-column';

    let chartData = null;

    try {
        const  response = await fetch(apiUrl);

        if (!response.ok) {
            throw new Error(`HTTP 오류! 상태: ${response.status}`);
        }

        chartData = await response.json();

        console.log("실제 API 응답 구조 (할당 후):", chartData);

        const colors = {
            '모바일': 'rgba(200, 92, 92, 0.8)',
            '웹': 'rgba(146, 180, 242, 0.8)',
            '오프라인': 'rgba(255, 210, 157, 0.8)'
        };

        chartData.datasets.forEach(dataset => {
            if (colors[dataset.label]) {
                // 'backgroundColor' 속성에 정의된 색상을 할당합니다.
                dataset.backgroundColor = colors[dataset.label];
                // 필요하다면 테두리 색상도 함께 설정할 수 있습니다.
                dataset.borderColor = colors[dataset.label].replace('0.8', '1'); // 불투명하게
                dataset.borderWidth = 1;
            }
        });

        // 데이터가 비어 있는지 확인 (Service에서 emptyList()를 반환한 경우)
        if (chartData.labels.length === 0) {
            document.getElementById('stackedChart')
                .innerHTML = '<p>조회된 월별 발급 추이 데이터가 없습니다.</p>';
            return;
        }

        // Chart.js 설정 및 그리기
        const context = document.getElementById('stackedChart').getContext('2d');

        new Chart(context, {
           type: 'bar',
           data: {
                labels: chartData.labels,
                datasets : chartData.datasets
           },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                   x: {
                       stacked: true,   // x축을 쌓아서 표시 (Stacked Column)
                   },
                    y: {
                       stacked: true,   // y축을 쌓아서 표시 (Stacked Column)
                        beginAtZero: true
                    }
                },
                plugins: {
                    legend: {
                        position: 'right', // 범례를 오른쪽에 표시
                        labels: {
                            padding: 20
                        }
                    }
                }
            }
        });



    } catch (e) {
        console.error('Stacked Chart 데이터 로드 실패:', e);
        document.getElementById('stackedChart').parentElement
            .innerHTML = '<p style="color: red;">차트 데이터를 불러오는 데 오류가 발생했습니다.</p>';
    }

}


// --- 3. 도넛 차트 (상품별 발급 추이) ------------------------------------------------
// Chart 인스턴스를 저장할 변수 (월 이동 시 차트를 업데이트하기 위함)
let doughnutChartInstance = null;

const loadDoughnutChart = async (month = null, direction = null) => {

    // 1. API URL 구성
    let apiUrl = '/wave/api/dashboard/chart/doughnut';
    const params = new URLSearchParams();

    if (month) {
        params.append('month', month);
    }
    if (direction) {
        params.append('direction', direction);
    }

    if (params.toString()) {
        apiUrl += '?' + params.toString();
    }

    try {
        const response = await fetch(apiUrl);

        if (!response.ok) {
            if (direction) {
                const message = direction === 'prev' ? "이전 월 데이터가 없습니다." : "다음 월 데이터가 없습니다.";
                alert(message);
                return; // 함수 종료, 차트 업데이트 방지
            }
            throw new Error(`HTTP 오류! 상태: ${response.status}`);
        }

        // DoughnutChartDTO 구조를 가진 JSON 데이터
        const chartData = await response.json();

        // 데이터가 비어 있는지 확인
        if (!chartData || chartData.data.every(d => d === 0)) {
            document.getElementById('doughnutChart').innerHTML =
                '<p>조회된 상품별 발급 추이 데이터가 없습니다.</p>';
            return;
        }

        // 현재 월 표시 업데이트
        document.getElementById('currentDoughnutMonth').textContent = chartData.currentMonth;

        // 버튼 활성화/비활성화 로직
        const prevBtn = document.getElementById('prevMonthBtn');
        const nextBtn = document.getElementById('nextMonthBtn');

        // DTO의 hasPreviousMonth/hasNextMonth 값에 따라 disabled 속성 설정
        prevBtn.disabled = !chartData.hasPreviousMonth;
        nextBtn.disabled = !chartData.hasNextMonth;

        // 2. Chart.js 설정
        const context = document.getElementById('doughnutChart').getContext('2d');

        const backgroundColors = [
            'rgb(199,93,68)',    // 기업체크
            'rgb(192,40,0)',     // 기업신용
            'rgb(240,199,202)',  // 개인체크
            'rgb(234,200,192)',  // 개인신용
            'rgb(215,146,131)'   // 프리미엄
        ];

        const chartConfig = {
            type: 'doughnut',
            data: {
                labels: chartData.labels, // 상품 이름
                datasets: [{
                    data: chartData.data, // 각 상품별 발급 건수
                    backgroundColor: backgroundColors,
                    hoverOffset: 10
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right', // 범례를 오른쪽에 표시
                        labels: {
                            padding: 20
                        }
                    }
                }
            }
        };

        // 3. 차트 생성 또는 업데이트
        if (doughnutChartInstance) {
            // 차트 인스턴스가 있으면 데이터만 업데이트하고 다시 그리기
            doughnutChartInstance.data.labels = chartData.labels;
            doughnutChartInstance.data.datasets[0].data = chartData.data;
            doughnutChartInstance.update();
        } else {
            // 차트 인스턴스가 없으면 새로 생성
            doughnutChartInstance = new Chart(context, chartConfig);
        }

    } catch (e) {
        console.error('Doughnut Chart 데이터 로드 실패:', e);
        document.getElementById('doughnutChart').parentElement
            .innerHTML = '<p style="color: red;">도넛 차트 데이터를 불러오는 데 오류가 발생했습니다.</p>';
    }
};


// --- 4. 이벤트 리스너 등록 (월 이동 기능) ------------------------------------------------

document.addEventListener('DOMContentLoaded', () => {
    // ... (기존 loadCurrentStats 및 loadStackedChart 호출 유지) ...

    // 🚨 Doughnut Chart 초기 로드 (최신 월)
    loadDoughnutChart();

    const prevBtn = document.getElementById('prevMonthBtn');
    const nextBtn = document.getElementById('nextMonthBtn');

    // 이전 월 버튼 클릭 이벤트
    prevBtn.addEventListener('click', () => {
        const currentMonth = document.getElementById('currentDoughnutMonth').textContent;
        // 현재 표시 월과 'prev' 방향을 인자로 넘겨 호출
        loadDoughnutChart(currentMonth, 'prev');
    });

    // 다음 월 버튼 클릭 이벤트
    nextBtn.addEventListener('click', () => {
        const currentMonth = document.getElementById('currentDoughnutMonth').textContent;
        // 현재 표시 월과 'next' 방향을 인자로 넘겨 호출
        loadDoughnutChart(currentMonth, 'next');
    });
});


// 페이지 로드 후 함수 실행 목록에 추가
document.addEventListener('DOMContentLoaded', () => {
    loadCurrentStats(); // 핵심 지표
    loadStackedChart(); // Stacked Chart 추가
});




