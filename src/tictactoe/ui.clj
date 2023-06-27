(ns tictactoe.ui
  (:require [tictactoe.database :as database]
            [tictactoe.utils :as utils]
            [tictactoe.board-state :as board-state]
            [tictactoe.game-mode :as game-mode]
            [tictactoe.move :as move]
            [tictactoe.menu :as menu]
            [quil.core :as q]
            [quil.middleware :as m])
  (:import (tictactoe.game_mode PvCGame)))

(defn print-board [board]
  (println (utils/board->str board))
  (println))

(defn print-game-over-message [board]
  (if (board-state/win? board)
    (println (str (board-state/winner board) " has won!"))
    (println "tie!"))
  (println "\nPlay Again:"))

(defn console-initialize []
  (println "TicTacToe Game"))

(defn console-draw [state]
  (let [board (:board state)
        over? (:over? state)]
    (print-board board)
    (if over?
      (print-game-over-message board))))

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

(defn quil-setup []
  (q/frame-rate 30)
  (q/background 200)
  {:menu menu/game-mode-menu
   :date (utils/now)})

(defn board-index-to-coords [index size]
  [(* 50 (inc (quot index size))) (+ 50 (* 50 (inc (mod index size))))])

(defn quil-draw-board [board]
  (let [size (board-state/board-size board)]
    (doall (for [i (range 0 (count board))
                 :let [x (mod i size)
                       y (quot i size)]]
             (quil-button (board-index-to-coords i size) [50 50] (str (nth board (+ (* size y) x))))))))

(defn quil-draw [state]
  (q/fill 200)
  (q/rect 0 0 384 384)
  (q/fill 0)
  (q/text-size 20)
  (let [options (:options (:menu state))]
    (if-not (empty? options)
      (do (q/text (:name (:menu state)) 5 20)
          (doall (for [i (range 0 (count options))]
                      (quil-button [5 (* 20 (+ i 3))] [(* 15 (count (:name (get options i))))20] (:name (get options i))))))
      (quil-draw-board (:board state))))
  (if (:over? state)
    (quil-button [50 300] [120 50] "Play Again?")))

(defn quil-update [state]
  (if (and (empty? (:options (:menus state)))
           (nil? (:board state)))
    (assoc state :board (:init-board (:gamemode (:value (:menu state)))))
    (if (and (not (empty? (utils/empty-indices (:board state))))
             (instance? PvCGame (:gamemode (:value (:menu state))))
             (not= (move/player-to-move (:init-board (:gamemode (:value (:menu state))))) (move/player-to-move (:board state))))
      (do (database/delete-game (:database (:value (:menu state))) (:old-date (:value (:menu state))))
          (database/update-game (:database (:value (:menu state))) (:date state) (game-mode/next-board (:gamemode (:value (:menu state))) (:board state)) (:gamemode (:value (:menu state))))
          (assoc state :board (game-mode/next-board (:gamemode (:value (:menu state))) (:board state))))
      (if (board-state/game-over? (:board state))
        (do (database/delete-game (:database (:value (:menu state))) (:date state))
            (assoc state :over? true))
        state))))

(defn quil-mouse-clicked [state data]
  (let [options (:options (:menu state))
        x (:x data)
        y (:y data)
        choice (first (filter #(point-in-rect? [x y] [5 (* 20 (+ % 3)) 150 20])
                              (range 0 (count options))))]

    (if (not= nil choice)
      (assoc state :menu (get (:options (:menu state)) choice))
      (let [board (:board state)
            tile (first (filter #(point-in-rect? [x y] (concat (board-index-to-coords % (board-state/board-size board)) [50 50]))
                                (range 0 (count board))))]
        (if (not= nil tile)
          (do (database/delete-game (:database (:value (:menu state))) (:old-date (:value (:menu state))))
              (database/update-game (:database (:value (:menu state))) (:date state) (move/play-move board tile) (:gamemode (:value (:menu state))))
              (assoc state :board (move/play-move board tile)))
          (let [play-again? (point-in-rect? [x y] [50 300 120 50])]
            (if (and (:over? state) play-again?)
              (quil-setup)
              state)))))))

(defn quil-initialize []
  (q/defsketch tictactoe
               :title "TicTacToe Game"
               :settings #(q/smooth 2)
               :setup quil-setup
               :draw quil-draw
               :update quil-update
               :mouse-clicked quil-mouse-clicked
               :size [384 384]
               :middleware [m/fun-mode]))