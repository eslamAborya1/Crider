# 🚗 CRidr – Smart Car Road Assistant  

## 📌 Overview  
**CRidr** is a smart road assistance system for drivers.  

- A **Customer** can create a **service request** (e.g., flat tire, battery issues, or any car breakdown).  
- A **Provider** (mechanic, towing service, etc.) can view nearby pending requests and accept one.  
- Once a provider accepts, the request goes into a **“Waiting for Customer Confirmation”** state.  
- The **Customer** then confirms or rejects the provider.  
- After confirmation, the provider’s **real-time location** is shared with the customer until the service is completed.  
- The system automatically calculates **distance, pricing, and estimated arrival time (ETA)** using geo-location utilities.  

---

## ⚙️ Features  
- 🧑‍💻 **Authentication & Authorization**  
  - Secure login/signup using **JWT (JSON Web Tokens)**.  
  - Role-based access control: `CUSTOMER` and `PROVIDER`.  

- 📍 **Service Requests**  
  - Customers can create new requests.  
  - Providers can view all **pending** requests.  
  - Providers accept requests → moves to **waiting for customer confirmation**.  
  - Customers confirm/reject the assigned provider.  

- 📡 **Real-time Updates (WebSockets)**   (In progress)
  - Providers send live location updates.  
  - Customers subscribed to a request receive location updates instantly.  

- 💰 **Dynamic Pricing & ETA**  
  - Base price calculated from the issue type.  
  - Distance-based pricing using **Haversine formula**.  
  - Automatic ETA estimation based on provider location and average speed.  

---

## 🛠️ Tech Stack  
- **Backend:** Spring Boot (Java)  
- **Database:** MySQL (with JPA/Hibernate)  
- **Security:** Spring Security + JWT  
- **Real-time:** WebSocket (Spring)  
- **Utilities:**  
  - `GeoUtils` → calculate distance (Haversine formula).  
  - `PricingUtils` → base price calculation per issue type.  

---


---

## 🚀 How It Works (Flow)  
1. **Customer creates a request** → status: `PENDING`.  
2. **Providers see pending requests** → one accepts → status: `WAITING_FOR_CUSTOMER`.  
3. **Customer confirms/rejects**:  
   - If confirm → status: `ACCEPTED`.  
   - If reject → request goes back to `PENDING`.  
4. **Provider sends location updates via WebSocket** → Customer receives them in real time.  
5. **Provider updates status** (e.g., `ON_THE_WAY`, `COMPLETED`).  

---

## ✅ Example API Endpoints  
- `POST /auth/signup` → Register a new user.  
- `POST /auth/login` → Authenticate and get JWT.  
- `POST /requests` → Customer creates a request.  
- `GET /requests/pending` → Provider views available requests.  
- `POST /requests/{id}/accept` → Provider accepts a request.  
- `POST /requests/{id}/confirm?accept=true` → Customer confirms provider.  
- `PATCH /requests/{id}/status?status=COMPLETED` → Provider updates status.
  
🔥 **CRidr – Because help should always be a few taps away!** 
