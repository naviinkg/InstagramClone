import React, { useState } from 'react';
import { signIn } from '../services/api';
import { setToken, setUserId } from '../services/session';
import { useNavigate } from 'react-router-dom';
import '../styles/SignInPage.css'; // Import the CSS file

const SignInPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleSignIn = async () => {
    try {
      const response = await signIn({ email, password });
      if(response.status===200) {
        alert("SignIn success");
        setToken(response.data.token);
        setUserId(response.data.userId);
        navigate('/');
      }
      else  
        alert("Sign-In failed");
    } catch (error) {
      console.error("Sign-in failed:", error);
    }
  };

  return (
    <div className="signin-container">
      <h1>Sign In</h1>
      <input
        type="email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Email"
        className="signin-input"
      />
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
        className="signin-input"
      />
      <button onClick={handleSignIn} className="signin-button">
        Sign In
      </button>
    </div>
  );
};

export default SignInPage;
