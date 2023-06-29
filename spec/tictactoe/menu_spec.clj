(ns tictactoe.menu-spec
  (:require [speclj.core :refer :all]
            [tictactoe.menu :refer :all]
            [tictactoe.database :as database]
            [tictactoe.file-database]
            [tictactoe.sql-database]
            [tictactoe.utils :as utils])
  (:import (tictactoe.file_database FileDatabase)
           (tictactoe.sql_database SQLDatabase)))

(def games
  [{:date "the-date", :board [\x \_ \_ \_ \_ \_ \_ \_ \_], :gamemode {:mode :pvp}}
   {:date "another-date", :board (utils/empty-board 4), :gamemode {:mode :pvc, :difficulty :hard}}])

(describe "A TicTacToe Menu"
  (it "sets the default state"
    (should= {:state :database} (next-state {} nil)))

  (it "selects the ui state"
    (should= :console (next-state {:state :ui} "1"))
    (should= :quil (next-state {:state :ui} "2")))

  (it "selects the database state"
    (should= (FileDatabase. "games.txt") (:database (next-state {:state :database} "1")))
    (should= :load-type (:state (next-state {:state :database} "1")))
    (should= (SQLDatabase. "games.db") (:database (next-state {:state :database} "2")))
    (should= :load-type (:state (next-state {:state :database} "2")))
    (should= {:state :database} (next-state {:state :database} "3")))

  (it "selects the load type state"
    (should= :new (:load-type (next-state {:state :load-type} "1")))
    (should= :board-size (:state (next-state {:state :load-type} "1")))
    (should= :load (:load-type (next-state {:state :load-type} "2")))
    (should= :select-game (:state (next-state {:state :load-type} "2")))
    (should= {:state :load-type} (next-state {:state :load-type} "3")))

  (with-stubs)
  (it "selects a game from a database"
    (with-redefs [database/fetch-all-games (stub :fetch-all-games {:return games})
                  database/initialize (stub :initialize {:return 0})]
      (should= {:board-size 3
                :board [\x \_ \_ \_ \_ \_ \_ \_ \_]
                :versus-type :pvp
                :difficulty nil
                :old-date "the-date"
                :state :done} (next-state {:state :select-game} "1"))
      (should= {:board-size 4
                :board [\_ \_ \_ \_ \_ \_ \_ \_ \_ \_ \_ \_ \_ \_ \_ \_]
                :versus-type :pvc
                :difficulty :hard
                :old-date "another-date"
                :state :done} (next-state {:state :select-game} "2"))
      (should= {:state :select-game} (next-state {:state :select-game} "3"))
      (should= {:state :select-game} (next-state {:state :select-game} nil))))

  (it "selects a new game if the database is empty"
    (with-redefs [database/fetch-all-games (stub :fetch-all-games {:return []})
                  database/initialize (stub :initialize {:return 0})]
      (should= {:state :board-size} (next-state {:state :select-game} "1"))))

  (it "selects the board size state"
    (should= 3 (:board-size (next-state {:state :board-size} "1")))
    (should= :versus-type (:state (next-state {:state :board-size} "1")))
    (should= 4 (:board-size (next-state {:state :board-size} "2")))
    (should= :versus-type (:state (next-state {:state :board-size} "2")))
    (should= {:state :board-size} (next-state {:state :board-size} "3")))

  (it "selects the versus type state"
    (should= :pvp (:versus-type (next-state {:state :versus-type} "1")))
    (should= :done (:state (next-state {:state :versus-type} "1")))
    (should= :pvc (:versus-type (next-state {:state :versus-type} "2")))
    (should= :difficulty (:state (next-state {:state :versus-type} "2")))
    (should= {:state :versus-type} (next-state {:state :versus-type} "3")))

  (it "selects the difficulty state"
    (should= :easy (:difficulty (next-state {:state :difficulty} "1")))
    (should= :character (:state (next-state {:state :difficulty} "1")))
    (should= :medium (:difficulty (next-state {:state :difficulty} "2")))
    (should= :character (:state (next-state {:state :difficulty} "2")))
    (should= :hard (:difficulty (next-state {:state :difficulty} "3")))
    (should= :character (:state (next-state {:state :difficulty} "3")))
    (should= {:state :difficulty} (next-state {:state :difficulty} "4")))

  (it "selects the character state"
    (let [state {:state :character
                 :board-size 3
                 :difficulty :hard}]
      (should= \x (:character (next-state state "1")))
      (should= :done (:state (next-state state "1")))
      (should= (utils/empty-board 3) (:board (next-state state "1")))
      (should= \o (:character (next-state state "2")))
      (should= :done (:state (next-state state "2")))
      (should= [\_ \_ \_ \_ \_ \_ \_ \_ \x] (:board (next-state state "2")))
      (should= state (next-state state "3"))))

  (it "stores the ui components to select the ui"
    (should= {:label "UI Type"
              :options ["1. Console UI" "2. Quil UI"]}
             (ui-components {:state :ui})))

  (it "stores the ui components to select the database"
    (should= {:label "Database"
              :options ["1. File Database" "2. SQL Database"]}
             (ui-components {:state :database})))

  (it "stores the ui components to select the load type"
    (should= {:label "Load Type"
              :options ["1. New Game" "2. Load Game"]}
             (ui-components {:state :load-type})))

  (it "stores the ui components to select a game from a database"
    (with-redefs [database/fetch-all-games (stub :fetch-all-games {:return games})
                  database/initialize (stub :initialize {:return 0})]
      (should= {:label "Game"
                :options ["1. the-date: [\\x \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_]"
                          "2. another-date: [\\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_]"]}
               (ui-components {:state :select-game :database nil}))))

  (it "stores the ui components to select a new game if the database is empty"
    (with-redefs [database/fetch-all-games (stub :fetch-all-games {:return []})
                  database/initialize (stub :initialize {:return 0})]
      (should= {:label "Game"
                :options ["1. New Game"]}
               (ui-components {:state :select-game :database nil}))))

  (it "stores the ui components to select the board size"
    (should= {:label "Board Size"
              :options ["1. 3x3" "2. 4x4"]}
             (ui-components {:state :board-size})))

  (it "stores the ui components to select the versus type"
    (should= {:label "Versus Type"
              :options ["1. Versus Player" "2. Versus Computer"]}
             (ui-components {:state :versus-type})))

  (it "stores the ui components to select the difficulty state"
    (should= {:label "Difficulty"
              :options ["1. Easy" "2. Medium" "3. Hard"]}
             (ui-components {:state :difficulty})))

  (it "stores the ui components to select the character"
    (should= {:label "Starting Character"
              :options ["1. x" "2. o"]}
             (ui-components {:state :character}))))