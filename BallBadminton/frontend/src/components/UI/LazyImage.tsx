import React, { useState, useEffect, useRef } from 'react';
import styled from 'styled-components';
import { getOptimizedImageUrl, generateSrcSet, generateSizes, createBlurPlaceholder } from '../../utils/imageOptimization';

/**
 * LazyImage Component
 * 
 * A performant image component with:
 * - Lazy loading using Intersection Observer
 * - Progressive image loading with blur placeholder
 * - Responsive srcset support
 * - WebP format support with fallback
 * - Error handling with fallback image
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */

interface LazyImageProps {
  src: string;
  alt: string;
  width?: number;
  height?: number;
  className?: string;
  objectFit?: 'cover' | 'contain' | 'fill' | 'none' | 'scale-down';
  quality?: number;
  responsive?: boolean;
  placeholder?: string;
  fallbackSrc?: string;
  onLoad?: () => void;
  onError?: () => void;
}

const ImageContainer = styled.div<{ aspectRatio?: number }>`
  position: relative;
  overflow: hidden;
  background-color: ${({ theme }) => theme.colors?.gray?.[100] || '#f5f5f5'};
  
  ${({ aspectRatio }) =>
    aspectRatio &&
    `
    padding-bottom: ${(1 / aspectRatio) * 100}%;
  `}
`;

const StyledImage = styled.img<{ isLoaded: boolean; objectFit: string }>`
  width: 100%;
  height: 100%;
  object-fit: ${({ objectFit }) => objectFit};
  transition: opacity 0.3s ease-in-out;
  opacity: ${({ isLoaded }) => (isLoaded ? 1 : 0)};
  
  ${({ isLoaded }) =>
    !isLoaded &&
    `
    position: absolute;
    top: 0;
    left: 0;
  `}
`;

const Placeholder = styled.div<{ blur?: string }>`
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: ${({ blur }) => (blur ? `url(${blur})` : 'none')};
  background-size: cover;
  background-position: center;
  filter: blur(10px);
  transform: scale(1.1);
  transition: opacity 0.3s ease-in-out;
`;

const LoadingSpinner = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;
  border: 3px solid ${({ theme }) => theme.colors?.gray?.[300] || '#e0e0e0'};
  border-top-color: ${({ theme }) => theme.colors?.primary || '#007bff'};
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  
  @keyframes spin {
    to {
      transform: translate(-50%, -50%) rotate(360deg);
    }
  }
`;

export const LazyImage: React.FC<LazyImageProps> = ({
  src,
  alt,
  width,
  height,
  className,
  objectFit = 'cover',
  quality = 75,
  responsive = true,
  placeholder,
  fallbackSrc = '/images/placeholder.png',
  onLoad,
  onError,
}) => {
  const [isLoaded, setIsLoaded] = useState(false);
  const [isInView, setIsInView] = useState(false);
  const [hasError, setHasError] = useState(false);
  const [blurPlaceholder] = useState(() => placeholder || createBlurPlaceholder());
  const imgRef = useRef<HTMLImageElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  // Intersection Observer for lazy loading
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setIsInView(true);
            observer.disconnect();
          }
        });
      },
      {
        rootMargin: '50px',
        threshold: 0.01,
      }
    );

    if (containerRef.current) {
      observer.observe(containerRef.current);
    }

    return () => {
      observer.disconnect();
    };
  }, []);

  const handleLoad = () => {
    setIsLoaded(true);
    onLoad?.();
  };

  const handleError = () => {
    setHasError(true);
    onError?.();
  };

  const optimizedSrc = getOptimizedImageUrl(hasError ? fallbackSrc : src, {
    width,
    height,
    quality,
    format: 'webp',
  });

  const srcSet = responsive && !hasError ? generateSrcSet(src) : undefined;
  const sizes = responsive ? generateSizes() : undefined;
  const aspectRatio = width && height ? width / height : undefined;

  return (
    <ImageContainer ref={containerRef} aspectRatio={aspectRatio} className={className}>
      {!isLoaded && <Placeholder blur={blurPlaceholder} />}
      {!isLoaded && <LoadingSpinner />}
      
      {isInView && (
        <StyledImage
          ref={imgRef}
          src={optimizedSrc}
          srcSet={srcSet}
          sizes={sizes}
          alt={alt}
          width={width}
          height={height}
          isLoaded={isLoaded}
          objectFit={objectFit}
          onLoad={handleLoad}
          onError={handleError}
          loading="lazy"
        />
      )}
    </ImageContainer>
  );
};

export default LazyImage;
