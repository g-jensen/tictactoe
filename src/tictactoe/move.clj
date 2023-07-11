(ns tictactoe.move
  (:require [tictactoe.utils :as utils]
            [tictactoe.board-state :as board-state]))

(defn player-to-move [board]
  (if (= (utils/tile-count board \x) (utils/tile-count board \o))
    \x
    \o))

(defn move-valid? [board index]
  (let [board (if (utils/board-3d? board) (flatten board) board)]
    (and (utils/in-range? 0 (count board) index)
         (= (nth board index) utils/empty-tile))))

(defn play-move [board index]
  (if-not (move-valid? board index)
    board
    (if (utils/board-3d? board)
      (partition (count (first board))
                 (assoc (vec (flatten board)) index (player-to-move board)))
      (assoc board index (player-to-move board)))))

(defn score-board [board]
  (cond
    (board-state/x-wins? board) 10
    (board-state/o-wins? board) -10
    (board-state/tie? board) 0))

(defn move-weight [board index depth]
  (let [board (play-move board index)]
    (cond
      (board-state/game-over? board)
        (score-board board)
      (zero? depth)
        0
      (= \x (player-to-move board))
        (apply max (map #(move-weight board % (dec depth)) (utils/empty-indices board)))
      :else
        (apply min (map #(move-weight board % (dec depth)) (utils/empty-indices board))))))

(def move-weight (memoize move-weight))

(defmulti get-computer-move (fn [difficulty board] difficulty))
(defmethod get-computer-move :hard [_ board]
  (let [depth (if (= 3 (board-state/board-size board)) 10 4)
        empty-indices (utils/empty-indices board)]
    (if (= \x (player-to-move board))
      (apply max-key #(move-weight board % depth) empty-indices)
      (apply min-key #(move-weight board % depth) empty-indices))))

(defmethod get-computer-move :medium [_ board]
  (if (>= (rand) 0.7)
    (first (utils/empty-indices board))
    (get-computer-move :hard board)))

(defmethod get-computer-move :easy [_ board]
  (if (>= (rand) 0.2)
    (first (utils/empty-indices board))
    (get-computer-move :hard board)))