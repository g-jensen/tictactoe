(ns tictactoe.core
  (:gen-class)
  (:require
    [tictactoe.game-state :as game-state]))

(defn -main [& args]
  (loop [state (game-state/initial-state)]
    (if (game-state/over? state)
      (game-state/clean-up state)
      (recur (game-state/update state)))))