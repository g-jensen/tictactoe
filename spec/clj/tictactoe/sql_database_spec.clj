(ns clj.tictactoe.sql-database-spec
  (:require [speclj.core :refer :all]
            [clj.tictactoe.game-state :as gs]
            [clj.tictactoe.sql-database :refer :all]
            [clojure.java.jdbc :refer :all]
            [clj.tictactoe.utils :as utils])
  (:import (clj.tictactoe.game_mode PvPGame)))

(def sample [{:date "Tue Jun 19 12:46:29 EDT 1729" :board "[\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_]" :gamemode "{:mode :pvp}"}
             {:date "Tue Jun 19 12:46:32 EDT 1729" :board "[\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_]" :gamemode "{:mode :pvp}"}
             {:date "Tue Jun 19 12:46:35 EDT 1729" :board "[\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_]" :gamemode "{:mode :pvp}"}])

(describe "An SQL Database"

  (it "fetches all games"
    (let [mock-db (atom {:content sample})]
      (with-redefs [query (fn [_ _] (:content @mock-db))]
        (should= [{:date "Tue Jun 19 12:46:29 EDT 1729" :board [\_ \_ \_ \x \_ \_ \o \x \_] :gamemode {:mode :pvp}}
                  {:date "Tue Jun 19 12:46:32 EDT 1729" :board [\_ \_ \_ \x \_ \_ \o \x \_] :gamemode {:mode :pvp}}
                  {:date "Tue Jun 19 12:46:35 EDT 1729" :board [\_ \_ \_ \x \_ \_ \o \x \_] :gamemode {:mode :pvp}}]
                 (gs/db-fetch-games {:database :sql})))))

  (it "initializes the database"
    (let [mock-db (atom {})]
      (with-redefs [db-do-commands (fn [_ data] (swap! mock-db assoc :content data))]
        (gs/db-initialize {:database :sql})
        (should= "CREATE TABLE IF NOT EXISTS GAMES(DATE text,BOARD blob,GAMEMODE blob)"
                 (:content @mock-db)))))

  (it "deletes a game"
    (let [mock-db (atom {:content sample})]
      (with-redefs [execute! (fn [_ _] (swap! mock-db assoc :content (rest (:content @mock-db))))
                    query (fn [_ _] (:content @mock-db))]
        (gs/db-delete-game {:database :sql} "Tue Jun 19 12:46:29 EDT 1729")
        (should= [{:date "Tue Jun 19 12:46:32 EDT 1729" :board [\_ \_ \_ \x \_ \_ \o \x \_] :gamemode {:mode :pvp}}
                  {:date "Tue Jun 19 12:46:35 EDT 1729" :board [\_ \_ \_ \x \_ \_ \o \x \_] :gamemode {:mode :pvp}}]
                 (gs/db-fetch-games {:database :sql})))))

  (it "updates the database"
    (let [mock-db (atom {:content []})]
      (with-redefs [query (fn [_ _] (:content @mock-db))
                    insert! (fn [_ _ data] (swap! mock-db assoc :content (cons data (:content @mock-db))))
                    execute! (fn [_ _] (swap! mock-db assoc :content (rest (:content @mock-db))))
                    query (fn [_ _] (:content @mock-db))]
        (gs/db-update-game {:database :sql} "the-date"
                           (utils/empty-board 3)
                           (PvPGame. 3 (utils/empty-board 3)))
        (should= [{:date "the-date", :board [\_ \_ \_ \_ \_ \_ \_ \_ \_], :gamemode {:mode :pvp}}]
                 (gs/db-fetch-games {:database :sql}))
        (gs/db-update-game {:database :sql} "the-date"
                           [\x \_ \_ \_ \_ \_ \_ \_ \_]
                           (PvPGame. 3 (utils/empty-board 3)))
        (should= [{:date "the-date", :board [\x \_ \_ \_ \_ \_ \_ \_ \_], :gamemode {:mode :pvp}}]
                 (gs/db-fetch-games {:database :sql}))))))