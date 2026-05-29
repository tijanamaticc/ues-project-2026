export interface Review {
  id?: number;
  userEmail?: string;
  rating?: number;
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

export interface PendingRequest {
  id: number;
  email: string;
  firstName?: string;
  lastName?: string;
  city?: string;
  address?: string;
  phoneNumber?: string;
}

export interface CurrentUser {
  email: string;
  name: string;
  role: string;
}

export type DashboardView = 'overview' | 'places' | 'ues' | 'admin' | 'profile';
export type AuthMode = 'login' | 'register';
