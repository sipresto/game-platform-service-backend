INSERT INTO games(price, game_id, description, name, service_url, rules, last_registration)
VALUES (1.99, 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc',
        'Tic Tac Toe is a puzzle game for two players, "X" and "O", who take turns marking spaces in a 3×3 grid. The player who succeeds in placing three of their marks in a horizontal, vertical, or diagonal row wins the game.',
        'Tic-Tac-Toe', '/api/games/tictactoe',
        'Two players take turns.
        Players alternate marking empty spaces in the grid with their assigned mark (X or O).
        The first player to get three of their marks in a row (horizontally, vertically, or diagonally) wins the game.
        If all nine spaces are filled and no player has three in a row, the game ends in a tie.', '2024-01-14T19:45:12Z'),
       (6.99, '0de4ce93-1319-48fa-9041-b430d68cedb3',
        'The abstract strategy game Quoridor is surprisingly deep for its simplistic rules. The goal of the game is to get your pawn to the other side of the board.',
        'Quoridor', '/api/games/quoridor',
        'Each player in turn, chooses to move his pawn or to put up one of his fences. When he has run out of fences, the player must move his pawn.
        At the beginning the board is empty. Choose and place your pawn in the center of the first line of your side of the board, your opponent takes another pawn and places it in the center of the first line of his side of the board (the one facing yours). Then take 10 fences each.
        The pawns are moved one square at a time, horizontally or vertically, forwards or backwards, never diagonally.
        The pawns must bypass the fences. If, while you move, you face your opponent''s pawn you can jump over.
        The fences must be placed between 2 sets of 2 squares.
        By placing fences, you force your opponent to move around it and increase the number of moves they need to make. But be careful, you are not allowed to lock up to lock up your opponents pawn, it must always be able to reach it''s goal by at least one square.
        When two pawns face each other on neighboring squares which are not separated by a fence, the player whose turn it is can jump the opponent''s pawn (and place himself behind him), thus advancing an extra square.
        If there is a fence behind the said pawn, the player can place his pawn to the left or the right of the other pawn.
        The first player who reaches one of the 9 squares opposite his base line is the winner.', '2024-01-14T19:45:12Z');

INSERT INTO game_categories (game_id, category)
VALUES ('c3c1d2b0-82fa-43a7-b326-0eb96136ecbc', 'NATIVE'),
       ('c3c1d2b0-82fa-43a7-b326-0eb96136ecbc', 'BOARDGAME');

INSERT INTO achievements(achievement_id, name, code, description, required_value, points,
                         counting_strategy, count_trigger, game_id)
VALUES ('937a2c83-aa8d-46c8-8bdb-6fdec5cf5cd6', 'Tic', 'TIC', 'Win your 1st game',
        1, 15, 'INCREMENT', 'WIN_ANY', 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc'),
       ('6c3d6f0a-af60-4874-98dc-168c4bf1a887', 'Tac', 'TAC', 'Win 5 games',
        5, 20, 'INCREMENT', 'WIN_ANY', 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc'),
       ('db0dcb50-ac90-485a-bd44-5e89737b2206', 'Toe', 'TOE', 'Win 10 games',
        10, 30, 'INCREMENT', 'WIN_ANY', 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc'),
       ('82bdcebc-8c5a-4a79-9ce4-5b35b59772f9', 'Getting warmed up', 'WARMED_UP', 'Play 10 games',
        10, 15, 'INCREMENT', 'PLAY_ANY', 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc'),
       ('899451fd-cd48-4d49-8fc3-b60212e756cf', 'Unbeatable', 'UNBEATABLE', 'Win or draw 10 games in a row',
        10, 50, 'STREAK', 'WIN_OR_DRAW_ANY', 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc'),
       ('8edc7479-2dc3-49d0-9aed-7c1a837e0446', 'Beat a bot', 'BEAT_BOT', 'Get a win against the AI',
        1, 20, 'INCREMENT', 'WIN_AI', 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc'),
       ('ee0e5c46-4862-4266-931e-3d30832781f6', 'Pummel a player', 'PUMMEL_PLAYER', 'Get a win against another player',
        1, 20, 'INCREMENT', 'WIN_PLAYER', 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc'),
       ('40d7026f-8f5f-4cda-8dbe-2fb76a08047a', 'X-celent', 'X_CELENT', 'Win one game as X',
        NULL, 10, NULL, NULL, 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc'),
       ('6e692b16-b6e9-4bce-a6ce-d6544f1ad87c', 'O-mazing', 'O_MAZING', 'Win one game as O',
        NULL, 10, NULL, NULL, 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc');

INSERT INTO users(user_id, keycloak_id, username, display_name)
VALUES ('cf0138b7-3272-4d1c-a34d-de88a47fe94c', 'c9d8cc34-6087-4a6f-a6c2-e208fbbc6ed2', 'xanderv', 'XanderV'),
       ('d80e5017-3f16-4659-9c25-f206213c5221', '8c022ef4-e461-44ca-bd6b-5a76729f39c6', 'jurgenp', 'JurgenP'),
       ('29b85f37-c8be-4323-bd88-77e359b0db30', '481aabea-5005-4dd4-8129-65421c7260f3', 'larsv', 'LarsV8'),
       ('6d0cdf67-26c4-4354-a4df-9c4a7575dbb6', '05b10974-d0fe-4679-ac0f-1a9a0b3ce415', 'robbevm', 'Robbe123'),
       ('04518661-62b1-47f4-933d-87c6f85ba5fb', '5041ef49-f46c-4083-993f-10780835bef8', 'johnny', 'JoTe');

INSERT INTO owned_games(user_id, game_id)
VALUES ('cf0138b7-3272-4d1c-a34d-de88a47fe94c', 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc'),
       ('04518661-62b1-47f4-933d-87c6f85ba5fb', 'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc'),
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
        '2024-01-14T20:45:12Z'),

       ('24419a95-5308-4f7b-94df-305cf930d6ad',
        'c3c1d2b0-82fa-43a7-b326-0eb96136ecbc',
        'EASY',
        null,
        'DRAW',
        '2024-01-13T14:22:00Z');

INSERT INTO session_players (session_id, player_id, player_order)
VALUES ('02d0ae5f-9547-4efe-b55f-a2a29c9a3068', 'cf0138b7-3272-4d1c-a34d-de88a47fe94c', 0),
       ('02d0ae5f-9547-4efe-b55f-a2a29c9a3068', '6d0cdf67-26c4-4354-a4df-9c4a7575dbb6', 1),
       ('9233ba67-1cee-4f11-832f-63eb223e20de', 'cf0138b7-3272-4d1c-a34d-de88a47fe94c', 0),
       ('9233ba67-1cee-4f11-832f-63eb223e20de', '6d0cdf67-26c4-4354-a4df-9c4a7575dbb6', 1),
       ('c85f3878-6528-4dca-85c4-8912447bb422', '6d0cdf67-26c4-4354-a4df-9c4a7575dbb6', 0),
       ('6c974240-6e1f-4e47-b39f-83a5f616af21', 'cf0138b7-3272-4d1c-a34d-de88a47fe94c', 0),
       ('24419a95-5308-4f7b-94df-305cf930d6ad', '04518661-62b1-47f4-933d-87c6f85ba5fb', 0);
