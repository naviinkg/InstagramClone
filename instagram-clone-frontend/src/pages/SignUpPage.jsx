import React, { useState } from 'react';
import OTPModal from '../components/OTPModal';
import '../styles/SignUpPage.css'; // Import the CSS file

const SignUpPage = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: ''
  });
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await registerUser(formData);
      if (response.status === 200) {
        setIsModalOpen(true);
      } else {
        alert('Error registering user');
      }
    } catch (error) {
      alert('An error occurred');
    }
  };

  const handleOtpSubmit = async (otp) => {
    formData.otp = otp;
    try {
      const response = await verifyOtp(formData);
      if (response.status === 201) {
        alert('Sign Up is successful');
        window.location.href = '/signin'; // Redirect to sign-in page
      } else {
        alert('OTP verification failed: ' + response.data.message);
      }
    } catch (error) {
      alert('An error occurred during OTP verification');
    } finally {
      setIsModalOpen(false);
    }
  };

  return (
    <div className="signup-container">
      <h2>Sign Up</h2>
      <form onSubmit={handleSubmit}>
        <label>
          Username:
          <input type="text" name="name" value={formData.name} onChange={handleChange} />
        </label>
        <label>
          Email:
          <input type="email" name="email" value={formData.email} onChange={handleChange} />
        </label>
        <label>
          Password:
          <input type="password" name="password" value={formData.password} onChange={handleChange} />
        </label>
        <button type="submit">Sign Up</button>
      </form>
      <OTPModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSubmit={handleOtpSubmit}
      />
    </div>
  );
};

// Helper functions for API calls
const registerUser = async (data) => {
  const response = await fetch('http://localhost:8080/user/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  });
  console.log(response);
  return response;
};

const verifyOtp = async (data) => {
  const response = await fetch('http://localhost:8080/user/verifyOtp', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return response;
};

export default SignUpPage;
