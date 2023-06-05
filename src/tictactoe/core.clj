(ns tictactoe.core
  (:require
    [tictactoe.board :refer :all]
    [tictactoe.ui :refer :all]
    [tictactoe.game-state :refer :all]
    [tictactoe.game-mode :refer :all]))

(defn -main [& args]
  (display-game-modes-prompt game-modes)
  (let [game-mode (prompt-game-mode game-modes)]
    (loop [board empty-board]
      (display-board board)
      (if (game-over? board)
        (display-game-over-message board)
        (recur (next-board game-mode board))))))