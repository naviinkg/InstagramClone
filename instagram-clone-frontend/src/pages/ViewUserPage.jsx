import React, { useState, useEffect } from 'react';
import { followUser, unfollowUser, getPostById, getReelById, getUserById } from '../services/api';
import '../styles/ProfilePage.css'; // Use the same CSS as ProfilePage
import { useLocation } from 'react-router-dom';
import { getToken, getUserId } from '../services/session';
import HighlightsModal from '../components/HighlightsModal';

const ViewUserPage = () => {
  const location = useLocation();
  const { viewUserId } = location.state || {};
  const [viewUser, setViewUser] = useState(null);
  const [activeTab, setActiveTab] = useState('Posts');
  const [contentItems, setContentItems] = useState([]);
  const [isFollowing, setIsFollowing] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalImages, setModalImages] = useState([]);
  const [modalTitle, setModalTitle] = useState('');

  const token = getToken();
  const userId = getUserId();

  // Fetch user details on mount and when `viewUserId` changes
  useEffect(() => {
    const fetchUserDetails = async () => {
      if (viewUserId && token) {
        try {
          const userDetails = await getUserById(viewUserId, token);
          setViewUser(userDetails.data);
          console.log("here in viewUserPage = ",viewUser);
          setIsFollowing(userDetails.data.followers.includes(userId));
        } catch (error) {
          console.error('Error fetching user details:', error);
        }
      }
    };

    fetchUserDetails();
  }, [viewUserId, token, userId]);

  // Fetch content based on the active tab and user details
  useEffect(() => {
    const fetchContentDetails = async () => {
      if (!viewUser || !token) return;

      try {
        let items = [];
        switch (activeTab) {
          case 'Posts':
            items = await Promise.all(viewUser.posts.map(id => getPostById(id, token)));
            break;
          case 'Reels':
            items = await Promise.all(viewUser.reels.map(id => getReelById(id, token)));
            break;
          case 'Saved':
            items = await Promise.all(viewUser.saved.map(id => getPostById(id, token)));
            break;
          case 'Tagged':
            items = await Promise.all(viewUser.tagged.map(id => getPostById(id, token)));
            break;
          default:
            break;
        }
        setContentItems(items);
      } catch (error) {
        console.error('Error fetching content details:', error);
      }
    };

    fetchContentDetails();
  }, [activeTab, viewUser, token]);

  const handleFollowClick = async () => {
    try {
      if (isFollowing) {
        const response = await unfollowUser(userId, viewUser.id, token);
        
        console.log(response);
        if (response.status === 200) {
          setIsFollowing(false);
        } else {
          alert("Error following user");
        }
      } else {
        const response = await followUser(userId, viewUser.id, token);
        if (response.status === 200) {
          setIsFollowing(true);
        } else {
          alert("Error following user");
        }
      }
    } catch (error) {
      console.error('Error following/unfollowing user:', error);
    }
  };

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

  const renderContent = () => {
    return contentItems.map((item, index) => (
      <div key={index} className="post-item">
        <img src={item.image} alt={item.caption} className="post-image" />
      </div>
    ));
  };

  if (!viewUser) {
    return <div>
      {console.log("If you seee this then the response user from backend is null",viewUser)}
      Loading...
      </div>;
  }

  if (viewUser.privateAccount && !isFollowing) {
    return (
      <div className="profile-container">
        <header className="profile-header">
          <img className="profile-picture" src={viewUser.profilePicture} alt={`${viewUser.username}'s profile`} />
          <div className="profile-details">
            <div className="profile-info">
              <h2 className="profile-username">{viewUser.id}</h2>
              <button className="follow-button" onClick={handleFollowClick}>
                {isFollowing ? 'Following' : 'Follow'}
              </button>
            </div>
            <div className="profile-stats">
              <span><strong>{viewUser.postsCount}</strong> posts</span>
              <span><strong>{viewUser.followersCount}</strong> followers</span>
              <span><strong>{viewUser.followingCount}</strong> following</span>
            </div>
            <div className="profile-bio">
              <p className="profile-fullname"><strong>{viewUser.fullName}</strong></p>
              <p>{viewUser.bio}</p>
              <a href={viewUser.website} className="profile-website" target="_blank" rel="noopener noreferrer">{viewUser.website}</a>
            </div>
          </div>
        </header>
        <p>This account is private.</p>
      </div>
    );
  }

  return (
    <div className="profile-container">
      <header className="profile-header">
        <img className="profile-picture" src={viewUser.profilePicture} alt={`${viewUser.username}'s profile`} />
        <div className="profile-details">
          <div className="profile-info">
            <h2 className="profile-username">{viewUser.id}</h2>
            <button className="follow-button" onClick={handleFollowClick}>
              {isFollowing ? 'Following' : 'Follow'}
            </button>
          </div>
          <div className="profile-stats">
            <span><strong>{viewUser.postsCount}</strong> posts</span>
            <span><strong>{viewUser.followersCount}</strong> followers</span>
            <span><strong>{viewUser.followingCount}</strong> following</span>
          </div>
          <div className="profile-bio">
            <p className="profile-fullname"><strong>{viewUser.fullName}</strong></p>
            <p>{viewUser.bio}</p>
            <a href={viewUser.website} className="profile-website" target="_blank" rel="noopener noreferrer">{viewUser.website}</a>
          </div>
        </div>
      </header>

      <div className="profile-highlights">
        {viewUser.highlights.map((highlight, index) => (
          <div key={index} className="highlight-item" onClick={() => openModal(highlight.images, highlight.title)}>
            <img src={highlight.images[0]} alt={highlight.title} className="highlight-image" />
            <p className="highlight-title">{highlight.title}</p>
          </div>
        ))}
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

      <HighlightsModal isOpen={modalOpen} onClose={closeModal} images={modalImages} title={modalTitle} />
    </div>
  );
};

export default ViewUserPage;
