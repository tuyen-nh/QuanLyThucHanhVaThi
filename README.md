Hệ thống Quản lý Phòng máy & Phát hiện Mã độc tích hợp AI
Lab Management System with AI-powered Malware Detection

1 Tổng quan dự án (Overview)
Dự án này là một giải pháp toàn diện nhằm quản lý các hoạt động trong phòng máy thực hành và phòng thi, đồng thời tích hợp lớp bảo mật 
thông minh sử dụng trí tuệ nhân tạo để phát hiện các tệp tin thực thi (PE) có dấu hiệu nguy hiểm.

2 Cấu trúc hệ thống (System Architecture)
Dự án được chia thành 4 module chính:
QuanLyPhongMay (Backend): Xây dựng bằng Java Spring Boot, quản lý cơ sở dữ liệu, điều phối lệnh tới các máy trạm và theo dõi trạng thái hệ thống.
QuanLyPhongThi (Frontend): Giao diện quản trị viên phát triển trên nền tảng React, hỗ trợ giám sát thời gian thực.
agent (Client side): Ứng dụng chạy ngầm trên máy trạm để thu thập thông tin và thực hiện quét mã độc tại chỗ.
ModelAiPredict (AI Engine): Trái tim của hệ thống bảo mật, chứa mô hình Machine Learning.
3 Tính năng AI (AI Features)
Đây là phần trọng tâm ứng dụng công nghệ AI để bảo vệ hệ thống:
Mô hình: Sử dụng thuật toán Random Forest được huấn luyện để phân loại file mã độc.
Trích xuất đặc trưng: Hệ thống tự động trích xuất 12 đặc trưng kỹ thuật từ các tệp tin PE (Portable Executable).
Triển khai: Mô hình được xuất dưới định dạng ONNX, cho phép module agent (Java) thực hiện dự đoán với tốc độ cao và tiêu tốn ít tài nguyên.
Giám sát: Tự động gửi cảnh báo về Dashboard khi phát hiện tệp tin nghi vấn thông qua FileWatcherService.
4 Công nghệ sử dụng
Languages: Java, Python, JavaScript.
Frameworks: Spring Boot, React, Vite.
AI/ML: Scikit-learn, ONNX Runtime, Pandas.
Security: JWT (JSON Web Token), API Key Authentication.
