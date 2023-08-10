(ns clj.tictactoe.game-mode)

(defprotocol GameMode
  (initial-board [this])
  (to-map [this]))

(defrecord PvPGame [size init-board]
  GameMode
  (initial-board [this]
    init-board)
  (to-map [this]
    {:mode :pvp}))

(defrecord PvCGame [size init-board difficulty]
  GameMode
  (initial-board [this]
    init-board)
  (to-map [this]
    {:mode :pvc
     :difficulty difficulty}))