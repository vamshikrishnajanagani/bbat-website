/**
 * Sitemap Generator Utility
 * Generates XML sitemap for SEO optimization
 */

export interface SitemapUrl {
  loc: string;
  lastmod?: string;
  changefreq?: 'always' | 'hourly' | 'daily' | 'weekly' | 'monthly' | 'yearly' | 'never';
  priority?: number;
}

/**
 * Generate XML sitemap from URL list
 */
export const generateSitemap = (urls: SitemapUrl[]): string => {
  const xmlHeader = '<?xml version="1.0" encoding="UTF-8"?>';
  const urlsetOpen = '<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">';
  const urlsetClose = '</urlset>';

  const urlEntries = urls.map(url => {
    const loc = `  <url>\n    <loc>${escapeXml(url.loc)}</loc>`;
    const lastmod = url.lastmod ? `\n    <lastmod>${url.lastmod}</lastmod>` : '';
    const changefreq = url.changefreq ? `\n    <changefreq>${url.changefreq}</changefreq>` : '';
    const priority = url.priority !== undefined ? `\n    <priority>${url.priority}</priority>` : '';
    
    return `${loc}${lastmod}${changefreq}${priority}\n  </url>`;
  }).join('\n');

  return `${xmlHeader}\n${urlsetOpen}\n${urlEntries}\n${urlsetClose}`;
};

/**
 * Escape XML special characters
 */
const escapeXml = (str: string): string => {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&apos;');
};

/**
 * Get static pages for sitemap
 */
export const getStaticPages = (baseUrl: string): SitemapUrl[] => {
  const today = new Date().toISOString().split('T')[0];
  
  return [
    {
      loc: `${baseUrl}/`,
      lastmod: today,
      changefreq: 'daily',
      priority: 1.0
    },
    {
      loc: `${baseUrl}/members`,
      lastmod: today,
      changefreq: 'weekly',
      priority: 0.9
    },
    {
      loc: `${baseUrl}/players`,
      lastmod: today,
      changefreq: 'weekly',
      priority: 0.9
    },
    {
      loc: `${baseUrl}/tournaments`,
      lastmod: today,
      changefreq: 'daily',
      priority: 0.9
    },
    {
      loc: `${baseUrl}/districts`,
      lastmod: today,
      changefreq: 'monthly',
      priority: 0.8
    },
    {
      loc: `${baseUrl}/news`,
      lastmod: today,
      changefreq: 'daily',
      priority: 0.8
    },
    {
      loc: `${baseUrl}/media`,
      lastmod: today,
      changefreq: 'weekly',
      priority: 0.7
    },
    {
      loc: `${baseUrl}/contact`,
      lastmod: today,
      changefreq: 'monthly',
      priority: 0.6
    }
  ];
};

/**
 * Generate dynamic URLs for content (to be called with API data)
 */
export const generateDynamicUrls = (
  baseUrl: string,
  items: Array<{ id: string; updatedAt?: string }>,
  pathPrefix: string,
  changefreq: SitemapUrl['changefreq'] = 'weekly',
  priority: number = 0.7
): SitemapUrl[] => {
  return items.map(item => ({
    loc: `${baseUrl}${pathPrefix}/${item.id}`,
    lastmod: item.updatedAt ? new Date(item.updatedAt).toISOString().split('T')[0] : undefined,
    changefreq,
    priority
  }));
};
