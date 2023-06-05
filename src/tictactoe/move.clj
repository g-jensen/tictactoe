(ns tictactoe.move
  (:require [tictactoe.board :refer :all]))

;;TODO - extract from game-engine

(defn- in-range? [start end n]
  (and (>= n start) (< n end)))

(defn- tile-count [board tile]
  (count (filter #(= tile %) board)))

(defn player-to-move [board]
  (if (= (tile-count board \x) (tile-count board \o))
    \x
    \o))

(defn move-valid? [board index]
  (and (in-range? 0 9 index)
       (= (nth board index) empty-tile)))

(defn play-move [board index]
  (if-not (move-valid? board index)
    board
    (assoc board index (player-to-move board))))

(defn get-user-move []
  (Integer/parseInt (read-line)))