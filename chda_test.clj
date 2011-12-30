(ns chda-test
  (:use chda clojure.contrib.test-is))

(deftest test-cpp-file?
  (is (cpp-file? ".cpp"))
  (is (cpp-file? ".c"))
  (is (cpp-file? ".inl"))
  (is (cpp-file? ".h"))
  (is (cpp-file? ".hpp"))
  (is (nil? (cpp-file? ".clj")))
  (is (nil? (cpp-file? ".lua")))
  (is (nil? (cpp-file? ".txt")))
  (is (nil? (cpp-file? ".exe")))
  (is (nil? (cpp-file? ".dll")))
  (is (nil? (cpp-file? ".obj"))))

(run-tests)
