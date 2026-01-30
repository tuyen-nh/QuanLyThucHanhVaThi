// import { useState } from 'react';
// import '../Css/signIn.css';
// import {useNavigate } from 'react-router-dom';

// export default function SignIn() {
//   const [formData, setFormData] = useState({
//     userName: '',
//     password: ''
//   });
//   const Navigate = useNavigate()
//   // 1 hàm dùng chung cho tất cả input
//   const handleOnChange = (e) => {
//     const { name, value } = e.target;
//     setFormData(prev => ({
//       ...prev,
//       [name]: value
//     }));
//   }

//   const handelSubmid = (e) => {
//     e.preventDefault();
//     console.log("Dữ liệu gửi đi:", formData); // kiểm tra trước khi gửi

//     fetch('http://localhost:8080/apt/login', {
//       method: 'POST',
//       headers: {
//         'Content-Type': 'application/json' // sửa đúng headers
//       },
//       body: JSON.stringify(formData)
//     })
//     .then(res => res.text())
//     .then(data => {
//       console.log("Phản hồi từ server:", data)
//       Navigate("/DashBoard")
//     })
//     .catch(err => {
//       console.error("Lỗi gửi dữ liệu:", err)
//     });
//   }

//   return (
//     <div className="container">
//       <h2 className="title">Đăng nhập</h2>
//       <form className="form" onSubmit={handelSubmid}>
//         <div className="formGroup">
//           <label htmlFor="username">Tên đăng nhập</label>
//           <input
//             type="text"
//             id="username"
//             name="userName"
//             value={formData.userName}
//             onChange={handleOnChange}
//             required
//             className="input"
//           />
//         </div>
//         <div className="formGroup">
//           <label htmlFor="password">Mật khẩu</label>
//           <input
//             type="password"
//             id="password"
//             name="password"
//             value={formData.password}
//             onChange={handleOnChange}
//             required
//             className="input"
//           />
//         </div>
//         <button type="submit" className="button">Đăng nhập</button>
//       </form>
//     </div>
//   );
// }


import { useState } from 'react';
import '../Css/signIn.css';
import {useNavigate } from 'react-router-dom';

export default function SignIn() {
  const [formData, setFormData] = useState({
    userName: '',
    password: ''
  });
  const Navigate = useNavigate()
  
  const handleOnChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  }

  const handelSubmid = (e) => {
    e.preventDefault();

    fetch('http://localhost:8080/apt/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json' 
      },
      body: JSON.stringify(formData)
    })
    .then(res => {
        // Kiểm tra trạng thái HTTP. Đăng nhập thành công thường là 200 OK
        if (!res.ok) {
            // Nếu lỗi (ví dụ: 401 Unauthorized), ném lỗi để nhảy vào catch
            return res.text().then(text => { throw new Error(text || 'Đăng nhập thất bại.'); });
        }
        // Giả định server chỉ trả về chuỗi Token (text)
        return res.text();
    })
    .then(token => {
      console.log("Phản hồi từ server (Token):", token);

      // BƯỚC QUAN TRỌNG: LƯU JWT TOKEN VÀO localStorage
      // Đảm bảo key 'authToken' khớp với key được sử dụng trong AdminDashboard.jsx
      localStorage.setItem('authToken', token);
      
      // Chuyển hướng đến Dashboard chỉ SAU KHI đã lưu Token thành công
      Navigate("/DashBoard");
    })
    .catch(err => {
      console.error("Lỗi đăng nhập:", err.message || err);
      alert(err.message || "Tên đăng nhập hoặc mật khẩu không đúng."); // Thông báo lỗi cho người dùng
    });
  }

  return (
    // ... (Phần UI không đổi)
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