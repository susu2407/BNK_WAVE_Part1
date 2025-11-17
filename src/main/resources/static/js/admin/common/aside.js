/*
    날짜 : 2025-11-17
    내용 : admin aside js
*/



document.addEventListener('DOMContentLoaded', function () {

    const body = document.body;
    const aside = document.querySelector('aside');
    const asideSwitch = document.querySelectorAll('aside-switch input[type="checkbox"]');
    const munuTitles = document.querySelectorAll('menu-title')

    // 스위치 상태를 확인하여 aside 초기 상태를 설정
    if(asideSwitch && asideSwitch.checked) {
        body.classList.add('sb-expanded');
    } else {
        body.classList.remove('sb-expanded');
    }

    // ---- 1. 사이드바 확장/축소 및 고정 기능 ----

    // 1-1. 스위치 (영구 고정) 로직
    if (asideSwitch) {
        asideSwitch.addEventListener('change', function () {
            if (this.checked) {
                body.classList.add('sb-expanded');
                aside.classList.add('sb-fixed');    // 마우스 오버 방해용
            } else {
                body.classList.remove('sb-expanded');
                aside.classList.remove('sb-fixed');
            }
        });
    }

    // 1-2. 마우스 오버 (일시적 확장) 로직
    if(aside) {
        aside.addEventListener('mouseenter', function () {
            if(!aside.classList.contains('sb-fixed')) {
                body.classList.add('sb-expanded');
            }
        });

        aside.addEventListener('mouseleave', function () {
            if (!aside.classList.contains('sb-fixed')) {
                body.classList.remove('sb-expanded');
            }
        });
    }

    // ---- 2. 서브메뉴 드롭다운 토글 기능 ----

    munuTitles.forEach(function (menuTitle) {
        menuTitle.addEventListener('click', function (e) {
            e.preventDefault();
            const submenu = this.nextElementSibling;

            // 다른 메뉴가 열려 있다면 닫음 (Accordion 기능)
            menuTitle.forEach(otherTitle => {
                if (otherTitle !== this) {
                    otherTitle.classList.remove('active-menu');
                    const otherSubmenu = otherTitle.nextElementSibling;
                    if(otherSubmenu && otherSubmenu.classList.contains('submenu')) {
                        otherSubmenu.style.maxHeight = null;
                    }
                }
            });

            // 현재 메뉴 토글
            this.classList.toggle('active-menu');
            if (submenu && submenu.classList.contains('submenu')) {
                if (submenu.style.maxHeight) {
                    // 열려 있으면 닫기
                    submenu.style.maxHeight = null;
                } else {
                    // 닫혀 있으면 열기
                    submenu.style.maxHeight = submenu.scrollHeight + 'px';
                }
            }
        });
    });

    // --- 3. 2차 메뉴 클릭 시 동작 ---
    document.querySelectorAll('.submenu a').forEach(function(submenuLink) {
        submenuLink.addEventListener('click', function(e) {
            // 페이지 이동은 기본적으로 허용합니다.

            // 고정 상태가 아니면 페이지 이동 후 사이드바를 축소합니다.
            if (!aside.classList.contains('sb-fixed')) {
                // 축소 상태로 이동 (클래스 제거)
                body.classList.remove('sb-expanded');
            }
            // 만약 고정 상태라면 (sb-fixed가 있다면), sb-expanded가 유지됩니다.

            // 실제로 페이지가 이동될 것이므로, 이 클래스 제거는 다음 페이지 로드 시 적용됩니다.
            // 만약 SPA(Single Page Application)처럼 동작한다면, 여기에 실제 축소 로직을 넣어야 하지만,
            // 현재는 페이지 이동이 발생한다고 가정하고 클래스만 제거합니다.
        });
    });



});