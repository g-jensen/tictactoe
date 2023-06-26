(ns tictactoe.ui
  (:require [tictactoe.database :as database]
            [tictactoe.utils :as utils]
            [tictactoe.board-state :as board-state]
            [tictactoe.game-mode :as game-mode]
            [tictactoe.move :as move]
            [quil.core :as q]
            [quil.middleware :as m])
  (:import (tictactoe.database FileDatabase SQLDatabase)))

(defprotocol UI
  (initialize [this])
  (draw [this state]))

(defn print-board [board]
  (println (utils/board->str board))
  (println))

(defn print-game-over-message [board]
  (if (board-state/win? board)
    (println (str (board-state/winner board) " has won!"))
    (println "tie!"))
  (println "Play Again:"))

(defrecord ConsoleUI []
  UI
  (initialize [this]
    (println "TicTacToe Game"))
  (draw [this state]
    (let [board (:board state)
          over? (:over? state)]
      (print-board board)
      (if over?
        (print-game-over-message board)))))

(defn quil-setup []
  (q/frame-rate 1)
  (q/background 200)
  {})

(defn quil-draw [state]
  )

(defn quil-update [state]
  )

(defrecord QuilUI []
  UI
  (initialize [this]
    (q/defsketch tictactoe
                 :title "TicTacToe Game"
                 :settings #(q/smooth 2)
                 :setup quil-setup
                 :draw quil-draw
                 :update quil-update
                 :size [284 384]
                 :middleware [m/fun-mode]))
  (draw [this state]
    ))