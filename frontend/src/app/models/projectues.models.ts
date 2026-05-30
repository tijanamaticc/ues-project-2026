export interface Review {
  id?: number;
  eventId?: number;
  userEmail?: string;
  reviewerUsername?: string;
  reviewerName?: string;
  eventName?: string;
  rating?: number;
  performanceRating?: number;
  soundLightRating?: number;
  spaceRating?: number;
  overallRating?: number;
  text?: string;
  createdAt?: string;
}

export interface Place {
  id: number;
  name: string;
  address: string;
  type?: string;
  description?: string;
  imageUrl?: string;
  reviews?: Review[];
  reviewCount?: number;
  averageRating?: number;
}

export interface EventItem {
  id: number;
  name: string;
  placeId?: number;
  placeName?: string;
  address?: string;
  type?: string;
  eventDate?: string;
  recurring?: boolean;
  entryPrice?: number;
  freeEntry?: boolean;
  description?: string;
}

export interface PendingRequest {
  id: number;
  email: string;
  username?: string;
  firstName?: string;
  lastName?: string;
  city?: string;
  address?: string;
  phoneNumber?: string;
}

export interface CurrentUser {
  email: string;
  username: string;
  name: string;
  role: string;
  firstName?: string;
  lastName?: string;
  address?: string;
  city?: string;
  phoneNumber?: string;
}

export type DashboardView = 'overview' | 'places' | 'events' | 'ues' | 'admin' | 'profile';
export type AuthMode = 'login' | 'register';
