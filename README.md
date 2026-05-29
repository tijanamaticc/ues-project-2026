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

Kako da testiraš 6 i 7:

1. Pokreni backend.

```powershell
cd "c:\Users\tijan\OneDrive\Radna površina\ProjectUES\backend"
mvn spring-boot:run
```

2. Pokreni frontend.

```powershell
cd "c:\Users\tijan\OneDrive\Radna površina\ProjectUES\frontend"
npm install
npm start
```

3. Otvori aplikaciju u browseru i uradi sledeće:

- prijavi se kao admin sa `admin@projectues.rs` / `admin123`
- proveri dashboard i meni
- kao običan korisnik pošalji registraciju i zatim je odobri kao admin
- testiraj pregled mesta, pretragu, dodavanje utiska i dodavanje novog mesta

Ako želiš samo da proveriš build bez pokretanja, koristi:

```powershell
cd "c:\Users\tijan\OneDrive\Radna površina\ProjectUES\backend"
mvn -q -DskipTests compile

cd "c:\Users\tijan\OneDrive\Radna površina\ProjectUES\frontend"
npm run build
```
