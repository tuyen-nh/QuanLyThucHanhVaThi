export const fetchWithAuth = async (url, options = {}) => {
    // Lấy token từ localStorage
    let token = localStorage.getItem('authToken');

    // Khởi tạo headers nếu chưa có
    const headers = new Headers(options.headers || {});

    // Tự động đính kèm token vào header Authorization nếu có
    if (token) {
        headers.set('Authorization', `Bearer ${token}`);
    }

    // Khởi tạo options mới với headers đã cập nhật
    const config = {
        ...options,
        headers
    };

    try {
        // Thực hiện request gốc
        let response = await fetch(url, config);

        // Xử lý trường hợp token hết hạn (lỗi 401)
        if (response.status === 401 || response.status === 403) {
            console.warn("Token có thể đã hết hạn, đang thử làm mới token...");
            const refreshToken = localStorage.getItem('refreshToken');

            if (refreshToken) {
                try {
                    // Gọi API refresh token
                    const refreshResponse = await fetch('http://localhost:8080/apt/refresh-token', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({ refreshToken })
                    });

                    if (refreshResponse.ok) {
                        const data = await refreshResponse.json();

                        // Cập nhật token mới vào localStorage
                        const newToken = data.accessToken || data.token;
                        if (newToken) {
                            localStorage.setItem('authToken', newToken);

                            // Cập nhật refreshToken mới nếu server có trả về
                            // if (data.refreshToken) {
                            //     localStorage.setItem('refreshToken', data.refreshToken);
                            // }

                            // Đính kèm token mới vào header của request ban đầu
                            headers.set('Authorization', `Bearer ${newToken}`);
                            const retryConfig = { ...options, headers };

                            // THỰC HIỆN LẠI REQUEST BAN ĐẦU
                            response = await fetch(url, retryConfig);
                        } else {
                            throw new Error("Không tìm thấy token mới từ phản hồi.");
                        }
                    } else {
                        console.error("Refresh token thất bại hoặc đã hết hạn.");
                        // Xóa token và có thể chuyển hướng về trang login (tùy thuộc vào ứng dụng)
                        localStorage.removeItem('authToken');
                        localStorage.removeItem('refreshToken');
                        window.location.href = '/Login'; // Hoặc đường dẫn đăng nhập của bạn
                        throw new Error('Refresh token hết hạn.');
                    }
                } catch (refreshError) {
                    console.error("Lỗi khi gọi API refresh token:", refreshError);
                    localStorage.removeItem('authToken');
                    localStorage.removeItem('refreshToken');
                    window.location.href = '/Login';
                    throw refreshError;
                }
            } else {
                // Không có refresh token, yêu cầu đăng nhập lại
                console.warn("Không tìm thấy refresh token trong localStorage.");
                localStorage.removeItem('authToken');
                window.location.href = '/Login';
            }
        }

        // Trả về response (cho dù là response gốc nếu thành công, hoặc response sau khi retry)
        return response;

    } catch (error) {
        console.error("Lỗi fetch:", error);
        throw error;
    }
};
