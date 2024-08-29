import React, { useEffect, useState } from 'react';
import { getPosts, createPost, getFollowingPosts} from '../services/api';
import { getToken, getUserId } from '../services/session';
import PostCard from '../components/PostCard';
import CreateModal from '../components/CreateModal'; // Import the modal
import { useNavigate } from 'react-router-dom';
import '../styles/HomePage.css';

const HomePage = () => {
  const [posts, setPosts] = useState([]);
  const [showSignInPopup, setShowSignInPopup] = useState(false);
  const [showCreateModal, setShowCreateModal] = useState(false); // State for modal
  const token = getToken();
  const userId = getUserId();
  const navigate = useNavigate();

  useEffect(() => {
    if (token) {
      const fetchPosts = async () => {
        try {
          const response = await getFollowingPosts(userId, token);
          setPosts(response.data);
        } catch (error) {
          console.error("Error fetching posts:", error);
        }
      };

      fetchPosts();
    } else {
      setShowSignInPopup(true);
    }
  }, [token]);

  const handleSignIn = () => {
    setShowSignInPopup(false);
    navigate('/signin');
  };

  const handleSignUp = () => {
    setShowSignInPopup(false);
    navigate('/signup');
  };

  const handleCreate = () => {
    setShowCreateModal(true);
  };

  const handleModalClose = () => {
    setShowCreateModal(false);
  };

  const handleModalSubmit = async ({ type, image, caption }) => {
    try {
      const newPost = { type, image, caption };
      const response = await createPost(newPost, userId, token);
      setPosts([...posts, response.data]);
    } catch (error) {
      console.error("Error creating post:", error);
    }
  };

  const handleExplore = async () => {
    navigate('/explore');
  };

  return (
    <div className="home-page">
      <nav className="sidebar">
        <h1 className="logo">Instagram</h1>
        <ul>
          <li><a href="#explore" onClick={handleExplore}>Explore</a></li>
          <li><a href="#reels">Reels</a></li>
          <li><a href="#create" onClick={handleCreate}>Create</a></li>
        </ul>
      </nav>
      <main className="content">
        {showSignInPopup ? (
          <div className="sign-in-popup">
            <h2>Please Sign In</h2>
            <button onClick={handleSignIn}>Sign In</button>
            <button onClick={handleSignUp}>Sign Up</button>
          </div>
        ) : (
          <div className="posts-container">
            {posts.map(post => (
              <PostCard key={post.id} post={post} />
            ))}
          </div>
        )}
      </main>
      <CreateModal
        isOpen={showCreateModal}
        onClose={handleModalClose}
        onSubmit={handleModalSubmit}
      />
    </div>
  );
};

export default HomePage;
