document.addEventListener('DOMContentLoaded', () => {
    // ==========================================
    // 1. 无缝无限轮播图逻辑 (保持不变)
    // ==========================================
    const slidesContainer = document.getElementById('slides');
    const slideElements = document.querySelectorAll('.slide');
    const totalRealSlides = slideElements.length;

    const firstClone = slideElements[0].cloneNode(true);
    const lastClone = slideElements[totalRealSlides - 1].cloneNode(true);

    slidesContainer.appendChild(firstClone);
    slidesContainer.insertBefore(lastClone, slideElements[0]);

    let currentSlide = 1;
    let isTransitioning = false;

    slidesContainer.style.transform = `translateX(-${currentSlide * 100}%)`;

    function changeSlide(direction) {
        if (isTransitioning) return;
        isTransitioning = true;
        currentSlide += direction;

        slidesContainer.style.transition = 'transform 0.4s ease-in-out';
        slidesContainer.style.transform = `translateX(-${currentSlide * 100}%)`;
    }

    slidesContainer.addEventListener('transitionend', () => {
        isTransitioning = false;
        if (currentSlide === 0) {
            slidesContainer.style.transition = 'none';
            currentSlide = totalRealSlides;
            slidesContainer.style.transform = `translateX(-${currentSlide * 100}%)`;
        } else if (currentSlide === totalRealSlides + 1) {
            slidesContainer.style.transition = 'none';
            currentSlide = 1;
            slidesContainer.style.transform = `translateX(-${currentSlide * 100}%)`;
        }
    });

    document.getElementById('btnPrev').addEventListener('click', () => changeSlide(-1));
    document.getElementById('btnNext').addEventListener('click', () => changeSlide(1));

    setInterval(() => changeSlide(1), 3000);

    // ==========================================
    // 2. 动态文字头像生成逻辑 (保持不变)
    // ==========================================
    function getAvatarDataURI(name) {
        if (!name) name = "U";
        const char = name.charAt(0).toUpperCase();
        const colors = ['#ff4d4f', '#fca130', '#52c41a', '#1890ff', '#722ed1', '#eb2f96'];
        const color = colors[char.charCodeAt(0) % colors.length] || colors[0];

        const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100">
            <rect width="100" height="100" fill="${color}"/>
            <text x="50%" y="50%" font-size="50" fill="white" font-weight="bold" font-family="sans-serif" text-anchor="middle" dy=".35em">${char}</text>
        </svg>`;
        return 'data:image/svg+xml;charset=UTF-8,' + encodeURIComponent(svg);
    }

    document.querySelectorAll('.dynamic-avatar').forEach(img => {
        img.src = getAvatarDataURI(img.dataset.name);
    });

    // ==========================================
    // 3. 业务状态与页面跳转逻辑 (Cookie 版本)
    // ==========================================

    // --- 新增：读取 Cookie 的辅助函数 ---
    function getCookie(name) {
        const nameEQ = name + "=";
        const ca = document.cookie.split(';');
        for(let i = 0; i < ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) === ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) === 0) {
                // 使用 decodeURIComponent 解码中文用户名
                return decodeURIComponent(c.substring(nameEQ.length, c.length));
            }
        }
        return null;
    }

    const paymentModal = document.getElementById('paymentModal');

    // 从 Cookie 初始化检查登录状态
    const isLoggedIn = getCookie('isLoggedIn') === 'true';
    const username = getCookie('username');

    if (isLoggedIn && username) {
        const topAvatarImg = document.getElementById('topAvatarImg');
        topAvatarImg.src = getAvatarDataURI(username);
        document.getElementById('userAvatar').style.display = 'block';
    }

    // 检查是否有刚登录回来待支付的金额 (使用 sessionStorage)
    const pendingAmount = sessionStorage.getItem('pendingPayAmount');
    if (pendingAmount && isLoggedIn) {
        showPaymentModal(pendingAmount);
        sessionStorage.removeItem('pendingPayAmount'); // 弹出后清除记录
    }

    // 绑定所有购买按钮
    document.querySelectorAll('.buy-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const amount = this.getAttribute('data-amount');
            if (!isLoggedIn) {
                // 没登录，记录要买的金额，跳转到登录页 (使用 sessionStorage)
                sessionStorage.setItem('pendingPayAmount', amount);
                window.location.href = 'login.html';
            } else {
                showPaymentModal(amount);
            }
        });
    });

    function showPaymentModal(amount) {
        document.getElementById('payAmountText').innerText = `支付金额：¥${amount}`;
        paymentModal.style.display = 'flex';
    }

    document.getElementById('btnCancelPay').addEventListener('click', () => {
        paymentModal.style.display = 'none';
    });

    document.getElementById('btnConfirmPay').addEventListener('click', () => {
        alert('支付成功！');
        paymentModal.style.display = 'none';
    });

    // ==========================================
    // 4. 倒计时模拟 (保持不变)
    // ==========================================
    let timeRemaining = 349;
    const timerDisplays = document.querySelectorAll('.timer-display');
    setInterval(() => {
        if(timeRemaining > 0) {
            timeRemaining--;
            let m = Math.floor(timeRemaining / 60).toString().padStart(2, '0');
            let s = (timeRemaining % 60).toString().padStart(2, '0');
            let timeStr = `00:${m}:${s}`;
            timerDisplays.forEach(el => el.innerText = timeStr);
        }
    }, 1000);
});