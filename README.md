# UES Project 2026

Projekat je postavljen u tehnologijama iz specifikacije:

- Backend: Spring Boot
- Frontend: Angular
- Baza: H2 za lokalni razvoj, lako se zamenjuje MySQL konfiguracijom

Pokretanje backend-a:

```powershell
cd "c:\Users\tijan\OneDrive\Radna površina\ProjectUES\backend"
mvn spring-boot:run
```

Pokretanje frontend-a:

```powershell
cd "c:\Users\tijan\OneDrive\Radna površina\ProjectUES\frontend"
npm install
npm start
```

Backend radi na `http://localhost:8080`.
Angular dev server koristi proxy za `/api` ka backend-u.

Početni admin nalog:

- Email: `admin@projectues.rs`
- Lozinka: `admin123`

Trenutno je urađena osnova za ocenu 6:

- prijava i registracija kroz zahtev za odobrenje
- pregled i pretraga mesta
- unos novih mesta
- lista zahteva za registraciju i odobravanje
