document.addEventListener("DOMContentLoaded", function () {
    const annualFeeList = document.getElementById("annualFeeList");
    const benefitList = document.getElementById("benefitList");

    function cloneAndReset(container) {
        const clone = container.cloneNode(true);
        clone.querySelectorAll("input").forEach(input => {
            input.value = "";
            input.removeAttribute("id");
        });
        clone.querySelectorAll("select").forEach(select => {
            select.selectedIndex = 0;
        });
        return clone;
    }

    annualFeeList.addEventListener("click", function (e) {
        if (e.target.classList.contains("annualFee-add-btn")) {
            const current = annualFeeList.querySelector(".annualFeeContainer");
            const clone = cloneAndReset(current);
            annualFeeList.appendChild(clone);
        }

        if (e.target.classList.contains("annualFee-remove-btn")) {
            const allContainers = annualFeeList.querySelectorAll(".annualFeeContainer");
            if (allContainers.length > 1) {
                e.target.closest(".annualFeeContainer").remove();
            } else {
                alert("더 이상 삭제할 수 없습니다.");
            }
        }
    });

    benefitList.addEventListener("click", function (e) {
        if (e.target.classList.contains("benefit-add-btn")) {
            const current = benefitList.querySelector(".benefitContainer");
            const clone = cloneAndReset(current);
            benefitList.appendChild(clone);
        }

        if (e.target.classList.contains("benefit-remove-btn")) {
            const allContainers = benefitList.querySelectorAll(".benefitContainer");
            if (allContainers.length > 1) {
                e.target.closest(".benefitContainer").remove();
            } else {
                alert("최소 1개의 혜택은 있어야 합니다.");
            }
        }
    });

    // 날짜 : 2025-11-21
    // 이름 : 이수연
    // 내용 : 숫자 입력할 때에, 3자리마다 쉽표 생성
    const priceInput = document.getElementsByClassName("product-num-input");

    Array.from(priceInput).forEach(function (input) {
        input.addEventListener("input", function () {
            // 숫자만 남기기
            let value = this.value.replace(/[^0-9]/g, "");

            // 3자리 콤마 적용
            this.value = value.replace(/\B(?=(\d{3})+(?!\d))/g, ",");

            // 벡엔드로 넘길 때(콤마 제거)
            const rawNumber = this.value.replace(/,/g, "");
        });
    });



});