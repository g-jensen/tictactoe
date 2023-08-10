(ns clj.tictactoe.quil-game
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clj.tictactoe.board-state :as board-state]
            [clj.tictactoe.game-state :as gs]
            [clj.tictactoe.utils :as utils]))

(defmulti draw-components #(:type (gs/ui-components %)))
(defmulti update-ui (fn [state _] (:type (gs/ui-components state))))
(defmulti get-input (fn [state _] (:type (gs/ui-components state))))

(defn point-in-rect? [point rect]
  (let [[x1 y1] point
        [x2 y2 width height] rect]
    (and (>= x1 x2)
         (<= x1 (+ x2 width))
         (>= (+ y1 height) y2)
         (<= (+ y1 height) (+ y2 height)))))

(defn add-points [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn sub-points [[x1 y1] [x2 y2]]
  [(- x1 x2) (- y1 y2)])

(defn board-index-to-coords [index size tile-size]
  [(* tile-size (inc (quot index size))) (+ tile-size (* tile-size (inc (mod index size))))])

(defn- clicked-tiles [mouse-pos board tile-size]
  (let [size (board-state/board-size board)]
    (filter #(point-in-rect? mouse-pos
                             (concat (board-index-to-coords % size tile-size)
                                     [tile-size tile-size]))
            (range 0 (count board)))))

(defn- updated-value [state data]
  (let [valid? (:valid? (gs/ui-components state))
        init (:initial-value (gs/ui-components state))
        val (get state :counter-val init)]
    (cond
      (point-in-rect? [(:x data) (:y data)] [5 85 30 30])
      (if (valid? (dec val)) (dec val) val)
      (point-in-rect? [(:x data) (:y data)] [35 85 30 30])
      (if (valid? (inc val)) (inc val) val)
      :else
      val)))

(defn- picked-tile [mouse-pos board]
  (if (utils/board-3d? board)
    (first (apply concat (for [i (range 0 (count board))]
                           (map #(+ (* i 9) %)
                                (clicked-tiles (sub-points mouse-pos [0 (* i 90)])
                                               (nth board i) 30)))))
    (first (clicked-tiles mouse-pos board 50))))

(defn- quil-button [[x y] [width height] text]
  (q/fill 255)
  (q/rect x (- y height) width height)
  (q/fill 0)
  (q/text text x y))

(defn- draw-tiles [board pos tile-size]
  (let [board-size (board-state/board-size board)]
    (for [i (range 0 (count board))
          :let [x (mod i board-size)
                y (quot i board-size)]]
      (quil-button (add-points pos (board-index-to-coords i board-size tile-size))
                   [tile-size tile-size] (str (nth board (+ (* board-size y) x)))))))

(defmethod get-input :menu [state data]
  (let [mouse-pos [(:x data) (:y data)]
        options (:options (gs/ui-components state))
        inputs (filter #(point-in-rect? mouse-pos [5 (* 20 (+ % 3)) 150 20])
                      (range 0 (count options)))]
    (if-not (empty? inputs)
      (str (inc (first inputs))))))

(defmethod get-input :counter [state data]
  (let [mouse-pos [(:x data) (:y data)]]
    (if (point-in-rect? mouse-pos [5 115 60 30])
      (str (:counter-val state)))))

(defmethod get-input :board [state data]
  (let [mouse-pos [(:x data) (:y data)]
        input (picked-tile mouse-pos (:board state))]
    (if-not (nil? input)
      (str (inc input)))))

(defmethod draw-components :menu [state]
  (let [comps (gs/ui-components state)
        label (:label comps)
        options (:options comps)]
    (do (q/text label 5 20)
        (doall
          (for [i (range 0 (count options))]
            (quil-button [5 (* 20 (+ i 3))]
                         [(* 15 (count (nth options i))) 20]
                         (nth options i)))))))

(defmethod update-ui :default [state _] state)

(defmethod update-ui :counter [state data]
  (if (point-in-rect? [(:x data) (:y data)] [5 115 60 30])
    (gs/next-state state (str (:counter-val state)))
    (assoc state :counter-val (updated-value state data))))

(defmethod draw-components :counter [state]
  (let [comps (gs/ui-components state)
        label (:label comps)
        init (:initial-value comps)
        val (get state :counter-val init)]
    (do (q/text label 5 20)
        (q/text (str val) 5 50)
        (quil-button [5 85] [30 30] "-")
        (quil-button [35 85] [30 30] "+")
        (quil-button [5 115] [60 30] "done"))))

(defmethod draw-components :board [state]
  (let [board (:board state)]
    (if (utils/board-3d? board)
      (doall (for [i (range 0 (count board))]
               (doall (draw-tiles (nth board i) [0 (* i 90)] 30))))
      (doall (draw-tiles board [0 0] 50))))
  (if (:over? state)
    (quil-button [150 300] [120 50] "Play Again?")))

(defn- quil-setup []
  (q/frame-rate 30)
  (q/background 200)
  (gs/next-state {} nil))

(defn- quil-draw [state]
  (q/fill 200)
  (q/rect 0 0 384 384)
  (q/fill 0)
  (q/text-size 20)
  (draw-components state))

(defn- mouse-clicked [state data]
  (println data)
  (if (:over? state)
    (gs/next-state {} nil)
    (gs/next-state (update-ui state data)
                   (get-input state data))))

(defmethod gs/run-tictactoe :quil [_]
  (q/defsketch tictactoe
               :title "TicTacToe Game"
               :settings #(q/smooth 2)
               :setup quil-setup
               :draw quil-draw
               :mouse-clicked mouse-clicked
               :size [384 384]
               :middleware [m/fun-mode]))