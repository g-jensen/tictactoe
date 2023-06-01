(ns tictactoe.core
  (:require
    [tictactoe.board :refer :all]
    [tictactoe.ui :refer :all]
    [tictactoe.game-engine :refer :all]))

(defn display-winning-message [board]
  (println (str (winner board) " has won!")))

(defn get-user-move []
  (Integer/parseInt (read-line)))

;;TODO - test main
(defn -main [& args]
  (loop [board empty-board]
    (display-board board)
    (if (win? board)
      (display-winning-message board)
      (recur (play-move board (get-user-move))))))