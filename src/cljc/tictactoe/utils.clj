(ns tictactoe.utils)

(def empty-tile \_)
(defn empty-board [n]
  (vec (repeat (* n n) empty-tile)))

(defn board-3d? [board]
  (coll? (first board)))

(defn tile-count [board tile]
  (if (board-3d? board)
    (apply + (map #(tile-count % tile) board))
    (count (filter #(= tile %) board))))

(defn in-range? [start end n]
  (and (>= n start) (< n end)))

(defn empty-indices [board]
  (let [board (flatten board)]
    (filter #(= empty-tile (nth board %)) (range 0 (count board)))))

(defn- is-digit [c]
  (some #(= c %) [\0 \1 \2 \3 \4 \5 \6 \7 \8 \9]))

(defn input-valid?
  ([input] (and (not (empty? input)) (every? #(is-digit %) input)))
  ([input options] (and (in-range? 1 (inc (count options)) input))))