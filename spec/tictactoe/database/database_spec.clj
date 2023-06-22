(ns tictactoe.database.database-spec
  (:require [speclj.core :refer :all]
            [tictactoe.database.database :refer :all]))

(describe "A TicTacToe Database"

  (it "deletes a specific object with a given date from a string"
    (should= (str "{:date \"Tue Jun 19 12:46:29 EDT 1729\", :board [\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_]}\n"
                  "{:date \"Tue Jun 19 12:46:35 EDT 1729\", :board [\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_]}")
             (delete-date
               (str "{:date \"Tue Jun 19 12:46:29 EDT 1729\", :board [\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_]}\n"
                    "{:date \"Tue Jun 19 12:46:32 EDT 1729\", :board [\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_]}\n"
                    "{:date \"Tue Jun 19 12:46:35 EDT 1729\", :board [\\_ \\_ \\_ \\x \\_ \\_ \\o \\x \\_]}\n")
               "Tue Jun 19 12:46:32 EDT 1729"))))