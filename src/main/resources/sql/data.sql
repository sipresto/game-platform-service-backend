INSERT INTO games(price, game_id, categories, description, name, service_url)
VALUES (1.99, 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc', 'native, boardgame', 'Tic Tac Toe is een puzzelspel voor twee spelers, "X" en "O", die om de beurt de velden in een 3×3 raster markeren. De speler die erin is geslaagd drie respectieve merktekens in een horizontale, verticale of diagonale rij te plaatsen, wint het spel.', 'Tic-Tac-Toe', '/api/games/tictactoe');

INSERT INTO users(user_id, username)
VALUES
    ('c9d8cc34-6087-4a6f-a6c2-e208fbbc6ed2', 'XanderV'),
    ('8c022ef4-e461-44ca-bd6b-5a76729f39c6', 'JurgenP');

INSERT INTO owned_games(user_id, game_id)
VALUES ('c9d8cc34-6087-4a6f-a6c2-e208fbbc6ed2', 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc');
