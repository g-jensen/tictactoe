(ns tictactoe.menu
  (:require [tictactoe.move :as move]
            [tictactoe.utils :as utils]
            [tictactoe.database :as database]
            [tictactoe.board-state :as board-state])
  (:import (tictactoe.database FileDatabase SQLDatabase)))

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

(defmulti next-state :state)
(defmethod next-state :default [state input]
  {:state :database})

(defmethod next-state :ui [state input]
  (cond
    (= "1" input) :console
    (= "2" input) :quil
    :else state))

(defmethod next-state :database [state input]
  (cond
    (= "1" input) (assoc state :database (FileDatabase. "games.txt") :state :load-type)
    (= "2" input) (assoc state :database (SQLDatabase. "games.db") :state :load-type)
    :else state))

(defmethod next-state :load-type [state input]
  (cond
    (= "1" input) (assoc state :load-type :new :state :board-size)
    (= "2" input) (assoc state :load-type :load :state :select-game)
    :else state))

(defmethod next-state :board-size [state input]
  (cond
    (= "1" input) (assoc state :board-size 3
                               :board (utils/empty-board 3)
                               :state :versus-type)
    (= "2" input) (assoc state :board-size 4
                               :state :versus-type
                               :board (utils/empty-board 4))
    :else state))

(defmethod next-state :versus-type [state input]
  (cond
    (= "1" input) (assoc state :versus-type :pvp :state :done)
    (= "2" input) (assoc state :versus-type :pvc :state :difficulty)
    :else state))

(defmethod next-state :difficulty [state input]
  (cond
    (= "1" input) (assoc state :difficulty :easy :state :character)
    (= "2" input) (assoc state :difficulty :medium :state :character)
    (= "3" input) (assoc state :difficulty :hard :state :character)
    :else state))

(defmethod next-state :character [state input]
  (let [empty-board (utils/empty-board (:board-size state))]
    (cond
      (= "1" input) (assoc state :character \x
                                 :board empty-board
                                 :state :done)
      (= "2" input) (assoc state :character \o
                                 :board (move/play-move empty-board
                                                        (move/get-computer-move
                                                          (:difficulty state)
                                                          empty-board))
                                 :state :done)
      :else state)))

(defmethod next-state :select-game [state input]
  (database/initialize (:database state))
  (let [games (database/fetch-all-games (:database state))
        choice (if (utils/input-valid? input games) (dec (Integer/parseInt input)) -1)
        game (nth games choice nil)]
    (cond
      (empty? games)
        (assoc state :state :board-size)
      (not (nil? game))
        (assoc state :board-size (board-state/board-size (:board game))
                     :board (:board game)
                     :versus-type (:mode (:gamemode game))
                     :difficulty (:difficulty (:gamemode game))
                     :old-date (:date game)
                     :state :done)
      :else
        state)))

(defmulti ui-components :state)

(defmethod ui-components :ui [state]
  {:label "UI Type"
   :options ["1. Console UI" "2. Quil UI"]})

(defmethod ui-components :database [state]
  {:label "Database"
   :options ["1. File Database" "2. SQL Database"]})

(defmethod ui-components :load-type [state]
  {:label "Load Type"
   :options ["1. New Game" "2. Load Game"]})

(defn number-options [options]
  (map #(str (inc %) ". " (nth options %)) (range 0 (count options))))

(defmethod ui-components :select-game [state]
  (database/initialize (:database state))
  (let [games (database/fetch-all-games (:database state))]
    (if (empty? games)
      {:label "Game"
       :options ["1. New Game"]}
      {:label "Game"
       :options (number-options (map #(str (:date %) ": " (:board %))
                                     (database/fetch-all-games (:database state))))})))

(defmethod ui-components :board-size [state]
  {:label "Board Size"
   :options ["1. 3x3" "2. 4x4"]})

(defmethod ui-components :versus-type [state]
  {:label "Versus Type"
   :options ["1. Versus Player" "2. Versus Computer"]})

(defmethod ui-components :difficulty [state]
  {:label "Difficulty"
   :options ["1. Easy" "2. Medium" "3. Hard"]})

(defmethod ui-components :character [state]
  {:label "Starting Character"
   :options ["1. x" "2. o"]})