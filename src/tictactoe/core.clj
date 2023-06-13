(ns tictactoe.core
  (:gen-class)
  (:require
    [tictactoe.ui :as ui]
    [tictactoe.game-state :as game-state]
    [tictactoe.game-mode :as game-mode]))

(defn -main [& args]
  (let [game-mode (ui/evaluate-menu ui/game-mode-menu)]
    (ui/display-guide)
    (loop [board (game-mode/initial-board game-mode)]
      (ui/display-board board)
      (if (game-state/game-over? board)
        (ui/display-game-over-message board)
        (recur (game-mode/next-board game-mode board))))))