INSERT INTO games(price, game_id, categories, description, name, service_url)
VALUES (1.99, 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc', 'native, boardgame',
        'Tic Tac Toe is a puzzle game for two players, "X" and "O", who take turns marking spaces in a 3×3 grid. The player who succeeds in placing three of their marks in a horizontal, vertical, or diagonal row wins the game.',
        'Tic-Tac-Toe', '/api/games/tictactoe');

INSERT INTO users(user_id, keycloak_id, username, display_name)
VALUES ('cf0138b7-3272-4d1c-a34d-de88a47fe94c', 'c9d8cc34-6087-4a6f-a6c2-e208fbbc6ed2', 'xanderv', 'XanderV'),
       ('d80e5017-3f16-4659-9c25-f206213c5221', '8c022ef4-e461-44ca-bd6b-5a76729f39c6', 'jurgenp', 'JurgenP'),
       ('29b85f37-c8be-4323-bd88-77e359b0db30', '481aabea-5005-4dd4-8129-65421c7260f3', 'larsv', 'LarsV8'),
       ('6d0cdf67-26c4-4354-a4df-9c4a7575dbb6', '05b10974-d0fe-4679-ac0f-1a9a0b3ce415', 'robbevm', 'Robbe123');

INSERT INTO owned_games(user_id, game_id)
VALUES ('cf0138b7-3272-4d1c-a34d-de88a47fe94c', 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc'),
       ('6d0cdf67-26c4-4354-a4df-9c4a7575dbb6', 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc');

INSERT INTO sessions (session_id, game_id, difficulty, winner_id, result, completed_at)
VALUES ('02d0ae5f-9547-4efe-b55f-a2a29c9a3068',
        'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc',
        null,
        'cf0138b7-3272-4d1c-a34d-de88a47fe94c',
        'PLAYER_WIN',
        '2024-01-10T10:15:30Z'),

       ('9233ba67-1cee-4f11-832f-63eb223e20de',
        'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc',
        null,
        null,
        'DRAW',
        '2024-01-12T14:22:00Z'),

       ('c85f3878-6528-4dca-85c4-8912447bb422',
        'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc',
        'HARD',
        null,
        'AI_WIN',
        '2024-01-14T19:45:12Z'),

       ('6c974240-6e1f-4e47-b39f-83a5f616af21',
        'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc',
        'HARD',
        null,
        'DRAW',
        '2024-01-14T20:45:12Z');

INSERT INTO session_players (session_id, player_id, player_order)
VALUES ('02d0ae5f-9547-4efe-b55f-a2a29c9a3068', 'cf0138b7-3272-4d1c-a34d-de88a47fe94c', 0),
       ('02d0ae5f-9547-4efe-b55f-a2a29c9a3068', '6d0cdf67-26c4-4354-a4df-9c4a7575dbb6', 1),
       ('9233ba67-1cee-4f11-832f-63eb223e20de', 'cf0138b7-3272-4d1c-a34d-de88a47fe94c', 0),
       ('9233ba67-1cee-4f11-832f-63eb223e20de', '6d0cdf67-26c4-4354-a4df-9c4a7575dbb6', 1),
       ('c85f3878-6528-4dca-85c4-8912447bb422', '6d0cdf67-26c4-4354-a4df-9c4a7575dbb6', 0),
       ('6c974240-6e1f-4e47-b39f-83a5f616af21', 'cf0138b7-3272-4d1c-a34d-de88a47fe94c', 0);
