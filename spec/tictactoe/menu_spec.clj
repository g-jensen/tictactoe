(ns tictactoe.menu-spec
  (:require [speclj.core :refer :all]
            [tictactoe.menu :refer :all]
            [tictactoe.database]
            [tictactoe.game-state :as gs]
            [tictactoe.file-database]
            [tictactoe.sql-database]
            [tictactoe.utils :as utils]))

(def games
  [{:date "the-date", :board [\x \_ \_ \_ \_ \_ \_ \_ \_], :gamemode {:mode :pvp}}
   {:date "another-date", :board (utils/empty-board 4), :gamemode {:mode :pvc, :difficulty :hard}}])

(describe "A TicTacToe Menu"
  (it "sets the default state"
    (should= {:state :database} (gs/next-state {} nil)))

  (it "selects the database state"
    (should= :file (:database (gs/next-state {:state :database} "1")))
    (should= :load-type (:state (gs/next-state {:state :database} "1")))
    (should= :sql (:database (gs/next-state {:state :database} "2")))
    (should= :load-type (:state (gs/next-state {:state :database} "2")))
    (should= {:state :database} (gs/next-state {:state :database} "3")))

  (it "selects the load type state"
    (should= :new (:load-type (gs/next-state {:state :load-type} "1")))
    (should= :dimension (:state (gs/next-state {:state :load-type} "1")))
    (should= :load (:load-type (gs/next-state {:state :load-type} "2")))
    (should= :select-game (:state (gs/next-state {:state :load-type} "2")))
    (should= {:state :load-type} (gs/next-state {:state :load-type} "3")))

  (it "selects the dimension"
    (should= 2 (:dimension (gs/next-state {:state :dimension} "1")))
    (should= :board-size (:state (gs/next-state {:state :dimension} "1")))
    (should= 3 (:dimension (gs/next-state {:state :dimension} "2")))
    (should= :versus-type (:state (gs/next-state {:state :dimension} "2")))
    (should= (repeat 3 (utils/empty-board 3))
             (:board (gs/next-state {:state :dimension} "2")))
    (should= {:state :dimension} (gs/next-state {:state :dimension} "3")))

  (with-stubs)
  (it "selects a game from a database"
    (with-redefs [gs/db-fetch-games (stub :fetch-all-games {:return games})
                  gs/db-initialize (stub :initialize {:return 0})]
      (should= {:board-size 3
                :board [\x \_ \_ \_ \_ \_ \_ \_ \_]
                :versus-type :pvp
                :character \o
                :difficulty nil
                :old-date "the-date"
                :state :done} (gs/next-state {:state :select-game} "1"))
      (should= {:board-size 4
                :board [\_ \_ \_ \_ \_ \_ \_ \_ \_ \_ \_ \_ \_ \_ \_ \_]
                :versus-type :pvc
                :character \x
                :difficulty :hard
                :old-date "another-date"
                :state :done} (gs/next-state {:state :select-game} "2"))
      (should= {:state :select-game} (gs/next-state {:state :select-game} "3"))
      (should= {:state :select-game} (gs/next-state {:state :select-game} nil))))

  (it "selects a new game if the database is empty"
    (with-redefs [gs/db-fetch-games (stub :fetch-all-games {:return []})
                  gs/db-initialize  (stub :initialize {:return 0})]
      (should= {:state :board-size} (gs/next-state {:state :select-game} "1"))))

  (it "selects the board size state"
    (should= 3 (:board-size (gs/next-state {:state :board-size} "3")))
    (should= :versus-type (:state (gs/next-state {:state :board-size} "3")))
    (should= (utils/empty-board 3) (:board (gs/next-state {:state :board-size} "3")))
    (should= 4 (:board-size (gs/next-state {:state :board-size} "4")))
    (should= :versus-type (:state (gs/next-state {:state :board-size} "4")))
    (should= (utils/empty-board 4) (:board (gs/next-state {:state :board-size} "4")))
    (should= {:state :board-size} (gs/next-state {:state :board-size} "2"))
    (should= {:state :board-size} (gs/next-state {:state :board-size} "greg")))

  (it "selects the versus type state"
    (should= :pvp (:versus-type (gs/next-state {:state :versus-type} "1")))
    (should= :done (:state (gs/next-state {:state :versus-type} "1")))
    (should= :pvc (:versus-type (gs/next-state {:state :versus-type} "2")))
    (should= :difficulty (:state (gs/next-state {:state :versus-type} "2")))
    (should= {:state :versus-type} (gs/next-state {:state :versus-type} "3")))

  (it "selects the difficulty state"
    (should= :easy (:difficulty (gs/next-state {:state :difficulty} "1")))
    (should= :character (:state (gs/next-state {:state :difficulty} "1")))
    (should= :medium (:difficulty (gs/next-state {:state :difficulty} "2")))
    (should= :character (:state (gs/next-state {:state :difficulty} "2")))
    (should= :hard (:difficulty (gs/next-state {:state :difficulty} "3")))
    (should= :character (:state (gs/next-state {:state :difficulty} "3")))
    (should= {:state :difficulty} (gs/next-state {:state :difficulty} "4")))

  (it "selects the character state"
    (let [state {:state :character
                 :board-size 3
                 :difficulty :hard}]
      (should= \x (:character (gs/next-state state "1")))
      (should= :done (:state (gs/next-state state "1")))
      (should= (utils/empty-board 3) (:board (gs/next-state state "1")))
      (should= \o (:character (gs/next-state state "2")))
      (should= :done (:state (gs/next-state state "2")))
      (should= [\_ \_ \_ \_ \_ \_ \_ \_ \x] (:board (gs/next-state state "2")))
      (should= state (gs/next-state state "3"))))

  (it "stores the ui components to select the database"
    (should= {:label "Database"
              :type :menu
              :options ["1. File Database" "2. SQL Database"]}
             (gs/ui-components {:state :database})))

  (it "stores the ui components to select the load type"
    (should= {:label "Load Type"
              :type :menu
              :options ["1. New Game" "2. Load Game"]}
             (gs/ui-components {:state :load-type})))

  (it "stores the ui components to select the dimension"
    (should= {:label "Dimension"
              :type :menu
              :options ["1. 2D" "2. 3D (3x3x3)"]}
             (gs/ui-components {:state :dimension})))

  (it "stores the ui components to select a game from a database"
    (with-redefs [gs/db-fetch-games (stub :fetch-all-games {:return games})
                  gs/db-initialize (stub :initialize {:return 0})]
      (should= {:label "Game"
                :type :menu
                :options ["1. the-date: [\\x \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_]"
                          "2. another-date: [\\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_ \\_]"]}
               (gs/ui-components {:state :select-game :database nil}))))

  (it "stores the ui components to select a new game if the database is empty"
    (with-redefs [gs/db-fetch-games (stub :fetch-all-games {:return []})
                  gs/db-initialize (stub :initialize {:return 0})]
      (should= {:label "Game"
                :type :menu
                :options ["1. New Game"]}
               (gs/ui-components {:state :select-game :database nil}))))

  (it "stores the ui components to select the board size"
    (should= {:label "Enter Board Size"
              :type :counter
              :initial-value 3
              :valid? greater-than-two?}
             (gs/ui-components {:state :board-size})))

  (it "stores the ui components to select the versus type"
    (should= {:label "Versus Type"
              :type :menu
              :options ["1. Versus Player" "2. Versus Computer"]}
             (gs/ui-components {:state :versus-type})))

  (it "stores the ui components to select the difficulty state"
    (should= {:label "Difficulty"
              :type :menu
              :options ["1. Easy" "2. Medium" "3. Hard"]}
             (gs/ui-components {:state :difficulty})))

  (it "stores the ui components to select the character"
    (should= {:label "Starting Character"
              :type :menu
              :options ["1. x" "2. o"]}
             (gs/ui-components {:state :character}))))