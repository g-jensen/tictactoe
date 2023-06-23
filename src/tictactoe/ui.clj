(ns tictactoe.ui
  (:require [tictactoe.database :as database]
            [tictactoe.utils :as utils]
            [tictactoe.board-state :as board-state]
            [tictactoe.game-mode :as game-mode]
            [tictactoe.move :as move])
  (:import (tictactoe.database FileDatabase SQLDatabase)))

(defn display-guide [board]
  (println (str "Pick a tile 1-" (count board))))

(defn display-board [board]
  (println (utils/board->str board))
  (println))

(defn display-game-over-message [board]
  (if (board-state/win? board)
    (println (str (board-state/winner board) " has won!"))
    (println "tie!")))

(defn character-picker-options [state]
  [{:name "Start as X"
    :options []
    :value {:gamemode (game-mode/->PvCGame (:size state) (utils/empty-board (:size state)) (:difficulty state))}}
   {:name    "Start as O"
    :options []
    :value {:gamemode (game-mode/->PvCGame (:size state) (move/play-move (utils/empty-board (:size state)) (move/get-computer-move (:difficulty state) (utils/empty-board (:size state)))) (:difficulty state))}}])

(defn versus-options [state]
  [{:name     "Versus Player"
     :options []
     :value {:gamemode (game-mode/->PvPGame (:size state) (utils/empty-board (:size state)))}}
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
  (let [db (:database state)]
    (database/initialize db)
    (vec (for [game (database/fetch-all-games db)]
      {:name (apply str [(:date game) ": " (:board game)])
       :options []
       :value (cond
                (= (:gamemode game) "PvPGame") {:gamemode (game-mode/->PvPGame (board-state/board-size (:board game)) (:board game)) :old-date (:date game)}
                (= (:gamemode game) "PvCGame :easy") {:gamemode (game-mode/->PvCGame (board-state/board-size (:board game)) (:board game) :easy) :old-date (:date game)}
                (= (:gamemode game) "PvCGame :medium") {:gamemode (game-mode/->PvCGame (board-state/board-size (:board game)) (:board game) :medium) :old-date (:date game)}
                (= (:gamemode game) "PvCGame :hard") {:gamemode (game-mode/->PvCGame (board-state/board-size (:board game)) (:board game) :hard) :old-date (:date game)})}))))

(defn load-type-options [state]
  [{:name    "New Game"
    :options (board-size-options state)}
   {:name    "Load Game"
    :options (let [opts (load-game-options state)]
               (if (empty? opts)
                 [{:name    "New Game"
                   :options (board-size-options state)}]
                 opts))}])

(def game-mode-menu
  {:name "TicTacToe Game\nChoose Database Persistence"
   :options [{:name "File Persistence"
              :options (load-type-options {:database (FileDatabase. "games.txt")})}
             {:name "SQL Persistence"
              :options (load-type-options {:database (SQLDatabase. "games.db")})}]})

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