import React, { useState, useEffect } from 'react';
import { addHighlight, getPostById, getReelById, getUserById, removeHighlight, updateUserProfile } from '../services/api';
import '../styles/ProfilePage.css';
import HighlightsModal from '../components/HighlightsModal';
import EditProfileModal from '../components/EditProfileModal';
import AddHighlightModal from '../components/AddHighlightModal';
import { useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faPencilAlt, faPlus, faTimes } from '@fortawesome/free-solid-svg-icons'


const ProfilePage = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [activeTab, setActiveTab] = useState('Posts');
  const [contentItems, setContentItems] = useState([]);
  const [token, setToken] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [modalImages, setModalImages] = useState([]);
  const [modalTitle, setModalTitle] = useState('');
  const [isEditMode, setIsEditMode] = useState(false);
  const [addHighlightModalOpen, setAddHighlightModalOpen] = useState(false); // State for AddHighlightModal
  const [isEditProfileModalOpen, setisEditProfileModalOpen] = useState(false);

    // Sample initial state, replace with actual data fetching logic
    const [profile, setProfile] = useState({
        fullName: "",
        bio: "",
        location: "",
        website: "",
        profilePicture: "",
        password: "", // Note: You may handle password editing separately for security
        privateAccount: false
    });

    const handleEditProfileClick = () => {
      if(user)
        setProfile(user);
        setisEditProfileModalOpen(true);
    };

    const handleCloseModal = () => {
        setisEditProfileModalOpen(false);
    };

    const handleProfileUpdate = async(updatedProfile) => {
        console.log("data in handle method = ", updatedProfile);
        setProfile(updatedProfile);
        const response = await updateUserProfile(user.id, updatedProfile, token);

        if(response.status === 200)
            alert("Profile has been updated");
        else  
          alert("There's a issue with updating");
        
        handleCloseModal();
        window.location.reload();
    };


  useEffect(() => {
    const fetchTokenAndUserData = async () => {
      console.log("Fetching token...");
      const storedToken = localStorage.getItem('authToken');
      const id = localStorage.getItem('userId');
      
      if (storedToken) {
        console.log("Token found:", storedToken);
        setToken(storedToken);
      } else {
        console.log("No token found");
      }

      try {
        console.log("Using token:", storedToken);
        const response = await getUserById(id, storedToken);

        if (response.status === 401) {
          alert("Please sign in.");
          navigate('/signin');
        } else {
          setUser(response.data);
        }
      } catch (error) {
        console.error('Error fetching user data:', error);
        alert('An error occurred while fetching user data.');
      }
    };

    fetchTokenAndUserData();
    
  }, []); 

  useEffect(() => {
    const fetchContentDetails = async () => {
      try {
        let items = [];
        if (user && token) {
          switch (activeTab) {
            case 'Posts':
              items = await Promise.all(user.posts.map(id => getPostById(id, token)));
              break;
            case 'Reels':
              items = await Promise.all(user.reels.map(id => getReelById(id, token)));
              break;
            case 'Saved':
              items = await Promise.all(user.saved.map(id => getPostById(id, token)));
              break;
            case 'Tagged':
              items = await Promise.all(user.tagged.map(id => getPostById(id, token)));
              break;
            default:
              break;
          }
          setContentItems(items);
        }
      } catch (error) {
        console.error('Error fetching content details:', error);
      }
    };

    fetchContentDetails();
  }, [activeTab, user, token]);

  const openModal = (images, title) => {
    setModalImages(images);
    setModalTitle(title);
    setModalOpen(true);
  };

  const closeModal = () => {
    setModalOpen(false);
    setModalImages([]);
    setModalTitle('');
  };

  const handleEditHighlights = () => {
    // Implement edit functionality
    setIsEditMode(!isEditMode);
    console.log("Edit highlights");
  };

  const handleAddHighlight = () => {
    setAddHighlightModalOpen(true); // Open the add highlight modal
  };

  const handleRemoveHighlight = async(index) => {
    console.log(`Remove highlight at index ${index}`);
    const respone = await removeHighlight(user.id, index, token);
    window.location.reload();
    console.log("response = ", respone);
  };

  const handleAddHighlightSubmit = async(data) => {
    const response = await addHighlight(user.id, data, token);
    
    if(response.status === 200) {
      setAddHighlightModalOpen(false);
      window.location.reload(); // Reload the page to see the new highlight
    }
  };

  if (!user) {
    return <div>Loading...</div>;
  }

  const renderContent = () => {
    return contentItems.map((item, index) => (
      <div key={index} className="post-item">
        <img src={item.image} alt={item.caption} className="post-image" />
      </div>
    ));
  };

  return (
    <div className="profile-container">
      <header className="profile-header">
        <img className="profile-picture" src={user.profilePicture} alt={`${user.username}'s profile`} />
        <div className="profile-details">
          <div className="profile-info">
            <h2 className="profile-username">{user.id}</h2>
            <button className="edit-profile-button" onClick={handleEditProfileClick}>Edit Profile</button>
            <span className="settings-icon">⚙️</span>
          </div>
          <div className="profile-stats">
            <span><strong>{user.postsCount}</strong> posts</span>
            <span><strong>{user.followersCount}</strong> followers</span>
            <span><strong>{user.followingCount}</strong> following</span>
          </div>
          <div className="profile-bio">
            <p className="profile-fullname"><strong>{user.fullName}</strong></p>
            <p>{user.bio}</p>
            <p className="profile-location">📍 {user.location}</p>
            <a href={user.website} className="profile-website" target="_blank" rel="noopener noreferrer">{user.website}</a>
          </div>
        </div>
      </header>

      <div className="profile-highlights">
        {user.highlights.map((highlight, index) => (
          <div key={index} className="highlight-item" onClick={() => openModal(highlight.images, highlight.title)}>
            <img src={highlight.images[0]} alt={highlight.title} className="highlight-image" />
            <p className="highlight-title">{highlight.title}</p>
            {isEditMode && (
              <button className="highlight-remove" onClick={() => handleRemoveHighlight(index)}>
                <FontAwesomeIcon icon={faTimes} />
              </button>
            )}
          </div>
        ))}
        <div className="highlight-item highlight-button" onClick={handleEditHighlights}>
          <div className="highlight-image edit-highlights">
            <FontAwesomeIcon icon={faPencilAlt} />
          </div>
          <p className="highlight-title">Edit</p>
        </div>
        <div className="highlight-item highlight-button" onClick={handleAddHighlight}>
          <div className="highlight-image add-highlight">
            <FontAwesomeIcon icon={faPlus} />
          </div>
          <p className="highlight-title">New</p>
        </div>
      </div>

      <div className="profile-tabs">
        <button className={`tab-button ${activeTab === 'Posts' ? 'active' : ''}`} onClick={() => setActiveTab('Posts')}>Posts</button>
        <button className={`tab-button ${activeTab === 'Reels' ? 'active' : ''}`} onClick={() => setActiveTab('Reels')}>Reels</button>
        <button className={`tab-button ${activeTab === 'Saved' ? 'active' : ''}`} onClick={() => setActiveTab('Saved')}>Saved</button>
        <button className={`tab-button ${activeTab === 'Tagged' ? 'active' : ''}`} onClick={() => setActiveTab('Tagged')}>Tagged</button>
      </div>

      <div className="profile-content">
        {renderContent()}
      </div>

      <AddHighlightModal 
        isOpen={addHighlightModalOpen} 
        onClose={() => setAddHighlightModalOpen(false)} 
        onSubmit={handleAddHighlightSubmit} 
      />

      {isEditProfileModalOpen && (
        <EditProfileModal 
          profile={profile}
          onClose={handleCloseModal}
          onUpdate={handleProfileUpdate}
        />
      )}

      <HighlightsModal isOpen={modalOpen} onClose={closeModal} images={modalImages} title={modalTitle} />

    </div>
  );
};

export default ProfilePage;