# Game Platform Service Backend

## 1. Introduction

The **Game Platform Service Backend** is a Spring Boot–based backend application that acts as the core service layer for a digital board game platform. It is responsible for game registration, achievement tracking, user interactions, security, and event-driven communication between games and the platform.

---

## 2. Architectural Overview

* **Java 21**
* **Keycloak** (Authentication & Authorization)
* **RabbitMQ** (Asynchronous messaging)

---

## 3. Authentication & Security

Authentication and authorization are handled via **Keycloak**.

Key features:

* Single Sign-On (SSO)
* JWT-based authentication
* Role- and scope-based access control

All incoming requests are validated using Keycloak-issued access tokens.
All users must have the **player** role to be able to play games.

---

## 4. Messaging & Event Contracts

The platform relies on RabbitMQ for all asynchronous communication between games and the Game Platform Service Backend. This section defines the **mandatory DTOs** that external games must use when publishing events.

All messages are expected to be serialized as **JSON** and conform exactly to the DTO structures.

---

## 4.1 Game Registration

* **Exchange:** `game-events-exchange`
* **Routing key:** `game.register.*`

Create the following data transfer objects:

### RegisterGameDto

| Field          | Type                           | Required | Description                         |
| -------------- |--------------------------------| -------- |-------------------------------------|
| `id`           | UUID                           | Yes      | Unique identifier of the game       |
| `name`         | String                         | Yes      | Display name                        |
| `description`  | String                         | No       | Short game description              |
| `price`        | BigDecimal                     | No       | Price of the game                   |
| `backendUrl`   | String                         | Yes      | Base URL of the game backend        |
| `frontendUrl`  | String                         | Yes      | URL of the game frontend            |
| `rules`        | String                         | No       | Game rules (max 2000 characters)    |
| `achievements` | List of RegisterAchievementDto | No       | Achievement definitions             |
| `categories`   | List of String (enum)          | No       | Game categories                     |
| `imageUrl`     | String                         | No       | Game image or banner                |
| `timestamp`    | Instant                        | Yes      | Registration timestamp              |
| `maxPlayers`   | int                            | Yes      | Maximum number of supported players |

Possible categories:
- STRATEGY
- BOARDGAME
- CARDGAME
- NATIVE
- EXTERNAL

### RegisterAchievementDto

| Field              | Type    | Required | Description                     |
| ------------------ | ------- | -------- | ------------------------------- |
| `name`             | String  | Yes      | Display name                    |
| `code`             | String  | Yes      | Unique achievement identifier   |
| `description`      | String  | Yes      | Description shown to players    |
| `requiredValue`    | Integer | No       | Completion threshold            |
| `points`           | Integer | No       | Reward points (defaults to 10)  |
| `countingStrategy` | String  | No       | Strategy for counting progress  |
| `countTrigger`     | String  | No       | Trigger for evaluating progress |

Counting Strategies - how to count progress towards achievements:
- INCREMENT - Player progress is incremented by 1 each time the trigger is met
- STREAK - Player progress is reset to 0 after the trigger is not met

Count Triggers - available triggers for automatic achievement tracking:
- WIN_PLAYER - Player wins against another human player
- WIN_AI - Player wins against AI
- WIN_ANY - Player wins (any opponent type)
- DRAW_PLAYER - Draw against human player
- DRAW_AI - Draw against AI
- DRAW_ANY - Draw (any opponent type)
- WIN_OR_DRAW_ANY - Player wins or draws
- PLAY_ANY - Game is played (completion)

### Example

```java
public static RegisterGameDto create() {
    List<RegisterAchievementDto> achievements = List.of(
        // Incremental achievements
        new RegisterAchievementDto(
                "First Victory",          // Display name
                "FIRST_WIN",              // Unique code
                "Win your first game",    // Description
                1,                        // Required value (how many times)
                10,                       // Points awarded
                "INCREMENT",              // Counting strategy
                "WIN_ANY"                 // Count trigger
        ),
        
        // Streak achievements
        new RegisterAchievementDto(
                "Winning Streak",
                "WIN_STREAK_5",
                "Win 5 games in a row",
                5,                        // Required streak length
                50,                       // Points awarded
                "STREAK",                 // Counting strategy
                "WIN_PLAYER"              // Count trigger
        ),
        
        // Manual/condition achievements
        new RegisterAchievementDto(
                "Perfect Game",
                "PERFECT",
                "Win without losing any pieces",
                null,                     // No required value (manual trigger)
                25,                       // Points awarded
                null,                     // No counting strategy
                null                      // No count trigger
        )
    );

    return new RegisterGameDto(
        UUID.fromString("your-game-uuid"),
        "Your Game Name",
        "An exciting description of your game",
        BigDecimal.valueOf(9.99),
        "https://your-backend.com/game",
        "https://your-frontend.com/game",
        "Detailed game rules...",
        achievements,
        List.of("EXTERNAL", "STRATEGY"),
        "https://your-cdn.com/game-image.png",
        Instant.now(),
        4
    );
}
```

---

## 4.2 Game Start

To start a game the controllermethod 'createGame' is called on the backend of your game.
It has a GameStartRequestDto as RequestBody.

### GameStartRequestDto

| Field              | Type                      | Required | Description                     |
|--------------------|---------------------------| -------- |---------------------------------|
| `game`             | UUID                      | Yes      | the id of the match             |
| `players`          | List of PlatformPlayerDto | Yes      | Minimum size of 1               |
| `difficulty`       | ENUM                      | Yes      | Look beneath to see the options |

Difficulty - how difficult the bot is, this gets sent even if the game is not played against bots.
- EASY
- HARD
- IMPOSSIBLE

### PlatformPlayerDto

| Field         | Type   | Required | Description     |
|---------------|--------| -------- |-----------------|
| `keycloakId`  | String | Yes      | id for keycloak |
| `displayName` | String | Yes      | Display name    |

### Example

```java
public static GameStartRequestDto create() {
    List<GameStartRequestDto.PlatformPlayerDto> players = List.of(
        new GameStartRequestDto.PlatformPlayerDto(
                "bac29c30-3002-439c-ad29-fe4d6f4a5b48",
                "Player 1"
        ),
        new GameStartRequestDto.PlatformPlayerDto(
                "8b3a8af1-ea1b-4bda-a66d-05f7dd1df5ab",
                "player2"
        )
    );

    return new GameStartRequestDto(
        "34a981ba-aa91-4829-b6e7-89dec1cd64f0",
        players,
        "EASY"
    );
}
```

---

## 4.3 Achievement completion

### Platform evaluated achievements

Increment and streak achievements are calculated by the platform automatically after game results have been sent.

### Achievement Progress

As seen in 4.1 it is also possible to define achievements that are evaluated manually.

To award these, a message can be sent to the platform when it has been reached by a player.

* **Exchange:** `game-events-exchange`
* **Routing key:** `achievement.completed.*`

Create the following data transfer object:

### AchievementCompletedDto
| Field       | Type    | Required | Description                               |
|-------------|---------| -------- |-------------------------------------------|
| `userId`    | String  | Yes      | Unique identifier of player               |
| `gameId`    | UUID    | Yes      | Unique identifier of the game             |
| `code`      | String  | Yes      | Unique achievement identifier             |
| `timestamp` | Instant | Yes      | Timestamp of when achievement was awarded |

---

## 4.4 Game Results, Rematch & Match Tracking and Notifications

When a match concludes, the game publishes a game result event.
This result will be visible on the session page of the website.

### Event Characteristics

* Tracks match outcome
* Associates results with players
* Triggers achievement evaluation and statistics updates

### Game Result Event

Sent when a match concludes.

* **Exchange:** `game-events-exchange`
* **Routing key:** `game.result.*`

#### GameResultMessageDto

| Field         | Type           | Required | Description                                     |
|---------------|----------------| -------- |-------------------------------------------------|
| `gameId`      | UUID           | Yes      | Identifier of the game                          |
| `playerIds`   | List of String | Yes      | Participating players                           |
| `winnerId`    | String         | No       | Winner of the match (null for draw)             |
| `status`      | String (enum)  | Yes      | Match result status (e.g. PLAYER_WIN, DRAW)     |
| `completedAt` | Instant        | Yes      | When the session came to an end                 |
| `endOfGame`   | Boolean        | Yes      | If the game has concluded (no rematch accepted) |

Status - game results:
- PLAYER_WIN
- DRAW
- AI_WIN

#### Example

```java
SessionEndDto example = new SessionEndDto(
    UUID.fromString("8496c496-a884-48ed-9bb3-7c3aa50fb8ca"),
    List.of(
        UUID.fromString("c7a40e75-46a9-42bd-a783-77d14cfdc3bd"),
        UUID.fromString("9829a95b-399f-44e4-a5f1-022eec59a3cb")
    ),
    UUID.fromString("c7a40e75-46a9-42bd-a783-77d14cfdc3bd"),
    "PLAYER_WIN",
    Instant.parse("2026-01-05T08:30:35.871185Z"),
    true
);
```

---

### Game Rematch Event

* **Exchange:** `game-events-exchange`
* **Routing key:** `game.rematch.*`

The Game Rematch Event handles the creation of a new game session when players choose to rematch after completing a game.
This event maintains the same game configuration (players, game type, difficulty) while creating a fresh session.

#### GameResultMessageDto

| Field         | Type | Required | Description                                                                    |
|---------------|------| -------- |--------------------------------------------------------------------------------|
| `oldGameId`   | UUID | Yes      | Identifier of the completed game session                                       |
| `newGameId`   | UUID | Yes      | Identifier for the new rematch game session (UUID to be generated by the game) |

---

### Real-Time Notification Event

Used to notify players when it is their turn.

* **Exchange:** `turn-notifications-exchange`
* **Routing key:** `turn.notification.*"`

#### NotificationDto

| Field            | Type                     | Required | Description                                        |
|------------------|--------------------------| -------- |----------------------------------------------------|
| `userId`         | UUID                     | Yes      | Identifier of the user whose turn it is            |
| `description`    | UUID                     | Yes      | Message describing the turn notification           |