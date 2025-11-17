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

});