(ns tictactoe.quil-game
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [tictactoe.board-state :as board-state]
            [tictactoe.database]
            [tictactoe.game-state :as gs]
            [tictactoe.move :as move]
            [tictactoe.utils :as utils]))

(defn point-in-rect? [point rect]
  (let [[x1 y1] point
        [x2 y2 width height] rect]
    (and (>= x1 x2)
         (<= x1 (+ x2 width))
         (>= (+ y1 height) y2)
         (<= (+ y1 height) (+ y2 height)))))

(defn quil-button [[x y] [width height] text]
  (q/fill 255)
  (q/rect x (- y height) width height)
  (q/fill 0)
  (q/text text x y))

(defn quil-setup [state]
  (q/frame-rate 30)
  (q/background 200)
  (gs/next-state state nil))

(defn add-points [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn sub-points [[x1 y1] [x2 y2]]
  [(- x1 x2) (- y1 y2)])

(defn board-index-to-coords [index size tile-size]
  [(* tile-size (inc (quot index size))) (+ tile-size (* tile-size (inc (mod index size))))])

(defn draw-tiles [board pos tile-size]
  (let [board-size (board-state/board-size board)]
    (for [i (range 0 (count board))
          :let [x (mod i board-size)
                y (quot i board-size)]]
      (quil-button (add-points pos (board-index-to-coords i board-size tile-size)) [tile-size tile-size] (str (nth board (+ (* board-size y) x)))))))

(defn quil-draw-board [board]
  (if (utils/board-3d? board)
    (doall (for [i (range 0 (count board))]
             (doall (draw-tiles (nth board i) [0 (* i 90)] 30))))
    (doall (draw-tiles board [0 0] 50))))

(defn quil-draw-buttons [state]
  (let [components (gs/ui-components state)
        label (:label components)
        options (:options components)]
    (do (q/text label 5 20)
        (doall
          (for [i (range 0 (count options))]
            (quil-button
              [5 (* 20 (+ i 3))]
              [(* 15 (count (nth options i))) 20]
              (nth options i)))))))

(defn picked-option [mouse-pos options]
  (first (filter #(point-in-rect? mouse-pos [5 (* 20 (+ % 3)) 150 20])
                 (range 0 (count options)))))

(defn clicked-tiles [mouse-pos board tile-size]
  (filter #(point-in-rect? mouse-pos
                           (concat (board-index-to-coords % (board-state/board-size board) tile-size) [tile-size tile-size]))
          (range 0 (count board))))

(defn picked-tile [mouse-pos board]
  (if (utils/board-3d? board)
    (first (apply concat (for [i (range 0 (count board))]
                           (map #(+ (* i 9) %)
                                (clicked-tiles (sub-points mouse-pos [0 (* i 90)])
                                               (nth board i) 30)))))
    (first (clicked-tiles mouse-pos board 50))))

(defn click-button [state data]
  (let [options (:options (gs/ui-components state))
        choice (picked-option [(:x data) (:y data)] options)]
    (if (nil? choice)
      state
      (gs/next-state state (str (inc choice))))))

(defn click-tile [state data]
  (let [tile (picked-tile [(:x data) (:y data)] (:board state))]
    (if (nil? tile)
      state
      (assoc state :board (move/play-move (:board state) tile)))))

(defn quil-draw [state]
  (q/fill 200)
  (q/rect 0 0 384 384)
  (q/fill 0)
  (q/text-size 20)
  (if (not= :done (:state state))
    (quil-draw-buttons state)
    (quil-draw-board (:board state)))
  (if (:over? state)
    (quil-button [150 300] [120 50] "Play Again?")))

(defn quil-mouse-clicked [state data]
  (if-not (= :done (:state state))
    (click-button state data)
    (let [play-again? (point-in-rect? [(:x data) (:y data)] [150 300 120 50])
          state (click-tile state data)]
      (if (and (:over? state) play-again?)
        (do (gs/next-state {:ui :quil} nil))
        (do (gs/db-save-game (gs/next-board state))
            state)))))

(defmethod gs/next-board :quil [state]
  (let [board (:board state)
        difficulty (:difficulty state)]
    (if (gs/computer-turn? state)
      (assoc state :board (move/play-move board (move/get-computer-move difficulty board)))
      state)))

(defmethod gs/run-tictactoe :quil [state]
  (q/defsketch tictactoe
               :title "TicTacToe Game"
               :settings #(q/smooth 2)
               :setup #(quil-setup state)
               :draw quil-draw
               :update gs/update-state
               :mouse-clicked quil-mouse-clicked
               :size [384 384]
               :middleware [m/fun-mode]))