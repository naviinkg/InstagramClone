import React, { useState, useEffect } from 'react';
import '../styles/HighlightsModal.css';

const HighlightsModal = ({ isOpen, onClose, images, title }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    let interval;

    if (isOpen) {
      setProgress(0);

      interval = setInterval(() => {
        setProgress((prevProgress) => {
          if (prevProgress >= 100) {
            handleNext();
            return 0;
          }
          return prevProgress + 1;
        });
      }, 100);
    }

    return () => {
      clearInterval(interval);

      if (!isOpen) {
        setCurrentIndex(0);
      }
    };
  }, [isOpen, currentIndex]);

  const handleNext = () => {
    if (currentIndex < images.length - 1) {
      setCurrentIndex(currentIndex + 1);
      setProgress(0);
    } else {
      onClose();
    }
  };

  const handlePrev = () => {
    if (currentIndex > 0) {
      setCurrentIndex(currentIndex - 1);
      setProgress(0);
    } else {
      onClose();
    }
  };

  const handleClick = (e) => {
    const clickX = e.clientX - e.currentTarget.getBoundingClientRect().left;
    const imageHalfWidth = e.currentTarget.clientWidth / 2;
    if (clickX < imageHalfWidth) {
      handlePrev();
    } else {
      handleNext();
    }
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h2 className="modal-title">{title}</h2>
        <div className="modal-image-container" onClick={handleClick}>
          <div className="progress-bar" style={{ width: `${progress}%` }}></div>
          <img
            src={images[currentIndex]}
            alt={`Highlight ${title} - ${currentIndex}`}
            className="modal-image"
          />
        </div>
      </div>
    </div>
  );
};

export default HighlightsModal;