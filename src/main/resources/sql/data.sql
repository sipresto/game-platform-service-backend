INSERT INTO games(price, game_id, categories, description, name, service_url)
VALUES (1.99, 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc', 'native, boardgame', 'Tic Tac Toe is een puzzelspel voor twee spelers, "X" en "O", die om de beurt de velden in een 3×3 raster markeren. De speler die erin is geslaagd drie respectieve merktekens in een horizontale, verticale of diagonale rij te plaatsen, wint het spel.', 'Tic-Tac-Toe', 'http://localhost:8096/api/tictactoe');

INSERT INTO users(user_id, keycloak_id, username, display_name)
VALUES
    ('cf0138b7-3272-4d1c-a34d-de88a47fe94c', 'c9d8cc34-6087-4a6f-a6c2-e208fbbc6ed2', 'xanderv', 'XanderV'),
    ('d80e5017-3f16-4659-9c25-f206213c5221', '8c022ef4-e461-44ca-bd6b-5a76729f39c6', 'jurgenp', 'JurgenP'),
    ('29b85f37-c8be-4323-bd88-77e359b0db30', '481aabea-5005-4dd4-8129-65421c7260f3', 'larsv', 'LarsV8'),
    ('6d0cdf67-26c4-4354-a4df-9c4a7575dbb6', '05b10974-d0fe-4679-ac0f-1a9a0b3ce415', 'robbevm', 'Robbe123');

INSERT INTO owned_games(user_id, game_id)
VALUES ('cf0138b7-3272-4d1c-a34d-de88a47fe94c', 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc');
