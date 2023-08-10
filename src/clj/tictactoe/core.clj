(ns clj.tictactoe.core
  (:gen-class)
  (:require [clj.tictactoe.game-state :as game-state]
            [clj.tictactoe.console-game]
            [clj.tictactoe.quil-game]
            [clj.tictactoe.web-game]))

(defn -main [& [ui]]
  (game-state/run-tictactoe {:ui (keyword ui)}))