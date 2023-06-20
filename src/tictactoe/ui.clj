(ns tictactoe.ui
  (:require [tictactoe.database :as database]
            [tictactoe.utils :as utils]
            [tictactoe.board-state :as game-state]
            [tictactoe.game-mode :as game-mode]
            [tictactoe.move :as move]))

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
    :value {:game-mode (game-mode/->PvCGame (:size state) (utils/empty-board (:size state)) (:difficulty state))}}
   {:name    "Start as O"
    :options []
    :value {:game-mode (game-mode/->PvCGame (:size state) (move/play-move (utils/empty-board (:size state)) (move/get-computer-move (:difficulty state) (utils/empty-board (:size state)))) (:difficulty state))}}])

(defn versus-options [state]
  [{:name     "Versus Player"
     :options []
     :value {:game-mode (game-mode/->PvPGame (:size state) (utils/empty-board (:size state)))}}
    {:name    "Versus Computer"
     :options [{:name    "Easy"
                :options (character-picker-options (assoc state :difficulty :easy))}
               {:name "Medium"
                :options (character-picker-options (assoc state :difficulty :medium))}
               {:name "Hard"
                :options (character-picker-options (assoc state :difficulty :hard))}]}])

(defn board-size-options [state]
  [{:name "3x3 board"
    :options (versus-options (assoc state :size 3))}
   {:name "4x4 board"
    :options (versus-options (assoc state :size 4))}])

(defn load-game-options [state]
  (vec (for [game (database/fetch-all-games)]
    {:name (apply str [(:date game) ": " (:board game)])
      :options []
      :value (cond
               (= (:game-mode game) "PvPGame") {:game-mode (game-mode/->PvPGame (game-state/board-size (:board game)) (:board game)) :old-date (:date game)}
               (= (:game-mode game) "PvCGame :easy") {:game-mode (game-mode/->PvCGame (game-state/board-size (:board game)) (:board game) :easy) :old-date (:date game)}
               (= (:game-mode game) "PvCGame :medium") {:game-mode (game-mode/->PvCGame (game-state/board-size (:board game)) (:board game) :medium) :old-date (:date game)}
               (= (:game-mode game) "PvCGame :hard") {:game-mode (game-mode/->PvCGame (game-state/board-size (:board game)) (:board game) :hard) :old-date (:date game)})})))

(def game-mode-menu
  {:name    "TicTacToe Game"
   :options [{:name    "New Game"
              :options (board-size-options {})}
             {:name    "Load Game"
              :options (let [opts (load-game-options {})]
                         (if (empty? opts)
                           [{:name    "New Game"
                             :options (board-size-options {})}]
                           opts))}]})

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