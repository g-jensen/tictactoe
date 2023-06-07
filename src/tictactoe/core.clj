(ns tictactoe.core
  (:require
    [tictactoe.board :as board]
    [tictactoe.ui :as ui]
    [tictactoe.game-state :as game-state]
    [tictactoe.game-mode :as game-mode]))

(defn -main [& args]
  (ui/display-game-modes-prompt game-mode/game-modes)
  (let [game-mode (ui/prompt-game-mode game-mode/game-modes)]
    (loop [board board/empty-board]
      (ui/display-board board)
      (if (game-state/game-over? board)
        (ui/display-game-over-message board)
        (recur (game-mode/next-board game-mode board))))))