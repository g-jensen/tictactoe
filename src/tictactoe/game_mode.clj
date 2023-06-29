(ns tictactoe.game-mode
  (:require [tictactoe.move :as move]))

(defprotocol GameMode
  (initial-board [this])
  (next-board [this board])
  (to-map [this]))

(defrecord PvPGame [size init-board]
  GameMode
  (initial-board [this]
    init-board)
  (next-board [this board]
    (move/play-move board (move/get-user-move board)))
  (to-map [this]
    {:mode :pvp}))

(defrecord PvCGame [size init-board difficulty]
  GameMode
  (initial-board [this]
    init-board)
  (next-board [this board]
    (if (= (move/player-to-move init-board) (move/player-to-move board))
      (move/play-move board (move/get-user-move board))
      (move/play-move board (move/get-computer-move difficulty board))))
  (to-map [this]
    {:mode :pvc
     :difficulty difficulty}))