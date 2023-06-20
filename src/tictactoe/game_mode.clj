(ns tictactoe.game-mode
  (:require [tictactoe.move :as move]
            [tictactoe.utils :as utils]))

(defprotocol GameMode
  (initial-board [this])
  (next-board [this board]))

(defrecord PvPGame [size]
  GameMode
  (initial-board [this]
    (utils/empty-board size))
  (next-board [this board]
    (move/play-move board (move/get-user-move board))))

(defrecord PvCGame [size player-character difficulty]
  GameMode
  (initial-board [this]
    (if (= player-character \x)
      (utils/empty-board size)
      (move/play-move (utils/empty-board size) (move/get-computer-move difficulty (utils/empty-board size)))))
  (next-board [this board]
    (if (= player-character (move/player-to-move board))
      (move/play-move board (move/get-user-move board))
      (move/play-move board (move/get-computer-move difficulty board)))))