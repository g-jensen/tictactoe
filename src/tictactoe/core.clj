(ns tictactoe.core
  (:require
    [tictactoe.board :refer :all]
    [tictactoe.ui :refer :all]
    [tictactoe.game-engine :refer :all]))

;;TODO - test main
(defn -main [& args]
  (loop [board empty-board]
    (display-board board)
    (if (win? board)
      (display-winning-message board)
      (recur (play-move board (get-user-move))))))