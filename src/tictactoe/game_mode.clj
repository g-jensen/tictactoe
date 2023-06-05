(ns tictactoe.game-mode
  (:require [tictactoe.move :refer :all]))

(defprotocol GameMode
  (next-board [this board]))

(deftype PvPGame []
  GameMode
  (next-board [this board]
    (play-move board (get-user-move))))

(deftype PvCGame []
  GameMode
  (next-board [this board]
    (if (= \x (player-to-move board))
      (play-move board (get-computer-move board))
      (play-move board (get-user-move)))))

(def game-modes [{:name "Versus Player" :mode (->PvPGame)}
                 {:name "Versus Unbeatable Computer" :mode (->PvCGame)}])