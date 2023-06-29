(ns tictactoe.database)

(defprotocol Database
  (initialize [this])
  (fetch-all-games [this])
  (update-game [this date board game-mode])
  (delete-game [this date]))