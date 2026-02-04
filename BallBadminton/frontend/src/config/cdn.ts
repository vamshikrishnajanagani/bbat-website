/**
 * CDN Configuration
 * 
 * Configures CDN settings for static assets:
 * - Image CDN URLs
 * - Static asset CDN URLs
 * - Cache busting strategies
 * - Fallback URLs
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */

/**
 * CDN configuration based on environment
 */
const CDN_CONFIG = {
  development: {
    enabled: false,
    baseUrl: '',
    imageUrl: '',
    staticUrl: '',
  },
  production: {
    enabled: true,
    baseUrl: process.env.REACT_APP_CDN_URL || 'https://cdn.telanganaballbadminton.org',
    imageUrl: process.env.REACT_APP_CDN_IMAGE_URL || 'https://images.telanganaballbadminton.org',
    staticUrl: process.env.REACT_APP_CDN_STATIC_URL || 'https://static.telanganaballbadminton.org',
  },
};

const environment = process.env.NODE_ENV || 'development';
const config = CDN_CONFIG[environment as keyof typeof CDN_CONFIG] || CDN_CONFIG.development;

/**
 * Get CDN URL for an asset
 * @param path - Asset path
 * @param type - Asset type (image, static, or default)
 * @returns Full CDN URL or original path if CDN is disabled
 */
export const getCdnUrl = (path: string, type: 'image' | 'static' | 'default' = 'default'): string => {
  if (!config.enabled || !path) {
    return path;
  }

  // Remove leading slash if present
  const cleanPath = path.startsWith('/') ? path.slice(1) : path;

  switch (type) {
    case 'image':
      return `${config.imageUrl}/${cleanPath}`;
    case 'static':
      return `${config.staticUrl}/${cleanPath}`;
    default:
      return `${config.baseUrl}/${cleanPath}`;
  }
};

/**
 * Get versioned asset URL for cache busting
 * @param path - Asset path
 * @param version - Version string (defaults to app version)
 * @returns Versioned URL
 */
export const getVersionedUrl = (path: string, version?: string): string => {
  const v = version || process.env.REACT_APP_VERSION || '1.0.0';
  const separator = path.includes('?') ? '&' : '?';
  return `${path}${separator}v=${v}`;
};

/**
 * Preload critical assets
 * @param urls - Array of asset URLs to preload
 * @param type - Resource type (image, script, style, font)
 */
export const preloadAssets = (urls: string[], type: 'image' | 'script' | 'style' | 'font' = 'image'): void => {
  urls.forEach((url) => {
    const link = document.createElement('link');
    link.rel = 'preload';
    link.href = url;
    
    switch (type) {
      case 'image':
        link.as = 'image';
        break;
      case 'script':
        link.as = 'script';
        break;
      case 'style':
        link.as = 'style';
        break;
      case 'font':
        link.as = 'font';
        link.crossOrigin = 'anonymous';
        break;
    }
    
    document.head.appendChild(link);
  });
};

/**
 * Prefetch assets for future navigation
 * @param urls - Array of asset URLs to prefetch
 */
export const prefetchAssets = (urls: string[]): void => {
  urls.forEach((url) => {
    const link = document.createElement('link');
    link.rel = 'prefetch';
    link.href = url;
    document.head.appendChild(link);
  });
};

/**
 * Get optimized image URL from CDN
 * @param path - Image path
 * @param options - Optimization options
 * @returns Optimized CDN URL
 */
export const getCdnImageUrl = (
  path: string,
  options: {
    width?: number;
    height?: number;
    quality?: number;
    format?: 'webp' | 'jpeg' | 'png';
  } = {}
): string => {
  const cdnUrl = getCdnUrl(path, 'image');
  
  const params = new URLSearchParams();
  if (options.width) params.append('w', options.width.toString());
  if (options.height) params.append('h', options.height.toString());
  if (options.quality) params.append('q', options.quality.toString());
  if (options.format) params.append('fm', options.format);
  
  const separator = cdnUrl.includes('?') ? '&' : '?';
  return params.toString() ? `${cdnUrl}${separator}${params.toString()}` : cdnUrl;
};

/**
 * Critical assets to preload on app initialization
 */
export const CRITICAL_ASSETS = {
  images: [
    '/images/logo.png',
    '/images/hero-background.jpg',
  ],
  fonts: [
    '/fonts/roboto-regular.woff2',
    '/fonts/roboto-bold.woff2',
  ],
  styles: [],
};

/**
 * Initialize CDN and preload critical assets
 */
export const initializeCdn = (): void => {
  if (config.enabled) {
    // Preload critical images
    const imageUrls = CRITICAL_ASSETS.images.map((path) => getCdnUrl(path, 'image'));
    preloadAssets(imageUrls, 'image');
    
    // Preload critical fonts
    const fontUrls = CRITICAL_ASSETS.fonts.map((path) => getCdnUrl(path, 'static'));
    preloadAssets(fontUrls, 'font');
    
    // Preload critical styles
    const styleUrls = CRITICAL_ASSETS.styles.map((path) => getCdnUrl(path, 'static'));
    preloadAssets(styleUrls, 'style');
  }
};

export default {
  getCdnUrl,
  getVersionedUrl,
  getCdnImageUrl,
  preloadAssets,
  prefetchAssets,
  initializeCdn,
  config,
};
