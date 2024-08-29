import React, { useState } from 'react';
import '../styles/OTPModal.css'; // Import CSS for styling

const OTPModal = ({ isOpen, onClose, onSubmit }) => {
  const [otp, setOtp] = useState('');

  const handleChange = (e) => {
    setOtp(e.target.value);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(otp);
    setOtp('');
  };

  if (!isOpen) return null;

  return (
    <div className="otp-modal">
      <div className="otp-modal-content">
        <span className="close" onClick={onClose}>&times;</span>
        <h2>Enter OTP</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            value={otp}
            onChange={handleChange}
            placeholder="Enter OTP"
            required
          />
          <button type="submit">Submit</button>
        </form>
      </div>
    </div>
  );
};

export default OTPModal;
