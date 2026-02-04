/**
 * Image Optimization Utilities
 * 
 * Provides utilities for:
 * - Lazy loading images
 * - Responsive image srcsets
 * - Image compression
 * - Progressive image loading
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */

/**
 * Generate srcset for responsive images
 * @param baseUrl - Base URL of the image
 * @param widths - Array of widths to generate
 * @returns srcset string
 */
export const generateSrcSet = (baseUrl: string, widths: number[] = [320, 640, 960, 1280, 1920]): string => {
  return widths
    .map(width => `${baseUrl}?w=${width} ${width}w`)
    .join(', ');
};

/**
 * Generate sizes attribute for responsive images
 * @param breakpoints - Object mapping breakpoints to sizes
 * @returns sizes string
 */
export const generateSizes = (breakpoints: Record<string, string> = {
  '(max-width: 640px)': '100vw',
  '(max-width: 1024px)': '50vw',
  '(max-width: 1280px)': '33vw',
}): string => {
  const entries = Object.entries(breakpoints);
  const sizeStrings = entries.slice(0, -1).map(([query, size]) => `${query} ${size}`);
  const defaultSize = entries[entries.length - 1]?.[1] || '25vw';
  return [...sizeStrings, defaultSize].join(', ');
};

/**
 * Get optimized image URL with query parameters
 * @param url - Original image URL
 * @param options - Optimization options
 * @returns Optimized image URL
 */
export const getOptimizedImageUrl = (
  url: string,
  options: {
    width?: number;
    height?: number;
    quality?: number;
    format?: 'webp' | 'jpeg' | 'png';
  } = {}
): string => {
  if (!url) return '';
  
  const params = new URLSearchParams();
  
  if (options.width) params.append('w', options.width.toString());
  if (options.height) params.append('h', options.height.toString());
  if (options.quality) params.append('q', options.quality.toString());
  if (options.format) params.append('fm', options.format);
  
  const separator = url.includes('?') ? '&' : '?';
  return params.toString() ? `${url}${separator}${params.toString()}` : url;
};

/**
 * Create a blur placeholder for progressive image loading
 * @param width - Width of the placeholder
 * @param height - Height of the placeholder
 * @returns Data URL for blur placeholder
 */
export const createBlurPlaceholder = (width: number = 10, height: number = 10): string => {
  const canvas = document.createElement('canvas');
  canvas.width = width;
  canvas.height = height;
  const ctx = canvas.getContext('2d');
  
  if (ctx) {
    // Create a simple gradient placeholder
    const gradient = ctx.createLinearGradient(0, 0, width, height);
    gradient.addColorStop(0, '#f0f0f0');
    gradient.addColorStop(1, '#e0e0e0');
    ctx.fillStyle = gradient;
    ctx.fillRect(0, 0, width, height);
  }
  
  return canvas.toDataURL();
};

/**
 * Preload an image
 * @param src - Image source URL
 * @returns Promise that resolves when image is loaded
 */
export const preloadImage = (src: string): Promise<void> => {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.onload = () => resolve();
    img.onerror = reject;
    img.src = src;
  });
};

/**
 * Check if WebP format is supported
 * @returns Promise that resolves to boolean
 */
export const supportsWebP = (): Promise<boolean> => {
  return new Promise((resolve) => {
    const webP = 'data:image/webp;base64,UklGRiQAAABXRUJQVlA4IBgAAAAwAQCdASoBAAEAAwA0JaQAA3AA/vuUAAA=';
    const img = new Image();
    img.onload = () => resolve(img.width === 1);
    img.onerror = () => resolve(false);
    img.src = webP;
  });
};

/**
 * Get image dimensions from URL
 * @param src - Image source URL
 * @returns Promise that resolves to dimensions
 */
export const getImageDimensions = (src: string): Promise<{ width: number; height: number }> => {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.onload = () => resolve({ width: img.naturalWidth, height: img.naturalHeight });
    img.onerror = reject;
    img.src = src;
  });
};

/**
 * Compress image file
 * @param file - Image file to compress
 * @param options - Compression options
 * @returns Promise that resolves to compressed file
 */
export const compressImage = (
  file: File,
  options: {
    maxWidth?: number;
    maxHeight?: number;
    quality?: number;
  } = {}
): Promise<Blob> => {
  const { maxWidth = 1920, maxHeight = 1080, quality = 0.8 } = options;
  
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    
    reader.onload = (e) => {
      const img = new Image();
      
      img.onload = () => {
        const canvas = document.createElement('canvas');
        let { width, height } = img;
        
        // Calculate new dimensions while maintaining aspect ratio
        if (width > maxWidth || height > maxHeight) {
          const ratio = Math.min(maxWidth / width, maxHeight / height);
          width *= ratio;
          height *= ratio;
        }
        
        canvas.width = width;
        canvas.height = height;
        
        const ctx = canvas.getContext('2d');
        if (!ctx) {
          reject(new Error('Failed to get canvas context'));
          return;
        }
        
        ctx.drawImage(img, 0, 0, width, height);
        
        canvas.toBlob(
          (blob) => {
            if (blob) {
              resolve(blob);
            } else {
              reject(new Error('Failed to compress image'));
            }
          },
          file.type,
          quality
        );
      };
      
      img.onerror = () => reject(new Error('Failed to load image'));
      img.src = e.target?.result as string;
    };
    
    reader.onerror = () => reject(new Error('Failed to read file'));
    reader.readAsDataURL(file);
  });
};

/**
 * Image optimization configuration
 */
export const IMAGE_CONFIG = {
  // Default quality settings
  quality: {
    thumbnail: 60,
    preview: 75,
    full: 85,
  },
  
  // Default sizes
  sizes: {
    thumbnail: { width: 150, height: 150 },
    small: { width: 320, height: 240 },
    medium: { width: 640, height: 480 },
    large: { width: 1280, height: 960 },
    xlarge: { width: 1920, height: 1440 },
  },
  
  // Responsive breakpoints
  breakpoints: [320, 640, 960, 1280, 1920],
  
  // Lazy loading options
  lazyLoad: {
    rootMargin: '50px',
    threshold: 0.01,
  },
};
