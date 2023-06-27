(ns tictactoe.core
  (:gen-class)
  (:require [tictactoe.game-state :as game-state]
            [tictactoe.ui :as ui]
            [tictactoe.menu :as menu]))

(defmulti run-tictactoe identity)

(defmethod run-tictactoe :console [_]
  (ui/console-initialize)
  (loop [state (game-state/initial-state)]
    (ui/console-draw state)
    (if (game-state/over? state)
      (do (game-state/clean-up state)
          (recur (game-state/initial-state)))
      (recur (game-state/update-state state)))))

(defmethod run-tictactoe :quil [_]
  (ui/quil-initialize))

(defn -main [& args]
  (run-tictactoe (menu/evaluate-menu menu/ui-picker-menu)))
