// AddHighlightModal.jsx
import React, { useState } from 'react';
import '../styles/AddHighlightModal.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTimes, faPlus } from '@fortawesome/free-solid-svg-icons';

const AddHighlightModal = ({ isOpen, onClose, onSubmit }) => {
  const [title, setTitle] = useState('');
  const [images, setImages] = useState(['']);

  const handleTitleChange = (e) => {
    setTitle(e.target.value);
  };

  const handleImageChange = (index, e) => {
    const newImages = [...images];
    newImages[index] = e.target.value;
    setImages(newImages);
  };

  const handleAddImage = () => {
    setImages([...images, '']);
  };

  const handleFormSubmit = (e) => {
    e.preventDefault();
    onSubmit({ title, images });
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <button className="close-button" onClick={onClose}>
          <FontAwesomeIcon icon={faTimes} />
        </button>
        <h2>Add New Highlight</h2>
        <form onSubmit={handleFormSubmit}>
          <label>
            Title:
            <input type="text" value={title} onChange={handleTitleChange} required />
          </label>
          <div className="image-fields">
            {images.map((image, index) => (
              <label key={index}>
                Image URL:
                <input
                  type="text"
                  value={image}
                  onChange={(e) => handleImageChange(index, e)}
                  required
                />
              </label>
            ))}
          </div>
          <button type="button" onClick={handleAddImage}>
            <FontAwesomeIcon icon={faPlus} /> Add More Images
          </button>
          <button type="submit">Submit</button>
        </form>
      </div>
    </div>
  );
};

export default AddHighlightModal;
