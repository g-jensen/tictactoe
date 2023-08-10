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

(defn- x-wins? [board]
  (and (board-state/win? board) (= \x (board-state/winner board))))

(defn- o-wins? [board]
  (and (board-state/win? board) (= \o (board-state/winner board))))

(defn score-board [board]
  (cond
    (x-wins? board) 10
    (o-wins? board) -10
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

(defn initial-depth [board]
  (let [size (board-state/board-size board)]
    (cond
      (= 3 size) 10
      (= 4 size) 4
      :else 2)))

(defn max-key-or-min-key? [board]
  (if (= \x (player-to-move board))
    max-key
    min-key))

(defn- includes? [coll val]
  (some #(= val %) coll))

(defn board-with-o [board]
  (first (filter #(includes? (nth board %) \o) (range 0 3))))

(defn board-with-x [board]
  (first (filter #(includes? (nth board %) \x) [0 2])))

(defn get-3d-move [board]
  (if (= \o (player-to-move board))
    (apply min-key #(move-weight board % 2) (utils/empty-indices board))
    (let [moves-map [[13 13 13 13] [4 3 22 21] [0 6 18 24]]
          x-count (utils/tile-count board \x)
          [x1 y1 x2 y2] (nth moves-map x-count (repeat nil))]
      (cond
        (< x-count 2)
          (if (= 0 (board-with-o board))
            (if (move-valid? board x1) x1 y1)
            (if (move-valid? board x2) x2 y2))
        (= x-count 2)
          (let [computer-move (apply max-key #(move-weight board % 1) (utils/empty-indices board))]
            (if (board-state/game-over? (play-move board computer-move))
              computer-move
              (if (= 0 (board-with-x board))
                (if (move-valid? board x1) x1 y1)
                (if (move-valid? board x2) x2 y2))))
        :else (apply max-key #(move-weight board % 1) (utils/empty-indices board))))))

(defmulti get-computer-move (fn [difficulty board] difficulty))
(defmethod get-computer-move :hard [_ board]
  (let [depth (initial-depth board)
        empty-indices (utils/empty-indices board)]
    (if (utils/board-3d? board)
      (get-3d-move board)
      (apply (max-key-or-min-key? board) #(move-weight board % depth) empty-indices))))

(defmethod get-computer-move :medium [_ board]
  (if (>= (rand) 0.7)
    (first (utils/empty-indices board))
    (get-computer-move :hard board)))

(defmethod get-computer-move :easy [_ board]
  (if (>= (rand) 0.2)
    (first (utils/empty-indices board))
    (get-computer-move :hard board)))