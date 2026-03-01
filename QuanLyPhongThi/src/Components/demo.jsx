import React, { useEffect, useState } from 'react';
// CSS import errors resolved by removing external file references.
// The necessary styles are included below in the <style> tag.

// Lucide icon for the avatar placeholder
const UserIcon = (props) => (
    <svg {...props} xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-user">
        <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
    </svg>
);

// Dữ liệu giả lập (Mock) cho các trường vi phạm mới
// GIẢ SỬ dữ liệu API trả về có thêm 3 trường sau: 
// isViolating: boolean, violationCount: number, violations: [{appName: string, time: string}]
const MOCK_VIOLATION_DATA = [
    { macAddress: "00:1A:2B:3C:4D:5E", nameComputer: "MAY01", ipAddress: "192.168.1.1", status: "on", statusFirewall: "off", softwareStatuses: [{ softwareName: "ExamBrowser", status: "running" }], isViolating: true, violationCount: 2, violations: [{ appName: "Chrome", time: "10:30" }, { appName: "TeamViewer", time: "10:31" }] },
    { macAddress: "F0:91:F5:6A:2B:7C", nameComputer: "MAY05", ipAddress: "192.168.1.5", status: "on", statusFirewall: "on", softwareStatuses: [{ softwareName: "ExamBrowser", status: "running" }], isViolating: false, violationCount: 0, violations: [] },
    { macAddress: "00:1B:8C:7D:6E:2F", nameComputer: "MAY02", ipAddress: "192.168.1.2", status: "off", statusFirewall: "off", softwareStatuses: [], isViolating: false, violationCount: 0, violations: [] },
    { macAddress: "A4:B6:C1:DE:F2:90", nameComputer: "MAY04", ipAddress: "192.168.1.4", status: "on", statusFirewall: "off", softwareStatuses: [{ softwareName: "ExamBrowser", status: "running" }], isViolating: true, violationCount: 1, violations: [{ appName: "Zalo", time: "10:35" }] },
];

const addMockViolationData = (data) => {
    // Nếu không có dữ liệu thật (chạy môi trường dev/không có token), trả về mock data để demo
    if (!data || data.length === 0) return MOCK_VIOLATION_DATA;
    
    // Nếu có dữ liệu thật, ghép thêm trường mock vào
    return data.map(item => {
        const mock = MOCK_VIOLATION_DATA.find(m => m.macAddress === item.macAddress);
        return {
            ...item,
            isViolating: mock ? mock.isViolating : false,
            violationCount: mock ? mock.violationCount : 0,
            violations: mock ? mock.violations : [],
        };
    });
};


function AdminDashboard() {
    const [dataDash, setDataDash] = useState([]);
    const [searchText, setSearchText] = useState('');

    // Hàm tính toán số máy vi phạm (chỉ tính máy online)
    const countViolatingComputers = (data) => {
        return data.filter(computer => computer.status === 'on' && computer.isViolating).length;
    };


    useEffect(() => {
        const token = localStorage.getItem('authToken'); 
        const hasToken = !!token;

        const fetchData = () => {
            // Cần có token để gọi API bảo mật
            if (!hasToken) {
                console.error("JWT Token không tồn tại. Vui lòng đăng nhập.");
                return;
            }

            fetch('http://localhost:8080/info', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    // Bổ sung JWT Token vào Authorization Header
                    'Authorization': `Bearer ${token}` 
                }
            })
            .then(response => {
                if (!response.ok) {
                    // Xử lý lỗi 401/403 (Unauthorized/Forbidden)
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                // Thêm mock data vi phạm
                setDataDash(addMockViolationData(data)); 
                console.log(data);
            })
            .catch(error => console.error('Error fetching data:', error));
        };

        if (hasToken) {
            fetchData(); // Gọi ngay lần đầu khi component mount

            const intervalId = setInterval(fetchData, 5000); // Gọi mỗi 5 giây

            // Cleanup khi component unmount
            return () => clearInterval(intervalId);
        } else {
            // Nếu không có token, chỉ dùng mock data để demo và không gọi API lặp lại
            setDataDash(addMockViolationData([])); 
            console.log("Sử dụng Mock Data vì thiếu JWT Token.");
        }
        
        return () => {}; // Cleanup rỗng nếu không có interval

    }, []);


    // chọn phần mềm để cài đặt
    const handleSoftwareInstall = (macAddress, softwareName) => {
        if (!softwareName) return; // bỏ qua nếu chưa chọn gì

        const token = localStorage.getItem('authToken'); // Lấy Token
        if (!token) {
            // Thay thế alert() bằng console.error()
            console.error("Error: JWT Token not found for software installation command.");
            return;
        }

        const payload = {
            macAddress: macAddress,
            softwareName: softwareName
        };

        fetch('http://localhost:8080/sendCommandToAgent', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` // Bổ sung JWT Token
            },
            body: JSON.stringify(payload)
        })
            .then(response => {
                if (response.ok) {
                    // Thay thế alert() bằng console.log()
                    console.log(`Successfully sent request to install ${softwareName} on ${macAddress}`);
                } else {
                    // Thay thế alert() bằng console.error()
                    console.error("Failed to send installation request! (Check Token and Server)");
                }
            })
            .catch(error => {
                console.error('Lỗi khi gửi yêu cầu:', error);
            });
    };


    // thay đổi trạng thái tường lửa 
    const toggleFirewall = async (macAddress, currentStatus) => {
        const newStatus = currentStatus === 'on' ? 'off' : 'on';
        
        const token = localStorage.getItem('authToken'); // Lấy Token
        if (!token) {
            // Thay thế alert() bằng console.error()
            console.error("Error: JWT Token not found for firewall command.");
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/sendFirewallCommand', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`, // Bổ sung JWT Token
                },
                body: JSON.stringify({
                    macAddress: macAddress,
                    command: newStatus
                }),
            });

            if (response.ok) {
                // Cập nhật trạng thái firewall mới cho máy cụ thể
                setDataDash(prev =>
                    prev.map(computer =>
                        computer.macAddress === macAddress
                            ? { ...computer, statusFirewall: newStatus }
                            : computer
                    )
                );
            } else {
                // Thay thế alert() bằng console.error()
                console.error('Error sending firewall command! (Check Token and Server)');
            }
        } catch (error) {
            console.error('Lỗi khi gửi lệnh firewall:', error);
            // Thay thế alert() bằng console.error()
            console.error('Cannot connect to server for firewall command.');
        }
    };


    const countOnlineComputers = () => {
        return filteredData.filter(computer => computer.status === 'on').length;
    };

    const countOffineComputers = () => {
        return filteredData.filter(computer => computer.status === 'off').length;
    };
    
    // Tạo danh sách máy đã lọc dựa trên searchText
    const filteredData = dataDash.filter(item =>
        item.macAddress.toLowerCase().includes(searchText.toLowerCase()) ||
        item.nameComputer.toLowerCase().includes(searchText.toLowerCase()) ||
        item.ipAddress.toLowerCase().includes(searchText.toLowerCase())
    );

    // Xử lý hiển thị chi tiết vi phạm (Chỉ in ra console vì đã gỡ Modal)
    const handleShowViolationDetails = (computer) => {
        console.log("Chi tiết vi phạm của máy:", computer.nameComputer);
        computer.violations.forEach(v => {
            console.log(`- App: ${v.appName}, Time: ${v.time}`);
        });
        // alert(`Máy ${computer.nameComputer} có ${computer.violationCount} vi phạm. Xem chi tiết trong Console.`);
        console.log(`Violation details for ${computer.nameComputer} (${computer.violationCount} total violations) are available in the console.`);
    };


    return (
        <div className="admin-dashboard">
            {/* INLINE CSS for new components and basic structural classes */}
            <style>
                {`
                        /* FLASHING EFFECT */
                        @keyframes pulse-red {
                            0% { box-shadow: 0 0 0 0 rgba(220, 38, 38, 0.7); }
                            70% { box-shadow: 0 0 0 10px rgba(220, 38, 38, 0); }
                            100% { box-shadow: 0 0 0 0 rgba(220, 38, 38, 0); }
                        }
                        .violating-pulse {
                            animation: pulse-red 2s infinite;
                            background-color: #fef2f2; /* Light red background */
                            padding: 5px 10px;
                            border-radius: 6px;
                        }

                        /* Violation card style */
                        .violation-card-style {
                            border-left: 5px solid #dc2626;
                            background-color: #fef2f2;
                        }

                    /* Basic structural styles */
                    .admin-dashboard { padding: 20px; font-family: sans-serif; }
                    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
                    .title { font-size: 1.5em; font-weight: bold; }
                    .admin-info { display: flex; align-items: center; }
                    .admin-label { margin-right: 10px; }
                    /* Style updated to use the SVG icon */
                    .avatar { width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; background-color: #e5e7eb; color: #4b5563; }
                    .card-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; }
                    .card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                    .card h2 { font-size: 1.1em; margin-bottom: 5px; }
                    .card p { font-size: 1.8em; font-weight: bold; }
                    .computer-table table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                    .computer-table th, .computer-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #eee; }
                    .computer-table thead { background-color: #f4f4f4; }

                    /* Status and button styles */
                    .status-on { background-color: #d1fae5; color: #065f46; padding: 4px 8px; border-radius: 4px; font-size: 0.8em; font-weight: bold; }
                    .status-off { background-color: #ffe6e6; color: #991b1b; padding: 4px 8px; border-radius: 4px; font-size: 0.8em; font-weight: bold; }
                    .statusFire-on { background-color: #dc2626; color: white; padding: 4px 10px; border: none; border-radius: 4px; cursor: pointer; }
                    .statusFire-off { background-color: #3b82f6; color: white; padding: 4px 10px; border: none; border-radius: 4px; cursor: pointer; }
                    .select-install { padding: 4px; border-radius: 4px; border: 1px solid #ccc; }
                `}
            </style>
            
            <header className="header">
                <h1 className="title">Computer Management</h1>
                <div className="admin-info">
                    <span className="admin-label">Admin</span>
                    <div className="avatar">
                        <UserIcon width={24} height={24} />
                    </div>
                </div>
            </header>

            <div className="tabs">
                <div className="dashboard-section">
                    <div className="card-grid">
                        {/* Card Online */}
                        <div className="card" style={{ borderLeft: '5px solid #10b981' }}>
                            <h2>🟢 Online PCs</h2>
                            <p>{countOnlineComputers()} / {filteredData.length}</p>
                        </div>
                        
                        {/* Card Offline */}
                        <div className="card" style={{ borderLeft: '5px solid #ef4444' }}>
                            <h2>🔴 Offline PCs</h2>
                            <p>{countOffineComputers()} / {filteredData.length}</p>
                        </div>
                        
                        {/* Card CẦN XỬ LÝ (VI PHẠM) - NEW CODE */}
                        <div 
                            className={`card violation-card-style ${countViolatingComputers(dataDash) > 0 ? 'violating-pulse' : ''}`}
                            onClick={() => countViolatingComputers(dataDDash) > 0 && setSearchText('violating')} 
                            style={{ cursor: 'pointer', borderLeft: '5px solid #dc2626' }}
                        >
                            <h2 style={{ color: '#dc2626' }}>⚠️ CẦN XỬ LÝ</h2>
                            <p style={{ color: '#dc2626', fontWeight: 'bold' }}>
                                {countViolatingComputers(dataDash)} <span style={{ fontSize: '0.8em', fontWeight: 'normal', color: '#6b7280' }}>máy vi phạm</span>
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <div style={{ margin: '20px 0' }}>
                <input
                    type="text"
                    placeholder="🔍 Tìm theo tên máy, IP hoặc phần mềm..."
                    value={searchText}
                    onChange={(e) => setSearchText(e.target.value)}
                    style={{
                        padding: '8px',
                        width: '100%',
                        maxWidth: '400px',
                        borderRadius: '6px',
                        border: '1px solid #ccc',
                    }}
                />
            </div>

            <div className="computer-table">
                <table>
                    <thead>
                        <tr>
                            <th>STT</th>
                            <th>Tên máy</th>
                            <th>IP</th>
                            <th>Trạng thái</th>
                            <th>Phần mềm đang chạy</th>
                            <th style={{ backgroundColor: '#fee2e2', color: '#991b1b', fontWeight: 'bold' }}>VI PHẠM</th> {/* Cột mới */}
                            <th>Firewall</th>
                            <th>Cài phần mềm</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            filteredData.map((computer, index) => (
                                <tr key={index}>
                                    <td>{index + 1}</td>
                                    <td>{computer.nameComputer} ({computer.macAddress})</td>
                                    <td>{computer.ipAddress}</td>
                                    <td>
                                        <span className={computer.status === 'on' ? "status-on" : "status-off"}>
                                            {computer.status}
                                        </span>
                                    </td>
                                    <td>
                                        {computer.status === 'off' ? (
                                            <span className="status-off">off</span>
                                        ) : (
                                            computer.softwareStatuses
                                                .filter(software => software.status === "running")
                                                .map((software, i, arr) => (
                                                    <span key={i}>
                                                        {software.softwareName}{i < arr.length - 1 ? ", " : ""}
                                                    </span>
                                                ))
                                        )}
                                    </td>
                                    
                                    {/* CỘT VI PHẠM (MỚI) */}
                                    <td onClick={() => computer.isViolating && handleShowViolationDetails(computer)} style={{ cursor: computer.isViolating ? 'pointer' : 'default' }}>
                                        {computer.status === 'on' && computer.isViolating ? (
                                            <span style={{ 
                                                backgroundColor: '#dc2626', color: 'white', padding: '5px 10px', 
                                                borderRadius: '4px', fontSize: '0.8em', fontWeight: 'bold' 
                                            }}>
                                                🔴 {computer.violationCount}
                                            </span>
                                        ) : (
                                            <span style={{ color: computer.status === 'on' ? '#10b981' : '#9ca3af' }}>
                                                {computer.status === 'on' ? 'Bình thường' : '-'}
                                            </span>
                                        )}
                                    </td>

                                    <td>
                                        <button
                                            onClick={() => toggleFirewall(computer.macAddress, computer.statusFirewall)}
                                            className={computer.statusFirewall === 'on' ? "statusFire-on" : "statusFire-off"}
                                        >
                                            {computer.statusFirewall === 'on' ? 'on' : 'off'}
                                        </button>
                                    </td>

                                    <td>
                                        <select
                                            className="select-install"
                                            onChange={(e) => handleSoftwareInstall(computer.macAddress, e.target.value)}
                                        >
                                            <option value="">Chọn</option>
                                            <option value="unikey">unikey</option>
                                            <option value="youtube">YouTube</option>
                                            <option value="unikey">Unikey</option>
                                        </select>

                                        {/* <a href="#">Xem</a> <a href="#">Tắt máy</a> */}
                                    </td>
                                </tr>
                            ))
                        }
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default AdminDashboard;
