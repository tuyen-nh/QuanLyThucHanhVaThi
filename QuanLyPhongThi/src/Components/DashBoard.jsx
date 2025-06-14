import React, { useEffect, useState } from 'react';
import '../Css/AdminDashboard.css';
import '../Css/ComputerTab.css';

function AdminDashboard() {
    const [dataDash, setDataDash] = useState([]);
    const [searchText, setSearchText] = useState('');

    useEffect(() => {
        const fetchData = () => {
            fetch('http://localhost:8080/info')
                .then(response => response.json())
                .then(data => {
                    setDataDash(data);
                    console.log(data);
                })
                .catch(error => console.error('Error fetching data:', error));
        };

        fetchData(); // Gọi ngay lần đầu khi component mount

        const intervalId = setInterval(fetchData, 5000); // Gọi mỗi 2 giây

        // Cleanup khi component unmount để tránh memory leak
        return () => clearInterval(intervalId);
    }, []);


    // chọn phần mềm để cài đặt
    const handleSoftwareInstall = (macAddress, softwareName) => {
        if (!softwareName) return; // bỏ qua nếu chưa chọn gì

        const payload = {
            macAddress: macAddress,
            softwareName: softwareName
        };

        fetch('http://localhost:8080/sendCommandToAgent', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        })
            .then(response => {
                if (response.ok) {
                    alert(`Đã gửi yêu cầu cài ${softwareName} đến máy ${macAddress}`);
                } else {
                    alert("Gửi yêu cầu thất bại!");
                }
            })
            .catch(error => {
                console.error('Lỗi khi gửi yêu cầu:', error);
            });
    };


    // thay đổi trạng thái tường lửa 
    const toggleFirewall = async (macAddress, currentStatus) => {
        const newStatus = currentStatus === 'on' ? 'off' : 'on';

        try {
            const response = await fetch('http://localhost:8080/sendFirewallCommand', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
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
                alert('Lỗi khi gửi lệnh firewall!');
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

    // Tạo danh sách máy đã lọc dựa trên searchText
    const filteredData = dataDash.filter(item =>
        item.macAddress.toLowerCase().includes(searchText.toLowerCase()) ||
        item.nameComputer.toLowerCase().includes(searchText.toLowerCase()) ||
        item.ipAddress.toLowerCase().includes(searchText.toLowerCase())
    );
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
