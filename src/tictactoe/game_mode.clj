(ns tictactoe.game-mode
  (:require [tictactoe.move :as move]))

(defprotocol GameMode
  (next-board [this board]))

(deftype PvPGame []
  GameMode
  (next-board [this board]
    (move/play-move board (move/get-user-move))))

(deftype PvCGame []
  GameMode
  (next-board [this board]
    (if (= \x (move/player-to-move board))
      (move/play-move board (move/get-computer-move board))
      (move/play-move board (move/get-user-move)))))

(def game-modes [{:name "Versus Player" :mode (->PvPGame)}
                 {:name "Versus Unbeatable Computer" :mode (->PvCGame)}])