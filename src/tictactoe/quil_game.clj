(ns tictactoe.quil-game
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [tictactoe.board-state :as board-state]
            [tictactoe.database]
            [tictactoe.game-state :as gs]
            [tictactoe.move :as move]))

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

(defn board-index-to-coords [index size]
  [(* 50 (inc (quot index size))) (+ 50 (* 50 (inc (mod index size))))])

(defn quil-draw-board [board]
  (let [size (board-state/board-size board)]
    (doall (for [i (range 0 (count board))
                 :let [x (mod i size)
                       y (quot i size)]]
             (quil-button (board-index-to-coords i size) [50 50] (str (nth board (+ (* size y) x))))))))

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

(defn picked-tile [mouse-pos board]
  (first (filter #(point-in-rect? mouse-pos (concat (board-index-to-coords % (board-state/board-size board)) [50 50]))
                 (range 0 (count board)))))

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
    (quil-button [50 300] [120 50] "Play Again?")))

(defn quil-mouse-clicked [state data]
  (if-not (= :done (:state state))
    (click-button state data)
    (let [play-again? (point-in-rect? [(:x data) (:y data)] [50 300 120 50])
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