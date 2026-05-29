import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { catchError, forkJoin, map, of, switchMap } from 'rxjs';
import {
  AuthMode,
  CurrentUser,
  DashboardView,
  PendingRequest,
  Place,
  Review
} from '../models/projectues.models';

@Injectable({ providedIn: 'root' })
export class ProjectuesApiService {
  private readonly http = inject(HttpClient);

  places: Place[] = [];
  requests: PendingRequest[] = [];
  query = '';
  loginMessage = '';
  registerMessage = '';
  authToken = '';
  isAuthenticated = false;
  isAdmin = false;
  login = { email: '', password: '' };
  register = { email: '', password: '', firstName: '', lastName: '', address: '', city: '', phoneNumber: '' };
  newPlace = { name: '', address: '', type: '', description: '', imageUrl: '' };
  uploadFile: File | null = null;
  newReviewText: { [key: number]: string } = {};
  newReviewRating: { [key: number]: number } = {};
  expandedReviews: { [key: number]: boolean } = {};
  currentUser: CurrentUser = { email: '', name: '', role: '' };
  currentView: DashboardView = 'overview';
  authMode: AuthMode = 'login';

  init(): void {
    this.restoreSession();
    this.loadPlaces();
  }

  get visibleReviewCount(): number {
    return this.places.reduce((total, place) => total + (place.reviewCount || place.reviews?.length || 0), 0);
  }

  get previewPlaces(): Place[] {
    return this.places.slice(0, 3);
  }

  restoreSession(): void {
    const savedToken = localStorage.getItem('authToken');
    const savedUser = localStorage.getItem('authUser');

    if (!savedToken) {
      return;
    }

    this.authToken = savedToken;
    this.isAuthenticated = true;

    if (savedUser) {
      try {
        this.currentUser = { ...this.currentUser, ...JSON.parse(savedUser) };
      } catch {
        this.currentUser.role = '';
      }
    }

    this.isAdmin = this.currentUser.role === 'ADMIN';
    this.currentView = this.isAdmin ? 'overview' : 'places';

    if (this.isAdmin) {
      this.loadRequests();
    }
  }

  loadPlaces(): void {
    const url = this.query ? `/api/places?query=${encodeURIComponent(this.query)}` : '/api/places';

    this.http
      .get<Place[]>(url)
      .pipe(switchMap((places) => this.attachReviews(places)), catchError(() => of([] as Place[])))
      .subscribe({
        next: (places) => (this.places = places),
        error: () => (this.places = [])
      });
  }

  loadRequests(): void {
    if (!this.isAdmin) {
      this.requests = [];
      return;
    }

    this.http.get<PendingRequest[]>('/api/admin/requests', { headers: this.authHeaders() }).subscribe({
      next: (requests) => (this.requests = requests),
      error: () => (this.requests = [])
    });
  }

  refreshAll(): void {
    this.loadPlaces();
    if (this.isAdmin) {
      this.loadRequests();
    }
  }

  onFile(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.uploadFile = input.files?.[0] || null;
  }

  doLogin(): void {
    this.http.post('/api/auth/login', this.login).subscribe({
      next: (response: any) => {
        this.authToken = response?.token || '';
        this.currentUser = {
          email: response?.email || this.login.email,
          name: response?.name || '',
          role: response?.role || ''
        };
        this.isAuthenticated = true;
        this.isAdmin = this.currentUser.role === 'ADMIN';
        localStorage.setItem('authToken', this.authToken);
        localStorage.setItem('authUser', JSON.stringify(this.currentUser));
        this.loginMessage = 'Prijava je uspešna.';
        this.currentView = this.isAdmin ? 'overview' : 'places';
        this.loadPlaces();

        if (this.isAdmin) {
          this.loadRequests();
        }
      },
      error: (error) => (this.loginMessage = error?.error?.error || 'Greška pri prijavi')
    });
  }

  doRegister(): void {
    const payload = {
      email: this.register.email,
      password: this.register.password,
      firstName: this.register.firstName,
      lastName: this.register.lastName,
      address: this.register.address,
      city: this.register.city,
      phoneNumber: this.register.phoneNumber
    };

    this.http.post('/api/auth/register', payload).subscribe({
      next: () => {
        this.registerMessage = 'Zahtev za registraciju je poslat administratoru.';
        this.authMode = 'login';
      },
      error: (error) => (this.registerMessage = error?.error?.error || 'Greška pri registraciji')
    });
  }

  selectView(view: DashboardView): void {
    this.currentView = view;
  }

  setAuthMode(mode: AuthMode): void {
    this.authMode = mode;
  }

  createPlace(): void {
    if (!this.isAdmin) {
      return;
    }

    if (this.uploadFile) {
      const formData = new FormData();
      formData.append('file', this.uploadFile);

      this.http.post<any>('/api/uploads', formData, { headers: this.authHeaders() }).subscribe({
        next: (response) => {
          this.newPlace.imageUrl = response.url;
          this.uploadFile = null;
          this.postPlace();
        }
      });
      return;
    }

    this.postPlace();
  }

  postPlace(): void {
    this.http.post('/api/places', this.newPlace, { headers: this.authHeaders() }).subscribe({
      next: () => {
        this.newPlace = { name: '', address: '', type: '', description: '', imageUrl: '' };
        this.loadPlaces();
      }
    });
  }

  approveRequest(id: number): void {
    if (!this.isAdmin) {
      return;
    }

    this.http.post(`/api/admin/requests/${id}/approve`, {}, { headers: this.authHeaders() }).subscribe({
      next: () => {
        this.loadRequests();
        this.loadPlaces();
      }
    });
  }

  addReview(placeId: number): void {
    if (!this.isAuthenticated) {
      return;
    }

    const payload = {
      userEmail: this.currentUser.email || 'anonymous',
      rating: this.newReviewRating[placeId] || 10,
      text: this.newReviewText[placeId] || ''
    };

    this.http.post(`/api/reviews/place/${placeId}`, payload, { headers: this.authHeaders() }).subscribe({
      next: () => {
        this.newReviewText[placeId] = '';
        this.newReviewRating[placeId] = 0;
        this.loadPlaces();
      }
    });
  }

  toggleReviews(placeId: number): void {
    this.expandedReviews[placeId] = !this.expandedReviews[placeId];
  }

  formatRating(rating?: number): string {
    if (rating === undefined || rating === null || Number.isNaN(rating)) {
      return 'Nema ocene';
    }

    return `${rating.toFixed(1)} / 10`;
  }

  logout(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('authUser');
    this.authToken = '';
    this.isAuthenticated = false;
    this.isAdmin = false;
    this.places = [];
    this.requests = [];
    this.login = { email: '', password: '' };
    this.currentUser = { email: '', name: '', role: '' };
    this.currentView = 'overview';
  }

  private authHeaders(): HttpHeaders {
    return this.authToken ? new HttpHeaders({ Authorization: `Bearer ${this.authToken}` }) : new HttpHeaders();
  }

  private attachReviews(places: Place[]) {
    if (!places.length) {
      return of([] as Place[]);
    }

    return forkJoin(
      places.map((place) =>
        this.http.get<Review[]>(`/api/reviews/place/${place.id}`).pipe(
          catchError(() => of([] as Review[])),
          map((reviews) => ({
            ...place,
            reviews,
            reviewCount: reviews.length,
            averageRating: this.calculateAverageRating(reviews)
          }))
        )
      )
    );
  }

  private calculateAverageRating(reviews: Review[]): number {
    if (!reviews.length) {
      return 0;
    }

    const total = reviews.reduce((sum, review) => sum + (review.rating || 0), 0);
    return Number((total / reviews.length).toFixed(1));
  }
}
