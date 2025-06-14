import { useState } from 'react';
import '../Css/signIn.css';
import {useNavigate } from 'react-router-dom';

export default function SignIn() {
  const [formData, setFormData] = useState({
    userName: '',
    password: ''
  });
  const Navigate = useNavigate()
  // 1 hàm dùng chung cho tất cả input
  const handleOnChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  }

  const handelSubmid = (e) => {
    e.preventDefault();
    console.log("Dữ liệu gửi đi:", formData); // kiểm tra trước khi gửi

    fetch('http://localhost:8080/apt/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json' // sửa đúng headers
      },
      body: JSON.stringify(formData)
    })
    .then(res => res.text())
    .then(data => {
      console.log("Phản hồi từ server:", data)
      Navigate("/DashBoard")
    })
    .catch(err => {
      console.error("Lỗi gửi dữ liệu:", err)
    });
  }

  return (
    <div className="container">
      <h2 className="title">Đăng nhập</h2>
      <form className="form" onSubmit={handelSubmid}>
        <div className="formGroup">
          <label htmlFor="username">Tên đăng nhập</label>
          <input
            type="text"
            id="username"
            name="userName"
            value={formData.userName}
            onChange={handleOnChange}
            required
            className="input"
          />
        </div>
        <div className="formGroup">
          <label htmlFor="password">Mật khẩu</label>
          <input
            type="password"
            id="password"
            name="password"
            value={formData.password}
            onChange={handleOnChange}
            required
            className="input"
          />
        </div>
        <button type="submit" className="button">Đăng nhập</button>
      </form>
    </div>
  );
}
