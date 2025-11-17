/*
    날짜 : 2025-11-17
    내용 : admin aside js
*/



document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.menu-title').forEach(function (menuTitle) {
        menuTitle.addEventListener('click', function (e) {
            e.preventDefault();
            const submenu = this.nextElementSibling;
            submenu.style.display = submenu.style.display === 'none' ? 'block' : 'none';
        });
    });
});