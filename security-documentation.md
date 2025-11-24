# Security Setup Documentation for Game Platform Realm

This document describes the setup of security configurations for the **Game Platform** realm in Keycloak.

---

## 1. Manage Realms

- **Create Realm**
    - Realm Name: `game_platform`

---

## 2. Configure Clients

### 2.1 game platform client
- **Client ID**: `game-platform`
- **Protocol**: OpenID Connect
- **Authentication**: No client authentication, only **Standard Flow**
- **Valid Redirect URIs**:
    - `http://localhost/*` (local development only)
- **Valid Post Logout Redirect URIs**:
    - `http://localhost/*`
- **Web Origins**:
    - `http://localhost`

---

## 3. Realm Roles

Create the following realm roles:
- `player`
- `admin`
- `game-developer`

---

## 4. Realm Settings

- Navigate to **Realm Settings → User Registration → Default Roles**
- Assign default role: `player`

---

## 5. User Management

- **Create New User**
    - Username: `johnny`
    - Password: `Test123`
    - Email: johnny.test@test.com
    - First name: Johnny
    - Last name: The tester
    - Roles Assigned: `player`, `admin`, `game-developer`
    - When you made this account, go to users, edit account and remove all required user actions. Put email verified on the on toggle.
---

## Important Notes 
- When making a new endpoint, remember that everything default requires Authenticated. If you want your endpoint to be exempt from this, you have to edit the SecurityConfig.java and in the securityFilterChain method you add this for example: .requestMatchers("/api/games").permitAll() Extra note: don't forget to change the url to your url
