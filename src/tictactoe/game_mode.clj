(ns tictactoe.game-mode
  (:require [tictactoe.move :as move]
            [tictactoe.utils :as utils]))

(defprotocol GameMode
  (initial-board [this])
  (next-board [this board]))

(defrecord PvPGame []
  GameMode
  (initial-board [this]
    utils/empty-board)
  (next-board [this board]
    (move/play-move board (move/get-user-move))))

(defrecord PvCGame [player-character difficulty]
  GameMode
  (initial-board [this]
    (if (= player-character \x)
      utils/empty-board
      (move/play-move utils/empty-board (move/get-computer-move difficulty utils/empty-board))))
  (next-board [this board]
    (if (= player-character (move/player-to-move board))
      (move/play-move board (move/get-user-move))
      (move/play-move board (move/get-computer-move difficulty board)))))