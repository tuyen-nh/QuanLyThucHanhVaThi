
import React, { useEffect, useState } from 'react';
import '../Css/AdminDashboard.css';
import '../Css/ComputerTab.css';

function AdminDashboard() {
    const [dataDash, setDataDash] = useState([]);
    const [dataBlackList, setDataBlackList] = useState([]);
    const [searchText, setSearchText] = useState('');
    const [dataListVirus, setDataListVirus] = useState([]); // State cho virus notifications
    const [showVirusModal, setShowVirusModal] = useState(false); // State hiển thị Modal Virus
    const [selectedVirusMachine, setSelectedVirusMachine] = useState(null); // State chọn máy trong Modal Virus
    // check violations
    const checkViolations = (computer, blacklist) => {
        // Đảm bảo softwareStatuses là một mảng
        const appsRunning = computer.softwareStatuses?.filter(app => app.status === "running") || [];
        // Chuyển Blacklist sang chữ thường để so sánh không phân biệt hoa thường   
        const lowerCaseBlacklist = blacklist.map(app => app.toLowerCase());

        const foundViolations = appsRunning
            .filter(app => lowerCaseBlacklist.includes(app.softwareName.toLowerCase()))
            .map(app => ({
                appName: app.softwareName,
                time: new Date().toLocaleTimeString(),
            }));
        return {
            ...computer,
            isViolating: foundViolations.length > 0,
            violationCount: foundViolations.length,
            violations: foundViolations,
        };
    };

    // lấy danh sách blacklist from server
    useEffect(() => {
        const token = localStorage.getItem('authToken');
        const hasToken = !!token;

        const fetchBlackListData = () => {
            if (!hasToken) {
                console.error("JWT Token không tồn tại. Vui lòng đăng nhập.");
                // Tạm dừng fetch nếu không có token và dùng mock
                setDataBlackList(MOCK_BLACKLIST);
                return;
            }

            fetch('http://localhost:8080/BlackList', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    const blacklistNames = data.map(item => item.nameBlackSoftware);
                    setDataBlackList(blacklistNames);
                    console.log("BlackList fetched:", blacklistNames);
                })
                .catch(error => console.error('Error fetching BlackList:', error));
        };

        fetchBlackListData(); // Gọi ngay lần đầu
    }, []);
    // láy thông tin computer từ server
    useEffect(() => {
        const fetchData = () => {
            // Lấy Token từ localStorage
            const token = localStorage.getItem('authToken');
            // Cần có token để gọi API bảo mật
            if (!token) {
                console.error("JWT Token không tồn tại. Vui lòng đăng nhập.");
                // Tạm dừng fetch nếu không có token
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
                    // setDataDash(data);
                    const processedData = data.map(c => checkViolations(c, dataBlackList));
                    setDataDash(processedData);

                })
                .catch(err => console.error('Error fetching info:', err));

            // Fetch Virus Data
            fetch('http://localhost:8080/AI/notifications', {
                method: 'GET',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }
            })
                .then(res => res.ok ? res.json() : Promise.reject(res.status))
                .then(data => {
                    // console.log("Virus Data Fetched:", data);
                    setDataListVirus(data);
                })
                .catch(err => console.error('Error fetching virus data:', err));
        };
        if (dataBlackList.length === 0) return;

        fetchData(); // Gọi ngay lần đầu khi component mount

        // Tăng thời gian refresh lên 10s để tránh spam log/network
        const intervalId = setInterval(fetchData, 10000);

        // Cleanup khi component unmount
        return () => clearInterval(intervalId);
    }, [dataBlackList]);


    // chọn phần mềm để cài đặt
    const handleSoftwareInstall = (macAddress, softwareName) => {
        if (!softwareName) return;

        const token = localStorage.getItem('authToken'); // Lấy Token
        if (!token) {
            alert("Lỗi: Không tìm thấy JWT Token!");
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
                    alert(`Đã gửi yêu cầu cài ${softwareName} đến máy ${macAddress}`);
                } else {
                    alert("Gửi yêu cầu thất bại! (Kiểm tra Token và Server)");
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
            alert("Lỗi: Không tìm thấy JWT Token!");
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
                alert('Lỗi khi gửi lệnh firewall! (Kiểm tra Token và Server)');
            }
        } catch (error) {
            console.error('Lỗi khi gửi lệnh firewall:', error);
            alert('Không thể kết nối đến server');
        }
    };


    const countOnlineComputers = () => {
        return filteredData.filter(computer => computer.status === 'on').length;
    };

    const countOffineComputers = () => {
        return filteredData.filter(computer => computer.status === 'off').length;
    };

    // Helper functions for Virus Features
    const getInfectedMacs = () => {
        // Use v.macAddress (from getMacAddress() in backend) or fallback to v.computer?.macAddress
        // Also normalize casing
        const macs = dataListVirus
            .map(v => v.macAddress || v.computer?.macAddress)
            .filter(Boolean)
            .map(mac => mac.toLowerCase().trim()); // Added trim()
        // console.log("Infected MACs found:", macs); // Removed spam log
        return Array.from(new Set(macs));
    };

    const infectedMacs = getInfectedMacs();
    const countInfectedComputers = infectedMacs.length;

    const getVirusDetailsForMachine = (mac) => {
        if (!mac) return [];
        const targetMac = mac.toLowerCase();
        return dataListVirus.filter(v => {
            const vMac = v.macAddress || v.computer?.macAddress;
            return vMac && vMac.toLowerCase().trim() === targetMac;
        });
    };

    // Modal Component (Inline)
    const VirusModal = () => {
        if (!showVirusModal) return null;

        return (
            <div style={{
                position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
                backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000
            }}>
                <div style={{
                    backgroundColor: 'white', padding: '20px', borderRadius: '10px',
                    width: '600px', maxHeight: '80vh', overflowY: 'auto', position: 'relative'
                }}>
                    <button
                        onClick={() => { setShowVirusModal(false); setSelectedVirusMachine(null); }}
                        style={{ position: 'absolute', top: '10px', right: '10px', background: 'none', border: 'none', fontSize: '20px', cursor: 'pointer' }}
                    >✖</button>

                    <h2 style={{ color: '#dc2626', marginBottom: '15px' }}>☣️ Danh sách máy nhiễm Virus</h2>

                    {!selectedVirusMachine ? (
                        <ul style={{ listStyle: 'none', padding: 0 }}>
                            {infectedMacs.map(mac => {
                                const compName = dataDash.find(c => c.macAddress?.toLowerCase() === mac)?.nameComputer || mac; // mac is already lowercased from infectedMacs
                                return (
                                    <li key={mac}
                                        onClick={() => setSelectedVirusMachine(mac)}
                                        style={{
                                            padding: '10px', borderBottom: '1px solid #eee', cursor: 'pointer',
                                            display: 'flex', justifyContent: 'space-between', alignItems: 'center'
                                        }}
                                        onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#ffeeef'}
                                        onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                                    >
                                        <span>🖥️ <b>{compName}</b> <small>({mac})</small></span>
                                        <span>👉</span>
                                    </li>
                                );
                            })}
                            {infectedMacs.length === 0 && <p>Không có máy nào bị nhiễm.</p>}
                        </ul>
                    ) : (
                        <div>
                            <button onClick={() => setSelectedVirusMachine(null)} style={{ bottomMargin: '10px', cursor: 'pointer' }}>⬅ Quay lại</button>
                            <h3 style={{ marginTop: '10px' }}>Chi tiết máy: {dataDash.find(c => c.macAddress?.toLowerCase() === selectedVirusMachine)?.nameComputer || selectedVirusMachine}</h3>
                            <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
                                <thead>
                                    <tr style={{ backgroundColor: '#f3f4f6' }}>
                                        <th style={{ padding: '8px', border: '1px solid #ddd' }}>File Path</th>
                                        <th style={{ padding: '8px', border: '1px solid #ddd' }}>File Name</th>
                                        <th style={{ padding: '8px', border: '1px solid #ddd' }}>Time</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {getVirusDetailsForMachine(selectedVirusMachine).map((v, idx) => (
                                        <tr key={idx}>
                                            <td style={{ padding: '8px', border: '1px solid #ddd', wordBreak: 'break-all' }}>{v.filePath}</td>
                                            <td style={{ padding: '8px', border: '1px solid #ddd' }}>{v.fileName}</td>
                                            <td style={{ padding: '8px', border: '1px solid #ddd' }}>{new Date(v.detectTime).toLocaleString()}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>
        );
    };

    const filteredData = dataDash.filter(item => {
        // Lọc theo từ khóa tìm kiếm
        const matchesSearch = item.macAddress.toLowerCase().includes(searchText.toLowerCase()) ||
            item.nameComputer.toLowerCase().includes(searchText.toLowerCase()) ||
            item.ipAddress.toLowerCase().includes(searchText.toLowerCase());

        // Lọc đặc biệt cho trạng thái vi phạm
        if (searchText === 'violating') {
            return item.status === 'on' && item.isViolating;
        }
        // lọc các máy cần bảo trì 
        if (searchText.toLowerCase() === 'bảo trì') {
            const daysUsed = item.timeUse / (24 * 60);
            const maxDays = 365;

            // Return only 'on' machines that need maintenance
            return item.status === 'on' && daysUsed >= maxDays;
        }
        // Nếu không phải lọc vi phạm, trả về kết quả tìm kiếm chung
        return matchesSearch;
    });

    // Xử lý hiển thị chi tiết vi phạm (Chỉ in ra console vì đã gỡ Modal)
    const handleShowViolationDetails = (computer) => {
        console.log("Chi tiết vi phạm của máy:", computer.nameComputer);
        computer.violations.forEach(v => {
            console.log(`- App: ${v.appName}, Time: ${v.time}`);
        });
        alert(`Máy ${computer.nameComputer} có ${computer.violationCount} vi phạm. Xem chi tiết trong Console.`);
    };
    // Hàm tính toán số máy vi phạm (chỉ tính máy online)
    const countViolatingComputers = (data) => {
        return data.filter(computer => computer.status === 'on' && computer.isViolating).length;
    };

    // This function is already in your AdminDashboard.js file
    const getMaintenanceStatus = (daysUsed, maxDays) => {

        // Calculate percentage
        const percentage = (daysUsed / maxDays) * 100;

        if (percentage >= 100) {
            // 🔴 Required
            return (
                // Added .toFixed(2) as requested
                <span style={{ color: '#dc2626', fontWeight: 'bold' }}>
                    🔴 Cần bảo trì ({daysUsed.toFixed(2)} ngày)
                </span>
            );
        }

        if (percentage >= 80) {
            // 🟡 Due Soon
            return (
                <span style={{ color: '#b45309' }}>
                    🟡 Sắp đến hạn
                </span>
            );
        }

        // 🟢 OK
        return (
            <span style={{ color: '#067647' }}>
                🟢 Hoạt động ổn
            </span>
        );
    };
    return (
        <div className="admin-dashboard">
            <header className="header">
                <h1 className="title">Computer Management</h1>
                <div className="admin-info">
                    <span className="admin-label">Admin</span>
                    <img src="/avatar.png" alt="avatar" className="avatar" />
                </div>
            </header>

            <div className="tabs">
                <div className="dashboard-section">
                    <div className="card-grid">
                        <div className="card">
                            <h2>🟢 Online PCs</h2>
                            <p>{countOnlineComputers()} / {filteredData.length}</p>
                        </div>
                        <div className="card">
                            <h2>🔴 Offline PCs</h2>
                            <p>{countOffineComputers()} / {filteredData.length}</p>
                        </div>

                        <div
                            className={`card violation-card-style ${countViolatingComputers(dataDash) > 0 ? 'violating-pulse' : ''}`}
                            onClick={() => countViolatingComputers(dataDash) > 0 && setSearchText('violating')}
                            style={{ cursor: 'pointer', borderLeft: '5px solid #dc2626' }}
                        >
                            <h2 style={{ color: '#dc2626' }}>⚠️ CẦN XỬ LÝ</h2>
                            <p style={{ color: '#dc2626', fontWeight: 'bold' }}>
                                {countViolatingComputers(dataDash)} <span style={{ fontSize: '0.8em', fontWeight: 'normal', color: '#6b7280' }}>máy vi phạm</span>
                            </p>
                        </div>
                        <div
                            className={`card`}
                            onClick={() => setShowVirusModal(true)}
                            style={{ cursor: 'pointer', borderLeft: '5px solid #ef4444', backgroundColor: countInfectedComputers > 0 ? '#fef2f2' : 'white' }}
                        >
                            <h2 style={{ color: '#ef4444' }}>☣️ PHÁT HIỆN VIRUS</h2>
                            <p style={{ color: '#ef4444', fontWeight: 'bold' }}>
                                {countInfectedComputers} <span style={{ fontSize: '0.8em', fontWeight: 'normal', color: '#6b7280' }}>máy bị nhiễm</span>
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
                            <th style={{ backgroundColor: '#fee2e2', color: '#991b1b', fontWeight: 'bold' }}>VI PHẠM</th>
                            <th>Bảo trì</th>
                            <th>Firewall</th>
                            <th>Cài phần mềm</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            filteredData.map((computer, index) => {
                                const localMac = computer.macAddress?.toLowerCase().trim();
                                const isInfected = infectedMacs.includes(localMac);
                                return (
                                    <tr key={index} style={isInfected ? { backgroundColor: '#fca5a5' } : {}}>
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
                                            {getMaintenanceStatus(computer.timeUse / (24 * 60), 365)}
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
                                                <option value="download">unikey</option>
                                                <option value="youtube">YouTube</option>
                                                <option value="unikey">Unikey</option>
                                            </select>

                                        </td>
                                    </tr>
                                );
                            })
                        }
                    </tbody>
                </table>
            </div>
            <VirusModal />
        </div>
    );
}

export default AdminDashboard;