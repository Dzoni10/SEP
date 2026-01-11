# 💬 RAZGOVOR - ŠTA MOŽEŠ DA URADIŠ DOK ČEKAŠ FRONTEND

## 🎯 SITUACIJA

- ✅ Backend za kartično plaćanje - **GOTOVO** (Bank servis, CardPaymentPlugin)
- ⏳ Frontend forma - **ČEKAŠ DRUGA**
- ⚠️ Parametri iz Tabele 1 - **DELIMIČNO**

---

## 💡 ŠTA MOŽEŠ DA URADIŠ DOK ČEKAŠ

### **1. Parametri iz Tabele 1** (1h) ⭐ DOBRA IDEJA

**Trenutno:**
- Web Shop šalje: `orderId`, `amount`, `currency`, `paymentMethod`, `callbackUrl`
- Nedostaje: `MERCHANT_ID`, `MERCHANT_PASSWORD`, `MERCHANT_TIMESTAMP`

**Šta možeš uraditi:**
- Hardkodovati MERCHANT_ID i MERCHANT_PASSWORD za sada
- Dodati MERCHANT_TIMESTAMP u PaymentInitiationRequest
- Ažurirati OrderController da šalje ove parametre

**Zašto je dobro:**
- Kada frontend bude spreman, sve će raditi odjednom
- Nećeš morati da se vraćaš na backend kasnije
- Ispuniš zahteve iz specifikacije

---

### **2. Testiranje backend-a** (1-2h) ⭐ JAKO DOBRA IDEJA

**Možeš testirati bez frontenda:**

#### 2.1. Postman/Insomnia testovi
- Testirati `/api/v1/bank/payment-url` endpoint
- Testirati `/api/v1/bank/process-payment` endpoint
- Testirati `/api/v1/psp/webshop/1/pay` endpoint
- Proveriti da li se transakcije čuvaju

#### 2.2. Unit testovi
- Testirati CardValidator (Lunova formula)
- Testirati BankService logiku
- Testirati CardPaymentPlugin

**Zašto je dobro:**
- Uveriš se da backend radi
- Kada frontend bude spreman, znaš da je problem u frontendu, ne u backend-u
- Profesionalniji pristup

---

### **3. Edge case handling** (1-2h)

**Prema specifikaciji, treba implementirati:**
- Promena iznosa tokom procesa plaćanja
- Mehanizam provere statusa transakcije kada servisi nisu dostupni
- Mehanizam obrade transakcije kada korisnik odustane od plaćanja
- Zaštita od dvostrukog plaćanja

**Možeš uraditi:**
- Vremensko ograničenje forme (npr. 15 minuta)
- Provera da li je transakcija već procesirana
- Retry mehanizam za callback

---

### **4. Dokumentacija** (30min)

**Možeš napraviti:**
- API dokumentaciju (Swagger/OpenAPI)
- Opis endpointa
- Primeri zahteva/odgovora

**Zašto je dobro:**
- Drug koji radi frontend će znati kako da poziva API
- Lakše testiranje
- Profesionalniji pristup

---

### **5. Logging i monitoring** (30min)

**Možeš dodati:**
- Logovanje svih transakcija
- Logovanje grešaka
- Health check endpointi

---

## 🤔 KAKO ĆE FUNKCIONISATI KADA FRONTEND BUDE SPREMAN

### **Tok kada frontend bude spreman:**

1. **Korisnik klikne "Plati" na Web Shop-u**
   - Frontend poziva: `POST /api/v1/orders/checkout`
   - Web Shop Backend kreira order
   - Web Shop Backend poziva: `POST /api/v1/psp/webshop/1/pay`
   - PSP vraća `redirectUrl` (npr. `http://localhost:8081/payment/abc123`)

2. **Frontend preusmerava korisnika na PSP**
   - Frontend dobije `redirectUrl` iz odgovora
   - Frontend preusmeri korisnika na taj URL
   - PSP Frontend prikaže formu za unos kartice

3. **Korisnik unosi podatke kartice**
   - Korisnik unosi: PAN, CVV, ime, datum
   - Frontend validira (Lunova formula, datum)
   - Korisnik klikne "Plati"

4. **Frontend šalje podatke na Bank Backend**
   - Frontend poziva: `POST /api/v1/bank/process-payment`
   - Bank Backend validira i procesira
   - Bank Backend vraća rezultat

5. **Frontend prikazuje rezultat**
   - Ako uspešno: prikaže "Plaćanje uspešno"
   - Ako neuspešno: prikaže grešku
   - Automatski pozove Web Shop callback

6. **Web Shop dobije callback**
   - Bank Backend poziva Web Shop callback
   - Web Shop ažurira status ordera

---

## 🎯 PREPORUKE

### **Prioritet 1 (Obavezno):**
1. ✅ **Parametri iz Tabele 1** - dodati MERCHANT_ID, MERCHANT_PASSWORD, MERCHANT_TIMESTAMP
2. ✅ **Testiranje backend-a** - Postman testovi

### **Prioritet 2 (Poželjno):**
3. Edge case handling
4. Dokumentacija API-ja

### **Prioritet 3 (Bonus):**
5. Logging
6. Health check endpointi

---

## 💬 PITANJA ZA RAZGOVOR

1. **Gde će se forma prikazivati?**
   - PSP Frontend ili Financial Frontend?
   - Prema dizajnu, izgleda da je PSP Frontend

2. **Kako će frontend dobiti paymentId?**
   - Iz URL-a (`/payment/:paymentId`)?
   - Ili iz query parametra?

3. **Kako će frontend dobiti iznos?**
   - Iz URL-a?
   - Ili pozivom API-ja sa paymentId?

4. **Gde će se prikazati rezultat?**
   - Na PSP Frontend-u?
   - Ili redirect na Web Shop?

---

## 🚀 PLAN AKCIJE

**Dok čekaš frontend, možeš:**

1. **Dodati parametre iz Tabele 1** (1h)
   - Hardkodovati MERCHANT_ID i MERCHANT_PASSWORD
   - Dodati MERCHANT_TIMESTAMP
   - Ažurirati PaymentInitiationRequest

2. **Testirati backend** (1-2h)
   - Postman testovi
   - Proveriti da li sve radi

3. **Pripremiti dokumentaciju** (30min)
   - Opis endpointa
   - Primeri zahteva/odgovora

**Kada frontend bude spreman:**
- Samo integracija - sve će raditi!

---

## ✅ ZAKLJUČAK

**Backend je gotov!** 🎉

**Dok čekaš frontend, možeš:**
- Dodati parametre iz Tabele 1
- Testirati backend
- Pripremiti dokumentaciju

**Kada frontend bude spreman:**
- Samo integracija i testiranje celokupnog toka

**Sve je spremno za integraciju!** ✅
