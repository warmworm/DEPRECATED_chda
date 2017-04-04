(ns chda-test
  (:use chda clojure.contrib.test-is))

(deftest test-cpp-file?
  (is (cpp-file? "test.cpp"))
  (is (cpp-file? "aaa.c"))
  (is (cpp-file? "bbb.inl"))
  (is (cpp-file? "cdefg.h"))
  (is (cpp-file? "asdfasdf.hpp"))
  (is (nil? (cpp-file? "sdf.clj")))
  (is (nil? (cpp-file? "ffsf.lua")))
  (is (nil? (cpp-file? "sadfasdf.txt")))
  (is (nil? (cpp-file? "sadsdf.exe")))
  (is (nil? (cpp-file? "sdfdsf.dll")))
  (is (nil? (cpp-file? "sdfse3.obj"))))

(run-tests)
