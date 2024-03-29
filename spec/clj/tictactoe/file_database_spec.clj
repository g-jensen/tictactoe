(ns tictactoe.file-database-spec
  (:require [speclj.core :refer :all]
            [tictactoe.file-database :refer :all]
            [tictactoe.game-state :as gs]))

(def sample (str "{:date \"Tue Jun 19 12:46:29 EDT 1729\", :board [\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_] :versus-type :pvp :difficulty nil}\n"
                 "{:date \"Tue Jun 19 12:46:32 EDT 1729\", :board [\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_] :versus-type :pvp :difficulty nil}\n"
                 "{:date \"Tue Jun 19 12:46:35 EDT 1729\", :board [\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_] :versus-type :pvp :difficulty nil}\n"))

(describe "A File Database"

  (it "deletes a specific object with a given date from a string"
    (should= "{:date \"Tue Jun 19 12:46:29 EDT 1729\", :board [\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_], :versus-type :pvp, :difficulty nil}\n{:date \"Tue Jun 19 12:46:35 EDT 1729\", :board [\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_], :versus-type :pvp, :difficulty nil}"
             (delete-date sample "Tue Jun 19 12:46:32 EDT 1729")))

  (it "fetches all games"
    (let [mock-file (atom {:content sample})]
      (with-redefs [slurp (fn [_] (:content @mock-file))]
        (should= [{:date "Tue Jun 19 12:46:29 EDT 1729", :board [\_ \_ \_ \x \_ \_ \o \x \_], :versus-type :pvp, :difficulty nil}
                  {:date "Tue Jun 19 12:46:32 EDT 1729", :board [\_ \_ \_ \x \_ \_ \o \x \_], :versus-type :pvp, :difficulty nil}
                  {:date "Tue Jun 19 12:46:35 EDT 1729", :board [\_ \_ \_ \x \_ \_ \o \x \_], :versus-type :pvp, :difficulty nil}]
                 (gs/db-fetch-games {:database :file})))))

  (it "initializes the database"
    (let [mock-file (atom {})]
      (with-redefs [spit (fn [_ data _ _] (swap! mock-file assoc :content data))]
        (gs/db-initialize {:database :file})
        (should= "" (:content @mock-file)))))

  (it "updates the database"
    (let [mock-file (atom {})]
      (with-redefs [spit (fn [_ data] (swap! mock-file assoc :content data))
                    slurp (fn [_] (:content @mock-file))]
        (gs/db-update-game {:database :file
                            :board [\_ \_ \_ \_ \_ \_ \_ \_ \_]
                            :date "the-date"
                            :versus-type :pvp})
        (should= [{:database :file :board [\_ \_ \_ \_ \_ \_ \_ \_ \_] :date "the-date" :versus-type :pvp}]
                 (gs/db-fetch-games {:database :file}))
        (gs/db-update-game {:database :file
                            :board [\x \_ \_ \_ \_ \_ \_ \_ \_]
                            :date "the-date"
                            :versus-type :pvp})
        (should= [{:database :file :board [\x \_ \_ \_ \_ \_ \_ \_ \_] :date "the-date" :versus-type :pvp}]
                 (gs/db-fetch-games {:database :file})))))

  (it "deletes a game"
    (let [mock-file (atom {:content sample})]
      (with-redefs [spit (fn [_ data] (swap! mock-file assoc :content data))
                    slurp (fn [_] (:content @mock-file))]
        (gs/db-delete-game {:database :file} "Tue Jun 19 12:46:29 EDT 1729")
        (should= [{:date "Tue Jun 19 12:46:32 EDT 1729", :board [\_ \_ \_ \x \_ \_ \o \x \_], :versus-type :pvp, :difficulty nil}
                  {:date "Tue Jun 19 12:46:35 EDT 1729", :board [\_ \_ \_ \x \_ \_ \o \x \_], :versus-type :pvp, :difficulty nil}]
                 (gs/db-fetch-games {:database :file}))))))
