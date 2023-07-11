(ns tictactoe.board-state
  (:require [tictactoe.utils :as utils]))

(defn winning-line? [seq]
  (and (not= utils/empty-tile (first seq))
       (apply = seq)))

(defn board-size [board]
  (if (utils/board-3d? board)
    (board-size (first board))
    (int (Math/sqrt (count board)))))

(defn rotated-planes [board]
  (let [[a b c d e f g h i] (first board)
        [j k l m n o p q r] (second board)
        [s t u v w x y z aa] (nth board 2)]
    [[a j s b k t c l u] [d m v e n w f o x] [g p y h q z i r aa]]))

(defn rows [board]
  (if (utils/board-3d? board)
    (apply concat (map #(rows %) (concat board (rotated-planes board))))
    (partition (board-size board) board)))

(defn columns [board]
  (if (utils/board-3d? board)
    (apply concat (map #(columns %) (concat board (rotated-planes board))))
    (for [i (range 0 (board-size board))]
      (map #(nth % i) (rows board)))))

(defn diagonal-planes [board]
  (let [[a b c d e f g h i] (first board)
        [j k l m n o p q r] (second board)
        [s t u v w x y z aa] (nth board 2)]
    [[a k u d n x g q aa] [c k s f n v i q y]]))

(defn diagonals [board]
  (if (utils/board-3d? board)
    (apply concat (map #(diagonals %) (concat board (diagonal-planes board))))
    (let [size (board-size board)]
      [(map #(nth board (* (inc size) %)) (range 0 size))
       (map #(nth board (* (dec size) (inc %))) (range 0 size))])))

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

(defn winner [board]
  (if (= (utils/tile-count board \x) (utils/tile-count board \o))
    \o
    \x))

(defn x-wins? [board]
  (and (win? board) (= \x (winner board))))

(defn o-wins? [board]
  (and (win? board) (= \o (winner board))))

(defn tie? [board]
  (if (utils/board-3d? board)
    (some true? (map #(tie? %) board))
    (and (not (win? board))
         (not (some #(= % utils/empty-tile) board)))))

(defn game-over? [board]
  (or (win? board) (tie? board)))