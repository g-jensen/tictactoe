(ns tictactoe.move
  (:require [tictactoe.utils :as utils]
            [tictactoe.game-state :as game-state]))

(defn player-to-move [board]
  (if (= (utils/tile-count board \x) (utils/tile-count board \o))
    \x
    \o))

(defn move-valid? [board index]
  (and (utils/in-range? 0 9 index)
       (= (nth board index) utils/empty-tile)))

(defn play-move [board index]
  (if-not (move-valid? board index)
    board
    (assoc board index (player-to-move board))))

(defn get-user-move []
  (let [input (read-line)]
    (if (utils/input-valid? input utils/empty-board)
      (dec (Integer/parseInt input))
      -1)))

(defn score-board [board]
  (cond
    (game-state/x-wins? board) 10
    (game-state/o-wins? board) -10
    (game-state/tie? board) 0))

(defn move-weight [board index]
  (let [board (play-move board index)]
    (cond
      (game-state/game-over? board)
        (score-board board)
      (= \x (player-to-move board))
        (apply max (map #(move-weight board %) (utils/empty-indices board)))
      :else
        (apply min (map #(move-weight board %) (utils/empty-indices board))))))

(def move-weight (memoize move-weight))

(defn get-computer-move [board]
  (if (= \x (player-to-move board))
    (apply max-key #(move-weight board %) (utils/empty-indices board))
    (apply min-key #(move-weight board %) (utils/empty-indices board))))