# Caching and Performance Optimization Implementation

## Overview

This document describes the caching and performance optimization features implemented for the Telangana Ball Badminton Association Website.

## Backend Caching (Redis)

### Configuration

Redis caching is configured in `CacheConfig.java` with the following cache regions:

| Cache Name | TTL | Use Case |
|------------|-----|----------|
| members | 6 hours | Member profiles (changes infrequently) |
| players | 3 hours | Player profiles and data |
| tournaments | 30 minutes | Tournament information (changes frequently) |
| districts | 1 day | Geographic data (rarely changes) |
| news | 15 minutes | News articles (frequently updated) |
| media | 2 hours | Media galleries |
| statistics | 10 minutes | Calculated statistics |
| rankings | 1 hour | Player rankings |

### Service Layer Caching

Caching annotations are applied to service methods:

```java
@Cacheable(value = "members", key = "#id")
public Optional<MemberResponse> getMemberById(UUID id) { ... }

@CacheEvict(value = "members", key = "#id")
public void deleteMember(UUID id) { ... }

@Caching(evict = {
    @CacheEvict(value = "members", key = "#id"),
    @CacheEvict(value = "members", key = "'all-active'")
})
public MemberResponse updateMember(UUID id, MemberRequest request) { ... }
```

### Cache Invalidation Strategy

- **Create operations**: Evict list caches
- **Update operations**: Evict specific item and list caches
- **Delete operations**: Evict specific item and related list caches
- **Conditional eviction**: Based on entity properties (e.g., prominent members)

## API Response Caching

### HTTP Cache Headers

The `CacheHeadersInterceptor` adds appropriate cache headers to API responses:

| Endpoint Pattern | Cache-Control | Duration |
|-----------------|---------------|----------|
| /districts | public, max-age=86400 | 1 day |
| /members | public, max-age=21600 | 6 hours |
| /players | public, max-age=10800 | 3 hours |
| /media | public, max-age=7200 | 2 hours |
| /tournaments | public, max-age=1800 | 30 minutes |
| /news | public, max-age=900 | 15 minutes |
| /statistics | public, max-age=600 | 10 minutes |
| /auth, /admin | no-cache, no-store | No cache |

### Static Resources

Static resources (images, CSS, JS) are cached for 1 year with immutable flag:
```
Cache-Control: public, max-age=31536000, immutable
```

## Frontend Optimization

### Image Optimization

#### LazyImage Component

The `LazyImage` component provides:
- Lazy loading using Intersection Observer
- Progressive image loading with blur placeholder
- Responsive srcset support
- WebP format with fallback
- Error handling with fallback images

Usage:
```tsx
<LazyImage
  src="/images/player.jpg"
  alt="Player photo"
  width={800}
  height={600}
  quality={75}
  responsive={true}
/>
```

#### Image Optimization Utilities

Located in `utils/imageOptimization.ts`:

- `generateSrcSet()`: Generate responsive image srcsets
- `getOptimizedImageUrl()`: Get optimized image URLs with query parameters
- `compressImage()`: Client-side image compression
- `preloadImage()`: Preload critical images
- `supportsWebP()`: Check WebP support

### CDN Configuration

CDN configuration in `config/cdn.ts`:

```typescript
const CDN_CONFIG = {
  production: {
    enabled: true,
    baseUrl: 'https://cdn.telanganaballbadminton.org',
    imageUrl: 'https://images.telanganaballbadminton.org',
    staticUrl: 'https://static.telanganaballbadminton.org',
  },
};
```

Functions:
- `getCdnUrl()`: Get CDN URL for assets
- `getCdnImageUrl()`: Get optimized image URL from CDN
- `preloadAssets()`: Preload critical assets
- `prefetchAssets()`: Prefetch assets for future navigation

### API Client Caching

The API client includes in-memory caching:

```typescript
api.get('/members', { 
  useCache: true, 
  cacheDuration: CACHE_DURATION.medium 
});
```

Cache durations:
- Short: 5 minutes
- Medium: 15 minutes
- Long: 1 hour

## Performance Best Practices

### Backend

1. **Use appropriate cache TTLs**: Match TTL to data volatility
2. **Implement cache warming**: Preload frequently accessed data
3. **Monitor cache hit rates**: Use Redis monitoring tools
4. **Use cache tags**: Group related cache entries for bulk invalidation

### Frontend

1. **Lazy load images**: Use IntersectionObserver for below-the-fold images
2. **Optimize image sizes**: Serve appropriate sizes for different viewports
3. **Use WebP format**: With fallback to JPEG/PNG
4. **Implement code splitting**: Load code on demand
5. **Preload critical resources**: Use `<link rel="preload">` for critical assets
6. **Use CDN**: Serve static assets from CDN

## Monitoring and Metrics

### Redis Monitoring

Monitor these metrics:
- Cache hit rate
- Memory usage
- Eviction rate
- Connection count

### Frontend Performance

Monitor these metrics:
- First Contentful Paint (FCP)
- Largest Contentful Paint (LCP)
- Time to Interactive (TTI)
- Total Blocking Time (TBT)
- Cumulative Layout Shift (CLS)

## Configuration

### Environment Variables

Backend:
```properties
# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_SSL=false
```

Frontend:
```env
# CDN Configuration
REACT_APP_CDN_URL=https://cdn.telanganaballbadminton.org
REACT_APP_CDN_IMAGE_URL=https://images.telanganaballbadminton.org
REACT_APP_CDN_STATIC_URL=https://static.telanganaballbadminton.org
```

## Testing

### Backend Cache Testing

Test cache behavior:
```java
@Test
public void testMemberCaching() {
    // First call - cache miss
    memberService.getMemberById(memberId);
    
    // Second call - cache hit
    memberService.getMemberById(memberId);
    
    // Verify cache hit
    verify(memberRepository, times(1)).findById(memberId);
}
```

### Frontend Performance Testing

Use Lighthouse or WebPageTest to measure:
- Page load time
- Time to First Byte (TTFB)
- Resource loading times
- Cache effectiveness

## Troubleshooting

### Common Issues

1. **Cache not working**: Check Redis connection and configuration
2. **Stale data**: Verify cache invalidation logic
3. **High memory usage**: Adjust TTLs or implement cache size limits
4. **Slow image loading**: Check CDN configuration and image optimization

### Debug Mode

Enable cache debugging:
```properties
logging.level.org.springframework.cache=DEBUG
```

## Future Enhancements

1. **Distributed caching**: Implement Redis Cluster for high availability
2. **Cache warming**: Preload frequently accessed data on startup
3. **Advanced invalidation**: Implement cache tags for complex invalidation
4. **Service Worker**: Implement offline caching with Service Workers
5. **HTTP/2 Server Push**: Push critical resources proactively
6. **Brotli compression**: Use Brotli for better compression than gzip
