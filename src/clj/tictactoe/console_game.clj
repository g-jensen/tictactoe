(ns tictactoe.console-game
  (:require [clojure.string :as str]
            [tictactoe.board-state :as board-state]
            [tictactoe.game-state :as gs]
            [tictactoe.file-database]
            [tictactoe.menu]
            [tictactoe.utils :as utils]))
(defmulti state->str #(:type (gs/ui-components %)))

(defn board->str [board]
  (if (utils/board-3d? board)
    (apply str (map #(str (board->str %) "\n\n") board))
    (->> (partition (int (Math/sqrt (count board))) board)
         (map #(str/join " " %))
         (str/join "\n"))))

(defmethod state->str :menu [state]
  (str (:label (gs/ui-components state)) "\n"
       (str/join "\n" (:options (gs/ui-components state)))))

(defmethod state->str :counter [state]
  (:label (gs/ui-components state)))

(defn- game-over-message [board]
  (str
    (if (board-state/win? board)
      (str (board-state/winner board) " has won!")
      "tie!")
    "\nPlay Again:"))

(defmethod state->str :board [state]
  (let [board (:board state)]
    (str (board->str board)
         (if (:over? state)
           (str "\n" (game-over-message board))
           ""))))
(defmethod gs/run-tictactoe :console [_]
  (loop [state (gs/next-state {} nil)]
    (println (state->str state))
    (if (:over? state)
      (recur (gs/next-state {} nil))
      (let [input (read-line)]
        (recur (gs/next-state state (if (utils/input-valid? input)
                                      (Integer/parseInt input))))))))