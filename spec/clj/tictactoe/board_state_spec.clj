(ns clj.tictactoe.board-state-spec
  (:require [speclj.core :refer :all]
            [clj.tictactoe.board-state :refer :all]
            [clj.tictactoe.utils :as utils]))

(describe "A TicTacToe Board State"

  (it "checks if a line is a winning line"
    (should-not (winning-line? [\_ \_ \_]))
    (should (winning-line? [\x \x \x]))
    (should-not (winning-line? [\x \_ \_])))

  (it "gets the size of a board"
    (should= 3 (board-size (utils/empty-board 3)))
    (should= 4 (board-size (utils/empty-board 4)))
    (should= 3 (board-size (repeat 3 (utils/empty-board 3)))))

  (it "gets the rows of a board"
    (should= [[:a :b :c] [:d :e :f] [:g :h :i]]
             (rows [:a :b :c :d :e :f :g :h :i]))
    (should= [[:a :b :c :d] [:e :f :g :h] [:i :j :k :l] [:m :n :o :p]]
             (rows [:a :b :c :d :e :f :g :h :i :j :k :l :m :n :o :p]))
    (should= [[:a :b :c] [:d :e :f] [:g :h :i]
              [:j :k :l] [:m :n :o] [:p :q :r]
              [:s :t :u] [:v :w :x] [:y :z :aa]
              [:a :d :g] [:j :m :p] [:s :v :y]
              [:b :e :h] [:k :n :q] [:t :w :z]
              [:c :f :i] [:l :o :r] [:u :x :aa]
              [:a :k :u] [:d :n :x] [:g :q :aa]
              [:c :k :s] [:f :n :v] [:i :q :y]]
             (rows [[:a :b :c :d :e :f :g :h :i]
                    [:j :k :l :m :n :o :p :q :r]
                    [:s :t :u :v :w :x :y :z :aa]])))

  (it "gets the columns of a board"
    (should= [[:a :d :g] [:b :e :h] [:c :f :i]]
             (columns [:a :b :c :d :e :f :g :h :i]))
    (should= [[:a :e :i :m] [:b :f :j :n] [:c :g :k :o] [:d :h :l :p]]
             (columns [:a :b :c :d :e :f :g :h :i :j :k :l :m :n :o :p]))
    (should= [[:a :d :g] [:b :e :h] [:c :f :i]
              [:j :m :p] [:k :n :q] [:l :o :r]
              [:s :v :y] [:t :w :z] [:u :x :aa]
              [:a :j :s] [:d :m :v] [:g :p :y]
              [:b :k :t] [:e :n :w] [:h :q :z]
              [:c :l :u] [:f :o :x] [:i :r :aa]
              [:a :d :g] [:k :n :q] [:u :x :aa]
              [:c :f :i] [:k :n :q] [:s :v :y]]
             (columns [[:a :b :c :d :e :f :g :h :i]
                       [:j :k :l :m :n :o :p :q :r]
                       [:s :t :u :v :w :x :y :z :aa]])))

  (it "gets the diagonals of a board"
    (should= [[:a :e :i] [:c :e :g]]
             (diagonals [:a :b :c :d :e :f :g :h :i]))
    (should= [[:a :f :k :p] [:d :g :j :m]]
             (diagonals [:a :b :c :d :e :f :g :h :i :j :k :l :m :n :o :p]))
    (should=   [[:a :e :i] [:c :e :g]
                [:j :n :r] [:l :n :p]
                [:s :w :aa] [:u :w :y]
                [:a :m :y] [:g :m :s]
                [:b :n :z] [:h :n :t]
                [:c :o :aa] [:i :o :u]
                [:a :n :aa] [:u :n :g]
                [:c :n :y] [:s :n :i]]
             (diagonals [[:a :b :c :d :e :f :g :h :i]
                         [:j :k :l :m :n :o :p :q :r]
                         [:s :t :u :v :w :x :y :z :aa]])))

  (it "checks if a player has won"
    (should-not (win? (utils/empty-board 3)))
    (should (win? [\x \x \x \_ \_ \_ \_ \_ \_]))
    (should (win? [\_ \_ \_ \x \x \x \_ \_ \_]))
    (should (win? [\_ \_ \_ \_ \_ \_ \x \x \x]))
    (should-not (win? [\x \x \_ \x \_ \_ \_ \_ \_]))
    (should (win? [\x \_ \_ \x \_ \_ \x \_ \_]))
    (should (win? [\_ \x \_ \_ \x \_ \_ \x \_]))
    (should (win? [\_ \_ \x \_ \_ \x \_ \_ \x]))
    (should (win? [\x \_ \_ \_ \x \_ \_ \_ \x]))
    (should (win? [\_ \_ \x \_ \x \_ \x \_ \_])))

  (it "gets who won a game"
    (should= \x (winner [\x \x \x \o \o \_ \_ \_ \_]))
    (should= \o (winner [\o \o \o \x \x \_ \x \_ \_]))
    (should= \o (winner [\x \x \_ \x \_ \_ \o \o \o]))
    (should= \x (winner [\o \o \_ \x \x \x \_ \_ \_])))

  (it "checks if there is a tie"
    (should-not (tie? (utils/empty-board 3)))
    (should (tie? [\o \x \x \x \o \o \o \x \x]))
    (should (tie? [\o \x \o \o \x \x \x \o \x]))
    (should-not (tie? [\x \x \x \o \o \_ \_ \_ \_]))))