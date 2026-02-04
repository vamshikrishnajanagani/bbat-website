/**
 * Structured Data (Schema.org) Utilities
 * Generate JSON-LD structured data for SEO
 */

export interface Person {
  name: string;
  position?: string;
  email?: string;
  phone?: string;
  image?: string;
}

export interface Tournament {
  id: string;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  venue: string;
  district: string;
}

export interface NewsArticle {
  id: string;
  title: string;
  content: string;
  author: string;
  publishedDate: string;
  modifiedDate?: string;
  category: string;
  imageUrl?: string;
}

/**
 * Generate Organization structured data
 */
export const generateOrganizationSchema = () => {
  return {
    "@context": "https://schema.org",
    "@type": "SportsOrganization",
    "name": "Telangana Ball Badminton Association",
    "alternateName": "TBBA",
    "url": "https://telanganaballbadminton.org",
    "logo": "https://telanganaballbadminton.org/logo512.png",
    "description": "Official sports organization promoting Ball Badminton in Telangana State, India",
    "address": {
      "@type": "PostalAddress",
      "addressLocality": "Hyderabad",
      "addressRegion": "Telangana",
      "addressCountry": "IN"
    },
    "contactPoint": {
      "@type": "ContactPoint",
      "telephone": "+91-40-1234-5678",
      "contactType": "customer service",
      "email": "info@telanganaballbadminton.org",
      "availableLanguage": ["English", "Telugu"]
    },
    "sameAs": [
      "https://facebook.com/telanganaballbadminton",
      "https://twitter.com/tbba_official",
      "https://instagram.com/tbba_official"
    ],
    "sport": "Ball Badminton"
  };
};

/**
 * Generate Person structured data
 */
export const generatePersonSchema = (person: Person, baseUrl: string) => {
  return {
    "@context": "https://schema.org",
    "@type": "Person",
    "name": person.name,
    "jobTitle": person.position,
    "email": person.email,
    "telephone": person.phone,
    "image": person.image,
    "worksFor": {
      "@type": "SportsOrganization",
      "name": "Telangana Ball Badminton Association",
      "url": baseUrl
    }
  };
};

/**
 * Generate SportsEvent structured data for tournaments
 */
export const generateTournamentSchema = (tournament: Tournament, baseUrl: string) => {
  return {
    "@context": "https://schema.org",
    "@type": "SportsEvent",
    "name": tournament.name,
    "description": tournament.description,
    "startDate": tournament.startDate,
    "endDate": tournament.endDate,
    "location": {
      "@type": "Place",
      "name": tournament.venue,
      "address": {
        "@type": "PostalAddress",
        "addressLocality": tournament.district,
        "addressRegion": "Telangana",
        "addressCountry": "IN"
      }
    },
    "organizer": {
      "@type": "SportsOrganization",
      "name": "Telangana Ball Badminton Association",
      "url": baseUrl
    },
    "sport": "Ball Badminton",
    "url": `${baseUrl}/tournaments/${tournament.id}`
  };
};

/**
 * Generate NewsArticle structured data
 */
export const generateNewsArticleSchema = (article: NewsArticle, baseUrl: string) => {
  return {
    "@context": "https://schema.org",
    "@type": "NewsArticle",
    "headline": article.title,
    "articleBody": article.content,
    "author": {
      "@type": "Person",
      "name": article.author
    },
    "publisher": {
      "@type": "Organization",
      "name": "Telangana Ball Badminton Association",
      "logo": {
        "@type": "ImageObject",
        "url": `${baseUrl}/logo512.png`
      }
    },
    "datePublished": article.publishedDate,
    "dateModified": article.modifiedDate || article.publishedDate,
    "image": article.imageUrl || `${baseUrl}/og-image.jpg`,
    "url": `${baseUrl}/news/${article.id}`,
    "articleSection": article.category,
    "inLanguage": "en-IN"
  };
};

/**
 * Generate BreadcrumbList structured data
 */
export const generateBreadcrumbSchema = (breadcrumbs: Array<{ name: string; url: string }>) => {
  return {
    "@context": "https://schema.org",
    "@type": "BreadcrumbList",
    "itemListElement": breadcrumbs.map((crumb, index) => ({
      "@type": "ListItem",
      "position": index + 1,
      "name": crumb.name,
      "item": crumb.url
    }))
  };
};

/**
 * Generate WebSite structured data with search action
 */
export const generateWebSiteSchema = (baseUrl: string) => {
  return {
    "@context": "https://schema.org",
    "@type": "WebSite",
    "name": "Telangana Ball Badminton Association",
    "url": baseUrl,
    "potentialAction": {
      "@type": "SearchAction",
      "target": {
        "@type": "EntryPoint",
        "urlTemplate": `${baseUrl}/search?q={search_term_string}`
      },
      "query-input": "required name=search_term_string"
    }
  };
};
