(ns tictactoe.core
  (:require
    [tictactoe.ui :as ui]
    [tictactoe.board-state :as board-state]
    [tictactoe.game-mode :as game-mode]
    [tictactoe.database :as database])
  (:import (java.util Date)))

(defn -main [& args]
  (let [menu (ui/evaluate-menu ui/game-mode-menu)
        game-mode (:game-mode menu)
        old-date (:old-date menu)
        date (str (Date.))]
    (if-not (nil? old-date)
      (database/delete-game old-date))
    (loop [board (game-mode/initial-board game-mode)]
      (ui/display-board board)
      (database/update-game date board game-mode)
      (if (board-state/game-over? board)
        (do (ui/display-game-over-message board)
            (database/delete-game date))
        (recur (game-mode/next-board game-mode board))))))