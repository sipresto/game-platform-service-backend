# Security Setup Documentation for Game Platform Realm

This document describes the setup of security configurations for the **Game Platform** realm in Keycloak and how secrets are managed.

## Keycloak Management
---

### 1. Manage Realms

- **Create Realm**
    - Realm Name: `game_platform`

---

### 2. Configure Clients

#### 2.1 game platform client
- **Client ID**: `game-platform`
- **Protocol**: OpenID Connect
- **Authentication**: No client authentication, only **Standard Flow**
- **Valid Redirect URIs**:
    - `http://localhost/*` (local development only)
- **Valid Post Logout Redirect URIs**:
    - `http://localhost/*`
- **Web Origins**:
    - `http://localhost`

#### 2.2 platform-backend-service

- **Client ID**: `platform-backend-service`
- **Protocol**: OpenID Connect
- **Authentication**: Client authentication on together with Service accounts roles
- **Service Accounts Roles**:
    - Assign the `service` role created in the next step to this client

---

### 3. Realm Roles

Create the following realm roles:
- `player`
- `admin`
- `game-developer`
- `service`

---

### 4. Realm Settings

- Navigate to **Realm Settings → User Registration → Default Roles**
- Assign default role: `player`
- Navigate too **Realm Settings → Login
- Set forgot password, remember me, Edit username and user registration on 'on'
- Navigate to **Realm Settings → Email
- From: vibegamesinfo@gmail.com
- From display name: Game Platform
- Host: smtp.gmail.com
- Port: 587
- Enable StartTLS: on
- Authentication: on
- Username: vibegamesinfo@gmail.com
- Password: Look into secret.properties for the password
- Click 'Test connection' to verify email settings are correct
---

### 5. User Management

- **Create New User**
    - Username: `johnny`
    - Password: `Test123`
    - Email: johnny.test@test.com
    - First name: Johnny
    - Last name: The tester
    - Roles Assigned: `player`, `admin`, `game-developer`
    - When you made this account, go to users, edit account and remove all required user actions. Put email verified on the on toggle.
---

### Important Notes 
- When making a new endpoint, remember that everything default requires Authenticated. If you want your endpoint to be exempt from this, you have to edit the SecurityConfig.java and in the securityFilterChain method you add this for example: .requestMatchers("/api/games").permitAll() Extra note: don't forget to change the url to your url
- Normally everything should be configured by the docker compose files except for user. This documentation is here for when something goes wrong

---

Secret Management
---
Secrets such as passwords and secret keys are to be put into secret.properties. This file is read by the application at runtime to configure sensitive information.

### Current secret list:
- stripe.secret-key
- app.services.platform.client-secret
- spring.rabbitmq.password
- spring.datasource.password
- spring.mail.host
- spring.mail.port
- spring.mail.username
- spring.mail.password

There can be more secrets added if needed. Check the file regularly for updates.

### Important Notes
- There may be a problem when running this in dev and production where you see an OAUth2 exception. There is a big chance that this is because the client secret wasn't updated. Please make sure that the client secret is correct before running the server!