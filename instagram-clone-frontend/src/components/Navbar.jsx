import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getToken, getUserId, removeToken } from '../services/session';
import { AiFillInstagram } from 'react-icons/ai'; // Import Instagram icon
import { getUserById, signOut } from '../services/api';
import '../styles/Navbar.css';

const Navbar = () => {
  const token = getToken();
  const navigate = useNavigate();
  const userId = getUserId();
  const [user, setUser] = useState();
  

  useEffect(() => {
      const fetchUser = async () => {
        try {
          const response = await getUserById(userId, token);
          setUser(response.data);
        } catch (error) {
          console.error("Error fetching usr:", error);
        }
      };

      fetchUser();
  }, [token]);



  const handleSignOut = async() => {
    const response = await signOut(user, token);

    if(response.status === 200) {
      removeToken();
      navigate('/signin');
    }
    else  
      alert("Error signing out");
  };

  return (
    <nav className="navbar">
      <div className="logo-container">
        <Link to="/" className="logo">
          <AiFillInstagram size={30} /> {/* Instagram logo with size */}
        </Link>
      </div>
      <div className="links-container">
        <Link to="/" className="link">
          Home
        </Link>
        {token ? (
          <>
            <Link to="/profile" className="link">
              Profile
            </Link>
            <button onClick={handleSignOut} className="sign-out-button">
              Sign Out
            </button>
          </>
        ) : (
          <>
            <Link to="/signin" className="link">
              Sign In
            </Link>
            <Link to="/signup" className="link">
              Sign Up
            </Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
