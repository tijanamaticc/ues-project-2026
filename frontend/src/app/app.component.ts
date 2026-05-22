import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface Place {
  id: number;
  name: string;
  address: string;
  type?: string;
  description?: string;
  imageUrl?: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="shell">
      <header class="hero">
        <p class="eyebrow">Projekt UES 2026</p>
        <h1>Novi Sad</h1>
        <p class="lead">Angular klijent za prijavu, registraciju i pregled mesta.</p>
      </header>

      <section class="grid">
        <article class="card auth">
          <h2>Prijava</h2>
          <input [(ngModel)]="login.email" placeholder="Email" />
          <input [(ngModel)]="login.password" type="password" placeholder="Lozinka" />
          <button (click)="doLogin()">Prijavi se</button>
          <p class="status">{{ loginMessage }}</p>
        </article>

        <article class="card auth">
          <h2>Registracija</h2>
          <input [(ngModel)]="register.email" placeholder="Email" />
          <input [(ngModel)]="register.password" type="password" placeholder="Lozinka" />
          <input [(ngModel)]="register.name" placeholder="Ime i prezime" />
          <input [(ngModel)]="register.address" placeholder="Adresa" />
          <input [(ngModel)]="register.city" placeholder="Grad" />
          <button (click)="doRegister()">Pošalji zahtev</button>
          <p class="status">{{ registerMessage }}</p>
        </article>

        <article class="card places">
          <div class="row">
            <h2>Mesta</h2>
            <button (click)="loadPlaces()">Osveži</button>
          </div>
          <div class="search-row">
            <input [(ngModel)]="query" placeholder="Pretraga po nazivu, adresi ili tipu" />
            <button (click)="loadPlaces()">Traži</button>
          </div>
          <div class="place-form">
            <input [(ngModel)]="newPlace.name" placeholder="Naziv" />
            <input [(ngModel)]="newPlace.address" placeholder="Adresa" />
            <input [(ngModel)]="newPlace.type" placeholder="Tip" />
            <textarea [(ngModel)]="newPlace.description" placeholder="Opis"></textarea>
            <input [(ngModel)]="newPlace.imageUrl" placeholder="URL slike" />
            <button (click)="createPlace()">Dodaj mesto</button>
          </div>
          <div class="list" *ngIf="places.length; else empty">
            <div class="place" *ngFor="let place of places">
              <h3>{{ place.name }}</h3>
              <p>{{ place.address }}</p>
              <small>{{ place.type }}</small>
            </div>
          </div>
          <ng-template #empty>
            <p>Nema unetih mesta.</p>
          </ng-template>
        </article>

        <article class="card requests">
          <div class="row">
            <h2>Zahtevi za registraciju</h2>
            <button (click)="loadRequests()">Osveži</button>
          </div>
          <div class="list" *ngIf="requests.length; else noRequests">
            <div class="place" *ngFor="let request of requests">
              <h3>{{ request.email }}</h3>
              <p>{{ request.name }} - {{ request.city }}</p>
              <button (click)="approveRequest(request.id)">Odobri</button>
            </div>
          </div>
          <ng-template #noRequests>
            <p>Nema novih zahteva.</p>
          </ng-template>
        </article>
      </section>
    </div>
  `,
  styles: [`
    .shell { padding: 32px; max-width: 1280px; margin: 0 auto; }
    .hero { margin-bottom: 28px; }
    .eyebrow { text-transform: uppercase; letter-spacing: .18em; color: #8db4ff; font-size: 12px; }
    h1 { font-size: 48px; margin: 0; }
    .lead { color: #b7c1d1; max-width: 680px; }
    .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 18px; }
    .card { background: rgba(255,255,255,.06); border: 1px solid rgba(255,255,255,.09); border-radius: 20px; padding: 20px; backdrop-filter: blur(12px); }
    .auth, .places { display: flex; flex-direction: column; gap: 12px; }
    input, textarea, button { border-radius: 12px; border: 1px solid rgba(255,255,255,.15); background: #0f1520; color: #eef2f7; padding: 12px 14px; font: inherit; }
    textarea { min-height: 100px; resize: vertical; }
    button { background: linear-gradient(135deg, #4f83ff, #69c7ff); color: #08111d; font-weight: 700; cursor: pointer; }
    .row { display: flex; justify-content: space-between; align-items: center; }
    .search-row { display: grid; grid-template-columns: 1fr auto; gap: 10px; }
    .place-form { display: grid; gap: 10px; margin: 6px 0 12px; }
    .list { display: grid; gap: 10px; }
    .place { padding: 14px; border-radius: 16px; background: rgba(255,255,255,.04); }
    .status { min-height: 20px; color: #b8f3c8; }
  `]
})
export class AppComponent implements OnInit {
  places: Place[] = [];
  requests: any[] = [];
  query = '';
  loginMessage = '';
  registerMessage = '';

  login = { email: '', password: '' };
  register = { email: '', password: '', name: '', address: '', city: '' };
  newPlace = { name: '', address: '', type: '', description: '', imageUrl: '' };

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadPlaces();
    this.loadRequests();
  }

  loadPlaces(): void {
    const url = this.query ? `/api/places?query=${encodeURIComponent(this.query)}` : '/api/places';
    this.http.get<Place[]>(url).subscribe({
      next: (places) => (this.places = places),
      error: () => (this.places = [])
    });
  }

  loadRequests(): void {
    this.http.get<any[]>('/api/admin/requests').subscribe({
      next: (requests) => (this.requests = requests),
      error: () => (this.requests = [])
    });
  }

  doLogin(): void {
    this.http.post('/api/auth/login', this.login).subscribe({
      next: () => (this.loginMessage = 'Prijava je uspešna.'),
      error: (error) => (this.loginMessage = error?.error?.error || 'Greška pri prijavi')
    });
  }

  doRegister(): void {
    this.http.post('/api/auth/register', this.register).subscribe({
      next: () => (this.registerMessage = 'Zahtev za registraciju je poslat administratoru.'),
      error: (error) => (this.registerMessage = error?.error?.error || 'Greška pri registraciji')
    });
  }

  createPlace(): void {
    this.http.post('/api/places', this.newPlace).subscribe({
      next: () => {
        this.newPlace = { name: '', address: '', type: '', description: '', imageUrl: '' };
        this.loadPlaces();
      }
    });
  }

  approveRequest(id: number): void {
    this.http.post(`/api/admin/requests/${id}/approve`, {}).subscribe({
      next: () => this.loadRequests()
    });
  }
}
