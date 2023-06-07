(ns tictactoe.game-state
  (:require [tictactoe.board :as board]))

(defn winning-line? [seq]
  (and (not= board/empty-tile (first seq))
       (apply = seq)))

(defn rows [board]
  (partition 3 board))

(defn columns [board]
  (for [i (range 0 3)]
    (map #(nth % i) (rows board))))

(defn diagonals [board]
  [(map #(nth board (* 4 %)) (range 0 3))
   (map #(nth board (* 2 (inc %))) (range 0 3))])

(defn- horizontal-win? [board]
  (boolean (some true? (map winning-line? (rows board)))))

(defn- vertical-win? [board]
  (boolean (some true? (map winning-line? (columns board)))))

(defn- diagonal-win? [board]
  (boolean (some true? (map winning-line? (diagonals board)))))

(defn win? [board]
  (or (horizontal-win? board)
      (vertical-win? board)
      (diagonal-win? board)))

;; TODO - fix duplicate tile-count function for here and in move.clj
(defn tile-count [board tile]
  (count (filter #(= tile %) board)))

(defn winner [board]
  (max-key #(tile-count board %) \x \o))

(defn x-wins? [board]
  (and (win? board) (= \x (winner board))))

(defn o-wins? [board]
  (and (win? board) (= \o (winner board))))

(defn tie? [board]
  (and (not (win? board)) (not (some #(= % board/empty-tile) board))))

(defn game-over? [board]
  (or (win? board) (tie? board)))