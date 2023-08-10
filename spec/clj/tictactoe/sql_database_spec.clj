(ns tictactoe.sql-database-spec
  (:require [speclj.core :refer :all]
            [tictactoe.game-state :as gs]
            [tictactoe.sql-database :refer :all]
            [clojure.java.jdbc :refer :all]))

(def sample [{:date "Tue Jun 19 12:46:29 EDT 1729" :board "[\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_]" :versustype ":pvp" :difficulty "nil"}
             {:date "Tue Jun 19 12:46:32 EDT 1729" :board "[\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_]" :versustype ":pvc" :difficulty ":hard"}
             {:date "Tue Jun 19 12:46:35 EDT 1729" :board "[\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_]" :versustype ":pvp" :difficulty "nil"}])

(describe "An SQL Database"

  (it "fetches all games"
    (let [mock-db (atom {:content sample})]
      (with-redefs [query (fn [_ _] (:content @mock-db))]
        (should= [{:date "Tue Jun 19 12:46:29 EDT 1729", :board [\_ \_ \_ \x \_ \_ \o \x \_], :difficulty nil, :versus-type :pvp}
                  {:date "Tue Jun 19 12:46:32 EDT 1729", :board [\_ \_ \_ \x \_ \_ \o \x \_], :difficulty :hard, :versus-type :pvc}
                  {:date "Tue Jun 19 12:46:35 EDT 1729", :board [\_ \_ \_ \x \_ \_ \o \x \_], :difficulty nil, :versus-type :pvp}]
                 (gs/db-fetch-games {:database :sql})))))

  (it "initializes the database"
    (let [mock-db (atom {})]
      (with-redefs [db-do-commands (fn [_ data] (swap! mock-db assoc :content data))]
        (gs/db-initialize {:database :sql})
        (should= "CREATE TABLE IF NOT EXISTS GAMES(DATE text,BOARD blob,VERSUSTYPE blob,DIFFICULTY blob)"
                 (:content @mock-db)))))

  (it "deletes a game"
    (let [mock-db (atom {:content sample})]
      (with-redefs [execute! (fn [_ _] (swap! mock-db assoc :content (rest (:content @mock-db))))
                    query (fn [_ _] (:content @mock-db))]
        (gs/db-delete-game {:database :sql} "Tue Jun 19 12:46:29 EDT 1729")
        (should= [{:date "Tue Jun 19 12:46:32 EDT 1729", :board [\_ \_ \_ \x \_ \_ \o \x \_], :difficulty :hard, :versus-type :pvc}
                  {:date "Tue Jun 19 12:46:35 EDT 1729", :board [\_ \_ \_ \x \_ \_ \o \x \_], :difficulty nil, :versus-type :pvp}]
                 (gs/db-fetch-games {:database :sql})))))

  (it "updates the database"
    (let [mock-db (atom {:content []})]
      (with-redefs [query (fn [_ _] (:content @mock-db))
                    insert! (fn [_ _ data] (swap! mock-db assoc :content (cons data (:content @mock-db))))
                    execute! (fn [_ _] (swap! mock-db assoc :content (rest (:content @mock-db))))
                    query (fn [_ _] (:content @mock-db))]
        (gs/db-update-game {:database :sql
                            :board [\_ \_ \_ \_ \_ \_ \_ \_ \_]
                            :date "the-date"
                            :versus-type :pvp})
        (should= [{:date "the-date", :board [\_ \_ \_ \_ \_ \_ \_ \_ \_], :versus-type :pvp, :difficulty nil}]
                 (gs/db-fetch-games {:database :sql}))
        (gs/db-update-game {:database :sql
                            :board [\x \_ \_ \_ \_ \_ \_ \_ \_]
                            :date "the-date"
                            :versus-type :pvp})
        (should= [{:date "the-date", :board [\x \_ \_ \_ \_ \_ \_ \_ \_], :difficulty nil, :versus-type :pvp}]
                 (gs/db-fetch-games {:database :sql}))))))