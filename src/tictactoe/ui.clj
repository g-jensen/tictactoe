(ns tictactoe.ui
  (:require [tictactoe.utils :as utils]
            [tictactoe.game-state :as game-state]
            [tictactoe.game-mode :as game-mode]))

(defn display-guide []
  (println "Pick a tile 1-9"))

(defn display-board [board]
  (println (utils/board->str board))
  (println))

(defn display-game-over-message [board]
  (if (game-state/win? board)
    (println (str (game-state/winner board) " has won!"))
    (println "tie!")))

(defn character-picker-options [state]
  [{:name "Start as X"
    :options []
    :value (game-mode/->PvCGame (:size state) \x (:difficulty state))}
   {:name "Start as O"
    :options []
    :value (game-mode/->PvCGame (:size state) \o (:difficulty state))}])

(defn versus-options [state]
   [{:name "Versus Player"
     :options []
     :value (game-mode/->PvPGame (:size state))}
    {:name "Versus Computer"
     :options [{:name "Easy"
                :options (character-picker-options (assoc state :difficulty :easy))}
               {:name "Medium"
                :options (character-picker-options (assoc state :difficulty :medium))}
               {:name "Hard"
                :options (character-picker-options (assoc state :difficulty :hard))}]}])

(def game-mode-menu
  {:name "TicTacToe Game\nPick a board size:"
   :options [{:name "3x3 board"
              :options (versus-options {:size 3})}
             {:name "4x4 board"
              :options (versus-options {:size 4})}]})

(defn display-options [options]
  (doall (map #(println (str (inc %) ": " (:name (get options %))))
              (range 0 (count options)))))

(defn choose-option [menu]
  (loop []
    (println (:name menu))
    (display-options (:options menu))
    (let [input (read-line)]
      (if (utils/input-valid? input (:options menu))
        (get (:options menu) (dec (Integer/parseInt input)))
        (recur)))))

(defn evaluate-menu [menu]
  (loop [menu menu]
    (let [value (get menu :value)]
      (if-not (nil? value)
        value
        (recur (choose-option menu))))))