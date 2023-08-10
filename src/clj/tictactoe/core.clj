(ns tictactoe.core
  (:gen-class)
  (:require [tictactoe.game-state :as game-state]
            [tictactoe.console-game]
            [tictactoe.quil-game]
            [tictactoe.web-game]))

(defn -main [& [ui]]
  (game-state/run-tictactoe {:ui (keyword ui)}))