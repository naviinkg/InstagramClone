import React, { useState, useEffect } from 'react';
import '../styles/PostCard.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeart, faComment, faPaperPlane, faTrash } from '@fortawesome/free-solid-svg-icons';
import { getUserId, getToken } from '../services/session';
import { likePost, unlikePost, addComment, unComment, getUserById } from '../services/api';
import { useNavigate } from 'react-router-dom';

const PostCard = ({ post }) => {
  const userId = getUserId();
  const token = getToken();
  const navigate = useNavigate();

  const isLiked = post.likes.includes(userId);

  const [liked, setLiked] = useState(isLiked);
  const [likesCount, setLikesCount] = useState(post.likes.length);
  const [showComments, setShowComments] = useState(false);
  const [comment, setComment] = useState('');
  const [comments, setComments] = useState(post.comments);

  useEffect(() => {
    if (!token) {
      navigate('/signin');
    }
  }, [token, navigate]);

  const handleUserClick = async(clickedUserId) => {
    if (userId === clickedUserId) {
      navigate('/profile');
    } else {
      console.log("clickedUserId is = ", clickedUserId);
        navigate('/viewUser', { state: { viewUserId : clickedUserId } });
    }
  };

  const handleLikeClick = async () => {
    if (liked) {
      const response = await unlikePost(userId, post.id, token);
      if (response.status === 200) {
        setLikesCount(likesCount - 1);
        setLiked(false);
      } else {
        alert("Error removing liked post");
      }
    } else {
      const response = await likePost(userId, post.id, token);
      if (response.status === 200) {
        setLikesCount(likesCount + 1);
        setLiked(true);
      } else {
        alert("Error liking post");
      }
    }
  };

  const handleCommentClick = () => {
    setShowComments(!showComments);
  };

  const handleCommentChange = (event) => {
    setComment(event.target.value);
  };

  const handleCommentSubmit = async () => {
    if (comment.trim()) {
      const response = await addComment(userId, post.id, comment, token);
      if (response.status === 200) {
        alert("Comment added successfully");
        setComments([...comments, { userId, message: comment }]);
        setComment('');
      } else {
        alert("Error submitting comment");
      }
    } else {
      alert("Comment cannot be empty");
    }
  };

  const handleDeleteComment = async (index) => {
    const response = await unComment(post.id, index, token);
    if (response.status === 200) {
      alert("Comment deleted successfully");
      setComments(comments.filter((_, i) => i !== index));
    } else {
      alert("Error deleting comment");
    }
  };

  return (
    <div className="post-card">
      {console.log(post)}
      <div className="post-header">
        <span className="username" onClick={() => handleUserClick(post.userId)}>
          {post.userId}
        </span>
      </div>
      <div className="post-image2">
        <img src={post.image} alt="Post content" />
      </div>
      <div className="post-actions">
        <button className="like-btn" onClick={handleLikeClick}>
          <FontAwesomeIcon
            icon={faHeart}
            style={{
              color: liked ? 'red' : 'black',
              border: liked ? 'none' : '1px solid black',
              borderRadius: '50%',
            }}
          />
        </button>
        <button className="comment-btn" onClick={handleCommentClick}>
          <FontAwesomeIcon icon={faComment} />
        </button>
      </div>

      <div className="post-likes">{likesCount} likes</div>
      <div className="post-caption">
        <span className="username">{post.username}</span> {post.caption}
      </div>
      <div className="post-comments">
        <a href={`#view-comments-${post.id}`} onClick={handleCommentClick}>
          {comments.length} comments
        </a>
        {showComments && (
          <div className="comment-list">
            {comments.map((comment, index) => (
              <div key={index} className="comment">
                <span className="username" onClick={() => handleUserClick(comment.userId)}>
                  {comment.userId}
                </span>{' '}
                {comment.message}
                {(comment.userId === userId || post.userId === userId) && (
                  <button
                    className="delete-comment-btn"
                    onClick={() => handleDeleteComment(index)}
                  >
                    <FontAwesomeIcon icon={faTrash} />
                  </button>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
      <div className="add-comment">
        <input
          type="text"
          placeholder="Add a comment..."
          value={comment}
          onChange={handleCommentChange}
        />
        <button className="submit-comment-btn" onClick={handleCommentSubmit}>
          <FontAwesomeIcon icon={faPaperPlane} />
        </button>
      </div>
    </div>
  );
};

export default PostCard;
