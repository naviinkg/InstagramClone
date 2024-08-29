// components/CreateModal.jsx
import React, { useState } from 'react';
import '../styles/CreateModal.css'; // Assuming you have a CSS file for modal styling

const CreateModal = ({ isOpen, onClose, onSubmit }) => {
  const [activeTab, setActiveTab] = useState('POST'); // Default tab is 'POST'
  const [image, setImage] = useState('');
  const [caption, setCaption] = useState('');

  const handleSubmit = () => {
    onSubmit({ type: activeTab, image, caption });
    setImage('');
    setCaption('');
    onClose(); // Close the modal after submission
  };

  if (!isOpen) return null; // Don't render anything if the modal is closed

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <button className="close-button" onClick={onClose}>X</button>
        <div className="tab-buttons">
          <button className={activeTab === 'POST' ? 'active' : ''} onClick={() => setActiveTab('POST')}>POST</button>
          <button className={activeTab === 'REELS' ? 'active' : ''} onClick={() => setActiveTab('REELS')}>REELS</button>
        </div>
        <input
          type="text"
          placeholder="Enter link"
          value={image}
          onChange={(e) => setImage(e.target.value)}
        />
        <textarea
          placeholder="Enter caption"
          value={caption}
          onChange={(e) => setCaption(e.target.value)}
        ></textarea>
        <button onClick={handleSubmit}>Submit</button>
      </div>
    </div>
  );
};

export default CreateModal;
