import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { catchError, forkJoin, map, of, switchMap } from 'rxjs';
import {
  AuthMode,
  CurrentUser,
  DashboardView,
  EventItem,
  PendingRequest,
  Place,
  Review
} from '../models/projectues.models';

@Injectable({ providedIn: 'root' })
export class ProjectuesApiService {
  private readonly http = inject(HttpClient);

  places: Place[] = [];
  events: EventItem[] = [];
  requests: PendingRequest[] = [];
  query = '';
  eventQuery = '';
  eventTypeFilter = '';
  eventPlaceFilter = '';
  eventAddressFilter = '';
  eventMaxPrice = '';
  eventFreeOnly = false;
  eventTodayOnly = false;
  loginMessage = '';
  registerMessage = '';
  placeMessage = '';
  eventMessage = '';
  profileMessage = '';
  authToken = '';
  isAuthenticated = false;
  isAdmin = false;
  login = { email: '', password: '' };
  register = { email: '', username: '', password: '', firstName: '', lastName: '', address: '', city: '', phoneNumber: '' };
  profile = { email: '', username: '', firstName: '', lastName: '', address: '', city: '', phoneNumber: '' };
  passwordChange = { currentPassword: '', newPassword: '', confirmNewPassword: '' };
  newPlace = { name: '', address: '', type: '', description: '', imageUrl: '' };
  newEvent = { name: '', placeId: '', placeName: '', address: '', type: '', eventDate: '', recurring: false, entryPrice: '', freeEntry: false, description: '' };
  uploadFile: File | null = null;
  newReviewText: { [key: number]: string } = {};
  newReviewRating: { [key: number]: number } = {};
  newReviewEventId: { [key: number]: number | '' } = {};
  newReviewPerformance: { [key: number]: number } = {};
  newReviewSoundLight: { [key: number]: number } = {};
  newReviewSpace: { [key: number]: number } = {};
  newReviewOverall: { [key: number]: number } = {};
  newReviewMessage: { [key: number]: string } = {};
  expandedReviews: { [key: number]: boolean } = {};
  placeEvents: { [key: number]: EventItem[] } = {};
  currentUser: CurrentUser = { email: '', username: '', name: '', role: '' };
  currentView: DashboardView = 'overview';
  authMode: AuthMode = 'login';

  init(): void {
    this.restoreSession();
    this.loadPlaces();
    this.loadEvents();
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
        this.profile = {
          email: this.currentUser.email || '',
          username: this.currentUser.username || '',
          firstName: this.currentUser.firstName || '',
          lastName: this.currentUser.lastName || '',
          address: this.currentUser.address || '',
          city: this.currentUser.city || '',
          phoneNumber: this.currentUser.phoneNumber || ''
        };
      } catch {
        this.currentUser.role = '';
      }
    }

    this.isAdmin = this.currentUser.role === 'ADMIN';
    this.currentView = this.isAdmin ? 'overview' : 'places';

    if (this.isAdmin) {
      this.loadRequests();
    }

    this.loadProfile();
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

  loadEvents(): void {
    const params = new URLSearchParams();

    if (this.eventQuery) params.set('query', this.eventQuery);
    if (this.eventTypeFilter) params.set('type', this.eventTypeFilter);
    if (this.eventPlaceFilter) params.set('place', this.eventPlaceFilter);
    if (this.eventAddressFilter) params.set('address', this.eventAddressFilter);
    if (this.eventMaxPrice) params.set('maxPrice', this.eventMaxPrice);
    if (this.eventFreeOnly) params.set('freeOnly', 'true');
    if (this.eventTodayOnly) params.set('todayOnly', 'true');

    const url = params.toString() ? `/api/events?${params.toString()}` : '/api/events';

    this.http.get<EventItem[]>(url).pipe(catchError(() => of([] as EventItem[]))).subscribe({
      next: (events) => (this.events = events),
      error: () => (this.events = [])
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

  loadProfile(): void {
    if (!this.isAuthenticated) {
      return;
    }

    this.http.get<any>('/api/profile', { headers: this.authHeaders() }).subscribe({
      next: (profile) => {
        this.profile = {
          email: profile?.email || this.currentUser.email || '',
          username: profile?.username || this.currentUser.username || '',
          firstName: profile?.firstName || '',
          lastName: profile?.lastName || '',
          address: profile?.address || '',
          city: profile?.city || '',
          phoneNumber: profile?.phoneNumber || ''
        };
        this.currentUser = {
          ...this.currentUser,
          email: this.profile.email,
          username: this.profile.username,
          firstName: this.profile.firstName,
          lastName: this.profile.lastName,
          address: this.profile.address,
          city: this.profile.city,
          phoneNumber: this.profile.phoneNumber,
          name: `${this.profile.firstName || ''} ${this.profile.lastName || ''}`.trim()
        };
        localStorage.setItem('authUser', JSON.stringify(this.currentUser));
      }
    });
  }

  refreshAll(): void {
    this.loadPlaces();
    this.loadEvents();
    if (this.isAdmin) {
      this.loadRequests();
    }
    if (this.isAuthenticated) {
      this.loadProfile();
    }
  }

  doLogin(): void {
    this.http.post('/api/auth/login', this.login).subscribe({
      next: (response: any) => {
        this.authToken = response?.token || '';
        this.currentUser = {
          email: response?.email || this.login.email,
          username: response?.username || '',
          name: response?.name || '',
          role: response?.role || '',
          firstName: response?.firstName || '',
          lastName: response?.lastName || '',
          address: response?.address || '',
          city: response?.city || '',
          phoneNumber: response?.phoneNumber || ''
        };
        this.isAuthenticated = true;
        this.isAdmin = this.currentUser.role === 'ADMIN';
        localStorage.setItem('authToken', this.authToken);
        localStorage.setItem('authUser', JSON.stringify(this.currentUser));
        this.loginMessage = 'Prijava je uspešna.';
        this.currentView = this.isAdmin ? 'overview' : 'places';
        this.loadPlaces();
        this.loadEvents();
        this.loadProfile();

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
      username: this.register.username,
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
        this.resetRegisterForm();
      },
      error: (error) => (this.registerMessage = error?.error?.error || 'Greška pri registraciji')
    });
  }

  selectView(view: DashboardView): void {
    this.currentView = view;
  }

  setAuthMode(mode: AuthMode): void {
    this.authMode = mode;
    this.loginMessage = '';
    this.registerMessage = '';
  }

  createPlace(): void {
    if (!this.isAdmin) {
      return;
    }

    this.http.post('/api/places', this.newPlace, { headers: this.authHeaders() }).subscribe({
      next: () => {
        this.placeMessage = 'Mesto je uspešno dodato.';
        this.resetPlaceForm();
        this.loadPlaces();
      },
      error: (error) => (this.placeMessage = error?.error?.error || 'Greška pri dodavanju mesta')
    });
  }

  createEvent(): void {
    if (!this.isAdmin) {
      return;
    }

    const selectedPlace = this.places.find((place) => place.id === Number(this.newEvent.placeId));

    const payload = {
      ...this.newEvent,
      placeId: this.newEvent.placeId ? Number(this.newEvent.placeId) : null,
      placeName: selectedPlace?.name || this.newEvent.placeName,
      address: selectedPlace?.address || this.newEvent.address,
      entryPrice: this.newEvent.freeEntry ? 0 : this.newEvent.entryPrice === '' ? null : Number(this.newEvent.entryPrice)
    };

    this.http.post('/api/events', payload, { headers: this.authHeaders() }).subscribe({
      next: () => {
        this.eventMessage = 'Događaj je uspešno dodat.';
        this.resetEventForm();
        this.loadEvents();
      },
      error: (error) => (this.eventMessage = error?.error?.error || 'Greška pri dodavanju događaja')
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
        this.loadEvents();
      }
    });
  }

  saveProfile(): void {
    if (!this.isAuthenticated) {
      return;
    }

    this.http.put('/api/profile', this.profile, { headers: this.authHeaders() }).subscribe({
      next: (response: any) => {
        this.profileMessage = 'Profil je uspešno ažuriran.';
        this.currentUser = {
          ...this.currentUser,
          email: response?.email || this.profile.email,
          username: response?.username || this.profile.username,
          name: response?.name || `${this.profile.firstName || ''} ${this.profile.lastName || ''}`.trim(),
          firstName: response?.firstName || this.profile.firstName,
          lastName: response?.lastName || this.profile.lastName,
          address: response?.address || this.profile.address,
          city: response?.city || this.profile.city,
          phoneNumber: response?.phoneNumber || this.profile.phoneNumber
        };
        localStorage.setItem('authUser', JSON.stringify(this.currentUser));
        this.loadProfile();
      },
      error: (error) => (this.profileMessage = error?.error?.error || 'Greška pri izmeni profila')
    });
  }

  changePassword(): void {
    if (!this.isAuthenticated) {
      return;
    }

    this.http.post('/api/profile/password', this.passwordChange, { headers: this.authHeaders() }).subscribe({
      next: () => {
        this.profileMessage = 'Lozinka je uspešno promenjena.';
        this.passwordChange = { currentPassword: '', newPassword: '', confirmNewPassword: '' };
      },
      error: (error) => (this.profileMessage = error?.error?.error || 'Greška pri promeni lozinke')
    });
  }

  addReview(placeId: number): void {
    if (!this.isAuthenticated) {
      return;
    }

    const payload = {
      eventId: this.newReviewEventId[placeId] || null,
      overallRating: this.newReviewOverall[placeId] || this.newReviewRating[placeId] || 10,
      performanceRating: this.newReviewPerformance[placeId] || null,
      soundLightRating: this.newReviewSoundLight[placeId] || null,
      spaceRating: this.newReviewSpace[placeId] || null,
      text: this.newReviewText[placeId] || ''
    };

    if (!payload.eventId) {
      this.newReviewMessage[placeId] = 'Izaberi događaj pre slanja.';
      return;
    }

    this.http.post(`/api/reviews/place/${placeId}`, payload, { headers: this.authHeaders() }).subscribe({
      next: () => {
        this.newReviewText[placeId] = '';
        this.newReviewRating[placeId] = 0;
        this.newReviewEventId[placeId] = '';
        this.newReviewMessage[placeId] = '';
        this.newReviewPerformance[placeId] = 0;
        this.newReviewSoundLight[placeId] = 0;
        this.newReviewSpace[placeId] = 0;
        this.newReviewOverall[placeId] = 0;
        this.newReviewMessage[placeId] = '';
        this.loadPlaces();
        this.loadPlaceEvents(placeId);
      }
    });
  }

  loadPlaceEvents(placeId: number): void {
    this.http.get<EventItem[]>(`/api/events/place/${placeId}`, { headers: this.authHeaders() }).pipe(catchError(() => of([] as EventItem[]))).subscribe({
      next: (events) => (this.placeEvents[placeId] = events),
      error: () => (this.placeEvents[placeId] = [])
    });
  }

  toggleReviews(placeId: number): void {
    this.expandedReviews[placeId] = !this.expandedReviews[placeId];
    if (this.expandedReviews[placeId] && !this.placeEvents[placeId]) {
      this.loadPlaceEvents(placeId);
    }
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
    this.events = [];
    this.requests = [];
    this.login = { email: '', password: '' };
    this.register = { email: '', username: '', password: '', firstName: '', lastName: '', address: '', city: '', phoneNumber: '' };
    this.profile = { email: '', username: '', firstName: '', lastName: '', address: '', city: '', phoneNumber: '' };
    this.passwordChange = { currentPassword: '', newPassword: '', confirmNewPassword: '' };
    this.newPlace = { name: '', address: '', type: '', description: '', imageUrl: '' };
    this.newEvent = { name: '', placeId: '', placeName: '', address: '', type: '', eventDate: '', recurring: false, entryPrice: '', freeEntry: false, description: '' };
    this.newReviewText = {};
    this.newReviewRating = {};
    this.newReviewEventId = {};
    this.newReviewPerformance = {};
    this.newReviewSoundLight = {};
    this.newReviewSpace = {};
    this.newReviewOverall = {};
    this.newReviewMessage = {};
    this.expandedReviews = {};
    this.placeEvents = {};
    this.currentUser = { email: '', username: '', name: '', role: '' };
    this.currentView = 'overview';
    this.authMode = 'login';
    this.loginMessage = '';
    this.registerMessage = '';
    this.placeMessage = '';
    this.eventMessage = '';
    this.profileMessage = '';
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

    const rated = reviews.map(r => (r.overallRating ?? r.rating ?? null)).filter(v => v !== null) as number[];
    if (!rated.length) return 0;
    const total = rated.reduce((sum, v) => sum + v, 0);
    return Number((total / rated.length).toFixed(1));
  }

  private resetRegisterForm(): void {
    this.register = { email: '', username: '', password: '', firstName: '', lastName: '', address: '', city: '', phoneNumber: '' };
  }

  private resetPlaceForm(): void {
    this.newPlace = { name: '', address: '', type: '', description: '', imageUrl: '' };
  }

  private resetEventForm(): void {
    this.newEvent = { name: '', placeId: '', placeName: '', address: '', type: '', eventDate: '', recurring: false, entryPrice: '', freeEntry: false, description: '' };
  }

  getReviewAuthor(review: Review): string {
    const name = review.reviewerName || review.userEmail || 'korisnik';
    const username = review.reviewerUsername ? `@${review.reviewerUsername}` : '';
    return username ? `${name} (${username})` : name;
  }

  getReviewEventLabel(placeId: number, eventId?: number): string {
    if (!eventId) {
      return 'Nije izabran događaj';
    }

    const cachedEvent = this.placeEvents[placeId]?.find((event) => event.id === eventId) || this.events.find((event) => event.id === eventId);
    return cachedEvent ? cachedEvent.name : `Događaj #${eventId}`;
  }
}
