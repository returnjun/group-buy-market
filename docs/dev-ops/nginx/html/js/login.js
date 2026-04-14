document.addEventListener('DOMContentLoaded', () => {

    document.getElementById('btnLogin').addEventListener('click', () => {
        const user = document.getElementById('username').value.trim();
        const pass = document.getElementById('password').value.trim();

        if (!user || !pass) {
            alert('请输入完整的用户名和密码！');
            return;
        }

        // --- Cookie 设置逻辑 ---
        // 1. 计算 2 天后的时间
        const d = new Date();
        d.setTime(d.getTime() + (2 * 24 * 60 * 60 * 1000));
        const expires = "expires=" + d.toUTCString();

        // 2. 将状态写入 Cookie，使用 encodeURIComponent 防止中文乱码，path=/ 保证全站有效
        document.cookie = "isLoggedIn=true; " + expires + "; path=/";
        document.cookie = "username=" + encodeURIComponent(user) + "; " + expires + "; path=/";

        // 登录成功后跳转回首页
        window.location.href = 'index.html';
    });

    // 返回按钮逻辑
    document.getElementById('btnBack').addEventListener('click', () => {
        // 对于仅用于跨页面跳转传值的临时变量（如待支付金额），
        // 推荐净使用 sessionStorage，它关闭网页就消失，比 Cookie 更干。
        sessionStorage.removeItem('pendingPayAmount');
        window.location.href = 'index.html';
    });
});