(ns tictactoe.move
  (:require [tictactoe.board :refer :all]
            [tictactoe.game-state :refer :all]))

(defn in-range? [start end n]
  (and (>= n start) (< n end)))

(defn tile-count [board tile]
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

(defn empty-indices [board]
  (filter #(= empty-tile (nth board %)) (range 0 9)))

(defn winner [board]
  (if (= \x (player-to-move board)) \o \x))

(defn x-wins? [board]
  (and (win? board) (= \x (winner board))))

(defn o-wins? [board]
  (and (win? board) (= \o (winner board))))

(defn score-board [board]
  (cond
    (x-wins? board) 10
    (o-wins? board) -10
    (tie? board) 0))

(defn move-weight [board index]
  (let [board (play-move board index)]
    (cond
      (game-over? board) (score-board board)
      (= \x (player-to-move board)) (apply max (map #(move-weight board %) (empty-indices board)))
      :else (apply min (map #(move-weight board %) (empty-indices board))))))

(def move-weight (memoize move-weight))

(defn get-computer-move [board]
  (if (= \x (player-to-move board))
    (apply max-key #(move-weight board %) (empty-indices board))
    (apply min-key #(move-weight board %) (empty-indices board))))