# 🚀 CLASS System - Startup & Troubleshooting Guide

## Prerequisites Checklist

### Backend Requirements
- [ ] Java 21+ installed
- [ ] PostgreSQL running on localhost:5432
- [ ] Database "CLASS" created with user "postgres/sigma"

### Frontend Requirements
- [ ] Node.js 18+ installed
- [ ] npm installed

---

## ✅ QUICK START (2 Steps)

### Step 1: Start PostgreSQL (if not running)

**Windows (PostgreSQL via Administrator):**
```bash
# Open Services (services.msc) or use command:
net start postgresql-x64-15  # Replace version with yours
```

**Or via Command Line:**
```bash
"C:\Program Files\PostgreSQL\15\bin\pg_ctl.exe" -D "C:\Program Files\PostgreSQL\15\data" start
```

### Step 2A: Create Database (One-time setup)

**Connect to PostgreSQL:**
```bash
psql -U postgres
```

**Inside psql, run:**
```sql
CREATE DATABASE "CLASS";
\q
```

---

## 🔧 Start the System

### Terminal 1: Start Backend

```bash
cd "c:\Users\canilloPC\Documents\Spring_Boot\CLASS\sigma-squad\class\app\backend"

# Option 1: With bootRun (auto-creates tables if DB exists)
.\gradlew bootRun

# Option 2: If bootRun fails, use bootJar
.\gradlew build
java -jar build/libs/computify-0.0.1-SNAPSHOT.jar

# Option 3: Run tests first to ensure setup
.\gradlew test
.\gradlew bootRun
```

**Expected Output:**
```
...
Started ComputifyApplication in X.XXX seconds (process running for Y.YYY)
INFO : [] Tomcat started on port(s): 8080 (http)
```

---

### Terminal 2: Start Frontend

```bash
cd "c:\Users\canilloPC\Documents\Spring_Boot\CLASS\sigma-squad\class\app\ui"

# Install dependencies (first time only)
npm install

# Start dev server
npm run dev
```

**Expected Output:**
```
VITE v8.0.12  ready in XXX ms

➜  Local:   http://localhost:5173/
➜  press h + enter to show help
```

---

## 🐛 Troubleshooting

### Problem 1: PostgreSQL Connection Failed

**Error Message:**
```
Connection refused: connect
org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```

**Solutions:**
```bash
# Check if PostgreSQL is running
netstat -an | findstr :5432

# If not running, start it:
# Windows: Services > PostgreSQL > Start
# Or: net start postgresql-x64-15

# Verify credentials
psql -U postgres -h localhost

# Check database exists
psql -U postgres -l | grep CLASS
```

---

### Problem 2: bootRun hangs/freezes

**Likely Cause:** Waiting for database connection

**Solutions:**
```bash
# Try with timeout
.\gradlew bootRun --max-workers=1

# Or skip tests and use bootJar
.\gradlew clean
.\gradlew build -x test
java -jar build/libs/computify-0.0.1-SNAPSHOT.jar

# Or check if port 8080 is already in use
netstat -an | findstr :8080
```

---

### Problem 3: React build errors (npm ERR!)

**Solutions:**
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -r node_modules package-lock.json
npm install

# Then run dev server
npm run dev
```

---

### Problem 4: Cannot login/register

**Check:**
1. Backend is running on http://localhost:8080
2. Frontend is running on http://localhost:5173
3. No CORS errors in browser console
4. Email ends with @students.nu-laguna.edu.ph
5. Student ID format: YYYY-[5-7 digits]

---

## 📊 Architecture

```
Frontend (Vite + React)
     ↓ (Axios)
http://localhost:5173/
  ↓
  └→ Proxy to http://localhost:8080/api
      ↓
Backend (Spring Boot)
    ↓
http://localhost:8080/api
  ↓
  ├→ POST /auth/register
  ├→ POST /auth/login
  ├→ GET /computers
  ├→ POST /reservations
  └→ ... (other endpoints)
      ↓
PostgreSQL
localhost:5432/CLASS
```

---

## ✅ Verification Checklist

### Frontend
- [ ] http://localhost:5173 loads
- [ ] CLASS logo visible
- [ ] Dark mode active
- [ ] Login form visible on right side
- [ ] Features grid visible on left side
- [ ] Form validation works (try invalid email)

### Backend
- [ ] http://localhost:8080 responds
- [ ] POST /api/auth/register works
- [ ] POST /api/auth/login works
- [ ] Database tables created in PostgreSQL

### End-to-End
- [ ] Can register new account
- [ ] Can login with credentials
- [ ] JWT token stored in localStorage
- [ ] Auto-redirect to /dashboard on login

---

## 🔐 Default Credentials (for testing)

After registration via UI:
```
Email: anyname@students.nu-laguna.edu.ph
Student ID: 2025-12345 (format: YYYY-[5-7 digits])
Password: (your choice, min 6 chars)
```

---

## 📝 Important Files

**Backend Config:**
- `src/main/resources/application.properties` - DB connection
- `build.gradle` - Dependencies & version info
- `.env` - Environment variables (create if needed)

**Frontend Config:**
- `.env` - API base URL (`VITE_API_BASE_URL=http://localhost:8080/api`)
- `vite.config.ts` - API proxy settings
- `package.json` - Dependencies

---

## 🆘 Still Stuck?

1. Check logs:
   - Backend: Console output from `./gradlew bootRun`
   - Frontend: Browser DevTools → Console tab
   
2. Verify connections:
   - Database: `psql -U postgres -l`
   - Port 8080: `netstat -an | findstr :8080`
   - Port 5173: `netstat -an | findstr :5173`

3. Try clean builds:
   - Backend: `.\gradlew clean build`
   - Frontend: `npm run build`

---

## 📞 Contact / Support

Refer to:
- `project_requirements.txt` - System requirements
- `context_rules.txt` - Architecture guidelines
- `additional_rules.txt` - Development rules
